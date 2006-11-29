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

package org.apache.directory.ldapstudio.browser.view.views;


import java.util.Comparator;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.TableColumn;


/**
 * This class implements the Sorter for the Attributes View
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AttributesViewSorter extends ViewerSorter
{
    /**
     * Simple data structure for grouping sort information by column.
     * 
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    private class SortableColumnInfo
    {
        Comparator comparator;
        boolean descending;
    }

    /** The associated viewer */
    private TableViewer viewer;

    /** The information on the sortable columns */
    private SortableColumnInfo[] infos;

    /** The currently selected sorter */
    private SortableColumnInfo currentSorter;


    /**
     * Creates a new instance of AttributesViewSorter.
     *
     * @param viewer the associated viewer
     * @param columns the array containing the sortable columns
     * @param comparators the array containing the associated comparators
     */
    public AttributesViewSorter( TableViewer viewer, TableColumn[] columns, Comparator[] comparators )
    {
        this.viewer = viewer;
        infos = new SortableColumnInfo[columns.length];
        for ( int i = 0; i < columns.length; i++ )
        {
            infos[i] = new SortableColumnInfo();
            infos[i].comparator = comparators[i];
            infos[i].descending = false;
            createSelectionListener( columns[i], infos[i] );
            if ( i == 0 )
            {
                currentSorter = infos[i];
            }
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public int compare( Viewer viewer, Object attribute1, Object attribute2 )
    {
        int result = currentSorter.comparator.compare( attribute1, attribute2 );
        if ( currentSorter.descending )
        {
            return -result;
        }
        else
        {
            return result;
        }
    }


    /**
     * Adds a SelectionLister on the given column and launches sorting when a SelectionEvent is triggerd
     *
     * @param column
     * @param info
     */
    private void createSelectionListener( TableColumn column, final SortableColumnInfo info )
    {
        column.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                sortUsing( info );
            }
        } );
    }


    /**
     * Sorts the viewer using given sort information
     *
     * @param info the associated sort information
     */
    private void sortUsing( SortableColumnInfo info )
    {
        currentSorter = info;
        currentSorter.descending = !currentSorter.descending;
        viewer.refresh();
    }
}
