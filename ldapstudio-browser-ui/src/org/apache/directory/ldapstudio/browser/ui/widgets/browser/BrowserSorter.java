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


import java.math.BigInteger;

import org.apache.directory.ldapstudio.browser.core.BrowserCoreConstants;
import org.apache.directory.ldapstudio.browser.core.internal.model.DirectoryMetadataEntry;
import org.apache.directory.ldapstudio.browser.core.internal.model.RootDSE;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.ISearchResult;
import org.apache.directory.ldapstudio.browser.core.model.RDN;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;


public class BrowserSorter extends ViewerSorter
{

    private TreeViewer viewer;

    private BrowserPreferences preferences;


    public BrowserSorter( BrowserPreferences preferences )
    {
        this.preferences = preferences;
    }


    public void connect( TreeViewer viewer )
    {
        this.viewer = viewer;
        this.viewer.setSorter( this );
    }


    public void dispose()
    {
        this.viewer = null;
    }


    public void sort( final Viewer viewer, final Object[] elements )
    {
        if ( elements != null && ( preferences.getSortLimit() <= 0 || elements.length < preferences.getSortLimit() )
            && ( preferences.getSortBy() != BrowserCoreConstants.SORT_BY_NONE || preferences.isLeafEntriesFirst() ) )
        {
            BrowserSorter.super.sort( viewer, elements );
        }
    }


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


    public int compare( Viewer viewer, Object o1, Object o2 )
    {

        if ( o1 == null && o2 == null )
        {
            return this.equal();
        }
        else if ( o1 == null && o2 != null )
        {
            return this.lessThan();
        }
        else if ( o1 != null && o2 == null )
        {
            return this.greaterThan();
        }
        else if ( o1 instanceof IEntry || o2 instanceof IEntry )
        {

            if ( !( o1 instanceof IEntry ) && !( o2 instanceof IEntry ) )
            {
                return this.equal();
            }
            else if ( !( o1 instanceof IEntry ) && ( o2 instanceof IEntry ) )
            {
                return this.lessThan();
            }
            else if ( ( o1 instanceof IEntry ) && !( o2 instanceof IEntry ) )
            {
                return this.greaterThan();
            }
            else
            {
                IEntry entry1 = ( IEntry ) o1;
                IEntry entry2 = ( IEntry ) o2;

                int cat1 = this.category( entry1 );
                int cat2 = this.category( entry2 );
                if ( cat1 != cat2 )
                {
                    return cat1 - cat2;
                }
                else if ( preferences.getSortBy() == BrowserCoreConstants.SORT_BY_NONE )
                {
                    return this.equal();
                }
                else if ( preferences.getSortBy() == BrowserCoreConstants.SORT_BY_RDN )
                {
                    return this.compareRdns( entry1, entry2 );
                }
                else if ( preferences.getSortBy() == BrowserCoreConstants.SORT_BY_RDN_VALUE )
                {
                    return this.compareRdnValues( entry1, entry2 );
                }
                else
                {
                    return this.equal();
                }
            }
        }
        else if ( o1 instanceof ISearchResult || o2 instanceof ISearchResult )
        {
            if ( !( o1 instanceof ISearchResult ) && !( o2 instanceof ISearchResult ) )
            {
                return this.equal();
            }
            else if ( !( o1 instanceof ISearchResult ) && ( o2 instanceof ISearchResult ) )
            {
                return this.lessThan();
            }
            else if ( ( o1 instanceof ISearchResult ) && !( o2 instanceof ISearchResult ) )
            {
                return this.greaterThan();
            }
            else
            {
                ISearchResult sr1 = ( ISearchResult ) o1;
                ISearchResult sr2 = ( ISearchResult ) o2;

                int cat1 = this.category( sr1 );
                int cat2 = this.category( sr2 );
                if ( cat1 != cat2 )
                {
                    return cat1 - cat2;
                }
                else if ( preferences.getSortBy() == BrowserCoreConstants.SORT_BY_NONE )
                {
                    return this.equal();
                }
                else if ( preferences.getSortBy() == BrowserCoreConstants.SORT_BY_RDN )
                {
                    return this.compareRdns( sr1.getEntry(), sr2.getEntry() );
                }
                else if ( preferences.getSortBy() == BrowserCoreConstants.SORT_BY_RDN_VALUE )
                {
                    return this.compareRdnValues( sr1.getEntry(), sr2.getEntry() );
                }
                else
                {
                    return this.equal();
                }
            }
        }
        else
        {
            return this.equal();
        }
    }


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


    private int lessThan()
    {
        return preferences.getSortOrder() == BrowserCoreConstants.SORT_ORDER_ASCENDING ? -1 : 1;
    }


    private int equal()
    {
        return 0;
    }


    private int greaterThan()
    {
        return preferences.getSortOrder() == BrowserCoreConstants.SORT_ORDER_ASCENDING ? 1 : -1;
    }


    private int compare( String s1, String s2 )
    {
        return preferences.getSortOrder() == BrowserCoreConstants.SORT_ORDER_ASCENDING ? s1.compareToIgnoreCase( s2 )
            : s2.compareToIgnoreCase( s1 );
    }


    private int compare( BigInteger bi1, BigInteger bi2 )
    {
        return preferences.getSortOrder() == BrowserCoreConstants.SORT_ORDER_ASCENDING ? bi1.compareTo( bi2 ) : bi2
            .compareTo( bi1 );
    }

}
