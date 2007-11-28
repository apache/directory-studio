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

import org.apache.directory.studio.apacheds.configuration.Activator;
import org.apache.directory.studio.apacheds.configuration.model.ServerConfiguration;
import org.apache.directory.studio.apacheds.configuration.model.ServerConfigurationParser;
import org.apache.directory.studio.apacheds.configuration.model.ServerConfigurationWriter;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
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
                FileEditorInput fei = ( FileEditorInput ) input;

                ServerConfigurationParser parser = new ServerConfigurationParser();
                serverConfiguration = parser.parse( fei.getFile().getContents() );
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
            else if ( input instanceof ServerConfigurationEditorInput )
            {
                // The 'ServerConfigurationEditorInput' class is used when a
                // new Server Configuration File is created.
                serverConfiguration = ( ( ServerConfigurationEditorInput ) input ).getServerConfiguration();
                dirty = true;
            }

            // TODO What to do in the other cases ?

            //        else
            //        {
            //            try
            //            {
            //                ServerConfigurationParser parser = new ServerConfigurationParser();
            //                serverConfiguration = parser.parse( new FileInputStream( new File( input.getToolTipText() ) ) );
            //                serverConfiguration.setPath( input.getToolTipText() );
            //            }
            //            catch ( ServerConfigurationParserException e )
            //            {
            //                MessageBox messageBox = new MessageBox(
            //                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OK | SWT.ICON_ERROR );
            //                messageBox.setText( "Error!" );
            //                messageBox.setMessage( "An error occurred when reading the file." + "\n" + e.getMessage() );
            //                messageBox.open();
            //                return;
            //            }
            //            catch ( FileNotFoundException e )
            //            {
            //                MessageBox messageBox = new MessageBox(
            //                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OK | SWT.ICON_ERROR );
            //                messageBox.setText( "Error!" );
            //                messageBox.setMessage( "An error occurred when reading the file." + "\n" + e.getMessage() );
            //                messageBox.open();
            //                return;
            //            }
            //        }
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
        // Saving the Server Configuration
        monitor.beginTask( "Saving the Server Configuration", 5 );
        generalPage.save();
        monitor.worked( 1 );
        partitionsPage.save();
        monitor.worked( 1 );
        interceptorsPage.save();
        monitor.worked( 1 );
        extendedOperationsPage.save();
        monitor.worked( 1 );

        try
        {
            IEditorInput input = getEditorInput();
            String inputClassName = input.getClass().getName();
            if ( input instanceof FileEditorInput )
            // FileEditorInput class is used when the file is opened
            // from an project in the workspace.
            {
                // Saving the ServerConfiguration to disk
                FileEditorInput fei = ( FileEditorInput ) input;
                String xml = ServerConfigurationWriter.toXml( serverConfiguration );
                fei.getFile().setContents( new ByteArrayInputStream( xml.getBytes() ), true, true, monitor );
            }
            else if ( inputClassName.equals( "org.eclipse.ui.internal.editors.text.JavaFileEditorInput" )
                || inputClassName.equals( "org.eclipse.ui.ide.FileStoreEditorInput" ) )
            // The class 'org.eclipse.ui.internal.editors.text.JavaFileEditorInput'
            // is used when opening a file from the menu File > Open... in Eclipse 3.2.x
            // The class 'org.eclipse.ui.ide.FileStoreEditorInput' is used when
            // opening a file from the menu File > Open... in Eclipse 3.3.x
            {
                // Saving the ServerConfiguration to disk
                BufferedWriter outFile = new BufferedWriter( new FileWriter( input.getToolTipText() ) );
                String xml = ServerConfigurationWriter.toXml( serverConfiguration );
                outFile.write( xml );
                outFile.close();
            }
            else if ( input instanceof ServerConfigurationEditorInput )
            {
                // The 'ServerConfigurationEditorInput' class is used when a
                // new Server Configuration File is created.
                ServerConfigurationEditorInput serverConfigurationEditorInput = ( ServerConfigurationEditorInput ) input;

                // Checking if the ServerConfiguration has already been saved
                if ( serverConfigurationEditorInput.getPath() == null )
                {
                    FileDialog fd = new FileDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        SWT.SAVE );
                    fd.setText( "Select a file" );
                    fd.setFilterExtensions( new String[]
                        { "*.xml", "*.*" } );
                    fd.setFilterNames( new String[]
                        { "XML files", "All files" } );
                    String selectedFile = fd.open();
                    // selected == null if 'cancel' has been pushed
                    if ( selectedFile == null || "".equals( selectedFile ) )
                    {
                        monitor.setCanceled( true );
                        return;
                    }

                    // TODO Add the overwrite code...

                    serverConfigurationEditorInput.setPath( selectedFile );
                    setTitleToolTip( getEditorInput().getToolTipText() );
                }

                // Saving the ServerConfiguration to disk
                BufferedWriter outFile = new BufferedWriter( new FileWriter( serverConfigurationEditorInput.getPath() ) );
                String xml = ServerConfigurationWriter.toXml( serverConfiguration );
                outFile.write( xml );
                outFile.close();
            }

            monitor.worked( 1 );
            setDirty( false );
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


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#doSaveAs()
     */
    public void doSaveAs()
    {
        SaveAsDialog dialog = new SaveAsDialog( Activator.getDefault().getWorkbench().getActiveWorkbenchWindow()
            .getShell() );
        dialog.setOriginalName( "Copy of" );
        dialog.open();
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
