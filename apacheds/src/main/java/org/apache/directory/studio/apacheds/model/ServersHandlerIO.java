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
    private static final String SERVER_INSTANCES_TAG = "serverInstances";
    private static final String SERVER_INSTANCE_TAG = "serverInstance";
    private static final String SERVER_ID_ATTRIBUTE = "id";
    private static final String SERVER_NAME_ATTRIBUTE = "name";


    /**
     * Reads the given input stream.
     *
     * @param stream
     *      the input stream
     * @return
     *      
     * @throws ServersHandlerIOException
     */
    public static List<ServerInstance> read( InputStream stream ) throws ServersHandlerIOException
    {
        List<ServerInstance> serverInstances = new ArrayList<ServerInstance>();

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
        if ( !rootElement.getName().equals( SERVER_INSTANCES_TAG ) )
        {
            throw new ServersHandlerIOException( "The file does not seem to be a valid servers file." );
        }

        for ( Iterator<?> i = rootElement.elementIterator( SERVER_INSTANCE_TAG ); i.hasNext(); )
        {
            serverInstances.add( readServerInstance( ( Element ) i.next() ) );
        }

        return serverInstances;
    }


    /**
     * Reads a server instance element.
     *
     * @param element
     *      the element
     * @return
     *      the corresponding {@link ServerInstance}
     */
    private static ServerInstance readServerInstance( Element element )
    {
        ServerInstance serverInstance = new ServerInstance();

        // ID
        Attribute idAttribute = element.attribute( SERVER_ID_ATTRIBUTE );
        if ( idAttribute != null )
        {
            serverInstance.setId( idAttribute.getValue() );
        }

        // Name
        Attribute nameAttribute = element.attribute( SERVER_NAME_ATTRIBUTE );
        if ( nameAttribute != null )
        {
            serverInstance.setName( nameAttribute.getValue() );
        }

        return serverInstance;
    }


    /**
     * Writes the list of server instances to the given stream.
     *
     * @param serverInstances
     *      the server instances
     * @param outputStream
     *      the output stream
     * @throws IOException
     *      if an error occurs when writing to the stream
     */
    public static void write( List<ServerInstance> serverInstances, OutputStream outputStream ) throws IOException
    {
        // Creating the Document
        Document document = DocumentHelper.createDocument();

        // Creating the root element
        Element root = document.addElement( SERVER_INSTANCES_TAG );

        if ( serverInstances != null )
        {
            for ( ServerInstance serverInstance : serverInstances )
            {
                addServerInstance( serverInstance, root );
            }
        }

        // Writing the file to the stream
        OutputFormat outformat = OutputFormat.createPrettyPrint();
        outformat.setEncoding( "UTF-8" );
        XMLWriter writer = new XMLWriter( outputStream, outformat );
        writer.write( document );
        writer.flush();
    }


    /**
     * Adds the XML representation of the server instance to the given parent. 
     *
     * @param serverInstance
     *      the server instance
     * @param parent
     *      the parent element
     */
    private static void addServerInstance( ServerInstance serverInstance, Element parent )
    {
        // Server instance element
        Element serverInstanceElement = parent.addElement( SERVER_INSTANCE_TAG );

        // ID
        serverInstanceElement.addAttribute( SERVER_ID_ATTRIBUTE, serverInstance.getId() );

        // Name
        serverInstanceElement.addAttribute( SERVER_NAME_ATTRIBUTE, serverInstance.getName() );
    }
}
