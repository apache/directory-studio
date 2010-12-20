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
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.directory.server.config.ConfigWriter;
import org.apache.directory.server.config.ConfigurationException;
import org.apache.directory.server.config.beans.ConfigBean;
import org.apache.directory.shared.ldap.entry.DefaultEntry;
import org.apache.directory.shared.ldap.ldif.LdifEntry;
import org.apache.directory.shared.ldap.schema.SchemaManager;
import org.apache.directory.studio.apacheds.configuration.v2.ApacheDS2ConfigurationPlugin;
import org.apache.directory.studio.apacheds.configuration.v2.jobs.EntryBasedConfigurationPartition;
import org.apache.directory.studio.apacheds.configuration.v2.jobs.LoadConfigurationRunnable;
import org.apache.directory.studio.apacheds.configuration.v2.jobs.PartitionsDiffComputer;
import org.apache.directory.studio.common.core.jobs.StudioJob;
import org.apache.directory.studio.common.core.jobs.StudioRunnableWithProgress;
import org.apache.directory.studio.common.ui.CommonUIUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.part.FileEditorInput;


/**
 * This class implements the Server Configuration Editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ServerConfigurationEditor extends FormEditor
{
    /** The Editor ID */
    public static final String ID = ServerConfigurationEditor.class.getName();

    /** The flag indicating if the editor is dirty */
    private boolean dirty = false;

    /** The configuration bean */
    private ConfigBean configBean;


    /**
     * {@inheritDoc}
     */
    public void init( IEditorSite site, IEditorInput input ) throws PartInitException
    {
        super.init( site, input );
        setPartName( input.getName() );

        readConfiguration();
    }


    /**
     * Reads the configuration
     */
    private void readConfiguration()
    {
        // Creating and scheduling the job to load the configuration
        StudioJob<StudioRunnableWithProgress> job = new StudioJob<StudioRunnableWithProgress>(
            new LoadConfigurationRunnable( this ) );
        job.schedule();
    }


    /**
     * {@inheritDoc}
     */
    protected void addPages()
    {
        try
        {
            addPage( new LoadingPage( this ) );
        }
        catch ( PartInitException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * {@inheritDoc}
     */
    public void doSave( IProgressMonitor monitor )
    {
        if ( dirty )
        {
            monitor.beginTask( "Saving the server configuration", IProgressMonitor.UNKNOWN );

            try
            {
                IEditorInput input = getEditorInput();
                String inputClassName = input.getClass().getName();
                boolean success = false;
                if ( input instanceof FileEditorInput )
                // FileEditorInput class is used when the file is opened
                // from a project in the workspace.
                {
                    // Saving the ServerConfiguration to disk
                    saveConfiguration( ( FileEditorInput ) input, monitor );
                    success = true;
                }
                // If the input is a ConnectionServerConfigurationInput, then we 
                // read the server configuration from the selected connection
                if ( input instanceof ConnectionServerConfigurationInput )
                {
                    // Saving the ServerConfiguration to the connection
                    saveConfiguration( ( ConnectionServerConfigurationInput ) input, monitor );
                    success = true;
                }
                else if ( input instanceof IPathEditorInput )
                {
                    // Saving the ServerConfiguration to disk
                    saveConfiguration( ( ( IPathEditorInput ) input ).getPath().toFile() );
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
                    saveConfiguration( input.getToolTipText() );
                    success = true;
                }
                else if ( input instanceof NewServerConfigurationInput )
                {
                    // The 'ServerConfigurationEditorInput' class is used when a
                    // new Server Configuration File is created.

                    // We are saving this as if it is a "Save as..." action.
                    success = doSaveAs( monitor );
                }

                setDirty( !success );
                monitor.done();
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
        }
    }


    /**
     * Saves the configuration.
     *
     * @param input
     *      the file editor input
     * @param monitor
     *      the monitor
     * @throws Exception
     */
    private void saveConfiguration( FileEditorInput input, IProgressMonitor monitor ) throws Exception
    {
        input.getFile().setContents( new ByteArrayInputStream( getConfigWriter().writeToString().getBytes() ), true,
            true, monitor );
    }


    /**
     * Saves the configuration.
     *
     * @param input
     *      the connection server configuration input
     * @param monitor
     *      the monitor
     * @throws ConfigurationException 
     * @throws Exception
     */
    private void saveConfiguration( ConnectionServerConfigurationInput input, IProgressMonitor monitor )
        throws ConfigurationException, Exception
    {
        // Getting the original configuration partition
        EntryBasedConfigurationPartition originalPartition = input.getOriginalPartition();

        // Creating a new configuration partition
        SchemaManager schemaManager = ApacheDS2ConfigurationPlugin.getDefault().getSchemaManager();
        EntryBasedConfigurationPartition newconfigurationPartition = new EntryBasedConfigurationPartition(
            schemaManager );
        newconfigurationPartition.initialize();
        List<LdifEntry> convertedLdifEntries = getConfigWriter().getConvertedLdifEntries();
        for ( LdifEntry ldifEntry : convertedLdifEntries )
        {
            newconfigurationPartition.addEntry( new DefaultEntry( schemaManager, ldifEntry.getEntry() ) );
        }

        // Comparing both partitions to get the list of modifications to be applied
        PartitionsDiffComputer partitionsDiffComputer = new PartitionsDiffComputer();
        partitionsDiffComputer.setOriginalPartition( originalPartition );
        partitionsDiffComputer.setDestinationPartition( newconfigurationPartition );
        List<LdifEntry> modificationsList = partitionsDiffComputer.computeModifications( new String[]
            { "*" } );

        System.out.println( modificationsList );
    }


    /**
     * Saves the configuration.
     *
     * @param file
     *      the file
     * @throws Exception
     */
    private void saveConfiguration( File file ) throws Exception
    {
        getConfigWriter().writeToFile( file );
    }


    /**
     * Saves the configuration.
     *
     * @param path
     *      the path
     * @throws Exception
     */
    private void saveConfiguration( String path ) throws Exception
    {
        saveConfiguration( new File( path ) );
    }


    /**
     * {@inheritDoc}
     */
    public void doSaveAs()
    {
        try
        {
            getSite().getWorkbenchWindow().run( false, false, new IRunnableWithProgress()
            {
                public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException
                {
                    try
                    {
                        monitor
                            .beginTask( "Saving The Server Configuration", IProgressMonitor.UNKNOWN );
                        boolean success = doSaveAs( monitor );
                        setDirty( !success );
                        monitor.done();
                    }
                    catch ( Exception e )
                    {
                        // TODO handle the exception
                    }
                }
            } );
        }
        catch ( Exception e )
        {
            // TODO handle the exception
            e.printStackTrace();
        }
    }


    /**
     * Performs the "Save as..." action.
     *
     * @param monitor
     *      the monitor to use
     * @throws Exception
     */
    private boolean doSaveAs( IProgressMonitor monitor ) throws Exception
    {
        // detect IDE or RCP:
        // check if perspective org.eclipse.ui.resourcePerspective is available
        boolean isIDE = CommonUIUtils.isIDEEnvironment();

        if ( isIDE )
        {
            // Asking the user for the location where to 'save as' the file
            SaveAsDialog dialog = new SaveAsDialog( getSite().getShell() );
            if ( !( getEditorInput() instanceof NewServerConfigurationInput ) )
            {
                dialog.setOriginalFile( ResourcesPlugin.getWorkspace().getRoot().getFile(
                    new Path( getEditorInput().getToolTipText() ) ) );
            }
            if ( dialog.open() != Dialog.OK )
            {
                return false;
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
            saveConfiguration( fei, monitor );

            // Setting the new input to the editor
            setInput( fei );
        }
        else
        {
            Shell shell = getSite().getShell();
            boolean canOverwrite = false;
            String path = null;

            while ( !canOverwrite )
            {
                // Open FileDialog
                FileDialog dialog = new FileDialog( shell, SWT.SAVE );
                path = dialog.open();
                if ( path == null )
                {
                    return false;
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
                    int overwrite = overwriteDialog.open();
                    switch ( overwrite )
                    {
                        case 0: // Yes
                            canOverwrite = true;
                            break;
                        case 1: // No
                            break;
                        case 2: // Cancel
                        default:
                            return false;
                    }
                }
                else
                {
                    canOverwrite = true;
                }
            }

            // Saving the file to disk
            saveConfiguration( path );

            // Creating the new input for the editor
            PathEditorInput newInput = new PathEditorInput( new Path( path ) );

            // Setting the new input to the editor
            setInput( newInput );
        }

        // Updating the title and tooltip texts
        setPartName( getEditorInput().getName() );

        return true;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isSaveAsAllowed()
    {
        return true;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isDirty()
    {
        return dirty;
    }


    /**
     * Sets the 'dirty' flag.
     *
     * @param dirty
     *      the 'dirty' flag
     */
    public void setDirty( boolean dirty )
    {
        this.dirty = dirty;
        firePropertyChange( PROP_DIRTY );
    }


    /**
     * Gets the configuration bean.
     *
     * @return
     *      the configuration bean
     */
    public ConfigBean getConfigBean()
    {
        return configBean;
    }


    /**
     * Sets the configuration.
     *
     * @param configBean
     *      the configuration bean
     */
    public void setConfiguration( ConfigBean configBean )
    {
        this.configBean = configBean;
    }


    /**
     * This method is called by the job responsible for loading the configuration.
     *
     * @param configBean
     *      the loaded configuration bean
     */
    public void configurationLoaded( ConfigBean configBean )
    {
        setConfiguration( configBean );

        hideLoadingPageAndDisplayConfigPages();
    }


    /**
     * Hides the loading page and displays the standard configuration pages.
     */
    private void hideLoadingPageAndDisplayConfigPages()
    {
        // Removing the loading page
        removePage( 0 );

        // Adding the configuration pages
        try
        {
            addPage( new OverviewPage( this ) );
            addPage( new LdapLdapsServersPage( this ) );
            addPage( new KerberosServerPage( this ) );
            addPage( new PartitionsPage( this ) );
            addPage( new ReplicationPage( this ) );
        }
        catch ( PartInitException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Activating the first page
        setActivePage( 0 );
    }


    /**
     * Gets the configuration writer.
     *
     * @return
     *      the configuration writer
     * @throws Exception
     */
    private ConfigWriter getConfigWriter() throws Exception
    {
        return new ConfigWriter( ApacheDS2ConfigurationPlugin.getDefault().getSchemaManager(), configBean );
    }
}

/**
 * This IEditorInput is used to open files that are located in the local file system.
 * 
 * Inspired from org.eclipse.ui.internal.editors.text.NonExistingFileEditorInput.java
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
class PathEditorInput implements IPathEditorInput
{
    /** The absolute path in local file system */
    private IPath path;


    /**
     * 
     * Creates a new instance of PathEditorInput.
     *
     * @param path the absolute path
     */
    public PathEditorInput( IPath path )
    {
        if ( path == null )
        {
            throw new IllegalArgumentException();
        }

        this.path = path;
    }


    /**
     * Returns hash code of the path.
     */
    public int hashCode()
    {
        return path.hashCode();
    }


    /** 
     * This implemention just compares the paths
     */
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o instanceof PathEditorInput )
        {
            PathEditorInput input = ( PathEditorInput ) o;
            return path.equals( input.path );
        }

        return false;
    }


    /**
     * {@inheritDoc}
     */
    public boolean exists()
    {
        return path.toFile().exists();
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return PlatformUI.getWorkbench().getEditorRegistry().getImageDescriptor( path.toString() );
    }


    /**
     * Returns the file name only.
     */
    public String getName()
    {
        return path.toFile().getName();
        //return path.toString();
    }


    /**
     * Returns the complete path. 
     */
    public String getToolTipText()
    {
        return path.makeRelative().toOSString();
    }


    /**
     * {@inheritDoc}
     */
    public IPath getPath()
    {
        return path;
    }


    /**
     * {@inheritDoc}
     */
    public Object getAdapter( Class adapter )
    {
        return Platform.getAdapterManager().getAdapter( this, adapter );
    }


    /**
     * {@inheritDoc}
     */
    public IPersistableElement getPersistable()
    {
        return null;
    }


    /**
     * Returns the path.
     */
    public IPath getErrorMessage( Object element )
    {
        if ( element instanceof PathEditorInput )
        {
            PathEditorInput input = ( PathEditorInput ) element;
            return input.getPath();
        }

        return null;
    }
}
