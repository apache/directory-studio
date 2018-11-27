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


import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IQuickSearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;


/**
 * Default implementation of IQuickSearch.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class QuickSearch extends Search implements IQuickSearch
{
    private static final long serialVersionUID = 4387604973869066354L;

    /** The search base entry. */
    private IEntry searchBaseEntry;


    /**
     * Instantiates a new quick search.
     * 
     * @param searchBaseEntry the search base entry
     */
    public QuickSearch( IEntry searchBaseEntry )
    {
        this.searchBaseEntry = searchBaseEntry;
    }


    /**
     * Instantiates a new quick search.
     * 
     * @param searchBaseEntry the search base entry
     * @param connection the connection
     */
    public QuickSearch( IEntry searchBaseEntry, IBrowserConnection connection )
    {
        this.searchBaseEntry = searchBaseEntry;
        this.connection = connection;

        // set default parameter
        getSearchParameter().setName( BrowserCoreMessages.model__quick_search_name );
        getSearchParameter().setSearchBase( searchBaseEntry.getDn() );
        getSearchParameter().setReturningAttributes( ISearch.NO_ATTRIBUTES );
        getSearchParameter().setAliasesDereferencingMethod( connection.getAliasesDereferencingMethod() );
        getSearchParameter().setReferralsHandlingMethod( connection.getReferralsHandlingMethod() );
        getSearchParameter().setCountLimit( connection.getCountLimit() );
        getSearchParameter().setTimeLimit( connection.getTimeLimit() );
        getSearchParameter().setScope( SearchScope.SUBTREE );
    }


    public IEntry getSearchBaseEntry()
    {
        return searchBaseEntry;
    }
}
