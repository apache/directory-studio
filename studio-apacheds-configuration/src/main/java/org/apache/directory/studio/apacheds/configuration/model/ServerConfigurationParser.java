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
package org.apache.directory.studio.apacheds.configuration.model;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;

import org.apache.directory.shared.ldap.ldif.LdifReader;
import org.apache.directory.shared.ldap.message.AttributesImpl;
import org.apache.directory.shared.ldap.util.StringTools;
import org.apache.directory.studio.apacheds.configuration.Activator;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;


/**
 * This class represents the Server Configuration Parser. It can be used to parse a 'server.xml' file 
 * and get Server Configuration Object from it.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ServerConfigurationParser
{
    /**
     * Parses a 'server.xml' file located at the given path and returns 
     * the corresponding ServerConfiguration Object.
     *
     * @param path
     *      the path of the file to parse
     * @return
     *      the corresponding ServerConfiguration Object
     * @throws ServerConfigurationParserException
     *      if an error occurrs when reading the Server Configuration file
     */
    public ServerConfiguration parse( String path ) throws ServerConfigurationParserException
    {
        try
        {
            EntityResolver resolver = new EntityResolver()
            {
                public InputSource resolveEntity( String publicId, String systemId )
                {
                    if ( publicId.equals( "-//SPRING//DTD BEAN//EN" ) )
                    {
                        InputStream in = Activator.class.getResourceAsStream( "spring-beans.dtd" );
                        return new InputSource( in );
                    }
                    return null;
                }
            };

            SAXReader reader = new SAXReader();
            reader.setEntityResolver( resolver );
            Document document = reader.read( path );

            ServerConfiguration serverConfiguration = new ServerConfiguration();
            parse( document, serverConfiguration );

            return serverConfiguration;
        }
        catch ( Exception e )
        {
            if ( e instanceof ServerConfigurationParserException )
            {
                throw ( ServerConfigurationParserException ) e;
            }
            else
            {
                ServerConfigurationParserException exception = new ServerConfigurationParserException( e.getMessage(),
                    e.getCause() );
                exception.setStackTrace( e.getStackTrace() );
                throw exception;
            }
        }
    }


    /**
     * Parses a 'server.xml' file located at the given path and returns 
     * the corresponding ServerConfiguration Object.
     *
     * @param inputStream
     *      the Input Stream of the file to parse
     * @return
     *      the corresponding ServerConfiguration Object
     * @throws ServerConfigurationParserException
     *      if an error occurrs when reading the Server Configuration file
     */
    public ServerConfiguration parse( InputStream inputStream ) throws ServerConfigurationParserException
    {
        try
        {
            EntityResolver resolver = new EntityResolver()
            {
                public InputSource resolveEntity( String publicId, String systemId )
                {
                    if ( publicId.equalsIgnoreCase( "-//SPRING//DTD BEAN//EN" ) )
                    {
                        InputStream in = Activator.class.getResourceAsStream( "spring-beans.dtd" );
                        return new InputSource( in );
                    }
                    return null;
                }
            };

            SAXReader reader = new SAXReader();
            reader.setEntityResolver( resolver );
            Document document = reader.read( inputStream );

            ServerConfiguration serverConfiguration = new ServerConfiguration();

            parse( document, serverConfiguration );

            return serverConfiguration;
        }
        catch ( Exception e )
        {
            if ( e instanceof ServerConfigurationParserException )
            {
                throw ( ServerConfigurationParserException ) e;
            }
            else
            {
                ServerConfigurationParserException exception = new ServerConfigurationParserException( e.getMessage(),
                    e.getCause() );
                exception.setStackTrace( e.getStackTrace() );
                throw exception;
            }
        }
    }


    /**
     * Parses the Document.
     *
     * @param document
     *      the Document
     * @param serverConfiguration
     *      the Server Configuration
     * @throws NumberFormatException
     * @throws BooleanFormatException
     * @throws ServerConfigurationParserException
     */
    private void parse( Document document, ServerConfiguration serverConfiguration ) throws NumberFormatException,
        BooleanFormatException, ServerConfigurationParserException
    {
        // Reading the 'Environment' Bean
        readEnvironmentBean( document, serverConfiguration );

        // Reading the 'ChangePasswordConfiguration' Bean
        readChangePasswordConfigurationBean( document, serverConfiguration );

        // Reading the 'NTPConfiguration' Bean
        readNTPConfigurationBean( document, serverConfiguration );

        // Reading the 'DNSConfiguration' Bean
        readDNSConfigurationBean( document, serverConfiguration );

        // Reading the 'KDCConfiguration' Bean
        readKDCConfigurationBean( document, serverConfiguration );

        // Reading the 'LDAPSConfiguration' Bean
        readLDAPSConfigurationBean( document, serverConfiguration );

        // Reading the 'LDAPConfiguration' Bean
        readLDAPConfigurationBean( document, serverConfiguration );

        // Reading the 'Configuration' Bean
        readConfigurationBean( document, serverConfiguration );
    }


    /**
     * Reads the "Environment" Bean and store its values in the given ServerConfiguration.
     *
     * @param document
     *      the document to use
     * @param serverConfiguration
     *      the Server Configuration
     */
    private void readEnvironmentBean( Document document, ServerConfiguration serverConfiguration )
    {
        Element environmentBean = getBeanElementById( document, "environment" );

        // Principal
        String principal = readEnvironmentBeanProperty( "java.naming.security.principal", environmentBean );
        if ( principal != null )
        {
            serverConfiguration.setPrincipal( principal );
        }

        // Password
        String password = readEnvironmentBeanProperty( "java.naming.security.credentials", environmentBean );
        if ( password != null )
        {
            serverConfiguration.setPassword( password );
        }

        // Binary Attributes
        String binaryAttributes = readEnvironmentBeanProperty( "java.naming.ldap.attributes.binary", environmentBean );
        if ( binaryAttributes != null )
        {
            String[] attributes = binaryAttributes.split( " " );

            for ( String attribute : attributes )
            {
                serverConfiguration.addBinaryAttribute( attribute );
            }
        }
    }


    /**
     * Reads the given property in the 'Environment' Bean and returns it.
     *
     * @param property
     *      the property
     * @param element
     *      the Environment Bean Element
     * @return
     *      the value of the property, or null if the property has not been found
     */
    private String readEnvironmentBeanProperty( String property, Element element )
    {
        Element propertyElement = element.element( "property" );
        if ( propertyElement != null )
        {
            Element propsElement = propertyElement.element( "props" );
            if ( propsElement != null )
            {
                for ( Iterator<?> i = propsElement.elementIterator( "prop" ); i.hasNext(); )
                {
                    Element propElement = ( Element ) i.next();
                    Attribute keyAttribute = propElement.attribute( "key" );
                    if ( keyAttribute != null && ( keyAttribute.getValue().equals( property ) ) )
                    {
                        return propElement.getText();
                    }
                }
            }
        }

        return null;
    }


    /**
     * Reads the "ChangePasswordConfiguration" Bean and store its values in the given ServerConfiguration.
     *
     * @param document
     *      the document to use
     * @param serverConfiguration
     *      the Server Configuration
     * @throws BooleanFormatException 
     * @throws NumberFormatException 
     */
    private void readChangePasswordConfigurationBean( Document document, ServerConfiguration serverConfiguration )
        throws BooleanFormatException, NumberFormatException
    {
        Element changePasswordConfigurationBean = getBeanElementById( document, "changePasswordConfiguration" );

        // Enabled
        String enabled = readBeanProperty( "enabled", changePasswordConfigurationBean );
        if ( enabled != null )
        {
            serverConfiguration.setEnableChangePassword( parseBoolean( enabled ) );
        }

        // IP Port
        String ipPort = readBeanProperty( "ipPort", changePasswordConfigurationBean );
        if ( ipPort != null )
        {
            serverConfiguration.setChangePasswordPort( Integer.parseInt( ipPort ) );
        }
    }


    /**
     * Reads the "NTPConfiguration" Bean and store its values in the given ServerConfiguration.
     *
     * @param document
     *      the document to use
     * @param serverConfiguration
     *      the Server Configuration
     * @throws BooleanFormatException 
     * @throws NumberFormatException 
     */
    private void readNTPConfigurationBean( Document document, ServerConfiguration serverConfiguration )
        throws BooleanFormatException, NumberFormatException
    {
        Element ntpConfigurationBean = getBeanElementById( document, "ntpConfiguration" );

        // Enabled
        String enabled = readBeanProperty( "enabled", ntpConfigurationBean );
        if ( enabled != null )
        {
            serverConfiguration.setEnableNtp( parseBoolean( enabled ) );
        }

        // IP Port
        String ipPort = readBeanProperty( "ipPort", ntpConfigurationBean );
        if ( ipPort != null )
        {
            serverConfiguration.setNtpPort( Integer.parseInt( ipPort ) );
        }
    }


    /**
     * Reads the "DNSConfiguration" Bean and store its values in the given ServerConfiguration.
     *
     * @param document
     *      the document to use
     * @param serverConfiguration
     *      the Server Configuration
     * @throws BooleanFormatException 
     * @throws NumberFormatException 
     */
    private void readDNSConfigurationBean( Document document, ServerConfiguration serverConfiguration )
        throws BooleanFormatException, NumberFormatException
    {
        Element dnsConfigurationBean = getBeanElementById( document, "dnsConfiguration" );

        // Enabled
        String enabled = readBeanProperty( "enabled", dnsConfigurationBean );
        if ( enabled != null )
        {
            serverConfiguration.setEnableDns( parseBoolean( enabled ) );
        }

        // IP Port
        String ipPort = readBeanProperty( "ipPort", dnsConfigurationBean );
        if ( ipPort != null )
        {
            serverConfiguration.setDnsPort( Integer.parseInt( ipPort ) );
        }
    }


    /**
     * Reads the "KDCConfiguration" Bean and store its values in the given ServerConfiguration.
     *
     * @param document
     *      the document to use
     * @param serverConfiguration
     *      the Server Configuration
     * @throws BooleanFormatException 
     * @throws NumberFormatException 
     */
    private void readKDCConfigurationBean( Document document, ServerConfiguration serverConfiguration )
        throws BooleanFormatException, NumberFormatException
    {
        Element kdcConfigurationBean = getBeanElementById( document, "kdcConfiguration" );

        // Enabled
        String enabled = readBeanProperty( "enabled", kdcConfigurationBean );
        if ( enabled != null )
        {
            serverConfiguration.setEnableKerberos( parseBoolean( enabled ) );
        }

        // IP Port
        String ipPort = readBeanProperty( "ipPort", kdcConfigurationBean );
        if ( ipPort != null )
        {
            serverConfiguration.setKerberosPort( Integer.parseInt( ipPort ) );
        }
    }


    /**
     * Reads the "LDAPSConfiguration" Bean and store its values in the given ServerConfiguration.
     *
     * @param document
     *      the document to use
     * @param serverConfiguration
     *      the Server Configuration
     * @throws BooleanFormatException 
     * @throws NumberFormatException 
     */
    private void readLDAPSConfigurationBean( Document document, ServerConfiguration serverConfiguration )
        throws BooleanFormatException, NumberFormatException
    {
        Element ldapsConfiguration = getBeanElementById( document, "ldapsConfiguration" );

        // Enabled
        String enabled = readBeanProperty( "enabled", ldapsConfiguration );
        if ( enabled != null )
        {
            serverConfiguration.setEnableLdaps( parseBoolean( enabled ) );
        }

        // IP Port
        String ipPort = readBeanProperty( "ipPort", ldapsConfiguration );
        if ( ipPort != null )
        {
            serverConfiguration.setLdapsPort( Integer.parseInt( ipPort ) );
        }
    }


    /**
     * Reads the "LDAPConfiguration" Bean and store its values in the given ServerConfiguration.
     *
     * @param document
     *      the document to use
     * @param serverConfiguration
     *      the Server Configuration
     * @throws BooleanFormatException 
     * @throws NumberFormatException 
     */
    private void readLDAPConfigurationBean( Document document, ServerConfiguration serverConfiguration )
        throws BooleanFormatException, NumberFormatException
    {
        Element ldapConfiguration = getBeanElementById( document, "ldapConfiguration" );

        // IP Port
        String ipPort = readBeanProperty( "ipPort", ldapConfiguration );
        if ( ipPort != null )
        {
            serverConfiguration.setLdapPort( Integer.parseInt( ipPort ) );
        }

        // AllowAnonymousAccess
        String allowAnonymousAccess = readBeanProperty( "allowAnonymousAccess", ldapConfiguration );
        if ( allowAnonymousAccess != null )
        {
            serverConfiguration.setAllowAnonymousAccess( parseBoolean( allowAnonymousAccess ) );
        }

        //  Supported Mechanisms
        Element supportedMechanismsElement = getBeanPropertyElement( "supportedMechanisms", ldapConfiguration );
        if ( supportedMechanismsElement != null )
        {
            Element listElement = supportedMechanismsElement.element( "list" );
            if ( listElement != null )
            {
                for ( Iterator<?> i = listElement.elementIterator( "value" ); i.hasNext(); )
                {
                    serverConfiguration.addSupportedMechanism( ( ( Element ) i.next() ).getTextTrim() );
                }
            }
        }

        // SASL Host
        String saslHost = readBeanProperty( "saslHost", ldapConfiguration );
        if ( saslHost != null )
        {
            serverConfiguration.setSaslHost( saslHost );
        }

        // SASL Principal
        String saslPrincipal = readBeanProperty( "saslPrincipal", ldapConfiguration );
        if ( saslPrincipal != null )
        {
            serverConfiguration.setSaslPrincipal( saslPrincipal );
        }

        // SASL QOP
        Element saslQopElement = getBeanPropertyElement( "saslQop", ldapConfiguration );
        if ( saslQopElement != null )
        {
            Element listElement = saslQopElement.element( "list" );
            if ( listElement != null )
            {
                for ( Iterator<?> i = listElement.elementIterator( "value" ); i.hasNext(); )
                {
                    serverConfiguration.addSaslQop( ( ( Element ) i.next() ).getTextTrim() );
                }
            }
        }

        // SASL Realms
        Element saslRealmsElement = getBeanPropertyElement( "saslRealms", ldapConfiguration );
        if ( saslQopElement != null )
        {
            Element listElement = saslRealmsElement.element( "list" );
            if ( listElement != null )
            {
                for ( Iterator<?> i = listElement.elementIterator( "value" ); i.hasNext(); )
                {
                    serverConfiguration.addSaslRealm( ( ( Element ) i.next() ).getTextTrim() );
                }
            }
        }

        // Search Base DN
        String searchBaseDn = readBeanProperty( "searchBaseDn", ldapConfiguration );
        if ( searchBaseDn != null )
        {
            serverConfiguration.setSearchBaseDn( searchBaseDn );
        }

        // MaxTimeLimit
        String maxTimeLimit = readBeanProperty( "maxTimeLimit", ldapConfiguration );
        if ( maxTimeLimit != null )
        {
            serverConfiguration.setMaxTimeLimit( Integer.parseInt( maxTimeLimit ) );
        }

        // MaxSizeLimit
        String maxSizeLimit = readBeanProperty( "maxSizeLimit", ldapConfiguration );
        if ( maxSizeLimit != null )
        {
            serverConfiguration.setMaxSizeLimit( Integer.parseInt( maxSizeLimit ) );
        }

        // ExtendedOperations
        readExtendedOperations( ldapConfiguration, serverConfiguration );
    }


    /**
     * Reads the "Configuration" Bean and store its values in the given ServerConfiguration.
     *
     * @param document
     *      the document to use
     * @param serverConfiguration
     *      the Server Configuration
     * @throws NumberFormatException
     * @throws BooleanFormatException 
     * @throws ServerConfigurationParserException 
     */
    private void readConfigurationBean( Document document, ServerConfiguration serverConfiguration )
        throws NumberFormatException, BooleanFormatException, ServerConfigurationParserException
    {
        Element configurationBean = getBeanElementById( document, "configuration" );

        // SynchPeriodMillis
        String synchPeriodMillis = readBeanProperty( "synchPeriodMillis", configurationBean );
        if ( synchPeriodMillis != null )
        {
            serverConfiguration.setSynchronizationPeriod( Long.parseLong( synchPeriodMillis ) );
        }

        // MaxThreads
        String maxThreads = readBeanProperty( "maxThreads", configurationBean );
        if ( maxThreads != null )
        {
            serverConfiguration.setMaxThreads( Integer.parseInt( maxThreads ) );
        }

        // AllowAnonymousAccess
        String allowAnonymousAccess = readBeanProperty( "allowAnonymousAccess", configurationBean );
        if ( allowAnonymousAccess != null )
        {
            serverConfiguration.setAllowAnonymousAccess( parseBoolean( allowAnonymousAccess ) );
        }

        // AccessControlEnabled
        String accessControlEnabled = readBeanProperty( "accessControlEnabled", configurationBean );
        if ( accessControlEnabled != null )
        {
            serverConfiguration.setEnableAccessControl( parseBoolean( accessControlEnabled ) );
        }

        // EnableDenormalizeOpAttrs
        String denormalizeOpAttrsEnabled = readBeanProperty( "denormalizeOpAttrsEnabled", configurationBean );
        if ( denormalizeOpAttrsEnabled != null )
        {
            serverConfiguration.setDenormalizeOpAttr( parseBoolean( denormalizeOpAttrsEnabled ) );
        }

        // SystemPartition
        String systemPartitionConfiguration = readBeanProperty( "systemPartitionConfiguration", configurationBean );
        if ( systemPartitionConfiguration != null )
        {
            Partition systemPartition = readPartition( document, systemPartitionConfiguration, true );
            if ( systemPartition != null )
            {
                serverConfiguration.addPartition( systemPartition );
            }
        }
        else
        {
            throw new ServerConfigurationParserException(
                "The Server Configuration does not contain a 'systemPartitionConfiguration' property." );
        }

        // Other Partitions
        readOtherPartitions( configurationBean, serverConfiguration );

        // Interceptors
        readInterceptors( configurationBean, serverConfiguration );
    }


    /**
     * Reads and adds Partitions (other than the SystemPartition) to the Server Configuration.
     *
     * @param configurationBean
     *      the Configuration Bean Element
     * @param serverConfiguration
     *      the Server Configuration
     * @throws BooleanFormatException 
     * @throws NumberFormatException 
     */
    private void readOtherPartitions( Element configurationBean, ServerConfiguration serverConfiguration )
        throws NumberFormatException, BooleanFormatException
    {
        Element propertyElement = getBeanPropertyElement( "partitionConfigurations", configurationBean );
        if ( propertyElement != null )
        {
            Element setElement = propertyElement.element( "set" );
            if ( setElement != null )
            {
                for ( Iterator<?> i = setElement.elementIterator( "ref" ); i.hasNext(); )
                {
                    Element element = ( Element ) i.next();
                    Attribute beanAttribute = element.attribute( "bean" );
                    if ( beanAttribute != null )
                    {
                        Partition partition = readPartition( configurationBean.getDocument(), beanAttribute.getValue(),
                            false );
                        if ( partition != null )
                        {
                            serverConfiguration.addPartition( partition );
                        }
                    }
                }
            }
        }
    }


    /**
     * Reads the partition associated with the given Bean ID and return it.
     *
     * @param document
     *      the document
     * @param id
     *      the Bean ID of the partition
     * @param isSystemPartition
     *      true if this partition is the System Partition
     * @return
     *      the partition associated with the given Bean ID
     * @throws BooleanFormatException 
     */
    private Partition readPartition( Document document, String id, boolean isSystemPartition )
        throws BooleanFormatException, NumberFormatException
    {
        Element partitionBean = getBeanElementById( document, id );
        if ( partitionBean != null )
        {
            Partition partition = new Partition();
            partition.setSystemPartition( isSystemPartition );

            // ID
            String partitionId = readBeanProperty( "id", partitionBean );
            if ( partitionId != null )
            {
                partition.setId( partitionId );
            }

            // CacheSize
            String cacheSize = readBeanProperty( "cacheSize", partitionBean );
            if ( cacheSize != null )
            {
                partition.setCacheSize( Integer.parseInt( cacheSize ) );
            }

            // Suffix
            String suffix = readBeanProperty( "suffix", partitionBean );
            if ( suffix != null )
            {
                partition.setSuffix( suffix );
            }

            // OptimizerEnabled
            String optimizerEnabled = readBeanProperty( "optimizerEnabled", partitionBean );
            if ( optimizerEnabled != null )
            {
                partition.setEnableOptimizer( parseBoolean( optimizerEnabled ) );
            }

            // SynchOnWrite
            String synchOnWrite = readBeanProperty( "synchOnWrite", partitionBean );
            if ( synchOnWrite != null )
            {
                partition.setSynchronizationOnWrite( parseBoolean( synchOnWrite ) );
            }

            // IndexedAttributes
            partition.setIndexedAttributes( readPartitionIndexedAttributes( partitionBean ) );

            // ContextEntry
            partition.setContextEntry( readPartitionContextEntry( partitionBean ) );

            return partition;
        }

        return null;
    }


    /**
     * Reads the Indexed Attributes of the given Partition Bean Element
     *
     * @param partitionBean
     *      the Partition Bean Element
     * @return
     *      the Indexed Attributes
     */
    private List<IndexedAttribute> readPartitionIndexedAttributes( Element partitionBean ) throws NumberFormatException
    {
        List<IndexedAttribute> indexedAttributes = new ArrayList<IndexedAttribute>();

        Element propertyElement = getBeanPropertyElement( "indexedAttributes", partitionBean );
        if ( propertyElement != null )
        {
            Element setElement = propertyElement.element( "set" );
            if ( setElement != null )
            {
                for ( Iterator<?> i = setElement.elementIterator( "bean" ); i.hasNext(); )
                {
                    Element beanElement = ( Element ) i.next();
                    IndexedAttribute ia = readIndexedAttribute( beanElement );
                    if ( ia != null )
                    {
                        indexedAttributes.add( ia );
                    }
                }
            }
        }

        return indexedAttributes;
    }


    /**
     * Reads an Indexed Attribute.
     *
     * @param beanElement
     *      the Bean Element of the Indexed Attribute
     * @return
     *      the corresponding Indexed Attribute or null if it could not be parsed
     * @throws NumberFormatException
     */
    private IndexedAttribute readIndexedAttribute( Element beanElement ) throws NumberFormatException
    {
        Attribute classAttribute = beanElement.attribute( "class" );
        if ( classAttribute != null
            && classAttribute.getValue().equals(
                "org.apache.directory.server.core.partition.impl.btree.MutableIndexConfiguration" ) )
        {
            String attributeId = readBeanProperty( "attributeId", beanElement );
            String cacheSize = readBeanProperty( "cacheSize", beanElement );
            if ( ( attributeId != null ) && ( cacheSize != null ) )
            {
                return new IndexedAttribute( attributeId, Integer.parseInt( cacheSize ) );
            }

        }

        return null;
    }


    /**
     * Reads the Context Entry of the given Partition Bean Element
     *
     * @param partitionBean
     *      the Partition Bean Element
     * @return
     *      the Context Entry
     */
    private Attributes readPartitionContextEntry( Element partitionBean )
    {
        Element propertyElement = getBeanPropertyElement( "contextEntry", partitionBean );
        if ( propertyElement != null )
        {
            Element valueElement = propertyElement.element( "value" );
            if ( valueElement != null )
            {
                return readContextEntry( valueElement.getText() );
            }
        }

        return new BasicAttributes( true );
    }


    /**
     * Read an entry (without DN)
     * 
     * @param text
     *            The ldif format text
     * @return An Attributes.
     */
    private Attributes readContextEntry( String text )
    {
        StringReader strIn = new StringReader( text );
        BufferedReader in = new BufferedReader( strIn );

        String line = null;
        Attributes attributes = new AttributesImpl( true );

        try
        {
            while ( ( line = ( ( BufferedReader ) in ).readLine() ) != null )
            {
                if ( line.length() == 0 )
                {
                    continue;
                }

                String addedLine = line.trim();

                if ( StringTools.isEmpty( addedLine ) )
                {
                    continue;
                }

                javax.naming.directory.Attribute attribute = LdifReader.parseAttributeValue( addedLine );
                javax.naming.directory.Attribute oldAttribute = attributes.get( attribute.getID() );

                if ( oldAttribute != null )
                {
                    try
                    {
                        oldAttribute.add( attribute.get() );
                        attributes.put( oldAttribute );
                    }
                    catch ( NamingException ne )
                    {
                        // Do nothing
                    }
                }
                else
                {
                    attributes.put( attribute );
                }
            }
        }
        catch ( IOException ioe )
        {
            // Do nothing : we can't reach this point !
        }

        return attributes;
    }


    /**
     * Reads and adds the Interceptors to the Server Configuration.
     *
     * @param configurationBean
     *      the Configuration Bean Element
     * @param serverConfiguration
     *      the Server Configuration
     */
    private void readInterceptors( Element configurationBean, ServerConfiguration serverConfiguration )
    {
        Element propertyElement = getBeanPropertyElement( "interceptorConfigurations", configurationBean );
        if ( propertyElement != null )
        {
            Element listElement = propertyElement.element( "list" );
            if ( listElement != null )
            {
                for ( Iterator<?> i = listElement.elementIterator( "bean" ); i.hasNext(); )
                {
                    Interceptor interceptor = readInterceptor( ( Element ) i.next() );
                    if ( interceptor != null )
                    {
                        serverConfiguration.addInterceptor( interceptor );
                    }
                }
            }
        }
    }


    /**
     * Reads an Interceptor.
     *
     * @param element
     *      the Interceptor Element
     * @return
     *      the Interceptor or null if it could not be parsed
     */
    private Interceptor readInterceptor( Element element )
    {
        Attribute classAttribute = element.attribute( "class" );
        if ( classAttribute != null
            && classAttribute.getValue().equals(
                "org.apache.directory.server.core.configuration.MutableInterceptorConfiguration" ) )
        {
            String name = readBeanProperty( "name", element );
            String interceptorClassName = readBeanProperty( "interceptorClassName", element );

            if ( ( name != null ) && ( interceptorClassName != null ) )
            {
                Interceptor interceptor = new Interceptor( name );
                interceptor.setClassType( interceptorClassName );
                return interceptor;
            }
        }

        return null;
    }


    /**
     * Reads and adds the ExtendedOperations to the Server Configuration.
     *
     * @param configurationBean
     *      the Configuration Bean Element
     * @param serverConfiguration
     *      the Server Configuration
     */
    private void readExtendedOperations( Element configurationBean, ServerConfiguration serverConfiguration )
    {
        Element propertyElement = getBeanPropertyElement( "extendedOperationHandlers", configurationBean );
        if ( propertyElement != null )
        {
            Element listElement = propertyElement.element( "list" );
            if ( listElement != null )
            {
                for ( Iterator<?> i = listElement.elementIterator( "bean" ); i.hasNext(); )
                {
                    ExtendedOperation extendedOperation = readExtendedOperation( ( Element ) i.next() );
                    if ( extendedOperation != null )
                    {
                        serverConfiguration.addExtendedOperation( extendedOperation );
                    }
                }
            }
        }
    }


    /**
     * Reads an Extended Operation.
     *
     * @param element
     *      the Extended Operation Element
     * @return
     *      the Extended Operation or null if it could not be parsed
     */
    private ExtendedOperation readExtendedOperation( Element element )
    {
        Attribute classAttribute = element.attribute( "class" );
        if ( classAttribute != null )
        {
            return new ExtendedOperation( classAttribute.getValue() );
        }

        return null;
    }


    /**
     * Gets the Bean element corresponding to the given ID.
     *
     * @param document
     *      the document to use
     * @param id
     *      the id
     * @return
     *       the Bean element corresponding to the given ID or null if the bean was not found
     */
    private Element getBeanElementById( Document document, String id )
    {
        for ( Iterator<?> i = document.getRootElement().elementIterator( "bean" ); i.hasNext(); )
        {
            Element element = ( Element ) i.next();
            Attribute idAttribute = element.attribute( "id" );
            if ( idAttribute != null && ( idAttribute.getValue().equals( id ) ) )
            {
                return element;
            }
        }

        return null;
    }


    /**
     * Reads the given property in the Bean and returns its value.
     *
     * @param property
     *      the property
     * @param element
     *      the Bean Element
     * @return
     *      the value of the property, or null if the property has not been found
     */
    private String readBeanProperty( String property, Element element )
    {
        Element propertyElement = getBeanPropertyElement( property, element );
        if ( propertyElement != null )
        {
            Attribute valueAttribute = propertyElement.attribute( "value" );
            if ( valueAttribute != null )
            {
                return valueAttribute.getValue();
            }

            Attribute refAttribute = propertyElement.attribute( "ref" );
            if ( refAttribute != null )
            {
                return refAttribute.getValue();
            }
        }

        return null;
    }


    /**
     * Gets the given property Element in the the bean
     *
     * @param property
     *      the propery
     * @param element
     *      the bean Element
     * @return
     *      the associated property, or null if the property has not been found
     */
    private Element getBeanPropertyElement( String property, Element element )
    {
        for ( Iterator<?> i = element.elementIterator( "property" ); i.hasNext(); )
        {
            Element propertyElement = ( Element ) i.next();
            Attribute nameAttribute = propertyElement.attribute( "name" );
            if ( nameAttribute != null && ( nameAttribute.getValue().equals( property ) ) )
            {
                return propertyElement;
            }
        }

        return null;
    }


    /**
     * Parses the string argument as a boolean.
     *
     * @param s
     *      a String containing the boolean representation to be parsed
     * @return
     *      the boolean value represented by the argument.
     * @throws BooleanFormatException
     *      if the string does not contain a parsable boolean.
     */
    private boolean parseBoolean( String s ) throws BooleanFormatException
    {
        if ( "true".equals( s ) )
        {
            return true;
        }
        else if ( "false".equals( s ) )
        {
            return false;
        }
        else
        {
            throw new BooleanFormatException( "The String '" + s + "' could not be parsed as a boolean." );
        }
    }

    /**
     * Thrown to indicate that the application has attempted to convert a string to a boolean, 
     * but that the string does not have the appropriate format.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    private class BooleanFormatException extends Exception
    {
        /** The Serial Version UID */
        private static final long serialVersionUID = -6426955193802317452L;


        /**
         * Creates a new instance of BooleanFormatException.
         *
         * @param message
         * @param cause
         */
        public BooleanFormatException( String message )
        {
            super( message );
        }
    }
}
