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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.directory.studio.schemaeditor.model.Project;
import org.apache.directory.studio.schemaeditor.model.Project.ProjectState;


/**
 * This class represents the ProjectsHandler.
 * <p>
 * It used to handle the schema projects.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ProjectsHandler
{
    /** The ProjectsHandler instance */
    private static ProjectsHandler instance;

    /** The projects List */
    private List<Project> projectsList;

    /** The projects Map */
    private Map<String, Project> projectsMap;

    /** The ProjectsHandler listeners */
    private List<ProjectsHandlerListener> projectsHandlerListeners;

    /** The projects listeners */
    private MultiValueMap projectsListeners;

    /** The open project */
    private Project openProject;


    /**
     * Gets the singleton instance of the ProjectsHandler.
     *
     * @return
     *      the singleton instance of the ProjectsHandler
     */
    public static ProjectsHandler getInstance()
    {
        if ( instance == null )
        {
            instance = new ProjectsHandler();
        }

        return instance;
    }


    /**
     * Private Constructor.
     */
    private ProjectsHandler()
    {
        projectsList = new ArrayList<Project>();
        projectsMap = new HashMap<String, Project>();
        projectsHandlerListeners = new ArrayList<ProjectsHandlerListener>();
        projectsListeners = new MultiValueMap();
    }


    /**
     * Adds a project.
     *
     * @param project
     *      a project
     */
    public void addProject( Project project )
    {
        projectsList.add( project );
        projectsMap.put( project.getName().toLowerCase(), project );

        notifyProjectAdded( project );
    }


    /**
     * Removes the given project.
     *
     * @param project
     *      the project
     */
    public void removeProject( Project project )
    {
        projectsList.remove( project );
        projectsMap.remove( project.getName().toLowerCase() );

        notifyProjectRemoved( project );
    }


    /**
     * Gets the project identified by the given name.
     *
     * @param name
     *      the name of the project
     * @return
     *      the corresponding project
     */
    public Project getProject( String name )
    {
        return projectsMap.get( name.toLowerCase() );
    }


    /**
     * Gets the projects List.
     *
     * @return
     *      the projects List
     */
    public List<Project> getProjects()
    {
        return projectsList;
    }


    /**
     * Renames the given project.
     *
     * @param project
     *      the project
     * @param name
     *      the new name
     */
    public void renameProject( Project project, String name )
    {
        projectsMap.remove( project.getName().toLowerCase() );
        project.setName( name );
        projectsMap.put( name.toLowerCase(), project );

        notifyProjectRenamed( project );
    }


    /**
     * Return whether or not the given name is already taken by another project
     *
     * @param name
     *      the name 
     * @return
     *  true if the given name is already taken, false if not
     */
    public boolean isProjectNameAlreadyTaken( String name )
    {
        return projectsMap.containsKey( name.toLowerCase() );
    }


    /**
     * Opens the given project (and closes the previously opened project)
     *
     * @param project
     *      the project
     */
    public void openProject( Project project )
    {
        Project oldOpenProject = openProject;
        if ( oldOpenProject != null )
        {
            oldOpenProject.setState( ProjectState.CLOSED );
        }

        openProject = project;
        openProject.setState( ProjectState.OPEN );

        notifyOpenProjectChanged( oldOpenProject, openProject );
    }


    /**
     * Closes the given project
     *
     * @param project
     *      the project
     */
    public void closeProject( Project project )
    {
        Project oldOpenProject = openProject;
        if ( oldOpenProject.equals( project ) )
        {
            oldOpenProject.setState( ProjectState.CLOSED );
            openProject = null;
        }

        notifyOpenProjectChanged( oldOpenProject, openProject );
    }


    /**
     * Gets the 'Open' project.
     *
     * @return
     *      the 'Open' project
     */
    public Project getOpenProject()
    {
        return openProject;
    }


    /**
     * Sets the 'Open' project
     *
     * @param project
     *      the project
     */
    public void setOpenProject( Project project )
    {
        openProject = project;
    }


    /**
     * Adds a ProjectsHandlerListener to the ProjectsHandler.
     *
     * @param listener
     *      the listener
     */
    public void addListener( ProjectsHandlerListener listener )
    {
        projectsHandlerListeners.add( listener );
    }


    /**
     * Remove the given ProjectsHandlerListener.
     *
     * @param listener
     *      the listener
     */
    public void removeListener( ProjectsHandlerListener listener )
    {
        projectsHandlerListeners.remove( listener );
    }


    /**
     * Adds a ProjectListener to the given Project.
     *
     * @param project
     *      the project
     * @param listener
     *      the listener
     */
    public void addListener( Project project, ProjectListener listener )
    {
        if ( !projectsListeners.containsValue( project, listener ) )
        {
            projectsListeners.put( project, listener );
        }
    }


    /**
     * Removes the given ProjectListener.
     *
     * @param project
     *      the project
     * @param listener
     *      the listener
     */
    public void removeListener( Project project, ProjectListener listener )
    {
        projectsListeners.remove( project, listener );
    }


    /**
     * Notifies the ProjectsHandler's listener that a project has been added. 
     *
     * @param project
     *      the added project
     */
    private void notifyProjectAdded( Project project )
    {
        for ( ProjectsHandlerListener listener : projectsHandlerListeners )
        {
            listener.projectAdded( project );
        }
    }


    /**
     * Notifies the ProjectsHandler's listener that a project has been removed. 
     *
     * @param project
     *      the removed project
     */
    private void notifyProjectRemoved( Project project )
    {
        for ( ProjectsHandlerListener listener : projectsHandlerListeners )
        {
            listener.projectRemoved( project );
        }
    }


    /**
     * Notifies the project's Listeners that the project has been renamed.
     *
     * @param project
     *      the renamed project
     */
    @SuppressWarnings("unchecked")
    private void notifyProjectRenamed( Project project )
    {
        List<ProjectListener> listeners = ( List<ProjectListener> ) projectsListeners.get( project );
        for ( ProjectListener listener : listeners )
        {
            listener.projectRenamed();
        }
    }


    /**
     * Notifies the ProjectsHandler's listener that a new project has been opened. 
     *
     * @param oldProject
     *      the old opened project
     * @param newProject
     *      the new opened project
     */
    private void notifyOpenProjectChanged( Project oldProject, Project newProject )
    {
        for ( ProjectsHandlerListener listener : projectsHandlerListeners )
        {
            listener.openProjectChanged( oldProject, newProject );
        }
    }
}
