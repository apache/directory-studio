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

package org.apache.directory.ldapstudio.schemas.view.editors;


import org.apache.directory.ldapstudio.schemas.model.AttributeType;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;


/**
 * This class is the Input class for the Attribute Type Editor
 */
public class AttributeTypeFormEditorInput implements IEditorInput
{
    private AttributeType attributeType = null;


    /**
     * Default constructor
     * 
     * @param at
     *            the input attribute type
     */
    public AttributeTypeFormEditorInput( AttributeType at )
    {
        super();
        this.attributeType = at;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IEditorInput#exists()
     */
    public boolean exists()
    {
        return ( this.attributeType == null );
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
     */
    public ImageDescriptor getImageDescriptor()
    {
        // TODO Auto-generated method stub
        return null;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IEditorInput#getName()
     */
    public String getName()
    {
        return this.attributeType.getNames()[0];
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IEditorInput#getPersistable()
     */
    public IPersistableElement getPersistable()
    {
        // TODO Auto-generated method stub
        return null;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IEditorInput#getToolTipText()
     */
    public String getToolTipText()
    {
        return this.attributeType.getNames()[0]
            + Messages.getString( "AttributeTypeFormEditorInput.In_the" ) //$NON-NLS-1$
            + this.attributeType.getOriginatingSchema().getName()
            + Messages.getString( "AttributeTypeFormEditorInput.Schema" ); //$NON-NLS-1$
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    public Object getAdapter( Class adapter )
    {
        return null;
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( !( obj instanceof AttributeTypeFormEditorInput ) )
            return false;
        AttributeTypeFormEditorInput other = ( AttributeTypeFormEditorInput ) obj;
        return other.getAttributeType().getOid().equals( this.attributeType.getOid() );
    }


    /**
     * Returns the input Attribute Type
     * 
     * @return the input Attribute Type
     */
    public AttributeType getAttributeType()
    {
        return this.attributeType;
    }
}
