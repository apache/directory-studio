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
package org.apache.directory.studio.apacheds.configuration.editor;


import org.apache.directory.studio.apacheds.configuration.model.ServerConfiguration;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;


/**
 * This class represents the Non Existing Server Configuration Input.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NonExistingServerConfigurationInput implements IEditorInput
{
    /** The Server Configuration */
    private ServerConfiguration serverConfiguration;


    /**
     * Creates a new instance of NonExistingServerConfigurationInput.
     *
     * @param serverConfiguration
     *      the Server Configuration
     */
    public NonExistingServerConfigurationInput( ServerConfiguration serverConfiguration )
    {
        this.serverConfiguration = serverConfiguration;
    }


    /**
     * Gets the Server Configuration
     *
     * @return
     *      the Server Configuration
     */
    public ServerConfiguration getServerConfiguration()
    {
        return serverConfiguration;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getToolTipText()
     */
    public String getToolTipText()
    {
        return getNameOrToolTipText();
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getName()
     */
    public String getName()
    {
        return getNameOrToolTipText();
    }


    /**
     * Returns the name or tooltip text.
     *
     * @return
     *      the name or tooltip text
     */
    private String getNameOrToolTipText()
    {
        switch ( serverConfiguration.getVersion() )
        {
            case VERSION_1_5_4:
                return "New Apache DS 1.5.4 Configuration File";
            case VERSION_1_5_3:
                return "New Apache DS 1.5.3 Configuration File";
            case VERSION_1_5_2:
                return "New Apache DS 1.5.2 Configuration File";
            case VERSION_1_5_1:
                return "New Apache DS 1.5.1 Configuration File";
            case VERSION_1_5_0:
                return "New Apache DS 1.5.0 Configuration File";
            default:
                return "New Apache DS Configuration File";
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#exists()
     */
    public boolean exists()
    {
        return ( serverConfiguration != null );
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


    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object obj )
    {
        if ( obj == null )
        {
            return false;
        }

        if ( obj instanceof NonExistingServerConfigurationInput )
        {
            NonExistingServerConfigurationInput input = ( NonExistingServerConfigurationInput ) obj;
            if ( input.exists() && exists() )
            {
                ServerConfiguration inputServerConfiguration = input.getServerConfiguration();

                if ( ( inputServerConfiguration != null ) && ( serverConfiguration != null ) )
                {
                    return inputServerConfiguration.equals( serverConfiguration );
                }
            }
        }

        return false;
    }
}
