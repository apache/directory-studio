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

package org.apache.directory.ldapstudio.browser.ui.editors.searchresult;


import java.util.Arrays;
import java.util.Comparator;

import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TableColumn;


public class SearchResultEditorSorter extends ViewerSorter implements SelectionListener
{

    protected SearchResultEditorContentProvider contentProvider;

    private ISearch search;

    private TableColumn[] columns;

    private boolean showDn;

    private int sortBy;

    private int sortOrder;


    public SearchResultEditorSorter()
    {
        super();
    }


    public void connect( SearchResultEditorContentProvider contentProvider )
    {

        this.contentProvider = contentProvider;

        this.sortBy = 0;
        this.sortOrder = BrowserCoreConstants.SORT_ORDER_NONE;

        this.columns = this.contentProvider.getViewer().getTable().getColumns();
        for ( int i = 0; i < this.columns.length; i++ )
        {
            this.columns[i].addSelectionListener( this );
        }
    }


    public void inputChanged( ISearch newSearch, boolean showDn )
    {

        this.search = newSearch;
        this.showDn = showDn;

        for ( int i = 0; this.columns != null && i < this.columns.length; i++ )
        {
            this.columns[i].removeSelectionListener( this );
        }
        this.columns = this.contentProvider.getViewer().getTable().getColumns();
        for ( int i = 0; i < this.columns.length; i++ )
        {
            this.columns[i].addSelectionListener( this );
        }

        // check sort column
        int visibleColumns = this.search.getReturningAttributes().length;
        if ( this.showDn )
            visibleColumns++;
        if ( visibleColumns < this.sortBy + 1 )
        {
            this.setSortColumn( 0 );
            this.setSortColumn( 0 );
            this.setSortColumn( 0 );
        }
    }


    public void dispose()
    {
        for ( int i = 0; this.columns != null && i < this.columns.length; i++ )
        {
            if ( !this.columns[i].isDisposed() )
                this.columns[i].removeSelectionListener( this );
        }
        this.columns = null;
        this.search = null;
        this.contentProvider = null;
    }


    public void widgetDefaultSelected( SelectionEvent e )
    {
    }


    public void widgetSelected( SelectionEvent e )
    {
        if ( e.widget instanceof TableColumn )
        {
            int index = this.contentProvider.getViewer().getTable().indexOf( ( ( TableColumn ) e.widget ) );
            this.setSortColumn( index );
        }
    }


    private void setSortColumn( int index )
    {
        if ( this.sortBy == index )
        {
            // toggle sort order
            this.sortOrder = this.sortOrder == BrowserCoreConstants.SORT_ORDER_ASCENDING ? BrowserCoreConstants.SORT_ORDER_DESCENDING
                : this.sortOrder == BrowserCoreConstants.SORT_ORDER_DESCENDING ? BrowserCoreConstants.SORT_ORDER_NONE
                    : BrowserCoreConstants.SORT_ORDER_ASCENDING;
        }
        else
        {
            // set new sort by
            this.sortBy = index;
            this.sortOrder = BrowserCoreConstants.SORT_ORDER_ASCENDING;
        }
        if ( this.sortOrder == BrowserCoreConstants.SORT_ORDER_NONE )
        {
            this.sortBy = BrowserCoreConstants.SORT_BY_NONE;
        }

        TableColumn[] columns = this.contentProvider.getViewer().getTable().getColumns();
        for ( int i = 0; i < columns.length; i++ )
        {
            columns[i].setImage( null );
        }

        if ( this.sortOrder == BrowserCoreConstants.SORT_ORDER_ASCENDING )
        {
            ( columns[index] ).setImage( BrowserCommonActivator.getDefault().getImage(
                BrowserCommonConstants.IMG_SORT_ASCENDING ) );
        }
        else if ( this.sortOrder == BrowserCoreConstants.SORT_ORDER_DESCENDING )
        {
            ( columns[index] ).setImage( BrowserCommonActivator.getDefault().getImage(
                BrowserCommonConstants.IMG_SORT_DESCENDING ) );
        }
        else
        {
            ( columns[index] ).setImage( null );
        }

        this.contentProvider.refresh();

    }


    public boolean isSorted()
    {
        // return this.sortOrder != SORT_ORDER_NONE;
        return true;
    }


    public void sort( final Viewer viewer, Object[] elements )
    {

        if ( isSorted() )
        {
            Arrays.sort( elements, new Comparator()
            {
                public int compare( Object a, Object b )
                {
                    return SearchResultEditorSorter.this.compare( viewer, a, b );
                }
            } );
        }

    }


    public int compare( Viewer viewer, Object o1, Object o2 )
    {

        if ( this.search == null )
        {
            return this.equal();
        }

        // if(o1 == null && o2 == null) {
        // return this.equal();
        // }
        // else if(o1 == null && o2 != null) {
        // return this.lessThan();
        // }
        // else if(o1 != null && o2 == null) {
        // return this.greaterThan();
        // }
        // else {
        // if(!(o1 instanceof ISearchResult) && !(o2 instanceof ISearchResult))
        // {
        // return this.equal();
        // }
        // else if(!(o1 instanceof ISearchResult) && (o2 instanceof
        // ISearchResult)) {
        // return this.lessThan();
        // }
        // else if((o1 instanceof ISearchResult) && !(o2 instanceof
        // ISearchResult)) {
        // return this.greaterThan();
        // }
        // else {
        ISearchResult sr1 = ( ISearchResult ) o1;
        ISearchResult sr2 = ( ISearchResult ) o2;

        IEntry entry1 = sr1.getEntry();
        IEntry entry2 = sr2.getEntry();

        String attributeName;
        if ( showDn && this.sortBy == 0 )
        {
            attributeName = BrowserUIConstants.DN;
        }
        else if ( showDn && this.sortBy > 0 )
        {
            attributeName = this.search.getReturningAttributes()[this.sortBy - 1];
        }
        else
        {
            attributeName = this.search.getReturningAttributes()[this.sortBy];
        }

        if ( attributeName == BrowserUIConstants.DN )
        {
            return compare( entry1.getDn().toString(), entry2.getDn().toString() );
        }
        else
        {
            AttributeHierarchy ah1 = entry1.getAttributeWithSubtypes( attributeName );
            AttributeHierarchy ah2 = entry2.getAttributeWithSubtypes( attributeName );
            if ( ah1 == null && ah2 == null )
            {
                return this.equal();
            }
            else if ( ah1 == null && ah2 != null )
            {
                return this.lessThan();
            }
            else if ( ah1 != null && ah2 == null )
            {
                return this.greaterThan();
            }
            else
            {
                IAttribute attribute1 = ah1.getAttribute();
                IAttribute attribute2 = ah2.getAttribute();

                String value1 = getValue( attribute1 );
                String value2 = getValue( attribute2 );
                return compare( value1, value2 );
            }
        }
        // }
        // }
    }


    private String getValue( IAttribute attribute )
    {
        if ( attribute.getValueSize() > 0 )
        {
            return attribute.getStringValue();
        }
        else
        {
            return "";
        }
    }


    private int lessThan()
    {
        return this.sortOrder == BrowserCoreConstants.SORT_ORDER_ASCENDING ? -1 : 1;
    }


    private int equal()
    {
        return 0;
    }


    private int greaterThan()
    {
        return this.sortOrder == BrowserCoreConstants.SORT_ORDER_ASCENDING ? 1 : -1;
    }


    private int compare( String s1, String s2 )
    {
        return this.sortOrder == BrowserCoreConstants.SORT_ORDER_ASCENDING ? s1.compareToIgnoreCase( s2 ) : s2
            .compareToIgnoreCase( s1 );
    }

}
