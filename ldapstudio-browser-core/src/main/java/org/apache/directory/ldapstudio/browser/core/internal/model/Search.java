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


import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.events.SearchUpdateEvent;
import org.apache.directory.ldapstudio.browser.core.internal.search.LdapSearchPageScoreComputer;
import org.apache.directory.ldapstudio.browser.core.model.Control;
import org.apache.directory.ldapstudio.browser.core.model.DN;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.ISearch;
import org.apache.directory.ldapstudio.browser.core.model.ISearchResult;
import org.apache.directory.ldapstudio.browser.core.model.SearchParameter;
import org.apache.directory.ldapstudio.browser.core.model.URL;
import org.eclipse.search.ui.ISearchPageScoreComputer;


public class Search implements ISearch
{

    private static final long serialVersionUID = -3482673086666351174L;

    private IConnection connection;

    private ISearchResult[] searchResults;

    private SearchParameter searchParameter;

    private boolean countLimitExceeded;


    /**
     * Creates a new search with the following parameters:
     * 
     * <li>searchName: current date
     * <li>connection: null
     * <li>searchBase: empty
     * <li>filter: empty (objectClass=*)
     * <li>returningAttributea: none
     * <li>scope: one level
     * <li>countLimit: preference value
     * <li>timeLimit: preference value
     */
    public Search()
    {
        this(
            new SimpleDateFormat( "yyyy-MM-dd HH-mm-ss" ).format( new Date() ), //$NON-NLS-1$	
            null, new DN(), FILTER_TRUE, NO_ATTRIBUTES, ISearch.SCOPE_ONELEVEL, 0, 0,
            IConnection.DEREFERENCE_ALIASES_NEVER, IConnection.HANDLE_REFERRALS_IGNORE, false, false, null );
    }


    public Search( IConnection conn, SearchParameter searchParameter )
    {
        this.connection = conn;
        this.searchResults = null;
        this.searchParameter = searchParameter;
        this.countLimitExceeded = false;
    }


    /**
     * Creates a new search with the given search parameters
     * 
     * @param searchName
     *                The name of the search
     * @param conn
     *                The connection of the search
     * @param searchBase
     *                The base DN of the search
     * @param filter
     *                The filter to use, null or empty filters will be
     *                transformed to (objectClass=*)
     * @param returningAttributes
     *                The attributes to return, empty array indicates none,
     *                null indicates all
     * @param scope
     *                the search scope, one of SCOPE_OBJECT, SCOPE_ONELEVEL,
     *                SCOPE_SUBTREE
     * @param countLimit
     *                The count limit, 0 indicates no limit
     * @param timeLimit
     *                The time limit in ms, 0 indicats no limit
     * @param aliasesDereferencingMethod
     * @param referralsHandlingMethod
     * @param initChildrenFlag
     * @param initAliasFlag
     * @param initObjectClasses
     */
    public Search( String searchName, IConnection conn, DN searchBase, String filter, String[] returningAttributes,
        int scope, int countLimit, int timeLimit, int aliasesDereferencingMethod, int referralsHandlingMethod,
        boolean initChildrenFlag, boolean initAliasAndReferralsFlag, Control[] controls )
    {
        this.connection = conn;
        this.searchResults = null;

        this.searchParameter = new SearchParameter();
        this.searchParameter.setName( searchName );
        this.searchParameter.setSearchBase( searchBase );
        if ( filter == null || "".equals( filter ) ) { //$NON-NLS-1$
            this.searchParameter.setFilter( FILTER_TRUE );
        }
        else
        {
            this.searchParameter.setFilter( filter );
        }
        if ( returningAttributes == null )
        {
            this.searchParameter.setReturningAttributes( new String[]
                { ALL_USER_ATTRIBUTES } );
        }
        else
        {
            this.searchParameter.setReturningAttributes( returningAttributes );
        }
        this.searchParameter.setScope( scope );
        this.searchParameter.setTimeLimit( timeLimit );
        this.searchParameter.setCountLimit( countLimit );
        this.searchParameter.setAliasesDereferencingMethod( aliasesDereferencingMethod );
        this.searchParameter.setReferralsHandlingMethod( referralsHandlingMethod );
        this.searchParameter.setInitChildrenFlag( initChildrenFlag );
        this.searchParameter.setInitAliasAndReferralFlag( initAliasAndReferralsFlag );
        this.searchParameter.setControls( controls );
        this.countLimitExceeded = false;
    }


    public URL getUrl()
    {
        return new URL( this );
    }


    private void fireSearchUpdated( int detail )
    {
        if ( this.getName() != null && !"".equals( this.getName() ) ) { //$NON-NLS-1$
            EventRegistry.fireSearchUpdated( new SearchUpdateEvent( this, detail ), this );
        }
    }


    public boolean isInitChildrenFlag()
    {
        return this.searchParameter.isInitChildrenFlag();
    }


    public boolean isInitAliasAndReferralFlag()
    {
        return this.searchParameter.isInitAliasAndReferralFlag();
    }


    public Control[] getControls()
    {
        return this.searchParameter.getControls();
    }


    public int getCountLimit()
    {
        return this.searchParameter.getCountLimit();
    }


    public void setCountLimit( int countLimit )
    {
        this.searchParameter.setCountLimit( countLimit );
        this.fireSearchUpdated( SearchUpdateEvent.SEARCH_PARAMETER_UPDATED );
    }


    public String getFilter()
    {
        return this.searchParameter.getFilter();
    }


    public void setFilter( String filter )
    {
        if ( filter == null || "".equals( filter ) ) { //$NON-NLS-1$
            this.searchParameter.setFilter( FILTER_TRUE );
        }
        else
        {
            this.searchParameter.setFilter( filter );
        }
        this.fireSearchUpdated( SearchUpdateEvent.SEARCH_PARAMETER_UPDATED );
    }


    public String[] getReturningAttributes()
    {
        return this.searchParameter.getReturningAttributes();
    }


    public void setReturningAttributes( String[] returningAttributes )
    {
        this.searchParameter.setReturningAttributes( returningAttributes );
        this.fireSearchUpdated( SearchUpdateEvent.SEARCH_PARAMETER_UPDATED );
    }


    public int getScope()
    {
        return this.searchParameter.getScope();
    }


    public void setScope( int scope )
    {
        this.searchParameter.setScope( scope );
        this.fireSearchUpdated( SearchUpdateEvent.SEARCH_PARAMETER_UPDATED );
    }


    public int getAliasesDereferencingMethod()
    {
        return this.searchParameter.getAliasesDereferencingMethod();
    }


    public void setAliasesDereferencingMethod( int aliasesDereferencingMethod )
    {
        this.searchParameter.setAliasesDereferencingMethod( aliasesDereferencingMethod );
        this.fireSearchUpdated( SearchUpdateEvent.SEARCH_PARAMETER_UPDATED );
    }


    public int getReferralsHandlingMethod()
    {
        return this.searchParameter.getReferralsHandlingMethod();
    }


    public void setReferralsHandlingMethod( int referralsHandlingMethod )
    {
        this.searchParameter.setReferralsHandlingMethod( referralsHandlingMethod );
        this.fireSearchUpdated( SearchUpdateEvent.SEARCH_PARAMETER_UPDATED );
    }


    public DN getSearchBase()
    {
        return this.searchParameter.getSearchBase();
    }


    public void setSearchBase( DN searchBase )
    {
        this.searchParameter.setSearchBase( searchBase );
        this.fireSearchUpdated( SearchUpdateEvent.SEARCH_PARAMETER_UPDATED );
    }


    public int getTimeLimit()
    {
        return this.searchParameter.getTimeLimit();
    }


    public void setTimeLimit( int timeLimit )
    {
        this.searchParameter.setTimeLimit( timeLimit );
        this.fireSearchUpdated( SearchUpdateEvent.SEARCH_PARAMETER_UPDATED );
    }


    public String getName()
    {
        return this.searchParameter.getName();
    }


    public void setName( String searchName )
    {
        this.searchParameter.setName( searchName );
        this.fireSearchUpdated( SearchUpdateEvent.SEARCH_RENAMED );
    }


    public ISearchResult[] getSearchResults()
    {
        return searchResults;
    }


    public void setSearchResults( ISearchResult[] searchResults )
    {
        this.searchResults = searchResults;
        if ( searchResults != null )
        {
            this.fireSearchUpdated( SearchUpdateEvent.SEARCH_PERFORMED );
        }
    }


    public boolean isCountLimitExceeded()
    {
        return this.countLimitExceeded;
    }


    public void setCountLimitExceeded( boolean countLimitExceeded )
    {
        this.countLimitExceeded = countLimitExceeded;
        this.fireSearchUpdated( SearchUpdateEvent.SEARCH_PERFORMED );
    }


    public IConnection getConnection()
    {
        return connection;
    }


    public void setConnection( IConnection connection )
    {
        this.connection = connection;
        this.searchParameter.setCountLimit( connection.getCountLimit() );
        this.searchParameter.setTimeLimit( connection.getTimeLimit() );
        this.searchParameter.setAliasesDereferencingMethod( connection.getAliasesDereferencingMethod() );
        this.searchParameter.setReferralsHandlingMethod( connection.getReferralsHandlingMethod() );
        this.fireSearchUpdated( SearchUpdateEvent.SEARCH_PARAMETER_UPDATED );
    }


    public String toString()
    {
        // return this.searchParameter.getFilter() +
        // Integer.toString(this.searchResults!=null?this.searchResults.length:0);
        return this.getName() + " (" + this.connection + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }


    public Object clone()
    {
        return new Search( this.getName(), this.getConnection(), this.getSearchBase(), this.getFilter(), this
            .getReturningAttributes(), this.getScope(), this.getCountLimit(), this.getTimeLimit(), this
            .getAliasesDereferencingMethod(), this.getReferralsHandlingMethod(), this.isInitChildrenFlag(), this
            .isInitAliasAndReferralFlag(), this.getControls() );
    }


    public SearchParameter getSearchParameter()
    {
        return searchParameter;
    }


    public void setSearchParameter( SearchParameter searchParameter )
    {
        this.searchParameter = searchParameter;
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
        if ( adapter == ISearch.class )
        {
            return this;
        }
        return null;
    }

}
