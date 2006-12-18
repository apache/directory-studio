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

package org.apache.directory.ldapstudio.browser.core.internal.model;


import org.apache.directory.ldapstudio.browser.core.events.BookmarkUpdateEvent;
import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.internal.search.LdapSearchPageScoreComputer;
import org.apache.directory.ldapstudio.browser.core.model.BookmarkParameter;
import org.apache.directory.ldapstudio.browser.core.model.DN;
import org.apache.directory.ldapstudio.browser.core.model.IBookmark;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.eclipse.search.ui.ISearchPageScoreComputer;


public class Bookmark implements IBookmark
{

    private static final long serialVersionUID = 2914726541167255499L;

    private IConnection connection;

    private BookmarkParameter bookmarkParameter;

    private DelegateEntry bookmarkEntry;


    protected Bookmark()
    {
    }


    public Bookmark( IConnection connection, BookmarkParameter bookmarkParameter )
    {
        this.connection = connection;
        this.bookmarkParameter = bookmarkParameter;
        this.bookmarkEntry = new DelegateEntry( connection, bookmarkParameter.getDn() );
    }


    public Bookmark( IConnection connection, DN dn, String name )
    {
        this.connection = connection;
        this.bookmarkParameter = new BookmarkParameter( dn, name );
        this.bookmarkEntry = new DelegateEntry( connection, dn );
    }


    public DN getDn()
    {
        return this.bookmarkParameter.getDn();
    }


    public void setDn( DN dn )
    {
        this.bookmarkParameter.setDn( dn );
        this.fireBookmarkUpdated( BookmarkUpdateEvent.BOOKMARK_UPDATED );
    }


    public String getName()
    {
        return this.bookmarkParameter.getName();
    }


    public void setName( String name )
    {
        this.bookmarkParameter.setName( name );
        this.fireBookmarkUpdated( BookmarkUpdateEvent.BOOKMARK_UPDATED );
    }


    public Object getAdapter( Class adapter )
    {
        if ( adapter.isAssignableFrom( ISearchPageScoreComputer.class ) )
        {
            return new LdapSearchPageScoreComputer();
        }
        if ( adapter == IConnection.class )
        {
            return this.connection;
        }
        if ( adapter == IEntry.class )
        {
            return this.bookmarkEntry;
        }
        if ( adapter == IBookmark.class )
        {
            return this;
        }
        return null;
    }


    private void fireBookmarkUpdated( int detail )
    {
        if ( this.getName() != null && !"".equals( this.getName() ) ) { //$NON-NLS-1$
            EventRegistry.fireBookmarkUpdated( new BookmarkUpdateEvent( this, detail ), this );
        }
    }


    public BookmarkParameter getBookmarkParameter()
    {
        return bookmarkParameter;
    }


    public void setBookmarkParameter( BookmarkParameter bookmarkParameter )
    {
        this.bookmarkParameter = bookmarkParameter;
    }


    public IConnection getConnection()
    {
        return this.connection;
    }


    public IEntry getEntry()
    {
        return this.bookmarkEntry;
    }


    public IBookmark getBookmark()
    {
        return this;
    }


    public String toString()
    {
        return this.getName();
    }

}
