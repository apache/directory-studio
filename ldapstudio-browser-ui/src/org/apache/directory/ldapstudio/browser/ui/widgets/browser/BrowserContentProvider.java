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

package org.apache.directory.ldapstudio.browser.ui.widgets.browser;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.directory.ldapstudio.browser.core.BrowserCoreConstants;
import org.apache.directory.ldapstudio.browser.core.BrowserCorePlugin;
import org.apache.directory.ldapstudio.browser.core.internal.model.DirectoryMetadataEntry;
import org.apache.directory.ldapstudio.browser.core.internal.model.RootDSE;
import org.apache.directory.ldapstudio.browser.core.jobs.InitializeChildrenJob;
import org.apache.directory.ldapstudio.browser.core.jobs.OpenConnectionsJob;
import org.apache.directory.ldapstudio.browser.core.jobs.SearchJob;
import org.apache.directory.ldapstudio.browser.core.model.IBookmark;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.ISearch;
import org.apache.directory.ldapstudio.browser.core.model.ISearchResult;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


public class BrowserContentProvider implements ITreeContentProvider
{

    protected BrowserPreferences layout;

    protected BrowserSorter sorter;

    private Map entryToEntryPagesMap;

    private Map searchToSearchResultPagesMap;

    private Map connectionToCategoriesMap;


    public BrowserContentProvider( BrowserPreferences layout, BrowserSorter sorter )
    {
        this.layout = layout;
        this.sorter = sorter;
        this.entryToEntryPagesMap = new HashMap();
        this.searchToSearchResultPagesMap = new HashMap();
        this.connectionToCategoriesMap = new HashMap();
    }


    public void inputChanged( Viewer v, Object oldInput, Object newInput )
    {
    }


    public void dispose()
    {

        if ( this.entryToEntryPagesMap != null )
        {
            this.entryToEntryPagesMap.clear();
            this.entryToEntryPagesMap = null;
        }
        if ( this.searchToSearchResultPagesMap != null )
        {
            this.searchToSearchResultPagesMap.clear();
            this.searchToSearchResultPagesMap = null;
        }
        if ( this.connectionToCategoriesMap != null )
        {
            this.connectionToCategoriesMap.clear();
            this.connectionToCategoriesMap = null;
        }
    }


    public Object[] getElements( Object parent )
    {
        if ( parent instanceof IConnection )
        {
            IConnection connection = ( IConnection ) parent;
            if ( !this.connectionToCategoriesMap.containsKey( connection ) )
            {
                BrowserCategory[] categories = new BrowserCategory[3];
                List entryList = new ArrayList();
                if ( connection.isOpened() )
                {
                    entryList.addAll( Arrays.asList( connection.getBaseDNEntries() ) );
                    entryList.add( connection.getRootDSE() );
                    entryList.addAll( Arrays.asList( connection.getMetadataEntries() ) );
                }
                categories[0] = new BrowserCategory( BrowserCategory.TYPE_DIT, connection, entryList.toArray() );
                categories[1] = new BrowserCategory( BrowserCategory.TYPE_SEARCHES, connection, connection
                    .getSearchManager().getSearches() );
                categories[2] = new BrowserCategory( BrowserCategory.TYPE_BOOKMARKS, connection, connection
                    .getBookmarkManager().getBookmarks() );
                this.connectionToCategoriesMap.put( connection, categories );
            }

            BrowserCategory[] categories = ( BrowserCategory[] ) this.connectionToCategoriesMap.get( connection );
            List entryList = new ArrayList();
            if ( connection.isOpened() )
            {
                entryList.addAll( Arrays.asList( connection.getBaseDNEntries() ) );
                entryList.add( connection.getRootDSE() );
                entryList.addAll( Arrays.asList( connection.getMetadataEntries() ) );
            }
            categories[0].setChildren( entryList.toArray() );
            categories[1].setChildren( connection.getSearchManager().getSearches() );
            categories[2].setChildren( connection.getBookmarkManager().getBookmarks() );

            List catList = new ArrayList( 3 );
            if ( this.layout.isShowDIT() )
                catList.add( categories[0] );
            if ( this.layout.isShowSearches() )
                catList.add( categories[1] );
            if ( this.layout.isShowBookmarks() )
                catList.add( categories[2] );

            return ( BrowserCategory[] ) catList.toArray( new BrowserCategory[0] );
        }
        else if ( parent instanceof IEntry[] )
        {
            return ( IEntry[] ) parent;
        }
        else
        {
            return getChildren( parent );
        }
    }


    public Object getParent( final Object child )
    {
        if ( child instanceof BrowserCategory )
        {
            return ( ( BrowserCategory ) child ).getParent();
        }
        else if ( child instanceof BrowserEntryPage )
        {
            return ( ( BrowserEntryPage ) child ).getParent();
        }
        else if ( child instanceof IEntry )
        {
            IEntry parentEntry = ( ( IEntry ) child ).getParententry();
            if ( parentEntry == null )
            {
                if ( this.connectionToCategoriesMap.get( ( ( IEntry ) child ).getConnection() ) != null )
                {
                    return ( ( BrowserCategory[] ) this.connectionToCategoriesMap.get( ( ( IEntry ) child )
                        .getConnection() ) )[0];
                }
                else
                {
                    return null;
                }
            }
            else if ( this.entryToEntryPagesMap.containsKey( parentEntry ) )
            {
                BrowserEntryPage[] entryPages = ( BrowserEntryPage[] ) this.entryToEntryPagesMap.get( parentEntry );
                BrowserEntryPage ep = null;
                for ( int i = 0; i < entryPages.length && ep == null; i++ )
                {
                    ep = entryPages[i].getParentOf( ( IEntry ) child );
                }
                return ep;
            }
            else
            {
                return parentEntry;
            }
        }
        else if ( child instanceof BrowserSearchResultPage )
        {
            return ( ( BrowserSearchResultPage ) child ).getParent();
        }
        else if ( child instanceof ISearch )
        {
            ISearch search = ( ( ISearch ) child );
            return ( ( BrowserCategory[] ) this.connectionToCategoriesMap.get( search.getConnection() ) )[1];
        }
        else if ( child instanceof ISearchResult )
        {
            ISearch parentSearch = ( ( ISearchResult ) child ).getSearch();
            if ( parentSearch != null && this.searchToSearchResultPagesMap.containsKey( parentSearch ) )
            {
                BrowserSearchResultPage[] srPages = ( BrowserSearchResultPage[] ) this.searchToSearchResultPagesMap
                    .get( parentSearch );
                BrowserSearchResultPage srp = null;
                for ( int i = 0; i < srPages.length && srp == null; i++ )
                {
                    srp = srPages[i].getParentOf( ( ISearchResult ) child );
                }
                return srp;
            }
            else
            {
                return parentSearch;
            }
        }
        else if ( child instanceof IBookmark )
        {
            IBookmark bookmark = ( ( IBookmark ) child );
            return ( ( BrowserCategory[] ) this.connectionToCategoriesMap.get( bookmark.getConnection() ) )[2];
        }
        else
        {
            return null;
        }
    }


    public Object[] getChildren( Object parent )
    {

        if ( parent instanceof BrowserEntryPage )
        {
            BrowserEntryPage entryPage = ( BrowserEntryPage ) parent;
            Object[] objects = entryPage.get();
            if ( objects == null )
            {
                return new String[]
                    { "Fetching Entries..." };
            }
            else if ( objects instanceof IEntry[] )
            {
                IEntry[] entries = ( IEntry[] ) objects;
                return entries;
            }
            else
            {
                return objects;
            }
        }
        else if ( parent instanceof IEntry )
        {
            final IEntry parentEntry = ( IEntry ) parent;

            if ( !parentEntry.isChildrenInitialized() && parentEntry.isDirectoryEntry() )
            {
                new InitializeChildrenJob( new IEntry[]
                    { parentEntry } ).execute();
                return new String[]
                    { "Fetching Entries..." };
            }

            int childrenCount = parentEntry.getChildrenCount();
            if ( childrenCount <= layout.getFoldingSize() || !layout.isUseFolding() )
            {
                if ( this.entryToEntryPagesMap.containsKey( parentEntry ) )
                {
                    this.entryToEntryPagesMap.remove( parentEntry );
                }

                IEntry[] entries = parentEntry.getChildren();
                if ( entries == null )
                {
                    return new String[]
                        { "Fetching Entries..." };
                }
                else
                {
                    return entries;
                }
            }
            else
            {
                BrowserEntryPage[] entryPages = null;
                if ( !this.entryToEntryPagesMap.containsKey( parentEntry ) )
                {
                    entryPages = getEntryPages( parentEntry, 0, childrenCount - 1 );
                    this.entryToEntryPagesMap.put( parentEntry, entryPages );
                }
                else
                {
                    entryPages = ( BrowserEntryPage[] ) this.entryToEntryPagesMap.get( parentEntry );
                    if ( childrenCount - 1 != entryPages[entryPages.length - 1].getLast() )
                    {
                        entryPages = getEntryPages( parentEntry, 0, childrenCount - 1 );
                        this.entryToEntryPagesMap.put( parentEntry, entryPages );
                    }
                }
                return entryPages;
            }
        }
        else if ( parent instanceof BrowserSearchResultPage )
        {
            BrowserSearchResultPage srPage = ( BrowserSearchResultPage ) parent;
            Object[] objects = srPage.get();
            if ( objects == null )
            {
                return new String[]
                    { "Fetching Search Results..." };
            }
            else if ( objects instanceof ISearchResult[] )
            {
                ISearchResult[] srs = ( ISearchResult[] ) objects;
                return srs;
            }
            else
            {
                return objects;
            }
        }
        else if ( parent instanceof ISearch )
        {
            ISearch search = ( ISearch ) parent;
            if ( search.getSearchResults() == null )
            {
                // return new Object[0];
                new SearchJob( new ISearch[]
                    { search } ).execute();
                return new String[]
                    { "Performing Search..." };
            }
            else if ( search.getSearchResults().length == 0 )
            {
                return new String[]
                    { "No Results" };
            }
            else if ( search.getSearchResults().length <= layout.getFoldingSize() || !layout.isUseFolding() )
            {
                ISearchResult[] results = search.getSearchResults();
                return results;
            }
            else
            {
                BrowserSearchResultPage[] srPages = null;
                if ( !this.searchToSearchResultPagesMap.containsKey( search ) )
                {
                    srPages = getSearchResultPages( search, 0, search.getSearchResults().length - 1 );
                    this.searchToSearchResultPagesMap.put( search, srPages );
                }
                else
                {
                    srPages = ( BrowserSearchResultPage[] ) this.searchToSearchResultPagesMap.get( search );
                    if ( search.getSearchResults().length - 1 != srPages[srPages.length - 1].getLast() )
                    {
                        srPages = getSearchResultPages( search, 0, search.getSearchResults().length - 1 );
                        this.searchToSearchResultPagesMap.put( search, srPages );
                    }
                }
                return srPages;
            }
        }
        else if ( parent instanceof BrowserCategory )
        {
            BrowserCategory category = ( BrowserCategory ) parent;

            List objects = new ArrayList( Arrays.asList( category.getChildren() ) );
            for ( Iterator it = objects.iterator(); it.hasNext(); )
            {
                Object o = it.next();
                if ( !this.layout.isShowDirectoryMetaEntries()
                    && ( o instanceof DirectoryMetadataEntry || o instanceof RootDSE ) )
                {
                    it.remove();
                }
            }

            // open connection when expanding DIT
            if ( category.getType() == BrowserCategory.TYPE_DIT && !category.getParent().isOpened() )
            {
                new OpenConnectionsJob( category.getParent() ).execute();
                return new String[]
                    { "Fetching Entries..." };
            }

            return objects.toArray();
        }
        else
        {
            return new Object[0];
        }
    }


    public boolean hasChildren( Object parent )
    {
        if ( parent instanceof IEntry )
        {
            IEntry parentEntry = ( IEntry ) parent;
            return parentEntry.hasChildren()
                || ( BrowserCorePlugin.getDefault().getPluginPreferences().getBoolean(
                    BrowserCoreConstants.PREFERENCE_SHOW_ALIAS_AND_REFERRAL_OBJECTS ) && ( parentEntry.isAlias() || parentEntry
                    .isReferral() ) );
        }
        else if ( parent instanceof BrowserEntryPage )
        {
            return true;
        }
        else if ( parent instanceof BrowserSearchResultPage )
        {
            return true;
        }
        else if ( parent instanceof ISearchResult )
        {
            return false;
        }
        else if ( parent instanceof ISearch )
        {
            // ISearch search = ((ISearch)parent);
            // return search.getSearchResults() != null;
            return true;
        }
        else if ( parent instanceof BrowserCategory )
        {
            return true;
            // BrowserCategory category = (BrowserCategory)parent;
            // return category.getChildren().length > 0;
        }
        else
        {
            return false;
        }
    }


    private BrowserEntryPage[] getEntryPages( IEntry entry, int first, int last )
    {

        int pagingSize = this.layout.getFoldingSize();

        int diff = last - first;
        int factor = diff > 0 ? ( int ) ( Math.log( diff ) / Math.log( pagingSize ) ) : 0;

        int groupFirst = first;
        int groupLast = first;
        BrowserEntryPage[] pages = new BrowserEntryPage[( int ) ( diff / Math.pow( pagingSize, factor ) ) + 1];
        for ( int i = 0; i < pages.length; i++ )
        {
            groupFirst = ( int ) ( i * Math.pow( pagingSize, factor ) ) + first;
            groupLast = ( int ) ( ( i + 1 ) * Math.pow( pagingSize, factor ) ) + first - 1;
            groupLast = groupLast > last ? last : groupLast;
            BrowserEntryPage[] subpages = ( factor > 1 ) ? getEntryPages( entry, groupFirst, groupLast ) : null;
            pages[i] = new BrowserEntryPage( entry, groupFirst, groupLast, subpages, this.sorter );
        }

        return pages;
    }


    private BrowserSearchResultPage[] getSearchResultPages( ISearch search, int first, int last )
    {

        int pagingSize = this.layout.getFoldingSize();

        int diff = last - first;
        int factor = diff > 0 ? ( int ) ( Math.log( diff ) / Math.log( pagingSize ) ) : 0;

        int groupFirst = first;
        int groupLast = first;
        BrowserSearchResultPage[] pages = new BrowserSearchResultPage[( int ) ( diff / Math.pow( pagingSize, factor ) ) + 1];
        for ( int i = 0; i < pages.length; i++ )
        {
            groupFirst = ( int ) ( i * Math.pow( pagingSize, factor ) ) + first;
            groupLast = ( int ) ( ( i + 1 ) * Math.pow( pagingSize, factor ) ) + first - 1;
            groupLast = groupLast > last ? last : groupLast;
            BrowserSearchResultPage[] subpages = ( factor > 1 ) ? getSearchResultPages( search, groupFirst, groupLast )
                : null;
            pages[i] = new BrowserSearchResultPage( search, groupFirst, groupLast, subpages, this.sorter );
        }

        return pages;
    }

}
