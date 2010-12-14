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


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.directory.server.config.ConfigPartitionReader;
import org.apache.directory.server.config.ReadOnlyConfigurationPartition;
import org.apache.directory.server.config.beans.ConfigBean;
import org.apache.directory.shared.ldap.schema.SchemaManager;
import org.apache.directory.studio.apacheds.configuration.v2.ApacheDS2ConfigurationPlugin;
import org.apache.directory.studio.apacheds.configuration.v2.editor.NewServerConfigurationInput;
import org.apache.directory.studio.apacheds.configuration.v2.editor.ServerConfigurationEditor;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.common.core.jobs.StudioRunnableWithProgress;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.part.FileEditorInput;


/**
 * This class implements a {@link Job} that is used to delete an LDAP Server.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LoadConfigurationRunnable implements StudioRunnableWithProgress
{
    /** The associated editor */
    private ServerConfigurationEditor editor;


    /**
     * Creates a new instance of StartLdapServerRunnable.
     * 
     * @param server
     *            the LDAP Server
     */
    public LoadConfigurationRunnable( ServerConfigurationEditor editor )
    {
        super();
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
        return "Load configuration";
    }


    /**
     * {@inheritDoc}
     */
    public void run( StudioProgressMonitor monitor )
    {
        IEditorInput input = editor.getEditorInput();

        ConfigBean configBean = null;
        try
        {
            configBean = getConfiguration( input );
            if ( configBean != null )
            {
                final ConfigBean finalConfigBean = configBean;

                Display.getDefault().asyncExec( new Runnable()
                {
                    public void run()
                {
                    editor.configurationLoaded( finalConfigBean );
                }
                } );
            }
        }
        catch ( Exception e )
        {
            // Reporting the error to the monitor
            monitor.reportError( e );
        }
    }


    /**
     * Gets a new default configuration.
     *
     * @return
     *      a new default configuration
     * @throws Exception
     */
    public ConfigBean getConfiguration( IEditorInput input ) throws Exception
    {
        // Getting the schema manager
        SchemaManager schemaManager = ApacheDS2ConfigurationPlugin.getDefault().getSchemaManager();

        // Getting an input stream to the configuration
        InputStream is = getInputStream( input );
        if ( is != null )
        {
            // Creating a partition associated from the input stream
            ReadOnlyConfigurationPartition configurationPartition = new ReadOnlyConfigurationPartition(
                ApacheDS2ConfigurationPlugin.class.getResourceAsStream( "config.ldif" ), schemaManager );
            configurationPartition.initialize();

            // Reading the configuration partition
            ConfigPartitionReader cpReader = new ConfigPartitionReader( configurationPartition );
            return cpReader.readConfig();
        }

        return null;
    }


    /**
     * Gets an input stream from the editor input.
     *
     * @param input
     *      the editor input
     * @return
     *      an input stream from the editor input, or <code>null</code>
     * @throws CoreException
     * @throws FileNotFoundException
     */
    private InputStream getInputStream( IEditorInput input ) throws CoreException, FileNotFoundException
    {
        String inputClassName = input.getClass().getName();
        // If the input is a NewServerConfigurationInput, then we only 
        // need to get the server configuration and return
        if ( input instanceof NewServerConfigurationInput )
        {
            return ApacheDS2ConfigurationPlugin.class.getResourceAsStream( "config.ldif" );
        }
        else if ( input instanceof FileEditorInput )
        // The 'FileEditorInput' class is used when the file is opened
        // from a project in the workspace.
        {
            return ( ( FileEditorInput ) input ).getFile().getContents();
        }
        else if ( input instanceof IPathEditorInput )
        {
            return new FileInputStream( new File( ( ( IPathEditorInput ) input ).getPath().toOSString() ) );
        }
        else if ( inputClassName.equals( "org.eclipse.ui.internal.editors.text.JavaFileEditorInput" ) //$NON-NLS-1$
            || inputClassName.equals( "org.eclipse.ui.ide.FileStoreEditorInput" ) ) //$NON-NLS-1$
        // The class 'org.eclipse.ui.internal.editors.text.JavaFileEditorInput'
        // is used when opening a file from the menu File > Open... in Eclipse 3.2.x
        // The class 'org.eclipse.ui.ide.FileStoreEditorInput' is used when
        // opening a file from the menu File > Open... in Eclipse 3.3.x
        {
            // We use the tooltip to get the full path of the file
            return new FileInputStream( new File( input.getToolTipText() ) );
        }

        return null;
    }
}
