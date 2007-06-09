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

package org.apache.directory.ldapstudio.browser.common.widgets.browser;


import java.util.Arrays;

import org.apache.directory.studio.ldapbrowser.core.model.IEntry;


/**
 * A BrowserEntryPage is a container for entries or other nested browser entry pages.
 * It is used when folding large branches. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BrowserEntryPage
{
    /** The tree sorter */
    private BrowserSorter sorter;

    /** The index of the first child entry in this page */ 
    private int first;

    /** The index of the last child entry in this page */
    private int last;

    /** The parent entry */
    private IEntry entry;

    /** The parent entry page or null if not nested */
    private BrowserEntryPage parentEntryPage;

    /** The sub pages */
    private BrowserEntryPage[] subpages;


    /**
     * Creates a new instance of BrowserEntryPage.
     *
     * @param entry the parent entry
     * @param first the index of the first child entry in this page
     * @param last the index of the last child entry in this page
     * @param subpages the sub pages
     * @param sorter the sorter
     */
    public BrowserEntryPage( IEntry entry, int first, int last, BrowserEntryPage[] subpages, BrowserSorter sorter )
    {
        this.entry = entry;
        this.first = first;
        this.last = last;
        this.subpages = subpages;
        this.sorter = sorter;

        if ( subpages != null )
        {
            for ( int i = 0; i < subpages.length; i++ )
            {
                subpages[i].parentEntryPage = this;
            }
        }
    }


    /**
     * Gets the children, either the sub pages or 
     * the entries contained in this page.
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
            IEntry[] children = entry.getChildren();

            // 2. sort
            sorter.sort( null, children );

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
     * Gets the parent entry.
     * 
     * @return the parent entry
     */
    public IEntry getEntry()
    {
        return entry;
    }


    /**
     * Gets the parent page if the given entry is contained in this page
     * or one of the sub pages.
     * 
     * @param entry the entry
     * 
     * @return the parent page of the given entry.
     */
    public BrowserEntryPage getParentOf( IEntry entry )
    {
        if ( subpages != null )
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
            IEntry[] sr = ( IEntry[] ) getChildren();
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


    /**
     * Gets the direct parent, either a page or the entry.
     * 
     * @return the direct parent
     */
    public Object getParent()
    {
        return ( parentEntryPage != null ) ? ( Object ) parentEntryPage : ( Object ) entry;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return entry.toString() + "[" + first + "..." + last + "]" + hashCode();
    }

}
