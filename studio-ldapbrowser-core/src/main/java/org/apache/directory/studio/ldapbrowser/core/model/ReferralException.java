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

package org.apache.directory.studio.ldapbrowser.core.model;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Search;


public class ReferralException extends ConnectionException
{

    private static final long serialVersionUID = 1L;

    private SearchParameter originalSearchParameter;

    private String[] referrals;


    public ReferralException( SearchParameter originalSearchParameter, String[] referrals, int ldapStatusCode,
        String message, Throwable originalThrowable )
    {
        super( ldapStatusCode, message, originalThrowable );
        this.originalSearchParameter = originalSearchParameter;
        this.referrals = referrals;
    }


    public String[] getReferrals()
    {
        return referrals;
    }


    public ISearch[] getReferralSearches() throws ConnectionException
    {

        // get referral handler
        IReferralHandler referralHandler = BrowserCorePlugin.getDefault().getReferralHandler();
        if ( referralHandler == null )
        {
            throw new ConnectionException( BrowserCoreMessages.model__no_referral_handler );
        }

        List referralSearchList = new ArrayList( getReferrals().length );

        for ( int i = 0; i < getReferrals().length; i++ )
        {

            // parse referral URL
            String referral = getReferrals()[i];
            URL referralUrl = new URL( referral );

            // get referral connection
            IBrowserConnection referralConnection = referralHandler.getReferralConnection( referralUrl );
            if ( referralConnection == null )
            {
                // throw new
                // ConnectionException(BrowserCoreMessages.model__no_referral_connection);
                continue;
            }

            // create search
            try
            {
                ISearch referralSearch = new Search(
                    null, //
                    referralConnection, //
                    referralUrl.hasDn() ? referralUrl.getDn() : originalSearchParameter.getSearchBase(), referralUrl
                        .hasFilter() ? referralUrl.getFilter() : originalSearchParameter.getFilter(),
                    originalSearchParameter.getReturningAttributes(), referralUrl.hasScope() ? referralUrl.getScope()
                        : originalSearchParameter.getScope(), originalSearchParameter.getCountLimit(),
                    originalSearchParameter.getTimeLimit(), originalSearchParameter.getAliasesDereferencingMethod(),
                    originalSearchParameter.getReferralsHandlingMethod(), originalSearchParameter.isInitHasChildrenFlag(),
                    originalSearchParameter.isInitAliasAndReferralFlag(), originalSearchParameter.getControls() );
                referralSearchList.add( referralSearch );
            }
            catch ( NoSuchFieldException nsfe )
            {
            }

        }

        ISearch[] referralSearches = ( ISearch[] ) referralSearchList.toArray( new ISearch[referralSearchList.size()] );
        return referralSearches;
    }

}
