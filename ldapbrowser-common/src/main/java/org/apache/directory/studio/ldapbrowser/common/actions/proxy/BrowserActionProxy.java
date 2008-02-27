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

package org.apache.directory.studio.ldapbrowser.common.actions.proxy;


import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionFolder;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;
import org.apache.directory.studio.connection.core.event.ConnectionUpdateListener;
import org.apache.directory.studio.connection.ui.ConnectionUIPlugin;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.actions.BrowserAction;
import org.apache.directory.studio.ldapbrowser.common.actions.BrowserSelectionUtils;
import org.apache.directory.studio.ldapbrowser.core.events.BookmarkUpdateEvent;
import org.apache.directory.studio.ldapbrowser.core.events.BookmarkUpdateListener;
import org.apache.directory.studio.ldapbrowser.core.events.EntryModificationEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EntryUpdateListener;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.events.SearchUpdateEvent;
import org.apache.directory.studio.ldapbrowser.core.events.SearchUpdateListener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;


public abstract class BrowserActionProxy extends Action implements ISelectionChangedListener, EntryUpdateListener,
    SearchUpdateListener, BookmarkUpdateListener, ConnectionUpdateListener
{

    protected BrowserAction action;

    protected ISelectionProvider selectionProvider;


    protected BrowserActionProxy( ISelectionProvider selectionProvider, BrowserAction action, int style )
    {
        super( action.getText(), style );
        this.selectionProvider = selectionProvider;
        this.action = action;

        super.setImageDescriptor( action.getImageDescriptor() );
        super.setActionDefinitionId( action.getCommandId() );

        selectionProvider.addSelectionChangedListener( this );
        // PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().addSelectionListener(this);

        ConnectionEventRegistry.addConnectionUpdateListener( this, ConnectionUIPlugin.getDefault().getEventRunner() );
        EventRegistry.addEntryUpdateListener( this, BrowserCommonActivator.getDefault().getEventRunner() );
        EventRegistry.addSearchUpdateListener( this, BrowserCommonActivator.getDefault().getEventRunner() );
        EventRegistry.addBookmarkUpdateListener( this, BrowserCommonActivator.getDefault().getEventRunner() );

        updateAction();
    }


    protected BrowserActionProxy( ISelectionProvider selectionProvider, BrowserAction action )
    {
        this( selectionProvider, action, Action.AS_PUSH_BUTTON );
    }


    public void dispose()
    {
        ConnectionEventRegistry.removeConnectionUpdateListener( this );
        EventRegistry.removeEntryUpdateListener( this );
        EventRegistry.removeSearchUpdateListener( this );
        EventRegistry.removeBookmarkUpdateListener( this );
        this.selectionProvider.removeSelectionChangedListener( this );
        // PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().removeSelectionListener(this);

        this.action.dispose();
        this.action = null;
    }


    public boolean isDisposed()
    {
        return this.action == null;
    }


    public final void entryUpdated( EntryModificationEvent entryModificationEvent )
    {
        if ( !this.isDisposed() )
        {
            this.updateAction();
        }
    }


    public void searchUpdated( SearchUpdateEvent searchUpdateEvent )
    {
        if ( !this.isDisposed() )
        {
            this.updateAction();
        }
    }


    public void bookmarkUpdated( BookmarkUpdateEvent bookmarkUpdateEvent )
    {
        if ( !this.isDisposed() )
        {
            this.updateAction();
        }
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionUpdated(org.apache.directory.studio.connection.core.Connection)
     */
    public final void connectionUpdated( Connection connection )
    {
        if ( !isDisposed() )
        {
            updateAction();
        }
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionAdded(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionAdded( Connection connection )
    {
        connectionUpdated( connection );
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionRemoved(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionRemoved( Connection connection )
    {
        connectionUpdated( connection );
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionOpened(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionOpened( Connection connection )
    {
        connectionUpdated( connection );
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionClosed(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionClosed( Connection connection )
    {
        connectionUpdated( connection );
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionFolderModified(org.apache.directory.studio.connection.core.ConnectionFolder)
     */
    public void connectionFolderModified( ConnectionFolder connectionFolder )
    {
        connectionUpdated( null );
    }


    public void inputChanged( Object input )
    {
        if ( !this.isDisposed() )
        {
            this.action.setInput( input );
            this.selectionChanged( new SelectionChangedEvent( this.selectionProvider, new StructuredSelection() ) );
            // this.updateAction();
        }
    }


    public void selectionChanged( SelectionChangedEvent event )
    {
        if ( !this.isDisposed() )
        {
            ISelection selection = event.getSelection();

            this.action.setSelectedBrowserViewCategories( BrowserSelectionUtils.getBrowserViewCategories( selection ) );
            this.action.setSelectedEntries( BrowserSelectionUtils.getEntries( selection ) );
            this.action.setSelectedBrowserEntryPages( BrowserSelectionUtils.getBrowserEntryPages( selection ) );
            this.action.setSelectedSearchResults( BrowserSelectionUtils.getSearchResults( selection ) );
            this.action.setSelectedBrowserSearchResultPages( BrowserSelectionUtils
                .getBrowserSearchResultPages( selection ) );
            this.action.setSelectedBookmarks( BrowserSelectionUtils.getBookmarks( selection ) );

            this.action.setSelectedSearches( BrowserSelectionUtils.getSearches( selection ) );

            this.action.setSelectedAttributes( BrowserSelectionUtils.getAttributes( selection ) );
            this.action.setSelectedAttributeHierarchies( BrowserSelectionUtils.getAttributeHierarchie( selection ) );
            this.action.setSelectedValues( BrowserSelectionUtils.getValues( selection ) );

            this.action.setSelectedProperties( BrowserSelectionUtils.getProperties( selection ) );

            this.updateAction();
        }
    }


    public void updateAction()
    {
        if ( !this.isDisposed() )
        {
            this.setText( this.action.getText() );
            this.setToolTipText( this.action.getText() );
            this.setEnabled( this.action.isEnabled() );
            this.setImageDescriptor( this.action.getImageDescriptor() );
        }
    }


    public void run()
    {
        if ( !isDisposed() )
        {
            action.run();
        }
    }


    public BrowserAction getAction()
    {
        return action;
    }

}
