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

package org.apache.directory.studio.schemaeditor.view.editors.attributetype;


import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.api.ldap.model.schema.MutableAttributeType;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.PluginUtils;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandlerAdapter;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.PageChangedEvent;
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
    public static final String ID = PluginConstants.EDITOR_ATTRIBUTE_TYPE_ID;

    /** The editor */
    private AttributeTypeEditor instance;

    /** The dirty state flag */
    private boolean dirty = false;

    // The pages
    private AttributeTypeEditorOverviewPage overviewPage;
    private AttributeTypeEditorSourceCodePage sourceCodePage;
    private AttributeTypeEditorUsedByPage usedByPage;

    /** The original attribute type */
    private MutableAttributeType originalAttributeType;

    /** The attribute type used to save modifications */
    private MutableAttributeType modifiedAttributeType;

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
                if ( !sourceCodePage.canLeaveThePage() )
                {
                    notifyError( Messages.getString( "AttributeTypeEditor.CodeErrors" ) ); //$NON-NLS-1$
                    return;
                }

                overviewPage.refreshUI();
            }
            else if ( selectedPage instanceof AttributeTypeEditorSourceCodePage )
            {
                if ( sourceCodePage.canLeaveThePage() )
                {
                    sourceCodePage.refreshUI();
                }
            }
        }
    };

    /** The SchemaHandler listener */
    private SchemaHandlerListener schemaHandlerListener = new SchemaHandlerAdapter()
    {
        public void attributeTypeModified( AttributeType at )
        {
            if ( at.equals( originalAttributeType ) )
            {
                // Updating the modified attribute type
                modifiedAttributeType = PluginUtils.getClone( originalAttributeType );

                // Refreshing the editor pages
                overviewPage.refreshUI();
                sourceCodePage.refreshUI();
                usedByPage.refreshUI();

                // Refreshing the part name (in case of a change in the name)
                setPartName( getEditorInput().getName() );
            }
        }


        public void attributeTypeRemoved( AttributeType at )
        {
            if ( at.equals( originalAttributeType ) )
            {
                getEditorSite().getPage().closeEditor( instance, false );
            }
        }


        public void schemaRemoved( Schema schema )
        {
            if ( schema.equals( originalSchema ) )
            {
                getEditorSite().getPage().closeEditor( instance, false );
            }
        }


        public void schemaRenamed( Schema schema )
        {
            if ( schema.equals( originalSchema ) )
            {
                // Updating the modified attribute type
                modifiedAttributeType = PluginUtils.getClone( originalAttributeType );

                // Refreshing the editor pages
                overviewPage.refreshUI();
                sourceCodePage.refreshUI();
                usedByPage.refreshUI();
            }
        }
    };


    /**
     * {@inheritDoc}
     */
    public void init( IEditorSite site, IEditorInput input ) throws PartInitException
    {
        super.init( site, input );

        instance = this;

        setSite( site );
        setInput( input );
        setPartName( input.getName() );

        originalAttributeType = (MutableAttributeType)( ( AttributeTypeEditorInput ) getEditorInput() ).getAttributeType();
        modifiedAttributeType = PluginUtils.getClone( originalAttributeType );

        SchemaHandler schemaHandler = Activator.getDefault().getSchemaHandler();
        originalSchema = schemaHandler.getSchema( originalAttributeType.getSchemaName() );
        schemaHandler.addListener( schemaHandlerListener );

        addPageChangedListener( pageChangedListener );
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        SchemaHandler schemaHandler = Activator.getDefault().getSchemaHandler();
        schemaHandler.removeListener( schemaHandlerListener );

        super.dispose();
    }


    /**
     * {@inheritDoc}
     */
    protected void addPages()
    {
        try
        {
            overviewPage = new AttributeTypeEditorOverviewPage( this );
            addPage( overviewPage );
            sourceCodePage = new AttributeTypeEditorSourceCodePage( this );
            addPage( sourceCodePage );
            usedByPage = new AttributeTypeEditorUsedByPage( this );
            addPage( usedByPage );
        }
        catch ( PartInitException e )
        {
            logger.debug( "error when adding pages" ); //$NON-NLS-1$
        }
    }


    /**
     * {@inheritDoc}
     */
    public void doSave( IProgressMonitor monitor )
    {
        // Verifying if there is an error on the source code page
        if ( !sourceCodePage.canLeaveThePage() )
        {
            notifyError( Messages.getString( "AttributeTypeEditor.AttributeErrors" ) ); //$NON-NLS-1$
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


    /**
     * {@inheritDoc}
     */
    public void doSaveAs()
    {
        // Nothing to do.
    }


    /**
     * {@inheritDoc}
     */
    public boolean isSaveAsAllowed()
    {
        return false;
    }


    /**
     * {@inheritDoc}
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
    public MutableAttributeType getModifiedAttributeType()
    {
        return modifiedAttributeType;
    }


    /**
     * Sets the modified attribute type.
     *
     * @param modifiedAttributeType
     *      the modified attribute type to set.
     */
    public void setModifiedAttributeType( MutableAttributeType modifiedAttributeType )
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
        MessageDialog.openError( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            Messages.getString( "AttributeTypeEditor.Error" ), message ); //$NON-NLS-1$
    }
}
