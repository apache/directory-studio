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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.SearchResult;

import org.apache.directory.server.config.ConfigPartitionReader;
import org.apache.directory.server.config.ReadOnlyConfigurationPartition;
import org.apache.directory.server.config.beans.ConfigBean;
import org.apache.directory.server.core.partition.impl.btree.AbstractBTreePartition;
import org.apache.directory.shared.ldap.model.constants.SchemaConstants;
import org.apache.directory.shared.ldap.model.entry.AttributeUtils;
import org.apache.directory.shared.ldap.model.entry.DefaultEntry;
import org.apache.directory.shared.ldap.model.entry.Entry;
import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.model.exception.LdapNoSuchObjectException;
import org.apache.directory.shared.ldap.model.message.SearchScope;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.shared.ldap.model.schema.SchemaManager;
import org.apache.directory.studio.apacheds.configuration.v2.ApacheDS2ConfigurationPlugin;
import org.apache.directory.studio.apacheds.configuration.v2.editor.ConnectionServerConfigurationInput;
import org.apache.directory.studio.apacheds.configuration.v2.editor.NewServerConfigurationInput;
import org.apache.directory.studio.apacheds.configuration.v2.editor.ServerConfigurationEditor;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.common.core.jobs.StudioRunnableWithProgress;
import org.apache.directory.studio.connection.core.io.StudioNamingEnumeration;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.jobs.SearchRunnable;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.SearchParameter;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.part.FileEditorInput;


/**
 * This class implements a {@link Job} that is used to load a server configuration.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LoadConfigurationRunnable implements StudioRunnableWithProgress
{
    /** The associated editor */
    private ServerConfigurationEditor editor;


    /**
     * Creates a new instance of LoadConfigurationRunnable.
     * 
     * @param editor
     *            the editor
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
            final ConfigBean configBean = getConfiguration( input, monitor );
            if ( configBean != null )
            {
                Display.getDefault().asyncExec( new Runnable()
                {
                    public void run()
                    {
                        editor.configurationLoaded( configBean );
                    }
                } );
            }
        }
        catch ( Exception e )
        {
            // Reporting the error to the monitor
            monitor.reportError( e );

            // Reporting the error to the editor
            final Exception exception = e;
            Display.getDefault().asyncExec( new Runnable()
            {
                public void run()
                {
                    editor.configurationLoadFailed( exception );
                }
            } );
        }
    }


    /**
     * Gets the configuration from the input.
     * 
     * @param input
     *      the editor input
     * @param monitor
     *      the studio progress monitor
     * @return
     *      the configuration
     * @throws Exception
     */
    public ConfigBean getConfiguration( IEditorInput input, StudioProgressMonitor monitor ) throws Exception
    {
        String inputClassName = input.getClass().getName();
        // If the input is a NewServerConfigurationInput, then we only 
        // need to get the server configuration and return
        if ( input instanceof NewServerConfigurationInput )
        {
            InputStream is = ApacheDS2ConfigurationPlugin.class.getResourceAsStream( "config.ldif" );
            return readConfiguration( is );
        }
        // If the input is a ConnectionServerConfigurationInput, then we 
        // read the server configuration from the selected connection
        if ( input instanceof ConnectionServerConfigurationInput )
        {
            return readConfiguration( ( ConnectionServerConfigurationInput ) input, monitor );
        }
        else if ( input instanceof FileEditorInput )
        // The 'FileEditorInput' class is used when the file is opened
        // from a project in the workspace.
        {
            InputStream is = ( ( FileEditorInput ) input ).getFile().getContents();
            return readConfiguration( is );
        }
        else if ( input instanceof IPathEditorInput )
        {
            InputStream is = new FileInputStream( new File( ( ( IPathEditorInput ) input ).getPath().toOSString() ) );
            return readConfiguration( is );
        }
        else if ( inputClassName.equals( "org.eclipse.ui.internal.editors.text.JavaFileEditorInput" ) //$NON-NLS-1$
            || inputClassName.equals( "org.eclipse.ui.ide.FileStoreEditorInput" ) ) //$NON-NLS-1$
        // The class 'org.eclipse.ui.internal.editors.text.JavaFileEditorInput'
        // is used when opening a file from the menu File > Open... in Eclipse 3.2.x
        // The class 'org.eclipse.ui.ide.FileStoreEditorInput' is used when
        // opening a file from the menu File > Open... in Eclipse 3.3.x
        {
            // We use the tooltip to get the full path of the file
            InputStream is = new FileInputStream( new File( input.getToolTipText() ) );
            return readConfiguration( is );
        }

        return null;
    }


    /**
     * Reads the configuration from the given input stream.
     *
     * @param is
     *      the input stream
     * @return
     *      the associated configuration bean
     * @throws Exception
     */
    public static ConfigBean readConfiguration( InputStream is ) throws Exception
    {
        if ( is != null )
        {
            // Creating a partition associated from the input stream
            ReadOnlyConfigurationPartition configurationPartition = new ReadOnlyConfigurationPartition( is,
                ApacheDS2ConfigurationPlugin.getDefault().getSchemaManager() );
            configurationPartition.initialize();

            // Reading the configuration partition
            return readConfiguration( configurationPartition );

        }

        return null;
    }


    /**
     * Reads the configuration from the given partition.
     *
     * @param partition
     *      the configuration partition
     * @return
     *      the associated configuration bean
     * @throws LdapException
     */
    private static ConfigBean readConfiguration( AbstractBTreePartition<Long> partition ) throws LdapException
    {
        if ( partition != null )
        {
            ConfigPartitionReader cpReader = new ConfigPartitionReader( partition );
            return cpReader.readConfig();
        }

        return null;
    }


    /**
     * Reads the configuration from the given connection.
     *
     * @param input
     *      the editor input
     * @param monitor 
     *      the studio progress monitor
     * @return
     *      the associated configuration bean
     * @throws Exception
     */
    private ConfigBean readConfiguration( ConnectionServerConfigurationInput input,
        StudioProgressMonitor monitor ) throws Exception
    {
        if ( input != null )
        {
            SchemaManager schemaManager = ApacheDS2ConfigurationPlugin.getDefault().getSchemaManager();

            // Getting the browser connection associated with the connection in the input
            IBrowserConnection browserConnection = BrowserCorePlugin.getDefault().getConnectionManager()
                .getBrowserConnection( input.getConnection() );

            // Creating and initializing the configuration partition
            EntryBasedConfigurationPartition configurationPartition = new EntryBasedConfigurationPartition(
                schemaManager );
            configurationPartition.initialize();

            // Creating the search parameter
            SearchParameter configSearchParameter = new SearchParameter();
            configSearchParameter.setSearchBase( new Dn( "ou=config" ) );
            configSearchParameter.setFilter( "(objectClass=*)" );
            configSearchParameter.setScope( SearchScope.OBJECT );
            configSearchParameter.setReturningAttributes( SchemaConstants.ALL_USER_ATTRIBUTES_ARRAY );

            // Looking for the 'ou=config' base entry
            Entry configEntry = null;
            StudioNamingEnumeration enumeration = SearchRunnable.search( browserConnection, configSearchParameter,
                monitor );

            // Checking if an error occurred
            if ( monitor.errorsReported() )
            {
                throw monitor.getException();
            }

            // Getting the entry
            if ( enumeration.hasMore() )
            {
                // Creating the 'ou=config' base entry
                SearchResult searchResult = ( SearchResult ) enumeration.next();
                configEntry = new DefaultEntry( schemaManager, AttributeUtils.toEntry(
                    searchResult.getAttributes(), new Dn( searchResult.getNameInNamespace() ) ) );
            }
            enumeration.close();

            // Verifying we found the 'ou=config' base entry
            if ( configEntry == null )
            {
                throw new LdapNoSuchObjectException( "Unable to find the 'ou=config' base entry." );
            }

            // Creating a list to hold the entries that need to be checked
            // for children and added to the partition
            List<Entry> entries = new ArrayList<Entry>();
            entries.add( configEntry );

            // Flag used to determine if the current entry is the context entry
            boolean isContextEntry = true;

            // Looping on the entries list until it's empty
            while ( !entries.isEmpty() )
            {
                // Removing the first entry from the list
                Entry entry = entries.remove( 0 );

                // Special handling for the context entry
                if ( isContextEntry )
                {
                    // Setting the context entry of the partition
                    configurationPartition.setContextEntry( entry );
                    isContextEntry = false;
                }

                // Adding the entry to the partition
                configurationPartition.addEntry( entry );

                SearchParameter searchParameter = new SearchParameter();
                searchParameter.setSearchBase( entry.getDn() );
                searchParameter.setFilter( "(objectClass=*)" );
                searchParameter.setScope( SearchScope.ONELEVEL );
                searchParameter.setReturningAttributes( SchemaConstants.ALL_USER_ATTRIBUTES_ARRAY );

                // Looking for the children of the entry
                StudioNamingEnumeration childrenEnumeration = SearchRunnable.search( browserConnection,
                    searchParameter, monitor );

                // Checking if an error occurred
                if ( monitor.errorsReported() )
                {
                    throw monitor.getException();
                }

                while ( childrenEnumeration.hasMore() )
                {
                    // Creating the child entry
                    SearchResult searchResult = ( SearchResult ) childrenEnumeration.next();
                    Entry childEntry = new DefaultEntry( schemaManager, AttributeUtils.toEntry(
                        searchResult.getAttributes(), new Dn( searchResult.getNameInNamespace() ) ) );

                    // Adding the children to the list of entries
                    entries.add( childEntry );
                }
                childrenEnumeration.close();
            }

            // Setting the created partition to the input
            input.setOriginalPartition( configurationPartition );

            return readConfiguration( configurationPartition );
        }

        return null;
    }
}
