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

import org.apache.directory.ldapstudio.schemas.model.AttributeType;
import org.apache.directory.ldapstudio.schemas.model.ObjectClass;
import org.apache.directory.ldapstudio.schemas.model.Schema;
import org.apache.directory.ldapstudio.schemas.model.SchemaPool;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.AlphabeticalOrderComparator;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.IntermediateNode;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.PoolManagerAttributeTypeWrapper;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.PoolManagerObjectClassWrapper;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.SchemaWrapper;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.IntermediateNode.IntermediateNodeType;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;


/**
 * Content provider for the schema-pool manager
 *
 */
public class PoolManagerContentProvider implements SortableContentProvider, IStructuredContentProvider,
    ITreeContentProvider
{

    private SchemaPool pool;
    private Comparator order = new AlphabeticalOrderComparator();


    /**
     * Default constructor
     */
    public PoolManagerContentProvider()
    {
        pool = SchemaPool.getInstance();
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements( Object inputElement )
    {
        return getChildren( inputElement );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose()
    {
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
    {
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public Object[] getChildren( Object parentElement )
    {
        if ( parentElement instanceof IntermediateNode )
        {
            IntermediateNode intermediate = ( IntermediateNode ) parentElement;

            if ( intermediate.getName().equals( "**Primary Node**" ) ) { //$NON-NLS-1$
                // clear the primary node (because it's always the same instance we need to
                //refresh it manually)
                intermediate.clearChildrens();

                Schema[] schemas = this.pool.getSchemas();
                for ( int i = 0; i < schemas.length; i++ )
                {
                    Schema schema = schemas[i];
                    SchemaWrapper schemaWrapper = new SchemaWrapper( schema, ( IntermediateNode ) parentElement );
                    intermediate.addElement( schemaWrapper );
                }
            }

            Object[] temp = intermediate.getChildren();
            return temp;
        }

        else if ( parentElement instanceof SchemaWrapper )
        {
            //we are looking for the childrens of the contained objectClass
            Schema schema = ( ( ( SchemaWrapper ) parentElement ).getMySchema() );

            IntermediateNode attributeTypes = new IntermediateNode(
                "Attribute Types", ( SchemaWrapper ) parentElement, this, IntermediateNodeType.ATTRIBUTE_TYPE_FOLDER ); //$NON-NLS-1$
            IntermediateNode objectClasses = new IntermediateNode(
                "Object Classes", ( SchemaWrapper ) parentElement, this, IntermediateNodeType.OBJECT_CLASS_FOLDER ); //$NON-NLS-1$

            // Let's get all Attribute Types defined in the schema
            AttributeType[] attributeTypeList = schema.getAttributeTypesAsArray();
            for ( int i = 0; i < attributeTypeList.length; i++ )
            {
                AttributeType attributeType = attributeTypeList[i];
                attributeTypes.addElement( new PoolManagerAttributeTypeWrapper( attributeType, attributeTypes ) );
            }

            // Let's get all Object Classes defined in the schema
            ObjectClass[] objectClassList = schema.getObjectClassesAsArray();
            for ( int i = 0; i < objectClassList.length; i++ )
            {
                ObjectClass objectClass = objectClassList[i];
                objectClasses.addElement( new PoolManagerObjectClassWrapper( objectClass, objectClasses ) );
            }

            return new Object[]
                { attributeTypes, objectClasses };
        }

        return new Object[0];
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent( Object element )
    {
        if ( element instanceof SchemaWrapper )
        {
            return ( ( SchemaWrapper ) element ).getParent();
        }
        else if ( element instanceof IntermediateNode )
        {
            return ( ( IntermediateNode ) element ).getParent();
        }
        return null;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren( Object element )
    {
        if ( element instanceof IntermediateNode )
        {
            if ( ( ( IntermediateNode ) element ).getChildren().length > 0 )
                return true;
        }
        else if ( element instanceof SchemaWrapper )
        {
            return true;
        }
        return false;
    }


    /******************************************
     *                 Logic                  *
     ******************************************/

    /**
     * Specify the comparator that will be used to sort the elements in the view
     * @param order the comparator
     */
    public void setOrder( Comparator order )
    {
        this.order = order;
    }


    /**
     * Returns the comparator used to sort the elements in the view
     * @return
     */
    public Comparator getOrder()
    {
        return order;
    }


    /**
     * Initialize a tree viewer to display the information provided by the specified content
     * provider
     * @param viewer the tree viewer
     */
    public void bindToTreeViewer( TreeViewer viewer )
    {
        viewer.setContentProvider( this );
        viewer.setLabelProvider( new HierarchicalLabelProvider() );

        IntermediateNode invisibleNode = new IntermediateNode( "**Primary Node**", null, this ); //$NON-NLS-1$
        viewer.setInput( invisibleNode );
    }
}
