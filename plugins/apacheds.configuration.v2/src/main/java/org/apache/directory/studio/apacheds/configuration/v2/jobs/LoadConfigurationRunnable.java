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
import org.apache.directory.server.core.partition.impl.btree.BTreePartition;
import org.apache.directory.shared.ldap.constants.SchemaConstants;
import org.apache.directory.shared.ldap.entry.DefaultEntry;
import org.apache.directory.shared.ldap.entry.Entry;
import org.apache.directory.shared.ldap.exception.LdapException;
import org.apache.directory.shared.ldap.name.DN;
import org.apache.directory.shared.ldap.schema.SchemaManager;
import org.apache.directory.shared.ldap.util.AttributeUtils;
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
import org.apache.directory.studio.ldapbrowser.core.model.ISearch.SearchScope;
import org.apache.directory.studio.ldapbrowser.core.model.SearchParameter;
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
            configBean = getConfiguration( input, monitor );
            if ( configBean != null )
            {
                final ConfigBean finalConfigBean = configBean;
                final IEditorInput finalInput = input;

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
        // Getting the schema manager
        SchemaManager schemaManager = ApacheDS2ConfigurationPlugin.getDefault().getSchemaManager();

        String inputClassName = input.getClass().getName();
        // If the input is a NewServerConfigurationInput, then we only 
        // need to get the server configuration and return
        if ( input instanceof NewServerConfigurationInput )
        {
            InputStream is = ApacheDS2ConfigurationPlugin.class.getResourceAsStream( "config.ldif" );
            return readConfiguration( schemaManager, is );
        }
        // If the input is a ConnectionServerConfigurationInput, then we 
        // read the server configuration from the selected connection
        if ( input instanceof ConnectionServerConfigurationInput )
        {
            return readConfiguration( schemaManager, ( ConnectionServerConfigurationInput ) input, monitor );
        }
        else if ( input instanceof FileEditorInput )
        // The 'FileEditorInput' class is used when the file is opened
        // from a project in the workspace.
        {
            InputStream is = ( ( FileEditorInput ) input ).getFile().getContents();
            return readConfiguration( schemaManager, is );
        }
        else if ( input instanceof IPathEditorInput )
        {
            InputStream is = new FileInputStream( new File( ( ( IPathEditorInput ) input ).getPath().toOSString() ) );
            return readConfiguration( schemaManager, is );
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
            return readConfiguration( schemaManager, is );
        }

        return null;
    }


    /**
     * Reads the configuration from the given input stream.
     *
     * @param schemaManager
     *      the schema manager
     * @param is
     *      the input stream
     * @return
     *      the associated configuration bean
     * @throws Exception
     */
    private ConfigBean readConfiguration( SchemaManager schemaManager, InputStream is ) throws Exception
    {
        if ( is != null )
        {
            // Creating a partition associated from the input stream
            ReadOnlyConfigurationPartition configurationPartition = new ReadOnlyConfigurationPartition(
                ApacheDS2ConfigurationPlugin.class.getResourceAsStream( "config.ldif" ), schemaManager );
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
    private ConfigBean readConfiguration( BTreePartition<Long> partition ) throws LdapException
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
     * @param schemaManager
     *      the schema manager
     * @param input
     *      the editor input
     * @param monitor 
     *      the studio progress monitor
     * @return
     *      the associated configuration bean
     * @throws Exception
     */
    private ConfigBean readConfiguration( SchemaManager schemaManager, ConnectionServerConfigurationInput input,
        StudioProgressMonitor monitor ) throws Exception
    {
        if ( input != null )
        {

            // Getting the browser connection associated with the 
            IBrowserConnection browserConnection = BrowserCorePlugin.getDefault().getConnectionManager()
                .getBrowserConnection( input.getConnection() );

            // Creating and initializing the configuration partition
            EntryBasedConfigurationPartition configurationPartition = new EntryBasedConfigurationPartition(
                schemaManager );
            configurationPartition.initialize();

            // Creating the search parameter
            SearchParameter configSearchParameter = new SearchParameter();
            configSearchParameter.setSearchBase( new DN( "ou=config" ) );
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
                return null;
            }

            // Getting the entry
            if ( enumeration.hasMore() )
            {
                // Creating the 'ou=config' base entry
                SearchResult searchResult = ( SearchResult ) enumeration.next();
                configEntry = new DefaultEntry( schemaManager, AttributeUtils.toClientEntry(
                    searchResult.getAttributes(), new DN( searchResult.getNameInNamespace() ) ) );
            }
            enumeration.close();

            // Verifying we found the 'ou=config' base entry
            if ( configEntry == null )
            {
                // TODO throw a new error
            }

            // Creating a list to hold the entries that needs to be checked
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
                    return null;
                }

                while ( childrenEnumeration.hasMore() )
                {
                    // Creating the child entry
                    SearchResult searchResult = ( SearchResult ) childrenEnumeration.next();
                    Entry childEntry = new DefaultEntry( schemaManager, AttributeUtils.toClientEntry(
                        searchResult.getAttributes(), new DN( searchResult.getNameInNamespace() ) ) );

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
