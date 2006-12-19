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


public interface ISearch extends Serializable, IAdaptable, SearchPropertyPageProvider, ConnectionPropertyPageProvider
{

    public static final String ALL_USER_ATTRIBUTES = "*"; //$NON-NLS-1$

    public static final String ALL_OPERATIONAL_ATTRIBUTES = "+"; //$NON-NLS-1$

    public static final String[] NO_ATTRIBUTES = new String[0];

    public static final String FILTER_TRUE = "(objectClass=*)"; //$NON-NLS-1$

    public static final String FILTER_FALSE = "(!(objectClass=*))"; //$NON-NLS-1$

    public static final int SCOPE_OBJECT = 0;

    public static final int SCOPE_ONELEVEL = 1;

    public static final int SCOPE_SUBTREE = 2;


    public abstract URL getUrl();


    public abstract boolean isInitChildrenFlag();


    public abstract boolean isInitAliasAndReferralFlag();


    public abstract Control[] getControls();


    public abstract int getCountLimit();


    public abstract void setCountLimit( int countLimit );


    public abstract String getFilter();


    public abstract void setFilter( String filter );


    public abstract String[] getReturningAttributes();


    public abstract void setReturningAttributes( String[] returningAttributes );


    public abstract int getScope();


    public abstract void setScope( int scope );


    public abstract int getAliasesDereferencingMethod();


    public abstract void setAliasesDereferencingMethod( int aliasesDereferencingMethod );


    public abstract int getReferralsHandlingMethod();


    public abstract void setReferralsHandlingMethod( int referralsHandlingMethod );


    public abstract DN getSearchBase();


    public abstract void setSearchBase( DN searchBase );


    public abstract int getTimeLimit();


    public abstract void setTimeLimit( int timeLimit );


    public abstract String getName();


    public abstract void setName( String searchName );


    public abstract ISearchResult[] getSearchResults();


    public abstract void setSearchResults( ISearchResult[] searchResults );


    public abstract boolean isCountLimitExceeded();


    public abstract void setCountLimitExceeded( boolean countLimitExceeded );


    public abstract IConnection getConnection();


    public abstract void setConnection( IConnection connection );


    public abstract Object clone();


    public abstract SearchParameter getSearchParameter();


    public abstract void setSearchParameter( SearchParameter searchParameter );

}
