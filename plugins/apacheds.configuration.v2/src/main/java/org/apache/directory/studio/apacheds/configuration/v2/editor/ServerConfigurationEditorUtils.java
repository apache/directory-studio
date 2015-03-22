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
package org.apache.directory.studio.apacheds.configuration.v2.editor;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.directory.api.ldap.model.constants.SchemaConstants;
import org.apache.directory.api.ldap.model.csn.CsnFactory;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.ldif.LdifEntry;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.schema.SchemaManager;
import org.apache.directory.server.config.ConfigWriter;
import org.apache.directory.server.core.api.CacheService;
import org.apache.directory.server.core.api.DnFactory;
import org.apache.directory.server.core.api.interceptor.context.AddOperationContext;
import org.apache.directory.server.core.partition.impl.btree.AbstractBTreePartition;
import org.apache.directory.server.core.partition.ldif.AbstractLdifPartition;
import org.apache.directory.server.core.partition.ldif.LdifPartition;
import org.apache.directory.server.core.partition.ldif.SingleFileLdifPartition;
import org.apache.directory.studio.apacheds.configuration.v2.ApacheDS2ConfigurationPlugin;
import org.apache.directory.studio.apacheds.configuration.v2.ApacheDS2ConfigurationPluginConstants;
import org.apache.directory.studio.apacheds.configuration.v2.jobs.EntryBasedConfigurationPartition;
import org.apache.directory.studio.apacheds.configuration.v2.jobs.PartitionsDiffComputer;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.common.ui.CommonUIUtils;
import org.apache.directory.studio.common.ui.filesystem.PathEditorInput;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.jobs.ExecuteLdifRunnable;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.FileEditorInput;


/**
 * This class contains helpful methods for the Server Configuration Editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ServerConfigurationEditorUtils
{
    /**
     * Performs the "Save as..." action.
     *
     * @param monitor
     *      the monitor
     * @param shell
     *      the shell
     * @param input
     *      the editor input
     * @param configWriter
     *      the configuration writer
     * @param newInput
     *      a flag to indicate if a new input is required
     * @return
     *      the new input for the editor
     * @throws Exception
     */
    public static IEditorInput saveAs( IProgressMonitor monitor, Shell shell, IEditorInput input,
        ConfigWriter configWriter, Configuration configuration, boolean newInput )
        throws Exception
    {
        // detect IDE or RCP:
        // check if perspective org.eclipse.ui.resourcePerspective is available
        boolean isIDE = CommonUIUtils.isIDEEnvironment();

        if ( isIDE )
        {
            // Asking the user for the location where to 'save as' the file
            final SaveAsDialog dialog = new SaveAsDialog( shell );

            String inputClassName = input.getClass().getName();
            if ( input instanceof FileEditorInput )
            // FileEditorInput class is used when the file is opened
            // from a project in the workspace.
            {
                dialog.setOriginalFile( ( ( FileEditorInput ) input ).getFile() );
            }
            else if ( input instanceof IPathEditorInput )
            {
                dialog.setOriginalFile( ResourcesPlugin.getWorkspace().getRoot()
                    .getFile( ( ( IPathEditorInput ) input ).getPath() ) );
            }
            else if ( inputClassName.equals( "org.eclipse.ui.internal.editors.text.JavaFileEditorInput" ) //$NON-NLS-1$
                || inputClassName.equals( "org.eclipse.ui.ide.FileStoreEditorInput" ) ) //$NON-NLS-1$
            // The class 'org.eclipse.ui.internal.editors.text.JavaFileEditorInput'
            // is used when opening a file from the menu File > Open... in Eclipse 3.2.x
            // The class 'org.eclipse.ui.ide.FileStoreEditorInput' is used when
            // opening a file from the menu File > Open... in Eclipse 3.3.x
            {
                dialog.setOriginalFile( ResourcesPlugin.getWorkspace().getRoot()
                    .getFile( new Path( input.getToolTipText() ) ) );
            }
            else
            {
                dialog.setOriginalName( ApacheDS2ConfigurationPluginConstants.CONFIG_LDIF );
            }

            // Open the dialog
            if ( openDialogInUIThread( dialog ) != Dialog.OK )
            {
                return null;
            }

            // Getting if the resulting file
            IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile( dialog.getResult() );

            // Creating the file if it does not exist
            if ( !file.exists() )
            {
                file.create( new ByteArrayInputStream( "".getBytes() ), true, null ); //$NON-NLS-1$
            }

            // Creating the new input for the editor
            FileEditorInput fei = new FileEditorInput( file );

            // Saving the file to disk
            File configFile = fei.getPath().toFile();
            saveConfiguration( configFile, configWriter, configuration );

            return fei;
        }
        else
        {
            boolean canOverwrite = false;
            String path = null;

            while ( !canOverwrite )
            {
                // Open FileDialog
                path = openFileDialogInUIThread( shell );
                if ( path == null )
                {
                    return null;
                }

                // Check whether file exists and if so, confirm overwrite
                final File externalFile = new File( path );
                if ( externalFile.exists() )
                {
                    String question = NLS.bind(
                        Messages.getString( "ServerConfigurationEditorUtils.TheFileAlreadyExistsWantToReplace" ), path ); //$NON-NLS-1$
                    MessageDialog overwriteDialog = new MessageDialog( shell,
                        Messages.getString( "ServerConfigurationEditorUtils.Question" ), null, question, //$NON-NLS-1$
                        MessageDialog.QUESTION, new String[]
                            { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL, IDialogConstants.CANCEL_LABEL }, 0 );
                    int overwrite = openDialogInUIThread( overwriteDialog );
                    switch ( overwrite )
                    {
                        case 0: // Yes
                            canOverwrite = true;
                            break;
                        case 1: // No
                            break;
                        case 2: // Cancel
                        default:
                            return null;
                    }
                }
                else
                {
                    canOverwrite = true;
                }
            }

            // Saving the file to disk
            saveConfiguration( new File(path), configWriter, configuration );

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
    }


    /**
     * Opens a {@link Dialog} in the UI thread.
     *
     * @param dialog
     *      the dialog
     * @return
     *      the result of the dialog
     */
    private static int openDialogInUIThread( final Dialog dialog )
    {
        // Defining our own encapsulating class for the result
        class DialogResult
        {
            private int result;


            public int getResult()
            {
                return result;
            }


            public void setResult( int result )
            {
                this.result = result;
            }
        }

        // Creating an object to hold the result
        final DialogResult result = new DialogResult();

        // Opening the dialog in the UI thread
        Display.getDefault().syncExec( new Runnable()
        {
            public void run()
            {
                result.setResult( dialog.open() );
            }
        } );

        return result.getResult();
    }


    /**
     * Opens a {@link FileDialog} in the UI thread.
     *
     * @param shell
     *      the shell
     * @return
     *      the result of the dialog
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
        Display.getDefault().syncExec( new Runnable()
        {
            public void run()
            {
                FileDialog dialog = new FileDialog( shell, SWT.SAVE );
                result.setResult( dialog.open() );
            }
        } );

        return result.getResult();
    }


    /**
     * Saves the configuration.
     *
     * @param input
     *      the connection server configuration input
     * @param configWriter
     *      the configuration writer
     * @param monitor
     *      the monitor
     * @return
     *      <code>true</code> if the operation is successful,
     *      <code>false</code> if not
     * @throws Exception
     */
    public static void saveConfiguration( ConnectionServerConfigurationInput input, ConfigWriter configWriter,
        IProgressMonitor monitor )
        throws Exception
    {
        // Getting the original configuration partition
        EntryBasedConfigurationPartition originalPartition = input.getOriginalPartition();

        // Creating a new configuration partition
        SchemaManager schemaManager = ApacheDS2ConfigurationPlugin.getDefault().getSchemaManager();
        EntryBasedConfigurationPartition newconfigurationPartition = new EntryBasedConfigurationPartition(
            schemaManager );
        CacheService cacheService = new CacheService();
        cacheService.initialize( null );
        newconfigurationPartition.setCacheService( cacheService );
        newconfigurationPartition.initialize();
        List<LdifEntry> convertedLdifEntries = configWriter.getConvertedLdifEntries();
        for ( LdifEntry ldifEntry : convertedLdifEntries )
        {
            newconfigurationPartition.addEntry( new DefaultEntry( schemaManager, ldifEntry.getEntry() ) );
        }

        // Suspends event firing in current thread.
        ConnectionEventRegistry.suspendEventFiringInCurrentThread();

        try
        {

            // Comparing both partitions to get the list of modifications to be applied
            PartitionsDiffComputer partitionsDiffComputer = new PartitionsDiffComputer();
            partitionsDiffComputer.setOriginalPartition( originalPartition );
            partitionsDiffComputer.setDestinationPartition( newconfigurationPartition );
            List<LdifEntry> modificationsList = partitionsDiffComputer.computeModifications( new String[]
                { SchemaConstants.ALL_USER_ATTRIBUTES } );

            // Building the resulting LDIF
            StringBuilder modificationsLdif = new StringBuilder();
            for ( LdifEntry ldifEntry : modificationsList )
            {
                modificationsLdif.append( ldifEntry.toString() );
            }

            // Getting the browser connection associated with the connection
            IBrowserConnection browserConnection = BrowserCorePlugin.getDefault().getConnectionManager()
                .getBrowserConnection( input.getConnection() );

            // Creating a StudioProgressMonitor to run the LDIF with
            StudioProgressMonitor studioProgressMonitor = new StudioProgressMonitor( new NullProgressMonitor() );

            // Updating the configuration with the resulting LDIF
            ExecuteLdifRunnable.executeLdif( browserConnection, modificationsLdif.toString(), true, true,
                studioProgressMonitor );

            // Checking if there were errors during the execution of the LDIF
            if ( studioProgressMonitor.errorsReported() )
            {
                throw new Exception(
                    Messages.getString( "ServerConfigurationEditorUtils.ChangesCouldNotBeSavedToConnection" ) ); //$NON-NLS-1$
            }
            else
            {
                // Swapping the new configuration partition
                input.setOriginalPartition( newconfigurationPartition );
            }
        }
        finally
        {
            // Resumes event firing in current thread.
            ConnectionEventRegistry.resumeEventFiringInCurrentThread();
        }
    }


    /**
     * Saves the configuration.
     *
     * @param file
     *      the file
     * @param configWriter
     *      the configuration writer
     * @throws Exception
     */
    public static void saveConfiguration( File file, ConfigWriter configWriter, Configuration configuration )
        throws Exception
    {
        SchemaManager schemaManager = ApacheDS2ConfigurationPlugin.getDefault().getSchemaManager();

        CacheService cacheService = new CacheService();
        cacheService.initialize( null );

        DnFactory dnFactory = null;

        CsnFactory csnFactory = new CsnFactory( 0 );

        if ( file != null )
        {
            // create partiton
            AbstractLdifPartition configPartition;
            if ( file.getName().equals( ApacheDS2ConfigurationPluginConstants.OU_CONFIG_LDIF ) )
            {
                File confDir = file.getParentFile();
                if ( file.exists() )
                {
                    FileUtils.deleteDirectory( confDir );
                }
                configPartition = createMultiFileConfiguration( confDir, schemaManager, dnFactory, cacheService );
            }
            else
            {
                if ( file.exists() )
                {
                    file.delete();
                }
                configPartition = createSingleFileConfiguration( file, schemaManager, dnFactory, cacheService );
            }

            // write entries to partition
            List<LdifEntry> convertedLdifEntries = configWriter.getConvertedLdifEntries();
            for ( LdifEntry ldifEntry : convertedLdifEntries )
            {
                Entry entry = new DefaultEntry( schemaManager, ldifEntry.getEntry() );
                if ( entry.get( SchemaConstants.ENTRY_CSN_AT ) == null )
                {
                    entry.add( SchemaConstants.ENTRY_CSN_AT, csnFactory.newInstance().toString() );
                }
                if ( entry.get( SchemaConstants.ENTRY_UUID_AT ) == null )
                {
                    String uuid = UUID.randomUUID().toString();
                    entry.add( SchemaConstants.ENTRY_UUID_AT, uuid );
                }
                configPartition.add( new AddOperationContext( null, entry ) );
            }
        }
    }


    private static SingleFileLdifPartition createSingleFileConfiguration( File configFile, SchemaManager schemaManager,
        DnFactory dnFactory, CacheService cacheService ) throws Exception
    {
        SingleFileLdifPartition configPartition = new SingleFileLdifPartition( schemaManager, dnFactory );
        configPartition.setId( "config" );
        configPartition.setPartitionPath( configFile.toURI() );
        configPartition.setSuffixDn( new Dn( schemaManager, "ou=config" ) );
        configPartition.setSchemaManager( schemaManager );
        configPartition.setCacheService( cacheService );
        configPartition.initialize();
        return configPartition;
    }


    private static LdifPartition createMultiFileConfiguration( File confDir, SchemaManager schemaManager, DnFactory dnFactory,
        CacheService cacheService ) throws Exception
    {
        LdifPartition configPartition = new LdifPartition( schemaManager, dnFactory );
        configPartition.setId( "config" );
        configPartition.setPartitionPath( confDir.toURI() );
        configPartition.setSuffixDn( new Dn( schemaManager, "ou=config" ) );
        configPartition.setSchemaManager( schemaManager );
        configPartition.setCacheService( cacheService );
        configPartition.initialize();
        return configPartition;
    }


    // TODO: somthing link this should be used in future to only write changes to partition
    private static List<LdifEntry> computeModifications( ConfigWriter configWriter,
        AbstractBTreePartition originalPartition ) throws Exception
    {
        // Creating a new configuration partition
        SchemaManager schemaManager = ApacheDS2ConfigurationPlugin.getDefault().getSchemaManager();
        EntryBasedConfigurationPartition newconfigurationPartition = new EntryBasedConfigurationPartition(
            schemaManager );
        CacheService cacheService = new CacheService();
        cacheService.initialize( null );
        newconfigurationPartition.setCacheService( cacheService );
        newconfigurationPartition.initialize();
        List<LdifEntry> convertedLdifEntries = configWriter.getConvertedLdifEntries();
        for ( LdifEntry ldifEntry : convertedLdifEntries )
        {
            newconfigurationPartition.addEntry( new DefaultEntry( schemaManager, ldifEntry.getEntry() ) );
        }

        // Comparing both partitions to get the list of modifications to be applied
        PartitionsDiffComputer partitionsDiffComputer = new PartitionsDiffComputer();
        partitionsDiffComputer.setOriginalPartition( originalPartition );
        partitionsDiffComputer.setDestinationPartition( newconfigurationPartition );
        List<LdifEntry> modificationsList = partitionsDiffComputer.computeModifications( new String[]
            { SchemaConstants.ALL_USER_ATTRIBUTES } );

        return modificationsList;
    }


    /**
     * Checks if the string is <code>null</code>
     * and returns an empty string in that case.
     *
     * @param s the string
     * @return a non-<code>null</code> string
     */
    public static String checkNull( String s )
    {
        if ( s == null )
        {
            return "";
        }

        return s;
    }


    /**
     * Checks if the string is <code>null</code>
     * and returns an empty string in that case.
     *
     * @param s the string
     * @return a non-<code>null</code> string
     */
    public static String checkEmptyString( String s )
    {
        if ( "".equals( s ) )
        {
            return null;
        }

        return s;
    }
}
