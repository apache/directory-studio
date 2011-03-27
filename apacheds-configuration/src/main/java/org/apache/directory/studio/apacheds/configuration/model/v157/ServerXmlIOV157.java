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
package org.apache.directory.studio.apacheds.configuration.model.v157;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.studio.apacheds.configuration.StudioEntityResolver;
import org.apache.directory.studio.apacheds.configuration.model.AbstractServerXmlIO;
import org.apache.directory.studio.apacheds.configuration.model.ServerConfiguration;
import org.apache.directory.studio.apacheds.configuration.model.ServerXmlIO;
import org.apache.directory.studio.apacheds.configuration.model.ServerXmlIOException;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;


/**
 * This class implements a parser and a writer for the 'server.xml' file of 
 * Apache Directory Server version 1.5.7.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ServerXmlIOV157 extends AbstractServerXmlIO implements ServerXmlIO
{
    private static final String ATTRIBUTE_ACCESS_CONTROL_ENABLED = "accessControlEnabled"; //$NON-NLS-1$
    private static final String ATTRIBUTE_ADDRESS = "address"; //$NON-NLS-1$
    private static final String ATTRIBUTE_ALLOW_ANONYMOUS_ACCESS = "allowAnonymousAccess"; //$NON-NLS-1$
    private static final String ATTRIBUTE_ATTRIBUTE_ID = "attributeId"; //$NON-NLS-1$
    private static final String ATTRIBUTE_BACKLOG = "backLog"; //$NON-NLS-1$
    private static final String ATTRIBUTE_CACHE_SIZE = "cacheSize"; //$NON-NLS-1$
    private static final String ATTRIBUTE_DENORMALIZE_OP_ATTRS_ENABLED = "denormalizeOpAttrsEnabled"; //$NON-NLS-1$
    private static final String ATTRIBUTE_ENABLESSL = "enableSSL"; //$NON-NLS-1$
    private static final String ATTRIBUTE_ID = "id"; //$NON-NLS-1$
    private static final String ATTRIBUTE_INSTANCE_ID = "instanceId"; //$NON-NLS-1$
    private static final String ATTRIBUTE_MAXPDUSIZE = "maxPDUSize"; //$NON-NLS-1$
    private static final String ATTRIBUTE_MAX_SIZE_LIMIT = "maxSizeLimit"; //$NON-NLS-1$
    private static final String ATTRIBUTE_MAX_TIME_LIMIT = "maxTimeLimit"; //$NON-NLS-1$
    private static final String ATTRIBUTE_MECH_NAME = "mech-name"; //$NON-NLS-1$
    private static final String ATTRIBUTE_NBTHREADS = "nbThreads"; //$NON-NLS-1$
    private static final String ATTRIBUTE_NTLM_PROVIDER_FQCN = "ntlmProviderFqcn"; //$NON-NLS-1$
    private static final String ATTRIBUTE_PORT = "port"; //$NON-NLS-1$
    private static final String ATTRIBUTE_OPTIMIZER_ENABLED = "optimizerEnabled"; //$NON-NLS-1$
    private static final String ATTRIBUTE_REPLICAID = "replicaId"; //$NON-NLS-1$
    private static final String ATTRIBUTE_SASL_HOST = "saslHost"; //$NON-NLS-1$
    private static final String ATTRIBUTE_SASL_PRINCIPAL = "saslPrincipal"; //$NON-NLS-1$
    private static final String ATTRIBUTE_SEARCH_BASE_DN = "searchBaseDn"; //$NON-NLS-1$
    private static final String ATTRIBUTE_SUFFIX = "suffix"; //$NON-NLS-1$
    private static final String ATTRIBUTE_SYNC_ON_WRITE = "syncOnWrite"; //$NON-NLS-1$
    private static final String ATTRIBUTE_SYNCH_PERIOD_MILLIS = "syncPeriodMillis"; //$NON-NLS-1$
    private static final String ATTRIBUTE_WORKING_DIRECTORY = "workingDirectory"; //$NON-NLS-1$
    private static final String ELEMENT_ACI_AUTHORIZATION_INTERCEPTOR = "aciAuthorizationInterceptor"; //$NON-NLS-1$
    private static final String ELEMENT_APACHE_DS = "apacheDS"; //$NON-NLS-1$
    private static final String ELEMENT_AUTHENTICATION_INTERCEPTOR = "authenticationInterceptor"; //$NON-NLS-1$
    private static final String ELEMENT_BEANS = "beans"; //$NON-NLS-1$
    private static final String ELEMENT_CHANGE_PASSWORD_SERVER = "changePasswordServer"; //$NON-NLS-1$
    private static final String ELEMENT_COLLECTIVE_ATTRIBUTE_INTERCEPTOR = "collectiveAttributeInterceptor"; //$NON-NLS-1$
    private static final String ELEMENT_CRAM_MD5_MECHANISM_HANDLER = "cramMd5MechanismHandler"; //$NON-NLS-1$
    private static final String ELEMENT_DEFAULT_AUTHORIZATION_INTERCEPTOR = "defaultAuthorizationInterceptor"; //$NON-NLS-1$
    private static final String ELEMENT_DEFAULT_DIRECTORY_SERVICE = "defaultDirectoryService"; //$NON-NLS-1$
    private static final String ELEMENT_DIGEST_MD5_MECHANISM_HANDLER = "digestMd5MechanismHandler"; //$NON-NLS-1$
    private static final String ELEMENT_DNS_SERVER = "dnsServer"; //$NON-NLS-1$
    private static final String ELEMENT_EVENT_INTERCEPTOR = "eventInterceptor"; //$NON-NLS-1$
    private static final String ELEMENT_EXCEPTION_INTERCEPTOR = "exceptionInterceptor"; //$NON-NLS-1$
    private static final String ELEMENT_EXTENDED_OPERATION_HANDLERS = "extendedOperationHandlers"; //$NON-NLS-1$
    private static final String ELEMENT_GRACEFUL_SHUTDOWN_HANDLER = "gracefulShutdownHandler"; //$NON-NLS-1$
    private static final String ELEMENT_GSSAPI_MECHANISM_HANDLER = "gssapiMechanismHandler"; //$NON-NLS-1$
    private static final String ELEMENT_INDEXED_ATTRIBUTES = "indexedAttributes"; //$NON-NLS-1$
    private static final String ELEMENT_INTERCEPTORS = "interceptors"; //$NON-NLS-1$
    private static final String ELEMENT_JDBM_INDEX = "jdbmIndex"; //$NON-NLS-1$
    private static final String ELEMENT_JDBM_PARTITION = "jdbmPartition"; //$NON-NLS-1$
    private static final String ELEMENT_KDC_SERVER = "kdcServer"; //$NON-NLS-1$
    private static final String ELEMENT_KEY_DERIVATION_INTERCEPTOR = "keyDerivationInterceptor"; //$NON-NLS-1$
    private static final String ELEMENT_LAUNCH_DIAGNOSTIC_UI_HANDLER = "launchDiagnosticUiHandler"; //$NON-NLS-1$
    private static final String ELEMENT_LDAP_SERVER = "ldapServer"; //$NON-NLS-1$
    private static final String ELEMENT_NORMALIZATION_INTERCEPTOR = "normalizationInterceptor"; //$NON-NLS-1$
    private static final String ELEMENT_NTLM_MECHANISM_HANDLER = "ntlmMechanismHandler"; //$NON-NLS-1$
    private static final String ELEMENT_NTP_SERVER = "ntpServer"; //$NON-NLS-1$
    private static final String ELEMENT_OPERATIONAL_ATTRIBUTE_INTERCEPTOR = "operationalAttributeInterceptor"; //$NON-NLS-1$
    private static final String ELEMENT_PARTITIONS = "partitions"; //$NON-NLS-1$
    private static final String ELEMENT_REFERRAL_INTERCEPTOR = "referralInterceptor"; //$NON-NLS-1$
    private static final String ELEMENT_REPLICATION_INTERCEPTOR = "replicationInterceptor"; //$NON-NLS-1$
    private static final String ELEMENT_SASL_MECHANISM_HANDLERS = "saslMechanismHandlers"; //$NON-NLS-1$
    private static final String ELEMENT_SASL_REALMS = "saslRealms"; //$NON-NLS-1$
    private static final String ELEMENT_SCHEMA_INTERCEPTOR = "schemaInterceptor"; //$NON-NLS-1$
    private static final String ELEMENT_SIMPLE_MECHANISM_HANDLER = "simpleMechanismHandler"; //$NON-NLS-1$
    private static final String ELEMENT_START_TLS_HANDLER = "startTlsHandler"; //$NON-NLS-1$
    private static final String ELEMENT_SUBENTRY_INTERCEPTOR = "subentryInterceptor"; //$NON-NLS-1$
    private static final String ELEMENT_SYSTEM_PARTITION = "systemPartition"; //$NON-NLS-1$
    private static final String ELEMENT_TCP_TRANSPORT = "tcpTransport"; //$NON-NLS-1$
    private static final String ELEMENT_TRANSPORTS = "transports"; //$NON-NLS-1$
    private static final String ELEMENT_TRIGGER_INTERCEPTOR = "triggerInterceptor"; //$NON-NLS-1$
    private static final String ELEMENT_UDP_TRANSPORT = "udpTransport"; //$NON-NLS-1$
    private static final String ELEMENT_VALUE = "value"; //$NON-NLS-1$
    private static final Namespace NAMESPACE_APACHEDS = new Namespace( null, "http://apacheds.org/config/1.5.7" ); //$NON-NLS-1$
    private static final Namespace NAMESPACE_SPRINGFRAMEWORK = new Namespace( "s", //$NON-NLS-1$
        "http://www.springframework.org/schema/beans" ); //$NON-NLS-1$
    private static final Namespace NAMESPACE_XBEAN_SPRING = new Namespace( "spring", //$NON-NLS-1$
        "http://xbean.apache.org/schemas/spring/1.0" ); //$NON-NLS-1$
    private static final String SUPPORTED_MECHANISM_CRAM_MD5 = "CRAM-MD5"; //$NON-NLS-1$
    private static final String SUPPORTED_MECHANISM_DIGEST_MD5 = "DIGEST-MD5"; //$NON-NLS-1$
    private static final String SUPPORTED_MECHANISM_GSS_SPNEGO = "GSS-SPNEGO"; //$NON-NLS-1$
    private static final String SUPPORTED_MECHANISM_GSSAPI = "GSSAPI"; //$NON-NLS-1$
    private static final String SUPPORTED_MECHANISM_NTLM = "NTLM"; //$NON-NLS-1$
    private static final String SUPPORTED_MECHANISM_SIMPLE = "SIMPLE"; //$NON-NLS-1$
    private static final String VALUE_ADDRESS_0_0_0_0 = "0.0.0.0"; //$NON-NLS-1$
    private static final String VALUE_ADDRESS_LOCALHOST = "localhost"; //$NON-NLS-1$
    private static final String VALUE_BACKLOG = "50"; //$NON-NLS-1$
    private static final String VALUE_CHANGEPASSWORDSERVER_NB_THREADS = "2"; //$NON-NLS-1$
    private static final String VALUE_DEFAULT = "default"; //$NON-NLS-1$
    private static final String VALUE_DIRECTORY_SERVICE = "directoryService"; //$NON-NLS-1$
    private static final String VALUE_EXAMPLE_DOT_COM = "example.com"; //$NON-NLS-1$
    private static final String VALUE_KDCSERVER_NB_THREADS = "4"; //$NON-NLS-1$
    private static final String VALUE_LDAPSERVER_NB_THREADS = "8"; //$NON-NLS-1$
    private static final String VALUE_MAXPDUSIZE = "2000000"; //$NON-NLS-1$
    private static final String VALUE_NTPSERVER_NB_THREADS = "1"; //$NON-NLS-1$
    private static final String VALUE_REPLICAID = "1"; //$NON-NLS-1$


    /**
     * Checks if the Document is valid.
     *
     * @param document
     *      the Document
     * @return
     *      true if the Document is valid, false if not
     */
    protected boolean isValid( Document document )
    {
        Element rootElement = document.getRootElement();
        if ( rootElement != null )
        {
            // Checking if the root element is named 'beans'
            if ( ServerXmlIOV157.ELEMENT_BEANS.equalsIgnoreCase( rootElement.getName() ) )
            {
                // Checking if we have the correct namespace
                return ( rootElement.getNamespaceForURI( NAMESPACE_APACHEDS.getURI() ) != null );
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
            reader.setEntityResolver( new StudioEntityResolver() );
            Document document = reader.read( is );

            // Parsing the document
            ServerConfigurationV157 serverConfiguration = new ServerConfigurationV157();
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
    private void parse( Document document, ServerConfigurationV157 serverConfiguration ) throws NumberFormatException,
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

        // Reading the 'ldapServer' Bean
        readLdapServerBean( rootElement, serverConfiguration );
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
    private void readDefaultDirectoryServiceBean( Element element, ServerConfigurationV157 serverConfiguration )
        throws ServerXmlIOException, NumberFormatException, BooleanFormatException
    {
        Element defaultDirectoryServiceElement = element.element( ELEMENT_DEFAULT_DIRECTORY_SERVICE );
        if ( defaultDirectoryServiceElement == null )
        {
            throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV157.ErrorDefaultDirectoryService" ) ); //$NON-NLS-1$
        }
        else
        {
            // Access Control Enabled
            org.dom4j.Attribute accessControlEnabledAttribute = defaultDirectoryServiceElement
                .attribute( ServerXmlIOV157.ATTRIBUTE_ACCESS_CONTROL_ENABLED );
            if ( accessControlEnabledAttribute == null )
            {
                // If the 'accessControlEnabled' attribute does not exists,
                // we throw an exception
                throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV157.ErrorAccessControlEnabled" ) ); //$NON-NLS-1$
            }
            else
            {
                serverConfiguration.setEnableAccessControl( parseBoolean( accessControlEnabledAttribute.getValue() ) );
            }

            // Denormalize Op Attrs Enabled
            org.dom4j.Attribute denormalizeOpAttrsEnabledAttribute = defaultDirectoryServiceElement
                .attribute( ServerXmlIOV157.ATTRIBUTE_DENORMALIZE_OP_ATTRS_ENABLED );
            if ( denormalizeOpAttrsEnabledAttribute == null )
            {
                // If the 'denormalizeOpAttrsEnabled' attribute does not exists,
                // we throw an exception
                throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV157.ErrorDenormalizeOpAttrsEnabled" ) ); //$NON-NLS-1$
            }
            else
            {
                serverConfiguration
                    .setDenormalizeOpAttr( parseBoolean( denormalizeOpAttrsEnabledAttribute.getValue() ) );
            }

            // SynchPeriodMillis
            org.dom4j.Attribute synchPeriodMillisAttribute = defaultDirectoryServiceElement
                .attribute( ServerXmlIOV157.ATTRIBUTE_SYNCH_PERIOD_MILLIS );
            if ( synchPeriodMillisAttribute == null )
            {
                // If the 'synchPeriodMillis' attribute does not exists,
                // we throw an exception
                throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV157.ErrorSyncPeriodMillis" ) ); //$NON-NLS-1$
            }
            else
            {
                serverConfiguration
                    .setSynchronizationPeriod( Integer.parseInt( synchPeriodMillisAttribute.getValue() ) );
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
     * Reads the system partition.
     *
     * @param element
     *      the element
     * @param serverConfiguration
     *      the server configuration
     * @throws ServerXmlIOException
     * @throws BooleanFormatException 
     */
    private void readSystemPartition( Element element, ServerConfigurationV157 serverConfiguration )
        throws ServerXmlIOException, NumberFormatException, BooleanFormatException
    {
        // Getting the 'systemPartition' element
        Element systemPartitionElement = element.element( ServerXmlIOV157.ELEMENT_SYSTEM_PARTITION );
        if ( systemPartitionElement == null )
        {
            // If the 'systemPartition' element does not exists,
            // we throw an exception
            throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV157.ErrorSystemPartition" ) ); //$NON-NLS-1$
        }
        else
        {
            // Getting the 'jdbmPartition' element
            Element jdbmPartitionElement = systemPartitionElement.element( ServerXmlIOV157.ELEMENT_JDBM_PARTITION );
            if ( jdbmPartitionElement == null )
            {
                // If the 'jdbmPartition' element does not exists,
                // we throw an exception
                throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV157.ErrorJDBMPartition" ) ); //$NON-NLS-1$
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
        org.dom4j.Attribute idAttribute = element.attribute( ServerXmlIOV157.ATTRIBUTE_ID );
        if ( idAttribute == null )
        {
            // If the 'id' attribute does not exists,
            // we throw an exception
            throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV157.ErrorId" ) ); //$NON-NLS-1$
        }
        else
        {
            partition.setId( idAttribute.getValue() );
        }

        // Cache Size
        org.dom4j.Attribute cacheSizeAttribute = element.attribute( ServerXmlIOV157.ATTRIBUTE_CACHE_SIZE );
        if ( cacheSizeAttribute == null )
        {
            // If the 'cacheSize' attribute does not exists,
            // we throw an exception
            throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV157.ErrorCacheSize" ) ); //$NON-NLS-1$
        }
        else
        {
            partition.setCacheSize( Integer.parseInt( cacheSizeAttribute.getValue() ) );
        }

        // Suffix
        org.dom4j.Attribute suffixAttribute = element.attribute( ServerXmlIOV157.ATTRIBUTE_SUFFIX );
        if ( suffixAttribute == null )
        {
            // If the 'suffix' attribute does not exists,
            // we throw an exception
            throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV157.ErrorSuffix" ) ); //$NON-NLS-1$
        }
        else
        {
            partition.setSuffix( suffixAttribute.getValue() );
        }

        // Optimizer Enabled
        org.dom4j.Attribute optimizerEnabledAttribute = element.attribute( ServerXmlIOV157.ATTRIBUTE_OPTIMIZER_ENABLED );
        if ( optimizerEnabledAttribute == null )
        {
            // If the 'optimizeEnabled' attribute does not exists,
            // we throw an exception
            throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV157.ErrorOptimizerEnabled" ) ); //$NON-NLS-1$
        }
        else
        {
            partition.setEnableOptimizer( parseBoolean( optimizerEnabledAttribute.getValue() ) );
        }

        // Sync On Write
        org.dom4j.Attribute syncOnWriteAttribute = element.attribute( ServerXmlIOV157.ATTRIBUTE_SYNC_ON_WRITE );
        if ( syncOnWriteAttribute == null )
        {
            // If the 'syncOnWrite' attribute does not exists,
            // we throw an exception
            throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV157.ErrorSyncOnWrite" ) ); //$NON-NLS-1$
        }
        else
        {
            partition.setSynchronizationOnWrite( parseBoolean( syncOnWriteAttribute.getValue() ) );
        }

        // Indexed attributes
        partition.setIndexedAttributes( readIndexedAttributes( element ) );
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
        Element indexedAttributesElement = element.element( ServerXmlIOV157.ELEMENT_INDEXED_ATTRIBUTES );
        if ( indexedAttributesElement != null )
        {
            // Looping on 'jdbmIndex' elements
            for ( Iterator<?> i = indexedAttributesElement.elementIterator( ServerXmlIOV157.ELEMENT_JDBM_INDEX ); i
                .hasNext(); )
            {
                // Getting the 'jdbmIndex' element
                Element jdbmIndexElement = ( Element ) i.next();

                // Getting the 'attributeId' attribute
                org.dom4j.Attribute attributeIdAttribute = jdbmIndexElement
                    .attribute( ServerXmlIOV157.ATTRIBUTE_ATTRIBUTE_ID );
                if ( attributeIdAttribute != null )
                {
                    // Getting the 'cacheSize' attribute
                    org.dom4j.Attribute cacheSizeAttribute = jdbmIndexElement
                        .attribute( ServerXmlIOV157.ATTRIBUTE_CACHE_SIZE );
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
    private void readPartitions( Element element, ServerConfigurationV157 serverConfiguration )
        throws NumberFormatException, ServerXmlIOException, BooleanFormatException
    {
        // Getting the 'partitions'element
        Element partitionsElement = element.element( ServerXmlIOV157.ELEMENT_PARTITIONS );
        if ( partitionsElement != null )
        {
            // Looping on all 'jdbmPartition' tags
            for ( Iterator<?> i = partitionsElement.elementIterator( ServerXmlIOV157.ELEMENT_JDBM_PARTITION ); i
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
    private void readInterceptors( Element element, ServerConfigurationV157 serverConfiguration )
    {
        // Getting the 'interceptors
        Element interceptorsElement = element.element( ServerXmlIOV157.ELEMENT_INTERCEPTORS );
        if ( interceptorsElement != null )
        {
            // Looping on all interceptor elements
            for ( Iterator<?> i = interceptorsElement.elementIterator(); i.hasNext(); )
            {
                // Getting the element
                Element interceptorElement = ( Element ) i.next();

                // Checking which interceptor it is
                String interceptorElementName = interceptorElement.getName();
                if ( ServerXmlIOV157.ELEMENT_NORMALIZATION_INTERCEPTOR.equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.NORMALIZATION );
                }
                else if ( ServerXmlIOV157.ELEMENT_AUTHENTICATION_INTERCEPTOR.equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.AUTHENTICATION );
                }
                else if ( ServerXmlIOV157.ELEMENT_REFERRAL_INTERCEPTOR.equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.REFERRAL );
                }
                else if ( ServerXmlIOV157.ELEMENT_ACI_AUTHORIZATION_INTERCEPTOR
                    .equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.ACI_AUTHORIZATION );
                }
                else if ( ServerXmlIOV157.ELEMENT_DEFAULT_AUTHORIZATION_INTERCEPTOR
                    .equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.DEFAULT_AUTHORIZATION );
                }
                else if ( ServerXmlIOV157.ELEMENT_EXCEPTION_INTERCEPTOR.equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.EXCEPTION );
                }
                else if ( ServerXmlIOV157.ELEMENT_OPERATIONAL_ATTRIBUTE_INTERCEPTOR
                    .equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.OPERATIONAL_ATTRIBUTE );
                }
                else if ( ServerXmlIOV157.ELEMENT_KEY_DERIVATION_INTERCEPTOR
                    .equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.KEY_DERIVATION );
                }
                else if ( ServerXmlIOV157.ELEMENT_SCHEMA_INTERCEPTOR.equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.SCHEMA );
                }
                else if ( ServerXmlIOV157.ELEMENT_SUBENTRY_INTERCEPTOR.equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.SUBENTRY );
                }
                else if ( ServerXmlIOV157.ELEMENT_COLLECTIVE_ATTRIBUTE_INTERCEPTOR
                    .equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.COLLECTIVE_ATTRIBUTE );
                }
                else if ( ServerXmlIOV157.ELEMENT_EVENT_INTERCEPTOR.equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.EVENT );
                }
                else if ( ServerXmlIOV157.ELEMENT_TRIGGER_INTERCEPTOR.equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.TRIGGER );
                }
                else if ( ServerXmlIOV157.ELEMENT_REPLICATION_INTERCEPTOR.equalsIgnoreCase( interceptorElementName ) )
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
    private void readChangePasswordServerBean( Element element, ServerConfigurationV157 serverConfiguration )
        throws NumberFormatException, ServerXmlIOException, BooleanFormatException
    {
        // By default, the protocol is not enabled
        serverConfiguration.setEnableChangePassword( false );

        // Getting the 'changePasswordServer' element
        Element changePasswordServerElement = element.element( ServerXmlIOV157.ELEMENT_CHANGE_PASSWORD_SERVER );
        if ( changePasswordServerElement != null )
        {
            serverConfiguration.setEnableChangePassword( true );

            // Getting the 'transports' element
            Element transportsElement = changePasswordServerElement.element( ServerXmlIOV157.ELEMENT_TRANSPORTS );
            if ( transportsElement != null )
            {
                // Getting the 'tcpTransport' element
                Element tcpTransportElement = transportsElement.element( ServerXmlIOV157.ELEMENT_TCP_TRANSPORT );
                if ( tcpTransportElement != null )
                {
                    // Getting the 'port' attribute
                    org.dom4j.Attribute portAttribute = tcpTransportElement.attribute( ServerXmlIOV157.ATTRIBUTE_PORT );
                    if ( portAttribute != null )
                    {
                        serverConfiguration.setChangePasswordPort( Integer.parseInt( portAttribute.getValue() ) );
                    }
                    else
                    {
                        // If the 'port' attribute does not exists,
                        // we throw an exception
                        throw new ServerXmlIOException(
                            Messages.getString( "ServerXmlIOV157.ErrorChangePasswordServerPort" ) ); //$NON-NLS-1$
                    }
                }
                else
                {
                    // If the 'tcpTransport' element does not exists,
                    // we throw an exception
                    throw new ServerXmlIOException(
                        Messages.getString( "ServerXmlIOV157.ErrorChangePasswordServerPort" ) ); //$NON-NLS-1$
                }
            }
            else
            {
                // If the 'transports' element does not exists,
                // we throw an exception
                throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV157.ErrorChangePasswordServerPort" ) ); //$NON-NLS-1$
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
    private void readKdcServerBean( Element element, ServerConfigurationV157 serverConfiguration )
        throws NumberFormatException, ServerXmlIOException, BooleanFormatException
    {
        // By default, the protocol is not enabled
        serverConfiguration.setEnableKerberos( false );

        // Getting the 'kdcServer' element
        Element kdcServerElement = element.element( ServerXmlIOV157.ELEMENT_KDC_SERVER );
        if ( kdcServerElement != null )
        {
            serverConfiguration.setEnableKerberos( true );

            // Getting the 'transports' element
            Element transportsElement = kdcServerElement.element( ServerXmlIOV157.ELEMENT_TRANSPORTS );
            if ( transportsElement != null )
            {
                // Getting the 'tcpTransport' element
                Element tcpTransportElement = transportsElement.element( ServerXmlIOV157.ELEMENT_TCP_TRANSPORT );
                if ( tcpTransportElement != null )
                {
                    // Getting the 'port' attribute
                    org.dom4j.Attribute portAttribute = tcpTransportElement.attribute( ServerXmlIOV157.ATTRIBUTE_PORT );
                    if ( portAttribute != null )
                    {
                        serverConfiguration.setKerberosPort( Integer.parseInt( portAttribute.getValue() ) );
                    }
                    else
                    {
                        // If the 'port' attribute does not exists,
                        // we throw an exception
                        throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV157.ErrorKdcServerPort" ) ); //$NON-NLS-1$
                    }
                }
                else
                {
                    // If the 'tcpTransport' element does not exists,
                    // we throw an exception
                    throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV157.ErrorKdcServerPort" ) ); //$NON-NLS-1$
                }
            }
            else
            {
                // If the 'transports' element does not exists,
                // we throw an exception
                throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV157.ErrorKdcServerPort" ) ); //$NON-NLS-1$
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
    private void readNtpServerBean( Element element, ServerConfigurationV157 serverConfiguration )
        throws NumberFormatException, ServerXmlIOException, BooleanFormatException
    {
        // By default, the protocol is not enabled
        serverConfiguration.setEnableNtp( false );

        // Getting the 'ntpServer' element
        Element ntpServerElement = element.element( ServerXmlIOV157.ELEMENT_NTP_SERVER );
        if ( ntpServerElement != null )
        {
            serverConfiguration.setEnableNtp( true );

            // Getting the 'transports' element
            Element transportsElement = ntpServerElement.element( ServerXmlIOV157.ELEMENT_TRANSPORTS );
            if ( transportsElement != null )
            {
                // Getting the 'tcpTransport' element
                Element tcpTransportElement = transportsElement.element( ServerXmlIOV157.ELEMENT_TCP_TRANSPORT );
                if ( tcpTransportElement != null )
                {
                    // Getting the 'port' attribute
                    org.dom4j.Attribute portAttribute = tcpTransportElement.attribute( ServerXmlIOV157.ATTRIBUTE_PORT );
                    if ( portAttribute != null )
                    {
                        serverConfiguration.setNtpPort( Integer.parseInt( portAttribute.getValue() ) );
                    }
                    else
                    {
                        // If the 'port' attribute does not exists,
                        // we throw an exception
                        throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV157.ErrorNtpServerPort" ) ); //$NON-NLS-1$
                    }
                }
                else
                {
                    // If the 'tcpTransport' element does not exists,
                    // we throw an exception
                    throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV157.ErrorNtpServerPort" ) ); //$NON-NLS-1$
                }
            }
            else
            {
                // If the 'transports' element does not exists,
                // we throw an exception
                throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV157.ErrorNtpServerPort" ) ); //$NON-NLS-1$
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
    private void readDnsServerBean( Element element, ServerConfigurationV157 serverConfiguration )
        throws NumberFormatException, ServerXmlIOException, BooleanFormatException
    {
        // By default, the protocol is not enabled
        serverConfiguration.setEnableDns( false );

        // Getting the 'dnsServer' element
        Element dnsServerElement = element.element( ServerXmlIOV157.ELEMENT_DNS_SERVER );
        if ( dnsServerElement != null )
        {
            serverConfiguration.setEnableDns( true );

            // Getting the 'transports' element
            Element transportsElement = dnsServerElement.element( ServerXmlIOV157.ELEMENT_TRANSPORTS );
            if ( transportsElement != null )
            {
                // Getting the 'tcpTransport' element
                Element tcpTransportElement = transportsElement.element( ServerXmlIOV157.ELEMENT_TCP_TRANSPORT );
                if ( tcpTransportElement != null )
                {
                    // Getting the 'port' attribute
                    org.dom4j.Attribute portAttribute = tcpTransportElement.attribute( ServerXmlIOV157.ATTRIBUTE_PORT );
                    if ( portAttribute != null )
                    {
                        serverConfiguration.setDnsPort( Integer.parseInt( portAttribute.getValue() ) );
                    }
                    else
                    {
                        // If the 'port' attribute does not exists,
                        // we throw an exception
                        throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV157.ErrorDnsServerPort" ) ); //$NON-NLS-1$
                    }
                }
                else
                {
                    // If the 'tcpTransport' element does not exists,
                    // we throw an exception
                    throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV157.ErrorDnsServerPort" ) ); //$NON-NLS-1$
                }
            }
            else
            {
                // If the 'transports' element does not exists,
                // we throw an exception
                throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV157.ErrorDnsServerPort" ) ); //$NON-NLS-1$
            }
        }
    }


    /**
     * Reads the LdapServer Bean.
     *
     * @param element
     *      the element
     * @param serverConfiguration
     *      the server configuration
     * @throws NumberFormatException
     * @throws ServerXmlIOException 
     * @throws BooleanFormatException 
     */
    private void readLdapServerBean( Element element, ServerConfigurationV157 serverConfiguration )
        throws NumberFormatException, ServerXmlIOException, BooleanFormatException
    {
        // By default, the protocols are not enabled
        serverConfiguration.setEnableLdap( false );
        serverConfiguration.setEnableLdaps( false );

        // Getting the 'ldapServer' element
        Element ldapServerElement = element.element( ServerXmlIOV157.ELEMENT_LDAP_SERVER );
        if ( ldapServerElement != null )
        {
            // Allow Anonymous Access
            org.dom4j.Attribute allowAnonymousAccessAttribute = ldapServerElement
                .attribute( ServerXmlIOV157.ATTRIBUTE_ALLOW_ANONYMOUS_ACCESS );
            if ( allowAnonymousAccessAttribute == null )
            {
                // If the 'allowAnonymousAccess' attribute does not exists,
                // we throw an exception
                throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV157.ErrorAllowAnonymousAccess" ) ); //$NON-NLS-1$
            }
            else
            {
                serverConfiguration.setAllowAnonymousAccess( parseBoolean( allowAnonymousAccessAttribute.getValue() ) );
            }

            // SaslHost
            org.dom4j.Attribute saslHostAttribute = ldapServerElement.attribute( ServerXmlIOV157.ATTRIBUTE_SASL_HOST );
            if ( saslHostAttribute == null )
            {
                // If the 'saslHost' attribute does not exists,
                // we throw an exception
                throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV157.ErrorSaslHost" ) ); //$NON-NLS-1$
            }
            else
            {
                serverConfiguration.setSaslHost( saslHostAttribute.getValue() );
            }

            // SaslPrincipal
            org.dom4j.Attribute saslPrincipalAttribute = ldapServerElement
                .attribute( ServerXmlIOV157.ATTRIBUTE_SASL_PRINCIPAL );
            if ( saslPrincipalAttribute == null )
            {
                // If the 'saslPrincipal' attribute does not exists,
                // we throw an exception
                throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV157.ErrorSaslPrincipal" ) ); //$NON-NLS-1$
            }
            else
            {
                serverConfiguration.setSaslPrincipal( saslPrincipalAttribute.getValue() );
            }

            // SearchBaseDn
            org.dom4j.Attribute searchBaseDnAttribute = ldapServerElement
                .attribute( ServerXmlIOV157.ATTRIBUTE_SEARCH_BASE_DN );
            if ( searchBaseDnAttribute == null )
            {
                // If the 'searchBaseDn' attribute does not exists,
                // we throw an exception
                throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV157.ErrorSearchBaseDn" ) ); //$NON-NLS-1$
            }
            else
            {
                serverConfiguration.setSearchBaseDn( searchBaseDnAttribute.getValue() );
            }

            // MaxTimeLimit
            org.dom4j.Attribute maxTimeLimitAttribute = ldapServerElement
                .attribute( ServerXmlIOV157.ATTRIBUTE_MAX_TIME_LIMIT );
            if ( maxTimeLimitAttribute == null )
            {
                // If the 'maxTimeLimit' attribute does not exists,
                // we throw an exception
                throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV157.ErrorMaxTimeLimit" ) ); //$NON-NLS-1$
            }
            else
            {
                serverConfiguration.setMaxTimeLimit( Integer.parseInt( maxTimeLimitAttribute.getValue() ) );
            }

            // MaxSizeLimit
            org.dom4j.Attribute maxSizeLimitAttribute = ldapServerElement
                .attribute( ServerXmlIOV157.ATTRIBUTE_MAX_SIZE_LIMIT );
            if ( maxSizeLimitAttribute == null )
            {
                // If the 'maxSizeLimit' attribute does not exists,
                // we throw an exception
                throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV157.ErrorMaxSizeLimit" ) ); //$NON-NLS-1$
            }
            else
            {
                serverConfiguration.setMaxSizeLimit( Integer.parseInt( maxSizeLimitAttribute.getValue() ) );
            }

            // Getting the 'transports' element
            Element transportsElement = ldapServerElement.element( ServerXmlIOV157.ELEMENT_TRANSPORTS );
            if ( transportsElement != null )
            {
                // Looping on all 'tcpTransport' elements
                for ( Iterator<?> iterator = transportsElement.elementIterator( ServerXmlIOV157.ELEMENT_TCP_TRANSPORT ); iterator
                    .hasNext(); )
                {
                    // Getting the 'tcpTransport' element
                    Element tcpTransportElement = ( Element ) iterator.next();

                    // Getting the 'port' attribute
                    org.dom4j.Attribute portAttribute = tcpTransportElement.attribute( ServerXmlIOV157.ATTRIBUTE_PORT );
                    if ( portAttribute == null )
                    {
                        // If the 'port' attribute does not exists,
                        // we throw an exception
                        throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV157.ErrorLdapServerPort" ) ); //$NON-NLS-1$
                    }

                    // Getting the 'enableSSL' attribute
                    boolean enableSsl = false;
                    org.dom4j.Attribute enableSslAttribut = tcpTransportElement
                        .attribute( ServerXmlIOV157.ATTRIBUTE_ENABLESSL );
                    if ( enableSslAttribut != null )
                    {
                        enableSsl = parseBoolean( enableSslAttribut.getValue() );
                    }

                    // Enabling the right protocol
                    if ( enableSsl )
                    {
                        serverConfiguration.setEnableLdaps( true );
                        serverConfiguration.setLdapsPort( Integer.parseInt( portAttribute.getValue() ) );
                    }
                    else
                    {
                        serverConfiguration.setEnableLdap( true );
                        serverConfiguration.setLdapPort( Integer.parseInt( portAttribute.getValue() ) );
                    }

                }
            }
            else
            {
                // If the 'transports' element does not exists,
                // we throw an exception
                throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV157.ErrorLdapServerPort" ) ); //$NON-NLS-1$
            }

            // Supported Mechanisms
            Element supportedMechanismsElement = ldapServerElement
                .element( ServerXmlIOV157.ELEMENT_SASL_MECHANISM_HANDLERS );
            if ( supportedMechanismsElement != null )
            {
                // Looping on all elements
                for ( Iterator<?> iterator = supportedMechanismsElement.elementIterator(); iterator.hasNext(); )
                {
                    // Getting the  element
                    Element supportedMechanismValueElement = ( Element ) iterator.next();
                    String supportedMechanismValue = supportedMechanismValueElement.getName();
                    org.dom4j.Attribute mechNameAttribute = supportedMechanismValueElement
                        .attribute( ServerXmlIOV157.ATTRIBUTE_MECH_NAME );
                    String mechNameValue = ( mechNameAttribute == null ) ? null : mechNameAttribute.getValue();

                    if ( ServerXmlIOV157.ELEMENT_SIMPLE_MECHANISM_HANDLER.equalsIgnoreCase( supportedMechanismValue )
                        && ServerXmlIOV157.SUPPORTED_MECHANISM_SIMPLE.equalsIgnoreCase( mechNameValue ) )
                    {
                        serverConfiguration.addSupportedMechanism( SupportedMechanismEnum.SIMPLE );
                    }
                    else if ( ServerXmlIOV157.ELEMENT_CRAM_MD5_MECHANISM_HANDLER
                        .equalsIgnoreCase( supportedMechanismValue )
                        && ServerXmlIOV157.SUPPORTED_MECHANISM_CRAM_MD5.equalsIgnoreCase( mechNameValue ) )
                    {
                        serverConfiguration.addSupportedMechanism( SupportedMechanismEnum.CRAM_MD5 );
                    }
                    else if ( ServerXmlIOV157.ELEMENT_DIGEST_MD5_MECHANISM_HANDLER
                        .equalsIgnoreCase( supportedMechanismValue )
                        && ServerXmlIOV157.SUPPORTED_MECHANISM_DIGEST_MD5.equalsIgnoreCase( mechNameValue ) )
                    {
                        serverConfiguration.addSupportedMechanism( SupportedMechanismEnum.DIGEST_MD5 );
                    }
                    else if ( ServerXmlIOV157.ELEMENT_GSSAPI_MECHANISM_HANDLER
                        .equalsIgnoreCase( supportedMechanismValue )
                        && ServerXmlIOV157.SUPPORTED_MECHANISM_GSSAPI.equalsIgnoreCase( mechNameValue ) )
                    {
                        serverConfiguration.addSupportedMechanism( SupportedMechanismEnum.GSSAPI );
                    }
                    else if ( ServerXmlIOV157.ELEMENT_NTLM_MECHANISM_HANDLER.equalsIgnoreCase( supportedMechanismValue )
                        && ServerXmlIOV157.SUPPORTED_MECHANISM_NTLM.equalsIgnoreCase( mechNameValue ) )
                    {
                        org.dom4j.Attribute ntlmProviderFcqnAttribute = supportedMechanismValueElement
                            .attribute( ServerXmlIOV157.ATTRIBUTE_NTLM_PROVIDER_FQCN );
                        if ( ntlmProviderFcqnAttribute != null )
                        {
                            SupportedMechanismEnum ntlmSupportedMechanism = SupportedMechanismEnum.NTLM;
                            ntlmSupportedMechanism.setNtlmProviderFqcn( ntlmProviderFcqnAttribute.getValue() );
                            serverConfiguration.addSupportedMechanism( ntlmSupportedMechanism );
                        }
                        else
                        {
                            serverConfiguration.addSupportedMechanism( SupportedMechanismEnum.NTLM );
                        }
                    }
                    else if ( ServerXmlIOV157.ELEMENT_NTLM_MECHANISM_HANDLER.equalsIgnoreCase( supportedMechanismValue )
                        && ServerXmlIOV157.SUPPORTED_MECHANISM_GSS_SPNEGO.equalsIgnoreCase( mechNameValue ) )
                    {
                        org.dom4j.Attribute ntlmProviderFcqnAttribute = supportedMechanismValueElement
                            .attribute( ServerXmlIOV157.ATTRIBUTE_NTLM_PROVIDER_FQCN );
                        if ( ntlmProviderFcqnAttribute != null )
                        {
                            SupportedMechanismEnum gssSpnegoSupportedMechanism = SupportedMechanismEnum.GSS_SPNEGO;
                            gssSpnegoSupportedMechanism.setNtlmProviderFqcn( ntlmProviderFcqnAttribute.getValue() );
                            serverConfiguration.addSupportedMechanism( gssSpnegoSupportedMechanism );
                        }
                        else
                        {
                            serverConfiguration.addSupportedMechanism( SupportedMechanismEnum.GSS_SPNEGO );
                        }
                    }
                }
            }

            // SaslRealms
            Element SaslRealmsElement = ldapServerElement.element( ServerXmlIOV157.ELEMENT_SASL_REALMS );
            if ( SaslRealmsElement != null )
            {
                // Looping on all 'value' elements
                for ( Iterator<?> iterator = SaslRealmsElement.elementIterator( ServerXmlIOV157.ELEMENT_VALUE ); iterator
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


    /**
     * Reads the extended operations.
     *
     * @param element
     *      the element
     * @param serverConfiguration
     *      the server configuration
     */
    private void readExtendedOperations( Element element, ServerConfigurationV157 serverConfiguration )
    {
        // Getting the 'interceptors
        Element interceptorsElement = element.element( ServerXmlIOV157.ELEMENT_EXTENDED_OPERATION_HANDLERS );
        if ( interceptorsElement != null )
        {
            // Looping on all interceptor elements
            for ( Iterator<?> i = interceptorsElement.elementIterator(); i.hasNext(); )
            {
                // Getting the element
                Element extendedOperationElement = ( Element ) i.next();

                // Checking which extended operation it is
                String extendedOperationElementName = extendedOperationElement.getName();
                if ( ServerXmlIOV157.ELEMENT_START_TLS_HANDLER.equalsIgnoreCase( extendedOperationElementName ) )
                {
                    serverConfiguration.addExtendedOperation( ExtendedOperationEnum.START_TLS );
                }
                if ( ServerXmlIOV157.ELEMENT_GRACEFUL_SHUTDOWN_HANDLER.equalsIgnoreCase( extendedOperationElementName ) )
                {
                    serverConfiguration.addExtendedOperation( ExtendedOperationEnum.GRACEFUL_SHUTDOWN );
                }
                if ( ServerXmlIOV157.ELEMENT_LAUNCH_DIAGNOSTIC_UI_HANDLER
                    .equalsIgnoreCase( extendedOperationElementName ) )
                {
                    serverConfiguration.addExtendedOperation( ExtendedOperationEnum.LAUNCH_DIAGNOSTIC_UI );
                }
            }

        }
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.configuration.model.ServerXmlIO#toXml(org.apache.directory.studio.apacheds.configuration.model.ServerConfiguration)
     */
    public String toXml( ServerConfiguration serverConfiguration ) throws IOException
    {
        // Creating the document
        Document document = DocumentHelper.createDocument();

        // Creating the root element with its namespaces definitions
        Element root = document.addElement( new QName( ServerXmlIOV157.ELEMENT_BEANS, NAMESPACE_XBEAN_SPRING ) );
        root.add( NAMESPACE_SPRINGFRAMEWORK );
        root.add( NAMESPACE_APACHEDS );

        // DefaultDirectoryService Bean
        createDefaultDirectoryServiceBean( root, ( ServerConfigurationV157 ) serverConfiguration );

        // ChangePasswordServer Bean
        createChangePasswordServerBean( root, ( ServerConfigurationV157 ) serverConfiguration );

        // KdcServer Bean
        createKdcServerBean( root, ( ServerConfigurationV157 ) serverConfiguration );

        // NtpServer Bean
        createNtpServerBean( root, ( ServerConfigurationV157 ) serverConfiguration );

        // DnsServer Bean
        createDnsServerBean( root, ( ServerConfigurationV157 ) serverConfiguration );

        // LdapServer Bean
        createLdapServerBean( root, ( ServerConfigurationV157 ) serverConfiguration );

        // ApacheDS Bean
        createApacheDSBean( root, ( ServerConfigurationV157 ) serverConfiguration );

        // Creating the output stream we're going to put the XML in
        OutputStream os = new ByteArrayOutputStream();
        OutputFormat outformat = OutputFormat.createPrettyPrint();
        outformat.setEncoding( "UTF-8" ); //$NON-NLS-1$

        // Writing the XML.
        XMLWriter writer = new XMLWriter( os, outformat );
        writer.write( document );
        writer.flush();
        writer.close();

        return os.toString();
    }


    /**
     * Creates the DefaultDirectoryService bean.
     *
     * @param root
     *      the root element
     * @param serverConfiguration
     *      the server configuration
     */
    private void createDefaultDirectoryServiceBean( Element root, ServerConfigurationV157 serverConfiguration )
    {
        // Adding the 'defaultDirectoryService' element
        Element defaultDirectoryServiceElement = root.addElement( ServerXmlIOV157.ELEMENT_DEFAULT_DIRECTORY_SERVICE );

        // Id
        defaultDirectoryServiceElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_ID,
            ServerXmlIOV157.VALUE_DIRECTORY_SERVICE );

        // InstanceId
        defaultDirectoryServiceElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_INSTANCE_ID,
            ServerXmlIOV157.VALUE_DEFAULT );

        // ReplicaId
        defaultDirectoryServiceElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_REPLICAID,
            ServerXmlIOV157.VALUE_REPLICAID );

        // WorkingDirectory
        defaultDirectoryServiceElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_WORKING_DIRECTORY,
            ServerXmlIOV157.VALUE_EXAMPLE_DOT_COM );

        // AllowAnonymousAccess
        defaultDirectoryServiceElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_ALLOW_ANONYMOUS_ACCESS, "" //$NON-NLS-1$
            + serverConfiguration.isAllowAnonymousAccess() );

        // AccessControlEnabled
        defaultDirectoryServiceElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_ACCESS_CONTROL_ENABLED, "" //$NON-NLS-1$
            + serverConfiguration.isEnableAccessControl() );

        // DenormalizeOpAttrsEnabled
        defaultDirectoryServiceElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_DENORMALIZE_OP_ATTRS_ENABLED, "" //$NON-NLS-1$
            + serverConfiguration.isDenormalizeOpAttr() );

        // SynchPeriodMillis
        defaultDirectoryServiceElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_SYNCH_PERIOD_MILLIS, "" //$NON-NLS-1$
            + serverConfiguration.getSynchronizationPeriod() );

        // MaxPDUSize
        defaultDirectoryServiceElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_MAXPDUSIZE,
            ServerXmlIOV157.VALUE_MAXPDUSIZE );

        // Adding the 'systemPartition' element
        Element systemPartitionElement = defaultDirectoryServiceElement
            .addElement( ServerXmlIOV157.ELEMENT_SYSTEM_PARTITION );

        // Adding System Partition Bean
        createSystemPartitionBean( systemPartitionElement, serverConfiguration );

        // Adding the 'partitions' element
        Element partitionsElement = defaultDirectoryServiceElement.addElement( ServerXmlIOV157.ELEMENT_PARTITIONS );

        // Adding User Partitions Beans
        createUserPartitions( partitionsElement, serverConfiguration );

        // Adding the 'interceptors' element
        Element interceptorsElement = defaultDirectoryServiceElement.addElement( ServerXmlIOV157.ELEMENT_INTERCEPTORS );

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
    private void createSystemPartitionBean( Element systemPartitionElement, ServerConfigurationV157 serverConfiguration )
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
    private void createUserPartitions( Element partitionsElement, ServerConfigurationV157 serverConfiguration )
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
        Element jdbmPartitionElement = element.addElement( ServerXmlIOV157.ELEMENT_JDBM_PARTITION );

        // Id
        jdbmPartitionElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_ID, partition.getId() );

        // CacheSize
        jdbmPartitionElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_CACHE_SIZE, "" + partition.getCacheSize() ); //$NON-NLS-1$

        // Suffix
        jdbmPartitionElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_SUFFIX, partition.getSuffix() );

        // OptimizerEnabled
        jdbmPartitionElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_OPTIMIZER_ENABLED, "" //$NON-NLS-1$
            + partition.isEnableOptimizer() );

        // SyncOnWrite
        jdbmPartitionElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_SYNC_ON_WRITE, "" //$NON-NLS-1$
            + partition.isSynchronizationOnWrite() );

        // IndexedAttributes
        createIndexedAttributes( jdbmPartitionElement, partition.getIndexedAttributes() );
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
        Element indexedAttributeElement = element.addElement( ServerXmlIOV157.ELEMENT_INDEXED_ATTRIBUTES );

        if ( indexedAttributes != null )
        {
            // Looping on indexed attributes
            for ( IndexedAttribute indexedAttribute : indexedAttributes )
            {
                // Adding the 'jdbmIndex' element
                Element jdbmIndexElement = indexedAttributeElement.addElement( ServerXmlIOV157.ELEMENT_JDBM_INDEX );
                jdbmIndexElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_ATTRIBUTE_ID,
                    indexedAttribute.getAttributeId() );
                jdbmIndexElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_CACHE_SIZE, "" //$NON-NLS-1$
                    + indexedAttribute.getCacheSize() );
            }
        }
    }


    /**
     * Creates the interceptor beans.
     *
     * @param interceptorsElement
     *      the interceptors element
     * @param serverConfiguration
     *      the server configuration
     */
    private void createInterceptors( Element interceptorsElement, ServerConfigurationV157 serverConfiguration )
    {
        List<InterceptorEnum> interceptors = serverConfiguration.getInterceptors();
        
        for ( InterceptorEnum interceptor : interceptors )
        {
            switch ( interceptor )
            {
                case NORMALIZATION:
                    interceptorsElement.addElement( ServerXmlIOV157.ELEMENT_NORMALIZATION_INTERCEPTOR );
                    break;
                case AUTHENTICATION:
                    interceptorsElement.addElement( ServerXmlIOV157.ELEMENT_AUTHENTICATION_INTERCEPTOR );
                    break;
                case REFERRAL:
                    interceptorsElement.addElement( ServerXmlIOV157.ELEMENT_REFERRAL_INTERCEPTOR );
                    break;
                case ACI_AUTHORIZATION:
                    interceptorsElement.addElement( ServerXmlIOV157.ELEMENT_ACI_AUTHORIZATION_INTERCEPTOR );
                    break;
                case DEFAULT_AUTHORIZATION:
                    interceptorsElement.addElement( ServerXmlIOV157.ELEMENT_DEFAULT_AUTHORIZATION_INTERCEPTOR );
                    break;
                case EXCEPTION:
                    interceptorsElement.addElement( ServerXmlIOV157.ELEMENT_EXCEPTION_INTERCEPTOR );
                    break;
                case OPERATIONAL_ATTRIBUTE:
                    interceptorsElement.addElement( ServerXmlIOV157.ELEMENT_OPERATIONAL_ATTRIBUTE_INTERCEPTOR );
                    break;
                case KEY_DERIVATION:
                    interceptorsElement.addElement( ServerXmlIOV157.ELEMENT_KEY_DERIVATION_INTERCEPTOR );
                    break;
                case SCHEMA:
                    interceptorsElement.addElement( ServerXmlIOV157.ELEMENT_SCHEMA_INTERCEPTOR );
                    break;
                case SUBENTRY:
                    interceptorsElement.addElement( ServerXmlIOV157.ELEMENT_SUBENTRY_INTERCEPTOR );
                    break;
                case COLLECTIVE_ATTRIBUTE:
                    interceptorsElement.addElement( ServerXmlIOV157.ELEMENT_COLLECTIVE_ATTRIBUTE_INTERCEPTOR );
                    break;
                case EVENT:
                    interceptorsElement.addElement( ServerXmlIOV157.ELEMENT_EVENT_INTERCEPTOR );
                    break;
                case TRIGGER:
                    interceptorsElement.addElement( ServerXmlIOV157.ELEMENT_TRIGGER_INTERCEPTOR );
                    break;
                case REPLICATION:
                    // TODO support replication interceptor
                    //            interceptorsElement.addElement( "replicationInterceptor" );
                    break;
            }
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
    private void createChangePasswordServerBean( Element root, ServerConfigurationV157 serverConfiguration )
    {
        if ( serverConfiguration.isEnableChangePassword() )
        {
            // Adding the 'changePasswordServer' element
            Element changePasswordServerElement = root.addElement( ServerXmlIOV157.ELEMENT_CHANGE_PASSWORD_SERVER );

            // Id
            changePasswordServerElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_ID,
                ServerXmlIOV157.ELEMENT_CHANGE_PASSWORD_SERVER );

            // Adding 'transports' element
            Element transportsElement = changePasswordServerElement.addElement( ServerXmlIOV157.ELEMENT_TRANSPORTS );

            // Adding 'tcpTransport' element
            Element tcpTransportElement = transportsElement.addElement( ServerXmlIOV157.ELEMENT_TCP_TRANSPORT );

            // Port
            tcpTransportElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_PORT, "" //$NON-NLS-1$
                + serverConfiguration.getChangePasswordPort() );

            // NbThreads
            tcpTransportElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_NBTHREADS,
                ServerXmlIOV157.VALUE_CHANGEPASSWORDSERVER_NB_THREADS );

            // BackLog
            tcpTransportElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_BACKLOG, ServerXmlIOV157.VALUE_BACKLOG );

            // Adding 'udpTransport' element
            Element udpTransportElement = transportsElement.addElement( ServerXmlIOV157.ELEMENT_UDP_TRANSPORT );

            // Port
            udpTransportElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_PORT, "" //$NON-NLS-1$
                + serverConfiguration.getChangePasswordPort() );

            // NbThreads
            udpTransportElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_NBTHREADS,
                ServerXmlIOV157.VALUE_CHANGEPASSWORDSERVER_NB_THREADS );

            // BackLog
            udpTransportElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_BACKLOG, ServerXmlIOV157.VALUE_BACKLOG );

            // Adding 'directoryService' element
            changePasswordServerElement.addElement( ServerXmlIOV157.VALUE_DIRECTORY_SERVICE ).setText(
                "#directoryService" ); //$NON-NLS-1$
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
    private void createKdcServerBean( Element root, ServerConfigurationV157 serverConfiguration )
    {
        if ( serverConfiguration.isEnableKerberos() )
        {
            // Adding the 'kdcServer' element
            Element kdcServerElement = root.addElement( ServerXmlIOV157.ELEMENT_KDC_SERVER );

            // Id
            kdcServerElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_ID, ServerXmlIOV157.ELEMENT_KDC_SERVER );

            // Adding 'transports' element
            Element transportsElement = kdcServerElement.addElement( ServerXmlIOV157.ELEMENT_TRANSPORTS );

            // Adding 'tcpTransport' element
            Element tcpTransportElement = transportsElement.addElement( ServerXmlIOV157.ELEMENT_TCP_TRANSPORT );

            // Port
            tcpTransportElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_PORT, "" //$NON-NLS-1$
                + serverConfiguration.getKerberosPort() );

            // NbThreads
            tcpTransportElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_NBTHREADS,
                ServerXmlIOV157.VALUE_KDCSERVER_NB_THREADS );

            // BackLog
            tcpTransportElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_BACKLOG, ServerXmlIOV157.VALUE_BACKLOG );

            // Adding 'udpTransport' element
            Element udpTransportElement = transportsElement.addElement( ServerXmlIOV157.ELEMENT_UDP_TRANSPORT );

            // Port
            udpTransportElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_PORT, "" //$NON-NLS-1$
                + serverConfiguration.getKerberosPort() );

            // NbThreads
            udpTransportElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_NBTHREADS,
                ServerXmlIOV157.VALUE_CHANGEPASSWORDSERVER_NB_THREADS );

            // BackLog
            udpTransportElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_BACKLOG, ServerXmlIOV157.VALUE_BACKLOG );

            // Adding 'directoryService' element
            kdcServerElement.addElement( ServerXmlIOV157.VALUE_DIRECTORY_SERVICE ).setText( "#directoryService" ); //$NON-NLS-1$
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
    private void createNtpServerBean( Element root, ServerConfigurationV157 serverConfiguration )
    {
        if ( serverConfiguration.isEnableNtp() )
        {
            // Adding the 'ntpServer' element
            Element ntpServerElement = root.addElement( ServerXmlIOV157.ELEMENT_NTP_SERVER );

            // Id
            ntpServerElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_ID, ServerXmlIOV157.ELEMENT_NTP_SERVER );

            // Adding 'transports' element
            Element transportsElement = ntpServerElement.addElement( ServerXmlIOV157.ELEMENT_TRANSPORTS );

            // Adding 'tcpTransport' element
            Element tcpTransportElement = transportsElement.addElement( ServerXmlIOV157.ELEMENT_TCP_TRANSPORT );

            // Port
            tcpTransportElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_PORT, "" //$NON-NLS-1$
                + serverConfiguration.getNtpPort() );

            // Adding 'udpTransport' element
            Element udpTransportElement = transportsElement.addElement( ServerXmlIOV157.ELEMENT_UDP_TRANSPORT );

            // Port
            udpTransportElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_PORT, "" //$NON-NLS-1$
                + serverConfiguration.getNtpPort() );

            // NbThreads
            udpTransportElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_NBTHREADS,
                ServerXmlIOV157.VALUE_NTPSERVER_NB_THREADS );
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
    private void createDnsServerBean( Element root, ServerConfigurationV157 serverConfiguration )
    {
        if ( serverConfiguration.isEnableDns() )
        {
            // Adding the 'dnsServer' element
            Element dnsServerElement = root.addElement( ServerXmlIOV157.ELEMENT_DNS_SERVER );

            // Id
            dnsServerElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_ID, ServerXmlIOV157.ELEMENT_DNS_SERVER );

            // Adding 'transports' element
            Element transportsElement = dnsServerElement.addElement( ServerXmlIOV157.ELEMENT_TRANSPORTS );

            // Adding 'tcpTransport' element
            Element tcpTransportElement = transportsElement.addElement( ServerXmlIOV157.ELEMENT_TCP_TRANSPORT );

            // Port
            tcpTransportElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_PORT, "" //$NON-NLS-1$
                + serverConfiguration.getDnsPort() );

            // Adding 'udpTransport' element
            Element udpTransportElement = transportsElement.addElement( ServerXmlIOV157.ELEMENT_UDP_TRANSPORT );

            // Port
            udpTransportElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_PORT, "" //$NON-NLS-1$
                + serverConfiguration.getDnsPort() );

            // Adding 'directoryService' element
            dnsServerElement.addElement( ServerXmlIOV157.VALUE_DIRECTORY_SERVICE ).setText( "#directoryService" ); //$NON-NLS-1$
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
    private void createLdapServerBean( Element root, ServerConfigurationV157 serverConfiguration )
    {
        if ( serverConfiguration.isEnableLdap() || serverConfiguration.isEnableLdaps() )
        {
            // Adding the 'ldapServer' element
            Element ldapServerElement = root.addElement( ServerXmlIOV157.ELEMENT_LDAP_SERVER );

            // Id
            ldapServerElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_ID, ServerXmlIOV157.ELEMENT_LDAP_SERVER );

            // AllowAnonymousAccess
            ldapServerElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_ALLOW_ANONYMOUS_ACCESS, "" //$NON-NLS-1$
                + serverConfiguration.isAllowAnonymousAccess() );

            // SaslHost
            ldapServerElement
                .addAttribute( ServerXmlIOV157.ATTRIBUTE_SASL_HOST, "" + serverConfiguration.getSaslHost() ); //$NON-NLS-1$

            // SaslPrincipal
            ldapServerElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_SASL_PRINCIPAL, "" //$NON-NLS-1$
                + serverConfiguration.getSaslPrincipal() );

            // SearchBaseDn
            ldapServerElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_SEARCH_BASE_DN, "ou=users,ou=system" ); //$NON-NLS-1$

            // MaxTimeLimit
            ldapServerElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_MAX_TIME_LIMIT, "" //$NON-NLS-1$
                + serverConfiguration.getMaxTimeLimit() );

            // MaxSizeLimit
            ldapServerElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_MAX_SIZE_LIMIT, "" //$NON-NLS-1$
                + serverConfiguration.getMaxSizeLimit() );

            // Adding 'transports' element
            Element transportsElement = ldapServerElement.addElement( ServerXmlIOV157.ELEMENT_TRANSPORTS );

            // LDAP
            if ( serverConfiguration.isEnableLdap() )
            {
                // Adding 'tcpTransport' element
                Element tcpTransportElement = transportsElement.addElement( ServerXmlIOV157.ELEMENT_TCP_TRANSPORT );

                // Address
                tcpTransportElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_ADDRESS,
                    ServerXmlIOV157.VALUE_ADDRESS_0_0_0_0 );

                // Port
                tcpTransportElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_PORT, "" //$NON-NLS-1$
                    + serverConfiguration.getLdapPort() );

                // NbThreads
                tcpTransportElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_NBTHREADS,
                    ServerXmlIOV157.VALUE_LDAPSERVER_NB_THREADS );

                // BackLog
                tcpTransportElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_BACKLOG, ServerXmlIOV157.VALUE_BACKLOG );

                // EnableSSL
                tcpTransportElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_ENABLESSL, "" + false ); //$NON-NLS-1$
            }

            // LDAPS
            if ( serverConfiguration.isEnableLdaps() )
            {
                // Adding 'tcpTransport' element
                Element tcpTransportElement = transportsElement.addElement( ServerXmlIOV157.ELEMENT_TCP_TRANSPORT );

                // Address
                tcpTransportElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_ADDRESS,
                    ServerXmlIOV157.VALUE_ADDRESS_LOCALHOST );

                // Port
                tcpTransportElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_PORT, "" //$NON-NLS-1$
                    + serverConfiguration.getLdapsPort() );

                // EnableSSL
                tcpTransportElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_ENABLESSL, "" + true ); //$NON-NLS-1$
            }

            // Adding 'directoryService' element
            ldapServerElement.addElement( ServerXmlIOV157.VALUE_DIRECTORY_SERVICE ).setText( "#directoryService" ); //$NON-NLS-1$

            // Adding 'saslMechanismHandlers' element
            Element saslMechanismHandlersElement = ldapServerElement
                .addElement( ServerXmlIOV157.ELEMENT_SASL_MECHANISM_HANDLERS );

            // Adding each supported mechanism
            for ( SupportedMechanismEnum supportedMechanism : serverConfiguration.getSupportedMechanisms() )
            {
                switch ( supportedMechanism )
                {
                    case SIMPLE:
                        Element simpleMechanismHandlerElement = saslMechanismHandlersElement
                            .addElement( ServerXmlIOV157.ELEMENT_SIMPLE_MECHANISM_HANDLER );
                        simpleMechanismHandlerElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_MECH_NAME,
                            ServerXmlIOV157.SUPPORTED_MECHANISM_SIMPLE );
                        break;
                    case CRAM_MD5:
                        Element cramMd5MechanismHandlerElement = saslMechanismHandlersElement
                            .addElement( ServerXmlIOV157.ELEMENT_CRAM_MD5_MECHANISM_HANDLER );
                        cramMd5MechanismHandlerElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_MECH_NAME,
                            ServerXmlIOV157.SUPPORTED_MECHANISM_CRAM_MD5 );
                        break;
                    case DIGEST_MD5:
                        Element digestMd5MechanismHandlerElement = saslMechanismHandlersElement
                            .addElement( ServerXmlIOV157.ELEMENT_DIGEST_MD5_MECHANISM_HANDLER );
                        digestMd5MechanismHandlerElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_MECH_NAME,
                            ServerXmlIOV157.SUPPORTED_MECHANISM_DIGEST_MD5 );
                        break;
                    case GSSAPI:
                        Element gssapiMechanismHandlerElement = saslMechanismHandlersElement
                            .addElement( ServerXmlIOV157.ELEMENT_GSSAPI_MECHANISM_HANDLER );
                        gssapiMechanismHandlerElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_MECH_NAME,
                            ServerXmlIOV157.SUPPORTED_MECHANISM_GSSAPI );
                        break;
                    case NTLM:
                        Element ntlmMechanismHandlerElement = saslMechanismHandlersElement
                            .addElement( ServerXmlIOV157.ELEMENT_NTLM_MECHANISM_HANDLER );
                        ntlmMechanismHandlerElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_MECH_NAME,
                            ServerXmlIOV157.SUPPORTED_MECHANISM_NTLM );
                        ntlmMechanismHandlerElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_NTLM_PROVIDER_FQCN,
                            supportedMechanism.getNtlmProviderFqcn() );
                        break;
                    case GSS_SPNEGO:
                        Element gssSpnegoMechanismHandlerElement = saslMechanismHandlersElement
                            .addElement( ServerXmlIOV157.ELEMENT_NTLM_MECHANISM_HANDLER );
                        gssSpnegoMechanismHandlerElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_MECH_NAME,
                            ServerXmlIOV157.SUPPORTED_MECHANISM_GSS_SPNEGO );
                        gssSpnegoMechanismHandlerElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_NTLM_PROVIDER_FQCN,
                            supportedMechanism.getNtlmProviderFqcn() );
                        break;
                }
            }

            // Adding 'SaslRealms' element
            Element saslRealmsElement = ldapServerElement.addElement( ServerXmlIOV157.ELEMENT_SASL_REALMS );

            // Adding each SaslRealm item
            for ( String saslRealm : serverConfiguration.getSaslRealms() )
            {
                saslRealmsElement.addElement( new QName( ServerXmlIOV157.ELEMENT_VALUE, NAMESPACE_SPRINGFRAMEWORK ) )
                    .setText( saslRealm );
            }

            // Adding 'ExtendedOperations' element
            Element extendedOperationsElement = ldapServerElement
                .addElement( ServerXmlIOV157.ELEMENT_EXTENDED_OPERATION_HANDLERS );

            // Adding each extended operation item
            List<ExtendedOperationEnum> extendedOperations = serverConfiguration.getExtendedOperations();
            if ( extendedOperations.contains( ExtendedOperationEnum.START_TLS ) )
            {
                extendedOperationsElement.addElement( ServerXmlIOV157.ELEMENT_START_TLS_HANDLER );
            }
            if ( extendedOperations.contains( ExtendedOperationEnum.GRACEFUL_SHUTDOWN ) )
            {
                extendedOperationsElement.addElement( ServerXmlIOV157.ELEMENT_GRACEFUL_SHUTDOWN_HANDLER );
            }
            if ( extendedOperations.contains( ExtendedOperationEnum.LAUNCH_DIAGNOSTIC_UI ) )
            {
                extendedOperationsElement.addElement( ServerXmlIOV157.ELEMENT_LAUNCH_DIAGNOSTIC_UI_HANDLER );
            }
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
    private void createApacheDSBean( Element root, ServerConfigurationV157 serverConfiguration )
    {
        // Adding the 'apacheDS' element
        Element apacheDSElement = root.addElement( ServerXmlIOV157.ELEMENT_APACHE_DS );

        // Id
        apacheDSElement.addAttribute( ServerXmlIOV157.ATTRIBUTE_ID, ServerXmlIOV157.ELEMENT_APACHE_DS );

        // Adding 'ldapService' element
        apacheDSElement.addElement( ServerXmlIOV157.ELEMENT_LDAP_SERVER ).setText( "#ldapServer" ); //$NON-NLS-1$
    }
}
