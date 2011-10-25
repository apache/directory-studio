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
import java.util.List;

import org.apache.directory.shared.ldap.model.schema.AttributeType;
import org.apache.directory.shared.ldap.model.schema.ObjectClass;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.model.hierarchy.HierarchyManager;
import org.apache.directory.studio.schemaeditor.view.wrappers.AttributeTypeWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.ObjectClassWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.TreeNode;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


/**
 * This class implements the Content Provider for the Schemas View.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class HierarchyViewContentProvider implements IStructuredContentProvider, ITreeContentProvider
{
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
    public Object[] getChildren( Object parentElement )
    {
        List<TreeNode> children = new ArrayList<TreeNode>();

        if ( parentElement instanceof ObjectClass )
        {
            ObjectClass oc = ( ObjectClass ) parentElement;

            children = createTypeHierarchyObjectClass( oc );
        }
        else if ( parentElement instanceof AttributeType )
        {
            AttributeType at = ( AttributeType ) parentElement;

            children = createTypeHierarchyAttributeType( at );
        }
        else if ( parentElement instanceof TreeNode )
        {
            children = ( ( TreeNode ) parentElement ).getChildren();
        }

        return children.toArray();
    }


    /**
     * Creates the Type Hierarchy for an object class.
     *
     * @param oc
     *      the object class
     * @return
     *      the Type Hierarchy for an object class
     */
    private List<TreeNode> createTypeHierarchyObjectClass( ObjectClass oc )
    {
        List<TreeNode> children = new ArrayList<TreeNode>();

        HierarchyManager hierarchyManager = new HierarchyManager();

        // Creating the wrapper of the object class
        ObjectClassWrapper ocw = new ObjectClassWrapper( oc );

        int mode = Activator.getDefault().getDialogSettings().getInt( PluginConstants.PREFS_HIERARCHY_VIEW_MODE );
        if ( mode == PluginConstants.PREFS_HIERARCHY_VIEW_MODE_TYPE )
        {
            // Creating its children's wrappers
            createChildrenHierarchy( ocw, hierarchyManager.getChildren( oc ), hierarchyManager );

            // Creating its parents' wrappers
            createParentHierarchy( hierarchyManager.getParents( oc ), children, ocw, hierarchyManager );
        }
        else if ( mode == PluginConstants.PREFS_HIERARCHY_VIEW_MODE_SUPERTYPE )
        {
            // Creating its parents' wrappers
            createParentHierarchy( hierarchyManager.getParents( oc ), children, ocw, hierarchyManager );
        }
        else if ( mode == PluginConstants.PREFS_HIERARCHY_VIEW_MODE_SUBTYPE )
        {
            // Creating its children's wrappers
            createChildrenHierarchy( ocw, hierarchyManager.getChildren( oc ), hierarchyManager );

            children.add( ocw );
        }

        return children;
    }


    /**
     * Creates the parent hierarchy.
     *
     * @param parents
     *      the parents
     * @param children
     *      the children
     * @param ocw
     *      the object class wrapper
     * @param hierarchyManager
     *      the hierarchy manager
     */
    private void createParentHierarchy( List<Object> parents, List<TreeNode> children, ObjectClassWrapper ocw,
        HierarchyManager hierarchyManager )
    {
        if ( parents != null )
        {
            for ( Object parent : parents )
            {
                if ( parent instanceof ObjectClass )
                {
                    ObjectClass parentOC = ( ObjectClass ) parent;
                    ObjectClassWrapper duplicatedOCW = ( ObjectClassWrapper ) duplicateTreeNode( ocw );

                    ObjectClassWrapper ocw2 = new ObjectClassWrapper( parentOC );
                    duplicatedOCW.setParent( ocw2 );
                    ocw2.addChild( duplicatedOCW );

                    createParentHierarchy( hierarchyManager.getParents( parentOC ), children, ocw2, hierarchyManager );
                }
                else
                {
                    children.add( ocw );
                }
            }
        }
        else
        {
            children.add( ocw );
        }
    }


    /**
     * Duplicates the given node.
     *
     * @param node
     *      the node
     * @return
     *      a duplicate of the given node
     */
    public TreeNode duplicateTreeNode( TreeNode node )
    {
        if ( node != null )
        {
            if ( node instanceof ObjectClassWrapper )
            {
                ObjectClassWrapper ocNode = ( ObjectClassWrapper ) node;

                ObjectClassWrapper duplicatedOCNode = new ObjectClassWrapper( ocNode.getObjectClass(), ocNode
                    .getParent() );

                for ( TreeNode child : ocNode.getChildren() )
                {
                    TreeNode duplicatedChild = duplicateTreeNode( child );
                    if ( duplicatedChild != null )
                    {
                        duplicatedOCNode.addChild( duplicatedChild );
                    }
                }

                return duplicatedOCNode;
            }
        }

        return null;
    }


    /**
     * Creates the Type Hierarchy for an attribute type.
     *
     * @param at
     *      the attribute type
     * @return
     *      the Type Hierarchy for an attribute type
     */
    private List<TreeNode> createTypeHierarchyAttributeType( AttributeType at )
    {
        List<TreeNode> children = new ArrayList<TreeNode>();
        HierarchyManager hierarchyManager = new HierarchyManager();
        int mode = Activator.getDefault().getDialogSettings().getInt( PluginConstants.PREFS_HIERARCHY_VIEW_MODE );

        // Creating the wrapper of the attribute type
        AttributeTypeWrapper atw = new AttributeTypeWrapper( at );

        if ( mode == PluginConstants.PREFS_HIERARCHY_VIEW_MODE_TYPE )
        {
            // Creating the children's wrappers
            createChildrenHierarchy( atw, hierarchyManager.getChildren( at ), hierarchyManager );

            // Creating its parents' wrappers
            List<Object> parents = hierarchyManager.getParents( at );
            while ( ( parents != null ) && ( parents.size() == 1 ) )
            {
                Object parent = parents.get( 0 );
                if ( parent instanceof AttributeType )
                {
                    AttributeType parentAT = ( AttributeType ) parent;

                    AttributeTypeWrapper atw2 = new AttributeTypeWrapper( parentAT );
                    atw.setParent( atw2 );
                    atw2.addChild( atw );

                    atw = atw2;

                    parents = hierarchyManager.getParents( parentAT );
                }
                else
                {
                    break;
                }
            }

            children.add( atw );
        }
        else if ( mode == PluginConstants.PREFS_HIERARCHY_VIEW_MODE_SUPERTYPE )
        {
            // Creating its parents' wrappers
            List<Object> parents = hierarchyManager.getParents( at );
            while ( ( parents != null ) && ( parents.size() == 1 ) )
            {
                Object parent = parents.get( 0 );
                if ( parent instanceof AttributeType )
                {
                    AttributeType parentAT = ( AttributeType ) parent;

                    AttributeTypeWrapper atw2 = new AttributeTypeWrapper( parentAT );
                    atw.setParent( atw2 );
                    atw2.addChild( atw );

                    atw = atw2;

                    parents = hierarchyManager.getParents( parentAT );
                }
                else
                {
                    break;
                }
            }

            children.add( atw );
        }
        else if ( mode == PluginConstants.PREFS_HIERARCHY_VIEW_MODE_SUBTYPE )
        {
            // Creating the children's wrappers
            createChildrenHierarchy( atw, hierarchyManager.getChildren( at ), hierarchyManager );

            children.add( atw );
        }

        return children;
    }


    /**
     * Create the children hierarchy
     *
     * @param node
     *      the parent node.
     * @param children
     *      the children
     */
    private void createChildrenHierarchy( TreeNode node, List<Object> children, HierarchyManager hierarchyManager )
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
                    ObjectClass oc = ( ObjectClass ) child;
                    childNode = new ObjectClassWrapper( oc, node );
                    node.addChild( childNode );
                }

                // Recursively creating the hierarchy for all children
                // of the given element.
                createChildrenHierarchy( childNode, hierarchyManager.getChildren( child ), hierarchyManager );
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
            return true;
        }

        // Default
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
    }


    /**
     * {@inheritDoc}
     */
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
    {
    }
}
