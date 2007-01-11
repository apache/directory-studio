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

package org.apache.directory.ldapstudio.browser.ui.views.browser;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.directory.ldapstudio.browser.core.events.AttributesInitializedEvent;
import org.apache.directory.ldapstudio.browser.core.events.BookmarkUpdateEvent;
import org.apache.directory.ldapstudio.browser.core.events.BookmarkUpdateListener;
import org.apache.directory.ldapstudio.browser.core.events.ConnectionUpdateEvent;
import org.apache.directory.ldapstudio.browser.core.events.ConnectionUpdateListener;
import org.apache.directory.ldapstudio.browser.core.events.EntryAddedEvent;
import org.apache.directory.ldapstudio.browser.core.events.EntryModificationEvent;
import org.apache.directory.ldapstudio.browser.core.events.EntryMovedEvent;
import org.apache.directory.ldapstudio.browser.core.events.EntryRenamedEvent;
import org.apache.directory.ldapstudio.browser.core.events.EntryUpdateListener;
import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.events.SearchUpdateEvent;
import org.apache.directory.ldapstudio.browser.core.events.SearchUpdateListener;
import org.apache.directory.ldapstudio.browser.core.model.IBookmark;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.ISearch;
import org.apache.directory.ldapstudio.browser.core.model.ISearchResult;
import org.apache.directory.ldapstudio.browser.ui.actions.SelectionUtils;
import org.apache.directory.ldapstudio.browser.ui.editors.entry.EntryEditorManager;
import org.apache.directory.ldapstudio.browser.ui.editors.searchresult.SearchResultEditorManager;
import org.apache.directory.ldapstudio.browser.ui.widgets.browser.BrowserCategory;
import org.apache.directory.ldapstudio.browser.ui.widgets.browser.BrowserUniversalListener;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;


public class BrowserViewUniversalListener extends BrowserUniversalListener implements ISelectionListener,
    SearchUpdateListener, EntryUpdateListener, ConnectionUpdateListener, BookmarkUpdateListener, IPartListener2
{

    private Map connectionToExpandedElementsMap;

    private Map connectionToSelectedElementMap;

    private BrowserView view;

    private IContextActivation contextActivation;


    public BrowserViewUniversalListener( BrowserView view )
    {
        super( view.getMainWidget().getViewer() );
        this.view = view;

        this.connectionToExpandedElementsMap = new HashMap();
        this.connectionToSelectedElementMap = new HashMap();

        EventRegistry.addSearchUpdateListener( this );
        EventRegistry.addBookmarkUpdateListener( this );
        EventRegistry.addEntryUpdateListener( this );
        EventRegistry.addConnectionUpdateListener( this );
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().addSelectionListener( this );
        view.getSite().getPage().addPartListener( this );
    }


    public void dispose()
    {
        if ( this.view != null )
        {
            view.getSite().getPage().removePartListener( this );
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().removeSelectionListener( this );
            EventRegistry.removeSearchUpdateListener( this );
            EventRegistry.removeBookmarkUpdateListener( this );
            EventRegistry.removeEntryUpdateListener( this );
            EventRegistry.removeConnectionUpdateListener( this );

            this.view = null;
            this.connectionToExpandedElementsMap.clear();
            this.connectionToExpandedElementsMap = null;
            this.connectionToSelectedElementMap.clear();
            this.connectionToSelectedElementMap = null;
        }

        super.dispose();
    }


    public void selectionChanged( IWorkbenchPart part, ISelection selection )
    {

        if ( part.getClass() == BrowserView.class )
        {
            this.sendSelectionHints( selection );
        }

    }


    private void sendSelectionHints( ISelection selection )
    {

        IEntry[] entries = SelectionUtils.getEntries( selection );
        ISearchResult[] searchResults = SelectionUtils.getSearchResults( selection );
        IBookmark[] bookmarks = SelectionUtils.getBookmarks( selection );

        ISearch[] searches = SelectionUtils.getSearches( selection );

        ISearch search = null;
        IEntry entry = null;
        if ( entries.length + searchResults.length + bookmarks.length + searches.length == 1 )
        {
            if ( entries.length == 1 )
            {
                entry = entries[0];
            }
            else if ( searchResults.length == 1 )
            {
                entry = searchResults[0].getEntry();
            }
            else if ( bookmarks.length == 1 )
            {
                entry = bookmarks[0].getEntry();
            }
            else if ( searches.length == 1 )
            {
                search = searches[0];
            }
        }

        EntryEditorManager.setInput( entry );
        SearchResultEditorManager.setInput( search );
    }


    void setInput( IConnection connection )
    {

        // only if another connection is selected
        if ( connection != this.viewer.getInput() )
        {

            IConnection currentConnection = this.viewer.getInput() instanceof IConnection ? ( IConnection ) this.viewer
                .getInput() : null;

            // save expanded elements and selection
            if ( currentConnection != null )
            {
                this.connectionToExpandedElementsMap.put( currentConnection, this.viewer.getExpandedElements() );
                if ( !this.viewer.getSelection().isEmpty() )
                {
                    this.connectionToSelectedElementMap.put( currentConnection, this.viewer.getSelection() );
                }
            }

            // change input
            this.viewer.setInput( connection );
            this.view.getActionGroup().setInput( connection );

            // restore expanded elements and selection
            if ( this.view != null && connection != null )
            {
                if ( this.connectionToExpandedElementsMap.containsKey( connection ) )
                {
                    this.viewer
                        .setExpandedElements( ( Object[] ) this.connectionToExpandedElementsMap.get( connection ) );
                }
                if ( this.connectionToSelectedElementMap.containsKey( connection )
                    && this.view.getSite().getPage().isPartVisible( this.view ) )
                {
                    this.viewer.setSelection( ( ISelection ) this.connectionToSelectedElementMap.get( connection ),
                        true );
                }
            }

            // send selection hint
            this.sendSelectionHints( this.viewer.getSelection() );
        }
    }


    public void connectionUpdated( ConnectionUpdateEvent connectionUpdateEvent )
    {

        if ( connectionUpdateEvent.getDetail() == ConnectionUpdateEvent.CONNECTION_OPENED )
        {
            this.viewer.refresh( connectionUpdateEvent.getConnection() );
            this.viewer.expandToLevel( 2 );

            if ( this.view.getConfiguration().getPreferences().isExpandBaseEntries() )
            {
                Object[] expandedElements = this.viewer.getExpandedElements();
                for ( int i = 0; i < expandedElements.length; i++ )
                {
                    Object object = expandedElements[i];
                    if ( object instanceof BrowserCategory )
                    {
                        BrowserCategory bc = ( BrowserCategory ) object;
                        if ( bc.getType() == BrowserCategory.TYPE_DIT )
                        {
                            this.viewer.expandToLevel( bc, 3 );
                        }
                    }
                }
            }
            // this.viewer.expandToLevel(this.view.getConfiguration().getPreferences().isExpandBaseEntries()?3:2);

        }
        else if ( connectionUpdateEvent.getDetail() == ConnectionUpdateEvent.CONNECTION_CLOSED )
        {
            this.viewer.collapseAll();
            this.connectionToExpandedElementsMap.remove( connectionUpdateEvent.getConnection() );
            this.connectionToSelectedElementMap.remove( connectionUpdateEvent.getConnection() );
            this.sendSelectionHints( this.viewer.getSelection() );
            this.viewer.refresh( connectionUpdateEvent.getConnection() );
        }
        else
        {
            this.viewer.refresh( connectionUpdateEvent.getConnection() );
        }
    }


    public void searchUpdated( SearchUpdateEvent searchUpdateEvent )
    {
        ISearch search = searchUpdateEvent.getSearch();
        this.viewer.refresh();
        if ( search.getSearchResults() != null && search.getSearchResults().length > 0 )
        {
            // this.viewer.refresh(search);
            // this.viewer.expandToLevel(search, TreeViewer.ALL_LEVELS);
        }

        if ( Arrays.asList( search.getConnection().getSearchManager().getSearches() ).contains( search ) )
        {
            this.viewer.setSelection( new StructuredSelection( search ), true );
        }
        else
        {
            Object searchCategory = ( ( ITreeContentProvider ) this.viewer.getContentProvider() ).getParent( search );
            this.viewer.setSelection( new StructuredSelection( searchCategory ), true );
        }
    }


    public void bookmarkUpdated( BookmarkUpdateEvent bookmarkUpdateEvent )
    {
        this.viewer.refresh();
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

        if ( event instanceof EntryAddedEvent )
        {
            this.viewer.refresh( event.getModifiedEntry(), true );
            this.viewer.refresh( event.getModifiedEntry().getParententry(), true );
            this.viewer.setSelection( new StructuredSelection( event.getModifiedEntry() ), true );
        }
        else if ( event instanceof EntryRenamedEvent )
        {
            EntryRenamedEvent ere = ( EntryRenamedEvent ) event;
            this.viewer.refresh( ere.getNewEntry().getParententry(), true );
            this.viewer.refresh( ere.getNewEntry(), true );
            this.viewer.setSelection( new StructuredSelection( ere.getNewEntry() ), true );
        }
        else if ( event instanceof EntryMovedEvent )
        {
            EntryMovedEvent eme = ( EntryMovedEvent ) event;
            this.viewer.refresh( eme.getOldEntry().getParententry(), true );
            this.viewer.refresh( eme.getNewEntry().getParententry(), true );
            this.viewer.refresh( eme.getNewEntry(), true );
            this.viewer.setSelection( new StructuredSelection( eme.getNewEntry() ), true );
        }

        // else if(event instanceof EntryDeletedEvent) {
        // this.viewer.refresh(event.getModifiedEntry(), true);
        // this.viewer.refresh(event.getModifiedEntry().getParententry(), true);
        // this.viewer.setSelection(new
        // StructuredSelection(event.getModifiedEntry().getParententry()),
        // true);
        // }
        // else if(event instanceof EntryRenamedEvent) {
        // EntryRenamedEvent ere = (EntryRenamedEvent) event;
        // this.viewer.refresh(ere.getModifiedEntry(), true);
        // this.viewer.refresh(ere.getOriginalParent(), true);
        // this.viewer.refresh(ere.getNewParent(), true);
        // }
        // else if(event instanceof EntryMovedEvent) {
        // EntryMovedEvent eme = (EntryMovedEvent) event;
        // this.viewer.refresh(eme.getModifiedEntry(), true);
        // this.viewer.refresh(eme.getOriginalParent(), true);
        // this.viewer.refresh(eme.getNewParent(), true);
        // }
        // else {
        // this.viewer.refresh(event.getModifiedEntry(), true);
        // }

        this.viewer.refresh( event.getModifiedEntry(), true );
    }


    public void doubleClick( DoubleClickEvent event )
    {

        // special behaviour for searches:
        // perform search on doouble-click but do not expand the tree.
        // if(event.getSelection() instanceof IStructuredSelection) {
        // Object obj =
        // ((IStructuredSelection)event.getSelection()).getFirstElement();
        //		    
        // // perform search
        // if(obj instanceof ISearch) {
        // ISearch search = (ISearch) obj;
        // if(search.getSearchResults() == null) {
        // IAction action = view.getActionGroup().getRefreshAction();
        // if(action.isEnabled()) {
        // action.run();
        // return;
        // }
        // }
        // }
        // }

        super.doubleClick( event );
    }


    public void partDeactivated( IWorkbenchPartReference partRef )
    {
        if ( partRef.getPart( false ) == this.view && contextActivation != null )
        {

            this.view.getActionGroup().deactivateGlobalActionHandlers();

            IContextService contextService = ( IContextService ) PlatformUI.getWorkbench().getAdapter(
                IContextService.class );
            contextService.deactivateContext( contextActivation );
            contextActivation = null;
        }
    }


    public void partActivated( IWorkbenchPartReference partRef )
    {
        if ( partRef.getPart( false ) == this.view )
        {

            IContextService contextService = ( IContextService ) PlatformUI.getWorkbench().getAdapter(
                IContextService.class );
            contextActivation = contextService
                .activateContext( "org.apache.directory.ldapstudio.browser.action.context" );
            // org.eclipse.ui.contexts.dialogAndWindow
            // org.eclipse.ui.contexts.window
            // org.eclipse.ui.text_editor_context

            this.view.getActionGroup().activateGlobalActionHandlers();
        }
    }


    public void partBroughtToTop( IWorkbenchPartReference partRef )
    {
    }


    public void partClosed( IWorkbenchPartReference partRef )
    {
    }


    public void partOpened( IWorkbenchPartReference partRef )
    {
    }


    public void partHidden( IWorkbenchPartReference partRef )
    {
    }


    public void partVisible( IWorkbenchPartReference partRef )
    {
    }


    public void partInputChanged( IWorkbenchPartReference partRef )
    {
    }

}
