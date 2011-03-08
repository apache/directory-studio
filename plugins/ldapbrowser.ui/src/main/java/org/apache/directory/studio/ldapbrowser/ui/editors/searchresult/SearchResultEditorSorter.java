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

package org.apache.directory.studio.ldapbrowser.ui.editors.searchresult;


import java.util.Arrays;
import java.util.Comparator;

import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TableColumn;


/**
 * The SearchResultEditorSorter implements the Sorter for the search result editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchResultEditorSorter extends ViewerSorter implements SelectionListener
{

    /** The content provider. */
    protected SearchResultEditorContentProvider contentProvider;

    /** The search. */
    private ISearch search;

    /** The columns. */
    private TableColumn[] columns;

    /** The show Dn flag. */
    private boolean showDn;

    /** The sort property. */
    private int sortBy;

    /** The sort order. */
    private int sortOrder;


    /**
     * Connects this sorter to the given content provider.
     * 
     * @param contentProvider the content provider
     */
    public void connect( SearchResultEditorContentProvider contentProvider )
    {
        this.contentProvider = contentProvider;

        sortBy = 0;
        sortOrder = BrowserCoreConstants.SORT_ORDER_NONE;

        columns = contentProvider.getViewer().getTable().getColumns();

        for ( TableColumn column : columns )
        {
            column.addSelectionListener( this );
        }
    }


    /**
     * Called when the input of the viewer has been changed.
     * 
     * @param newSearch the new search
     * @param showDn the show Dn flag
     */
    public void inputChanged( ISearch newSearch, boolean showDn )
    {
        this.search = newSearch;
        this.showDn = showDn;

        if ( columns != null )
        {
            for ( TableColumn column : columns )
            {
                column.removeSelectionListener( this );
            }
        }
        
        columns = contentProvider.getViewer().getTable().getColumns();
        
        for ( TableColumn column : columns )
        {
            column.addSelectionListener( this );
        }

        // check sort column
        int visibleColumns = search.getReturningAttributes().length;
        
        if ( showDn )
        {
            visibleColumns++;
        }
        
        if ( visibleColumns < sortBy + 1 )
        {
            setSortColumn( 0 );
            setSortColumn( 0 );
            setSortColumn( 0 );
        }
    }


    public void dispose()
    {
        if ( columns != null )
        {
            for ( TableColumn column : columns )
            {
                if ( !column.isDisposed() )
                {
                    column.removeSelectionListener( this );
                }
            }
        }
        
        columns = null;
        search = null;
        contentProvider = null;
    }


    /**
     * {@inheritDoc}
     */
    public void widgetDefaultSelected( SelectionEvent e )
    {
    }


    /**
     * {@inheritDoc}
     */
    public void widgetSelected( SelectionEvent e )
    {
        if ( e.widget instanceof TableColumn )
        {
            int index = contentProvider.getViewer().getTable().indexOf( ( ( TableColumn ) e.widget ) );
            setSortColumn( index );
        }
    }


    /**
     * Sets the sort column.
     * 
     * @param index the new sort column
     */
    private void setSortColumn( int index )
    {
        if ( sortBy == index )
        {
            // toggle sort order
            sortOrder = sortOrder == BrowserCoreConstants.SORT_ORDER_ASCENDING ? BrowserCoreConstants.SORT_ORDER_DESCENDING
                : sortOrder == BrowserCoreConstants.SORT_ORDER_DESCENDING ? BrowserCoreConstants.SORT_ORDER_NONE
                    : BrowserCoreConstants.SORT_ORDER_ASCENDING;
        }
        else
        {
            // set new sort by
            sortBy = index;
            sortOrder = BrowserCoreConstants.SORT_ORDER_ASCENDING;
        }
        
        if ( sortOrder == BrowserCoreConstants.SORT_ORDER_NONE )
        {
            sortBy = BrowserCoreConstants.SORT_BY_NONE;
        }

        TableColumn[] columns = contentProvider.getViewer().getTable().getColumns();
        
        for ( TableColumn column : columns )
        {
            column.setImage( null );
        }

        if ( sortOrder == BrowserCoreConstants.SORT_ORDER_ASCENDING )
        {
            ( columns[index] ).setImage( BrowserCommonActivator.getDefault().getImage(
                BrowserCommonConstants.IMG_SORT_ASCENDING ) );
        }
        else if ( sortOrder == BrowserCoreConstants.SORT_ORDER_DESCENDING )
        {
            ( columns[index] ).setImage( BrowserCommonActivator.getDefault().getImage(
                BrowserCommonConstants.IMG_SORT_DESCENDING ) );
        }
        else
        {
            ( columns[index] ).setImage( null );
        }

        contentProvider.refresh();
    }


    /**
     * Checks if is sorted.
     * 
     * @return true, if is sorted
     */
    public boolean isSorted()
    {
        // return sortOrder != SORT_ORDER_NONE;
        return true;
    }


    /**
     * {@inheritDoc}
     */
    public void sort( final Viewer viewer, Object[] elements )
    {
        if ( isSorted() )
        {
            Arrays.sort( elements, new Comparator<Object>()
            {
                public int compare( Object a, Object b )
                {
                    return SearchResultEditorSorter.this.compare( viewer, a, b );
                }
            } );
        }

    }


    /**
     * {@inheritDoc}
     */
    public int compare( Viewer viewer, Object o1, Object o2 )
    {
        if ( search == null )
        {
            return equal();
        }

        ISearchResult sr1 = ( ISearchResult ) o1;
        ISearchResult sr2 = ( ISearchResult ) o2;

        IEntry entry1 = sr1.getEntry();
        IEntry entry2 = sr2.getEntry();

        if ( entry1 == null )
        {
            if ( entry2 == null )
            {
                return equal();
            }
            else
            {
                return lessThan();
            }
        }
        else if ( entry2 == null )
        {
            return greaterThan();
        }
        else
        {
            String attributeName;
            
            if ( showDn && ( sortBy == 0 ) )
            {
                attributeName = BrowserUIConstants.DN;
            }
            else if ( showDn && ( sortBy > 0 ) )
            {
                attributeName = search.getReturningAttributes()[sortBy - 1];
            }
            else
            {
                attributeName = search.getReturningAttributes()[sortBy];
            }

            if ( attributeName == BrowserUIConstants.DN )
            {
                // compare normalized names
                return compare( entry1.getDn().getNormName(), entry2.getDn().getNormName() );
            }
            else
            {
                AttributeHierarchy ah1 = entry1.getAttributeWithSubtypes( attributeName );
                AttributeHierarchy ah2 = entry2.getAttributeWithSubtypes( attributeName );
                
                if ( ah1 == null )
                {
                    if ( ah2 == null )
                    {
                        return equal();
                    }
                    else
                    {
                        return lessThan();
                    }
                }
                else if ( ah2 == null )
                {
                    return greaterThan();
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
        }
    }


    /**
     * Gets the value.
     * 
     * @param attribute the attribute
     * 
     * @return the value
     */
    private String getValue( IAttribute attribute )
    {
        if ( attribute.getValueSize() > 0 )
        {
            return attribute.getStringValue();
        }
        else
        {
            return ""; //$NON-NLS-1$
        }
    }


    /**
     * Gets the less than value.
     * 
     * @return the less than value
     */
    private int lessThan()
    {
        return sortOrder == BrowserCoreConstants.SORT_ORDER_ASCENDING ? -1 : 1;
    }


    /**
     * Gets the equal value.
     * 
     * @return the equal value
     */
    private int equal()
    {
        return 0;
    }


    /**
     * Gets the greater than value.
     * 
     * @return the greater than value
     */
    private int greaterThan()
    {
        return sortOrder == BrowserCoreConstants.SORT_ORDER_ASCENDING ? 1 : -1;
    }


    /**
     * Compare two strings.
     * 
     * @param s1 the 1st string
     * @param s2 the 2nd string
     * 
     * @return the compare result
     */
    private int compare( String s1, String s2 )
    {
        return sortOrder == BrowserCoreConstants.SORT_ORDER_ASCENDING ? s1.compareToIgnoreCase( s2 ) : s2
            .compareToIgnoreCase( s1 );
    }

}
