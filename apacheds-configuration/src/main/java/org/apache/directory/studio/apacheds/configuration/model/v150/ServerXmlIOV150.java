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
package org.apache.directory.studio.apacheds.configuration.model.v150;


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

import org.apache.directory.studio.apacheds.configuration.model.AbstractServerXmlIO;
import org.apache.directory.studio.apacheds.configuration.model.ServerConfiguration;
import org.apache.directory.studio.apacheds.configuration.model.ServerXmlIO;
import org.apache.directory.studio.apacheds.configuration.model.ServerXmlIOException;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


/**
 * This class implements a parser and a writer for the 'server.xml' file of 
 * Apache Directory Server version 1.5.0.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ServerXmlIOV150 extends AbstractServerXmlIO implements ServerXmlIO
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


    /**
     * Checks if the InputStream is valid.
     *
     * @param inputStream
     *      the InputStream
     * @return
     *      true if the InputStream is valid, false if not
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
        for ( Iterator<?> i = document.getRootElement().elementIterator( "bean" ); i.hasNext(); )
        {
            Element element = ( Element ) i.next();
            org.dom4j.Attribute classAttribute = element.attribute( "class" );
            if ( classAttribute != null
                && ( classAttribute.getValue()
                    .equals( "org.apache.directory.server.core.partition.impl.btree.MutableBTreePartitionConfiguration" ) ) )
            {
                String partitionName = readBeanProperty( "name", element );

                if ( partitionName != null )
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
            SAXReader reader = new SAXReader();
            Document document = reader.read( is );

            ServerConfigurationV150 serverConfiguration = new ServerConfigurationV150();

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
    private void parse( Document document, ServerConfigurationV150 serverConfiguration ) throws NumberFormatException,
        BooleanFormatException, ServerXmlIOException
    {
        // Reading the 'Environment' Bean
        readEnvironmentBean( document, serverConfiguration );

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
    private void readEnvironmentBean( Document document, ServerConfigurationV150 serverConfiguration )
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
                    org.dom4j.Attribute keyAttribute = propElement.attribute( "key" );
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
    private void readConfigurationBean( Document document, ServerConfigurationV150 serverConfiguration )
        throws NumberFormatException, BooleanFormatException, ServerXmlIOException
    {
        Element configurationBean = getBeanElementById( document, "configuration" );

        // LdapPort
        String ldapPort = readBeanProperty( "ldapPort", configurationBean );
        if ( ldapPort != null )
        {
            serverConfiguration.setPort( Integer.parseInt( ldapPort ) );
        }

        // SynchPeriodMillis
        String synchPeriodMillis = readBeanProperty( "synchPeriodMillis", configurationBean );
        if ( synchPeriodMillis != null )
        {
            serverConfiguration.setSynchronizationPeriod( Long.parseLong( synchPeriodMillis ) );
        }

        // MaxTimeLimit
        String maxTimeLimit = readBeanProperty( "maxTimeLimit", configurationBean );
        if ( maxTimeLimit != null )
        {
            serverConfiguration.setMaxTimeLimit( Integer.parseInt( maxTimeLimit ) );
        }

        // MaxSizeLimit
        String maxSizeLimit = readBeanProperty( "maxSizeLimit", configurationBean );
        if ( maxSizeLimit != null )
        {
            serverConfiguration.setMaxSizeLimit( Integer.parseInt( maxSizeLimit ) );
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

        // EnableNtp
        String enableNtp = readBeanProperty( "enableNtp", configurationBean );
        if ( enableNtp != null )
        {
            serverConfiguration.setEnableNTP( parseBoolean( enableNtp ) );
        }

        // EnableKerberos
        String enableKerberos = readBeanProperty( "enableKerberos", configurationBean );
        if ( enableKerberos != null )
        {
            serverConfiguration.setEnableKerberos( parseBoolean( enableKerberos ) );
        }

        // EnableChangePassword
        String enableChangePassword = readBeanProperty( "enableChangePassword", configurationBean );
        if ( enableChangePassword != null )
        {
            serverConfiguration.setEnableChangePassword( parseBoolean( enableChangePassword ) );
        }

        // EnableChangePassword
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
            throw new ServerXmlIOException(
                "The Server Configuration does not contain a 'systemPartitionConfiguration' property." );
        }

        // Other Partitions
        readOtherPartitions( configurationBean, serverConfiguration );

        // Interceptors
        readInterceptors( configurationBean, serverConfiguration );

        // ExtendedOperations
        readExtendedOperations( configurationBean, serverConfiguration );
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
    private void readOtherPartitions( Element configurationBean, ServerConfigurationV150 serverConfiguration )
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
                    org.dom4j.Attribute beanAttribute = element.attribute( "bean" );
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

            // Name
            String name = readBeanProperty( "name", partitionBean );
            if ( name != null )
            {
                partition.setId( name );
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
        org.dom4j.Attribute classAttribute = beanElement.attribute( "class" );
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
     * Reads and adds the Interceptors to the Server Configuration.
     *
     * @param configurationBean
     *      the Configuration Bean Element
     * @param serverConfiguration
     *      the Server Configuration
     */
    private void readInterceptors( Element configurationBean, ServerConfigurationV150 serverConfiguration )
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
        org.dom4j.Attribute classAttribute = element.attribute( "class" );
        if ( classAttribute != null
            && classAttribute.getValue().equals(
                "org.apache.directory.server.core.configuration.MutableInterceptorConfiguration" ) )
        {
            String name = readBeanProperty( "name", element );

            for ( Iterator<?> i = element.elementIterator( "property" ); i.hasNext(); )
            {
                Element propertyElement = ( Element ) i.next();
                org.dom4j.Attribute nameAttribute = propertyElement.attribute( "name" );
                if ( nameAttribute != null && ( nameAttribute.getValue().equals( "interceptor" ) ) )
                {
                    Element beanElement = propertyElement.element( "bean" );
                    if ( beanElement != null )
                    {
                        org.dom4j.Attribute beanClassAttribute = beanElement.attribute( "class" );
                        if ( beanClassAttribute != null )
                        {
                            Interceptor interceptor = new Interceptor( name );
                            interceptor.setClassType( beanClassAttribute.getValue() );
                            return interceptor;
                        }
                    }
                }
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
    private void readExtendedOperations( Element configurationBean, ServerConfigurationV150 serverConfiguration )
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
        org.dom4j.Attribute classAttribute = element.attribute( "class" );
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
        Element root = document.addElement( "beans" );

        // Environment Bean
        createEnvironmentBean( root, ( ServerConfigurationV150 ) serverConfiguration );

        // Configuration Bean
        createConfigurationBean( root, ( ServerConfigurationV150 ) serverConfiguration );

        // System Partition Configuration Bean
        createSystemPartitionConfigurationBean( root, ( ServerConfigurationV150 ) serverConfiguration );

        // User Partitions Beans
        createUserPartitionsConfigurationsBean( root, ( ServerConfigurationV150 ) serverConfiguration );

        // CustomEditors Bean
        createCustomEditorsBean( root );

        Document stylizedDocument = null;
        try
        {
            stylizedDocument = styleDocument( document );
        }
        catch ( TransformerException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        stylizedDocument.addDocType( "beans", "-//SPRING//DTD BEAN//EN",
            "http://www.springframework.org/dtd/spring-beans.dtd" );

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
    private void createEnvironmentBean( Element root, ServerConfigurationV150 serverConfiguration )
    {
        Element environmentBean = root.addElement( "bean" );
        environmentBean.addAttribute( "id", "environment" );
        environmentBean.addAttribute( "class", "org.springframework.beans.factory.config.PropertiesFactoryBean" );

        Element propertyElement = environmentBean.addElement( "property" );
        propertyElement.addAttribute( "name", "properties" );
        Element propsElement = propertyElement.addElement( "props" );

        // Key 'java.naming.security.authentication'
        Element propElement = propsElement.addElement( "prop" );
        propElement.addAttribute( "key", "java.naming.security.authentication" );
        propElement.setText( "simple" );

        // Key 'java.naming.security.principal'
        propElement = propsElement.addElement( "prop" );
        propElement.addAttribute( "key", "java.naming.security.principal" );
        propElement.setText( serverConfiguration.getPrincipal() );

        // Key 'java.naming.security.credentials'
        propElement = propsElement.addElement( "prop" );
        propElement.addAttribute( "key", "java.naming.security.credentials" );
        propElement.setText( serverConfiguration.getPassword() );

        // Key 'java.naming.ldap.attributes.binary'
        if ( !serverConfiguration.getBinaryAttributes().isEmpty() )
        {
            propElement = propsElement.addElement( "prop" );
            propElement.addAttribute( "key", "java.naming.ldap.attributes.binary" );
            StringBuffer sb = new StringBuffer();
            for ( String attribute : serverConfiguration.getBinaryAttributes() )
            {
                sb.append( attribute );
                sb.append( " " );
            }
            String attributes = sb.toString();
            propElement.setText( attributes.substring( 0, attributes.length() - 1 ) );
        }

    }


    /**
     * Creates the Configuration Bean.
     *
     * @param root
     *      the root Element
     * @param serverConfiguration
     *      the Server Configuration
     */
    private void createConfigurationBean( Element root, ServerConfigurationV150 serverConfiguration )
    {
        Element configurationBean = root.addElement( "bean" );
        configurationBean.addAttribute( "id", "configuration" );
        configurationBean.addAttribute( "class",
            "org.apache.directory.server.configuration.MutableServerStartupConfiguration" );

        // Working directory
        Element propertyElement = configurationBean.addElement( "property" );
        propertyElement.addAttribute( "name", "workingDirectory" );
        propertyElement.addAttribute( "value", "example.com" ); // Ask Alex about this value.

        // SynchPeriodMillis
        propertyElement = configurationBean.addElement( "property" );
        propertyElement.addAttribute( "name", "synchPeriodMillis" );
        propertyElement.addAttribute( "value", "" + serverConfiguration.getSynchronizationPeriod() );

        // MaxTimeLimit
        propertyElement = configurationBean.addElement( "property" );
        propertyElement.addAttribute( "name", "maxTimeLimit" );
        propertyElement.addAttribute( "value", "" + serverConfiguration.getMaxTimeLimit() );

        // MaxSizeLimit
        propertyElement = configurationBean.addElement( "property" );
        propertyElement.addAttribute( "name", "maxSizeLimit" );
        propertyElement.addAttribute( "value", "" + serverConfiguration.getMaxSizeLimit() );

        // MaxThreads
        propertyElement = configurationBean.addElement( "property" );
        propertyElement.addAttribute( "name", "maxThreads" );
        propertyElement.addAttribute( "value", "" + serverConfiguration.getMaxThreads() );

        // AllowAnonymousAccess
        propertyElement = configurationBean.addElement( "property" );
        propertyElement.addAttribute( "name", "allowAnonymousAccess" );
        propertyElement.addAttribute( "value", "" + serverConfiguration.isAllowAnonymousAccess() );

        // AccessControlEnabled
        propertyElement = configurationBean.addElement( "property" );
        propertyElement.addAttribute( "name", "accessControlEnabled" );
        propertyElement.addAttribute( "value", "" + serverConfiguration.isEnableAccessControl() );

        // Enable NTP
        propertyElement = configurationBean.addElement( "property" );
        propertyElement.addAttribute( "name", "enableNtp" );
        propertyElement.addAttribute( "value", "" + serverConfiguration.isEnableNTP() );

        // EnableKerberos
        propertyElement = configurationBean.addElement( "property" );
        propertyElement.addAttribute( "name", "enableKerberos" );
        propertyElement.addAttribute( "value", "" + serverConfiguration.isEnableKerberos() );

        // EnableChangePassword
        propertyElement = configurationBean.addElement( "property" );
        propertyElement.addAttribute( "name", "enableChangePassword" );
        propertyElement.addAttribute( "value", "" + serverConfiguration.isEnableChangePassword() );

        // DenormalizeOpAttrsEnabled
        propertyElement = configurationBean.addElement( "property" );
        propertyElement.addAttribute( "name", "denormalizeOpAttrsEnabled" );
        propertyElement.addAttribute( "value", "" + serverConfiguration.isDenormalizeOpAttr() );

        // LdapPort
        propertyElement = configurationBean.addElement( "property" );
        propertyElement.addAttribute( "name", "ldapPort" );
        propertyElement.addAttribute( "value", "" + serverConfiguration.getPort() );

        // SystemPartitionConfiguration
        propertyElement = configurationBean.addElement( "property" );
        propertyElement.addAttribute( "name", "systemPartitionConfiguration" );
        propertyElement.addAttribute( "ref", "systemPartitionConfiguration" );

        // PartitionConfigurations
        propertyElement = configurationBean.addElement( "property" );
        propertyElement.addAttribute( "name", "partitionConfigurations" );
        if ( serverConfiguration.getPartitions().size() > 1 )
        {
            Element setElement = propertyElement.addElement( "set" );
            int partitionCounter = 1;
            for ( Partition partition : serverConfiguration.getPartitions() )
            {
                if ( !partition.isSystemPartition() )
                {
                    setElement.addElement( "ref" ).addAttribute( "bean", "partition-" + partitionCounter );
                    partitionCounter++;
                }
            }
        }

        // ExtendedOperationHandlers
        propertyElement = configurationBean.addElement( "property" );
        propertyElement.addAttribute( "name", "extendedOperationHandlers" );
        if ( serverConfiguration.getExtendedOperations().size() > 1 )
        {
            Element listElement = propertyElement.addElement( "list" );
            for ( ExtendedOperation extendedOperation : serverConfiguration.getExtendedOperations() )
            {
                listElement.addElement( "bean" ).addAttribute( "class", extendedOperation.getClassType() );
            }
        }

        // InterceptorConfigurations
        propertyElement = configurationBean.addElement( "property" );
        propertyElement.addAttribute( "name", "interceptorConfigurations" );
        if ( serverConfiguration.getInterceptors().size() > 1 )
        {
            Element listElement = propertyElement.addElement( "list" );
            for ( Interceptor interceptor : serverConfiguration.getInterceptors() )
            {
                Element interceptorBeanElement = listElement.addElement( "bean" );
                interceptorBeanElement.addAttribute( "class",
                    "org.apache.directory.server.core.configuration.MutableInterceptorConfiguration" );

                Element interceptorPropertyElement = interceptorBeanElement.addElement( "property" );
                interceptorPropertyElement.addAttribute( "name", "name" );
                interceptorPropertyElement.addAttribute( "value", interceptor.getName() );

                interceptorPropertyElement = interceptorBeanElement.addElement( "property" );
                interceptorPropertyElement.addAttribute( "name", "interceptor" );
                interceptorPropertyElement.addElement( "bean" ).addAttribute( "class",
                    ( interceptor.getClassType() == null ? "" : interceptor.getClassType() ) );
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
    private void createSystemPartitionConfigurationBean( Element root, ServerConfigurationV150 serverConfiguration )
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
            createPartitionConfigurationBean( root, systemPartition, "systemPartitionConfiguration" );
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
    private void createUserPartitionsConfigurationsBean( Element root, ServerConfigurationV150 serverConfiguration )
    {
        int counter = 1;
        for ( Partition partition : serverConfiguration.getPartitions() )
        {
            if ( !partition.isSystemPartition() )
            {
                createPartitionConfigurationBean( root, partition, "partition-" + counter );
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
    private void createPartitionConfigurationBean( Element root, Partition partition, String name )
    {
        Element partitionBean = root.addElement( "bean" );
        partitionBean.addAttribute( "id", name );
        partitionBean.addAttribute( "class",
            "org.apache.directory.server.core.partition.impl.btree.MutableBTreePartitionConfiguration" );

        // Name
        Element propertyElement = partitionBean.addElement( "property" );
        propertyElement.addAttribute( "name", "name" );
        propertyElement.addAttribute( "value", partition.getId() );

        // CacheSize
        propertyElement = partitionBean.addElement( "property" );
        propertyElement.addAttribute( "name", "cacheSize" );
        propertyElement.addAttribute( "value", "" + partition.getCacheSize() );

        // Suffix
        propertyElement = partitionBean.addElement( "property" );
        propertyElement.addAttribute( "name", "suffix" );
        propertyElement.addAttribute( "value", partition.getSuffix() );

        // OptimizerEnabled
        propertyElement = partitionBean.addElement( "property" );
        propertyElement.addAttribute( "name", "optimizerEnabled" );
        propertyElement.addAttribute( "value", "" + partition.isEnableOptimizer() );

        // SynchOnWrite
        propertyElement = partitionBean.addElement( "property" );
        propertyElement.addAttribute( "name", "synchOnWrite" );
        propertyElement.addAttribute( "value", "" + partition.isSynchronizationOnWrite() );

        // Indexed Attributes
        propertyElement = partitionBean.addElement( "property" );
        propertyElement.addAttribute( "name", "indexedAttributes" );
        if ( partition.getIndexedAttributes().size() > 1 )
        {
            Element setElement = propertyElement.addElement( "set" );
            for ( IndexedAttribute indexedAttribute : partition.getIndexedAttributes() )
            {
                Element beanElement = setElement.addElement( "bean" );
                beanElement.addAttribute( "class",
                    "org.apache.directory.server.core.partition.impl.btree.MutableIndexConfiguration" );

                // AttributeID
                Element beanPropertyElement = beanElement.addElement( "property" );
                beanPropertyElement.addAttribute( "name", "attributeId" );
                beanPropertyElement.addAttribute( "value", indexedAttribute.getAttributeId() );

                // CacheSize
                beanPropertyElement = beanElement.addElement( "property" );
                beanPropertyElement.addAttribute( "name", "cacheSize" );
                beanPropertyElement.addAttribute( "value", "" + indexedAttribute.getCacheSize() );
            }
        }

        // ContextEntry
        propertyElement = partitionBean.addElement( "property" );
        propertyElement.addAttribute( "name", "contextEntry" );
        if ( partition.getContextEntry() != null )
        {
            Element valueElement = propertyElement.addElement( "value" );

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
                        sb.append( attribute.getID() + ": " + values.nextElement() + "\n" );
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
    private void createCustomEditorsBean( Element root )
    {
        Element customEditorsBean = root.addElement( "bean" );
        customEditorsBean.addAttribute( "class", "org.springframework.beans.factory.config.CustomEditorConfigurer" );
        Element propertyElement = customEditorsBean.addElement( "property" );
        propertyElement.addAttribute( "name", "customEditors" );
        Element mapElement = propertyElement.addElement( "map" );
        Element entryElement = mapElement.addElement( "entry" );
        entryElement.addAttribute( "key", "javax.naming.directory.Attributes" );
        Element entryBeanElement = entryElement.addElement( "bean" );
        entryBeanElement.addAttribute( "class",
            "org.apache.directory.server.core.configuration.AttributesPropertyEditor" );
    }

}
