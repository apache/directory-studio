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


import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.naming.LinkLoopException;

import org.apache.directory.shared.ldap.model.message.Referral;


/**
 * Helper class that holds info about referrals to be processed and
 * already processed referrals. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ReferralsInfo
{
    private LinkedList<Referral> referralsToProcess = new LinkedList<Referral>();

    private Set<String> processedUrls = new HashSet<String>();


    /**
     * Adds the referral entry to the list of referrals to be processed.
     * 
     * If the URLs are already in the list or if the URL was already processed
     * a NamingException will be thrown
     * 
     * @param referral the referral
     * 
     * @throws LinkLoopException if a loop was encountered.
     */
    public void addReferral( Referral referral )
    {
        referralsToProcess.addLast( referral );
    }


    /**
     * Gets the next referral URL or null.
     * 
     * @return the next referral URL or null
     */
    public Referral getNextReferral()
    {
        if ( !referralsToProcess.isEmpty() )
        {
            Referral referral = referralsToProcess.removeFirst();
            for ( String url : referral.getLdapUrls() )
            {
                processedUrls.add( url );
            }
            return referral;
        }
        else
        {
            return null;
        }
    }


    /**
     * Checks for more referrals.
     * 
     * @return true, if there are more referrals
     */
    public boolean hasMoreReferrals()
    {
        return !referralsToProcess.isEmpty();
    }
}
