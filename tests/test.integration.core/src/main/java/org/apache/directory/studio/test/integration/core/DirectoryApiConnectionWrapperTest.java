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

package org.apache.directory.studio.test.integration.core;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.ConnectException;
import java.nio.channels.UnresolvedAddressException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.directory.ldap.client.api.exception.InvalidConnectionException;
import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.server.core.integ.FrameworkRunner;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.apache.directory.studio.connection.core.ConnectionParameter.AuthenticationMethod;
import org.apache.directory.studio.connection.core.ConnectionParameter.EncryptionMethod;
import org.apache.directory.studio.connection.core.ConnectionParameter.NetworkProvider;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;
import org.apache.directory.studio.connection.core.io.ConnectionWrapper;
import org.apache.directory.studio.connection.core.io.api.DirectoryApiConnectionWrapper;
import org.apache.directory.studio.ldapbrowser.core.jobs.InitializeRootDSERunnable;
import org.apache.directory.studio.ldapbrowser.core.model.impl.BrowserConnection;
import org.apache.mina.util.AvailablePortFinder;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests the {@link DirectoryApiConnectionWrapper}.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
@RunWith(FrameworkRunner.class)
@CreateLdapServer(transports =
    { @CreateTransport(protocol = "LDAP"), @CreateTransport(protocol = "LDAPS") })
public class DirectoryApiConnectionWrapperTest extends AbstractLdapTestUnit
{

    /**
     * Tests connecting to the server.
     */
    @Test
    public void testConnect()
    {
        StudioProgressMonitor monitor = getProgressMonitor();
        ConnectionParameter connectionParameter = new ConnectionParameter( null, "localhost", ldapServer.getPort(),
            EncryptionMethod.NONE, NetworkProvider.APACHE_DIRECTORY_LDAP_API, AuthenticationMethod.NONE, null, null,
            null, true, null );
        Connection connection = new Connection( connectionParameter );
        ConnectionWrapper connectionWrapper = connection.getConnectionWrapper();

        assertFalse( connectionWrapper.isConnected() );

        connectionWrapper.connect( monitor );
        assertTrue( connectionWrapper.isConnected() );
        assertNull( monitor.getException() );

        connectionWrapper.disconnect();
        assertFalse( connectionWrapper.isConnected() );

        // TODO: SSL, StartTLS
    }


    /**
     * Test failed connections to the server.
     */
    @Test
    public void testConnectFailures()
    {
        StudioProgressMonitor monitor = null;
        ConnectionParameter connectionParameter = null;
        Connection connection = null;
        ConnectionWrapper connectionWrapper = null;

        // invalid port
        monitor = getProgressMonitor();
        connectionParameter = new ConnectionParameter( null, "localhost", AvailablePortFinder.getNextAvailable(),
            EncryptionMethod.NONE, NetworkProvider.APACHE_DIRECTORY_LDAP_API, AuthenticationMethod.NONE, null, null,
            null, true, null );
        connection = new Connection( connectionParameter );
        connectionWrapper = connection.getConnectionWrapper();
        connectionWrapper.connect( monitor );
        assertFalse( connectionWrapper.isConnected() );
        assertNotNull( monitor.getException() );
        assertTrue( monitor.getException() instanceof InvalidConnectionException );
        assertNotNull( monitor.getException().getCause() );
        assertTrue( monitor.getException().getCause() instanceof ConnectException );

        // unknown host
        monitor = getProgressMonitor();
        connectionParameter = new ConnectionParameter( null, "555.555.555.555", ldapServer.getPort(),
            EncryptionMethod.NONE, NetworkProvider.APACHE_DIRECTORY_LDAP_API, AuthenticationMethod.NONE, null, null,
            null, true, null );
        connection = new Connection( connectionParameter );
        connectionWrapper = connection.getConnectionWrapper();
        connectionWrapper.connect( monitor );
        assertFalse( connectionWrapper.isConnected() );
        assertNotNull( monitor.getException() );
        assertTrue( monitor.getException() instanceof InvalidConnectionException );
        assertNotNull( monitor.getException().getCause() );
        assertTrue( monitor.getException().getCause() instanceof UnresolvedAddressException );

        // TODO: SSL, StartTLS
    }


    /**
     * Test binding to the server.
     */
    @Test
    public void testBind()
    {
        StudioProgressMonitor monitor = getProgressMonitor();
        ConnectionParameter connectionParameter = new ConnectionParameter( null, "localhost", ldapServer.getPort(),
            EncryptionMethod.NONE, NetworkProvider.APACHE_DIRECTORY_LDAP_API, AuthenticationMethod.SIMPLE,
            "uid=admin,ou=system", "secret", null, true, null );
        Connection connection = new Connection( connectionParameter );
        ConnectionWrapper connectionWrapper = connection.getConnectionWrapper();

        assertFalse( connectionWrapper.isConnected() );

        connectionWrapper.connect( monitor );
        connectionWrapper.bind( monitor );
        assertTrue( connectionWrapper.isConnected() );
        assertNull( monitor.getException() );

        connectionWrapper.unbind();
        connectionWrapper.disconnect();
        assertFalse( connectionWrapper.isConnected() );

    }


    /**
     * Test failed binds to the server.
     */
    @Test
    public void testBindFailures()
    {
        StudioProgressMonitor monitor = null;
        ConnectionParameter connectionParameter = null;
        Connection connection = null;
        ConnectionWrapper connectionWrapper = null;

        // simple auth without principal and credential
        monitor = getProgressMonitor();
        connectionParameter = new ConnectionParameter( null, "localhost", ldapServer.getPort(), EncryptionMethod.NONE,
            NetworkProvider.APACHE_DIRECTORY_LDAP_API, AuthenticationMethod.SIMPLE, "uid=admin", "invalid", null, true,
            null );
        connection = new Connection( connectionParameter );
        connectionWrapper = connection.getConnectionWrapper();
        connectionWrapper.connect( monitor );
        connectionWrapper.bind( monitor );
        assertFalse( connectionWrapper.isConnected() );
        assertNotNull( monitor.getException() );
        assertTrue( monitor.getException() instanceof Exception );
        assertTrue( monitor.getException().getMessage().contains( "error code 49 - INVALID_CREDENTIALS" ) );

        // simple auth with invalid principal and credential
        monitor = getProgressMonitor();
        connectionParameter = new ConnectionParameter( null, "localhost", ldapServer.getPort(), EncryptionMethod.NONE,
            NetworkProvider.APACHE_DIRECTORY_LDAP_API, AuthenticationMethod.SIMPLE, "uid=admin,ou=system", "bar", null,
            true, null );
        connection = new Connection( connectionParameter );
        connectionWrapper = connection.getConnectionWrapper();
        connectionWrapper.connect( monitor );
        connectionWrapper.bind( monitor );
        assertFalse( connectionWrapper.isConnected() );
        assertNotNull( monitor.getException() );
        assertTrue( monitor.getException() instanceof Exception );
        assertTrue( monitor.getException().getMessage().contains( "error code 49 - INVALID_CREDENTIALS" ) );
    }


    /**
     * Test searching.
     */
    @Test
    public void testSearch()
    {
        StudioProgressMonitor monitor = null;
        ConnectionParameter connectionParameter = null;
        Connection connection = null;
        ConnectionWrapper connectionWrapper = null;

        // simple auth without principal and credential
        monitor = getProgressMonitor();
        connectionParameter = new ConnectionParameter( null, "localhost", ldapServer.getPort(), EncryptionMethod.NONE,
            NetworkProvider.APACHE_DIRECTORY_LDAP_API, AuthenticationMethod.SIMPLE, "uid=admin,ou=system", "secret",
            null, true, null );
        connection = new Connection( connectionParameter );
        connectionWrapper = connection.getConnectionWrapper();
        connectionWrapper.connect( monitor );
        connectionWrapper.bind( monitor );
        assertTrue( connectionWrapper.isConnected() );
        assertNull( monitor.getException() );

        SearchControls searchControls = new SearchControls();
        NamingEnumeration<SearchResult> result = connectionWrapper.search( "ou=system", "(objectClass=*)",
            searchControls, AliasDereferencingMethod.NEVER, ReferralHandlingMethod.IGNORE, null, monitor, null );
        assertNotNull( result );
    }


    /**
     * Test initializing of Root DSE.
     */
    @Test
    public void testInitializeAttributesRunnable() throws Exception
    {
        StudioProgressMonitor monitor = getProgressMonitor();
        ConnectionParameter connectionParameter = new ConnectionParameter( null, "localhost", ldapServer.getPort(),
            EncryptionMethod.NONE, NetworkProvider.APACHE_DIRECTORY_LDAP_API, AuthenticationMethod.SIMPLE,
            "uid=admin,ou=system", "secret", null, true, null );
        Connection connection = new Connection( connectionParameter );
        BrowserConnection browserConnection = new BrowserConnection( connection );

        assertFalse( browserConnection.getRootDSE().isAttributesInitialized() );

        InitializeRootDSERunnable.loadRootDSE( browserConnection, monitor );

        assertTrue( browserConnection.getRootDSE().isAttributesInitialized() );
    }


    /**
     * DIRSTUDIO-1039
     */
    @Test
    public void testConcurrentUseAndCloseOfConnection() throws Exception
    {
        final StudioProgressMonitor monitor = getProgressMonitor();
        final ConnectionParameter connectionParameter = new ConnectionParameter( null, "localhost",
            ldapServer.getPort(), EncryptionMethod.NONE, NetworkProvider.APACHE_DIRECTORY_LDAP_API,
            AuthenticationMethod.SIMPLE, "uid=admin,ou=system", "secret", null, true, null );
        final Connection connection = new Connection( connectionParameter );
        final BrowserConnection browserConnection = new BrowserConnection( connection );

        ExecutorService pool = Executors.newFixedThreadPool( 2 );

        final AtomicLong closeCounter = new AtomicLong();
        final AtomicLong useCounter = new AtomicLong();

        // Thread that permanently closes the connection
        Callable<Void> closeCallable = new Callable<Void>()
        {
            @Override
            public Void call() throws Exception
            {
                ConnectionEventRegistry.suspendEventFiringInCurrentThread();

                while ( true )
                {
                    connection.getConnectionWrapper().unbind();
                    connection.getConnectionWrapper().disconnect();
                    closeCounter.incrementAndGet();
                    Thread.sleep( 10 );
                }
            }
        };
        Future<Void> closeFuture = pool.submit( closeCallable );

        // Thread that permanently uses the connection
        Callable<Void> useCallable = new Callable<Void>()
        {
            @Override
            public Void call() throws Exception
            {
                for ( int i = 0; i < 10; i++ )
                {
                    browserConnection.getRootDSE().setAttributesInitialized( false );
                    assertFalse( browserConnection.getRootDSE().isAttributesInitialized() );
                    InitializeRootDSERunnable.loadRootDSE( browserConnection, monitor );
                    assertTrue( browserConnection.getRootDSE().isAttributesInitialized() );
                    useCounter.incrementAndGet();
                }
                return null;
            }
        };
        Future<Void> useFuture = pool.submit( useCallable );

        // wait till useFuture is done
        useFuture.get( 60, TimeUnit.SECONDS );

        // assert counters, 
        assertEquals( 10, useCounter.get() );
        assertTrue( closeCounter.get() > 10 );

        // shutdown pool
        pool.shutdownNow();
        pool.awaitTermination( 60, TimeUnit.SECONDS );
        assertTrue( useFuture.isDone() );
        assertTrue( closeFuture.isDone() );
    }


    private StudioProgressMonitor getProgressMonitor()
    {
        StudioProgressMonitor monitor = new StudioProgressMonitor( new NullProgressMonitor() );
        return monitor;
    }

}
