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
package org.apache.directory.studio.apacheds.schemaeditor.view.views;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.PluginConstants;
import org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.Schema;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.AttributeTypeWrapper;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.FirstNameSorter;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.Folder;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.ObjectClassWrapper;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.OidSorter;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.SchemaSorter;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.SchemaViewRoot;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.SchemaWrapper;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.TreeNode;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.Folder.FolderType;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


/**
 * This class implements the ContentProvider for the SchemaView.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SchemaViewContentProvider implements IStructuredContentProvider, ITreeContentProvider
{
    /** The preferences store */
    private IPreferenceStore store;

    /** The FirstName Sorter */
    private FirstNameSorter firstNameSorter;

    /** The OID Sorter */
    private OidSorter oidSorter;

    /** The Schema Sorter */
    private SchemaSorter schemaSorter;


    /**
     * Creates a new instance of DifferencesWidgetSchemaContentProvider.
     */
    public SchemaViewContentProvider()
    {
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
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose()
    {
        // Nothing to do
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
    {
        // Nothing to do
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public Object[] getChildren( Object parentElement )
    {
        List<TreeNode> children = new ArrayList<TreeNode>();

        int group = store.getInt( PluginConstants.PREFS_SCHEMA_VIEW_GROUPING );
        int sortBy = store.getInt( PluginConstants.PREFS_SCHEMA_VIEW_SORTING_BY );
        int sortOrder = store.getInt( PluginConstants.PREFS_SCHEMA_VIEW_SORTING_ORDER );

        if ( parentElement instanceof SchemaViewRoot )
        {
            SchemaViewRoot root = ( SchemaViewRoot ) parentElement;

            if ( root.getChildren().isEmpty() )
            {
                SchemaHandler schemaHandler = Activator.getDefault().getSchemaHandler();
                if ( schemaHandler != null )
                {
                    List<Schema> schemas = schemaHandler.getSchemas();
                    for ( Schema schema : schemas )
                    {
                        SchemaWrapper schemaWrapper = new SchemaWrapper( schema, root );
                        root.addChild( schemaWrapper );

                        if ( group == PluginConstants.PREFS_SCHEMA_VIEW_GROUPING_FOLDERS )
                        {
                            Folder atFolder = new Folder( FolderType.ATTRIBUTE_TYPE, schemaWrapper );
                            schemaWrapper.addChild( atFolder );

                            for ( AttributeTypeImpl attributeType : schema.getAttributeTypes() )
                            {
                                atFolder.addChild( new AttributeTypeWrapper( attributeType, atFolder ) );
                            }

                            Folder ocFolder = new Folder( FolderType.OBJECT_CLASS, schemaWrapper );
                            schemaWrapper.addChild( ocFolder );

                            for ( ObjectClassImpl objectClass : schema.getObjectClasses() )
                            {
                                ocFolder.addChild( new ObjectClassWrapper( objectClass, ocFolder ) );
                            }
                        }
                        else if ( group == PluginConstants.PREFS_SCHEMA_VIEW_GROUPING_MIXED )
                        {
                            for ( AttributeTypeImpl attributeType : schema.getAttributeTypes() )
                            {
                                schemaWrapper.addChild( new AttributeTypeWrapper( attributeType, schemaWrapper ) );
                            }

                            for ( ObjectClassImpl objectClass : schema.getObjectClasses() )
                            {
                                schemaWrapper.addChild( new ObjectClassWrapper( objectClass, schemaWrapper ) );
                            }
                        }
                    }
                }
            }

            children = root.getChildren();

            Collections.sort( children, schemaSorter );
        }
        else if ( parentElement instanceof SchemaWrapper )
        {
            children = ( ( SchemaWrapper ) parentElement ).getChildren();

            if ( group == PluginConstants.PREFS_SCHEMA_VIEW_GROUPING_MIXED )
            {
                // Sort by
                if ( sortBy == PluginConstants.PREFS_SCHEMA_VIEW_SORTING_BY_FIRSTNAME )
                {
                    Collections.sort( children, firstNameSorter );
                }
                else if ( sortBy == PluginConstants.PREFS_SCHEMA_VIEW_SORTING_BY_OID )
                {
                    Collections.sort( children, oidSorter );
                }

                // Sort Order
                if ( sortOrder == PluginConstants.PREFS_SCHEMA_VIEW_SORTING_ORDER_DESCENDING )
                {
                    Collections.reverse( children );
                }
            }
        }
        else if ( parentElement instanceof Folder )
        {
            children = ( ( Folder ) parentElement ).getChildren();

            // Sort by
            if ( sortBy == PluginConstants.PREFS_SCHEMA_VIEW_SORTING_BY_FIRSTNAME )
            {
                Collections.sort( children, firstNameSorter );
            }
            else if ( sortBy == PluginConstants.PREFS_SCHEMA_VIEW_SORTING_BY_OID )
            {
                Collections.sort( children, oidSorter );
            }

            // Sort Order
            if ( sortOrder == PluginConstants.PREFS_SCHEMA_VIEW_SORTING_ORDER_DESCENDING )
            {
                Collections.reverse( children );
            }
        }

        return children.toArray();
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent( Object element )
    {

        if ( element instanceof TreeNode )
        {
            return ( ( TreeNode ) element ).getParent();
        }

        // Default
        return null;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren( Object element )
    {
        if ( element instanceof TreeNode )
        {
            return ( ( TreeNode ) element ).hasChildren();
        }

        // Default
        return false;
    }
}
