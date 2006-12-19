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

package org.apache.directory.ldapstudio.browser.ui.editors.entry;


import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;


public class EntryEditorInput implements IEditorInput
{

    private IEntry entry;


    public EntryEditorInput( IEntry entry )
    {
        this.entry = entry;
    }


    public boolean exists()
    {
        return false;
    }


    public ImageDescriptor getImageDescriptor()
    {
        return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_ATTRIBUTE );
    }


    public String getName()
    {
        return "Entry Editor";
    }


    public String getToolTipText()
    {
        return this.entry != null ? this.entry.getDn().toString() : "";
    }


    public IPersistableElement getPersistable()
    {
        return null;
    }


    public Object getAdapter( Class adapter )
    {
        return null;
    }


    public IEntry getEntry()
    {
        return entry;
    }


    public boolean equals( Object obj )
    {
        return obj instanceof EntryEditorInput;
    }

}
