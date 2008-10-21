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

package org.apache.directory.studio.ldapbrowser.common.actions;


import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.common.widgets.browser.BrowserEntryPage;
import org.apache.directory.studio.ldapbrowser.common.widgets.browser.BrowserSearchResultPage;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;


/**
 * This class implements the Up Action.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class UpAction extends BrowserAction
{
    protected TreeViewer viewer;


    /**
     * Creates a new instance of UpAction.
     *
     * @param viewer
     *      the attached TreeViewer
     */
    public UpAction( TreeViewer viewer )
    {
        super();
        this.viewer = viewer;
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return "Up";
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return BrowserCommonActivator.getDefault().getImageDescriptor( BrowserCommonConstants.IMG_PARENT );
    }


    /**
     * {@inheritDoc}
     */
    public String getCommandId()
    {
        return "org.apache.directory.studio.ldapbrowser.action.openSearchResult";
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        IEntry[] entries = getSelectedEntries();
        ISearch[] searches = getSelectedSearches();
        ISearchResult[] searchResults = getSelectedSearchResults();
        IBookmark[] bookmarks = getSelectedBookmarks();
        BrowserEntryPage[] browserEntryPages = getSelectedBrowserEntryPages();
        BrowserSearchResultPage[] browserSearchResultPages = getSelectedBrowserSearchResultPages();

        Object selection = null;

        if ( entries.length > 0 )
        {
            selection = entries[0];
        }
        else if ( searches.length > 0 )
        {
            selection = searches[0];
        }
        else if ( searchResults.length > 0 )
        {
            selection = searchResults[0];
        }
        else if ( bookmarks.length > 0 )
        {
            selection = bookmarks[0];
        }
        else if ( browserEntryPages.length > 0 )
        {
            selection = browserEntryPages[0];
        }
        else if ( browserSearchResultPages.length > 0 )
        {
            selection = browserSearchResultPages[0];
        }

        if ( selection != null )
        {
            ITreeContentProvider contentProvider = ( ITreeContentProvider ) viewer.getContentProvider();
            Object newSelection = contentProvider.getParent( selection );
            viewer.reveal( newSelection );
            viewer.setSelection( new StructuredSelection( newSelection ), true );
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        IEntry[] entries = getSelectedEntries();
        ISearch[] searches = getSelectedSearches();
        ISearchResult[] searchResults = getSelectedSearchResults();
        IBookmark[] bookmarks = getSelectedBookmarks();
        BrowserEntryPage[] browserEntryPages = getSelectedBrowserEntryPages();
        BrowserSearchResultPage[] browserSearchResultPages = getSelectedBrowserSearchResultPages();

        return entries.length > 0 || searches.length > 0 || searchResults.length > 0 || bookmarks.length > 0
            || browserEntryPages.length > 0 || browserSearchResultPages.length > 0;
    }
}
