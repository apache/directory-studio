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

import org.apache.directory.studio.ldapbrowser.common.dnd.ConnectionTransfer;
import org.apache.directory.studio.ldapbrowser.core.model.IConnection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;


/**
 * This class implements a {@link DragSourceListener} that is used to
 * drag and drop connections withing the connections view.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DragConnectionListener implements DragSourceListener
{

    /**
     * Creates a new instance of DragConnectionListener.
     */
    public DragConnectionListener()
    {
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation does nothing.
     */
    public void dragStart( DragSourceEvent event )
    {
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation adds the dragged connections to the 
     * given event data.
     */
    public void dragSetData( DragSourceEvent event )
    {
        if ( ConnectionTransfer.getInstance().isSupportedType( event.dataType ) )
        {
            if ( event.widget instanceof DragSource )
            {
                DragSource dragSource = ( DragSource ) event.widget;
                if ( dragSource.getControl() instanceof Table )
                {
                    Table table = ( Table ) dragSource.getControl();
                    TableItem[] items = table.getSelection();
                    List<IConnection> connectionList = new ArrayList<IConnection>();
                    for ( int i = 0; i < items.length; i++ )
                    {
                        if ( items[i].getData() instanceof IConnection )
                        {
                            connectionList.add( ( IConnection ) items[i].getData() );
                        }
                    }
                    event.data = connectionList.toArray( new IConnection[connectionList.size()] );
                }
            }
        }
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation does nothing.
     */
    public void dragFinished( DragSourceEvent event )
    {
        if ( event.detail == DND.DROP_MOVE && event.doit )
        {
            // this.connectionManager.removeConnection(this.dragConnection);
        }
    }

}
