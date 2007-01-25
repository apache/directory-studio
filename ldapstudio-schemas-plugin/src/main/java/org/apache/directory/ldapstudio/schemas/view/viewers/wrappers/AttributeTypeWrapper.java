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


import org.apache.directory.ldapstudio.schemas.controller.Application;
import org.apache.directory.ldapstudio.schemas.model.AttributeType;
import org.apache.directory.ldapstudio.schemas.view.IImageKeys;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * Nasty trick to display object-classes attributes in the tree-viewer
 */
public class AttributeTypeWrapper implements DisplayableTreeElement
{
    /******************************************
     *               Fields                   *
     ******************************************/

    private IntermediateNode parent;
    private AttributeType myAttributeType;


    /******************************************
     *              Constructors              *
     ******************************************/

    /**
     * Default constructor
     * @param myAttributeType
     * @param parent
     */
    public AttributeTypeWrapper( AttributeType myAttributeType, IntermediateNode parent )
    {
        this.myAttributeType = myAttributeType;
        this.parent = parent;
    }


    /******************************************
     *             Wrapper Methods            *
     ******************************************/

    /**
     * @return the name of the wrapped attribute type
     */
    public String getName()
    {
        return myAttributeType.getNames()[0];
    }


    /**
     * @return the OID of the wrapped attribute type
     */
    public String getOid()
    {
        return myAttributeType.getOid();
    }


    /******************************************
     *               Accessors                *
     ******************************************/

    /**
     * @return the wrapped attribute type
     */
    public AttributeType getMyAttributeType()
    {
        return myAttributeType;
    }


    /**
     * @return the parent element
     */
    public IntermediateNode getParent()
    {
        return parent;
    }


    /******************************************
     *       DisplayableTreeElement Impl.     *
     ******************************************/

    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.DisplayableTreeElement#getDisplayImage()
     */
    public Image getDisplayImage()
    {
        return AbstractUIPlugin.imageDescriptorFromPlugin( Application.PLUGIN_ID, IImageKeys.ATTRIBUTE_TYPE )
            .createImage();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.DisplayableTreeElement#getDisplayName()
     */
    public String getDisplayName()
    {
        return getName() + "  [" + myAttributeType.getOriginatingSchema().getName() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
    }


    /******************************************
     *           Object Redefinition          *
     ******************************************/
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object obj )
    {
        if ( obj instanceof AttributeTypeWrapper )
        {
            AttributeTypeWrapper compared = ( AttributeTypeWrapper ) obj;
            return compared.getOid().equals( this.getOid() );
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return myAttributeType + " wrapper"; //$NON-NLS-1$
    }
}
