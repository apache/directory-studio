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
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;
import org.apache.directory.studio.connection.core.event.ConnectionUpdateListener;
import org.apache.directory.studio.connection.core.io.ConnectionIO;
import org.apache.directory.studio.connection.core.io.ConnectionIOException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;


/**
 * This class is used to manage {@link Connection}s.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ConnectionManager implements ConnectionUpdateListener
{
    private static final String LOGS_PATH = "logs"; //$NON-NLS-1$

    private static final String SEARCH_LOGS_PREFIX = "search-"; //$NON-NLS-1$

    private static final String MODIFICATIONS_LOG_PREFIX = "modifications-"; //$NON-NLS-1$

    private static final String LDIFLOG_SUFFIX = "-%u-%g.ldiflog"; //$NON-NLS-1$

    private static final String CONNECTIONS_XML = "connections.xml"; //$NON-NLS-1$

    public static final String ENCODING_UTF8 = "UTF-8"; //$NON-NLS-1$

    public static final String TEMP_SUFFIX = "-temp"; //$NON-NLS-1$


    /** The list of connections. */
    private Set<Connection> connectionList;


    /**
     * Creates a new instance of ConnectionManager.
     */
    public ConnectionManager()
    {
        this.connectionList = new HashSet<Connection>();
        loadInitializers();
        loadConnections();
        ConnectionEventRegistry.addConnectionUpdateListener( this, ConnectionCorePlugin.getDefault().getEventRunner() );
    }


    /**
     * Loads the Connection Initializers. This happens only for the first time, 
     * which is determined by whether or not the connectionStore file is present.
     */
    private void loadInitializers()
    {
        File connectionStore = new File( getConnectionStoreFileName() );
        if ( connectionStore.exists() )
        {
            return; // connections are stored from a previous sessions - don't call initializers
        }

        IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(
            ConnectionCorePlugin.getDefault().getPluginProperties().getString(
                "ExtensionPoint_ConnectionInitializer_id" ) ); //$NON-NLS-1$

        IConfigurationElement[] configurationElements = extensionPoint.getConfigurationElements();
        for ( IConfigurationElement configurationElement : configurationElements )
        {
            if ( "connection".equals( configurationElement.getName() ) ) //$NON-NLS-1$
            {
                addInitialConnection( configurationElement );
            }
        }
    }


    /**
     * Creates the ConnectionParameter from the configElement and creates the connection.
     * 
     * @param configurationElement The configuration element
     */
    private void addInitialConnection( IConfigurationElement configurationElement )
    {
        try
        {
            ConnectionParameter connectionParameter = ( ConnectionParameter ) configurationElement
                .createExecutableExtension( "class" ); //$NON-NLS-1$
            Connection conn = new Connection( connectionParameter );
            connectionList.add( conn );
        }
        catch ( CoreException e )
        {
            Status status = new Status( IStatus.ERROR, ConnectionCoreConstants.PLUGIN_ID,
                Messages.error__execute_connection_initializer + e.getMessage(), e );
            ConnectionCorePlugin.getDefault().getLog().log( status );
        }
    }


    /**
     * Gets the Modification Log filename for the corresponding connection.
     *
     * @param connection
     *      the connection
     * @return
     *      the Modification Log filename
     */
    public static final String getModificationLogFileName( Connection connection )
    {
        IPath p = ConnectionCorePlugin.getDefault().getStateLocation().append( LOGS_PATH );
        File file = p.toFile();
        if ( !file.exists() )
        {
            file.mkdir();
        }
        return p
            .append( MODIFICATIONS_LOG_PREFIX + Utils.getFilenameString( connection.getId() ) + LDIFLOG_SUFFIX ).toOSString();
    }


    /**
     * Gets the Search Log filename for the corresponding connection.
     *
     * @param connection
     *      the connection
     * @return
     *      the Search Log filename
     */
    public static final String getSearchLogFileName( Connection connection )
    {
        IPath p = ConnectionCorePlugin.getDefault().getStateLocation().append( LOGS_PATH ); //$NON-NLS-1$
        File file = p.toFile();
        if ( !file.exists() )
        {
            file.mkdir();
        }
        return p.append( SEARCH_LOGS_PREFIX + Utils.getFilenameString( connection.getId() ) + LDIFLOG_SUFFIX ).toOSString();
    }


    /**
     * Gets the filename of the Connection Store.
     *
     * @return
     *      the filename of the Connection Store
     */
    public static final String getConnectionStoreFileName()
    {
        String filename = ConnectionCorePlugin.getDefault().getStateLocation().append( CONNECTIONS_XML ).toOSString();
        return filename;
    }


    /**
     * Adds the connection to the end of the connection list. If there is
     * already a connection with this name, the new connection is renamed.
     *
     * @param connection the connection to add
     */
    public void addConnection( Connection connection )
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

        connectionList.add( connection );
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
        for ( Connection conn : connectionList )
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
        for ( Connection conn : connectionList )
        {
            if ( conn.getConnectionParameter().getName().equals( name ) )
            {
                return conn;
            }
        }
        return null;
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
        Set<ConnectionParameter> connectionParameters = new HashSet<ConnectionParameter>();
        for ( Connection connection : connectionList )
        {
            connectionParameters.add( connection.getConnectionParameter() );
        }

        // To avoid a corrupt file, save object to a temp file first 
        try
        {
            ConnectionIO.save( connectionParameters, new FileOutputStream( getConnectionStoreFileName() + TEMP_SUFFIX ) );
        }
        catch ( IOException e )
        {
            Status status = new Status( IStatus.ERROR, ConnectionCoreConstants.PLUGIN_ID,
                Messages.error__saving_connections + e.getMessage(), e );
            ConnectionCorePlugin.getDefault().getLog().log( status );
        }

        // move temp file to good file
        File file = new File( getConnectionStoreFileName() );
        File tempFile = new File( getConnectionStoreFileName() + TEMP_SUFFIX );
        if ( file.exists() )
        {
            file.delete();
        }

        try
        {
            String content = FileUtils.readFileToString( tempFile, ENCODING_UTF8 );
            FileUtils.writeStringToFile( file, content, ENCODING_UTF8 );
        }
        catch ( IOException e )
        {
            Status status = new Status( IStatus.ERROR, ConnectionCoreConstants.PLUGIN_ID,
                Messages.error__saving_connections + e.getMessage(), e );
            ConnectionCorePlugin.getDefault().getLog().log( status );
        }
    }


    /**
     * Loads the Connections
     */
    private synchronized void loadConnections()
    {
        Set<ConnectionParameter> connectionParameters = null;

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
                    .load( new FileInputStream( getConnectionStoreFileName() + TEMP_SUFFIX ) );
            }
            catch ( FileNotFoundException e1 )
            {
                // ignore, this is a fresh workspace
                return;
            }
            catch ( ConnectionIOException e1 )
            {
                Status status = new Status( IStatus.ERROR, ConnectionCoreConstants.PLUGIN_ID,
                    Messages.error__loading_connections + e.getMessage(), e );
                ConnectionCorePlugin.getDefault().getLog().log( status );
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
