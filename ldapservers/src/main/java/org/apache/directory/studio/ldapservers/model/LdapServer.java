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


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.eclipse.core.runtime.IAdaptable;


/**
 * The {@link LdapServer} interface defines the required methods
 * to implement an LDAP Server instance.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdapServer implements IAdaptable
{
    /** The ID of the server */
    private String id;

    /** The name of the server*/
    private String name;

    /** The status of the server */
    private LdapServerStatus status = LdapServerStatus.STOPPED;

    /** The LDAP Server Adapter Extension */
    private LdapServerAdapterExtension ldapServerAdapterExtension;

    /** The list of listeners */
    private List<LdapServerListener> listeners = new ArrayList<LdapServerListener>();


    /**
     * Creates a new instance of LDAP Server.
     * <p>
     * An ID is automatically created.
     */
    public LdapServer()
    {
        id = createId();
    }


    /**
     * Creates a new instance of LDAP Server.
     * <p>
     * An ID is automatically created.
     *
     * @param name
     *      the name of the server
     */
    public LdapServer( String name )
    {
        this.name = name;
        id = createId();
    }


    /**
     * Creates a new ID.
     *
     * @return
     *      a new ID
     */
    private static String createId()
    {
        return UUID.randomUUID().toString();
    }


    /**
     * Adds the {@link LdapServerListener} to the server.
     *
     * @param listener
     *      the listener to be added
     */
    public void addListener( LdapServerListener listener )
    {
        if ( !listeners.contains( listener ) )
        {
            listeners.add( listener );
        }
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


    /**
     * Gets the associated {@link LdapServerAdapterExtension}.
     *
     * @return
     *      the associated {@link LdapServerAdapterExtension}
     */
    public LdapServerAdapterExtension getLdapServerAdapterExtension()
    {
        return ldapServerAdapterExtension;
    }


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


    /**
     * Removes the {@link LdapServerListener} from the server.
     *
     * @param listener
     *      the listener to be removed
     */
    public void removeListener( LdapServerListener listener )
    {
        if ( !listeners.contains( listener ) )
        {
            listeners.remove( listener );
        }
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
     * Sets the ID of the server
     *
     * @param id
     *      the ID of the server
     */
    public void setId( String id )
    {
        this.id = id;
    }


    public void setLdapServerAdapterExtension( LdapServerAdapterExtension ldapServerAdapterExtension )
    {
        this.ldapServerAdapterExtension = ldapServerAdapterExtension;
    }


    /**
     * Sets the name of the server
     *
     * @param name
     *      the name of the server
     */
    public void setName( String name )
    {
        this.name = name;
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
     * {@inheritDoc}
     */
    public Object getAdapter( Class adapter )
    {
        return null;
    }

}
