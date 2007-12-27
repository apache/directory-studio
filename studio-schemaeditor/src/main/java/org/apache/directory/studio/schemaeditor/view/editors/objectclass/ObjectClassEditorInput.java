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


import org.apache.directory.studio.schemaeditor.model.ObjectClassImpl;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;


/**
 * This class is the Input class for the Object Class Editor
 */
public class ObjectClassEditorInput implements IEditorInput
{
    private ObjectClassImpl objectClass;


    /**
     * Default constructor.
     * 
     * @param obj
     *      the object class
     */
    public ObjectClassEditorInput( ObjectClassImpl obj )
    {
        super();
        objectClass = obj;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#exists()
     */
    public boolean exists()
    {
        return ( objectClass == null );
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
        String[] names = objectClass.getNames();
        if ( ( names != null ) && ( names.length > 0 ) )
        {
            return names[0];
        }
        else
        {
            return objectClass.getOid();
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
        return getName() + " from the " + objectClass.getSchema() + " schema";
    }


    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
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
    public ObjectClassImpl getObjectClass()
    {
        return this.objectClass;
    }
}
