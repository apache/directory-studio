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


import java.util.Collection;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.directory.ldapstudio.schemas.model.AttributeType;
import org.apache.directory.ldapstudio.schemas.model.ObjectClass;
import org.apache.directory.ldapstudio.schemas.model.SchemaPool;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.AlphabeticalOrderComparator;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.AttributeTypeWrapper;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.IntermediateNode;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ObjectClassWrapper;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;


public class HierarchicalContentProvider implements SortableContentProvider, IStructuredContentProvider,
    ITreeContentProvider
{
    private SchemaPool schemaPool;
    private Hashtable<String, ObjectClass> objectClassTable;
    private Hashtable<String, AttributeType> attributeTypeTable;
    private Comparator order = new AlphabeticalOrderComparator();


    public HierarchicalContentProvider( SchemaPool schemaPool )
    {
        this.schemaPool = schemaPool;

        objectClassTable = schemaPool.getObjectClassesAsHashTableByName();

        attributeTypeTable = schemaPool.getAttributeTypesAsHashTableByName();
    }


    /******************************************
     *       Interfaces Implementation        *
     ******************************************/

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements( Object inputElement )
    {
        return getChildren( inputElement );
    }


    /**
     * Disposes of this content provider. This is called by the viewer when it is disposed.
     */

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


    /******************************************
     *       ITreeContentProvider Impl        *
     ******************************************/

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public Object[] getChildren( Object parentElement )
    {

        if ( parentElement instanceof ObjectClassWrapper )
        {
            //we are looking for the childrens of the contained objectClass
            ObjectClass objectClass =  ( ( ObjectClassWrapper ) parentElement ).getMyObjectClass();

            IntermediateNode sub = new IntermediateNode( "Sub-types", ( ObjectClassWrapper ) parentElement, this ); //$NON-NLS-1$
            IntermediateNode may = new IntermediateNode(
                "Optionnal Attributes", ( ObjectClassWrapper ) parentElement, this ); //$NON-NLS-1$
            IntermediateNode must = new IntermediateNode(
                "Mandatory Attributes", ( ObjectClassWrapper ) parentElement, this ); //$NON-NLS-1$

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
                                sub.addElement( new ObjectClassWrapper( oClass, sub ) );
                                break; //break only the inner for
                            }
                        }
                    }
                }
            }

            //complete optional attributes
            String[] optAttributes = objectClass.getMay();
            for ( String name : optAttributes )
            {
                AttributeType attr = attributeTypeTable.get( name );
                //A CHANGER, ON FAIT SAUTER LES ATTR QUI NE SONT PAS DEFINIS
                //DANS LE SCHEMA COURANT (ATTRS PAR DEFAUT)
                if ( attr == null )
                    continue;
                //we use an attribute-type wrapper
                may.addElement( new AttributeTypeWrapper( attr, may ) );
            }

            //complete mandatory attributes
            String[] mandAttributes = objectClass.getMust();
            for ( String name : mandAttributes )
            {
                AttributeType attr = attributeTypeTable.get( name );
                //A CHANGER, ON FAIT SAUTER LES ATTR QUI NE SONT PAS DEFINIS
                //DANS LE SCHEMA COURANT (ATTRS PAR DEFAUT)
                if ( attr == null )
                    continue;

                //we use an attribute-type wrapper
                must.addElement( new AttributeTypeWrapper( attr, must ) );
            }

            return new Object[]
                { sub, may, must };
        }

        else if ( parentElement instanceof IntermediateNode )
        {
            IntermediateNode intermediate = ( IntermediateNode ) parentElement;

            if ( intermediate.getName().equals( "**Primary Node**" ) ) { //$NON-NLS-1$
                //if we are asked for the primary node it's because the whole viewer
                //is beeing refreshed 
                // -> the pool has been modified or it's the first display
                // -> we need to regenerate the hashmaps containing the schemas elements
                refresh();

                //clear the primary node (because it's always the same instance we need to
                //refresh it manually)
                intermediate.clearChildrens();

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
            return true;
        }
        else if ( element instanceof AttributeTypeWrapper )
        {
            return false;
        }
        else if ( element instanceof IntermediateNode )
        {
            if ( ( ( IntermediateNode ) element ).getChildren().length > 0 )
                return true;
        }

        return false;
    }


    /******************************************
     *             Internal Logic             *
     ******************************************/

    private void refresh()
    {
        objectClassTable = schemaPool.getObjectClassesAsHashTableByName();

        attributeTypeTable = schemaPool.getAttributeTypesAsHashTableByName();
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
