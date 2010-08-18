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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IActionFilter;


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

    /** The Map for custom objects */
    private Map<String, Object> customObjectsMap = new HashMap<String, Object>();


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
     * Returns the value to which the specified key is mapped, 
     * or null if no mapping for the key is found.
     *
     * @param key
     *      the key
     * @return
     *      the value to which the specified key is mapped, 
     *      or null if no mapping for the key is found.
     */
    public Object getCustomObject( String key )
    {
        return customObjectsMap.get( key );
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
     * Associates the specified value with the specified key.
     *
     * @param key
     *      the key
     * @param value
     *      the value
     */
    public void putCustomObject( String key, Object value )
    {
        customObjectsMap.put( key, value );
    }


    /**
     * Removes the value to which the specified key is mapped.
     * <p>
     * Returns the value previously associated the key,
     * or null if there was no mapping for the key.
     *
     * @param key
     * @return
     */
    public Object removeCustomObject( String key )
    {
        return customObjectsMap.remove( key );
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
        if ( this.name == name )
        {
            return;
        }

        this.name = name;

        fireServerNameChangeEvent();
    }


    /**
     * Fire a server listener name change event.
     */
    private void fireServerNameChangeEvent()
    {
        for ( LdapServerListener listener : listeners.toArray( new LdapServerListener[0] ) )
        {
            listener.serverChanged( new LdapServerEvent( this, LdapServerEventType.RENAMED ) );
        }
    }


    /**
     * Sets the status
     *
     * @param status
     *      the status
     */
    public void setStatus( LdapServerStatus status )
    {
        if ( this.status == status )
        {
            return;
        }

        this.status = status;

        fireServerStateChangeEvent();
    }


    /**
     * Fires a server listener status change event.
     */
    private void fireServerStateChangeEvent()
    {
        for ( LdapServerListener listener : listeners.toArray( new LdapServerListener[0] ) )
        {
            listener.serverChanged( new LdapServerEvent( this, LdapServerEventType.STATUS_CHANGED ) );
        }
    }


    /**
     * {@inheritDoc}
     */
    public Object getAdapter( Class adapter )
    {
        if ( adapter == IActionFilter.class )
        {
            return LdapServerActionFilterAdapter.getInstance();
        }

        return null;
    }

}
