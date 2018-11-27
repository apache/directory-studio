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
package org.apache.directory.studio.templateeditor.view;


import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;


/**
 * This class implements the comparator for a table viewer with columns.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ColumnsTableViewerComparator extends ViewerComparator
{
    public static final int ASCENDING = 1;

    /** The associated table label provider */
    protected ITableLabelProvider labelProvider;

    /** The comparison order */
    int order = ASCENDING;

    /** The column used to compare objects */
    int column = 0;


    /**
     * Creates a new instance of ColumnsTableViewerComparator.
     *
     * @param labelProvider
     *      the label provider
     */
    public ColumnsTableViewerComparator( ITableLabelProvider labelProvider )
    {
        this.labelProvider = labelProvider;
    }


    /**
     * {@inheritDoc}
     */
    public int compare( Viewer viewer, Object e1, Object e2 )
    {
        String s1 = labelProvider.getColumnText( e1, column );
        String s2 = labelProvider.getColumnText( e2, column );

        return s1.compareToIgnoreCase( s2 ) * order;
    }


    /**
     * Gets the column.
     *
     * @return the column
     */
    public int getColumn()
    {
        return column;
    }


    /**
     * Gets the order.
     *
     * @return the order
     */
    public int getOrder()
    {
        return order;
    }


    /**
     * Reverses the order.
     */
    public void reverseOrder()
    {
        order = order * -1;
    }


    /**
     * Sets the column.
     *
     * @param column the column
     */
    public void setColumn( int column )
    {
        this.column = column;
    }


    /**
     * Sets the order.
     *
     * @param order the order
     */
    public void setOrder( int order )
    {
        this.order = order;
    }
}