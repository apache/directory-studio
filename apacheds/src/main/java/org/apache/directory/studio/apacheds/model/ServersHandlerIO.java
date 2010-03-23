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
package org.apache.directory.studio.apacheds.model;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
 * @version $Rev$, $Date$
 */
public class ServersHandlerIO
{
    // XML tags and attributes
    private static final String SERVERS_TAG = "servers"; //$NON-NLS-1$
    private static final String SERVER_TAG = "server"; //$NON-NLS-1$
    private static final String SERVER_ID_ATTRIBUTE = "id"; //$NON-NLS-1$
    private static final String SERVER_NAME_ATTRIBUTE = "name"; //$NON-NLS-1$
    private static final String SERVER_VERSION_ATTRIBUTE = "version"; //$NON-NLS-1$


    /**
     * Reads the given input stream.
     *
     * @param stream
     *      the input stream
     * @return
     *      
     * @throws ServersHandlerIOException
     */
    public static List<Server> read( InputStream stream ) throws ServersHandlerIOException
    {
        List<Server> servers = new ArrayList<Server>();

        SAXReader saxReader = new SAXReader();
        Document document = null;

        try
        {
            document = saxReader.read( stream );
        }
        catch ( DocumentException e )
        {
            throw new ServersHandlerIOException( e.getMessage() );
        }

        Element rootElement = document.getRootElement();
        if ( !rootElement.getName().equals( SERVERS_TAG ) )
        {
            throw new ServersHandlerIOException( Messages.getString( "ServersHandlerIO.ErrorNotValidServersFile" ) ); //$NON-NLS-1$
        }

        for ( Iterator<?> i = rootElement.elementIterator( SERVER_TAG ); i.hasNext(); )
        {
            servers.add( readServer( ( Element ) i.next() ) );
        }

        return servers;
    }


    /**
     * Reads a server element.
     *
     * @param element
     *      the element
     * @return
     *      the corresponding {@link Server}
     */
    private static Server readServer( Element element )
    {
        Server server = new Server();

        // ID
        Attribute idAttribute = element.attribute( SERVER_ID_ATTRIBUTE );
        if ( idAttribute != null )
        {
            server.setId( idAttribute.getValue() );
        }

        // Name
        Attribute nameAttribute = element.attribute( SERVER_NAME_ATTRIBUTE );
        if ( nameAttribute != null )
        {
            server.setName( nameAttribute.getValue() );
        }

        // Version
        Attribute versionAttribute = element.attribute( SERVER_VERSION_ATTRIBUTE );
        if ( versionAttribute != null )
        {
            if ( versionAttribute.getValue().equalsIgnoreCase( "1.5.6" ) ) //$NON-NLS-1$
            {
                server.setVersion( ServerVersion.VERSION_1_5_6 );
            }
            else if ( versionAttribute.getValue().equalsIgnoreCase( "1.5.5" ) ) //$NON-NLS-1$
            {
                server.setVersion( ServerVersion.VERSION_1_5_5 );
            }
            else if ( versionAttribute.getValue().equalsIgnoreCase( "1.5.4" ) ) //$NON-NLS-1$
            {
                server.setVersion( ServerVersion.VERSION_1_5_4 );
            }
            else if ( versionAttribute.getValue().equalsIgnoreCase( "1.5.3" ) ) //$NON-NLS-1$
            {
                server.setVersion( ServerVersion.VERSION_1_5_3 );
            }
            // <!> Compatibility mode <!>
            // if the server does not have a version attribute, this means it's an
            // Apache DS 1.5.3 server
            else
            {
                server.setVersion( ServerVersion.VERSION_1_5_3 );
            }
        }
        // <!> Compatibility mode <!>
        // if the server does not have a version attribute, this means it's an
        // Apache DS 1.5.3 server
        else
        {
            server.setVersion( ServerVersion.VERSION_1_5_3 );
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
    public static void write( List<Server> servers, OutputStream outputStream ) throws IOException
    {
        // Creating the Document
        Document document = DocumentHelper.createDocument();

        // Creating the root element
        Element root = document.addElement( SERVERS_TAG );

        if ( servers != null )
        {
            for ( Server server : servers )
            {
                addServer( server, root );
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
     * Adds the XML representation of the server to the given parent. 
     *
     * @param server
     *      the server
     * @param parent
     *      the parent element
     */
    private static void addServer( Server server, Element parent )
    {
        // Server element
        Element serverElement = parent.addElement( SERVER_TAG );

        // ID
        serverElement.addAttribute( SERVER_ID_ATTRIBUTE, server.getId() );

        // Name
        serverElement.addAttribute( SERVER_NAME_ATTRIBUTE, server.getName() );

        // Version
        serverElement.addAttribute( SERVER_VERSION_ATTRIBUTE, server.getVersion().toString() );
    }
}
