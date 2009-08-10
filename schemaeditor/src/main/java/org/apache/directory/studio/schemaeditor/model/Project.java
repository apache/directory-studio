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
package org.apache.directory.studio.schemaeditor.model;


import java.util.List;

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.schemaeditor.model.io.SchemaConnector;


/**
 * This class implements a Project.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class Project
{
    /**
     * This enum represents the different states of Project.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    public enum ProjectState
    {
        /** An Open project*/
        OPEN,
        /** A Closed project*/
        CLOSED
    }

    /** The type of the project */
    private ProjectType type;

    /** The name of the project */
    private String name;

    /** The connection of the project */
    private Connection connection;

    /** The state of the project */
    private ProjectState state;

    /** The SchemaConnector of the project */
    private SchemaConnector schemaConnector;

    /** The SchemaHandler */
    private SchemaHandler schemaHandler;

    /** The backup of the Online Schema */
    private List<Schema> schemaBackup;

    /** The flag for Online Schema Fetch */
    private boolean hasOnlineSchemaBeenFetched = false;


    /**
     * Creates a new instance of Project.
     *
     * @param type
     *      the type of project
     * @param name
     *      the name of project
     */
    public Project( ProjectType type, String name )
    {
        init( type, name, ProjectState.CLOSED );
    }


    /**
     * Creates a new instance of Project.
     * The default type is used : OFFLINE.
     */
    public Project()
    {
        init( ProjectType.OFFLINE, null, ProjectState.CLOSED );
    }


    /**
     * Creates a new instance of Project.
     *
     * @param type
     *      the type of project
     */
    public Project( ProjectType type )
    {
        init( type, null, ProjectState.CLOSED);
    }
    
    /**
     * Inits the project.
     *
     * @param type
     *      the type of the project
     * @param name
     *      the name of the project
     * @param state
     *      the state of the project
     */
    private void init( ProjectType type, String name, ProjectState state)
    {
        this.type = type;
        this.name = name;
        this.state = state;
        schemaHandler = new SchemaHandler();
    }


    /**
     * Gets the type of the project.
     *
     * @return
     *      the type of the project
     */
    public ProjectType getType()
    {
        return type;
    }


    /**
     * Sets the type of the project.
     *
     * @param type
     *      the type of the project
     */
    public void setType( ProjectType type )
    {
        this.type = type;
    }


    /**
     * Gets the name of the project.
     *
     * @return
     *      the name of the project
     */
    public String getName()
    {
        return name;
    }


    /**
     * Sets the name of the project
     *
     * @param name
     *      the name
     */
    public void setName( String name )
    {
        this.name = name;
    }


    /**
     * Gets the state of the project.
     *
     * @return
     *      the state of the project
     */
    public ProjectState getState()
    {
        return state;
    }


    /**
     * Sets the state of the project
     *
     * @param state
     *      the state
     */
    public void setState( ProjectState state )
    {
        this.state = state;
    }


    /**
     * Gets the SchemaHandler
     *
     * @return
     *      the SchemaHandler
     */
    public SchemaHandler getSchemaHandler()
    {
        return schemaHandler;
    }


    /**
     * Gets the Connection.
     *
     * @return
     *      the connection
     */
    public Connection getConnection()
    {
        return connection;
    }


    /**
     * Sets the Connection.
     *
     * @param connection
     *      the connection
     */
    public void setConnection( Connection connection )
    {
        this.connection = connection;
    }


    /**
     * Fetches the Online Schema.
     *
     * @param monitor
     *      a StudioProgressMonitor
     */
    public void fetchOnlineSchema( StudioProgressMonitor monitor )
    {
        if ( ( !hasOnlineSchemaBeenFetched ) && ( connection != null ) && ( schemaConnector != null ) )
        {
            schemaBackup = schemaConnector.importSchema( connection, monitor );
            for ( Schema schema : schemaBackup )
            {
                schema.setProject( this );
            }

            if ( schemaBackup != null )
            {
                monitor.beginTask( "Adding Schema to project", schemaBackup.size() );
                for ( Schema schema : schemaBackup )
                {
                    getSchemaHandler().addSchema( schema );
                }
            }

            // TODO Add error Handling
            monitor.done();
            hasOnlineSchemaBeenFetched = true;
        }
    }


    /**
     * Returns whether the online schema has been fetched.
     *
     * @return
     *      true if the online schema has bee fetched
     */
    public boolean hasOnlineSchemaBeenFetched()
    {
        return hasOnlineSchemaBeenFetched;
    }


    /**
     * Gets the Schema Backup.
     *
     * @return
     *      the Schema Backup
     */
    public List<Schema> getSchemaBackup()
    {
        return schemaBackup;
    }


    /**
     * Sets the Schema Backup
     *
     * @param schemaBackup
     *      the Schema Backup
     */
    public void setSchemaBackup( List<Schema> schemaBackup )
    {
        this.schemaBackup = schemaBackup;
    }


    /**
     * Gets the SchemaConnector.
     *
     * @return
     *      the SchemaConnector
     */
    public SchemaConnector getSchemaConnector()
    {
        return schemaConnector;
    }


    /**
     * Sets the SchemaConnector.
     *
     * @param schemaConnector
     *      the SchemaConnector
     */
    public void setSchemaConnector( SchemaConnector schemaConnector )
    {
        this.schemaConnector = schemaConnector;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object obj )
    {
        if ( obj instanceof Project )
        {
            Project project = ( Project ) obj;
            if ( !getName().equals( project.getName() ) )
            {
                return false;
            }
            else if ( !getType().equals( project.getType() ) )
            {
                return false;
            }
            else if ( !getState().equals( project.getState() ) )
            {
                return false;
            }

            return true;
        }

        // Default
        return super.equals( obj );
    }
}
