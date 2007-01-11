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

package org.apache.directory.ldapstudio.browser.ui.widgets.browser;


import org.apache.directory.ldapstudio.browser.core.events.AttributesInitializedEvent;
import org.apache.directory.ldapstudio.browser.core.events.ConnectionUpdateEvent;
import org.apache.directory.ldapstudio.browser.core.events.ConnectionUpdateListener;
import org.apache.directory.ldapstudio.browser.core.events.EntryModificationEvent;
import org.apache.directory.ldapstudio.browser.core.events.EntryUpdateListener;
import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;


public class BrowserUniversalListener implements ITreeViewerListener, IDoubleClickListener, ConnectionUpdateListener,
    EntryUpdateListener
{

    protected TreeViewer viewer;


    public BrowserUniversalListener( TreeViewer viewer )
    {
        this.viewer = viewer;

        this.viewer.addTreeListener( this );
        this.viewer.addDoubleClickListener( this );
        EventRegistry.addConnectionUpdateListener( this );
        EventRegistry.addEntryUpdateListener( this );
    }


    public void dispose()
    {
        if ( this.viewer != null )
        {
            this.viewer.removeTreeListener( this );
            this.viewer.removeDoubleClickListener( this );
            EventRegistry.removeConnectionUpdateListener( this );
            EventRegistry.removeEntryUpdateListener( this );

            this.viewer = null;
        }
    }


    public void doubleClick( DoubleClickEvent event )
    {
        if ( event.getSelection() instanceof IStructuredSelection )
        {
            Object obj = ( ( IStructuredSelection ) event.getSelection() ).getFirstElement();
            if ( this.viewer.getExpandedState( obj ) )
                this.viewer.collapseToLevel( obj, 1 );
            else if ( ( ( ITreeContentProvider ) this.viewer.getContentProvider() ).hasChildren( obj ) )
            {
                this.viewer.expandToLevel( obj, 1 );
            }
        }
    }


    public void connectionUpdated( ConnectionUpdateEvent connectionUpdateEvent )
    {
        if ( connectionUpdateEvent.getDetail() == ConnectionUpdateEvent.CONNECTION_CLOSED )
        {
            this.viewer.collapseAll();
        }
        else if ( connectionUpdateEvent.getDetail() == ConnectionUpdateEvent.CONNECTION_OPENED )
        {
            this.viewer.refresh( connectionUpdateEvent.getConnection() );
        }
        else
        {
            this.viewer.refresh( connectionUpdateEvent.getConnection() );
        }
    }


    public void entryUpdated( EntryModificationEvent event )
    {

        // Don't handle attribute initalization, could cause double
        // retrieval of children. 
        //
        // When double-clicking an entry two Jobs/Threads are started:
        // - InitializeAttributesJob and
        // - InitializeChildrenJob
        // If the InitializeAttributesJob is finished first the
        // AttributesInitializedEvent is fired. If this causes
        // a refresh of the tree before the children are initialized
        // another InitializeChildrenJob is executed.
        if ( event instanceof AttributesInitializedEvent )
        {
            return;
        }

        this.viewer.refresh( event.getModifiedEntry(), true );

    }


    public void treeCollapsed( TreeExpansionEvent event )
    {
        // System.out.println("treeCollapsed() " + event + ", " +
        // event.getElement());
        if ( event.getElement() instanceof IEntry )
        {
            IEntry entry = ( IEntry ) event.getElement();
            if ( entry.isChildrenInitialized() && entry.hasMoreChildren()
                && entry.getChildrenCount() < entry.getConnection().getCountLimit() )
            {
                entry.setChildrenInitialized( false, entry.getConnection() );
            }
        }
    }


    public void treeExpanded( TreeExpansionEvent event )
    {
    }

}
