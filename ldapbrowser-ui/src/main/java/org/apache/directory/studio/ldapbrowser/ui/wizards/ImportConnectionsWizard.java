/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */

package org.apache.directory.studio.ldapbrowser.ui.wizards;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionFolder;
import org.apache.directory.studio.connection.core.ConnectionFolderManager;
import org.apache.directory.studio.connection.core.ConnectionManager;
import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.apache.directory.studio.connection.core.io.ConnectionIO;
import org.apache.directory.studio.connection.core.io.ConnectionIOException;
import org.apache.directory.studio.ldapbrowser.core.BrowserConnectionIO;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.impl.BrowserConnection;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.eclipse.swt.widgets.Composite;


/**
 * This class implements the Wizard for Importing connections.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ImportConnectionsWizard extends ExportBaseWizard
{
    /** The wizard page */
    private ImportConnectionsWizardPage page;


    /**
     * Creates a new instance of ImportConnectionsWizard.
     */
    public ImportConnectionsWizard()
    {
        super( Messages.getString( "ImportConnectionsWizard.ConnectionsImport" ) ); //$NON-NLS-1$
    }


    /**
     * Gets the ID of the Import Connections Wizard
     * 
     * @return The ID of the Import Connections Wizard
     */
    public static String getId()
    {
        return BrowserUIConstants.WIZARD_IMPORT_CONNECTIONS;
    }


    /**
     * {@inheritDoc}
     */
    public void addPages()
    {
        page = new ImportConnectionsWizardPage();
        addPage( page );
    }


    /**
     * {@inheritDoc}
     */
    public void createPageControls( Composite pageContainer )
    {
        super.createPageControls( pageContainer );

        // set help context ID
        //        PlatformUI.getWorkbench().getHelpSystem().setHelp( fromPage.getControl(),
        //            BrowserUIPlugin.PLUGIN_ID + "." + "tools_ldifexport_wizard" );
        //TODO: Add Help Context
    }


    /**
     * {@inheritDoc}
     */
    public boolean performFinish()
    {
        page.saveDialogSettings();

        String importFileName = page.getImportFileName();

        try
        {
            ZipFile importFile = new ZipFile( new File( importFileName ) );

            // Loading the Connections
            ZipEntry connectionsEntry = importFile.getEntry( "connections.xml" ); //$NON-NLS-1$
            if ( connectionsEntry != null )
            {
                InputStream connectionsInputStream = importFile.getInputStream( connectionsEntry );
                ConnectionManager connectionManager = ConnectionCorePlugin.getDefault().getConnectionManager();
                Set<ConnectionParameter> connectionParametersSet = ConnectionIO.load( connectionsInputStream );
                for ( ConnectionParameter connectionParameter : connectionParametersSet )
                {
                    connectionManager.addConnection( new Connection( connectionParameter ) );
                }
            }

            // Loading the ConnectionFolders
            ZipEntry connectionFoldersEntry = importFile.getEntry( "connectionFolders.xml" ); //$NON-NLS-1$
            ConnectionFolder rootConnectionFolder = null;
            if ( connectionFoldersEntry != null )
            {
                InputStream connectionFoldersInputStream = importFile.getInputStream( connectionFoldersEntry );
                ConnectionFolderManager connectionFolderManager = ConnectionCorePlugin.getDefault()
                    .getConnectionFolderManager();
                Set<ConnectionFolder> connectionFoldersSet = ConnectionIO
                    .loadConnectionFolders( connectionFoldersInputStream );
                for ( ConnectionFolder connectionFolder : connectionFoldersSet )
                {
                    if ( !"0".equals( connectionFolder.getId() ) ) //$NON-NLS-1$
                    {
                        connectionFolderManager.addConnectionFolder( connectionFolder );
                    }
                    else
                    {
                        rootConnectionFolder = connectionFolder;
                    }
                }

                // Root ConnectionFolder must be the last one to be loaded 
                if ( rootConnectionFolder != null )
                {
                    ConnectionFolder realRootConnectionFolder = connectionFolderManager.getRootConnectionFolder();
                    // Adding subfolders
                    List<String> realSubFolderIds = realRootConnectionFolder.getSubFolderIds();
                    for ( String subFolderId : rootConnectionFolder.getSubFolderIds() )
                    {
                        if ( !realSubFolderIds.contains( subFolderId ) )
                        {
                            realRootConnectionFolder.addSubFolderId( subFolderId );
                        }
                    }

                    // Adding connections 
                    List<String> realConnectionIds = realRootConnectionFolder.getConnectionIds();
                    for ( String connectionId : rootConnectionFolder.getConnectionIds() )
                    {
                        if ( !realConnectionIds.contains( connectionId ) )
                        {
                            realRootConnectionFolder.addConnectionId( connectionId );
                        }
                    }
                }
            }

            // Loading the BrowserConnections
            ZipEntry browserConnectionsEntry = importFile.getEntry( "browserconnections.xml" ); //$NON-NLS-1$
            if ( browserConnectionsEntry != null )
            {
                InputStream browserConnectionsInputStream = importFile.getInputStream( browserConnectionsEntry );

                Connection[] connections = ConnectionCorePlugin.getDefault().getConnectionManager().getConnections();
                Map<String, IBrowserConnection> connectionsMap = new HashMap<String, IBrowserConnection>();
                for ( int i = 0; i < connections.length; i++ )
                {
                    Connection connection = connections[i];
                    BrowserConnection browserConnection = new BrowserConnection( connection );
                    connectionsMap.put( connection.getId(), browserConnection );
                }

                BrowserConnectionIO.load( browserConnectionsInputStream, connectionsMap );
            }
        }
        catch ( ZipException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch ( IOException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch ( ConnectionIOException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return true;
    }
}
