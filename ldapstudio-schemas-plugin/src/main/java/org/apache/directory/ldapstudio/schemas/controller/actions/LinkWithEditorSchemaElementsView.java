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
import org.apache.directory.ldapstudio.schemas.PluginConstants;
import org.apache.directory.ldapstudio.schemas.model.AttributeType;
import org.apache.directory.ldapstudio.schemas.model.ObjectClass;
import org.apache.directory.ldapstudio.schemas.model.SchemaElement;
import org.apache.directory.ldapstudio.schemas.view.editors.attributeType.AttributeTypeEditor;
import org.apache.directory.ldapstudio.schemas.view.editors.objectClass.ObjectClassEditor;
import org.apache.directory.ldapstudio.schemas.view.viewers.SchemaElementsView;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.AttributeTypeWrapper;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ITreeNode;
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
public class LinkWithEditorSchemaElementsView extends Action
{
    /** The String for storing the checked state of the action */
    private static final String LINK_WITH_EDITOR_SCHEMAS_VIEW_DS_KEY = LinkWithEditorSchemaElementsView.class.getName()
        + ".dialogsettingkey";

    /** The associated view */
    private SchemaElementsView view;

    /** The listener listening on changes on editors */
    private IPartListener2 editorListener = new IPartListener2()
    {
        /* (non-Javadoc)
         * @see org.eclipse.ui.IPartListener2#partVisible(org.eclipse.ui.IWorkbenchPartReference)
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
        }


        /* (non-Javadoc)
         * @see org.eclipse.ui.IPartListener2#partActivated(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partActivated( IWorkbenchPartReference partRef )
        {
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
         * @see org.eclipse.ui.IPartListener2#partBroughtToTop(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partBroughtToTop( IWorkbenchPartReference partRef )
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
                linkEditorWithView( ( ITreeNode ) selectedObject );
            }
        }
    };


    /**
     * Creates a new instance of LinkWithEditorSchemaElementsView.
     *
     * @param view
     *      the associated view
     */
    public LinkWithEditorSchemaElementsView( SchemaElementsView view )
    {
        super( "Link with Editor", AS_CHECK_BOX );
        setToolTipText( getText() );
        setId( PluginConstants.CMD_LINK_WITH_EDITOR_SCHEMA_ELEMENTS_VIEW );
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
            PluginConstants.IMG_LINK_WITH_EDITOR ) );
        setEnabled( true );
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
            if ( activeEditor instanceof ObjectClassEditor )
            {
                linkViewWithEditor( ( ( ObjectClassEditor ) activeEditor ).getOriginalObjectClass() );
            }
            else if ( activeEditor instanceof AttributeTypeEditor )
            {
                linkViewWithEditor( ( ( AttributeTypeEditor ) activeEditor ).getOriginalAttributeType() );
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
     * @param schemaElement
     *      the Schema Element
     */
    private void linkViewWithEditor( SchemaElement schemaElement )
    {
        StructuredSelection structuredSelection = null;
        ITreeNode wrapper = null;

        if ( schemaElement instanceof AttributeType )
        {
            wrapper = new AttributeTypeWrapper( ( AttributeType ) schemaElement, null );
            structuredSelection = new StructuredSelection( wrapper );

            view.getViewer().setSelection( structuredSelection, true );
        }
        else if ( schemaElement instanceof ObjectClass )
        {
            wrapper = new ObjectClassWrapper( ( ObjectClass ) schemaElement, null );
            structuredSelection = new StructuredSelection( wrapper );
        }
        else
        {
            return;
        }

        Object foundItem = view.getViewer().testFindItem( wrapper );
        if ( foundItem != null ) // The node we are looking for is already loaded in the TreeViewer
        {
            view.getViewer().setSelection( structuredSelection, true );
        }
        else
        // The node we are looking for is not yet loaded in the TreeViewer, we have to find and load it.
        {
            ITreeNode foundElement = view.findElementInTree( wrapper );

            if ( foundElement != null )
            {
                expandFromTopToBottom( foundElement );
                view.getViewer().setSelection( structuredSelection );
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
            if ( !view.getViewer().getExpandedState( schemaWrapper ) )
            {
                view.getViewer().setExpandedState( schemaWrapper, true );
            }
        }
        else if ( element instanceof ObjectClassWrapper )
        {
            ObjectClassWrapper objectClassWrapper = ( ObjectClassWrapper ) element;
            expandFromTopToBottom( objectClassWrapper.getParent() );
            view.getViewer().setExpandedState( objectClassWrapper, true );
        }
        else if ( element instanceof AttributeTypeWrapper )
        {
            AttributeTypeWrapper attributeTypeWrapper = ( AttributeTypeWrapper ) element;
            expandFromTopToBottom( attributeTypeWrapper.getParent() );
            view.getViewer().setExpandedState( attributeTypeWrapper, true );
        }
        else if ( element instanceof IntermediateNode )
        {
            IntermediateNode intermediateNode = ( IntermediateNode ) element;
            expandFromTopToBottom( intermediateNode.getParent() );
            view.getViewer().setExpandedState( intermediateNode, true );
        }
    }


    /**
     * Links the editor to the view
     *
     * @param wrapper
     *      the selected element in the view
     */
    private void linkEditorWithView( ITreeNode wrapper )
    {
        if ( wrapper != null )
        {
            IEditorReference[] references = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                .getEditorReferences();

            for ( IEditorReference reference : references )
            {
                IWorkbenchPart workbenchPart = reference.getPart( true );

                if ( ( workbenchPart instanceof ObjectClassEditor ) && ( wrapper instanceof ObjectClassWrapper ) )
                {
                    if ( ( ( ObjectClassEditor ) workbenchPart ).getOriginalObjectClass().equals(
                        ( ( ObjectClassWrapper ) wrapper ).getMyObjectClass() ) )
                    {
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().bringToTop( workbenchPart );
                        return;
                    }
                }
                else if ( ( workbenchPart instanceof AttributeTypeEditor )
                    && ( wrapper instanceof AttributeTypeWrapper ) )
                {
                    if ( ( ( AttributeTypeEditor ) workbenchPart ).getOriginalAttributeType().equals(
                        ( ( AttributeTypeWrapper ) wrapper ).getMyAttributeType() ) )
                    {
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().bringToTop( workbenchPart );
                        return;
                    }
                }
            }
        }
    }
}
