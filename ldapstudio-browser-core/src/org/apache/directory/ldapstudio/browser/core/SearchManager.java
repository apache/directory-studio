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

import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.events.SearchUpdateEvent;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.ISearch;


public class SearchManager implements Serializable
{

    private static final long serialVersionUID = 8665227628274097691L;

    private List searchList;

    private IConnection connection;


    protected SearchManager()
    {
    }


    public SearchManager( IConnection connection )
    {
        this.connection = connection;
        this.searchList = new ArrayList();
    }


    public IConnection getConnection()
    {
        return this.connection;
    }


    public void addSearch( ISearch search )
    {
        this.addSearch( this.searchList.size(), search );
    }


    public void addSearch( int index, ISearch search )
    {
        if ( this.getSearch( search.getName() ) != null )
        {
            String newSearchName = BrowserCoreMessages.bind( BrowserCoreMessages.copy_n_of_s, "", search.getName() ); //$NON-NLS-1$
            for ( int i = 2; this.getSearch( newSearchName ) != null; i++ )
            {
                newSearchName = BrowserCoreMessages.bind( BrowserCoreMessages.copy_n_of_s, i + " ", search.getName() ); //$NON-NLS-1$
            }
            search.setName( newSearchName );
        }

        this.searchList.add( index, search );
        EventRegistry.fireSearchUpdated( new SearchUpdateEvent( search, SearchUpdateEvent.SEARCH_ADDED ), this );
    }


    public ISearch getSearch( String name )
    {
        for ( Iterator it = this.searchList.iterator(); it.hasNext(); )
        {
            ISearch search = ( ISearch ) it.next();
            if ( search.getName().equals( name ) )
            {
                return search;
            }
        }
        return null;
    }


    public int indexOf( ISearch search )
    {
        return this.searchList.indexOf( search );
    }


    public void removeSearch( ISearch search )
    {
        this.searchList.remove( search );
        EventRegistry.fireSearchUpdated( new SearchUpdateEvent( search, SearchUpdateEvent.SEARCH_REMOVED ), this );
    }


    public void removeSearch( String name )
    {
        this.removeSearch( this.getSearch( name ) );
    }


    public ISearch[] getSearches()
    {
        return ( ISearch[] ) this.searchList.toArray( new ISearch[0] );
    }


    public int getSearchCount()
    {
        return this.searchList.size();
    }

}
