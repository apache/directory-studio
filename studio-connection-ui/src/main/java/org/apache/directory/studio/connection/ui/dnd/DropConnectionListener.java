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

package org.apache.directory.studio.connection.ui.dnd;


import java.util.Set;

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionFolder;
import org.apache.directory.studio.connection.core.ConnectionFolderManager;
import org.apache.directory.studio.connection.core.ConnectionManager;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;


/**
 * This class implements a {@link DropTargetListener} that is used to
 * drag and drop connections within the connections view.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DropConnectionListener implements DropTargetListener
{

    /**
     * Creates a new instance of DropConnectionListener.
     */
    public DropConnectionListener()
    {
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation checks if the event's data type is 
     * supported. If not supported dropping is rejected.
     */
    public void dragEnter( DropTargetEvent event )
    {
        if ( !ConnectionTransfer.getInstance().isSupportedType( event.currentDataType ) )
        {
            event.detail = DND.DROP_NONE;
        }
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation just calls {@link #dragOver(DropTargetEvent)}.
     */
    public void dragOperationChanged( DropTargetEvent event )
    {
        dragOver( event );
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation does nothing.
     */
    public void dragLeave( DropTargetEvent event )
    {
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation checks if the event's data type is 
     * supported. If not supported dropping is rejected.
     */
    public void dragOver( DropTargetEvent event )
    {
        try
        {
            // move connection folder: check that the new connection folder is not the same or a parent folder
            boolean isMoveConnectionFolderForbidden = false;
            if ( event.detail == DND.DROP_MOVE || event.detail == DND.DROP_NONE )
            {
                if ( ConnectionTransfer.getInstance().isSupportedType( event.currentDataType ) )
                {
                    if ( event.item != null && event.item.getData() instanceof ConnectionFolder )
                    {
                        ConnectionFolderManager connectionFolderManager = ConnectionCorePlugin.getDefault()
                            .getConnectionFolderManager();
                        ConnectionFolder overFolder = ( ConnectionFolder ) event.item.getData();
                        Set<ConnectionFolder> allParentFolders = connectionFolderManager
                            .getAllParentFolders( overFolder );

                        if ( event.widget instanceof DropTarget )
                        {
                            DropTarget dropTarget = ( DropTarget ) event.widget;
                            if ( dropTarget.getControl() instanceof Tree )
                            {
                                Tree tree = ( Tree ) dropTarget.getControl();
                                TreeItem[] items = tree.getSelection();
                                for ( int i = 0; i < items.length; i++ )
                                {
                                    if ( items[i].getData() instanceof ConnectionFolder )
                                    {
                                        ConnectionFolder folder = ( ConnectionFolder ) items[i].getData();
                                        if ( allParentFolders.contains( folder ) )
                                        {
                                            isMoveConnectionFolderForbidden = true;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if ( !ConnectionTransfer.getInstance().isSupportedType( event.currentDataType ) )
            {
                event.detail = DND.DROP_NONE;
            }
            else if ( isMoveConnectionFolderForbidden )
            {
                event.detail = DND.DROP_NONE;
            }
            else if ( event.detail == DND.DROP_LINK )
            {
                event.detail = DND.DROP_NONE;
            }
            else if ( event.detail == DND.DROP_NONE )
            {
                event.detail = DND.DROP_DEFAULT;
            }
        }
        catch ( Exception e )
        {
            event.detail = DND.DROP_NONE;
            e.printStackTrace();
        }
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation does nothing.
     */
    public void dropAccept( DropTargetEvent event )
    {
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation drops the dragged connection to
     * the selected position.
     */
    public void drop( DropTargetEvent event )
    {
        ConnectionManager connectionManager = ConnectionCorePlugin.getDefault().getConnectionManager();
        ConnectionFolderManager connectionFolderManager = ConnectionCorePlugin.getDefault()
            .getConnectionFolderManager();

        try
        {
            if ( ConnectionTransfer.getInstance().isSupportedType( event.currentDataType ) )
            {
                // get connection and folders to handle
                Object[] objects = ( Object[] ) event.data;
                Object target = event.item != null ? event.item.getData() : connectionFolderManager
                    .getRootConnectionFolder();

                ConnectionFolder targetFolder = null;
                if ( target instanceof ConnectionFolder )
                {
                    targetFolder = ( ConnectionFolder ) target;
                }
                else if ( target instanceof Connection )
                {
                    Connection connection = ( Connection ) target;
                    targetFolder = connectionFolderManager.getParentConnectionFolder( connection );
                }

                for ( Object object : objects )
                {
                    if ( object instanceof Connection )
                    {
                        Connection connection = ( Connection ) object;
                        if ( event.detail == DND.DROP_MOVE )
                        {
                            ConnectionFolder parentConnectionFolder = connectionFolderManager
                                .getParentConnectionFolder( connection );
                            parentConnectionFolder.removeConnectionId( connection.getId() );
                            targetFolder.addConnectionId( connection.getId() );
                        }
                        else if ( event.detail == DND.DROP_COPY )
                        {
                            Connection newConnection = ( Connection ) connection.clone();
                            connectionManager.addConnection( newConnection );
                            targetFolder.addConnectionId( newConnection.getId() );
                        }
                    }
                    else if ( object instanceof ConnectionFolder )
                    {
                        ConnectionFolder folder = ( ConnectionFolder ) object;
                        if ( event.detail == DND.DROP_MOVE )
                        {
                            ConnectionFolder parentConnectionFolder = connectionFolderManager
                                .getParentConnectionFolder( folder );
                            parentConnectionFolder.removeSubFolderId( folder.getId() );
                            targetFolder.addSubFolderId( folder.getId() );
                            // TODO: expand target folder
                        }
                        else if ( event.detail == DND.DROP_COPY )
                        {
                            ConnectionFolder newFolder = ( ConnectionFolder ) folder.clone();
                            connectionFolderManager.addConnectionFolder( newFolder );
                            targetFolder.addSubFolderId( newFolder.getId() );
                            // TODO: expand target folder
                        }
                    }
                }
            }
            else
            {
                event.detail = DND.DROP_NONE;
            }
        }
        catch ( Exception e )
        {
            event.detail = DND.DROP_NONE;
            e.printStackTrace();
        }
    }

}
