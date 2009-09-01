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

package org.apache.directory.studio.ldapbrowser.ui.editors.entry;


import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.common.actions.BrowserSelectionUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.entryeditor.EntryEditorWidgetUniversalListener;
import org.apache.directory.studio.ldapbrowser.core.events.EntryModificationEvent;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.ui.views.browser.BrowserView;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.INullSelectionListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;


/**
 * The EntryEditorUniversalListener manages all events for the entry editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class EntryEditorUniversalListener extends EntryEditorWidgetUniversalListener
{

    /** The entry editor */
    private EntryEditor entryEditor;

    /** Token used to activate and deactivate shortcuts in the editor */
    private IContextActivation contextActivation;

    /** Listener that listens for selections of IEntry, ISeachResult and IBookmark objects. */
    private INullSelectionListener entrySelectionListener = new INullSelectionListener()
    {
        /**
         * {@inheritDoc}
         * 
         * This implementation sets the editor's input when a entry, search result or bookmark is selected.
         */
        public void selectionChanged( IWorkbenchPart part, ISelection selection )
        {
            if ( entryEditor != null && part != null )
            {
                // TODO: should be removed, opening the editor and changing input will be completely managed by the browser view 
//                if ( entryEditor.getSite().getWorkbenchWindow() == part.getSite().getWorkbenchWindow() )
//                {
//                    IEntry[] entries = BrowserSelectionUtils.getEntries( selection );
//                    ISearchResult[] searchResults = BrowserSelectionUtils.getSearchResults( selection );
//                    IBookmark[] bookmarks = BrowserSelectionUtils.getBookmarks( selection );
//                    Object[] objects = BrowserSelectionUtils.getObjects( selection );
//                    if ( entries.length + searchResults.length + bookmarks.length == 1 && objects.length == 1 )
//                    {
//                        if ( entries.length == 1 )
//                        {
//                            entryEditor.setInput( new EntryEditorInput( entries[0] ) );
//                        }
//                        else if ( searchResults.length == 1 )
//                        {
//                            entryEditor.setInput( new EntryEditorInput( searchResults[0] ) );
//                        }
//                        else if ( bookmarks.length == 1 )
//                        {
//                            entryEditor.setInput( new EntryEditorInput( bookmarks[0] ) );
//                        }
//                    }
//                    else
//                    {
//                        entryEditor.setInput( new EntryEditorInput( ( IEntry ) null ) );
//                    }
//                }
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
            if ( partRef.getPart( false ) == entryEditor && contextActivation != null )
            {

                entryEditor.getActionGroup().deactivateGlobalActionHandlers();

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
            if ( partRef.getPart( false ) == entryEditor )
            {

                IContextService contextService = ( IContextService ) PlatformUI.getWorkbench().getAdapter(
                    IContextService.class );
                contextActivation = contextService.activateContext( BrowserCommonConstants.CONTEXT_WINDOWS );
                // org.eclipse.ui.contexts.dialogAndWindow
                // org.eclipse.ui.contexts.window
                // org.eclipse.ui.text_editor_context

                entryEditor.getActionGroup().activateGlobalActionHandlers();
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


    /**
     * Creates a new instance of EntryEditorUniversalListener.
     *
     * @param entryEditor the entry editor
     */
    public EntryEditorUniversalListener( EntryEditor entryEditor )
    {
        super( entryEditor.getMainWidget().getViewer(), entryEditor.getConfiguration(), entryEditor.getActionGroup(),
            entryEditor.getActionGroup().getOpenDefaultEditorAction() );
        this.entryEditor = entryEditor;

        // register listeners
        entryEditor.getSite().getPage().addPartListener( partListener );
        entryEditor.getSite().getWorkbenchWindow().getSelectionService().addPostSelectionListener( BrowserView.getId(),
            entrySelectionListener );
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        if ( entryEditor != null )
        {
            // deregister listeners
            entryEditor.getSite().getPage().removePartListener( partListener );
            entryEditor.getSite().getWorkbenchWindow().getSelectionService().removePostSelectionListener(
                BrowserView.getId(), entrySelectionListener );
            entryEditor = null;
        }

        super.dispose();
    }


    /**
     * {@inheritDoc}
     *
     * This implementation updates the outline page when the entry is updated.
     */
    public void entryUpdated( EntryModificationEvent event )
    {
        super.entryUpdated( event );
        expandFoldedAttributes();

        EntryEditorOutlinePage outlinePage = ( EntryEditorOutlinePage ) entryEditor
            .getAdapter( IContentOutlinePage.class );
        if ( outlinePage != null )
        {
            outlinePage.refresh();
        }
    }

}
