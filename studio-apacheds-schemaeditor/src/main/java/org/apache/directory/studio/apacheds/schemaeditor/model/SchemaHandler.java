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


import java.util.ArrayList;
import java.util.List;


/**
 * The SchemaHandler is used to manage the Schema.
 * 
 * This class is a Singleton, use SchemaHandler.getInstance() to get an 
 * instance of the class.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SchemaHandler
{
    /** The instance */
    private static SchemaHandler instance = null;

    private List<Schema> schemas;


    /**
     * Gets the instance of SchemaHandler.
     *
     * @return
     *      the instance of SchemaHandler
     */
    public static SchemaHandler getInstance()
    {
        if ( instance == null )
        {
            instance = new SchemaHandler();
        }

        return instance;
    }


    /**
     * Private Constructor.
     */
    private  SchemaHandler()
    {
        // Constructor is 'private' to disable the instanciation ("new SchemaHandler")

        schemas = new ArrayList<Schema>();
    }


    /**
     * Gets the Schema List.
     *
     * @return
     *      the Schema List
     */
    public List<Schema> getSchemas()
    {
        return schemas;
    }


    /**
     * Adds a Schema.
     *
     * @param schema
     *      the Schema
     * @return
     *      true (as per the general contract of the Collection.add method).
     */
    public boolean addSchema( Schema schema )
    {
        return schemas.add( schema );
    }


    /**
     * Removes a Schema.
     *
     * @param schema
     *      the Schema
     * @return
     *      true if this list contained the specified element.
     */
    public boolean removeSchema( Schema schema )
    {
        return schemas.remove( schema );
    }
}
