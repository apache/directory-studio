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
package org.apache.directory.studio.apacheds.configuration.model.v154;


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
 * Apache Directory Server version 1.5.4.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ServerXmlIOV154 extends AbstractServerXmlIO implements ServerXmlIO
{
    private static final String ATTRIBUTE_ACCESS_CONTROL_ENABLED = "accessControlEnabled"; //$NON-NLS-1$
    private static final String ATTRIBUTE_ALLOW_ANONYMOUS_ACCESS = "allowAnonymousAccess"; //$NON-NLS-1$
    private static final String ATTRIBUTE_ATTRIBUTE_ID = "attributeId"; //$NON-NLS-1$
    private static final String ATTRIBUTE_CACHE_SIZE = "cacheSize"; //$NON-NLS-1$
    private static final String ATTRIBUTE_CLASS = "class"; //$NON-NLS-1$
    private static final String ATTRIBUTE_DENORMALIZE_OP_ATTRS_ENABLED = "denormalizeOpAttrsEnabled"; //$NON-NLS-1$
    private static final String ATTRIBUTE_ENABLE_LDAPS = "enableLdaps"; //$NON-NLS-1$
    private static final String ATTRIBUTE_ENABLED = "enabled"; //$NON-NLS-1$
    private static final String ATTRIBUTE_ID = "id"; //$NON-NLS-1$
    private static final String ATTRIBUTE_INSTANCE_ID = "instanceId"; //$NON-NLS-1$
    private static final String ATTRIBUTE_IP_PORT = "ipPort"; //$NON-NLS-1$
    private static final String ATTRIBUTE_KEY = "key"; //$NON-NLS-1$
    private static final String ATTRIBUTE_MAX_SIZE_LIMIT = "maxSizeLimit"; //$NON-NLS-1$
    private static final String ATTRIBUTE_MAX_THREADS = "maxThreads"; //$NON-NLS-1$
    private static final String ATTRIBUTE_MAX_TIME_LIMIT = "maxTimeLimit"; //$NON-NLS-1$
    private static final String ATTRIBUTE_MECH_NAME = "mech-name"; //$NON-NLS-1$
    private static final String ATTRIBUTE_NAME = "name"; //$NON-NLS-1$
    private static final String ATTRIBUTE_NTLM_PROVIDER_FQCN = "ntlmProviderFqcn"; //$NON-NLS-1$
    private static final String ATTRIBUTE_OPTIMIZER_ENABLED = "optimizerEnabled"; //$NON-NLS-1$
    private static final String ATTRIBUTE_SASL_HOST = "saslHost"; //$NON-NLS-1$
    private static final String ATTRIBUTE_SASL_PRINCIPAL = "saslPrincipal"; //$NON-NLS-1$
    private static final String ATTRIBUTE_SEARCH_BASE_DN = "searchBaseDn"; //$NON-NLS-1$
    private static final String ATTRIBUTE_SUFFIX = "suffix"; //$NON-NLS-1$
    private static final String ATTRIBUTE_SYNC_ON_WRITE = "syncOnWrite"; //$NON-NLS-1$
    private static final String ATTRIBUTE_SYNCH_PERIOD_MILLIS = "synchPeriodMillis"; //$NON-NLS-1$
    private static final String ATTRIBUTE_WORKING_DIRECTORY = "workingDirectory"; //$NON-NLS-1$
    private static final String ELEMENT_ACI_AUTHORIZATION_INTERCEPTOR = "aciAuthorizationInterceptor"; //$NON-NLS-1$
    private static final String ELEMENT_APACHE_DS = "apacheDS"; //$NON-NLS-1$
    private static final String ELEMENT_AUTHENTICATION_INTERCEPTOR = "authenticationInterceptor"; //$NON-NLS-1$
    private static final String ELEMENT_BEAN = "bean"; //$NON-NLS-1$
    private static final String ELEMENT_BEANS = "beans"; //$NON-NLS-1$
    private static final String ELEMENT_CHANGE_PASSWORD_SERVER = "changePasswordServer"; //$NON-NLS-1$
    private static final String ELEMENT_COLLECTIVE_ATTRIBUTE_INTERCEPTOR = "collectiveAttributeInterceptor"; //$NON-NLS-1$
    private static final String ELEMENT_CRAM_MD5_MECHANISM_HANDLER = "cramMd5MechanismHandler"; //$NON-NLS-1$
    private static final String ELEMENT_DATAGRAM_ACCEPTOR = "datagramAcceptor"; //$NON-NLS-1$
    private static final String ELEMENT_DEFAULT_AUTHORIZATION_INTERCEPTOR = "defaultAuthorizationInterceptor"; //$NON-NLS-1$
    private static final String ELEMENT_DEFAULT_DIRECTORY_SERVICE = "defaultDirectoryService"; //$NON-NLS-1$
    private static final String ELEMENT_DIGEST_MD5_MECHANISM_HANDLER = "digestMd5MechanismHandler"; //$NON-NLS-1$
    private static final String ELEMENT_DNS_SERVER = "dnsServer"; //$NON-NLS-1$
    private static final String ELEMENT_ENTRY = "entry"; //$NON-NLS-1$
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
    private static final String ELEMENT_LAUNCH_DIAGNOSTIC_UI_HANDLER = "launchDiagnosticUiHandler"; //$NON-NLS-1$
    private static final String ELEMENT_LDAP_SERVICE = "ldapService"; //$NON-NLS-1$
    private static final String ELEMENT_LDAPS_SERVICE = "ldapsService"; //$NON-NLS-1$
    private static final String ELEMENT_LOGIC_EXECUTOR = "logicExecutor"; //$NON-NLS-1$
    private static final String ELEMENT_MAP = "map"; //$NON-NLS-1$
    private static final String ELEMENT_NORMALIZATION_INTERCEPTOR = "normalizationInterceptor"; //$NON-NLS-1$
    private static final String ELEMENT_NTLM_MECHANISM_HANDLER = "ntlmMechanismHandler"; //$NON-NLS-1$
    private static final String ELEMENT_NTP_SERVER = "ntpServer"; //$NON-NLS-1$
    private static final String ELEMENT_OPERATIONAL_ATTRIBUTE_INTERCEPTOR = "operationalAttributeInterceptor"; //$NON-NLS-1$
    private static final String ELEMENT_PARTITIONS = "partitions"; //$NON-NLS-1$
    private static final String ELEMENT_PROPERTY = "property"; //$NON-NLS-1$
    private static final String ELEMENT_REPLICATION_INTERCEPTOR = "replicationInterceptor"; //$NON-NLS-1$
    private static final String ELEMENT_SASL_MECHANISM_HANDLERS = "saslMechanismHandlers"; //$NON-NLS-1$
    private static final String ELEMENT_SASL_QOP = "saslQop"; //$NON-NLS-1$
    private static final String ELEMENT_SASL_REALMS = "saslRealms"; //$NON-NLS-1$
    private static final String ELEMENT_SCHEMA_INTERCEPTOR = "schemaInterceptor"; //$NON-NLS-1$
    private static final String ELEMENT_SIMPLE_MECHANISM_HANDLER = "simpleMechanismHandler"; //$NON-NLS-1$
    private static final String ELEMENT_SOCKET_ACCEPTOR = "socketAcceptor"; //$NON-NLS-1$
    private static final String ELEMENT_STANDARD_THREAD_POOL = "standardThreadPool"; //$NON-NLS-1$
    private static final String ELEMENT_START_TLS_HANDLER = "startTlsHandler"; //$NON-NLS-1$
    private static final String ELEMENT_SUBENTRY_INTERCEPTOR = "subentryInterceptor"; //$NON-NLS-1$
    private static final String ELEMENT_SYSTEM_PARTITION = "systemPartition"; //$NON-NLS-1$
    private static final String ELEMENT_TRIGGER_INTERCEPTOR = "triggerInterceptor"; //$NON-NLS-1$
    private static final String ELEMENT_VALUE = "value"; //$NON-NLS-1$
    private static final Namespace NAMESPACE_APACHEDS = new Namespace( null, "http://apacheds.org/config/1.0" ); //$NON-NLS-1$
    private static final Namespace NAMESPACE_SPRINGFRAMEWORK = new Namespace( "s", //$NON-NLS-1$
        "http://www.springframework.org/schema/beans" ); //$NON-NLS-1$
    private static final Namespace NAMESPACE_XBEAN_SPRING = new Namespace( "spring", //$NON-NLS-1$
        "http://xbean.apache.org/schemas/spring/1.0" ); //$NON-NLS-1$
    private static final String SASL_QOP_AUTH = "auth"; //$NON-NLS-1$
    private static final String SASL_QOP_AUTH_CONF = "auth-conf"; //$NON-NLS-1$
    private static final String SASL_QOP_AUTH_INT = "auth-int"; //$NON-NLS-1$
    private static final String SUPPORTED_MECHANISM_CRAM_MD5 = "CRAM-MD5"; //$NON-NLS-1$
    private static final String SUPPORTED_MECHANISM_DIGEST_MD5 = "DIGEST-MD5"; //$NON-NLS-1$
    private static final String SUPPORTED_MECHANISM_GSS_SPNEGO = "GSS-SPNEGO"; //$NON-NLS-1$
    private static final String SUPPORTED_MECHANISM_GSSAPI = "GSSAPI"; //$NON-NLS-1$
    private static final String SUPPORTED_MECHANISM_NTLM = "NTLM"; //$NON-NLS-1$
    private static final String SUPPORTED_MECHANISM_SIMPLE = "SIMPLE"; //$NON-NLS-1$
    private static final String VALUE_CUSTOM_EDITORS = "customEditors"; //$NON-NLS-1$
    private static final String VALUE_DEFAULT = "default"; //$NON-NLS-1$
    private static final String VALUE_DIRECTORY_SERVICE = "directoryService"; //$NON-NLS-1$
    private static final String VALUE_EXAMPLE_DOT_COM = "example.com"; //$NON-NLS-1$


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
            if ( ServerXmlIOV154.ELEMENT_BEANS.equalsIgnoreCase( rootElement.getName() ) )
            {
                // Looking for the 'apacheDS' element
                Element apacheDSElement = rootElement.element( ServerXmlIOV154.ELEMENT_APACHE_DS );
                if ( apacheDSElement != null )
                {
                    // Looking for the 'apacheDS' element
                    Element ldapServiceElement = rootElement.element( ServerXmlIOV154.ELEMENT_LDAP_SERVICE );

                    return ( ldapServiceElement != null );
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
            // Reading and creating the document
            SAXReader reader = new SAXReader();
            reader.setEntityResolver( new StudioEntityResolver() );
            Document document = reader.read( is );

            // Parsing the document
            ServerConfigurationV154 serverConfiguration = new ServerConfigurationV154();
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
    private void parse( Document document, ServerConfigurationV154 serverConfiguration ) throws NumberFormatException,
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

        // Reading the 'ldapsService' Bean
        readLdapsServiceBean( rootElement, serverConfiguration );

        // Reading the 'LdapService' Bean
        readLdapServiceBean( rootElement, serverConfiguration );

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
    private void readDefaultDirectoryServiceBean( Element element, ServerConfigurationV154 serverConfiguration )
        throws ServerXmlIOException, NumberFormatException, BooleanFormatException
    {
        Element defaultDirectoryServiceElement = element.element( ELEMENT_DEFAULT_DIRECTORY_SERVICE );
        if ( defaultDirectoryServiceElement == null )
        {
            throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV154.ErrorDefaultDirectoryService" ) ); //$NON-NLS-1$
        }
        else
        {
            // Access Control Enabled
            org.dom4j.Attribute accessControlEnabledAttribute = defaultDirectoryServiceElement
                .attribute( ServerXmlIOV154.ATTRIBUTE_ACCESS_CONTROL_ENABLED );
            if ( accessControlEnabledAttribute == null )
            {
                // If the 'accessControlEnabled' attribute does not exists,
                // we throw an exception
                throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV154.ErrorAccessControlEnabled" ) ); //$NON-NLS-1$
            }
            else
            {
                serverConfiguration.setEnableAccessControl( parseBoolean( accessControlEnabledAttribute.getValue() ) );
            }

            // Denormalize Op Attrs Enabled
            org.dom4j.Attribute denormalizeOpAttrsEnabledAttribute = defaultDirectoryServiceElement
                .attribute( ServerXmlIOV154.ATTRIBUTE_DENORMALIZE_OP_ATTRS_ENABLED );
            if ( denormalizeOpAttrsEnabledAttribute == null )
            {
                // If the 'denormalizeOpAttrsEnabled' attribute does not exists,
                // we throw an exception
                throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV154.ErrorDenormalizeOpAttrsEnabled" ) ); //$NON-NLS-1$
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
    private void readStandardThreadPoolBean( Element element, ServerConfigurationV154 serverConfiguration )
        throws ServerXmlIOException, NumberFormatException
    {
        Element standardThreadPoolElement = element.element( ServerXmlIOV154.ELEMENT_STANDARD_THREAD_POOL );
        if ( standardThreadPoolElement == null )
        {
            throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV154.ErrorStandardThreadPool" ) ); //$NON-NLS-1$
        }
        else
        {
            // MaxThreads
            org.dom4j.Attribute maxThreadsAttribute = standardThreadPoolElement
                .attribute( ServerXmlIOV154.ATTRIBUTE_MAX_THREADS );
            if ( maxThreadsAttribute == null )
            {
                // If the 'maxThreads' attribute does not exists,
                // we throw an exception
                throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV154.ErrorMaxThreads" ) ); //$NON-NLS-1$
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
    private void readSystemPartition( Element element, ServerConfigurationV154 serverConfiguration )
        throws ServerXmlIOException, NumberFormatException, BooleanFormatException
    {
        // Getting the 'systemPartition' element
        Element systemPartitionElement = element.element( ServerXmlIOV154.ELEMENT_SYSTEM_PARTITION );
        if ( systemPartitionElement == null )
        {
            // If the 'systemPartition' element does not exists,
            // we throw an exception
            throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV154.ErrorSystemPartition" ) ); //$NON-NLS-1$
        }
        else
        {
            // Getting the 'jdbmPartition' element
            Element jdbmPartitionElement = systemPartitionElement.element( ServerXmlIOV154.ELEMENT_JDBM_PARTITION );
            if ( jdbmPartitionElement == null )
            {
                // If the 'jdbmPartition' element does not exists,
                // we throw an exception
                throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV154.ErrorJDBMPartition" ) ); //$NON-NLS-1$
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
        org.dom4j.Attribute idAttribute = element.attribute( ServerXmlIOV154.ATTRIBUTE_ID );
        if ( idAttribute == null )
        {
            // If the 'id' attribute does not exists,
            // we throw an exception
            throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV154.ErrorId" ) ); //$NON-NLS-1$
        }
        else
        {
            partition.setId( idAttribute.getValue() );
        }

        // Cache Size
        org.dom4j.Attribute cacheSizeAttribute = element.attribute( ServerXmlIOV154.ATTRIBUTE_CACHE_SIZE );
        if ( cacheSizeAttribute == null )
        {
            // If the 'cacheSize' attribute does not exists,
            // we throw an exception
            throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV154.ErrorCacheSize" ) ); //$NON-NLS-1$
        }
        else
        {
            partition.setCacheSize( Integer.parseInt( cacheSizeAttribute.getValue() ) );
        }

        // Suffix
        org.dom4j.Attribute suffixAttribute = element.attribute( ServerXmlIOV154.ATTRIBUTE_SUFFIX );
        if ( suffixAttribute == null )
        {
            // If the 'suffix' attribute does not exists,
            // we throw an exception
            throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV154.ErrorSuffix" ) ); //$NON-NLS-1$
        }
        else
        {
            partition.setSuffix( suffixAttribute.getValue() );
        }

        // Optimizer Enabled
        org.dom4j.Attribute optimizerEnabledAttribute = element.attribute( ServerXmlIOV154.ATTRIBUTE_OPTIMIZER_ENABLED );
        if ( optimizerEnabledAttribute == null )
        {
            // If the 'optimizeEnabled' attribute does not exists,
            // we throw an exception
            throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV154.ErrorOptimizerEnabled" ) ); //$NON-NLS-1$
        }
        else
        {
            partition.setEnableOptimizer( parseBoolean( optimizerEnabledAttribute.getValue() ) );
        }

        // Sync On Write
        org.dom4j.Attribute syncOnWriteAttribute = element.attribute( ServerXmlIOV154.ATTRIBUTE_SYNC_ON_WRITE );
        if ( syncOnWriteAttribute == null )
        {
            // If the 'syncOnWrite' attribute does not exists,
            // we throw an exception
            throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV154.ErrorSyncOnWrite" ) ); //$NON-NLS-1$
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
        Element indexedAttributesElement = element.element( ServerXmlIOV154.ELEMENT_INDEXED_ATTRIBUTES );
        if ( indexedAttributesElement != null )
        {
            // Looping on 'jdbmIndex' elements
            for ( Iterator<?> i = indexedAttributesElement.elementIterator( ServerXmlIOV154.ELEMENT_JDBM_INDEX ); i
                .hasNext(); )
            {
                // Getting the 'jdbmIndex' element
                Element jdbmIndexElement = ( Element ) i.next();

                // Getting the 'attributeId' attribute
                org.dom4j.Attribute attributeIdAttribute = jdbmIndexElement
                    .attribute( ServerXmlIOV154.ATTRIBUTE_ATTRIBUTE_ID );
                if ( attributeIdAttribute != null )
                {
                    // Getting the 'cacheSize' attribute
                    org.dom4j.Attribute cacheSizeAttribute = jdbmIndexElement
                        .attribute( ServerXmlIOV154.ATTRIBUTE_CACHE_SIZE );
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
    private void readPartitions( Element element, ServerConfigurationV154 serverConfiguration )
        throws NumberFormatException, ServerXmlIOException, BooleanFormatException
    {
        // Getting the 'partitions'element
        Element partitionsElement = element.element( ServerXmlIOV154.ELEMENT_PARTITIONS );
        if ( partitionsElement != null )
        {
            // Looping on all 'jdbmPartition' tags
            for ( Iterator<?> i = partitionsElement.elementIterator( ServerXmlIOV154.ELEMENT_JDBM_PARTITION ); i
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
    private void readInterceptors( Element element, ServerConfigurationV154 serverConfiguration )
    {
        // Getting the 'interceptors
        Element interceptorsElement = element.element( ServerXmlIOV154.ELEMENT_INTERCEPTORS );
        if ( interceptorsElement != null )
        {
            // Looping on all interceptor elements
            for ( Iterator<?> i = interceptorsElement.elementIterator(); i.hasNext(); )
            {
                // Getting the element
                Element interceptorElement = ( Element ) i.next();

                // Checking which interceptor it is
                String interceptorElementName = interceptorElement.getName();
                if ( ServerXmlIOV154.ELEMENT_NORMALIZATION_INTERCEPTOR.equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.NORMALIZATION );
                }
                else if ( ServerXmlIOV154.ELEMENT_AUTHENTICATION_INTERCEPTOR.equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.AUTHENTICATION );
                }
                else if ( ServerXmlIOV154.ELEMENT_ACI_AUTHORIZATION_INTERCEPTOR
                    .equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.ACI_AUTHORIZATION );
                }
                else if ( ServerXmlIOV154.ELEMENT_DEFAULT_AUTHORIZATION_INTERCEPTOR
                    .equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.DEFAULT_AUTHORIZATION );
                }
                else if ( ServerXmlIOV154.ELEMENT_EXCEPTION_INTERCEPTOR.equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.EXCEPTION );
                }
                else if ( ServerXmlIOV154.ELEMENT_OPERATIONAL_ATTRIBUTE_INTERCEPTOR
                    .equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.OPERATIONAL_ATTRIBUTE );
                }
                else if ( ServerXmlIOV154.ELEMENT_SCHEMA_INTERCEPTOR.equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.SCHEMA );
                }
                else if ( ServerXmlIOV154.ELEMENT_SUBENTRY_INTERCEPTOR.equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.SUBENTRY );
                }
                else if ( ServerXmlIOV154.ELEMENT_COLLECTIVE_ATTRIBUTE_INTERCEPTOR
                    .equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.COLLECTIVE_ATTRIBUTE );
                }
                else if ( ServerXmlIOV154.ELEMENT_EVENT_INTERCEPTOR.equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.EVENT );
                }
                else if ( ServerXmlIOV154.ELEMENT_TRIGGER_INTERCEPTOR.equalsIgnoreCase( interceptorElementName ) )
                {
                    serverConfiguration.addInterceptor( InterceptorEnum.TRIGGER );
                }
                else if ( ServerXmlIOV154.ELEMENT_REPLICATION_INTERCEPTOR.equalsIgnoreCase( interceptorElementName ) )
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
    private void readChangePasswordServerBean( Element element, ServerConfigurationV154 serverConfiguration )
        throws NumberFormatException, ServerXmlIOException, BooleanFormatException
    {
        // Getting the 'changePasswordServer' element
        Element changePasswordServerElement = element.element( ServerXmlIOV154.ELEMENT_CHANGE_PASSWORD_SERVER );
        if ( changePasswordServerElement != null )
        {
            // Getting the 'enabled' attribute
            org.dom4j.Attribute enabledAttribute = changePasswordServerElement
                .attribute( ServerXmlIOV154.ATTRIBUTE_ENABLED );
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
                .attribute( ServerXmlIOV154.ATTRIBUTE_IP_PORT );
            if ( ipPortAttribute == null )
            {
                // If the 'ipPort' attribute does not exists,
                // we throw an exception
                throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV154.ErrorChangePasswordServerPort" ) ); //$NON-NLS-1$
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
    private void readKdcServerBean( Element element, ServerConfigurationV154 serverConfiguration )
        throws NumberFormatException, ServerXmlIOException, BooleanFormatException
    {
        // Getting the 'kdcServer' element
        Element kdcServerElement = element.element( ServerXmlIOV154.ELEMENT_KDC_SERVER );
        if ( kdcServerElement != null )
        {
            // Getting the 'enabled' attribute
            org.dom4j.Attribute enabledAttribute = kdcServerElement.attribute( ServerXmlIOV154.ATTRIBUTE_ENABLED );
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
            org.dom4j.Attribute ipPortAttribute = kdcServerElement.attribute( ServerXmlIOV154.ATTRIBUTE_IP_PORT );
            if ( ipPortAttribute == null )
            {
                // If the 'ipPort' attribute does not exists,
                // we throw an exception
                throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV154.ErrorKdcServerPort" ) ); //$NON-NLS-1$
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
    private void readNtpServerBean( Element element, ServerConfigurationV154 serverConfiguration )
        throws NumberFormatException, ServerXmlIOException, BooleanFormatException
    {
        // Getting the 'ntpServer' element
        Element ntpServerElement = element.element( ServerXmlIOV154.ELEMENT_NTP_SERVER );
        if ( ntpServerElement != null )
        {
            // Getting the 'enabled' attribute
            org.dom4j.Attribute enabledAttribute = ntpServerElement.attribute( ServerXmlIOV154.ATTRIBUTE_ENABLED );
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
            org.dom4j.Attribute ipPortAttribute = ntpServerElement.attribute( ServerXmlIOV154.ATTRIBUTE_IP_PORT );
            if ( ipPortAttribute == null )
            {
                // If the 'ipPort' attribute does not exists,
                // we throw an exception
                throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV154.ErrorNtpServerPort" ) ); //$NON-NLS-1$
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
    private void readDnsServerBean( Element element, ServerConfigurationV154 serverConfiguration )
        throws NumberFormatException, ServerXmlIOException, BooleanFormatException
    {
        // Getting the 'dnsServer' element
        Element dnsServerElement = element.element( ServerXmlIOV154.ELEMENT_DNS_SERVER );
        if ( dnsServerElement != null )
        {
            // Getting the 'enabled' attribute
            org.dom4j.Attribute enabledAttribute = dnsServerElement.attribute( ServerXmlIOV154.ATTRIBUTE_ENABLED );
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
            org.dom4j.Attribute ipPortAttribute = dnsServerElement.attribute( ServerXmlIOV154.ATTRIBUTE_IP_PORT );
            if ( ipPortAttribute == null )
            {
                // If the 'ipPort' attribute does not exists,
                // we throw an exception
                throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV154.ErrorDnsServerPort" ) ); //$NON-NLS-1$
            }
            else
            {
                serverConfiguration.setDnsPort( Integer.parseInt( ipPortAttribute.getValue() ) );
            }
        }
    }


    /**
     * Reads the LdapsService Bean.
     *
     * @param element
     *      the element
     * @param serverConfiguration
     *      the server configuration
     * @throws NumberFormatException
     * @throws ServerXmlIOException 
     * @throws BooleanFormatException 
     */
    private void readLdapsServiceBean( Element element, ServerConfigurationV154 serverConfiguration )
        throws NumberFormatException, ServerXmlIOException, BooleanFormatException
    {
        // Looping on all 'ldapService' elements
        for ( Iterator<?> i = element.getDocument().getRootElement().elementIterator(
            ServerXmlIOV154.ELEMENT_LDAP_SERVICE ); i.hasNext(); )
        {
            // Getting the 'ldapService' element
            Element ldapServiceElement = ( Element ) i.next();

            // Getting the 'id' attribute
            org.dom4j.Attribute idAttribute = ldapServiceElement.attribute( ServerXmlIOV154.ATTRIBUTE_ID );
            if ( idAttribute == null )
            {
                // If the 'id' attribute does not exists,
                // we throw an exception
                throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV154.ErrorLdapServerId" ) ); //$NON-NLS-1$
            }
            else
            {
                // Checking if the 'ldapService' element is the one for LDAPS
                if ( ServerXmlIOV154.ELEMENT_LDAPS_SERVICE.equalsIgnoreCase( idAttribute.getValue() ) )
                {
                    // Getting the 'enableLdaps' attribute
                    org.dom4j.Attribute enableLdapsAttribute = ldapServiceElement
                        .attribute( ServerXmlIOV154.ATTRIBUTE_ENABLE_LDAPS );
                    if ( enableLdapsAttribute == null )
                    {
                        // By default, the protocol is not enabled
                        serverConfiguration.setEnableLdaps( false );
                    }
                    else
                    {
                        // Getting the 'enabled' attribute
                        org.dom4j.Attribute enabledAttribute = ldapServiceElement
                            .attribute( ServerXmlIOV154.ATTRIBUTE_ENABLED );
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
                    org.dom4j.Attribute ipPortAttribute = ldapServiceElement
                        .attribute( ServerXmlIOV154.ATTRIBUTE_IP_PORT );
                    if ( ipPortAttribute == null )
                    {
                        // If the 'ipPort' attribute does not exists,
                        // we throw an exception
                        throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV154.ErrorLdapsServerPort" ) ); //$NON-NLS-1$
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
     * Reads the LdapService Bean.
     *
     * @param element
     *      the element
     * @param serverConfiguration
     *      the server configuration
     * @throws NumberFormatException
     * @throws ServerXmlIOException 
     * @throws BooleanFormatException 
     */
    private void readLdapServiceBean( Element element, ServerConfigurationV154 serverConfiguration )
        throws NumberFormatException, ServerXmlIOException, BooleanFormatException
    {
        // Looping on all 'ldapServeice' elements
        for ( Iterator<?> i = element.getDocument().getRootElement().elementIterator(
            ServerXmlIOV154.ELEMENT_LDAP_SERVICE ); i.hasNext(); )
        {
            // Getting the 'ldapService' element
            Element ldapServiceElement = ( Element ) i.next();

            // Getting the 'id' attribute
            org.dom4j.Attribute idAttribute = ldapServiceElement.attribute( ServerXmlIOV154.ATTRIBUTE_ID );
            if ( idAttribute == null )
            {
                // If the 'id' attribute does not exists,
                // we throw an exception
                throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV154.ErrorLdapServerId" ) ); //$NON-NLS-1$
            }
            else
            {
                // Checking if the 'ldapService' element is the one for LDAP
                if ( ServerXmlIOV154.ELEMENT_LDAP_SERVICE.equalsIgnoreCase( idAttribute.getValue() ) )
                {
                    // Getting the 'enabled' attribute
                    org.dom4j.Attribute enabledAttribute = ldapServiceElement
                        .attribute( ServerXmlIOV154.ATTRIBUTE_ENABLED );
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
                    org.dom4j.Attribute ipPortAttribute = ldapServiceElement
                        .attribute( ServerXmlIOV154.ATTRIBUTE_IP_PORT );
                    if ( ipPortAttribute == null )
                    {
                        // If the 'ipPort' attribute does not exists,
                        // we throw an exception
                        throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV154.ErrorLdapServerPort" ) ); //$NON-NLS-1$
                    }
                    else
                    {
                        serverConfiguration.setLdapPort( Integer.parseInt( ipPortAttribute.getValue() ) );
                    }

                    // Allow Anonymous Access
                    org.dom4j.Attribute allowAnonymousAccessAttribute = ldapServiceElement
                        .attribute( ServerXmlIOV154.ATTRIBUTE_ALLOW_ANONYMOUS_ACCESS );
                    if ( allowAnonymousAccessAttribute == null )
                    {
                        // If the 'allowAnonymousAccess' attribute does not exists,
                        // we throw an exception
                        throw new ServerXmlIOException( Messages
                            .getString( "ServerXmlIOV154.ErrorAllowAnonymousAccess" ) ); //$NON-NLS-1$
                    }
                    else
                    {
                        serverConfiguration.setAllowAnonymousAccess( parseBoolean( allowAnonymousAccessAttribute
                            .getValue() ) );
                    }

                    // SaslHost
                    org.dom4j.Attribute saslHostAttribute = ldapServiceElement
                        .attribute( ServerXmlIOV154.ATTRIBUTE_SASL_HOST );
                    if ( saslHostAttribute == null )
                    {
                        // If the 'saslHost' attribute does not exists,
                        // we throw an exception
                        throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV154.ErrorSaslHost" ) ); //$NON-NLS-1$
                    }
                    else
                    {
                        serverConfiguration.setSaslHost( saslHostAttribute.getValue() );
                    }

                    // SaslPrincipal
                    org.dom4j.Attribute saslPrincipalAttribute = ldapServiceElement
                        .attribute( ServerXmlIOV154.ATTRIBUTE_SASL_PRINCIPAL );
                    if ( saslPrincipalAttribute == null )
                    {
                        // If the 'saslPrincipal' attribute does not exists,
                        // we throw an exception
                        throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV154.ErrorSaslPrincipal" ) ); //$NON-NLS-1$
                    }
                    else
                    {
                        serverConfiguration.setSaslPrincipal( saslPrincipalAttribute.getValue() );
                    }

                    // SearchBaseDn
                    org.dom4j.Attribute searchBaseDnAttribute = ldapServiceElement
                        .attribute( ServerXmlIOV154.ATTRIBUTE_SEARCH_BASE_DN );
                    if ( searchBaseDnAttribute == null )
                    {
                        // If the 'searchBaseDn' attribute does not exists,
                        // we throw an exception
                        throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV154.ErrorSearchBaseDn" ) ); //$NON-NLS-1$
                    }
                    else
                    {
                        serverConfiguration.setSearchBaseDn( searchBaseDnAttribute.getValue() );
                    }

                    // MaxTimeLimit
                    org.dom4j.Attribute maxTimeLimitAttribute = ldapServiceElement
                        .attribute( ServerXmlIOV154.ATTRIBUTE_MAX_TIME_LIMIT );
                    if ( maxTimeLimitAttribute == null )
                    {
                        // If the 'maxTimeLimit' attribute does not exists,
                        // we throw an exception
                        throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV154.ErrorMaxTimeLimit" ) ); //$NON-NLS-1$
                    }
                    else
                    {
                        serverConfiguration.setMaxTimeLimit( Integer.parseInt( maxTimeLimitAttribute.getValue() ) );
                    }

                    // MaxSizeLimit
                    org.dom4j.Attribute maxSizeLimitAttribute = ldapServiceElement
                        .attribute( ServerXmlIOV154.ATTRIBUTE_MAX_SIZE_LIMIT );
                    if ( maxSizeLimitAttribute == null )
                    {
                        // If the 'maxSizeLimit' attribute does not exists,
                        // we throw an exception
                        throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV154.ErrorMaxSizeLimit" ) ); //$NON-NLS-1$
                    }
                    else
                    {
                        serverConfiguration.setMaxSizeLimit( Integer.parseInt( maxSizeLimitAttribute.getValue() ) );
                    }

                    // Supported Mechanisms
                    Element supportedMechanismsElement = ldapServiceElement
                        .element( ServerXmlIOV154.ELEMENT_SASL_MECHANISM_HANDLERS );
                    if ( supportedMechanismsElement != null )
                    {
                        // Looping on all elements
                        for ( Iterator<?> iterator = supportedMechanismsElement.elementIterator(); iterator.hasNext(); )
                        {
                            // Getting the  element
                            Element supportedMechanismValueElement = ( Element ) iterator.next();
                            String supportedMechanismValue = supportedMechanismValueElement.getName();
                            org.dom4j.Attribute mechNameAttribute = supportedMechanismValueElement
                                .attribute( ServerXmlIOV154.ATTRIBUTE_MECH_NAME );
                            String mechNameValue = ( mechNameAttribute == null ) ? null : mechNameAttribute.getValue();

                            if ( ServerXmlIOV154.ELEMENT_SIMPLE_MECHANISM_HANDLER
                                .equalsIgnoreCase( supportedMechanismValue )
                                && ServerXmlIOV154.SUPPORTED_MECHANISM_SIMPLE.equalsIgnoreCase( mechNameValue ) )
                            {
                                serverConfiguration.addSupportedMechanism( SupportedMechanismEnum.SIMPLE );
                            }
                            else if ( ServerXmlIOV154.ELEMENT_CRAM_MD5_MECHANISM_HANDLER
                                .equalsIgnoreCase( supportedMechanismValue )
                                && ServerXmlIOV154.SUPPORTED_MECHANISM_CRAM_MD5.equalsIgnoreCase( mechNameValue ) )
                            {
                                serverConfiguration.addSupportedMechanism( SupportedMechanismEnum.CRAM_MD5 );
                            }
                            else if ( ServerXmlIOV154.ELEMENT_DIGEST_MD5_MECHANISM_HANDLER
                                .equalsIgnoreCase( supportedMechanismValue )
                                && ServerXmlIOV154.SUPPORTED_MECHANISM_DIGEST_MD5.equalsIgnoreCase( mechNameValue ) )
                            {
                                serverConfiguration.addSupportedMechanism( SupportedMechanismEnum.DIGEST_MD5 );
                            }
                            else if ( ServerXmlIOV154.ELEMENT_GSSAPI_MECHANISM_HANDLER
                                .equalsIgnoreCase( supportedMechanismValue )
                                && ServerXmlIOV154.SUPPORTED_MECHANISM_GSSAPI.equalsIgnoreCase( mechNameValue ) )
                            {
                                serverConfiguration.addSupportedMechanism( SupportedMechanismEnum.GSSAPI );
                            }
                            else if ( ServerXmlIOV154.ELEMENT_NTLM_MECHANISM_HANDLER
                                .equalsIgnoreCase( supportedMechanismValue )
                                && ServerXmlIOV154.SUPPORTED_MECHANISM_NTLM.equalsIgnoreCase( mechNameValue ) )
                            {
                                org.dom4j.Attribute ntlmProviderFcqnAttribute = supportedMechanismValueElement
                                    .attribute( ServerXmlIOV154.ATTRIBUTE_NTLM_PROVIDER_FQCN );
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
                            else if ( ServerXmlIOV154.ELEMENT_NTLM_MECHANISM_HANDLER
                                .equalsIgnoreCase( supportedMechanismValue )
                                && ServerXmlIOV154.SUPPORTED_MECHANISM_GSS_SPNEGO.equalsIgnoreCase( mechNameValue ) )
                            {
                                org.dom4j.Attribute ntlmProviderFcqnAttribute = supportedMechanismValueElement
                                    .attribute( ServerXmlIOV154.ATTRIBUTE_NTLM_PROVIDER_FQCN );
                                if ( ntlmProviderFcqnAttribute != null )
                                {
                                    SupportedMechanismEnum gssSpnegoSupportedMechanism = SupportedMechanismEnum.GSS_SPNEGO;
                                    gssSpnegoSupportedMechanism.setNtlmProviderFqcn( ntlmProviderFcqnAttribute
                                        .getValue() );
                                    serverConfiguration.addSupportedMechanism( gssSpnegoSupportedMechanism );
                                }
                                else
                                {
                                    serverConfiguration.addSupportedMechanism( SupportedMechanismEnum.GSS_SPNEGO );
                                }
                            }
                        }
                    }

                    // SaslQop
                    Element SaslQopElement = ldapServiceElement.element( ServerXmlIOV154.ELEMENT_SASL_QOP );
                    if ( SaslQopElement != null )
                    {
                        // Looping on all 'value' elements
                        for ( Iterator<?> iterator = SaslQopElement.elementIterator( ServerXmlIOV154.ELEMENT_VALUE ); iterator
                            .hasNext(); )
                        {
                            // Getting the 'value' element
                            Element saslQopValueElement = ( Element ) iterator.next();

                            // Adding the SaslQop value
                            String saslQopValue = saslQopValueElement.getText().trim();
                            if ( ServerXmlIOV154.SASL_QOP_AUTH.equalsIgnoreCase( saslQopValue ) )
                            {
                                serverConfiguration.addSaslQop( SaslQualityOfProtectionEnum.AUTH );
                            }
                            else if ( ServerXmlIOV154.SASL_QOP_AUTH_INT.equalsIgnoreCase( saslQopValue ) )
                            {
                                serverConfiguration.addSaslQop( SaslQualityOfProtectionEnum.AUTH_INT );
                            }
                            else if ( ServerXmlIOV154.SASL_QOP_AUTH_CONF.equalsIgnoreCase( saslQopValue ) )
                            {
                                serverConfiguration.addSaslQop( SaslQualityOfProtectionEnum.AUTH_CONF );
                            }
                        }
                    }

                    // SaslRealms
                    Element SaslRealmsElement = ldapServiceElement.element( ServerXmlIOV154.ELEMENT_SASL_REALMS );
                    if ( SaslRealmsElement != null )
                    {
                        // Looping on all 'value' elements
                        for ( Iterator<?> iterator = SaslRealmsElement.elementIterator( ServerXmlIOV154.ELEMENT_VALUE ); iterator
                            .hasNext(); )
                        {
                            // Getting the 'value' element
                            Element saslRealmValueElement = ( Element ) iterator.next();

                            // Adding the SaslRealm value
                            serverConfiguration.addSaslRealm( saslRealmValueElement.getText().trim() );

                        }
                    }

                    // Extended operations
                    readExtendedOperations( ldapServiceElement, serverConfiguration );

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
    private void readExtendedOperations( Element element, ServerConfigurationV154 serverConfiguration )
    {
        // Getting the 'interceptors
        Element interceptorsElement = element.element( ServerXmlIOV154.ELEMENT_EXTENDED_OPERATION_HANDLERS );
        if ( interceptorsElement != null )
        {
            // Looping on all interceptor elements
            for ( Iterator<?> i = interceptorsElement.elementIterator(); i.hasNext(); )
            {
                // Getting the element
                Element extendedOperationElement = ( Element ) i.next();

                // Checking which extended operation it is
                String extendedOperationElementName = extendedOperationElement.getName();
                if ( ServerXmlIOV154.ELEMENT_START_TLS_HANDLER.equalsIgnoreCase( extendedOperationElementName ) )
                {
                    serverConfiguration.addExtendedOperation( ExtendedOperationEnum.START_TLS );
                }
                if ( ServerXmlIOV154.ELEMENT_GRACEFUL_SHUTDOWN_HANDLER.equalsIgnoreCase( extendedOperationElementName ) )
                {
                    serverConfiguration.addExtendedOperation( ExtendedOperationEnum.GRACEFUL_SHUTDOWN );
                }
                if ( ServerXmlIOV154.ELEMENT_LAUNCH_DIAGNOSTIC_UI_HANDLER
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
    private void readApacheDSBean( Element element, ServerConfigurationV154 serverConfiguration )
        throws ServerXmlIOException, NumberFormatException
    {
        // Getting the 'apacheDS' element
        Element apacheDsElement = element.element( ServerXmlIOV154.ELEMENT_APACHE_DS );
        if ( apacheDsElement != null )
        {
            // SynchPeriodMillis
            org.dom4j.Attribute synchPeriodMillisAttribute = apacheDsElement
                .attribute( ServerXmlIOV154.ATTRIBUTE_SYNCH_PERIOD_MILLIS );
            if ( synchPeriodMillisAttribute == null )
            {
                // If the 'synchPeriodMillis' attribute does not exists,
                // we throw an exception
                throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV154.ErrorSyncPeriodMillis" ) ); //$NON-NLS-1$
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
    public String toXml( ServerConfiguration serverConfiguration ) throws IOException
    {
        // Creating the document
        Document document = DocumentHelper.createDocument();

        // Creating the root element with its namespaces definitions
        Element root = document.addElement( new QName( ServerXmlIOV154.ELEMENT_BEANS, NAMESPACE_XBEAN_SPRING ) );
        root.add( NAMESPACE_SPRINGFRAMEWORK );
        root.add( NAMESPACE_APACHEDS );

        // DefaultDirectoryService Bean
        createDefaultDirectoryServiceBean( root, ( ServerConfigurationV154 ) serverConfiguration );

        // Adding the 'standardThreadPool' element
        Element standardThreadPoolElement = root.addElement( ServerXmlIOV154.ELEMENT_STANDARD_THREAD_POOL );
        standardThreadPoolElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_ID,
            ServerXmlIOV154.ELEMENT_STANDARD_THREAD_POOL );
        standardThreadPoolElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_MAX_THREADS, "" //$NON-NLS-1$
            + ( ( ServerConfigurationV154 ) serverConfiguration ).getMaxThreads() );

        // Adding the 'datagramAcceptor' element
        Element datagramAcceptorElement = root.addElement( ServerXmlIOV154.ELEMENT_DATAGRAM_ACCEPTOR );
        datagramAcceptorElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_ID, ServerXmlIOV154.ELEMENT_DATAGRAM_ACCEPTOR );
        datagramAcceptorElement.addAttribute( ServerXmlIOV154.ELEMENT_LOGIC_EXECUTOR, "#standardThreadPool" ); //$NON-NLS-1$

        // Adding the 'socketAcceptor' element
        Element socketAcceptorElement = root.addElement( ServerXmlIOV154.ELEMENT_SOCKET_ACCEPTOR );
        socketAcceptorElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_ID, ServerXmlIOV154.ELEMENT_SOCKET_ACCEPTOR );
        socketAcceptorElement.addAttribute( ServerXmlIOV154.ELEMENT_LOGIC_EXECUTOR, "#standardThreadPool" ); //$NON-NLS-1$

        // ChangePasswordServer Bean
        createChangePasswordServerBean( root, ( ServerConfigurationV154 ) serverConfiguration );

        // KdcServer Bean
        createKdcServerBean( root, ( ServerConfigurationV154 ) serverConfiguration );

        // NtpServer Bean
        createNtpServerBean( root, ( ServerConfigurationV154 ) serverConfiguration );

        // DnsServer Bean
        createDnsServerBean( root, ( ServerConfigurationV154 ) serverConfiguration );

        // LdapsServer Bean
        createLdapsServerBean( root, ( ServerConfigurationV154 ) serverConfiguration );

        // LdapServer Bean
        createLdapServerBean( root, ( ServerConfigurationV154 ) serverConfiguration );

        // ApacheDS Bean
        createApacheDSBean( root, ( ServerConfigurationV154 ) serverConfiguration );

        // CustomEditorConfigurer Bean
        createCustomEditorConfigurerBean( root );

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
    private void createDefaultDirectoryServiceBean( Element root, ServerConfigurationV154 serverConfiguration )
    {
        // Adding the 'defaultDirectoryService' element
        Element defaultDirectoryServiceElement = root.addElement( ServerXmlIOV154.ELEMENT_DEFAULT_DIRECTORY_SERVICE );

        // Id
        defaultDirectoryServiceElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_ID,
            ServerXmlIOV154.VALUE_DIRECTORY_SERVICE );

        // InstanceId
        defaultDirectoryServiceElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_INSTANCE_ID,
            ServerXmlIOV154.VALUE_DEFAULT );

        // WorkingDirectory
        defaultDirectoryServiceElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_WORKING_DIRECTORY,
            ServerXmlIOV154.VALUE_EXAMPLE_DOT_COM );

        // AllowAnonymousAccess
        defaultDirectoryServiceElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_ALLOW_ANONYMOUS_ACCESS, "" //$NON-NLS-1$
            + serverConfiguration.isAllowAnonymousAccess() );

        // AccessControlEnabled
        defaultDirectoryServiceElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_ACCESS_CONTROL_ENABLED, "" //$NON-NLS-1$
            + serverConfiguration.isEnableAccessControl() );

        // DenormalizeOpAttrsEnabled
        defaultDirectoryServiceElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_DENORMALIZE_OP_ATTRS_ENABLED, "" //$NON-NLS-1$
            + serverConfiguration.isDenormalizeOpAttr() );

        // Adding the 'systemPartition' element
        Element systemPartitionElement = defaultDirectoryServiceElement
            .addElement( ServerXmlIOV154.ELEMENT_SYSTEM_PARTITION );

        // Adding System Partition Bean
        createSystemPartitionBean( systemPartitionElement, serverConfiguration );

        // Adding the 'partitions' element
        Element partitionsElement = defaultDirectoryServiceElement.addElement( ServerXmlIOV154.ELEMENT_PARTITIONS );

        // Adding User Partitions Beans
        createUserPartitions( partitionsElement, serverConfiguration );

        // Adding the 'interceptors' element
        Element interceptorsElement = defaultDirectoryServiceElement.addElement( ServerXmlIOV154.ELEMENT_INTERCEPTORS );

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
    private void createSystemPartitionBean( Element systemPartitionElement, ServerConfigurationV154 serverConfiguration )
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
    private void createUserPartitions( Element partitionsElement, ServerConfigurationV154 serverConfiguration )
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
        Element jdbmPartitionElement = element.addElement( ServerXmlIOV154.ELEMENT_JDBM_PARTITION );

        // Id
        jdbmPartitionElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_ID, partition.getId() );

        // CacheSize
        jdbmPartitionElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_CACHE_SIZE, "" + partition.getCacheSize() ); //$NON-NLS-1$

        // Suffix
        jdbmPartitionElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_SUFFIX, partition.getSuffix() );

        // OptimizerEnabled
        jdbmPartitionElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_OPTIMIZER_ENABLED, "" //$NON-NLS-1$
            + partition.isEnableOptimizer() );

        // SyncOnWrite
        jdbmPartitionElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_SYNC_ON_WRITE, "" //$NON-NLS-1$
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
        Element indexedAttributeElement = element.addElement( ServerXmlIOV154.ELEMENT_INDEXED_ATTRIBUTES );

        if ( indexedAttributes != null )
        {
            // Looping on indexed attributes
            for ( IndexedAttribute indexedAttribute : indexedAttributes )
            {
                // Adding the 'jdbmIndex' element
                Element jdbmIndexElement = indexedAttributeElement.addElement( ServerXmlIOV154.ELEMENT_JDBM_INDEX );
                jdbmIndexElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_ATTRIBUTE_ID, indexedAttribute
                    .getAttributeId() );
                jdbmIndexElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_CACHE_SIZE, "" //$NON-NLS-1$
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
    private void createInterceptors( Element interceptorsElement, ServerConfigurationV154 serverConfiguration )
    {
        List<InterceptorEnum> interceptors = serverConfiguration.getInterceptors();

        if ( interceptors.contains( InterceptorEnum.NORMALIZATION ) )
        {
            interceptorsElement.addElement( ServerXmlIOV154.ELEMENT_NORMALIZATION_INTERCEPTOR );
        }
        if ( interceptors.contains( InterceptorEnum.AUTHENTICATION ) )
        {
            interceptorsElement.addElement( ServerXmlIOV154.ELEMENT_AUTHENTICATION_INTERCEPTOR );
        }
        if ( interceptors.contains( InterceptorEnum.ACI_AUTHORIZATION ) )
        {
            interceptorsElement.addElement( ServerXmlIOV154.ELEMENT_ACI_AUTHORIZATION_INTERCEPTOR );
        }
        if ( interceptors.contains( InterceptorEnum.DEFAULT_AUTHORIZATION ) )
        {
            interceptorsElement.addElement( ServerXmlIOV154.ELEMENT_DEFAULT_AUTHORIZATION_INTERCEPTOR );
        }
        if ( interceptors.contains( InterceptorEnum.EXCEPTION ) )
        {
            interceptorsElement.addElement( ServerXmlIOV154.ELEMENT_EXCEPTION_INTERCEPTOR );
        }
        if ( interceptors.contains( InterceptorEnum.OPERATIONAL_ATTRIBUTE ) )
        {
            interceptorsElement.addElement( ServerXmlIOV154.ELEMENT_OPERATIONAL_ATTRIBUTE_INTERCEPTOR );
        }
        if ( interceptors.contains( InterceptorEnum.SCHEMA ) )
        {
            interceptorsElement.addElement( ServerXmlIOV154.ELEMENT_SCHEMA_INTERCEPTOR );
        }
        if ( interceptors.contains( InterceptorEnum.SUBENTRY ) )
        {
            interceptorsElement.addElement( ServerXmlIOV154.ELEMENT_SUBENTRY_INTERCEPTOR );
        }
        if ( interceptors.contains( InterceptorEnum.COLLECTIVE_ATTRIBUTE ) )
        {
            interceptorsElement.addElement( ServerXmlIOV154.ELEMENT_COLLECTIVE_ATTRIBUTE_INTERCEPTOR );
        }
        if ( interceptors.contains( InterceptorEnum.EVENT ) )
        {
            interceptorsElement.addElement( ServerXmlIOV154.ELEMENT_EVENT_INTERCEPTOR );
        }
        if ( interceptors.contains( InterceptorEnum.TRIGGER ) )
        {
            interceptorsElement.addElement( ServerXmlIOV154.ELEMENT_TRIGGER_INTERCEPTOR );
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
    private void createChangePasswordServerBean( Element root, ServerConfigurationV154 serverConfiguration )
    {
        if ( serverConfiguration.isEnableChangePassword() )
        {
            // Adding the 'changePasswordServer' element
            Element changePasswordServerElement = root.addElement( ServerXmlIOV154.ELEMENT_CHANGE_PASSWORD_SERVER );

            // Enabled
            changePasswordServerElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_ENABLED, "" //$NON-NLS-1$
                + serverConfiguration.isEnableChangePassword() );

            // IpPort
            changePasswordServerElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_IP_PORT, "" //$NON-NLS-1$
                + serverConfiguration.getChangePasswordPort() );

            // Adding 'directoryService' element
            changePasswordServerElement.addElement( ServerXmlIOV154.VALUE_DIRECTORY_SERVICE ).setText(
                "#directoryService" ); //$NON-NLS-1$

            // Adding 'datagramAcceptor' element
            changePasswordServerElement.addElement( ServerXmlIOV154.ELEMENT_DATAGRAM_ACCEPTOR ).setText(
                "#datagramAcceptor" ); //$NON-NLS-1$

            // Adding 'socketAcceptor' element
            changePasswordServerElement.addElement( ServerXmlIOV154.ELEMENT_SOCKET_ACCEPTOR ).setText(
                "#socketAcceptor" ); //$NON-NLS-1$
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
    private void createKdcServerBean( Element root, ServerConfigurationV154 serverConfiguration )
    {
        if ( serverConfiguration.isEnableKerberos() )
        {
            // Adding the 'kdcServer' element
            Element kdcServerElement = root.addElement( ServerXmlIOV154.ELEMENT_KDC_SERVER );

            // Enabled
            kdcServerElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_ENABLED, "" //$NON-NLS-1$
                + serverConfiguration.isEnableKerberos() );

            // IpPort
            kdcServerElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_IP_PORT, "" //$NON-NLS-1$
                + serverConfiguration.getKerberosPort() );

            // Adding 'directoryService' element
            kdcServerElement.addElement( ServerXmlIOV154.VALUE_DIRECTORY_SERVICE ).setText( "#directoryService" ); //$NON-NLS-1$

            // Adding 'datagramAcceptor' element
            kdcServerElement.addElement( ServerXmlIOV154.ELEMENT_DATAGRAM_ACCEPTOR ).setText( "#datagramAcceptor" ); //$NON-NLS-1$

            // Adding 'socketAcceptor' element
            kdcServerElement.addElement( ServerXmlIOV154.ELEMENT_SOCKET_ACCEPTOR ).setText( "#socketAcceptor" ); //$NON-NLS-1$
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
    private void createNtpServerBean( Element root, ServerConfigurationV154 serverConfiguration )
    {
        if ( serverConfiguration.isEnableNtp() )
        {
            // Adding the 'ntpServer' element
            Element ntpServerElement = root.addElement( ServerXmlIOV154.ELEMENT_NTP_SERVER );

            // Enabled
            ntpServerElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_ENABLED, "" + serverConfiguration.isEnableNtp() ); //$NON-NLS-1$

            // IpPort
            ntpServerElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_IP_PORT, "" + serverConfiguration.getNtpPort() ); //$NON-NLS-1$

            // Adding 'datagramAcceptor' element
            ntpServerElement.addElement( ServerXmlIOV154.ELEMENT_DATAGRAM_ACCEPTOR ).setText( "#datagramAcceptor" ); //$NON-NLS-1$

            // Adding 'socketAcceptor' element
            ntpServerElement.addElement( ServerXmlIOV154.ELEMENT_SOCKET_ACCEPTOR ).setText( "#socketAcceptor" ); //$NON-NLS-1$
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
    private void createDnsServerBean( Element root, ServerConfigurationV154 serverConfiguration )
    {
        if ( serverConfiguration.isEnableDns() )
        {
            // Adding the 'dnsServer' element
            Element dnsServerElement = root.addElement( ServerXmlIOV154.ELEMENT_DNS_SERVER );

            // Enabled
            dnsServerElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_ENABLED, "" + serverConfiguration.isEnableDns() ); //$NON-NLS-1$

            // IpPort
            dnsServerElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_IP_PORT, "" + serverConfiguration.getDnsPort() ); //$NON-NLS-1$

            // Adding 'directoryService' element
            dnsServerElement.addElement( ServerXmlIOV154.VALUE_DIRECTORY_SERVICE ).setText( "#directoryService" ); //$NON-NLS-1$

            // Adding 'datagramAcceptor' element
            dnsServerElement.addElement( ServerXmlIOV154.ELEMENT_DATAGRAM_ACCEPTOR ).setText( "#datagramAcceptor" ); //$NON-NLS-1$

            // Adding 'socketAcceptor' element
            dnsServerElement.addElement( ServerXmlIOV154.ELEMENT_SOCKET_ACCEPTOR ).setText( "#socketAcceptor" ); //$NON-NLS-1$
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
    private void createLdapsServerBean( Element root, ServerConfigurationV154 serverConfiguration )
    {
        // Adding the 'ldapServer' element
        Element ldapServerElement = root.addElement( ServerXmlIOV154.ELEMENT_LDAP_SERVICE );

        // Id
        ldapServerElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_ID, ServerXmlIOV154.ELEMENT_LDAPS_SERVICE );

        // IpPort
        ldapServerElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_IP_PORT, "" + serverConfiguration.getLdapsPort() ); //$NON-NLS-1$

        // Enabled
        ldapServerElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_ENABLED, "" + serverConfiguration.isEnableLdaps() ); //$NON-NLS-1$

        // EnableLdaps
        ldapServerElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_ENABLE_LDAPS, "" //$NON-NLS-1$
            + serverConfiguration.isEnableLdaps() );

        // Adding 'directoryService' element
        ldapServerElement.addElement( ServerXmlIOV154.VALUE_DIRECTORY_SERVICE ).setText( "#directoryService" ); //$NON-NLS-1$

        // Adding 'socketAcceptor' element
        ldapServerElement.addElement( ServerXmlIOV154.ELEMENT_SOCKET_ACCEPTOR ).setText( "#socketAcceptor" ); //$NON-NLS-1$
    }


    /**
     * Creates the LdapServer bean.
     *
     * @param root
     *      the root element
     * @param serverConfiguration
     *      the server configuration
     */
    private void createLdapServerBean( Element root, ServerConfigurationV154 serverConfiguration )
    {
        // Adding the 'ldapServer' element
        Element ldapServerElement = root.addElement( ServerXmlIOV154.ELEMENT_LDAP_SERVICE );

        // Id
        ldapServerElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_ID, ServerXmlIOV154.ELEMENT_LDAP_SERVICE );

        // Enabled
        ldapServerElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_ENABLED, "" + serverConfiguration.isEnableLdap() ); //$NON-NLS-1$

        // IpPort
        ldapServerElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_IP_PORT, "" + serverConfiguration.getLdapPort() ); //$NON-NLS-1$

        // AllowAnonymousAccess
        ldapServerElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_ALLOW_ANONYMOUS_ACCESS, "" //$NON-NLS-1$
            + serverConfiguration.isAllowAnonymousAccess() );

        // SaslHost
        ldapServerElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_SASL_HOST, "" + serverConfiguration.getSaslHost() ); //$NON-NLS-1$

        // SaslPrincipal
        ldapServerElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_SASL_PRINCIPAL, "" //$NON-NLS-1$
            + serverConfiguration.getSaslPrincipal() );

        // SearchBaseDn
        ldapServerElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_SEARCH_BASE_DN, "ou=users,ou=system" ); //$NON-NLS-1$

        // MaxTimeLimit
        ldapServerElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_MAX_TIME_LIMIT, "" //$NON-NLS-1$
            + serverConfiguration.getMaxTimeLimit() );

        // MaxSizeLimit
        ldapServerElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_MAX_SIZE_LIMIT, "" //$NON-NLS-1$
            + serverConfiguration.getMaxSizeLimit() );

        // Adding 'directoryService' element
        ldapServerElement.addElement( ServerXmlIOV154.VALUE_DIRECTORY_SERVICE ).setText( "#directoryService" ); //$NON-NLS-1$

        // Adding 'socketAcceptor' element
        ldapServerElement.addElement( ServerXmlIOV154.ELEMENT_SOCKET_ACCEPTOR ).setText( "#socketAcceptor" ); //$NON-NLS-1$

        // Adding 'saslMechanismHandlers' element
        Element saslMechanismHandlersElement = ldapServerElement
            .addElement( ServerXmlIOV154.ELEMENT_SASL_MECHANISM_HANDLERS );

        // Adding each supported mechanism
        for ( SupportedMechanismEnum supportedMechanism : serverConfiguration.getSupportedMechanisms() )
        {
            switch ( supportedMechanism )
            {
                case SIMPLE:
                    Element simpleMechanismHandlerElement = saslMechanismHandlersElement
                        .addElement( ServerXmlIOV154.ELEMENT_SIMPLE_MECHANISM_HANDLER );
                    simpleMechanismHandlerElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_MECH_NAME,
                        ServerXmlIOV154.SUPPORTED_MECHANISM_SIMPLE );
                    break;
                case CRAM_MD5:
                    Element cramMd5MechanismHandlerElement = saslMechanismHandlersElement
                        .addElement( ServerXmlIOV154.ELEMENT_CRAM_MD5_MECHANISM_HANDLER );
                    cramMd5MechanismHandlerElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_MECH_NAME,
                        ServerXmlIOV154.SUPPORTED_MECHANISM_CRAM_MD5 );
                    break;
                case DIGEST_MD5:
                    Element digestMd5MechanismHandlerElement = saslMechanismHandlersElement
                        .addElement( ServerXmlIOV154.ELEMENT_DIGEST_MD5_MECHANISM_HANDLER );
                    digestMd5MechanismHandlerElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_MECH_NAME,
                        ServerXmlIOV154.SUPPORTED_MECHANISM_DIGEST_MD5 );
                    break;
                case GSSAPI:
                    Element gssapiMechanismHandlerElement = saslMechanismHandlersElement
                        .addElement( ServerXmlIOV154.ELEMENT_GSSAPI_MECHANISM_HANDLER );
                    gssapiMechanismHandlerElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_MECH_NAME,
                        ServerXmlIOV154.SUPPORTED_MECHANISM_GSSAPI );
                    break;
                case NTLM:
                    Element ntlmMechanismHandlerElement = saslMechanismHandlersElement
                        .addElement( ServerXmlIOV154.ELEMENT_NTLM_MECHANISM_HANDLER );
                    ntlmMechanismHandlerElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_MECH_NAME,
                        ServerXmlIOV154.SUPPORTED_MECHANISM_NTLM );
                    ntlmMechanismHandlerElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_NTLM_PROVIDER_FQCN,
                        supportedMechanism.getNtlmProviderFqcn() );
                    break;
                case GSS_SPNEGO:
                    Element gssSpnegoMechanismHandlerElement = saslMechanismHandlersElement
                        .addElement( ServerXmlIOV154.ELEMENT_NTLM_MECHANISM_HANDLER );
                    gssSpnegoMechanismHandlerElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_MECH_NAME,
                        ServerXmlIOV154.SUPPORTED_MECHANISM_GSS_SPNEGO );
                    gssSpnegoMechanismHandlerElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_NTLM_PROVIDER_FQCN,
                        supportedMechanism.getNtlmProviderFqcn() );
                    break;
            }
        }

        // Adding 'SaslQop' element
        Element saslQopElement = ldapServerElement.addElement( ServerXmlIOV154.ELEMENT_SASL_QOP );

        // Adding each SaslQop item
        for ( SaslQualityOfProtectionEnum saslQop : serverConfiguration.getSaslQops() )
        {
            switch ( saslQop )
            {
                case AUTH:
                    saslQopElement.addElement( new QName( ServerXmlIOV154.ELEMENT_VALUE, NAMESPACE_SPRINGFRAMEWORK ) )
                        .setText( ServerXmlIOV154.SASL_QOP_AUTH );
                    break;
                case AUTH_INT:
                    saslQopElement.addElement( new QName( ServerXmlIOV154.ELEMENT_VALUE, NAMESPACE_SPRINGFRAMEWORK ) )
                        .setText( ServerXmlIOV154.SASL_QOP_AUTH_INT );
                    break;
                case AUTH_CONF:
                    saslQopElement.addElement( new QName( ServerXmlIOV154.ELEMENT_VALUE, NAMESPACE_SPRINGFRAMEWORK ) )
                        .setText( ServerXmlIOV154.SASL_QOP_AUTH_CONF );
                    break;
            }
        }

        // Adding 'SaslRealms' element
        Element saslRealmsElement = ldapServerElement.addElement( ServerXmlIOV154.ELEMENT_SASL_REALMS );

        // Adding each SaslRealm item
        for ( String saslRealm : serverConfiguration.getSaslRealms() )
        {
            saslRealmsElement.addElement( new QName( ServerXmlIOV154.ELEMENT_VALUE, NAMESPACE_SPRINGFRAMEWORK ) )
                .setText( saslRealm );
        }

        // Adding 'ExtendedOperations' element
        Element extendedOperationsElement = ldapServerElement
            .addElement( ServerXmlIOV154.ELEMENT_EXTENDED_OPERATION_HANDLERS );

        // Adding each extended operation item
        List<ExtendedOperationEnum> extendedOperations = serverConfiguration.getExtendedOperations();
        if ( extendedOperations.contains( ExtendedOperationEnum.START_TLS ) )
        {
            extendedOperationsElement.addElement( ServerXmlIOV154.ELEMENT_START_TLS_HANDLER );
        }
        if ( extendedOperations.contains( ExtendedOperationEnum.GRACEFUL_SHUTDOWN ) )
        {
            extendedOperationsElement.addElement( ServerXmlIOV154.ELEMENT_GRACEFUL_SHUTDOWN_HANDLER );
        }
        if ( extendedOperations.contains( ExtendedOperationEnum.LAUNCH_DIAGNOSTIC_UI ) )
        {
            extendedOperationsElement.addElement( ServerXmlIOV154.ELEMENT_LAUNCH_DIAGNOSTIC_UI_HANDLER );
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
    private void createApacheDSBean( Element root, ServerConfigurationV154 serverConfiguration )
    {
        // Adding the 'apacheDS' element
        Element apacheDSElement = root.addElement( ServerXmlIOV154.ELEMENT_APACHE_DS );

        // Id
        apacheDSElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_ID, ServerXmlIOV154.ELEMENT_APACHE_DS );

        // SynchPeriodMillis
        apacheDSElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_SYNCH_PERIOD_MILLIS, "" //$NON-NLS-1$
            + serverConfiguration.getSynchronizationPeriod() );

        // AllowAnonymousAccess
        apacheDSElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_ALLOW_ANONYMOUS_ACCESS, "" //$NON-NLS-1$
            + serverConfiguration.isAllowAnonymousAccess() );

        // Adding 'directoryService' element
        apacheDSElement.addElement( ServerXmlIOV154.VALUE_DIRECTORY_SERVICE ).setText( "#directoryService" ); //$NON-NLS-1$

        // Adding 'ldapService' element
        apacheDSElement.addElement( ServerXmlIOV154.ELEMENT_LDAP_SERVICE ).setText( "#ldapService" ); //$NON-NLS-1$

        // Adding 'ldapsService' element
        apacheDSElement.addElement( ServerXmlIOV154.ELEMENT_LDAPS_SERVICE ).setText( "#ldapsService" ); //$NON-NLS-1$
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
        Element beanElement = root.addElement( new QName( ServerXmlIOV154.ELEMENT_BEAN, NAMESPACE_XBEAN_SPRING ) );
        beanElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_CLASS,
            "org.springframework.beans.factory.config.CustomEditorConfigurer" ); //$NON-NLS-1$

        // Adding the 'property' element
        Element propertyElement = beanElement.addElement( new QName( ServerXmlIOV154.ELEMENT_PROPERTY,
            NAMESPACE_XBEAN_SPRING ) );
        propertyElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_NAME, ServerXmlIOV154.VALUE_CUSTOM_EDITORS );

        // Adding the 'map' element
        Element mapElement = propertyElement
            .addElement( new QName( ServerXmlIOV154.ELEMENT_MAP, NAMESPACE_XBEAN_SPRING ) );

        // Adding the 'entry' element
        Element entryElement = mapElement
            .addElement( new QName( ServerXmlIOV154.ELEMENT_ENTRY, NAMESPACE_XBEAN_SPRING ) );
        entryElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_KEY, "javax.naming.directory.Attributes" ); //$NON-NLS-1$

        // Adding the inner 'bean' element
        Element innerBeanElement = entryElement.addElement( new QName( ServerXmlIOV154.ELEMENT_BEAN,
            NAMESPACE_XBEAN_SPRING ) );
        innerBeanElement.addAttribute( ServerXmlIOV154.ATTRIBUTE_CLASS,
            "org.apache.directory.server.core.configuration.AttributesPropertyEditor" ); //$NON-NLS-1$
    }
}
