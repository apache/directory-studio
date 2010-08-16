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


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.shared.ldap.util.LdapURL;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IContinuation;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.SearchParameter;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;


/**
 * An {@link SearchContinuation} represents a search continuation.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchContinuation extends Search implements IContinuation
{

    private static final long serialVersionUID = 9039452279802784225L;

    /** The search continuation URL */
    private LdapURL searchContinuationURL;

    /** The state */
    private State state;

    /** The dummy connection. */
    private DummyConnection dummyConnection;


    /**
     * Creates a new instance of ContinuedSearchResultEntry.
     * 
     * @param dn the DN
     * @param resultBrowserConnection the connection 
     * @param connection the connection of the continued search
     * @param dn the DN of the entry
     */
    public SearchContinuation( ISearch originalSearch, LdapURL searchContinuationURL )
    {
        super( null, ( SearchParameter ) originalSearch.getSearchParameter().clone() );
        this.searchContinuationURL = searchContinuationURL;
        this.state = State.UNRESOLVED;

        getSearchParameter().setName( searchContinuationURL.toString() );

        // apply parameters from URL
        if ( searchContinuationURL.getDn() != null && !searchContinuationURL.getDn().isEmpty() )
        {
            getSearchParameter().setSearchBase( searchContinuationURL.getDn() );
        }
        if ( searchContinuationURL.getFilter() != null && getSearchParameter().getFilter().length() > 0 )
        {
            getSearchParameter().setFilter( searchContinuationURL.getFilter() );
        }
        if ( searchContinuationURL.getScope() > -1 )
        {
            switch ( searchContinuationURL.getScope() )
            {
                case 0:
                    getSearchParameter().setScope( SearchScope.OBJECT );
                    break;
                case 1:
                    getSearchParameter().setScope( SearchScope.ONELEVEL );
                    break;
                case 2:
                    getSearchParameter().setScope( SearchScope.SUBTREE );
                    break;
            }
        }
        if ( searchContinuationURL.getAttributes() != null && !searchContinuationURL.getAttributes().isEmpty() )
        {
            getSearchParameter()
                .setReturningAttributes( searchContinuationURL.getAttributes().toArray( new String[0] ) );
        }
    }


    @Override
    public IBrowserConnection getBrowserConnection()
    {
        if ( state == State.RESOLVED )
        {
            return super.getBrowserConnection();
        }
        else
        {
            if ( dummyConnection == null )
            {
                dummyConnection = new DummyConnection( Schema.DEFAULT_SCHEMA );
            }
            return dummyConnection;
        }
    }


    @Override
    public ISearchResult[] getSearchResults()
    {
        if ( state == State.RESOLVED )
        {
            return super.getSearchResults();
        }
        else
        {
            return null;
        }
    }


    /**
     * {@inheritDoc}
     */
    public State getState()
    {
        return state;
    }


    /**
     * {@inheritDoc}
     */
    public void resolve()
    {
        // get referral connection, exit if canceled
        List<LdapURL> urls = new ArrayList<LdapURL>();
        urls.add( searchContinuationURL );
        Connection referralConnection = ConnectionCorePlugin.getDefault().getReferralHandler().getReferralConnection(
            urls );
        if ( referralConnection == null )
        {
            state = State.CANCELED;
            return;
        }
        else
        {
            super.connection = BrowserCorePlugin.getDefault().getConnectionManager().getBrowserConnection(
                referralConnection );
            state = State.RESOLVED;
        }
    }


    /**
     * {@inheritDoc}
     */
    public LdapURL getUrl()
    {
        return searchContinuationURL;
    }


    /**
     * {@inheritDoc}
     */
    public Object clone()
    {
        SearchContinuation clone = new SearchContinuation( this, getUrl() );
        clone.state = this.state;
        clone.dummyConnection = this.dummyConnection;
        clone.connection = super.connection;
        return clone;
    }
}
