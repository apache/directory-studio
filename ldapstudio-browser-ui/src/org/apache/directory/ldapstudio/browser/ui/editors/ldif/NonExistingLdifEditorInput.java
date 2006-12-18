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

package org.apache.directory.ldapstudio.browser.ui.editors.ldif;


import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.editors.text.ILocationProvider;


public class NonExistingLdifEditorInput implements IEditorInput, ILocationProvider
{

    private static int counter = 0;

    private String name;


    public NonExistingLdifEditorInput()
    {
        ++counter;
        name = "LDIF " + counter; //$NON-NLS-1$
    }


    public boolean exists()
    {
        return false;
    }


    public ImageDescriptor getImageDescriptor()
    {
        return null;
    }


    public String getName()
    {
        return name;
    }


    public IPersistableElement getPersistable()
    {
        return null;
    }


    public String getToolTipText()
    {
        return name;
    }


    public Object getAdapter( Class adapter )
    {
        if ( ILocationProvider.class.equals( adapter ) )
            return this;
        return Platform.getAdapterManager().getAdapter( this, adapter );
    }


    public IPath getPath( Object element )
    {
        return BrowserUIPlugin.getDefault().getStateLocation().append( name + ".ldif" );
    }


    public boolean equals( Object o )
    {
        if ( o == this )
            return true;

        if ( o instanceof NonExistingLdifEditorInput )
        {
            NonExistingLdifEditorInput input = ( NonExistingLdifEditorInput ) o;
            return name.equals( input.name );
        }

        return false;
    }


    public int hashCode()
    {
        return name.hashCode();
    }
}
