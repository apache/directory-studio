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

package org.apache.directory.studio.ldapbrowser.ui.views.connection;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionManager;
import org.apache.directory.studio.connection.ui.dnd.ConnectionTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;


/**
 * This class implements a {@link DropTargetListener} that is used to
 * drag and drop connections withing the connections view.
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
        boolean isOverSelection = false;
        if ( event.detail == DND.DROP_MOVE || event.detail == DND.DROP_NONE )
        {
            if ( ConnectionTransfer.getInstance().isSupportedType( event.currentDataType ) )
            {
                if ( event.item != null && event.item.getData() instanceof Connection )
                {
                    Connection overConn = ( Connection ) event.item.getData();
                    if ( event.widget instanceof DropTarget )
                    {
                        DropTarget dropTarget = ( DropTarget ) event.widget;
                        if ( dropTarget.getControl() instanceof Table )
                        {
                            Table table = ( Table ) dropTarget.getControl();
                            TableItem[] items = table.getSelection();
                            List<Connection> connectionList = new ArrayList<Connection>();
                            for ( int i = 0; i < items.length; i++ )
                            {
                                if ( items[i].getData() instanceof Connection )
                                {
                                    connectionList.add( ( Connection ) items[i].getData() );
                                }
                            }
                            if ( connectionList.contains( overConn ) )
                            {
                                isOverSelection = true;
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
        else if ( event.item == null )
        {
            event.detail = DND.DROP_NONE;
        }
        else if ( isOverSelection )
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

        try
        {
            if ( ConnectionTransfer.getInstance().isSupportedType( event.currentDataType ) )
            {
                // get connection to handle
                Connection[] connections = ( Connection[] ) event.data;
                Connection targetConnection = ( Connection ) event.item.getData();

                if ( event.detail == DND.DROP_MOVE )
                {
                    boolean fromTop = connectionManager.indexOf( connections[0] ) < connectionManager
                        .indexOf( targetConnection );
                    for ( int i = 0; i < connections.length; i++ )
                    {
                        connectionManager.removeConnection( connections[i] );
                    }
                    for ( int i = 0; i < connections.length; i++ )
                    {
                        int index = connectionManager.indexOf( targetConnection );
                        if ( fromTop )
                        {
                            index++;
                            connectionManager.addConnection( index + i, connections[i] );
                        }
                        else
                        {
                            connectionManager.addConnection( index, connections[i] );
                        }
                    }
                }
                else if ( event.detail == DND.DROP_COPY )
                {
                    for ( int i = 0; i < connections.length; i++ )
                    {
                        Connection newConnection = ( Connection ) connections[i].clone();
                        int index = connectionManager.indexOf( targetConnection );
                        connectionManager.addConnection( index + i + 1, newConnection );
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
