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

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.schemaeditor.model.Schema;


/**
 * This interface defines a SchemaConnector.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
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
    /**
     * TODO isSuitableConnector.
     *
     * @param connection
     * @param monitor
     * @return
     */
    public boolean isSuitableConnector( Connection connection, StudioProgressMonitor monitor );


    /**
     * Imports the Schema of the LDAP Server using the given connection and
     * progress monitor.
     *
     * @param connection
     *      the connection
     * @param monitor
     *      the progress monitor
     * @return
     *      the list of schemas of the LDAP Server
     */
    public List<Schema> importSchema( Connection connection, StudioProgressMonitor monitor );


    /**
     * Exports the Schema to the LDAP Server using the given connection and
     * progress monitor.
     *
     * @param connection
     *      the connection
     * @param monitor
     *      the progress monitor
     */
    public void exportSchema( Connection connection, StudioProgressMonitor monitor );


    /**
     * Gets the name.
     *
     * @return
     *      the name
     */
    public String getName();


    /**
     * Sets the name.
     *
     * @param name
     *      the name
     */
    public void setName( String name );


    /**
     * Gets the ID.
     *
     * @return
     *      the ID
     */
    public String getId();


    /**
     * Sets the ID.
     *
     * @param id
     *      the ID
     */
    public void setId( String id );


    /**
     * Gets the description.
     *
     * @return
     *      the description
     */
    public String getDescription();


    /**
     * Sets the description.
     *
     * @param description
     *      the description
     */
    public void setDescription( String description );
}
