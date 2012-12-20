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

package org.apache.directory.studio.schemaeditor.view.editors.objectclass;


import org.apache.directory.api.ldap.model.schema.MutableObjectClass;
import org.apache.directory.api.ldap.model.schema.ObjectClass;
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
 * This class is the ObjectClass Editor main class
 */
public class ObjectClassEditor extends FormEditor
{
    /** The logger */
    private static Logger logger = Logger.getLogger( ObjectClassEditor.class );

    /** The ID of the Editor */
    public static final String ID = PluginConstants.EDITOR_OBJECT_CLASS_ID;

    /** The editor */
    private ObjectClassEditor instance;

    // The pages
    private ObjectClassEditorOverviewPage overviewPage;
    private ObjectClassEditorSourceCodePage sourceCodePage;

    /** The dirty state flag */
    private boolean dirty = false;

    /** The original object class */
    private MutableObjectClass originalObjectClass;

    /** The object class used to save modifications */
    private MutableObjectClass modifiedObjectClass;

    /** The originalSchema */
    private Schema originalSchema;

    /** The listener for page changed */
    private IPageChangedListener pageChangedListener = new IPageChangedListener()
    {
        public void pageChanged( PageChangedEvent event )
        {
            Object selectedPage = event.getSelectedPage();

            if ( selectedPage instanceof ObjectClassEditorOverviewPage )
            {
                if ( !sourceCodePage.canLeaveThePage() )
                {
                    notifyError( Messages.getString( "ObjectClassEditor.CodeError" ) ); //$NON-NLS-1$
                    return;
                }

                overviewPage.refreshUI();
            }
            else if ( selectedPage instanceof ObjectClassEditorSourceCodePage )
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
        public void objectClassModified( ObjectClass oc )
        {
            if ( oc.equals( originalObjectClass ) )
            {
                // Updating the modified object class
                modifiedObjectClass = PluginUtils.getClone( originalObjectClass );

                // Refreshing the editor pages
                overviewPage.refreshUI();
                sourceCodePage.refreshUI();

                // Refreshing the part name (in case of a change in the name)
                setPartName( getEditorInput().getName() );
            }
        }


        public void objectClassRemoved( ObjectClass oc )
        {
            if ( oc.equals( originalObjectClass ) )
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
                // Updating the modified object class
                modifiedObjectClass = PluginUtils.getClone( originalObjectClass );

                // Refreshing the editor pages
                overviewPage.refreshUI();
                sourceCodePage.refreshUI();
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

        originalObjectClass = ( ( ObjectClassEditorInput ) getEditorInput() ).getObjectClass();
        modifiedObjectClass = PluginUtils.getClone( originalObjectClass );

        SchemaHandler schemaHandler = Activator.getDefault().getSchemaHandler();
        originalSchema = schemaHandler.getSchema( originalObjectClass.getSchemaName() );
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
            overviewPage = new ObjectClassEditorOverviewPage( this );
            addPage( overviewPage );
            sourceCodePage = new ObjectClassEditorSourceCodePage( this );
            addPage( sourceCodePage );
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
            notifyError( Messages.getString( "ObjectClassEditor.CodeErrorObject" ) ); //$NON-NLS-1$
            monitor.setCanceled( true );
            return;
        }

        Activator.getDefault().getSchemaHandler().modifyObjectClass( originalObjectClass, modifiedObjectClass );

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
     * Gets the original object class.
     *
     * @return
     *      the original object class
     */
    public ObjectClass getOriginalObjectClass()
    {
        return originalObjectClass;
    }


    /**
     * Gets the modified object class.
     *
     * @return
     *      the modified object class
     */
    public MutableObjectClass getModifiedObjectClass()
    {
        return modifiedObjectClass;
    }


    /**
     * Sets the modified object class.
     *
     * @param modifiedObjectClass
     *      the modified object class to set.
     */
    public void setModifiedObjectClass( MutableObjectClass modifiedObjectClass )
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
        MessageDialog.openError( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            Messages.getString( "ObjectClassEditor.Error" ), message ); //$NON-NLS-1$
    }
}
