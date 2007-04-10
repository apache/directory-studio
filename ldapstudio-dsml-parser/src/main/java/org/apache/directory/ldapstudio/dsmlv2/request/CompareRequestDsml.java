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

import org.apache.directory.shared.ldap.codec.LdapMessage;
import org.apache.directory.shared.ldap.codec.compare.CompareRequest;
import org.dom4j.Element;

/**
 * DSML Decorator for CompareRequest
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class CompareRequestDsml extends AbstractRequestDsml
{
    /**
     * Creates a new instance of CompareRequestDsml.
     *
     * @param ldapMessage
     *      the message to decorate
     */
    public CompareRequestDsml( LdapMessage ldapMessage )
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
        
        CompareRequest request = ( CompareRequest ) instance;
        
        // DN
        if ( request.getEntry() != null )
        {
            element.addAttribute( "dn", request.getEntry().toString() );
        }
        
        // Assertion
        Element assertionElement = element.addElement( "assertion");
        if ( request.getAttributeDesc() != null )
        {
            assertionElement.addAttribute( "name", request.getAttributeDesc() );
        }
        if ( request.getAssertionValue() != null )
        {
            assertionElement.addElement( "value").setText( (String) request.getAssertionValue() );
        }
        
        return element;
    }
}
