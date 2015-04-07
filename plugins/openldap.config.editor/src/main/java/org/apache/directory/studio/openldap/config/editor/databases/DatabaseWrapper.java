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
package org.apache.directory.studio.openldap.config.editor.databases;

import org.apache.directory.studio.openldap.config.model.database.OlcDatabaseConfig;




/**
 * This class implements a database wrapper used in the 'Databases' page UI.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DatabaseWrapper
{
    /** The wrapped database */
    private OlcDatabaseConfig database;


    /**
     * Creates a new instance of DatabaseWrapper.
     */
    public DatabaseWrapper()
    {
    }


    /**
     * Creates a new instance of DatabaseWrapper.
     *
     * @param database the wrapped database
     */
    public DatabaseWrapper( OlcDatabaseConfig database )
    {
        this.database = database;
    }


    /**
     * Gets the wrapped database.
     *
     * @return the wrapped database
     */
    public OlcDatabaseConfig getDatabase()
    {
        return database;
    }


    /**
     * Sets the wrapped database.
     *
     * @param database the wrapped database
     */
    public void setDatabase( OlcDatabaseConfig database )
    {
        this.database = database;
    }
}
