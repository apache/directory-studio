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
import org.apache.directory.studio.openldap.config.model.database.OlcDatabaseConfig;


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
    private List<OlcDatabaseConfig> databases = new ArrayList<>();

    /** The other configuration elements list*/
    private List<OlcConfig> configurationElements = new ArrayList<>();
    
    /** The loaded modules */
    private List<OlcModuleList> modules = new ArrayList<>();


    /**
     * @return the list of modules
     */
    public List<OlcModuleList> getModules()
    {
        return modules;
    }


    /**
     * Add a module in the list of modules
     * 
     * @param modules the modules to add
     */
    public void add( OlcModuleList module )
    {
        modules.add( module );
    }


    /**
     * Remove a module from the list of modules
     * 
     * @param modules the modules to remove
     */
    public boolean remove( OlcModuleList module )
    {
        return modules.remove( module );
    }


    /**
     * Reset the module list
     */
    public void clearModuleList()
    {
        modules.clear();
    }


    /**
     * @return the list of configuration elements
     */
    public List<OlcConfig> getConfigurationElements()
    {
        return configurationElements;
    }

    
    /**
     * Add a configuration element in the list of elements
     * 
     * @param element the element to add
     */
    public boolean add( OlcConfig element )
    {
        return configurationElements.add( element );
    }


    /**
     * Tells if the list of elements contains a given element
     *
     * @param element The element we are looking for
     * @return true if the element exists
     */
    public boolean contains( OlcConfig element )
    {
        return configurationElements.contains( element );
    }


    /**
     * Remove a element from the list of configuration elements
     * 
     * @param element the element to remove
     */
    public boolean remove( OlcConfig element )
    {
        return configurationElements.remove( element );
    }


    /**
     * @return the list of databases
     */
    public List<OlcDatabaseConfig> getDatabases()
    {
        return databases;
    }


    /**
     * Add a database in the list of databases
     * 
     * @param database the database to add
     */
    public boolean add( OlcDatabaseConfig database )
    {
        return databases.add( database );
    }


    /**
     * Reset the database list
     */
    public void clearDatabases()
    {
        databases.clear();
    }


    /**
     * Remove a database from the list of databases
     * 
     * @param database the database to remove
     */
    public boolean remove( OlcDatabaseConfig database )
    {
        return databases.remove( database );
    }


    /**
     * @return the connection
     */
    public Connection getConnection()
    {
        return connection;
    }


    /**
     * @return the global configuration
     */
    public OlcGlobal getGlobal()
    {
        return global;
    }


    /**
     * Store the global configuration (which belongs to cn=config)
     * @param global The configuration
     */
    public void setGlobal( OlcGlobal global )
    {
        this.global = global;
    }


    /**
     * Stores the connection in the configuration
     *
     * @param connection The connection to store
     */
    public void setConnection( Connection connection )
    {
        this.connection = connection;
    }


    /**
     * @return The number of configuration elements stored
     */
    public int size()
    {
        return configurationElements.size();
    }
}
