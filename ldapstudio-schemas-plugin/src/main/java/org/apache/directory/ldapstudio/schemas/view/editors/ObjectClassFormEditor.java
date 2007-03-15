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

package org.apache.directory.ldapstudio.schemas.view.editors;


import org.apache.directory.ldapstudio.schemas.Activator;
import org.apache.directory.ldapstudio.schemas.model.ObjectClass;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;


/**
 * This class is the ObjectClass Editor main class
 */
public class ObjectClassFormEditor extends FormEditor
{
    /** The logger */
    private static Logger logger = Logger.getLogger( ObjectClassFormEditor.class );

    /** The ID of the Editor */
    public static final String ID = Activator.PLUGIN_ID + ".view.objectClassEditor"; //$NON-NLS-1$

    /** The Overview page */
    private ObjectClassFormEditorOverviewPage overview;

    /** The Source Code page */
    private ObjectClassFormEditorSourceCodePage sourceCode;

    /** The dirty state flag */
    private boolean dirty = false;

    /** The original object class */
    private ObjectClass originalObjectClass;

    /** The object class used to save modifications */
    private ObjectClass modifiedObjectClass;

    /** The listener for page changed */
    private IPageChangedListener pageChangedListener = new IPageChangedListener()
    {
        public void pageChanged( PageChangedEvent event )
        {
            Object selectedPage = event.getSelectedPage();

            if ( selectedPage instanceof ObjectClassFormEditorOverviewPage )
            {
                if ( !sourceCode.canLeaveThePage() )
                {
                    notifyError( "The editor of the Source Code contains errors, you cannot return to the Overview page until these errors are fixed." );
                    return;
                }

                overview.refreshUI();
            }
            else if ( selectedPage instanceof ObjectClassFormEditorSourceCodePage )
            {
                if ( sourceCode.canLeaveThePage() )
                {
                    sourceCode.refreshUI();
                }
            }
        }
    };


    /**
     * Default constructor
     */
    public ObjectClassFormEditor()
    {
        super();
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormEditor#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     */
    @Override
    public void init( IEditorSite site, IEditorInput input ) throws PartInitException
    {
        setSite( site );
        setInput( input );
        setPartName( input.getName() );

        originalObjectClass = ( ( ObjectClassFormEditorInput ) getEditorInput() ).getObjectClass();
        originalObjectClass.setEditor( this );

        try
        {
            modifiedObjectClass = ( ObjectClass ) originalObjectClass.clone();
        }
        catch ( CloneNotSupportedException e )
        {
            // Will never occurr.
        }

        addPageChangedListener( pageChangedListener );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormEditor#dispose()
     */
    @Override
    public void dispose()
    {
        originalObjectClass.removeEditor( this );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
     */
    @Override
    protected void addPages()
    {
        try
        {
            overview = new ObjectClassFormEditorOverviewPage( this ); //$NON-NLS-1$ //$NON-NLS-2$
            addPage( overview );
            sourceCode = new ObjectClassFormEditorSourceCodePage( this ); //$NON-NLS-1$ //$NON-NLS-2$
            addPage( sourceCode );
        }
        catch ( PartInitException e )
        {
            logger.debug( "error when adding pages" ); //$NON-NLS-1$
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void doSave( IProgressMonitor monitor )
    {
        // Verifying if there is an error on the source code page
        if ( !sourceCode.canLeaveThePage() )
        {
            notifyError( "The editor of the Source Code contains errors, you cannot save the object class until these errors are fixed." );
            monitor.setCanceled( true );
            return;
        }

        originalObjectClass.update( modifiedObjectClass );

        setPartName( getEditorInput().getName() );
        if ( !monitor.isCanceled() )
        {
            setDirty( false );
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#doSaveAs()
     */
    @Override
    public void doSaveAs()
    {
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
     */
    @Override
    public boolean isSaveAsAllowed()
    {
        return false;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormEditor#isDirty()
     */
    @Override
    public boolean isDirty()
    {
        return dirty;
    }


    /**
     * Sets the dirty state of the editor
     * 
     * @param dirty
     *            the dirty state
     */
    public void setDirty( boolean dirty )
    {
        this.dirty = dirty;
        editorDirtyStateChanged();
    }


    /**
     * Gets the modified object class.
     *
     * @return
     *      the modified object class
     */
    public ObjectClass getModifiedObjectClass()
    {
        return modifiedObjectClass;
    }


    /**
     * Sets the modified object class.
     *
     * @param modifiedObjectClass
     *      the modified object class to set.
     */
    public void setModifiedObjectClass( ObjectClass modifiedObjectClass )
    {
        this.modifiedObjectClass = modifiedObjectClass;
    }


    /**
     * Opens an error dialog displaying the given message.
     *  
     * @param message
     *      the message to display
     */
    private void notifyError( String message )
    {
        MessageBox messageBox = new MessageBox( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OK
            | SWT.ICON_ERROR );
        messageBox.setMessage( message );
        messageBox.open();
    }
}
