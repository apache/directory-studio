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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionFolder;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.widgets.Tree;


/**
 * This class implements a {@link DragSourceListener} that is used to
 * drag and drop connections withing the connections view.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DragConnectionListener implements DragSourceListener
{
    /** The associated viewer */
    private TreeViewer treeViewer;

    /** The selection (used for drag and drop) */
    private StructuredSelection selection = null;


    /**
     * Creates a new instance of DragConnectionListener.
     */
    public DragConnectionListener( TreeViewer viewer )
    {
        treeViewer = viewer;
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation saves the selection.
     */
    public void dragStart( DragSourceEvent event )
    {
        selection = ( StructuredSelection ) treeViewer.getSelection();
        event.doit = !selection.isEmpty();
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation adds the dragged connections and connection folders to the 
     * given event data.
     */
    public void dragSetData( DragSourceEvent event )
    {
        if ( ConnectionTransfer.getInstance().isSupportedType( event.dataType ) )
        {
            if ( event.widget instanceof DragSource )
            {
                DragSource dragSource = ( DragSource ) event.widget;
                if ( dragSource.getControl() instanceof Tree )
                {
                    List<Object> objectList = new ArrayList<Object>();
                    if ( selection != null )
                    {
                        for ( Iterator<?> iterator = selection.iterator(); iterator.hasNext(); )
                        {
                            Object item = iterator.next();
                            if ( item instanceof Connection || item instanceof ConnectionFolder )
                            {
                                objectList.add( item );
                            }
                        }
                    }
                    event.data = objectList.toArray();
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
    }
}
