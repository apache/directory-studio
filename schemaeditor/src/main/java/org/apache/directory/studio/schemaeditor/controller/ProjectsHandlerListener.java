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


import org.apache.directory.studio.schemaeditor.model.Project;


/**
 * Classes which implement this interface provide methods that deal with the 
 * events that are generated when the ProjectsHandler is modified.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface ProjectsHandlerListener
{
    /**
     * This method is called when a project is added.
     *
     * @param project
     *      the added project
     */
    public void projectAdded( Project project );


    /**
     * This method is called when a project is removed.
     *
     * @param project
     *      the removed project
     */
    public void projectRemoved( Project project );


    /**
     * This method is called when a project is opened.
     *
     * @param oldProject
     *      the old opened project
     * @param newProject
     *      the new opened project
     */
    public void openProjectChanged( Project oldProject, Project newProject );
}
