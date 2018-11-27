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
package org.apache.directory.studio.schemaeditor.controller.actions;


import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.view.editors.attributetype.AttributeTypeEditor;
import org.apache.directory.studio.schemaeditor.view.editors.objectclass.ObjectClassEditor;
import org.apache.directory.studio.schemaeditor.view.editors.schema.SchemaEditor;
import org.apache.directory.studio.schemaeditor.view.views.SchemaView;
import org.apache.directory.studio.schemaeditor.view.views.SchemaViewContentProvider;
import org.apache.directory.studio.schemaeditor.view.wrappers.AttributeTypeWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.ObjectClassWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.SchemaWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.TreeNode;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;


/**
 * This action is used to link the with the view with the frontmost editor.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LinkWithEditorSchemaViewAction extends Action implements IWorkbenchWindowActionDelegate
{
    /** The String for storing the checked state of the action */
    private static final String LINK_WITH_EDITOR_SCHEMAS_VIEW_DS_KEY = LinkWithEditorSchemaViewAction.class.getName()
        + ".dialogsettingkey"; //$NON-NLS-1$

    /** The associated view */
    private SchemaView view;

    /** The listener listening on changes on editors */
    private IPartListener2 editorListener = new IPartListener2()
    {

        /**
         * {@inheritDoc}
         */
        public void partVisible( IWorkbenchPartReference partRef )
        {
            IWorkbenchPart part = partRef.getPart( true );

            if ( part instanceof ObjectClassEditor )
            {
                view.getSite().getPage().removePostSelectionListener( viewListener );
                linkViewWithEditor( ( ( ObjectClassEditor ) part ).getOriginalObjectClass() );
                view.getSite().getPage().addPostSelectionListener( viewListener );
            }
            else if ( part instanceof AttributeTypeEditor )
            {
                view.getSite().getPage().removePostSelectionListener( viewListener );
                linkViewWithEditor( ( ( AttributeTypeEditor ) part ).getOriginalAttributeType() );
                view.getSite().getPage().addPostSelectionListener( viewListener );
            }
            else if ( part instanceof SchemaEditor )
            {
                view.getSite().getPage().removePostSelectionListener( viewListener );
                linkViewWithEditor( ( ( SchemaEditor ) part ).getSchema() );
                view.getSite().getPage().addPostSelectionListener( viewListener );
            }
        }


        /**
         * {@inheritDoc}
         */
        public void partActivated( IWorkbenchPartReference partRef )
        {
        }


        /**
         * {@inheritDoc}
         */
        public void partClosed( IWorkbenchPartReference partRef )
        {
        }


        /**
         * {@inheritDoc}
         */
        public void partDeactivated( IWorkbenchPartReference partRef )
        {
        }


        /**
         * {@inheritDoc}
         */
        public void partHidden( IWorkbenchPartReference partRef )
        {
        }


        /**
         * {@inheritDoc}
         */
        public void partInputChanged( IWorkbenchPartReference partRef )
        {
        }


        /**
         * {@inheritDoc}
         */
        public void partOpened( IWorkbenchPartReference partRef )
        {
        }


        /**
         * {@inheritDoc}
         */
        public void partBroughtToTop( IWorkbenchPartReference partRef )
        {
        }
    };

    /** The listener listening on changes on the view */
    private ISelectionListener viewListener = new ISelectionListener()
    {
        /**
         * {@inheritDoc}
         */
        public void selectionChanged( IWorkbenchPart part, ISelection selection )
        {
            IStructuredSelection iSelection = ( IStructuredSelection ) selection;

            Object selectedObject = iSelection.getFirstElement();

            if ( ( selectedObject instanceof SchemaWrapper ) || ( selectedObject instanceof ObjectClassWrapper )
                || ( selectedObject instanceof AttributeTypeWrapper ) )
            {
                linkEditorWithView( ( TreeNode ) selectedObject );
            }
        }
    };


    /**
     * Creates a new instance of ExportSchemasAsXmlAction.
     */
    public LinkWithEditorSchemaViewAction( SchemaView view )
    {
        super( Messages.getString( "LinkWithEditorSchemaViewAction.LinkEditorAction" ), AS_CHECK_BOX ); //$NON-NLS-1$
        setToolTipText( Messages.getString( "LinkWithEditorSchemaViewAction.LinkEditorToolTip" ) ); //$NON-NLS-1$
        setImageDescriptor( Activator.getDefault().getImageDescriptor( PluginConstants.IMG_LINK_WITH_EDITOR ) );
        setEnabled( false );
        this.view = view;

        // Setting up the default key value (if needed)
        if ( Activator.getDefault().getDialogSettings().get( LINK_WITH_EDITOR_SCHEMAS_VIEW_DS_KEY ) == null )
        {
            Activator.getDefault().getDialogSettings().put( LINK_WITH_EDITOR_SCHEMAS_VIEW_DS_KEY, false );
        }

        // Setting state from the dialog settings
        setChecked( Activator.getDefault().getDialogSettings().getBoolean( LINK_WITH_EDITOR_SCHEMAS_VIEW_DS_KEY ) );

        // Enabling the listeners
        if ( isChecked() )
        {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener( editorListener );
            view.getSite().getPage().addPostSelectionListener( viewListener );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        setChecked( isChecked() );
        Activator.getDefault().getDialogSettings().put( LINK_WITH_EDITOR_SCHEMAS_VIEW_DS_KEY, isChecked() );

        if ( isChecked() ) // Enabling the listeners
        {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener( editorListener );

            IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                .getActiveEditor();
            if ( activeEditor instanceof ObjectClassEditor )
            {
                view.getSite().getPage().removePostSelectionListener( viewListener );
                linkViewWithEditor( ( ( ObjectClassEditor ) activeEditor ).getOriginalObjectClass() );
                view.getSite().getPage().addPostSelectionListener( viewListener );
            }
            else if ( activeEditor instanceof AttributeTypeEditor )
            {
                view.getSite().getPage().removePostSelectionListener( viewListener );
                linkViewWithEditor( ( ( AttributeTypeEditor ) activeEditor ).getOriginalAttributeType() );
                view.getSite().getPage().addPostSelectionListener( viewListener );
            }
            else if ( activeEditor instanceof SchemaEditor )
            {
                view.getSite().getPage().removePostSelectionListener( viewListener );
                linkViewWithEditor( ( ( SchemaEditor ) activeEditor ).getSchema() );
                view.getSite().getPage().addPostSelectionListener( viewListener );
            }

            view.getSite().getPage().addPostSelectionListener( viewListener );
        }
        else
        // Disabling the listeners
        {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().removePartListener( editorListener );
            view.getSite().getPage().removePostSelectionListener( viewListener );
        }
    }


    /**
     * Links the view with the right editor
     *
     * @param o
     *      the object
     */
    private void linkViewWithEditor( Object o )
    {
        TreeNode wrapper = ( ( SchemaViewContentProvider ) view.getViewer().getContentProvider() ).getWrapper( o );
        if ( wrapper != null )
        {
            view.getViewer().setSelection( new StructuredSelection( wrapper ) );
        }
    }


    /**
     * Links the editor to the view
     *
     * @param wrapper
     *      the selected element in the view
     */
    private void linkEditorWithView( TreeNode wrapper )
    {
        IEditorReference[] references = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .getEditorReferences();

        for ( IEditorReference reference : references )
        {
            IWorkbenchPart workbenchPart = reference.getPart( true );

            if ( ( workbenchPart instanceof ObjectClassEditor ) && ( wrapper instanceof ObjectClassWrapper ) )
            {
                ObjectClassEditor editor = ( ObjectClassEditor ) workbenchPart;
                ObjectClassWrapper ocw = ( ObjectClassWrapper ) wrapper;
                if ( editor.getOriginalObjectClass().equals( ocw.getObjectClass() ) )
                {
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().bringToTop( workbenchPart );
                    return;
                }
            }
            else if ( ( workbenchPart instanceof AttributeTypeEditor ) && ( wrapper instanceof AttributeTypeWrapper ) )
            {
                AttributeTypeEditor editor = ( AttributeTypeEditor ) workbenchPart;
                AttributeTypeWrapper atw = ( AttributeTypeWrapper ) wrapper;
                if ( editor.getOriginalAttributeType().equals( atw.getAttributeType() ) )
                {
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().bringToTop( workbenchPart );
                    return;
                }
            }
            else if ( ( workbenchPart instanceof SchemaEditor ) && ( wrapper instanceof SchemaWrapper ) )
            {
                SchemaEditor editor = ( SchemaEditor ) workbenchPart;
                SchemaWrapper sw = ( SchemaWrapper ) wrapper;
                if ( editor.getSchema().equals( sw.getSchema() ) )
                {
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().bringToTop( workbenchPart );
                    return;
                }
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void run( IAction action )
    {
        run();
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        // Nothing to do
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbenchWindow window )
    {
        // Nothing to do
    }


    /**
     * {@inheritDoc}
     */
    public void selectionChanged( IAction action, ISelection selection )
    {
        // Nothing to do
    }
}
