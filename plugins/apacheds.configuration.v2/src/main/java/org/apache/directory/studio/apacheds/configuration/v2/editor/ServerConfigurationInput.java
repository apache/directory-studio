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
package org.apache.directory.studio.apacheds.configuration.v2.editor;


import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;


/**
 * This class represents the Non Existing Server Configuration Input.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ServerConfigurationInput implements IEditorInput
{
    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getToolTipText()
     */
    public String getToolTipText()
    {
        return getName();
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getName()
     */
    public String getName()
    {
        return "ServerConfigurationInput";
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#exists()
     */
    public boolean exists()
    {
        return true;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
     */
    public ImageDescriptor getImageDescriptor()
    {
        return null;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getPersistable()
     */
    public IPersistableElement getPersistable()
    {
        return null;
    }


    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public Object getAdapter( Class adapter )
    {
        return null;
    }
}
