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
import org.apache.directory.ldapstudio.schemas.model.AttributeType;
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
 * This class is the Attribute Type Editor main class
 */
public class AttributeTypeFormEditor extends FormEditor
{
    /** The logger */
    private static Logger logger = Logger.getLogger( AttributeTypeFormEditor.class );

    /** The ID of the Editor */
    public static final String ID = Activator.PLUGIN_ID + ".view.attributeTypeEditor"; //$NON-NLS-1$

    /** The Overview page */
    private AttributeTypeFormEditorOverviewPage overview;

    /** The Source Code page */
    private AttributeTypeFormEditorSourceCodePage sourceCode;

    /** The Used By page */
    private AttributeTypeFormEditorUsedByPage usedBy;

    /** The dirty state flag */
    private boolean dirty = false;

    /** The original attribute type */
    private AttributeType originalAttributeType;

    /** The attribute type used to save modifications */
    private AttributeType modifiedAttributeType;

    /** The listener for page changed */
    private IPageChangedListener pageChangedListener = new IPageChangedListener()
    {
        public void pageChanged( PageChangedEvent event )
        {
            Object selectedPage = event.getSelectedPage();

            if ( selectedPage instanceof AttributeTypeFormEditorOverviewPage )
            {
                if ( !sourceCode.canLeaveThePage() )
                {
                    notifyError( "The editor of the Source Code contains errors, you cannot return to the Overview page until these errors are fixed." );
                    return;
                }

                overview.refreshUI();
            }
            else if ( selectedPage instanceof AttributeTypeFormEditorSourceCodePage )
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
    public AttributeTypeFormEditor()
    {
        super();
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.forms.editor.FormEditor#init(org.eclipse.ui.IEditorSite,
     *      org.eclipse.ui.IEditorInput)
     */
    @Override
    public void init( IEditorSite site, IEditorInput input ) throws PartInitException
    {
        setSite( site );
        setInput( input );
        setPartName( input.getName() );

        originalAttributeType = ( ( AttributeTypeFormEditorInput ) getEditorInput() ).getAttributeType();
        originalAttributeType.setEditor( this );

        try
        {
            modifiedAttributeType = ( AttributeType ) originalAttributeType.clone();
        }
        catch ( CloneNotSupportedException e )
        {
            // Will never occurr.
        }

        addPageChangedListener( pageChangedListener );
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.forms.editor.FormEditor#dispose()
     */
    @Override
    public void dispose()
    {
        originalAttributeType.removeEditor( this );
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
     */
    @Override
    protected void addPages()
    {
        try
        {
            overview = new AttributeTypeFormEditorOverviewPage( this );
            addPage( overview );
            sourceCode = new AttributeTypeFormEditorSourceCodePage( this );
            addPage( sourceCode );
            usedBy = new AttributeTypeFormEditorUsedByPage( this );
            addPage( usedBy );
        }
        catch ( PartInitException e )
        {
            logger.debug( "error when adding pages" ); //$NON-NLS-1$
        }
    }


    /*
     * (non-Javadoc)
     * 
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

        originalAttributeType.update( modifiedAttributeType );

        setPartName( getEditorInput().getName() );
        if ( !monitor.isCanceled() )
        {
            setDirty( false );
        }
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.EditorPart#doSaveAs()
     */
    @Override
    public void doSaveAs()
    {
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
     */
    @Override
    public boolean isSaveAsAllowed()
    {
        return false;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.forms.editor.FormEditor#isDirty()
     */
    @Override
    public boolean isDirty()
    {
        return this.dirty;
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
     * Gets the original attribute type.
     *
     * @return
     *      the original attribute type
     */
    public AttributeType getOriginalAttributeType()
    {
        return originalAttributeType;
    }


    /**
     * Gets the modified attribute type.
     *
     * @return
     *      the modified attribute type
     */
    public AttributeType getModifiedAttributeType()
    {
        return modifiedAttributeType;
    }


    /**
     * Sets the modified attribute type.
     *
     * @param modifiedAttributeType
     *      the modified attribute type to set.
     */
    public void setModifiedAttributeType( AttributeType modifiedAttributeType )
    {
        this.modifiedAttributeType = modifiedAttributeType;
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
