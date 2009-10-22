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
package org.apache.directory.studio.schemaeditor.model.hierarchy;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * This class represents the HierarchyWrapper.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class HierarchyWrapper
{
    /** The wrapped object */
    private Object wrappedObject;

    /** The parent */
    private Object parent;

    /** The children */
    private List<HierarchyWrapper> children = new ArrayList<HierarchyWrapper>();


    /**
     * Creates a new instance of HierarchyWrapper.
     *
     * @param obj
     *      the wrapped object
     */
    public HierarchyWrapper( Object obj )
    {
        wrappedObject = obj;
        parent = null;
    }


    /**
     * Creates a new instance of HierarchyWrapper.
     *
     * @param obj
     *      the wrapped object
     * @param parent
     *      the parent
     */
    public HierarchyWrapper( Object obj, Object parent )
    {
        wrappedObject = obj;
        this.parent = parent;
    }


    /**
     * Adds a child.
     *
     * @param child
     *      the child
     * @return
     *      true (as per the general contract of the Collection.add method).
     */
    public boolean addChild( HierarchyWrapper child )
    {
        return children.add( child );
    }


    /**
     * Appends all of the elements in the specified collection to the end 
     * of the children, in the order that they are returned by the specified 
     * collection's iterator (optional operation). 
     *
     * @param collection
     *      the collection
     * @return
     *      true if the children changed as a result of the call.
     */
    public boolean addChildren( Collection<? extends HierarchyWrapper> children )
    {
        return this.children.addAll( children );
    }


    /**
     * Indicates whether some other object is "equal to" this wrapped object.
     *
     * @param o
     *      the reference object with which to compare.
     * @return
     *      true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equalsWrappedObject( Object obj )
    {
        if ( obj != null )
        {
            return obj.equals( wrappedObject );
        }

        return false;
    }


    /**
     * Gets the children.
     *
     * @return
     *      the children
     */
    public List<HierarchyWrapper> getChildren()
    {
        return children;
    }


    /**
     * Gets the parent.
     *
     * @return
     *      the parent
     */
    public Object getParent()
    {
        return parent;
    }


    /**
     * Gets the wrapped object.
     *
     * @return
     *      the wrapped object
     */
    public Object getWrappedObject()
    {
        return wrappedObject;
    }


    /**
     * Removes a child.
     *
     * @param child
     *      the child
     * @return
     *      true if the children contained the specified element.
     */
    public boolean removeChild( HierarchyWrapper child )
    {
        return children.remove( child );
    }


    /**
     * Removes from the children all the elements that are contained in 
     * the specified collection (optional operation).
     *
     * @param collection
     *      the collection
     * @return
     *      true if the children changed as a result of the call.
     */
    public boolean removeChildren( Collection<? extends HierarchyWrapper> children )
    {
        return this.children.removeAll( children );
    }


    /**
     * Sets the children.
     *
     * @param children
     *      the children
     */
    public void setChildren( List<HierarchyWrapper> children )
    {
        this.children = children;
    }


    /**
     * Sets the parent.
     *
     * @param parent
     *      the parent
     */
    public void setParent( Object parent )
    {
        this.parent = parent;
    }


    /**
     * Sets the wrapped object.
     *
     * @param wrappedObject
     *      the wrapped object
     */
    public void setWrappedObject( Object wrappedObject )
    {
        this.wrappedObject = wrappedObject;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "{|" + wrappedObject + "|" + children + "}"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
}
