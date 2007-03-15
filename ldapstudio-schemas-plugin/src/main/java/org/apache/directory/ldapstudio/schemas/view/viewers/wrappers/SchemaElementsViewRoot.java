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


/**
 * This class is used to represent the root entry of the TreeViewer used in the Schema Elements View.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SchemaElementsViewRoot extends TreeNode
{
    /** The AT children */
    private List<AttributeTypeWrapper> aTChildren;

    /** The OC children */
    private List<ObjectClassWrapper> ocChildren;


    /**
     * Creates a new instance of SchemasViewRoot.
     *
     */
    public SchemaElementsViewRoot()
    {
        super( null );
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "SchemaElementsViewRoot";
    }


    /**
     * Adds an Attribute Type Wrapper to the list of children.
     *
     * @param atw
     *      the Attribute Type Wrapper to add
     */
    public void addAttributeType( AttributeTypeWrapper atw )
    {
        if ( aTChildren == null )
        {
            aTChildren = new ArrayList<AttributeTypeWrapper>();
        }

        if ( !aTChildren.contains( atw ) )
        {
            aTChildren.add( atw );
        }
    }


    /**
     * Adds an Object Class Wrapper to the list of children.
     *
     * @param ocw
     *      the Object Class Wrapper to add
     */
    public void addObjectClass( ObjectClassWrapper ocw )
    {
        if ( ocChildren == null )
        {
            ocChildren = new ArrayList<ObjectClassWrapper>();
        }

        if ( !ocChildren.contains( ocw ) )
        {
            ocChildren.add( ocw );
        }
    }


    /**
     * Gets the children Attribute Type Wrappers.
     *
     * @return
     *      the children Attribute Type Wrappers
     */
    public List<AttributeTypeWrapper> getAttributeTypes()
    {
        if ( aTChildren == null )
        {
            aTChildren = new ArrayList<AttributeTypeWrapper>();
        }

        return aTChildren;
    }


    /**
     * Gets the children Object Class Wrappers.
     *
     * @return
     *      the children Object Class Wrappers
     */
    public List<ObjectClassWrapper> getObjectClasses()
    {
        if ( ocChildren == null )
        {
            ocChildren = new ArrayList<ObjectClassWrapper>();
        }

        return ocChildren;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.TreeNode#addChild(org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ITreeNode)
     */
    public void addChild( ITreeNode node )
    {
        if ( node instanceof AttributeTypeWrapper )
        {
            addAttributeType( ( AttributeTypeWrapper ) node );
        }
        else if ( node instanceof ObjectClassWrapper )
        {
            addObjectClass( ( ObjectClassWrapper ) node );
        }
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.TreeNode#addAllChildren(java.util.Collection)
     */
    public boolean addAllChildren( Collection<? extends ITreeNode> c )
    {
        for ( ITreeNode child : c )
        {
            addChild( child );
        }
        return true;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.TreeNode#removeChild(org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ITreeNode)
     */
    public void removeChild( ITreeNode node )
    {
        if ( node instanceof AttributeTypeWrapper )
        {
            removeAttributeType( ( AttributeTypeWrapper ) node );
        }
        else if ( node instanceof ObjectClassWrapper )
        {
            removeObjectClass( ( ObjectClassWrapper ) node );
        }
    }


    /**
     * Removes an Attribute Type Wrapper.
     *
     * @param wrapper
     *      the Attribute Type Wrapper to remove
     */
    private void removeAttributeType( AttributeTypeWrapper wrapper )
    {
        if ( aTChildren != null )
        {
            aTChildren.remove( wrapper );
        }
    }


    /**
     * Removes an Object Class Wrapper.
     *
     * @param wrapper
     *      the Object Class Wrapper to remove
     */
    private void removeObjectClass( ObjectClassWrapper wrapper )
    {
        if ( ocChildren != null )
        {
            ocChildren.remove( wrapper );
        }
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.TreeNode#getChildren()
     */
    public List<ITreeNode> getChildren()
    {
        List<ITreeNode> children = new ArrayList<ITreeNode>();
        children.addAll( getAttributeTypes() );
        children.addAll( getObjectClasses() );
        return children;
    }
}
