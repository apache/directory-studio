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
import org.apache.directory.studio.ldapbrowser.core.utils.AttributeComparator;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Tree;
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

        for ( TreeColumn column : ( ( TreeViewer ) viewer ).getTree().getColumns() )
        {
            column.addSelectionListener( this );
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
    public void widgetSelected( SelectionEvent event )
    {
        if ( ( event.widget instanceof TreeColumn ) && ( viewer != null ) )
        {
            Tree tree = viewer.getTree();
            TreeColumn treeColumn = ( ( TreeColumn ) event.widget );

            int index = tree.indexOf( treeColumn );

            switch ( index )
            {
                case EntryEditorWidgetTableMetadata.KEY_COLUMN_INDEX:
                    if ( sortBy == BrowserCoreConstants.SORT_BY_ATTRIBUTE_DESCRIPTION )
                    {
                        toggleSortOrder();
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
                        toggleSortOrder();
                    }
                    else
                    {
                        // set new sort by
                        sortBy = BrowserCoreConstants.SORT_BY_VALUE;
                        sortOrder = BrowserCoreConstants.SORT_ORDER_ASCENDING;
                    }

                    break;

                default:;
            }

            if ( sortOrder == BrowserCoreConstants.SORT_ORDER_NONE )
            {
                sortBy = BrowserCoreConstants.SORT_BY_NONE;
            }

            for ( TreeColumn column : tree.getColumns() )
            {
                column.setImage( null );
            }

            if ( sortOrder == BrowserCoreConstants.SORT_ORDER_ASCENDING )
            {
                treeColumn.setImage( BrowserCommonActivator.getDefault().getImage(
                    BrowserCommonConstants.IMG_SORT_ASCENDING ) );
            }
            else if ( sortOrder == BrowserCoreConstants.SORT_ORDER_DESCENDING )
            {
                treeColumn.setImage( BrowserCommonActivator.getDefault().getImage(
                    BrowserCommonConstants.IMG_SORT_DESCENDING ) );
            }

            viewer.refresh();
        }
    }


    /**
     * Rotate the sort order. If it was none, change it to ascending. If it was 
     * ascending, change it to descending, and if it was descending, change it to none.
     */
    private void toggleSortOrder()
    {
        switch ( sortOrder )
        {
            case BrowserCoreConstants.SORT_ORDER_NONE:
                sortOrder = BrowserCoreConstants.SORT_ORDER_ASCENDING;
                break;

            case BrowserCoreConstants.SORT_ORDER_ASCENDING:
                sortOrder = BrowserCoreConstants.SORT_ORDER_DESCENDING;
                break;

            case BrowserCoreConstants.SORT_ORDER_DESCENDING:
                sortOrder = BrowserCoreConstants.SORT_ORDER_NONE;
                break;
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
        boolean objectClassAndMustAttributesFirst = preferences == null
            || preferences.isObjectClassAndMustAttributesFirst();
        boolean operationalAttributesLast = preferences == null || preferences.isOperationalAttributesLast();
        AttributeComparator comparator = new AttributeComparator( sortBy, getDefaultSortBy(), sortOrder,
            getDefaultSortOrder(), objectClassAndMustAttributesFirst, operationalAttributesLast );
        return comparator.compare( o1, o2 );
    }


    private int getDefaultSortOrder()
    {
        if ( preferences == null )
        {
            return BrowserCoreConstants.SORT_ORDER_ASCENDING;
        }
        else
        {
            return preferences.getDefaultSortOrder();
        }
    }


    private int getDefaultSortBy()
    {
        if ( preferences == null )
        {
            return BrowserCoreConstants.SORT_BY_ATTRIBUTE_DESCRIPTION;
        }
        else
        {
            return preferences.getDefaultSortBy();
        }
    }

}
