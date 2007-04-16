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


import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
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
     * TODO write.
     *
     * @param serverConfiguration
     */
    public void write( ServerConfiguration serverConfiguration )
    {
        System.out.println( "Writing file to disk." );

        Document document = DocumentHelper.createDocument();
        Element root = document.addElement( "beans" );

        // Environment Bean
        createEnvironmentBean( root, serverConfiguration );

        // Configuration Bean
        createConfigurationBean( root, serverConfiguration );

        // System Partition Configuration Bean
        createSystemPartitionConfigurationBean( root, serverConfiguration );

        // User Partitions Beans
        createUserPartitionConfigurationsBean( root, serverConfiguration );

        System.out.println( styleDocument( document ).asXML() );
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

        Element propElement = propsElement.addElement( "prop" );
        propElement.addAttribute( "key", "java.naming.security.authentication" );
        propElement.setText( "simple" );

        propElement = propsElement.addElement( "prop" );
        propElement.addAttribute( "key", "java.naming.security.principal" );
        propElement.setText( serverConfiguration.getPrincipal() );

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
        propertyElement.addAttribute( "name", "enableNTP" );
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
        propertyElement.addAttribute( "value", "systemPartitionConfiguration" );

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
                interceptorPropertyElement.addElement( "bean" ).addAttribute( "class", interceptor.getClassType() );
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
    private void createUserPartitionConfigurationsBean( Element root, ServerConfiguration serverConfiguration )
    {
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
        Element systemPartitionBean = root.addElement( "bean" );
        systemPartitionBean.addAttribute( "id", name );
        systemPartitionBean.addAttribute( "class",
            "org.apache.directory.server.core.partition.impl.btree.MutableBTreePartitionConfiguration" );
    }


    /**
     * XML Pretty Printer XSLT Tranformation
     * 
     * @param document
     *      the Dom4j Document
     * @return
     */
    private Document styleDocument( Document document )
    {
        // load the transformer using JAXP
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try
        {
            transformer = factory.newTransformer( new StreamSource( Activator.class
                .getResourceAsStream( "template.xslt" ) ) );
        }
        catch ( TransformerConfigurationException e1 )
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // now lets style the given document
        DocumentSource source = new DocumentSource( document );
        DocumentResult result = new DocumentResult();
        try
        {
            transformer.transform( source, result );
        }
        catch ( TransformerException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // return the transformed document
        Document transformedDoc = result.getDocument();
        return transformedDoc;
    }
}
