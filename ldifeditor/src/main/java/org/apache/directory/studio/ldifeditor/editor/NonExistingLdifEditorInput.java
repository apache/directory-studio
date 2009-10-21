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

package org.apache.directory.studio.ldifeditor.editor;


import org.apache.directory.studio.ldifeditor.LdifEditorActivator;
import org.apache.directory.studio.ldifeditor.LdifEditorConstants;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.editors.text.ILocationProvider;


/**
 * This EditorInput is used to create a LDIF file that isn't saved yet.
 * It is used from File->New, but also from the embedded LDIF editors
 * in modification view, in batch operation wizard and the LDIF preference page.
 * 
 * Inspired from org.eclipse.ui.internal.editors.text.NonExistingFileEditorInput.java
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NonExistingLdifEditorInput implements IPathEditorInput, ILocationProvider
{
    /** The counter to create unique names */
    private static int counter = 0;

    /** The name, displayed in Editor tab */
    private String name;


    /**
     * Creates a new instance of NonExistingLdifEditorInput.
     */
    public NonExistingLdifEditorInput()
    {
        counter++;
        name = "LDIF " + counter; //$NON-NLS-1$
    }


    /**
     * As the name says, this implementations always returns false.
     */
    public boolean exists()
    {
        return false;
    }


    /**
     * Returns the LDIF file image.
     */
    public ImageDescriptor getImageDescriptor()
    {
        return LdifEditorActivator.getDefault().getImageDescriptor( LdifEditorConstants.IMG_BROWSER_LDIFEDITOR );
    }


    /**
     * Returns the name.
     */
    public String getName()
    {
        return name;
    }


    /**
     * As the name says, this implementations always returns false.
     */
    public IPersistableElement getPersistable()
    {
        return null;
    }


    /**
     * Returns the name.
     */
    public String getToolTipText()
    {
        return name;
    }


    /**
     * An EditorInput must return a good ILocationProvider, otherwise
     * the editor is not editable.
     */
    public Object getAdapter( Class adapter )
    {
        if ( ILocationProvider.class.equals( adapter ) )
        {
            return this;
        }

        return Platform.getAdapterManager().getAdapter( this, adapter );
    }


    /**
     * This implementation returns a path that point to the plugin's
     * state location. 
     * 
     * A valid, writeable path must be returned, otherwise the editor
     * is not editable.
     */
    public IPath getPath( Object element )
    {
        if ( element instanceof NonExistingLdifEditorInput )
        {
            NonExistingLdifEditorInput input = ( NonExistingLdifEditorInput ) element;
            return input.getPath();
        }

        return null;
    }


    /** 
     * This implemention just compares the names
     */
    public boolean equals( Object o )
    {
        if ( o == this )
        {
            return true;
        }

        if ( o instanceof NonExistingLdifEditorInput )
        {
            NonExistingLdifEditorInput input = ( NonExistingLdifEditorInput ) o;
            return name.equals( input.name );
        }

        return false;
    }


    /**
     * Returns hash code of the name string.
     */
    public int hashCode()
    {
        return name.hashCode();
    }


    /**
     * This implementation returns a path that point to the plugin's
     * state location. The state location is a platform indepentend 
     * location that is writeable.
     * 
     * A valid, writeable path must be returned, otherwise the editor
     * is not editable.
     */
    public IPath getPath()
    {
        return LdifEditorActivator.getDefault().getStateLocation().append( name + ".ldif" ); //$NON-NLS-1$
    }

}
