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
package org.apache.directory.studio.schemaeditor.controller;


import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.controller.actions.CloseProjectAction;
import org.apache.directory.studio.schemaeditor.controller.actions.DeleteProjectAction;
import org.apache.directory.studio.schemaeditor.controller.actions.ExportProjectsAction;
import org.apache.directory.studio.schemaeditor.controller.actions.ImportProjectsAction;
import org.apache.directory.studio.schemaeditor.controller.actions.NewProjectAction;
import org.apache.directory.studio.schemaeditor.controller.actions.OpenProjectAction;
import org.apache.directory.studio.schemaeditor.controller.actions.RenameProjectAction;
import org.apache.directory.studio.schemaeditor.model.Project;
import org.apache.directory.studio.schemaeditor.model.Project.ProjectState;
import org.apache.directory.studio.schemaeditor.view.views.ProjectsView;
import org.apache.directory.studio.schemaeditor.view.wrappers.ProjectWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.ProjectsViewRoot;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;


/**
 * This class implements the Controller for the SchemaView.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ProjectsViewController
{
    /** The associated view */
    private ProjectsView view;

    /** The Context Menu */
    private MenuManager contextMenu;

    /** The TableViewer */
    private TableViewer viewer;

    /** The ProjectsHandler */
    private ProjectsHandler projectsHandler;

    /** Token used to activate and deactivate shortcuts in the view */
    private IContextActivation contextActivation;

    // The Actions
    private NewProjectAction newProject;
    private OpenProjectAction openProject;
    private CloseProjectAction closeProject;
    private RenameProjectAction renameProject;
    private DeleteProjectAction deleteProject;
    private ImportProjectsAction importProjects;
    private ExportProjectsAction exportProjects;


    /**
     * Creates a new instance of SchemasViewController.
     *
     * @param view
     *      the associated view
     */
    public ProjectsViewController( ProjectsView view )
    {
        this.view = view;
        viewer = view.getViewer();

        projectsHandler = Activator.getDefault().getProjectsHandler();

        initActions();
        initToolbar();
        initContextMenu();
        initViewer();
        initDoubleClickListener();
        initPartListener();
    }


    /**
     * Initializes the Actions.
     */
    private void initActions()
    {
        newProject = new NewProjectAction();
        openProject = new OpenProjectAction( view.getViewer() );
        closeProject = new CloseProjectAction( view.getViewer() );
        renameProject = new RenameProjectAction( view.getViewer() );
        deleteProject = new DeleteProjectAction( view.getViewer() );
        importProjects = new ImportProjectsAction();
        exportProjects = new ExportProjectsAction( view.getViewer() );
    }


    /**
     * Initializes the Toolbar.
     */
    private void initToolbar()
    {
        IToolBarManager toolbar = view.getViewSite().getActionBars().getToolBarManager();
        toolbar.add( newProject );
    }


    /**
     * Initializes the ContextMenu.
     */
    private void initContextMenu()
    {
        contextMenu = new MenuManager( "" ); //$NON-NLS-1$
        contextMenu.setRemoveAllWhenShown( true );
        contextMenu.addMenuListener( new IMenuListener()
        {
            public void menuAboutToShow( IMenuManager manager )
            {
                MenuManager importManager = new MenuManager( Messages.getString( "ProjectsViewController.ImportAction" ) ); //$NON-NLS-1$
                MenuManager exportManager = new MenuManager( Messages.getString( "ProjectsViewController.ExportAction" ) ); //$NON-NLS-1$
                manager.add( newProject );
                manager.add( new Separator() );
                manager.add( openProject );
                manager.add( closeProject );
                manager.add( new Separator() );
                manager.add( renameProject );
                manager.add( new Separator() );
                manager.add( deleteProject );
                manager.add( new Separator() );
                manager.add( importManager );
                importManager.add( importProjects );
                manager.add( exportManager );
                exportManager.add( exportProjects );

                manager.add( new Separator( IWorkbenchActionConstants.MB_ADDITIONS ) );
            }
        } );

        // set the context menu to the table viewer
        viewer.getControl().setMenu( contextMenu.createContextMenu( viewer.getControl() ) );

        // register the context menu to enable extension actions
        view.getSite().registerContextMenu( contextMenu, viewer );
    }


    /**
     * Initializes the Viewer.
     */
    private void initViewer()
    {
        viewer.setInput( new ProjectsViewRoot( viewer ) );
        viewer.getTable().addKeyListener( new KeyAdapter()
        {
            public void keyReleased( KeyEvent e )
            {
                if ( ( e.keyCode == Action.findKeyCode( "BACKSPACE" ) ) //$NON-NLS-1$
                    || ( e.keyCode == Action.findKeyCode( "DELETE" ) ) ) //$NON-NLS-1$
                {
                    deleteProject.run();
                }
            }
        } );
    }


    /**
     * Initializes the DoubleClickListener.
     */
    private void initDoubleClickListener()
    {
        viewer.addDoubleClickListener( new IDoubleClickListener()
        {
            public void doubleClick( DoubleClickEvent event )
            {
                StructuredSelection selection = ( StructuredSelection ) viewer.getSelection();

                if ( ( !selection.isEmpty() ) && ( selection.size() == 1 ) )
                {
                    Project project = ( ( ProjectWrapper ) selection.getFirstElement() ).getProject();
                    if ( project.getState().equals( ProjectState.CLOSED ) )
                    {
                        projectsHandler.openProject( project );
                    }
                }
            }
        } );
    }


    /**
     * Initializes the PartListener.
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
                        commandService.getCommand( newProject.getActionDefinitionId() ).setHandler( null );
                        commandService.getCommand( renameProject.getActionDefinitionId() ).setHandler( null );
                        commandService.getCommand( deleteProject.getActionDefinitionId() ).setHandler( null );
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
                    contextActivation = contextService.activateContext( PluginConstants.CONTEXT_PROJECTS_VIEW );

                    ICommandService commandService = ( ICommandService ) PlatformUI.getWorkbench().getAdapter(
                        ICommandService.class );
                    if ( commandService != null )
                    {
                        commandService.getCommand( newProject.getActionDefinitionId() ).setHandler(
                            new ActionHandler( newProject ) );
                        commandService.getCommand( renameProject.getActionDefinitionId() ).setHandler(
                            new ActionHandler( renameProject ) );
                        commandService.getCommand( deleteProject.getActionDefinitionId() ).setHandler(
                            new ActionHandler( deleteProject ) );
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
}
