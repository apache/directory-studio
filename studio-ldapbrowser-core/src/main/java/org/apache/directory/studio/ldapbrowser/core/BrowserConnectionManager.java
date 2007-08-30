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

package org.apache.directory.studio.ldapbrowser.core;


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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;
import org.apache.directory.studio.connection.core.event.ConnectionUpdateListener;
import org.apache.directory.studio.ldapbrowser.core.events.BookmarkUpdateEvent;
import org.apache.directory.studio.ldapbrowser.core.events.BookmarkUpdateListener;
import org.apache.directory.studio.ldapbrowser.core.events.BrowserConnectionUpdateEvent;
import org.apache.directory.studio.ldapbrowser.core.events.BrowserConnectionUpdateListener;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.events.SearchUpdateEvent;
import org.apache.directory.studio.ldapbrowser.core.events.SearchUpdateListener;
import org.apache.directory.studio.ldapbrowser.core.internal.model.Bookmark;
import org.apache.directory.studio.ldapbrowser.core.internal.model.BrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.internal.model.Search;
import org.apache.directory.studio.ldapbrowser.core.model.BookmarkParameter;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.SearchParameter;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldapbrowser.core.utils.LdifUtils;
import org.eclipse.core.runtime.IPath;


/**
 * This class is used to manage {@link IBrowserConnection}s.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BrowserConnectionManager implements ConnectionUpdateListener, BrowserConnectionUpdateListener, SearchUpdateListener, BookmarkUpdateListener
{

    /** The list of connections. */
    private Map<String, IBrowserConnection> connectionMap;


    /**
     * Creates a new instance of ConnectionManager.
     */
    public BrowserConnectionManager()
    {
        this.connectionMap = new HashMap<String, IBrowserConnection>();
        loadBrowserConnections();
        ConnectionEventRegistry.addConnectionUpdateListener( this, ConnectionCorePlugin.getDefault().getEventRunner() );
        EventRegistry.addSearchUpdateListener( this, BrowserCorePlugin.getDefault().getEventRunner() );
        EventRegistry.addBookmarkUpdateListener( this, BrowserCorePlugin.getDefault().getEventRunner() );
        EventRegistry.addBrowserConnectionUpdateListener( this, BrowserCorePlugin.getDefault().getEventRunner() );
    }


    /**
     * Gets the Schema Cache filename for the corresponding browser connection.
     *
     * @param browserConnection
     *      the browser connection
     * @return
     *      the Schema Cache filename for the corresponding browser connection
     */
    public static final String getSchemaCacheFileName( IBrowserConnection browserConnection )
    {
        return BrowserCorePlugin.getDefault().getStateLocation().append(
            "schema-" + toSaveString( browserConnection.getConnection().getId() ) + ".ldif" ).toOSString(); //$NON-NLS-1$ //$NON-NLS-2$
    }


    /**
     * Gets the Modification Log filename for the corresponding browser connection.
     *
     * @param browserConnection
     *      the browser connection
     * @return
     *      the Modification Log filename
     */
    public static final String getModificationLogFileName( IBrowserConnection browserConnection )
    {
        IPath p = BrowserCorePlugin.getDefault().getStateLocation().append( "logs" ); //$NON-NLS-1$
        File file = p.toFile();
        if ( !file.exists() )
        {
            file.mkdir();
        }
        return p
            .append( "modifications-" + toSaveString( browserConnection.getConnection().getId() ) + "-%u-%g.ldiflog" ).toOSString(); //$NON-NLS-1$ //$NON-NLS-2$
    }


    /**
     * Gets the filename of the Connection Store.
     *
     * @return
     *      the filename of the Connection Store
     */
    public static final String getBrowserConnectionStoreFileName()
    {
        String filename = BrowserCorePlugin.getDefault().getStateLocation().append( "browserconnections.xml" ).toOSString(); //$NON-NLS-1$
        File file = new File( filename );
        if ( !file.exists() )
        {
            // try to convert old connections.xml:
            // 1st search it in current workspace with the old ldapstudio plugin ID
            // 2nd search it in old .ldapstudio workspace with the old ldapstudio plugin ID
            String[] oldFilenames = new String[2];
            oldFilenames[0] = filename.replace( "org.apache.directory.studio.ldapbrowser.core",
                "org.apache.directory.ldapstudio.browser.core" );
            oldFilenames[1] = oldFilenames[0].replace( ".ApacheDirectoryStudio", ".ldapstudio" );
            for ( int i = 0; i < oldFilenames.length; i++ )
            {
                File oldFile = new File( oldFilenames[i] );
                if ( oldFile.exists() )
                {
                    try
                    {
                        String oldContent = FileUtils.readFileToString( oldFile, "UTF-8" );
                        String newContent = oldContent.replace( "org.apache.directory.ldapstudio.browser.core",
                            "org.apache.directory.studio.ldapbrowser.core" );
                        FileUtils.writeStringToFile( file, newContent, "UTF-8" );
                        break;
                    }
                    catch ( IOException e )
                    {
                        e.printStackTrace();
                    }
                }
            }
        }

        return filename;
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
     * Gets a browser connection from its id.
     *
     * @param id
     *      the id of the Connection
     * @return
     *      the corresponding IBrowserConnection
     */
    public IBrowserConnection getBrowserConnectionById( String id )
    {
        return connectionMap.get( id );
    }


    /**
     * Gets a browser connection from its name.
     *
     * @param name
     *      the name of the Connection
     * @return
     *      the corresponding IBrowserConnection
     */
    public IBrowserConnection getBrowserConnectionByName( String name )
    {
        Connection connection = ConnectionCorePlugin.getDefault().getConnectionManager().getConnectionByName( name );
        return getBrowserConnection( connection );
    }
    
    
    /**
     * Gets a browser connection from its underlying connection.
     *
     * @param connection
     *      the underlying connection
     * @return
     *      the corresponding IBrowserConnection
     */
    public IBrowserConnection getBrowserConnection( Connection connection )
    {
        return connection != null ? getBrowserConnectionById( connection.getId() ) : null;
    }


    /**
     * Gets an array containing all the Connections.
     *
     * @return
     *      an array containing all the Connections
     */
    public IBrowserConnection[] getBrowserConnections()
    {
        return ( IBrowserConnection[] ) connectionMap.values().toArray( new IBrowserConnection[0] );
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionRemoved(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionRemoved( Connection connection )
    {
        // update connection list
        IBrowserConnection browserConnection = connectionMap.remove( connection.getId() );

        // remove schema file
        File schemaFile = new File( getSchemaCacheFileName( browserConnection ) );
        if ( schemaFile.exists() )
        {
            schemaFile.delete();
        }

        // make persistent
        saveBrowserConnections();
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionAdded(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionAdded( Connection connection )
    {
        // update connection list
        BrowserConnection browserConnection = new BrowserConnection( connection );
        connectionMap.put( connection.getId(), browserConnection );

        // make persistent
        saveBrowserConnections();
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#connectionUpdated(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionUpdated( Connection connection )
    {
        saveBrowserConnections();
        saveSchema( getBrowserConnection( connection ) );
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
     * @see org.apache.directory.studio.ldapbrowser.core.events.BrowserConnectionUpdateListener#browserConnectionUpdated(org.apache.directory.studio.ldapbrowser.core.events.BrowserConnectionUpdateEvent)
     */
    public void browserConnectionUpdated( BrowserConnectionUpdateEvent browserConnectionUpdateEvent )
    {
        if ( browserConnectionUpdateEvent.getDetail() == BrowserConnectionUpdateEvent.Detail.BROWSER_CONNECTION_OPENED
            || browserConnectionUpdateEvent.getDetail() == BrowserConnectionUpdateEvent.Detail.SCHEMA_UPDATED )
        {
            saveSchema( browserConnectionUpdateEvent.getBrowserConnection() );
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
            saveBrowserConnections();
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
            saveBrowserConnections();
        }
    }


    /**
     * Saves the browser Connections
     */
    private void saveBrowserConnections()
    {
        Object[][] object = new Object[connectionMap.size()][3];

        Iterator<IBrowserConnection> connectionIterator = connectionMap.values().iterator();
        for ( int i = 0; connectionIterator.hasNext(); i++ )
        {
            IBrowserConnection browserConnection = connectionIterator.next();
            
            ISearch[] searches = browserConnection.getSearchManager().getSearches();
            SearchParameter[] searchParameters = new SearchParameter[searches.length];
            for ( int k = 0; k < searches.length; k++ )
            {
                searchParameters[k] = searches[k].getSearchParameter();
            }
            
            IBookmark[] bookmarks = browserConnection.getBookmarkManager().getBookmarks();
            BookmarkParameter[] bookmarkParameters = new BookmarkParameter[bookmarks.length];
            for ( int k = 0; k < bookmarks.length; k++ )
            {
                bookmarkParameters[k] = bookmarks[k].getBookmarkParameter();
            }

            object[i][0] = browserConnection.getConnection().getId();
            object[i][1] = searchParameters;
            object[i][2] = bookmarkParameters;
        }

        save( object, getBrowserConnectionStoreFileName() );
    }


    /**
     * Saves the Schema of the Connection
     *
     * @param connection
     *      the Connection
     */
    private void saveSchema( IBrowserConnection connection )
    {
        try
        {
            String filename = getSchemaCacheFileName( connection );
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
    private void loadBrowserConnections()
    {
        Connection[] connections = ConnectionCorePlugin.getDefault().getConnectionManager().getConnections();
        for ( int i = 0; i < connections.length; i++ )
        {
            Connection connection = connections[i];
            BrowserConnection browserConnection = new BrowserConnection( connection );
            connectionMap.put( connection.getId(), browserConnection );
            
            try
            {
                String schemaFilename = getSchemaCacheFileName( browserConnection );
                FileReader reader = new FileReader( schemaFilename );
                Schema schema = new Schema();
                schema.loadFromLdif( reader );
                browserConnection.setSchema( schema );
            }
            catch ( Exception e )
            {
            }
        }
        
        try
        {
            Object[][] object = ( Object[][] ) this.load( getBrowserConnectionStoreFileName() );

            if ( object != null )
            {
                try
                {
                    for ( int i = 0; i < object.length; i++ )
                    {
                        String connectionId = ( String ) object[i][0];
                        IBrowserConnection browserConnection = getBrowserConnectionById( connectionId );
                        
                        if( browserConnection != null )
                        {
                            if ( object[i].length > 0 )
                            {
                                SearchParameter[] searchParameters = ( SearchParameter[] ) object[i][1];
                                for ( int k = 0; k < searchParameters.length; k++ )
                                {
                                    ISearch search = new Search( browserConnection, searchParameters[k] );
                                    browserConnection.getSearchManager().addSearch( search );
                                }
                            }
    
                            if ( object[i].length > 1 )
                            {
                                BookmarkParameter[] bookmarkParameters = ( BookmarkParameter[] ) object[i][2];
                                for ( int k = 0; k < bookmarkParameters.length; k++ )
                                {
                                    IBookmark bookmark = new Bookmark( browserConnection, bookmarkParameters[k] );
                                    browserConnection.getBookmarkManager().addBookmark( bookmark );
                                }
                            }
    
//                            try
//                            {
//                                String schemaFilename = getSchemaCacheFileName( browserConnection.getName() );
//                                FileReader reader = new FileReader( schemaFilename );
//                                Schema schema = new Schema();
//                                schema.loadFromLdif( reader );
//                                browserConnection.setSchema( schema );
//                            }
//                            catch ( Exception e )
//                            {
//                            }
    
                        }
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
}
