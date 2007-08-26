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


import java.beans.Encoder;
import java.beans.ExceptionListener;
import java.beans.Expression;
import java.beans.PersistenceDelegate;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.directory.studio.connection.core.ConnectionParameter.AuthenticationMethod;
import org.apache.directory.studio.connection.core.ConnectionParameter.EncryptionMethod;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;
import org.apache.directory.studio.connection.core.event.ConnectionUpdateListener;


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
        //        File file = new File( filename );
        //        if ( !file.exists() )
        //        {
        //            // try to convert old connections.xml:
        //            // 1st search it in current workspace with the old ldapstudio plugin ID
        //            // 2nd search it in old .ldapstudio workspace with the old ldapstudio plugin ID
        //            String[] oldFilenames = new String[2];
        //            oldFilenames[0] = filename.replace( "org.apache.directory.studio.ldapbrowser.core",
        //                "org.apache.directory.ldapstudio.browser.core" );
        //            oldFilenames[1] = oldFilenames[0].replace( ".ApacheDirectoryStudio",
        //                ".ldapstudio" );
        //            for ( int i = 0; i < oldFilenames.length; i++ )
        //            {
        //                File oldFile = new File( oldFilenames[i] );
        //                if ( oldFile.exists() )
        //                {
        //                    try
        //                    {
        //                        String oldContent = FileUtils.readFileToString( oldFile, "UTF-8" );
        //                        String newContent = oldContent.replace( "org.apache.directory.ldapstudio.browser.core",
        //                            "org.apache.directory.studio.ldapbrowser.core" );
        //                        FileUtils.writeStringToFile( file, newContent, "UTF-8" );
        //                        break;
        //                    }
        //                    catch ( IOException e )
        //                    {
        //                        e.printStackTrace();
        //                    }
        //                }
        //            }
        //        }

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
        if ( getConnection( connection.getConnectionParameter().getName() ) != null )
        {
            String newConnectionName = Messages.bind( Messages.copy_n_of_s,
                "", connection.getConnectionParameter().getName() ); //$NON-NLS-1$
            for ( int i = 2; getConnection( newConnectionName ) != null; i++ )
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
     * Gets a connection from its name.
     *
     * @param name
     *      the name of the Connection
     * @return
     *      the corresponding Connection
     */
    public Connection getConnection( String name )
    {
        for ( Iterator it = connectionList.iterator(); it.hasNext(); )
        {
            Connection conn = ( Connection ) it.next();
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
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionRenamed(org.apache.directory.studio.connection.core.Connection, java.lang.String)
     */
    public void connectionRenamed( Connection connection, String oldName )
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
     * Saves the Connections
     */
    private void saveConnections()
    {
        Object[] object = new Object[connectionList.size()];

        Iterator connectionIterator = connectionList.iterator();
        for ( int i = 0; connectionIterator.hasNext(); i++ )
        {
            Connection conn = ( Connection ) connectionIterator.next();
            ConnectionParameter connectionParameters = conn.getConnectionParameter();

            object[i] = connectionParameters;
        }

        save( object, getConnectionStoreFileName() );
    }


    /**
     * Loads the Connections
     */
    private void loadConnections()
    {
        try
        {
            Object[] object = ( Object[] ) this.load( getConnectionStoreFileName() );

            if ( object != null )
            {
                try
                {
                    for ( int i = 0; i < object.length; i++ )
                    {
                        ConnectionParameter connectionParameters = ( ConnectionParameter ) object[i];
                        Connection conn = new Connection( connectionParameters );
                        connectionList.add( conn );
                    }

                }
                catch ( ArrayIndexOutOfBoundsException e )
                {
                    // Thrown by decoder.readObject(), signals EOF
                }
                catch ( Exception e )
                {
                    e.printStackTrace();
                }
            }
        }
        catch ( Exception e )
        {
        }
    }


    /**
     * Loads an Object from an XML file
     *
     * @param filename
     *      the filename of the XML file
     * @return
     *      the deserialized Object
     */
    private synchronized Object load( String filename )
    {
        try
        {
            Thread.currentThread().setContextClassLoader( getClass().getClassLoader() );
            XMLDecoder decoder = new XMLDecoder( new BufferedInputStream( ( new FileInputStream( filename ) ) ) );
            Object object = decoder.readObject();
            decoder.close();
            return object;
        }
        catch ( IOException ioe )
        {
            return null;
        }
        catch ( Exception e )
        {
            // if loading failed, try with temp file
            String tempFilename = filename + "-temp";
            try
            {
                XMLDecoder decoder = new XMLDecoder( new BufferedInputStream( ( new FileInputStream( tempFilename ) ) ) );
                Object object = decoder.readObject();
                decoder.close();
                return object;
            }
            catch ( IOException ioe2 )
            {
                return null;
            }
            catch ( Exception e2 )
            {
                return null;
            }
        }
    }


    /**
     * Saves an Object into a serialized XML file
     *
     * @param object
     *      the object to save
     * @param filename
     *      the filename to save to
     */
    private synchronized void save( Object object, String filename )
    {
        XMLEncoder encoder = null;
        try
        {
            // to avoid a corrupt file, save object to a temp file first 
            String tempFilename = filename + "-temp";
            Thread.currentThread().setContextClassLoader( getClass().getClassLoader() );
            encoder = new XMLEncoder( new BufferedOutputStream( new FileOutputStream( tempFilename ) ) );

            encoder.setPersistenceDelegate( AuthenticationMethod.class, enumPersistenceDelegate );
            encoder.setPersistenceDelegate( EncryptionMethod.class, enumPersistenceDelegate );

            encoder.setExceptionListener( new ExceptionListener()
            {
                public void exceptionThrown( Exception e )
                {
                    e.printStackTrace();
                }
            } );
            encoder.writeObject( object );
            encoder.close();

            // move temp file to good file
            File file = new File( filename );
            File tempFile = new File( tempFilename );
            if ( file.exists() )
            {
                file.delete();
            }
            String content = FileUtils.readFileToString( tempFile, "UTF-8" );
            FileUtils.writeStringToFile( file, content, "UTF-8" );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        finally
        {
            if ( encoder != null )
            {
                encoder.close();
            }
        }
    }

    private static final PersistenceDelegate enumPersistenceDelegate = new PersistenceDelegate()
    {
        protected boolean mutatesTo( Object oldInstance, Object newInstance )
        {
            return oldInstance == newInstance;
        }


        protected Expression instantiate( Object oldInstance, Encoder out )
        {
            Enum e = ( Enum ) oldInstance;
            return new Expression( e, e.getClass(), "valueOf", new Object[]
                { e.name() } );
        }
    };
}
