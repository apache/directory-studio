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


import java.util.Arrays;

import org.apache.directory.ldapstudio.browser.core.model.ISearch;
import org.apache.directory.ldapstudio.browser.core.model.ISearchResult;


public class BrowserSearchResultPage
{

    private BrowserSorter sorter;

    private int first;

    private int last;

    private ISearch search;

    private BrowserSearchResultPage parentSearchResultPage;

    private BrowserSearchResultPage[] subpages;


    public BrowserSearchResultPage( ISearch search, int first, int last, BrowserSorter sorter )
    {
        this( search, first, last, null, sorter );
    }


    public BrowserSearchResultPage( ISearch search, int first, int last, BrowserSearchResultPage[] subcontainers,
        BrowserSorter sorter )
    {
        this.search = search;
        this.first = first;
        this.last = last;
        this.subpages = subcontainers;

        this.sorter = sorter;

        if ( this.subpages != null )
        {
            for ( int i = 0; i < this.subpages.length; i++ )
                subcontainers[i].parentSearchResultPage = this;
        }
    }


    public Object[] get()
    {
        if ( this.subpages != null )
        {
            // return subpages
            return this.subpages;
        }
        else
        {
            // return children

            // 1. get children
            ISearchResult[] children = this.search.getSearchResults();

            // 2. sort
            this.sorter.sort( null, children );

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


    public int getFirst()
    {
        return this.first;
    }


    public int getLast()
    {
        return this.last;
    }


    public ISearch getSearch()
    {
        return this.search;
    }


    public BrowserSearchResultPage getParentOf( ISearchResult searchReslt )
    {
        if ( this.subpages != null )
        {
            BrowserSearchResultPage ep = null;
            for ( int i = 0; i < subpages.length && ep == null; i++ )
            {
                ep = subpages[i].getParentOf( searchReslt );
            }
            return ep;
        }
        else
        {
            ISearchResult[] sr = ( ISearchResult[] ) this.get();
            if ( sr != null && Arrays.asList( sr ).contains( searchReslt ) )
            {
                return this;
            }
            else
            {
                return null;
            }
        }
    }


    public Object getParent()
    {
        return ( this.parentSearchResultPage != null ) ? ( Object ) this.parentSearchResultPage
            : ( Object ) this.search;
    }


    public String toString()
    {
        return this.search.toString() + "[" + this.first + "..." + this.last + "]" + this.hashCode();
    }

}
