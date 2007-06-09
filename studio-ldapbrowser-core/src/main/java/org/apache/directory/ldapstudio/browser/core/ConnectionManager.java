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

package org.apache.directory.ldapstudio.browser.core;


import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.ldapstudio.browser.core.events.BookmarkUpdateEvent;
import org.apache.directory.ldapstudio.browser.core.events.BookmarkUpdateListener;
import org.apache.directory.ldapstudio.browser.core.events.ConnectionRenamedEvent;
import org.apache.directory.ldapstudio.browser.core.events.ConnectionUpdateEvent;
import org.apache.directory.ldapstudio.browser.core.events.ConnectionUpdateListener;
import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.events.SearchUpdateEvent;
import org.apache.directory.ldapstudio.browser.core.events.SearchUpdateListener;
import org.apache.directory.ldapstudio.browser.core.internal.model.Bookmark;
import org.apache.directory.ldapstudio.browser.core.internal.model.Connection;
import org.apache.directory.ldapstudio.browser.core.internal.model.Search;
import org.apache.directory.ldapstudio.browser.core.model.BookmarkParameter;
import org.apache.directory.ldapstudio.browser.core.model.ConnectionParameter;
import org.apache.directory.ldapstudio.browser.core.model.IBookmark;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.ISearch;
import org.apache.directory.ldapstudio.browser.core.model.SearchParameter;
import org.apache.directory.ldapstudio.browser.core.model.schema.Schema;
import org.apache.directory.ldapstudio.browser.core.utils.LdifUtils;
import org.eclipse.core.runtime.IPath;


/**
 * This class is used to manage {@link IConnection}s.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ConnectionManager implements ConnectionUpdateListener, SearchUpdateListener, BookmarkUpdateListener
{

    /** The list of connections. */
    private List<IConnection> connectionList;


    /**
     * Creates a new instance of ConnectionManager.
     */
    public ConnectionManager()
    {
        this.connectionList = new ArrayList<IConnection>();
        loadConnections();
        EventRegistry.addConnectionUpdateListener( this, BrowserCorePlugin.getDefault().getEventRunner() );
        EventRegistry.addSearchUpdateListener( this, BrowserCorePlugin.getDefault().getEventRunner() );
        EventRegistry.addBookmarkUpdateListener( this, BrowserCorePlugin.getDefault().getEventRunner() );
    }


    /**
     * Gets the Schema Cache filename for the corresponding Connection name.
     *
     * @param connectionName
     *      the connection name
     * @return
     *      the Schema Cache filename for the corresponding Connection name
     */
    public static final String getSchemaCacheFileName( String connectionName )
    {
        return BrowserCorePlugin.getDefault().getStateLocation().append(
            "schema-" + toSaveString( connectionName ) + ".ldif" ).toOSString(); //$NON-NLS-1$ //$NON-NLS-2$
    }


    /**
     * Gets the Modification Log filename for the corresponding Connection name.
     *
     * @param connectionName
     *      the connection name
     * @return
     *      the Modification Log filename
     */
    public static final String getModificationLogFileName( String connectionName )
    {
        IPath p = BrowserCorePlugin.getDefault().getStateLocation().append( "logs" ); //$NON-NLS-1$
        File file = p.toFile();
        if ( !file.exists() )
        {
            file.mkdir();
        }
        return p.append( "modifications-" + toSaveString( connectionName ) + "-%u-%g.ldiflog" ).toOSString(); //$NON-NLS-1$ //$NON-NLS-2$
    }


    /**
     * Gets the filename of the Connection Store.
     *
     * @return
     *      the filename of the Connection Store
     */
    public static final String getConnectionStoreFileName()
    {
        return BrowserCorePlugin.getDefault().getStateLocation().append( "connections.xml" ).toOSString(); //$NON-NLS-1$
    }


    /**
     * Converts a String into a Saveable String.
     *
     * @param s
     *      the String to convert
     * @return
     *      the converted String
     */
    private static String toSaveString( String s )
    {
        if ( s == null )
        {
            return null;
        }

        byte[] b = LdifUtils.utf8encode( s );
        StringBuffer sb = new StringBuffer();
        for ( int i = 0; i < b.length; i++ )
        {

            if ( b[i] == '-' || b[i] == '_' || ( '0' <= b[i] && b[i] <= '9' ) || ( 'A' <= b[i] && b[i] <= 'Z' )
                || ( 'a' <= b[i] && b[i] <= 'z' ) )
            {
                sb.append( ( char ) b[i] );
            }
            else
            {
                int x = ( int ) b[i];
                if ( x < 0 )
                    x = 256 + x;
                String t = Integer.toHexString( x );
                if ( t.length() == 1 )
                    t = "0" + t; //$NON-NLS-1$
                sb.append( t );
            }
        }

        return sb.toString();
    }


    /**
     * Adds the connection to the end of the connection list. If there is
     * already a connection with this name, the new connection is renamed.
     *
     * @param connection
     */
    public void addConnection( IConnection connection )
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
    public void addConnection( int index, IConnection connection )
    {
        if ( getConnection( connection.getName() ) != null )
        {
            String newConnectionName = BrowserCoreMessages.bind( BrowserCoreMessages.copy_n_of_s,
                "", connection.getName() ); //$NON-NLS-1$
            for ( int i = 2; getConnection( newConnectionName ) != null; i++ )
            {
                newConnectionName = BrowserCoreMessages.bind( BrowserCoreMessages.copy_n_of_s,
                    i + " ", connection.getName() ); //$NON-NLS-1$
            }
            connection.getConnectionParameter().setName( newConnectionName );
        }

        connectionList.add( index, connection );
        EventRegistry.fireConnectionUpdated( new ConnectionUpdateEvent( connection,
            ConnectionUpdateEvent.EventDetail.CONNECTION_ADDED ), this );
    }


    /**
     * Gets a connection from its name.
     *
     * @param name
     *      the name of the Connection
     * @return
     *      the corresponding Connection
     */
    public IConnection getConnection( String name )
    {
        for ( Iterator it = connectionList.iterator(); it.hasNext(); )
        {
            IConnection conn = ( IConnection ) it.next();
            if ( conn.getName().equals( name ) )
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
    public int indexOf( IConnection connection )
    {
        return connectionList.indexOf( connection );
    }


    /**
     * Removes the given Connection from the Connection list.
     *
     * @param conn
     *      the connection to remove
     */
    public void removeConnection( IConnection conn )
    {
        connectionList.remove( conn );
        EventRegistry.fireConnectionUpdated(
            new ConnectionUpdateEvent( conn, ConnectionUpdateEvent.EventDetail.CONNECTION_REMOVED ), this );
    }


    /**
     * Gets an array containing all the Connections.
     *
     * @return
     *      an array containing all the Connections
     */
    public IConnection[] getConnections()
    {
        return ( IConnection[] ) connectionList.toArray( new IConnection[0] );
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
     * {@inheritDoc}
     */
    public void connectionUpdated( ConnectionUpdateEvent connectionUpdateEvent )
    {
        if ( connectionUpdateEvent.getDetail() == ConnectionUpdateEvent.EventDetail.CONNECTION_ADDED
            || connectionUpdateEvent.getDetail() == ConnectionUpdateEvent.EventDetail.CONNECTION_REMOVED
            || connectionUpdateEvent.getDetail() == ConnectionUpdateEvent.EventDetail.CONNECTION_RENAMED
            || connectionUpdateEvent.getDetail() == ConnectionUpdateEvent.EventDetail.CONNECTION_PARAMETER_UPDATED )
        {
            saveConnections();
        }

        if ( connectionUpdateEvent instanceof ConnectionRenamedEvent )
        {
            String oldName = ( ( ConnectionRenamedEvent ) connectionUpdateEvent ).getOldName();
            String newName = connectionUpdateEvent.getConnection().getName();
            String oldSchemaFile = getSchemaCacheFileName( oldName );
            String newSchemaFile = getSchemaCacheFileName( newName );
            File oldFile = new File( oldSchemaFile );
            File newFile = new File( newSchemaFile );
            if ( oldFile.exists() )
            {
                oldFile.renameTo( newFile );
            }
        }
        if ( connectionUpdateEvent.getDetail() == ConnectionUpdateEvent.EventDetail.SCHEMA_LOADED
            || connectionUpdateEvent.getDetail() == ConnectionUpdateEvent.EventDetail.CONNECTION_OPENED )
        {
            saveSchema( connectionUpdateEvent.getConnection() );
        }
        if ( connectionUpdateEvent.getDetail() == ConnectionUpdateEvent.EventDetail.CONNECTION_REMOVED )
        {
            File file = new File( getSchemaCacheFileName( connectionUpdateEvent.getConnection().getName() ) );
            if ( file.exists() )
            {
                file.delete();
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void searchUpdated( SearchUpdateEvent searchUpdateEvent )
    {
        if ( searchUpdateEvent.getDetail() == SearchUpdateEvent.EventDetail.SEARCH_ADDED
            || searchUpdateEvent.getDetail() == SearchUpdateEvent.EventDetail.SEARCH_REMOVED
            || searchUpdateEvent.getDetail() == SearchUpdateEvent.EventDetail.SEARCH_RENAMED
            || searchUpdateEvent.getDetail() == SearchUpdateEvent.EventDetail.SEARCH_PARAMETER_UPDATED )
        {
            saveConnections();
        }
    }


    /**
     * {@inheritDoc}
     */
    public void bookmarkUpdated( BookmarkUpdateEvent bookmarkUpdateEvent )
    {
        if ( bookmarkUpdateEvent.getDetail() == BookmarkUpdateEvent.Detail.BOOKMARK_ADDED
            || bookmarkUpdateEvent.getDetail() == BookmarkUpdateEvent.Detail.BOOKMARK_REMOVED
            || bookmarkUpdateEvent.getDetail() == BookmarkUpdateEvent.Detail.BOOKMARK_UPDATED )
        {
            saveConnections();
        }
    }


    /**
     * Saves the Connections
     */
    private void saveConnections()
    {
        Object[][] object = new Object[connectionList.size()][3];

        Iterator connectionIterator = connectionList.iterator();
        for ( int i = 0; connectionIterator.hasNext(); i++ )
        {
            IConnection conn = ( IConnection ) connectionIterator.next();
            ConnectionParameter connectionParameters = conn.getConnectionParameter();
            ISearch[] searches = conn.getSearchManager().getSearches();
            SearchParameter[] searchParameters = new SearchParameter[searches.length];
            for ( int k = 0; k < searches.length; k++ )
            {
                searchParameters[k] = searches[k].getSearchParameter();
            }
            IBookmark[] bookmarks = conn.getBookmarkManager().getBookmarks();
            BookmarkParameter[] bookmarkParameters = new BookmarkParameter[bookmarks.length];
            for ( int k = 0; k < bookmarks.length; k++ )
            {
                bookmarkParameters[k] = bookmarks[k].getBookmarkParameter();
            }

            object[i][0] = connectionParameters;
            object[i][1] = searchParameters;
            object[i][2] = bookmarkParameters;
        }

        save( object, getConnectionStoreFileName() );
    }


    /**
     * Saves the Schema of the Connection
     *
     * @param connection
     *      the Connection
     */
    private void saveSchema( IConnection connection )
    {
        try
        {
            String filename = getSchemaCacheFileName( connection.getName() );
            FileWriter writer = new FileWriter( filename );
            connection.getSchema().saveToLdif( writer );
            writer.close();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }


    /**
     * Loads the Connections
     */
    private void loadConnections()
    {
        try
        {
            Object[][] object = ( Object[][] ) this.load( getConnectionStoreFileName() );

            if ( object != null )
            {
                try
                {
                    for ( int i = 0; i < object.length; i++ )
                    {
                        IConnection conn = new Connection();

                        ConnectionParameter connectionParameters = ( ConnectionParameter ) object[i][0];
                        conn.setConnectionParameter( connectionParameters );

                        if ( object[i].length > 1 )
                        {
                            SearchParameter[] searchParameters = ( SearchParameter[] ) object[i][1];
                            for ( int k = 0; k < searchParameters.length; k++ )
                            {
                                ISearch search = new Search( conn, searchParameters[k] );
                                conn.getSearchManager().addSearch( search );
                            }
                        }

                        if ( object[i].length > 2 )
                        {
                            BookmarkParameter[] bookmarkParameters = ( BookmarkParameter[] ) object[i][2];
                            for ( int k = 0; k < bookmarkParameters.length; k++ )
                            {
                                IBookmark bookmark = new Bookmark( conn, bookmarkParameters[k] );
                                conn.getBookmarkManager().addBookmark( bookmark );
                            }
                        }

                        try
                        {
                            String schemaFilename = getSchemaCacheFileName( conn.getName() );
                            FileReader reader = new FileReader( schemaFilename );
                            Schema schema = new Schema();
                            schema.loadFromLdif( reader );
                            conn.setSchema( schema );
                        }
                        catch ( Exception e )
                        {
                        }

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
            e.printStackTrace();
            return null;
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
            Thread.currentThread().setContextClassLoader( getClass().getClassLoader() );
            encoder = new XMLEncoder( new BufferedOutputStream( new FileOutputStream( filename ) ) );

            encoder.setExceptionListener( new ExceptionListener()
            {

                public void exceptionThrown( Exception e )
                {
                    e.printStackTrace();
                }

            } );

            encoder.writeObject( object );
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
}
