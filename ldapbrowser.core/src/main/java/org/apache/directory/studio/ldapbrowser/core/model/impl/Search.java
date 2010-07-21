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


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.util.LdapURL;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.StudioControl;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.connection.core.jobs.StudioBulkRunnableWithProgress;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.events.SearchUpdateEvent;
import org.apache.directory.studio.ldapbrowser.core.internal.search.LdapSearchPageScoreComputer;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.SearchParameter;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;
import org.eclipse.search.ui.ISearchPageScoreComputer;


/**
 * Default implementation of ISearch.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class Search implements ISearch
{

    /** The serialVersionUID. */
    private static final long serialVersionUID = -3482673086666351174L;

    /** The connection. */
    protected IBrowserConnection connection;

    /** The search results. */
    protected ISearchResult[] searchResults;

    /** The search parameter. */
    protected SearchParameter searchParameter;

    /** The count limit exceeded flag. */
    protected boolean countLimitExceeded;

    /** The next search runnable. */
    protected StudioBulkRunnableWithProgress nextSearchRunnable;

    /** The top search runnable. */
    protected StudioBulkRunnableWithProgress topSearchRunnable;

    /** The search continuations. */
    protected SearchContinuation[] searchContinuations;


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
     * <li>always dereference aliases
     * <li>follow referrals
     * <li>no initialization of hasChildren flag
     * <li>no controls
     * <li>no response controls
     * </ul>
     */
    public Search()
    {
        this(
            new SimpleDateFormat( "yyyy-MM-dd HH-mm-ss" ).format( new Date() ), //$NON-NLS-1$
            null, EMPTY_SEARCH_BASE, FILTER_TRUE, NO_ATTRIBUTES, SearchScope.ONELEVEL, 0, 0,
            AliasDereferencingMethod.ALWAYS, ReferralHandlingMethod.FOLLOW, false, null );
    }


    /**
     * Creates a new Search with the given connection and search parameters.
     *
     * @param conn the connection
     * @param searchParameter the search parameters
     */
    public Search( IBrowserConnection conn, SearchParameter searchParameter )
    {
        this.connection = conn;
        this.searchResults = null;
        this.searchParameter = searchParameter;
        this.countLimitExceeded = false;
        this.nextSearchRunnable = null;
        this.searchContinuations = null;
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
     *                the search scope
     * @param countLimit
     *                the count limit, 0 indicates no limit
     * @param timeLimit
     *                the time limit in seconds, 0 indicates no limit
     * @param aliasesDereferencingMethod
     *                the aliases dereferencing method
     * @param referralsHandlingMethod
     *                the referrals handling method
     * @param initHasChildrenFlag
     *                the init hasChildren flag
     * @param controls
     *                the controls
     */
    public Search( String searchName, IBrowserConnection conn, LdapDN searchBase, String filter,
        String[] returningAttributes, SearchScope scope, int countLimit, int timeLimit,
        AliasDereferencingMethod aliasesDereferencingMethod, ReferralHandlingMethod referralsHandlingMethod,
        boolean initHasChildrenFlag, List<StudioControl> controls )
    {
        this.connection = conn;
        this.searchResults = null;
        this.countLimitExceeded = false;
        this.nextSearchRunnable = null;

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
        if ( controls != null )
        {
            this.searchParameter.getControls().addAll( controls );
        }
    }


    /**
     * {@inheritDoc}
     */
    public LdapURL getUrl()
    {
        return Utils.getLdapURL( this );
    }


    /**
     * Fires a search update event if the search name is set.
     *
     * @param detail the SearchUpdateEvent detail
     */
    protected void fireSearchUpdated( SearchUpdateEvent.EventDetail detail )
    {
        if ( getName() != null && !"".equals( getName() ) ) { //$NON-NLS-1$
            EventRegistry.fireSearchUpdated( new SearchUpdateEvent( this, detail ), this );
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean isInitHasChildrenFlag()
    {
        return searchParameter.isInitHasChildrenFlag();
    }


    /**
     * {@inheritDoc}
     */
    public List<StudioControl> getControls()
    {
        return searchParameter.getControls();
    }


    /**
     * {@inheritDoc}
     */
    public List<StudioControl> getResponseControls()
    {
        return searchParameter.getResponseControls();
    }


    /**
     * {@inheritDoc}
     */
    public int getCountLimit()
    {
        return searchParameter.getCountLimit();
    }


    /**
     * {@inheritDoc}
     */
    public void setCountLimit( int countLimit )
    {
        searchParameter.setCountLimit( countLimit );
        fireSearchUpdated( SearchUpdateEvent.EventDetail.SEARCH_PARAMETER_UPDATED );
    }


    /**
     * {@inheritDoc}
     */
    public String getFilter()
    {
        return searchParameter.getFilter();
    }


    /**
     * {@inheritDoc}
     */
    public void setFilter( String filter )
    {
        searchParameter.setFilter( filter );
        fireSearchUpdated( SearchUpdateEvent.EventDetail.SEARCH_PARAMETER_UPDATED );
    }


    /**
     * {@inheritDoc}
     */
    public String[] getReturningAttributes()
    {
        return searchParameter.getReturningAttributes();
    }


    /**
     * {@inheritDoc}
     */
    public void setReturningAttributes( String[] returningAttributes )
    {
        searchParameter.setReturningAttributes( returningAttributes );
        fireSearchUpdated( SearchUpdateEvent.EventDetail.SEARCH_PARAMETER_UPDATED );
    }


    /**
     * {@inheritDoc}
     */
    public SearchScope getScope()
    {
        return searchParameter.getScope();
    }


    /**
     * {@inheritDoc}
     */
    public void setScope( SearchScope scope )
    {
        searchParameter.setScope( scope );
        fireSearchUpdated( SearchUpdateEvent.EventDetail.SEARCH_PARAMETER_UPDATED );
    }


    /**
     * {@inheritDoc}
     */
    public AliasDereferencingMethod getAliasesDereferencingMethod()
    {
        return searchParameter.getAliasesDereferencingMethod();
    }


    /**
     * {@inheritDoc}
     */
    public void setAliasesDereferencingMethod( Connection.AliasDereferencingMethod aliasesDereferencingMethod )
    {
        searchParameter.setAliasesDereferencingMethod( aliasesDereferencingMethod );
        fireSearchUpdated( SearchUpdateEvent.EventDetail.SEARCH_PARAMETER_UPDATED );
    }


    /**
     * {@inheritDoc}
     */
    public ReferralHandlingMethod getReferralsHandlingMethod()
    {
        return searchParameter.getReferralsHandlingMethod();
    }


    /**
     * {@inheritDoc}
     */
    public void setReferralsHandlingMethod( Connection.ReferralHandlingMethod referralsHandlingMethod )
    {
        searchParameter.setReferralsHandlingMethod( referralsHandlingMethod );
        fireSearchUpdated( SearchUpdateEvent.EventDetail.SEARCH_PARAMETER_UPDATED );
    }


    /**
     * {@inheritDoc}
     */
    public LdapDN getSearchBase()
    {
        return searchParameter.getSearchBase();
    }


    /**
     * {@inheritDoc}
     */
    public void setSearchBase( LdapDN searchBase )
    {
        searchParameter.setSearchBase( searchBase );
        fireSearchUpdated( SearchUpdateEvent.EventDetail.SEARCH_PARAMETER_UPDATED );
    }


    /**
     * {@inheritDoc}
     */
    public int getTimeLimit()
    {
        return searchParameter.getTimeLimit();
    }


    /**
     * {@inheritDoc}
     */
    public void setTimeLimit( int timeLimit )
    {
        searchParameter.setTimeLimit( timeLimit );
        fireSearchUpdated( SearchUpdateEvent.EventDetail.SEARCH_PARAMETER_UPDATED );
    }


    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return searchParameter.getName();
    }


    /**
     * {@inheritDoc}
     */
    public void setName( String searchName )
    {
        searchParameter.setName( searchName );
        fireSearchUpdated( SearchUpdateEvent.EventDetail.SEARCH_RENAMED );
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
        if ( searchResults != null && getName() != null )
        {
            fireSearchUpdated( SearchUpdateEvent.EventDetail.SEARCH_PERFORMED );
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean isCountLimitExceeded()
    {
        return countLimitExceeded;
    }


    /**
     * {@inheritDoc}
     */
    public void setCountLimitExceeded( boolean countLimitExceeded )
    {
        this.countLimitExceeded = countLimitExceeded;
        fireSearchUpdated( SearchUpdateEvent.EventDetail.SEARCH_PERFORMED );
    }


    /**
     * {@inheritDoc}
     */
    public IBrowserConnection getBrowserConnection()
    {
        return connection;
    }


    /**
     * {@inheritDoc}
     */
    public void setBrowserConnection( IBrowserConnection connection )
    {
        this.connection = connection;
        searchParameter.setCountLimit( connection.getCountLimit() );
        searchParameter.setTimeLimit( connection.getTimeLimit() );
        searchParameter.setAliasesDereferencingMethod( connection.getAliasesDereferencingMethod() );
        searchParameter.setReferralsHandlingMethod( connection.getReferralsHandlingMethod() );
        fireSearchUpdated( SearchUpdateEvent.EventDetail.SEARCH_PARAMETER_UPDATED );
    }


    /**
     * {@inheritDoc}
     */
    public StudioBulkRunnableWithProgress getNextSearchRunnable()
    {
        return nextSearchRunnable;
    }


    /**
     * {@inheritDoc}
     */
    public void setNextPageSearchRunnable( StudioBulkRunnableWithProgress nextSearchRunnable )
    {
        this.nextSearchRunnable = nextSearchRunnable;
    }


    /**
     * {@inheritDoc}
     */
    public StudioBulkRunnableWithProgress getTopSearchRunnable()
    {
        return topSearchRunnable;
    }


    /**
     * {@inheritDoc}
     */
    public void setTopPageSearchRunnable( StudioBulkRunnableWithProgress topSearchRunnable )
    {
        this.topSearchRunnable = topSearchRunnable;
    }


    /**
     * {@inheritDoc}
     */
    public SearchContinuation[] getSearchContinuations()
    {
        return searchContinuations;
    }


    /**
     * {@inheritDoc}
     */
    public void setSearchContinuations( SearchContinuation[] searchContinuations )
    {
        this.searchContinuations = searchContinuations;
        if ( searchContinuations != null && getName() != null )
        {
            fireSearchUpdated( SearchUpdateEvent.EventDetail.SEARCH_PERFORMED );
        }

    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return getName() + " (" + getBrowserConnection() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }


    /**
     * {@inheritDoc}
     */
    public Object clone()
    {
        return new Search( getName(), getBrowserConnection(), getSearchBase(), getFilter(), getReturningAttributes(),
            getScope(), getCountLimit(), getTimeLimit(), getAliasesDereferencingMethod(), getReferralsHandlingMethod(),
            isInitHasChildrenFlag(), getControls() );
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
    @SuppressWarnings("unchecked")
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
        if ( clazz.isAssignableFrom( ISearch.class ) )
        {
            return this;
        }

        return null;
    }


    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( connection == null ) ? 0 : connection.hashCode() );
        result = prime
            * result
            + ( ( searchParameter == null || searchParameter.getName() == null ) ? 0 : searchParameter.getName()
                .hashCode() );
        return result;
    }


    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( !( obj instanceof Search ) )
        {
            return false;
        }
        Search other = ( Search ) obj;
        if ( connection == null )
        {
            if ( other.connection != null )
            {
                return false;
            }
        }
        else if ( !connection.equals( other.connection ) )
        {
            return false;
        }
        if ( searchParameter == null || searchParameter.getName() == null )
        {
            if ( other.searchParameter != null && other.searchParameter.getName() != null )
            {
                return false;
            }
        }
        else if ( !searchParameter.getName().equals( other.searchParameter.getName() ) )
        {
            return false;
        }
        return true;
    }

}
