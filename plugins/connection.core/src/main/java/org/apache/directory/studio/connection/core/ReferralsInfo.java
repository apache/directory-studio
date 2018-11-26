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
package org.apache.directory.studio.connection.core;


import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.apache.directory.api.ldap.model.exception.LdapLoopDetectedException;
import org.apache.directory.api.ldap.model.message.Referral;


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

    private boolean throwExceptionOnLoop;


    /**
     * Creates a new instance of ReferralsInfo.
     *
     * @param throwExceptionOnLoop if an exception should be thrown when a referral loop is detected.
     */
    public ReferralsInfo( boolean throwExceptionOnLoop )
    {
        this.throwExceptionOnLoop = throwExceptionOnLoop;
    }


    /**
     * Adds the referral entry to the list of referrals to be processed.
     * 
     * @param referral the referral
     */
    public void addReferral( Referral referral )
    {
        referralsToProcess.addLast( referral );
    }


    /**
     * Gets the next referral or null.
     * 
     * @return the next referral or null
     * @throws LdapLoopDetectedException 
     */
    public Referral getNextReferral() throws LdapLoopDetectedException
    {
        handleAlreadyProcessedUrls();
        if ( !referralsToProcess.isEmpty() )
        {
            Referral referral = referralsToProcess.removeFirst();
            processedUrls.addAll( referral.getLdapUrls() );
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
     * @throws LdapLoLinkLoopExceptionopDetectedException 
     */
    public boolean hasMoreReferrals() throws LdapLoopDetectedException
    {
        handleAlreadyProcessedUrls();
        return !referralsToProcess.isEmpty();
    }


    private void handleAlreadyProcessedUrls() throws LdapLoopDetectedException
    {
        while ( !referralsToProcess.isEmpty() )
        {
            Referral referral = referralsToProcess.getFirst();
            boolean alreadyProcessed = referral.getLdapUrls().stream().anyMatch( url -> processedUrls.contains( url ) );
            if ( alreadyProcessed )
            {
                // yes, already processed, remove the current referral and continue with filtering
                if ( throwExceptionOnLoop )
                {
                    throw new LdapLoopDetectedException( "Referral " + referral.getLdapUrls() + " already processed" );
                }
                else
                {
                    referralsToProcess.removeFirst();
                }
            }
            else
            {
                // no, not yet processed, done with filtering
                return;
            }
        }
    }

}
