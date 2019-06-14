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
package org.apache.directory.studio.connection.core.io.api;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.naming.directory.SearchControls;

import org.apache.directory.api.ldap.codec.api.LdapApiService;
import org.apache.directory.api.ldap.codec.api.LdapApiServiceFactory;
import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.SearchCursor;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.Control;
import org.apache.directory.api.ldap.model.message.Referral;
import org.apache.directory.api.ldap.model.message.Response;
import org.apache.directory.api.ldap.model.message.SearchResultDone;
import org.apache.directory.api.ldap.model.message.SearchResultEntry;
import org.apache.directory.api.ldap.model.message.SearchResultEntryImpl;
import org.apache.directory.api.ldap.model.message.SearchResultReference;
import org.apache.directory.api.ldap.model.url.LdapUrl;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ILdapLogger;
import org.apache.directory.studio.connection.core.ReferralsInfo;
import org.apache.directory.studio.connection.core.io.ConnectionWrapperUtils;


/**
 * A naming enumeration that handles referrals itself. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StudioSearchResultEnumeration
{
    private Connection connection;

    private String searchBase;
    private String filter;
    private SearchControls searchControls;
    private AliasDereferencingMethod aliasesDereferencingMethod;
    private ReferralHandlingMethod referralsHandlingMethod;
    private Control[] controls;
    private long requestNum;
    private StudioProgressMonitor monitor;
    private ReferralsInfo referralsInfo;
    private long resultEntryCounter;

    private SearchCursor cursor;
    private SearchResultEntry currentSearchResultEntry;
    private List<String> currentReferralUrlsList;
    private StudioSearchResultEnumeration referralEnumeration;
    private SearchResultDone searchResultDone;


    /**
     * Creates a new instance of StudioSearchResultEnumeration.
     * 
     * @param connection the connection
     * @param cursor the search cursor
     * @param searchBase the search base
     * @param filter the filter
     * @param searchControls the search controls
     * @param aliasesDereferencingMethod the aliases dereferencing method
     * @param referralsHandlingMethod the referrals handling method
     * @param controls the LDAP controls
     * @param monitor the progress monitor
     * @param referralsInfo the referrals info
     */
    public StudioSearchResultEnumeration( Connection connection, SearchCursor cursor, String searchBase, String filter,
        SearchControls searchControls, AliasDereferencingMethod aliasesDereferencingMethod,
        ReferralHandlingMethod referralsHandlingMethod, Control[] controls, long requestNum,
        StudioProgressMonitor monitor, ReferralsInfo referralsInfo )
    {
        //        super( connection, searchBase, filter, searchControls, aliasesDereferencingMethod, referralsHandlingMethod,
        //            controls, requestNum, monitor, referralsInfo );

        this.connection = connection;
        this.searchBase = searchBase;
        this.filter = filter;
        this.searchControls = searchControls;
        this.aliasesDereferencingMethod = aliasesDereferencingMethod;
        this.referralsHandlingMethod = referralsHandlingMethod;
        this.controls = controls;
        this.requestNum = requestNum;
        this.monitor = monitor;
        this.referralsInfo = referralsInfo;
        this.resultEntryCounter = 0;

        if ( referralsInfo == null )
        {
            this.referralsInfo = new ReferralsInfo( false );
        }

        this.cursor = cursor;
    }


    public void close() throws LdapException
    {
        try
        {
            cursor.close();
        }
        catch ( Exception e )
        {
            throw new LdapException( e.getMessage() );
        }
    }


    public boolean hasMore() throws LdapException
    {
        try
        {
            // Nulling the current search result entry
            currentSearchResultEntry = null;

            // Do we have another response in the cursor?
            while ( cursor.next() )
            {
                Response currentResponse = cursor.get();

                // Is it a search result entry?
                if ( currentResponse instanceof SearchResultEntry )
                {
                    currentSearchResultEntry = ( SearchResultEntry ) currentResponse;

                    // return true if the current response is a search result entry
                    return true;
                }
                // Is it a search result reference (ie. a referral)?
                else if ( currentResponse instanceof SearchResultReference )
                {
                    // Are we ignoring referrals?
                    if ( referralsHandlingMethod != ReferralHandlingMethod.IGNORE )
                    {
                        // Storing the referral for later use
                        referralsInfo.addReferral( ( ( SearchResultReference ) currentResponse ).getReferral() );
                    }
                }
            }

            // Storing the search result done (if needed)
            if ( searchResultDone == null )
            {
                searchResultDone = ( ( SearchCursor ) cursor ).getSearchResultDone();
                Referral referral = searchResultDone.getLdapResult().getReferral();
                if ( referralsHandlingMethod != ReferralHandlingMethod.IGNORE && referral != null )
                {
                    // Storing the referral for later use
                    referralsInfo.addReferral( referral );
                }
            }

            // Are we following referrals manually?
            if ( referralsHandlingMethod == ReferralHandlingMethod.FOLLOW_MANUALLY )
            {
                // Checking the current referral's URLs list
                if ( ( currentReferralUrlsList != null ) && ( currentReferralUrlsList.size() > 0 ) )
                {
                    // return true if there's at least one referral LDAP URL to handle
                    return true;
                }

                // Checking the referrals list
                if ( referralsInfo.hasMoreReferrals() )
                {
                    // Getting the list of the next referral
                    currentReferralUrlsList = new ArrayList<String>( referralsInfo.getNextReferral().getLdapUrls() );

                    // return true if there's at least one referral LDAP URL to handle
                    return currentReferralUrlsList.size() > 0;
                }
            }
            // Are we following referrals automatically?
            else if ( referralsHandlingMethod == ReferralHandlingMethod.FOLLOW )
            {
                if ( ( referralEnumeration != null ) && ( referralEnumeration.hasMore() ) )
                {
                    // return true if there's at least one more entry in the current cursor naming enumeration
                    return true;
                }

                if ( referralsInfo.hasMoreReferrals() )
                {
                    Referral referral = referralsInfo.getNextReferral();
                    List<String> referralUrls = new ArrayList<String>( referral.getLdapUrls() );
                    LdapUrl url = new LdapUrl( referralUrls.get( 0 ) );

                    Connection referralConnection = ConnectionWrapperUtils.getReferralConnection( referral, monitor,
                        this );
                    if ( referralConnection != null )
                    {
                        String referralSearchBase = url.getDn() != null && !url.getDn().isEmpty()
                            ? url.getDn().getName()
                            : searchBase;
                        String referralFilter = url.getFilter() != null && url.getFilter().length() == 0
                            ? url.getFilter()
                            : filter;
                        SearchControls referralSearchControls = new SearchControls();
                        referralSearchControls.setSearchScope( url.getScope().getScope() > -1
                            ? url.getScope().getScope()
                            : searchControls.getSearchScope() );
                        referralSearchControls
                            .setReturningAttributes( url.getAttributes() != null && url.getAttributes().size() > 0
                                ? url.getAttributes().toArray( new String[url.getAttributes().size()] )
                                : searchControls.getReturningAttributes() );
                        referralSearchControls.setCountLimit( searchControls.getCountLimit() );
                        referralSearchControls.setTimeLimit( searchControls.getTimeLimit() );
                        referralSearchControls.setDerefLinkFlag( searchControls.getDerefLinkFlag() );
                        referralSearchControls.setReturningObjFlag( searchControls.getReturningObjFlag() );

                        referralEnumeration = referralConnection.getConnectionWrapper().search( referralSearchBase,
                            referralFilter, referralSearchControls, aliasesDereferencingMethod, referralsHandlingMethod,
                            controls, monitor, referralsInfo );

                        return referralEnumeration.hasMore();
                    }
                }
            }

            for ( ILdapLogger logger : ConnectionCorePlugin.getDefault().getLdapLoggers() )
            {
                logger.logSearchResultDone( connection, resultEntryCounter, requestNum, null );
            }

            return false;
        }
        catch ( CursorException e )
        {
            throw new LdapException( e.getMessage(), e );
        }
    }


    public StudioSearchResult next() throws LdapException
    {
        try
        {
            if ( currentSearchResultEntry != null )
            {
                resultEntryCounter++;
                StudioSearchResult ssr = new StudioSearchResult( currentSearchResultEntry, connection, false, null );
                return ssr;
            }

            // Are we following referrals manually?
            if ( referralsHandlingMethod == ReferralHandlingMethod.FOLLOW_MANUALLY )
            {
                // Checking the current referral's URLs list
                if ( ( currentReferralUrlsList != null ) && ( currentReferralUrlsList.size() > 0 ) )
                {
                    resultEntryCounter++;
                    // Building an LDAP URL from the the url
                    LdapUrl url = new LdapUrl( currentReferralUrlsList.remove( 0 ) );

                    // Building the search result
                    SearchResultEntry sre = new SearchResultEntryImpl();
                    sre.setEntry( new DefaultEntry() );
                    sre.setObjectName( url.getDn() );

                    return new StudioSearchResult( sre, null, false, url );
                }
            }
            // Are we following referrals automatically?
            else if ( referralsHandlingMethod == ReferralHandlingMethod.FOLLOW )
            {
                resultEntryCounter++;
                return new StudioSearchResult( referralEnumeration.next().getSearchResultEntry(), connection,
                    true, null );
            }

            return null;
        }
        catch ( Exception e )
        {
            throw new LdapException( e.getMessage() );
        }
    }


    /**
     * Gets the connection.
     * 
     * @return the connection
     */
    public Connection getConnection()
    {
        return connection;
    }


    /**
     * Gets the response controls.
     * 
     * @return the response controls, may be null
     */
    public Collection<Control> getResponseControls()
    {
        if ( searchResultDone != null )
        {
            Map<String, Control> controlsMap = searchResultDone
                .getControls();
            if ( ( controlsMap != null ) && ( controlsMap.size() > 0 ) )
            {
                return controlsMap.values();
            }
        }

        return Collections.emptyList();
    }

}
