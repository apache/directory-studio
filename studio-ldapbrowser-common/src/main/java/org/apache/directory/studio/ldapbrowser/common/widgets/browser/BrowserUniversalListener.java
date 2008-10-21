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
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;
import org.apache.directory.studio.connection.core.event.ConnectionUpdateListener;
import org.apache.directory.studio.connection.ui.ConnectionUIPlugin;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.core.events.AttributesInitializedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EntryModificationEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EntryUpdateListener;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;


/**
 * The BrowserUniversalListener manages all events for the browser widget.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BrowserUniversalListener implements ConnectionUpdateListener, EntryUpdateListener
{

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
    public BrowserUniversalListener( TreeViewer viewer )
    {
        this.viewer = viewer;

        viewer.addTreeListener( treeViewerListener );
        viewer.addDoubleClickListener( doubleClickListener );

        ConnectionEventRegistry.addConnectionUpdateListener( this, ConnectionUIPlugin.getDefault().getEventRunner() );
        EventRegistry.addEntryUpdateListener( this, BrowserCommonActivator.getDefault().getEventRunner() );
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
        if ( event instanceof AttributesInitializedEvent )
        {
            return;
        }

        viewer.refresh( event.getModifiedEntry(), true );

    }

}
