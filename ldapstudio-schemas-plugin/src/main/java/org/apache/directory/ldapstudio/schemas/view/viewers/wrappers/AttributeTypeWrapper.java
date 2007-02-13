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
 * This class is used to represent an attribute type in a tree viewer.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class AttributeTypeWrapper implements DisplayableTreeElement
{
    /** The parent element */
    private DisplayableTreeElement parent;

    /** The associated attribute type */
    private AttributeType myAttributeType;


    /**
     * Creates a new instance of AttributeTypeWrapper.
     *
     * @param myAttributeType
     *      the associated attribute type
     * @param parent
     *      the parent element
     */
    public AttributeTypeWrapper( AttributeType myAttributeType, DisplayableTreeElement parent )
    {
        this.myAttributeType = myAttributeType;
        this.parent = parent;
    }


    /**
     * Gets the name of the associated attribute type.
     * 
     * @return
     *      the name of the associated attribute type
     */
    public String getName()
    {
        return myAttributeType.getNames()[0];
    }


    /**
     * Gets the OID of the associated attribute type.
     * 
     * @return
     *      the OID of the associated attribute type
     */
    public String getOid()
    {
        return myAttributeType.getOid();
    }


    /**
     * Gets the associated attribute type.
     * 
     * @return 
     *      the associated attribute type
     */
    public AttributeType getMyAttributeType()
    {
        return myAttributeType;
    }


    /**
     * Gets the parent element.
     * 
     * @return
     *      the parent element
     */
    public DisplayableTreeElement getParent()
    {
        return parent;
    }


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
