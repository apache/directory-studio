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
package org.apache.directory.studio.ldapservers;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.studio.ldapservers.model.LdapServer;
import org.apache.directory.studio.ldapservers.model.LdapServerAdapterExtension;
import org.apache.directory.studio.ldapservers.model.UnknownLdapServerAdapterExtension;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;


/**
 * This class is used to read/write the 'servers.xml' file.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdapServersManagerIO
{
    // XML tags and attributes
    private static final String LDAP_SERVERS_TAG = "ldapServers"; //$NON-NLS-1$
    private static final String LDAP_SERVER_TAG = "ldapServer"; //$NON-NLS-1$
    private static final String ID_ATTRIBUTE = "id"; //$NON-NLS-1$
    private static final String NAME_ATTRIBUTE = "name"; //$NON-NLS-1$
    private static final String ADAPTER_ID_ATTRIBUTE = "adapterId"; //$NON-NLS-1$
    private static final String ADAPTER_NAME_ATTRIBUTE = "adapterName"; //$NON-NLS-1$
    private static final String ADAPTER_VENDOR_ATTRIBUTE = "adapterVendor"; //$NON-NLS-1$
    private static final String ADAPTER_VERSION_ATTRIBUTE = "adapterVersion"; //$NON-NLS-1$


    /**
     * Reads the given input stream.
     *
     * @param stream
     *      the input stream
     * @return
     *      the list of LDAP Servers found in the input stream
     * @throws LdapServersManagerIOException
     */
    public static List<LdapServer> read( InputStream stream ) throws LdapServersManagerIOException
    {
        List<LdapServer> servers = new ArrayList<LdapServer>();

        SAXReader saxReader = new SAXReader();
        Document document = null;

        try
        {
            document = saxReader.read( stream );
        }
        catch ( DocumentException e )
        {
            throw new LdapServersManagerIOException( e.getMessage() );
        }

        Element rootElement = document.getRootElement();
        if ( !rootElement.getName().equals( LDAP_SERVERS_TAG ) )
        {
            throw new LdapServersManagerIOException(
                Messages.getString( "LdapServersManagerIO.ErrorNotValidServersFile" ) ); //$NON-NLS-1$
        }

        for ( Iterator<?> i = rootElement.elementIterator( LDAP_SERVER_TAG ); i.hasNext(); )
        {
            servers.add( readLdapServer( ( Element ) i.next() ) );
        }

        return servers;
    }


    /**
     * Reads an LDAP Server element.
     *
     * @param element
     *      the element
     * @return
     *      the corresponding {@link LdapServer}
     */
    private static LdapServer readLdapServer( Element element )
    {
        LdapServer server = new LdapServer();

        // ID
        Attribute idAttribute = element.attribute( ID_ATTRIBUTE );
        if ( idAttribute != null )
        {
            server.setId( idAttribute.getValue() );
        }

        // Name
        Attribute nameAttribute = element.attribute( NAME_ATTRIBUTE );
        if ( nameAttribute != null )
        {
            server.setName( nameAttribute.getValue() );
        }

        // Adapter ID
        Attribute adapterIdAttribute = element.attribute( ADAPTER_ID_ATTRIBUTE );
        if ( adapterIdAttribute != null )
        {
            // Getting the id
            String adapterId = adapterIdAttribute.getValue();

            // Looking for the correct LDAP Server Adapter Extension object
            LdapServerAdapterExtension ldapServerAdapterExtension = LdapServerAdapterExtensionsManager.getDefault()
                .getLdapServerAdapterExtensionById( adapterId );
            if ( ldapServerAdapterExtension != null )
            {
                // The Adapter Extension has been found
                // Assigning it to the server
                server.setLdapServerAdapterExtension( ldapServerAdapterExtension );
            }
            else
            {
                // The Adapter Extension has not been found
                // Creating an "unknown" Adapter Extension
                UnknownLdapServerAdapterExtension unknownLdapServerAdapterExtension = new UnknownLdapServerAdapterExtension();

                // Adapter Id
                unknownLdapServerAdapterExtension.setId( adapterId );

                // Adapter Name
                Attribute adapterNameAttribute = element.attribute( ADAPTER_NAME_ATTRIBUTE );
                if ( adapterNameAttribute != null )
                {
                    unknownLdapServerAdapterExtension.setName( adapterNameAttribute.getValue() );
                }

                // Adapter Vendor
                Attribute adapterVendorAttribute = element.attribute( ADAPTER_VENDOR_ATTRIBUTE );
                if ( adapterVendorAttribute != null )
                {
                    unknownLdapServerAdapterExtension.setVendor( adapterVendorAttribute.getValue() );
                }

                // Adapter Version
                Attribute adapterVersionAttribute = element.attribute( ADAPTER_VERSION_ATTRIBUTE );
                if ( adapterVersionAttribute != null )
                {
                    unknownLdapServerAdapterExtension.setVersion( adapterVersionAttribute.getValue() );
                }

                // Assigning the "unknown" Adapter Extension to the server
                server.setLdapServerAdapterExtension( unknownLdapServerAdapterExtension );
            }
        }
        else
        {
            // TODO No Adapter ID, throw an error ?
        }

        return server;
    }


    /**
     * Writes the list of servers to the given stream.
     *
     * @param servers
     *      the servers
     * @param outputStream
     *      the output stream
     * @throws IOException
     *      if an error occurs when writing to the stream
     */
    public static void write( List<LdapServer> servers, OutputStream outputStream ) throws IOException
    {
        // Creating the Document
        Document document = DocumentHelper.createDocument();

        // Creating the root element
        Element root = document.addElement( LDAP_SERVERS_TAG );

        if ( servers != null )
        {
            for ( LdapServer server : servers )
            {
                addLdapServer( server, root );
            }
        }

        // Writing the file to the stream
        OutputFormat outformat = OutputFormat.createPrettyPrint();
        outformat.setEncoding( "UTF-8" ); //$NON-NLS-1$
        XMLWriter writer = new XMLWriter( outputStream, outformat );
        writer.write( document );
        writer.flush();
    }


    /**
     * Adds the XML representation of the LDAP Server to the given parent. 
     *
     * @param server
     *      the server
     * @param parent
     *      the parent element
     */
    private static void addLdapServer( LdapServer server, Element parent )
    {
        // Server element
        Element serverElement = parent.addElement( LDAP_SERVER_TAG );

        // ID
        serverElement.addAttribute( ID_ATTRIBUTE, server.getId() );

        // Name
        serverElement.addAttribute( NAME_ATTRIBUTE, server.getName() );

        // Adapter ID
        serverElement.addAttribute( ADAPTER_ID_ATTRIBUTE, server.getLdapServerAdapterExtension().getId() );

        // Adapter Name
        serverElement.addAttribute( ADAPTER_NAME_ATTRIBUTE, server.getLdapServerAdapterExtension().getName() );

        // Adapter Vendor
        serverElement.addAttribute( ADAPTER_VENDOR_ATTRIBUTE, server.getLdapServerAdapterExtension().getVendor() );

        // Adapter Version
        serverElement.addAttribute( ADAPTER_VERSION_ATTRIBUTE, server.getLdapServerAdapterExtension().getVersion() );
    }
}
