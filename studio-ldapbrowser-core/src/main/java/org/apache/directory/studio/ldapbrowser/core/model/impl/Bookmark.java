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

package org.apache.directory.studio.ldapbrowser.core.model.impl;


import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.ldapbrowser.core.events.BookmarkUpdateEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.internal.search.LdapSearchPageScoreComputer;
import org.apache.directory.studio.ldapbrowser.core.model.BookmarkParameter;
import org.apache.directory.studio.ldapbrowser.core.model.DN;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.eclipse.search.ui.ISearchPageScoreComputer;


/**
 * Default implementation if IBookmark.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class Bookmark implements IBookmark
{

    /** The serialVersionUID. */
    private static final long serialVersionUID = 2914726541167255499L;

    /** The connection. */
    private IBrowserConnection connection;

    /** The bookmark parameter. */
    private BookmarkParameter bookmarkParameter;

    /** The bookmark entry. */
    private DelegateEntry bookmarkEntry;


    /**
     * Creates a new instance of Bookmark.
     */
    protected Bookmark()
    {
    }


    /**
     * Creates a new instance of Bookmark.
     *
     * @param connection the connection
     * @param bookmarkParameter the bookmark parameter
     */
    public Bookmark( IBrowserConnection connection, BookmarkParameter bookmarkParameter )
    {
        this.connection = connection;
        this.bookmarkParameter = bookmarkParameter;
        this.bookmarkEntry = new DelegateEntry( connection, bookmarkParameter.getDn() );
    }


    /**
     * Creates a new instance of Bookmark.
     *
     * @param connection the connection
     * @param dn the target DN
     * @param name the symbolic name
     */
    public Bookmark( IBrowserConnection connection, DN dn, String name )
    {
        this.connection = connection;
        this.bookmarkParameter = new BookmarkParameter( dn, name );
        this.bookmarkEntry = new DelegateEntry( connection, dn );
    }


    /**
     * {@inheritDoc}
     */
    public DN getDn()
    {
        return this.bookmarkParameter.getDn();
    }


    /**
     * {@inheritDoc}
     */
    public void setDn( DN dn )
    {
        this.bookmarkParameter.setDn( dn );
        this.fireBookmarkUpdated( BookmarkUpdateEvent.Detail.BOOKMARK_UPDATED );
    }


    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return this.bookmarkParameter.getName();
    }


    /**
     * {@inheritDoc}
     */
    public void setName( String name )
    {
        this.bookmarkParameter.setName( name );
        this.fireBookmarkUpdated( BookmarkUpdateEvent.Detail.BOOKMARK_UPDATED );
    }


    /**
     * {@inheritDoc}
     */
    public Object getAdapter( Class adapter )
    {
        Class<?> clazz = ( Class<?> ) adapter;
        if ( clazz.isAssignableFrom( ISearchPageScoreComputer.class ) )
        {
            return new LdapSearchPageScoreComputer();
        }
        if ( clazz.isAssignableFrom( Connection.class ) )
        {
            return getBrowserConnection().getConnection();
        }
        if ( clazz.isAssignableFrom( IBrowserConnection.class ) )
        {
            return getBrowserConnection();
        }
        if ( clazz.isAssignableFrom( IEntry.class ) )
        {
            return getEntry();
        }
        if ( clazz.isAssignableFrom( IBookmark.class ) )
        {
            return this;
        }

        return null;
    }


    private void fireBookmarkUpdated( BookmarkUpdateEvent.Detail detail )
    {
        if ( this.getName() != null && !"".equals( this.getName() ) ) { //$NON-NLS-1$
            EventRegistry.fireBookmarkUpdated( new BookmarkUpdateEvent( this, detail ), this );
        }
    }


    /**
     * {@inheritDoc}
     */
    public BookmarkParameter getBookmarkParameter()
    {
        return bookmarkParameter;
    }


    /**
     * {@inheritDoc}
     */
    public void setBookmarkParameter( BookmarkParameter bookmarkParameter )
    {
        this.bookmarkParameter = bookmarkParameter;
    }


    /**
     * {@inheritDoc}
     */
    public IBrowserConnection getBrowserConnection()
    {
        return this.connection;
    }


    /**
     * {@inheritDoc}
     */
    public IEntry getEntry()
    {
        return this.bookmarkEntry;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return this.getName();
    }

}
