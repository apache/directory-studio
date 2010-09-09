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
package org.apache.directory.studio.apacheds.configuration.editor;


import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.apache.directory.studio.apacheds.configuration.ApacheDSConfigurationPlugin;
import org.apache.directory.studio.apacheds.configuration.ApacheDSConfigurationPluginConstants;
import org.apache.directory.studio.apacheds.configuration.ApacheDSConfigurationPluginUtils;
import org.apache.directory.studio.apacheds.configuration.model.ServerConfiguration;
import org.apache.directory.studio.apacheds.configuration.model.ServerXmlIO;
import org.apache.directory.studio.apacheds.configuration.model.ServerXmlIOException;
import org.apache.directory.studio.apacheds.configuration.model.v150.ServerXmlIOV150;
import org.apache.directory.studio.apacheds.configuration.model.v151.ServerXmlIOV151;
import org.apache.directory.studio.apacheds.configuration.model.v152.ServerXmlIOV152;
import org.apache.directory.studio.apacheds.configuration.model.v153.ServerXmlIOV153;
import org.apache.directory.studio.apacheds.configuration.model.v154.ServerXmlIOV154;
import org.apache.directory.studio.apacheds.configuration.model.v155.ServerXmlIOV155;
import org.apache.directory.studio.apacheds.configuration.model.v156.ServerXmlIOV156;
import org.apache.directory.studio.apacheds.configuration.model.v157.ServerXmlIOV157;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
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
    public static final String ID = ApacheDSConfigurationPluginConstants.EDITOR_SERVER_CONFIGURATION_EDITOR;

    /** The Server Configuration */
    private ServerConfiguration serverConfiguration;

    /** The associated ServerXmlIO class */
    private ServerXmlIO serverXmlIO;

    /** The dirty flag */
    private boolean dirty = false;

    /** The error message */
    private String errorMessage;

    // The Pages
    private SaveableFormPage generalPage;
    private SaveableFormPage authenticationPage;
    private SaveableFormPage partitionsPage;
    private SaveableFormPage interceptorsPage;
    private SaveableFormPage extendedOperationsPage;


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormEditor#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     */
    public void init( IEditorSite site, IEditorInput input ) throws PartInitException
    {
        super.init( site, input );
        setPartName( input.getName() );

        try
        {
            readServerConfiguration( input );
        }
        catch ( Exception e )
        {
            errorMessage = e.getMessage();
        }
    }


    /**
     * Reads the server configuration from the given editor input.
     *
     * @param input
     *      the editor input
     * @throws CoreException 
     * @throws FileNotFoundException 
     * @throws ServerXmlIOException 
     */
    private void readServerConfiguration( IEditorInput input ) throws CoreException, FileNotFoundException,
        ServerXmlIOException
    {
        // If the input is a NonExistingServerConfigurationInput, then we only 
        // need to get the server configuration and return
        if ( input instanceof NonExistingServerConfigurationInput )
        {
            // The 'ServerConfigurationEditorInput' class is used when a
            // new Server Configuration File is created.
            serverConfiguration = ( ( NonExistingServerConfigurationInput ) input ).getServerConfiguration();
            dirty = true;

            // Setting the ServerXmlIO class
            switch ( serverConfiguration.getVersion() )
            {
                case VERSION_1_5_7:
                    serverXmlIO = new ServerXmlIOV157();
                    break;
                case VERSION_1_5_6:
                    serverXmlIO = new ServerXmlIOV156();
                    break;
                case VERSION_1_5_5:
                    serverXmlIO = new ServerXmlIOV155();
                    break;
                case VERSION_1_5_4:
                    serverXmlIO = new ServerXmlIOV154();
                    break;
                case VERSION_1_5_3:
                    serverXmlIO = new ServerXmlIOV153();
                    break;
                case VERSION_1_5_2:
                    serverXmlIO = new ServerXmlIOV152();
                    break;
                case VERSION_1_5_1:
                    serverXmlIO = new ServerXmlIOV151();
                    break;
                case VERSION_1_5_0:
                    serverXmlIO = new ServerXmlIOV150();
                    break;
            }
            return;
        }

        // Looping on the ServerXmlIO classes to find a corresponding one
        ServerXmlIO[] serverXmlIOs = ApacheDSConfigurationPlugin.getDefault().getServerXmlIOs();
        for ( ServerXmlIO validationServerXmlIO : serverXmlIOs )
        {
            // Checking if the ServerXmlIO is valid
            if ( validationServerXmlIO.isValid( getInputStream( input ) ) )
            {
                serverXmlIO = validationServerXmlIO;
                serverConfiguration = serverXmlIO.parse( getInputStream( input ) );
                return;
            }
        }
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
        if ( input instanceof FileEditorInput )
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


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
     */
    protected void addPages()
    {
        try
        {
            if ( serverConfiguration == null )
            {
                ErrorPage errorPage = new ErrorPage( this );
                addPage( errorPage );
            }
            else
            {
                switch ( serverConfiguration.getVersion() )
                {
                    case VERSION_1_5_7:
                        generalPage = new org.apache.directory.studio.apacheds.configuration.editor.v157.GeneralPage(
                            this );
                        addPage( generalPage );

                        authenticationPage = new org.apache.directory.studio.apacheds.configuration.editor.v157.AuthenticationPage(
                            this );
                        addPage( authenticationPage );

                        partitionsPage = new org.apache.directory.studio.apacheds.configuration.editor.v157.PartitionsPage(
                            this );
                        addPage( partitionsPage );

                        interceptorsPage = new org.apache.directory.studio.apacheds.configuration.editor.v157.InterceptorsPage(
                            this );
                        addPage( interceptorsPage );

                        extendedOperationsPage = new org.apache.directory.studio.apacheds.configuration.editor.v157.ExtendedOperationsPage(
                            this );
                        addPage( extendedOperationsPage );
                        break;
                    case VERSION_1_5_6:
                        generalPage = new org.apache.directory.studio.apacheds.configuration.editor.v156.GeneralPage(
                            this );
                        addPage( generalPage );

                        authenticationPage = new org.apache.directory.studio.apacheds.configuration.editor.v156.AuthenticationPage(
                            this );
                        addPage( authenticationPage );

                        partitionsPage = new org.apache.directory.studio.apacheds.configuration.editor.v156.PartitionsPage(
                            this );
                        addPage( partitionsPage );

                        interceptorsPage = new org.apache.directory.studio.apacheds.configuration.editor.v156.InterceptorsPage(
                            this );
                        addPage( interceptorsPage );

                        extendedOperationsPage = new org.apache.directory.studio.apacheds.configuration.editor.v156.ExtendedOperationsPage(
                            this );
                        addPage( extendedOperationsPage );
                        break;
                    case VERSION_1_5_5:
                        generalPage = new org.apache.directory.studio.apacheds.configuration.editor.v155.GeneralPage(
                            this );
                        addPage( generalPage );

                        authenticationPage = new org.apache.directory.studio.apacheds.configuration.editor.v155.AuthenticationPage(
                            this );
                        addPage( authenticationPage );

                        partitionsPage = new org.apache.directory.studio.apacheds.configuration.editor.v155.PartitionsPage(
                            this );
                        addPage( partitionsPage );

                        interceptorsPage = new org.apache.directory.studio.apacheds.configuration.editor.v155.InterceptorsPage(
                            this );
                        addPage( interceptorsPage );

                        extendedOperationsPage = new org.apache.directory.studio.apacheds.configuration.editor.v155.ExtendedOperationsPage(
                            this );
                        addPage( extendedOperationsPage );
                        break;
                    case VERSION_1_5_4:
                        generalPage = new org.apache.directory.studio.apacheds.configuration.editor.v154.GeneralPage(
                            this );
                        addPage( generalPage );

                        authenticationPage = new org.apache.directory.studio.apacheds.configuration.editor.v154.AuthenticationPage(
                            this );
                        addPage( authenticationPage );

                        partitionsPage = new org.apache.directory.studio.apacheds.configuration.editor.v154.PartitionsPage(
                            this );
                        addPage( partitionsPage );

                        interceptorsPage = new org.apache.directory.studio.apacheds.configuration.editor.v154.InterceptorsPage(
                            this );
                        addPage( interceptorsPage );

                        extendedOperationsPage = new org.apache.directory.studio.apacheds.configuration.editor.v154.ExtendedOperationsPage(
                            this );
                        addPage( extendedOperationsPage );
                        break;
                    case VERSION_1_5_3:
                        generalPage = new org.apache.directory.studio.apacheds.configuration.editor.v153.GeneralPage(
                            this );
                        addPage( generalPage );

                        authenticationPage = new org.apache.directory.studio.apacheds.configuration.editor.v153.AuthenticationPage(
                            this );
                        addPage( authenticationPage );

                        partitionsPage = new org.apache.directory.studio.apacheds.configuration.editor.v153.PartitionsPage(
                            this );
                        addPage( partitionsPage );

                        interceptorsPage = new org.apache.directory.studio.apacheds.configuration.editor.v153.InterceptorsPage(
                            this );
                        addPage( interceptorsPage );

                        extendedOperationsPage = new org.apache.directory.studio.apacheds.configuration.editor.v153.ExtendedOperationsPage(
                            this );
                        addPage( extendedOperationsPage );
                        break;
                    case VERSION_1_5_2:
                        generalPage = new org.apache.directory.studio.apacheds.configuration.editor.v152.GeneralPage(
                            this );
                        addPage( generalPage );

                        authenticationPage = new org.apache.directory.studio.apacheds.configuration.editor.v152.AuthenticationPage(
                            this );
                        addPage( authenticationPage );

                        partitionsPage = new org.apache.directory.studio.apacheds.configuration.editor.v152.PartitionsPage(
                            this );
                        addPage( partitionsPage );

                        interceptorsPage = new org.apache.directory.studio.apacheds.configuration.editor.v152.InterceptorsPage(
                            this );
                        addPage( interceptorsPage );

                        extendedOperationsPage = new org.apache.directory.studio.apacheds.configuration.editor.v152.ExtendedOperationsPage(
                            this );
                        addPage( extendedOperationsPage );
                        break;
                    case VERSION_1_5_1:
                        generalPage = new org.apache.directory.studio.apacheds.configuration.editor.v151.GeneralPage(
                            this );
                        addPage( generalPage );

                        partitionsPage = new org.apache.directory.studio.apacheds.configuration.editor.v151.PartitionsPage(
                            this );
                        addPage( partitionsPage );

                        interceptorsPage = new org.apache.directory.studio.apacheds.configuration.editor.v151.InterceptorsPage(
                            this );
                        addPage( interceptorsPage );

                        extendedOperationsPage = new org.apache.directory.studio.apacheds.configuration.editor.v151.ExtendedOperationsPage(
                            this );
                        addPage( extendedOperationsPage );
                        break;
                    case VERSION_1_5_0:
                        generalPage = new org.apache.directory.studio.apacheds.configuration.editor.v150.GeneralPage(
                            this );
                        addPage( generalPage );

                        partitionsPage = new org.apache.directory.studio.apacheds.configuration.editor.v150.PartitionsPage(
                            this );
                        addPage( partitionsPage );

                        interceptorsPage = new org.apache.directory.studio.apacheds.configuration.editor.v150.InterceptorsPage(
                            this );
                        addPage( interceptorsPage );

                        extendedOperationsPage = new org.apache.directory.studio.apacheds.configuration.editor.v150.ExtendedOperationsPage(
                            this );
                        addPage( extendedOperationsPage );
                        break;
                }
            }
        }
        catch ( PartInitException e )
        {
            ApacheDSConfigurationPlugin.getDefault().getLog().log(
                new Status( Status.ERROR, ApacheDSConfigurationPluginConstants.PLUGIN_ID, Status.OK, e.getMessage(), e
                    .getCause() ) );
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void doSave( IProgressMonitor monitor )
    {
        monitor.beginTask(
            Messages.getString( "ServerConfigurationEditor.SavingTheServerConfiguration" ), IProgressMonitor.UNKNOWN ); //$NON-NLS-1$

        // Saving the editor pages
        saveEditorPages();

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
            else if ( input instanceof IPathEditorInput )
            {
                // Saving the ServerConfiguration to disk
                saveConfiguration( ( ( IPathEditorInput ) input ).getPath().toOSString() );
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
            else if ( input instanceof NonExistingServerConfigurationInput )
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
            MessageBox messageBox = new MessageBox( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                SWT.OK | SWT.ICON_ERROR );
            messageBox.setText( Messages.getString( "ServerConfigurationEditor.Error" ) ); //$NON-NLS-1$
            messageBox
                .setMessage( Messages.getString( "ServerConfigurationEditor.AnErrorOccurredWhenWritingTheFileToDisk" ) + "\n" + e.getMessage() ); //$NON-NLS-1$ //$NON-NLS-2$
            messageBox.open();
            setDirty( true );
            monitor.done();
            return;
        }
    }


    /**
     * Saves the editor pages.
     *
     * @param monitor
     *      the monitor to use
     */
    private void saveEditorPages()
    {
        if ( serverConfiguration != null )
        {
            switch ( serverConfiguration.getVersion() )
            {
                case VERSION_1_5_7:
                    generalPage.save();
                    authenticationPage.save();
                    partitionsPage.save();
                    interceptorsPage.save();
                    extendedOperationsPage.save();
                    break;
                case VERSION_1_5_6:
                    generalPage.save();
                    authenticationPage.save();
                    partitionsPage.save();
                    interceptorsPage.save();
                    extendedOperationsPage.save();
                    break;
                case VERSION_1_5_5:
                    generalPage.save();
                    authenticationPage.save();
                    partitionsPage.save();
                    interceptorsPage.save();
                    extendedOperationsPage.save();
                    break;
                case VERSION_1_5_4:
                    generalPage.save();
                    authenticationPage.save();
                    partitionsPage.save();
                    interceptorsPage.save();
                    extendedOperationsPage.save();
                    break;
                case VERSION_1_5_3:
                    generalPage.save();
                    authenticationPage.save();
                    partitionsPage.save();
                    interceptorsPage.save();
                    extendedOperationsPage.save();
                    break;
                case VERSION_1_5_2:
                    generalPage.save();
                    authenticationPage.save();
                    partitionsPage.save();
                    interceptorsPage.save();
                    extendedOperationsPage.save();
                    break;
                case VERSION_1_5_1:
                    generalPage.save();
                    partitionsPage.save();
                    interceptorsPage.save();
                    extendedOperationsPage.save();
                    break;
                case VERSION_1_5_0:
                    generalPage.save();
                    partitionsPage.save();
                    interceptorsPage.save();
                    extendedOperationsPage.save();
                    break;
            }
        }
    }


    /**
     * Saves the server configuration to the given path.
     *
     * @param path
     *      the path where to save the file
     * @throws IOException
     * @throws ServerConfigurationWriterException
     */
    private void saveConfiguration( String path ) throws IOException
    {
        BufferedWriter outFile = new BufferedWriter( new FileWriter( path ) );
        String xml = serverXmlIO.toXml( serverConfiguration );
        outFile.write( xml );
        outFile.close();
    }


    /**
     * Saves the server configuration using the given {@link FileEditorInput}.
     *
     * @param fei
     *      the {@link FileEditorInput}
     * @throws ServerConfigurationWriterException 
     * @throws CoreException 
     * @throws IOException 
     */
    private void saveConfiguration( FileEditorInput fei, IProgressMonitor monitor ) throws CoreException, IOException
    {
        String xml = serverXmlIO.toXml( serverConfiguration );
        fei.getFile().setContents( new ByteArrayInputStream( xml.getBytes() ), true, true, monitor );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#doSaveAs()
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
                            .beginTask(
                                Messages.getString( "ServerConfigurationEditor.SavingTheServerConfiguration" ), IProgressMonitor.UNKNOWN ); //$NON-NLS-1$
                        saveEditorPages();
                        boolean success = doSaveAs( monitor );
                        setDirty( !success );
                        monitor.done();
                    }
                    catch ( Exception e )
                    {
                        MessageBox messageBox = new MessageBox( PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                            .getShell(), SWT.OK | SWT.ICON_ERROR );
                        messageBox.setText( Messages.getString( "ServerConfigurationEditor.Error" ) ); //$NON-NLS-1$
                        messageBox.setMessage( Messages
                            .getString( "ServerConfigurationEditor.AnErrorOccurredWhenWritingTheFileToDisk" ) + "\n" //$NON-NLS-1$ //$NON-NLS-2$
                            + e.getMessage() );
                        messageBox.open();
                        setDirty( true );
                        monitor.done();
                        return;
                    }
                }
            } );
        }
        catch ( Exception e )
        {
            MessageBox messageBox = new MessageBox( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                SWT.OK | SWT.ICON_ERROR );
            messageBox.setText( Messages.getString( "ServerConfigurationEditor.Error" ) ); //$NON-NLS-1$
            messageBox
                .setMessage( Messages.getString( "ServerConfigurationEditor.AnErrorOccurredWhenWritingTheFileToDisk" ) + "\n" + e.getMessage() ); //$NON-NLS-1$ //$NON-NLS-2$
            messageBox.open();
            return;
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
        boolean isIDE = ApacheDSConfigurationPluginUtils.isIDEEnvironment();

        if ( isIDE )
        {
            // Asking the user for the location where to 'save as' the file
            SaveAsDialog dialog = new SaveAsDialog( getSite().getShell() );
            if ( !( getEditorInput() instanceof NonExistingServerConfigurationInput ) )
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
            saveEditorPages();
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
                    String question = NLS.bind( Messages
                        .getString( "ServerConfigurationEditor.TheFileAlreadyExistsReplace" ), path ); //$NON-NLS-1$
                    MessageDialog overwriteDialog = new MessageDialog( shell, Messages
                        .getString( "ServerConfigurationEditor.Question" ), null, question, //$NON-NLS-1$
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
            saveEditorPages();
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


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
     */
    public boolean isSaveAsAllowed()
    {
        return true;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormEditor#isDirty()
     */
    public boolean isDirty()
    {
        return dirty;
    }


    /**
     * Sets the dirty state of the editor.
     * 
     * @param dirty
     *      the new dirty
     */
    public void setDirty( boolean dirty )
    {
        this.dirty = dirty;
        editorDirtyStateChanged();
    }


    /**
     * Gets the Server Configuration.
     *
     * @return
     *      the Server Configuration
     */
    public ServerConfiguration getServerConfiguration()
    {
        return serverConfiguration;
    }


    /**
     * Gets the error message.
     *
     * @return
     *      the error message
     */
    public String getErrorMessage()
    {
        return errorMessage;
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
    @SuppressWarnings("unchecked")
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
