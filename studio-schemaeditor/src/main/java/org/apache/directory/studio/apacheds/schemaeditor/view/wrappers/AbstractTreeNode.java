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
package org.apache.directory.studio.apacheds.schemaeditor.view.wrappers;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * This abstract class implements the TreeNode Interface.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public abstract class AbstractTreeNode implements TreeNode
{
    /** The children */
    protected List<TreeNode> fChildren;

    /** The parent */
    protected TreeNode fParent;


    /**
     * Creates a new instance of AbstractTreeNode.
     *
     * @param parent
     *      the parent element
     */
    public AbstractTreeNode( TreeNode parent )
    {
        fParent = parent;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemas.view.viewers.wrappers.ITreeNode#hasChildren()
     */
    public boolean hasChildren()
    {
        if ( fChildren == null )
        {
            return false;
        }

        return !fChildren.isEmpty();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemas.view.viewers.wrappers.ITreeNode#getParent()
     */
    public TreeNode getParent()
    {
        return fParent;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemas.view.viewers.wrappers.ITreeNode#setParent(org.apache.directory.studio.schemas.view.viewers.wrappers.ITreeNode)
     */
    public void setParent( TreeNode parent )
    {
        fParent = parent;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemas.view.viewers.wrappers.ITreeNode#getChildren()
     */
    public List<TreeNode> getChildren()
    {
        if ( fChildren == null )
        {
            fChildren = new ArrayList<TreeNode>();
        }

        return fChildren;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemas.view.viewers.wrappers.ITreeNode#addChild(org.apache.directory.studio.schemas.view.viewers.wrappers.ITreeNode)
     */
    public void addChild( TreeNode node )
    {
        if ( fChildren == null )
        {
            fChildren = new ArrayList<TreeNode>();
        }

        if ( !fChildren.contains( node ) )
        {
            fChildren.add( node );
        }
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemas.view.viewers.wrappers.ITreeNode#removeChild(org.apache.directory.studio.schemas.view.viewers.wrappers.ITreeNode)
     */
    public void removeChild( TreeNode node )
    {
        if ( fChildren != null )
        {
            fChildren.remove( node );
        }
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemas.view.viewers.wrappers.ITreeNode#addAllChildren(java.util.Collection)
     */
    public boolean addAllChildren( Collection<? extends TreeNode> c )
    {
        if ( fChildren == null )
        {
            fChildren = new ArrayList<TreeNode>();
        }

        return fChildren.addAll( c );
    }
}
