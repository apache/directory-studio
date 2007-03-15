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
package org.apache.directory.ldapstudio.schemas.view.viewers.wrappers;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.graphics.Image;


/**
 * This abstract class implements the ITreeNode Interface.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public abstract class TreeNode implements ITreeNode
{
    /** The children */
    protected List<ITreeNode> fChildren;

    /** The parent */
    protected ITreeNode fParent;


    /**
     * Creates a new instance of TreeNode.
     *
     * @param parent
     *      the parent element
     */
    public TreeNode( ITreeNode parent )
    {
        fParent = parent;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ITreeNode#getImage()
     */
    public Image getImage()
    {
        return null;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ITreeNode#hasChildren()
     */
    public boolean hasChildren()
    {
        if ( fChildren == null )
        {
            return true;
        }

        return !fChildren.isEmpty();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ITreeNode#getParent()
     */
    public ITreeNode getParent()
    {
        return fParent;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ITreeNode#setParent(org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ITreeNode)
     */
    public void setParent( ITreeNode parent )
    {
        fParent = parent;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ITreeNode#getChildren()
     */
    public List<ITreeNode> getChildren()
    {
        if ( fChildren == null )
        {
            fChildren = new ArrayList<ITreeNode>();
        }

        return fChildren;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ITreeNode#addChild(org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ITreeNode)
     */
    public void addChild( ITreeNode node )
    {
        if ( fChildren == null )
        {
            fChildren = new ArrayList<ITreeNode>();
        }

        if ( !fChildren.contains( node ) )
        {
            fChildren.add( node );
        }
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ITreeNode#removeChild(org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ITreeNode)
     */
    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ITreeNode#removeChild(org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ITreeNode)
     */
    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ITreeNode#removeChild(org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ITreeNode)
     */
    public void removeChild( ITreeNode node )
    {
        if ( fChildren != null )
        {
            fChildren.remove( node );
        }
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ITreeNode#addAllChildren(java.util.Collection)
     */
    public boolean addAllChildren( Collection<? extends ITreeNode> c )
    {
        if ( fChildren == null )
        {
            fChildren = new ArrayList<ITreeNode>();
        }

        return fChildren.addAll( c );
    }
}
