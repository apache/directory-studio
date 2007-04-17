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
package org.apache.directory.ldapstudio.apacheds.configuration.editor;


import org.apache.directory.ldapstudio.apacheds.configuration.Activator;
import org.apache.directory.ldapstudio.apacheds.configuration.model.ServerConfiguration;
import org.apache.directory.ldapstudio.apacheds.configuration.model.ServerConfigurationWriter;
import org.apache.directory.ldapstudio.apacheds.configuration.model.ServerConfigurationWriterException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;


/**
 * This class implements the Server Configuration Editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ServerConfigurationEditor extends FormEditor
{
    /** The Editor ID */
    public static final String ID = "org.apache.directory.ldapstudio.apacheds.configuration.editor";

    /** The editor input */
    private IEditorInput input;

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
        this.input = input;
        setPartName( input.getName() );
        serverConfiguration = ( ( ServerConfigurationEditorInput ) input ).getServerConfiguration();
        dirty = serverConfiguration.getPath() == null;
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
        generalPage.save();
        monitor.worked( 1 );
        partitionsPage.save();
        monitor.worked( 1 );
        interceptorsPage.save();
        monitor.worked( 1 );
        extendedOperationsPage.save();
        monitor.worked( 1 );

        // Checking if the ServerConfiguration is already existing or if it's a new file.
        if ( serverConfiguration.getPath() == null )
        {
            FileDialog fd = new FileDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.SAVE );
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

            serverConfiguration.setPath( selectedFile );
            setTitleToolTip( input.getToolTipText() );
        }

        // Saving the ServerConfiguration to disk
        try
        {
            ServerConfigurationWriter writer = new ServerConfigurationWriter();
            writer.write( serverConfiguration );
        }
        catch ( ServerConfigurationWriterException e )
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

        monitor.worked( 1 );
        setDirty( false );
        monitor.done();
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#doSaveAs()
     */
    public void doSaveAs()
    {
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
     */
    public boolean isSaveAsAllowed()
    {
        return false;
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
}
