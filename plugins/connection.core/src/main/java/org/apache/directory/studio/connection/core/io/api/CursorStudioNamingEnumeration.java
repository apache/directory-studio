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


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.BasicControl;
import javax.naming.ldap.Control;
import javax.naming.ldap.PagedResultsResponseControl;

import org.apache.directory.api.ldap.codec.api.CodecControl;
import org.apache.directory.api.ldap.codec.api.LdapApiService;
import org.apache.directory.api.ldap.codec.api.LdapApiServiceFactory;
import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.SearchCursor;
import org.apache.directory.api.ldap.model.entry.AttributeUtils;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.Referral;
import org.apache.directory.api.ldap.model.message.Response;
import org.apache.directory.api.ldap.model.message.SearchResultDone;
import org.apache.directory.api.ldap.model.message.SearchResultEntry;
import org.apache.directory.api.ldap.model.message.SearchResultReference;
import org.apache.directory.api.ldap.model.url.LdapUrl;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.IJndiLogger;
import org.apache.directory.studio.connection.core.io.AbstractStudioNamingEnumeration;
import org.apache.directory.studio.connection.core.io.ConnectionWrapperUtils;
import org.apache.directory.studio.connection.core.io.StudioNamingEnumeration;
import org.apache.directory.studio.connection.core.io.jndi.ReferralsInfo;
import org.apache.directory.studio.connection.core.io.jndi.StudioSearchResult;


/**
 * A naming enumeration that handles referrals itself. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CursorStudioNamingEnumeration extends AbstractStudioNamingEnumeration
{
    private SearchCursor cursor;
    private SearchResultEntry currentSearchResultEntry;
    private List<String> currentReferralUrlsList;
    private StudioNamingEnumeration cursorNamingEnumeration;
    private SearchResultDone searchResultDone;

    // @TODO: By Alex: temporary fix until things are in order (needs to be fixed)
    private LdapApiService codec = LdapApiServiceFactory.getSingleton();


    /**
     * Creates a new instance of ReferralNamingEnumeration.
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
    public CursorStudioNamingEnumeration( Connection connection, SearchCursor cursor, String searchBase, String filter,
        SearchControls searchControls, AliasDereferencingMethod aliasesDereferencingMethod,
        ReferralHandlingMethod referralsHandlingMethod, Control[] controls, long requestNum,
        StudioProgressMonitor monitor, ReferralsInfo referralsInfo )
    {
        super( connection, searchBase, filter, searchControls, aliasesDereferencingMethod, referralsHandlingMethod,
            controls, requestNum, monitor, referralsInfo );
        this.connection = connection;
        this.cursor = cursor;

        this.searchBase = searchBase;
        this.filter = filter;
        this.searchControls = searchControls;
        this.aliasesDereferencingMethod = aliasesDereferencingMethod;
        this.referralsHandlingMethod = referralsHandlingMethod;
        this.controls = controls;
        this.monitor = monitor;

        if ( super.referralsInfo == null )
        {
            super.referralsInfo = new ReferralsInfo( false );
        }
    }


    /**
     * @see javax.naming.NamingEnumeration#close()
     */
    public void close() throws NamingException
    {
        try
        {
            cursor.close();
        }
        catch ( Exception e )
        {
            throw new NamingException( e.getMessage() );
        }
    }


    /**
     * @see javax.naming.NamingEnumeration#hasMore()
     */
    public boolean hasMore() throws NamingException
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
                if ( ( cursorNamingEnumeration != null ) && ( cursorNamingEnumeration.hasMore() ) )
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
                            ? url.getDn().getName() : searchBase;
                        String referralFilter = url.getFilter() != null && url.getFilter().length() == 0
                            ? url.getFilter() : filter;
                        SearchControls referralSearchControls = new SearchControls();
                        referralSearchControls.setSearchScope( url.getScope().getScope() > -1
                            ? url.getScope().getScope() : searchControls.getSearchScope() );
                        referralSearchControls
                            .setReturningAttributes( url.getAttributes() != null && url.getAttributes().size() > 0
                                ? url.getAttributes().toArray( new String[url.getAttributes().size()] )
                                : searchControls.getReturningAttributes() );
                        referralSearchControls.setCountLimit( searchControls.getCountLimit() );
                        referralSearchControls.setTimeLimit( searchControls.getTimeLimit() );
                        referralSearchControls.setDerefLinkFlag( searchControls.getDerefLinkFlag() );
                        referralSearchControls.setReturningObjFlag( searchControls.getReturningObjFlag() );

                        cursorNamingEnumeration = referralConnection.getConnectionWrapper().search( referralSearchBase,
                            referralFilter, referralSearchControls, aliasesDereferencingMethod, referralsHandlingMethod,
                            controls, monitor, referralsInfo );

                        return cursorNamingEnumeration.hasMore();
                    }
                }
            }

            for ( IJndiLogger logger : ConnectionCorePlugin.getDefault().getJndiLoggers() )
            {
                logger.logSearchResultDone( connection, resultEntryCounter, requestNum, null );
            }

            return false;
        }
        catch ( CursorException | LdapException e )
        {
            throw new NamingException( e.getMessage() );
        }
    }


    /**
     * @see java.util.Enumeration#hasMoreElements()
     */
    public boolean hasMoreElements()
    {
        throw new UnsupportedOperationException( "Call hasMore() instead of hasMoreElements() !" ); //$NON-NLS-1$
    }


    /**
     * @see javax.naming.NamingEnumeration#next()
     */
    public StudioSearchResult next() throws NamingException
    {
        try
        {
            if ( currentSearchResultEntry != null )
            {
                resultEntryCounter++;
                SearchResult sr = new SearchResult( currentSearchResultEntry.getObjectName().toString(), null,
                    AttributeUtils.toAttributes( currentSearchResultEntry.getEntry() ) );
                sr.setNameInNamespace( currentSearchResultEntry.getObjectName().toString() );

                // Converting the SearchResult to a StudioSearchResult
                StudioSearchResult ssr = new StudioSearchResult( sr, connection, false, null );
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
                    SearchResult searchResult = new SearchResult( url.getDn().getName(), null, new BasicAttributes(),
                        false );
                    searchResult.setNameInNamespace( url.getDn().getName() );

                    return new StudioSearchResult( searchResult, null, false, url );
                }
            }
            // Are we following referrals automatically?
            else if ( referralsHandlingMethod == ReferralHandlingMethod.FOLLOW )
            {
                resultEntryCounter++;
                return new StudioSearchResult( cursorNamingEnumeration.next(), connection, true, null );
            }

            return null;
        }
        catch ( Exception e )
        {
            throw new NamingException( e.getMessage() );
        }
    }


    /**
     * @see java.util.Enumeration#nextElement()
     */
    public StudioSearchResult nextElement()
    {
        throw new UnsupportedOperationException( "Call next() instead of nextElement() !" ); //$NON-NLS-1$
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
     * 
     * @throws NamingException the naming exception
     */
    public Control[] getResponseControls() throws NamingException
    {
        if ( searchResultDone != null )
        {
            Map<String, org.apache.directory.api.ldap.model.message.Control> controlsMap = searchResultDone
                .getControls();
            if ( ( controlsMap != null ) && ( controlsMap.size() > 0 ) )
            {
                return convertControls( controlsMap.values() );
            }
        }

        return new Control[0];
    }


    /**
     * Converts the controls.
     *
     * @param controls
     *      an array of controls
     * @return
     *      an array of converted controls
     */
    private Control[] convertControls( Collection<org.apache.directory.api.ldap.model.message.Control> controls )
    {
        if ( controls != null )
        {
            List<Control> convertedControls = new ArrayList<Control>();

            for ( org.apache.directory.api.ldap.model.message.Control control : controls )
            {
                Control convertedControl = null;
                CodecControl<? extends org.apache.directory.api.ldap.model.message.Control> wrapped = null;

                if ( control instanceof CodecControl )
                {
                    wrapped = ( org.apache.directory.api.ldap.codec.api.CodecControl<?> ) control;
                }
                else
                {
                    wrapped = codec.newControl( control );
                }

                if ( PagedResultsResponseControl.OID.equals( control.getOid() ) )
                {
                    // Special case for the PagedResultsResponseControl
                    try
                    {
                        convertedControl = new PagedResultsResponseControl( wrapped.getOid(), wrapped.isCritical(),
                            wrapped.getValue() );
                    }
                    catch ( IOException e )
                    {
                        convertedControl = new BasicControl( wrapped.getOid(), wrapped.isCritical(),
                            wrapped.getValue() );
                    }
                }
                else
                {
                    // Default case
                    convertedControl = new BasicControl( wrapped.getOid(), wrapped.isCritical(), wrapped.getValue() );
                }

                convertedControls.add( convertedControl );
            }

            return convertedControls.toArray( new Control[0] );
        }
        else
        {
            return new Control[0];
        }
    }
}
