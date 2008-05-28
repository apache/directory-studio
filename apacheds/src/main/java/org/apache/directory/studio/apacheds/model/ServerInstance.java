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

import org.apache.directory.studio.apacheds.jobs.LaunchServerInstanceJob;
import org.eclipse.core.runtime.IAdaptable;


/**
 * This class represents an Apache DS instance.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ServerInstance implements IAdaptable
{
    /** The name of the instance */
    private String name;

    /** The ID of the instance */
    private String id;

    /** The state of the instance */
    private ServerStateEnum state = ServerStateEnum.STOPPED;

    /** The listeners list*/
    private List<ServerListener> listeners = new ArrayList<ServerListener>();

    /** The launch job */
    private LaunchServerInstanceJob launchJob;


    /**
     * Creates a new instance of ApacheDsInstance.
     */
    public ServerInstance()
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
     * Creates a new instance of ApacheDsInstance.
     * <p>
     * An ID is automatically created.
     *
     * @param name
     *      the name of the instance
     */
    public ServerInstance( String name )
    {
        this.name = name;
        id = createId();
    }


    /**
     * Creates a new instance of ApacheDsInstance.
     *
     * @param name
     *      the name of the instance
     * @param id
     *      the id of the instance
     */
    public ServerInstance( String name, String id )
    {
        this.name = name;
        this.id = id;
    }


    /**
     * Gets the name of the instance
     *
     * @return
     *      the name of the instance
     */
    public String getName()
    {
        return name;
    }


    /**
     * Sets the name of the instance
     *
     * @param name
     *      the name of the instance
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
     * Gets the ID of the instance.
     *
     * @return
     *      the ID of the instance
     */
    public String getId()
    {
        return id;
    }


    /**
     * Sets the ID of the instance.
     *
     * @param id
     *      the ID of the instance
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
    public LaunchServerInstanceJob getLaunchJob()
    {
        return launchJob;
    }


    /**
     * Sets the launch job.
     *
     * @param launchJob
     *      the launch job
     */
    public void setLaunchJob( LaunchServerInstanceJob launchJob )
    {
        this.launchJob = launchJob;
    }


    public Object getAdapter( Class adapter )
    {
        // TODO Auto-generated method stub
        return null;
    }
}
