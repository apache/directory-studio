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
package org.apache.directory.studio.schemaeditor.view.wrappers;


import java.util.Collection;
import java.util.List;


/**
 * This interface defines an element that can be used in a TreeViewer.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface TreeNode
{
    /**
     * Gets the children of the element.
     *
     * @return
     *      the children of the element
     */
    public List<TreeNode> getChildren();


    /**
     * Returns true if the element has children.
     *
     * @return
     *      true if the element has children
     */
    public boolean hasChildren();


    /**
     * Gets the parent of the element.
     *
     * @return
     *      the parent of the element
     */
    public TreeNode getParent();


    /**
     * Sets the parent of the element.
     *
     * @param node
     *      the parent of the element
     */
    public void setParent( TreeNode parent );


    /**
     * Adds a node to the element.
     *
     * @param node
     *      the node to add
     */
    public void addChild( TreeNode node );


    /**
     * Removes a node from the element.
     *
     * @param node
     *      the node to remove 
     */
    public void removeChild( TreeNode node );


    /**
     *  Appends all of the elements in the specified collection to the end of this list, in the order that they are returned by the specified collection's iterator (optional operation). The behavior of this operation is unspecified if the specified collection is modified while the operation is in progress. (Note that this will occur if the specified collection is this list, and it's nonempty.)
     * 
     * @param c
     *      the collection whose elements are to be added to this list.
     * @return
     *      true if this list changed as a result of the call.
     *      
     * @see java.util.List.addAll(Collection c)
     */
    public boolean addAllChildren( Collection<? extends TreeNode> c );
}
