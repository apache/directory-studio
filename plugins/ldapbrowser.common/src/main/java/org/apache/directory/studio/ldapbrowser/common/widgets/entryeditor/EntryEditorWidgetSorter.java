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

package org.apache.directory.studio.ldapbrowser.common.widgets.entryeditor;


import java.util.Arrays;
import java.util.Comparator;

import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TreeColumn;


/**
 * The EntryEditorWidgetSorter implements the Sorter for the entry editor widget.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EntryEditorWidgetSorter extends ViewerSorter implements SelectionListener
{

    /** The tree viewer. */
    private TreeViewer viewer;

    /** The sort property. */
    private int sortBy;

    /** The sort order. */
    private int sortOrder;

    /** The preferences. */
    private EntryEditorWidgetPreferences preferences;


    /**
     * Creates a new instance of EntryEditorWidgetSorter.
     * 
     * @param preferences the preferences
     */
    public EntryEditorWidgetSorter( EntryEditorWidgetPreferences preferences )
    {
        this.sortBy = BrowserCoreConstants.SORT_BY_NONE;
        this.sortOrder = BrowserCoreConstants.SORT_ORDER_NONE;
        this.preferences = preferences;
    }


    /**
     * Connects this sorter to the given tree viewer.
     * 
     * @param viewer the viewer
     */
    public void connect( TreeViewer viewer )
    {
        this.viewer = viewer;
        viewer.setSorter( this );

        TreeColumn[] columns = ( ( TreeViewer ) viewer ).getTree().getColumns();
        for ( int i = 0; i < columns.length; i++ )
        {
            columns[i].addSelectionListener( this );
        }
    }


    /**
     * Disposes this sorter.
     */
    public void dispose()
    {
        viewer = null;
        preferences = null;
    }


    /**
     * {@inheritDoc}
     */
    public void widgetDefaultSelected( SelectionEvent e )
    {
    }


    /**
     * {@inheritDoc}
     * 
     * Switches the sort property and sort order when clicking the table headers.
     */
    public void widgetSelected( SelectionEvent e )
    {
        if ( e.widget instanceof TreeColumn && viewer != null )
        {
            int index = viewer.getTree().indexOf( ( ( TreeColumn ) e.widget ) );
            switch ( index )
            {
                case EntryEditorWidgetTableMetadata.KEY_COLUMN_INDEX:
                    if ( sortBy == BrowserCoreConstants.SORT_BY_ATTRIBUTE_DESCRIPTION )
                    {
                        // toggle sort order
                        sortOrder = sortOrder == BrowserCoreConstants.SORT_ORDER_NONE ? BrowserCoreConstants.SORT_ORDER_ASCENDING
                            : sortOrder == BrowserCoreConstants.SORT_ORDER_ASCENDING ? BrowserCoreConstants.SORT_ORDER_DESCENDING
                                : BrowserCoreConstants.SORT_ORDER_NONE;
                    }
                    else
                    {
                        // set new sort by
                        sortBy = BrowserCoreConstants.SORT_BY_ATTRIBUTE_DESCRIPTION;
                        sortOrder = BrowserCoreConstants.SORT_ORDER_ASCENDING;
                    }
                    break;
                case EntryEditorWidgetTableMetadata.VALUE_COLUMN_INDEX:
                    if ( sortBy == BrowserCoreConstants.SORT_BY_VALUE )
                    {
                        // toggle sort order
                        sortOrder = sortOrder == BrowserCoreConstants.SORT_ORDER_NONE ? BrowserCoreConstants.SORT_ORDER_ASCENDING
                            : sortOrder == BrowserCoreConstants.SORT_ORDER_ASCENDING ? BrowserCoreConstants.SORT_ORDER_DESCENDING
                                : BrowserCoreConstants.SORT_ORDER_NONE;
                    }
                    else
                    {
                        // set new sort by
                        sortBy = BrowserCoreConstants.SORT_BY_VALUE;
                        sortOrder = BrowserCoreConstants.SORT_ORDER_ASCENDING;
                    }
                    break;
                default:
                    ;
            }
            if ( sortOrder == BrowserCoreConstants.SORT_ORDER_NONE )
            {
                sortBy = BrowserCoreConstants.SORT_BY_NONE;
            }

            TreeColumn[] columns = viewer.getTree().getColumns();
            for ( int i = 0; i < columns.length; i++ )
            {
                columns[i].setImage( null );
            }

            if ( sortOrder == BrowserCoreConstants.SORT_ORDER_ASCENDING )
            {
                ( ( TreeColumn ) e.widget ).setImage( BrowserCommonActivator.getDefault().getImage(
                    BrowserCommonConstants.IMG_SORT_ASCENDING ) );
            }
            else if ( sortOrder == BrowserCoreConstants.SORT_ORDER_DESCENDING )
            {
                ( ( TreeColumn ) e.widget ).setImage( BrowserCommonActivator.getDefault().getImage(
                    BrowserCommonConstants.IMG_SORT_DESCENDING ) );
            }

            viewer.refresh();
        }
    }


    /**
     * {@inheritDoc}
     */
    public void sort( final Viewer viewer, Object[] elements )
    {
        Arrays.sort( elements, new Comparator<Object>()
        {
            public int compare( Object a, Object b )
            {
                return EntryEditorWidgetSorter.this.compare( viewer, a, b );
            }
        } );

    }


    /**
     * {@inheritDoc}
     */
    public int compare( Viewer viewer, Object o1, Object o2 )
    {
        // check o1
        IAttribute attribute1 = null;
        IValue value1 = null;
        if ( o1 instanceof IAttribute )
        {
            attribute1 = ( IAttribute ) o1;
        }
        else if ( o1 instanceof IValue )
        {
            value1 = ( IValue ) o1;
            attribute1 = value1.getAttribute();
        }

        // check o2
        IAttribute attribute2 = null;
        IValue value2 = null;
        if ( o2 instanceof IAttribute )
        {
            attribute2 = ( IAttribute ) o2;
        }
        else if ( o2 instanceof IValue )
        {
            value2 = ( IValue ) o2;
            attribute2 = value2.getAttribute();
        }

        // compare
        if ( value1 != null && value2 != null )
        {
            if ( getSortByOrDefault() == BrowserCoreConstants.SORT_BY_ATTRIBUTE_DESCRIPTION )
            {
                if ( value1.getAttribute() != value2.getAttribute() )
                {
                    return compareAttributes( value1.getAttribute(), value2.getAttribute() );
                }
                else
                {
                    return compareValues( value1, value2 );
                }
            }
            else if ( getSortByOrDefault() == BrowserCoreConstants.SORT_BY_VALUE )
            {
                return compareValues( value1, value2 );
            }
            else
            {
                return equal();
            }
        }
        else if ( attribute1 != null && attribute2 != null )
        {
            return compareAttributes( attribute1, attribute2 );
        }
        else
        {
            return equal();
        }
    }


    /**
     * Compares attribute kind or description.
     * 
     * @param attribute1 the attribute1
     * @param attribute2 the attribute2
     * 
     * @return the compare result
     */
    private int compareAttributes( IAttribute attribute1, IAttribute attribute2 )
    {
        if ( this.sortOrder == BrowserCoreConstants.SORT_ORDER_NONE )
        {
            if ( preferences == null || preferences.isObjectClassAndMustAttributesFirst() )
            {
                if ( attribute1.isObjectClassAttribute() )
                {
                    return lessThan();
                }
                else if ( attribute2.isObjectClassAttribute() )
                {
                    return greaterThan();
                }

                if ( attribute1.isMustAttribute() && !attribute2.isMustAttribute() )
                {
                    return lessThan();
                }
                else if ( attribute2.isMustAttribute() && !attribute1.isMustAttribute() )
                {
                    return greaterThan();
                }
            }
            if ( preferences == null || preferences.isOperationalAttributesLast() )
            {
                if ( attribute1.isOperationalAttribute() && !attribute2.isOperationalAttribute() )
                {
                    return greaterThan();
                }
                else if ( attribute2.isOperationalAttribute() && !attribute1.isOperationalAttribute() )
                {
                    return lessThan();
                }
            }
        }

        return compare( attribute1.getDescription(), attribute2.getDescription() );
    }


    /**
     * Compares values.
     * 
     * @param value1 the value1
     * @param value2 the value2
     * 
     * @return the compare result
     */
    private int compareValues( IValue value1, IValue value2 )
    {
        if ( value1.isEmpty() && value2.isEmpty() )
        {
            return equal();
        }
        else if ( value1.isEmpty() && !value2.isEmpty() )
        {
            return greaterThan();
        }
        else if ( !value1.isEmpty() && value2.isEmpty() )
        {
            return lessThan();
        }
        else
        {
            return compare( value1.getStringValue(), value2.getStringValue() );
        }
    }


    /**
     * Gets the current sort order or the default sort order from the preferences .
     * 
     * @return the current sort order or default sort order
     */
    private int getSortOrderOrDefault()
    {
        if ( preferences == null )
        {
            return BrowserCoreConstants.SORT_ORDER_ASCENDING;
        }
        else if ( sortOrder == BrowserCoreConstants.SORT_ORDER_NONE )
        {
            return preferences.getDefaultSortOrder();
        }
        else
        {
            return sortOrder;
        }
    }


    /**
     * Gets the current sort property or the default sort property from the preferences .
     * 
     * @return the current sort property or default sort property
     */
    private int getSortByOrDefault()
    {
        if ( preferences == null )
        {
            return BrowserCoreConstants.SORT_BY_ATTRIBUTE_DESCRIPTION;
        }
        else if ( sortBy == BrowserCoreConstants.SORT_BY_NONE )
        {
            return preferences.getDefaultSortBy();
        }
        else
        {
            return sortBy;
        }
    }


    /**
     * Returns +1 or -1, depending on the sort order.
     *
     * @return +1 or -1, depending on the sort order
     */
    private int lessThan()
    {
        return this.getSortOrderOrDefault() == BrowserCoreConstants.SORT_ORDER_ASCENDING ? -1 : 1;
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
        return this.getSortOrderOrDefault() == BrowserCoreConstants.SORT_ORDER_ASCENDING ? 1 : -1;
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
        return this.getSortOrderOrDefault() == BrowserCoreConstants.SORT_ORDER_ASCENDING ? s1.compareToIgnoreCase( s2 )
            : s2.compareToIgnoreCase( s1 );
    }

}
