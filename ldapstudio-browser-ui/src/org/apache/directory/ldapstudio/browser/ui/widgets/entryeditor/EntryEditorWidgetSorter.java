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

package org.apache.directory.ldapstudio.browser.ui.widgets.entryeditor;


import java.util.Arrays;
import java.util.Comparator;

import org.apache.directory.ldapstudio.browser.core.BrowserCoreConstants;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TreeColumn;


public class EntryEditorWidgetSorter extends ViewerSorter implements SelectionListener
{

    private TreeViewer viewer;

    private int sortBy;

    private int sortOrder;

    private EntryEditorWidgetPreferences preferences;


    public EntryEditorWidgetSorter( EntryEditorWidgetPreferences preferences )
    {
        this.sortBy = BrowserCoreConstants.SORT_BY_NONE;
        this.sortOrder = BrowserCoreConstants.SORT_ORDER_NONE;
        this.preferences = preferences;
    }


    public void connect( TreeViewer viewer )
    {
        this.viewer = viewer;
        this.viewer.setSorter( this );

        TreeColumn[] columns = ( ( TreeViewer ) viewer ).getTree().getColumns();
        for ( int i = 0; i < columns.length; i++ )
        {
            columns[i].addSelectionListener( this );
        }
    }


    public void dispose()
    {
        /*
         * TableColumn[] columns = this.viewer.getTable().getColumns(); for(int
         * i=0; i <columns.length; i++) {
         * columns[i].removeSelectionListener(this); }
         */
        this.viewer = null;
    }


    public void widgetDefaultSelected( SelectionEvent e )
    {
    }


    public void widgetSelected( SelectionEvent e )
    {
        if ( e.widget instanceof TreeColumn && this.viewer != null )
        {

            int index = this.viewer.getTree().indexOf( ( ( TreeColumn ) e.widget ) );
            switch ( index )
            {
                case EntryEditorWidgetTableMetadata.KEY_COLUMN_INDEX:
                    if ( this.sortBy == BrowserCoreConstants.SORT_BY_ATTRIBUTE_DESCRIPTION )
                    {
                        // toggle sort order
                        this.sortOrder = this.sortOrder == BrowserCoreConstants.SORT_ORDER_NONE ? BrowserCoreConstants.SORT_ORDER_ASCENDING
                            : this.sortOrder == BrowserCoreConstants.SORT_ORDER_ASCENDING ? BrowserCoreConstants.SORT_ORDER_DESCENDING
                                : BrowserCoreConstants.SORT_ORDER_NONE;
                    }
                    else
                    {
                        // set new sort by
                        this.sortBy = BrowserCoreConstants.SORT_BY_ATTRIBUTE_DESCRIPTION;
                        this.sortOrder = BrowserCoreConstants.SORT_ORDER_ASCENDING;
                    }
                    break;
                case EntryEditorWidgetTableMetadata.VALUE_COLUMN_INDEX:
                    if ( this.sortBy == BrowserCoreConstants.SORT_BY_VALUE )
                    {
                        // toggle sort order
                        this.sortOrder = this.sortOrder == BrowserCoreConstants.SORT_ORDER_NONE ? BrowserCoreConstants.SORT_ORDER_ASCENDING
                            : this.sortOrder == BrowserCoreConstants.SORT_ORDER_ASCENDING ? BrowserCoreConstants.SORT_ORDER_DESCENDING
                                : BrowserCoreConstants.SORT_ORDER_NONE;
                    }
                    else
                    {
                        // set new sort by
                        this.sortBy = BrowserCoreConstants.SORT_BY_VALUE;
                        this.sortOrder = BrowserCoreConstants.SORT_ORDER_ASCENDING;
                    }
                    break;
                default:
                    ;
            }
            if ( this.sortOrder == BrowserCoreConstants.SORT_ORDER_NONE )
            {
                this.sortBy = BrowserCoreConstants.SORT_BY_NONE;
            }

            TreeColumn[] columns = this.viewer.getTree().getColumns();
            for ( int i = 0; i < columns.length; i++ )
            {
                columns[i].setImage( null );
            }

            if ( this.sortOrder == BrowserCoreConstants.SORT_ORDER_ASCENDING )
            {
                ( ( TreeColumn ) e.widget ).setImage( BrowserUIPlugin.getDefault().getImage(
                    BrowserUIConstants.IMG_SORT_ASCENDING ) );
            }
            else if ( this.sortOrder == BrowserCoreConstants.SORT_ORDER_DESCENDING )
            {
                ( ( TreeColumn ) e.widget ).setImage( BrowserUIPlugin.getDefault().getImage(
                    BrowserUIConstants.IMG_SORT_DESCENDING ) );
            }

            this.viewer.refresh();
        }
    }


    public void sort( final Viewer viewer, Object[] elements )
    {

        Arrays.sort( elements, new Comparator()
        {
            public int compare( Object a, Object b )
            {
                return EntryEditorWidgetSorter.this.compare( viewer, a, b );
            }
        } );

    }


    public int compare( Viewer viewer, Object o1, Object o2 )
    {

        // System.out.println("compare() " + o1 + " - " + o2);

        // if (o1 == null && o2 == null) {
        // return this.equal();
        // }
        // else if (o1 == null && o2 != null) {
        // return this.lessThan();
        // }
        // else if (o1 != null && o2 == null) {
        // return this.greaterThan();
        // }
        // else {
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

        if ( value1 != null && value2 != null )
        {
            if ( this.getSortByOrDefault() == BrowserCoreConstants.SORT_BY_ATTRIBUTE_DESCRIPTION )
            {
                if ( value1.getAttribute() != value2.getAttribute() )
                {
                    return this.compareAttributeNames( value1.getAttribute(), value2.getAttribute() );
                }
                else
                {
                    return this.compareValues( value1, value2 );
                }
            }
            else if ( this.getSortByOrDefault() == BrowserCoreConstants.SORT_BY_VALUE )
            {
                return this.compareValues( value1, value2 );
            }
            else
            {
                return this.equal();
            }
        }
        else if ( attribute1 != null && attribute2 != null )
        {
            return this.compareAttributeNames( attribute1, attribute2 );
        }
        else
        {
            return this.equal();
        }

        // }
    }


    private int compareAttributeNames( IAttribute attribute1, IAttribute attribute2 )
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
        // else if ((value1.isString()) && !(value2.isString())) {
        // return lessThan();
        // }
        // else if ((value2.isString()) && !(value1.isString())) {
        // return greaterThan();
        // }
        // else if ((value1.isString()) && (value2.isString())) {
        // return compare(value1.getStringValue(), value2.getStringValue());
        // }
        // else {
        // return equal();
        // }
        else
        {
            return compare( value1.getStringValue(), value2.getStringValue() );
        }
    }


    private int getSortOrderOrDefault()
    {
        if ( preferences == null )
        {
            return BrowserCoreConstants.SORT_ORDER_ASCENDING;
        }
        else if ( this.sortOrder == BrowserCoreConstants.SORT_ORDER_NONE )
        {
            return preferences.getDefaultSortOrder();
        }
        else
        {
            return this.sortOrder;
        }
    }


    private int getSortByOrDefault()
    {
        if ( preferences == null )
        {
            return BrowserCoreConstants.SORT_BY_ATTRIBUTE_DESCRIPTION;
        }
        else if ( this.sortBy == BrowserCoreConstants.SORT_BY_NONE )
        {
            return preferences.getDefaultSortBy();
        }
        else
        {
            return this.sortBy;
        }
    }


    private int lessThan()
    {
        return this.getSortOrderOrDefault() == BrowserCoreConstants.SORT_ORDER_ASCENDING ? -1 : 1;
    }


    private int equal()
    {
        return 0;
    }


    private int greaterThan()
    {
        return this.getSortOrderOrDefault() == BrowserCoreConstants.SORT_ORDER_ASCENDING ? 1 : -1;
    }


    private int compare( String s1, String s2 )
    {
        return this.getSortOrderOrDefault() == BrowserCoreConstants.SORT_ORDER_ASCENDING ? s1.compareToIgnoreCase( s2 )
            : s2.compareToIgnoreCase( s1 );
    }

}
