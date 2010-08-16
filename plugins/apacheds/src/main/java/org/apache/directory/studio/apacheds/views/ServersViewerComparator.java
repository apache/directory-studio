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
/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.apache.directory.studio.apacheds.views;


import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;


/**
 * This class implements the servers table viewer comparator.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ServersViewerComparator extends ViewerComparator
{
    public static final int MAX_DEPTH = 3;
    public static final int ASCENDING = 1;
    public static final int DESCENDING = -1;

    protected ServersViewLabelProvider labelProvider;

    protected int[] priorities = new int[]
        { 0 };

    protected int[] directions = new int[]
        { ASCENDING };


    /**
     * Creates a new instance of ServersViewerComparator.
     *
     * @param labelProvider
     *      the label provider
     */
    public ServersViewerComparator( ServersViewLabelProvider labelProvider )
    {
        this.labelProvider = labelProvider;
    }


    /**
     * Sets the top priority.
     *
     * @param priority
     *      the priority
     */
    public void setTopPriority( int priority )
    {
        if ( priorities[0] == priority )
        {
            return;
        }

        int len = priorities.length + 1;
        if ( len > MAX_DEPTH )
        {
            len = MAX_DEPTH;
        }

        int[] temp = new int[len];
        System.arraycopy( priorities, 0, temp, 1, len - 1 );
        temp[0] = priority;
        priorities = temp;

        temp = new int[len];
        System.arraycopy( directions, 0, temp, 1, len - 1 );
        temp[0] = ASCENDING;
        directions = temp;
    }


    /**
     * Gets the top priority.
     *
     * @return
     *      the top priority
     */
    public int getTopPriority()
    {
        return priorities[0];
    }


    /**
     * Sets the top priority direction
     *
     * @param direction
     *      the direction
     */
    public void setTopPriorityDirection( int direction )
    {
        if ( direction == ASCENDING || direction == DESCENDING )
        {
            directions[0] = direction;
        }
    }


    /**
     * Gets the top priority direction
     *
     * @return
     *      the top priority direction
     */
    public int getTopPriorityDirection()
    {
        return directions[0];
    }


    /**
     * Reverses the top priority
     */
    public void reverseTopPriority()
    {
        directions[0] *= -1;
    }


    /**
     * Returns a negative, zero, or positive number depending on whether
     * the first element is less than, equal to, or greater than
     * the second element.
     * <p>
     * The default implementation of this method is based on
     * comparing the elements' categories as computed by the <code>category</code>
     * framework method. Elements within the same category are further 
     * subjected to a case insensitive compare of their label strings, either
     * as computed by the content viewer's label provider, or their 
     * <code>toString</code> values in other cases. Subclasses may override.
     * </p>
     * 
     * @param viewer the viewer
     * @param e1 the first element
     * @param e2 the second element
     * @param a the direction
     * @return a negative number if the first element is less  than the 
     *  second element; the value <code>0</code> if the first element is
     *  equal to the second element; and a positive number if the first
     *  element is greater than the second element
     */
    public int compare( Viewer viewer, Object e1, Object e2, int a )
    {
        int col = priorities[a];

        String s1 = labelProvider.getColumnText( e1, col );
        String s2 = labelProvider.getColumnText( e2, col );

        int s = s1.compareToIgnoreCase( s2 ) * directions[a];
        if ( s == 0 )
        {
            if ( a == priorities.length - 1 )
            {
                return 0;
            }
            return compare( viewer, e1, e2, a + 1 );
        }
        return s;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public int compare( Viewer viewer, Object e1, Object e2 )
    {
        return compare( viewer, e1, e2, 0 );
    }
}