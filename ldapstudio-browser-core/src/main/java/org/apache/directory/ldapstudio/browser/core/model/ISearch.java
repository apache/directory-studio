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

package org.apache.directory.ldapstudio.browser.core.model;


import java.io.Serializable;

import org.apache.directory.ldapstudio.browser.core.propertypageproviders.ConnectionPropertyPageProvider;
import org.apache.directory.ldapstudio.browser.core.propertypageproviders.SearchPropertyPageProvider;
import org.eclipse.core.runtime.IAdaptable;


/**
 * An ISearch holds all search parameters and search results of an
 * LDAP search.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface ISearch extends Serializable, IAdaptable, SearchPropertyPageProvider, ConnectionPropertyPageProvider
{

    /** Constant for empty search base */
    public static final DN EMPTY_SEARCH_BASE = new DN(); //$NON-NLS-1$

    /** The returning attribute shortcut for all user attributes '*' */
    public static final String ALL_USER_ATTRIBUTES = "*"; //$NON-NLS-1$

    /** The returning attribute shortcut for all operational attributes '+' */
    public static final String ALL_OPERATIONAL_ATTRIBUTES = "+"; //$NON-NLS-1$

    /** Constant for no returning attributes, an empty array */
    public static final String[] NO_ATTRIBUTES = new String[0];

    /** True filter (objectClass=*) */
    public static final String FILTER_TRUE = "(objectClass=*)"; //$NON-NLS-1$

    /** False filter (!(objectClass=*)) */
    public static final String FILTER_FALSE = "(!(objectClass=*))"; //$NON-NLS-1$

    /** Search scope object */
    public static final int SCOPE_OBJECT = 0;

    /** Search scope one level */
    public static final int SCOPE_ONELEVEL = 1;

    /** Search scope subtree */
    public static final int SCOPE_SUBTREE = 2;


    /**
     * Gets the LDAP URL of this search.
     * 
     * @return the LDAP URL of this search
     */
    public abstract URL getUrl();


    /**
     * Checks if the hasChildren flag should be initialized.
     * 
     * @return true, if the hasChildren flag should be initialized
     */
    public abstract boolean isInitHasChildrenFlag();


    /**
     * Checks if the isAlias and isReferral flags should be initialized.
     * 
     * @return true, if the isAlias and isReferral flags should be initialized
     */
    public abstract boolean isInitAliasAndReferralFlag();


    /**
     * Gets the controls.
     * 
     * @return the controls
     */
    public abstract Control[] getControls();


    /**
     * Gets the count limit, 0 means no limit.
     * 
     * @return the count limit
     */
    public abstract int getCountLimit();


    /**
     * Sets the count limit, 0 means no limit.
     * 
     * @param countLimit the count limit
     */
    public abstract void setCountLimit( int countLimit );


    /**
     * Gets the filter.
     * 
     * @return the filter
     */
    public abstract String getFilter();


    /**
     * Sets the filter, a null or empty filter will be
     * transformed to (objectClass=*).
     * 
     * Calling this method causes firing a search update event.
     * 
     * @param filter the filter
     */
    public abstract void setFilter( String filter );


    /**
     * Gets the returning attributes.
     * 
     * @return the returning attributes
     */
    public abstract String[] getReturningAttributes();


    /**
     * Sets the returning attributes, an empty array indicates none,
     * null will be transformed to '*' (all user attributes).
     * 
     * Calling this method causes firing a search update event.
     * 
     * @param returningAttributes the returning attributes
     */
    public abstract void setReturningAttributes( String[] returningAttributes );


    /**
     * Gets the search scope, one of ISearch.SCOPE_OBJECT, 
     * ISearch.SCOPE_ONELEVEL or ISearch.SCOPE_SUBTREE.
     * 
     * @return the search scope
     */
    public abstract int getScope();


    /**
     * Sets the search scope, one of ISearch.SCOPE_OBJECT, 
     * ISearch.SCOPE_ONELEVEL or ISearch.SCOPE_SUBTREE.
     * 
     * Calling this method causes firing a search update event.
     * 
     * @param scope the search scope
     */
    public abstract void setScope( int scope );


    /**
     * Gets the aliases dereferencing method, one of IConnection.DEREFERENCE_ALIASES_NEVER, 
     * IConnection.DEREFERENCE_ALIASES_ALWAYS, IConnection.DEREFERENCE_ALIASES_FINDING 
     * or IConnection.DEREFERENCE_ALIASES_SEARCH.
     * 
     * 
     * @return the aliases dereferencing method
     */
    public abstract int getAliasesDereferencingMethod();


    /**
     * Sets the aliases dereferencing method, one of IConnection.DEREFERENCE_ALIASES_NEVER, 
     * IConnection.DEREFERENCE_ALIASES_ALWAYS, IConnection.DEREFERENCE_ALIASES_FINDING 
     * or IConnection.DEREFERENCE_ALIASES_SEARCH.
     * 
     * Calling this method causes firing a search update event.
     * 
     * @param aliasesDereferencingMethod the aliases dereferencing method
     */
    public abstract void setAliasesDereferencingMethod( int aliasesDereferencingMethod );


    /**
     * Gets the referrals handling method, one of IConnection.HANDLE_REFERRALS_IGNORE
     * or IConnection.HANDLE_REFERRALS_FOLLOW.
     *  
     * @return the referrals handling method
     */
    public abstract int getReferralsHandlingMethod();


    /**
     * Sets the referrals handling method, one of IConnection.HANDLE_REFERRALS_IGNORE or 
     * IConnection.HANDLE_REFERRALS_FOLLOW.
     * 
     * Calling this method causes firing a search update event.
     * 
     * @param referralsHandlingMethod the referrals handling method
     */
    public abstract void setReferralsHandlingMethod( int referralsHandlingMethod );


    /**
     * Gets the search base.
     * 
     * @return the search base
     */
    public abstract DN getSearchBase();


    /**
     * Sets the search base, a null search base will be
     * transformed to an empty DN.
     * 
     * Calling this method causes firing a search update event.
     * 
     * @param searchBase the search base
     */
    public abstract void setSearchBase( DN searchBase );


    /**
     * Gets the time limit in milliseconds, 0 means no limit.
     * 
     * @return the time limit
     */
    public abstract int getTimeLimit();


    /**
     * Sets the time limit in milliseconds, 0 means no limit.
     * 
     * Calling this method causes firing a search update event.
     * 
     * @param timeLimit the time limit
     */
    public abstract void setTimeLimit( int timeLimit );


    /**
     * Gets the symbolic name.
     * 
     * @return the name
     */
    public abstract String getName();


    /**
     * Sets the symbolic name.
     * 
     * Calling this method causes firing a search update event.
     * 
     * @param name the name
     */
    public abstract void setName( String searchName );


    /**
     * Gets the search results, null indicates that the
     * search wasn't performed yet.
     * 
     * @return the search results
     */
    public abstract ISearchResult[] getSearchResults();


    /**
     * Sets the search results.
     * 
     * Calling this method causes firing a search update event.
     * 
     * @param searchResults the search results
     */
    public abstract void setSearchResults( ISearchResult[] searchResults );


    /**
     * Checks if the count limit exceeded.
     * 
     * @return true, if the count limit exceeded
     */
    public abstract boolean isCountLimitExceeded();


    /**
     * Sets the count limit exceeded flag.
     * 
     * Calling this method causes firing a search update event.
     * 
     * @param countLimitExceeded the count limit exceeded flag
     */
    public abstract void setCountLimitExceeded( boolean countLimitExceeded );


    /**
     * Gets the connection.
     * 
     * @return the connection
     */
    public abstract IConnection getConnection();


    /**
     * Sets the connection.
     * 
     * Calling this method causes firing a search update event.
     * 
     * @param connection the connection
     */
    public abstract void setConnection( IConnection connection );


    /**
     * Clones this search.
     * 
     * @return the cloned search
     */
    public abstract Object clone();


    /**
     * Gets the search parameter.
     * 
     * @return the search parameter
     */
    public abstract SearchParameter getSearchParameter();


    /**
     * Sets the search parameter.
     * 
     * @param searchParameter the search parameter
     */
    public abstract void setSearchParameter( SearchParameter searchParameter );

}
