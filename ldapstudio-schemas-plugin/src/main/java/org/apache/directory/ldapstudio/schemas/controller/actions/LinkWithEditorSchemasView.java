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
package org.apache.directory.ldapstudio.schemas.controller.actions;


import org.apache.directory.ldapstudio.schemas.Activator;
import org.apache.directory.ldapstudio.schemas.model.AttributeType;
import org.apache.directory.ldapstudio.schemas.model.ObjectClass;
import org.apache.directory.ldapstudio.schemas.model.SchemaPool;
import org.apache.directory.ldapstudio.schemas.view.IImageKeys;
import org.apache.directory.ldapstudio.schemas.view.editors.AttributeTypeFormEditor;
import org.apache.directory.ldapstudio.schemas.view.editors.ObjectClassFormEditor;
import org.apache.directory.ldapstudio.schemas.view.editors.SchemaFormEditor;
import org.apache.directory.ldapstudio.schemas.view.viewers.SchemasView;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.AttributeTypeWrapper;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.DisplayableTreeElement;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.IntermediateNode;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ObjectClassWrapper;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.SchemaWrapper;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class implements the Link With Editor Action for the Schemas View
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class LinkWithEditorSchemasView extends Action
{
    /** The String for storing the checked state of the action */
    private static final String LINK_WITH_EDITOR_SCHEMAS_VIEW_DS_KEY = LinkWithEditorSchemasView.class.getName()
        + ".dialogsettingkey";

    /** The associated view */
    private SchemasView schemasView;

    /** The listener listening on changes on editors */
    private IPartListener2 editorListener = new IPartListener2()
    {
        /* (non-Javadoc)
         * @see org.eclipse.ui.IPartListener2#partBroughtToTop(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partBroughtToTop( IWorkbenchPartReference partRef )
        {
            String id = partRef.getId();

            if ( ( id.equals( ObjectClassFormEditor.ID ) || ( id.equals( AttributeTypeFormEditor.ID ) ) ) )
            {
                schemasView.getSite().getPage().removePostSelectionListener( SchemasView.ID, viewListener );
                linkViewWithEditor( partRef.getPartName(), id );
                schemasView.getSite().getPage().addPostSelectionListener( SchemasView.ID, viewListener );
            }
        }


        /* (non-Javadoc)
         * @see org.eclipse.ui.IPartListener2#partActivated(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partActivated( IWorkbenchPartReference partRef )
        {
            String id = partRef.getId();

            if ( ( id.equals( ObjectClassFormEditor.ID ) || ( id.equals( AttributeTypeFormEditor.ID ) ) ) )
            {
                schemasView.getSite().getPage().removePostSelectionListener( SchemasView.ID, viewListener );
                linkViewWithEditor( partRef.getPartName(), id );
                schemasView.getSite().getPage().addPostSelectionListener( SchemasView.ID, viewListener );
            }
        }


        /* (non-Javadoc)
         * @see org.eclipse.ui.IPartListener2#partClosed(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partClosed( IWorkbenchPartReference partRef )
        {
        }


        /* (non-Javadoc)
         * @see org.eclipse.ui.IPartListener2#partDeactivated(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partDeactivated( IWorkbenchPartReference partRef )
        {
        }


        /* (non-Javadoc)
         * @see org.eclipse.ui.IPartListener2#partHidden(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partHidden( IWorkbenchPartReference partRef )
        {
        }


        /* (non-Javadoc)
         * @see org.eclipse.ui.IPartListener2#partInputChanged(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partInputChanged( IWorkbenchPartReference partRef )
        {
        }


        /* (non-Javadoc)
         * @see org.eclipse.ui.IPartListener2#partOpened(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partOpened( IWorkbenchPartReference partRef )
        {
        }


        /* (non-Javadoc)
         * @see org.eclipse.ui.IPartListener2#partVisible(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partVisible( IWorkbenchPartReference partRef )
        {
        }
    };

    /** The listener listening on changes on the view */
    private ISelectionListener viewListener = new ISelectionListener()
    {
        /* (non-Javadoc)
         * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
         */
        public void selectionChanged( IWorkbenchPart part, ISelection selection )
        {
            ITreeSelection iSelection = ( ITreeSelection ) selection;

            Object selectedObject = iSelection.getFirstElement();

            if ( ( selectedObject instanceof SchemaWrapper ) || ( selectedObject instanceof ObjectClassWrapper )
                || ( selectedObject instanceof AttributeTypeWrapper ) )
            {
                linkEditorWithView( ( DisplayableTreeElement ) selectedObject );
            }
        }
    };


    /**
     * Creates a new instance of LinkWithEditorSchemasView.
     *
     * @param view
     *      the associated view
     */
    public LinkWithEditorSchemasView( SchemasView view )
    {
        super( "Link with Editor", AS_CHECK_BOX );
        super.setActionDefinitionId( Activator.PLUGIN_ID + "linkwitheditorschemasview" );
        super.setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
            IImageKeys.LINK_WITH_EDITOR ) );
        super.setEnabled( true );
        schemasView = view;

        // Setting up the default key value (if needed)
        if ( Activator.getDefault().getDialogSettings().get( LINK_WITH_EDITOR_SCHEMAS_VIEW_DS_KEY ) == null )
        {
            Activator.getDefault().getDialogSettings().put( LINK_WITH_EDITOR_SCHEMAS_VIEW_DS_KEY, false );
        }

        // Setting state from the dialog settings
        super
            .setChecked( Activator.getDefault().getDialogSettings().getBoolean( LINK_WITH_EDITOR_SCHEMAS_VIEW_DS_KEY ) );

        // Enabling the listeners
        if ( isChecked() )
        {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener( editorListener );
            schemasView.getSite().getPage().addPostSelectionListener( SchemasView.ID, viewListener );
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#run()
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
            if ( activeEditor instanceof ObjectClassFormEditor )
            {
                ObjectClassFormEditor editor = ( ObjectClassFormEditor ) activeEditor;
                linkViewWithEditor( editor.getPartName(), ObjectClassFormEditor.ID );
            }
            else if ( activeEditor instanceof AttributeTypeFormEditor )
            {
                AttributeTypeFormEditor editor = ( AttributeTypeFormEditor ) activeEditor;
                linkViewWithEditor( editor.getPartName(), AttributeTypeFormEditor.ID );
            }

            schemasView.getSite().getPage().addPostSelectionListener( SchemasView.ID, viewListener );
        }
        else
        // Disabling the listeners
        {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().removePartListener( editorListener );
            schemasView.getSite().getPage().removePostSelectionListener( SchemasView.ID, viewListener );
        }
    }


    /**
     * Links the view with the right editor
     *
     * @param editorName
     *      the name of the editor
     * @param editorID
     *      the id of the editor
     */
    private void linkViewWithEditor( String editorName, String editorID )
    {
        StructuredSelection structuredSelection = null;
        DisplayableTreeElement wrapper = null;

        // Only editors for attribute types and object class are accepted
        if ( editorID.equals( AttributeTypeFormEditor.ID ) )
        {
            AttributeType at = SchemaPool.getInstance().getAttributeType( editorName );
            wrapper = new AttributeTypeWrapper( at, null );
            structuredSelection = new StructuredSelection( wrapper );

            schemasView.getViewer().setSelection( structuredSelection, true );
        }
        else if ( editorID.equals( ObjectClassFormEditor.ID ) )
        {
            ObjectClass oc = SchemaPool.getInstance().getObjectClass( editorName );
            wrapper = new ObjectClassWrapper( oc, null );
            structuredSelection = new StructuredSelection( wrapper );
        }
        else
        {
            // If the editor isn't an attribute type editor or object class editor, we return
            return;
        }

        Object foundItem = schemasView.getViewer().testFindItem( wrapper );
        if ( foundItem != null ) // The node we are looking for is already loaded in the TreeViewer
        {
            schemasView.getViewer().setSelection( structuredSelection, true );
        }
        else
        // The node we are looking for is not yet loaded in the TreeViewer, we have to find and load it.
        {
            DisplayableTreeElement foundElement = schemasView.findElementInTree( wrapper );

            if ( foundElement != null )
            {
                expandFromTopToBottom( foundElement );
                schemasView.getViewer().setSelection( structuredSelection );
            }
        }
    }


    /**
     * Expands from top to bottom the element and its successive parent (if needed)
     *
     * @param element
     *      the bottom element
     */
    private void expandFromTopToBottom( Object element )
    {
        if ( element instanceof SchemaWrapper )
        {
            SchemaWrapper schemaWrapper = ( SchemaWrapper ) element;
            if ( !schemasView.getViewer().getExpandedState( schemaWrapper ) )
            {
                schemasView.getViewer().setExpandedState( schemaWrapper, true );
            }
        }
        else if ( element instanceof ObjectClassWrapper )
        {
            ObjectClassWrapper objectClassWrapper = ( ObjectClassWrapper ) element;
            expandFromTopToBottom( objectClassWrapper.getParent() );
            schemasView.getViewer().setExpandedState( objectClassWrapper, true );
        }
        else if ( element instanceof AttributeTypeWrapper )
        {
            AttributeTypeWrapper attributeTypeWrapper = ( AttributeTypeWrapper ) element;
            expandFromTopToBottom( attributeTypeWrapper.getParent() );
            schemasView.getViewer().setExpandedState( attributeTypeWrapper, true );
        }
        else if ( element instanceof IntermediateNode )
        {
            IntermediateNode intermediateNode = ( IntermediateNode ) element;
            expandFromTopToBottom( intermediateNode.getParent() );
            schemasView.getViewer().setExpandedState( intermediateNode, true );
        }
    }


    /**
     * Links the editor to the view
     *
     * @param wrapper
     *      the selected element in the view
     */
    private void linkEditorWithView( DisplayableTreeElement wrapper )
    {
        IEditorReference[] editorReferences = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .getEditorReferences();

        for ( int i = 0; i < editorReferences.length; i++ )
        {
            IEditorReference reference = editorReferences[i];
            IWorkbenchPart workbenchPart = reference.getPart( true );

            if ( ( ( workbenchPart instanceof ObjectClassFormEditor ) && ( wrapper instanceof ObjectClassWrapper ) && ( reference.getPartName().equals( ( ( ObjectClassWrapper ) wrapper).getMyObjectClass().getNames()[0] ) ) )
              || ( ( workbenchPart instanceof AttributeTypeFormEditor ) && ( wrapper instanceof AttributeTypeWrapper ) && ( reference.getPartName().equals( ( ( AttributeTypeWrapper ) wrapper).getMyAttributeType().getNames()[0] ) ) ) 
              || ( ( workbenchPart instanceof SchemaFormEditor ) && ( wrapper instanceof SchemaWrapper ) && ( reference.getPartName().equals( ( ( SchemaWrapper ) wrapper).getMySchema().getName() ) ) ) 
               )
            {
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().bringToTop( workbenchPart );
                return;
            }
        }
    }
}
