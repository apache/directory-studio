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


import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.ldapstudio.schemas.Activator;
import org.apache.directory.ldapstudio.schemas.PluginConstants;
import org.apache.directory.ldapstudio.schemas.model.AttributeType;
import org.apache.directory.ldapstudio.schemas.model.LDAPModelEvent;
import org.apache.directory.ldapstudio.schemas.model.ObjectClass;
import org.apache.directory.ldapstudio.schemas.model.PoolListener;
import org.apache.directory.ldapstudio.schemas.model.Schema;
import org.apache.directory.ldapstudio.schemas.model.SchemaElement;
import org.apache.directory.ldapstudio.schemas.model.SchemaPool;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.AttributeTypeWrapper;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.FirstNameSorter;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ITreeNode;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.IntermediateNode;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ObjectClassWrapper;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.OidSorter;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.SchemaSorter;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.SchemaWrapper;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.SchemasViewRoot;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.IntermediateNode.IntermediateNodeType;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;


/**
 * This class implements the Content Provider for the Schemas View.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SchemasViewContentProvider implements IStructuredContentProvider, ITreeContentProvider, PoolListener
{
    /** The Schema Pool */
    private SchemaPool schemaPool;

    /** The associated viewer */
    private TreeViewer viewer;

    /** The preferences store */
    private IPreferenceStore store;

    /** The FirstName Sorter */
    private FirstNameSorter firstNameSorter;

    /** The OID Sorter */
    private OidSorter oidSorter;

    /** The Schema Sorter */
    private SchemaSorter schemaSorter;


    /**
     * Default constructor
     */
    public SchemasViewContentProvider( TreeViewer viewer )
    {
        this.viewer = viewer;
        schemaPool = SchemaPool.getInstance();
        store = Activator.getDefault().getPreferenceStore();

        firstNameSorter = new FirstNameSorter();
        oidSorter = new OidSorter();
        schemaSorter = new SchemaSorter();

        SchemaPool.getInstance().addListener( this );
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
        int group = store.getInt( PluginConstants.PREFS_SCHEMAS_VIEW_GROUPING );
        int sortBy = store.getInt( PluginConstants.PREFS_SCHEMAS_VIEW_SORTING_BY );
        int sortOrder = store.getInt( PluginConstants.PREFS_SCHEMAS_VIEW_SORTING_ORDER );
        List<ITreeNode> children = null;

        if ( parentElement instanceof SchemasViewRoot )
        {
            SchemasViewRoot root = ( SchemasViewRoot ) parentElement;

            if ( root.getChildren().isEmpty() )
            {
                Schema[] schemas = this.schemaPool.getSchemas();
                for ( int i = 0; i < schemas.length; i++ )
                {
                    root.addChild( new SchemaWrapper( schemas[i], root ) );
                }
            }

            children = root.getChildren();

            Collections.sort( children, schemaSorter );
        }
        if ( parentElement instanceof IntermediateNode )
        {
            IntermediateNode intermediate = ( IntermediateNode ) parentElement;

            if ( intermediate.getChildren().isEmpty() )
            {
                if ( intermediate.getType().equals( IntermediateNodeType.ATTRIBUTE_TYPE_FOLDER ) )
                {
                    Schema schema = ( ( SchemaWrapper ) intermediate.getParent() ).getMySchema();

                    AttributeType[] attributeTypeList = schema.getAttributeTypesAsArray();
                    for ( int i = 0; i < attributeTypeList.length; i++ )
                    {
                        intermediate.addChild( new AttributeTypeWrapper( attributeTypeList[i], intermediate ) );
                    }
                }
                else if ( intermediate.getType().equals( IntermediateNodeType.OBJECT_CLASS_FOLDER ) )
                {
                    Schema schema = ( ( SchemaWrapper ) intermediate.getParent() ).getMySchema();

                    ObjectClass[] objectClassList = schema.getObjectClassesAsArray();
                    for ( int i = 0; i < objectClassList.length; i++ )
                    {
                        intermediate.addChild( new ObjectClassWrapper( objectClassList[i], intermediate ) );
                    }
                }
            }

            children = intermediate.getChildren();

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
        else if ( parentElement instanceof SchemaWrapper )
        {
            SchemaWrapper schemaWrapper = ( SchemaWrapper ) parentElement;

            if ( group == PluginConstants.PREFS_SCHEMAS_VIEW_GROUPING_FOLDERS )
            {
                if ( schemaWrapper.getChildren().isEmpty() )
                {
                    IntermediateNode attributeTypes = new IntermediateNode(
                        "Attribute Types", ( SchemaWrapper ) parentElement, IntermediateNodeType.ATTRIBUTE_TYPE_FOLDER ); //$NON-NLS-1$
                    IntermediateNode objectClasses = new IntermediateNode(
                        "Object Classes", ( SchemaWrapper ) parentElement, IntermediateNodeType.OBJECT_CLASS_FOLDER ); //$NON-NLS-1$
                    schemaWrapper.addChild( attributeTypes );
                    schemaWrapper.addChild( objectClasses );
                }

                children = schemaWrapper.getChildren();
            }
            else if ( group == PluginConstants.PREFS_SCHEMAS_VIEW_GROUPING_MIXED )
            {
                if ( schemaWrapper.getChildren().isEmpty() )
                {
                    Schema schema = schemaWrapper.getMySchema();

                    AttributeType[] attributeTypeList = schema.getAttributeTypesAsArray();
                    for ( int i = 0; i < attributeTypeList.length; i++ )
                    {
                        schemaWrapper.addChild( new AttributeTypeWrapper( attributeTypeList[i], schemaWrapper ) );
                    }

                    ObjectClass[] objectClassList = schema.getObjectClassesAsArray();
                    for ( int i = 0; i < objectClassList.length; i++ )
                    {
                        schemaWrapper.addChild( new ObjectClassWrapper( objectClassList[i], schemaWrapper ) );
                    }
                }

                children = schemaWrapper.getChildren();

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

        if ( children == null )
        {
            return new Object[0];
        }
        else
        {
            return children.toArray();
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent( Object element )
    {
        if ( element instanceof ITreeNode )
        {
            return ( ( ITreeNode ) element ).getParent();
        }

        // Default
        return null;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren( Object element )
    {
        if ( element instanceof SchemaWrapper )
        {
            return true;
        }
        else if ( element instanceof IntermediateNode )
        {
            return true;
        }

        // Default
        return false;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.schemas.model.PoolListener#poolChanged(org.apache.directory.ldapstudio.schemas.model.SchemaPool, org.apache.directory.ldapstudio.schemas.model.LDAPModelEvent)
     */
    public void poolChanged( SchemaPool p, LDAPModelEvent e )
    {
        switch ( e.getReason() )
        {
            case SchemaAdded:
                schemaAdded( p, e );
                break;

            case SchemaRemoved:
                schemaRemoved( p, e );
                break;

            case ATAdded:
            case OCAdded:
                aTOrOCAdded( p, e );
                break;

            case ATModified:
            case OCModified:
                aTOrOCModified( p, e );
                break;

            case ATRemoved:
            case OCRemoved:
                aTOrOCRemoved( p, e );
                break;

            default:
                break;
        }
    }


    /**
     * Refreshes the viewer when a SchemaAdded event is fired.
     *
     * @param p
     *      the schema pool
     * @param e
     *      the event
     */
    private void schemaAdded( SchemaPool p, LDAPModelEvent e )
    {
        ITreeNode rootNode = ( ITreeNode ) viewer.getInput();
        SchemaWrapper schemaWrapper = new SchemaWrapper( ( Schema ) e.getNewValue(), rootNode );
        rootNode.addChild( schemaWrapper );

        Collections.sort( rootNode.getChildren(), new SchemaSorter() );

        viewer.refresh( rootNode );
        viewer.setSelection( new StructuredSelection( schemaWrapper ) );
    }


    /**
     * Refreshes the viewer when a SchemaRemoved event is fired.
     *
     * @param p
     *      the schema pool
     * @param e
     *      the event
     */
    private void schemaRemoved( SchemaPool p, LDAPModelEvent e )
    {
        ITreeNode rootNode = ( ITreeNode ) viewer.getInput();

        List<ITreeNode> schemaWrapperList = rootNode.getChildren();
        for ( Iterator iter = schemaWrapperList.iterator(); iter.hasNext(); )
        {
            SchemaWrapper schemaWrapper = ( SchemaWrapper ) iter.next();
            if ( schemaWrapper.getMySchema().equals( ( Schema ) e.getNewValue() ) )
            {
                rootNode.removeChild( schemaWrapper );
                viewer.refresh( rootNode );
                break;
            }
        }
    }


    /**
     * Refreshes the viewer when a ATAdded or OCAdded event is fired.
     *
     * @param p
     *      the schema pool
     * @param e
     *      the event
     */
    private void aTOrOCAdded( SchemaPool p, LDAPModelEvent e )
    {
        SchemaElement element = ( SchemaElement ) e.getNewValue();

        ITreeNode parentNode = findParentElement( element );
        if ( parentNode == null )
        {
            return;
        }

        // Forcing the load of the children
        getChildren( parentNode );
        
        // Creating and adding the new element
        ITreeNode newElement = null;
        if ( element instanceof AttributeType )
        {
            newElement = new AttributeTypeWrapper( ( AttributeType ) element, parentNode );
        }
        else if ( element instanceof ObjectClass )
        {
            newElement = new ObjectClassWrapper( ( ObjectClass ) element, parentNode );
        }
        parentNode.addChild( newElement );

        // Refreshing the UI and selecting the newly created element
        viewer.refresh( parentNode );
        viewer.setSelection( new StructuredSelection( newElement ) );
    }


    /**
     * Refreshes the viewer when a ATModified or OCModified event is fired.
     *
     * @param p
     *      the schema pool
     * @param e
     *      the event
     */
    private void aTOrOCModified( SchemaPool p, LDAPModelEvent e )
    {
        SchemaElement element = ( SchemaElement ) e.getNewValue();

        ITreeNode parentNode = findParentElement( element );
        if ( parentNode == null )
        {
            return;
        }

        ITreeNode fakeNode = null;
        if ( element instanceof ObjectClass )
        {
            fakeNode = new ObjectClassWrapper( ( ObjectClass ) element, null );
        }
        else if ( element instanceof AttributeType )
        {
            fakeNode = new AttributeTypeWrapper( ( AttributeType ) element, null );
        }

        ITreeNode realNode = null;
        Object[] children = getChildren( parentNode );
        for ( int i = 0; i < children.length; i++ )
        {
            if ( children[i].equals( fakeNode ) )
            {
                realNode = ( ITreeNode ) children[i];
                break;
            }
        }

        if ( realNode != null )
        {
            viewer.update( realNode, null );
        }
    }


    /**
     * Refreshes the viewer when a ATRemoved or OCRemoved event is fired.
     *
     * @param p
     *      the schema pool
     * @param e
     *      the event
     */
    private void aTOrOCRemoved( SchemaPool p, LDAPModelEvent e )
    {
        SchemaElement element = ( SchemaElement ) e.getOldValue();

        ITreeNode parentNode = findParentElement( element );
        if ( parentNode == null )
        {
            return;
        }

        ITreeNode fakeNode = null;
        if ( element instanceof ObjectClass )
        {
            ( ( ObjectClass ) element ).closeAssociatedEditor();
            fakeNode = new ObjectClassWrapper( ( ObjectClass ) element, null );
        }
        else if ( element instanceof AttributeType )
        {
            ( ( AttributeType ) element ).closeAssociatedEditor();
            fakeNode = new AttributeTypeWrapper( ( AttributeType ) element, null );
        }

        ITreeNode realNode = null;
        Object[] children = getChildren( parentNode );
        for ( int i = 0; i < children.length; i++ )
        {
            if ( children[i].equals( fakeNode ) )
            {
                realNode = ( ITreeNode ) children[i];
                break;
            }
        }

        if ( realNode != null )
        {
            realNode.getParent().removeChild( realNode );
            viewer.refresh( realNode.getParent() );
        }
    }


    /**
     * Finds the corresponding Schema Wrapper in the Tree.
     *
     * @param schemaWrapper
     *      the Schema Wrapper to search
     * @return
     *      the corresponding Schema Wrapper in the Tree
     */
    private ITreeNode findSchemaWrapperInTree( SchemaWrapper schemaWrapper )
    {
        Object[] schemaWrappers = getChildren( ( ITreeNode ) viewer.getInput() );
        for ( int i = 0; i < schemaWrappers.length; i++ )
        {
            SchemaWrapper sw = ( SchemaWrapper ) schemaWrappers[i];
            if ( sw.equals( schemaWrapper ) )
            {
                return sw;
            }
        }

        return null;
    }


    /**
     * Finds the parent node of a given element (AT or OT).
     *
     * @param element
     *      the element
     * @return
     *      the parent node of a given element (AT or OT)
     */
    private ITreeNode findParentElement( SchemaElement element )
    {
        int group = Activator.getDefault().getPreferenceStore().getInt( PluginConstants.PREFS_SCHEMAS_VIEW_GROUPING );

        // Finding the associated SchemaWrapper
        SchemaWrapper fakeSchemaWrapper = new SchemaWrapper( element.getOriginatingSchema(), null );
        ITreeNode realSchemaWrapper = findSchemaWrapperInTree( fakeSchemaWrapper );
        if ( realSchemaWrapper == null )
        {
            return null;
        }

        // Finding the correct node
        ITreeNode parentNode = null;
        if ( group == PluginConstants.PREFS_SCHEMAS_VIEW_GROUPING_FOLDERS )
        {
            Object[] children = getChildren( realSchemaWrapper );
            for ( int i = 0; i < children.length; i++ )
            {
                IntermediateNode intermediateNode = ( IntermediateNode ) children[i];

                if ( element instanceof AttributeType )
                {
                    if ( intermediateNode.getType() == IntermediateNodeType.ATTRIBUTE_TYPE_FOLDER )
                    {
                        parentNode = intermediateNode;
                    }
                }
                else if ( element instanceof ObjectClass )
                {
                    if ( intermediateNode.getType() == IntermediateNodeType.OBJECT_CLASS_FOLDER )
                    {
                        parentNode = intermediateNode;
                    }
                }
            }
        }
        else if ( group == PluginConstants.PREFS_SCHEMAS_VIEW_GROUPING_MIXED )
        {
            parentNode = realSchemaWrapper;
        }

        return parentNode;
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
