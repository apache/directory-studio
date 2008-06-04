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
    private static final String ATTRIBUTE_ACCESS_CONTROL_ENABLED = "accessControlEnabled";
    private static final String ATTRIBUTE_ALLOW_ANONYMOUS_ACCESS = "allowAnonymousAccess";
    private static final String ATTRIBUTE_ATTRIBUTE_ID = "attributeId";
    private static final String ATTRIBUTE_CACHE_SIZE = "cacheSize";
    private static final String ATTRIBUTE_CLASS = "class";
    private static final String ATTRIBUTE_DENORMALIZE_OP_ATTRS_ENABLED = "denormalizeOpAttrsEnabled";
    private static final String ATTRIBUTE_ENABLE_LDAPS = "enableLdaps";
    private static final String ATTRIBUTE_ENABLED = "enabled";
    private static final String ATTRIBUTE_ID = "id";
    private static final String ATTRIBUTE_INSTANCE_ID = "instanceId";
    private static final String ATTRIBUTE_IP_PORT = "ipPort";
    private static final String ATTRIBUTE_KEY = "key";
    private static final String ATTRIBUTE_LOCAL = "local";
    private static final String ATTRIBUTE_MAX_SIZE_LIMIT = "maxSizeLimit";
    private static final String ATTRIBUTE_MAX_THREADS = "maxThreads";
    private static final String ATTRIBUTE_MAX_TIME_LIMIT = "maxTimeLimit";
    private static final String ATTRIBUTE_NAME = "name";
    private static final String ATTRIBUTE_OPTIMIZER_ENABLED = "optimizerEnabled";
    private static final String ATTRIBUTE_SASL_HOST = "saslHost";
    private static final String ATTRIBUTE_SASL_PRINCIPAL = "saslPrincipal";
    private static final String ATTRIBUTE_SEARCH_BASE_DN = "searchBaseDn";
    private static final String ATTRIBUTE_SUFFIX = "suffix";
    private static final String ATTRIBUTE_SYNC_ON_WRITE = "syncOnWrite";
    private static final String ATTRIBUTE_SYNCH_PERIOD_MILLIS = "synchPeriodMillis";
    private static final String ATTRIBUTE_WORKING_DIRECTORY = "workingDirectory";
    private static final String ELEMENT_ACI_AUTHORIZATION_INTERCEPTOR = "aciAuthorizationInterceptor";
    private static final String ELEMENT_APACHE_DS = "apacheDS";
    private static final String ELEMENT_AUTHENTICATION_INTERCEPTOR = "authenticationInterceptor";
    private static final String ELEMENT_BEAN = "bean";
    private static final String ELEMENT_BEANS = "beans";
    private static final String ELEMENT_CHANGE_PASSWORD_SERVER = "changePasswordServer";
    private static final String ELEMENT_COLLECTIVE_ATTRIBUTE_INTERCEPTOR = "collectiveAttributeInterceptor";
    private static final String ELEMENT_CONTEXT_ENTRY = "contextEntry";
    private static final String ELEMENT_DATAGRAM_ACCEPTOR = "datagramAcceptor";
    private static final String ELEMENT_DEFAULT_AUTHORIZATION_INTERCEPTOR = "defaultAuthorizationInterceptor";
    private static final String ELEMENT_DEFAULT_DIRECTORY_SERVICE = "defaultDirectoryService";
    private static final String ELEMENT_DNS_SERVER = "dnsServer";
    private static final String ELEMENT_ENTRY = "entry";
    private static final String ELEMENT_EVENT_INTERCEPTOR = "eventInterceptor";
    private static final String ELEMENT_EXCEPTION_INTERCEPTOR = "exceptionInterceptor";
    private static final String ELEMENT_EXTENDED_OPERATION_HANDLERS = "extendedOperationHandlers";
    private static final String ELEMENT_GRACEFUL_SHUTDOWN_HANDLER = "gracefulShutdownHandler";
    private static final String ELEMENT_INDEXED_ATTRIBUTES = "indexedAttributes";
    private static final String ELEMENT_INTERCEPTORS = "interceptors";
    private static final String ELEMENT_JDBM_INDEX = "jdbmIndex";
    private static final String ELEMENT_JDBM_PARTITION = "jdbmPartition";
    private static final String ELEMENT_KDC_SERVER = "kdcServer";
    private static final String ELEMENT_LAUNCH_DIAGNOSTIC_UI_HANDLER = "launchDiagnosticUiHandler";
    private static final String ELEMENT_LDAP_SERVER = "ldapServer";
    private static final String ELEMENT_LDAPS_SERVER = "ldapsServer";
    private static final String ELEMENT_LIST = "list";
    private static final String ELEMENT_LOGIC_EXECUTOR = "logicExecutor";
    private static final String ELEMENT_MAP = "map";
    private static final String ELEMENT_NORMALIZATION_INTERCEPTOR = "normalizationInterceptor";
    private static final String ELEMENT_NTP_SERVER = "ntpServer";
    private static final String ELEMENT_OPERATIONAL_ATTRIBUTE_INTERCEPTOR = "operationalAttributeInterceptor";
    private static final String ELEMENT_PARTITIONS = "partitions";
    private static final String ELEMENT_PROPERTY = "property";
    private static final String ELEMENT_REF = "ref";
    private static final String ELEMENT_REFERRAL_INTERCEPTOR = "referralInterceptor";
    private static final String ELEMENT_REPLICATION_INTERCEPTOR = "replicationInterceptor";
    private static final String ELEMENT_SASL_QOP = "saslQop";
    private static final String ELEMENT_SASL_REALMS = "saslRealms";
    private static final String ELEMENT_SCHEMA_INTERCEPTOR = "schemaInterceptor";
    private static final String ELEMENT_SOCKET_ACCEPTOR = "socketAcceptor";
    private static final String ELEMENT_STANDARD_THREAD_POOL = "standardThreadPool";
    private static final String ELEMENT_START_TLS_HANDLER = "startTlsHandler";
    private static final String ELEMENT_SUBENTRY_INTERCEPTOR = "subentryInterceptor";
    private static final String ELEMENT_SUPPORTED_MECHANISMS = "supportedMechanisms";
    private static final String ELEMENT_SYSTEM_PARTITION = "systemPartition";
    private static final String ELEMENT_TRIGGER_INTERCEPTOR = "triggerInterceptor";
    private static final String ELEMENT_VALUE = "value";
    private static final Namespace NAMESPACE_APACHEDS = new Namespace( null, "http://apacheds.org/config/1.0" );
    private static final Namespace NAMESPACE_SPRINGFRAMEWORK = new Namespace( "s",
        "http://www.springframework.org/schema/beans" );
    private static final Namespace NAMESPACE_XBEAN_SPRING = new Namespace( "spring",
        "http://xbean.apache.org/schemas/spring/1.0" );
    private static final String SASL_QOP_AUTH_CONF = "auth-conf";
    private static final String SASL_QOP_AUTH_INT = "auth-int";
    private static final String SASL_QOP_AUTH = "auth";
    private static final String SUPPORTED_MECHANISM_GSSAPI = "GSSAPI";
    private static final String SUPPORTED_MECHANISM_DIGEST_MD5 = "DIGEST-MD5";
    private static final String SUPPORTED_MECHANISM_CRAM_MD5 = "CRAM-MD5";
    private static final String SUPPORTED_MECHANISM_SIMPLE = "SIMPLE";
    private static final String VALUE_ARGUMENTS = "arguments";
    private static final String VALUE_CUSTOM_EDITORS = "customEditors";
    private static final String VALUE_DEFAULT = "default";
    private static final String VALUE_DIRECTORY_SERVICE = "directoryService";
    private static final String VALUE_EXAMPLE_DOT_COM = "example.com";


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
            if ( ServerXmlIOV152.ELEMENT_BEANS.equalsIgnoreCase( rootElement.getName() ) )
            {
                // Looking for the 'apacheDS' element
                Element apacheDSElement = rootElement.element( ServerXmlIOV152.ELEMENT_APACHE_DS );
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

        // Reading the 'standardThreadPool' Bean
        readStandardThreadPoolBean( rootElement, serverConfiguration );

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


    /**
     * Reads the DefaultDirectoryService Bean.
     *
     * @param element
     *      the element
     * @param serverConfiguration
     *      the server configuration
     * @throws ServerXmlIOException
     * @throws NumberFormatException
     * @throws BooleanFormatException
     */
    private void readDefaultDirectoryServiceBean( Element element, ServerConfigurationV152 serverConfiguration )
        throws ServerXmlIOException, NumberFormatException, BooleanFormatException
    {
        Element defaultDirectoryServiceElement = element.element( ELEMENT_DEFAULT_DIRECTORY_SERVICE );
        if ( defaultDirectoryServiceElement == null )
        {
            throw new ServerXmlIOException( "Unable to find the 'defaultDirectoryService' tag." );
        }
        else
        {
            // Access Control Enabled
            org.dom4j.Attribute accessControlEnabledAttribute = defaultDirectoryServiceElement
                .attribute( ServerXmlIOV152.ATTRIBUTE_ACCESS_CONTROL_ENABLED );
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
                .attribute( ServerXmlIOV152.ATTRIBUTE_DENORMALIZE_OP_ATTRS_ENABLED );
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
     * Reads the StandardThreadPool Bean.
     *
     * @param element
     *      the element
     * @param serverConfiguration
     *      the server configuration
     * @throws ServerXmlIOException
     * @throws NumberFormatException
     */
    private void readStandardThreadPoolBean( Element element, ServerConfigurationV152 serverConfiguration )
        throws ServerXmlIOException, NumberFormatException
    {
        Element standardThreadPoolElement = element.element( ServerXmlIOV152.ELEMENT_STANDARD_THREAD_POOL );
        if ( standardThreadPoolElement == null )
        {
            throw new ServerXmlIOException( "Unable to find the 'standardThreadPool' tag." );
        }
        else
        {
            // MaxThreads
            org.dom4j.Attribute maxThreadsAttribute = standardThreadPoolElement
                .attribute( ServerXmlIOV152.ATTRIBUTE_MAX_THREADS );
            if ( maxThreadsAttribute == null )
            {
                // If the 'maxThreads' attribute does not exists,
                // we throw an exception
                throw new ServerXmlIOException(
                    "Unable to find the 'maxThreads' attribute for the StandardThreadPool bean." );
            }
            else
            {
                serverConfiguration.setMaxThreads( Integer.parseInt( maxThreadsAttribute.getValue() ) );
            }
        }
    }


    /**
     * Reads the system partition.
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
        Element systemPartitionElement = element.element( ServerXmlIOV152.ELEMENT_SYSTEM_PARTITION );
        if ( systemPartitionElement == null )
        {
            // If the 'systemPartition' element does not exists,
            // we throw an exception
            throw new ServerXmlIOException( "Unable to find the 'systemPartition' element." );
        }
        else
        {
            // Getting the 'jdbmPartition' element
            Element jdbmPartitionElement = systemPartitionElement.element( ServerXmlIOV152.ELEMENT_JDBM_PARTITION );
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
        org.dom4j.Attribute idAttribute = element.attribute( ServerXmlIOV152.ATTRIBUTE_ID );
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
        org.dom4j.Attribute cacheSizeAttribute = element.attribute( ServerXmlIOV152.ATTRIBUTE_CACHE_SIZE );
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
        org.dom4j.Attribute suffixAttribute = element.attribute( ServerXmlIOV152.ATTRIBUTE_SUFFIX );
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
        org.dom4j.Attribute optimizerEnabledAttribute = element.attribute( ServerXmlIOV152.ATTRIBUTE_OPTIMIZER_ENABLED );
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
        org.dom4j.Attribute syncOnWriteAttribute = element.attribute( ServerXmlIOV152.ATTRIBUTE_SYNC_ON_WRITE );
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
        Element indexedAttributesElement = element.element( ServerXmlIOV152.ELEMENT_INDEXED_ATTRIBUTES );
        if ( indexedAttributesElement != null )
        {
            // Looping on 'jdbmIndex' elements
            for ( Iterator<?> i = indexedAttributesElement.elementIterator( ServerXmlIOV152.ELEMENT_JDBM_INDEX ); i
                .hasNext(); )
            {
                // Getting the 'jdbmIndex' element
                Element jdbmIndexElement = ( Element ) i.next();

                // Getting the 'attributeId' attribute
                org.dom4j.Attribute attributeIdAttribute = jdbmIndexElement
                    .attribute( ServerXmlIOV152.ATTRIBUTE_ATTRIBUTE_ID );
                if ( attributeIdAttribute != null )
                {
                    // Getting the 'cacheSize' attribute
                    org.dom4j.Attribute cacheSizeAttribute = jdbmIndexElement
                        .attribute( ServerXmlIOV152.ATTRIBUTE_CACHE_SIZE );
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
        Element contextEntryElement = element.element( ServerXmlIOV152.ELEMENT_CONTEXT_ENTRY );
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
            for ( Iterator<?> i = element.getDocument().getRootElement().elementIterator( ServerXmlIOV152.ELEMENT_BEAN ); i
                .hasNext(); )
            {
                // Getting the bean element
                Element beanElement = ( Element ) i.next();

                // Getting the id attribute
                org.dom4j.Attribute idAttribute = beanElement.attribute( ServerXmlIOV152.ATTRIBUTE_ID );
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
                        for ( Iterator<?> i2 = beanElement.elementIterator( ServerXmlIOV152.ELEMENT_PROPERTY ); i2
                            .hasNext(); )
                        {
                            // Getting the property element
                            Element propertyElement = ( Element ) i2.next();

                            // Getting the name attribute
                            org.dom4j.Attribute nameAttribute = propertyElement
                                .attribute( ServerXmlIOV152.ATTRIBUTE_NAME );
                            if ( nameAttribute != null )
                            {
                                if ( nameAttribute.getValue().equalsIgnoreCase( ServerXmlIOV152.VALUE_ARGUMENTS ) )
                                {
                                    // Setting the 'foundProperty' flag to true
                                    foundProperty = true;

                                    // Getting the list element
                                    Element listElement = propertyElement.element( ServerXmlIOV152.ELEMENT_LIST );
                                    if ( listElement != null )
                                    {
                                        // Looping on all 'value' tags
                                        for ( Iterator<?> i3 = listElement
                                            .elementIterator( ServerXmlIOV152.ELEMENT_VALUE ); i3.hasNext(); )
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
        Element partitionsElement = element.element( ServerXmlIOV152.ELEMENT_PARTITIONS );
        if ( partitionsElement != null )
        {
            // Looping on all 'jdbmPartition' tags
            for ( Iterator<?> i = partitionsElement.elementIterator( ServerXmlIOV152.ELEMENT_JDBM_PARTITION ); i
                .hasNext(); )
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
        Element interceptorsElement = element.element( ServerXmlIOV152.ELEMENT_INTERCEPTORS );
        if ( interceptorsElement != null )
        {
            // Looping on all interceptor elements
            for ( Iterator<?> i = interceptorsElement.elementIterator(); i.hasNext(); )
            {
                // Getting the element
                Element interceptorElement = ( Element ) i.next();

                // Checking which interceptor it is
                String interceptorElementName = interceptorElement.getName();
                if ( ServerXmlIOV152.ELEMENT_NORMALIZATION_INTERCEPTOR.equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.NORMALIZATION );
                }
                else if ( ServerXmlIOV152.ELEMENT_AUTHENTICATION_INTERCEPTOR.equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.AUTHENTICATION );
                }
                else if ( ServerXmlIOV152.ELEMENT_REFERRAL_INTERCEPTOR.equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.REFERRAL );
                }
                else if ( ServerXmlIOV152.ELEMENT_ACI_AUTHORIZATION_INTERCEPTOR
                    .equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.ACI_AUTHORIZATION );
                }
                else if ( ServerXmlIOV152.ELEMENT_DEFAULT_AUTHORIZATION_INTERCEPTOR
                    .equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.DEFAULT_AUTHORIZATION );
                }
                else if ( ServerXmlIOV152.ELEMENT_EXCEPTION_INTERCEPTOR.equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.EXCEPTION );
                }
                else if ( ServerXmlIOV152.ELEMENT_OPERATIONAL_ATTRIBUTE_INTERCEPTOR
                    .equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.OPERATIONAL_ATTRIBUTE );
                }
                else if ( ServerXmlIOV152.ELEMENT_SCHEMA_INTERCEPTOR.equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.SCHEMA );
                }
                else if ( ServerXmlIOV152.ELEMENT_SUBENTRY_INTERCEPTOR.equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.SUBENTRY );
                }
                else if ( ServerXmlIOV152.ELEMENT_COLLECTIVE_ATTRIBUTE_INTERCEPTOR
                    .equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.COLLECTIVE_ATTRIBUTE );
                }
                else if ( ServerXmlIOV152.ELEMENT_EVENT_INTERCEPTOR.equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.EVENT );
                }
                else if ( ServerXmlIOV152.ELEMENT_TRIGGER_INTERCEPTOR.equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.TRIGGER );
                }
                else if ( ServerXmlIOV152.ELEMENT_REPLICATION_INTERCEPTOR.equalsIgnoreCase( interceptorElementName ) )
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
     * @throws BooleanFormatException 
     */
    private void readChangePasswordServerBean( Element element, ServerConfigurationV152 serverConfiguration )
        throws NumberFormatException, ServerXmlIOException, BooleanFormatException
    {
        // Getting the 'changePasswordServer' element
        Element changePasswordServerElement = element.element( ServerXmlIOV152.ELEMENT_CHANGE_PASSWORD_SERVER );
        if ( changePasswordServerElement != null )
        {
            // Getting the 'enabled' attribute
            org.dom4j.Attribute enabledAttribute = changePasswordServerElement
                .attribute( ServerXmlIOV152.ATTRIBUTE_ENABLED );
            if ( enabledAttribute == null )
            {
                // By default, the protocol is not enabled
                serverConfiguration.setEnableChangePassword( false );
            }
            else
            {
                serverConfiguration.setEnableChangePassword( parseBoolean( enabledAttribute.getValue() ) );
            }

            // Getting the 'ipPort' attribute
            org.dom4j.Attribute ipPortAttribute = changePasswordServerElement
                .attribute( ServerXmlIOV152.ATTRIBUTE_IP_PORT );
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
     * @throws BooleanFormatException 
     */
    private void readKdcServerBean( Element element, ServerConfigurationV152 serverConfiguration )
        throws NumberFormatException, ServerXmlIOException, BooleanFormatException
    {
        // Getting the 'kdcServer' element
        Element kdcServerElement = element.element( ServerXmlIOV152.ELEMENT_KDC_SERVER );
        if ( kdcServerElement != null )
        {
            // Getting the 'enabled' attribute
            org.dom4j.Attribute enabledAttribute = kdcServerElement.attribute( ServerXmlIOV152.ATTRIBUTE_ENABLED );
            if ( enabledAttribute == null )
            {
                // By default, the protocol is not enabled
                serverConfiguration.setEnableKerberos( false );
            }
            else
            {
                serverConfiguration.setEnableKerberos( parseBoolean( enabledAttribute.getValue() ) );
            }

            // Getting the 'ipPort' attribute
            org.dom4j.Attribute ipPortAttribute = kdcServerElement.attribute( ServerXmlIOV152.ATTRIBUTE_IP_PORT );
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
     * @throws BooleanFormatException 
     */
    private void readNtpServerBean( Element element, ServerConfigurationV152 serverConfiguration )
        throws NumberFormatException, ServerXmlIOException, BooleanFormatException
    {
        // Getting the 'ntpServer' element
        Element ntpServerElement = element.element( ServerXmlIOV152.ELEMENT_NTP_SERVER );
        if ( ntpServerElement != null )
        {
            // Getting the 'enabled' attribute
            org.dom4j.Attribute enabledAttribute = ntpServerElement.attribute( ServerXmlIOV152.ATTRIBUTE_ENABLED );
            if ( enabledAttribute == null )
            {
                // By default, the protocol is not enabled
                serverConfiguration.setEnableNtp( false );
            }
            else
            {
                serverConfiguration.setEnableNtp( parseBoolean( enabledAttribute.getValue() ) );
            }

            // Getting the 'ipPort' attribute
            org.dom4j.Attribute ipPortAttribute = ntpServerElement.attribute( ServerXmlIOV152.ATTRIBUTE_IP_PORT );
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
     * @throws BooleanFormatException 
     */
    private void readDnsServerBean( Element element, ServerConfigurationV152 serverConfiguration )
        throws NumberFormatException, ServerXmlIOException, BooleanFormatException
    {
        // Getting the 'dnsServer' element
        Element dnsServerElement = element.element( ServerXmlIOV152.ELEMENT_DNS_SERVER );
        if ( dnsServerElement != null )
        {
            // Getting the 'enabled' attribute
            org.dom4j.Attribute enabledAttribute = dnsServerElement.attribute( ServerXmlIOV152.ATTRIBUTE_ENABLED );
            if ( enabledAttribute == null )
            {
                // By default, the protocol is not enabled
                serverConfiguration.setEnableDns( false );
            }
            else
            {
                serverConfiguration.setEnableDns( parseBoolean( enabledAttribute.getValue() ) );
            }

            // Getting the 'ipPort' attribute
            org.dom4j.Attribute ipPortAttribute = dnsServerElement.attribute( ServerXmlIOV152.ATTRIBUTE_IP_PORT );
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
        for ( Iterator<?> i = element.getDocument().getRootElement().elementIterator(
            ServerXmlIOV152.ELEMENT_LDAP_SERVER ); i.hasNext(); )
        {
            // Getting the 'ldapServer' element
            Element ldapServerElement = ( Element ) i.next();

            // Getting the 'id' attribute
            org.dom4j.Attribute idAttribute = ldapServerElement.attribute( ServerXmlIOV152.ATTRIBUTE_ID );
            if ( idAttribute == null )
            {
                // If the 'id' attribute does not exists,
                // we throw an exception
                throw new ServerXmlIOException( "Unable to find the 'id' attribute for the 'ldapServer' bean." );
            }
            else
            {
                // Checking if the 'ldapServer' element is the one for LDAPS
                if ( ServerXmlIOV152.ELEMENT_LDAPS_SERVER.equalsIgnoreCase( idAttribute.getValue() ) )
                {
                    // Getting the 'enableLdaps' attribute
                    org.dom4j.Attribute enableLdapsAttribute = ldapServerElement
                        .attribute( ServerXmlIOV152.ATTRIBUTE_ENABLE_LDAPS );
                    if ( enableLdapsAttribute == null )
                    {
                        // By default, the protocol is not enabled
                        serverConfiguration.setEnableLdaps( false );
                    }
                    else
                    {
                        // Getting the 'enabled' attribute
                        org.dom4j.Attribute enabledAttribute = ldapServerElement
                            .attribute( ServerXmlIOV152.ATTRIBUTE_ENABLED );
                        if ( enabledAttribute == null )
                        {
                            // By default, the protocol is not enabled
                            serverConfiguration.setEnableLdaps( false );
                        }
                        else
                        {
                            serverConfiguration.setEnableLdaps( parseBoolean( enableLdapsAttribute.getValue() )
                                && parseBoolean( enabledAttribute.getValue() ) );
                        }
                    }

                    // Getting the 'ipPort' attribute
                    org.dom4j.Attribute ipPortAttribute = ldapServerElement
                        .attribute( ServerXmlIOV152.ATTRIBUTE_IP_PORT );
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
        for ( Iterator<?> i = element.getDocument().getRootElement().elementIterator(
            ServerXmlIOV152.ELEMENT_LDAP_SERVER ); i.hasNext(); )
        {
            // Getting the 'ldapServer' element
            Element ldapServerElement = ( Element ) i.next();

            // Getting the 'id' attribute
            org.dom4j.Attribute idAttribute = ldapServerElement.attribute( ServerXmlIOV152.ATTRIBUTE_ID );
            if ( idAttribute == null )
            {
                // If the 'id' attribute does not exists,
                // we throw an exception
                throw new ServerXmlIOException( "Unable to find the 'id' attribute for the 'ldapServer' bean." );
            }
            else
            {
                // Checking if the 'ldapServer' element is the one for LDAP
                if ( ServerXmlIOV152.ELEMENT_LDAP_SERVER.equalsIgnoreCase( idAttribute.getValue() ) )
                {
                    // Getting the 'enabled' attribute
                    org.dom4j.Attribute enabledAttribute = ldapServerElement
                        .attribute( ServerXmlIOV152.ATTRIBUTE_ENABLED );
                    if ( enabledAttribute == null )
                    {
                        // By default, the protocol is enabled
                        serverConfiguration.setEnableLdap( true );
                    }
                    else
                    {
                        serverConfiguration.setEnableLdap( parseBoolean( enabledAttribute.getValue() ) );
                    }
                    // IpPort
                    org.dom4j.Attribute ipPortAttribute = ldapServerElement
                        .attribute( ServerXmlIOV152.ATTRIBUTE_IP_PORT );
                    if ( ipPortAttribute == null )
                    {
                        // If the 'ipPort' attribute does not exists,
                        // we throw an exception
                        throw new ServerXmlIOException(
                            "Unable to find the 'ipPort' attribute for the 'ldapServer' bean." );
                    }
                    else
                    {
                        serverConfiguration.setLdapPort( Integer.parseInt( ipPortAttribute.getValue() ) );
                    }

                    // Allow Anonymous Access
                    org.dom4j.Attribute allowAnonymousAccessAttribute = ldapServerElement
                        .attribute( ServerXmlIOV152.ATTRIBUTE_ALLOW_ANONYMOUS_ACCESS );
                    if ( allowAnonymousAccessAttribute == null )
                    {
                        // If the 'allowAnonymousAccess' attribute does not exists,
                        // we throw an exception
                        throw new ServerXmlIOException(
                            "Unable to find the 'allowAnonymousAccess' attribute for the 'ldapServer' bean." );
                    }
                    else
                    {
                        serverConfiguration.setAllowAnonymousAccess( parseBoolean( allowAnonymousAccessAttribute
                            .getValue() ) );
                    }

                    // SaslHost
                    org.dom4j.Attribute saslHostAttribute = ldapServerElement
                        .attribute( ServerXmlIOV152.ATTRIBUTE_SASL_HOST );
                    if ( saslHostAttribute == null )
                    {
                        // If the 'saslHost' attribute does not exists,
                        // we throw an exception
                        throw new ServerXmlIOException(
                            "Unable to find the 'saslHost' attribute for the 'ldapServer' bean." );
                    }
                    else
                    {
                        serverConfiguration.setSaslHost( saslHostAttribute.getValue() );
                    }

                    // SaslPrincipal
                    org.dom4j.Attribute saslPrincipalAttribute = ldapServerElement
                        .attribute( ServerXmlIOV152.ATTRIBUTE_SASL_PRINCIPAL );
                    if ( saslPrincipalAttribute == null )
                    {
                        // If the 'saslPrincipal' attribute does not exists,
                        // we throw an exception
                        throw new ServerXmlIOException(
                            "Unable to find the 'saslPrincipal' attribute for the 'ldapServer' bean." );
                    }
                    else
                    {
                        serverConfiguration.setSaslPrincipal( saslPrincipalAttribute.getValue() );
                    }

                    // SearchBaseDn
                    org.dom4j.Attribute searchBaseDnAttribute = ldapServerElement
                        .attribute( ServerXmlIOV152.ATTRIBUTE_SEARCH_BASE_DN );
                    if ( searchBaseDnAttribute == null )
                    {
                        // If the 'searchBaseDn' attribute does not exists,
                        // we throw an exception
                        throw new ServerXmlIOException(
                            "Unable to find the 'searchBaseDn' attribute for the 'ldapServer' bean." );
                    }
                    else
                    {
                        serverConfiguration.setSearchBaseDn( searchBaseDnAttribute.getValue() );
                    }

                    // MaxTimeLimit
                    org.dom4j.Attribute maxTimeLimitAttribute = ldapServerElement
                        .attribute( ServerXmlIOV152.ATTRIBUTE_MAX_TIME_LIMIT );
                    if ( maxTimeLimitAttribute == null )
                    {
                        // If the 'maxTimeLimit' attribute does not exists,
                        // we throw an exception
                        throw new ServerXmlIOException(
                            "Unable to find the 'maxTimeLimit' attribute for the 'ldapServer' bean." );
                    }
                    else
                    {
                        serverConfiguration.setMaxTimeLimit( Integer.parseInt( maxTimeLimitAttribute.getValue() ) );
                    }

                    // MaxSizeLimit
                    org.dom4j.Attribute maxSizeLimitAttribute = ldapServerElement
                        .attribute( ServerXmlIOV152.ATTRIBUTE_MAX_SIZE_LIMIT );
                    if ( maxSizeLimitAttribute == null )
                    {
                        // If the 'maxSizeLimit' attribute does not exists,
                        // we throw an exception
                        throw new ServerXmlIOException(
                            "Unable to find the 'maxSizeLimit' attribute for the 'ldapServer' bean." );
                    }
                    else
                    {
                        serverConfiguration.setMaxSizeLimit( Integer.parseInt( maxSizeLimitAttribute.getValue() ) );
                    }

                    // Supported Mechanisms
                    Element supportedMechanismsElement = ldapServerElement
                        .element( ServerXmlIOV152.ELEMENT_SUPPORTED_MECHANISMS );
                    if ( supportedMechanismsElement != null )
                    {
                        // Looping on all 'value' elements
                        for ( Iterator<?> iterator = supportedMechanismsElement
                            .elementIterator( ServerXmlIOV152.ELEMENT_VALUE ); iterator.hasNext(); )
                        {
                            // Getting the 'value' element
                            Element supportedMechanismValueElement = ( Element ) iterator.next();

                            String supportedMechanismValue = supportedMechanismValueElement.getText().trim();
                            if ( ServerXmlIOV152.SUPPORTED_MECHANISM_SIMPLE.equalsIgnoreCase( supportedMechanismValue ) )
                            {
                                serverConfiguration.addSupportedMechanism( SupportedMechanismEnum.SIMPLE );
                            }
                            else if ( ServerXmlIOV152.SUPPORTED_MECHANISM_CRAM_MD5
                                .equalsIgnoreCase( supportedMechanismValue ) )
                            {
                                serverConfiguration.addSupportedMechanism( SupportedMechanismEnum.CRAM_MD5 );
                            }
                            else if ( ServerXmlIOV152.SUPPORTED_MECHANISM_DIGEST_MD5
                                .equalsIgnoreCase( supportedMechanismValue ) )
                            {
                                serverConfiguration.addSupportedMechanism( SupportedMechanismEnum.DIGEST_MD5 );
                            }
                            else if ( ServerXmlIOV152.SUPPORTED_MECHANISM_GSSAPI
                                .equalsIgnoreCase( supportedMechanismValue ) )
                            {
                                serverConfiguration.addSupportedMechanism( SupportedMechanismEnum.GSSAPI );
                            }
                        }
                    }

                    // SaslQop
                    Element SaslQopElement = ldapServerElement.element( ServerXmlIOV152.ELEMENT_SASL_QOP );
                    if ( SaslQopElement != null )
                    {
                        // Looping on all 'value' elements
                        for ( Iterator<?> iterator = SaslQopElement.elementIterator( ServerXmlIOV152.ELEMENT_VALUE ); iterator
                            .hasNext(); )
                        {
                            // Getting the 'value' element
                            Element saslQopValueElement = ( Element ) iterator.next();

                            // Adding the SaslQop value
                            String saslQopValue = saslQopValueElement.getText().trim();
                            if ( ServerXmlIOV152.SASL_QOP_AUTH.equalsIgnoreCase( saslQopValue ) )
                            {
                                serverConfiguration.addSaslQop( SaslQualityOfProtectionEnum.AUTH );
                            }
                            else if ( ServerXmlIOV152.SASL_QOP_AUTH_INT.equalsIgnoreCase( saslQopValue ) )
                            {
                                serverConfiguration.addSaslQop( SaslQualityOfProtectionEnum.AUTH_INT );
                            }
                            else if ( ServerXmlIOV152.SASL_QOP_AUTH_CONF.equalsIgnoreCase( saslQopValue ) )
                            {
                                serverConfiguration.addSaslQop( SaslQualityOfProtectionEnum.AUTH_CONF );
                            }
                        }
                    }

                    // SaslRealms
                    Element SaslRealmsElement = ldapServerElement.element( ServerXmlIOV152.ELEMENT_SASL_REALMS );
                    if ( SaslRealmsElement != null )
                    {
                        // Looping on all 'value' elements
                        for ( Iterator<?> iterator = SaslRealmsElement.elementIterator( ServerXmlIOV152.ELEMENT_VALUE ); iterator
                            .hasNext(); )
                        {
                            // Getting the 'value' element
                            Element saslRealmValueElement = ( Element ) iterator.next();

                            // Adding the SaslRealm value
                            serverConfiguration.addSaslRealm( saslRealmValueElement.getText().trim() );

                        }
                    }

                    // Extended operations
                    readExtendedOperations( ldapServerElement, serverConfiguration );

                    return;
                }
            }
        }
    }


    /**
     * Reads the extended operations.
     *
     * @param element
     *      the element
     * @param serverConfiguration
     *      the server configuration
     */
    private void readExtendedOperations( Element element, ServerConfigurationV152 serverConfiguration )
    {
        // Getting the 'interceptors
        Element interceptorsElement = element.element( ServerXmlIOV152.ELEMENT_EXTENDED_OPERATION_HANDLERS );
        if ( interceptorsElement != null )
        {
            // Looping on all interceptor elements
            for ( Iterator<?> i = interceptorsElement.elementIterator(); i.hasNext(); )
            {
                // Getting the element
                Element extendedOperationElement = ( Element ) i.next();

                // Checking which extended operation it is
                String extendedOperationElementName = extendedOperationElement.getName();
                if ( ServerXmlIOV152.ELEMENT_START_TLS_HANDLER.equalsIgnoreCase( extendedOperationElementName ) )
                {
                    serverConfiguration.addExtendedOperation( ExtendedOperationEnum.START_TLS );
                }
                if ( ServerXmlIOV152.ELEMENT_GRACEFUL_SHUTDOWN_HANDLER.equalsIgnoreCase( extendedOperationElementName ) )
                {
                    serverConfiguration.addExtendedOperation( ExtendedOperationEnum.GRACEFUL_SHUTDOWN );
                }
                if ( ServerXmlIOV152.ELEMENT_LAUNCH_DIAGNOSTIC_UI_HANDLER
                    .equalsIgnoreCase( extendedOperationElementName ) )
                {
                    serverConfiguration.addExtendedOperation( ExtendedOperationEnum.LAUNCH_DIAGNOSTIC_UI );
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
        Element apacheDsElement = element.element( ServerXmlIOV152.ELEMENT_APACHE_DS );
        if ( apacheDsElement != null )
        {
            // SynchPeriodMillis
            org.dom4j.Attribute synchPeriodMillisAttribute = apacheDsElement
                .attribute( ServerXmlIOV152.ATTRIBUTE_SYNCH_PERIOD_MILLIS );
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
        Element root = document.addElement( new QName( ServerXmlIOV152.ELEMENT_BEANS, NAMESPACE_XBEAN_SPRING ) );
        root.add( NAMESPACE_SPRINGFRAMEWORK );
        root.add( NAMESPACE_APACHEDS );

        // DefaultDirectoryService Bean
        createDefaultDirectoryServiceBean( root, ( ServerConfigurationV152 ) serverConfiguration );

        // Adding the 'standardThreadPool' element
        Element standardThreadPoolElement = root.addElement( ServerXmlIOV152.ELEMENT_STANDARD_THREAD_POOL );
        standardThreadPoolElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_ID,
            ServerXmlIOV152.ELEMENT_STANDARD_THREAD_POOL );
        standardThreadPoolElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_MAX_THREADS, ""
            + ( ( ServerConfigurationV152 ) serverConfiguration ).getMaxThreads() );

        // Adding the 'datagramAcceptor' element
        Element datagramAcceptorElement = root.addElement( ServerXmlIOV152.ELEMENT_DATAGRAM_ACCEPTOR );
        datagramAcceptorElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_ID, ServerXmlIOV152.ELEMENT_DATAGRAM_ACCEPTOR );
        datagramAcceptorElement.addAttribute( ServerXmlIOV152.ELEMENT_LOGIC_EXECUTOR, "#standardThreadPool" );

        // Adding the 'socketAcceptor' element
        Element socketAcceptorElement = root.addElement( ServerXmlIOV152.ELEMENT_SOCKET_ACCEPTOR );
        socketAcceptorElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_ID, ServerXmlIOV152.ELEMENT_SOCKET_ACCEPTOR );
        socketAcceptorElement.addAttribute( ServerXmlIOV152.ELEMENT_LOGIC_EXECUTOR, "#standardThreadPool" );

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
        Element defaultDirectoryServiceElement = root.addElement( ServerXmlIOV152.ELEMENT_DEFAULT_DIRECTORY_SERVICE );

        // Id
        defaultDirectoryServiceElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_ID,
            ServerXmlIOV152.VALUE_DIRECTORY_SERVICE );

        // InstanceId
        defaultDirectoryServiceElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_INSTANCE_ID,
            ServerXmlIOV152.VALUE_DEFAULT );

        // WorkingDirectory
        defaultDirectoryServiceElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_WORKING_DIRECTORY,
            ServerXmlIOV152.VALUE_EXAMPLE_DOT_COM );

        // AllowAnonymousAccess
        defaultDirectoryServiceElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_ALLOW_ANONYMOUS_ACCESS, ""
            + serverConfiguration.isAllowAnonymousAccess() );

        // AccessControlEnabled
        defaultDirectoryServiceElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_ACCESS_CONTROL_ENABLED, ""
            + serverConfiguration.isEnableAccessControl() );

        // DenormalizeOpAttrsEnabled
        defaultDirectoryServiceElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_DENORMALIZE_OP_ATTRS_ENABLED, ""
            + serverConfiguration.isDenormalizeOpAttr() );

        // Adding the 'systemPartition' element
        Element systemPartitionElement = defaultDirectoryServiceElement
            .addElement( ServerXmlIOV152.ELEMENT_SYSTEM_PARTITION );

        // Adding System Partition Bean
        createSystemPartitionBean( systemPartitionElement, serverConfiguration );

        // Adding the 'partitions' element
        Element partitionsElement = defaultDirectoryServiceElement.addElement( ServerXmlIOV152.ELEMENT_PARTITIONS );

        // Adding User Partitions Beans
        createUserPartitions( partitionsElement, serverConfiguration );

        // Adding the 'interceptors' element
        Element interceptorsElement = defaultDirectoryServiceElement.addElement( ServerXmlIOV152.ELEMENT_INTERCEPTORS );

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
        Element jdbmPartitionElement = element.addElement( ServerXmlIOV152.ELEMENT_JDBM_PARTITION );

        // Id
        jdbmPartitionElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_ID, partition.getId() );

        // CacheSize
        jdbmPartitionElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_CACHE_SIZE, "" + partition.getCacheSize() );

        // Suffix
        jdbmPartitionElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_SUFFIX, partition.getSuffix() );

        // OptimizerEnabled
        jdbmPartitionElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_OPTIMIZER_ENABLED, ""
            + partition.isEnableOptimizer() );

        // SyncOnWrite
        jdbmPartitionElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_SYNC_ON_WRITE, ""
            + partition.isSynchronizationOnWrite() );

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
        Element indexedAttributeElement = element.addElement( ServerXmlIOV152.ELEMENT_INDEXED_ATTRIBUTES );

        if ( indexedAttributes != null )
        {
            // Looping on indexed attributes
            for ( IndexedAttribute indexedAttribute : indexedAttributes )
            {
                // Adding the 'jdbmIndex' element
                Element jdbmIndexElement = indexedAttributeElement.addElement( ServerXmlIOV152.ELEMENT_JDBM_INDEX );
                jdbmIndexElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_ATTRIBUTE_ID, indexedAttribute
                    .getAttributeId() );
                jdbmIndexElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_CACHE_SIZE, ""
                    + indexedAttribute.getCacheSize() );
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
        element.addElement( ServerXmlIOV152.ELEMENT_CONTEXT_ENTRY ).setText( "#" + id + "ContextEntry" );

        // Adding the 'bean' element
        Element beanElement = element.getDocument().getRootElement().addElement(
            new QName( ServerXmlIOV152.ELEMENT_BEAN, NAMESPACE_XBEAN_SPRING ) );
        beanElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_ID, id + "ContextEntry" );
        beanElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_CLASS,
            "org.springframework.beans.factory.config.MethodInvokingFactoryBean" );

        // Adding the targetObject 'property' element
        Element targetObjectPropertyElement = beanElement.addElement( new QName( ServerXmlIOV152.ELEMENT_PROPERTY,
            NAMESPACE_XBEAN_SPRING ) );
        targetObjectPropertyElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_NAME, "targetObject" );

        // Adding the targetObject 'ref' element
        Element targetObjectRefElement = targetObjectPropertyElement.addElement( new QName(
            ServerXmlIOV152.ELEMENT_REF, NAMESPACE_XBEAN_SPRING ) );
        targetObjectRefElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_LOCAL, ServerXmlIOV152.VALUE_DIRECTORY_SERVICE );

        // Adding the targetMethod 'property' element
        Element targetMethodPropertyElement = beanElement.addElement( new QName( ServerXmlIOV152.ELEMENT_PROPERTY,
            NAMESPACE_XBEAN_SPRING ) );
        targetMethodPropertyElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_NAME, "targetMethod" );

        // Adding the targetMethod 'value' element
        targetMethodPropertyElement.addElement( new QName( ServerXmlIOV152.ELEMENT_VALUE, NAMESPACE_XBEAN_SPRING ) )
            .setText( "newEntry" );

        // Adding the arguments 'property' element
        Element argumentsPropertyElement = beanElement.addElement( new QName( ServerXmlIOV152.ELEMENT_PROPERTY,
            NAMESPACE_XBEAN_SPRING ) );
        argumentsPropertyElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_NAME, ServerXmlIOV152.VALUE_ARGUMENTS );

        // Adding the arguments 'list' element
        Element argumentsListElement = argumentsPropertyElement.addElement( new QName( ServerXmlIOV152.ELEMENT_LIST,
            NAMESPACE_XBEAN_SPRING ) );

        // Adding the arguments attributes 'value' element
        Element argumentsAttributesValueElement = argumentsListElement.addElement( new QName(
            ServerXmlIOV152.ELEMENT_VALUE, new Namespace( "spring", "http://www.springframework.org/schema/beans" ) ) );

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
        argumentsListElement.addElement( new QName( ServerXmlIOV152.ELEMENT_VALUE, NAMESPACE_XBEAN_SPRING ) ).setText(
            dn );
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
            interceptorsElement.addElement( ServerXmlIOV152.ELEMENT_NORMALIZATION_INTERCEPTOR );
        }
        if ( interceptors.contains( InterceptorEnum.AUTHENTICATION ) )
        {
            interceptorsElement.addElement( ServerXmlIOV152.ELEMENT_AUTHENTICATION_INTERCEPTOR );
        }
        if ( interceptors.contains( InterceptorEnum.REFERRAL ) )
        {
            interceptorsElement.addElement( ServerXmlIOV152.ELEMENT_REFERRAL_INTERCEPTOR );
        }
        if ( interceptors.contains( InterceptorEnum.ACI_AUTHORIZATION ) )
        {
            interceptorsElement.addElement( ServerXmlIOV152.ELEMENT_ACI_AUTHORIZATION_INTERCEPTOR );
        }
        if ( interceptors.contains( InterceptorEnum.DEFAULT_AUTHORIZATION ) )
        {
            interceptorsElement.addElement( ServerXmlIOV152.ELEMENT_DEFAULT_AUTHORIZATION_INTERCEPTOR );
        }
        if ( interceptors.contains( InterceptorEnum.EXCEPTION ) )
        {
            interceptorsElement.addElement( ServerXmlIOV152.ELEMENT_EXCEPTION_INTERCEPTOR );
        }
        if ( interceptors.contains( InterceptorEnum.OPERATIONAL_ATTRIBUTE ) )
        {
            interceptorsElement.addElement( ServerXmlIOV152.ELEMENT_OPERATIONAL_ATTRIBUTE_INTERCEPTOR );
        }
        if ( interceptors.contains( InterceptorEnum.SCHEMA ) )
        {
            interceptorsElement.addElement( ServerXmlIOV152.ELEMENT_SCHEMA_INTERCEPTOR );
        }
        if ( interceptors.contains( InterceptorEnum.SUBENTRY ) )
        {
            interceptorsElement.addElement( ServerXmlIOV152.ELEMENT_SUBENTRY_INTERCEPTOR );
        }
        if ( interceptors.contains( InterceptorEnum.COLLECTIVE_ATTRIBUTE ) )
        {
            interceptorsElement.addElement( ServerXmlIOV152.ELEMENT_COLLECTIVE_ATTRIBUTE_INTERCEPTOR );
        }
        if ( interceptors.contains( InterceptorEnum.EVENT ) )
        {
            interceptorsElement.addElement( ServerXmlIOV152.ELEMENT_EVENT_INTERCEPTOR );
        }
        if ( interceptors.contains( InterceptorEnum.TRIGGER ) )
        {
            interceptorsElement.addElement( ServerXmlIOV152.ELEMENT_TRIGGER_INTERCEPTOR );
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
            Element changePasswordServerElement = root.addElement( ServerXmlIOV152.ELEMENT_CHANGE_PASSWORD_SERVER );

            // Enabled
            changePasswordServerElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_ENABLED, ""
                + serverConfiguration.isEnableChangePassword() );

            // IpPort
            changePasswordServerElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_IP_PORT, ""
                + serverConfiguration.getChangePasswordPort() );

            // Adding 'directoryService' element
            changePasswordServerElement.addElement( ServerXmlIOV152.VALUE_DIRECTORY_SERVICE ).setText(
                "#directoryService" );

            // Adding 'datagramAcceptor' element
            changePasswordServerElement.addElement( ServerXmlIOV152.ELEMENT_DATAGRAM_ACCEPTOR ).setText(
                "#datagramAcceptor" );

            // Adding 'socketAcceptor' element
            changePasswordServerElement.addElement( ServerXmlIOV152.ELEMENT_SOCKET_ACCEPTOR ).setText(
                "#socketAcceptor" );
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
            Element kdcServerElement = root.addElement( ServerXmlIOV152.ELEMENT_KDC_SERVER );

            // Enabled
            kdcServerElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_ENABLED, ""
                + serverConfiguration.isEnableKerberos() );

            // IpPort
            kdcServerElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_IP_PORT, ""
                + serverConfiguration.getKerberosPort() );

            // Adding 'directoryService' element
            kdcServerElement.addElement( ServerXmlIOV152.VALUE_DIRECTORY_SERVICE ).setText( "#directoryService" );

            // Adding 'datagramAcceptor' element
            kdcServerElement.addElement( ServerXmlIOV152.ELEMENT_DATAGRAM_ACCEPTOR ).setText( "#datagramAcceptor" );

            // Adding 'socketAcceptor' element
            kdcServerElement.addElement( ServerXmlIOV152.ELEMENT_SOCKET_ACCEPTOR ).setText( "#socketAcceptor" );
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
            Element ntpServerElement = root.addElement( ServerXmlIOV152.ELEMENT_NTP_SERVER );

            // Enabled
            ntpServerElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_ENABLED, "" + serverConfiguration.isEnableNtp() );

            // IpPort
            ntpServerElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_IP_PORT, "" + serverConfiguration.getNtpPort() );

            // Adding 'datagramAcceptor' element
            ntpServerElement.addElement( ServerXmlIOV152.ELEMENT_DATAGRAM_ACCEPTOR ).setText( "#datagramAcceptor" );

            // Adding 'socketAcceptor' element
            ntpServerElement.addElement( ServerXmlIOV152.ELEMENT_SOCKET_ACCEPTOR ).setText( "#socketAcceptor" );
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
            Element dnsServerElement = root.addElement( ServerXmlIOV152.ELEMENT_DNS_SERVER );

            // Enabled
            dnsServerElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_ENABLED, "" + serverConfiguration.isEnableDns() );

            // IpPort
            dnsServerElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_IP_PORT, "" + serverConfiguration.getDnsPort() );

            // Adding 'directoryService' element
            dnsServerElement.addElement( ServerXmlIOV152.VALUE_DIRECTORY_SERVICE ).setText( "#directoryService" );

            // Adding 'datagramAcceptor' element
            dnsServerElement.addElement( ServerXmlIOV152.ELEMENT_DATAGRAM_ACCEPTOR ).setText( "#datagramAcceptor" );

            // Adding 'socketAcceptor' element
            dnsServerElement.addElement( ServerXmlIOV152.ELEMENT_SOCKET_ACCEPTOR ).setText( "#socketAcceptor" );
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
            Element ldapServerElement = root.addElement( ServerXmlIOV152.ELEMENT_LDAP_SERVER );

            // Id
            ldapServerElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_ID, ServerXmlIOV152.ELEMENT_LDAPS_SERVER );

            // IpPort
            ldapServerElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_IP_PORT, "" + serverConfiguration.getLdapsPort() );

            // Enabled
            ldapServerElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_ENABLED, "" + "true" );

            // EnableLdaps
            ldapServerElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_ENABLE_LDAPS, "" + "true" );

            // Adding 'directoryService' element
            ldapServerElement.addElement( ServerXmlIOV152.VALUE_DIRECTORY_SERVICE ).setText( "#directoryService" );

            // Adding 'socketAcceptor' element
            ldapServerElement.addElement( ServerXmlIOV152.ELEMENT_SOCKET_ACCEPTOR ).setText( "#socketAcceptor" );
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
        Element ldapServerElement = root.addElement( ServerXmlIOV152.ELEMENT_LDAP_SERVER );

        // Enabled
        ldapServerElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_ENABLED, "" + serverConfiguration.isEnableLdap() );

        // Id
        ldapServerElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_ID, ServerXmlIOV152.ELEMENT_LDAP_SERVER );

        // IpPort
        ldapServerElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_IP_PORT, "" + serverConfiguration.getLdapPort() );

        // AllowAnonymousAccess
        ldapServerElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_ALLOW_ANONYMOUS_ACCESS, ""
            + serverConfiguration.isAllowAnonymousAccess() );

        // SaslHost
        ldapServerElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_SASL_HOST, "" + serverConfiguration.getSaslHost() );

        // SaslPrincipal
        ldapServerElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_SASL_PRINCIPAL, ""
            + serverConfiguration.getSaslPrincipal() );

        // SearchBaseDn
        ldapServerElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_SEARCH_BASE_DN, "ou=users,ou=system" );

        // MaxTimeLimit
        ldapServerElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_MAX_TIME_LIMIT, ""
            + serverConfiguration.getMaxTimeLimit() );

        // MaxSizeLimit
        ldapServerElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_MAX_SIZE_LIMIT, ""
            + serverConfiguration.getMaxSizeLimit() );

        // Adding 'directoryService' element
        ldapServerElement.addElement( ServerXmlIOV152.VALUE_DIRECTORY_SERVICE ).setText( "#directoryService" );

        // Adding 'socketAcceptor' element
        ldapServerElement.addElement( ServerXmlIOV152.ELEMENT_SOCKET_ACCEPTOR ).setText( "#socketAcceptor" );

        // Adding 'supportedMechanisms' element
        Element supportedMechanismsElement = ldapServerElement
            .addElement( ServerXmlIOV152.ELEMENT_SUPPORTED_MECHANISMS );

        // Adding each supported mechanism
        for ( SupportedMechanismEnum supportedMechanism : serverConfiguration.getSupportedMechanisms() )
        {
            switch ( supportedMechanism )
            {
                case SIMPLE:
                    supportedMechanismsElement.addElement(
                        new QName( ServerXmlIOV152.ELEMENT_VALUE, NAMESPACE_SPRINGFRAMEWORK ) ).setText(
                        ServerXmlIOV152.SUPPORTED_MECHANISM_SIMPLE );
                    break;
                case CRAM_MD5:
                    supportedMechanismsElement.addElement(
                        new QName( ServerXmlIOV152.ELEMENT_VALUE, NAMESPACE_SPRINGFRAMEWORK ) ).setText(
                        ServerXmlIOV152.SUPPORTED_MECHANISM_CRAM_MD5 );
                    break;
                case DIGEST_MD5:
                    supportedMechanismsElement.addElement(
                        new QName( ServerXmlIOV152.ELEMENT_VALUE, NAMESPACE_SPRINGFRAMEWORK ) ).setText(
                        ServerXmlIOV152.SUPPORTED_MECHANISM_DIGEST_MD5 );
                    break;
                case GSSAPI:
                    supportedMechanismsElement.addElement(
                        new QName( ServerXmlIOV152.ELEMENT_VALUE, NAMESPACE_SPRINGFRAMEWORK ) ).setText(
                        ServerXmlIOV152.SUPPORTED_MECHANISM_GSSAPI );
                    break;
            }
        }

        // Adding 'SaslQop' element
        Element saslQopElement = ldapServerElement.addElement( ServerXmlIOV152.ELEMENT_SASL_QOP );

        // Adding each SaslQop item
        for ( SaslQualityOfProtectionEnum saslQop : serverConfiguration.getSaslQops() )
        {
            switch ( saslQop )
            {
                case AUTH:
                    saslQopElement.addElement( new QName( ServerXmlIOV152.ELEMENT_VALUE, NAMESPACE_SPRINGFRAMEWORK ) )
                        .setText( ServerXmlIOV152.SASL_QOP_AUTH );
                    break;
                case AUTH_INT:
                    saslQopElement.addElement( new QName( ServerXmlIOV152.ELEMENT_VALUE, NAMESPACE_SPRINGFRAMEWORK ) )
                        .setText( ServerXmlIOV152.SASL_QOP_AUTH_INT );
                    break;
                case AUTH_CONF:
                    saslQopElement.addElement( new QName( ServerXmlIOV152.ELEMENT_VALUE, NAMESPACE_SPRINGFRAMEWORK ) )
                        .setText( ServerXmlIOV152.SASL_QOP_AUTH_CONF );
                    break;
            }
        }

        // Adding 'SaslRealms' element
        Element saslRealmsElement = ldapServerElement.addElement( ServerXmlIOV152.ELEMENT_SASL_REALMS );

        // Adding each SaslRealm item
        for ( String saslRealm : serverConfiguration.getSaslRealms() )
        {
            saslRealmsElement.addElement( new QName( ServerXmlIOV152.ELEMENT_VALUE, NAMESPACE_SPRINGFRAMEWORK ) )
                .setText( saslRealm );
        }

        // Adding 'ExtendedOperations' element
        Element extendedOperationsElement = ldapServerElement
            .addElement( ServerXmlIOV152.ELEMENT_EXTENDED_OPERATION_HANDLERS );

        // Adding each extended operation item
        List<ExtendedOperationEnum> extendedOperations = serverConfiguration.getExtendedOperations();
        if ( extendedOperations.contains( ExtendedOperationEnum.START_TLS ) )
        {
            extendedOperationsElement.addElement( ServerXmlIOV152.ELEMENT_START_TLS_HANDLER );
        }
        if ( extendedOperations.contains( ExtendedOperationEnum.GRACEFUL_SHUTDOWN ) )
        {
            extendedOperationsElement.addElement( ServerXmlIOV152.ELEMENT_GRACEFUL_SHUTDOWN_HANDLER );
        }
        if ( extendedOperations.contains( ExtendedOperationEnum.LAUNCH_DIAGNOSTIC_UI ) )
        {
            extendedOperationsElement.addElement( ServerXmlIOV152.ELEMENT_LAUNCH_DIAGNOSTIC_UI_HANDLER );
        }
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
        Element apacheDSElement = root.addElement( ServerXmlIOV152.ELEMENT_APACHE_DS );

        // Id
        apacheDSElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_ID, ServerXmlIOV152.ELEMENT_APACHE_DS );

        // SynchPeriodMillis
        apacheDSElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_SYNCH_PERIOD_MILLIS, ""
            + serverConfiguration.getSynchronizationPeriod() );

        // AllowAnonymousAccess
        apacheDSElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_ALLOW_ANONYMOUS_ACCESS, ""
            + serverConfiguration.isAllowAnonymousAccess() );

        // Adding 'directoryService' element
        apacheDSElement.addElement( ServerXmlIOV152.VALUE_DIRECTORY_SERVICE ).setText( "#directoryService" );

        // Adding 'ldapServer' element
        apacheDSElement.addElement( ServerXmlIOV152.ELEMENT_LDAP_SERVER ).setText( "#ldapServer" );

        // LDAP Protocol
        if ( serverConfiguration.isEnableLdaps() )
        {
            // Adding 'ldapsServer' element
            apacheDSElement.addElement( ServerXmlIOV152.ELEMENT_LDAPS_SERVER ).setText( "#ldapsServer" );
        }

        // LDAPS Protocol
        if ( serverConfiguration.isEnableLdaps() )
        {
            // Adding 'ldapsServer' element
            apacheDSElement.addElement( ServerXmlIOV152.ELEMENT_LDAPS_SERVER ).setText( "#ldapsServer" );
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
        Element beanElement = root.addElement( new QName( ServerXmlIOV152.ELEMENT_BEAN, NAMESPACE_XBEAN_SPRING ) );
        beanElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_CLASS,
            "org.springframework.beans.factory.config.CustomEditorConfigurer" );

        // Adding the 'property' element
        Element propertyElement = beanElement.addElement( new QName( ServerXmlIOV152.ELEMENT_PROPERTY,
            NAMESPACE_XBEAN_SPRING ) );
        propertyElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_NAME, ServerXmlIOV152.VALUE_CUSTOM_EDITORS );

        // Adding the 'map' element
        Element mapElement = propertyElement
            .addElement( new QName( ServerXmlIOV152.ELEMENT_MAP, NAMESPACE_XBEAN_SPRING ) );

        // Adding the 'entry' element
        Element entryElement = mapElement
            .addElement( new QName( ServerXmlIOV152.ELEMENT_ENTRY, NAMESPACE_XBEAN_SPRING ) );
        entryElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_KEY, "javax.naming.directory.Attributes" );

        // Adding the inner 'bean' element
        Element innerBeanElement = entryElement.addElement( new QName( ServerXmlIOV152.ELEMENT_BEAN,
            NAMESPACE_XBEAN_SPRING ) );
        innerBeanElement.addAttribute( ServerXmlIOV152.ATTRIBUTE_CLASS,
            "org.apache.directory.server.core.configuration.AttributesPropertyEditor" );
    }
}
