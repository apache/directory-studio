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

package org.apache.directory.studio.apacheds.configuration.jobs;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.SearchResult;

import org.apache.directory.api.ldap.model.constants.LdapConstants;
import org.apache.directory.api.ldap.model.constants.SchemaConstants;
import org.apache.directory.api.ldap.model.entry.AttributeUtils;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapNoSuchObjectException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.schema.SchemaManager;
import org.apache.directory.server.config.ConfigPartitionReader;
import org.apache.directory.server.config.ConfigPartitionInitializer;
import org.apache.directory.server.config.ReadOnlyConfigurationPartition;
import org.apache.directory.server.config.beans.ConfigBean;
import org.apache.directory.server.core.api.CacheService;
import org.apache.directory.server.core.api.DnFactory;
import org.apache.directory.server.core.api.InstanceLayout;
import org.apache.directory.server.constants.ServerDNConstants;
import org.apache.directory.server.core.partition.impl.btree.AbstractBTreePartition;
import org.apache.directory.server.core.partition.ldif.LdifPartition;
import org.apache.directory.studio.apacheds.configuration.ApacheDS2ConfigurationPlugin;
import org.apache.directory.studio.apacheds.configuration.ApacheDS2ConfigurationPluginConstants;
import org.apache.directory.studio.apacheds.configuration.editor.Configuration;
import org.apache.directory.studio.apacheds.configuration.editor.ConnectionServerConfigurationInput;
import org.apache.directory.studio.apacheds.configuration.editor.NewServerConfigurationInput;
import org.apache.directory.studio.apacheds.configuration.editor.ServerConfigurationEditor;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.common.core.jobs.StudioRunnableWithProgress;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.IConnectionListener;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;
import org.apache.directory.studio.connection.core.io.StudioNamingEnumeration;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.jobs.SearchRunnable;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.SearchParameter;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.osgi.framework.Bundle;


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
     * @param editor the editor
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
        return Messages.getString( "LoadConfigurationRunnable.UnableToLoadConfiguration" ); //$NON-NLS-1$
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
        return Messages.getString( "LoadConfigurationRunnable.LoadConfiguration" ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public void run( StudioProgressMonitor monitor )
    {
        IEditorInput input = editor.getEditorInput();

        try
        {
            final Configuration configuration = getConfiguration( input, monitor );
            
            if ( configuration != null )
            {
                Display.getDefault().asyncExec( new Runnable()
                {
                    public void run()
                    {
                        editor.configurationLoaded( configuration );
                    }
                } );
            }
        }
        catch ( Exception e )
        {
            ApacheDS2ConfigurationPlugin.getDefault().getLog().log( 
                new Status( Status.ERROR, "org.apache.directory.studio.apacheds.configuration", 
                    e.getMessage() ) );

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
     * @param input the editor input
     * @param monitor the studio progress monitor
     * @return the configuration
     * @throws Exception If the configuration wasn't correctly read
     */
    public Configuration getConfiguration( IEditorInput input, StudioProgressMonitor monitor ) throws Exception
    {
        String inputClassName = input.getClass().getName();
        
        // If the input is a NewServerConfigurationInput, then we only 
        // need to get the server configuration and return
        if ( input instanceof NewServerConfigurationInput )
        {
            Bundle bundle = Platform.getBundle( "org.apache.directory.server.config" );
            URL resource = bundle.getResource( "config.ldif" );
            InputStream is = resource.openStream();
            return readSingleFileConfiguration( is );
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
            File file = ( ( FileEditorInput ) input ).getFile().getLocation().toFile();
            return readConfiguration( file );
        }
        else if ( input instanceof IPathEditorInput )
        {
            File file = ( ( IPathEditorInput ) input ).getPath().toFile();
            return readConfiguration( file );
        }
        else if ( inputClassName.equals( "org.eclipse.ui.internal.editors.text.JavaFileEditorInput" ) //$NON-NLS-1$
            || inputClassName.equals( "org.eclipse.ui.ide.FileStoreEditorInput" ) ) //$NON-NLS-1$
        // The class 'org.eclipse.ui.internal.editors.text.JavaFileEditorInput'
        // is used when opening a file from the menu File > Open... in Eclipse 3.2.x
        // The class 'org.eclipse.ui.ide.FileStoreEditorInput' is used when
        // opening a file from the menu File > Open... in Eclipse 3.3.x
        {
            // We use the tooltip to get the full path of the file
            File file = new File( input.getToolTipText() );
            return readConfiguration( file );
        }

        return null;
    }


    /**
     * Reads the configuration from the given input stream.
     *
     * @param is the input stream
     * @return the associated configuration bean
     * @throws Exception if we weren't able to load the configuration
     */
    public static Configuration readConfiguration( File file ) throws Exception
    {
        if ( file != null )
        {
            if(file.getName().equals( ApacheDS2ConfigurationPluginConstants.CONFIG_LDIF )) {
                return readSingleFileConfiguration( file );
            }
            else if(file.getName().equals( ApacheDS2ConfigurationPluginConstants.OU_CONFIG_LDIF )) {
                return readMultiFileConfigureation( file.getParentFile() );
            }
        }

        return null;
    }


    private static Configuration readSingleFileConfiguration( File configLdifFile ) throws Exception
    {
        InputStream is = new FileInputStream( configLdifFile );

        // Reading the configuration partition
        return readSingleFileConfiguration( is );
    }


    private static Configuration readSingleFileConfiguration( InputStream is ) throws Exception
    {
        // Creating a partition associated from the input stream
        ReadOnlyConfigurationPartition configurationPartition = new ReadOnlyConfigurationPartition( is,
            ApacheDS2ConfigurationPlugin.getDefault().getSchemaManager() );
        CacheService cacheService = new CacheService();
        cacheService.initialize( null );
        configurationPartition.setCacheService( cacheService );

        configurationPartition.initialize();

        // Reading the configuration partition
        return readConfiguration( configurationPartition );
    }


    private static synchronized Configuration readMultiFileConfigureation( File confDirectory ) throws Exception
    {
        InstanceLayout instanceLayout = new InstanceLayout( confDirectory.getParentFile() );

        CacheService cacheService = new CacheService();
        cacheService.initialize( null );

        SchemaManager schemaManager = ApacheDS2ConfigurationPlugin.getDefault().getSchemaManager();

        DnFactory dnFactory = null;

        ConfigPartitionInitializer init = new ConfigPartitionInitializer( instanceLayout, dnFactory, cacheService, schemaManager );
        LdifPartition configurationPartition = init.initConfigPartition();

        return readConfiguration( configurationPartition );
    }


    /**
     * Reads the configuration from the given partition.
     *
     * @param partition the configuration partition
     * @return the associated configuration bean
     * @throws LdapException if we weren't able to load the configuration
     */
    private static Configuration readConfiguration( AbstractBTreePartition partition ) throws LdapException
    {
        if ( partition != null )
        {
            ConfigPartitionReader cpReader = new ConfigPartitionReader( partition );
            ConfigBean configBean = cpReader.readConfig();
            return new Configuration( configBean, partition );
        }

        return null;
    }


    /**
     * Reads the configuration from the given connection.
     *
     * @param input the editor input
     * @param monitor the studio progress monitor
     * @return the associated configuration bean
     * @throws Exception if we weren't able to load the configuration
     */
    private Configuration readConfiguration( ConnectionServerConfigurationInput input, StudioProgressMonitor monitor ) throws Exception
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
            CacheService cacheService = new CacheService();
            cacheService.initialize( null );
            configurationPartition.setCacheService( cacheService );
            configurationPartition.initialize();

            // Opening the connection
            openConnection( input, monitor );

            // Creating the search parameter
            SearchParameter configSearchParameter = new SearchParameter();
            configSearchParameter.setSearchBase( new Dn( ServerDNConstants.CONFIG_DN ) ); //$NON-NLS-1$
            //configSearchParameter.setSearchBase( new Dn( "ou=config" ) ); //$NON-NLS-1$
            configSearchParameter.setFilter( LdapConstants.OBJECT_CLASS_STAR ); //$NON-NLS-1$
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
                ApacheDS2ConfigurationPlugin.getDefault().getLog().log( 
                    new Status( Status.ERROR, "org.apache.directory.studio.apacheds.configuration", 
                        Messages.getString( "LoadConfigurationRunnable.UnableToFindConfigBaseEntry" ) ) );
                throw new LdapNoSuchObjectException(
                    Messages.getString( "LoadConfigurationRunnable.UnableToFindConfigBaseEntry" ) ); //$NON-NLS-1$
            }

            // Creating a list to hold the entries that need to be checked
            // for children and added to the partition
            List<Entry> entries = new ArrayList<Entry>();
            entries.add( configEntry );

            // Looping on the entries list until it's empty
            while ( !entries.isEmpty() )
            {
                // Removing the first entry from the list
                Entry entry = entries.remove( 0 );

                // Adding the entry to the partition
                configurationPartition.addEntry( entry );

                SearchParameter searchParameter = new SearchParameter();
                searchParameter.setSearchBase( entry.getDn() );
                searchParameter.setFilter( LdapConstants.OBJECT_CLASS_STAR ); //$NON-NLS-1$
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


    /**
     * Opens the connection.
     *
     * @param input the input
     * @param monitor the monitor
     */
    private void openConnection( ConnectionServerConfigurationInput input, StudioProgressMonitor monitor )
    {
        Connection connection = input.getConnection();

        if ( connection != null && !connection.getConnectionWrapper().isConnected() )
        {
            connection.getConnectionWrapper().connect( monitor );
            
            if ( connection.getConnectionWrapper().isConnected() )
            {
                connection.getConnectionWrapper().bind( monitor );
            }

            if ( connection.getConnectionWrapper().isConnected() )
            {
                for ( IConnectionListener listener : ConnectionCorePlugin.getDefault()
                    .getConnectionListeners() )
                {
                    listener.connectionOpened( connection, monitor );
                }
                
                ConnectionEventRegistry.fireConnectionOpened( connection, input );
            }
        }
    }
}
