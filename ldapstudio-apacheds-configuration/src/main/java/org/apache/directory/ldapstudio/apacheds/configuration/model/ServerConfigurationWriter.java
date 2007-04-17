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
package org.apache.directory.ldapstudio.apacheds.configuration.model;


import java.io.BufferedWriter;
import java.io.FileWriter;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.directory.ldapstudio.apacheds.configuration.Activator;
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
     * Writes the Server Configuration to disk.
     *
     * @param serverConfiguration
     *      the Server Configuration
     * @throws ServerConfigurationWriterException
     *      if an error occurrs when writing the Server Configuration file
     */
    public void write( ServerConfiguration serverConfiguration ) throws ServerConfigurationWriterException
    {
        try
        {
            BufferedWriter outFile = new BufferedWriter( new FileWriter( serverConfiguration.getPath() ) );

            Document document = DocumentHelper.createDocument();
            Element root = document.addElement( "beans" );

            // Environment Bean
            createEnvironmentBean( root, serverConfiguration );

            // Configuration Bean
            createConfigurationBean( root, serverConfiguration );

            // System Partition Configuration Bean
            createSystemPartitionConfigurationBean( root, serverConfiguration );

            // User Partitions Beans
            createUserPartitionsConfigurationsBean( root, serverConfiguration );

            // CustomEditors Bean
            createCustomEditorsBean( root );

            Document stylizedDocuement = styleDocument( document );
            stylizedDocuement.addDocType( "beans", "-//SPRING//DTD BEAN//EN",
                "http://www.springframework.org/dtd/spring-beans.dtd" );
            outFile.write( stylizedDocuement.asXML() );
            outFile.close();
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
    private void createEnvironmentBean( Element root, ServerConfiguration serverConfiguration )
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
    }


    /**
     * Creates the Configuration Bean.
     *
     * @param root
     *      the root Element
     * @param serverConfiguration
     *      the Server Configuration
     */
    private void createConfigurationBean( Element root, ServerConfiguration serverConfiguration )
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
        propertyElement.addAttribute( "value", "false" ); // TODO Add a UI Field for editing this value.

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
    private void createSystemPartitionConfigurationBean( Element root, ServerConfiguration serverConfiguration )
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
    private void createUserPartitionsConfigurationsBean( Element root, ServerConfiguration serverConfiguration )
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
        propertyElement.addAttribute( "value", partition.getName() );

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


    /**
     * XML Pretty Printer XSLT Tranformation
     * 
     * @param document
     *      the Dom4j Document
     * @return
     *      the stylized Document
     * @throws TransformerException 
     */
    private Document styleDocument( Document document ) throws TransformerException
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
