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

import javax.naming.LinkLoopException;

import org.apache.directory.shared.ldap.codec.util.LdapURL;
import org.apache.directory.shared.ldap.name.LdapDN;


/**
 * Helper class that holds info about referrals to be processed and
 * already processed referrals. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ReferralsInfo
{
    private List<UrlAndDn> referralsToProcess;

    private List<UrlAndDn> processedReferrals;


    /**
     * 
     * Creates a new instance of ReferralsInfo.
     */
    public ReferralsInfo()
    {
        this.referralsToProcess = new ArrayList<UrlAndDn>();
        this.processedReferrals = new ArrayList<UrlAndDn>();
    }


    /**
     * Adds the referral URL and DN to the list of referrals to be processed.
     * 
     * If the URL is already in the list or if the URL was already processed
     * a NamingException will be thrown
     * 
     * @param url the URL
     * @param dn the DN
     * 
     * @throws LinkLoopException if a loop was encountered.
     */
    public void addReferralUrl( LdapURL url, LdapDN dn ) throws LinkLoopException
    {
        UrlAndDn urlAndDn = new UrlAndDn( url, dn );
        if ( !referralsToProcess.contains( urlAndDn ) && !processedReferrals.contains( urlAndDn ) )
        {
            referralsToProcess.add( urlAndDn );
        }
        else
        {
            throw new LinkLoopException( "Loop detected while following referral: " + urlAndDn.toString() );
        }
    }


    /**
     * Gets the next referral URL or null.
     * 
     * @return the next referral URL or null
     */
    public UrlAndDn getNext()
    {
        if ( !referralsToProcess.isEmpty() )
        {
            UrlAndDn urlAndDn = referralsToProcess.remove( 0 );
            processedReferrals.add( urlAndDn );
            return urlAndDn;
        }
        else
        {
            return null;
        }
    }

    /**
     * Container for an LDAP URL and an LDAP DN.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    class UrlAndDn
    {
        private LdapURL url;
        private LdapDN dn;


        /**
         * Creates a new instance of UrlAndDn.
         *
         * @param url the URL, never null
         * @param dn the DN, never null
         */
        private UrlAndDn( LdapURL url, LdapDN dn )
        {
            if ( url == null )
            {
                throw new IllegalArgumentException( "URL may not be null" );
            }
            if ( dn == null )
            {
                throw new IllegalArgumentException( "DN may not be null" );
            }
            this.url = url;
            this.dn = dn;
        }


        /**
         * Gets the URL.
         * 
         * @return the URL
         */
        public LdapURL getUrl()
        {
            return url;
        }


        /**
         * Gets the DN.
         * 
         * @return the DN
         */
        public LdapDN getDn()
        {
            return dn;
        }


        /**
         * {@inheritDoc}
         */
        public int hashCode()
        {
            // dn and url are never null
            int h = 37;
            h = h * 17 + url.hashCode();
            h = h * 17 + dn.hashCode();
            return h;
        }


        /**
         * {@inheritDoc}
         */
        public boolean equals( Object obj )
        {
            // dn and url are never null
            if ( this == obj )
            {
                return true;
            }
            if ( !( obj instanceof UrlAndDn ) )
            {
                return false;
            }

            UrlAndDn other = ( UrlAndDn ) obj;
            return dn.equals( other.dn ) && url.equals( other.url );
        }

    }

}
