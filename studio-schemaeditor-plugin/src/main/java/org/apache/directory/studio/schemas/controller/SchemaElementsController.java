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

package org.apache.directory.studio.schemas.controller;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.schemas.Activator;
import org.apache.directory.studio.schemas.PluginConstants;
import org.apache.directory.studio.schemas.controller.actions.HideAttributeTypesAction;
import org.apache.directory.studio.schemas.controller.actions.HideObjectClassesAction;
import org.apache.directory.studio.schemas.controller.actions.LinkWithEditorSchemaElementsView;
import org.apache.directory.studio.schemas.controller.actions.OpenSchemaElementsViewPreferencesAction;
import org.apache.directory.studio.schemas.controller.actions.OpenSchemaElementsViewSortDialogAction;
import org.apache.directory.studio.schemas.controller.actions.OpenTypeHierarchyAction;
import org.apache.directory.studio.schemas.view.editors.attributeType.AttributeTypeEditor;
import org.apache.directory.studio.schemas.view.editors.attributeType.AttributeTypeEditorInput;
import org.apache.directory.studio.schemas.view.editors.objectClass.ObjectClassEditor;
import org.apache.directory.studio.schemas.view.editors.objectClass.ObjectClassEditorInput;
import org.apache.directory.studio.schemas.view.views.SchemaElementsView;
import org.apache.directory.studio.schemas.view.views.wrappers.AttributeTypeWrapper;
import org.apache.directory.studio.schemas.view.views.wrappers.IntermediateNode;
import org.apache.directory.studio.schemas.view.views.wrappers.ObjectClassWrapper;
import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;


/**
 * This class implements the Controller for the Schema Elements View.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SchemaElementsController
{
    /** The logger */
    private static Logger logger = Logger.getLogger( SchemaElementsController.class );

    /** The authorized Preferences keys*/
    private List<String> authorizedPrefs;

    /** The associated view */
    private SchemaElementsView view;

    /** The Context Menu */
    private MenuManager contextMenu;

    /** Token used to activate and deactivate shortcuts in the view */
    private IContextActivation contextActivation;

    // The Actions
    private Action hideObjectClasses;
    private Action hideAttributeTypes;
    private Action linkWithEditor;
    private Action openSortDialog;
    private Action openPreferencePage;
    private Action openTypeHierarchy;


    /**
     * Creates a new instance of SchemaElementsController.
     */
    public SchemaElementsController( SchemaElementsView view )
    {
        this.view = view;

        initAuthorizedPrefs();
        initActions();
        initToolbar();
        initMenu();
        initContextMenu();
        initDoubleClickListener();
        initPreferencesListener();
        initPartListener();
    }


    /**
     * Initializes the part listener. It is used to activate and deactivate the 
     * shortcuts (key bindins) when the view is activated and deactivated.
     */
    private void initPartListener()
    {

        view.getSite().getPage().addPartListener( new IPartListener2()
        {
            /**
             * This implementation deactivates the shortcuts when the part is deactivated.
             */
            public void partDeactivated( IWorkbenchPartReference partRef )
            {
                if ( partRef.getPart( false ) == view && contextActivation != null )
                {
                    ICommandService commandService = ( ICommandService ) PlatformUI.getWorkbench().getAdapter(
                        ICommandService.class );
                    if ( commandService != null )
                    {
                        commandService.getCommand( openTypeHierarchy.getActionDefinitionId() ).setHandler( null );
                    }

                    IContextService contextService = ( IContextService ) PlatformUI.getWorkbench().getAdapter(
                        IContextService.class );
                    contextService.deactivateContext( contextActivation );
                    contextActivation = null;
                }
            }


            /**
             * This implementation activates the shortcuts when the part is activated.
             */
            public void partActivated( IWorkbenchPartReference partRef )
            {
                if ( partRef.getPart( false ) == view )
                {
                    IContextService contextService = ( IContextService ) PlatformUI.getWorkbench().getAdapter(
                        IContextService.class );
                    contextActivation = contextService.activateContext( PluginConstants.CONTEXT_WINDOWS );

                    ICommandService commandService = ( ICommandService ) PlatformUI.getWorkbench().getAdapter(
                        ICommandService.class );
                    if ( commandService != null )
                    {
                        commandService.getCommand( openTypeHierarchy.getActionDefinitionId() ).setHandler(
                            new ActionHandler( openTypeHierarchy ) );
                    }
                }
            }


            public void partBroughtToTop( IWorkbenchPartReference partRef )
            {
            }


            public void partClosed( IWorkbenchPartReference partRef )
            {
            }


            public void partHidden( IWorkbenchPartReference partRef )
            {
            }


            public void partInputChanged( IWorkbenchPartReference partRef )
            {
            }


            public void partOpened( IWorkbenchPartReference partRef )
            {
            }


            public void partVisible( IWorkbenchPartReference partRef )
            {
            }

        } );

    }


    private void initAuthorizedPrefs()
    {
        authorizedPrefs = new ArrayList<String>();
        authorizedPrefs.add( PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_LABEL );
        authorizedPrefs.add( PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_ABBREVIATE );
        authorizedPrefs.add( PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_ABBREVIATE_MAX_LENGTH );
        authorizedPrefs.add( PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_SECONDARY_LABEL_DISPLAY );
        authorizedPrefs.add( PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_SECONDARY_LABEL );
        authorizedPrefs.add( PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_SECONDARY_LABEL_ABBREVIATE );
        authorizedPrefs.add( PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_SECONDARY_LABEL_ABBREVIATE_MAX_LENGTH );
        authorizedPrefs.add( PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_GROUPING );
        authorizedPrefs.add( PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_SORTING_BY );
        authorizedPrefs.add( PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_SORTING_ORDER );
    }


    /**
     * Initializes the Actions.
     */
    private void initActions()
    {
        hideObjectClasses = new HideObjectClassesAction( view.getViewer() );
        hideAttributeTypes = new HideAttributeTypesAction( view.getViewer() );
        linkWithEditor = new LinkWithEditorSchemaElementsView( view );
        openSortDialog = new OpenSchemaElementsViewSortDialogAction();
        openPreferencePage = new OpenSchemaElementsViewPreferencesAction();
        openTypeHierarchy = new OpenTypeHierarchyAction();
    }


    /**
     * Initializes the Toolbar.
     */
    private void initToolbar()
    {
        IToolBarManager toolbar = view.getViewSite().getActionBars().getToolBarManager();
        toolbar.add( hideObjectClasses );
        toolbar.add( hideAttributeTypes );
        toolbar.add( new Separator() );
        toolbar.add( linkWithEditor );
    }


    /**
     * Initializes the Menu.
     */
    private void initMenu()
    {
        IMenuManager menu = view.getViewSite().getActionBars().getMenuManager();
        menu.add( openSortDialog );
        menu.add( new Separator() );
        menu.add( linkWithEditor );
        menu.add( new Separator() );
        menu.add( openPreferencePage );
    }


    /**
     * Initializes the DoubleClickListener
     */
    private void initDoubleClickListener()
    {
        view.getViewer().addDoubleClickListener( new IDoubleClickListener()
        {
            public void doubleClick( DoubleClickEvent event )
            {
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

                SchemaElementsView view = ( SchemaElementsView ) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                    .getActivePage().findView( SchemaElementsView.ID );
                TreeViewer viewer = view.getViewer();

                // What we get from the treeViewer is a StructuredSelection
                StructuredSelection selection = ( StructuredSelection ) event.getSelection();

                // Here's the real object (an AttributeTypeWrapper, ObjectClassWrapper or IntermediateNode)
                Object objectSelection = selection.getFirstElement();
                IEditorInput input = null;
                String editorId = null;

                // Selecting the right editor and input
                if ( objectSelection instanceof AttributeTypeWrapper )
                {
                    input = new AttributeTypeEditorInput( ( ( AttributeTypeWrapper ) objectSelection )
                        .getMyAttributeType() );
                    editorId = AttributeTypeEditor.ID;
                }
                else if ( objectSelection instanceof ObjectClassWrapper )
                {
                    input = new ObjectClassEditorInput( ( ( ObjectClassWrapper ) objectSelection ).getMyObjectClass() );
                    editorId = ObjectClassEditor.ID;
                }
                else if ( objectSelection instanceof IntermediateNode )
                {
                    // Here we don't open an editor, we just expand the node.
                    viewer.setExpandedState( objectSelection, !viewer.getExpandedState( objectSelection ) );
                }

                // Let's open the editor
                if ( input != null )
                {
                    try
                    {
                        page.openEditor( input, editorId );
                    }
                    catch ( PartInitException e )
                    {
                        logger.debug( "error when opening the editor" ); //$NON-NLS-1$
                    }
                }
            }
        } );
    }


    /**
     * Initializes the listener on the preferences store
     */
    private void initPreferencesListener()
    {
        Activator.getDefault().getPreferenceStore().addPropertyChangeListener( new IPropertyChangeListener()
        {
            /* (non-Javadoc)
             * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
             */
            public void propertyChange( PropertyChangeEvent event )
            {
                if ( authorizedPrefs.contains( event.getProperty() ) )
                {
                    if ( PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_GROUPING == event.getProperty() )
                    {
                        view.refresh();
                    }
                    else
                    {
                        view.update();
                    }
                }
            }
        } );
    }


    /**
     * Initializes the ContextMenu.
     */
    private void initContextMenu()
    {
        TreeViewer viewer = view.getViewer();
        contextMenu = new MenuManager( "" ); //$NON-NLS-1$
        contextMenu.setRemoveAllWhenShown( true );
        contextMenu.addMenuListener( new IMenuListener()
        {
            public void menuAboutToShow( IMenuManager manager )
            {
                Object selection = ( ( TreeSelection ) view.getViewer().getSelection() ).getFirstElement();

                if ( ( selection instanceof AttributeTypeWrapper ) )
                {
                    manager.add( openTypeHierarchy );
                }
                else if ( ( selection instanceof ObjectClassWrapper ) )
                {
                    manager.add( openTypeHierarchy );
                }

                manager.add( new Separator( IWorkbenchActionConstants.MB_ADDITIONS ) );
            }
        } );

        // set the context menu to the table viewer
        viewer.getControl().setMenu( contextMenu.createContextMenu( viewer.getControl() ) );

        // register the context menu to enable extension actions
        view.getSite().registerContextMenu( contextMenu, viewer );
    }
}
