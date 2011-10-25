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

package org.apache.directory.studio.schemaeditor.view.editors.attributetype;


import java.util.List;

import org.apache.directory.shared.ldap.model.schema.AttributeType;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;


/**
 * This class is the Input class for the Attribute Type Editor
 */
public class AttributeTypeEditorInput implements IEditorInput
{
    /** The input attribute type */
    private AttributeType attributeType;


    /**
     * Default constructor
     * 
     * @param at
     *      the input attribute type
     */
    public AttributeTypeEditorInput( AttributeType at )
    {
        attributeType = at;
    }


    /**
     * {@inheritDoc}
     */
    public boolean exists()
    {
        return ( this.attributeType == null );
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        List<String> names = attributeType.getNames();
        if ( ( names != null ) && ( names.size() > 0 ) )
        {
            return names.get( 0 );
        }
        else
        {
            return attributeType.getOid();
        }
    }


    /**
     * {@inheritDoc}
     */
    public IPersistableElement getPersistable()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public String getToolTipText()
    {
        return NLS.bind( Messages.getString( "AttributeTypeEditorInput.FromSchema" ), new String[] //$NON-NLS-1$
            { getName(), attributeType.getSchemaName() } );
    }


    /**
     * {@inheritDoc}
     */
    public Object getAdapter( Class adapter )
    {
        return null;
    }


    /**
     * {@inheritDoc}
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
        return other.getAttributeType().equals( this.attributeType );
    }


    /**
     * Returns the input Attribute Type.
     * 
     * @return
     *      the input Attribute Type
     */
    public AttributeType getAttributeType()
    {
        return this.attributeType;
    }
}
