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

import org.apache.directory.ldapstudio.browser.core.model.IEntry;


public class BrowserEntryPage
{

    private BrowserSorter sorter;

    private int first;

    private int last;

    private IEntry entry;

    private BrowserEntryPage parentEntryPage;

    private BrowserEntryPage[] subpages;


    public BrowserEntryPage( IEntry entry, int first, int last, BrowserSorter sorter )
    {
        this( entry, first, last, null, sorter );
    }


    public BrowserEntryPage( IEntry entry, int first, int last, BrowserEntryPage[] subcontainers, BrowserSorter sorter )
    {
        this.entry = entry;
        this.first = first;
        this.last = last;
        this.subpages = subcontainers;

        this.sorter = sorter;

        if ( this.subpages != null )
        {
            for ( int i = 0; i < this.subpages.length; i++ )
                subcontainers[i].parentEntryPage = this;
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
            IEntry[] children = this.entry.getChildren();

            // 2. sort
            this.sorter.sort( null, children );

            // 3. extraxt range
            if ( children != null )
            {
                IEntry[] childrenRange = new IEntry[last - first + 1];
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


    public IEntry getEntry()
    {
        return this.entry;
    }


    public BrowserEntryPage getParentOf( IEntry entry )
    {
        if ( this.subpages != null )
        {
            BrowserEntryPage ep = null;
            for ( int i = 0; i < subpages.length && ep == null; i++ )
            {
                ep = subpages[i].getParentOf( entry );
            }
            return ep;
        }
        else
        {
            IEntry[] sr = ( IEntry[] ) this.get();
            if ( sr != null && Arrays.asList( sr ).contains( entry ) )
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
        return ( this.parentEntryPage != null ) ? ( Object ) this.parentEntryPage : ( Object ) this.entry;
    }


    public String toString()
    {
        return this.entry.toString() + "[" + this.first + "..." + this.last + "]" + this.hashCode();
    }

}
