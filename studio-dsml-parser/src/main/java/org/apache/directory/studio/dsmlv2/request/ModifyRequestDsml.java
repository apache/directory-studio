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
package org.apache.directory.studio.dsmlv2.request;


import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;

import org.apache.directory.shared.ldap.codec.modify.ModifyRequest;
import org.apache.directory.studio.dsmlv2.ParserUtils;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;


/**
 * DSML Decorator for ModifyRequest
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ModifyRequestDsml extends AbstractRequestDsml
{
    /**
     * Creates a new instance of ModifyRequestDsml.
     */
    public ModifyRequestDsml()
    {
        super( new ModifyRequest() );
    }


    /**
     * Creates a new instance of ModifyRequestDsml.
     *
     * @param ldapMessage
     *      the message to decorate
     */
    public ModifyRequestDsml( ModifyRequest ldapMessage )
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

        ModifyRequest request = ( ModifyRequest ) instance;

        // DN
        if ( request.getObject() != null )
        {
            element.addAttribute( "dn", request.getObject().toString() );
        }

        // Modifications
        List<ModificationItem> modifications = request.getModifications();

        for ( int i = 0; i < modifications.size(); i++ )
        {
            ModificationItem modificationItem = modifications.get( i );

            Element modElement = element.addElement( "modification" );
            if ( modificationItem.getAttribute() != null )
            {
                modElement.addAttribute( "name", modificationItem.getAttribute().getID() );

                try
                {
                    NamingEnumeration ne = modificationItem.getAttribute().getAll();
                    while ( ne.hasMoreElements() )
                    {
                        Object value = ( Object ) ne.nextElement();

                        if ( value != null )
                        {
                            if ( ParserUtils.needsBase64Encoding( value ) )
                            {
                                Namespace xsdNamespace = new Namespace( "xsd", ParserUtils.XML_SCHEMA_URI );
                                Namespace xsiNamespace = new Namespace( "xsi", ParserUtils.XML_SCHEMA_INSTANCE_URI );
                                element.getDocument().getRootElement().add( xsdNamespace );
                                element.getDocument().getRootElement().add( xsiNamespace );

                                Element valueElement = modElement.addElement( "value" ).addText(
                                    ParserUtils.base64Encode( value ) );
                                valueElement.addAttribute( new QName( "type", xsiNamespace ), "xsd:"
                                    + ParserUtils.BASE64BINARY );
                            }
                            else
                            {
                                modElement.addElement( "value" ).setText( ( String ) value );
                            }
                        }
                    }
                }
                catch ( NamingException e )
                {
                }
            }

            int operation = modificationItem.getModificationOp();
            if ( operation == DirContext.ADD_ATTRIBUTE )
            {
                modElement.addAttribute( "operation", "add" );
            }
            else if ( operation == DirContext.REPLACE_ATTRIBUTE )
            {
                modElement.addAttribute( "operation", "replace" );
            }
            else if ( operation == DirContext.REMOVE_ATTRIBUTE )
            {
                modElement.addAttribute( "operation", "delete" );
            }
        }

        return element;
    }
}
