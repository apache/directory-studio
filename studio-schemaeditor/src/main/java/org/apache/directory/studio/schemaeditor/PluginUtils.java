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
package org.apache.directory.studio.schemaeditor;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.schemaeditor.controller.ProjectsHandler;
import org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.schemaeditor.model.Project;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.apache.directory.studio.schemaeditor.model.io.ProjectsExporter;
import org.apache.directory.studio.schemaeditor.model.io.ProjectsImportException;
import org.apache.directory.studio.schemaeditor.model.io.ProjectsImporter;
import org.apache.directory.studio.schemaeditor.model.io.XMLSchemaFileImportException;
import org.apache.directory.studio.schemaeditor.model.io.XMLSchemaFileImporter;
import org.apache.directory.studio.schemaeditor.view.ViewUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;


/**
 * This class contains helper methods.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class PluginUtils
{
    /**
     * Verifies that the given name is syntaxely correct according to the RFC 2252 
     * (Lightweight Directory Access Protocol (v3): Attribute Syntax Definitions).
     *
     * @param name
     *      the name to test
     * @return
     *      true if the name is correct, false if the name is not correct.
     */
    public static boolean verifyName( String name )
    {
        return name.matches( "[a-zA-Z]+[a-zA-Z0-9;-]*" ); //$NON-NLS-1$
    }


    /**
     * Returns a clone of the given attribute type.
     *
     * @param at
     *      the attribute type to clone
     * @return
     *      a clone of the given attribute type
     */
    public static AttributeTypeImpl getClone( AttributeTypeImpl at )
    {
        AttributeTypeImpl clone = new AttributeTypeImpl( at.getOid() );
        clone.setNames( at.getNames() );
        clone.setSchema( at.getSchema() );
        clone.setDescription( at.getDescription() );
        clone.setSuperiorName( at.getSuperiorName() );
        clone.setUsage( at.getUsage() );
        clone.setSyntaxOid( at.getSyntaxOid() );
        clone.setLength( at.getLength() );
        clone.setObsolete( at.isObsolete() );
        clone.setSingleValue( at.isSingleValue() );
        clone.setCollective( at.isCollective() );
        clone.setCanUserModify( at.isCanUserModify() );
        clone.setEqualityName( at.getEqualityName() );
        clone.setOrderingName( at.getOrderingName() );
        clone.setSubstrName( at.getSubstrName() );

        return clone;
    }


    /**
     * Returns a clone of the given object class.
     *
     * @param oc
     *      the object class to clone
     * @return
     *      a clone of the given object class
     */
    public static ObjectClassImpl getClone( ObjectClassImpl oc )
    {
        ObjectClassImpl clone = new ObjectClassImpl( oc.getOid() );
        clone.setNames( oc.getNames() );
        clone.setSchema( oc.getSchema() );
        clone.setDescription( oc.getDescription() );
        clone.setSuperClassesNames( oc.getSuperClassesNames() );
        clone.setType( oc.getType() );
        clone.setObsolete( oc.isObsolete() );
        clone.setMustNamesList( oc.getMustNamesList() );
        clone.setMayNamesList( oc.getMayNamesList() );

        return clone;
    }


    /**
     * Gets the Projects (where is store information about the loaded Projects).
     *
     * @return
     *      the Projects File
     */
    private static File getProjectsFile()
    {
        return Activator.getDefault().getStateLocation().append( "projects.xml" ).toFile(); //$NON-NLS-1$
    }


    /**
     * Loads the projects saved in the Projects File.
     */
    public static void loadProjects()
    {
        ProjectsHandler projectsHandler = Activator.getDefault().getProjectsHandler();
        File projectsFile = getProjectsFile();

        if ( projectsFile.exists() )
        {
            Project[] projects = null;
            try
            {
                projects = ProjectsImporter.getProjects( projectsFile.getAbsolutePath() );
            }
            catch ( ProjectsImportException e )
            {
                PluginUtils.logError( "An error occured when loading the projects.", e );
                ViewUtils.displayErrorMessageBox( "Projects Loading Error",
                    "An error occured when loading the projects." );
            }

            for ( Project project : projects )
            {
                projectsHandler.addProject( project );
            }
        }
    }


    /**
     * Saves the projects in the Projects File.
     */
    public static void saveProjects()
    {
        ProjectsHandler projectsHandler = Activator.getDefault().getProjectsHandler();
        File projectsFile = getProjectsFile();

        try
        {
            BufferedWriter buffWriter = new BufferedWriter( new FileWriter( projectsFile ) );
            buffWriter.write( ProjectsExporter.toXml( projectsHandler.getProjects().toArray( new Project[0] ) ) );
            buffWriter.close();
        }
        catch ( IOException e )
        {
            PluginUtils.logError( "An error occured when saving the projects.", e );
            ViewUtils.displayErrorMessageBox( "Projects Saving Error", "An error occured when saving the projects." );
        }
    }


    /**
     * Logs the given message and exception with the ERROR status level.
     * 
     * @param message
     *      the message
     * @param exception
     *      the exception
     */
    public static void logError( String message, Throwable exception )
    {
        Activator.getDefault().getLog().log(
            new Status( Status.ERROR, Activator.getDefault().getBundle().getSymbolicName(), Status.OK, message,
                exception ) );
    }


    /**
     * Logs the given message and exception with the WARNING status level.
     * 
     * @param message
     *      the message
     * @param exception
     *      the exception
     */
    public static void logWarning( String message, Throwable exception )
    {
        Activator.getDefault().getLog().log(
            new Status( Status.WARNING, Activator.getDefault().getBundle().getSymbolicName(), Status.OK, message,
                exception ) );
    }


    /**
     * Loads the 'core' corresponding to the given name.
     *
     * @param schemaName
     *      the name of the 'core' schema
     * @return
     *      the corresponding schema, or null if no schema has been found
     */
    public static Schema loadCoreSchema( String schemaName )
    {
        Schema schema = null;

        try
        {
            URL url = Platform.getBundle( Activator.PLUGIN_ID )
                .getResource( "resources/schemas/" + schemaName + ".xml" );

            if ( url == null )
            {
                PluginUtils.logError( "An error occured when loading the schema " + schemaName + ".", null );
                ViewUtils.displayErrorMessageBox( "Projects Saving Error", "An error occured when loading the schema "
                    + schemaName + "." );
            }
            else
            {
                schema = XMLSchemaFileImporter.getSchema( url.toString() );
            }
        }
        catch ( XMLSchemaFileImportException e )
        {
            PluginUtils.logError( "An error occured when loading the schema " + schemaName + ".", e );
            ViewUtils.displayErrorMessageBox( "Projects Saving Error", "An error occured when loading the schema "
                + schemaName + "." );
        }

        return schema;
    }


    /**
     * Gets a Connection from the given name.
     *
     * @param name
     *      the name of the Connection
     * @return
     *      the corresponding Connection, or null if no connection was found.
     */
    public static Connection getConnection( String name )
    {
        Connection[] connectionsArray = ConnectionCorePlugin.getDefault().getConnectionManager().getConnections();

        HashMap<String, Connection> connections = new HashMap<String, Connection>();
        for ( Connection connection : connectionsArray )
        {
            connections.put( connection.getName(), connection );
        }

        return connections.get( name );
    }
}
