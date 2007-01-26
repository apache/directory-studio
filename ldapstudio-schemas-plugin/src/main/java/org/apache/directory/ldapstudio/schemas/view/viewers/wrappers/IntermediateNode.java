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

package org.apache.directory.ldapstudio.schemas.view.viewers.wrappers;


import java.util.ArrayList;
import java.util.Collections;

import org.apache.directory.ldapstudio.schemas.controller.Application;
import org.apache.directory.ldapstudio.schemas.view.IImageKeys;
import org.apache.directory.ldapstudio.schemas.view.viewers.SortableContentProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * Class used to display intermediate folder (like 'Optionnal Attributes',
 * 'Mandatory Attributes' and so on...) in the tree view of the schema.
 * Instances of the class may contain any kind of displayable objects like
 * AttributeTypeLiterals or ObjectClassLiterals.
 *
 */
public class IntermediateNode implements DisplayableTreeElement
{

    /** This enum represent the different types of IntermediateNodes */
    public enum IntermediateNodeType
    {
        NONE, OBJECT_CLASS_FOLDER, ATTRIBUTE_TYPE_FOLDER
    }

    /******************************************
     *               Fields                   *
     ******************************************/
    private SortableContentProvider contentProvider;
    private String name;
    private DisplayableTreeElement parent;
    private ArrayList<DisplayableTreeElement> elements;
    private IntermediateNodeType type;


    /******************************************
     *              Constructors              *
     ******************************************/

    /**
     * Default constructor
     * @param name the name that will be displayed in the tree viewer
     * @param parent the parent DisplayableTreeElement in the schema relationship
     * hierarchy
     */
    public IntermediateNode( String name, DisplayableTreeElement parent, SortableContentProvider contentProvider )
    {
        this.name = name;
        this.parent = parent;
        this.contentProvider = contentProvider;
        elements = new ArrayList<DisplayableTreeElement>();
        this.type = IntermediateNodeType.NONE;
    }


    /**
     * Default constructor
     * @param name
     *      the name that will be displayed in the tree viewer
     * @param parent
     *      the parent DisplayableTreeElement in the schema relationship
     * hierarchy
     * @param type 
     *      the type of IntermediateNode
     */
    public IntermediateNode( String name, DisplayableTreeElement parent, SortableContentProvider contentProvider,
        IntermediateNodeType type )
    {
        this.name = name;
        this.parent = parent;
        this.contentProvider = contentProvider;
        elements = new ArrayList<DisplayableTreeElement>();
        this.type = type;
    }


    /******************************************
     *               Accessors                *
     ******************************************/

    /**
     * @return the name of the intermediate node
     */
    public String getName()
    {
        return name;
    }


    /**
     * @return the parent of the intermediate node
     */
    public DisplayableTreeElement getParent()
    {
        return parent;
    }


    /******************************************
     *                 Logic                  *
     ******************************************/

    /**
     * Adds a 'child' element to the node only if it's not already under the node.
     * @param o usually AttributeTypeWrappers, SchemaWrapper or ObjectClassWrappers.
     */
    public void addElement( DisplayableTreeElement o )
    {
        if ( o instanceof AttributeTypeWrapper )
        {
            //initial case
            if ( elements.size() == 0 )
                elements.add( o );

            AttributeTypeWrapper toAdd = ( AttributeTypeWrapper ) o;
            for ( DisplayableTreeElement element : elements )
            {
                if ( element instanceof AttributeTypeWrapper )
                {
                    AttributeTypeWrapper alreadyThere = ( AttributeTypeWrapper ) element;
                    //check if the attributeType instance has already been added
                    if ( toAdd.getMyAttributeType().equals( alreadyThere.getMyAttributeType() ) )
                        return;
                }
            }
            elements.add( o );
        }
        else if ( o instanceof ObjectClassWrapper )
        {
            //initial case
            if ( elements.size() == 0 )
                elements.add( o );

            ObjectClassWrapper toAdd = ( ObjectClassWrapper ) o;
            for ( DisplayableTreeElement element : elements )
            {
                if ( element instanceof ObjectClassWrapper )
                {
                    ObjectClassWrapper alreadyThere = ( ObjectClassWrapper ) element;

                    //check if the objectClass instance has already been added
                    if ( toAdd.getMyObjectClass().equals( alreadyThere.getMyObjectClass() ) )
                        return;
                }
            }
            elements.add( o );
        }
        else if ( o instanceof SchemaWrapper )
        {
            //initial case
            if ( elements.size() == 0 )
                elements.add( o );

            SchemaWrapper toAdd = ( SchemaWrapper ) o;
            for ( DisplayableTreeElement element : elements )
            {
                if ( element instanceof SchemaWrapper )
                {
                    SchemaWrapper alreadyThere = ( SchemaWrapper ) element;

                    if ( toAdd.getMySchema().getName().equals( alreadyThere.getMySchema().getName() ) )
                        return;
                }
            }
            elements.add( o );
        }
    }


    /**
     * Clears the list of childrens from the intermediate nodes
     */
    public void clearChildrens()
    {
        elements = new ArrayList<DisplayableTreeElement>();
    }


    /**
     * Returns the child elements in the form of an array of objects
     * @return
     */
    @SuppressWarnings("unchecked")//$NON-NLS-1$
    public Object[] getChildren()
    {
        Collections.sort( elements, contentProvider.getOrder() );
        return elements.toArray( new Object[]
            {} );
    }


    /******************************************
     *       DisplayableTreeElement Impl.     *
     ******************************************/

    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.DisplayableTreeElement#getDisplayName()
     */
    public String getDisplayName()
    {
        return getName();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.DisplayableTreeElement#getDisplayImage()
     */
    public Image getDisplayImage()
    {
        switch ( type )
        {
            case NONE:
                String imageKey = ISharedImages.IMG_OBJ_FOLDER;
                return PlatformUI.getWorkbench().getSharedImages().getImage( imageKey );
            case ATTRIBUTE_TYPE_FOLDER:
                return AbstractUIPlugin.imageDescriptorFromPlugin( Application.PLUGIN_ID, IImageKeys.FOLDER_ATTRIBUTE_TYPE )
                .createImage();
            case OBJECT_CLASS_FOLDER:
                return AbstractUIPlugin.imageDescriptorFromPlugin( Application.PLUGIN_ID, IImageKeys.FOLDER_OBJECT_CLASS )
                .createImage();
        }
        
        String imageKey = ISharedImages.IMG_OBJ_FOLDER;
        return PlatformUI.getWorkbench().getSharedImages().getImage( imageKey );
    }


    /******************************************
     *           Object Redefinition          *
     ******************************************/

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return name;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object obj )
    {
        if ( obj instanceof IntermediateNode )
        {
            IntermediateNode compared = ( IntermediateNode ) obj;
            if ( compared.getName().equals( this.getName() ) )
            {
                if ( ( compared.getParent() == null ) || ( this.getParent() == null ) )
                    return true;
                if ( compared.getParent().equals( this.getParent() ) )
                    return true;
            }
        }
        return false;
    }


    /**
     * Gets the type of IntermediateNode
     *
     * @return
     *      the type of IntermediateNode
     */
    public IntermediateNodeType getType()
    {
        return type;
    }


    /**
     * Sets the type of IntermediateNode
     *
     * @param type
     *      the type to set
     */
    public void setType( IntermediateNodeType type )
    {
        this.type = type;
    }
}
