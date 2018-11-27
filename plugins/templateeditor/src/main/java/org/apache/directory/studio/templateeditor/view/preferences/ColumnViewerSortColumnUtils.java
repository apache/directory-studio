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
package org.apache.directory.studio.templateeditor.view.preferences;


import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.PlatformUI;

import org.apache.directory.studio.templateeditor.view.ColumnsTableViewerComparator;


/**
 * This helper class can be used to add sort columns to {@link TableViewer} 
 * and {@link TreeViewer} objects.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ColumnViewerSortColumnUtils
{
    /**
     * Adds a sort column to the table viewer.
     *
     * @param tableViewer
     *      the table viewer
     * @param tableColumn
     *      the table column
     */
    public static void addSortColumn( TableViewer tableViewer, TableColumn tableColumn )
    {
        if ( tableColumn == null )
        {
            return;
        }

        Table table = tableViewer.getTable();
        if ( table == null )
        {
            return;
        }

        // Looking for the column index of the table column
        for ( int columnIndex = 0; columnIndex < table.getColumnCount(); columnIndex++ )
        {
            if ( tableColumn.equals( table.getColumn( columnIndex ) ) )
            {
                tableColumn.addSelectionListener( getHeaderListener( tableViewer, columnIndex ) );
            }
        }
    }


    /**
     * Gets a header listener for the given column.
     * 
     * @param tableViewer
     *      the table viewer
     * @param columnIndex
     *      the column index
     * @return
     *      a header listener for the given column
     */
    private static SelectionListener getHeaderListener( final TableViewer tableViewer, final int columnIndex )
    {
        return new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                if ( tableViewer == null )
                {
                    return;
                }

                TableColumn column = ( TableColumn ) e.widget;
                resortTable( tableViewer, column, columnIndex );
            }
        };
    }


    /**
     * Resorts the table based on column.
     * 
     * @param tableViewer
     *      the table viewer
     * @param tableColumn 
     *      the table column being resorted
     * @param columnIndex
     *      the column index
     */
    protected static void resortTable( final TableViewer tableViewer, final TableColumn tableColumn, int columnIndex )
    {
        // Getting the sorter
        ColumnsTableViewerComparator sorter = ( ColumnsTableViewerComparator ) tableViewer.getComparator();

        // Checking if sorting needs to be reversed or set to another columns
        if ( columnIndex == sorter.getColumn() )
        {
            sorter.reverseOrder();
        }
        else
        {
            sorter.setColumn( columnIndex );
        }

        // Refreshing the table and updating the direction indicator asynchronously
        PlatformUI.getWorkbench().getDisplay().asyncExec( new Runnable()
        {
            public void run()
            {
                tableViewer.refresh();
                updateDirectionIndicator( tableViewer, tableColumn );
            }
        } );
    }


    /**
     * Updates the direction indicator as column is now the primary column.
     *
     * @param tableViewer
     *      the table viewer
     * @param tableColumn
     *      the table column
     */
    protected static void updateDirectionIndicator( TableViewer tableViewer, TableColumn tableColumn )
    {
        tableViewer.getTable().setSortColumn( tableColumn );
        if ( ( ( ColumnsTableViewerComparator ) tableViewer.getComparator() ).getOrder() == ColumnsTableViewerComparator.ASCENDING )
        {
            tableViewer.getTable().setSortDirection( SWT.UP );
        }
        else
        {
            tableViewer.getTable().setSortDirection( SWT.DOWN );
        }
    }


    /**
     * Adds a sort column to the tree viewer.
     *
     * @param treeViewer
     *      the tree viewer
     * @param treeColumn
     *      the tree column
     */
    public static void addSortColumn( TreeViewer treeViewer, TreeColumn treeColumn )
    {
        if ( treeColumn == null )
        {
            return;
        }

        Tree tree = treeViewer.getTree();
        if ( tree == null )
        {
            return;
        }

        // Looking for the column index of the ttreeable column
        for ( int columnIndex = 0; columnIndex < tree.getColumnCount(); columnIndex++ )
        {
            if ( treeColumn.equals( tree.getColumn( columnIndex ) ) )
            {
                treeColumn.addSelectionListener( getHeaderListener( treeViewer, columnIndex ) );
            }
        }
    }


    /**
     * Gets a header listener for the given column.
     * 
     * @param treeViewer
     *      the tree viewer
     * @param columnIndex
     *      the column index
     * @return
     *      a header listener for the given column
     */
    private static SelectionListener getHeaderListener( final TreeViewer treeViewer, final int columnIndex )
    {
        return new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                if ( treeViewer == null )
                {
                    return;
                }

                TreeColumn column = ( TreeColumn ) e.widget;
                resortTree( treeViewer, column, columnIndex );
            }
        };
    }


    /**
     * Resorts the tree based on column.
     * 
     * @param treeViewer
     *      the tree viewer
     * @param treeColumn 
     *      the tree column being resorted
     * @param columnIndex
     *      the column index
     */
    protected static void resortTree( final TreeViewer treeViewer, final TreeColumn treeColumn, int columnIndex )
    {
        // Getting the sorter
        ColumnsTableViewerComparator sorter = ( ColumnsTableViewerComparator ) treeViewer.getComparator();

        // Checking if sorting needs to be reversed or set to another columns
        if ( columnIndex == sorter.getColumn() )
        {
            sorter.reverseOrder();
        }
        else
        {
            sorter.setColumn( columnIndex );
        }

        // Refreshing the tree and updating the direction indicator asynchronously
        PlatformUI.getWorkbench().getDisplay().asyncExec( new Runnable()
        {
            public void run()
            {
                treeViewer.refresh();
                updateDirectionIndicator( treeViewer, treeColumn );
            }
        } );
    }


    /**
     * Updates the direction indicator as column is now the primary column.
     *
     * @param treeViewer
     *      the tree viewer
     * @param treeColumn
     *      the tree column
     */
    protected static void updateDirectionIndicator( TreeViewer treeViewer, TreeColumn treeColumn )
    {
        treeViewer.getTree().setSortColumn( treeColumn );
        if ( ( ( ColumnsTableViewerComparator ) treeViewer.getComparator() ).getColumn() == ColumnsTableViewerComparator.ASCENDING )
        {
            treeViewer.getTree().setSortDirection( SWT.UP );
        }
        else
        {
            treeViewer.getTree().setSortDirection( SWT.DOWN );
        }
    }
}
