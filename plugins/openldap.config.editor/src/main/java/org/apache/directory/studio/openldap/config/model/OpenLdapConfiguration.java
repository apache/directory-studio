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
package org.apache.directory.studio.openldap.config.model;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.connection.core.Connection;


/**
 * This class implements the basic class for an OpenLDAP configuration.
 * <p>
 * It contains all the configuration objects found under the "cn=config" branch.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OpenLdapConfiguration
{
    /** The connection */
    private Connection connection;

    /** The global configuration */
    private OlcGlobal global;

    /** The databases list */
    private List<OlcDatabaseConfig> databases = new ArrayList<OlcDatabaseConfig>();

    /** The other configuration elements list*/
    private List<OlcConfig> configurationElements = new ArrayList<OlcConfig>();


    public boolean add( OlcConfig o )
    {
        return configurationElements.add( o );
    }


    public boolean addDatabase( OlcDatabaseConfig o )
    {
        return databases.add( o );
    }


    public void clearDatabases()
    {
        databases.clear();
    }


    public boolean contains( OlcConfig o )
    {
        return configurationElements.contains( o );
    }


    public List<OlcConfig> getConfigurationElements()
    {
        return configurationElements;
    }


    public Connection getConnection()
    {
        return connection;
    }


    public List<OlcDatabaseConfig> getDatabases()
    {
        return databases;
    }


    public OlcGlobal getGlobal()
    {
        return global;
    }


    public boolean remove( OlcConfig o )
    {
        return configurationElements.remove( o );
    }


    public boolean removeDatabase( OlcDatabaseConfig o )
    {
        return databases.remove( o );
    }


    public void setConnection( Connection connection )
    {
        this.connection = connection;
    }


    public void setGlobal( OlcGlobal global )
    {
        this.global = global;
    }


    public int size()
    {
        return configurationElements.size();
    }
}
