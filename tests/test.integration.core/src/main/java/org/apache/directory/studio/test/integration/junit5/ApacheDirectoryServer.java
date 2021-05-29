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

package org.apache.directory.studio.test.integration.junit5;


import static org.apache.directory.studio.test.integration.junit5.Constants.LOCALHOST;

import java.io.File;
import java.util.Collections;
import java.util.stream.Collectors;

import org.apache.directory.server.core.api.DirectoryService;
import org.apache.directory.server.core.api.partition.Partition;
import org.apache.directory.server.core.factory.DefaultDirectoryServiceFactory;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.ldap.handlers.extended.PwdModifyHandler;
import org.apache.directory.server.ldap.handlers.extended.StartTlsHandler;
import org.apache.directory.server.ldap.handlers.extended.WhoAmIHandler;
import org.apache.directory.server.ldap.handlers.sasl.SimpleMechanismHandler;
import org.apache.directory.server.ldap.handlers.sasl.cramMD5.CramMd5MechanismHandler;
import org.apache.directory.server.ldap.handlers.sasl.digestMD5.DigestMd5MechanismHandler;
import org.apache.directory.server.protocol.shared.transport.TcpTransport;
import org.apache.directory.server.protocol.shared.transport.Transport;
import org.apache.mina.util.AvailablePortFinder;


/**
 * An ApacheDS implementation of a test LDAP server.
 * 
 * This implementation starts an embedded ApacheDS and adds a partition dc=example,dc=org.
 */
public class ApacheDirectoryServer extends TestLdapServer
{

    private static ApacheDirectoryServer instance;

    private DirectoryService service;
    private LdapServer server;
    private String defaultKeyStoreFile;

    public static synchronized ApacheDirectoryServer getInstance()
    {
        if ( instance == null )
        {
            int port = AvailablePortFinder.getNextAvailable( 1024 );
            int portSSL = AvailablePortFinder.getNextAvailable( port + 1 );
            instance = new ApacheDirectoryServer( port, portSSL );
            instance.startServer();
        }
        return instance;
    }


    private void startServer()
    {
        try
        {
            DefaultDirectoryServiceFactory factory = new DefaultDirectoryServiceFactory();
            factory.init( "test" );
            service = factory.getDirectoryService();
            Partition partition = factory.getPartitionFactory().createPartition( service.getSchemaManager(),
                service.getDnFactory(), "example.org", "dc=example,dc=org", 1024,
                new File( service.getInstanceLayout().getPartitionsDirectory(), "example.org" ) );
            partition.initialize();
            service.addPartition( partition );
            service.getSchemaManager().enable( "nis", "krb5kdc" );
            service.getInterceptor( "passwordHashingInterceptor" );
            service.setInterceptors( service.getInterceptors().stream()
                .filter( i -> !i.getName().equals( "ConfigurableHashingInterceptor" ) )
                .collect( Collectors.toList() ) );
            System.out.println( service.getInterceptors() );

            server = new LdapServer();
            server.setDirectoryService( service );
            Transport ldap = new TcpTransport( port );
            server.addTransports( ldap );
            Transport ldaps = new TcpTransport( portSSL );
            ldaps.setEnableSSL( true );
            server.addTransports( ldaps );

            server.addSaslMechanismHandler( "SIMPLE", new SimpleMechanismHandler() );
            server.addSaslMechanismHandler( "DIGEST-MD5", new DigestMd5MechanismHandler() );
            server.setSaslRealms( Collections.singletonList( "EXAMPLE.ORG" ) );
            server.setSaslHost( getHost() );
            server.setSearchBaseDn( TestFixture.CONTEXT_DN.getName() );

            server.addExtendedOperationHandler( new StartTlsHandler() );
            server.addExtendedOperationHandler( new PwdModifyHandler() );
            server.addExtendedOperationHandler( new WhoAmIHandler() );

            defaultKeyStoreFile = CertificateUtil.createTempKeyStore( "testStore", "changeit".toCharArray() )
                .getAbsolutePath();
            server.setKeystoreFile( defaultKeyStoreFile );
            server.setCertificatePassword( "changeit" );

            server.start();
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }


    @Override
    public void prepare()
    {
        super.prepare();

        try
        {
            if ( !defaultKeyStoreFile.equals( server.getKeystoreFile() ) )
            {
                server.setKeystoreFile( defaultKeyStoreFile );
                server.reloadSslContext();
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }


    public void setKeystore( String keystorePath ) throws Exception
    {
        server.setKeystoreFile( keystorePath );
        server.reloadSslContext();
    }


    public DirectoryService getService()
    {
        return service;
    }


    private ApacheDirectoryServer( int port, int portSSL )
    {
        super( LdapServerType.ApacheDS, LOCALHOST, port, portSSL, "uid=admin,ou=system", "secret" );
    }

}
