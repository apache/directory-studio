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
package org.apache.directory.studio.apacheds.schemaeditor.model.io;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.studio.apacheds.schemaeditor.model.Project;
import org.apache.directory.studio.apacheds.schemaeditor.model.Project.ProjectType;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


/**
 * This class is used to import a Project file.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ProjectsImporter
{
    // The tags
    private static final String PROJECT_TAG = "project";
    private static final String PROJECTS_TAG = "projects";
    private static final String NAME_TAG = "name";
    private static final String TYPE_TAG = "type";


    /**
     * Extract the project from the given path
     *
     * @param path
     *      the path of the file
     * @return
     *      the corresponding project
     * @throws ProjectsImportException 
     *      if an error occurs when importing the project
     */
    public static Project getProject( String path ) throws ProjectsImportException
    {
        Project project = new Project();

        SAXReader reader = new SAXReader();
        Document document = null;
        try
        {
            document = reader.read( path );
        }
        catch ( DocumentException e )
        {
            throw new ProjectsImportException( "The file '" + path + "' can not be read correctly." );
        }

        Element rootElement = document.getRootElement();
        if ( !rootElement.getName().equals( PROJECT_TAG ) )
        {
            throw new ProjectsImportException( "The file '" + path + "' does not seem to be a valid project file." );
        }

        readProject( rootElement, project, path );

        return project;
    }


    /**
     * Extract the projects from the given path
     *
     * @param path
     *      the path of the file
     * @return
     *      the corresponding projects
     * @throws ProjectsImportException 
     *      if an error occurs when importing the project
     */
    public static Project[] getProjects( String path ) throws ProjectsImportException
    {
        List<Project> projects = new ArrayList<Project>();

        SAXReader reader = new SAXReader();
        Document document = null;
        try
        {
            document = reader.read( path );
        }
        catch ( DocumentException e )
        {
            throw new ProjectsImportException( "The file '" + path + "' can not be read correctly." );
        }

        Element rootElement = document.getRootElement();
        if ( !rootElement.getName().equals( PROJECTS_TAG ) )
        {
            throw new ProjectsImportException( "The file '" + path + "' does not seem to be a valid project file." );
        }

        for ( Iterator<?> i = rootElement.elementIterator( PROJECT_TAG ); i.hasNext(); )
        {
            Project project = new Project();
            readProject( rootElement, project, path );
            projects.add( project );
        }

        return projects.toArray( new Project[0] );
    }


    /**
     * Reads a project.
     *
     * @param element
     *      the element
     * @param project
     *      the project
     * @param path
     *      the path
     * @throws ProjectsImportException 
     *      if an error occurs when importing the project
     */
    private static void readProject( Element element, Project project, String path ) throws ProjectsImportException
    {
        // Name
        Attribute nameAttribute = element.attribute( NAME_TAG );
        if ( ( nameAttribute != null ) && ( !nameAttribute.getValue().equals( "" ) ) )
        {
            project.setName( nameAttribute.getValue() );
        }

        // Type
        Attribute typeAttribute = element.attribute( TYPE_TAG );
        if ( ( typeAttribute != null ) && ( !typeAttribute.getValue().equals( "" ) ) )
        {
            try
            {
                project.setType( ProjectType.valueOf( typeAttribute.getText() ) );
            }
            catch ( IllegalArgumentException e )
            {
                throw new ProjectsImportException( "The parser was not able to convert the type value of the project." );
            }
        }
    }

    /**
     * This enum represents the different types of project files.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    public enum ProjectFileType
    {
        SINGLE, MULTIPLE
    }


    /**
     * Gets the type of file.
     *
     * @param path
     *      the path of the file
     * @return
     *      the type of the file
     * @throws ProjectsImportException
     */
    public static ProjectFileType getProjectFileType( String path ) throws ProjectsImportException
    {
        SAXReader reader = new SAXReader();
        Document document = null;
        try
        {
            document = reader.read( path );
        }
        catch ( DocumentException e )
        {
            throw new ProjectsImportException( "The file '" + path + "' can not be read correctly." );
        }

        Element rootElement = document.getRootElement();
        if ( rootElement.getName().equals( PROJECT_TAG ) )
        {
            return ProjectFileType.SINGLE;
        }
        else if ( rootElement.getName().equals( PROJECTS_TAG ) )
        {
            return ProjectFileType.MULTIPLE;
        }
        else
        {
            throw new ProjectsImportException( "The file '" + path + "' does not seem to be a valid project file." );
        }
    }
}
