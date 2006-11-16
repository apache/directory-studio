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


import org.apache.directory.shared.ldap.codec.LdapMessage;
import org.dom4j.Element;


/**
 * DSML Decorator for AuthResponse
 */
public class AuthResponseDsml extends LdapResponseDecorator implements DsmlDecorator
{
    /**
     * Default constructor
     * @param ldapMessage the message to decorate
     */
    public AuthResponseDsml( LdapMessage ldapMessage )
    {
        super( ldapMessage );
    }


    /**
     * Get the message type
     * @return Returns the type.
     */
    public int getMessageType()
    {
        return instance.getBindResponse().getMessageType();
    }


    /**
     * Convert the request to its XML representation in the DSMLv2 format.
     * @param root the root dom4j Element
     * @return the dom4j Element corresponding to the entry.
     */
    public Element toDsml( Element root )
    {
        Element element = root.addElement( "authResponse" );

        LdapResultDsml ldapResultDsml = new LdapResultDsml( instance.getBindResponse().getLdapResult(), instance );
        ldapResultDsml.toDsml( element );
        return element;
    }

}
