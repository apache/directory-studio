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


import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.directory.server.configuration.MutableServerStartupConfiguration;
import org.apache.directory.server.core.configuration.MutableInterceptorConfiguration;
import org.apache.directory.server.core.configuration.PartitionConfiguration;
import org.apache.directory.server.core.partition.impl.btree.MutableIndexConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;


/**
 * This class represents the Server Configuration Parser. It can be used to parse a 'server.xml' file 
 * and get Server Configuration Object from it.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ServerConfigurationParser
{
    /**
     * Parses a 'server.xml' file located at the given path and returns 
     * the corresponding ServerConfiguration Object.
     *
     * @param path
     *      the path of the file to parse
     * @return
     *      the corresponding ServerConfiguration Object
     * @throws ServerConfigurationParserException
     *      if an error occurrs when reading the Server Configuration file
     */
    public ServerConfiguration parse( String path ) throws ServerConfigurationParserException
    {
        try
        {
            ApplicationContext ac = new FileSystemXmlApplicationContext( "file:" + path );
            MutableServerStartupConfiguration config = ( MutableServerStartupConfiguration ) ac
                .getBean( "configuration" );
            Properties environment = ( Properties ) ac.getBean( "environment" );

            ServerConfiguration serverConfiguration = new ServerConfiguration();
            serverConfiguration.setPath( path );
            serverConfiguration.setAllowAnonymousAccess( config.isAllowAnonymousAccess() );
            serverConfiguration.setEnableAccessControl( config.isAccessControlEnabled() );
            serverConfiguration.setEnableChangePassword( config.isEnableChangePassword() );
            serverConfiguration.setEnableKerberos( config.isEnableKerberos() );
            serverConfiguration.setEnableNTP( config.isEnableNtp() );
            serverConfiguration.setMaxSizeLimit( config.getMaxSizeLimit() );
            serverConfiguration.setMaxThreads( config.getMaxThreads() );
            serverConfiguration.setMaxTimeLimit( config.getMaxTimeLimit() );
            serverConfiguration.setPassword( environment.getProperty( "java.naming.security.credentials", "" ) );
            serverConfiguration.setPort( config.getLdapPort() );
            serverConfiguration.setPrincipal( environment.getProperty( "java.naming.security.principal", "" ) );
            serverConfiguration.setSynchronizationPeriod( config.getSynchPeriodMillis() );

            // System Partition
            Partition systemPartition = createPartition( config.getSystemPartitionConfiguration(), true );
            serverConfiguration.addPartition( systemPartition );

            // Other Partitions
            Set<PartitionConfiguration> partitionConfigurations = config.getPartitionConfigurations();
            for ( PartitionConfiguration partitionConfiguration : partitionConfigurations )
            {
                Partition partition = createPartition( partitionConfiguration, false );
                serverConfiguration.addPartition( partition );
            }

            // Interceptors
            List interceptorConfigurations = config.getInterceptorConfigurations();
            for ( Object interceptorConfiguration : interceptorConfigurations )
            {
                MutableInterceptorConfiguration interceptor = ( MutableInterceptorConfiguration ) interceptorConfiguration;
                Interceptor newInterceptor = new Interceptor( interceptor.getName() );
                newInterceptor.setClassType( interceptor.getInterceptor().getClass().getName() );
                serverConfiguration.addInterceptor( newInterceptor );
            }

            // Extended Operations
            Collection extendedOperationHandlers = config.getExtendedOperationHandlers();
            for ( Object extendedOperationHandler : extendedOperationHandlers )
            {
                ExtendedOperation extendedOperation = new ExtendedOperation( extendedOperationHandler.getClass()
                    .getName() );
                serverConfiguration.addExtendedOperation( extendedOperation );
            }

            return serverConfiguration;

        }
        catch ( Exception e )
        {
            ServerConfigurationParserException exception = new ServerConfigurationParserException( e.getMessage(), e
                .getCause() );
            exception.setStackTrace( e.getStackTrace() );
            throw exception;
        }
    }


    /**
     * Creates a Partition from the given Partition Configuration.
     *
     * @param partitionConfiguration
     *      the Partition Configuration
     * @param isSystemPartition
     *      a flag to indicate if the created partition must be the system partition
     * @return
     *      the corresponding Partition
     * @throws Exception
     *      if an error occurrs
     */
    private Partition createPartition( PartitionConfiguration partitionConfiguration, boolean isSystemPartition )
        throws Exception
    {
        Partition partition = new Partition();
        partition.setSystemPartition( isSystemPartition );
        partition.setName( partitionConfiguration.getName() );
        partition.setCacheSize( partitionConfiguration.getCacheSize() );
        partition.setContextEntry( partitionConfiguration.getContextEntry() );
        partition.setSuffix( partitionConfiguration.getSuffix() );

        for ( Object indexedAt : partitionConfiguration.getIndexedAttributes() )
        {
            MutableIndexConfiguration indexConfiguration = ( MutableIndexConfiguration ) indexedAt;
            IndexedAttribute indexedAttribute = new IndexedAttribute( indexConfiguration.getAttributeId(),
                indexConfiguration.getCacheSize() );
            partition.addIndexedAttribute( indexedAttribute );
        }

        return partition;
    }
}
