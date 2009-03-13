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
        this( selectionProvider, action, action.getStyle() );
    }


    public void dispose()
    {
        ConnectionEventRegistry.removeConnectionUpdateListener( this );
        EventRegistry.removeEntryUpdateListener( this );
        EventRegistry.removeSearchUpdateListener( this );
        EventRegistry.removeBookmarkUpdateListener( this );
        selectionProvider.removeSelectionChangedListener( this );
        // PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().removeSelectionListener(this);

        action.dispose();
        action = null;
    }


    public boolean isDisposed()
    {
        return action == null;
    }


    public final void entryUpdated( EntryModificationEvent entryModificationEvent )
    {
        if ( !isDisposed() )
        {
            updateAction();
        }
    }


    public void searchUpdated( SearchUpdateEvent searchUpdateEvent )
    {
        if ( !isDisposed() )
        {
            updateAction();
        }
    }


    public void bookmarkUpdated( BookmarkUpdateEvent bookmarkUpdateEvent )
    {
        if ( !isDisposed() )
        {
            updateAction();
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
            action.setInput( input );
            selectionChanged( new SelectionChangedEvent( selectionProvider, new StructuredSelection() ) );
            // this.updateAction();
        }
    }


    public void selectionChanged( SelectionChangedEvent event )
    {
        if ( !isDisposed() )
        {
            ISelection selection = event.getSelection();

            action.setSelectedBrowserViewCategories( BrowserSelectionUtils.getBrowserViewCategories( selection ) );
            action.setSelectedEntries( BrowserSelectionUtils.getEntries( selection ) );
            action.setSelectedBrowserEntryPages( BrowserSelectionUtils.getBrowserEntryPages( selection ) );
            action.setSelectedSearchResults( BrowserSelectionUtils.getSearchResults( selection ) );
            action.setSelectedBrowserSearchResultPages( BrowserSelectionUtils.getBrowserSearchResultPages( selection ) );
            action.setSelectedBookmarks( BrowserSelectionUtils.getBookmarks( selection ) );

            action.setSelectedSearches( BrowserSelectionUtils.getSearches( selection ) );

            action.setSelectedAttributes( BrowserSelectionUtils.getAttributes( selection ) );
            action.setSelectedAttributeHierarchies( BrowserSelectionUtils.getAttributeHierarchie( selection ) );
            action.setSelectedValues( BrowserSelectionUtils.getValues( selection ) );

            action.setSelectedProperties( BrowserSelectionUtils.getProperties( selection ) );

            updateAction();
        }
    }


    public void updateAction()
    {
        if ( !isDisposed() )
        {
            setText( action.getText() );
            setToolTipText( action.getText() );
            setEnabled( action.isEnabled() );
            setImageDescriptor( action.getImageDescriptor() );
            setChecked( action.isChecked() );
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
