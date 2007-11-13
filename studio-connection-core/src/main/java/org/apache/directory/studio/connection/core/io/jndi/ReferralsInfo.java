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
import javax.naming.NamingException;

import org.apache.directory.shared.ldap.codec.util.LdapURL;


/**
 * Helper class that holds info about referrals to be processed and
 * already processed referrals. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ReferralsInfo
{
    private List<LdapURL> referralsToProcess;

    private List<LdapURL> processedReferrals;


    /**
     * 
     * Creates a new instance of ReferralsInfo.
     */
    public ReferralsInfo()
    {
        this.referralsToProcess = new ArrayList<LdapURL>();
        this.processedReferrals = new ArrayList<LdapURL>();
    }


    /**
     * Adds the referral URL to the list of referrals to be processed.
     * 
     * If the URL is already in the list or if the URL was already processed
     * a NamingException will be thrown
     * 
     * @param url the URL
     * 
     * @throws NamingException the naming exception
     */
    public void addReferralUrl( LdapURL url ) throws NamingException
    {
        if ( !referralsToProcess.contains( url ) && !processedReferrals.contains( url ) )
        {
            referralsToProcess.add( url );
        }
        else
        {
            throw new LinkLoopException( "Loop detected: " + url.toString() );
        }
    }


    /**
     * Gets the next referral URL or null.
     * 
     * @return the next referral URL or null
     */
    public LdapURL getNext()
    {
        if ( !referralsToProcess.isEmpty() )
        {
            LdapURL url = referralsToProcess.remove( 0 );
            processedReferrals.add( url );
            return url;
        }
        else
        {
            return null;
        }
    }

}
