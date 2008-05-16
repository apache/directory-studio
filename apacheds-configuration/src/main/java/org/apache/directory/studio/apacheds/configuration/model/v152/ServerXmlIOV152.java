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
package org.apache.directory.studio.apacheds.configuration.model.v152;


import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.xml.transform.TransformerException;

import org.apache.directory.studio.apacheds.configuration.model.AbstractServerXmlIO;
import org.apache.directory.studio.apacheds.configuration.model.ServerConfiguration;
import org.apache.directory.studio.apacheds.configuration.model.ServerXmlIO;
import org.apache.directory.studio.apacheds.configuration.model.ServerXmlIOException;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.SAXReader;


/**
 * This class implements a parser and a writer for the 'server.xml' file of 
 * Apache Directory Server version 1.5.2.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ServerXmlIOV152 extends AbstractServerXmlIO implements ServerXmlIO
{
    private static final Namespace NAMESPACE_APACHEDS = new Namespace( null, "http://apacheds.org/config/1.0" );
    private static final Namespace NAMESPACE_SPRINGFRAMEWORK = new Namespace( "s",
        "http://www.springframework.org/schema/beans" );
    private static final Namespace NAMESPACE_XBEAN_SPRING = new Namespace( "spring",
        "http://xbean.apache.org/schemas/spring/1.0" );


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
        Element rootElement = document.getRootElement();

        if ( rootElement != null )
        {
            // Checking if the root element is named 'beans'
            if ( "beans".equalsIgnoreCase( rootElement.getName() ) )
            {
                // Looking for the 'apacheDS' element
                Element apacheDSElement = rootElement.element( "apacheDS" );
                return ( apacheDSElement != null );
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
            // Reading and creating the document
            SAXReader reader = new SAXReader();
            Document document = reader.read( is );

            // Parsing the document
            ServerConfigurationV152 serverConfiguration = new ServerConfigurationV152();
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
    private void parse( Document document, ServerConfigurationV152 serverConfiguration ) throws NumberFormatException,
        BooleanFormatException, ServerXmlIOException
    {
        Element rootElement = document.getRootElement();

        // Reading the 'defaultDirectoryService' Bean
        readDefaultDirectoryServiceBean( rootElement, serverConfiguration );

        // Reading the 'changePasswordServer' Bean
        readChangePasswordServerBean( rootElement, serverConfiguration );

        // Reading the 'kdcServer' Bean
        readKdcServerBean( rootElement, serverConfiguration );

        // Reading the 'ntpServer' Bean
        readNtpServerBean( rootElement, serverConfiguration );

        // Reading the 'dnsServer' Bean
        readDnsServerBean( rootElement, serverConfiguration );

        // Reading the 'ldapsServer' Bean
        readLdapsServerBean( rootElement, serverConfiguration );

        // Reading the 'LdapServer' Bean
        readLdapServerBean( rootElement, serverConfiguration );

        // Reading the 'apacheDS' Bean
        readApacheDSBean( rootElement, serverConfiguration );
    }


    private void readDefaultDirectoryServiceBean( Element element, ServerConfigurationV152 serverConfiguration )
        throws ServerXmlIOException, NumberFormatException, BooleanFormatException
    {
        Element defaultDirectoryServiceElement = element.element( "defaultDirectoryService" );
        if ( defaultDirectoryServiceElement == null )
        {
            throw new ServerXmlIOException( "Unable to find the 'defaultDirectoryService' tag." );
        }
        else
        {
            // Access Control Enabled
            org.dom4j.Attribute accessControlEnabledAttribute = defaultDirectoryServiceElement
                .attribute( "accessControlEnabled" );
            if ( accessControlEnabledAttribute == null )
            {
                // If the 'accessControlEnabled' attribute does not exists,
                // we throw an exception
                throw new ServerXmlIOException(
                    "Unable to find the 'accessControlEnabled' attribute for the default directory service bean." );
            }
            else
            {
                serverConfiguration.setEnableAccessControl( parseBoolean( accessControlEnabledAttribute.getValue() ) );
            }

            // Denormalize Op Attrs Enabled
            org.dom4j.Attribute denormalizeOpAttrsEnabledAttribute = defaultDirectoryServiceElement
                .attribute( "denormalizeOpAttrsEnabled" );
            if ( denormalizeOpAttrsEnabledAttribute == null )
            {
                // If the 'denormalizeOpAttrsEnabled' attribute does not exists,
                // we throw an exception
                throw new ServerXmlIOException(
                    "Unable to find the 'denormalizeOpAttrsEnabled' attribute for the default directory service bean." );
            }
            else
            {
                serverConfiguration
                    .setDenormalizeOpAttr( parseBoolean( denormalizeOpAttrsEnabledAttribute.getValue() ) );
            }

            // System partition
            readSystemPartition( defaultDirectoryServiceElement, serverConfiguration );

            // Other partitions
            readPartitions( defaultDirectoryServiceElement, serverConfiguration );

            // Interceptors
            readInterceptors( defaultDirectoryServiceElement, serverConfiguration );
        }
    }


    /**
     * Reads the system partition
     *
     * @param element
     *      the element
     * @param serverConfiguration
     *      the server configuration
     * @throws ServerXmlIOException
     * @throws BooleanFormatException 
     */
    private void readSystemPartition( Element element, ServerConfigurationV152 serverConfiguration )
        throws ServerXmlIOException, NumberFormatException, BooleanFormatException
    {
        // Getting the 'systemPartition' element
        Element systemPartitionElement = element.element( "systemPartition" );
        if ( systemPartitionElement == null )
        {
            // If the 'systemPartition' element does not exists,
            // we throw an exception
            throw new ServerXmlIOException( "Unable to find the 'systemPartition' element." );
        }
        else
        {
            // Getting the 'jdbmPartition' element
            Element jdbmPartitionElement = systemPartitionElement.element( "jdbmPartition" );
            if ( jdbmPartitionElement == null )
            {
                // If the 'jdbmPartition' element does not exists,
                // we throw an exception
                throw new ServerXmlIOException( "Unable to find the 'jdbmPartition' element for the system partition." );
            }
            else
            {
                // Creating the system partition
                Partition systemPartition = new Partition();
                systemPartition.setSystemPartition( true );

                // Reading the partition
                readPartition( jdbmPartitionElement, systemPartition );

                // Adding the partition to the server configuration
                serverConfiguration.addPartition( systemPartition );
            }
        }
    }


    /**
     * Reads a partition.
     *
     * @param element
     *      the partition element
     * @param partition
     *      the partition
     * @throws ServerXmlIOException
     * @throws NumberFormatException
     * @throws BooleanFormatException
     */
    private void readPartition( Element element, Partition partition ) throws ServerXmlIOException,
        NumberFormatException, BooleanFormatException
    {
        // Id
        org.dom4j.Attribute idAttribute = element.attribute( "id" );
        if ( idAttribute == null )
        {
            // If the 'id' attribute does not exists,
            // we throw an exception
            throw new ServerXmlIOException( "Unable to find the 'id' attribute for a partition." );
        }
        else
        {
            partition.setId( idAttribute.getValue() );
        }

        // Cache Size
        org.dom4j.Attribute cacheSizeAttribute = element.attribute( "cacheSize" );
        if ( cacheSizeAttribute == null )
        {
            // If the 'cacheSize' attribute does not exists,
            // we throw an exception
            throw new ServerXmlIOException( "Unable to find the 'cacheSize' attribute for a partition." );
        }
        else
        {
            partition.setCacheSize( Integer.parseInt( cacheSizeAttribute.getValue() ) );
        }

        // Suffix
        org.dom4j.Attribute suffixAttribute = element.attribute( "suffix" );
        if ( suffixAttribute == null )
        {
            // If the 'suffix' attribute does not exists,
            // we throw an exception
            throw new ServerXmlIOException( "Unable to find the 'suffix' attribute for a partition." );
        }
        else
        {
            partition.setSuffix( suffixAttribute.getValue() );
        }

        // Optimizer Enabled
        org.dom4j.Attribute optimizerEnabledAttribute = element.attribute( "optimizerEnabled" );
        if ( optimizerEnabledAttribute == null )
        {
            // If the 'optimizeEnabled' attribute does not exists,
            // we throw an exception
            throw new ServerXmlIOException( "Unable to find the 'OptimizerEnabled' attribute for a partition." );
        }
        else
        {
            partition.setEnableOptimizer( parseBoolean( optimizerEnabledAttribute.getValue() ) );
        }

        // Sync On Write
        org.dom4j.Attribute syncOnWriteAttribute = element.attribute( "syncOnWrite" );
        if ( syncOnWriteAttribute == null )
        {
            // If the 'syncOnWrite' attribute does not exists,
            // we throw an exception
            throw new ServerXmlIOException( "Unable to find the 'syncOnWrite' attribute for a partition." );
        }
        else
        {
            partition.setSynchronizationOnWrite( parseBoolean( syncOnWriteAttribute.getValue() ) );
        }

        // Indexed attributes
        partition.setIndexedAttributes( readIndexedAttributes( element ) );

        // Context Entry
        partition.setContextEntry( readContextEntry( element ) );
    }


    /**
     * Reads and returns the indexed attributes.
     *
     * @param element
     *      the element
     * @return
     *      the list of indexed attributes
     * @throws NumberFormatException
     */
    private List<IndexedAttribute> readIndexedAttributes( Element element ) throws NumberFormatException
    {
        List<IndexedAttribute> indexedAttributes = new ArrayList<IndexedAttribute>();

        // Getting the 'indexedAttributes' element
        Element indexedAttributesElement = element.element( "indexedAttributes" );
        if ( indexedAttributesElement != null )
        {
            // Looping on 'jdbmIndex' elements
            for ( Iterator<?> i = indexedAttributesElement.elementIterator( "jdbmIndex" ); i.hasNext(); )
            {
                // Getting the 'jdbmIndex' element
                Element jdbmIndexElement = ( Element ) i.next();

                // Getting the 'attributeId' attribute
                org.dom4j.Attribute attributeIdAttribute = jdbmIndexElement.attribute( "attributeId" );
                if ( attributeIdAttribute != null )
                {
                    // Getting the 'cacheSize' attribute
                    org.dom4j.Attribute cacheSizeAttribute = jdbmIndexElement.attribute( "cacheSize" );
                    if ( cacheSizeAttribute != null )
                    {
                        // Adding a new indexed attribute to the list
                        indexedAttributes.add( new IndexedAttribute( attributeIdAttribute.getValue(), Integer
                            .parseInt( cacheSizeAttribute.getValue() ) ) );
                    }
                }
            }
        }

        return indexedAttributes;
    }


    /**
     * Read the context from a partition element.
     *
     * @param element
     *      the partition element
     * @return
     *      the corresponding attributes
     * @throws ServerXmlIOException 
     */
    private Attributes readContextEntry( Element element ) throws ServerXmlIOException
    {
        Element contextEntryElement = element.element( "contextEntry" );
        if ( contextEntryElement == null )
        {
            // If the 'contextEntry' element does not exists,
            // we throw an exception
            throw new ServerXmlIOException( "Unable to find the 'contextEntry' element for a partition." );
        }
        else
        {
            // Getting the id of the linked bean
            String linkedBeanId = contextEntryElement.getText().trim();

            // Removing the '#' character at the beginning of the value
            linkedBeanId = linkedBeanId.substring( 1, linkedBeanId.length() );

            // Creating a 'foundBean' flag to check if we've found the associated bean
            boolean foundBean = false;

            // Looping on all 'bean' tags
            for ( Iterator<?> i = element.getDocument().getRootElement().elementIterator( "bean" ); i.hasNext(); )
            {
                // Getting the bean element
                Element beanElement = ( Element ) i.next();

                // Getting the id attribute
                org.dom4j.Attribute idAttribute = beanElement.attribute( "id" );
                if ( idAttribute != null )
                {
                    // Checking if we've found the correct bean
                    if ( linkedBeanId.equalsIgnoreCase( idAttribute.getValue() ) )
                    {
                        // Setting the 'foundBean' flag to true
                        foundBean = true;

                        // Creating a 'foundProperty' flag to check if we've found the associated bean
                        boolean foundProperty = false;

                        // Looping on all 'property' tags
                        for ( Iterator<?> i2 = beanElement.elementIterator( "property" ); i2.hasNext(); )
                        {
                            // Getting the property element
                            Element propertyElement = ( Element ) i2.next();

                            // Getting the name attribute
                            org.dom4j.Attribute nameAttribute = propertyElement.attribute( "name" );
                            if ( nameAttribute != null )
                            {
                                if ( nameAttribute.getValue().equalsIgnoreCase( "arguments" ) )
                                {
                                    // Setting the 'foundProperty' flag to true
                                    foundProperty = true;

                                    // Getting the list element
                                    Element listElement = propertyElement.element( "list" );
                                    if ( listElement != null )
                                    {
                                        // Looping on all 'value' tags
                                        for ( Iterator<?> i3 = listElement.elementIterator( "value" ); i3.hasNext(); )
                                        {
                                            // Getting the value element
                                            Element valueElement = ( Element ) i3.next();

                                            // Getting the text value
                                            String value = valueElement.getText().trim();

                                            // We are looking for LDIF, so let's look if the text value
                                            // contains any ':'
                                            if ( value.indexOf( ':' ) != -1 )
                                            {
                                                // Returning the LDIF converted to JNDI Attributes
                                                return readContextEntry( valueElement.getText().trim() );
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Checking if we have found the associated property
                        // If not, we throw an error
                        if ( !foundProperty )
                        {
                            // If the correct property element does not exists,
                            // we throw an exception
                            throw new ServerXmlIOException( "Unable to find the property element named 'arguments'." );
                        }
                    }
                }
            }

            // Checking if we have found the associated bean
            // If not, we throw an error
            if ( !foundBean )
            {
                // If the correct bean element does not exists,
                // we throw an exception
                throw new ServerXmlIOException( "Unable to find the bean element named '" + linkedBeanId + "'." );
            }
        }

        return null;
    }


    /**
     * Reads the partitions.
     *
     * @param element
     *      the element
     * @param serverConfiguration
     *      the server configuration
     * @throws NumberFormatException
     * @throws ServerXmlIOException
     * @throws BooleanFormatException
     */
    private void readPartitions( Element element, ServerConfigurationV152 serverConfiguration )
        throws NumberFormatException, ServerXmlIOException, BooleanFormatException
    {
        // Getting the 'partitions'element
        Element partitionsElement = element.element( "partitions" );
        if ( partitionsElement != null )
        {
            // Looping on all 'jdbmPartition' tags
            for ( Iterator<?> i = partitionsElement.elementIterator( "jdbmPartition" ); i.hasNext(); )
            {
                // Getting the 'jbdmPartition' element
                Element jdbmPartitionElement = ( Element ) i.next();

                // Creating the partition
                Partition partition = new Partition();

                // Reading the partition
                readPartition( jdbmPartitionElement, partition );

                // Adding the partition to the server configuration
                serverConfiguration.addPartition( partition );
            }
        }
    }


    /**
     * Reads the interceptors.
     *
     * @param element
     *      the element
     * @param serverConfiguration
     *      the server configuration
     */
    private void readInterceptors( Element element, ServerConfigurationV152 serverConfiguration )
    {
        // Getting the 'interceptors
        Element interceptorsElement = element.element( "interceptors" );
        if ( interceptorsElement != null )
        {
            // Looping on all interceptor elements
            for ( Iterator<?> i = interceptorsElement.elementIterator(); i.hasNext(); )
            {
                // Getting the 'jbdmPartition' element
                Element interceptorElement = ( Element ) i.next();

                // Checking which interceptor it is
                String interceptorElementName = interceptorElement.getName();
                if ( "normalizationInterceptor".equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.NORMALIZATION );
                }
                else if ( "authenticationInterceptor".equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.AUTHENTICATION );
                }
                else if ( "referralInterceptor".equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.REFERRAL );
                }
                else if ( "aciAuthorizationInterceptor".equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.ACI_AUTHORIZATION );
                }
                else if ( "defaultAuthorizationInterceptor".equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.DEFAULT_AUTHORIZATION );
                }
                else if ( "exceptionInterceptor".equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.EXCEPTION );
                }
                else if ( "operationalAttributeInterceptor".equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.OPERATIONAL_ATTRIBUTE );
                }
                else if ( "schemaInterceptor".equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.SCHEMA );
                }
                else if ( "subentryInterceptor".equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.SUBENTRY );
                }
                else if ( "collectiveAttributeInterceptor".equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.COLLECTIVE_ATTRIBUTE );
                }
                else if ( "eventInterceptor".equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.EVENT );
                }
                else if ( "triggerInterceptor".equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.TRIGGER );
                }
                else if ( "replicationInterceptor".equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.REPLICATION );
                }
            }
        }
    }


    /**
     * Reads the ChangePasswordServer Bean.
     *
     * @param element
     *      the element
     * @param serverConfiguration
     *      the server configuration
     * @throws NumberFormatException
     * @throws ServerXmlIOException 
     */
    private void readChangePasswordServerBean( Element element, ServerConfigurationV152 serverConfiguration )
        throws NumberFormatException, ServerXmlIOException
    {
        // Getting the 'changePasswordServer' element
        Element changePasswordServerElement = element.element( "changePasswordServer" );
        if ( changePasswordServerElement != null )
        {
            // Enabling the Change Password protocol
            serverConfiguration.setEnableChangePassword( true );

            // Getting the 'ipPort' attribute
            org.dom4j.Attribute ipPortAttribute = changePasswordServerElement.attribute( "ipPort" );
            if ( ipPortAttribute == null )
            {
                // If the 'ipPort' attribute does not exists,
                // we throw an exception
                throw new ServerXmlIOException(
                    "Unable to find the 'ipPort' attribute for the 'changePasswordServer' bean." );
            }
            else
            {
                serverConfiguration.setChangePasswordPort( Integer.parseInt( ipPortAttribute.getValue() ) );
            }
        }
    }


    /**
     * Reads the KdcServer Bean.
     *
     * @param element
     *      the element
     * @param serverConfiguration
     *      the server configuration
     * @throws NumberFormatException
     * @throws ServerXmlIOException 
     */
    private void readKdcServerBean( Element element, ServerConfigurationV152 serverConfiguration )
        throws NumberFormatException, ServerXmlIOException
    {
        // Getting the 'kdcServer' element
        Element kdcServerElement = element.element( "kdcServer" );
        if ( kdcServerElement != null )
        {
            // Enabling the Kerberos protocol
            serverConfiguration.setEnableKerberos( true );

            // Getting the 'ipPort' attribute
            org.dom4j.Attribute ipPortAttribute = kdcServerElement.attribute( "ipPort" );
            if ( ipPortAttribute == null )
            {
                // If the 'ipPort' attribute does not exists,
                // we throw an exception
                throw new ServerXmlIOException( "Unable to find the 'ipPort' attribute for the 'kdcServer' bean." );
            }
            else
            {
                serverConfiguration.setKerberosPort( Integer.parseInt( ipPortAttribute.getValue() ) );
            }
        }
    }


    /**
     * Reads the NtpServer Bean.
     *
     * @param element
     *      the element
     * @param serverConfiguration
     *      the server configuration
     * @throws NumberFormatException
     * @throws ServerXmlIOException 
     */
    private void readNtpServerBean( Element element, ServerConfigurationV152 serverConfiguration )
        throws NumberFormatException, ServerXmlIOException
    {
        // Getting the 'ntpServer' element
        Element ntpServerElement = element.element( "ntpServer" );
        if ( ntpServerElement != null )
        {
            // Enabling the NTP protocol
            serverConfiguration.setEnableNtp( true );

            // Getting the 'ipPort' attribute
            org.dom4j.Attribute ipPortAttribute = ntpServerElement.attribute( "ipPort" );
            if ( ipPortAttribute == null )
            {
                // If the 'ipPort' attribute does not exists,
                // we throw an exception
                throw new ServerXmlIOException( "Unable to find the 'ipPort' attribute for the 'ntpServer' bean." );
            }
            else
            {
                serverConfiguration.setNtpPort( Integer.parseInt( ipPortAttribute.getValue() ) );
            }
        }
    }


    /**
     * Reads the DnsServer Bean.
     *
     * @param element
     *      the element
     * @param serverConfiguration
     *      the server configuration
     * @throws NumberFormatException
     * @throws ServerXmlIOException 
     */
    private void readDnsServerBean( Element element, ServerConfigurationV152 serverConfiguration )
        throws NumberFormatException, ServerXmlIOException
    {
        // Getting the 'dnsServer' element
        Element dnsServerElement = element.element( "dnsServer" );
        if ( dnsServerElement != null )
        {
            // Enabling the DNS protocol
            serverConfiguration.setEnableDns( true );

            // Getting the 'ipPort' attribute
            org.dom4j.Attribute ipPortAttribute = dnsServerElement.attribute( "ipPort" );
            if ( ipPortAttribute == null )
            {
                // If the 'ipPort' attribute does not exists,
                // we throw an exception
                throw new ServerXmlIOException( "Unable to find the 'ipPort' attribute for the 'dnsServer' bean." );
            }
            else
            {
                serverConfiguration.setDnsPort( Integer.parseInt( ipPortAttribute.getValue() ) );
            }
        }
    }


    /**
     * Reads the LdapsSever Bean.
     *
     * @param element
     *      the element
     * @param serverConfiguration
     *      the server configuration
     * @throws NumberFormatException
     * @throws ServerXmlIOException 
     * @throws BooleanFormatException 
     */
    private void readLdapsServerBean( Element element, ServerConfigurationV152 serverConfiguration )
        throws NumberFormatException, ServerXmlIOException, BooleanFormatException
    {
        // Looping on all 'ldapServer' elements
        for ( Iterator<?> i = element.getDocument().getRootElement().elementIterator( "ldapServer" ); i.hasNext(); )
        {
            // Getting the 'ldapServer' element
            Element ldapServerElement = ( Element ) i.next();

            // Getting the 'id' attribute
            org.dom4j.Attribute idAttribute = ldapServerElement.attribute( "id" );
            if ( idAttribute == null )
            {
                // If the 'id' attribute does not exists,
                // we throw an exception
                throw new ServerXmlIOException( "Unable to find the 'id' attribute for the 'ldapServer' bean." );
            }
            else
            {
                // Checking if the 'ldapServer' element is the one for LDAPS
                if ( "ldapsServer".equalsIgnoreCase( idAttribute.getValue() ) )
                {
                    // Getting the 'enableLdaps' attribute
                    org.dom4j.Attribute enableLdapsAttribute = ldapServerElement.attribute( "enableLdaps" );
                    if ( enableLdapsAttribute == null )
                    {
                        // Enabling by default
                        serverConfiguration.setEnableLdaps( true );
                    }
                    else
                    {
                        serverConfiguration.setEnableLdaps( parseBoolean( enableLdapsAttribute.getValue() ) );
                    }

                    // Getting the 'ipPort' attribute
                    org.dom4j.Attribute ipPortAttribute = ldapServerElement.attribute( "ipPort" );
                    if ( ipPortAttribute == null )
                    {
                        // If the 'ipPort' attribute does not exists,
                        // we throw an exception
                        throw new ServerXmlIOException(
                            "Unable to find the 'ipPort' attribute for the 'ldapsServer' bean." );
                    }
                    else
                    {
                        serverConfiguration.setLdapsPort( Integer.parseInt( ipPortAttribute.getValue() ) );
                    }

                    return;
                }
            }
        }
    }


    /**
     * Reads the LdapSever Bean.
     *
     * @param element
     *      the element
     * @param serverConfiguration
     *      the server configuration
     * @throws NumberFormatException
     * @throws ServerXmlIOException 
     * @throws BooleanFormatException 
     */
    private void readLdapServerBean( Element element, ServerConfigurationV152 serverConfiguration )
        throws NumberFormatException, ServerXmlIOException, BooleanFormatException
    {
        // Looping on all 'ldapServer' elements
        for ( Iterator<?> i = element.getDocument().getRootElement().elementIterator( "ldapServer" ); i.hasNext(); )
        {
            // Getting the 'ldapServer' element
            Element ldapServerElement = ( Element ) i.next();

            // Getting the 'id' attribute
            org.dom4j.Attribute idAttribute = ldapServerElement.attribute( "id" );
            if ( idAttribute == null )
            {
                // If the 'id' attribute does not exists,
                // we throw an exception
                throw new ServerXmlIOException( "Unable to find the 'id' attribute for the 'ldapServer' bean." );
            }
            else
            {
                // Checking if the 'ldapServer' element is the one for LDAP
                if ( "ldapServer".equalsIgnoreCase( idAttribute.getValue() ) )
                {
                    // Getting the 'ipPort' attribute
                    org.dom4j.Attribute ipPortAttribute = ldapServerElement.attribute( "ipPort" );
                    if ( ipPortAttribute == null )
                    {
                        // If the 'ipPort' attribute does not exists,
                        // we throw an exception
                        throw new ServerXmlIOException(
                            "Unable to find the 'ipPort' attribute for the 'ldapsServer' bean." );
                    }
                    else
                    {
                        serverConfiguration.setLdapPort( Integer.parseInt( ipPortAttribute.getValue() ) );
                    }

                    // Allow Anonymous Access
                    org.dom4j.Attribute allowAnonymousAccess = ldapServerElement.attribute( "allowAnonymousAccess" );
                    if ( allowAnonymousAccess == null )
                    {
                        // If the 'allowAnonymousAccess' attribute does not exists,
                        // we throw an exception
                        throw new ServerXmlIOException(
                            "Unable to find the 'allowAnonymousAccess' attribute for the 'ldapsServer' bean." );
                    }
                    else
                    {
                        serverConfiguration.setAllowAnonymousAccess( parseBoolean( allowAnonymousAccess.getValue() ) );
                    }

                    return;
                }
            }
        }
    }


    /**
     * Reads the ApacheDS Bean.
     *
     * @param element
     *      the element
     * @param serverConfiguration
     *      the server configuration
     * @throws ServerXmlIOException
     * @throws NumberFormatException
     */
    private void readApacheDSBean( Element element, ServerConfigurationV152 serverConfiguration )
        throws ServerXmlIOException, NumberFormatException
    {
        // Getting the 'apacheDS' element
        Element apacheDsElement = element.element( "apacheDS" );
        if ( apacheDsElement != null )
        {
            // SynchPeriodMillis
            org.dom4j.Attribute synchPeriodMillisAttribute = apacheDsElement.attribute( "synchPeriodMillis" );
            if ( synchPeriodMillisAttribute == null )
            {
                // If the 'synchPeriodMillis' attribute does not exists,
                // we throw an exception
                throw new ServerXmlIOException( "Unable to find the 'synchPeriodMillis' attribute for a partition." );
            }
            else
            {
                serverConfiguration
                    .setSynchronizationPeriod( Integer.parseInt( synchPeriodMillisAttribute.getValue() ) );
            }
        }
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.configuration.model.ServerXmlIO#toXml(org.apache.directory.studio.apacheds.configuration.model.ServerConfiguration)
     */
    public String toXml( ServerConfiguration serverConfiguration )
    {
        // Creating the document
        Document document = DocumentHelper.createDocument();

        // Creating the root element with its namespaces definitions
        Element root = document.addElement( new QName( "beans", NAMESPACE_XBEAN_SPRING ) );
        root.add( NAMESPACE_SPRINGFRAMEWORK );
        root.add( NAMESPACE_APACHEDS );

        // DefaultDirectoryService Bean
        createDefaultDirectoryServiceBean( root, ( ServerConfigurationV152 ) serverConfiguration );

        // Adding the 'standardThreadPool' element
        Element standardThreadPoolElement = root.addElement( "standardThreadPool" );
        standardThreadPoolElement.addAttribute( "id", "standardThreadPool" );
        standardThreadPoolElement.addAttribute( "maxThreads", ""
            + ( ( ServerConfigurationV152 ) serverConfiguration ).getMaxThreads() );

        // Adding the 'datagramAcceptor' element
        Element datagramAcceptorElement = root.addElement( "datagramAcceptor" );
        datagramAcceptorElement.addAttribute( "id", "datagramAcceptor" );
        datagramAcceptorElement.addAttribute( "logicExecutor", "#standardThreadPool" );

        // Adding the 'socketAcceptor' element
        Element socketAcceptorElement = root.addElement( "socketAcceptor" );
        socketAcceptorElement.addAttribute( "id", "socketAcceptor" );
        socketAcceptorElement.addAttribute( "logicExecutor", "#standardThreadPool" );

        // ChangePasswordServer Bean
        createChangePasswordServerBean( root, ( ServerConfigurationV152 ) serverConfiguration );

        // KdcServer Bean
        createKdcServerBean( root, ( ServerConfigurationV152 ) serverConfiguration );

        // NtpServer Bean
        createNtpServerBean( root, ( ServerConfigurationV152 ) serverConfiguration );

        // DnsServer Bean
        createDnsServerBean( root, ( ServerConfigurationV152 ) serverConfiguration );

        // LdapsServer Bean
        createLdapsServerBean( root, ( ServerConfigurationV152 ) serverConfiguration );

        // LdapServer Bean
        createLdapServerBean( root, ( ServerConfigurationV152 ) serverConfiguration );

        // ApacheDS Bean
        createApacheDSBean( root, ( ServerConfigurationV152 ) serverConfiguration );

        // CustomEditorConfigurer Bean
        createCustomEditorConfigurerBean( root );

        Document stylizedDocument = null;
        try
        {
            stylizedDocument = styleDocument( document );
        }
        catch ( TransformerException e )
        {
            // Will never occur
        }

        return stylizedDocument.asXML();
    }


    /**
     * Creates the DefaultDirectoryService bean.
     *
     * @param root
     *      the root element
     * @param serverConfiguration
     *      the server configuration
     */
    private void createDefaultDirectoryServiceBean( Element root, ServerConfigurationV152 serverConfiguration )
    {
        // Adding the 'defaultDirectoryService' element
        Element defaultDirectoryServiceElement = root.addElement( "defaultDirectoryService" );

        // Id
        defaultDirectoryServiceElement.addAttribute( "id", "directoryService" );

        // InstanceId
        defaultDirectoryServiceElement.addAttribute( "instanceId", "default" );

        // WorkingDirectory
        defaultDirectoryServiceElement.addAttribute( "workingDirectory", "example.com" );

        // AllowAnonymousAccess
        defaultDirectoryServiceElement.addAttribute( "allowAnonymousAccess", ""
            + serverConfiguration.isAllowAnonymousAccess() );

        // AccessControlEnabled
        defaultDirectoryServiceElement.addAttribute( "accessControlEnabled", ""
            + serverConfiguration.isEnableAccessControl() );

        // DenormalizeOpAttrsEnabled
        defaultDirectoryServiceElement.addAttribute( "denormalizeOpAttrsEnabled", ""
            + serverConfiguration.isDenormalizeOpAttr() );

        // Adding the 'systemPartition' element
        Element systemPartitionElement = defaultDirectoryServiceElement.addElement( "systemPartition" );

        // Adding System Partition Bean
        createSystemPartitionBean( systemPartitionElement, serverConfiguration );

        // Adding the 'partitions' element
        Element partitionsElement = defaultDirectoryServiceElement.addElement( "partitions" );

        // Adding User Partitions Beans
        createUserPartitions( partitionsElement, serverConfiguration );

        // Adding the 'interceptors' element
        Element interceptorsElement = defaultDirectoryServiceElement.addElement( "interceptors" );

        // Adding Interceptors Beans
        createInterceptors( interceptorsElement, serverConfiguration );
    }


    /**
     * Creates the system partition bean.
     *
     * @param systemPartitionElement
     *      the systemPartition element
     * @param serverConfiguration
     *      the server configuration
     */
    private void createSystemPartitionBean( Element systemPartitionElement, ServerConfigurationV152 serverConfiguration )
    {
        // Looping on partitions to find the system partition
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
            createPartition( systemPartitionElement, systemPartition );
        }
    }


    /**
     * Creates the user partition beans.
     *
     * @param partitionsElement
     *      the partitions element
     * @param serverConfiguration
     *      the server configuration
     */
    private void createUserPartitions( Element partitionsElement, ServerConfigurationV152 serverConfiguration )
    {
        // Looping on partitions
        for ( Partition partition : serverConfiguration.getPartitions() )
        {
            if ( !partition.isSystemPartition() )
            {
                createPartition( partitionsElement, partition );
            }
        }
    }


    /**
     * Creates the partition bean.
     *
     * @param element
     *      the element
     * @param serverConfiguration
     *      the server configuration
     */
    private void createPartition( Element element, Partition partition )
    {
        // Adding the 'jdbmPartition' element
        Element jdbmPartitionElement = element.addElement( "jdbmPartition" );

        // Id
        jdbmPartitionElement.addAttribute( "id", partition.getId() );

        // CacheSize
        jdbmPartitionElement.addAttribute( "cacheSize", "" + partition.getCacheSize() );

        // Suffix
        jdbmPartitionElement.addAttribute( "suffix", partition.getSuffix() );

        // OptimizerEnabled
        jdbmPartitionElement.addAttribute( "optimizerEnabled", "" + partition.isEnableOptimizer() );

        // SyncOnWrite
        jdbmPartitionElement.addAttribute( "syncOnWrite", "" + partition.isSynchronizationOnWrite() );

        // IndexedAttributes
        createIndexedAttributes( jdbmPartitionElement, partition.getIndexedAttributes() );

        // ContextEntry
        createContextEntry( jdbmPartitionElement, partition.getContextEntry(), partition.getId(), partition.getSuffix() );
    }


    /**
     * Creates the indexed attributes bean.
     *
     * @param element
     *      the element
     * @param indexedAttributes
     *      the indexed attributes list
     */
    private void createIndexedAttributes( Element element, List<IndexedAttribute> indexedAttributes )
    {
        // Adding the 'indexedAttribute' element
        Element indexedAttributeElement = element.addElement( "indexedAttribute" );

        if ( indexedAttributes != null )
        {
            // Looping on indexed attributes
            for ( IndexedAttribute indexedAttribute : indexedAttributes )
            {
                // Adding the 'jdbmIndex' element
                Element jdbmIndexElement = indexedAttributeElement.addElement( "jdbmIndex" );
                jdbmIndexElement.addAttribute( "attributeId", indexedAttribute.getAttributeId() );
                jdbmIndexElement.addAttribute( "cacheSize", "" + indexedAttribute.getCacheSize() );
            }
        }
    }


    /**
     * Creates the context entry bean.
     *
     * @param element
     *      the element
     * @param contextEntry
     *      the attributes
     * @param id
     *      the partition id
     * @param dn 
     *      the dn
     */
    private void createContextEntry( Element element, Attributes contextEntry, String id, String dn )
    {
        // Adding the 'contextEntry' element
        element.addElement( "contextEntry" ).setText( "#" + id + "ContextEntry" );

        // Adding the 'bean' element
        Element beanElement = element.getDocument().getRootElement().addElement(
            new QName( "bean", NAMESPACE_XBEAN_SPRING ) );
        beanElement.addAttribute( "id", id + "ContextEntry" );
        beanElement.addAttribute( "class", "org.springframework.beans.factory.config.MethodInvokingFactoryBean" );

        // Adding the targetObject 'property' element
        Element targetObjectPropertyElement = beanElement.addElement( new QName( "property", NAMESPACE_XBEAN_SPRING ) );
        targetObjectPropertyElement.addAttribute( "name", "targetObject" );

        // Adding the targetObject 'ref' element
        Element targetObjectRefElement = targetObjectPropertyElement.addElement( new QName( "ref",
            NAMESPACE_XBEAN_SPRING ) );
        targetObjectRefElement.addAttribute( "local", "directoryService" );

        // Adding the targetMethod 'property' element
        Element targetMethodPropertyElement = beanElement.addElement( new QName( "property", NAMESPACE_XBEAN_SPRING ) );
        targetMethodPropertyElement.addAttribute( "name", "targetMethod" );

        // Adding the targetMethod 'value' element
        targetMethodPropertyElement.addElement( new QName( "value", NAMESPACE_XBEAN_SPRING ) ).setText( "newEntry" );

        // Adding the arguments 'property' element
        Element argumentsPropertyElement = beanElement.addElement( new QName( "property", NAMESPACE_XBEAN_SPRING ) );
        argumentsPropertyElement.addAttribute( "name", "arguments" );

        // Adding the arguments 'list' element
        Element argumentsListElement = argumentsPropertyElement
            .addElement( new QName( "list", NAMESPACE_XBEAN_SPRING ) );

        // Adding the arguments attributes 'value' element
        Element argumentsAttributesValueElement = argumentsListElement.addElement( new QName( "value", new Namespace(
            "spring", "http://www.springframework.org/schema/beans" ) ) );

        // Creating a string buffer to contain the LDIF data
        StringBuffer sb = new StringBuffer();

        // Looping on attributes
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

        // Assigning the value to the element
        argumentsAttributesValueElement.setText( sb.toString() );

        // Adding the arguments dn 'value' element
        argumentsListElement.addElement( new QName( "value", NAMESPACE_XBEAN_SPRING ) ).setText( dn );
    }


    /**
     * Creates the interceptor beans.
     *
     * @param interceptorsElement
     *      the interceptors element
     * @param serverConfiguration
     *      the server configuration
     */
    private void createInterceptors( Element interceptorsElement, ServerConfigurationV152 serverConfiguration )
    {
        List<InterceptorEnum> interceptors = serverConfiguration.getInterceptors();

        if ( interceptors.contains( InterceptorEnum.NORMALIZATION ) )
        {
            interceptorsElement.addElement( "normalizationInterceptor" );
        }
        if ( interceptors.contains( InterceptorEnum.AUTHENTICATION ) )
        {
            interceptorsElement.addElement( "authenticationInterceptor" );
        }
        if ( interceptors.contains( InterceptorEnum.REFERRAL ) )
        {
            interceptorsElement.addElement( "referralInterceptor" );
        }
        if ( interceptors.contains( InterceptorEnum.ACI_AUTHORIZATION ) )
        {
            interceptorsElement.addElement( "aciAuthorizationInterceptor" );
        }
        if ( interceptors.contains( InterceptorEnum.DEFAULT_AUTHORIZATION ) )
        {
            interceptorsElement.addElement( "defaultAuthorizationInterceptor" );
        }
        if ( interceptors.contains( InterceptorEnum.EXCEPTION ) )
        {
            interceptorsElement.addElement( "exceptionInterceptor" );
        }
        if ( interceptors.contains( InterceptorEnum.OPERATIONAL_ATTRIBUTE ) )
        {
            interceptorsElement.addElement( "operationalAttributeInterceptor" );
        }
        if ( interceptors.contains( InterceptorEnum.SCHEMA ) )
        {
            interceptorsElement.addElement( "schemaInterceptor" );
        }
        if ( interceptors.contains( InterceptorEnum.SUBENTRY ) )
        {
            interceptorsElement.addElement( "subentryInterceptor" );
        }
        if ( interceptors.contains( InterceptorEnum.COLLECTIVE_ATTRIBUTE ) )
        {
            interceptorsElement.addElement( "collectiveAttributeInterceptor" );
        }
        if ( interceptors.contains( InterceptorEnum.EVENT ) )
        {
            interceptorsElement.addElement( "eventInterceptor" );
        }
        if ( interceptors.contains( InterceptorEnum.TRIGGER ) )
        {
            interceptorsElement.addElement( "triggerInterceptor" );
        }
        if ( interceptors.contains( InterceptorEnum.REPLICATION ) )
        {
            // TODO support replication interceptor
            //            interceptorsElement.addElement( "replicationInterceptor" );
        }
    }


    /**
     * Creates the ChangePasswordServer bean.
     *
     * @param root
     *      the root element
     * @param serverConfiguration
     *      the server configuration
     */
    private void createChangePasswordServerBean( Element root, ServerConfigurationV152 serverConfiguration )
    {
        if ( serverConfiguration.isEnableChangePassword() )
        {
            // Adding the 'changePasswordServer' element
            Element changePasswordServerElement = root.addElement( "changePasswordServer" );

            // IpPort
            changePasswordServerElement.addAttribute( "ipPort", "" + serverConfiguration.getChangePasswordPort() );

            // Adding 'directoryService' element
            changePasswordServerElement.addElement( "directoryService" ).setText( "#directoryService" );

            // Adding 'datagramAcceptor' element
            changePasswordServerElement.addElement( "datagramAcceptor" ).setText( "#datagramAcceptor" );

            // Adding 'socketAcceptor' element
            changePasswordServerElement.addElement( "socketAcceptor" ).setText( "#socketAcceptor" );
        }
    }


    /**
     * Creates the KdcServer bean.
     *
     * @param root
     *      the root element
     * @param serverConfiguration
     *      the server configuration
     */
    private void createKdcServerBean( Element root, ServerConfigurationV152 serverConfiguration )
    {
        if ( serverConfiguration.isEnableKerberos() )
        {
            // Adding the 'kdcServer' element
            Element kdcServerElement = root.addElement( "kdcServer" );

            // IpPort
            kdcServerElement.addAttribute( "ipPort", "" + serverConfiguration.getKerberosPort() );

            // Adding 'directoryService' element
            kdcServerElement.addElement( "directoryService" ).setText( "#directoryService" );

            // Adding 'datagramAcceptor' element
            kdcServerElement.addElement( "datagramAcceptor" ).setText( "#datagramAcceptor" );

            // Adding 'socketAcceptor' element
            kdcServerElement.addElement( "socketAcceptor" ).setText( "#socketAcceptor" );
        }
    }


    /**
     * Creates the NtpServer bean.
     *
     * @param root
     *      the root element
     * @param serverConfiguration
     *      the server configuration
     */
    private void createNtpServerBean( Element root, ServerConfigurationV152 serverConfiguration )
    {
        if ( serverConfiguration.isEnableNtp() )
        {
            // Adding the 'ntpServer' element
            Element ntpServerElement = root.addElement( "ntpServer" );

            // IpPort
            ntpServerElement.addAttribute( "ipPort", "" + serverConfiguration.getNtpPort() );

            // Adding 'datagramAcceptor' element
            ntpServerElement.addElement( "datagramAcceptor" ).setText( "#datagramAcceptor" );

            // Adding 'socketAcceptor' element
            ntpServerElement.addElement( "socketAcceptor" ).setText( "#socketAcceptor" );
        }
    }


    /**
     * Creates the DnsServer bean.
     *
     * @param root
     *      the root element
     * @param serverConfiguration
     *      the server configuration
     */
    private void createDnsServerBean( Element root, ServerConfigurationV152 serverConfiguration )
    {
        if ( serverConfiguration.isEnableDns() )
        {
            // Adding the 'dnsServer' element
            Element dnsServerElement = root.addElement( "dnsServer" );

            // IpPort
            dnsServerElement.addAttribute( "ipPort", "" + serverConfiguration.getDnsPort() );

            // Adding 'directoryService' element
            dnsServerElement.addElement( "directoryService" ).setText( "#directoryService" );

            // Adding 'datagramAcceptor' element
            dnsServerElement.addElement( "datagramAcceptor" ).setText( "#datagramAcceptor" );

            // Adding 'socketAcceptor' element
            dnsServerElement.addElement( "socketAcceptor" ).setText( "#socketAcceptor" );
        }
    }


    /**
     * Creates the LdapsServer bean.
     *
     * @param root
     *      the root element
     * @param serverConfiguration
     *      the server configuration
     */
    private void createLdapsServerBean( Element root, ServerConfigurationV152 serverConfiguration )
    {
        if ( serverConfiguration.isEnableLdaps() )
        {
            // Adding the 'ldapServer' element
            Element ldapServerElement = root.addElement( "ldapServer" );

            // Id
            ldapServerElement.addAttribute( "id", "ldapsServer" );

            // IpPort
            ldapServerElement.addAttribute( "ipPort", "" + serverConfiguration.getLdapsPort() );

            // Enable
            ldapServerElement.addAttribute( "enable", "" + "true" );

            // EnableLdaps
            ldapServerElement.addAttribute( "enableLdaps", "" + "true" );

            // Adding 'directoryService' element
            ldapServerElement.addElement( "directoryService" ).setText( "#directoryService" );

            // Adding 'socketAcceptor' element
            ldapServerElement.addElement( "socketAcceptor" ).setText( "#socketAcceptor" );
        }
    }


    /**
     * Creates the LdapServer bean.
     *
     * @param root
     *      the root element
     * @param serverConfiguration
     *      the server configuration
     */
    private void createLdapServerBean( Element root, ServerConfigurationV152 serverConfiguration )
    {
        // Adding the 'ldapServer' element
        Element ldapServerElement = root.addElement( "ldapServer" );

        // Id
        ldapServerElement.addAttribute( "id", "ldapServer" );

        // IpPort
        ldapServerElement.addAttribute( "ipPort", "" + serverConfiguration.getLdapPort() );

        // AllowAnonymousAccess
        ldapServerElement.addAttribute( "allowAnonymousAccess", "" + serverConfiguration.isAllowAnonymousAccess() );

        // SaslHost
        ldapServerElement.addAttribute( "saslHost", "" + serverConfiguration.getSaslHost() );

        // SaslPrincipal
        ldapServerElement.addAttribute( "saslPrincipal", "" + serverConfiguration.getSaslPrincipal() );

        // SearchBaseDn
        ldapServerElement.addAttribute( "searchBaseDn", "ou=users,ou=system" );

        // MaxTimeLimit
        ldapServerElement.addAttribute( "maxTimeLimit", "" + serverConfiguration.getMaxTimeLimit() );

        // MaxSizeLimit
        ldapServerElement.addAttribute( "maxSizeLimit", "" + serverConfiguration.getMaxSizeLimit() );

        // Adding 'directoryService' element
        ldapServerElement.addElement( "directoryService" ).setText( "#directoryService" );

        // Adding 'socketAcceptor' element
        ldapServerElement.addElement( "socketAcceptor" ).setText( "#socketAcceptor" );

        // Adding 'supportedMechanisms' element
        Element supportedMechanismsElement = ldapServerElement.addElement( "supportedMechanisms" );

        // Adding each supported mechanism
        for ( String supportedMechanism : serverConfiguration.getSupportedMechanisms() )
        {
            supportedMechanismsElement.addElement( new QName( "value", NAMESPACE_SPRINGFRAMEWORK ) ).setText(
                supportedMechanism );
        }

        // Adding 'SaslQop' element
        Element saslQopElement = ldapServerElement.addElement( "saslQop" );

        // Adding each SaslQop item
        for ( String saslQop : serverConfiguration.getSaslQops() )
        {
            saslQopElement.addElement( new QName( "value", NAMESPACE_SPRINGFRAMEWORK ) ).setText( saslQop );
        }

        // Adding 'SaslRealms' element
        Element saslRealmsElement = ldapServerElement.addElement( "saslRealms" );

        // Adding each SaslRealm item
        for ( String saslRealm : serverConfiguration.getSaslRealms() )
        {
            saslRealmsElement.addElement( new QName( "value", NAMESPACE_SPRINGFRAMEWORK ) ).setText( saslRealm );
        }

        // TODO Add Extended Operations
    }


    /**
     * Creates the ApacheDS bean.
     *
     * @param root
     *      the root element
     * @param serverConfiguration
     *      the server configuration
     */
    private void createApacheDSBean( Element root, ServerConfigurationV152 serverConfiguration )
    {
        // Adding the 'apacheDS' element
        Element apacheDSElement = root.addElement( "apacheDS" );

        // Id
        apacheDSElement.addAttribute( "id", "apacheDS" );

        // SyncPeriodMillis
        apacheDSElement.addAttribute( "syncPeriodMillis", "" + serverConfiguration.getSynchronizationPeriod() );

        // AllowAnonymousAccess
        apacheDSElement.addAttribute( "allowAnonymousAccess", "" + serverConfiguration.isAllowAnonymousAccess() );

        // Adding 'directoryService' element
        apacheDSElement.addElement( "directoryService" ).setText( "#directoryService" );

        // Adding 'ldapServer' element
        apacheDSElement.addElement( "ldapServer" ).setText( "#ldapServer" );

        // LDAP Protocol
        if ( serverConfiguration.isEnableLdaps() )
        {
            // Adding 'ldapsServer' element
            apacheDSElement.addElement( "ldapsServer" ).setText( "#ldapsServer" );
        }

        // LDAPS Protocol
        if ( serverConfiguration.isEnableLdaps() )
        {
            // Adding 'ldapsServer' element
            apacheDSElement.addElement( "ldapsServer" ).setText( "#ldapsServer" );
        }
    }


    /**
     * Creates the CustomEditorConfigurer bean.
     *
     * @param root
     *      the root element
     * @param serverConfiguration
     *      the server configuration
     */
    private void createCustomEditorConfigurerBean( Element root )
    {
        // Adding the 'bean' element
        Element beanElement = root.addElement( new QName( "bean", NAMESPACE_XBEAN_SPRING ) );
        beanElement.addAttribute( "class", "org.springframework.beans.factory.config.CustomEditorConfigurer" );

        // Adding the 'property' element
        Element propertyElement = beanElement.addElement( new QName( "property", NAMESPACE_XBEAN_SPRING ) );
        propertyElement.addAttribute( "name", "customEditors" );

        // Adding the 'map' element
        Element mapElement = propertyElement.addElement( new QName( "map", NAMESPACE_XBEAN_SPRING ) );

        // Adding the 'entry' element
        Element entryElement = mapElement.addElement( new QName( "entry", NAMESPACE_XBEAN_SPRING ) );
        entryElement.addAttribute( "key", "javax.naming.directory.Attributes" );

        // Adding the inner 'bean' element
        Element innerBeanElement = entryElement.addElement( new QName( "entry", NAMESPACE_XBEAN_SPRING ) );
        innerBeanElement.addAttribute( "class",
            "org.apache.directory.server.core.configuration.AttributesPropertyEditor" );
    }
}
