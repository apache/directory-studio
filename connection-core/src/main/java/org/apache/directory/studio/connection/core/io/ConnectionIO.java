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


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.directory.studio.connection.core.ConnectionFolder;
import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.apache.directory.studio.connection.core.ConnectionParameter.AuthenticationMethod;
import org.apache.directory.studio.connection.core.ConnectionParameter.EncryptionMethod;
import org.apache.directory.studio.connection.core.ConnectionParameter.Krb5Configuration;
import org.apache.directory.studio.connection.core.ConnectionParameter.Krb5CredentialConfiguration;
import org.apache.directory.studio.connection.core.ConnectionParameter.SaslQop;
import org.apache.directory.studio.connection.core.ConnectionParameter.SaslSecurityStrength;
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
    private static final String CONNECTIONS_TAG = "connections"; //$NON-NLS-1$
    private static final String CONNECTION_TAG = "connection"; //$NON-NLS-1$
    private static final String ID_TAG = "id"; //$NON-NLS-1$
    private static final String NAME_TAG = "name"; //$NON-NLS-1$
    private static final String HOST_TAG = "host"; //$NON-NLS-1$
    private static final String PORT_TAG = "port"; //$NON-NLS-1$
    private static final String ENCRYPTION_METHOD_TAG = "encryptionMethod"; //$NON-NLS-1$
    private static final String AUTH_METHOD_TAG = "authMethod"; //$NON-NLS-1$
    private static final String BIND_PRINCIPAL_TAG = "bindPrincipal"; //$NON-NLS-1$
    private static final String BIND_PASSWORD_TAG = "bindPassword"; //$NON-NLS-1$
    private static final String SASL_REALM_TAG = "saslRealm"; //$NON-NLS-1$
    private static final String SASL_QOP_TAG = "saslQop"; //$NON-NLS-1$
    private static final String SASL_SEC_STRENGTH_TAG = "saslSecStrenght"; //$NON-NLS-1$
    private static final String SASL_MUTUAL_AUTH_TAG = "saslMutualAuth"; //$NON-NLS-1$
    private static final String KRB5_CREDENTIALS_CONF_TAG = "krb5CredentialsConf"; //$NON-NLS-1$
    private static final String KRB5_CONFIG_TAG = "krb5Config"; //$NON-NLS-1$
    private static final String KRB5_CONFIG_FILE_TAG = "krb5ConfigFile"; //$NON-NLS-1$
    private static final String KRB5_REALM_TAG = "krb5Realm"; //$NON-NLS-1$
    private static final String KRB5_KDC_HOST_TAG = "krb5KdcHost"; //$NON-NLS-1$
    private static final String KRB5_KDC_PORT_TAG = "krb5KdcPort"; //$NON-NLS-1$

    private static final String EXTENDED_PROPERTIES_TAG = "extendedProperties"; //$NON-NLS-1$
    private static final String EXTENDED_PROPERTY_TAG = "extendedProperty"; //$NON-NLS-1$
    private static final String KEY_TAG = "key"; //$NON-NLS-1$
    private static final String VALUE_TAG = "value"; //$NON-NLS-1$

    private static final String CONNECTION_FOLDERS_TAG = "connectionFolders"; //$NON-NLS-1$
    private static final String CONNECTION_FOLDER_TAG = "connectionFolder"; //$NON-NLS-1$
    private static final String SUB_FOLDERS_TAG = "subFolders"; //$NON-NLS-1$
    private static final String SUB_FOLDER_TAG = "subFolder"; //$NON-NLS-1$


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
    public static Set<ConnectionParameter> load( InputStream stream ) throws ConnectionIOException
    {
        Set<ConnectionParameter> connections = new HashSet<ConnectionParameter>();

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
            throw new ConnectionIOException( "The file does not seem to be a valid Connections file." ); //$NON-NLS-1$
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
                throw new ConnectionIOException( "Unable to parse 'Port' of connection '" + connection.getName() //$NON-NLS-1$
                    + "' as int value. Port value :" + portAttribute.getValue() ); //$NON-NLS-1$
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
                throw new ConnectionIOException( "Unable to parse 'Encryption Method' of connection '" //$NON-NLS-1$
                    + connection.getName() + "' as int value. Encryption Method value :" //$NON-NLS-1$
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
                throw new ConnectionIOException( "Unable to parse 'Authentication Method' of connection '" //$NON-NLS-1$
                    + connection.getName() + "' as int value. Authentication Method value :" //$NON-NLS-1$
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

        // SASL Realm
        Attribute saslRealmAttribute = element.attribute( SASL_REALM_TAG );
        if ( saslRealmAttribute != null )
        {
            connection.setSaslRealm( saslRealmAttribute.getValue() );
        }

        // SASL Quality of Protection
        Attribute saslQopAttribute = element.attribute( SASL_QOP_TAG );
        if ( saslQopAttribute != null )
        {
            connection.setSaslQop( SaslQop.valueOf( saslQopAttribute.getValue() ) );
        }

        // SASL Security Strength
        Attribute saslSecStrengthAttribute = element.attribute( SASL_SEC_STRENGTH_TAG );
        if ( saslSecStrengthAttribute != null )
        {
            connection.setSaslSecurityStrength( SaslSecurityStrength.valueOf( saslSecStrengthAttribute.getValue() ) );
        }

        // SASL Mutual Authentication
        Attribute saslMutualAuthAttribute = element.attribute( SASL_MUTUAL_AUTH_TAG );
        if ( saslMutualAuthAttribute != null )
        {
            connection.setSaslMutualAuthentication( Boolean.parseBoolean( saslMutualAuthAttribute.getValue() ) );
        }

        // KRB5 Credentials Conf
        Attribute krb5CredentialsConf = element.attribute( KRB5_CREDENTIALS_CONF_TAG );
        if ( krb5CredentialsConf != null )
        {
            connection.setKrb5CredentialConfiguration( Krb5CredentialConfiguration.valueOf( krb5CredentialsConf
                .getValue() ) );
        }

        // KRB5 Configuration
        Attribute krb5Config = element.attribute( KRB5_CONFIG_TAG );
        if ( krb5Config != null )
        {
            connection.setKrb5Configuration( Krb5Configuration.valueOf( krb5Config.getValue() ) );
        }

        // KRB5 Configuration File
        Attribute krb5ConfigFile = element.attribute( KRB5_CONFIG_FILE_TAG );
        if ( krb5ConfigFile != null )
        {
            connection.setKrb5ConfigurationFile( krb5ConfigFile.getValue() );
        }

        // KRB5 REALM
        Attribute krb5Realm = element.attribute( KRB5_REALM_TAG );
        if ( krb5Realm != null )
        {
            connection.setKrb5Realm( krb5Realm.getValue() );
        }

        // KRB5 KDC Host
        Attribute krb5KdcHost = element.attribute( KRB5_KDC_HOST_TAG );
        if ( krb5KdcHost != null )
        {
            connection.setKrb5KdcHost( krb5KdcHost.getValue() );
        }

        // KRB5 KDC Port
        Attribute krb5KdcPort = element.attribute( KRB5_KDC_PORT_TAG );
        if ( krb5KdcPort != null )
        {
            connection.setKrb5KdcPort( Integer.valueOf( krb5KdcPort.getValue() ) );
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
    public static void save( Set<ConnectionParameter> connections, OutputStream stream ) throws IOException
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
        outformat.setEncoding( "UTF-8" ); //$NON-NLS-1$
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
        connectionElement.addAttribute( PORT_TAG, "" + connection.getPort() ); //$NON-NLS-1$

        // Encryption Method
        connectionElement.addAttribute( ENCRYPTION_METHOD_TAG, connection.getEncryptionMethod().toString() );

        // Auth Method
        connectionElement.addAttribute( AUTH_METHOD_TAG, connection.getAuthMethod().toString() );

        // Bind Principal
        connectionElement.addAttribute( BIND_PRINCIPAL_TAG, connection.getBindPrincipal() );

        // Bind Password
        connectionElement.addAttribute( BIND_PASSWORD_TAG, connection.getBindPassword() );

        // SASL Realm
        connectionElement.addAttribute( SASL_REALM_TAG, connection.getSaslRealm() );

        // SASL Quality of Protection
        connectionElement.addAttribute( SASL_QOP_TAG, connection.getSaslQop().toString() );
        
        // SASL Security Strength
        connectionElement.addAttribute( SASL_SEC_STRENGTH_TAG, connection.getSaslSecurityStrength().toString() );

        // SASL Mutual Authentication
        connectionElement.addAttribute( SASL_MUTUAL_AUTH_TAG, "" + connection.isSaslMutualAuthentication() ); //$NON-NLS-1$

        // KRB5 Credentials Conf
        connectionElement.addAttribute( KRB5_CREDENTIALS_CONF_TAG, connection.getKrb5CredentialConfiguration()
            .toString() );

        // KRB5 Configuration
        connectionElement.addAttribute( KRB5_CONFIG_TAG, connection.getKrb5Configuration().toString() );

        // KRB5 Configuration File
        connectionElement.addAttribute( KRB5_CONFIG_FILE_TAG, connection.getKrb5ConfigurationFile() );

        // KRB5 REALM
        connectionElement.addAttribute( KRB5_REALM_TAG, connection.getKrb5Realm() );

        // KRB5 KDC Host
        connectionElement.addAttribute( KRB5_KDC_HOST_TAG, connection.getKrb5KdcHost() );

        // KRB5 KDC Port
        connectionElement.addAttribute( KRB5_KDC_PORT_TAG, "" + connection.getKrb5KdcPort() ); //$NON-NLS-1$

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
    public static Set<ConnectionFolder> loadConnectionFolders( InputStream stream ) throws ConnectionIOException
    {
        Set<ConnectionFolder> connectionFolders = new HashSet<ConnectionFolder>();

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
            throw new ConnectionIOException( "The file does not seem to be a valid ConnectionFolders file." ); //$NON-NLS-1$
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
    public static void saveConnectionFolders( Set<ConnectionFolder> connectionFolders, OutputStream stream )
        throws IOException
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
        outformat.setEncoding( "UTF-8" ); //$NON-NLS-1$
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
