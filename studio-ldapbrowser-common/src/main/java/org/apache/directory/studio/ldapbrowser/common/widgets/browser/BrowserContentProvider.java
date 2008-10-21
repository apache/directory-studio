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

package org.apache.directory.studio.ldapbrowser.common.widgets.browser;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.directory.studio.connection.core.jobs.OpenConnectionsJob;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.internal.model.DirectoryMetadataEntry;
import org.apache.directory.studio.ldapbrowser.core.jobs.InitializeChildrenJob;
import org.apache.directory.studio.ldapbrowser.core.jobs.SearchJob;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IRootDSE;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


/**
 * The BrowserContentProvider implements the content provider for
 * the browser widget. It accepts an IConnection as input.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BrowserContentProvider implements ITreeContentProvider
{

    /** The prefernces */
    protected BrowserPreferences preferences;

    /** The sorter */
    protected BrowserSorter sorter;

    /** This map contains the pages for entries with many children (if folding is activated) */
    private Map<IEntry, BrowserEntryPage[]> entryToEntryPagesMap;

    /** This map contains the pages for searches with many results (if folding is activated) */
    private Map<ISearch, BrowserSearchResultPage[]> searchToSearchResultPagesMap;

    /** This map contains the top-level categories for each connection */
    private Map<IBrowserConnection, BrowserCategory[]> connectionToCategoriesMap;


    /**
     * Creates a new instance of BrowserContentProvider.
     *
     * @param preferences the preferences
     * @param sorter the sorter
     */
    public BrowserContentProvider( BrowserPreferences preferences, BrowserSorter sorter )
    {
        this.preferences = preferences;
        this.sorter = sorter;
        this.entryToEntryPagesMap = new HashMap<IEntry, BrowserEntryPage[]>();
        this.searchToSearchResultPagesMap = new HashMap<ISearch, BrowserSearchResultPage[]>();
        this.connectionToCategoriesMap = new HashMap<IBrowserConnection, BrowserCategory[]>();
    }


    /**
     * {@inheritDoc}
     */
    public void inputChanged( Viewer v, Object oldInput, Object newInput )
    {
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        if ( entryToEntryPagesMap != null )
        {
            entryToEntryPagesMap.clear();
            entryToEntryPagesMap = null;
        }
        if ( searchToSearchResultPagesMap != null )
        {
            searchToSearchResultPagesMap.clear();
            searchToSearchResultPagesMap = null;
        }
        if ( connectionToCategoriesMap != null )
        {
            connectionToCategoriesMap.clear();
            connectionToCategoriesMap = null;
        }
    }


    /**
     * {@inheritDoc}
     */
    public Object[] getElements( Object parent )
    {
        if ( parent instanceof IBrowserConnection )
        {
            IBrowserConnection connection = ( IBrowserConnection ) parent;
            if ( !connectionToCategoriesMap.containsKey( connection ) )
            {
                BrowserCategory[] categories = new BrowserCategory[3];
                categories[0] = new BrowserCategory( BrowserCategory.TYPE_DIT, connection );
                categories[1] = new BrowserCategory( BrowserCategory.TYPE_SEARCHES, connection );
                categories[2] = new BrowserCategory( BrowserCategory.TYPE_BOOKMARKS, connection );
                connectionToCategoriesMap.put( connection, categories );
            }

            BrowserCategory[] categories = connectionToCategoriesMap.get( connection );

            List<BrowserCategory> catList = new ArrayList<BrowserCategory>( 3 );
            if ( preferences.isShowDIT() )
            {
                catList.add( categories[0] );
            }
            if ( preferences.isShowSearches() )
            {
                catList.add( categories[1] );
            }
            if ( preferences.isShowBookmarks() )
            {
                catList.add( categories[2] );
            }

            return catList.toArray( new BrowserCategory[0] );
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


    /**
     * {@inheritDoc}
     */
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
                if ( connectionToCategoriesMap.get( ( ( IEntry ) child ).getBrowserConnection() ) != null )
                {
                    return connectionToCategoriesMap.get( ( ( IEntry ) child ).getBrowserConnection() )[0];
                }
                else
                {
                    return null;
                }
            }
            else if ( entryToEntryPagesMap.containsKey( parentEntry ) )
            {
                BrowserEntryPage[] entryPages = entryToEntryPagesMap.get( parentEntry );
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
            return connectionToCategoriesMap.get( search.getBrowserConnection() )[1];
        }
        else if ( child instanceof ISearchResult )
        {
            ISearch parentSearch = ( ( ISearchResult ) child ).getSearch();
            if ( parentSearch != null && searchToSearchResultPagesMap.containsKey( parentSearch ) )
            {
                BrowserSearchResultPage[] srPages = searchToSearchResultPagesMap.get( parentSearch );
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
            return connectionToCategoriesMap.get( bookmark.getBrowserConnection() )[2];
        }
        else
        {
            return null;
        }
    }


    /**
     * {@inheritDoc}
     */
    public Object[] getChildren( Object parent )
    {
        if ( parent instanceof BrowserEntryPage )
        {
            BrowserEntryPage entryPage = ( BrowserEntryPage ) parent;
            Object[] objects = entryPage.getChildren();
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
        else if ( parent instanceof IRootDSE )
        {
            final IRootDSE rootDSE = ( IRootDSE ) parent;

            if ( !rootDSE.isChildrenInitialized() && rootDSE.isDirectoryEntry() )
            {
                new InitializeChildrenJob( new IEntry[]
                    { rootDSE } ).execute();
                return new String[]
                    { "Fetching Entries..." };
            }

            // get base entries
            List<IEntry> entryList = new ArrayList<IEntry>();
            entryList.addAll( Arrays.asList( rootDSE.getChildren() ) );

            // remove non-visible entries
            for ( Iterator<IEntry> it = entryList.iterator(); it.hasNext(); )
            {
                Object o = it.next();
                if ( !preferences.isShowDirectoryMetaEntries() && ( o instanceof DirectoryMetadataEntry ) )
                {
                    it.remove();
                }
            }

            return entryList.toArray();
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
            if ( childrenCount <= preferences.getFoldingSize() || !preferences.isUseFolding() )
            {
                if ( entryToEntryPagesMap.containsKey( parentEntry ) )
                {
                    entryToEntryPagesMap.remove( parentEntry );
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
                if ( !entryToEntryPagesMap.containsKey( parentEntry ) )
                {
                    entryPages = getEntryPages( parentEntry, 0, childrenCount - 1 );
                    entryToEntryPagesMap.put( parentEntry, entryPages );
                }
                else
                {
                    entryPages = entryToEntryPagesMap.get( parentEntry );
                    if ( childrenCount - 1 != entryPages[entryPages.length - 1].getLast() )
                    {
                        entryPages = getEntryPages( parentEntry, 0, childrenCount - 1 );
                        entryToEntryPagesMap.put( parentEntry, entryPages );
                    }
                }
                return entryPages;
            }
        }
        else if ( parent instanceof BrowserSearchResultPage )
        {
            BrowserSearchResultPage srPage = ( BrowserSearchResultPage ) parent;
            Object[] objects = srPage.getChildren();
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
            else if ( search.getSearchResults().length <= preferences.getFoldingSize() || !preferences.isUseFolding() )
            {
                ISearchResult[] results = search.getSearchResults();
                return results;
            }
            else
            {
                BrowserSearchResultPage[] srPages = null;
                if ( !searchToSearchResultPagesMap.containsKey( search ) )
                {
                    srPages = getSearchResultPages( search, 0, search.getSearchResults().length - 1 );
                    searchToSearchResultPagesMap.put( search, srPages );
                }
                else
                {
                    srPages = searchToSearchResultPagesMap.get( search );
                    if ( search.getSearchResults().length - 1 != srPages[srPages.length - 1].getLast() )
                    {
                        srPages = getSearchResultPages( search, 0, search.getSearchResults().length - 1 );
                        searchToSearchResultPagesMap.put( search, srPages );
                    }
                }
                return srPages;
            }
        }
        else if ( parent instanceof BrowserCategory )
        {
            BrowserCategory category = ( BrowserCategory ) parent;
            IBrowserConnection connection = category.getParent();

            switch ( category.getType() )
            {
                case BrowserCategory.TYPE_DIT:
                {
                    // open connection when expanding DIT
                    if ( !connection.getConnection().getJNDIConnectionWrapper().isConnected() )
                    {
                        new OpenConnectionsJob( connection.getConnection() ).execute();
                        return new String[]
                            { "Opening Connection..." };
                    }

                    return new Object[]
                        { connection.getRootDSE() };
                }

                case BrowserCategory.TYPE_SEARCHES:
                {
                    return connection.getSearchManager().getSearches();
                }

                case BrowserCategory.TYPE_BOOKMARKS:
                {
                    return connection.getBookmarkManager().getBookmarks();
                }
            }

            return new Object[0];
        }
        else
        {
            return new Object[0];
        }
    }


    /**
     * {@inheritDoc}
     */
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
            return true;
        }
        else if ( parent instanceof BrowserCategory )
        {
            return true;
        }
        else
        {
            return false;
        }
    }


    /**
     * Creates and returns the entry pages for the given entry. The number of pages
     * depends on the number of entries and the paging size. 
     *
     * @param entry the parent entry
     * @param first the index of the first child entry
     * @param last the index of the last child entry
     * @return the created entry pages
     */
    private BrowserEntryPage[] getEntryPages( IEntry entry, int first, int last )
    {
        int pagingSize = preferences.getFoldingSize();

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
            pages[i] = new BrowserEntryPage( entry, groupFirst, groupLast, subpages, sorter );
        }

        return pages;
    }


    /**
     * Creates and returns the search result pages for the given search. The number of pages
     * depends on the number of search results and the paging size. 
     *
     * @param search the parent search
     * @param first the index of the first search result
     * @param last the index of the last child search result
     * @return the created search result pages
     */
    private BrowserSearchResultPage[] getSearchResultPages( ISearch search, int first, int last )
    {
        int pagingSize = preferences.getFoldingSize();

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
            pages[i] = new BrowserSearchResultPage( search, groupFirst, groupLast, subpages, sorter );
        }

        return pages;
    }

}
