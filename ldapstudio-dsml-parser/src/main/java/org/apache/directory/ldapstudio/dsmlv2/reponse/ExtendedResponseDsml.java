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

import org.apache.directory.ldapstudio.dsmlv2.ParserUtils;
import org.apache.directory.shared.ldap.codec.LdapMessage;
import org.apache.directory.shared.ldap.codec.extended.ExtendedResponse;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;


/**
 * DSML Decorator for ExtendedResponse
 */
public class ExtendedResponseDsml extends LdapResponseDecorator implements DsmlDecorator
{
    /**
     * Default constructor
     * @param ldapMessage the message to decorate
     */
    public ExtendedResponseDsml( LdapMessage ldapMessage )
    {
        super( ldapMessage );
    }


    /**
     * Get the message type
     * 
     * @return Returns the type.
     */
    public int getMessageType()
    {
        return instance.getExtendedResponse().getMessageType();
    }


    /**
     * Convert the request to its XML representation in the DSMLv2 format.
     * @param root the root dom4j Element
     * @return the dom4j Element corresponding to the entry.
     */
    public Element toDsml( Element root )
    {
        Element element = root.addElement( "extendedResponse" );
        ExtendedResponse extendedResponse = instance.getExtendedResponse();

        // LDAP Result
        LdapResultDsml ldapResultDsml = new LdapResultDsml( extendedResponse.getLdapResult(), instance );
        ldapResultDsml.toDsml( element );
        
        // ResponseName
        String responseName = extendedResponse.getResponseName();
        if ( responseName != null )
        {
            element.addElement( "responseName").addText( responseName );
        }
        
        // Response
        Object response = extendedResponse.getResponse();
        if ( response != null )
        {
            if ( ParserUtils.needsBase64Encoding( response ) )
            {
                Namespace xsdNamespace = new Namespace( "xsd", ParserUtils.XML_SCHEMA_URI );
                Namespace xsiNamespace = new Namespace( "xsi", ParserUtils.XML_SCHEMA_INSTANCE_URI );
                element.getDocument().getRootElement().add( xsdNamespace );
                element.getDocument().getRootElement().add( xsiNamespace );
                
                Element responseElement = element.addElement( "response").addText( ParserUtils.base64Encode( response ) );
                responseElement.addAttribute( new QName("type", xsiNamespace), "xsd:" + ParserUtils.BASE64BINARY );
            }
            else
            {
                element.addElement( "response").addText( response.toString() );
            }
        }
        
        return element;
    }
}
