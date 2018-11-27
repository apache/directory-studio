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


import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.schemaeditor.model.Project;


/**
 * This interface defines a SchemaConnector.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface SchemaConnector
{
    /**
     * Indicates whether the SchemaConnector is suitable for use with the
     * given connection.
     *
     * @param connection
     *      the connection
     * @param monitor
     *      the progress monitor
     * @return
     *      true if the SchemaConnector is suitable for the given connection,
     *      false if not
     */
    boolean isSuitableConnector( Connection connection, StudioProgressMonitor monitor );


    /**
     * Imports the Schema of the LDAP Server using the given project and
     * progress monitor.
     *
     * @param project
     *      the project
     * @param monitor
     *      the progress monitor
     * @throws SchemaConnectorException
     */
    void importSchema( Project project, StudioProgressMonitor monitor )
        throws SchemaConnectorException;


    /**
     * Exports the Schema to the LDAP Server using the given connection and
     * progress monitor.
     *
     * @param project
     *      the project
     * @param monitor
     *      the progress monitor
     * @throws SchemaConnectorException
     */
    void exportSchema( Project project, StudioProgressMonitor monitor )
        throws SchemaConnectorException;


    /**
     * Gets the name.
     *
     * @return
     *      the name
     */
    String getName();


    /**
     * Sets the name.
     *
     * @param name
     *      the name
     */
    void setName( String name );


    /**
     * Gets the ID.
     *
     * @return
     *      the ID
     */
    String getId();


    /**
     * Sets the ID.
     *
     * @param id
     *      the ID
     */
    void setId( String id );


    /**
     * Gets the description.
     *
     * @return
     *      the description
     */
    String getDescription();


    /**
     * Sets the description.
     *
     * @param description
     *      the description
     */
    void setDescription( String description );
}
