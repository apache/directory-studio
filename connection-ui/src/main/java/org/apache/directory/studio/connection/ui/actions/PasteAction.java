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

package org.apache.directory.studio.connection.ui.actions;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.directory.shared.ldap.codec.util.LdapURLEncodingException;
import org.apache.directory.shared.ldap.util.LdapURL;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCoreConstants;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionFolder;
import org.apache.directory.studio.connection.core.ConnectionFolderManager;
import org.apache.directory.studio.connection.core.ConnectionManager;
import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.apache.directory.studio.connection.ui.ConnectionParameterPage;
import org.apache.directory.studio.connection.ui.ConnectionParameterPageManager;
import org.apache.directory.studio.connection.ui.dnd.ConnectionTransfer;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;


/**
 * This class implements the Paste Action.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class PasteAction extends StudioAction
{
    /**
     * Creates a new instance of PasteAction.
     */
    public PasteAction()
    {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        List<Connection> connections = getConnectionsToPaste();
        List<ConnectionFolder> connectionFolders = getConnectionFoldersToPaste();
        if ( connections.size() > 0 && connectionFolders.size() == 0 )
        {
            return connections.size() > 1 ? "Paste Connections" : "Paste Connection";
        }
        else if ( connectionFolders.size() > 0 && connections.size() == 0 )
        {
            return connectionFolders.size() > 1 ? "Paste Connection Folders" : "Paste Connection Folder";
        }
        else
        {
            return "Paste";
        }
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor( ISharedImages.IMG_TOOL_PASTE );
    }


    /**
     * {@inheritDoc}
     */
    public String getCommandId()
    {
        return IWorkbenchActionDefinitionIds.PASTE;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        return this.getFromClipboard( ConnectionTransfer.getInstance() ) != null
            || !getConnectionsByLdapUrl().isEmpty();
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        ConnectionFolderManager connectionFolderManager = ConnectionCorePlugin.getDefault()
            .getConnectionFolderManager();
        ConnectionManager connectionManager = ConnectionCorePlugin.getDefault().getConnectionManager();

        ConnectionFolder[] selectedFolders = getSelectedConnectionFolders();
        Connection[] selectedConnections = getSelectedConnections();
        ConnectionFolder targetFolder = null;
        if ( selectedFolders.length > 0 )
        {
            targetFolder = selectedFolders[0];
        }
        else if ( selectedConnections.length > 0 )
        {
            targetFolder = connectionFolderManager.getParentConnectionFolder( selectedConnections[0] );
        }
        if ( targetFolder == null )
        {
            targetFolder = connectionFolderManager.getRootConnectionFolder();
        }

        // connections
        List<Connection> connections = getConnectionsToPaste();
        for ( Connection connection : connections )
        {
            Connection newConnection = ( Connection ) connection.clone();
            connectionManager.addConnection( newConnection );
            targetFolder.addConnectionId( newConnection.getId() );
        }

        // connection folders
        List<ConnectionFolder> connectionFolders = getConnectionFoldersToPaste();
        for ( ConnectionFolder connectionFolder : connectionFolders )
        {
            ConnectionFolder newConnectionFolder = ( ConnectionFolder ) connectionFolder.clone();
            connectionFolderManager.addConnectionFolder( newConnectionFolder );
            targetFolder.addSubFolderId( newConnectionFolder.getId() );
        }
    }


    /**
     * Condition: there are connections in clipboard
     * 
     * @return the connections to paste
     */
    private List<Connection> getConnectionsToPaste()
    {
        List<Connection> connections = new ArrayList<Connection>();

        // first check for Connection objects in the clipboard
        Object content = getFromClipboard( ConnectionTransfer.getInstance() );
        if ( content != null && content instanceof Object[] )
        {
            Object[] objects = ( Object[] ) content;
            for ( Object object : objects )
            {
                if ( object instanceof Connection )
                {
                    connections.add( ( Connection ) object );
                }
            }
        }

        // if there are no Connection objects in the clipboard
        // then check for LDAP URLs
        if ( connections.isEmpty() )
        {
            List<Connection> connectionByLdapUrl = getConnectionsByLdapUrl();
            connections.addAll( connectionByLdapUrl );
        }

        return connections;
    }


    private List<Connection> getConnectionsByLdapUrl()
    {
        List<Connection> connections = new ArrayList<Connection>();

        Object content = getFromClipboard( TextTransfer.getInstance() );
        if ( content != null && content instanceof String )
        {
            ConnectionParameterPage[] connectionParameterPages = ConnectionParameterPageManager
                .getConnectionParameterPages();

            String[] lines = ( ( String ) content ).split( ConnectionCoreConstants.LINE_SEPARATOR );
            for ( String line : lines )
            {
                line = line.trim();
                if ( StringUtils.isNotEmpty( line ) )
                {
                    try
                    {
                        LdapURL ldapUrl = new LdapURL( line );
                        ConnectionParameter parameter = new ConnectionParameter();
                        for ( ConnectionParameterPage connectionParameterPage : connectionParameterPages )
                        {
                            connectionParameterPage.mergeLdapUrlToParameters( ldapUrl, parameter );
                        }
                        Connection connection = new Connection( parameter );
                        connections.add( connection );
                    }
                    catch ( LdapURLEncodingException e )
                    {
                        // this was a string that doesn't represent an LDAP URL, ignore
                    }
                }
            }
        }

        return connections;
    }


    /**
     * Condition: there are connection folders in clipboard
     * 
     * @return the connection folders to paste
     */
    private List<ConnectionFolder> getConnectionFoldersToPaste()
    {
        List<ConnectionFolder> folders = new ArrayList<ConnectionFolder>();

        Object content = this.getFromClipboard( ConnectionTransfer.getInstance() );
        if ( content != null && content instanceof Object[] )
        {
            Object[] objects = ( Object[] ) content;
            for ( Object object : objects )
            {
                if ( object instanceof ConnectionFolder )
                {
                    folders.add( ( ConnectionFolder ) object );
                }
            }
        }

        return folders;
    }


    /**
     * Retrieve the data of the specified type currently available on the system clipboard.
     *
     * @param dataType
     *      the transfer agent for the type of data being requested
     * @return
     *      the data obtained from the clipboard or null if no data of this type is available
     */
    protected Object getFromClipboard( Transfer dataType )
    {
        Clipboard clipboard = null;
        try
        {
            clipboard = new Clipboard( Display.getCurrent() );
            return clipboard.getContents( dataType );
        }
        finally
        {
            if ( clipboard != null )
                clipboard.dispose();
        }
    }
}
