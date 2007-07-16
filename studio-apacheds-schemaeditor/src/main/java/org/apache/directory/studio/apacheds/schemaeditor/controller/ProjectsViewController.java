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
package org.apache.directory.studio.apacheds.schemaeditor.controller;


import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.controller.actions.CloseProjectAction;
import org.apache.directory.studio.apacheds.schemaeditor.controller.actions.DeleteProjectAction;
import org.apache.directory.studio.apacheds.schemaeditor.controller.actions.ExportProjectsAction;
import org.apache.directory.studio.apacheds.schemaeditor.controller.actions.ImportProjectsAction;
import org.apache.directory.studio.apacheds.schemaeditor.controller.actions.NewProjectAction;
import org.apache.directory.studio.apacheds.schemaeditor.controller.actions.OpenProjectAction;
import org.apache.directory.studio.apacheds.schemaeditor.controller.actions.RenameProjectAction;
import org.apache.directory.studio.apacheds.schemaeditor.model.Project;
import org.apache.directory.studio.apacheds.schemaeditor.model.Project.ProjectState;
import org.apache.directory.studio.apacheds.schemaeditor.view.views.ProjectsView;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.ProjectWrapper;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchActionConstants;


/**
 * This class implements the Controller for the SchemaView.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ProjectsViewController
{
    /** The associated view */
    private ProjectsView view;

    /** The Context Menu */
    private MenuManager contextMenu;

    /** The ProjectsHandler */
    private ProjectsHandler projectsHandler;

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

        projectsHandler = Activator.getDefault().getProjectsHandler();

        initActions();
        initToolbar();
        initContextMenu();
        initViewer();
        initDoubleClickListener();
    }


    /**
     * Initializes the Actions.
     */
    private void initActions()
    {
        newProject = new NewProjectAction();
        openProject = new OpenProjectAction( view );
        closeProject = new CloseProjectAction( view );
        renameProject = new RenameProjectAction( view );
        deleteProject = new DeleteProjectAction( view );
        importProjects = new ImportProjectsAction();
        exportProjects = new ExportProjectsAction();
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
                MenuManager importManager = new MenuManager( "Import..." );
                MenuManager exportManager = new MenuManager( "Export..." );
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

        TableViewer viewer = view.getViewer();

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
        view.getViewer().setInput( projectsHandler.getProjects() );
        view.getViewer().getTable().addKeyListener( new KeyAdapter()
        {
            public void keyReleased( KeyEvent e )
            {
                if ( ( e.keyCode == Action.findKeyCode( "BACKSPACE" ) )
                    || ( e.keyCode == Action.findKeyCode( "DELETE" ) ) )
                {
                    deleteProject.run();
                }
            }
        } );
        projectsHandler.addListener( new ProjectsHandlerListener()
        {
            public void projectAdded( Project project )
            {
                refreshProjectsViewer();
            }


            public void projectRemoved( Project project )
            {
                refreshProjectsViewer();
            }


            public void openProjectChanged( Project oldProject, Project newProject )
            {
                refreshProjectsViewer();
            }
        } );
    }


    /**
     * Refreshes the Projects Viewer
     */
    public void refreshProjectsViewer()
    {
        Display.getDefault().asyncExec( new Runnable()
        {
            public void run()
            {
                view.getViewer().refresh();
            }
        } );
    }


    /**
     * Initializes the DoubleClickListener.
     */
    private void initDoubleClickListener()
    {
        view.getViewer().addDoubleClickListener( new IDoubleClickListener()
        {
            public void doubleClick( DoubleClickEvent event )
            {
                StructuredSelection selection = ( StructuredSelection ) view.getViewer().getSelection();

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
}
