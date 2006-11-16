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


import org.apache.directory.ldapstudio.schemas.model.Schema;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;


/**
 * This class is the Input class for the Schema Editor
 */
public class SchemaFormEditorInput implements IEditorInput
{
    private Schema schema;


    /**
     * Default constructor
     * @param schema
     */
    public SchemaFormEditorInput( Schema schema )
    {
        super();
        this.schema = schema;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#exists()
     */
    public boolean exists()
    {
        return ( this.schema == null );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
     */
    public ImageDescriptor getImageDescriptor()
    {
        return null;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getName()
     */
    public String getName()
    {
        return this.schema.getName();
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
        return Messages.getString( "SchemaFormEditorInput.Source_code_of" ) + this.schema.getName(); //$NON-NLS-1$
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
            return true;
        if ( !( obj instanceof SchemaFormEditorInput ) )
            return false;
        SchemaFormEditorInput other = ( SchemaFormEditorInput ) obj;
        return other.getSchema().getName().equals( this.schema.getName() );
    }


    /**
     * Returns the input schema
     * @return the input schema
     */
    public Schema getSchema()
    {
        return this.schema;
    }
}
