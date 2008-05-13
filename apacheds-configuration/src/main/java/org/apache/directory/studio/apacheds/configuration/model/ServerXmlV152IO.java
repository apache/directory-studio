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


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.directory.Attributes;

import org.apache.directory.studio.apacheds.configuration.model.AbstractServerXmlIO.BooleanFormatException;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


/**
 * This class implements a parser and a writer for the 'server.xml' file of 
 * Apache Directory Server version 1.5.2.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ServerXmlV152IO extends AbstractServerXmlIO implements ServerXmlIO
{
    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.configuration.model.ServerXmlIO#isValid(java.io.InputStream)
     */
    public boolean isValid( InputStream is )
    {
        // TODO Auto-generated method stub
        return true;
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
            ServerConfiguration serverConfiguration = new ServerConfiguration();
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
    private void parse( Document document, ServerConfiguration serverConfiguration ) throws NumberFormatException,
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


    private void readDefaultDirectoryServiceBean( Element element, ServerConfiguration serverConfiguration )
        throws ServerXmlIOException, NumberFormatException, BooleanFormatException
    {
        Element defaultDirectoryServiceElement = element.element( "defaultDirectoryService" );
        if ( defaultDirectoryServiceElement == null )
        {
            throw new ServerXmlIOException( "Unable to find the 'defaultDirectoryService' tag." );
        }
        else
        {
            // Allow Anonymous Access
            //            Attribute allowAnonymousAccess = defaultDirectoryServiceElement.attribute( "allowAnonymousAccess" );
            //            if ( allowAnonymousAccess == null )
            //            {
            //                // TODO throw an error
            //            }
            //            else
            //            {
            //                serverConfiguration.setAllowAnonymousAccess( parseBoolean( allowAnonymousAccess.getValue() ) );
            //            }

            // Access Control Enabled
            Attribute accessControlEnabledAttribute = defaultDirectoryServiceElement.attribute( "accessControlEnabled" );
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
            Attribute denormalizeOpAttrsEnabledAttribute = defaultDirectoryServiceElement
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
    private void readSystemPartition( Element element, ServerConfiguration serverConfiguration )
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
        Attribute idAttribute = element.attribute( "id" );
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
        Attribute cacheSizeAttribute = element.attribute( "cacheSize" );
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
        Attribute suffixAttribute = element.attribute( "suffix" );
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
        Attribute optimizerEnabledAttribute = element.attribute( "optimizerEnabled" );
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

        // Synch On Write
        Attribute synchOnWriteAttribute = element.attribute( "synchOnWrite" );
        if ( synchOnWriteAttribute == null )
        {
            // If the 'synchOnWrite' attribute does not exists,
            // we throw an exception
            throw new ServerXmlIOException( "Unable to find the 'synchOnWrite' attribute for a partition." );
        }
        else
        {
            partition.setSynchronizationOnWrite( parseBoolean( synchOnWriteAttribute.getValue() ) );
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
                Attribute attributeIdAttribute = jdbmIndexElement.attribute( "attributeId" );
                if ( attributeIdAttribute != null )
                {
                    // Getting the 'cacheSize' attribute
                    Attribute cacheSizeAttribute = jdbmIndexElement.attribute( "cacheSize" );
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
                Attribute idAttribute = beanElement.attribute( "id" );
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
                            Attribute nameAttribute = propertyElement.attribute( "name" );
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
    private void readPartitions( Element element, ServerConfiguration serverConfiguration )
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


    private void readInterceptors( Element element, ServerConfiguration serverConfiguration )
    {
        // TODO Auto-generated method stub

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
    private void readChangePasswordServerBean( Element element, ServerConfiguration serverConfiguration )
        throws NumberFormatException, ServerXmlIOException
    {
        // Getting the 'changePasswordServer' element
        Element changePasswordServerElement = element.element( "changePasswordServer" );
        if ( changePasswordServerElement != null )
        {
            // Enabling the Change Password protocol
            serverConfiguration.setEnableChangePassword( true );

            // Getting the 'ipPort' attribute
            Attribute ipPortAttribute = changePasswordServerElement.attribute( "ipPort" );
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
    private void readKdcServerBean( Element element, ServerConfiguration serverConfiguration )
        throws NumberFormatException, ServerXmlIOException
    {
        // Getting the 'kdcServer' element
        Element kdcServerElement = element.element( "kdcServer" );
        if ( kdcServerElement != null )
        {
            // Enabling the Kerberos protocol
            serverConfiguration.setEnableKerberos( true );

            // Getting the 'ipPort' attribute
            Attribute ipPortAttribute = kdcServerElement.attribute( "ipPort" );
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
    private void readNtpServerBean( Element element, ServerConfiguration serverConfiguration )
        throws NumberFormatException, ServerXmlIOException
    {
        // Getting the 'ntpServer' element
        Element ntpServerElement = element.element( "ntpServer" );
        if ( ntpServerElement != null )
        {
            // Enabling the NTP protocol
            serverConfiguration.setEnableNtp( true );

            // Getting the 'ipPort' attribute
            Attribute ipPortAttribute = ntpServerElement.attribute( "ipPort" );
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
    private void readDnsServerBean( Element element, ServerConfiguration serverConfiguration )
        throws NumberFormatException, ServerXmlIOException
    {
        // Getting the 'dnsServer' element
        Element dnsServerElement = element.element( "dnsServer" );
        if ( dnsServerElement != null )
        {
            // Enabling the DNS protocol
            serverConfiguration.setEnableDns( true );

            // Getting the 'ipPort' attribute
            Attribute ipPortAttribute = dnsServerElement.attribute( "ipPort" );
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
    private void readLdapsServerBean( Element element, ServerConfiguration serverConfiguration )
        throws NumberFormatException, ServerXmlIOException, BooleanFormatException
    {
        // Looping on all 'ldapServer' elements
        for ( Iterator<?> i = element.getDocument().getRootElement().elementIterator( "ldapServer" ); i.hasNext(); )
        {
            // Getting the 'ldapServer' element
            Element ldapServerElement = ( Element ) i.next();

            // Getting the 'id' attribute
            Attribute idAttribute = ldapServerElement.attribute( "id" );
            if ( idAttribute == null )
            {
                // TODO throw an error
            }
            else
            {
                // Checking if the 'ldapServer' element is the one for LDAPS
                if ( "ldapsServer".equalsIgnoreCase( idAttribute.getValue() ) )
                {
                    // Getting the 'enableLdaps' attribute
                    Attribute enableLdapsAttribute = ldapServerElement.attribute( "enableLdaps" );
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
                    Attribute ipPortAttribute = ldapServerElement.attribute( "ipPort" );
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
    private void readLdapServerBean( Element element, ServerConfiguration serverConfiguration )
        throws NumberFormatException, ServerXmlIOException, BooleanFormatException
    {
        // Looping on all 'ldapServer' elements
        for ( Iterator<?> i = element.getDocument().getRootElement().elementIterator( "ldapServer" ); i.hasNext(); )
        {
            // Getting the 'ldapServer' element
            Element ldapServerElement = ( Element ) i.next();

            // Getting the 'id' attribute
            Attribute idAttribute = ldapServerElement.attribute( "id" );
            if ( idAttribute == null )
            {
                // TODO throw an error
            }
            else
            {
                // Checking if the 'ldapServer' element is the one for LDAP
                if ( "ldapServer".equalsIgnoreCase( idAttribute.getValue() ) )
                {
                    // Getting the 'ipPort' attribute
                    Attribute ipPortAttribute = ldapServerElement.attribute( "ipPort" );
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
                    Attribute allowAnonymousAccess = ldapServerElement.attribute( "allowAnonymousAccess" );
                    if ( allowAnonymousAccess == null )
                    {
                        // TODO throw an error
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


    private void readApacheDSBean( Element element, ServerConfiguration serverConfiguration )
    {
        // TODO Auto-generated method stub

    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.configuration.model.ServerXmlIO#toXml(org.apache.directory.studio.apacheds.configuration.model.ServerConfiguration)
     */
    public String toXml( ServerConfiguration serverConfiguration )
    {
        // TODO Auto-generated method stub
        return null;
    }
}
