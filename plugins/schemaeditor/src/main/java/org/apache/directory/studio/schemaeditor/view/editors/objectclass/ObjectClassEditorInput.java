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

package org.apache.directory.studio.schemaeditor.view.editors.objectclass;


import java.util.List;

import org.apache.directory.api.ldap.model.schema.MutableObjectClass;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;


/**
 * This class is the Input class for the Object Class Editor
 */
public class ObjectClassEditorInput implements IEditorInput
{
    private MutableObjectClass objectClass;


    /**
     * Default constructor.
     * 
     * @param obj
     *      the object class
     */
    public ObjectClassEditorInput( MutableObjectClass obj )
    {
        super();
        objectClass = obj;
    }


    /**
     * {@inheritDoc}
     */
    public boolean exists()
    {
        return ( objectClass == null );
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
        List<String> names = objectClass.getNames();
        if ( ( names != null ) && ( names.size() > 0 ) )
        {
            return names.get( 0 );
        }
        else
        {
            return objectClass.getOid();
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
        return NLS.bind( Messages.getString( "ObjectClassEditorInput.FromSchema" ), new String[] //$NON-NLS-1$
            { getName(), objectClass.getSchemaName() } );
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
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
            return true;
        if ( !( obj instanceof ObjectClassEditorInput ) )
            return false;
        ObjectClassEditorInput other = ( ObjectClassEditorInput ) obj;
        return other.getObjectClass().equals( this.objectClass );
    }


    /**
     * Returns the input object class
     * 
     * @return
     *      the input object class
     */
    public MutableObjectClass getObjectClass()
    {
        return this.objectClass;
    }
}
