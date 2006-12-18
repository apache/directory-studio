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

package org.apache.directory.ldapstudio.browser.model;


/**
 * This class is used to represent the events related to modifications on the
 * Connections Class, such as : - "Add", - "Update" or - "Remove" of a
 * Connection
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ConnectionsEvent
{
    /**
     * This enum represents the different types of a ConnectionEvent
     */
    public enum ConnectionsEventType
    {
        ADD, UPDATE, REMOVE
    };

    private ConnectionsEventType type;

    private Connection connection;


    /**
     * Default constructor
     * 
     * @param type
     *                the type of the event
     * @param connection
     *                the connection associated with the event
     */
    public ConnectionsEvent( ConnectionsEventType type, Connection connection )
    {
        this.type = type;
        this.connection = connection;
    }


    public Connection getConnection()
    {
        return connection;
    }


    public ConnectionsEventType getType()
    {
        return type;
    }
}
