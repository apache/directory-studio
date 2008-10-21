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
package org.apache.directory.studio.schemaeditor.view.wrappers;


import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.controller.ProjectsHandler;
import org.apache.directory.studio.schemaeditor.controller.ProjectsHandlerListener;
import org.apache.directory.studio.schemaeditor.model.Project;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Display;


/**
 * This wrapper is used as root in the ProjectsView.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ProjectsViewRoot extends AbstractTreeNode
{
    /** The TableViewer */
    private TableViewer viewer;

    /** The ProjectsHandler */
    private ProjectsHandler projectsHandler;


    /**
     * Creates a new instance of ProjectsViewRoot.
     *
     * @param tableViewer
     *      the associated TableViewer
     */
    public ProjectsViewRoot( TableViewer tableViewer )
    {
        super( null );
        this.viewer = tableViewer;

        projectsHandler = Activator.getDefault().getProjectsHandler();
        projectsHandler.addListener( new ProjectsHandlerListener()
        {
            public void projectAdded( Project project )
            {
                addProjectWrapper( project );
                refreshProjectsViewer();
            }


            public void projectRemoved( Project project )
            {
                deleteProjectWrapper( project );
                refreshProjectsViewer();
            }


            public void openProjectChanged( Project oldProject, Project newProject )
            {
                refreshProjectsViewer();
            }
        } );
    }


    /**
     * Add a ProjectWrapper for the given project.
     *
     * @param project
     *      the project
     */
    private void addProjectWrapper( Project project )
    {
        addChild( new ProjectWrapper( project, viewer ) );
    }


    /**
     * Deletes the ProjectWrapper associated with the given project.
     *
     * @param project
     *      the project
     */
    private void deleteProjectWrapper( Project project )
    {
        for ( TreeNode node : getChildren() )
        {
            ProjectWrapper pw = ( ProjectWrapper ) node;
            if ( project == pw.getProject() )
            {
                removeChild( node );
                return;
            }
        }
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
                viewer.refresh();
            }
        } );
    }
}
