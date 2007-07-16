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
package org.apache.directory.studio.apacheds.schemaeditor.view.wrappers;


import org.apache.directory.studio.apacheds.schemaeditor.model.Project;


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


    /**
     * Creates a new instance of ProjectWrapper.
     *
     * @param project
     *      the wrapped Project
     */
    public ProjectWrapper( Project project )
    {
        super( null );
        this.project = project;
    }


    /**
     * Creates a new instance of ProjectWrapper.
     * 
     * @param project
     *      the wrapped Project
     * @param parent
     *      the parent TreeNode
     */
    public ProjectWrapper( Project project, TreeNode parent )
    {
        super( parent );
        this.project = project;
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
     * @see org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.AbstractTreeNode#hasChildren()
     */
    public boolean hasChildren()
    {
        return false;
    }
}
