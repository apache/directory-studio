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

import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.internal.model.DirectoryMetadataEntry;
import org.apache.directory.studio.ldapbrowser.core.internal.model.RootDSE;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.RDN;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;


/**
 * The BrowserSorter implements the sorter for the browser widget. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
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
     * Disposes this sorter.
     */
    public void dispose()
    {
    }


    /**
     * {@inheritDoc}
     * 
     * For performance reasons this implemention first checks if sorting is enabled 
     * and if the number of elements is less than the sort limit.
     */
    public void sort( final Viewer viewer, final Object[] elements )
    {
        if ( elements != null && ( preferences.getSortLimit() <= 0 || elements.length < preferences.getSortLimit() )
            && ( preferences.getSortBy() != BrowserCoreConstants.SORT_BY_NONE || preferences.isLeafEntriesFirst() ) )
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
        if ( preferences.isLeafEntriesFirst() || preferences.isMetaEntriesLast() )
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
                else if ( entry.isSubentry() && preferences.isLeafEntriesFirst() )
                {
                    return 0;
                }
                else if ( !entry.hasChildren() && preferences.isLeafEntriesFirst() )
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
        else
        {
            return 0;
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
        if ( o1 == null && o2 == null )
        {
            return equal();
        }
        else if ( o1 == null && o2 != null )
        {
            return lessThan();
        }
        else if ( o1 != null && o2 == null )
        {
            return greaterThan();
        }
        else if ( o1 instanceof IEntry || o2 instanceof IEntry )
        {
            if ( !( o1 instanceof IEntry ) && !( o2 instanceof IEntry ) )
            {
                return equal();
            }
            else if ( !( o1 instanceof IEntry ) && ( o2 instanceof IEntry ) )
            {
                return lessThan();
            }
            else if ( ( o1 instanceof IEntry ) && !( o2 instanceof IEntry ) )
            {
                return greaterThan();
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
                else if ( preferences.getSortBy() == BrowserCoreConstants.SORT_BY_NONE )
                {
                    return equal();
                }
                else if ( preferences.getSortBy() == BrowserCoreConstants.SORT_BY_RDN )
                {
                    return compareRdns( entry1, entry2 );
                }
                else if ( preferences.getSortBy() == BrowserCoreConstants.SORT_BY_RDN_VALUE )
                {
                    return compareRdnValues( entry1, entry2 );
                }
                else
                {
                    return equal();
                }
            }
        }
        else if ( o1 instanceof ISearchResult || o2 instanceof ISearchResult )
        {
            if ( !( o1 instanceof ISearchResult ) && !( o2 instanceof ISearchResult ) )
            {
                return equal();
            }
            else if ( !( o1 instanceof ISearchResult ) && ( o2 instanceof ISearchResult ) )
            {
                return lessThan();
            }
            else if ( ( o1 instanceof ISearchResult ) && !( o2 instanceof ISearchResult ) )
            {
                return greaterThan();
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
                else if ( preferences.getSortBy() == BrowserCoreConstants.SORT_BY_NONE )
                {
                    return equal();
                }
                else if ( preferences.getSortBy() == BrowserCoreConstants.SORT_BY_RDN )
                {
                    return compareRdns( sr1.getEntry(), sr2.getEntry() );
                }
                else if ( preferences.getSortBy() == BrowserCoreConstants.SORT_BY_RDN_VALUE )
                {
                    return compareRdnValues( sr1.getEntry(), sr2.getEntry() );
                }
                else
                {
                    return equal();
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
        RDN rdn1 = entry1.getRdn();
        RDN rdn2 = entry2.getRdn();

        if ( rdn1 == null && rdn2 == null )
        {
            return equal();
        }
        else if ( rdn1 == null && rdn2 != null )
        {
            return greaterThan();
        }
        else if ( rdn1 != null && rdn2 == null )
        {
            return lessThan();
        }
        else
        {
            return compare( rdn1.toString(), rdn2.toString() );
        }
    }


    /**
     * Compares the RDN values of two IEntry objects.
     * Numeric values are compared as numeric.
     *  
     * @param entry1 the first entry
     * @param entry2 the second entry
     * @return a negative integer, zero, or a positive integer
     */
    private int compareRdnValues( IEntry entry1, IEntry entry2 )
    {

        RDN rdn1 = entry1.getRdn();
        RDN rdn2 = entry2.getRdn();

        if ( ( rdn1 == null || rdn1.getValue() == null || "".equals( rdn1.getValue() ) )
            && ( rdn2 == null || rdn2.getValue() == null || "".equals( rdn2.getValue() ) ) )
        {
            return equal();
        }
        else if ( ( rdn1 == null || rdn1.getValue() == null || "".equals( rdn1.getValue() ) )
            && !( rdn2 == null || rdn2.getValue() == null || "".equals( rdn2.getValue() ) ) )
        {
            return greaterThan();
        }
        else if ( !( rdn1 == null || rdn1.getValue() == null || "".equals( rdn1.getValue() ) )
            && ( rdn2 == null || rdn2.getValue() == null || "".equals( rdn2.getValue() ) ) )
        {
            return lessThan();
        }

        else if ( rdn1.getValue().matches( "\\d*" ) && !rdn2.getValue().matches( "\\d*" ) )
        {
            // return lessThan();
            return compare( rdn1.getValue(), rdn2.getValue() );
        }
        else if ( !rdn1.getValue().matches( "\\d*" ) && rdn2.getValue().matches( "\\d*" ) )
        {
            // return greaterThan();
            return compare( rdn1.getValue(), rdn2.getValue() );
        }
        else if ( rdn1.getValue().matches( "\\d*" ) && rdn2.getValue().matches( "\\d*" ) )
        {
            BigInteger bi1 = new BigInteger( rdn1.getValue() );
            BigInteger bi2 = new BigInteger( rdn2.getValue() );
            return compare( bi1, bi2 );
            // return Integer.parseInt(rdn1.getValue()) -
            // Integer.parseInt(rdn2.getValue());
        }
        else
        {
            return compare( rdn1.getValue(), rdn2.getValue() );
        }
    }


    /**
     * Returns +1 or -1, depending on the sort order.
     *
     * @return +1 or -1, depending on the sort order
     */
    private int lessThan()
    {
        return preferences.getSortOrder() == BrowserCoreConstants.SORT_ORDER_ASCENDING ? -1 : 1;
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
     * Returns +1 or -1, depending on the sort order.
     *
     * @return +1 or -1, depending on the sort order
     */
    private int greaterThan()
    {
        return preferences.getSortOrder() == BrowserCoreConstants.SORT_ORDER_ASCENDING ? 1 : -1;
    }


    /**
     * Compares the two strings using the Strings's compareToIgnoreCase method, 
     * pays attention for the sort order.
     *
     * @param s1 the first string to compare
     * @param s2 the second string to compare
     * @return a negative integer, zero, or a positive integer
     * @see java.lang.String#compareToIgnoreCase(String)
     */
    private int compare( String s1, String s2 )
    {
        return preferences.getSortOrder() == BrowserCoreConstants.SORT_ORDER_ASCENDING ? s1.compareToIgnoreCase( s2 )
            : s2.compareToIgnoreCase( s1 );
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
        return preferences.getSortOrder() == BrowserCoreConstants.SORT_ORDER_ASCENDING ? bi1.compareTo( bi2 ) : bi2
            .compareTo( bi1 );
    }

}
