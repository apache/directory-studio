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
package org.apache.directory.studio.apacheds.model;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.directory.studio.apacheds.jobs.LaunchServerJob;
import org.eclipse.core.runtime.IAdaptable;


/**
 * This class represents an Apache DS server.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class Server implements IAdaptable
{
    /** The name of the server */
    private String name;

    /** The ID of the server */
    private String id;

    /** The version of the server */
    private ServerVersion version = ServerVersion.VERSION_1_5_4;

    /** The state of the server */
    private ServerStateEnum state = ServerStateEnum.STOPPED;

    /** The listeners list*/
    private List<ServerListener> listeners = new ArrayList<ServerListener>();

    /** The launch job */
    private LaunchServerJob launchJob;


    /**
     * Creates a new instance of Server.
     */
    public Server()
    {
    }


    /**
     * Adds a listener.
     *
     * @param listener
     *      the listener
     */
    public void addListener( ServerListener listener )
    {
        if ( listener == null )
        {
            throw new IllegalArgumentException( "Listener cannot be null" );
        }

        listeners.add( listener );
    }


    /**
     * Removes a listener.
     *
     * @param listener
     *      the listener
     */
    public void removeListener( ServerListener listener )
    {
        if ( listener == null )
        {
            throw new IllegalArgumentException( "Listener cannot be null" );
        }

        listeners.remove( listener );
    }


    /**
     * Creates a new instance of Server.
     * <p>
     * An ID is automatically created.
     *
     * @param name
     *      the name of the server
     */
    public Server( String name )
    {
        this.name = name;
        id = createId();
    }


    /**
     * Creates a new instance of Server.
     *
     * @param name
     *      the name of the server
     * @param id
     *      the id of the server
     */
    public Server( String name, String id )
    {
        this.name = name;
        this.id = id;
    }


    /**
     * Gets the name of the server
     *
     * @return
     *      the name of the server
     */
    public String getName()
    {
        return name;
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
        for ( ServerListener listener : listeners.toArray( new ServerListener[0] ) )
        {
            listener.serverChanged( new ServerEvent( this, ServerEventEnum.RENAMED ) );
        }
    }


    /**
     * Gets the ID of the server.
     *
     * @return
     *      the ID of the server
     */
    public String getId()
    {
        return id;
    }


    /**
     * Sets the ID of the server.
     *
     * @param id
     *      the ID of the server
     */
    public void setId( String id )
    {
        this.id = id;
    }


    /**
     * Creates a new ID.
     *
     * @return
     *      a new ID
     */
    public static String createId()
    {
        return UUID.randomUUID().toString();
    }


    /**
     * Gets the state.
     *
     * @return
     *      the state
     */
    public ServerStateEnum getState()
    {
        return state;
    }


    /**
     * Sets the state
     *
     * @param state
     *      the state
     */
    public void setState( ServerStateEnum state )
    {
        if ( this.state == state )
        {
            return;
        }

        this.state = state;

        fireServerStateChangeEvent();
    }


    /**
     * Fires a server listener state change event.
     */
    private void fireServerStateChangeEvent()
    {
        for ( ServerListener listener : listeners.toArray( new ServerListener[0] ) )
        {
            listener.serverChanged( new ServerEvent( this, ServerEventEnum.STATE_CHANGED ) );
        }
    }


    /**
     * Gets the launch job.
     *
     * @return
     *      the launch job
     */
    public LaunchServerJob getLaunchJob()
    {
        return launchJob;
    }


    /**
     * Sets the launch job.
     *
     * @param launchJob
     *      the launch job
     */
    public void setLaunchJob( LaunchServerJob launchJob )
    {
        this.launchJob = launchJob;
    }


    /**
     * Gets the version of the server
     *
     * @return
     *      the version of the server
     */
    public ServerVersion getVersion()
    {
        return version;
    }


    /**
     * Sets the version of the server
     *
     * @param version
     *      the version of the server
     */
    public void setVersion( ServerVersion version )
    {
        this.version = version;
    }


    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    public Object getAdapter( Class adapter )
    {
        return null;
    }
}
