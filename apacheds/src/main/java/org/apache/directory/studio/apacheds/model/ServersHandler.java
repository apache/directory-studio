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


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.directory.studio.apacheds.ApacheDsPlugin;
import org.eclipse.core.runtime.IPath;


/**
 * This class implements the servers handler.
 * <p>
 * 
 * It is used to store all the server instances used in the plugin.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ServersHandler
{
    /** The default instance */
    private static ServersHandler instance;

    /** The list of server instances */
    private List<ServerInstance> serverInstancesList;
    
    /** The map of server instances identified by ID */
    private Map<String, ServerInstance> serverInstancesIdMap;

    /** The listeners */
    private List<ServersHandlerListener> listeners;


    /**
     * Creates a new instance of ServersHandler.
     */
    private ServersHandler()
    {
        // Initializing lists and maps
        serverInstancesList = new ArrayList<ServerInstance>();
        serverInstancesIdMap = new HashMap<String, ServerInstance>();
        listeners = new ArrayList<ServersHandlerListener>();
    }


    /**
     * Gets the default servers handler (singleton pattern).
     *
     * @return
     *      the default servers handler
     */
    public static ServersHandler getDefault()
    {
        if ( instance == null )
        {
            instance = new ServersHandler();
        }

        return instance;
    }


    /**
     * Adds a server instance.
     *
     * @param serverInstance
     *      the server instance to be added
     */
    public void addServerInstance( ServerInstance serverInstance )
    {
        addServerInstance( serverInstance, true );

        saveServerInstancesToStore();
    }


    /**
     * Adds a server instance.
     *
     * @param serverInstance
     *      the server instance to be added
     * @param notifyListeners
     *      <code>true</code> if the listeners need to be notified, 
     *      <code>false</code> if not.
     */
    private void addServerInstance( ServerInstance serverInstance, boolean notifyListeners )
    {
        if ( !serverInstancesList.contains( serverInstance ) )
        {
            // Adding the server instance
            serverInstancesList.add( serverInstance );
            serverInstancesIdMap.put( serverInstance.getId(), serverInstance );

            // Notifying listeners
            if ( notifyListeners )
            {
                for ( ServersHandlerListener listener : listeners.toArray( new ServersHandlerListener[0] ) )
                {
                    listener.serverInstanceAdded( serverInstance );
                }
            }
        }
    }


    /**
     * Removes a server instance
     *
     * @param serverInstance
     *      the server instance to be removed
     */
    public void removeServerInstance( ServerInstance serverInstance )
    {
        removeServerInstance( serverInstance, true );

        saveServerInstancesToStore();
    }


    /**
     * Removes a server instance
     *
     * @param serverInstance
     *      the server instance to be removed
     * @param notifyListeners
     *      <code>true</code> if the listeners need to be notified, 
     *      <code>false</code> if not.
     */
    private void removeServerInstance( ServerInstance serverInstance, boolean notifyListeners )
    {
        if ( serverInstancesList.contains( serverInstance ) )
        {
            // Removing the server instance
            serverInstancesList.remove( serverInstance );
            serverInstancesIdMap.remove( serverInstance.getId() );

            // Notifying listeners
            if ( notifyListeners )
            {
                for ( ServersHandlerListener listener : listeners.toArray( new ServersHandlerListener[0] ) )
                {
                    listener.serverInstanceRemoved( serverInstance );
                }
            }
        }
    }


    /**
     * Indicates if the server handler contains the given server instance.
     *
     * @param serverInstance
     *      the server instance
     * @return
     *      <code>true</code> if the server hander contains the given server
     *      instance, <code>false</code> if not
     */
    public boolean containsServerInstance( ServerInstance serverInstance )
    {
        return serverInstancesList.contains( serverInstance );
    }


    /**
     * Adds a listener to the servers handler.
     *
     * @param listener
     *      the listener to add
     */
    public void addListener( ServersHandlerListener listener )
    {
        if ( !listeners.contains( listener ) )
        {
            listeners.add( listener );
        }
    }


    /**
     * Removes a listener to the servers handler.
     *
     * @param listener
     *      the listener to remove
     */
    public void removeListener( ServersHandlerListener listener )
    {
        if ( listeners.contains( listener ) )
        {
            listeners.remove( listener );
        }
    }


    /**
     * Loads the server instances from the file store.
     */
    public void loadServerInstancesFromStore()
    {
        File store = getServerInstancesStorePath().toFile();

        if ( store.exists() )
        {
            try
            {
                InputStream inputStream = new FileInputStream( store );
                List<ServerInstance> serverInstances = ServersHandlerIO.read( inputStream );
                for ( ServerInstance serverInstance : serverInstances )
                {
                    addServerInstance( serverInstance, false );
                }
            }
            catch ( FileNotFoundException e )
            {
                // Will never occur as the store file exists
            }
            catch ( ServersHandlerIOException e )
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }


    /**
     * Saves the server instances to the file store.
     */
    public void saveServerInstancesToStore()
    {
        try
        {
            OutputStream outputStream = new FileOutputStream( getServerInstancesStorePath().toFile() );
            ServersHandlerIO.write( serverInstancesList, outputStream );
        }
        catch ( FileNotFoundException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch ( IOException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * Gets the path to the server instances file.
     *
     * @return
     *      the path to the server instances file.
     */
    private IPath getServerInstancesStorePath()
    {
        return ApacheDsPlugin.getDefault().getStateLocation().append( "serverInstances.xml" );
    }


    /**
     * Indicates if the given is available (i.e. not already taken by another 
     * server instance).
     *
     * @param name
     *      the name
     * @return
     *      <code>true</code> if the name is available, <code>false</code> if
     *      not
     */
    public boolean isNameAvailable( String name )
    {
        for ( ServerInstance server : serverInstancesList )
        {
            if ( server.getName().equalsIgnoreCase( name ) )
            {
                return false;
            }
        }

        return true;
    }


    /**
     * Gets the server instances list.
     *
     * @return
     *      the server instances list.
     */
    public List<ServerInstance> getServerInstancesList()
    {
        return serverInstancesList;
    }


    /**
     * Gets the server instance associated with the given id.
     *
     * @return
     *      the server instance associated witht the given id.
     */
    public ServerInstance getServerInstanceById( String id )
    {
        return serverInstancesIdMap.get( id );
    }
}
