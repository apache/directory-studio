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
package org.apache.directory.studio.apacheds.schemaeditor.model;


import java.util.List;

import javax.naming.NamingException;

import org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.apacheds.schemaeditor.model.io.ApacheDSSchemaImporter;
import org.apache.directory.studio.apacheds.schemaeditor.model.schemachecker.SchemaChecker;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.StudioProgressMonitor;


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

    /** The SchemaHandler */
    private SchemaHandler schemaHandler;

    /** The SchemaCheker */
    private SchemaChecker schemaChecker;

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
        this.type = type;
        this.name = name;
        this.state = ProjectState.CLOSED;
        schemaHandler = new SchemaHandler();
        schemaChecker = new SchemaChecker();
    }


    /**
     * Creates a new instance of Project.
     * The default type is used : OFFLINE.
     */
    public Project()
    {
        type = ProjectType.OFFLINE;
        this.state = ProjectState.CLOSED;
        schemaHandler = new SchemaHandler();
        schemaChecker = new SchemaChecker();
    }


    /**
     * Creates a new instance of Project.
     *
     * @param type
     *      the type of project
     */
    public Project( ProjectType type )
    {
        this.type = type;
        this.state = ProjectState.CLOSED;
        schemaHandler = new SchemaHandler();
        schemaChecker = new SchemaChecker();
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
     * Gets the SchemaChecker
     *
     * @return
     *      the SchemaChecker
     */
    public SchemaChecker getSchemaChecker()
    {
        return schemaChecker;
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
        if ( !hasOnlineSchemaBeenFetched && ( connection != null ) )
        {
            try
            {
                schemaBackup = ApacheDSSchemaImporter.importSchema( connection.getJNDIConnectionWrapper(), monitor );
            }
            catch ( NamingException e )
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            monitor.beginTask( "Adding Schema to project", schemaBackup.size() );
            for ( Schema schema : schemaBackup )
            {
                getSchemaHandler().addSchema( schema );
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
