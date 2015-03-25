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


import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

import org.apache.directory.api.ldap.model.constants.SchemaConstants;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.ldif.LdifEntry;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.schema.SchemaManager;
import org.apache.directory.api.ldap.model.schema.registries.SchemaLoader;
import org.apache.directory.api.ldap.schema.manager.impl.DefaultSchemaManager;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.common.ui.CommonUIUtils;
import org.apache.directory.studio.common.ui.filesystem.PathEditorInput;
import org.apache.directory.studio.connection.core.Connection;
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

import org.apache.directory.studio.openldap.config.ConnectionSchemaLoader;
import org.apache.directory.studio.openldap.config.jobs.EntryBasedConfigurationPartition;
import org.apache.directory.studio.openldap.config.jobs.PartitionsDiffComputer;
import org.apache.directory.studio.openldap.config.model.io.ConfigurationWriter;


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
     * @param configurationWriter
     *      the configuration writer
     * @return
     *      the new input for the editor
     * @throws Exception
     */
    public static IEditorInput saveAs( IProgressMonitor monitor, Shell shell, IEditorInput input,
        ConfigurationWriter configurationWriter )
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
                dialog.setOriginalName( "config.ldif" );
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
            saveConfiguration( fei, configurationWriter, monitor );

            return fei;
        }
        else
        {
            boolean canOverwrite = false;
            String path = null;

            while ( !canOverwrite )
            {
                // Open FileDialog
                final FileDialog dialog = new FileDialog( shell, SWT.SAVE );
                path = openFileDialogInUIThread( dialog );
                if ( path == null )
                {
                    return null;
                }

                // Check whether file exists and if so, confirm overwrite
                final File externalFile = new File( path );
                if ( externalFile.exists() )
                {
                    String question = NLS.bind(
                        "The file \"{0}\" already exists. Do you want to replace the existing file?", path ); //$NON-NLS-1$
                    MessageDialog overwriteDialog = new MessageDialog( shell, "Question", null, question, //$NON-NLS-1$
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
            saveConfiguration( path, configurationWriter );

            // Creating the new input for the editor
            PathEditorInput newInput = new PathEditorInput( new Path( path ) );

            return newInput;
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
     * @param dialog
     *      the file dialog
     * @return
     *      the result of the dialog
     */
    private static String openFileDialogInUIThread( final FileDialog dialog )
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
                result.setResult( dialog.open() );
            }
        } );

        return result.getResult();
    }


    /**
     * Saves the configuration.
     *
     * @param input
     *      the file editor input
     * @param configurationWriter
     *      the configuration writer
     * @param monitor
     *      the monitor
     * @throws Exception
     */
    public static void saveConfiguration( FileEditorInput input, ConfigurationWriter configurationWriter,
        IProgressMonitor monitor )
        throws Exception
    {
        input.getFile().setContents( new ByteArrayInputStream( configurationWriter.writeToString().getBytes() ), true,
            true, monitor );
    }


    /**
     * Saves the configuration.
     *
     * @param input
     *      the connection server configuration input
     * @param configurationWriter
     *      the configuration writer
     * @param monitor
     *      the monitor
     * @return
     *      <code>true</code> if the operation is successful,
     *      <code>false</code> if not
     * @throws Exception
     */
    public static void saveConfiguration( ConnectionServerConfigurationInput input,
        ConfigurationWriter configurationWriter, IProgressMonitor monitor ) throws Exception
    {
        // Getting the original configuration partition and its schema manager
        EntryBasedConfigurationPartition originalPartition = input.getOriginalPartition();
        SchemaManager schemaManager = originalPartition.getSchemaManager();

        // Creating a new configuration partition
        EntryBasedConfigurationPartition newconfigurationPartition = createConfigurationPartition( schemaManager,
            originalPartition.getSuffixDn() );
        for ( LdifEntry ldifEntry : configurationWriter.getConvertedLdifEntries() )
        {
            newconfigurationPartition.addEntry( new DefaultEntry( schemaManager, ldifEntry.getEntry() ) );
        }

        // Comparing both partitions to get the list of modifications to be applied
        PartitionsDiffComputer partitionsDiffComputer = new PartitionsDiffComputer( originalPartition,
            newconfigurationPartition );
        List<LdifEntry> modificationsList = partitionsDiffComputer.computeModifications( new String[]
            { SchemaConstants.ALL_USER_ATTRIBUTES } );

        // Building the resulting LDIF
        StringBuilder modificationsLdif = new StringBuilder();
        for ( LdifEntry ldifEntry : modificationsList )
        {
            modificationsLdif.append( ldifEntry.toString() );
        }

        // Getting the browser connection associated with the 
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
            input.setOriginalPartition( newconfigurationPartition );
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
    public static void saveConfiguration( File file, ConfigurationWriter configurationWriter ) throws Exception
    {
        configurationWriter.writeToFile( file );
    }


    /**
     * Saves the configuration.
     *
     * @param path
     *      the path
     * @param configWriter
     *      the configuration writer
     * @throws Exception
     */
    public static void saveConfiguration( String path, ConfigurationWriter configurationWriter ) throws Exception
    {
        saveConfiguration( new File( path ), configurationWriter );
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
     * Creates a schema manager for the given connection.
     *
     * @param connection the connection
     * @return a schema manager for the given connection
     * @throws Exception 
     */
    public static SchemaManager createSchemaManager( Connection connection ) throws Exception
    {
        // Initializing the schema loader and schema manager
        SchemaLoader loader = new ConnectionSchemaLoader( connection );
        SchemaManager schemaManager = new DefaultSchemaManager( loader );

        // Loading only the connection schema with its dependencies
        schemaManager.loadWithDeps( ConnectionSchemaLoader.CONNECTION_SCHEMA_NAME );

        // Checking if no error occurred when loading the schemas
        if ( schemaManager.getErrors().size() != 0 )
        {
            throw new Exception( "Could not load the schema correctly." );
        }

        return schemaManager;
    }
}
