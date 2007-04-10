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


import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.apache.directory.ldapstudio.dsmlv2.ParserUtils;
import org.apache.directory.shared.ldap.codec.LdapMessage;
import org.apache.directory.shared.ldap.codec.add.AddRequest;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;


/**
 * DSML Decorator for AddRequest
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class AddRequestDsml extends AbstractRequestDsml
{
    /**
     * Creates a new instance of AddRequestDsml.
     *
     * @param ldapMessage
     *      the message to decorate
     */
    public AddRequestDsml( LdapMessage ldapMessage )
    {
        super( ldapMessage );
    }


    /**
     * {@inheritDoc}
     */
    public int getMessageType()
    {
        return instance.getMessageType();
    }


    /**
     * {@inheritDoc}
     */
    public Element toDsml( Element root )
    {
        Element element = super.toDsml( root );

        AddRequest request = ( AddRequest ) instance;

        // DN
        if ( request.getEntry() != null )
        {
            element.addAttribute( "dn", request.getEntry().toString() );
        }

        // Attributes
        Attributes attributes = request.getAttributes();
        if ( attributes != null )
        {
            NamingEnumeration ne = attributes.getAll();
            while ( ne.hasMoreElements() )
            {
                Attribute attribute = ( Attribute ) ne.nextElement();
                Element attributeElement = element.addElement( "attr" );
                attributeElement.addAttribute( "name", attribute.getID() );
    
                // Looping on Values Enumeration
                try
                {
                    NamingEnumeration ne2 = attribute.getAll();
    
                    while ( ne2.hasMoreElements() )
                    {
                        Object value = ne2.nextElement();
    
                        if ( ParserUtils.needsBase64Encoding( value ) )
                        {
                            Namespace xsdNamespace = new Namespace( "xsd", ParserUtils.XML_SCHEMA_URI );
                            Namespace xsiNamespace = new Namespace( "xsi", ParserUtils.XML_SCHEMA_INSTANCE_URI );
                            attributeElement.getDocument().getRootElement().add( xsdNamespace );
                            attributeElement.getDocument().getRootElement().add( xsiNamespace );
    
                            Element valueElement = attributeElement.addElement( "value" ).addText(
                                ParserUtils.base64Encode( value ) );
                            valueElement
                                .addAttribute( new QName( "type", xsiNamespace ), "xsd:" + ParserUtils.BASE64BINARY );
                        }
                        else
                        {
                            attributeElement.addElement( "value" ).addText( value.toString() );
                        }
                    }
                }
                catch ( NamingException e )
                {
                }
            }
        }

        return element;
    }
}
