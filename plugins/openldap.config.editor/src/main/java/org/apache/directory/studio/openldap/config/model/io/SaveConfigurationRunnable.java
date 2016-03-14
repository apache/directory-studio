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
package org.apache.directory.studio.openldap.config.model.io;


import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.common.core.jobs.StudioRunnableWithProgress;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IEditorInput;

import org.apache.directory.studio.openldap.config.editor.ConnectionServerConfigurationInput;
import org.apache.directory.studio.openldap.config.editor.DirectoryServerConfigurationInput;
import org.apache.directory.studio.openldap.config.editor.OpenLDAPServerConfigurationEditor;
import org.apache.directory.studio.openldap.config.editor.OpenLDAPServerConfigurationEditorUtils;


/**
 * This class implements a {@link Job} that is used to save a server configuration.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SaveConfigurationRunnable implements StudioRunnableWithProgress
{
    /** The associated editor */
    private OpenLDAPServerConfigurationEditor editor;


    /**
     * Creates a new instance of SaveConfigurationRunnable.
     * 
     * @param editor
     *            the editor
     */
    public SaveConfigurationRunnable( OpenLDAPServerConfigurationEditor editor )
    {
        super();
        this.editor = editor;
    }


    /**
     * {@inheritDoc}
     */
    public String getErrorMessage()
    {
        return "Unable to save the configuration.";
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
        return "Save Configuration";
    }


    /**
     * {@inheritDoc}
     */
    public void run( StudioProgressMonitor monitor )
    {
        try
        {
            if ( editor.isDirty() )
            {
                monitor.beginTask( "Saving the server configuration", IProgressMonitor.UNKNOWN );

                IEditorInput input = editor.getEditorInput();
                boolean success = false;

                if ( input instanceof ConnectionServerConfigurationInput )
                {
                    // Saving the ServerConfiguration to the connection
                    OpenLDAPServerConfigurationEditorUtils.saveConfiguration( ( ConnectionServerConfigurationInput ) input,
                        editor, monitor );
                    success = true;
                }
                else if ( input instanceof DirectoryServerConfigurationInput )
                {
                    // Saving the ServerConfiguration to the 'slapd.d' directory
                    OpenLDAPServerConfigurationEditorUtils.saveConfiguration( editor.getConfiguration(),
                        ( ( DirectoryServerConfigurationInput ) input ).getDirectory() );
                    success = true;
                }

                editor.setDirty( !success );
            }
        }
        catch ( Exception e )
        {
            // Reporting the error to the monitor
            monitor.reportError( e );
        }
    }
}
