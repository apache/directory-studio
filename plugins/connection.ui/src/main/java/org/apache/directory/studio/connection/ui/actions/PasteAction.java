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

import org.apache.directory.studio.common.ui.ClipboardUtils;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionFolder;
import org.apache.directory.studio.connection.core.ConnectionFolderManager;
import org.apache.directory.studio.connection.core.ConnectionManager;
import org.apache.directory.studio.connection.ui.dnd.ConnectionTransfer;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.PlatformUI;


/**
 * This class implements the Paste Action.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PasteAction extends StudioAction
{
    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        List<Connection> connections = getConnectionsToPaste();
        List<ConnectionFolder> connectionFolders = getConnectionFoldersToPaste();
        
        if ( !connections.isEmpty() && connectionFolders.isEmpty() )
        {
            if ( connections.size() > 1 )
            {
                return Messages.getString( "PasteAction.PasteConnections" );
            }
            else
            {
                return Messages.getString( "PasteAction.PasteConnection" ); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        else if ( !connectionFolders.isEmpty() && connections.isEmpty() )
        {
            if ( connectionFolders.size() > 1 )
            {
                return Messages.getString( "PasteAction.PasteConnectionFolders" );
            }
            else
            {
                return  Messages.getString( "PasteAction.PasteConnectionFolder" ); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        else
        {
            return Messages.getString( "PasteAction.Paste" ); //$NON-NLS-1$
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
        return IWorkbenchCommandConstants.EDIT_PASTE;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        return ClipboardUtils.isAvailable( ConnectionTransfer.getInstance() );
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
        List<Connection> connections = new ArrayList<>();

        Object content = ClipboardUtils.getFromClipboard( ConnectionTransfer.getInstance() );

        if ( content instanceof Object[] )
        {
            for ( Object object : ( Object[] )content )
            {
                if ( object instanceof Connection )
                {
                    connections.add( ( Connection ) object );
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
        List<ConnectionFolder> folders = new ArrayList<>();

        Object content = ClipboardUtils.getFromClipboard( ConnectionTransfer.getInstance() );

        if ( content instanceof Object[] )
        {
            for ( Object object : ( Object[] ) content )
            {
                if ( object instanceof ConnectionFolder )
                {
                    folders.add( ( ConnectionFolder ) object );
                }
            }
        }

        return folders;
    }

}
