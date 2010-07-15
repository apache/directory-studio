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
package org.apache.directory.studio.ldapservers;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.directory.studio.common.CommonUiUtils;
import org.apache.directory.studio.ldapservers.model.LdapServer;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;


/**
 * This class implements the LDAP Servers Manager.
 * <p>
 * It is used to store all the LDAP Servers used and defined in the plugin.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdapServersManager
{
    private static final String SERVERS = "servers";

    /** The default instance */
    private static LdapServersManager instance;

    /** The list of servers */
    private List<LdapServer> serversList;

    /** The map of servers identified by ID */
    private Map<String, LdapServer> serversIdMap;

    /** The listeners */
    private List<LdapServersManagerListener> listeners;


    /**
     * Creates a new instance of ServersHandler.
     */
    private LdapServersManager()
    {
    }


    /**
     * Gets the default servers handler (singleton pattern).
     *
     * @return
     *      the default servers handler
     */
    public static LdapServersManager getDefault()
    {
        if ( instance == null )
        {
            instance = new LdapServersManager();
        }

        return instance;
    }


    /**
     * Adds a server.
     *
     * @param server
     *      the server to be added
     */
    public void addServer( LdapServer server )
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
    private void addServer( LdapServer server, boolean notifyListeners )
    {
        if ( !serversList.contains( server ) )
        {
            // Adding the server
            serversList.add( server );
            serversIdMap.put( server.getId(), server );

            // Notifying listeners
            if ( notifyListeners )
            {
                for ( LdapServersManagerListener listener : listeners.toArray( new LdapServersManagerListener[0] ) )
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
    public void removeServer( LdapServer server )
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
    private void removeServer( LdapServer server, boolean notifyListeners )
    {
        if ( serversList.contains( server ) )
        {
            // Removing the server
            serversList.remove( server );
            serversIdMap.remove( server.getId() );

            // Notifying listeners
            if ( notifyListeners )
            {
                for ( LdapServersManagerListener listener : listeners.toArray( new LdapServersManagerListener[0] ) )
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
    public boolean containsServer( LdapServer server )
    {
        return serversList.contains( server );
    }


    /**
     * Adds a listener to the servers handler.
     *
     * @param listener
     *      the listener to add
     */
    public void addListener( LdapServersManagerListener listener )
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
    public void removeListener( LdapServersManagerListener listener )
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
        // Initializing lists and maps
        serversList = new ArrayList<LdapServer>();
        serversIdMap = new HashMap<String, LdapServer>();
        listeners = new ArrayList<LdapServersManagerListener>();

        File store = getServersStorePath().toFile();
        File tempStore = getServersStoreTempPath().toFile();
        boolean loadFailed = false;
        String exceptionMessage = ""; //$NON-NLS-1$

        // We try to load the servers file
        if ( store.exists() )
        {
            try
            {
                InputStream inputStream = new FileInputStream( store );
                List<LdapServer> servers = LdapServersManagerIO.read( inputStream );
                for ( LdapServer server : servers )
                {
                    addServer( server, false );
                }
                return;
            }
            catch ( FileNotFoundException e )
            {
                loadFailed = true;
                exceptionMessage = e.getMessage();
            }
            catch ( LdapServersManagerIOException e )
            {
                loadFailed = true;
                exceptionMessage = e.getMessage();
            }

            if ( loadFailed )
            {
                if ( tempStore.exists() )
                {
                    // If something went wrong, we try to load the temp servers file
                    try
                    {
                        InputStream inputStream = new FileInputStream( tempStore );
                        List<LdapServer> servers = LdapServersManagerIO.read( inputStream );
                        for ( LdapServer server : servers )
                        {
                            addServer( server, false );
                        }
                        return;
                    }
                    catch ( Exception e )
                    {
                        CommonUiUtils.reportError( Messages.getString( "LdapServersManager.ErrorLoadingServer" ) //$NON-NLS-1$
                            + e.getMessage() );
                    }
                }
                else
                {
                    CommonUiUtils.reportError( Messages.getString( "LdapServersManager.ErrorLoadingServer" ) //$NON-NLS-1$
                        + exceptionMessage );
                }
            }
        }
    }


    /**
     * Saves the server to the file store.
     */
    public void saveServersToStore()
    {
        File store = getServersStorePath().toFile();
        File tempStore = getServersStoreTempPath().toFile();
        boolean saveFailed = false;

        try
        {
            // Saving the servers to the temp servers file
            OutputStream outputStream = new FileOutputStream( tempStore );
            LdapServersManagerIO.write( serversList, outputStream );

            // Copying the temp servers file to the final location
            String content = FileUtils.readFileToString( tempStore, "UTF-8" ); //$NON-NLS-1$
            FileUtils.writeStringToFile( store, content, "UTF-8" ); //$NON-NLS-1$
        }
        catch ( Exception e )
        {
            saveFailed = true;
        }

        if ( saveFailed )
        {
            // If an error occurs when saving to the temp servers file or
            // when copying the temp servers file to the final location,
            // we try to save the servers directly to the final location.
            try
            {
                // Saving the servers to the temp servers file
                OutputStream outputStream = new FileOutputStream( store );
                LdapServersManagerIO.write( serversList, outputStream );
                outputStream.close();
            }
            catch ( Exception e )
            {
                CommonUiUtils
                    .reportError( Messages.getString( "LdapServersManager.ErrorLoadingServer" ) + e.getMessage() ); //$NON-NLS-1$
            }
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
        return LdapServersPlugin.getDefault().getStateLocation().append( "ldapServers.xml" ); //$NON-NLS-1$
    }


    /**
     * Gets the path to the server temp file.
     *
     * @return
     *      the path to the server temp file.
     */
    private IPath getServersStoreTempPath()
    {
        return LdapServersPlugin.getDefault().getStateLocation().append( "ldapServers-temp.xml" ); //$NON-NLS-1$
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
        for ( LdapServer serverInstance : serversList )
        {
            if ( serverInstance.getName().equalsIgnoreCase( name ) )
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
    public List<LdapServer> getServersList()
    {
        return serversList;
    }


    /**
     * Gets the server associated with the given id.
     *
     * @return
     *      the server associated witht the given id.
     */
    public LdapServer getServerById( String id )
    {
        return serversIdMap.get( id );
    }


    /**
     * Get the path to the servers folder.
     *
     * @return
     *      the path to the server folder
     */
    public static IPath getServersFolder()
    {
        return LdapServersPlugin.getDefault().getStateLocation().append( SERVERS );
    }


    /**
     * Gets the path to the server's folder.
     *
     * @param server
     *      the server
     * @return
     *      the path to the server's folder
     */
    public static IPath getServerFolder( LdapServer server )
    {
        if ( server != null )
        {

            return getServersFolder().append( server.getId() );
        }

        return null;
    }


    /**
    * Creates a new server folder for the given id.
    *
    * @param id
    *      the id of the server
    */
    public static void createNewServerFolder( LdapServer server )
    {
        if ( server != null )
        {
            // Creating the server folder
            File serverFolder = getServerFolder( server ).toFile();
            serverFolder.mkdir();
        }
    }


    /**
     * Gets the memento for the given server.
     *
     * @param server
     *      the server
     * @return
     *      the associated memento
     */
    public static IMemento getMementoForServer( LdapServer server )
    {
        try
        {
            if ( server != null )
            {
                // Creating the File of the memento (if needed)
                File mementoFile = getServerFolder( server ).append( "memento.xml" ).toFile();
                if ( !mementoFile.exists() )
                {
                    mementoFile.createNewFile();
                }

                // Getting a (read-only) memento from the File
                XMLMemento readMemento = XMLMemento.createReadRoot( new FileReader( mementoFile ) );

                // Converting the read memento to a writable memento
                XMLMemento memento = XMLMemento.createWriteRoot( "memento" );
                memento.putMemento( readMemento );

                return memento;
            }

            return null;
        }
        catch ( Exception e )
        {
            return null;
        }
    }
}
