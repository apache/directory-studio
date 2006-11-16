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
import org.apache.directory.ldapstudio.schemas.model.ObjectClass;
import org.apache.directory.ldapstudio.schemas.view.IImageKeys;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * Nasty trick to display object class in the tree-viewer
 */
public class ObjectClassWrapper implements DisplayableTreeElement
{
    /******************************************
     *               Fields                   *
     ******************************************/
    public enum State
    {
        resolved, unResolved
    };

    private State state;
    private IntermediateNode parent;
    private ObjectClass myObjectClass;


    /******************************************
     *              Constructors              *
     ******************************************/

    /**
     * Default constructor
     * @param parent
     * @param myObjectClass
     */
    public ObjectClassWrapper( ObjectClass myObjectClass, IntermediateNode parent )
    {
        this.parent = parent;
        this.myObjectClass = myObjectClass;
        this.state = State.resolved;
    }


    /******************************************
     *             Wrapper Methods            *
     ******************************************/

    /**
     * @return the names of the wrapped object class
     */
    public String[] getNames()
    {
        return myObjectClass.getNames();
    }


    /**
     * @return the oid of the wrapped object class
     */
    public String getOid()
    {
        return myObjectClass.getOid();
    }


    /******************************************
     *               Accessors                *
     ******************************************/

    /**
     * @return the wrapped object class
     */
    public ObjectClass getMyObjectClass()
    {
        return myObjectClass;
    }


    /**
     * @return the parent element
     */
    public IntermediateNode getParent()
    {
        return parent;
    }


    /**
     * @return the state of the wrapped object class
     */
    public State getState()
    {
        return state;
    }


    /**
     * Sets the state of the wrapped object class
     * @param state
     */
    public void setState( State state )
    {
        this.state = state;
    }


    /******************************************
     *       DisplayableTreeElement Impl.     *
     ******************************************/

    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.DisplayableTreeElement#getDisplayImage()
     */
    public Image getDisplayImage()
    {
        String imageKey = ISharedImages.IMG_OBJS_WARN_TSK;

        if ( state == State.resolved )
            return AbstractUIPlugin.imageDescriptorFromPlugin( Application.PLUGIN_ID, IImageKeys.OBJECT_CLASS )
                .createImage();
        else if ( state == State.unResolved )
            return AbstractUIPlugin.imageDescriptorFromPlugin( Application.PLUGIN_ID, IImageKeys.OBJECT_CLASS_WARNING )
                .createImage();

        return PlatformUI.getWorkbench().getSharedImages().getImage( imageKey );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.DisplayableTreeElement#getDisplayName()
     */
    public String getDisplayName()
    {
        return getNames()[0] + "  [" + myObjectClass.getOriginatingSchema().getName() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
    }


    /******************************************
     *           Object Redefinition          *
     ******************************************/

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object obj )
    {
        if ( obj instanceof ObjectClassWrapper )
        {
            ObjectClassWrapper compared = ( ObjectClassWrapper ) obj;
            return compared.getOid().equals( this.getOid() );
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
