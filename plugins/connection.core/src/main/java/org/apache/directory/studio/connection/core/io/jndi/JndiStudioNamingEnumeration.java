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
package org.apache.directory.studio.connection.core.io.jndi;


import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.PartialResultException;
import javax.naming.ReferralException;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.LdapContext;

import org.apache.directory.shared.ldap.codec.util.LdapURLEncodingException;
import org.apache.directory.shared.ldap.message.Referral;
import org.apache.directory.shared.ldap.util.LdapURL;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.connection.core.io.AbstractStudioNamingEnumeration;
import org.apache.directory.studio.connection.core.io.ConnectionWrapperUtils;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.IJndiLogger;


/**
 * A naming enumeration that handles referrals itself. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class JndiStudioNamingEnumeration extends AbstractStudioNamingEnumeration
{
    private final LdapContext ctx;
    private NamingEnumeration<SearchResult> initialNamingEnumeration;
    private NamingEnumeration<SearchResult> delegate;
    private NamingException initialReferralException;

    private long requestNum;
    private long resultEntryCounter;


    /**
     * Creates a new instance of ReferralNamingEnumeration.
     * 
     * @param connection the connection
     * @param ctx the JNDI context
     * @param searchBase the search base
     * @param filter the filter
     * @param searchControls the search controls
     * @param aliasesDereferencingMethod the aliases dereferencing method
     * @param referralsHandlingMethod the referrals handling method
     * @param controls the LDAP controls
     * @param monitor the progress monitor
     * @param referralsInfo the referrals info
     */
    public JndiStudioNamingEnumeration( Connection connection, LdapContext ctx,
        NamingEnumeration<SearchResult> delegate,
        NamingException initialReferralException, String searchBase, String filter, SearchControls searchControls,
        AliasDereferencingMethod aliasesDereferencingMethod, ReferralHandlingMethod referralsHandlingMethod,
        Control[] controls, long requestNum, StudioProgressMonitor monitor, ReferralsInfo referralsInfo )
    {
        super( connection, searchBase, filter, searchControls, aliasesDereferencingMethod, referralsHandlingMethod,
            controls, requestNum, monitor, referralsInfo );
        this.ctx = ctx;
        this.initialNamingEnumeration = delegate;
        this.delegate = delegate;
        this.initialReferralException = initialReferralException;
        this.requestNum = requestNum;
        this.resultEntryCounter = 0;
    }


    /**
     * @see javax.naming.NamingEnumeration#close()
     */
    public void close() throws NamingException
    {
        delegate.close();
    }


    /**
     * @see javax.naming.NamingEnumeration#hasMore()
     */
    public boolean hasMore() throws NamingException
    {
        NamingException logResultDoneException = null;
        boolean done = false;

        while ( true )
        {
            try
            {
                if ( initialReferralException != null )
                {
                    NamingException referralException = initialReferralException;
                    initialReferralException = null;
                    throw referralException;
                }

                boolean hasMore = delegate != null && delegate.hasMore();
                if ( !hasMore && !done && referralsInfo != null && referralsInfo.hasMoreReferrals() )
                {
                    done = checkReferral();
                }
                else
                {
                    done = !hasMore;
                    return hasMore;
                }
            }
            catch ( PartialResultException pre )
            {
                done = true;
                logResultDoneException = pre;

                // ignore exception if referrals handling method is IGNORE
                // report exception if referrals handling method is FOLLOW or MANGAGE
                if ( referralsHandlingMethod == ReferralHandlingMethod.IGNORE )
                {
                    return false;
                }
                else
                {
                    throw pre;
                }
            }
            catch ( ReferralException re )
            {
                done = true;
                logResultDoneException = re;
                referralsInfo = JNDIConnectionWrapper.handleReferralException( re, referralsInfo );
                if ( referralsInfo.hasMoreReferrals() )
                {
                    logResultDoneException = null;
                    done = checkReferral();
                }
            }
            catch ( NamingException ne )
            {
                done = true;
                logResultDoneException = ne;
                throw ne;
            }
            finally
            {
                if ( done )
                {
                    for ( IJndiLogger logger : ConnectionCorePlugin.getDefault().getJndiLoggers() )
                    {
                        logger.logSearchResultDone( connection, resultEntryCounter, requestNum, logResultDoneException );
                    }
                }
            }
        }
    }


    /**
     * @see java.util.Enumeration#hasMoreElements()
     */
    public boolean hasMoreElements()
    {
        throw new UnsupportedOperationException( "Call hasMore() instead of hasMoreElements() !" );
    }


    /**
     * @see javax.naming.NamingEnumeration#next()
     */
    public StudioSearchResult next() throws NamingException
    {
        StudioSearchResult studioSearchResult = null;
        NamingException namingException = null;
        try
        {
            SearchResult searchResult = delegate.next();
            resultEntryCounter++;
            if ( searchResult instanceof StudioSearchResult )
            {
                studioSearchResult = ( StudioSearchResult ) searchResult;
            }
            else
            {
                studioSearchResult = new StudioSearchResult( searchResult, getConnection(), referralsInfo != null, null );
            }
            return studioSearchResult;
        }
        catch ( NamingException ne )
        {
            namingException = ne;
            throw ne;
        }
        finally
        {
            if ( delegate == initialNamingEnumeration )
            {
                for ( IJndiLogger logger : ConnectionCorePlugin.getDefault().getJndiLoggers() )
                {
                    logger.logSearchResultEntry( connection, studioSearchResult, requestNum, namingException );
                }
            }
        }
    }


    /**
     * @see java.util.Enumeration#nextElement()
     */
    public StudioSearchResult nextElement()
    {
        throw new UnsupportedOperationException( "Call next() instead of nextElement() !" );
    }


    /**
     * Gets the connection.
     * 
     * @return the connection
     */
    public Connection getConnection()
    {
        if ( delegate instanceof JndiStudioNamingEnumeration )
        {
            return ( ( JndiStudioNamingEnumeration ) delegate ).getConnection();
        }
        else
        {
            return connection;
        }
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
        return ctx != null ? ctx.getResponseControls() : null;
    }


    private boolean checkReferral()
    {
        try
        {
            boolean done = false;

            // ignore exception if referrals handling method is IGNORE
            // follow referral if referrals handling method is FOLLOW
            // follow manually if referrals handling method is FOLLOW_MANUALLY
            if ( referralsHandlingMethod == ReferralHandlingMethod.IGNORE )
            {
                done = true;
                delegate = null;
            }
            else if ( referralsHandlingMethod == ReferralHandlingMethod.FOLLOW_MANUALLY )
            {
                delegate = new NamingEnumeration<SearchResult>()
                {

                    List<String> urls = new ArrayList<String>();
                    {
                        while ( referralsInfo.hasMoreReferrals() )
                    {
                        Referral referral = referralsInfo.getNextReferral();
                        for ( IJndiLogger logger : ConnectionCorePlugin.getDefault().getJndiLoggers() )
                        {
                            logger.logSearchResultReference( connection, referral, referralsInfo, requestNum, null );
                        }
                        urls.addAll( referral.getLdapUrls() );
                    }
                }


                    public SearchResult nextElement()
                {
                    throw new UnsupportedOperationException( "Call next() instead of nextElement() !" );
                }


                    public boolean hasMoreElements()
                {
                    throw new UnsupportedOperationException( "Call hasMore() instead of hasMoreElements() !" );
                }


                    public SearchResult next() throws NamingException
                {
                    try
                    {
                        LdapURL url = new LdapURL( urls.remove( 0 ) );
                        SearchResult searchResult = new SearchResult( url.getDn().getName(), null,
                            new BasicAttributes(),
                            false );
                        searchResult.setNameInNamespace( url.getDn().getName() );
                        StudioSearchResult ssr = new StudioSearchResult( searchResult, null, false, url );
                        return ssr;
                    }
                    catch ( LdapURLEncodingException e )
                    {
                        throw new NamingException( e.getMessage() );
                    }
                }


                    public boolean hasMore() throws NamingException
                {
                    return !urls.isEmpty();
                }


                    public void close() throws NamingException
                {
                    urls.clear();
                    referralsInfo = null;
                }
                };
            }
            else if ( referralsHandlingMethod == ReferralHandlingMethod.FOLLOW )
            {
                Referral referral = referralsInfo.getNextReferral();
                for ( IJndiLogger logger : ConnectionCorePlugin.getDefault().getJndiLoggers() )
                {
                    logger.logSearchResultReference( connection, referral, referralsInfo, requestNum, null );
                }
                List<String> urls = new ArrayList<String>( referral.getLdapUrls() );
                LdapURL url = new LdapURL( urls.get( 0 ) );
                Connection referralConnection = ConnectionWrapperUtils.getReferralConnection( referral, monitor, this );
                if ( referralConnection != null )
                {
                    done = false;
                    String referralSearchBase = url.getDn() != null && !url.getDn().isEmpty() ? url.getDn().getName()
                        : searchBase;
                    String referralFilter = url.getFilter() != null && url.getFilter().length() == 0 ? url.getFilter()
                        : filter;
                    SearchControls referralSearchControls = new SearchControls();
                    referralSearchControls.setSearchScope( url.getScope().getScope() > -1 ? url.getScope().getScope()
                        : searchControls
                            .getSearchScope() );
                    referralSearchControls.setReturningAttributes( url.getAttributes() != null
                        && url.getAttributes().size() > 0 ? url.getAttributes().toArray(
                        new String[url.getAttributes().size()] ) : searchControls.getReturningAttributes() );
                    referralSearchControls.setCountLimit( searchControls.getCountLimit() );
                    referralSearchControls.setTimeLimit( searchControls.getTimeLimit() );
                    referralSearchControls.setDerefLinkFlag( searchControls.getDerefLinkFlag() );
                    referralSearchControls.setReturningObjFlag( searchControls.getReturningObjFlag() );

                    delegate = referralConnection.getConnectionWrapper().search( referralSearchBase, referralFilter,
                        referralSearchControls, aliasesDereferencingMethod, referralsHandlingMethod, controls, monitor,
                        referralsInfo );
                }
                else
                {
                    done = true;
                    delegate = null;
                }
            }
            return done;
        }
        catch ( LdapURLEncodingException e )
        {
            return false;
        }
    }

}
