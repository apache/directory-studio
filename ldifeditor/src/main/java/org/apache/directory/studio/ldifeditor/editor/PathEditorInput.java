/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.apache.directory.studio.ldifeditor.editor;


import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.ILocationProvider;


/**
 * EditorInput that stores a path.
 */

/**
 * This EditorInput is used to open LDIF files that are located in the local file system.
 * 
 * Inspired from org.eclipse.ui.internal.editors.text.NonExistingFileEditorInput.java
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class PathEditorInput implements IPathEditorInput, ILocationProvider
{

    /** The absolute path in local file system */
    private IPath path;


    /**
     * 
     * Creates a new instance of PathEditorInput.
     *
     * @param path the absolute path
     */
    public PathEditorInput( IPath path )
    {
        if ( path == null )
        {
            throw new IllegalArgumentException();
        }

        this.path = path;
    }


    /**
     * Returns hash code of the path.
     */
    public int hashCode()
    {
        return path.hashCode();
    }


    /** 
     * This implemention just compares the names
     */
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o instanceof PathEditorInput )
        {
            PathEditorInput input = ( PathEditorInput ) o;
            return path.equals( input.path );
        }

        return false;
    }


    /*
     * @see org.eclipse.ui.IEditorInput#exists()
     */
    public boolean exists()
    {
        return path.toFile().exists();
    }


    /**
     * Returns the LDIF file image.
     */
    public ImageDescriptor getImageDescriptor()
    {
        return PlatformUI.getWorkbench().getEditorRegistry().getImageDescriptor( path.toString() );
    }


    /**
     * Returns the file name only.
     */
    public String getName()
    {
        return path.toFile().getName();
        //return path.toString();
    }


    /**
     * Returns the complete path. 
     */
    public String getToolTipText()
    {
        return path.makeRelative().toOSString();
    }


    /**
     * Returns the path. 
     */
    public IPath getPath()
    {
        return path;
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


    /*
     * @see org.eclipse.ui.IEditorInput#getPersistable()
     */
    public IPersistableElement getPersistable()
    {
        return null;
    }


    /**
     * Returns the path.
     */
    public IPath getPath( Object element )
    {
        if ( element instanceof PathEditorInput )
        {
            PathEditorInput input = ( PathEditorInput ) element;
            return input.getPath();
        }

        return null;
    }
}
