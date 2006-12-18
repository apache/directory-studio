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


public class ConnectionManager implements ConnectionUpdateListener, SearchUpdateListener, BookmarkUpdateListener
{

    private List connectionList;


    public ConnectionManager()
    {
        this.connectionList = new ArrayList();
        this.loadConnections();
        EventRegistry.addConnectionUpdateListener( this );
        EventRegistry.addSearchUpdateListener( this );
        EventRegistry.addBookmarkUpdateListener( this );
    }


    public static final String getSchemaCacheFileName( String connectionName )
    {
        return BrowserCorePlugin.getDefault().getStateLocation().append(
            "schema-" + toSaveString( connectionName ) + ".ldif" ).toOSString(); //$NON-NLS-1$ //$NON-NLS-2$
    }


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


    public static final String getConnectionStoreFileName()
    {
        return BrowserCorePlugin.getDefault().getStateLocation().append( "connections.xml" ).toOSString(); //$NON-NLS-1$
    }


    private static String toSaveString( String s )
    {

        if ( s == null )
            return null;

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
        this.addConnection( this.connectionList.size(), connection );
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
        if ( this.getConnection( connection.getName() ) != null )
        {
            String newConnectionName = BrowserCoreMessages.bind( BrowserCoreMessages.copy_n_of_s,
                "", connection.getName() ); //$NON-NLS-1$
            for ( int i = 2; this.getConnection( newConnectionName ) != null; i++ )
            {
                newConnectionName = BrowserCoreMessages.bind( BrowserCoreMessages.copy_n_of_s,
                    i + " ", connection.getName() ); //$NON-NLS-1$
            }
            connection.getConnectionParameter().setName( newConnectionName );
        }

        this.connectionList.add( index, connection );
        EventRegistry.fireConnectionUpdated( new ConnectionUpdateEvent( connection,
            ConnectionUpdateEvent.CONNECTION_ADDED ), this );
    }


    public IConnection getConnection( String name )
    {
        for ( Iterator it = this.connectionList.iterator(); it.hasNext(); )
        {
            IConnection conn = ( IConnection ) it.next();
            if ( conn.getName().equals( name ) )
            {
                return conn;
            }
        }
        return null;
    }


    public int indexOf( IConnection connection )
    {
        return this.connectionList.indexOf( connection );
    }


    public void removeConnection( IConnection conn )
    {
        this.connectionList.remove( conn );
        EventRegistry.fireConnectionUpdated(
            new ConnectionUpdateEvent( conn, ConnectionUpdateEvent.CONNECTION_REMOVED ), this );
    }


    public IConnection[] getConnections()
    {
        return ( IConnection[] ) this.connectionList.toArray( new IConnection[0] );
    }


    public int getConnectionCount()
    {
        return this.connectionList.size();
    }


    public void connectionUpdated( ConnectionUpdateEvent connectionUpdateEvent )
    {
        if ( connectionUpdateEvent.getDetail() == ConnectionUpdateEvent.CONNECTION_ADDED
            || connectionUpdateEvent.getDetail() == ConnectionUpdateEvent.CONNECTION_REMOVED
            || connectionUpdateEvent.getDetail() == ConnectionUpdateEvent.CONNECTION_RENAMED
            || connectionUpdateEvent.getDetail() == ConnectionUpdateEvent.CONNECTION_PARAMETER_UPDATED )
        {
            this.saveConnections();
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
        if ( connectionUpdateEvent.getDetail() == ConnectionUpdateEvent.CONNECTION_SCHEMA_LOADED )
        {
            this.saveSchema( connectionUpdateEvent.getConnection() );
        }
        if ( connectionUpdateEvent.getDetail() == ConnectionUpdateEvent.CONNECTION_REMOVED )
        {
            File file = new File( getSchemaCacheFileName( connectionUpdateEvent.getConnection().getName() ) );
            if ( file.exists() )
            {
                file.delete();
            }
        }
    }


    public void searchUpdated( SearchUpdateEvent searchUpdateEvent )
    {
        if ( searchUpdateEvent.getDetail() == SearchUpdateEvent.SEARCH_ADDED
            || searchUpdateEvent.getDetail() == SearchUpdateEvent.SEARCH_REMOVED
            || searchUpdateEvent.getDetail() == SearchUpdateEvent.SEARCH_RENAMED
            || searchUpdateEvent.getDetail() == SearchUpdateEvent.SEARCH_PARAMETER_UPDATED )
        {
            this.saveConnections();
        }
    }


    public void bookmarkUpdated( BookmarkUpdateEvent bookmarkUpdateEvent )
    {
        if ( bookmarkUpdateEvent.getDetail() == BookmarkUpdateEvent.BOOKMARK_ADDED
            || bookmarkUpdateEvent.getDetail() == BookmarkUpdateEvent.BOOKMARK_REMOVED
            || bookmarkUpdateEvent.getDetail() == BookmarkUpdateEvent.BOOKMARK_UPDATED )
        {
            this.saveConnections();
        }
    }


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

            // object[i][0] = conn.getClass().getName();
            object[i][0] = connectionParameters;
            object[i][1] = searchParameters;
            object[i][2] = bookmarkParameters;
        }

        // long t1 = System.currentTimeMillis();
        this.save( object, getConnectionStoreFileName() );
        // long t2 = System.currentTimeMillis();
        // System.out.println("Saved connections in " + (t2-t1) + "ms");

    }


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
            // BrowserPlugin.getDefault().getExceptionHandler().handleException(e.getMessage(),
            // e);
        }
    }


    private void loadConnections()
    {
        try
        {
            // long t1 = System.currentTimeMillis();
            Object[][] object = ( Object[][] ) this.load( getConnectionStoreFileName() );
            // long t2 = System.currentTimeMillis();
            // System.out.println("Loaded connections in " + (t2-t1) +
            // "ms");

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

                        this.connectionList.add( conn );
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
