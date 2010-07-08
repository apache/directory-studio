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


import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.jobs.StudioProgressMonitor;


/**
 * This class represents an AbstractSchemaConnector and implements SchemaConnector.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class AbstractSchemaConnector implements SchemaConnector
{
    /** The name */
    private String name;

    /** The ID */
    private String id;

    /** The description */
    private String description;


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.model.io.SchemaConnector#getDecription()
     */
    public String getDescription()
    {
        return description;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.model.io.SchemaConnector#getId()
     */
    public String getId()
    {
        return id;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.model.io.SchemaConnector#getName()
     */
    public String getName()
    {
        return name;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.model.io.SchemaConnector#isSuitableConnector(org.apache.directory.studio.connection.core.Connection, org.apache.directory.studio.connection.core.StudioProgressMonitor)
     */
    public boolean isSuitableConnector( Connection connection, StudioProgressMonitor monitor )
    {
        return false;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.model.io.SchemaConnector#setDescription(java.lang.String)
     */
    public void setDescription( String description )
    {
        this.description = description;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.model.io.SchemaConnector#setId(java.lang.String)
     */
    public void setId( String id )
    {
        this.id = id;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.model.io.SchemaConnector#setName(java.lang.String)
     */
    public void setName( String name )
    {
        this.name = name;
    }
}
