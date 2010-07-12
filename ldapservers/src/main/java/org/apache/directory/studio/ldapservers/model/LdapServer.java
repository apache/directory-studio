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

package org.apache.directory.studio.ldapservers.model;


/**
 * The {@link LdapServer} interface defines the required methods
 * to implement an LDAP Server instance.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdapServer
{
    private String id;

    private String name;

    private LdapServerStatus status = LdapServerStatus.STOPPED;


    /**
     * Gets the name of the server.
     *
     * @return
     *      the name of the server
     */
    public String getName()
    {
        return name;
    }


    public void setName( String name )
    {
        this.name = name;
    }


    /**
     * Gets the id of the server.
     *
     * @return
     *      the id of the server
     */
    public String getId()
    {
        return id;
    }


    public void setId( String id )
    {
        this.id = id;
    }


    /**
     * Gets the associated {@link LdapServerAdapter}.
     *
     * @return
     *      the associated {@link LdapServerAdapter}
     */
    public LdapServerAdapter getLdapServerAdapter()
    {
        return null;
    }


    /**
     * Adds the {@link LdapServerListener} to the server.
     *
     * @param listener
     *      the listener to be added
     */
    public void addListener( LdapServerListener listener )
    {

    }


    /**
     * Removes the {@link LdapServerListener} from the server.
     *
     * @param listener
     *      the listener to be removed
     */
    public void removeListener( LdapServerListener listener )
    {

    }


    /**
     * Starts the server.
     *
     * @throws Exception
     *      if an error occurs when restarting the server
     */
    public void start() throws Exception
    {

    }


    /**
     * Stops the server.
     *
     * @throws Exception
     *      if an error occurs when restarting the server
     */
    public void stop() throws Exception
    {

    }


    /**
     * Restarts the server.
     *
     * @throws Exception
     *      if an error occurs when restarting the server
     */
    public void restart() throws Exception
    {

    }


    /**
     * Gets the status of the server.
     *
     * @return
     *      the status of the server
     */
    public LdapServerStatus getStatus()
    {
        return status;
    }
}
