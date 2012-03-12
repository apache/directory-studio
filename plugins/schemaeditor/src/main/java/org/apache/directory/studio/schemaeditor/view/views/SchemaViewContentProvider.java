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
import org.apache.directory.shared.ldap.model.schema.AttributeType;
import org.apache.directory.shared.ldap.model.schema.MutableObjectClass;
import org.apache.directory.shared.ldap.model.schema.ObjectClass;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.apache.directory.studio.schemaeditor.model.hierarchy.HierarchyManager;
import org.apache.directory.studio.schemaeditor.view.wrappers.AttributeTypeWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.FirstNameSorter;
import org.apache.directory.studio.schemaeditor.view.wrappers.Folder;
import org.apache.directory.studio.schemaeditor.view.wrappers.Folder.FolderType;
import org.apache.directory.studio.schemaeditor.view.wrappers.ObjectClassWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.OidSorter;
import org.apache.directory.studio.schemaeditor.view.wrappers.SchemaSorter;
import org.apache.directory.studio.schemaeditor.view.wrappers.SchemaViewRoot;
import org.apache.directory.studio.schemaeditor.view.wrappers.SchemaWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.TreeNode;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


/**
 * This class implements the ContentProvider for the SchemaView.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
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
    }


    /**
     * {@inheritDoc}
     */
    public Object[] getElements( Object inputElement )
    {
        return getChildren( inputElement );
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        // Nothing to do
    }


    /**
     * {@inheritDoc}
     */
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
    {
        // Nothing to do
    }


    /**
     * {@inheritDoc}
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
                    elementsToWrappersMap = new MultiValueMap();

                    SchemaHandler schemaHandler = Activator.getDefault().getSchemaHandler();
                    if ( schemaHandler != null )
                    {
                        List<Schema> schemas = schemaHandler.getSchemas();
                        for ( Schema schema : schemas )
                        {
                            addSchemaFlatPresentation( schema );
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
                    elementsToWrappersMap = new MultiValueMap();

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
                                if ( rootChild instanceof AttributeType )
                                {
                                    AttributeType at = ( AttributeType ) rootChild;
                                    childNode = new AttributeTypeWrapper( at, atFolder );
                                    atFolder.addChild( childNode );
                                }
                                else if ( rootChild instanceof ObjectClass )
                                {
                                    MutableObjectClass oc = ( MutableObjectClass ) rootChild;
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
        else if ( ( parentElement instanceof AttributeTypeWrapper ) || ( parentElement instanceof ObjectClassWrapper ) )
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
        else if ( parentElement instanceof SchemaWrapper )
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
                if ( child instanceof AttributeType )
                {
                    AttributeType at = ( AttributeType ) child;
                    childNode = new AttributeTypeWrapper( at, node );
                    node.addChild( childNode );
                }
                else if ( child instanceof ObjectClass )
                {
                    MutableObjectClass oc = ( MutableObjectClass ) child;
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


    /**
     * {@inheritDoc}
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


    /**
     * {@inheritDoc}
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


    /**
     * This method is called when an attribute type is added.
     *
     * @param at
     *      the added attribute type
     */
    public void attributeTypeAdded( AttributeType at )
    {
        int presentation = store.getInt( PluginConstants.PREFS_SCHEMA_VIEW_SCHEMA_PRESENTATION );
        if ( presentation == PluginConstants.PREFS_SCHEMA_VIEW_SCHEMA_PRESENTATION_FLAT )
        {
            attributeTypeAddedFlatPresentation( at );
        }
        else if ( presentation == PluginConstants.PREFS_SCHEMA_VIEW_SCHEMA_PRESENTATION_HIERARCHICAL )
        {
            attributeTypeAddedHierarchicalPresentation( at );
        }
    }


    /**
     * Updates the TreeNodes when an attribute type is added and the
     * presentation is set as 'Flat'.
     *
     * @param at
     *      the added attribute type
     */
    public void attributeTypeAddedFlatPresentation( AttributeType at )
    {
        SchemaWrapper schemaWrapper = ( SchemaWrapper ) getWrapper( Activator.getDefault().getSchemaHandler()
            .getSchema( at.getSchemaName() ) );
        if ( schemaWrapper != null )
        {
            AttributeTypeWrapper atw = null;
            int group = store.getInt( PluginConstants.PREFS_SCHEMA_VIEW_GROUPING );
            if ( group == PluginConstants.PREFS_SCHEMA_VIEW_GROUPING_FOLDERS )
            {
                for ( TreeNode child : schemaWrapper.getChildren() )
                {
                    if ( ( ( Folder ) child ).getType() == FolderType.ATTRIBUTE_TYPE )
                    {
                        atw = new AttributeTypeWrapper( at, child );
                        break;
                    }
                }
            }
            else if ( group == PluginConstants.PREFS_SCHEMA_VIEW_GROUPING_MIXED )
            {
                atw = new AttributeTypeWrapper( at, schemaWrapper );
            }

            atw.getParent().addChild( atw );
            elementsToWrappersMap.put( at, atw );
        }
    }


    /**
     * Updates the TreeNodes when an attribute type is added and the
     * presentation is set as 'Hierarchical'.
     *
     * @param at
     *      the added attribute type
     */
    public void attributeTypeAddedHierarchicalPresentation( AttributeType at )
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
                    AttributeTypeWrapper atw = new AttributeTypeWrapper( ( AttributeType ) child, createdWrapper );
                    atw.getParent().addChild( atw );
                    elementsToWrappersMap.put( child, atw );
                }
            }
        }
    }


    /**
     * This method is called when an attribute type is modified.
     *
     * @param at
     *      the modified attribute type
     */
    public void attributeTypeModified( AttributeType at )
    {
        int presentation = store.getInt( PluginConstants.PREFS_SCHEMA_VIEW_SCHEMA_PRESENTATION );
        if ( presentation == PluginConstants.PREFS_SCHEMA_VIEW_SCHEMA_PRESENTATION_FLAT )
        {
            attributeTypeModifiedFlatPresentation( at );
        }
        else if ( presentation == PluginConstants.PREFS_SCHEMA_VIEW_SCHEMA_PRESENTATION_HIERARCHICAL )
        {
            attributeTypeModifiedHierarchicalPresentation( at );
        }
    }


    /**
     * Updates the TreeNodes when an attribute type is modified and the
     * presentation is set as 'Flat'.
     *
     * @param at
     *      the modified attribute type
     */
    public void attributeTypeModifiedFlatPresentation( AttributeType at )
    {
        // Nothing to do
    }


    /**
     * Updates the TreeNodes when an attribute type is modified and the
     * presentation is set as 'Hierarchical'.
     *
     * @param at
     *      the modified attribute type
     */
    public void attributeTypeModifiedHierarchicalPresentation( AttributeType at )
    {
        // Propagating the modification to the hierarchy manager
        hierarchyManager.attributeTypeModified( at );

        // Removing the Wrappers
        List<TreeNode> wrappers = getWrappers( at );
        if ( wrappers != null )
        {
            for ( TreeNode wrapper : wrappers )
            {
                wrapper.getParent().removeChild( wrapper );
            }

            elementsToWrappersMap.remove( at );
        }

        // Creating the wrapper
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
                elementsToWrappersMap.put( at, atw );
                addHierarchyChildren( atw, hierarchyManager.getChildren( at ) );
            }
        }
    }


    /**
     * This method is called when an attribute type is removed.
     *
     * @param at
     *      the removed attribute type
     */
    public void attributeTypeRemoved( AttributeType at )
    {
        int presentation = store.getInt( PluginConstants.PREFS_SCHEMA_VIEW_SCHEMA_PRESENTATION );
        if ( presentation == PluginConstants.PREFS_SCHEMA_VIEW_SCHEMA_PRESENTATION_FLAT )
        {
            attributeTypeRemovedFlatPresentation( at );
        }
        else if ( presentation == PluginConstants.PREFS_SCHEMA_VIEW_SCHEMA_PRESENTATION_HIERARCHICAL )
        {
            attributeTypeRemovedHierarchicalPresentation( at );
        }
    }


    /**
     * Updates the TreeNodes when an attribute type is removed and the
     * presentation is set as 'Flat'.
     *
     * @param at
     *      the removed attribute type
     */
    private void attributeTypeRemovedFlatPresentation( AttributeType at )
    {
        AttributeTypeWrapper atw = ( AttributeTypeWrapper ) getWrapper( at );
        if ( atw != null )
        {
            atw.getParent().removeChild( atw );
            elementsToWrappersMap.remove( at, atw );
        }
    }


    /**
     * Updates the TreeNodes when an attribute type is removed and the
     * presentation is set as 'Hierarchical'.
     *
     * @param at
     *      the removed attribute type
     */
    private void attributeTypeRemovedHierarchicalPresentation( AttributeType at )
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
                                atw = new AttributeTypeWrapper( ( AttributeType ) child, folder );
                                break;
                            }
                        }
                    }
                }
                else if ( group == PluginConstants.PREFS_SCHEMA_VIEW_GROUPING_MIXED )
                {
                    atw = new AttributeTypeWrapper( ( AttributeType ) child, root );
                }

                atw.getParent().addChild( atw );
                elementsToWrappersMap.put( child, atw );
            }
        }

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

        // Propagating the removal to the hierarchy manager
        hierarchyManager.attributeTypeRemoved( at );
    }


    /**
     * Recursively removes the children of the given wrapper.
     *
     * @param wrapper
     *      the wrapper
     */
    private void removeRecursiveChildren( TreeNode wrapper )
    {
        for ( TreeNode child : wrapper.getChildren() )
        {
            if ( child instanceof AttributeTypeWrapper )
            {
                AttributeTypeWrapper atw = ( AttributeTypeWrapper ) child;
                elementsToWrappersMap.remove( atw.getAttributeType(), child );
                removeRecursiveChildren( atw );
            }
            else if ( child instanceof ObjectClassWrapper )
            {
                ObjectClassWrapper ocw = ( ObjectClassWrapper ) child;
                elementsToWrappersMap.remove( ocw.getObjectClass(), child );
                removeRecursiveChildren( ocw );
            }
            else
            {
                removeRecursiveChildren( child );
            }
        }
    }


    /**
     * This method is called when an object class is added.
     *
     * @param oc
     *      the added object class
     */
    public void objectClassAdded( MutableObjectClass oc )
    {
        int presentation = store.getInt( PluginConstants.PREFS_SCHEMA_VIEW_SCHEMA_PRESENTATION );
        if ( presentation == PluginConstants.PREFS_SCHEMA_VIEW_SCHEMA_PRESENTATION_FLAT )
        {
            objectClassAddedFlatPresentation( oc );
        }
        else if ( presentation == PluginConstants.PREFS_SCHEMA_VIEW_SCHEMA_PRESENTATION_HIERARCHICAL )
        {
            objectClassAddedHierarchicalPresentation( oc );
        }
    }


    /**
     * Updates the TreeNodes when an object class is added and the
     * presentation is set as 'Flat'.
     *
     * @param oc
     *      the added object class
     */
    public void objectClassAddedFlatPresentation( MutableObjectClass oc )
    {
        SchemaWrapper schemaWrapper = ( SchemaWrapper ) getWrapper( Activator.getDefault().getSchemaHandler()
            .getSchema( oc.getSchemaName() ) );
        if ( schemaWrapper != null )
        {
            ObjectClassWrapper ocw = null;
            int group = store.getInt( PluginConstants.PREFS_SCHEMA_VIEW_GROUPING );
            if ( group == PluginConstants.PREFS_SCHEMA_VIEW_GROUPING_FOLDERS )
            {
                for ( TreeNode child : schemaWrapper.getChildren() )
                {
                    if ( ( ( Folder ) child ).getType() == FolderType.OBJECT_CLASS )
                    {
                        ocw = new ObjectClassWrapper( oc, child );
                        break;
                    }
                }
            }
            else if ( group == PluginConstants.PREFS_SCHEMA_VIEW_GROUPING_MIXED )
            {
                ocw = new ObjectClassWrapper( oc, schemaWrapper );
            }

            ocw.getParent().addChild( ocw );
            elementsToWrappersMap.put( oc, ocw );
        }
    }


    /**
     * Updates the TreeNodes when an object class is added and the
     * presentation is set as 'Hierarchical'.
     *
     * @param oc
     *      the added object class
     */
    public void objectClassAddedHierarchicalPresentation( MutableObjectClass oc )
    {
        // Removing unattached nodes for "top"
        List<Object> ocChildren = new ArrayList<Object>();
        List<Object> ocChildren2 = null;
        if ( "2.5.6.0".equals( oc.getOid() ) )
        {
            ocChildren2 = hierarchyManager.getChildren( "2.5.6.0" );
            if ( ocChildren2 != null )
            {
                ocChildren.addAll( ocChildren2 );
            }
            ocChildren2 = hierarchyManager.getChildren( "top" );
            if ( ocChildren2 != null )
            {
                ocChildren.addAll( ocChildren2 );
            }
        }
        ocChildren2 = hierarchyManager.getChildren( oc );
        if ( ocChildren2 != null )
        {
            ocChildren.addAll( ocChildren2 );
        }
        for ( Object ocChild : ocChildren )
        {
            List<TreeNode> wrappers = getWrappers( ocChild );
            if ( wrappers != null )
            {
                for ( TreeNode wrapper : wrappers )
                {
                    int group = store.getInt( PluginConstants.PREFS_SCHEMA_VIEW_GROUPING );
                    if ( group == PluginConstants.PREFS_SCHEMA_VIEW_GROUPING_FOLDERS )
                    {
                        if ( wrapper.getParent().getParent().equals( root ) )
                        {
                            wrapper.getParent().removeChild( wrapper );
                            elementsToWrappersMap.remove( oc, wrapper );
                        }
                    }
                    else if ( group == PluginConstants.PREFS_SCHEMA_VIEW_GROUPING_MIXED )
                    {
                        if ( wrapper.getParent().equals( root ) )
                        {
                            wrapper.getParent().removeChild( wrapper );
                            elementsToWrappersMap.remove( oc, wrapper );
                        }
                    }
                    removeRecursiveChildren( wrapper );
                }
            }
        }

        // Propagating the addition to the hierarchy manager
        hierarchyManager.objectClassAdded( oc );

        List<TreeNode> createdWrappers = new ArrayList<TreeNode>();

        List<Object> parents = hierarchyManager.getParents( oc );

        if ( parents != null )
        {
            for ( Object parent : parents )
            {
                ObjectClassWrapper parentOCW = ( ObjectClassWrapper ) getWrapper( parent );
                ObjectClassWrapper ocw = null;
                if ( parentOCW == null )
                {
                    int group = store.getInt( PluginConstants.PREFS_SCHEMA_VIEW_GROUPING );
                    if ( group == PluginConstants.PREFS_SCHEMA_VIEW_GROUPING_FOLDERS )
                    {
                        for ( TreeNode child : root.getChildren() )
                        {
                            if ( child instanceof Folder )
                            {
                                Folder folder = ( Folder ) child;
                                if ( folder.getType().equals( FolderType.OBJECT_CLASS ) )
                                {
                                    ocw = new ObjectClassWrapper( oc, folder );
                                    break;
                                }
                            }
                        }
                    }
                    else if ( group == PluginConstants.PREFS_SCHEMA_VIEW_GROUPING_MIXED )
                    {
                        ocw = new ObjectClassWrapper( oc, root );
                    }

                }
                else
                {
                    ocw = new ObjectClassWrapper( oc, parentOCW );
                }
                ocw.getParent().addChild( ocw );
                createdWrappers.add( ocw );
                elementsToWrappersMap.put( oc, ocw );
            }
        }

        List<Object> children = hierarchyManager.getChildren( oc );
        if ( children != null )
        {
            for ( Object child : children )
            {
                List<TreeNode> childOCWs = getWrappers( child );
                if ( childOCWs != null )
                {
                    for ( TreeNode childOCW : childOCWs )
                    {
                        if ( root.equals( childOCW.getParent() ) )
                        {
                            elementsToWrappersMap.remove( child );
                            childOCW.getParent().removeChild( childOCW );
                        }
                    }
                }

                for ( TreeNode createdWrapper : createdWrappers )
                {
                    ObjectClassWrapper ocw = new ObjectClassWrapper( ( MutableObjectClass ) child, createdWrapper );
                    ocw.getParent().addChild( ocw );
                    elementsToWrappersMap.put( child, ocw );
                    addHierarchyChildren( ocw, hierarchyManager.getChildren( child ) );
                }
            }
        }
    }


    /**
     * This method is called when an object class is modified.
     *
     * @param oc
     *      the modified object class
     */
    public void objectClassModified( MutableObjectClass oc )
    {
        int presentation = store.getInt( PluginConstants.PREFS_SCHEMA_VIEW_SCHEMA_PRESENTATION );
        if ( presentation == PluginConstants.PREFS_SCHEMA_VIEW_SCHEMA_PRESENTATION_FLAT )
        {
            objectClassModifiedFlatPresentation( oc );
        }
        else if ( presentation == PluginConstants.PREFS_SCHEMA_VIEW_SCHEMA_PRESENTATION_HIERARCHICAL )
        {
            objectClassModifiedHierarchicalPresentation( oc );
        }
    }


    /**
     * Updates the TreeNodes when an object class is modified and the
     * presentation is set as 'Flat'.
     *
     * @param oc
     *      the modified object class
     */
    public void objectClassModifiedFlatPresentation( ObjectClass oc )
    {
        // Nothing to do
    }


    /**
     * Updates the TreeNodes when an object class is modified and the
     * presentation is set as 'Hierarchical'.
     *
     * @param oc
     *      the modified object class
     */
    public void objectClassModifiedHierarchicalPresentation( MutableObjectClass oc )
    {
        // Propagating the modification to the hierarchy manager
        hierarchyManager.objectClassModified( oc );

        // Removing the Wrappers
        List<TreeNode> wrappers = getWrappers( oc );
        if ( wrappers != null )
        {
            for ( TreeNode wrapper : wrappers )
            {
                wrapper.getParent().removeChild( wrapper );
            }

            elementsToWrappersMap.remove( oc );
        }

        // Creating the wrapper
        List<Object> parents = hierarchyManager.getParents( oc );
        if ( parents != null )
        {
            for ( Object parent : parents )
            {
                ObjectClassWrapper parentOCW = ( ObjectClassWrapper ) getWrapper( parent );
                ObjectClassWrapper ocw = null;
                if ( parentOCW == null )
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
                                    ocw = new ObjectClassWrapper( oc, folder );
                                    break;
                                }
                            }
                        }
                    }
                    else if ( group == PluginConstants.PREFS_SCHEMA_VIEW_GROUPING_MIXED )
                    {
                        ocw = new ObjectClassWrapper( oc, root );
                    }

                }
                else
                {
                    ocw = new ObjectClassWrapper( oc, parentOCW );
                }
                ocw.getParent().addChild( ocw );
                elementsToWrappersMap.put( oc, ocw );
                addHierarchyChildren( ocw, hierarchyManager.getChildren( oc ) );
            }
        }
    }


    /**
     * This method is called when an object class is removed.
     *
     * @param oc
     *      the removed object class
     */
    public void objectClassRemoved( ObjectClass oc )
    {
        int presentation = store.getInt( PluginConstants.PREFS_SCHEMA_VIEW_SCHEMA_PRESENTATION );
        if ( presentation == PluginConstants.PREFS_SCHEMA_VIEW_SCHEMA_PRESENTATION_FLAT )
        {
            objectClassRemovedFlatPresentation( oc );
        }
        else if ( presentation == PluginConstants.PREFS_SCHEMA_VIEW_SCHEMA_PRESENTATION_HIERARCHICAL )
        {
            objectClassRemovedHierarchicalPresentation( oc );
        }
    }


    /**
     * Updates the TreeNodes when an object class is removed and the
     * presentation is set as 'Flat'.
     *
     * @param oc
     *      the removed object class
     */
    public void objectClassRemovedFlatPresentation( ObjectClass oc )
    {
        ObjectClassWrapper ocw = ( ObjectClassWrapper ) getWrapper( oc );
        if ( ocw != null )
        {
            ocw.getParent().removeChild( ocw );
            elementsToWrappersMap.remove( oc, ocw );
        }
    }


    /**
     * Updates the TreeNodes when an object class is removed and the
     * presentation is set as 'Hierarchical'.
     *
     * @param oc
     *      the removed object class
     */
    public void objectClassRemovedHierarchicalPresentation( ObjectClass oc )
    {
        // Creating children nodes of the OC
        // and attaching them to the root
        List<Object> children = hierarchyManager.getChildren( oc );
        if ( children != null )
        {
            for ( Object child : children )
            {
                ObjectClassWrapper ocw = null;
                int group = store.getInt( PluginConstants.PREFS_SCHEMA_VIEW_GROUPING );
                if ( group == PluginConstants.PREFS_SCHEMA_VIEW_GROUPING_FOLDERS )
                {
                    for ( TreeNode rootChild : root.getChildren() )
                    {
                        if ( rootChild instanceof Folder )
                        {
                            Folder folder = ( Folder ) rootChild;
                            if ( folder.getType().equals( FolderType.OBJECT_CLASS ) )
                            {
                                ocw = new ObjectClassWrapper( ( MutableObjectClass ) child, folder );
                                break;
                            }
                        }
                    }
                }
                else if ( group == PluginConstants.PREFS_SCHEMA_VIEW_GROUPING_MIXED )
                {
                    ocw = new ObjectClassWrapper( ( MutableObjectClass ) child, root );
                }

                ocw.getParent().addChild( ocw );
                elementsToWrappersMap.put( child, ocw );
                addHierarchyChildren( ocw, hierarchyManager.getChildren( child ) );
            }
        }

        // Removing the Wrappers
        List<TreeNode> wrappers = getWrappers( oc );
        if ( wrappers != null )
        {
            for ( TreeNode wrapper : wrappers )
            {
                wrapper.getParent().removeChild( wrapper );
                removeRecursiveChildren( wrapper );
            }

            elementsToWrappersMap.remove( oc );
        }

        // Propagating the removal to the hierarchy manager
        hierarchyManager.objectClassRemoved( oc );
    }


    /**
     * This method is called when a schema is added.
     *
     * @param schema
     *      the added schema
     */
    public void schemaAdded( Schema schema )
    {
        int presentation = store.getInt( PluginConstants.PREFS_SCHEMA_VIEW_SCHEMA_PRESENTATION );
        if ( presentation == PluginConstants.PREFS_SCHEMA_VIEW_SCHEMA_PRESENTATION_FLAT )
        {
            schemaAddedFlatPresentation( schema );
        }
        else if ( presentation == PluginConstants.PREFS_SCHEMA_VIEW_SCHEMA_PRESENTATION_HIERARCHICAL )
        {
            schemaAddedHierarchicalPresentation( schema );
        }
    }


    /**
     * Updates the TreeNodes when a schema is added and the
     * presentation is set as 'Flat'.
     *
     * @param oc
     *      the added schema
     */
    private void schemaAddedFlatPresentation( Schema schema )
    {
        addSchemaFlatPresentation( schema );
    }


    /**
     * Updates the TreeNodes when a schema is added and the
     * presentation is set as 'Hierarchical'.
     *
     * @param oc
     *      the added schema
     */
    private void schemaAddedHierarchicalPresentation( Schema schema )
    {
        for ( AttributeType at : schema.getAttributeTypes() )
        {
            attributeTypeAddedHierarchicalPresentation( at );
        }

        for ( MutableObjectClass oc : schema.getObjectClasses() )
        {
            objectClassAddedHierarchicalPresentation( oc );
        }
    }


    /**
     * This method is called when a schema is removed.
     *
     * @param schema
     *      the removed schema
     */
    public void schemaRemoved( Schema schema )
    {
        int presentation = store.getInt( PluginConstants.PREFS_SCHEMA_VIEW_SCHEMA_PRESENTATION );
        if ( presentation == PluginConstants.PREFS_SCHEMA_VIEW_SCHEMA_PRESENTATION_FLAT )
        {
            schemaRemovedFlatPresentation( schema );
        }
        else if ( presentation == PluginConstants.PREFS_SCHEMA_VIEW_SCHEMA_PRESENTATION_HIERARCHICAL )
        {
            schemaRemovedHierarchicalPresentation( schema );
        }
    }


    /**
     * Updates the TreeNodes when a schema is removed and the
     * presentation is set as 'Flat'.
     *
     * @param schema
     *      the removed schema
     */
    private void schemaRemovedFlatPresentation( Schema schema )
    {
        SchemaWrapper sw = ( SchemaWrapper ) getWrapper( schema );
        if ( sw != null )
        {
            sw.getParent().removeChild( sw );
            elementsToWrappersMap.remove( schema, sw );
            removeRecursiveChildren( sw );
        }
    }


    /**
     * Updates the TreeNodes when a schema is removed and the
     * presentation is set as 'Hierarchical'.
     *
     * @param schema
     *      the removed schema
     */
    private void schemaRemovedHierarchicalPresentation( Schema schema )
    {
        for ( AttributeType at : schema.getAttributeTypes() )
        {
            attributeTypeRemovedHierarchicalPresentation( at );
        }

        for ( ObjectClass oc : schema.getObjectClasses() )
        {
            objectClassRemovedHierarchicalPresentation( oc );
        }
    }


    /**
     * Adds a schema in 'Flat' Presentation.
     *
     * @param schema
     */
    public void addSchemaFlatPresentation( Schema schema )
    {
        SchemaWrapper schemaWrapper = new SchemaWrapper( schema, root );
        root.addChild( schemaWrapper );
        elementsToWrappersMap.put( schema, schemaWrapper );

        int group = store.getInt( PluginConstants.PREFS_SCHEMA_VIEW_GROUPING );
        if ( group == PluginConstants.PREFS_SCHEMA_VIEW_GROUPING_FOLDERS )
        {
            Folder atFolder = new Folder( FolderType.ATTRIBUTE_TYPE, schemaWrapper );
            schemaWrapper.addChild( atFolder );

            for ( AttributeType attributeType : schema.getAttributeTypes() )
            {
                AttributeTypeWrapper atw = new AttributeTypeWrapper( attributeType, atFolder );
                atw.getParent().addChild( atw );
                elementsToWrappersMap.put( attributeType, atw );
            }

            Folder ocFolder = new Folder( FolderType.OBJECT_CLASS, schemaWrapper );
            schemaWrapper.addChild( ocFolder );

            for ( MutableObjectClass objectClass : schema.getObjectClasses() )
            {
                ObjectClassWrapper ocw = new ObjectClassWrapper( objectClass, ocFolder );
                ocw.getParent().addChild( ocw );
                elementsToWrappersMap.put( objectClass, ocw );
            }
        }
        else if ( group == PluginConstants.PREFS_SCHEMA_VIEW_GROUPING_MIXED )
        {
            for ( AttributeType attributeType : schema.getAttributeTypes() )
            {
                AttributeTypeWrapper atw = new AttributeTypeWrapper( attributeType, schemaWrapper );
                atw.getParent().addChild( atw );
                elementsToWrappersMap.put( attributeType, atw );
            }

            for ( MutableObjectClass objectClass : schema.getObjectClasses() )
            {
                ObjectClassWrapper ocw = new ObjectClassWrapper( objectClass, schemaWrapper );
                ocw.getParent().addChild( ocw );
                elementsToWrappersMap.put( objectClass, ocw );
            }
        }
    }
}
