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

import org.apache.directory.ldapstudio.schemas.Activator;
import org.apache.directory.ldapstudio.schemas.PluginConstants;
import org.apache.directory.ldapstudio.schemas.model.AttributeType;
import org.apache.directory.ldapstudio.schemas.model.ObjectClass;
import org.apache.directory.ldapstudio.schemas.model.Schema;
import org.apache.directory.ldapstudio.schemas.model.SchemaPool;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.AttributeTypeWrapper;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.DisplayableTreeElement;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.FirstNameSorter;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.IntermediateNode;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ObjectClassWrapper;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.OidSorter;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.SchemaSorter;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.SchemaWrapper;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.IntermediateNode.IntermediateNodeType;
import org.eclipse.jface.preference.IPreferenceStore;
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
public class SchemasViewContentProvider implements IStructuredContentProvider, ITreeContentProvider
{
    /** The Schema Pool */
    private SchemaPool schemaPool;

    /** The preferences store */
    IPreferenceStore store;

    /** The FirstName Sorter */
    private FirstNameSorter firstNameSorter;

    /** The OID Sorter */
    private OidSorter oidSorter;
    
    /** The Schema Sorter */
    private SchemaSorter schemaSorter;


    /**
     * Default constructor
     */
    public SchemasViewContentProvider()
    {
        schemaPool = SchemaPool.getInstance();
        store = Activator.getDefault().getPreferenceStore();

        firstNameSorter = new FirstNameSorter();
        oidSorter = new OidSorter();
        schemaSorter = new SchemaSorter();
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

        int group = store.getInt( PluginConstants.PREFS_SCHEMAS_VIEW_GROUPING );
        int sortBy = store.getInt( PluginConstants.PREFS_SCHEMAS_VIEW_SORTING_BY );
        int sortOrder = store.getInt( PluginConstants.PREFS_SCHEMAS_VIEW_SORTING_ORDER );

        if ( parentElement instanceof IntermediateNode )
        {
            IntermediateNode intermediate = ( IntermediateNode ) parentElement;
            if ( intermediate.getName().equals( "**Primary Node**" ) ) //$NON-NLS-1$
            {
                Schema[] schemas = this.schemaPool.getSchemas();
                for ( int i = 0; i < schemas.length; i++ )
                {
                    children.add( new SchemaWrapper( schemas[i], ( IntermediateNode ) parentElement ) );
                }
                
                Collections.sort( children, schemaSorter );
            }
            else if ( intermediate.getType().equals( IntermediateNodeType.ATTRIBUTE_TYPE_FOLDER ) )
            {
                Schema schema = ( ( SchemaWrapper ) intermediate.getParent() ).getMySchema();

                AttributeType[] attributeTypeList = schema.getAttributeTypesAsArray();
                for ( int i = 0; i < attributeTypeList.length; i++ )
                {
                    children.add( new AttributeTypeWrapper( attributeTypeList[i], intermediate ) );
                }

                // Sort by
                if ( sortBy == PluginConstants.PREFS_SCHEMAS_VIEW_SORTING_BY_FIRSTNAME )
                {
                    Collections.sort( children, firstNameSorter );
                }
                else if ( sortBy == PluginConstants.PREFS_SCHEMAS_VIEW_SORTING_BY_OID )
                {
                    Collections.sort( children, oidSorter );
                }

                // Sort order
                if ( sortOrder == PluginConstants.PREFS_SCHEMAS_VIEW_SORTING_ORDER_DESCENDING )
                {
                    Collections.reverse( children );
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

                // Sort by
                if ( sortBy == PluginConstants.PREFS_SCHEMAS_VIEW_SORTING_BY_FIRSTNAME )
                {
                    Collections.sort( children, firstNameSorter );
                }
                else if ( sortBy == PluginConstants.PREFS_SCHEMAS_VIEW_SORTING_BY_OID )
                {
                    Collections.sort( children, oidSorter );
                }

                // Sort order
                if ( sortOrder == PluginConstants.PREFS_SCHEMAS_VIEW_SORTING_ORDER_DESCENDING )
                {
                    Collections.reverse( children );
                }
            }
        }
        else if ( parentElement instanceof SchemaWrapper )
        {
            if ( group == PluginConstants.PREFS_SCHEMAS_VIEW_GROUPING_FOLDERS )
            {
                IntermediateNode attributeTypes = new IntermediateNode(
                    "Attribute Types", ( SchemaWrapper ) parentElement, IntermediateNodeType.ATTRIBUTE_TYPE_FOLDER ); //$NON-NLS-1$
                IntermediateNode objectClasses = new IntermediateNode(
                    "Object Classes", ( SchemaWrapper ) parentElement, IntermediateNodeType.OBJECT_CLASS_FOLDER ); //$NON-NLS-1$

                children.add( attributeTypes );
                children.add( objectClasses );
            }
            else if ( group == PluginConstants.PREFS_SCHEMAS_VIEW_GROUPING_MIXED )
            {
                Schema schema = ( ( SchemaWrapper ) parentElement ).getMySchema();

                AttributeType[] attributeTypeList = schema.getAttributeTypesAsArray();
                for ( int i = 0; i < attributeTypeList.length; i++ )
                {
                    children.add( new AttributeTypeWrapper( attributeTypeList[i], ( SchemaWrapper ) parentElement ) );
                }

                ObjectClass[] objectClassList = schema.getObjectClassesAsArray();
                for ( int i = 0; i < objectClassList.length; i++ )
                {
                    children.add( new ObjectClassWrapper( objectClassList[i], ( SchemaWrapper ) parentElement ) );
                }

                // Sort by
                if ( sortBy == PluginConstants.PREFS_SCHEMAS_VIEW_SORTING_BY_FIRSTNAME )
                {
                    Collections.sort( children, firstNameSorter );
                }
                else if ( sortBy == PluginConstants.PREFS_SCHEMAS_VIEW_SORTING_BY_OID )
                {
                    Collections.sort( children, oidSorter );
                }

                // Sort order
                if ( sortOrder == PluginConstants.PREFS_SCHEMAS_VIEW_SORTING_ORDER_DESCENDING )
                {
                    Collections.reverse( children );
                }
            }
        }

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
        viewer.setLabelProvider( new SchemasViewLabelProvider() );

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
