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

package org.apache.directory.ldapstudio.browser.common.actions.proxy;


import org.apache.directory.ldapstudio.browser.common.BrowserCommonActivator;
import org.apache.directory.ldapstudio.browser.common.actions.BrowserAction;
import org.apache.directory.ldapstudio.browser.common.actions.SelectionUtils;
import org.apache.directory.ldapstudio.browser.core.events.BookmarkUpdateEvent;
import org.apache.directory.ldapstudio.browser.core.events.BookmarkUpdateListener;
import org.apache.directory.ldapstudio.browser.core.events.ConnectionUpdateEvent;
import org.apache.directory.ldapstudio.browser.core.events.ConnectionUpdateListener;
import org.apache.directory.ldapstudio.browser.core.events.EntryModificationEvent;
import org.apache.directory.ldapstudio.browser.core.events.EntryUpdateListener;
import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.events.SearchUpdateEvent;
import org.apache.directory.ldapstudio.browser.core.events.SearchUpdateListener;
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

        this.selectionProvider.addSelectionChangedListener( this );
        // PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().addSelectionListener(this);

        EventRegistry.addConnectionUpdateListener( this, BrowserCommonActivator.getDefault().getEventRunner() );
        EventRegistry.addEntryUpdateListener( this, BrowserCommonActivator.getDefault().getEventRunner() );
        EventRegistry.addSearchUpdateListener( this, BrowserCommonActivator.getDefault().getEventRunner() );
        EventRegistry.addBookmarkUpdateListener( this, BrowserCommonActivator.getDefault().getEventRunner() );

        this.updateAction();

    }


    protected BrowserActionProxy( ISelectionProvider selectionProvider, BrowserAction action )
    {
        this( selectionProvider, action, Action.AS_PUSH_BUTTON );
    }


    public void dispose()
    {
        EventRegistry.removeConnectionUpdateListener( this );
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
            this.action.entryUpdated( entryModificationEvent );
            this.updateAction();
        }
    }


    public void searchUpdated( SearchUpdateEvent searchUpdateEvent )
    {
        if ( !this.isDisposed() )
        {
            this.action.searchUpdated( searchUpdateEvent );
            this.updateAction();
        }
    }


    public void bookmarkUpdated( BookmarkUpdateEvent bookmarkUpdateEvent )
    {
        if ( !this.isDisposed() )
        {
            this.action.bookmarkUpdated( bookmarkUpdateEvent );
            this.updateAction();
        }
    }


    public final void connectionUpdated( ConnectionUpdateEvent connectionUpdateEvent )
    {
        if ( !this.isDisposed() )
        {
            this.action.connectionUpdated( connectionUpdateEvent );
            this.updateAction();
        }
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

            this.action.setSelectedConnections( SelectionUtils.getConnections( selection ) );

            this.action.setSelectedBrowserViewCategories( SelectionUtils.getBrowserViewCategories( selection ) );
            this.action.setSelectedEntries( SelectionUtils.getEntries( selection ) );
            this.action.setSelectedBrowserEntryPages( SelectionUtils.getBrowserEntryPages( selection ) );
            this.action.setSelectedSearchResults( SelectionUtils.getSearchResults( selection ) );
            this.action.setSelectedBrowserSearchResultPages( SelectionUtils.getBrowserSearchResultPages( selection ) );
            this.action.setSelectedBookmarks( SelectionUtils.getBookmarks( selection ) );

            this.action.setSelectedSearches( SelectionUtils.getSearches( selection ) );

            this.action.setSelectedAttributes( SelectionUtils.getAttributes( selection ) );
            this.action.setSelectedAttributeHierarchies( SelectionUtils.getAttributeHierarchie( selection ) );
            this.action.setSelectedValues( SelectionUtils.getValues( selection ) );
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
        if ( !this.isDisposed() )
        {
            this.action.run();
        }
    }


    public BrowserAction getAction()
    {
        return action;
    }

}
