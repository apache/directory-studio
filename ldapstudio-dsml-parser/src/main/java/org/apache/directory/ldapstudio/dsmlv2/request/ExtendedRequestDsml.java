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
package org.apache.directory.ldapstudio.dsmlv2.request;

import org.apache.directory.ldapstudio.dsmlv2.ParserUtils;
import org.apache.directory.shared.ldap.codec.LdapMessage;
import org.apache.directory.shared.ldap.codec.extended.ExtendedRequest;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;

/**
 * DSML Decorator for ExtendedRequest
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ExtendedRequestDsml extends AbstractRequestDsml
{
    /**
     * Creates a new instance of ExtendedRequestDsml.
     *
     * @param ldapMessage
     *      the message to decorate
     */
    public ExtendedRequestDsml( LdapMessage ldapMessage )
    {
        super( ldapMessage );
    }
    
    
    /**
     * {@inheritDoc}
     */
    public int getMessageType()
    {
        return instance.getExtendedRequest().getMessageType();
    }

    
    /**
     * {@inheritDoc}
     */
    public Element toDsml( Element root )
    {
        Element element = super.toDsml( root );
        
        ExtendedRequest request = instance.getExtendedRequest();
        
        // Request Name
        if ( request.getRequestName() != null )
        {
            element.addElement( "requestName" ).setText( request.getRequestName() );
        }
        
        // Request Value        
        Namespace xsdNamespace = new Namespace( "xsd", ParserUtils.XML_SCHEMA_URI );
        Namespace xsiNamespace = new Namespace( "xsi", ParserUtils.XML_SCHEMA_INSTANCE_URI );
        element.getDocument().getRootElement().add( xsdNamespace );
        element.getDocument().getRootElement().add( xsiNamespace );

        Element valueElement = element.addElement( "requestValue" ).addText(
            ParserUtils.base64Encode( request.getRequestValue() ) );
        valueElement
            .addAttribute( new QName( "type", xsiNamespace ), "xsd:" + ParserUtils.BASE64BINARY );
        
        return element;
    }
}
