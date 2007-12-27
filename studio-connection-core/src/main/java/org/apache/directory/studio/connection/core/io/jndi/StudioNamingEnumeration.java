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


import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.PartialResultException;
import javax.naming.ReferralException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;

import org.apache.directory.shared.ldap.codec.util.LdapURL;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;


/**
 * A naming enumeration that handles referrals itself. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class StudioNamingEnumeration implements NamingEnumeration<SearchResult>
{
    private final Connection connection;
    private NamingEnumeration<SearchResult> delegate;

    private String searchBase;
    private String filter;
    private SearchControls searchControls;
    private String aliasesDereferencingMethod;
    private String referralsHandlingMethod;
    private Control[] controls;
    private StudioProgressMonitor monitor;
    private ReferralsInfo referralsInfo;


    /**
     * Creates a new instance of ReferralNamingEnumeration.
     * 
     * @param connection the connection
     * @param delegate the delegate
     * @param searchBase the search base
     * @param filter the filter
     * @param searchControls the search controls
     * @param aliasesDereferencingMethod the aliases dereferencing method
     * @param referralsHandlingMethod the referrals handling method
     * @param controls the LDAP controls
     * @param monitor the progress monitor
     * @param referralsInfo the referrals info
     */
    public StudioNamingEnumeration( Connection connection, NamingEnumeration<SearchResult> delegate, String searchBase,
        String filter, SearchControls searchControls, String aliasesDereferencingMethod,
        String referralsHandlingMethod, Control[] controls, StudioProgressMonitor monitor, ReferralsInfo referralsInfo )
    {
        this.connection = connection;
        this.delegate = delegate;

        this.searchBase = searchBase;
        this.filter = filter;
        this.searchControls = searchControls;
        this.aliasesDereferencingMethod = aliasesDereferencingMethod;
        this.referralsHandlingMethod = referralsHandlingMethod;
        this.controls = controls;
        this.monitor = monitor;
        this.referralsInfo = referralsInfo;
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
        while ( true )
        {
            try
            {
                return delegate.hasMore();
            }
            catch ( PartialResultException pre )
            {
                if ( JNDIConnectionWrapper.REFERRAL_IGNORE.equals( referralsHandlingMethod ) )
                {
                    // ignore
                    return false;
                }
                else
                {
                    throw pre;
                }
            }
            catch ( ReferralException re )
            {
                if ( JNDIConnectionWrapper.REFERRAL_IGNORE.equals( referralsHandlingMethod ) )
                {
                    // ignore
                    return false;
                }
                else if ( JNDIConnectionWrapper.REFERRAL_FOLLOW.equals( referralsHandlingMethod ) )
                {
                    referralsInfo = JNDIConnectionWrapper.handleReferralException( re, referralsInfo );
                    LdapURL url = referralsInfo.getNext();
                    if ( url != null )
                    {
                        Connection referralConnection = JNDIConnectionWrapper.getReferralConnection( url );
                        if ( referralConnection != null )
                        {
                            String referralSearchBase = url.getDn() != null && !url.getDn().isEmpty() ? url.getDn()
                                .getUpName() : searchBase;
                            String referralFilter = url.getFilter() != null && url.getFilter().length() == 0 ? url
                                .getFilter() : filter;
                            SearchControls referralSearchControls = new SearchControls();
                            referralSearchControls.setSearchScope( url.getScope() > -1 ? url.getScope()
                                : searchControls.getSearchScope() );
                            referralSearchControls.setReturningAttributes( url.getAttributes() != null ? url
                                .getAttributes().toArray( new String[url.getAttributes().size()] ) : searchControls
                                .getReturningAttributes() );
                            referralSearchControls.setCountLimit( searchControls.getCountLimit() );
                            referralSearchControls.setTimeLimit( searchControls.getTimeLimit() );
                            referralSearchControls.setDerefLinkFlag( searchControls.getDerefLinkFlag() );
                            referralSearchControls.setReturningObjFlag( searchControls.getReturningObjFlag() );

                            if ( referralConnection.getJNDIConnectionWrapper().isConnected() )
                            {
                                referralConnection.getJNDIConnectionWrapper().connect( monitor );
                                referralConnection.getJNDIConnectionWrapper().bind( monitor );
                                ConnectionEventRegistry.fireConnectionOpened( referralConnection, this );
                            }

                            delegate = referralConnection.getJNDIConnectionWrapper().search( referralSearchBase,
                                referralFilter, referralSearchControls, aliasesDereferencingMethod,
                                referralsHandlingMethod, controls, monitor, referralsInfo );
                        }
                    }
                }
                else
                {
                    throw re;
                }
            }
        }
    }


    /**
     * @see java.util.Enumeration#hasMoreElements()
     */
    public boolean hasMoreElements()
    {
        return delegate.hasMoreElements();
    }


    /**
     * @see javax.naming.NamingEnumeration#next()
     */
    public SearchResult next() throws NamingException
    {
        SearchResult searchResult = delegate.next();
        StudioSearchResult studioSearchResult = new StudioSearchResult( searchResult, getConnection(),
            referralsInfo != null );
        return studioSearchResult;
    }


    /**
     * @see java.util.Enumeration#nextElement()
     */
    public SearchResult nextElement()
    {
        SearchResult searchResult = delegate.nextElement();
        return new StudioSearchResult( searchResult, getConnection(), referralsInfo != null );
    }


    /**
     * Gets the connection.
     * 
     * @return the connection
     */
    public Connection getConnection()
    {
        if ( delegate instanceof StudioNamingEnumeration )
        {
            return ( ( StudioNamingEnumeration ) delegate ).getConnection();
        }
        else
        {
            return connection;
        }
    }

}
