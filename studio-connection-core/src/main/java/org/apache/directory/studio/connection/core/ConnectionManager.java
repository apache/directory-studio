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

package org.apache.directory.studio.connection.core;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;
import org.apache.directory.studio.connection.core.event.ConnectionUpdateListener;
import org.apache.directory.studio.connection.core.io.ConnectionIO;
import org.apache.directory.studio.connection.core.io.ConnectionIOException;


/**
 * This class is used to manage {@link Connection}s.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ConnectionManager implements ConnectionUpdateListener
{

    /** The list of connections. */
    private List<Connection> connectionList;


    /**
     * Creates a new instance of ConnectionManager.
     */
    public ConnectionManager()
    {
        this.connectionList = new ArrayList<Connection>();
        loadConnections();
        ConnectionEventRegistry.addConnectionUpdateListener( this, ConnectionCorePlugin.getDefault().getEventRunner() );
    }


    /**
     * Gets the filename of the Connection Store.
     *
     * @return
     *      the filename of the Connection Store
     */
    public static final String getConnectionStoreFileName()
    {
        String filename = ConnectionCorePlugin.getDefault().getStateLocation().append( "connections.xml" ).toOSString(); //$NON-NLS-1$
        return filename;
    }


    /**
     * Adds the connection to the end of the connection list. If there is
     * already a connection with this name, the new connection is renamed.
     *
     * @param connection
     */
    public void addConnection( Connection connection )
    {
        addConnection( connectionList.size(), connection );
    }


    /**
     * Adds the connection at the specified position of the connection list.
     * If there is already a connection with this name the new connection is
     * renamed.
     *
     * @param index
     * @param connection
     */
    public void addConnection( int index, Connection connection )
    {
        if ( getConnectionByName( connection.getConnectionParameter().getName() ) != null )
        {
            String newConnectionName = Messages.bind( Messages.copy_n_of_s,
                "", connection.getConnectionParameter().getName() ); //$NON-NLS-1$
            for ( int i = 2; getConnectionByName( newConnectionName ) != null; i++ )
            {
                newConnectionName = Messages.bind( Messages.copy_n_of_s,
                    i + " ", connection.getConnectionParameter().getName() ); //$NON-NLS-1$
            }
            connection.getConnectionParameter().setName( newConnectionName );
        }

        connectionList.add( index, connection );
        ConnectionEventRegistry.fireConnectionAdded( connection, this );
    }


    /**
     * Gets a connection from its id.
     *
     * @param id
     *      the id of the Connection
     * @return
     *      the corresponding Connection
     */
    public Connection getConnectionById( String id )
    {
        for ( Connection conn:connectionList )
        {
            if ( conn.getConnectionParameter().getId().equals( id ) )
            {
                return conn;
            }
        }
        return null;
    }


    /**
     * Gets a connection from its name.
     *
     * @param name
     *      the name of the Connection
     * @return
     *      the corresponding Connection
     */
    public Connection getConnectionByName( String name )
    {
        for ( Connection conn:connectionList )
        {
            if ( conn.getConnectionParameter().getName().equals( name ) )
            {
                return conn;
            }
        }
        return null;
    }


    /**
     * Gets the index in the Connection list of the first occurrence of the specified Connection.
     *
     * @param connection
     *      the Connection to search for
     * @return
     *      the index in the Connection list of the first occurrence of the specified Connection
     */
    public int indexOf( Connection connection )
    {
        return connectionList.indexOf( connection );
    }


    /**
     * Removes the given Connection from the Connection list.
     *
     * @param connection
     *      the connection to remove
     */
    public void removeConnection( Connection connection )
    {
        connectionList.remove( connection );
        ConnectionEventRegistry.fireConnectionRemoved( connection, this );
    }


    /**
     * Gets an array containing all the Connections.
     *
     * @return
     *      an array containing all the Connections
     */
    public Connection[] getConnections()
    {
        return ( Connection[] ) connectionList.toArray( new Connection[0] );
    }


    /**
     * Gets the number of Connections.
     *
     * @return
     *      the number of Connections
     */
    public int getConnectionCount()
    {
        return connectionList.size();
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionAdded(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionAdded( Connection connection )
    {
        saveConnections();
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionRemoved(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionRemoved( Connection connection )
    {
        saveConnections();
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionUpdated(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionUpdated( Connection connection )
    {
        saveConnections();
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionOpened(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionOpened( Connection connection )
    {
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionClosed(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionClosed( Connection connection )
    {
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionFolderModified(org.apache.directory.studio.connection.core.ConnectionFolder)
     */
    public void connectionFolderModified( ConnectionFolder connectionFolder )
    {
    }


    /**
     * Saves the Connections
     */
    private synchronized void saveConnections()
    {
        List<ConnectionParameter> connectionParameters = new ArrayList<ConnectionParameter>();
        for ( Connection connection : connectionList )
        {
            connectionParameters.add( connection.getConnectionParameter() );
        }

        // To avoid a corrupt file, save object to a temp file first 
        try
        {
            ConnectionIO.save( connectionParameters, new FileOutputStream( getConnectionStoreFileName() + "-temp" ) );
        }
        catch ( IOException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // move temp file to good file
        File file = new File( getConnectionStoreFileName() );
        File tempFile = new File( getConnectionStoreFileName() + "-temp" );
        if ( file.exists() )
        {
            file.delete();
        }

        try
        {
            String content = FileUtils.readFileToString( tempFile, "UTF-8" );
            FileUtils.writeStringToFile( file, content, "UTF-8" );
        }
        catch ( IOException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * Loads the Connections
     */
    private synchronized void loadConnections()
    {
        List<ConnectionParameter> connectionParameters = null;

        try
        {
            connectionParameters = ConnectionIO.load( new FileInputStream( getConnectionStoreFileName() ) );
        }
        catch ( Exception e )
        {
            // If loading failed, try with temp file
            try
            {
                connectionParameters = ConnectionIO
                    .load( new FileInputStream( getConnectionStoreFileName() + "-temp" ) );
            }
            catch ( FileNotFoundException e1 )
            {
                // TODO Auto-generated catch block
                return;
            }
            catch ( ConnectionIOException e1 )
            {
                // TODO Auto-generated catch block
                return;
            }
        }

        if ( connectionParameters != null )
        {
            for ( ConnectionParameter connectionParameter : connectionParameters )
            {
                Connection conn = new Connection( connectionParameter );
                connectionList.add( conn );
            }
        }
    }
}
