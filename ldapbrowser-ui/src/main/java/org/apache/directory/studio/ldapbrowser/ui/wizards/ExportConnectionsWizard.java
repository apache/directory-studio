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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionFolder;
import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.apache.directory.studio.connection.core.io.ConnectionIO;
import org.apache.directory.studio.ldapbrowser.core.BrowserConnectionIO;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.eclipse.swt.widgets.Composite;


/**
 * This class implements the Wizard for Exporting connections.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ExportConnectionsWizard extends ExportBaseWizard
{
    /** The wizard page */
    private ExportConnectionsWizardPage page;


    /**
     * Creates a new instance of ExportConnectionsWizard.
     */
    public ExportConnectionsWizard()
    {
        super( Messages.getString( "ExportConnectionsWizard.ConnectionsExport" ) ); //$NON-NLS-1$
    }


    /**
     * Gets the ID of the Export Connections Wizard
     * 
     * @return The ID of the Export Connections Wizard
     */
    public static String getId()
    {
        return BrowserUIConstants.WIZARD_EXPORT_CONNECTIONS;
    }


    /**
     * {@inheritDoc}
     */
    public void addPages()
    {
        page = new ExportConnectionsWizardPage();
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

        String exportFileName = page.getExportFileName();

        try
        {
            // Creating the ZipOutputStream
            ZipOutputStream zos = new ZipOutputStream( new FileOutputStream( new File( exportFileName ) ) );
            // Writing the Connections file.
            zos.putNextEntry( new ZipEntry( Messages.getString( "ExportConnectionsWizard.1" ) ) ); //$NON-NLS-1$
            Connection[] connections = ConnectionCorePlugin.getDefault().getConnectionManager().getConnections();
            Set<ConnectionParameter> connectionParameters = new HashSet<ConnectionParameter>();
            for ( Connection connection : connections )
            {
                connectionParameters.add( connection.getConnectionParameter() );
            }
            ConnectionIO.save( connectionParameters, zos );
            zos.closeEntry();
            // Writing the Connection Folders file.
            zos.putNextEntry( new ZipEntry( Messages.getString( "ExportConnectionsWizard.2" ) ) ); //$NON-NLS-1$
            ConnectionFolder[] connectionFolders = ConnectionCorePlugin.getDefault().getConnectionFolderManager()
                .getConnectionFolders();
            Set<ConnectionFolder> connectionFoldersSet = new HashSet<ConnectionFolder>();
            for ( ConnectionFolder connectionFolder : connectionFolders )
            {
                connectionFoldersSet.add( connectionFolder );
            }
            ConnectionIO.saveConnectionFolders( connectionFoldersSet, zos );
            zos.closeEntry();
            // Writing the Browser Connections file.
            zos.putNextEntry( new ZipEntry( Messages.getString( "ExportConnectionsWizard.3" ) ) ); //$NON-NLS-1$
            IBrowserConnection[] browserConnections = BrowserCorePlugin.getDefault().getConnectionManager()
                .getBrowserConnections();
            Map<String, IBrowserConnection> browserConnectionsMap = new HashMap<String, IBrowserConnection>();
            for ( IBrowserConnection browserConnection : browserConnections )
            {
                browserConnectionsMap.put( browserConnection.getConnection().getId(), browserConnection );
            }
            BrowserConnectionIO.save( zos, browserConnectionsMap );
            zos.closeEntry();
            // Closing the ZipOutputStream
            zos.close();
        }
        catch ( FileNotFoundException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch ( IOException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return true;
    }
}
