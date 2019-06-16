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
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.apache.directory.api.ldap.model.constants.SchemaConstants;
import org.apache.directory.api.ldap.model.csn.CsnFactory;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.ldif.LdifEntry;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.apache.directory.api.ldap.model.schema.SchemaManager;
import org.apache.directory.api.ldap.model.schema.registries.ObjectClassRegistry;
import org.apache.directory.api.ldap.util.tree.DnNode;
import org.apache.directory.api.util.DateUtils;
import org.apache.directory.api.util.TimeProvider;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.common.ui.CommonUIUtils;
import org.apache.directory.studio.common.ui.filesystem.PathEditorInput;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.jobs.ExecuteLdifRunnable;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.openldap.config.ExpandedLdifUtils;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPlugin;
import org.apache.directory.studio.openldap.config.jobs.EntryBasedConfigurationPartition;
import org.apache.directory.studio.openldap.config.jobs.PartitionsDiffComputer;
import org.apache.directory.studio.openldap.config.model.OpenLdapConfiguration;
import org.apache.directory.studio.openldap.config.model.io.ConfigurationException;
import org.apache.directory.studio.openldap.config.model.io.ConfigurationReader;
import org.apache.directory.studio.openldap.config.model.io.ConfigurationUtils;
import org.apache.directory.studio.openldap.config.model.io.ConfigurationWriter;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;


/**
 * This class contains helpful methods for the Server Configuration Editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OpenLdapServerConfigurationEditorUtils
{
    private OpenLdapServerConfigurationEditorUtils()
    {
        // Do nothing
    }
    
    
    /**
     * Opens a {@link FileDialog} in the UI thread.
     *
     * @param shell the shell
     * @return the result of the dialog
     */
    private static String openFileDialogInUIThread( final Shell shell )
    {
        // Defining our own encapsulating class for the result
        class DialogResult
        {
            private String result;

            public String getResult()
            {
                return result;
            }


            public void setResult( String result )
            {
                this.result = result;
            }
        }

        // Creating an object to hold the result
        final DialogResult result = new DialogResult();

        // Opening the dialog in the UI thread
        Display.getDefault().syncExec( () ->
            {
                FileDialog dialog = new FileDialog( shell, SWT.SAVE );
                result.setResult( dialog.open() );
            } );

        return result.getResult();
    }

    
    /**
     * Performs the "Save as..." action.
     *
     * @param configuration the configuration
     * @param newInput a flag to indicate if a new input is required
     * @return the new input for the editor
     * @throws Exception
     */
    public static IEditorInput saveAs( OpenLdapConfiguration configuration, Shell shell, boolean newInput ) throws Exception
    {
        // detect IDE or RCP:
        // check if perspective org.eclipse.ui.resourcePerspective is available
        boolean isIDE = CommonUIUtils.isIDEEnvironment();
        String path = null;

        if ( isIDE )
        {
        }
        else
        {
            boolean canOverwrite = false;

            while ( !canOverwrite )
            {
                // Opening the dialog
                // Open FileDialog
                path = openFileDialogInUIThread( shell );
                
                // Checking the returned path
                if ( path == null )
                {
                    // Cancel button has been clicked
                    return null;
                }
    
                // Getting the directory indicated by the user
                final File directory = new File( path );
    
                // Checking if the directory exists
                if ( !directory.exists() )
                {
                    CommonUIUtils.openErrorDialog( "The directory does not exist." );
                    continue;
                }
    
                // Checking if the location is a directory
                if ( !directory.isDirectory() )
                {
                    CommonUIUtils.openErrorDialog( "The location is not a directory." );
                    continue;
                }
    
                // Checking if the directory is writable
                if ( !directory.canWrite() )
                {
                    CommonUIUtils.openErrorDialog( "The directory is not writable." );
                    continue;
                }
    
                // Checking if the directory is empty
                if ( !isEmpty( directory ) )
                {
                    CommonUIUtils.openErrorDialog( "The directory is not empty." );
                    continue;
                }
    
                // The directory meets all requirements
                break;
            }
        }

        // Saving the file to disk
        saveConfiguration( configuration, new File( path ) );

        // Checking if a new input is required
        if ( newInput )
        {
            // Creating the new input for the editor
            return new PathEditorInput( new Path( path ) );
        }
        else
        {
            return null;
        }
    }


    private static boolean isEmpty( File directory )
    {
        if ( directory != null )
        {
            String[] children = directory.list( ( dir, name ) -> 
                    // Only accept visible files (which don't start with a dot).
                    !name.startsWith( "." )
                );

            return ( ( children == null ) || ( children.length == 0 ) );
        }

        return false;
    }


    /**
     * Opens a {@link DirectoryDialog} in the UI thread.
     *
     * @param dialog the directory dialog
     * @return the result of the dialog
     */
    private static String openDirectoryDialogInUIThread( final DirectoryDialog dialog )
    {
        // Defining our own encapsulating class for the result
        class DialogResult
        {
            private String result;


            public String getResult()
            {
                return result;
            }


            public void setResult( String result )
            {
                this.result = result;
            }
        }

        // Creating an object to hold the result
        final DialogResult result = new DialogResult();

        // Opening the dialog in the UI thread
        Display.getDefault().syncExec( () -> result.setResult( dialog.open() ) );

        return result.getResult();
    }


    /**
     * Saves the configuration.
     *
     * @param configuration the configuration
     * @param directory the directory
     * @throws Exception
     */
    public static void saveConfiguration( OpenLdapConfiguration configuration, File directory ) throws Exception
    {
        saveConfiguration( null, configuration, directory );
    }


    /**
     * Saves the configuration.
     *
     * @param browserConnection the browser connection
     * @param configurationthe configuration
     * @param directory the directory
     * @throws Exception
     */
    public static void saveConfiguration( IBrowserConnection browserConnection, OpenLdapConfiguration configuration,
        File directory ) throws Exception
    {
        // Creating the configuration writer
        ConfigurationWriter configurationWriter = new ConfigurationWriter( browserConnection, configuration );

        SchemaManager schemaManager = OpenLdapConfigurationPlugin.getDefault().getSchemaManager();

        // Converting the configuration beans to entries
        List<LdifEntry> entries = configurationWriter.getConvertedLdifEntries( ConfigurationUtils
            .getDefaultConfigurationDn() );

        // Creating a tree to store entries
        DnNode<Entry> tree = new DnNode<>();

        CsnFactory csnFactory = new CsnFactory( 1 );

        for ( LdifEntry entry : entries )
        {
            // Getting the current generalized time
            String currentgeGeneralizedTime = DateUtils.getGeneralizedTime( TimeProvider.DEFAULT );

            // 'createTimestamp' attribute
            entry.addAttribute( "createTimestamp", currentgeGeneralizedTime );

            // 'creatorsName' attribute
            entry.addAttribute( "creatorsName", "cn=config" );

            // 'entryCSN' attribute
            entry.addAttribute( "entryCSN", csnFactory.newInstance().toString() );

            // 'entryUUID' attribute
            entry.addAttribute( "entryUUID", UUID.randomUUID().toString() );

            // 'modifiersName' attribute
            entry.addAttribute( "modifiersName", "cn=config" );

            // 'modifyTimestamp' attribute
            entry.addAttribute( "modifyTimestamp", currentgeGeneralizedTime );

            // 'structuralObjectClass' attribute
            entry.addAttribute( "structuralObjectClass", getStructuralObjectClass( entry ) );

            // Adding the entry to tree
            tree.add( new Dn( schemaManager, entry.getDn() ), entry.getEntry() );
        }

        try
        {
            Dn rootDn = ConfigurationUtils.getDefaultConfigurationDn();
            ExpandedLdifUtils.write( tree, new Dn( schemaManager, rootDn ), directory );
        }
        catch ( ConfigurationException e )
        {
            throw new IOException( e );
        }
    }


    /**
     * Gets the structural object class of the entry.
     *
     * @param ldifEntry the LDIF entry
     * @return the structural object class of the entry
     * @throws ConfigurationException
     */
    private static Object getStructuralObjectClass( LdifEntry ldifEntry ) throws ConfigurationException
    {
        if ( ldifEntry != null )
        {
            Entry entry = ldifEntry.getEntry();

            if ( entry != null )
            {
                ObjectClass structuralObjectClass = ConfigurationReader
                    .getHighestStructuralObjectClass( entry.get( SchemaConstants.OBJECT_CLASS_AT ) );

                if ( structuralObjectClass != null )
                {
                    return structuralObjectClass.getName();
                }
            }
        }

        return SchemaConstants.TOP_OC;
    }


    /**
     * Saves the configuration.
     *
     * @param input the connection server configuration input
     * @param editor the editor
     * @param monitor the monitor
     * @return <code>true</code> if the operation is successful, <code>false</code> if not
     * @throws Exception
     */
    public static void saveConfiguration( ConnectionServerConfigurationInput input, OpenLdapServerConfigurationEditor editor,
        IProgressMonitor monitor ) throws Exception
    {
        // Getting the browser connection associated with the connection in the input
        IBrowserConnection browserConnection = BrowserCorePlugin.getDefault().getConnectionManager()
            .getBrowserConnection( input.getConnection() );

        // Creating the configuration writer
        ConfigurationWriter configurationWriter = new ConfigurationWriter( browserConnection, editor.getConfiguration() );

        // Getting the original configuration partition and its schema manager
        EntryBasedConfigurationPartition originalPartition = input.getOriginalPartition();
        SchemaManager schemaManager = originalPartition.getSchemaManager();

        // Suspends event firing in current thread.
        ConnectionEventRegistry.suspendEventFiringInCurrentThread();

        try
        {
            // Creating a new configuration partition
            EntryBasedConfigurationPartition modifiedPartition = createConfigurationPartition( schemaManager,
                originalPartition.getSuffixDn() );
            
            for ( LdifEntry ldifEntry : configurationWriter.getConvertedLdifEntries() )
            {
                modifiedPartition.addEntry( new DefaultEntry( schemaManager, ldifEntry.getEntry() ) );
            }

            // Comparing both partitions to get the list of modifications to be applied
            List<LdifEntry> modificationsList = PartitionsDiffComputer.computeModifications( originalPartition, 
                modifiedPartition, new String[] { SchemaConstants.ALL_USER_ATTRIBUTES } );

            // Building the resulting LDIF
            StringBuilder modificationsLdif = new StringBuilder();
            
            for ( LdifEntry ldifEntry : modificationsList )
            {
                modificationsLdif.append( ldifEntry.toString() );
            }

            // Creating a StudioProgressMonitor to run the LDIF with
            StudioProgressMonitor studioProgressMonitor = new StudioProgressMonitor( new NullProgressMonitor() );

            // Updating the configuration with the resulting LDIF
            ExecuteLdifRunnable.executeLdif( browserConnection, modificationsLdif.toString(), true, true,
                studioProgressMonitor );

            // Checking if there were errors during the execution of the LDIF
            if ( studioProgressMonitor.errorsReported() )
            {
                StringBuilder message = new StringBuilder();
                message.append( "Changes could not be saved to the connection." );

                Exception exception = studioProgressMonitor.getException();
                if ( exception != null )
                {
                    message.append( "\n\n" );
                    message.append( "Cause: " );
                    message.append( exception.getMessage() );

                    throw new Exception( message.toString(), exception );
                }
                else
                {
                    throw new Exception( message.toString() );
                }
            }
            else
            {
                // Swapping the new configuration partition
                input.setOriginalPartition( modifiedPartition );
            }
        }
        finally
        {
            // Resumes event firing in current thread.
            ConnectionEventRegistry.resumeEventFiringInCurrentThread();
        }
    }


    /**
     * Creates a configuration partition to store configuration entries.
     *
     * @param schemaManager the schema manager
     * @param configBaseDn the configuration base DN
     * @return a configuration partition to store configuration entries
     * @throws LdapException
     */
    public static EntryBasedConfigurationPartition createConfigurationPartition( SchemaManager schemaManager,
        Dn configBaseDn ) throws LdapException
    {
        EntryBasedConfigurationPartition configurationPartition = new EntryBasedConfigurationPartition(
            schemaManager, configBaseDn );
        configurationPartition.initialize();

        return configurationPartition;
    }


    /**
     * Gets the object class for the given name.
     *
     * @param schemaManager the schema manager
     * @param name the name
     * @return the object class for the given name
     */
    public static ObjectClass getObjectClass( SchemaManager schemaManager, String name )
    {
        // Checking the schema manager and name
        if ( ( schemaManager != null ) && ( name != null ) )
        {
            try
            {
                // Getting the object class registry
                ObjectClassRegistry ocRegistry = schemaManager.getObjectClassRegistry();

                if ( ocRegistry != null )
                {
                    // Getting the oid from the object class name
                    String oid = ocRegistry.getOidByName( name );

                    if ( oid != null )
                    {
                        // Getting the object class from the oid
                        return ocRegistry.get( oid );
                    }
                }
            }
            catch ( LdapException e )
            {
                // No OID found for the given name
                return null;
            }
        }

        return null;
    }
}
