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

package org.apache.directory.studio.openldap.config.jobs;


import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.common.core.jobs.StudioRunnableWithProgress;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;

import org.apache.directory.studio.openldap.config.editor.ConnectionServerConfigurationInput;
import org.apache.directory.studio.openldap.config.editor.DirectoryServerConfigurationInput;
import org.apache.directory.studio.openldap.config.editor.NewServerConfigurationInput;
import org.apache.directory.studio.openldap.config.editor.OpenLdapServerConfigurationEditor;
import org.apache.directory.studio.openldap.config.model.OlcGlobal;
import org.apache.directory.studio.openldap.config.model.OpenLdapConfiguration;
import org.apache.directory.studio.openldap.config.model.io.ConfigurationReader;


/**
 * This class implements a {@link Job} that is used to load a server configuration.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LoadConfigurationRunnable implements StudioRunnableWithProgress
{
    /** The associated editor */
    private OpenLdapServerConfigurationEditor editor;


    /**
     * Creates a new instance of LoadConfigurationRunnable.
     * 
     * @param editor the editor
     */
    public LoadConfigurationRunnable( OpenLdapServerConfigurationEditor editor )
    {
        this.editor = editor;
    }


    /**
     * {@inheritDoc}
     */
    public String getErrorMessage()
    {
        return "Unable to load the configuration.";
    }


    /**
     * {@inheritDoc}
     */
    public Object[] getLockedObjects()
    {
        return new Object[0];
    }


    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return "Load Configuration";
    }


    /**
     * {@inheritDoc}
     */
    public void run( StudioProgressMonitor monitor )
    {
        IEditorInput input = editor.getEditorInput();

        try
        {
            final OpenLdapConfiguration configuration = getConfiguration( input, monitor );
            
            if ( configuration != null )
            {
                Display.getDefault().asyncExec( () -> editor.configurationLoaded( configuration ) );
            }
        }
        catch ( Exception e )
        {
            // Reporting the error to the monitor
            monitor.reportError( e );

            // Reporting the error to the editor
            final Exception exception = e;
            
            Display.getDefault().asyncExec( () -> editor.configurationLoadFailed( exception ) );
        }
    }


    /**
     * Gets the configuration from the input. It may come from an existing connection, 
     * or from an existing file/directory on the disk, or a brand new configuration
     * 
     * @param input the editor input
     * @param monitor the studio progress monitor
     * @return the configuration
     * @throws Exception
     */
    public OpenLdapConfiguration getConfiguration( IEditorInput input, StudioProgressMonitor monitor ) throws Exception
    {
        if ( input instanceof ConnectionServerConfigurationInput )
        {
            // If the input is a ConnectionServerConfigurationInput, then we 
            // read the server configuration from the selected connection
            ConfigurationReader.readConfiguration( ( ConnectionServerConfigurationInput ) input );
        }
        else if ( input instanceof DirectoryServerConfigurationInput )
        {
            // If the input is a DirectoryServerConfigurationInput, then we
            // read the server configuration from the selected 'slapd.d' directory.
            return ConfigurationReader.readConfiguration( ( DirectoryServerConfigurationInput ) input );
        }
        else if ( input instanceof NewServerConfigurationInput )
        {
            // If the input is a NewServerConfigurationInput, then we only 
            // need to create a server configuration and return.
            // The new configuration will be pretty empty, with just
            // the main container (and the olcGlobal instance
            OpenLdapConfiguration configuration = new OpenLdapConfiguration();
            configuration.setGlobal( new OlcGlobal() );
            
            return configuration;
        }

        return null;
    }
}
