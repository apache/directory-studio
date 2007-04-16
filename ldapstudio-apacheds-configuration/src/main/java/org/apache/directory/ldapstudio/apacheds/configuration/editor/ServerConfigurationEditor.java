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
import org.apache.directory.ldapstudio.apacheds.configuration.model.ServerConfigurationWriter;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
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
        ServerConfigurationWriter writer = new ServerConfigurationWriter();
        writer.write( ( ( ServerConfigurationEditorInput ) getEditorInput() ).getServerConfiguration() );
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
