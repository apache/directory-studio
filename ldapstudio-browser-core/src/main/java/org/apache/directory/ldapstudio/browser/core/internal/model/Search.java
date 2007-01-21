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


/**
 * Default implementation of ISearch.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class Search implements ISearch
{

    /** The serialVersionUID. */
    private static final long serialVersionUID = -3482673086666351174L;

    /** The connection. */
    private IConnection connection;

    /** The search results. */
    private ISearchResult[] searchResults;

    /** The search parameter. */
    private SearchParameter searchParameter;

    /** The count limit exceeded flag. */
    private boolean countLimitExceeded;


    /**
     * Creates a new search with the following parameters:
     * <ul>
     * <li>searchName: current date
     * <li>connection: null
     * <li>empty search base
     * <li>default filter (objectClass=*)
     * <li>no returning attributes
     * <li>search scope one level
     * <li>no count limit
     * <li>no time limit
     * <li>never dereference aliases
     * <li>ignore referrals
     * <li>no initialization of hasChildren flag
     * <li>no initialization of isAlias and isReferral flag
     * <li>no controls  
     * <li>
     * </ul>
     */
    public Search()
    {
        this(
            new SimpleDateFormat( "yyyy-MM-dd HH-mm-ss" ).format( new Date() ), //$NON-NLS-1$	
            null, EMPTY_SEARCH_BASE, FILTER_TRUE, NO_ATTRIBUTES, ISearch.SCOPE_ONELEVEL, 0, 0,
            IConnection.DEREFERENCE_ALIASES_NEVER, IConnection.HANDLE_REFERRALS_IGNORE, false, false, null );
    }


    /**
     * Creates a new Search with the given connection and search parameters.
     *
     * @param conn the connection
     * @param searchParameter the search parameters
     */
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
     *                the name of the search
     * @param conn
     *                the connection of the search
     * @param searchBase
     *                the base DN of the search, a null search base will be
     *                transformed to an empty DN.
     * @param filter
     *                the filter to use, null or empty filters will be
     *                transformed to (objectClass=*)
     * @param returningAttributes
     *                the attributes to return, an empty array indicates none,
     *                null will be transformed to '*' (all user attributes)
     * @param scope
     *                the search scope, one of SCOPE_OBJECT, SCOPE_ONELEVEL,
     *                SCOPE_SUBTREE
     * @param countLimit
     *                the count limit, 0 indicates no limit
     * @param timeLimit
     *                the time limit in ms, 0 indicates no limit
     * @param aliasesDereferencingMethod
     *                the aliases dereferencing method, one of IConnection.DEREFERENCE_ALIASES_NEVER, 
     *                IConnection.DEREFERENCE_ALIASES_ALWAYS, IConnection.DEREFERENCE_ALIASES_FINDING
     *                or IConnection.DEREFERENCE_ALIASES_SEARCH
     * @param referralsHandlingMethod
     *                the referrals handling method, one of IConnection.HANDLE_REFERRALS_IGNORE 
     *                or IConnection.HANDLE_REFERRALS_FOLLOW 
     * @param initHasChildrenFlag
     *                the init hasChildren flag
     * @param initAliasAndReferralsFlag
     *                the init isAlias and isReferral flag
     */
    public Search( String searchName, IConnection conn, DN searchBase, String filter, String[] returningAttributes,
        int scope, int countLimit, int timeLimit, int aliasesDereferencingMethod, int referralsHandlingMethod,
        boolean initHasChildrenFlag, boolean initAliasAndReferralsFlag, Control[] controls )
    {
        this.connection = conn;
        this.searchResults = null;
        this.countLimitExceeded = false;

        this.searchParameter = new SearchParameter();
        this.searchParameter.setName( searchName );
        this.searchParameter.setSearchBase( searchBase );
        this.searchParameter.setFilter( filter );
        this.searchParameter.setReturningAttributes( returningAttributes );
        this.searchParameter.setScope( scope );
        this.searchParameter.setTimeLimit( timeLimit );
        this.searchParameter.setCountLimit( countLimit );
        this.searchParameter.setAliasesDereferencingMethod( aliasesDereferencingMethod );
        this.searchParameter.setReferralsHandlingMethod( referralsHandlingMethod );
        this.searchParameter.setInitHasChildrenFlag( initHasChildrenFlag );
        this.searchParameter.setInitAliasAndReferralFlag( initAliasAndReferralsFlag );
        this.searchParameter.setControls( controls );
    }


    /**
     * {@inheritDoc}
     */
    public URL getUrl()
    {
        return new URL( this );
    }


    /**
     * Fires a search update event if the search name is set.
     *
     * @param detail the SearchUpdateEvent detail
     */
    private void fireSearchUpdated( int detail )
    {
        if ( this.getName() != null && !"".equals( this.getName() ) ) { //$NON-NLS-1$
            EventRegistry.fireSearchUpdated( new SearchUpdateEvent( this, detail ), this );
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean isInitHasChildrenFlag()
    {
        return this.searchParameter.isInitHasChildrenFlag();
    }


    /**
     * {@inheritDoc}
     */
    public boolean isInitAliasAndReferralFlag()
    {
        return this.searchParameter.isInitAliasAndReferralFlag();
    }


    /**
     * {@inheritDoc}
     */
    public Control[] getControls()
    {
        return this.searchParameter.getControls();
    }


    /**
     * {@inheritDoc}
     */
    public int getCountLimit()
    {
        return this.searchParameter.getCountLimit();
    }


    /**
     * {@inheritDoc}
     */
    public void setCountLimit( int countLimit )
    {
        this.searchParameter.setCountLimit( countLimit );
        this.fireSearchUpdated( SearchUpdateEvent.SEARCH_PARAMETER_UPDATED );
    }


    /**
     * {@inheritDoc}
     */
    public String getFilter()
    {
        return this.searchParameter.getFilter();
    }


    /**
     * {@inheritDoc}
     */
    public void setFilter( String filter )
    {
        this.searchParameter.setFilter( filter );
        this.fireSearchUpdated( SearchUpdateEvent.SEARCH_PARAMETER_UPDATED );
    }


    /**
     * {@inheritDoc}
     */
    public String[] getReturningAttributes()
    {
        return this.searchParameter.getReturningAttributes();
    }


    /**
     * {@inheritDoc}
     */
    public void setReturningAttributes( String[] returningAttributes )
    {
        this.searchParameter.setReturningAttributes( returningAttributes );
        this.fireSearchUpdated( SearchUpdateEvent.SEARCH_PARAMETER_UPDATED );
    }


    /**
     * {@inheritDoc}
     */
    public int getScope()
    {
        return this.searchParameter.getScope();
    }


    /**
     * {@inheritDoc}
     */
    public void setScope( int scope )
    {
        this.searchParameter.setScope( scope );
        this.fireSearchUpdated( SearchUpdateEvent.SEARCH_PARAMETER_UPDATED );
    }


    /**
     * {@inheritDoc}
     */
    public int getAliasesDereferencingMethod()
    {
        return this.searchParameter.getAliasesDereferencingMethod();
    }


    /**
     * {@inheritDoc}
     */
    public void setAliasesDereferencingMethod( int aliasesDereferencingMethod )
    {
        this.searchParameter.setAliasesDereferencingMethod( aliasesDereferencingMethod );
        this.fireSearchUpdated( SearchUpdateEvent.SEARCH_PARAMETER_UPDATED );
    }


    /**
     * {@inheritDoc}
     */
    public int getReferralsHandlingMethod()
    {
        return this.searchParameter.getReferralsHandlingMethod();
    }


    /**
     * {@inheritDoc}
     */
    public void setReferralsHandlingMethod( int referralsHandlingMethod )
    {
        this.searchParameter.setReferralsHandlingMethod( referralsHandlingMethod );
        this.fireSearchUpdated( SearchUpdateEvent.SEARCH_PARAMETER_UPDATED );
    }


    /**
     * {@inheritDoc}
     */
    public DN getSearchBase()
    {
        return this.searchParameter.getSearchBase();
    }


    /**
     * {@inheritDoc}
     */
    public void setSearchBase( DN searchBase )
    {
        this.searchParameter.setSearchBase( searchBase );
        this.fireSearchUpdated( SearchUpdateEvent.SEARCH_PARAMETER_UPDATED );
    }


    /**
     * {@inheritDoc}
     */
    public int getTimeLimit()
    {
        return this.searchParameter.getTimeLimit();
    }


    /**
     * {@inheritDoc}
     */
    public void setTimeLimit( int timeLimit )
    {
        this.searchParameter.setTimeLimit( timeLimit );
        this.fireSearchUpdated( SearchUpdateEvent.SEARCH_PARAMETER_UPDATED );
    }


    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return this.searchParameter.getName();
    }


    /**
     * {@inheritDoc}
     */
    public void setName( String searchName )
    {
        this.searchParameter.setName( searchName );
        this.fireSearchUpdated( SearchUpdateEvent.SEARCH_RENAMED );
    }


    /**
     * {@inheritDoc}
     */
    public ISearchResult[] getSearchResults()
    {
        return searchResults;
    }


    /**
     * {@inheritDoc}
     */
    public void setSearchResults( ISearchResult[] searchResults )
    {
        this.searchResults = searchResults;
        if ( searchResults != null )
        {
            this.fireSearchUpdated( SearchUpdateEvent.SEARCH_PERFORMED );
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean isCountLimitExceeded()
    {
        return this.countLimitExceeded;
    }


    /**
     * {@inheritDoc}
     */
    public void setCountLimitExceeded( boolean countLimitExceeded )
    {
        this.countLimitExceeded = countLimitExceeded;
        this.fireSearchUpdated( SearchUpdateEvent.SEARCH_PERFORMED );
    }


    /**
     * {@inheritDoc}
     */
    public IConnection getConnection()
    {
        return connection;
    }


    /**
     * {@inheritDoc}
     */
    public void setConnection( IConnection connection )
    {
        this.connection = connection;
        this.searchParameter.setCountLimit( connection.getCountLimit() );
        this.searchParameter.setTimeLimit( connection.getTimeLimit() );
        this.searchParameter.setAliasesDereferencingMethod( connection.getAliasesDereferencingMethod() );
        this.searchParameter.setReferralsHandlingMethod( connection.getReferralsHandlingMethod() );
        this.fireSearchUpdated( SearchUpdateEvent.SEARCH_PARAMETER_UPDATED );
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return this.getName() + " (" + this.connection + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }


    /**
     * {@inheritDoc}
     */
    public Object clone()
    {
        return new Search( this.getName(), this.getConnection(), this.getSearchBase(), this.getFilter(), this
            .getReturningAttributes(), this.getScope(), this.getCountLimit(), this.getTimeLimit(), this
            .getAliasesDereferencingMethod(), this.getReferralsHandlingMethod(), this.isInitHasChildrenFlag(), this
            .isInitAliasAndReferralFlag(), this.getControls() );
    }


    /**
     * {@inheritDoc}
     */
    public SearchParameter getSearchParameter()
    {
        return searchParameter;
    }


    /**
     * {@inheritDoc}
     */
    public void setSearchParameter( SearchParameter searchParameter )
    {
        this.searchParameter = searchParameter;
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
        if ( clazz.isAssignableFrom( IConnection.class ) )
        {
            return getConnection();
        }
        if ( clazz.isAssignableFrom( ISearch.class ) )
        {
            return this;
        }

        return null;
    }

}
