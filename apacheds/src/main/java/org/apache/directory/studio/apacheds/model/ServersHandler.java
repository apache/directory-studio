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
 * It is used to store all the servers used in the plugin.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ServersHandler
{
    /** The default instance */
    private static ServersHandler instance;

    /** The list of servers */
    private List<Server> serversList;
    
    /** The map of servers identified by ID */
    private Map<String, Server> serversIdMap;

    /** The listeners */
    private List<ServersHandlerListener> listeners;


    /**
     * Creates a new instance of ServersHandler.
     */
    private ServersHandler()
    {
        // Initializing lists and maps
        serversList = new ArrayList<Server>();
        serversIdMap = new HashMap<String, Server>();
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
     * Adds a server.
     *
     * @param server
     *      the server to be added
     */
    public void addServer( Server server )
    {
        addServer( server, true );

        saveServersToStore();
    }


    /**
     * Adds a server.
     *
     * @param server
     *      the server to be added
     * @param notifyListeners
     *      <code>true</code> if the listeners need to be notified, 
     *      <code>false</code> if not.
     */
    private void addServer( Server server, boolean notifyListeners )
    {
        if ( !serversList.contains( server ) )
        {
            // Adding the server
            serversList.add( server );
            serversIdMap.put( server.getId(), server );

            // Notifying listeners
            if ( notifyListeners )
            {
                for ( ServersHandlerListener listener : listeners.toArray( new ServersHandlerListener[0] ) )
                {
                    listener.serverAdded( server );
                }
            }
        }
    }


    /**
     * Removes a server.
     *
     * @param server
     *      the server to be removed
     */
    public void removeServer( Server server )
    {
        removeServer( server, true );

        saveServersToStore();
    }


    /**
     * Removes a server.
     *
     * @param server
     *      the server to be removed
     * @param notifyListeners
     *      <code>true</code> if the listeners need to be notified, 
     *      <code>false</code> if not.
     */
    private void removeServer( Server server, boolean notifyListeners )
    {
        if ( serversList.contains( server ) )
        {
            // Removing the server
            serversList.remove( server );
            serversIdMap.remove( server.getId() );

            // Notifying listeners
            if ( notifyListeners )
            {
                for ( ServersHandlerListener listener : listeners.toArray( new ServersHandlerListener[0] ) )
                {
                    listener.serverRemoved( server );
                }
            }
        }
    }


    /**
     * Indicates if the server handler contains the given server.
     *
     * @param server
     *      the server
     * @return
     *      <code>true</code> if the server hander contains the given server, 
     *      <code>false</code> if not
     */
    public boolean containsServer( Server server )
    {
        return serversList.contains( server );
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
     * Loads the server from the file store.
     */
    public void loadServersFromStore()
    {
        File store = getServersStorePath().toFile();

        if ( store.exists() )
        {
            try
            {
                InputStream inputStream = new FileInputStream( store );
                List<Server> servers = ServersHandlerIO.read( inputStream );
                for ( Server server : servers )
                {
                    addServer( server, false );
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
     * Saves the server to the file store.
     */
    public void saveServersToStore()
    {
        try
        {
            OutputStream outputStream = new FileOutputStream( getServersStorePath().toFile() );
            ServersHandlerIO.write( serversList, outputStream );
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
     * Gets the path to the server file.
     *
     * @return
     *      the path to the server file.
     */
    private IPath getServersStorePath()
    {
        return ApacheDsPlugin.getDefault().getStateLocation().append( "servers.xml" );
    }


    /**
     * Indicates if the given is available (i.e. not already taken by another 
     * server).
     *
     * @param name
     *      the name
     * @return
     *      <code>true</code> if the name is available, <code>false</code> if
     *      not
     */
    public boolean isNameAvailable( String name )
    {
        for ( Server server : serversList )
        {
            if ( server.getName().equalsIgnoreCase( name ) )
            {
                return false;
            }
        }

        return true;
    }


    /**
     * Gets the servers list.
     *
     * @return
     *      the servers list.
     */
    public List<Server> getServersList()
    {
        return serversList;
    }


    /**
     * Gets the server associated with the given id.
     *
     * @return
     *      the server associated witht the given id.
     */
    public Server getServerById( String id )
    {
        return serversIdMap.get( id );
    }
}
