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
import java.util.Arrays;
import java.util.List;

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCoreConstants;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionFolder;
import org.apache.directory.studio.connection.core.jobs.CloseConnectionsRunnable;
import org.apache.directory.studio.connection.core.jobs.StudioConnectionJob;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;


/**
 * This Action implements the Delete Action. It deletes Connections, Entries, Searches, Bookmarks, Attributes or Values.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DeleteAction extends StudioAction
{
    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        Connection[] connections = getSelectedConnections();
        ConnectionFolder[] connectionFolders = getSelectedConnectionFolders();        
        if ( connections.length > 0 && connectionFolders.length == 0 )
        {
            return connections.length > 1 ? "Delete Connections" : "Delete Connection";
        }
        else if ( connectionFolders.length > 0 && connections.length == 0 )
        {
            return connectionFolders.length > 1 ? "Delete Connection Folders" : "Delete Connection Folder";
        }
        else 
        {
            return "Delete";
        }
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor( ISharedImages.IMG_TOOL_DELETE );
    }


    /**
     * {@inheritDoc}
     */
    public String getCommandId()
    {
        return IWorkbenchActionDefinitionIds.DELETE;
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        Connection[] connections = getSelectedConnections();
        ConnectionFolder[] connectionFolders = getSelectedConnectionFolders();        

        StringBuffer message = new StringBuffer();

        if ( connections.length > 0 )
        {
            if ( connections.length <= 5 )
            {
                message.append( connections.length == 1 ? "Are your sure to delete the following connection?"
                    : "Are your sure to delete the following connections?" );
                for ( int i = 0; i < connections.length; i++ )
                {
                    message.append( ConnectionCoreConstants.LINE_SEPARATOR );
                    message.append( "  - " );
                    message.append( connections[i].getName() );
                }
            }
            else
            {
                message.append( "Are your sure to delete the selected connections?" );
            }
            message.append( ConnectionCoreConstants.LINE_SEPARATOR );
            message.append( ConnectionCoreConstants.LINE_SEPARATOR );
        }

        if ( connectionFolders.length > 0 )
        {
            if ( connectionFolders.length <= 5 )
            {
                message.append( connectionFolders.length == 1 ? "Are your sure to delete the following connection folder including all connections?"
                    : "Are your sure to delete the following connection folders including all connections?" );
                for ( int i = 0; i < connectionFolders.length; i++ )
                {
                    message.append( ConnectionCoreConstants.LINE_SEPARATOR );
                    message.append( "  - " );
                    message.append( connectionFolders[i].getName() );
                }
            }
            else
            {
                message.append( "Are your sure to delete the selected connection folders including all connections?" );
            }
            message.append( ConnectionCoreConstants.LINE_SEPARATOR );
            message.append( ConnectionCoreConstants.LINE_SEPARATOR );
        }

        if ( message.length() == 0 || MessageDialog.openConfirm( getShell(), getText(), message.toString() ) )
        {
            List<Connection> connectionsToDelete = getConnectionsToDelete();
            List<ConnectionFolder> connectionsFoldersToDelete = getConnectionsFoldersToDelete();
            
            if ( connections.length > 0 )
            {
                deleteConnections( connectionsToDelete );
            }
            if ( connectionFolders.length > 0 )
            {
                deleteConnectionFolders( connectionsFoldersToDelete );
            }
        }
    }


    private List<ConnectionFolder> getConnectionsFoldersToDelete()
    {
        List<ConnectionFolder> selectedFolders = new ArrayList<ConnectionFolder>(Arrays.asList( getSelectedConnectionFolders() ));
        List<ConnectionFolder> foldersToDelete = new ArrayList<ConnectionFolder>();
        while( !selectedFolders.isEmpty() )
        {
            ConnectionFolder folder = selectedFolders.get( 0 );

            List<String> subFolderIds = folder.getSubFolderIds();
            for ( String subFolderId : subFolderIds )
            {
                ConnectionFolder subFolder = ConnectionCorePlugin.getDefault().getConnectionFolderManager().getConnectionFolderById( subFolderId );
                if ( subFolder != null )
                {
                    selectedFolders.add( subFolder );
                }
            }

            if( !foldersToDelete.contains( folder ) )
            {
                foldersToDelete.add( folder );
            }
            
            selectedFolders.remove( folder );
        }
        return foldersToDelete;
    }


    private List<Connection> getConnectionsToDelete()
    {
        List<ConnectionFolder> selectedFolders = new ArrayList<ConnectionFolder>(Arrays.asList( getSelectedConnectionFolders() ));
        List<Connection> selectedConnections = new ArrayList<Connection>(Arrays.asList( getSelectedConnections() ));
        List<Connection> connectionsToDelete = new ArrayList<Connection>( selectedConnections );
        while( !selectedFolders.isEmpty() )
        {
            ConnectionFolder folder = selectedFolders.get( 0 );

            List<String> subFolderIds = folder.getSubFolderIds();
            for ( String subFolderId : subFolderIds )
            {
                ConnectionFolder subFolder = ConnectionCorePlugin.getDefault().getConnectionFolderManager().getConnectionFolderById( subFolderId );
                if ( subFolder != null )
                {
                    selectedFolders.add( subFolder );
                }
            }

            List<String> connectionIds = folder.getConnectionIds();
            for ( String connectionId : connectionIds )
            {
                Connection connection = ConnectionCorePlugin.getDefault().getConnectionManager().getConnectionById( connectionId );
                if(connection != null && !connectionsToDelete.contains( connection ))
                {
                    connectionsToDelete.add( connection );
                }
            }

            selectedFolders.remove( folder );
        }
        return connectionsToDelete;
    }

    
    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        return getSelectedConnections().length + getSelectedConnectionFolders().length > 0;
    }


    /**
     * Deletes Connections
     *
     * @param connectionsToDelete
     *      the Connections to delete
     */
    private void deleteConnections( List<Connection> connectionsToDelete )
    {
        new StudioConnectionJob( new CloseConnectionsRunnable( connectionsToDelete ) ).execute();
        for ( Connection connection : connectionsToDelete )
        {
            ConnectionCorePlugin.getDefault().getConnectionManager().removeConnection( connection);
        }
    }


    /**
     * Deletes Connection Folders
     *
     * @param connectionsFoldersToDelete
     *      the Connection Folders to delete
     */
    private void deleteConnectionFolders( List<ConnectionFolder> connectionsFoldersToDelete )
    {
        for ( ConnectionFolder connectionFolder : connectionsFoldersToDelete )
        {
            ConnectionCorePlugin.getDefault().getConnectionFolderManager().removeConnectionFolder( connectionFolder );
        }
    }

}
