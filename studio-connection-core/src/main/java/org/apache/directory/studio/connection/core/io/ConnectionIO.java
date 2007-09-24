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
package org.apache.directory.studio.connection.core.io;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.directory.studio.connection.core.ConnectionFolder;
import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.apache.directory.studio.connection.core.ConnectionParameter.AuthenticationMethod;
import org.apache.directory.studio.connection.core.ConnectionParameter.EncryptionMethod;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;


/**
 * This class is used to read/write the 'connections.xml' file.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ConnectionIO
{
    // XML tags
    private static final String CONNECTIONS_TAG = "connections";
    private static final String CONNECTION_TAG = "connection";
    private static final String ID_TAG = "id";
    private static final String NAME_TAG = "name";
    private static final String HOST_TAG = "host";
    private static final String PORT_TAG = "port";
    private static final String ENCRYPTION_METHOD_TAG = "encryptionMethod";
    private static final String AUTH_METHOD_TAG = "authMethod";
    private static final String BIND_PRINCIPAL_TAG = "bindPrincipal";
    private static final String BIND_PASSWORD_TAG = "bindPassword";
    private static final String EXTENDED_PROPERTIES_TAG = "extendedProperties";
    private static final String EXTENDED_PROPERTY_TAG = "extendedProperty";
    private static final String KEY_TAG = "key";
    private static final String VALUE_TAG = "value";

    private static final String CONNECTION_FOLDERS_TAG = "connectionFolders";
    private static final String CONNECTION_FOLDER_TAG = "connectionFolder";
    private static final String SUB_FOLDERS_TAG = "subFolders";
    private static final String SUB_FOLDER_TAG = "subFolder";

    /**
     * Loads the connections using the reader
     *
     * @param stream
     *      the FileInputStream
     * @return
     *      the connections
     * @throws ConnectionIOException 
     *      if an error occurs when converting the document
     */
    public static List<ConnectionParameter> load( FileInputStream stream ) throws ConnectionIOException
    {
        List<ConnectionParameter> connections = new ArrayList<ConnectionParameter>();

        SAXReader saxReader = new SAXReader();
        Document document = null;

        try
        {
            document = saxReader.read( stream );
        }
        catch ( DocumentException e )
        {
            throw new ConnectionIOException( e.getMessage() );
        }

        Element rootElement = document.getRootElement();
        if ( !rootElement.getName().equals( CONNECTIONS_TAG ) )
        {
            throw new ConnectionIOException( "The file does not seem to be a valid Connections file." );
        }

        for ( Iterator<?> i = rootElement.elementIterator( CONNECTION_TAG ); i.hasNext(); )
        {
            Element connectionElement = ( Element ) i.next();
            connections.add( readConnection( connectionElement ) );
        }

        return connections;
    }


    /**
     * Reads a connection from the given Element.
     *
     * @param element
     *      the element
     * @return
     *      the corresponding connection
     * @throws ConnectionIOException
     *      if an error occurs when converting values
     */
    private static ConnectionParameter readConnection( Element element ) throws ConnectionIOException
    {
        ConnectionParameter connection = new ConnectionParameter();

        // ID
        Attribute idAttribute = element.attribute( ID_TAG );
        if ( idAttribute != null )
        {
            connection.setId( idAttribute.getValue() );
        }

        // Name
        Attribute nameAttribute = element.attribute( NAME_TAG );
        if ( nameAttribute != null )
        {
            connection.setName( nameAttribute.getValue() );
        }

        // Host        
        Attribute hostAttribute = element.attribute( HOST_TAG );
        if ( hostAttribute != null )
        {
            connection.setHost( hostAttribute.getValue() );
        }

        // Port
        Attribute portAttribute = element.attribute( PORT_TAG );
        if ( portAttribute != null )
        {
            try
            {
                connection.setPort( Integer.parseInt( portAttribute.getValue() ) );
            }
            catch ( NumberFormatException e )
            {
                throw new ConnectionIOException( "Unable to parse 'Port' of connection '" + connection.getName()
                    + "' as int value. Port value :" + portAttribute.getValue() );
            }
        }

        // Encryption Method
        Attribute encryptionMethodAttribute = element.attribute( ENCRYPTION_METHOD_TAG );
        if ( encryptionMethodAttribute != null )
        {
            try
            {
                connection.setEncryptionMethod( EncryptionMethod.valueOf( encryptionMethodAttribute.getValue() ) );
            }
            catch ( IllegalArgumentException e )
            {
                throw new ConnectionIOException( "Unable to parse 'Encryption Method' of connection '"
                    + connection.getName() + "' as int value. Encryption Method value :"
                    + encryptionMethodAttribute.getValue() );
            }
        }

        // Auth Method
        Attribute authMethodAttribute = element.attribute( AUTH_METHOD_TAG );
        if ( authMethodAttribute != null )
        {
            try
            {
                connection.setAuthMethod( AuthenticationMethod.valueOf( authMethodAttribute.getValue() ) );
            }
            catch ( IllegalArgumentException e )
            {
                throw new ConnectionIOException( "Unable to parse 'Authentication Method' of connection '"
                    + connection.getName() + "' as int value. Authentication Method value :"
                    + authMethodAttribute.getValue() );
            }
        }

        // Bind Principal        
        Attribute bindPrincipalAttribute = element.attribute( BIND_PRINCIPAL_TAG );
        if ( bindPrincipalAttribute != null )
        {
            connection.setBindPrincipal( bindPrincipalAttribute.getValue() );
        }

        // Bind Password
        Attribute bindPasswordAttribute = element.attribute( BIND_PASSWORD_TAG );
        if ( bindPasswordAttribute != null )
        {
            connection.setBindPassword( bindPasswordAttribute.getValue() );
        }

        // Extended Properties
        Element extendedPropertiesElement = element.element( EXTENDED_PROPERTIES_TAG );
        if ( extendedPropertiesElement != null )
        {
            for ( Iterator<?> i = extendedPropertiesElement.elementIterator( EXTENDED_PROPERTY_TAG ); i.hasNext(); )
            {
                Element extendedPropertyElement = ( Element ) i.next();

                Attribute keyAttribute = extendedPropertyElement.attribute( KEY_TAG );
                Attribute valueAttribute = extendedPropertyElement.attribute( VALUE_TAG );

                if ( keyAttribute != null && valueAttribute != null )
                {
                    connection.setExtendedProperty( keyAttribute.getValue(), valueAttribute.getValue() );
                }
            }
        }

        return connection;
    }


    /**
     * Saves the connections using the writer.
     *
     * @param connections
     *      the connections
     * @param stream
     *      the OutputStream
     * @throws IOException
     *      if an I/O error occurs
     */
    public static void save( List<ConnectionParameter> connections, FileOutputStream stream ) throws IOException
    {
        // Creating the Document
        Document document = DocumentHelper.createDocument();

        // Creating the root element
        Element root = document.addElement( CONNECTIONS_TAG );

        if ( connections != null )
        {
            for ( ConnectionParameter connection : connections )
            {
                addConnection( root, connection );
            }
        }

        // Writing the file to disk
        OutputFormat outformat = OutputFormat.createPrettyPrint();
        outformat.setEncoding( "UTF-8" );
        XMLWriter writer = new XMLWriter( stream, outformat );
        writer.write( document );
        writer.flush();
    }


    /**
     * Adds the given connection to the given parent Element.
     *
     * @param parent
     *      the parent Element
     * @param connection
     *      the connection
     */
    private static void addConnection( Element parent, ConnectionParameter connection )
    {
        Element connectionElement = parent.addElement( CONNECTION_TAG );

        // ID
        connectionElement.addAttribute( ID_TAG, connection.getId() );

        // Name
        connectionElement.addAttribute( NAME_TAG, connection.getName() );

        // Host
        connectionElement.addAttribute( HOST_TAG, connection.getHost() );

        // Port
        connectionElement.addAttribute( PORT_TAG, "" + connection.getPort() );

        // Encryption Method
        connectionElement.addAttribute( ENCRYPTION_METHOD_TAG, connection.getEncryptionMethod().toString() );

        // Auth Method
        connectionElement.addAttribute( AUTH_METHOD_TAG, connection.getAuthMethod().toString() );

        // Bind Principal
        connectionElement.addAttribute( BIND_PRINCIPAL_TAG, connection.getBindPrincipal() );

        // Bind Password
        connectionElement.addAttribute( BIND_PASSWORD_TAG, connection.getBindPassword() );

        // Extended Properties
        Element extendedPropertiesElement = connectionElement.addElement( EXTENDED_PROPERTIES_TAG );
        Map<String, String> extendedProperties = connection.getExtendedProperties();
        if ( extendedProperties != null )
        {
            for ( Iterator<Entry<String, String>> iter = extendedProperties.entrySet().iterator(); iter.hasNext(); )
            {
                Map.Entry<String, String> element = ( Map.Entry<String, String> ) iter.next();

                Element extendedPropertyElement = extendedPropertiesElement.addElement( EXTENDED_PROPERTY_TAG );
                extendedPropertyElement.addAttribute( KEY_TAG, element.getKey() );
                extendedPropertyElement.addAttribute( VALUE_TAG, element.getValue() );
            }
        }
    }


    /**
     * Loads the connection folders using the reader
     *
     * @param stream
     *      the FileInputStream
     * @return
     *      the connection folders
     * @throws ConnectionIOException 
     *      if an error occurs when converting the document
     */
    public static List<ConnectionFolder> loadConnectionFolders( FileInputStream stream ) throws ConnectionIOException
    {
        List<ConnectionFolder> connectionFolders = new ArrayList<ConnectionFolder>();

        SAXReader saxReader = new SAXReader();
        Document document = null;

        try
        {
            document = saxReader.read( stream );
        }
        catch ( DocumentException e )
        {
            throw new ConnectionIOException( e.getMessage() );
        }

        Element rootElement = document.getRootElement();
        if ( !rootElement.getName().equals( CONNECTION_FOLDERS_TAG ) )
        {
            throw new ConnectionIOException( "The file does not seem to be a valid ConnectionFolders file." );
        }

        for ( Iterator<?> i = rootElement.elementIterator( CONNECTION_FOLDER_TAG ); i.hasNext(); )
        {
            Element connectionFolderElement = ( Element ) i.next();
            connectionFolders.add( readConnectionFolder( connectionFolderElement ) );
        }

        return connectionFolders;
    }


    /**
     * Reads a connection folder from the given Element.
     *
     * @param element
     *      the element
     * @return
     *      the corresponding connection folder
     * @throws ConnectionIOException
     *      if an error occurs when converting values
     */
    private static ConnectionFolder readConnectionFolder( Element element ) throws ConnectionIOException
    {
        ConnectionFolder connectionFolder = new ConnectionFolder();

        // ID
        Attribute idAttribute = element.attribute( ID_TAG );
        if ( idAttribute != null )
        {
            connectionFolder.setId( idAttribute.getValue() );
        }

        // Name
        Attribute nameAttribute = element.attribute( NAME_TAG );
        if ( nameAttribute != null )
        {
            connectionFolder.setName( nameAttribute.getValue() );
        }

        // Connections
        Element connectionsElement = element.element( CONNECTIONS_TAG );
        if ( connectionsElement != null )
        {
            for ( Iterator<?> i = connectionsElement.elementIterator( CONNECTION_TAG ); i.hasNext(); )
            {
                Element connectionElement = ( Element ) i.next();

                Attribute connectionIdAttribute = connectionElement.attribute( ID_TAG );

                if ( connectionIdAttribute != null )
                {
                    connectionFolder.addConnectionId( connectionIdAttribute.getValue() );
                }
            }
        }

        // Sub-folders
        Element foldersElement = element.element( SUB_FOLDERS_TAG );
        if ( foldersElement != null )
        {
            for ( Iterator<?> i = foldersElement.elementIterator( SUB_FOLDER_TAG ); i.hasNext(); )
            {
                Element folderElement = ( Element ) i.next();
                
                Attribute folderIdAttribute = folderElement.attribute( ID_TAG );
                
                if ( folderIdAttribute != null )
                {
                    connectionFolder.addSubFolderId( folderIdAttribute.getValue() );
                }
            }
        }

        return connectionFolder;
    }


    /**
     * Saves the connection folders using the writer.
     *
     * @param connectionFolders
     *      the connection folders
     * @param stream
     *      the OutputStream
     * @throws IOException
     *      if an I/O error occurs
     */
    public static void saveConnectionFolders( List<ConnectionFolder> connectionFolders, FileOutputStream stream ) throws IOException
    {
        // Creating the Document
        Document document = DocumentHelper.createDocument();

        // Creating the root element
        Element root = document.addElement( CONNECTION_FOLDERS_TAG );

        if ( connectionFolders != null )
        {
            for ( ConnectionFolder connectionFolder : connectionFolders )
            {
                addFolderConnection( root, connectionFolder );
            }
        }

        // Writing the file to disk
        OutputFormat outformat = OutputFormat.createPrettyPrint();
        outformat.setEncoding( "UTF-8" );
        XMLWriter writer = new XMLWriter( stream, outformat );
        writer.write( document );
        writer.flush();
    }


    /**
     * Adds the given connection folder to the given parent Element.
     *
     * @param parent
     *      the parent Element
     * @param connectionFolder
     *      the connection folder
     */
    private static void addFolderConnection( Element parent, ConnectionFolder connectionFolder )
    {
        Element connectionFolderElement = parent.addElement( CONNECTION_FOLDER_TAG );

        // ID
        connectionFolderElement.addAttribute( ID_TAG, connectionFolder.getId() );

        // Name
        connectionFolderElement.addAttribute( NAME_TAG, connectionFolder.getName() );

        // Connections
        Element connectionsElement = connectionFolderElement.addElement( CONNECTIONS_TAG );
        for ( String connectionId : connectionFolder.getConnectionIds() )
        {
            Element connectionElement = connectionsElement.addElement( CONNECTION_TAG );
            connectionElement.addAttribute( ID_TAG, connectionId );
        }
        
        // Sub-folders
        Element foldersElement = connectionFolderElement.addElement( SUB_FOLDERS_TAG );
        for ( String folderId : connectionFolder.getSubFolderIds() )
        {
            Element folderElement = foldersElement.addElement( SUB_FOLDER_TAG );
            folderElement.addAttribute( ID_TAG, folderId );
        }
    }

}
