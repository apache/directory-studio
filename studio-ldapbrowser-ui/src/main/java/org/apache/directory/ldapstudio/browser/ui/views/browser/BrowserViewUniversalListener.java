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

import org.apache.directory.ldapstudio.browser.ui.editors.entry.EntryEditor;
import org.apache.directory.ldapstudio.browser.ui.editors.entry.EntryEditorInput;
import org.apache.directory.ldapstudio.browser.ui.editors.searchresult.SearchResultEditor;
import org.apache.directory.ldapstudio.browser.ui.editors.searchresult.SearchResultEditorInput;
import org.apache.directory.ldapstudio.browser.ui.views.connection.ConnectionView;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.actions.SelectionUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.browser.BrowserUniversalListener;
import org.apache.directory.studio.ldapbrowser.core.events.AttributesInitializedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.BookmarkUpdateEvent;
import org.apache.directory.studio.ldapbrowser.core.events.BookmarkUpdateListener;
import org.apache.directory.studio.ldapbrowser.core.events.BulkModificationEvent;
import org.apache.directory.studio.ldapbrowser.core.events.ConnectionUpdateEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EntryAddedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EntryModificationEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EntryMovedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EntryRenamedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.events.SearchUpdateEvent;
import org.apache.directory.studio.ldapbrowser.core.events.SearchUpdateListener;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IRootDSE;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.INullSelectionListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;


/**
 * The BrowserViewUniversalListener manages all events for the browser view.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BrowserViewUniversalListener extends BrowserUniversalListener implements SearchUpdateListener,
    BookmarkUpdateListener
{

    /** This map contains all expanded elements for a particular connection */
    private Map<IConnection, Object[]> connectionToExpandedElementsMap;

    /** This map contains all selected elements for a particular connection */
    private Map<IConnection, ISelection> connectionToSelectedElementMap;

    /** The browser view */
    private BrowserView view;

    /** Token used to activate and deactivate shortcuts in the view */
    private IContextActivation contextActivation;

    /** Listener that listens for selections of connections */
    private INullSelectionListener connectionSelectionListener = new INullSelectionListener()
    {
        /**
         * {@inheritDoc}
         *
         * This implementation sets the input when another connection was selected.
         */
        public void selectionChanged( IWorkbenchPart part, ISelection selection )
        {
            if ( view != null && part != null )
            {
                if ( view.getSite().getWorkbenchWindow() == part.getSite().getWorkbenchWindow() )
                {
                    IConnection[] connections = SelectionUtils.getConnections( selection );
                    if ( connections.length == 1 )
                    {
                        setInput( connections[0] );
                    }
                    else
                    {
                        setInput( null );
                    }
                }
            }
        }
    };

    /** The part listener used to activate and deactivate the shortcuts */
    private IPartListener2 partListener = new IPartListener2()
    {
        /**
         * {@inheritDoc}
         *
         * This implementation deactivates the shortcuts when the part is deactivated.
         */
        public void partDeactivated( IWorkbenchPartReference partRef )
        {
            if ( partRef.getPart( false ) == view && contextActivation != null )
            {

                view.getActionGroup().deactivateGlobalActionHandlers();

                IContextService contextService = ( IContextService ) PlatformUI.getWorkbench().getAdapter(
                    IContextService.class );
                contextService.deactivateContext( contextActivation );
                contextActivation = null;
            }
        }


        /**
         * {@inheritDoc}
         *
         * This implementation activates the shortcuts when the part is activated.
         */
        public void partActivated( IWorkbenchPartReference partRef )
        {
            if ( partRef.getPart( false ) == view )
            {

                IContextService contextService = ( IContextService ) PlatformUI.getWorkbench().getAdapter(
                    IContextService.class );
                contextActivation = contextService
                    .activateContext( "org.apache.directory.ldapstudio.browser.action.context" );
                // org.eclipse.ui.contexts.dialogAndWindow
                // org.eclipse.ui.contexts.window
                // org.eclipse.ui.text_editor_context

                view.getActionGroup().activateGlobalActionHandlers();
            }
        }


        /**
         * {@inheritDoc}
         */
        public void partBroughtToTop( IWorkbenchPartReference partRef )
        {
        }


        /**
         * {@inheritDoc}
         */
        public void partClosed( IWorkbenchPartReference partRef )
        {
        }


        /**
         * {@inheritDoc}
         */
        public void partOpened( IWorkbenchPartReference partRef )
        {
        }


        /**
         * {@inheritDoc}
         */
        public void partHidden( IWorkbenchPartReference partRef )
        {
        }


        /**
         * {@inheritDoc}
         */
        public void partVisible( IWorkbenchPartReference partRef )
        {
        }


        /**
         * {@inheritDoc}
         */
        public void partInputChanged( IWorkbenchPartReference partRef )
        {
        }
    };

    /** This listener is used to ensure that the entry editor and search result editor are opened
     when an object in the browser view is selected */
    private ISelectionChangedListener viewerSelectionListener = new ISelectionChangedListener()
    {
        /**
         * {@inheritDoc}
         */
        public void selectionChanged( SelectionChangedEvent event )
        {
            ensureEditorsVisible( event.getSelection() );
        }
    };


    /**
     * Creates a new instance of BrowserViewUniversalListener.
     *
     * @param view the browser view
     */
    public BrowserViewUniversalListener( BrowserView view )
    {
        super( view.getMainWidget().getViewer() );
        this.view = view;

        // create maps
        connectionToExpandedElementsMap = new HashMap<IConnection, Object[]>();
        connectionToSelectedElementMap = new HashMap<IConnection, ISelection>();

        // register listeners
        EventRegistry.addSearchUpdateListener( this, BrowserCommonActivator.getDefault().getEventRunner() );
        EventRegistry.addBookmarkUpdateListener( this, BrowserCommonActivator.getDefault().getEventRunner() );
        EventRegistry.addEntryUpdateListener( this, BrowserCommonActivator.getDefault().getEventRunner() );
        EventRegistry.addConnectionUpdateListener( this, BrowserCommonActivator.getDefault().getEventRunner() );

        view.getSite().getPage().addPartListener( partListener );
        view.getSite().getWorkbenchWindow().getSelectionService().addPostSelectionListener( ConnectionView.getId(),
            connectionSelectionListener );

        viewer.addSelectionChangedListener( viewerSelectionListener );
    }


    /**
     * Ensures that the entry editor or the search result editor are
     * opended and ready to show the given selection.
     *
     * @param selection the browser's selection.
     */
    private void ensureEditorsVisible( ISelection selection )
    {
        if ( view != null )
        {
            IEntry[] entries = SelectionUtils.getEntries( selection );
            ISearchResult[] searchResults = SelectionUtils.getSearchResults( selection );
            IBookmark[] bookmarks = SelectionUtils.getBookmarks( selection );
            ISearch[] searches = SelectionUtils.getSearches( selection );

            if ( entries.length + searchResults.length + bookmarks.length + searches.length == 1 )
            {
                if ( entries.length == 1 )
                {
                    try
                    {
                        EntryEditorInput input = new EntryEditorInput( entries[0] );
                        view.getSite().getPage().openEditor( input, EntryEditor.getId(), false );
                    }
                    catch ( PartInitException e )
                    {
                    }
                }
                else if ( searchResults.length == 1 )
                {
                    try
                    {
                        EntryEditorInput input = new EntryEditorInput( searchResults[0] );
                        view.getSite().getPage().openEditor( input, EntryEditor.getId(), false );
                    }
                    catch ( PartInitException e )
                    {
                    }
                }
                else if ( bookmarks.length == 1 )
                {
                    try
                    {
                        EntryEditorInput input = new EntryEditorInput( bookmarks[0] );
                        view.getSite().getPage().openEditor( input, EntryEditor.getId(), false );
                    }
                    catch ( PartInitException e )
                    {
                    }
                }
                else if ( searches.length == 1 )
                {
                    try
                    {
                        SearchResultEditorInput input = new SearchResultEditorInput( searches[0] );
                        view.getSite().getPage().openEditor( input, SearchResultEditor.getId(), false );
                    }
                    catch ( PartInitException e )
                    {
                    }
                }
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        if ( view != null )
        {
            EventRegistry.removeSearchUpdateListener( this );
            EventRegistry.removeBookmarkUpdateListener( this );
            EventRegistry.removeEntryUpdateListener( this );
            EventRegistry.removeConnectionUpdateListener( this );

            view.getSite().getPage().removePartListener( partListener );
            view.getSite().getWorkbenchWindow().getSelectionService().removePostSelectionListener(
                ConnectionView.getId(), connectionSelectionListener );

            view = null;
            connectionToExpandedElementsMap.clear();
            connectionToExpandedElementsMap = null;
            connectionToSelectedElementMap.clear();
            connectionToSelectedElementMap = null;
        }

        super.dispose();
    }


    /**
     * Sets the input to the viewer and saves/restores the expanded and selected elements.
     *
     * @param connection the connection input
     */
    void setInput( IConnection connection )
    {
        // only if another connection is selected
        if ( connection != viewer.getInput() )
        {

            IConnection currentConnection = viewer.getInput() instanceof IConnection ? ( IConnection ) viewer
                .getInput() : null;

            // save expanded elements and selection
            if ( currentConnection != null )
            {
                connectionToExpandedElementsMap.put( currentConnection, viewer.getExpandedElements() );
                if ( !viewer.getSelection().isEmpty() )
                {
                    connectionToSelectedElementMap.put( currentConnection, viewer.getSelection() );
                }
            }

            // change input
            viewer.setInput( connection );
            view.getActionGroup().setInput( connection );

            // restore expanded elements and selection
            if ( view != null && connection != null )
            {
                if ( connectionToExpandedElementsMap.containsKey( connection ) )
                {
                    viewer.setExpandedElements( ( Object[] ) connectionToExpandedElementsMap.get( connection ) );
                }
                if ( connectionToSelectedElementMap.containsKey( connection )
                    && this.view.getSite().getPage().isPartVisible( view ) )
                {
                    viewer.setSelection( ( ISelection ) connectionToSelectedElementMap.get( connection ), true );
                }
            }
        }
    }


    /**
     * {@inheritDoc}
     *
     * This implementation refreshes the tree and expands/collapses the
     * tree when the connection is opened/closed.
     */
    public void connectionUpdated( ConnectionUpdateEvent connectionUpdateEvent )
    {
        if ( connectionUpdateEvent.getDetail() == ConnectionUpdateEvent.EventDetail.CONNECTION_OPENED )
        {
            // expand viewer
            viewer.refresh( connectionUpdateEvent.getConnection() );
            viewer.expandToLevel( 2 );

            // expand root DSE to show base entries
            IRootDSE rootDSE = connectionUpdateEvent.getConnection().getRootDSE();
            viewer.expandToLevel( rootDSE, 1 );

            // expand base entries, if requested
            if ( view.getConfiguration().getPreferences().isExpandBaseEntries() )
            {
                viewer.expandToLevel( rootDSE, 2 );
            }
        }
        else if ( connectionUpdateEvent.getDetail() == ConnectionUpdateEvent.EventDetail.CONNECTION_CLOSED )
        {
            viewer.collapseAll();
            connectionToExpandedElementsMap.remove( connectionUpdateEvent.getConnection() );
            connectionToSelectedElementMap.remove( connectionUpdateEvent.getConnection() );
            viewer.refresh( connectionUpdateEvent.getConnection() );
        }
        else
        {
            viewer.refresh( connectionUpdateEvent.getConnection() );
        }
    }


    /**
     * {@inheritDoc}
     *
     * This viewer selects the updated search.
     */
    public void searchUpdated( SearchUpdateEvent searchUpdateEvent )
    {
        ISearch search = searchUpdateEvent.getSearch();
        viewer.refresh();

        if ( Arrays.asList( search.getConnection().getSearchManager().getSearches() ).contains( search ) )
        {
            viewer.setSelection( new StructuredSelection( search ), true );
        }
        else
        {
            Object searchCategory = ( ( ITreeContentProvider ) viewer.getContentProvider() ).getParent( search );
            viewer.setSelection( new StructuredSelection( searchCategory ), true );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void bookmarkUpdated( BookmarkUpdateEvent bookmarkUpdateEvent )
    {
        viewer.refresh();
    }


    /**
     * {@inheritDoc}
     *
     * This implementation refreshes the tree and
     * selects an entry depending on the event type.
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

        if ( event instanceof EntryAddedEvent )
        {
            viewer.refresh( event.getModifiedEntry(), true );
            viewer.refresh( event.getModifiedEntry().getParententry(), true );
            viewer.setSelection( new StructuredSelection( event.getModifiedEntry() ), true );
        }
        else if ( event instanceof EntryRenamedEvent )
        {
            EntryRenamedEvent ere = ( EntryRenamedEvent ) event;
            viewer.refresh( ere.getNewEntry().getParententry(), true );
            viewer.refresh( ere.getNewEntry(), true );
            viewer.setSelection( new StructuredSelection( ere.getNewEntry() ), true );
        }
        else if ( event instanceof EntryMovedEvent )
        {
            EntryMovedEvent eme = ( EntryMovedEvent ) event;
            viewer.refresh( eme.getOldEntry().getParententry(), true );
            viewer.refresh( eme.getNewEntry().getParententry(), true );
            viewer.refresh( eme.getNewEntry(), true );
            viewer.setSelection( new StructuredSelection( eme.getNewEntry() ), true );
        }
        else if ( event instanceof BulkModificationEvent )
        {
            viewer.refresh();
        }

        viewer.refresh( event.getModifiedEntry(), true );
    }

}
