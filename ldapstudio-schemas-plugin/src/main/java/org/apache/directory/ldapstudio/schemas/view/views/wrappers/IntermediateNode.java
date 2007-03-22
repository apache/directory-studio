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

package org.apache.directory.ldapstudio.schemas.view.views.wrappers;


import org.apache.directory.ldapstudio.schemas.Activator;
import org.apache.directory.ldapstudio.schemas.PluginConstants;
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
public class IntermediateNode extends TreeNode
{
    /** This enum represent the different types of IntermediateNodes */
    public enum IntermediateNodeType
    {
        NONE, OBJECT_CLASS_FOLDER, ATTRIBUTE_TYPE_FOLDER
    }

    /** The name */
    private String name;

    /** The type */
    private IntermediateNodeType type;


    /**
     * Default constructor
     * @param name the name that will be displayed in the tree viewer
     * @param parent the parent DisplayableTreeElement in the schema relationship
     * hierarchy
     */
    public IntermediateNode( String name, ITreeNode parent )
    {
        super( parent );
        this.name = name;
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
    public IntermediateNode( String name, ITreeNode parent, IntermediateNodeType type )
    {
        super( parent );
        this.name = name;
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


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.TreeNode#getImage()
     */
    public Image getImage()
    {
        switch ( type )
        {
            case NONE:
                String imageKey = ISharedImages.IMG_OBJ_FOLDER;
                return PlatformUI.getWorkbench().getSharedImages().getImage( imageKey );
            case ATTRIBUTE_TYPE_FOLDER:
                return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                    PluginConstants.IMG_FOLDER_ATTRIBUTE_TYPE ).createImage();
            case OBJECT_CLASS_FOLDER:
                return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                    PluginConstants.IMG_FOLDER_OBJECT_CLASS ).createImage();
        }

        String imageKey = ISharedImages.IMG_OBJ_FOLDER;
        return PlatformUI.getWorkbench().getSharedImages().getImage( imageKey );
    }


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
