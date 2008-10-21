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
package org.apache.directory.studio.schemaeditor.model.io;


import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.model.Project;
import org.apache.directory.studio.schemaeditor.model.ProjectType;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;


/**
 * This class is used to export Project(s) into the XML Format.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ProjectsExporter
{
    // The tags
    private static final String PROJECT_TAG = "project";
    private static final String PROJECTS_TAG = "projects";
    private static final String NAME_TAG = "name";
    private static final String TYPE_TAG = "type";
    private static final String CONNECTION_TAG = "connection";
    private static final String SCHEMA_BACKUP_TAG = "schemaBackup";


    /**
     * Converts the given project to its code representation
     * in XML file format.
     *
     * @param project
     *      the project to convert
     * @return
     *      the corresponding code representation
     */
    public static String toXml( Project project )
    {
        // Creating the Document
        Document document = DocumentHelper.createDocument();

        // Adding the project
        addProject( project, document );

        return styleDocument( document ).asXML();
    }


    /**
     * Converts the given projects to their code representation
     * in XML file format.
     *
     * @param projects
     *      the projects to convert
     * @return
     *      the corresponding code representation
     */
    public static String toXml( Project[] projects )
    {
        // Creating the Document
        Document document = DocumentHelper.createDocument();
        Element projectsElement = document.addElement( PROJECTS_TAG );

        if ( projects != null )
        {
            for ( Project project : projects )
            {
                addProject( project, projectsElement );
            }
        }

        return styleDocument( document ).asXML();
    }


    /**
     * Add the XML representation of the given project
     * to the given branch
     *
     * @param project
     *      the project
     * @param branch
     *      the branch
     */
    private static void addProject( Project project, Branch branch )
    {
        Element element = branch.addElement( PROJECT_TAG );

        if ( project != null )
        {
            // Name 
            String name = project.getName();
            if ( ( name != null ) && ( !name.equals( "" ) ) )
            {
                element.addAttribute( NAME_TAG, name );
            }

            // Type
            ProjectType type = project.getType();
            if ( type != null )
            {
                element.addAttribute( TYPE_TAG, type.toString() );
            }

            // If project is an Apache Directory Server Online Schema Project
            if ( type.equals( ProjectType.APACHE_DIRECTORY_SERVER ) )
            {
                // Connection Name
                element.addAttribute( CONNECTION_TAG, project.getConnection().getName() );

                // Schema Backup
                Element schemaBackupElement = element.addElement( SCHEMA_BACKUP_TAG );
                List<Schema> backupSchemas = project.getSchemaBackup();
                if ( backupSchemas != null )
                {
                    XMLSchemaFileExporter.addSchemas( backupSchemas.toArray( new Schema[0] ), schemaBackupElement );
                }

            }

            // Schemas
            XMLSchemaFileExporter
                .addSchemas( project.getSchemaHandler().getSchemas().toArray( new Schema[0] ), element );
        }
    }


    /**
     * XML Pretty Printer XSLT Transformation
     * 
     * @param document
     *      the Dom4j Document
     * @return
     */
    private static Document styleDocument( Document document )
    {
        // load the transformer using JAXP
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try
        {
            transformer = factory.newTransformer( new StreamSource( Activator.class
                .getResourceAsStream( "XmlFileFormat.xslt" ) ) );
        }
        catch ( TransformerConfigurationException e1 )
        {
            // Will never occur
        }

        // now lets style the given document
        DocumentSource source = new DocumentSource( document );
        DocumentResult result = new DocumentResult();
        try
        {
            transformer.transform( source, result );
        }
        catch ( TransformerException e )
        {
            // Will never occur
        }

        // return the transformed document
        Document transformedDoc = result.getDocument();
        return transformedDoc;
    }
}
