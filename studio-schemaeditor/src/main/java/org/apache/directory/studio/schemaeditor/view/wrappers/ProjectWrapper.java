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
import org.apache.directory.studio.schemaeditor.controller.ProjectListener;
import org.apache.directory.studio.schemaeditor.model.Project;
import org.eclipse.jface.viewers.TableViewer;


/**
 * This class is used to wrap a Project in a TreeViewer.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ProjectWrapper extends AbstractTreeNode
{
    /** The wrapped Project */
    private Project project;

    /** The TableViewer */
    private TableViewer viewer;


    /**
     * Creates a new instance of ProjectWrapper.
     *
     * @param project
     *      the wrapped Project
     */
    public ProjectWrapper( Project project, final TableViewer tableViewer )
    {
        super( null );
        this.project = project;
        this.viewer = tableViewer;

        Activator.getDefault().getProjectsHandler().addListener( project, new ProjectListener()
        {
            public void projectRenamed()
            {
                viewer.refresh();
            }
        } );
    }


    /**
     * Gets the wrapped Project.
     *
     * @return
     *      the wrapped Project
     */
    public Project getProject()
    {
        return project;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.view.wrappers.AbstractTreeNode#hasChildren()
     */
    public boolean hasChildren()
    {
        return false;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object obj )
    {
        if ( obj instanceof ProjectWrapper )
        {
            ProjectWrapper projectWrapper = ( ProjectWrapper ) obj;

            if ( ( project != null ) && ( !project.equals( projectWrapper.getProject() ) ) )
            {
                return false;
            }

            return true;
        }

        return false;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.view.wrappers.AbstractTreeNode#hashCode()
     */
    public int hashCode()
    {
        int result = super.hashCode();

        if ( project != null )
        {
            result = 37 * result + project.hashCode();
        }

        return result;
    }
}
