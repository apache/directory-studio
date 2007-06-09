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


import java.util.Arrays;

import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;


/**
 * A BrowserSearchResultPage is a container for search results or other nested browser search result pages.
 * It is used when folding searches with many results. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BrowserSearchResultPage
{

    /** The tree sorter */
    private BrowserSorter sorter;

    /** The index of the first child search result in this page */
    private int first;

    /** The index of the last child search result in this page */
    private int last;

    /** The parent search */
    private ISearch search;

    /** The parent search result page or null if not nested */
    private BrowserSearchResultPage parentSearchResultPage;

    /** The sub pages */
    private BrowserSearchResultPage[] subpages;


    /**
     * Creates a new instance of BrowserSearchResultPage.
     *
     * @param search the parent search
     * @param first the index of the first child search result in this page
     * @param last the index of the last child search result in this page
     * @param subpages the sub pages
     * @param sorter the sorter
     */
    public BrowserSearchResultPage( ISearch search, int first, int last, BrowserSearchResultPage[] subpages,
        BrowserSorter sorter )
    {
        this.search = search;
        this.first = first;
        this.last = last;
        this.subpages = subpages;
        this.sorter = sorter;

        if ( subpages != null )
        {
            for ( int i = 0; i < subpages.length; i++ )
            {
                subpages[i].parentSearchResultPage = this;
            }
        }
    }


    /**
     * Gets the children, either the sub pages or 
     * the search results contained in this page.
     *
     * @return the children
     */
    public Object[] getChildren()
    {
        if ( subpages != null )
        {
            return subpages;
        }
        else
        {
            // 1. get children
            ISearchResult[] children = search.getSearchResults();

            // 2. sort
            sorter.sort( null, children );

            // 3. extraxt range
            if ( children != null )
            {
                ISearchResult[] childrenRange = new ISearchResult[last - first + 1];
                for ( int i = first; i <= last; i++ )
                {
                    childrenRange[i - first] = children[i];
                }
                return childrenRange;
            }
            else
            {
                return null;
            }
        }
    }


    /**
     * Gets the first.
     * 
     * @return the first
     */
    public int getFirst()
    {
        return first;
    }


    /**
     * Gets the last.
     * 
     * @return the last
     */
    public int getLast()
    {
        return last;
    }


    /**
     * Gets the search.
     * 
     * @return the search
     */
    public ISearch getSearch()
    {
        return search;
    }


    /**
     * Gets the parent page if the given search result is contained in this page
     * or one of the sub pages.
     * 
     * @param searchResult the search result
     * 
     * @return the parent page of the given search result.
     */
    public BrowserSearchResultPage getParentOf( ISearchResult searchResult )
    {
        if ( subpages != null )
        {
            BrowserSearchResultPage ep = null;
            for ( int i = 0; i < subpages.length && ep == null; i++ )
            {
                ep = subpages[i].getParentOf( searchResult );
            }
            return ep;
        }
        else
        {
            ISearchResult[] sr = ( ISearchResult[] ) getChildren();
            if ( sr != null && Arrays.asList( sr ).contains( searchResult ) )
            {
                return this;
            }
            else
            {
                return null;
            }
        }
    }


    /**
     * Gets the direct parent, either a page or the search.
     * 
     * @return the direct parent
     */
    public Object getParent()
    {
        return ( parentSearchResultPage != null ) ? ( Object ) parentSearchResultPage : ( Object ) search;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return search.toString() + "[" + first + "..." + last + "]" + hashCode();
    }

}
