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

package org.apache.directory.ldapstudio.dsmlv2.reponse;


import java.util.ArrayList;

import org.apache.directory.shared.ldap.codec.LdapMessage;
import org.apache.directory.shared.ldap.codec.LdapResult;
import org.dom4j.Element;


/**
 * DSML Decorator for the LdapResult class.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class LdapResultDsml implements DsmlDecorator
{
    /** The LDAP Result to decorate */
    private LdapResult result;

    /** The associated LDAP Message */
    private LdapMessage message;


    /**
     * Creates a new instance of LdapResultDsml.
     *
     * @param result
     *      the LdapResult to decorate
     * @param message
     *      the associated message
     */
    public LdapResultDsml( LdapResult result, LdapMessage message )
    {
        this.result = result;
        this.message = message;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.dsmlv2.reponse.DsmlDecorator#toDsml(org.dom4j.Element)
     */
    public Element toDsml( Element root )
    {

        // RequestID
        int requestID = message.getMessageId();
        if ( requestID != 0 )
        {
            root.addAttribute( "requestID", "" + requestID );
        }

        // Matched DN
        String matchedDN = result.getMatchedDN();
        if ( !matchedDN.equals( "" ) )
        {
            root.addAttribute( "matchedDN", matchedDN );
        }

        // TODO Add Control values

        // ResultCode
        Element resultCodeElement = root.addElement( "resultCode" );
        resultCodeElement.addAttribute( "code", "" + result.getResultCode() );
        resultCodeElement.addAttribute( "descr", LdapResultEnum.getResultCodeDescr( result.getResultCode() ) );

        // ErrorMessage
        String errorMessage = ( result.getErrorMessage() );
        if ( ( errorMessage != null ) && ( !errorMessage.equals( "" ) ) )
        {
            Element errorMessageElement = root.addElement( "errorMessage" );
            errorMessageElement.addText( errorMessage );
        }

        // Referals
        ArrayList referals = result.getReferrals();
        if ( referals != null )
        {
            for ( int i = 0; i < referals.size(); i++ )
            {
                Element referalElement = root.addElement( "referal" );
                referalElement.addText( referals.get( i ).toString() );
            }
        }

        return root;
    }
}
