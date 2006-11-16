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

package org.apache.directory.ldapstudio.schemas.view.viewers;


import java.util.Comparator;

import org.eclipse.jface.viewers.TreeViewer;


/**
 * Common interface for sortable content providers
 *
 */
public interface SortableContentProvider
{
    /**
     * Specify the comparator that will be used to sort the elements in the view
     * @param order the comparator
     */
    public void setOrder( Comparator order );


    /**
     * Returns the comparator used to sort the elements in the view
     * @return
     */
    public Comparator getOrder();


    /**
     * Initialize a tree viewer to display the information provided by the specified content
     * provider
     * @param viewer the tree viewer
     */
    public void bindToTreeViewer( TreeViewer viewer );
}
