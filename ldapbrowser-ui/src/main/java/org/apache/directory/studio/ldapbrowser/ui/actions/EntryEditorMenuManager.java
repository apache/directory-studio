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
        super( Messages.getString("EntryEditorMenuManager.OpenWith") ); //$NON-NLS-1$
        this.selectionProvider = selectionProvider;
        openEntryEditorsPreferencePageAction = new OpenEntryEditorsPreferencePageAction();
        addMenuListener( this );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IMenuListener#menuAboutToShow(org.eclipse.jface.action.IMenuManager)
     */
    public void menuAboutToShow( IMenuManager manager )
    {
        // As the Menu Manager is dynamic, we need to 
        // remove all the previously added actions
        removeAll();

        // Getting the entry editors and creating an action for each
        Collection<EntryEditorExtension> entryEditors = BrowserUIPlugin.getDefault().getEntryEditorManager()
            .getSortedEntryEditorExtensions();
        for ( EntryEditorExtension entryEditorExtension : entryEditors )
        {
            add( createAction( entryEditorExtension ) );
        }

        // Separator
        add( new Separator() );

        // Preferences Action
        add( openEntryEditorsPreferencePageAction );
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

        // TODO Add enable/disable action if the entry editor can "handle" the entry.
        // TODO Or do include this entry editor in the list of available entry editors.
        //action.setEnabled( false );

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
