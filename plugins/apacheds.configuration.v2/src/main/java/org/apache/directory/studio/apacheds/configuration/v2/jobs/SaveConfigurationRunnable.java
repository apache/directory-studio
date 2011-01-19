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

package org.apache.directory.studio.apacheds.configuration.v2.jobs;


import org.apache.directory.studio.apacheds.configuration.v2.editor.ConnectionServerConfigurationInput;
import org.apache.directory.studio.apacheds.configuration.v2.editor.NewServerConfigurationInput;
import org.apache.directory.studio.apacheds.configuration.v2.editor.ServerConfigurationEditor;
import org.apache.directory.studio.apacheds.configuration.v2.editor.ServerConfigurationEditorUtils;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.common.core.jobs.StudioRunnableWithProgress;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.part.FileEditorInput;


/**
 * This class implements a {@link Job} that is used to save a server configuration.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SaveConfigurationRunnable implements StudioRunnableWithProgress
{
    /** The associated editor */
    private ServerConfigurationEditor editor;


    /**
     * Creates a new instance of SaveConfigurationRunnable.
     * 
     * @param editor
     *            the editor
     */
    public SaveConfigurationRunnable( ServerConfigurationEditor editor )
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
                String inputClassName = input.getClass().getName();
                boolean success = false;
                if ( input instanceof FileEditorInput )
                // FileEditorInput class is used when the file is opened
                // from a project in the workspace.
                {
                    // Saving the ServerConfiguration to disk
                    ServerConfigurationEditorUtils.saveConfiguration( ( FileEditorInput ) input,
                        editor.getConfigWriter(),
                        monitor );
                    success = true;
                }
                // If the input is a ConnectionServerConfigurationInput, then we 
                // read the server configuration from the selected connection
                if ( input instanceof ConnectionServerConfigurationInput )
                {
                    // Saving the ServerConfiguration to the connection
                    ServerConfigurationEditorUtils.saveConfiguration( ( ConnectionServerConfigurationInput ) input,
                        editor.getConfigWriter(), monitor );
                    success = true;
                }
                else if ( input instanceof IPathEditorInput )
                {
                    // Saving the ServerConfiguration to disk
                    ServerConfigurationEditorUtils
                        .saveConfiguration( ( ( IPathEditorInput ) input ).getPath().toFile(), editor.getConfigWriter() );
                    success = true;
                }
                else if ( inputClassName.equals( "org.eclipse.ui.internal.editors.text.JavaFileEditorInput" ) //$NON-NLS-1$
                    || inputClassName.equals( "org.eclipse.ui.ide.FileStoreEditorInput" ) ) //$NON-NLS-1$
                // The class 'org.eclipse.ui.internal.editors.text.JavaFileEditorInput'
                // is used when opening a file from the menu File > Open... in Eclipse 3.2.x
                // The class 'org.eclipse.ui.ide.FileStoreEditorInput' is used when
                // opening a file from the menu File > Open... in Eclipse 3.3.x
                {
                    // Saving the ServerConfiguration to disk
                    ServerConfigurationEditorUtils.saveConfiguration( input.getToolTipText(), editor.getConfigWriter() );
                    success = true;
                }
                else if ( input instanceof NewServerConfigurationInput )
                {
                    // The 'ServerConfigurationEditorInput' class is used when a
                    // new Server Configuration File is created.

                    // We are saving this as if it is a "Save as..." action.
                    success = editor.doSaveAs( monitor );
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
