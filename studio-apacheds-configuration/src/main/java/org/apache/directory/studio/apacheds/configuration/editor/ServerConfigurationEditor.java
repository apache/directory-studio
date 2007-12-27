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
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.directory.studio.apacheds.configuration.Activator;
import org.apache.directory.studio.apacheds.configuration.PluginUtils;
import org.apache.directory.studio.apacheds.configuration.model.ServerConfiguration;
import org.apache.directory.studio.apacheds.configuration.model.ServerConfigurationParser;
import org.apache.directory.studio.apacheds.configuration.model.ServerConfigurationWriter;
import org.apache.directory.studio.apacheds.configuration.model.ServerConfigurationWriterException;
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
import org.eclipse.jface.window.Window;
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
 * @version $Rev$, $Date$
 */
public class ServerConfigurationEditor extends FormEditor
{
    /** The Editor ID */
    public static final String ID = "org.apache.directory.studio.apacheds.configuration.editor";

    /** The Server Configuration */
    private ServerConfiguration serverConfiguration;

    /** The dirty flag */
    private boolean dirty = false;

    // The Pages
    private GeneralPage generalPage;
    private PartitionsPage partitionsPage;
    private InterceptorsPage interceptorsPage;
    private ExtendedOperationsPage extendedOperationsPage;


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormEditor#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     */
    public void init( IEditorSite site, IEditorInput input ) throws PartInitException
    {
        super.init( site, input );
        setPartName( input.getName() );

        String inputClassName = input.getClass().getName();
        try
        {
            if ( input instanceof FileEditorInput )
            // The 'FileEditorInput' class is used when the file is opened
            // from a project in the workspace.
            {
                ServerConfigurationParser parser = new ServerConfigurationParser();
                serverConfiguration = parser.parse( ( ( FileEditorInput ) input ).getFile().getContents() );
            }
            else if ( input instanceof IPathEditorInput )
            {
                ServerConfigurationParser parser = new ServerConfigurationParser();
                serverConfiguration = parser.parse( new FileInputStream( new File( ( ( IPathEditorInput ) input )
                    .getPath().toOSString() ) ) );
            }
            else if ( inputClassName.equals( "org.eclipse.ui.internal.editors.text.JavaFileEditorInput" )
                || inputClassName.equals( "org.eclipse.ui.ide.FileStoreEditorInput" ) )
            // The class 'org.eclipse.ui.internal.editors.text.JavaFileEditorInput'
            // is used when opening a file from the menu File > Open... in Eclipse 3.2.x
            // The class 'org.eclipse.ui.ide.FileStoreEditorInput' is used when
            // opening a file from the menu File > Open... in Eclipse 3.3.x
            {
                // We use the tooltip to get the full path of the file
                ServerConfigurationParser parser = new ServerConfigurationParser();
                serverConfiguration = parser.parse( new FileInputStream( new File( input.getToolTipText() ) ) );
            }
            else if ( input instanceof NonExistingServerConfigurationInput )
            {
                // The 'ServerConfigurationEditorInput' class is used when a
                // new Server Configuration File is created.
                serverConfiguration = ( ( NonExistingServerConfigurationInput ) input ).getServerConfiguration();
                dirty = true;
            }
        }
        catch ( Exception e )
        {
            MessageBox messageBox = new MessageBox( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                SWT.OK | SWT.ICON_ERROR );
            messageBox.setText( "Error!" );
            messageBox.setMessage( "An error occurred when reading the file." + "\n" + e.getMessage() );
            messageBox.open();
            return;
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
     */
    protected void addPages()
    {
        try
        {
            generalPage = new GeneralPage( this );
            addPage( generalPage );

            partitionsPage = new PartitionsPage( this );
            addPage( partitionsPage );

            interceptorsPage = new InterceptorsPage( this );
            addPage( interceptorsPage );

            extendedOperationsPage = new ExtendedOperationsPage( this );
            addPage( extendedOperationsPage );
        }
        catch ( PartInitException e )
        {
            Activator.getDefault().getLog().log(
                new Status( Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e.getCause() ) );
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void doSave( IProgressMonitor monitor )
    {
        monitor.beginTask( "Saving the Server Configuration", 5 );

        // Saving the editor pages
        saveEditorPages( monitor );

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
            else if ( inputClassName.equals( "org.eclipse.ui.internal.editors.text.JavaFileEditorInput" )
                || inputClassName.equals( "org.eclipse.ui.ide.FileStoreEditorInput" ) )
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

            monitor.worked( 1 );
            setDirty( !success );
            monitor.done();
        }
        catch ( Exception e )
        {
            MessageBox messageBox = new MessageBox( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                SWT.OK | SWT.ICON_ERROR );
            messageBox.setText( "Error!" );
            messageBox.setMessage( "An error occurred when writing the file to disk." + "\n" + e.getMessage() );
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
    private void saveEditorPages( IProgressMonitor monitor )
    {
        generalPage.save();
        if ( monitor != null )
        {
            monitor.worked( 1 );
        }

        partitionsPage.save();
        if ( monitor != null )
        {
            monitor.worked( 1 );
        }

        interceptorsPage.save();
        if ( monitor != null )
        {
            monitor.worked( 1 );
        }

        extendedOperationsPage.save();
        if ( monitor != null )
        {
            monitor.worked( 1 );
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
    private void saveConfiguration( String path ) throws IOException, ServerConfigurationWriterException
    {
        BufferedWriter outFile = new BufferedWriter( new FileWriter( path ) );
        String xml = ServerConfigurationWriter.toXml( serverConfiguration );
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
     */
    private void saveConfiguration( FileEditorInput fei, IProgressMonitor monitor )
        throws ServerConfigurationWriterException, CoreException
    {
        String xml = ServerConfigurationWriter.toXml( serverConfiguration );
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
                        monitor.beginTask( "Saving the Server Configuration", 5 );
                        saveEditorPages( monitor );
                        boolean success = doSaveAs( monitor );
                        monitor.worked( 1 );
                        setDirty( !success );
                        monitor.done();
                    }
                    catch ( Exception e )
                    {
                        MessageBox messageBox = new MessageBox( PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                            .getShell(), SWT.OK | SWT.ICON_ERROR );
                        messageBox.setText( "Error!" );
                        messageBox.setMessage( "An error occurred when writing the file to disk." + "\n"
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
            messageBox.setText( "Error!" );
            messageBox.setMessage( "An error occurred when saving the file." + "\n" + e.getMessage() );
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
        boolean isIDE = PluginUtils.isIDEEnvironment();

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
                file.create( new ByteArrayInputStream( "".getBytes() ), true, null );
            }

            // Creating the new input for the editor
            FileEditorInput fei = new FileEditorInput( file );

            // Saving the file to disk
            saveEditorPages( monitor );
            saveConfiguration( fei, monitor );

            // Setting the new input to the editor
            setInput( fei );
        }
        else
        {
            Shell shell = getSite().getShell();

            // Open FileDialog
            FileDialog dialog = new FileDialog( shell, SWT.SAVE );

            String path = dialog.open();
            if ( path == null )
            {
                return false;
            }

            // Check whether file exists and if so, confirm overwrite
            final File externalFile = new File( path );
            if ( externalFile.exists() )
            {
                MessageDialog overwriteDialog = new MessageDialog( shell, "Overwrite", null, "Overwrite?",
                    MessageDialog.WARNING, new String[]
                        { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL }, 1 ); // 'No' is the default
                if ( overwriteDialog.open() != Window.OK )
                {
                    return false;
                }
            }

            // Saving the file to disk
            saveEditorPages( monitor );
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
}

/**
 * This IEditorInput is used to open files that are located in the local file system.
 * 
 * Inspired from org.eclipse.ui.internal.editors.text.NonExistingFileEditorInput.java
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
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
    public IPath getPath( Object element )
    {
        if ( element instanceof PathEditorInput )
        {
            PathEditorInput input = ( PathEditorInput ) element;
            return input.getPath();
        }

        return null;
    }
}
