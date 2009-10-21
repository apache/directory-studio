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


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.ldapbrowser.core.events.BookmarkUpdateEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.osgi.util.NLS;


/**
 * This class is used to manage Bookmarks.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BookmarkManager implements Serializable
{
    private static final long serialVersionUID = 7605293576518974531L;

    private List<IBookmark> bookmarkList;

    private IBrowserConnection connection;


    /**
     * Creates a new instance of BookmarkManager.
     *
     * @param connection
     *      the attached Connection
     */
    public BookmarkManager( IBrowserConnection connection )
    {
        this.connection = connection;
        bookmarkList = new ArrayList<IBookmark>();
    }


    /**
     * Gets the Connection
     *
     * @return
     *      the Connection
     */
    public IBrowserConnection getConnection()
    {
        return connection;
    }


    /**
     * Adds a Bookmark
     *
     * @param bookmark
     *      the Bookmark to add
     */
    public void addBookmark( IBookmark bookmark )
    {
        addBookmark( bookmarkList.size(), bookmark );
    }


    /**
     * Adds a Bookmark at a specified position.
     *
     * @param index
     *      the index at which the specified element is to be inserted.
     * @param bookmark
     *      the Bookmark to add
     */
    public void addBookmark( int index, IBookmark bookmark )
    {
        if ( getBookmark( bookmark.getName() ) != null )
        {
            String newBookmarkName = NLS.bind( BrowserCoreMessages.copy_n_of_s, "", bookmark.getName() ); //$NON-NLS-1$

            for ( int i = 2; this.getBookmark( newBookmarkName ) != null; i++ )
            {
                newBookmarkName = NLS.bind( BrowserCoreMessages.copy_n_of_s, i + " ", bookmark.getName() ); //$NON-NLS-1$
            }

            bookmark.setName( newBookmarkName );
        }

        bookmarkList.add( index, bookmark );
        EventRegistry.fireBookmarkUpdated(
            new BookmarkUpdateEvent( bookmark, BookmarkUpdateEvent.Detail.BOOKMARK_ADDED ), this );
    }


    /**
     * Gets a Bookmark
     *
     * @param name
     *      the name of the Bookmark
     * @return
     *      the corresponding Bookmark
     */
    public IBookmark getBookmark( String name )
    {
        for ( IBookmark bookmark : bookmarkList )
        {
            if ( bookmark.getName().equals( name ) )
            {
                return bookmark;
            }
        }

        return null;
    }


    /**
     * Returns the index in the Bookmarks list of the first occurrence of the specified Bookmark
     *
     * @param bookmark
     *      the bookmark to search for
     * @return
     *      the index in the Bookmarks list of the first occurrence of the specified Bookmark
     */
    public int indexOf( IBookmark bookmark )
    {
        return bookmarkList.indexOf( bookmark );
    }


    /**
     * Removes a Bookmark
     *
     * @param bookmark
     *      the Bookmark to remove
     */
    public void removeBookmark( IBookmark bookmark )
    {
        bookmarkList.remove( bookmark );
        EventRegistry.fireBookmarkUpdated( new BookmarkUpdateEvent( bookmark,
            BookmarkUpdateEvent.Detail.BOOKMARK_REMOVED ), this );
    }


    /**
     * Removes a Bookmark
     *
     * @param name
     *      the name of the Bookmark to remove
     */
    public void removeBookmark( String name )
    {
        this.removeBookmark( this.getBookmark( name ) );
    }


    /**
     * Gets an array containing all Bookmarks
     *
     * @return
     *      an array containing all Bookmarks
     */
    public IBookmark[] getBookmarks()
    {
        return bookmarkList.toArray( new IBookmark[0] );
    }


    /**
     * Gets the number of Bookmarks
     *
     * @return
     *      the number of Bookmarjs
     */
    public int getBookmarkCount()
    {
        return bookmarkList.size();
    }
}
