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
package org.apache.directory.studio.apacheds.configuration.model.v151;


import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.xml.transform.TransformerException;

import org.apache.directory.studio.apacheds.configuration.ApacheDSConfigurationPlugin;
import org.apache.directory.studio.apacheds.configuration.model.AbstractServerXmlIO;
import org.apache.directory.studio.apacheds.configuration.model.ServerConfiguration;
import org.apache.directory.studio.apacheds.configuration.model.ServerXmlIO;
import org.apache.directory.studio.apacheds.configuration.model.ServerXmlIOException;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;


/**
 * This class implements a parser and a writer for the 'server.xml' file of 
 * Apache Directory Server version 1.5.1.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ServerXmlIOV151 extends AbstractServerXmlIO implements ServerXmlIO
{
    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.configuration.model.ServerXmlIO#isValid(java.io.InputStream)
     */
    public boolean isValid( InputStream is )
    {
        try
        {
            SAXReader saxReader = new SAXReader();

            return isValid( saxReader.read( is ) );
        }
        catch ( Exception e )
        {
            return false;
        }
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.configuration.model.ServerXmlIO#isValid(java.io.Reader)
     */
    public boolean isValid( Reader reader )
    {
        try
        {
            SAXReader saxReader = new SAXReader();

            return isValid( saxReader.read( reader ) );
        }
        catch ( Exception e )
        {
            return false;
        }
    }


    /**
     * Checks if the Document is valid.
     *
     * @param document
     *      the Document
     * @return
     *      true if the Document is valid, false if not
     */
    private boolean isValid( Document document )
    {
        for ( Iterator<?> i = document.getRootElement().elementIterator( "bean" ); i.hasNext(); ) //$NON-NLS-1$
        {
            Element element = ( Element ) i.next();
            org.dom4j.Attribute classAttribute = element.attribute( "class" ); //$NON-NLS-1$
            if ( classAttribute != null
                && ( classAttribute.getValue()
                    .equals( "org.apache.directory.server.core.partition.impl.btree.MutableBTreePartitionConfiguration" ) ) ) //$NON-NLS-1$
            {
                String partitionId = readBeanProperty( "id", element ); //$NON-NLS-1$

                if ( partitionId != null )
                {
                    return true;
                }
            }
        }

        return false;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.configuration.model.ServerXmlIO#parse(java.io.InputStream)
     */
    public ServerConfiguration parse( InputStream is ) throws ServerXmlIOException
    {
        try
        {
            // Assigning the Spring Beans DTD to an entity resoler
            // (This will prevent the parser to try to get it online)
            EntityResolver resolver = new EntityResolver()
            {
                public InputSource resolveEntity( String publicId, String systemId )
                {
                    if ( publicId.equalsIgnoreCase( "-//SPRING//DTD BEAN//EN" ) ) //$NON-NLS-1$
                    {
                        InputStream in = ApacheDSConfigurationPlugin.class.getResourceAsStream( "spring-beans.dtd" ); //$NON-NLS-1$
                        return new InputSource( in );
                    }
                    return null;
                }
            };

            // Reading and creating the document
            SAXReader reader = new SAXReader();
            reader.setEntityResolver( resolver );
            Document document = reader.read( is );

            // Parsing the document
            ServerConfigurationV151 serverConfiguration = new ServerConfigurationV151();
            parse( document, serverConfiguration );

            return serverConfiguration;
        }
        catch ( Exception e )
        {
            if ( e instanceof ServerXmlIOException )
            {
                throw ( ServerXmlIOException ) e;
            }
            else
            {
                ServerXmlIOException exception = new ServerXmlIOException( e.getMessage(), e.getCause() );
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
     * @throws ServerXmlIOException
     */
    private void parse( Document document, ServerConfigurationV151 serverConfiguration ) throws NumberFormatException,
        BooleanFormatException, ServerXmlIOException
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
    private void readEnvironmentBean( Document document, ServerConfigurationV151 serverConfiguration )
    {
        Element environmentBean = getBeanElementById( document, "environment" ); //$NON-NLS-1$

        // Principal
        String principal = readEnvironmentBeanProperty( "java.naming.security.principal", environmentBean ); //$NON-NLS-1$
        if ( principal != null )
        {
            serverConfiguration.setPrincipal( principal );
        }

        // Password
        String password = readEnvironmentBeanProperty( "java.naming.security.credentials", environmentBean ); //$NON-NLS-1$
        if ( password != null )
        {
            serverConfiguration.setPassword( password );
        }

        // Binary Attributes
        String binaryAttributes = readEnvironmentBeanProperty( "java.naming.ldap.attributes.binary", environmentBean ); //$NON-NLS-1$
        if ( binaryAttributes != null )
        {
            String[] attributes = binaryAttributes.split( " " ); //$NON-NLS-1$

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
        Element propertyElement = element.element( "property" ); //$NON-NLS-1$
        if ( propertyElement != null )
        {
            Element propsElement = propertyElement.element( "props" ); //$NON-NLS-1$
            if ( propsElement != null )
            {
                for ( Iterator<?> i = propsElement.elementIterator( "prop" ); i.hasNext(); ) //$NON-NLS-1$
                {
                    Element propElement = ( Element ) i.next();
                    org.dom4j.Attribute keyAttribute = propElement.attribute( "key" ); //$NON-NLS-1$
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
    private void readChangePasswordConfigurationBean( Document document, ServerConfigurationV151 serverConfiguration )
        throws BooleanFormatException, NumberFormatException
    {
        Element changePasswordConfigurationBean = getBeanElementById( document, "changePasswordConfiguration" ); //$NON-NLS-1$

        // Enabled
        String enabled = readBeanProperty( "enabled", changePasswordConfigurationBean ); //$NON-NLS-1$
        if ( enabled != null )
        {
            serverConfiguration.setEnableChangePassword( parseBoolean( enabled ) );
        }

        // IP Port
        String ipPort = readBeanProperty( "ipPort", changePasswordConfigurationBean ); //$NON-NLS-1$
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
    private void readNTPConfigurationBean( Document document, ServerConfigurationV151 serverConfiguration )
        throws BooleanFormatException, NumberFormatException
    {
        Element ntpConfigurationBean = getBeanElementById( document, "ntpConfiguration" ); //$NON-NLS-1$

        // Enabled
        String enabled = readBeanProperty( "enabled", ntpConfigurationBean ); //$NON-NLS-1$
        if ( enabled != null )
        {
            serverConfiguration.setEnableNtp( parseBoolean( enabled ) );
        }

        // IP Port
        String ipPort = readBeanProperty( "ipPort", ntpConfigurationBean ); //$NON-NLS-1$
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
    private void readDNSConfigurationBean( Document document, ServerConfigurationV151 serverConfiguration )
        throws BooleanFormatException, NumberFormatException
    {
        Element dnsConfigurationBean = getBeanElementById( document, "dnsConfiguration" ); //$NON-NLS-1$

        // Enabled
        String enabled = readBeanProperty( "enabled", dnsConfigurationBean ); //$NON-NLS-1$
        if ( enabled != null )
        {
            serverConfiguration.setEnableDns( parseBoolean( enabled ) );
        }

        // IP Port
        String ipPort = readBeanProperty( "ipPort", dnsConfigurationBean ); //$NON-NLS-1$
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
    private void readKDCConfigurationBean( Document document, ServerConfigurationV151 serverConfiguration )
        throws BooleanFormatException, NumberFormatException
    {
        Element kdcConfigurationBean = getBeanElementById( document, "kdcConfiguration" ); //$NON-NLS-1$

        // Enabled
        String enabled = readBeanProperty( "enabled", kdcConfigurationBean ); //$NON-NLS-1$
        if ( enabled != null )
        {
            serverConfiguration.setEnableKerberos( parseBoolean( enabled ) );
        }

        // IP Port
        String ipPort = readBeanProperty( "ipPort", kdcConfigurationBean ); //$NON-NLS-1$
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
    private void readLDAPSConfigurationBean( Document document, ServerConfigurationV151 serverConfiguration )
        throws BooleanFormatException, NumberFormatException
    {
        Element ldapsConfiguration = getBeanElementById( document, "ldapsConfiguration" ); //$NON-NLS-1$

        // Enabled
        String enabled = readBeanProperty( "enabled", ldapsConfiguration ); //$NON-NLS-1$
        if ( enabled != null )
        {
            serverConfiguration.setEnableLdaps( parseBoolean( enabled ) );
        }

        // IP Port
        String ipPort = readBeanProperty( "ipPort", ldapsConfiguration ); //$NON-NLS-1$
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
    private void readLDAPConfigurationBean( Document document, ServerConfigurationV151 serverConfiguration )
        throws BooleanFormatException, NumberFormatException
    {
        Element ldapConfiguration = getBeanElementById( document, "ldapConfiguration" ); //$NON-NLS-1$

        // Enabled
        String enabled = readBeanProperty( "enabled", ldapConfiguration ); //$NON-NLS-1$
        if ( enabled != null )
        {
            serverConfiguration.setEnableLdap( parseBoolean( enabled ) );
        }
        else
        {
            serverConfiguration.setEnableLdap( true );
        }

        // IP Port
        String ipPort = readBeanProperty( "ipPort", ldapConfiguration ); //$NON-NLS-1$
        if ( ipPort != null )
        {
            serverConfiguration.setLdapPort( Integer.parseInt( ipPort ) );
        }

        // AllowAnonymousAccess
        String allowAnonymousAccess = readBeanProperty( "allowAnonymousAccess", ldapConfiguration ); //$NON-NLS-1$
        if ( allowAnonymousAccess != null )
        {
            serverConfiguration.setAllowAnonymousAccess( parseBoolean( allowAnonymousAccess ) );
        }

        //  Supported Mechanisms
        Element supportedMechanismsElement = getBeanPropertyElement( "supportedMechanisms", ldapConfiguration ); //$NON-NLS-1$
        if ( supportedMechanismsElement != null )
        {
            Element listElement = supportedMechanismsElement.element( "list" ); //$NON-NLS-1$
            if ( listElement != null )
            {
                for ( Iterator<?> i = listElement.elementIterator( "value" ); i.hasNext(); ) //$NON-NLS-1$
                {
                    serverConfiguration.addSupportedMechanism( ( ( Element ) i.next() ).getTextTrim() );
                }
            }
        }

        // SASL Host
        String saslHost = readBeanProperty( "saslHost", ldapConfiguration ); //$NON-NLS-1$
        if ( saslHost != null )
        {
            serverConfiguration.setSaslHost( saslHost );
        }

        // SASL Principal
        String saslPrincipal = readBeanProperty( "saslPrincipal", ldapConfiguration ); //$NON-NLS-1$
        if ( saslPrincipal != null )
        {
            serverConfiguration.setSaslPrincipal( saslPrincipal );
        }

        // SASL QOP
        Element saslQopElement = getBeanPropertyElement( "saslQop", ldapConfiguration ); //$NON-NLS-1$
        if ( saslQopElement != null )
        {
            Element listElement = saslQopElement.element( "list" ); //$NON-NLS-1$
            if ( listElement != null )
            {
                for ( Iterator<?> i = listElement.elementIterator( "value" ); i.hasNext(); ) //$NON-NLS-1$
                {
                    serverConfiguration.addSaslQop( ( ( Element ) i.next() ).getTextTrim() );
                }
            }
        }

        // SASL Realms
        Element saslRealmsElement = getBeanPropertyElement( "saslRealms", ldapConfiguration ); //$NON-NLS-1$
        if ( saslQopElement != null )
        {
            Element listElement = saslRealmsElement.element( "list" ); //$NON-NLS-1$
            if ( listElement != null )
            {
                for ( Iterator<?> i = listElement.elementIterator( "value" ); i.hasNext(); ) //$NON-NLS-1$
                {
                    serverConfiguration.addSaslRealm( ( ( Element ) i.next() ).getTextTrim() );
                }
            }
        }

        // Search Base DN
        String searchBaseDn = readBeanProperty( "searchBaseDn", ldapConfiguration ); //$NON-NLS-1$
        if ( searchBaseDn != null )
        {
            serverConfiguration.setSearchBaseDn( searchBaseDn );
        }

        // MaxTimeLimit
        String maxTimeLimit = readBeanProperty( "maxTimeLimit", ldapConfiguration ); //$NON-NLS-1$
        if ( maxTimeLimit != null )
        {
            serverConfiguration.setMaxTimeLimit( Integer.parseInt( maxTimeLimit ) );
        }

        // MaxSizeLimit
        String maxSizeLimit = readBeanProperty( "maxSizeLimit", ldapConfiguration ); //$NON-NLS-1$
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
     * @throws ServerXmlIOException 
     */
    private void readConfigurationBean( Document document, ServerConfigurationV151 serverConfiguration )
        throws NumberFormatException, BooleanFormatException, ServerXmlIOException
    {
        Element configurationBean = getBeanElementById( document, "configuration" ); //$NON-NLS-1$

        // SynchPeriodMillis
        String synchPeriodMillis = readBeanProperty( "synchPeriodMillis", configurationBean ); //$NON-NLS-1$
        if ( synchPeriodMillis != null )
        {
            serverConfiguration.setSynchronizationPeriod( Long.parseLong( synchPeriodMillis ) );
        }

        // MaxThreads
        String maxThreads = readBeanProperty( "maxThreads", configurationBean ); //$NON-NLS-1$
        if ( maxThreads != null )
        {
            serverConfiguration.setMaxThreads( Integer.parseInt( maxThreads ) );
        }

        // AllowAnonymousAccess
        String allowAnonymousAccess = readBeanProperty( "allowAnonymousAccess", configurationBean ); //$NON-NLS-1$
        if ( allowAnonymousAccess != null )
        {
            serverConfiguration.setAllowAnonymousAccess( parseBoolean( allowAnonymousAccess ) );
        }

        // AccessControlEnabled
        String accessControlEnabled = readBeanProperty( "accessControlEnabled", configurationBean ); //$NON-NLS-1$
        if ( accessControlEnabled != null )
        {
            serverConfiguration.setEnableAccessControl( parseBoolean( accessControlEnabled ) );
        }

        // EnableDenormalizeOpAttrs
        String denormalizeOpAttrsEnabled = readBeanProperty( "denormalizeOpAttrsEnabled", configurationBean ); //$NON-NLS-1$
        if ( denormalizeOpAttrsEnabled != null )
        {
            serverConfiguration.setDenormalizeOpAttr( parseBoolean( denormalizeOpAttrsEnabled ) );
        }

        // SystemPartition
        String systemPartitionConfiguration = readBeanProperty( "systemPartitionConfiguration", configurationBean ); //$NON-NLS-1$
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
            throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV151.55" ) ); //$NON-NLS-1$
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
    private void readOtherPartitions( Element configurationBean, ServerConfigurationV151 serverConfiguration )
        throws NumberFormatException, BooleanFormatException
    {
        Element propertyElement = getBeanPropertyElement( "partitionConfigurations", configurationBean ); //$NON-NLS-1$
        if ( propertyElement != null )
        {
            Element setElement = propertyElement.element( "set" ); //$NON-NLS-1$
            if ( setElement != null )
            {
                for ( Iterator<?> i = setElement.elementIterator( "ref" ); i.hasNext(); ) //$NON-NLS-1$
                {
                    Element element = ( Element ) i.next();
                    org.dom4j.Attribute beanAttribute = element.attribute( "bean" ); //$NON-NLS-1$
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
            String partitionId = readBeanProperty( "id", partitionBean ); //$NON-NLS-1$
            if ( partitionId != null )
            {
                partition.setId( partitionId );
            }

            // CacheSize
            String cacheSize = readBeanProperty( "cacheSize", partitionBean ); //$NON-NLS-1$
            if ( cacheSize != null )
            {
                partition.setCacheSize( Integer.parseInt( cacheSize ) );
            }

            // Suffix
            String suffix = readBeanProperty( "suffix", partitionBean ); //$NON-NLS-1$
            if ( suffix != null )
            {
                partition.setSuffix( suffix );
            }

            // OptimizerEnabled
            String optimizerEnabled = readBeanProperty( "optimizerEnabled", partitionBean ); //$NON-NLS-1$
            if ( optimizerEnabled != null )
            {
                partition.setEnableOptimizer( parseBoolean( optimizerEnabled ) );
            }

            // SynchOnWrite
            String synchOnWrite = readBeanProperty( "synchOnWrite", partitionBean ); //$NON-NLS-1$
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

        Element propertyElement = getBeanPropertyElement( "indexedAttributes", partitionBean ); //$NON-NLS-1$
        if ( propertyElement != null )
        {
            Element setElement = propertyElement.element( "set" ); //$NON-NLS-1$
            if ( setElement != null )
            {
                for ( Iterator<?> i = setElement.elementIterator( "bean" ); i.hasNext(); ) //$NON-NLS-1$
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
        org.dom4j.Attribute classAttribute = beanElement.attribute( "class" ); //$NON-NLS-1$
        if ( classAttribute != null
            && classAttribute.getValue().equals(
                "org.apache.directory.server.core.partition.impl.btree.MutableIndexConfiguration" ) ) //$NON-NLS-1$
        {
            String attributeId = readBeanProperty( "attributeId", beanElement ); //$NON-NLS-1$
            String cacheSize = readBeanProperty( "cacheSize", beanElement ); //$NON-NLS-1$
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
        Element propertyElement = getBeanPropertyElement( "contextEntry", partitionBean ); //$NON-NLS-1$
        if ( propertyElement != null )
        {
            Element valueElement = propertyElement.element( "value" ); //$NON-NLS-1$
            if ( valueElement != null )
            {
                return readContextEntry( valueElement.getText() );
            }
        }

        return new BasicAttributes( true );
    }


    /**
     * Reads and adds the Interceptors to the Server Configuration.
     *
     * @param configurationBean
     *      the Configuration Bean Element
     * @param serverConfiguration
     *      the Server Configuration
     */
    private void readInterceptors( Element configurationBean, ServerConfigurationV151 serverConfiguration )
    {
        Element propertyElement = getBeanPropertyElement( "interceptorConfigurations", configurationBean ); //$NON-NLS-1$
        if ( propertyElement != null )
        {
            Element listElement = propertyElement.element( "list" ); //$NON-NLS-1$
            if ( listElement != null )
            {
                for ( Iterator<?> i = listElement.elementIterator( "bean" ); i.hasNext(); ) //$NON-NLS-1$
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
        org.dom4j.Attribute classAttribute = element.attribute( "class" ); //$NON-NLS-1$
        if ( classAttribute != null
            && classAttribute.getValue().equals(
                "org.apache.directory.server.core.configuration.MutableInterceptorConfiguration" ) ) //$NON-NLS-1$
        {
            String name = readBeanProperty( "name", element ); //$NON-NLS-1$
            String interceptorClassName = readBeanProperty( "interceptorClassName", element ); //$NON-NLS-1$

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
    private void readExtendedOperations( Element configurationBean, ServerConfigurationV151 serverConfiguration )
    {
        Element propertyElement = getBeanPropertyElement( "extendedOperationHandlers", configurationBean ); //$NON-NLS-1$
        if ( propertyElement != null )
        {
            Element listElement = propertyElement.element( "list" ); //$NON-NLS-1$
            if ( listElement != null )
            {
                for ( Iterator<?> i = listElement.elementIterator( "bean" ); i.hasNext(); ) //$NON-NLS-1$
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
        org.dom4j.Attribute classAttribute = element.attribute( "class" ); //$NON-NLS-1$
        if ( classAttribute != null )
        {
            return new ExtendedOperation( classAttribute.getValue() );
        }

        return null;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.configuration.model.ServerXmlIO#toXml(org.apache.directory.studio.apacheds.configuration.model.ServerConfiguration)
     */
    public String toXml( ServerConfiguration serverConfiguration )
    {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement( "beans" ); //$NON-NLS-1$

        // Environment Bean
        createEnvironmentBean( root, ( ServerConfigurationV151 ) serverConfiguration );

        // Change Password Configuration Bean
        createChangePasswordConfigurationBean( root, ( ServerConfigurationV151 ) serverConfiguration );

        // NTP Configuration Bean
        createNtpConfigurationBean( root, ( ServerConfigurationV151 ) serverConfiguration );

        // DNS Configuration Bean
        createDnsConfigurationBean( root, ( ServerConfigurationV151 ) serverConfiguration );

        // KDC Configuration Bean
        createKdcConfigurationBean( root, ( ServerConfigurationV151 ) serverConfiguration );

        // LDAPS Configuration Bean
        createLdapsConfigurationBean( root, ( ServerConfigurationV151 ) serverConfiguration );

        // LDAP Configuration Bean
        createLdapConfigurationBean( root, ( ServerConfigurationV151 ) serverConfiguration );

        // Configuration Bean
        createConfigurationBean( root, ( ServerConfigurationV151 ) serverConfiguration );

        // System Partition Configuration Bean
        createSystemPartitionConfigurationBean( root, ( ServerConfigurationV151 ) serverConfiguration );

        // User Partitions Beans
        createUserPartitionsConfigurationsBean( root, ( ServerConfigurationV151 ) serverConfiguration );

        // CustomEditors Bean
        createCustomEditorsBean( root );

        Document stylizedDocument = null;
        try
        {
            stylizedDocument = styleDocument( document );
        }
        catch ( TransformerException e )
        {
            // Will never occur
        }

        stylizedDocument.addDocType( "beans", "-//SPRING//DTD BEAN//EN", //$NON-NLS-1$ //$NON-NLS-2$
            "http://www.springframework.org/dtd/spring-beans.dtd" ); //$NON-NLS-1$

        return stylizedDocument.asXML();
    }


    /**
     * Creates the Environment Bean
     *
     * @param root
     *      the root Element
     * @param serverConfiguration
     *      the Server Configuration
     */
    private static void createEnvironmentBean( Element root, ServerConfigurationV151 serverConfiguration )
    {
        Element environmentBean = root.addElement( "bean" ); //$NON-NLS-1$
        environmentBean.addAttribute( "id", "environment" ); //$NON-NLS-1$ //$NON-NLS-2$
        environmentBean.addAttribute( "class", "org.springframework.beans.factory.config.PropertiesFactoryBean" ); //$NON-NLS-1$ //$NON-NLS-2$

        Element propertyElement = environmentBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "properties" ); //$NON-NLS-1$ //$NON-NLS-2$
        Element propsElement = propertyElement.addElement( "props" ); //$NON-NLS-1$

        // Key 'java.naming.security.authentication'
        Element propElement = propsElement.addElement( "prop" ); //$NON-NLS-1$
        propElement.addAttribute( "key", "java.naming.security.authentication" ); //$NON-NLS-1$ //$NON-NLS-2$
        propElement.setText( "simple" ); //$NON-NLS-1$

        // Key 'java.naming.security.principal'
        propElement = propsElement.addElement( "prop" ); //$NON-NLS-1$
        propElement.addAttribute( "key", "java.naming.security.principal" ); //$NON-NLS-1$ //$NON-NLS-2$
        propElement.setText( serverConfiguration.getPrincipal() );

        // Key 'java.naming.security.credentials'
        propElement = propsElement.addElement( "prop" ); //$NON-NLS-1$
        propElement.addAttribute( "key", "java.naming.security.credentials" ); //$NON-NLS-1$ //$NON-NLS-2$
        propElement.setText( serverConfiguration.getPassword() );

        // Key 'java.naming.ldap.attributes.binary'
        if ( !serverConfiguration.getBinaryAttributes().isEmpty() )
        {
            propElement = propsElement.addElement( "prop" ); //$NON-NLS-1$
            propElement.addAttribute( "key", "java.naming.ldap.attributes.binary" ); //$NON-NLS-1$ //$NON-NLS-2$
            StringBuffer sb = new StringBuffer();
            for ( String attribute : serverConfiguration.getBinaryAttributes() )
            {
                sb.append( attribute );
                sb.append( " " ); //$NON-NLS-1$
            }
            String attributes = sb.toString();
            propElement.setText( attributes.substring( 0, attributes.length() - 1 ) );
        }

    }


    /**
     * Creates the Change Password Configuration Bean.
     *
     * @param root
     *      the root Element
     * @param serverConfiguration
     *      the Server Configuration
     */
    private static void createChangePasswordConfigurationBean( Element root, ServerConfigurationV151 serverConfiguration )
    {
        createProtocolConfigurationBean( root, "changePasswordConfiguration", //$NON-NLS-1$
            "org.apache.directory.server.changepw.ChangePasswordConfiguration", serverConfiguration //$NON-NLS-1$
                .isEnableChangePassword(), serverConfiguration.getChangePasswordPort() );
    }


    /**
     * Creates the NTP Configuration Bean.
     *
     * @param root
     *      the root Element
     * @param serverConfiguration
     *      the Server Configuration
     */
    private static void createNtpConfigurationBean( Element root, ServerConfigurationV151 serverConfiguration )
    {
        createProtocolConfigurationBean( root, "ntpConfiguration", "org.apache.directory.server.ntp.NtpConfiguration", //$NON-NLS-1$ //$NON-NLS-2$
            serverConfiguration.isEnableNtp(), serverConfiguration.getNtpPort() );
    }


    /**
     * Creates the DNS Configuration Bean.
     *
     * @param root
     *      the root Element
     * @param serverConfiguration
     *      the Server Configuration
     */
    private static void createDnsConfigurationBean( Element root, ServerConfigurationV151 serverConfiguration )
    {
        createProtocolConfigurationBean( root, "dnsConfiguration", "org.apache.directory.server.dns.DnsConfiguration", //$NON-NLS-1$ //$NON-NLS-2$
            serverConfiguration.isEnableDns(), serverConfiguration.getDnsPort() );
    }


    /**
     * Creates the Kerberos Configuration Bean.
     *
     * @param root
     *      the root Element
     * @param serverConfiguration
     *      the Server Configuration
     */
    private static void createKdcConfigurationBean( Element root, ServerConfigurationV151 serverConfiguration )
    {
        createProtocolConfigurationBean( root, "kdcConfiguration", "org.apache.directory.server.kdc.KdcConfiguration", //$NON-NLS-1$ //$NON-NLS-2$
            serverConfiguration.isEnableKerberos(), serverConfiguration.getKerberosPort() );
    }


    /**
     * Creates the LDAPS Configuration Bean.
     *
     * @param root
     *      the root Element
     * @param serverConfiguration
     *      the Server Configuration
     */
    private static void createLdapsConfigurationBean( Element root, ServerConfigurationV151 serverConfiguration )
    {
        Element ldapsConfiguration = createProtocolConfigurationBean( root, "ldapsConfiguration", //$NON-NLS-1$
            "org.apache.directory.server.ldap.LdapConfiguration", serverConfiguration.isEnableLdaps(), //$NON-NLS-1$
            serverConfiguration.getLdapsPort() );

        // Enable LDAPS
        Element enableLdapsPropertyElement = ldapsConfiguration.addElement( "property" ); //$NON-NLS-1$
        enableLdapsPropertyElement.addAttribute( "name", "enableLdaps" ); //$NON-NLS-1$ //$NON-NLS-2$
        enableLdapsPropertyElement.addAttribute( "value", "" + true ); //$NON-NLS-1$ //$NON-NLS-2$
    }


    /**
     * Creates the LDAP Configuration Bean.
     *
     * @param root
     *      the root Element
     * @param serverConfiguration
     *      the Server Configuration
     */
    private static void createLdapConfigurationBean( Element root, ServerConfigurationV151 serverConfiguration )
    {
        Element ldapConfiguration = createProtocolConfigurationBean( root, "ldapConfiguration", //$NON-NLS-1$
            "org.apache.directory.server.ldap.LdapConfiguration", serverConfiguration.isEnableLdap(), //$NON-NLS-1$
            serverConfiguration.getLdapPort() );

        // AllowAnonymousAccess
        Element propertyElement = ldapConfiguration.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "allowAnonymousAccess" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "value", "" + serverConfiguration.isAllowAnonymousAccess() ); //$NON-NLS-1$ //$NON-NLS-2$

        // Supported Mechanisms
        propertyElement = ldapConfiguration.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "supportedMechanisms" ); //$NON-NLS-1$ //$NON-NLS-2$
        if ( serverConfiguration.getSupportedMechanisms().size() > 1 )
        {
            Element listElement = propertyElement.addElement( "list" ); //$NON-NLS-1$
            for ( String supportedMechanism : serverConfiguration.getSupportedMechanisms() )
            {
                listElement.addElement( "value" ).setText( supportedMechanism ); //$NON-NLS-1$
            }
        }

        // SASL Host
        propertyElement = ldapConfiguration.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "saslHost" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "value", serverConfiguration.getSaslHost() ); //$NON-NLS-1$

        // SASL Principal
        propertyElement = ldapConfiguration.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "saslPrincipal" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "value", serverConfiguration.getSaslPrincipal() ); //$NON-NLS-1$

        // SASL QOP
        propertyElement = ldapConfiguration.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "saslQop" ); //$NON-NLS-1$ //$NON-NLS-2$
        if ( serverConfiguration.getSaslQops().size() > 1 )
        {
            Element listElement = propertyElement.addElement( "list" ); //$NON-NLS-1$
            for ( String saslQop : serverConfiguration.getSaslQops() )
            {
                listElement.addElement( "value" ).setText( saslQop ); //$NON-NLS-1$
            }
        }

        // SASL Realms
        propertyElement = ldapConfiguration.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "saslRealms" ); //$NON-NLS-1$ //$NON-NLS-2$
        if ( serverConfiguration.getSaslRealms().size() > 1 )
        {
            Element listElement = propertyElement.addElement( "list" ); //$NON-NLS-1$
            for ( String saslRealm : serverConfiguration.getSaslRealms() )
            {
                listElement.addElement( "value" ).setText( saslRealm ); //$NON-NLS-1$
            }
        }

        // Search Base DN
        propertyElement = ldapConfiguration.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "searchBaseDN" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "value", serverConfiguration.getSearchBaseDn() ); //$NON-NLS-1$

        // MaxTimeLimit
        propertyElement = ldapConfiguration.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "maxTimeLimit" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "value", "" + serverConfiguration.getMaxTimeLimit() ); //$NON-NLS-1$ //$NON-NLS-2$

        // MaxSizeLimit
        propertyElement = ldapConfiguration.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "maxSizeLimit" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "value", "" + serverConfiguration.getMaxSizeLimit() ); //$NON-NLS-1$ //$NON-NLS-2$

        // ExtendedOperationHandlers
        propertyElement = ldapConfiguration.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "extendedOperationHandlers" ); //$NON-NLS-1$ //$NON-NLS-2$
        if ( serverConfiguration.getExtendedOperations().size() > 1 )
        {
            Element listElement = propertyElement.addElement( "list" ); //$NON-NLS-1$
            for ( ExtendedOperation extendedOperation : serverConfiguration.getExtendedOperations() )
            {
                listElement.addElement( "bean" ).addAttribute( "class", extendedOperation.getClassType() ); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
    }


    /**
     * Creates a Protocol Configuration Bean.
     *
     * @param root
     *      the root Element
     * @param id
     *      the id of the Bean
     * @param className
     *      the class name of the Bean
     * @param enabled
     *      the enabled flag
     * @param ipPort
     *      the port
     * @return
     *      the corresponding Protocol Configuration Bean
     */
    private static Element createProtocolConfigurationBean( Element root, String id, String className, boolean enabled,
        int ipPort )
    {
        Element protocolConfigurationBean = root.addElement( "bean" ); //$NON-NLS-1$
        protocolConfigurationBean.addAttribute( "id", id ); //$NON-NLS-1$
        protocolConfigurationBean.addAttribute( "class", className ); //$NON-NLS-1$

        // Enabled
        Element enabledPropertyElement = protocolConfigurationBean.addElement( "property" ); //$NON-NLS-1$
        enabledPropertyElement.addAttribute( "name", "enabled" ); //$NON-NLS-1$ //$NON-NLS-2$
        enabledPropertyElement.addAttribute( "value", "" + enabled ); //$NON-NLS-1$ //$NON-NLS-2$

        // IP Port
        Element ipPortPropertyElement = protocolConfigurationBean.addElement( "property" ); //$NON-NLS-1$
        ipPortPropertyElement.addAttribute( "name", "ipPort" ); //$NON-NLS-1$ //$NON-NLS-2$
        ipPortPropertyElement.addAttribute( "value", "" + ipPort ); //$NON-NLS-1$ //$NON-NLS-2$

        return protocolConfigurationBean;
    }


    /**
     * Creates the Configuration Bean.
     *
     * @param root
     *      the root Element
     * @param serverConfiguration
     *      the Server Configuration
     */
    private static void createConfigurationBean( Element root, ServerConfigurationV151 serverConfiguration )
    {
        Element configurationBean = root.addElement( "bean" ); //$NON-NLS-1$
        configurationBean.addAttribute( "id", "configuration" ); //$NON-NLS-1$ //$NON-NLS-2$
        configurationBean.addAttribute( "class", //$NON-NLS-1$
            "org.apache.directory.server.configuration.MutableServerStartupConfiguration" ); //$NON-NLS-1$

        // Working directory
        Element propertyElement = configurationBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "workingDirectory" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "value", "example.com" ); // TODO Ask Alex about this value. //$NON-NLS-1$ //$NON-NLS-2$

        // LDIF Directory
        // TODO Ask Alex about this value.

        // LDIF Filters
        // TODO Ask Alex about this value.

        // SynchPeriodMillis
        propertyElement = configurationBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "synchPeriodMillis" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "value", "" + serverConfiguration.getSynchronizationPeriod() ); //$NON-NLS-1$ //$NON-NLS-2$

        // MaxThreads
        propertyElement = configurationBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "maxThreads" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "value", "" + serverConfiguration.getMaxThreads() ); //$NON-NLS-1$ //$NON-NLS-2$

        // AllowAnonymousAccess
        propertyElement = configurationBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "allowAnonymousAccess" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "value", "" + serverConfiguration.isAllowAnonymousAccess() ); //$NON-NLS-1$ //$NON-NLS-2$

        // AccessControlEnabled
        propertyElement = configurationBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "accessControlEnabled" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "value", "" + serverConfiguration.isEnableAccessControl() ); //$NON-NLS-1$ //$NON-NLS-2$

        // DenormalizeOpAttrsEnabled
        propertyElement = configurationBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "denormalizeOpAttrsEnabled" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "value", "" + serverConfiguration.isDenormalizeOpAttr() ); //$NON-NLS-1$ //$NON-NLS-2$

        // NTP Configuration Ref
        propertyElement = configurationBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "ntpConfiguration" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "ref", "ntpConfiguration" ); //$NON-NLS-1$ //$NON-NLS-2$

        // DNS Configuration Ref
        propertyElement = configurationBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "dnsConfiguration" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "ref", "dnsConfiguration" ); //$NON-NLS-1$ //$NON-NLS-2$

        // Change Password Configuration Ref
        propertyElement = configurationBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "changePasswordConfiguration" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "ref", "changePasswordConfiguration" ); //$NON-NLS-1$ //$NON-NLS-2$

        // KDC Configuration Ref
        propertyElement = configurationBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "kdcConfiguration" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "ref", "kdcConfiguration" ); //$NON-NLS-1$ //$NON-NLS-2$

        // LDAPS Configuration Ref
        propertyElement = configurationBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "ldapsConfiguration" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "ref", "ldapsConfiguration" ); //$NON-NLS-1$ //$NON-NLS-2$

        // LDAP Configuration Ref
        propertyElement = configurationBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "ldapConfiguration" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "ref", "ldapConfiguration" ); //$NON-NLS-1$ //$NON-NLS-2$

        // SystemPartitionConfiguration
        propertyElement = configurationBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "systemPartitionConfiguration" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "ref", "systemPartitionConfiguration" ); //$NON-NLS-1$ //$NON-NLS-2$

        // PartitionConfigurations
        propertyElement = configurationBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "partitionConfigurations" ); //$NON-NLS-1$ //$NON-NLS-2$
        if ( serverConfiguration.getPartitions().size() > 1 )
        {
            Element setElement = propertyElement.addElement( "set" ); //$NON-NLS-1$
            int partitionCounter = 1;
            for ( Partition partition : serverConfiguration.getPartitions() )
            {
                if ( !partition.isSystemPartition() )
                {
                    setElement.addElement( "ref" ).addAttribute( "bean", "partition-" + partitionCounter ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    partitionCounter++;
                }
            }
        }

        // InterceptorConfigurations
        propertyElement = configurationBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "interceptorConfigurations" ); //$NON-NLS-1$ //$NON-NLS-2$
        if ( serverConfiguration.getInterceptors().size() > 1 )
        {
            Element listElement = propertyElement.addElement( "list" ); //$NON-NLS-1$
            for ( Interceptor interceptor : serverConfiguration.getInterceptors() )
            {
                Element interceptorBeanElement = listElement.addElement( "bean" ); //$NON-NLS-1$
                interceptorBeanElement.addAttribute( "class", //$NON-NLS-1$
                    "org.apache.directory.server.core.configuration.MutableInterceptorConfiguration" ); //$NON-NLS-1$

                Element interceptorPropertyElement = interceptorBeanElement.addElement( "property" ); //$NON-NLS-1$
                interceptorPropertyElement.addAttribute( "name", "name" ); //$NON-NLS-1$ //$NON-NLS-2$
                interceptorPropertyElement.addAttribute( "value", interceptor.getName() ); //$NON-NLS-1$

                interceptorPropertyElement = interceptorBeanElement.addElement( "property" ); //$NON-NLS-1$
                interceptorPropertyElement.addAttribute( "name", "interceptorClassName" ); //$NON-NLS-1$ //$NON-NLS-2$
                interceptorPropertyElement.addAttribute( "value", ( interceptor.getClassType() == null ? "" //$NON-NLS-1$ //$NON-NLS-2$
                    : interceptor.getClassType() ) );
            }
        }

    }


    /**
     * Creates the SystemPartitionConfiguration Bean.
     *
     * @param root
     *      the root Element
     * @param serverConfiguration
     *      the Server Configuration
     */
    private static void createSystemPartitionConfigurationBean( Element root,
        ServerConfigurationV151 serverConfiguration )
    {
        Partition systemPartition = null;
        for ( Partition partition : serverConfiguration.getPartitions() )
        {
            if ( partition.isSystemPartition() )
            {
                systemPartition = partition;
                break;
            }
        }

        if ( systemPartition != null )
        {
            createPartitionConfigurationBean( root, systemPartition, "systemPartitionConfiguration" ); //$NON-NLS-1$
        }
    }


    /**
     * Creates the UserPartitionConfigurations Bean.
     *
     * @param root
     *      the root Element
     * @param serverConfiguration
     *      the Server Configuration
     */
    private static void createUserPartitionsConfigurationsBean( Element root,
        ServerConfigurationV151 serverConfiguration )
    {
        int counter = 1;
        for ( Partition partition : serverConfiguration.getPartitions() )
        {
            if ( !partition.isSystemPartition() )
            {
                createPartitionConfigurationBean( root, partition, "partition-" + counter ); //$NON-NLS-1$
                counter++;
            }
        }
    }


    /**
     * Creates a Partition Configuration Bean.
     *
     * @param root
     *      the root Element
     * @param partition
     *      the Partition
     * @param name
     *      the name to use
     */
    private static void createPartitionConfigurationBean( Element root, Partition partition, String name )
    {
        Element partitionBean = root.addElement( "bean" ); //$NON-NLS-1$
        partitionBean.addAttribute( "id", name ); //$NON-NLS-1$
        partitionBean.addAttribute( "class", //$NON-NLS-1$
            "org.apache.directory.server.core.partition.impl.btree.MutableBTreePartitionConfiguration" ); //$NON-NLS-1$

        // ID
        Element propertyElement = partitionBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "id" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "value", partition.getId() ); //$NON-NLS-1$

        // CacheSize
        propertyElement = partitionBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "cacheSize" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "value", "" + partition.getCacheSize() ); //$NON-NLS-1$ //$NON-NLS-2$

        // Suffix
        propertyElement = partitionBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "suffix" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "value", partition.getSuffix() ); //$NON-NLS-1$

        // PartitionClassName
        propertyElement = partitionBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "partitionClassName" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "value", //$NON-NLS-1$
            "org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmPartition" ); //$NON-NLS-1$

        // OptimizerEnabled
        propertyElement = partitionBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "optimizerEnabled" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "value", "" + partition.isEnableOptimizer() ); //$NON-NLS-1$ //$NON-NLS-2$

        // SynchOnWrite
        propertyElement = partitionBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "synchOnWrite" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "value", "" + partition.isSynchronizationOnWrite() ); //$NON-NLS-1$ //$NON-NLS-2$

        // Indexed Attributes
        propertyElement = partitionBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "indexedAttributes" ); //$NON-NLS-1$ //$NON-NLS-2$
        if ( partition.getIndexedAttributes().size() > 1 )
        {
            Element setElement = propertyElement.addElement( "set" ); //$NON-NLS-1$
            for ( IndexedAttribute indexedAttribute : partition.getIndexedAttributes() )
            {
                Element beanElement = setElement.addElement( "bean" ); //$NON-NLS-1$
                beanElement.addAttribute( "class", //$NON-NLS-1$
                    "org.apache.directory.server.core.partition.impl.btree.MutableIndexConfiguration" ); //$NON-NLS-1$

                // AttributeID
                Element beanPropertyElement = beanElement.addElement( "property" ); //$NON-NLS-1$
                beanPropertyElement.addAttribute( "name", "attributeId" ); //$NON-NLS-1$ //$NON-NLS-2$
                beanPropertyElement.addAttribute( "value", indexedAttribute.getAttributeId() ); //$NON-NLS-1$

                // CacheSize
                beanPropertyElement = beanElement.addElement( "property" ); //$NON-NLS-1$
                beanPropertyElement.addAttribute( "name", "cacheSize" ); //$NON-NLS-1$ //$NON-NLS-2$
                beanPropertyElement.addAttribute( "value", "" + indexedAttribute.getCacheSize() ); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }

        // ContextEntry
        propertyElement = partitionBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "contextEntry" ); //$NON-NLS-1$ //$NON-NLS-2$
        if ( partition.getContextEntry() != null )
        {
            Element valueElement = propertyElement.addElement( "value" ); //$NON-NLS-1$

            Attributes contextEntry = partition.getContextEntry();
            StringBuffer sb = new StringBuffer();
            NamingEnumeration<? extends Attribute> ne = contextEntry.getAll();
            while ( ne.hasMoreElements() )
            {
                Attribute attribute = ( Attribute ) ne.nextElement();
                try
                {
                    NamingEnumeration<?> values = attribute.getAll();
                    while ( values.hasMoreElements() )
                    {
                        sb.append( attribute.getID() + ": " + values.nextElement() + "\n" ); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                }
                catch ( NamingException e )
                {
                }
            }

            valueElement.setText( sb.toString() );
        }
    }


    /**
     * Creates the Custom Editors Bean.
     *
     * @param root
     *      the root Element
     */
    private static void createCustomEditorsBean( Element root )
    {
        Element customEditorsBean = root.addElement( "bean" ); //$NON-NLS-1$
        customEditorsBean.addAttribute( "class", "org.springframework.beans.factory.config.CustomEditorConfigurer" ); //$NON-NLS-1$ //$NON-NLS-2$
        Element propertyElement = customEditorsBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "customEditors" ); //$NON-NLS-1$ //$NON-NLS-2$
        Element mapElement = propertyElement.addElement( "map" ); //$NON-NLS-1$
        Element entryElement = mapElement.addElement( "entry" ); //$NON-NLS-1$
        entryElement.addAttribute( "key", "javax.naming.directory.Attributes" ); //$NON-NLS-1$ //$NON-NLS-2$
        Element entryBeanElement = entryElement.addElement( "bean" ); //$NON-NLS-1$
        entryBeanElement.addAttribute( "class", //$NON-NLS-1$
            "org.apache.directory.server.core.configuration.AttributesPropertyEditor" ); //$NON-NLS-1$
    }
}
