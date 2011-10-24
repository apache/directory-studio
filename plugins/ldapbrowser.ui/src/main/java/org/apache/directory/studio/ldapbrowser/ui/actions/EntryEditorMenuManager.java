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
package org.apache.directory.studio.ldapbrowser.ui.actions;


import java.util.Collection;

import org.apache.directory.studio.entryeditors.EntryEditorExtension;
import org.apache.directory.studio.entryeditors.EntryEditorManager;
import org.apache.directory.studio.ldapbrowser.common.actions.BrowserSelectionUtils;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;


public class EntryEditorMenuManager extends MenuManager implements IMenuListener
{
    /** The selection provider */
    private ISelectionProvider selectionProvider;

    /** The OpenEntryEditorsPreferencePageAction */
    private OpenEntryEditorsPreferencePageAction openEntryEditorsPreferencePageAction;


    /**
     * Creates a menu manager.  The text and id are <code>null</code>.
     * Typically used for creating a context menu, where it doesn't need to be referred to by id.
     */
    public EntryEditorMenuManager( ISelectionProvider selectionProvider )
    {
        super( Messages.getString( "EntryEditorMenuManager.OpenWith" ) ); //$NON-NLS-1$
        this.selectionProvider = selectionProvider;
        openEntryEditorsPreferencePageAction = new OpenEntryEditorsPreferencePageAction();
        addMenuListener( this );
    }


    /**
     * {@inheritDoc}
     */
    public void menuAboutToShow( IMenuManager manager )
    {
        // As the Menu Manager is dynamic, we need to 
        // remove all the previously added actions
        removeAll();

        // Getting the currently selected entry
        IEntry selectedEntry = getCurrentSelection();
        if ( selectedEntry != null )
        {
            // Getting the entry editors and creating an action for each one
            // that can handle the entry
            Collection<EntryEditorExtension> entryEditors = BrowserUIPlugin.getDefault().getEntryEditorManager()
                .getSortedEntryEditorExtensions();
            for ( EntryEditorExtension entryEditor : entryEditors )
            {
                // Verifying that the editor can handle the entry
                if ( entryEditor.getEditorInstance().canHandle( selectedEntry ) )
                {
                    // Creating the action associated with the entry editor
                    add( createAction( entryEditor ) );
                }
            }
        }

        // Separator
        add( new Separator() );

        // Preferences Action
        add( openEntryEditorsPreferencePageAction );
    }


    /**
     * Gets the currently selected entry.
     *
     * @return
     *      the currently selected entry
     */
    private IEntry getCurrentSelection()
    {
        StructuredSelection structuredSelection = ( StructuredSelection ) selectionProvider.getSelection();
        if ( !structuredSelection.isEmpty() )
        {
            Object selection = structuredSelection.getFirstElement();
            if ( selection instanceof IEntry )
            {
                return ( IEntry ) selection;
            }
            else if ( selection instanceof ISearchResult )
            {
                return ( ( ISearchResult ) selection ).getEntry();
            }
            else if ( selection instanceof IBookmark )
            {
                return ( ( IBookmark ) selection ).getEntry();
            }
        }

        return null;
    }


    /**
     * Creates an action for the given entry editor.
     *
     * @param entryEditorExtension
     *      the entry editor
     * @return
     *      an action associated with the entry editor
     */
    private IAction createAction( final EntryEditorExtension entryEditorExtension )
    {
        Action action = new Action( entryEditorExtension.getName(), entryEditorExtension.getIcon() )
        {
            public void run()
            {
                EntryEditorManager entryEditorManager = BrowserUIPlugin.getDefault().getEntryEditorManager();
                ISelection selection = selectionProvider.getSelection();
                IEntry[] selectedEntries = BrowserSelectionUtils.getEntries( selection );
                ISearchResult[] selectedSearchResults = BrowserSelectionUtils.getSearchResults( selection );
                IBookmark[] selectedBookMarks = BrowserSelectionUtils.getBookmarks( selection );
                entryEditorManager.openEntryEditor( entryEditorExtension, selectedEntries, selectedSearchResults,
                    selectedBookMarks );
            }
        };

        return action;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isVisible()
    {
        ISelection selection = selectionProvider.getSelection();

        IBookmark[] selectedBookMarks = BrowserSelectionUtils.getBookmarks( selection );
        IEntry[] selectedEntries = BrowserSelectionUtils.getEntries( selection );
        ISearchResult[] selectedSearchResults = BrowserSelectionUtils.getSearchResults( selection );

        return ( selectedSearchResults.length + selectedBookMarks.length + selectedEntries.length == 1 );
    }


    /**
     * {@inheritDoc}
     */
    public boolean isDynamic()
    {
        return true;
    }
}
