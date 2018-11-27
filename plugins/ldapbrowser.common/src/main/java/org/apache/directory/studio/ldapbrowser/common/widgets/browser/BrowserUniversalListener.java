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

package org.apache.directory.studio.ldapbrowser.common.widgets.browser;


import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionFolder;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;
import org.apache.directory.studio.connection.core.event.ConnectionUpdateListener;
import org.apache.directory.studio.connection.ui.ConnectionUIPlugin;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.core.events.AttributesInitializedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.ChildrenInitializedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EntryModificationEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EntryUpdateListener;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.events.SearchUpdateEvent;
import org.apache.directory.studio.ldapbrowser.core.events.SearchUpdateListener;
import org.apache.directory.studio.ldapbrowser.core.events.SearchUpdateEvent.EventDetail;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IQuickSearch;
import org.apache.directory.studio.ldapbrowser.core.model.IRootDSE;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;


/**
 * The BrowserUniversalListener manages all events for the browser widget.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class BrowserUniversalListener implements ConnectionUpdateListener, EntryUpdateListener, SearchUpdateListener
{
    /** The browser widget */
    protected BrowserWidget widget;

    /** The tree viewer */
    protected TreeViewer viewer;

    /** The tree viewer listener */
    private ITreeViewerListener treeViewerListener = new ITreeViewerListener()
    {
        /**
         * {@inheritDoc}
         *
         * This implementation checks if the collapsed entry more children
         * than currently fetched. If this is the case cached children are
         * cleared an must be fetched newly when expanding the tree.
         *
         * This could happen when first using a search that returns
         * only some of an entry's children.
         */
        public void treeCollapsed( TreeExpansionEvent event )
        {
            if ( event.getElement() instanceof IEntry )
            {
                IEntry entry = ( IEntry ) event.getElement();
                if ( entry.isChildrenInitialized() && entry.hasMoreChildren()
                    && entry.getChildrenCount() < entry.getBrowserConnection().getCountLimit() )
                {
                    entry.setChildrenInitialized( false );
                }
            }
        }


        /**
         * {@inheritDoc}
         */
        public void treeExpanded( TreeExpansionEvent event )
        {
        }
    };

    /** The double click listener. */
    private IDoubleClickListener doubleClickListener = new IDoubleClickListener()
    {

        public void doubleClick( DoubleClickEvent event )
        {
            if ( event.getSelection() instanceof IStructuredSelection )
            {
                Object obj = ( ( IStructuredSelection ) event.getSelection() ).getFirstElement();
                if ( viewer.getExpandedState( obj ) )
                {
                    viewer.collapseToLevel( obj, 1 );
                }
                else if ( ( ( ITreeContentProvider ) viewer.getContentProvider() ).hasChildren( obj ) )
                {
                    viewer.expandToLevel( obj, 1 );
                }
            }
        }

    };


    /**
     * Creates a new instance of BrowserUniversalListener.
     *
     * @param viewer the tree viewer
     */
    public BrowserUniversalListener( BrowserWidget widget )
    {
        this.widget = widget;
        this.viewer = widget.getViewer();

        viewer.addTreeListener( treeViewerListener );
        viewer.addDoubleClickListener( doubleClickListener );

        ConnectionEventRegistry.addConnectionUpdateListener( this, ConnectionUIPlugin.getDefault().getEventRunner() );
        EventRegistry.addEntryUpdateListener( this, BrowserCommonActivator.getDefault().getEventRunner() );
        EventRegistry.addSearchUpdateListener( this, BrowserCommonActivator.getDefault().getEventRunner() );
    }


    /**
     * Disposes this listener.
     */
    public void dispose()
    {
        if ( viewer != null )
        {
            viewer.removeTreeListener( treeViewerListener );
            viewer.removeDoubleClickListener( doubleClickListener );

            ConnectionEventRegistry.removeConnectionUpdateListener( this );
            EventRegistry.removeEntryUpdateListener( this );
            EventRegistry.removeSearchUpdateListener( this );

            viewer = null;
        }
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionOpened(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionOpened( Connection connection )
    {
        viewer.refresh();
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionClosed(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionClosed( Connection connection )
    {
        viewer.collapseAll();
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionUpdated(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionUpdated( Connection connection )
    {
        viewer.refresh();
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionAdded(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionAdded( Connection connection )
    {
        viewer.refresh();
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionRemoved(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionRemoved( Connection connection )
    {
        viewer.refresh();
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionFolderModified(org.apache.directory.studio.connection.core.ConnectionFolder)
     */
    public void connectionFolderModified( ConnectionFolder connectionFolder )
    {
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionFolderAdded(org.apache.directory.studio.connection.core.ConnectionFolder)
     */
    public void connectionFolderAdded( ConnectionFolder connectionFolder )
    {
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionFolderRemoved(org.apache.directory.studio.connection.core.ConnectionFolder)
     */
    public void connectionFolderRemoved( ConnectionFolder connectionFolder )
    {
    }


    /**
     * {@inheritDoc}
     *
     * This implementation refreshes the tree.
     */
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
        if ( event instanceof AttributesInitializedEvent && !( event.getModifiedEntry() instanceof IRootDSE ) )
        {
            return;
        }

        if ( event instanceof ChildrenInitializedEvent )
        {
            boolean expandedState = viewer.getExpandedState( event.getModifiedEntry() );
            viewer.collapseToLevel( event.getModifiedEntry(), TreeViewer.ALL_LEVELS );
            if ( expandedState )
            {
                viewer.expandToLevel( event.getModifiedEntry(), 1 );
            }
            viewer.refresh( event.getModifiedEntry(), true );
        }
        else
        {
            viewer.refresh( event.getModifiedEntry(), true );
        }
    }


    /**
     * {@inheritDoc}
     *
     * This implementation refreshes the tree and selects the search.
     */
    public void searchUpdated( SearchUpdateEvent searchUpdateEvent )
    {
        ISearch search = searchUpdateEvent.getSearch();

        if ( ( search instanceof IQuickSearch ) && ( searchUpdateEvent.getDetail() == EventDetail.SEARCH_REMOVED ) )
        {
            if ( widget.getQuickSearch() == search )
            {
                widget.setQuickSearch( null );
            }
        }

        viewer.refresh();

        if ( ( search instanceof IQuickSearch ) && ( searchUpdateEvent.getDetail() != EventDetail.SEARCH_REMOVED ) )
        {
            viewer.setSelection( new StructuredSelection( search ), true );
            viewer.expandToLevel( search, 1 );
        }
    }
}
