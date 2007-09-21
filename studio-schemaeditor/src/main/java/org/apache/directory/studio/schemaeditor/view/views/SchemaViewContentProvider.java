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
package org.apache.directory.studio.schemaeditor.view.views;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.apache.directory.studio.schemaeditor.model.hierarchy.HierarchyManager;
import org.apache.directory.studio.schemaeditor.view.wrappers.AttributeTypeWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.FirstNameSorter;
import org.apache.directory.studio.schemaeditor.view.wrappers.Folder;
import org.apache.directory.studio.schemaeditor.view.wrappers.ObjectClassWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.OidSorter;
import org.apache.directory.studio.schemaeditor.view.wrappers.SchemaSorter;
import org.apache.directory.studio.schemaeditor.view.wrappers.SchemaViewRoot;
import org.apache.directory.studio.schemaeditor.view.wrappers.SchemaWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.TreeNode;
import org.apache.directory.studio.schemaeditor.view.wrappers.Folder.FolderType;
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

    /** The RootWrapper */
    private SchemaViewRoot root;

    /** The 'Elements To Wrappers' Map */
    private MultiValueMap elementsToWrappersMap;

    private HierarchyManager hierarchyManager;


    /**
     * Creates a new instance of DifferencesWidgetSchemaContentProvider.
     */
    public SchemaViewContentProvider()
    {
        store = Activator.getDefault().getPreferenceStore();

        firstNameSorter = new FirstNameSorter();
        oidSorter = new OidSorter();
        schemaSorter = new SchemaSorter();

        elementsToWrappersMap = new MultiValueMap();
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

        int presentation = store.getInt( PluginConstants.PREFS_SCHEMA_VIEW_SCHEMA_PRESENTATION );
        int group = store.getInt( PluginConstants.PREFS_SCHEMA_VIEW_GROUPING );
        int sortBy = store.getInt( PluginConstants.PREFS_SCHEMA_VIEW_SORTING_BY );
        int sortOrder = store.getInt( PluginConstants.PREFS_SCHEMA_VIEW_SORTING_ORDER );

        if ( parentElement instanceof SchemaViewRoot )
        {
            root = ( SchemaViewRoot ) parentElement;

            if ( presentation == PluginConstants.PREFS_SCHEMA_VIEW_SCHEMA_PRESENTATION_FLAT )
            {
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
            else if ( presentation == PluginConstants.PREFS_SCHEMA_VIEW_SCHEMA_PRESENTATION_HIERARCHICAL )
            {
                if ( root.getChildren().isEmpty() )
                {
                    hierarchyManager = new HierarchyManager();

                    if ( group == PluginConstants.PREFS_SCHEMA_VIEW_GROUPING_FOLDERS )
                    {
                        Folder atFolder = new Folder( FolderType.ATTRIBUTE_TYPE, root );
                        Folder ocFolder = new Folder( FolderType.OBJECT_CLASS, root );
                        root.addChild( atFolder );
                        root.addChild( ocFolder );

                        List<Object> rootChildren = hierarchyManager.getChildren( hierarchyManager.getRootObject() );
                        if ( ( rootChildren != null ) && ( rootChildren.size() > 0 ) )
                        {
                            for ( Object rootChild : rootChildren )
                            {
                                TreeNode childNode = null;

                                // Creating the wrapper
                                if ( rootChild instanceof AttributeTypeImpl )
                                {
                                    AttributeTypeImpl at = ( AttributeTypeImpl ) rootChild;
                                    childNode = new AttributeTypeWrapper( at, atFolder );
                                    atFolder.addChild( childNode );
                                }
                                else if ( rootChild instanceof ObjectClassImpl )
                                {
                                    ObjectClassImpl oc = ( ObjectClassImpl ) rootChild;
                                    childNode = new ObjectClassWrapper( oc, ocFolder );
                                    ocFolder.addChild( childNode );
                                }

                                // Filling the 'Elements To Wrappers' Map
                                elementsToWrappersMap.put( rootChild, childNode );

                                // Recursively creating the hierarchy for all children
                                // of the root element.
                                addHierarchyChildren( childNode, hierarchyManager.getChildren( rootChild ) );
                            }
                        }
                    }
                    else if ( group == PluginConstants.PREFS_SCHEMA_VIEW_GROUPING_MIXED )
                    {
                        addHierarchyChildren( root, hierarchyManager.getChildren( hierarchyManager.getRootObject() ) );
                    }
                }

                children = root.getChildren();

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

        }
        else if ( parentElement instanceof Folder )
        {
            children = ( ( TreeNode ) parentElement ).getChildren();

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
        else if ( ( parentElement instanceof AttributeTypeWrapper ) || ( parentElement instanceof ObjectClassWrapper )
            || ( parentElement instanceof SchemaWrapper ) )
        {
            children = ( ( TreeNode ) parentElement ).getChildren();

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

        return children.toArray();
    }


    /**
     * Converts the given children and adds them to the given node.
     *
     * @param node
     *      the parent node.
     * @param children
     *      the children
     */
    private void addHierarchyChildren( TreeNode node, List<Object> children )
    {
        if ( ( children != null ) && ( children.size() > 0 ) )
        {
            for ( Object child : children )
            {
                TreeNode childNode = null;
                if ( child instanceof AttributeTypeImpl )
                {
                    AttributeTypeImpl at = ( AttributeTypeImpl ) child;
                    childNode = new AttributeTypeWrapper( at, node );
                    node.addChild( childNode );
                }
                else if ( child instanceof ObjectClassImpl )
                {
                    ObjectClassImpl oc = ( ObjectClassImpl ) child;
                    childNode = new ObjectClassWrapper( oc, node );
                    node.addChild( childNode );
                }

                // Filling the 'Elements To Wrappers' Map
                elementsToWrappersMap.put( child, childNode );

                // Recursively creating the hierarchy for all children
                // of the given element.
                addHierarchyChildren( childNode, hierarchyManager.getChildren( child ) );
            }
        }
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


    /**
     * Gets the wrappers associated with the given object.
     *
     * @param o
     *      the object
     * @return
     *      the wrappers associated with the given object
     */
    @SuppressWarnings("unchecked")
    public List<TreeNode> getWrappers( Object o )
    {
        return ( List<TreeNode> ) elementsToWrappersMap.get( o );
    }


    /**
     * Gets the wrapper associated with the given object.
     *
     * @param o
     *      the object
     * @return
     *      the wrapper associated with the given object
     */
    public TreeNode getWrapper( Object o )
    {
        List<TreeNode> wrappers = getWrappers( o );
        if ( ( wrappers != null ) && ( wrappers.size() > 0 ) )
        {
            return wrappers.get( 0 );
        }

        // Default
        return null;
    }


    /**
     * Gets the Root Element.
     *
     * @return
     *      the Root Element
     */
    public SchemaViewRoot getRoot()
    {
        return root;
    }


    /**
     * Adds the given <element, wrapper> association.
     *
     * @param element
     *      the element
     * @param wrapper
     *      the wrapper
     */
    public void addElementToWrapper( Object element, TreeNode wrapper )
    {
        elementsToWrappersMap.put( element, wrapper );
    }


    /**
     * Removes the given <element, wrapper> association.
     *
     * @param element
     *      the element
     * @param wrapper
     *      the wrapper
     */
    public void removeElementToWrapper( Object element, TreeNode wrapper )
    {
        elementsToWrappersMap.remove( element, wrapper );
    }


    /**
     * Remove all <element, wrapper> association for the given element.
     *
     * @param element
     *      the element
     */
    public void removeElementToWrapper( Object element )
    {
        elementsToWrappersMap.remove( element );
    }


    public void attributeTypeAdded( AttributeTypeImpl at )
    {
        hierarchyManager.attributeTypeAdded( at );

        List<TreeNode> createdWrappers = new ArrayList<TreeNode>();

        List<Object> parents = hierarchyManager.getParents( at );

        if ( parents != null )
        {
            for ( Object parent : parents )
            {
                AttributeTypeWrapper parentATW = ( AttributeTypeWrapper ) getWrapper( parent );
                AttributeTypeWrapper atw = null;
                if ( parentATW == null )
                {
                    int group = store.getInt( PluginConstants.PREFS_SCHEMA_VIEW_GROUPING );
                    if ( group == PluginConstants.PREFS_SCHEMA_VIEW_GROUPING_FOLDERS )
                    {
                        for ( TreeNode child : root.getChildren() )
                        {
                            if ( child instanceof Folder )
                            {
                                Folder folder = ( Folder ) child;
                                if ( folder.getType().equals( FolderType.ATTRIBUTE_TYPE ) )
                                {
                                    atw = new AttributeTypeWrapper( at, folder );
                                    break;
                                }
                            }
                        }
                    }
                    else if ( group == PluginConstants.PREFS_SCHEMA_VIEW_GROUPING_MIXED )
                    {
                        atw = new AttributeTypeWrapper( at, root );
                    }

                }
                else
                {
                    atw = new AttributeTypeWrapper( at, parentATW );
                }
                atw.getParent().addChild( atw );
                createdWrappers.add( atw );
                elementsToWrappersMap.put( at, atw );
            }
        }

        List<Object> children = hierarchyManager.getChildren( at );
        if ( children != null )
        {
            for ( Object child : children )
            {
                AttributeTypeWrapper childATW = ( AttributeTypeWrapper ) getWrapper( child );
                elementsToWrappersMap.remove( child );
                childATW.getParent().removeChild( childATW );

                for ( TreeNode createdWrapper : createdWrappers )
                {
                    AttributeTypeWrapper atw = new AttributeTypeWrapper( ( AttributeTypeImpl ) child, createdWrapper );
                    atw.getParent().addChild( atw );
                    elementsToWrappersMap.put( ( AttributeTypeImpl ) child, atw );
                }
            }
        }

    }


    public void attributeTypeModified( AttributeTypeImpl at )
    {
    }


    public void attributeTypeRemoved( AttributeTypeImpl at )
    {
        // Creating children nodes of the AT 
        // and attaching them to the root
        List<Object> children = hierarchyManager.getChildren( at );
        if ( children != null )
        {
            for ( Object child : children )
            {
                AttributeTypeWrapper atw = null;
                int group = store.getInt( PluginConstants.PREFS_SCHEMA_VIEW_GROUPING );
                if ( group == PluginConstants.PREFS_SCHEMA_VIEW_GROUPING_FOLDERS )
                {
                    for ( TreeNode rootChild : root.getChildren() )
                    {
                        if ( rootChild instanceof Folder )
                        {
                            Folder folder = ( Folder ) rootChild;
                            if ( folder.getType().equals( FolderType.ATTRIBUTE_TYPE ) )
                            {
                                atw = new AttributeTypeWrapper( ( AttributeTypeImpl ) child, folder );
                                break;
                            }
                        }
                    }
                }
                else if ( group == PluginConstants.PREFS_SCHEMA_VIEW_GROUPING_MIXED )
                {
                    atw = new AttributeTypeWrapper( ( AttributeTypeImpl ) child, root );
                }

                atw.getParent().addChild( atw );
                elementsToWrappersMap.put( ( AttributeTypeImpl ) child, atw );
            }
        }

        // Propagating the removal to the hierarchy manager
        hierarchyManager.attributeTypeRemoved( at );

        // Removing the Wrappers
        List<TreeNode> wrappers = getWrappers( at );
        if ( wrappers != null )
        {
            for ( TreeNode wrapper : wrappers )
            {
                wrapper.getParent().removeChild( wrapper );
                removeRecursiveChildren( wrapper );
            }

            elementsToWrappersMap.remove( at );
        }
    }


    private void removeRecursiveChildren( TreeNode wrapper )
    {
        for ( TreeNode child : wrapper.getChildren() )
        {
            if ( child instanceof AttributeTypeImpl )
            {
                elementsToWrappersMap.remove( ( AttributeTypeImpl ) child, child );
            }
            else if ( child instanceof ObjectClassImpl )
            {
                elementsToWrappersMap.remove( ( ObjectClassImpl ) child, child );
            }
        }
    }


    public void objectClassAdded( ObjectClassImpl oc )
    {
    }


    public void objectClassModified( ObjectClassImpl oc )
    {
    }


    public void objectClassRemoved( ObjectClassImpl oc )
    {
    }


    public void schemaAdded( Schema schema )
    {
    }


    public void schemaRemoved( Schema schema )
    {
    }
}
