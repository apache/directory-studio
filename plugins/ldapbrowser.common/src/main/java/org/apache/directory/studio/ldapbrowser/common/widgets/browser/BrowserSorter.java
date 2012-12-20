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


import java.math.BigInteger;

import org.apache.directory.api.ldap.model.name.Rdn;
import org.apache.directory.studio.connection.core.jobs.StudioConnectionRunnableWithProgress;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IQuickSearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.impl.DirectoryMetadataEntry;
import org.apache.directory.studio.ldapbrowser.core.model.impl.RootDSE;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;


/**
 * The BrowserSorter implements the sorter for the browser widget. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class BrowserSorter extends ViewerSorter
{
    /** The browser preferences, used to get the sort settings */
    private BrowserPreferences preferences;


    /**
     * Creates a new instance of BrowserSorter.
     *
     * @param preferences the browser preferences, used to get the sort settings
     */
    public BrowserSorter( BrowserPreferences preferences )
    {
        this.preferences = preferences;
    }


    /**
     * Connects the tree viewer to this sorter.
     *
     * @param viewer the tree viewer
     */
    public void connect( TreeViewer viewer )
    {
        viewer.setSorter( this );
    }


    /**
     * {@inheritDoc}
     * 
     * For performance reasons this implementation first checks if sorting is enabled 
     * and if the number of elements is less than the sort limit.
     */
    public void sort( final Viewer viewer, final Object[] elements )
    {
        if ( elements != null && ( preferences.getSortLimit() <= 0 || elements.length < preferences.getSortLimit() ) )
        {
            BrowserSorter.super.sort( viewer, elements );
        }
    }


    /**
     * {@inheritDoc}
     * 
     * This method is used to categorize leaf entries, meta entries and normal entries.
     */
    public int category( Object element )
    {
        if ( element instanceof IEntry )
        {
            IEntry entry = ( IEntry ) element;
            if ( ( entry instanceof DirectoryMetadataEntry || entry instanceof RootDSE || entry.isAlias() || entry
                .isReferral() )
                && preferences.isMetaEntriesLast() )
            {
                return 3;
            }
            else if ( entry.isSubentry() )
            {
                return 0;
            }
            else if ( !entry.hasChildren() && preferences.isLeafEntriesFirst() )
            {
                return 1;
            }
            else if ( entry.hasChildren() && preferences.isContainerEntriesFirst() )
            {
                return 1;
            }
            else
            {
                return 2;
            }
        }
        else
        {
            return 4;
        }
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation compares IEntry or ISearchResult objects. Depending on
     * the sort settings it delegates comparation to {@link #compareRdns(IEntry, IEntry)}
     * or {@link #compareRdnValues(IEntry, IEntry)}.
     */
    public int compare( Viewer viewer, Object o1, Object o2 )
    {
        // o1 is StudioConnectionRunnableWithProgress
        if ( o1 instanceof StudioConnectionRunnableWithProgress )
        {
            StudioConnectionRunnableWithProgress runnable = ( StudioConnectionRunnableWithProgress ) o1;

            for ( Object lockedObject : runnable.getLockedObjects() )
            {
                if ( lockedObject instanceof ISearch )
                {
                    ISearch search = ( ISearch ) lockedObject;

                    if ( o1 == search.getTopSearchRunnable() )
                    {
                        return lessThanEntries();
                    }
                    else if ( o1 == search.getNextSearchRunnable() )
                    {
                        return greaterThanEntries();
                    }
                }
                else if ( lockedObject instanceof IEntry )
                {
                    IEntry entry = ( IEntry ) lockedObject;

                    if ( o1 == entry.getTopPageChildrenRunnable() )
                    {
                        return lessThanEntries();
                    }
                    else if ( o1 == entry.getNextPageChildrenRunnable() )
                    {
                        return greaterThanEntries();
                    }
                }
            }

            return lessThanEntries();
        }

        // o2 is StudioConnectionRunnableWithProgress
        if ( o2 instanceof StudioConnectionRunnableWithProgress )
        {
            StudioConnectionRunnableWithProgress runnable = ( StudioConnectionRunnableWithProgress ) o2;

            for ( Object lockedObject : runnable.getLockedObjects() )
            {
                if ( lockedObject instanceof ISearch )
                {
                    ISearch search = ( ISearch ) lockedObject;

                    if ( o2 == search.getTopSearchRunnable() )
                    {
                        return greaterThanEntries();
                    }
                    else if ( o2 == search.getNextSearchRunnable() )
                    {
                        return lessThanEntries();
                    }
                }
                else if ( lockedObject instanceof IEntry )
                {
                    IEntry entry = ( IEntry ) lockedObject;

                    if ( o2 == entry.getTopPageChildrenRunnable() )
                    {
                        return greaterThanEntries();
                    }
                    else if ( o2 == entry.getNextPageChildrenRunnable() )
                    {
                        return lessThanEntries();
                    }
                }
            }

            return greaterThanEntries();
        }

        // o1 and o2 are null
        if ( o1 == null && o2 == null )
        {
            return equal();
        }

        // o1 is null, o2 isn't
        else if ( o1 == null && o2 != null )
        {
            return lessThanEntries();
        }

        // o1 isn't null, o1 is
        else if ( o1 != null && o2 == null )
        {
            return greaterThanEntries();
        }
        
        // special case for quick search
        else if ( o1 instanceof IQuickSearch || o2 instanceof IQuickSearch )
        {
            if ( !( o1 instanceof IQuickSearch ) && ( o2 instanceof IQuickSearch ) )
            {
                return 1;
            }
            else if ( ( o1 instanceof IQuickSearch ) && !( o2 instanceof IQuickSearch ) )
            {
                return -1;
            }
            else
            {
                return equal();
            }
        }
        
        // o1 and o2 are entries
        else if ( o1 instanceof IEntry || o2 instanceof IEntry )
        {
            if ( !( o1 instanceof IEntry ) && !( o2 instanceof IEntry ) )
            {
                return equal();
            }
            else if ( !( o1 instanceof IEntry ) && ( o2 instanceof IEntry ) )
            {
                return lessThanEntries();
            }
            else if ( ( o1 instanceof IEntry ) && !( o2 instanceof IEntry ) )
            {
                return greaterThanEntries();
            }
            else
            {
                IEntry entry1 = ( IEntry ) o1;
                IEntry entry2 = ( IEntry ) o2;

                int cat1 = category( entry1 );
                int cat2 = category( entry2 );

                if ( cat1 != cat2 )
                {
                    return cat1 - cat2;
                }
                else if ( preferences.getSortEntriesBy() == BrowserCoreConstants.SORT_BY_NONE )
                {
                    return equal();
                }
                else if ( preferences.getSortEntriesBy() == BrowserCoreConstants.SORT_BY_RDN )
                {
                    return compareRdns( entry1, entry2 );
                }
                else if ( preferences.getSortEntriesBy() == BrowserCoreConstants.SORT_BY_RDN_VALUE )
                {
                    return compareRdnValues( entry1, entry2 );
                }
                else
                {
                    return equal();
                }
            }
        }

        // o1 and o2 are search results
        else if ( o1 instanceof ISearchResult || o2 instanceof ISearchResult )
        {
            if ( !( o1 instanceof ISearchResult ) && !( o2 instanceof ISearchResult ) )
            {
                return equal();
            }
            else if ( !( o1 instanceof ISearchResult ) && ( o2 instanceof ISearchResult ) )
            {
                return lessThanEntries();
            }
            else if ( ( o1 instanceof ISearchResult ) && !( o2 instanceof ISearchResult ) )
            {
                return greaterThanEntries();
            }
            else
            {
                ISearchResult sr1 = ( ISearchResult ) o1;
                ISearchResult sr2 = ( ISearchResult ) o2;

                int cat1 = category( sr1 );
                int cat2 = category( sr2 );

                if ( cat1 != cat2 )
                {
                    return cat1 - cat2;
                }
                else if ( preferences.getSortEntriesBy() == BrowserCoreConstants.SORT_BY_NONE )
                {
                    return equal();
                }
                else if ( preferences.getSortEntriesBy() == BrowserCoreConstants.SORT_BY_RDN )
                {
                    return compareRdns( sr1.getEntry(), sr2.getEntry() );
                }
                else if ( preferences.getSortEntriesBy() == BrowserCoreConstants.SORT_BY_RDN_VALUE )
                {
                    return compareRdnValues( sr1.getEntry(), sr2.getEntry() );
                }
                else
                {
                    return equal();
                }
            }
        }

        // o1 and o2 are searches
        else if ( o1 instanceof ISearch || o2 instanceof ISearch )
        {
            if ( !( o1 instanceof ISearch ) && !( o2 instanceof ISearch ) )
            {
                return equal();
            }
            else if ( !( o1 instanceof ISearch ) && ( o2 instanceof ISearch ) )
            {
                return lessThanSearches();
            }
            else if ( ( o1 instanceof ISearch ) && !( o2 instanceof ISearch ) )
            {
                return greaterThanSearches();
            }
            else
            {
                ISearch s1 = ( ISearch ) o1;
                ISearch s2 = ( ISearch ) o2;

                if ( preferences.getSortSearchesOrder() == BrowserCoreConstants.SORT_ORDER_NONE )
                {
                    return equal();
                }
                else
                {
                    return compareSearches( s1.getName(), s2.getName() );
                }
            }
        }

        // o1 and o2 are bookmarks
        else if ( o1 instanceof IBookmark || o2 instanceof IBookmark )
        {
            if ( !( o1 instanceof IBookmark ) && !( o2 instanceof IBookmark ) )
            {
                return equal();
            }
            else if ( !( o1 instanceof IBookmark ) && ( o2 instanceof IBookmark ) )
            {
                return lessThanBookmarks();
            }
            else if ( ( o1 instanceof IBookmark ) && !( o2 instanceof IBookmark ) )
            {
                return greaterThanBookmarks();
            }
            else
            {
                IBookmark b1 = ( IBookmark ) o1;
                IBookmark b2 = ( IBookmark ) o2;

                if ( preferences.getSortBookmarksOrder() == BrowserCoreConstants.SORT_ORDER_NONE )
                {
                    return equal();
                }
                else
                {
                    return compareBookmarks( b1.getName(), b2.getName() );
                }
            }
        }
        else
        {
            return equal();
        }
    }


    /**
     * Compares the string representation of the RDNs of two IEntry objects.
     *  
     * @param entry1 the first entry
     * @param entry2 the second entry
     * @return a negative integer, zero, or a positive integer
     */
    private int compareRdns( IEntry entry1, IEntry entry2 )
    {
        Rdn rdn1 = entry1.getRdn();
        Rdn rdn2 = entry2.getRdn();

        if ( rdn1 == null && rdn2 == null )
        {
            return equal();
        }
        else if ( rdn1 == null && rdn2 != null )
        {
            return greaterThanEntries();
        }
        else if ( rdn1 != null && rdn2 == null )
        {
            return lessThanEntries();
        }
        else
        {
            return compareEntries( rdn1.getName(), rdn2.getName() );
        }
    }


    /**
     * Compares the Rdn values of two IEntry objects.
     * Numeric values are compared as numeric.
     *  
     * @param entry1 the first entry
     * @param entry2 the second entry
     * @return a negative integer, zero, or a positive integer
     */
    private int compareRdnValues( IEntry entry1, IEntry entry2 )
    {
        if ( ( entry1 == null ) && ( entry2 == null ) )
        {
            return equal();
        }
        else if ( ( entry1 != null ) && ( entry2 == null ) )
        {
            return greaterThanEntries();
        }
        else if ( ( entry1 == null ) && ( entry2 != null ) )
        {
            return lessThanEntries();
        }
        else
        {
            Rdn rdn1 = entry1.getRdn();
            Rdn rdn2 = entry2.getRdn();

            if ( ( rdn1 == null || rdn1.getName() == null || "".equals( rdn1.getName() ) ) //$NON-NLS-1$
                && ( rdn2 == null || rdn2.getName() == null || "".equals( rdn2.getName() ) ) ) //$NON-NLS-1$
            {
                return equal();
            }
            else if ( ( rdn1 == null || rdn1.getName() == null || "".equals( rdn1.getName() ) ) //$NON-NLS-1$
                && !( rdn2 == null || rdn2.getName() == null || "".equals( rdn2.getName() ) ) ) //$NON-NLS-1$
            {
                return greaterThanEntries();
            }
            else if ( !( rdn1 == null || rdn1.getName() == null || "".equals( rdn1.getName() ) ) //$NON-NLS-1$
                && ( rdn2 == null || rdn2.getName() == null || "".equals( rdn2.getName() ) ) ) //$NON-NLS-1$
            {
                return lessThanEntries();
            }

            String rdn1Value = ( String ) rdn1.getName();
            String rdn2Value = ( String ) rdn2.getName();
            if ( rdn1Value.matches( "\\d*" ) && !rdn2Value.matches( "\\d*" ) ) //$NON-NLS-1$ //$NON-NLS-2$
            {
                // return lessThan();
                return compareEntries( rdn1Value, rdn2Value );
            }
            else if ( !rdn1Value.matches( "\\d*" ) && rdn2Value.matches( "\\d*" ) ) //$NON-NLS-1$ //$NON-NLS-2$
            {
                // return greaterThan();
                return compareEntries( rdn1Value, rdn2Value );
            }
            else if ( rdn2Value.matches( "\\d*" ) && rdn2Value.matches( "\\d*" ) ) //$NON-NLS-1$ //$NON-NLS-2$
            {
                BigInteger bi1 = new BigInteger( rdn1Value );
                BigInteger bi2 = new BigInteger( rdn2Value );
                return compare( bi1, bi2 );
                // return Integer.parseInt(rdn1.getValue()) -
                // Integer.parseInt(rdn2.getValue());
            }
            else
            {
                return compareEntries( rdn1Value, rdn2Value );
            }
        }
    }


    /**
     * Returns +1 or -1, depending on the sort entries order.
     *
     * @return +1 or -1, depending on the sort entries order
     */
    private int lessThanEntries()
    {
        return preferences.getSortEntriesOrder() == BrowserCoreConstants.SORT_ORDER_ASCENDING ? -1 : 1;
    }


    /**
     * Returns +1 or -1, depending on the sort searches order.
     *
     * @return +1 or -1, depending on the sort searches order
     */
    private int lessThanSearches()
    {
        return preferences.getSortSearchesOrder() == BrowserCoreConstants.SORT_ORDER_ASCENDING ? -1 : 1;
    }


    /**
     * Returns +1 or -1, depending on the sort entries order.
     *
     * @return +1 or -1, depending on the sort entries order
     */
    private int lessThanBookmarks()
    {
        return preferences.getSortBookmarksOrder() == BrowserCoreConstants.SORT_ORDER_ASCENDING ? -1 : 1;
    }


    /**
     * Returns 0.
     *
     * @return 0
     */
    private int equal()
    {
        return 0;
    }


    /**
     * Returns +1 or -1, depending on the sort entries order.
     *
     * @return +1 or -1, depending on the sort entries order
     */
    private int greaterThanEntries()
    {
        return preferences.getSortEntriesOrder() == BrowserCoreConstants.SORT_ORDER_ASCENDING ? 1 : -1;
    }


    /**
     * Returns +1 or -1, depending on the sort searches order.
     *
     * @return +1 or -1, depending on the sort searches order
     */
    private int greaterThanSearches()
    {
        return preferences.getSortSearchesOrder() == BrowserCoreConstants.SORT_ORDER_ASCENDING ? 1 : -1;
    }


    /**
     * Returns +1 or -1, depending on the sort bookmarks order.
     *
     * @return +1 or -1, depending on the sort bookmarks order
     */
    private int greaterThanBookmarks()
    {
        return preferences.getSortBookmarksOrder() == BrowserCoreConstants.SORT_ORDER_ASCENDING ? 1 : -1;
    }


    /**
     * Compares the two strings using the Strings's compareToIgnoreCase method, 
     * pays attention for the sort entries order.
     *
     * @param s1 the first string to compare
     * @param s2 the second string to compare
     * @return a negative integer, zero, or a positive integer
     * @see java.lang.String#compareToIgnoreCase(String)
     */
    private int compareEntries( String s1, String s2 )
    {
        return preferences.getSortEntriesOrder() == BrowserCoreConstants.SORT_ORDER_ASCENDING ? s1
            .compareToIgnoreCase( s2 ) : s2.compareToIgnoreCase( s1 );
    }


    /**
     * Compares the two strings using the Strings's compareToIgnoreCase method, 
     * pays attention for the sort searches order.
     *
     * @param s1 the first string to compare
     * @param s2 the second string to compare
     * @return a negative integer, zero, or a positive integer
     * @see java.lang.String#compareToIgnoreCase(String)
     */
    private int compareSearches( String s1, String s2 )
    {
        return preferences.getSortSearchesOrder() == BrowserCoreConstants.SORT_ORDER_ASCENDING ? s1
            .compareToIgnoreCase( s2 ) : s2.compareToIgnoreCase( s1 );
    }


    /**
     * Compares the two strings using the Strings's compareToIgnoreCase method, 
     * pays attention for the sort bookmarks order.
     *
     * @param s1 the first string to compare
     * @param s2 the second string to compare
     * @return a negative integer, zero, or a positive integer
     * @see java.lang.String#compareToIgnoreCase(String)
     */
    private int compareBookmarks( String s1, String s2 )
    {
        return preferences.getSortBookmarksOrder() == BrowserCoreConstants.SORT_ORDER_ASCENDING ? s1
            .compareToIgnoreCase( s2 ) : s2.compareToIgnoreCase( s1 );
    }


    /**
     * Compares the two numbers using the BigInteger compareTo method, 
     * pays attention for the sort order.
     *
     * @param bi1 the first number to compare
     * @param bi1 the second number to compare
     * @return -1, 0 or 1 as this BigInteger is numerically less than, equal
     *         to, or greater than
     * @see java.math.BigInteger#compareTo(BigInteger)
     */
    private int compare( BigInteger bi1, BigInteger bi2 )
    {
        return preferences.getSortEntriesOrder() == BrowserCoreConstants.SORT_ORDER_ASCENDING ? bi1.compareTo( bi2 )
            : bi2.compareTo( bi1 );
    }
}
