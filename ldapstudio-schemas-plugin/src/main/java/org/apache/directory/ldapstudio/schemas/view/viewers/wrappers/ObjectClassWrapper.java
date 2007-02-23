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


import org.apache.directory.ldapstudio.schemas.Activator;
import org.apache.directory.ldapstudio.schemas.model.ObjectClass;
import org.apache.directory.ldapstudio.schemas.view.IImageKeys;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class is used to display an object class in a tree viewer. 
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ObjectClassWrapper implements DisplayableTreeElement
{
    /**
     * This enum represent the different states of an ObjectClassWrapper
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    public enum State
    {
        resolved, unResolved
    }

    /** The state */
    private State state;

    /** The parent element */
    private DisplayableTreeElement parent;

    /** The associated object class */
    private ObjectClass myObjectClass;


    /**
     * Creates a new instance of ObjectClassWrapper.
     *
     * @param myObjectClass
     *      the associated object class
     * @param parent
     *      the parent element
     */
    public ObjectClassWrapper( ObjectClass myObjectClass, DisplayableTreeElement parent )
    {
        this.parent = parent;
        this.myObjectClass = myObjectClass;
        this.state = State.resolved;
    }


    /**
     * Gets the associated object class
     * 
     * @return 
     *      the associated object class
     */
    public ObjectClass getMyObjectClass()
    {
        return myObjectClass;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.DisplayableTreeElement#getParent()
     */
    public DisplayableTreeElement getParent()
    {
        return parent;
    }


    /**
     * Gets the state of the object class wrapper.
     * 
     * @return
     *      the state of the object class wrapper
     */
    public State getState()
    {
        return state;
    }


    /**
     * Sets the state of the object class wrapper.
     * 
     * @param state
     *      the state of the object class wrapper
     */
    public void setState( State state )
    {
        this.state = state;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.DisplayableTreeElement#getDisplayImage()
     */
    public Image getDisplayImage()
    {
        String imageKey = ISharedImages.IMG_OBJS_WARN_TSK;

        if ( state == State.resolved )
            return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, IImageKeys.OBJECT_CLASS )
                .createImage();
        else if ( state == State.unResolved )
            return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, IImageKeys.OBJECT_CLASS_WARNING )
                .createImage();

        return PlatformUI.getWorkbench().getSharedImages().getImage( imageKey );
    }


    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object obj )
    {
        if ( obj instanceof ObjectClassWrapper )
        {
            ObjectClassWrapper compared = ( ObjectClassWrapper ) obj;
            return compared.getMyObjectClass().getOid().equals( this.getMyObjectClass().getOid() );
        }
        return false;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return myObjectClass + " wrapper"; //$NON-NLS-1$
    }
}
