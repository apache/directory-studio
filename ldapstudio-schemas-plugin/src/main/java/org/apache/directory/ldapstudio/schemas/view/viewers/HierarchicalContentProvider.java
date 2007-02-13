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
import java.util.Collection;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.ldapstudio.schemas.Activator;
import org.apache.directory.ldapstudio.schemas.controller.actions.HideAttributeTypesAction;
import org.apache.directory.ldapstudio.schemas.controller.actions.HideObjectClassesAction;
import org.apache.directory.ldapstudio.schemas.model.AttributeType;
import org.apache.directory.ldapstudio.schemas.model.ObjectClass;
import org.apache.directory.ldapstudio.schemas.model.SchemaPool;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.AlphabeticalOrderComparator;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.AttributeTypeWrapper;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.DisplayableTreeElement;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.IntermediateNode;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ObjectClassWrapper;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;


/**
 * This class implements the content provider for the Hierarchy View.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class HierarchicalContentProvider implements SortableContentProvider, IStructuredContentProvider,
    ITreeContentProvider
{
    /** The Schema Pool holding all schemas */
    private SchemaPool schemaPool;

    /** The HashTable containing all the object classes */
    private Hashtable<String, ObjectClass> objectClassTable;

    /** The HashTable containing all the attribute types */
    private Hashtable<String, AttributeType> attributeTypeTable;

    /** The Order Comparator */
    private Comparator order = new AlphabeticalOrderComparator();


    /**
     * Creates a new instance of HierarchicalContentProvider.
     *
     * @param schemaPool
     *      the associated Schema Pool
     */
    public HierarchicalContentProvider()
    {
        this.schemaPool = SchemaPool.getInstance();

        objectClassTable = schemaPool.getObjectClassesAsHashTableByName();
        attributeTypeTable = schemaPool.getAttributeTypesAsHashTableByName();
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
        if ( parentElement instanceof ObjectClassWrapper )
        {
            //we are looking for the childrens of the contained objectClass
            ObjectClass objectClass = ( ( ObjectClassWrapper ) parentElement ).getMyObjectClass();

            List<ObjectClassWrapper> subTypes = new ArrayList<ObjectClassWrapper>();

            //-> we need to compare each and every other objectClass's sup against them 
            //-> we also need to find a better way to do this (complexity wise)

            Collection<ObjectClass> objectClasses = objectClassTable.values();
            for ( Iterator iter = objectClasses.iterator(); iter.hasNext(); )
            {
                ObjectClass oClass = ( ObjectClass ) iter.next();

                //not this object class
                if ( oClass.getOid() != objectClass.getOid() )
                {
                    String[] sups = oClass.getSuperiors();
                    for ( String sup : sups )
                    {
                        ObjectClass oClassSup = objectClassTable.get( sup );
                        if ( oClassSup != null )
                        {
                            //the current object class is a sup of oClass
                            if ( oClassSup.equals( objectClass ) )
                            {
                                //we use an objectClass wrapper
                                subTypes
                                    .add( new ObjectClassWrapper( oClass, ( DisplayableTreeElement ) parentElement ) );
                                break; //break only the inner for
                            }
                        }
                    }
                }
            }

            return subTypes.toArray();
        }

        if ( parentElement instanceof AttributeTypeWrapper )
        {
            //we are looking for the childrens of the contained attribute type
            AttributeType attributeType = ( ( AttributeTypeWrapper ) parentElement ).getMyAttributeType();

            List<AttributeTypeWrapper> subTypes = new ArrayList<AttributeTypeWrapper>();

            //-> we need to compare each and every other attribute type sup against them 
            //-> we also need to find a better way to do this (complexity wise)

            Collection<AttributeType> attributeTypes = attributeTypeTable.values();
            for ( Iterator iter = attributeTypes.iterator(); iter.hasNext(); )
            {
                AttributeType aType = ( AttributeType ) iter.next();

                //not this attribute type
                if ( aType.getOid() != attributeType.getOid() )
                {
                    String aTypeSupName = aType.getSuperior();
                    if ( aTypeSupName != null )
                    {
                        AttributeType aTypeSup = attributeTypeTable.get( aType.getSuperior() );
                        if ( aTypeSup != null )
                        {
                            //the current object class is a sup of oClass
                            if ( aTypeSup.equals( attributeType ) )
                            {
                                //we use an objectClass wrapper
                                subTypes
                                    .add( new AttributeTypeWrapper( aType, ( DisplayableTreeElement ) parentElement ) );
                                break; //break only the inner for
                            }
                        }
                    }
                }
            }

            return subTypes.toArray();
        }

        else if ( parentElement instanceof IntermediateNode )
        {
            IntermediateNode intermediate = ( IntermediateNode ) parentElement;

            if ( intermediate.getName().equals( "**Primary Node**" ) ) { //$NON-NLS-1$
                //if we are asked for the primary node it's because the whole viewer
                //is beeing refreshed 
                // -> the pool has been modified or it's the first display
                // -> we need to regenerate the hashmaps containing the schemas elements
                refreshOcsAndAts();

                //clear the primary node (because it's always the same instance we need to
                //refresh it manually)
                intermediate.clearChildrens();
                if ( !Activator.getDefault().getDialogSettings().getBoolean(
                    HideObjectClassesAction.HIDE_OBJECT_CLASSES_DS_KEY ) )
                {
                    //bootstrap the tree exploring process by adding the top node into the tree
                    ObjectClass top = schemaPool.getObjectClass( "top" ); //$NON-NLS-1$
                    if ( top != null )
                    {
                        ObjectClassWrapper topWrapper = new ObjectClassWrapper( top, intermediate );
                        intermediate.addElement( topWrapper );
                    }

                    //add the unresolved object-classes to the top of the hierarchy
                    Collection<ObjectClass> objectClasses = objectClassTable.values();
                    for ( Iterator iter = objectClasses.iterator(); iter.hasNext(); )
                    {
                        ObjectClass oClass = ( ObjectClass ) iter.next();
                        String[] sups = oClass.getSuperiors();
                        //if no supperiors had been set
                        if ( sups.length == 0 )
                        {
                            ObjectClassWrapper wrapper = new ObjectClassWrapper( oClass, intermediate );
                            wrapper.setState( ObjectClassWrapper.State.unResolved );
                            intermediate.addElement( wrapper );
                            this.hasChildren( wrapper );
                        }
                        else
                        {
                            for ( String sup : sups )
                            {
                                ObjectClass oClassSup = objectClassTable.get( sup );
                                if ( oClassSup == null )
                                {
                                    ObjectClassWrapper wrapper = new ObjectClassWrapper( oClass, intermediate );
                                    wrapper.setState( ObjectClassWrapper.State.unResolved );
                                    intermediate.addElement( wrapper );
                                }
                            }
                        }
                    }
                }

                if ( !Activator.getDefault().getDialogSettings().getBoolean(
                    HideAttributeTypesAction.HIDE_ATTRIBUTE_TYPES_DS_KEY ) )
                {
                    //add the unresolved object-classes to the top of the hierarchy
                    Collection<AttributeType> attributeTypes = attributeTypeTable.values();
                    for ( Iterator iter = attributeTypes.iterator(); iter.hasNext(); )
                    {
                        AttributeType aType = ( AttributeType ) iter.next();
                        String sup = aType.getSuperior();
                        //if no superior had been set
                        if ( sup == null )
                        {
                            AttributeTypeWrapper wrapper = new AttributeTypeWrapper( aType, intermediate );
                            intermediate.addElement( wrapper );
                        }
                    }
                }
            }

            return intermediate.getChildren();
        }
        return new Object[0];
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent( Object element )
    {
        if ( element instanceof ObjectClassWrapper )
        {
            return ( ( ObjectClassWrapper ) element ).getParent();
        }
        else if ( element instanceof AttributeTypeWrapper )
        {
            return ( ( AttributeTypeWrapper ) element ).getParent();
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
        if ( element instanceof ObjectClassWrapper )
        {
            return getChildren( ( ObjectClassWrapper ) element ).length > 0;
        }
        else if ( element instanceof AttributeTypeWrapper )
        {
            return getChildren( ( AttributeTypeWrapper ) element ).length > 0;
        }
        else if ( element instanceof IntermediateNode )
        {
            return ( ( IntermediateNode ) element ).getChildren().length > 0;
        }

        return false;
    }


    /**
     * Refreshes the object classes and attribute types HahshTables.
     */
    private void refreshOcsAndAts()
    {
        objectClassTable = schemaPool.getObjectClassesAsHashTableByName();

        attributeTypeTable = schemaPool.getAttributeTypesAsHashTableByName();
    }


    /**
     * Specify the comparator that will be used to sort the elements in the view.
     * 
     * @param order
     *      the comparator
     */
    public void setOrder( Comparator order )
    {
        this.order = order;
    }


    /**
     * Returns the comparator used to sort the elements in the view.
     * 
     * @return
     */
    public Comparator getOrder()
    {
        return order;
    }


    /**
     * Initialize a tree viewer to display the information provided by the specified content
     * provider.
     * 
     * @param viewer
     *      the tree viewer
     */
    public void bindToTreeViewer( TreeViewer viewer )
    {
        viewer.setContentProvider( this );
        viewer.setLabelProvider( new HierarchicalLabelProvider() );

        IntermediateNode invisibleNode = new IntermediateNode( "**Primary Node**", null, this ); //$NON-NLS-1$
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
