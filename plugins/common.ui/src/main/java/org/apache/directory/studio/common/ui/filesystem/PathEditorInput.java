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

package org.apache.directory.studio.common.ui.filesystem;


import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.ILocationProvider;


/**
 * This class defines an editor input based on the local file system path of a file.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PathEditorInput implements IPathEditorInput, ILocationProvider
{
    /** The path */
    private IPath path;


    /**
     * Creates a new instance of PathEditorInput.
     *
     * @param path the path
     */
    public PathEditorInput( IPath path )
    {
        this.path = path;
    }


    /**
     * {@inheritDoc}
     */
    public boolean exists()
    {
        if ( path != null )
        {
            return path.toFile().exists();
        }

        return false;
    }


    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return PlatformUI.getWorkbench().getEditorRegistry().getImageDescriptor( path.toString() );
    }


    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        if ( path != null )
        {
            return path.toFile().getName();
        }

        return "";
    }


    /**
     * {@inheritDoc}
     */
    public IPath getPath()
    {
        if ( path != null )
        {
            return path;
        }

        return null;
    }


    /**
     * {@inheritDoc}
     */
    public IPath getPath( Object element )
    {
        if ( element instanceof PathEditorInput )
        {
            return ( ( PathEditorInput ) element ).getPath();
        }

        return null;
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
        if ( path != null )
        {
            return path.makeRelative().toOSString();
        }

        return "";
    }


    /**
     * {@inheritDoc}
     */
    public int hashCode()
    {
        if ( path != null )
        {
            return path.hashCode();
        }

        return super.hashCode();
    }


    /** 
     * {@inheritDoc}
     */
    public boolean equals( Object o )
    {
        if ( path != null )
        {
            // Shortcut
            if ( this == o )
            {
                return true;
            }

            if ( o instanceof PathEditorInput )
            {
                PathEditorInput input = ( PathEditorInput ) o;

                return path.equals( input.path );
            }
        }

        return super.equals( o );
    }
}
