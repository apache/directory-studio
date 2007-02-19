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


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.directory.ldapstudio.schemas.model.AttributeType;
import org.apache.directory.ldapstudio.schemas.model.ObjectClass;
import org.apache.directory.ldapstudio.schemas.model.Schema;
import org.apache.directory.ldapstudio.schemas.model.SchemaPool;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.AttributeTypeWrapper;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.DisplayableTreeElement;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.IntermediateNode;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ObjectClassWrapper;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.SchemaWrapper;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.SchemasViewSorter;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.IntermediateNode.IntermediateNodeType;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;


/**
 * This class implements the Content Provider for the Schemas View.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class PoolManagerContentProvider implements IStructuredContentProvider, ITreeContentProvider
{
    /** The Schema Pool */
    private SchemaPool pool;


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
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public Object[] getChildren( Object parentElement )
    {
        List<DisplayableTreeElement> children = new ArrayList<DisplayableTreeElement>();

        if ( parentElement instanceof IntermediateNode )
        {
            IntermediateNode intermediate = ( IntermediateNode ) parentElement;
            if ( intermediate.getName().equals( "**Primary Node**" ) ) //$NON-NLS-1$
            {
                Schema[] schemas = this.pool.getSchemas();
                for ( int i = 0; i < schemas.length; i++ )
                {
                    children.add( new SchemaWrapper( schemas[i], ( IntermediateNode ) parentElement ) );
                }
            }
            else if ( intermediate.getType().equals( IntermediateNodeType.ATTRIBUTE_TYPE_FOLDER ) )
            {
                Schema schema = ( ( SchemaWrapper ) intermediate.getParent() ).getMySchema();

                AttributeType[] attributeTypeList = schema.getAttributeTypesAsArray();
                for ( int i = 0; i < attributeTypeList.length; i++ )
                {
                    children.add( new AttributeTypeWrapper( attributeTypeList[i], intermediate ) );
                }
            }
            else if ( intermediate.getType().equals( IntermediateNodeType.OBJECT_CLASS_FOLDER ) )
            {
                Schema schema = ( ( SchemaWrapper ) intermediate.getParent() ).getMySchema();

                ObjectClass[] objectClassList = schema.getObjectClassesAsArray();
                for ( int i = 0; i < objectClassList.length; i++ )
                {
                    children.add( new ObjectClassWrapper( objectClassList[i], intermediate ) );
                }
            }
        }
        else if ( parentElement instanceof SchemaWrapper )
        {
            IntermediateNode attributeTypes = new IntermediateNode(
                "Attribute Types", ( SchemaWrapper ) parentElement, IntermediateNodeType.ATTRIBUTE_TYPE_FOLDER ); //$NON-NLS-1$
            IntermediateNode objectClasses = new IntermediateNode(
                "Object Classes", ( SchemaWrapper ) parentElement, IntermediateNodeType.OBJECT_CLASS_FOLDER ); //$NON-NLS-1$

            children.add( attributeTypes );
            children.add( objectClasses );
        }

        // Sorting children
        Collections.sort( children, new SchemasViewSorter() );

        return children.toArray();
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent( Object element )
    {
        if ( element instanceof DisplayableTreeElement )
        {
            return ( ( DisplayableTreeElement ) element ).getParent();
        }

        return null;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren( Object element )
    {
        if ( element instanceof DisplayableTreeElement )
        {
            return getChildren( element ).length > 0;
        }

        return false;
    }


    /**
     * Initialize a tree viewer to display the information provided by the specified content
     * provider
     * @param viewer the tree viewer
     */
    public void bindToTreeViewer( TreeViewer viewer )
    {
        viewer.setContentProvider( this );
        viewer.setLabelProvider( new PoolManagerLabelProvider() );

        IntermediateNode invisibleNode = new IntermediateNode( "**Primary Node**", null ); //$NON-NLS-1$
        viewer.setInput( invisibleNode );
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
}
