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

import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.events.SearchUpdateEvent;
import org.apache.directory.studio.ldapbrowser.core.model.IConnection;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.eclipse.osgi.util.NLS;


/**
 * This class is used to manages {@link ISearch}es of an {@link IConnection}
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SearchManager implements Serializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8665227628274097691L;

    /** The list of searches. */
    private List<ISearch> searchList;

    /** The connection. */
    private IConnection connection;


    /**
     * Creates a new instance of SearchManager.
     */
    protected SearchManager()
    {
    }


    /**
     * Creates a new instance of SearchManager.
     *
     * @param connection
     *      the attached Connection
     */
    public SearchManager( IConnection connection )
    {
        this.connection = connection;
        this.searchList = new ArrayList<ISearch>();
    }


    /**
     * Gets the Connection.
     *
     * @return
     *      the Connection
     */
    public IConnection getConnection()
    {
        return this.connection;
    }


    /**
     * Adds a Search.
     *
     * @param search
     *      the Search to add
     */
    public void addSearch( ISearch search )
    {
        this.addSearch( this.searchList.size(), search );
    }


    /**
     * Adds a Search at a specified position.
     *
     * @param index
     *      index at which the specified Search is to be inserted.
     * @param search
     *      the Search to be inserted
     */
    public void addSearch( int index, ISearch search )
    {
        if ( getSearch( search.getName() ) != null )
        {
            String newSearchName = NLS.bind( BrowserCoreMessages.copy_n_of_s, "", search.getName() ); //$NON-NLS-1$

            for ( int i = 2; this.getSearch( newSearchName ) != null; i++ )
            {
                newSearchName = NLS.bind( BrowserCoreMessages.copy_n_of_s, i + " ", search.getName() ); //$NON-NLS-1$
            }

            search.setName( newSearchName );
        }

        searchList.add( index, search );
        EventRegistry.fireSearchUpdated( new SearchUpdateEvent( search, SearchUpdateEvent.EventDetail.SEARCH_ADDED ), this );
    }


    /**
     * Gets a Search.
     *
     * @param name
     *      the name of the Search
     * @return
     *      the corresponding Search
     */
    public ISearch getSearch( String name )
    {
        for ( ISearch search:searchList )
        {
            if ( search.getName().equals( name ) )
            {
                return search;
            }
        }

        return null;
    }


    /**
     * Returns the index in the Searches list of the first occurrence of the specified Search.
     *
     * @param search
     *      the Search to search for
     * @return
     *      the index in the Searches list of the first occurrence of the specified Search
     */
    public int indexOf( ISearch search )
    {
        return searchList.indexOf( search );
    }


    /**
     * Removes a Search
     *
     * @param search
     *      the Search to remove
     */
    public void removeSearch( ISearch search )
    {
        searchList.remove( search );
        EventRegistry.fireSearchUpdated( new SearchUpdateEvent( search, SearchUpdateEvent.EventDetail.SEARCH_REMOVED ), this );
    }


    /**
     * Removes a Search
     *
     * @param name
     *      the name of the Search to remove
     */
    public void removeSearch( String name )
    {
        this.removeSearch( this.getSearch( name ) );
    }


    /**
     * Gets an array containing all the Searches
     *
     * @return
     *      an array containing all the Searches
     */
    public ISearch[] getSearches()
    {
        return searchList.toArray( new ISearch[0] );
    }


    /**
     * Gets the number of Searches
     *
     * @return
     *      the number of Searches
     */
    public int getSearchCount()
    {
        return searchList.size();
    }
}
