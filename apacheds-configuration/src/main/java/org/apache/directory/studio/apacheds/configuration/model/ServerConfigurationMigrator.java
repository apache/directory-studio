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


import java.util.ArrayList;
import java.util.List;


/**
 * This class can be used to migrate a server configuration from a version 
 * to another one.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ServerConfigurationMigrator
{
    /**
     * Migrates a server configuration from version 1.5.3 to version 1.5.4.
     *
     * @param configuration
     *      a 1.5.3 server configuration
     * @return
     *      the associated 1.5.4 server configuration
     */
    public static org.apache.directory.studio.apacheds.configuration.model.v154.ServerConfigurationV154 migrateToVersion154(
        org.apache.directory.studio.apacheds.configuration.model.v153.ServerConfigurationV153 configuration )
    {
        org.apache.directory.studio.apacheds.configuration.model.v154.ServerConfigurationV154 destinationConfiguration = new org.apache.directory.studio.apacheds.configuration.model.v154.ServerConfigurationV154();

        destinationConfiguration.setChangePasswordPort( configuration.getChangePasswordPort() );
        destinationConfiguration.setDnsPort( configuration.getDnsPort() );
        destinationConfiguration.setExtendedOperations( migrateExtendedOperationsToVersion154( configuration
            .getExtendedOperations() ) );
        destinationConfiguration.setInterceptors( migrateInterceptorsToVersion154( configuration.getInterceptors() ) );
        destinationConfiguration.setKerberosPort( configuration.getKerberosPort() );
        destinationConfiguration.setLdapPort( configuration.getLdapPort() );
        destinationConfiguration.setLdapsPort( configuration.getLdapsPort() );
        destinationConfiguration.setMaxSizeLimit( configuration.getMaxSizeLimit() );
        destinationConfiguration.setMaxThreads( configuration.getMaxThreads() );
        destinationConfiguration.setMaxTimeLimit( configuration.getMaxTimeLimit() );
        destinationConfiguration.setNtpPort( configuration.getNtpPort() );
        destinationConfiguration.setPartitions( migratePartitionsToVersion154( configuration.getPartitions() ) );
        destinationConfiguration.setSaslHost( configuration.getSaslHost() );
        destinationConfiguration.setSaslPrincipal( configuration.getSaslPrincipal() );
        destinationConfiguration
            .setSaslQops( migrateSaslQualityOfProtectionToVersion154( configuration.getSaslQops() ) );
        destinationConfiguration.setSaslRealms( configuration.getSaslRealms() );
        destinationConfiguration.setSearchBaseDn( configuration.getSearchBaseDn() );
        destinationConfiguration.setSupportedMechanisms( migrateSupportedMechanismsToVersion154( configuration
            .getSupportedMechanisms() ) );
        destinationConfiguration.setSynchronizationPeriod( configuration.getSynchronizationPeriod() );
        destinationConfiguration.setAllowAnonymousAccess( configuration.isAllowAnonymousAccess() );
        destinationConfiguration.setDenormalizeOpAttr( configuration.isDenormalizeOpAttr() );
        destinationConfiguration.setEnableAccessControl( configuration.isEnableAccessControl() );
        destinationConfiguration.setEnableChangePassword( configuration.isEnableChangePassword() );
        destinationConfiguration.setEnableDns( configuration.isEnableDns() );
        destinationConfiguration.setEnableKerberos( configuration.isEnableKerberos() );
        destinationConfiguration.setEnableLdap( configuration.isEnableLdap() );
        destinationConfiguration.setEnableLdaps( configuration.isEnableLdaps() );
        destinationConfiguration.setEnableNtp( configuration.isEnableNtp() );

        return destinationConfiguration;
    }


    /**
     * Migrates a list of extended operations from version 1.5.3 to version 1.5.4.
     *
     * @param extendedOperations
     *      a list of 1.5.3 extended operations
     * @return
     *      the associated list of 1.5.4 extended operations
     */
    private static List<org.apache.directory.studio.apacheds.configuration.model.v154.ExtendedOperationEnum> migrateExtendedOperationsToVersion154(
        List<org.apache.directory.studio.apacheds.configuration.model.v153.ExtendedOperationEnum> extendedOperations )
    {
        if ( extendedOperations != null )
        {
            List<org.apache.directory.studio.apacheds.configuration.model.v154.ExtendedOperationEnum> destinationExtendedOperations = new ArrayList<org.apache.directory.studio.apacheds.configuration.model.v154.ExtendedOperationEnum>();
            for ( org.apache.directory.studio.apacheds.configuration.model.v153.ExtendedOperationEnum extendedOperation : extendedOperations )
            {
                switch ( extendedOperation )
                {
                    case GRACEFUL_SHUTDOWN:
                        destinationExtendedOperations
                            .add( org.apache.directory.studio.apacheds.configuration.model.v154.ExtendedOperationEnum.GRACEFUL_SHUTDOWN );
                        break;
                    case LAUNCH_DIAGNOSTIC_UI:
                        destinationExtendedOperations
                            .add( org.apache.directory.studio.apacheds.configuration.model.v154.ExtendedOperationEnum.LAUNCH_DIAGNOSTIC_UI );
                        break;
                    case START_TLS:
                        destinationExtendedOperations
                            .add( org.apache.directory.studio.apacheds.configuration.model.v154.ExtendedOperationEnum.START_TLS );
                        break;
                }
            }

            return destinationExtendedOperations;
        }

        return null;
    }


    /**
     * Migrates a list of interceptors from version 1.5.3 to version 1.5.4.
     *
     * @param interceptors
     *      a list of 1.5.3 interceptors
     * @return
     *      the associated list of 1.5.4 interceptors
     */
    private static List<org.apache.directory.studio.apacheds.configuration.model.v154.InterceptorEnum> migrateInterceptorsToVersion154(
        List<org.apache.directory.studio.apacheds.configuration.model.v153.InterceptorEnum> interceptors )
    {
        if ( interceptors != null )
        {
            List<org.apache.directory.studio.apacheds.configuration.model.v154.InterceptorEnum> destinationInterceptors = new ArrayList<org.apache.directory.studio.apacheds.configuration.model.v154.InterceptorEnum>();
            for ( org.apache.directory.studio.apacheds.configuration.model.v153.InterceptorEnum interceptor : interceptors )
            {
                switch ( interceptor )
                {
                    case ACI_AUTHORIZATION:
                        destinationInterceptors
                            .add( org.apache.directory.studio.apacheds.configuration.model.v154.InterceptorEnum.ACI_AUTHORIZATION );
                        break;
                    case AUTHENTICATION:
                        destinationInterceptors
                            .add( org.apache.directory.studio.apacheds.configuration.model.v154.InterceptorEnum.AUTHENTICATION );
                        break;
                    case COLLECTIVE_ATTRIBUTE:
                        destinationInterceptors
                            .add( org.apache.directory.studio.apacheds.configuration.model.v154.InterceptorEnum.COLLECTIVE_ATTRIBUTE );
                        break;
                    case DEFAULT_AUTHORIZATION:
                        destinationInterceptors
                            .add( org.apache.directory.studio.apacheds.configuration.model.v154.InterceptorEnum.DEFAULT_AUTHORIZATION );
                        break;
                    case EVENT:
                        destinationInterceptors
                            .add( org.apache.directory.studio.apacheds.configuration.model.v154.InterceptorEnum.EVENT );
                        break;
                    case EXCEPTION:
                        destinationInterceptors
                            .add( org.apache.directory.studio.apacheds.configuration.model.v154.InterceptorEnum.EXCEPTION );
                        break;
                    case NORMALIZATION:
                        destinationInterceptors
                            .add( org.apache.directory.studio.apacheds.configuration.model.v154.InterceptorEnum.NORMALIZATION );
                        break;
                    case OPERATIONAL_ATTRIBUTE:
                        destinationInterceptors
                            .add( org.apache.directory.studio.apacheds.configuration.model.v154.InterceptorEnum.OPERATIONAL_ATTRIBUTE );
                        break;
                    case REFERRAL:
                        // The referral interceptor has disappeared from the 1.5.4 configuration
                        break;
                    case REPLICATION:
                        destinationInterceptors
                            .add( org.apache.directory.studio.apacheds.configuration.model.v154.InterceptorEnum.REPLICATION );
                        break;
                    case SCHEMA:
                        destinationInterceptors
                            .add( org.apache.directory.studio.apacheds.configuration.model.v154.InterceptorEnum.SCHEMA );
                        break;
                    case SUBENTRY:
                        destinationInterceptors
                            .add( org.apache.directory.studio.apacheds.configuration.model.v154.InterceptorEnum.SUBENTRY );
                        break;
                    case TRIGGER:
                        destinationInterceptors
                            .add( org.apache.directory.studio.apacheds.configuration.model.v154.InterceptorEnum.TRIGGER );
                        break;
                }
            }

            return destinationInterceptors;
        }

        return null;
    }


    /**
     * Migrates a list of partitions from version 1.5.3 to version 1.5.4.
     *
     * @param partitions
     *      a list of 1.5.3 partitions
     * @return
     *      the associated list of 1.5.4 partitions
     */
    private static List<org.apache.directory.studio.apacheds.configuration.model.v154.Partition> migratePartitionsToVersion154(
        List<org.apache.directory.studio.apacheds.configuration.model.v153.Partition> partitions )
    {
        if ( partitions != null )
        {
            List<org.apache.directory.studio.apacheds.configuration.model.v154.Partition> destinationPartitions = new ArrayList<org.apache.directory.studio.apacheds.configuration.model.v154.Partition>();
            for ( org.apache.directory.studio.apacheds.configuration.model.v153.Partition partition : partitions )
            {
                org.apache.directory.studio.apacheds.configuration.model.v154.Partition destinationPartition = new org.apache.directory.studio.apacheds.configuration.model.v154.Partition();
                destinationPartition.setCacheSize( partition.getCacheSize() );
                destinationPartition.setId( partition.getId() );
                destinationPartition.setIndexedAttributes( migrateIndexedAttributestoVersion154( partition
                    .getIndexedAttributes() ) );
                destinationPartition.setSuffix( partition.getSuffix() );
                destinationPartition.setEnableOptimizer( partition.isEnableOptimizer() );
                destinationPartition.setSynchronizationOnWrite( partition.isSynchronizationOnWrite() );
                destinationPartition.setSystemPartition( partition.isSystemPartition() );

                destinationPartitions.add( destinationPartition );
            }

            return destinationPartitions;
        }

        return null;
    }


    /**
     * Migrates a list of indexed attributes from version 1.5.3 to version 1.5.4.
     *
     * @param indexedAttributes
     *       a list of 1.5.3 indexed attributes
     * @return
     *      the associated list of 1.5.4 indexed attributes
     */
    private static List<org.apache.directory.studio.apacheds.configuration.model.v154.IndexedAttribute> migrateIndexedAttributestoVersion154(
        List<org.apache.directory.studio.apacheds.configuration.model.v153.IndexedAttribute> indexedAttributes )
    {
        if ( indexedAttributes != null )
        {
            List<org.apache.directory.studio.apacheds.configuration.model.v154.IndexedAttribute> destinationIndexedAttributes = new ArrayList<org.apache.directory.studio.apacheds.configuration.model.v154.IndexedAttribute>();
            for ( org.apache.directory.studio.apacheds.configuration.model.v153.IndexedAttribute indexedAttribute : indexedAttributes )
            {
                String attributeId = indexedAttribute.getAttributeId();
                int cacheSize = indexedAttribute.getCacheSize();
                org.apache.directory.studio.apacheds.configuration.model.v154.IndexedAttribute destinationIndexedAttribute = new org.apache.directory.studio.apacheds.configuration.model.v154.IndexedAttribute(
                    attributeId, cacheSize );
                destinationIndexedAttributes.add( destinationIndexedAttribute );
            }

            return destinationIndexedAttributes;
        }

        return null;
    }


    /**
     * Migrates a list of SASL quality of protection objects from version 1.5.3 to version 1.5.4.
     *
     * @param saslQops
     *      a list of 1.5.3 SASL quality of protection objects
     * @return
     *      the associated list of 1.5.4 SASL quality of protection objects
     */
    private static List<org.apache.directory.studio.apacheds.configuration.model.v154.SaslQualityOfProtectionEnum> migrateSaslQualityOfProtectionToVersion154(
        List<org.apache.directory.studio.apacheds.configuration.model.v153.SaslQualityOfProtectionEnum> saslQops )
    {
        if ( saslQops != null )
        {
            List<org.apache.directory.studio.apacheds.configuration.model.v154.SaslQualityOfProtectionEnum> destinationSaslQops = new ArrayList<org.apache.directory.studio.apacheds.configuration.model.v154.SaslQualityOfProtectionEnum>();
            for ( org.apache.directory.studio.apacheds.configuration.model.v153.SaslQualityOfProtectionEnum saslQop : saslQops )
            {
                switch ( saslQop )
                {
                    case AUTH:
                        destinationSaslQops
                            .add( org.apache.directory.studio.apacheds.configuration.model.v154.SaslQualityOfProtectionEnum.AUTH );
                        break;
                    case AUTH_CONF:
                        destinationSaslQops
                            .add( org.apache.directory.studio.apacheds.configuration.model.v154.SaslQualityOfProtectionEnum.AUTH_CONF );
                        break;
                    case AUTH_INT:
                        destinationSaslQops
                            .add( org.apache.directory.studio.apacheds.configuration.model.v154.SaslQualityOfProtectionEnum.AUTH_INT );
                        break;
                }
            }

            return destinationSaslQops;
        }

        return null;
    }


    /**
     * Migrates a list of supported mechanisms from version 1.5.3 to version 1.5.4.
     *
     * @param supportedMechanisms
     *      a list of 1.5.3 supported mechanisms
     * @return
     *      the associated list of 1.5.4 supported mechanisms
     */
    private static List<org.apache.directory.studio.apacheds.configuration.model.v154.SupportedMechanismEnum> migrateSupportedMechanismsToVersion154(
        List<org.apache.directory.studio.apacheds.configuration.model.v153.SupportedMechanismEnum> supportedMechanisms )
    {
        if ( supportedMechanisms != null )
        {
            List<org.apache.directory.studio.apacheds.configuration.model.v154.SupportedMechanismEnum> destinationSupportedMechanisms = new ArrayList<org.apache.directory.studio.apacheds.configuration.model.v154.SupportedMechanismEnum>();
            for ( org.apache.directory.studio.apacheds.configuration.model.v153.SupportedMechanismEnum supportedMechanism : supportedMechanisms )
            {
                switch ( supportedMechanism )
                {
                    case CRAM_MD5:
                        destinationSupportedMechanisms
                            .add( org.apache.directory.studio.apacheds.configuration.model.v154.SupportedMechanismEnum.CRAM_MD5 );
                        break;
                    case DIGEST_MD5:
                        destinationSupportedMechanisms
                            .add( org.apache.directory.studio.apacheds.configuration.model.v154.SupportedMechanismEnum.DIGEST_MD5 );
                        break;
                    case GSS_SPNEGO:
                        destinationSupportedMechanisms
                            .add( org.apache.directory.studio.apacheds.configuration.model.v154.SupportedMechanismEnum.GSS_SPNEGO );
                        break;
                    case GSSAPI:
                        destinationSupportedMechanisms
                            .add( org.apache.directory.studio.apacheds.configuration.model.v154.SupportedMechanismEnum.GSSAPI );
                        break;
                    case NTLM:
                        destinationSupportedMechanisms
                            .add( org.apache.directory.studio.apacheds.configuration.model.v154.SupportedMechanismEnum.NTLM );
                        break;
                    case SIMPLE:
                        destinationSupportedMechanisms
                            .add( org.apache.directory.studio.apacheds.configuration.model.v154.SupportedMechanismEnum.SIMPLE );
                        break;
                }
            }

            return destinationSupportedMechanisms;
        }

        return null;
    }
}
