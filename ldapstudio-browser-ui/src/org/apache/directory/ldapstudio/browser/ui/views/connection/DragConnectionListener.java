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

package org.apache.directory.ldapstudio.browser.ui.views.connection;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.ui.dnd.ConnectionTransfer;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;


public class DragConnectionListener implements DragSourceListener
{

    public DragConnectionListener()
    {
    }


    public void dispose()
    {
    }


    public void dragStart( org.eclipse.swt.dnd.DragSourceEvent event )
    {
    }


    public void dragSetData( org.eclipse.swt.dnd.DragSourceEvent event )
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
                    List connectionList = new ArrayList();
                    for ( int i = 0; i < items.length; i++ )
                    {
                        if ( items[i].getData() instanceof IConnection )
                        {
                            connectionList.add( items[i].getData() );
                        }
                    }
                    event.data = ( IConnection[] ) connectionList.toArray( new IConnection[connectionList.size()] );
                }
            }
        }
    }


    public void dragFinished( org.eclipse.swt.dnd.DragSourceEvent event )
    {
        if ( event.detail == DND.DROP_MOVE && event.doit )
        {
            // this.connectionManager.removeConnection(this.dragConnection);
        }
    }

}
