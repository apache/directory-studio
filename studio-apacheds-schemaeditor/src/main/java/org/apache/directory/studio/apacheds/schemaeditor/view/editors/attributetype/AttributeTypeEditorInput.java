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

package org.apache.directory.studio.apacheds.schemaeditor.view.editors.attributetype;


import org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;


/**
 * This class is the Input class for the Attribute Type Editor
 */
public class AttributeTypeEditorInput implements IEditorInput
{
    /** The input attribute type */
    private AttributeTypeImpl attributeType;


    /**
     * Default constructor
     * 
     * @param at
     *      the input attribute type
     */
    public AttributeTypeEditorInput( AttributeTypeImpl at )
    {
        attributeType = at;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#exists()
     */
    public boolean exists()
    {
        return ( this.attributeType == null );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
     */
    public ImageDescriptor getImageDescriptor()
    {
        return null;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IEditorInput#getName()
     */
    public String getName()
    {
        if ( attributeType.getNames().length != 0 )
        {
            return attributeType.getNames()[0];
        }
        else
        {
            return attributeType.getOid();
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getPersistable()
     */
    public IPersistableElement getPersistable()
    {
        return null;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getToolTipText()
     */
    public String getToolTipText()
    {
        return getName() + " from the " + attributeType.getSchema() + " schema.";
    }


    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    public Object getAdapter( Class adapter )
    {
        return null;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {

            return true;
        }
        else if ( !( obj instanceof AttributeTypeEditorInput ) )
        {
            return false;
        }

        AttributeTypeEditorInput other = ( AttributeTypeEditorInput ) obj;
        return other.getAttributeType().getOid().equals( this.attributeType.getOid() );
    }


    /**
     * Returns the input Attribute Type.
     * 
     * @return
     *      the input Attribute Type
     */
    public AttributeTypeImpl getAttributeType()
    {
        return this.attributeType;
    }
}
