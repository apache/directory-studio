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


import org.apache.directory.studio.entryeditors.EntryEditorInput;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ContributionItemFactory;


/**
 * The EntryEditorShowInMenuManager manages actions for the "Show In" menu manager.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EntryEditorShowInMenuManager
{
    /** The entry editor */
    private EntryEditor entryEditor;

    /** The locate entry in DIT action */
    private LocateEntryInLdapBrowserAction locateEntryInDitAction;

    /** The locate search result or bookmark action */
    private LocateSearchResultOrBookmarkAction locateSearchResultOrBookmarkAction;


    /**
     * Creates a new instance of EntryEditorShowInMenuManager.
     * 
     * @param entryEditor the entry editor
     */
    public EntryEditorShowInMenuManager( EntryEditor entryEditor )
    {
        this.entryEditor = entryEditor;

        locateEntryInDitAction = new LocateEntryInLdapBrowserAction( entryEditor, this );
        locateSearchResultOrBookmarkAction = new LocateSearchResultOrBookmarkAction( entryEditor, this );
    }


    /**
     * Creates the menu manager.
     *
     * @param parent parent menu manager
     */
    public void createMenuManager( IMenuManager parent )
    {
        MenuManager showInMenuManager = new MenuManager( Messages.getString( "EntryEditorShowInMenuManager.ShowIn" ) ); //$NON-NLS-1$
        parent.add( showInMenuManager );

        Object input = getInput();

        if ( input != null )
        {
            showInMenuManager.add( locateEntryInDitAction );

            if ( inputIsSearchResultOrBookmark() )
            {
                showInMenuManager.add( locateSearchResultOrBookmarkAction );
            }
        }

        showInMenuManager.add( ContributionItemFactory.VIEWS_SHOW_IN.create( PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow() ) );
    }


    /**
     * Gets the input.
     *
     * @return the input
     */
    public Object getInput()
    {
        if ( entryEditor != null )
        {
            EntryEditorInput editorInput = entryEditor.getEntryEditorInput();

            if ( editorInput != null )
            {
                return editorInput.getInput();
            }
        }

        return null;
    }


    /**
     * Indicates if the input is a search result or a bookmark.
     *
     * @return <code>true</code> if the input is a search result or a bookmark,
     *         <code>false</code> if not.
     */
    private boolean inputIsSearchResultOrBookmark()
    {
        Object input = getInput();

        return ( ( input instanceof ISearchResult ) || ( input instanceof IBookmark ) );
    }
}
