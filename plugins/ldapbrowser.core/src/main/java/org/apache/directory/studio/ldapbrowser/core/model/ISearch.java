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

package org.apache.directory.studio.ldapbrowser.core.model;


import java.io.Serializable;
import java.util.List;

import org.apache.directory.api.ldap.model.constants.LdapConstants;
import org.apache.directory.api.ldap.model.message.Control;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.url.LdapUrl;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionPropertyPageProvider;
import org.apache.directory.studio.connection.core.jobs.StudioConnectionBulkRunnableWithProgress;
import org.apache.directory.studio.ldapbrowser.core.model.impl.SearchContinuation;
import org.apache.directory.studio.ldapbrowser.core.propertypageproviders.SearchPropertyPageProvider;
import org.eclipse.core.runtime.IAdaptable;


/**
 * An ISearch holds all search parameters and search results of an
 * LDAP search.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface ISearch extends Serializable, IAdaptable, SearchPropertyPageProvider, ConnectionPropertyPageProvider
{
    /** Constant for empty search base */
    Dn EMPTY_SEARCH_BASE = new Dn(); //$NON-NLS-1$

    /** Constant for no returning attributes, an empty array */
    String[] NO_ATTRIBUTES = new String[0];

    /** True filter (objectClass=*) */
    String FILTER_TRUE = LdapConstants.OBJECT_CLASS_STAR;

    /** False filter (!(objectClass=*)) */
    String FILTER_FALSE = "(!(objectClass=*))"; //$NON-NLS-1$

    /** Filter for fetching subentries (|(objectClass=subentry)(objectClass=ldapSubentry)) */
    String FILTER_SUBENTRY = "(|(objectClass=subentry)(objectClass=ldapSubentry))"; //$NON-NLS-1$

    /** Filter for fetching aliases (objectClass=alias) */
    String FILTER_ALIAS = "(objectClass=alias)"; //$NON-NLS-1$

    /** Filter for fetching referrals (objectClass=referral) */
    String FILTER_REFERRAL = "(objectClass=referral)"; //$NON-NLS-1$

    /** Filter for fetching aliases and referrals (|(objectClass=alias)(objectClass=referral)) */
    String FILTER_ALIAS_OR_REFERRAL = "(|(objectClass=alias)(objectClass=referral))"; //$NON-NLS-1$


    /**
     * Gets the LDAP URL of this search.
     * 
     * @return the LDAP URL of this search
     */
    LdapUrl getUrl();


    /**
     * Checks if the hasChildren flag should be initialized.
     * 
     * @return true, if the hasChildren flag should be initialized
     */
    boolean isInitHasChildrenFlag();


    /**
     * Gets the controls.
     * 
     * @return the controls
     */
    List<Control> getControls();


    /**
     * Gets the response controls.
     * 
     * @return the response controls
     */
    List<Control> getResponseControls();


    /**
     * Gets the count limit, 0 means no limit.
     * 
     * @return the count limit
     */
    int getCountLimit();


    /**
     * Sets the count limit, 0 means no limit.
     * 
     * @param countLimit the count limit
     */
    void setCountLimit( int countLimit );


    /**
     * Gets the filter.
     * 
     * @return the filter
     */
    String getFilter();


    /**
     * Sets the filter, a null or empty filter will be
     * transformed to (objectClass=*).
     * 
     * Calling this method causes firing a search update event.
     * 
     * @param filter the filter
     */
    void setFilter( String filter );


    /**
     * Gets the returning attributes.
     * 
     * @return the returning attributes
     */
    String[] getReturningAttributes();


    /**
     * Sets the returning attributes, an empty array indicates none,
     * null will be transformed to '*' (all user attributes).
     * 
     * Calling this method causes firing a search update event.
     * 
     * @param returningAttributes the returning attributes
     */
    void setReturningAttributes( String[] returningAttributes );


    /**
     * Gets the search scope.
     * 
     * @return the search scope
     */
    SearchScope getScope();


    /**
     * Sets the search scope.
     * 
     * Calling this method causes firing a search update event.
     * 
     * @param scope the search scope
     */
    void setScope( SearchScope scope );


    /**
     * Gets the aliases dereferencing method.
     * 
     * 
     * @return the aliases dereferencing method
     */
    Connection.AliasDereferencingMethod getAliasesDereferencingMethod();


    /**
     * Sets the aliases dereferencing method.
     * 
     * Calling this method causes firing a search update event.
     * 
     * @param aliasesDereferencingMethod the aliases dereferencing method
     */
    void setAliasesDereferencingMethod( Connection.AliasDereferencingMethod aliasesDereferencingMethod );


    /**
     * Gets the referrals handling method.
     *  
     * @return the referrals handling method
     */
    Connection.ReferralHandlingMethod getReferralsHandlingMethod();


    /**
     * Sets the referrals handling method.
     * 
     * Calling this method causes firing a search update event.
     * 
     * @param referralsHandlingMethod the referrals handling method
     */
    void setReferralsHandlingMethod( Connection.ReferralHandlingMethod referralsHandlingMethod );


    /**
     * Gets the search base.
     * 
     * @return the search base
     */
    Dn getSearchBase();


    /**
     * Sets the search base, a null search base will be
     * transformed to an empty Dn.
     * 
     * Calling this method causes firing a search update event.
     * 
     * @param searchBase the search base
     */
    void setSearchBase( Dn searchBase );


    /**
     * Gets the time limit in seconds, 0 means no limit.
     * 
     * @return the time limit
     */
    int getTimeLimit();


    /**
     * Sets the time limit in seconds, 0 means no limit.
     * 
     * Calling this method causes firing a search update event.
     * 
     * @param timeLimit the time limit
     */
    void setTimeLimit( int timeLimit );


    /**
     * Gets the symbolic name.
     * 
     * @return the name
     */
    String getName();


    /**
     * Sets the symbolic name.
     * 
     * Calling this method causes firing a search update event.
     * 
     * @param name the name
     */
    void setName( String name );


    /**
     * Gets the search results, null indicates that the
     * search wasn't performed yet.
     * 
     * @return the search results
     */
    ISearchResult[] getSearchResults();


    /**
     * Sets the search results.
     * 
     * Calling this method causes firing a search update event.
     * 
     * @param searchResults the search results
     */
    void setSearchResults( ISearchResult[] searchResults );


    /**
     * Checks if the count limit exceeded.
     * 
     * @return true, if the count limit exceeded
     */
    boolean isCountLimitExceeded();


    /**
     * Sets the count limit exceeded flag.
     * 
     * Calling this method causes firing a search update event.
     * 
     * @param countLimitExceeded the count limit exceeded flag
     */
    void setCountLimitExceeded( boolean countLimitExceeded );


    /**
     * Gets the browser connection.
     * 
     * @return the browser connection
     */
    IBrowserConnection getBrowserConnection();


    /**
     * Sets the browser connection.
     * 
     * Calling this method causes firing a search update event.
     * 
     * @param browserConnection the browser connection
     */
    void setBrowserConnection( IBrowserConnection browserConnection );


    /**
     * Clones this search.
     * 
     * @return the cloned search
     */
    Object clone();


    /**
     * Gets the search parameter.
     * 
     * @return the search parameter
     */
    SearchParameter getSearchParameter();


    /**
     * Sets the search parameter.
     * 
     * @param searchParameter the search parameter
     */
    void setSearchParameter( SearchParameter searchParameter );


    /**
     * Gets the next search runnable.
     * 
     * @return the next search runnable, null if none
     */
    StudioConnectionBulkRunnableWithProgress getNextSearchRunnable();


    /**
     * Sets the next search runnable.
     * 
     * @param nextSearchRunnable the next search runnable
     */
    void setNextPageSearchRunnable( StudioConnectionBulkRunnableWithProgress nextSearchRunnable );


    /**
     * Gets the top search runnable.
     * 
     * @return the top search runnable, null if none
     */
    StudioConnectionBulkRunnableWithProgress getTopSearchRunnable();


    /**
     * Sets the top search runnable.
     * 
     * @param nextSearchRunnable the top search runnable
     */
    void setTopPageSearchRunnable( StudioConnectionBulkRunnableWithProgress nextSearchRunnable );


    /**
     * Gets the search continuations.
     * 
     * @return the search continuations
     */
    SearchContinuation[] getSearchContinuations();


    /**
     * Sets the search continuations 
     *
     * @param the search continuations
     */
    void setSearchContinuations( SearchContinuation[] searchContinuations );
}
