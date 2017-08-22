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
package org.apache.directory.studio.openldap.config.editor;


import java.io.File;

import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPlugin;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPluginConstants;
import org.eclipse.jface.resource.ImageDescriptor;


/**
 * This class represents the Directory Server Configuration Input.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DirectoryServerConfigurationInput extends AbstractServerConfigurationInput
{
    /** The directory */
    private File directory;


    /**
     * Creates a new instance of DirectoryServerConfigurationInput.
     *
     * @param directory
     *      the directory
     */
    public DirectoryServerConfigurationInput( File directory )
    {
        this.directory = directory;
    }


    /**
     * Gets the directory.
     *
     * @return
     *      the directory
     */
    public File getDirectory()
    {
        return directory;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getToolTipText()
    {
        if ( directory != null )
        {
            return directory.toString();
        }

        return getName();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getName()
    {
        if ( directory != null )
        {
            return directory.getName();
        }

        return "OpenLDAP Configuration";
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists()
    {
        return directory != null;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ImageDescriptor getImageDescriptor()
    {
        return OpenLdapConfigurationPlugin.getDefault().getImageDescriptor(
            OpenLdapConfigurationPluginConstants.IMG_EDITOR );
    }


    /**
     * {@inheritDoc}
     */
    public boolean equals( Object obj )
    {
        if ( obj == null )
        {
            return false;
        }

        if ( obj instanceof DirectoryServerConfigurationInput )
        {
            DirectoryServerConfigurationInput input = ( DirectoryServerConfigurationInput ) obj;

            if ( input.exists() && exists() )
            {
                File inputDirectory = input.getDirectory();

                if ( inputDirectory != null )
                {
                    return inputDirectory.equals( directory );
                }
            }
        }

        return false;
    }


    /**
     * {@inheritDoc}
     */
    public int hashCode()
    {
        if ( directory != null )
        {
            return directory.hashCode();
        }

        return super.hashCode();
    }
}
