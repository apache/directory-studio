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

package org.apache.directory.studio.actions;


import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IPersistableElement;


/**
 * This IEditorInput is used to open files that are located in the local file system.
 * 
 * Inspired from org.eclipse.ui.internal.editors.text.NonExistingFileEditorInput.java
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class PathEditorInput implements IPathEditorInput
{

    /** The absolute path in local file system */
    private IPath path;

    /** The image for the input file */
    private ImageDescriptor imageDescriptor;


    /**
     * 
     * Creates a new instance of PathEditorInput.
     *
     * @param path the absolute path
     * @param imageDescriptor the image for the input file
     */
    public PathEditorInput( IPath path, ImageDescriptor imageDescriptor )
    {
        if ( path == null )
        {
            throw new IllegalArgumentException();
        }

        this.path = path;
        this.imageDescriptor = imageDescriptor;
    }


    /**
     * Returns hash code of the path.
     */
    public int hashCode()
    {
        return path.hashCode();
    }


    /** 
     * This implemention just compares the paths
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


    /**
     * {@inheritDoc}
     */
    public boolean exists()
    {
        return path.toFile().exists();
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return imageDescriptor;
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
     * {@inheritDoc}
     */
    public IPath getPath()
    {
        return path;
    }


    /**
     * {@inheritDoc}
     */
    public Object getAdapter( Class adapter )
    {
        return Platform.getAdapterManager().getAdapter( this, adapter );
    }


    /**
     * {@inheritDoc}
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
