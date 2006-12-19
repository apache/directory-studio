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


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.ldapstudio.browser.core.events.BookmarkUpdateEvent;
import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.model.IBookmark;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;


public class BookmarkManager implements Serializable
{

    private static final long serialVersionUID = 7605293576518974531L;

    private List bookmarkList;

    private IConnection connection;


    public BookmarkManager( IConnection connection )
    {
        this.connection = connection;
        this.bookmarkList = new ArrayList();
    }


    public IConnection getConnection()
    {
        return this.connection;
    }


    public void addBookmark( IBookmark bookmark )
    {
        this.addBookmark( this.bookmarkList.size(), bookmark );
    }


    public void addBookmark( int index, IBookmark bookmark )
    {
        if ( this.getBookmark( bookmark.getName() ) != null )
        {
            String newBookmarkName = BrowserCoreMessages.bind( BrowserCoreMessages.copy_n_of_s, "", bookmark.getName() ); //$NON-NLS-1$
            for ( int i = 2; this.getBookmark( newBookmarkName ) != null; i++ )
            {
                newBookmarkName = BrowserCoreMessages.bind( BrowserCoreMessages.copy_n_of_s,
                    i + " ", bookmark.getName() ); //$NON-NLS-1$
            }
            bookmark.setName( newBookmarkName );
        }

        this.bookmarkList.add( index, bookmark );
        EventRegistry.fireBookmarkUpdated( new BookmarkUpdateEvent( bookmark, BookmarkUpdateEvent.BOOKMARK_ADDED ),
            this );
    }


    public IBookmark getBookmark( String name )
    {
        for ( Iterator it = this.bookmarkList.iterator(); it.hasNext(); )
        {
            IBookmark bookmark = ( IBookmark ) it.next();
            if ( bookmark.getName().equals( name ) )
            {
                return bookmark;
            }
        }
        return null;
    }


    public int indexOf( IBookmark bookmark )
    {
        return this.bookmarkList.indexOf( bookmark );
    }


    public void removeBookmark( IBookmark bookmark )
    {
        this.bookmarkList.remove( bookmark );
        EventRegistry.fireBookmarkUpdated( new BookmarkUpdateEvent( bookmark, BookmarkUpdateEvent.BOOKMARK_REMOVED ),
            this );
    }


    public void removeBookmark( String name )
    {
        this.removeBookmark( this.getBookmark( name ) );
    }


    public IBookmark[] getBookmarks()
    {
        return ( IBookmark[] ) this.bookmarkList.toArray( new IBookmark[0] );
    }


    public int getBookmarkCount()
    {
        return this.bookmarkList.size();
    }

}
