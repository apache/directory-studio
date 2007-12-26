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


import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.directory.studio.apacheds.configuration.Activator;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;


/**
 * This class represents the Server Configuration Writer. It can be used to save a 'server.xml' file from.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ServerConfigurationWriter
{
    /**
     * TODO
     *
     * @param serverConfiguration
     *      the Server Configuration
     * @throws ServerConfigurationWriterException
     *      if an error occurrs when writing the Server Configuration file
     */
    public static String toXml( ServerConfiguration serverConfiguration ) throws ServerConfigurationWriterException
    {
        try
        {
            Document document = DocumentHelper.createDocument();
            Element root = document.addElement( "beans" );

            // Environment Bean
            createEnvironmentBean( root, serverConfiguration );

            // Change Password Configuration Bean
            createChangePasswordConfigurationBean( root, serverConfiguration );

            // NTP Configuration Bean
            createNtpConfigurationBean( root, serverConfiguration );

            // DNS Configuration Bean
            createDnsConfigurationBean( root, serverConfiguration );

            // KDC Configuration Bean
            createKdcConfigurationBean( root, serverConfiguration );

            // LDAPS Configuration Bean
            createLdapsConfigurationBean( root, serverConfiguration );

            // LDAP Configuration Bean
            createLdapConfigurationBean( root, serverConfiguration );

            // Configuration Bean
            createConfigurationBean( root, serverConfiguration );

            // System Partition Configuration Bean
            createSystemPartitionConfigurationBean( root, serverConfiguration );

            // User Partitions Beans
            createUserPartitionsConfigurationsBean( root, serverConfiguration );

            // CustomEditors Bean
            createCustomEditorsBean( root );

            Document stylizedDocument = styleDocument( document );
            stylizedDocument.addDocType( "beans", "-//SPRING//DTD BEAN//EN",
                "http://www.springframework.org/dtd/spring-beans.dtd" );

            return stylizedDocument.asXML();
        }
        catch ( Exception e )
        {
            ServerConfigurationWriterException exception = new ServerConfigurationWriterException( e.getMessage(), e
                .getCause() );
            exception.setStackTrace( e.getStackTrace() );
            throw exception;
        }
    }


    /**
     * Creates the Environment Bean
     *
     * @param root
     *      the root Element
     * @param serverConfiguration
     *      the Server Configuration
     */
    private static void createEnvironmentBean( Element root, ServerConfiguration serverConfiguration )
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
     * Creates the Change Password Configuration Bean.
     *
     * @param root
     *      the root Element
     * @param serverConfiguration
     *      the Server Configuration
     */
    private static void createChangePasswordConfigurationBean( Element root, ServerConfiguration serverConfiguration )
    {
        createProtocolConfigurationBean( root, "changePasswordConfiguration",
            "org.apache.directory.server.changepw.ChangePasswordConfiguration", serverConfiguration
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
    private static void createNtpConfigurationBean( Element root, ServerConfiguration serverConfiguration )
    {
        createProtocolConfigurationBean( root, "ntpConfiguration", "org.apache.directory.server.ntp.NtpConfiguration",
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
    private static void createDnsConfigurationBean( Element root, ServerConfiguration serverConfiguration )
    {
        createProtocolConfigurationBean( root, "dnsConfiguration", "org.apache.directory.server.dns.DnsConfiguration",
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
    private static void createKdcConfigurationBean( Element root, ServerConfiguration serverConfiguration )
    {
        createProtocolConfigurationBean( root, "kdcConfiguration", "org.apache.directory.server.kdc.KdcConfiguration",
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
    private static void createLdapsConfigurationBean( Element root, ServerConfiguration serverConfiguration )
    {
        Element ldapsConfiguration = createProtocolConfigurationBean( root, "ldapsConfiguration",
            "org.apache.directory.server.ldap.LdapConfiguration", serverConfiguration.isEnableLdaps(),
            serverConfiguration.getLdapsPort() );

        // Enable LDAPS
        Element enableLdapsPropertyElement = ldapsConfiguration.addElement( "property" );
        enableLdapsPropertyElement.addAttribute( "name", "enableLdaps" );
        enableLdapsPropertyElement.addAttribute( "value", "" + true );
    }


    /**
     * Creates the LDAP Configuration Bean.
     *
     * @param root
     *      the root Element
     * @param serverConfiguration
     *      the Server Configuration
     */
    private static void createLdapConfigurationBean( Element root, ServerConfiguration serverConfiguration )
    {
        Element ldapConfiguration = createProtocolConfigurationBean( root, "ldapConfiguration",
            "org.apache.directory.server.ldap.LdapConfiguration", true, serverConfiguration.getLdapPort() );

        // AllowAnonymousAccess
        Element propertyElement = ldapConfiguration.addElement( "property" );
        propertyElement.addAttribute( "name", "allowAnonymousAccess" );
        propertyElement.addAttribute( "value", "" + serverConfiguration.isAllowAnonymousAccess() );

        // Supported Mechanisms
        propertyElement = ldapConfiguration.addElement( "property" );
        propertyElement.addAttribute( "name", "supportedMechanisms" );
        if ( serverConfiguration.getSupportedMechanisms().size() > 1 )
        {
            Element listElement = propertyElement.addElement( "list" );
            for ( String supportedMechanism : serverConfiguration.getSupportedMechanisms() )
            {
                listElement.addElement( "value" ).setText( supportedMechanism );
            }
        }

        // SASL Host
        propertyElement = ldapConfiguration.addElement( "property" );
        propertyElement.addAttribute( "name", "saslHost" );
        propertyElement.addAttribute( "value", serverConfiguration.getSaslHost() );

        // SASL Principal
        propertyElement = ldapConfiguration.addElement( "property" );
        propertyElement.addAttribute( "name", "saslPrincipal" );
        propertyElement.addAttribute( "value", serverConfiguration.getSaslPrincipal() );

        // SASL QOP
        propertyElement = ldapConfiguration.addElement( "property" );
        propertyElement.addAttribute( "name", "saslQop" );
        if ( serverConfiguration.getSaslQops().size() > 1 )
        {
            Element listElement = propertyElement.addElement( "list" );
            for ( String saslQop : serverConfiguration.getSaslQops() )
            {
                listElement.addElement( "value" ).setText( saslQop );
            }
        }

        // SASL Realms
        propertyElement = ldapConfiguration.addElement( "property" );
        propertyElement.addAttribute( "name", "saslRealms" );
        if ( serverConfiguration.getSaslRealms().size() > 1 )
        {
            Element listElement = propertyElement.addElement( "list" );
            for ( String saslRealm : serverConfiguration.getSaslRealms() )
            {
                listElement.addElement( "value" ).setText( saslRealm );
            }
        }

        // Search Base DN
        propertyElement = ldapConfiguration.addElement( "property" );
        propertyElement.addAttribute( "name", "searchBaseDN" );
        propertyElement.addAttribute( "value", serverConfiguration.getSearchBaseDn() );

        // MaxTimeLimit
        propertyElement = ldapConfiguration.addElement( "property" );
        propertyElement.addAttribute( "name", "maxTimeLimit" );
        propertyElement.addAttribute( "value", "" + serverConfiguration.getMaxTimeLimit() );

        // MaxSizeLimit
        propertyElement = ldapConfiguration.addElement( "property" );
        propertyElement.addAttribute( "name", "maxSizeLimit" );
        propertyElement.addAttribute( "value", "" + serverConfiguration.getMaxSizeLimit() );

        // ExtendedOperationHandlers
        propertyElement = ldapConfiguration.addElement( "property" );
        propertyElement.addAttribute( "name", "extendedOperationHandlers" );
        if ( serverConfiguration.getExtendedOperations().size() > 1 )
        {
            Element listElement = propertyElement.addElement( "list" );
            for ( ExtendedOperation extendedOperation : serverConfiguration.getExtendedOperations() )
            {
                listElement.addElement( "bean" ).addAttribute( "class", extendedOperation.getClassType() );
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
        Element protocolConfigurationBean = root.addElement( "bean" );
        protocolConfigurationBean.addAttribute( "id", id );
        protocolConfigurationBean.addAttribute( "class", className );

        // Enabled
        Element enabledPropertyElement = protocolConfigurationBean.addElement( "property" );
        enabledPropertyElement.addAttribute( "name", "enabled" );
        enabledPropertyElement.addAttribute( "value", "" + enabled );

        // IP Port
        Element ipPortPropertyElement = protocolConfigurationBean.addElement( "property" );
        ipPortPropertyElement.addAttribute( "name", "ipPort" );
        ipPortPropertyElement.addAttribute( "value", "" + ipPort );

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
    private static void createConfigurationBean( Element root, ServerConfiguration serverConfiguration )
    {
        Element configurationBean = root.addElement( "bean" );
        configurationBean.addAttribute( "id", "configuration" );
        configurationBean.addAttribute( "class",
            "org.apache.directory.server.configuration.MutableServerStartupConfiguration" );

        // Working directory
        Element propertyElement = configurationBean.addElement( "property" );
        propertyElement.addAttribute( "name", "workingDirectory" );
        propertyElement.addAttribute( "value", "example.com" ); // TODO Ask Alex about this value.

        // LDIF Directory
        // TODO Ask Alex about this value.

        // LDIF Filters
        // TODO Ask Alex about this value.

        // SynchPeriodMillis
        propertyElement = configurationBean.addElement( "property" );
        propertyElement.addAttribute( "name", "synchPeriodMillis" );
        propertyElement.addAttribute( "value", "" + serverConfiguration.getSynchronizationPeriod() );

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

        // DenormalizeOpAttrsEnabled
        propertyElement = configurationBean.addElement( "property" );
        propertyElement.addAttribute( "name", "denormalizeOpAttrsEnabled" );
        propertyElement.addAttribute( "value", "" + serverConfiguration.isDenormalizeOpAttr() );

        // NTP Configuration Ref
        propertyElement = configurationBean.addElement( "property" );
        propertyElement.addAttribute( "name", "ntpConfiguration" );
        propertyElement.addAttribute( "ref", "ntpConfiguration" );

        // DNS Configuration Ref
        propertyElement = configurationBean.addElement( "property" );
        propertyElement.addAttribute( "name", "dnsConfiguration" );
        propertyElement.addAttribute( "ref", "dnsConfiguration" );

        // Change Password Configuration Ref
        propertyElement = configurationBean.addElement( "property" );
        propertyElement.addAttribute( "name", "changePasswordConfiguration" );
        propertyElement.addAttribute( "ref", "changePasswordConfiguration" );

        // KDC Configuration Ref
        propertyElement = configurationBean.addElement( "property" );
        propertyElement.addAttribute( "name", "kdcConfiguration" );
        propertyElement.addAttribute( "ref", "kdcConfiguration" );

        // LDAPS Configuration Ref
        propertyElement = configurationBean.addElement( "property" );
        propertyElement.addAttribute( "name", "ldapsConfiguration" );
        propertyElement.addAttribute( "ref", "ldapsConfiguration" );

        // LDAP Configuration Ref
        propertyElement = configurationBean.addElement( "property" );
        propertyElement.addAttribute( "name", "ldapConfiguration" );
        propertyElement.addAttribute( "ref", "ldapConfiguration" );

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
                interceptorPropertyElement.addAttribute( "name", "interceptorClassName" );
                interceptorPropertyElement.addAttribute( "value", ( interceptor.getClassType() == null ? ""
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
    private static void createSystemPartitionConfigurationBean( Element root, ServerConfiguration serverConfiguration )
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
    private static void createUserPartitionsConfigurationsBean( Element root, ServerConfiguration serverConfiguration )
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
    private static void createPartitionConfigurationBean( Element root, Partition partition, String name )
    {
        Element partitionBean = root.addElement( "bean" );
        partitionBean.addAttribute( "id", name );
        partitionBean.addAttribute( "class",
            "org.apache.directory.server.core.partition.impl.btree.MutableBTreePartitionConfiguration" );

        // ID
        Element propertyElement = partitionBean.addElement( "property" );
        propertyElement.addAttribute( "name", "id" );
        propertyElement.addAttribute( "value", partition.getId() );

        // CacheSize
        propertyElement = partitionBean.addElement( "property" );
        propertyElement.addAttribute( "name", "cacheSize" );
        propertyElement.addAttribute( "value", "" + partition.getCacheSize() );

        // Suffix
        propertyElement = partitionBean.addElement( "property" );
        propertyElement.addAttribute( "name", "suffix" );
        propertyElement.addAttribute( "value", partition.getSuffix() );
        
        // PartitionClassName
        propertyElement = partitionBean.addElement( "property" );
        propertyElement.addAttribute( "name", "partitionClassName" );
        propertyElement.addAttribute( "value", "org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmPartition" );

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
    private static void createCustomEditorsBean( Element root )
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


    /**
     * XML Pretty Printer XSLT Tranformation
     * 
     * @param document
     *      the Dom4j Document
     * @return
     *      the stylized Document
     * @throws TransformerException 
     */
    private static Document styleDocument( Document document ) throws TransformerException
    {
        // load the transformer using JAXP
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = null;

        transformer = factory
            .newTransformer( new StreamSource( Activator.class.getResourceAsStream( "template.xslt" ) ) );

        // now lets style the given document
        DocumentSource source = new DocumentSource( document );
        DocumentResult result = new DocumentResult();

        transformer.transform( source, result );

        // return the transformed document
        Document transformedDoc = result.getDocument();
        return transformedDoc;
    }
}
