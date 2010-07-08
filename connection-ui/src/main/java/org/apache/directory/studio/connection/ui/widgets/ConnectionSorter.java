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

package org.apache.directory.studio.connection.ui.widgets;


import org.apache.directory.studio.connection.core.ConnectionFolder;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;


/**
 * The ConnectionSorter implements the sorter for the connection widget. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ConnectionSorter extends ViewerSorter
{

    /**
     * Creates a new instance of ConnectionSorter.
     */
    public ConnectionSorter()
    {
    }


    /**
     * Connects the tree viewer to this sorter.
     *
     * @param viewer the tree viewer
     */
    public void connect( TreeViewer viewer )
    {
        viewer.setSorter( this );
    }


    /**
     * Disposes this sorter.
     */
    public void dispose()
    {
    }


    /**
     * {@inheritDoc}
     * 
     * This method is used to categorize connection folders and connections.
     */
    public int category( Object element )
    {
        if ( element instanceof ConnectionFolder )
        {
            return 1;
        }
        else
        {
            return 2;
        }
    }

}
