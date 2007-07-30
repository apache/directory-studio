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

package org.apache.directory.studio.apacheds.schemaeditor.view.editors.attributetype;


import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.PluginUtils;
import org.apache.directory.studio.apacheds.schemaeditor.controller.AttributeTypeAdapter;
import org.apache.directory.studio.apacheds.schemaeditor.controller.AttributeTypeListener;
import org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaHandlerAdapter;
import org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaHandlerListener;
import org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.Schema;
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
 * This class represent the Attribute Type Editor.
 * <p>
 * It is used to edit the values of an attribute type.
 */
public class AttributeTypeEditor extends FormEditor
{
    /** The logger */
    private static Logger logger = Logger.getLogger( AttributeTypeEditor.class );

    /** The ID of the Editor */
    public static final String ID = Activator.PLUGIN_ID + ".view.attributeTypeEditor"; //$NON-NLS-1$

    /** The dirty state flag */
    private boolean dirty = false;

    // The pages
    private AttributeTypeEditorOverviewPage overview;
    private AttributeTypeEditorSourceCodePage sourceCode;
    private AttributeTypeEditorUsedByPage usedBy;

    /** The original attribute type */
    private AttributeTypeImpl originalAttributeType;

    /** The attribute type used to save modifications */
    private AttributeTypeImpl modifiedAttributeType;

    /** The originalSchema */
    private Schema originalSchema;

    /** The listener for page changed */
    private IPageChangedListener pageChangedListener = new IPageChangedListener()
    {
        public void pageChanged( PageChangedEvent event )
        {
            Object selectedPage = event.getSelectedPage();

            if ( selectedPage instanceof AttributeTypeEditorOverviewPage )
            {
                if ( !sourceCode.canLeaveThePage() )
                {
                    notifyError( "AttributeTypeEditor.Souce_Code_Error_cannot_return_to_Overview_page" ); //TODO
                    return;
                }

                overview.refreshUI();
            }
            else if ( selectedPage instanceof AttributeTypeEditorSourceCodePage )
            {
                if ( sourceCode.canLeaveThePage() )
                {
                    sourceCode.refreshUI();
                }
            }
        }
    };

    /** The editor */
    private AttributeTypeEditor instance;

    /** The attribute type listener */
    private AttributeTypeListener attributeTypeListener = new AttributeTypeAdapter()
    {
        public void attributeTypeRemoved()
        {
            getEditorSite().getPage().closeEditor( instance, false );
        }
    };

    /** The SchemaHandler listener */
    private SchemaHandlerListener schemaHandlerListener = new SchemaHandlerAdapter()
    {
        public void schemaRemoved( Schema schema )
        {
            if ( schema.equals( originalSchema )  )
            {
                getEditorSite().getPage().closeEditor( instance, false );
            }
        }
    };


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormEditor#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     */
    public void init( IEditorSite site, IEditorInput input ) throws PartInitException
    {
        instance = this;

        setSite( site );
        setInput( input );
        setPartName( input.getName() );

        originalAttributeType = ( ( AttributeTypeEditorInput ) getEditorInput() ).getAttributeType();
        modifiedAttributeType = ( AttributeTypeImpl ) PluginUtils.getClone( originalAttributeType );

        SchemaHandler schemaHandler = Activator.getDefault().getSchemaHandler();
        originalSchema = schemaHandler.getSchema( originalAttributeType.getSchema() );
        schemaHandler.addListener( originalAttributeType, attributeTypeListener );
        schemaHandler.addListener( schemaHandlerListener );

        addPageChangedListener( pageChangedListener );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormEditor#dispose()
     */
    public void dispose()
    {
        SchemaHandler schemaHandler = Activator.getDefault().getSchemaHandler();
        schemaHandler.removeListener( originalAttributeType, attributeTypeListener );
        schemaHandler.removeListener( schemaHandlerListener );

        super.dispose();
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
     */
    protected void addPages()
    {
        try
        {
            overview = new AttributeTypeEditorOverviewPage( this );
            addPage( overview );
            sourceCode = new AttributeTypeEditorSourceCodePage( this );
            addPage( sourceCode );
            usedBy = new AttributeTypeEditorUsedByPage( this );
            addPage( usedBy );
        }
        catch ( PartInitException e )
        {
            logger.debug( "error when adding pages" ); //$NON-NLS-1$
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void doSave( IProgressMonitor monitor )
    {
        // Verifying if there is an error on the source code page
        if ( !sourceCode.canLeaveThePage() )
        {
            notifyError( "AttributeTypeEditor.Souce_Code_Error_cannot_save_object_class" ); //TODO
            monitor.setCanceled( true );
            return;
        }

        Activator.getDefault().getSchemaHandler().modifyAttributeType( originalAttributeType, modifiedAttributeType );

        setPartName( getEditorInput().getName() );
        if ( !monitor.isCanceled() )
        {
            setDirty( false );
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#doSaveAs()
     */
    public void doSaveAs()
    {
        // Nothing to do.
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
    public AttributeTypeImpl getOriginalAttributeType()
    {
        return originalAttributeType;
    }


    /**
     * Gets the modified attribute type.
     *
     * @return
     *      the modified attribute type
     */
    public AttributeTypeImpl getModifiedAttributeType()
    {
        return modifiedAttributeType;
    }


    /**
     * Sets the modified attribute type.
     *
     * @param modifiedAttributeType
     *      the modified attribute type to set.
     */
    public void setModifiedAttributeType( AttributeTypeImpl modifiedAttributeType )
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
