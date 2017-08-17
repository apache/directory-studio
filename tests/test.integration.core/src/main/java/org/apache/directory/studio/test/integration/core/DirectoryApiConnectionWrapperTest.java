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


import static org.apache.directory.studio.test.integration.core.Constants.LOCALHOST;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.connection.core.ConnectionParameter.AuthenticationMethod;
import org.apache.directory.studio.connection.core.ConnectionParameter.EncryptionMethod;
import org.apache.directory.studio.connection.core.ConnectionParameter.NetworkProvider;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;
import org.apache.directory.studio.connection.core.io.api.DirectoryApiConnectionWrapper;
import org.apache.directory.studio.ldapbrowser.core.jobs.InitializeRootDSERunnable;
import org.apache.directory.studio.ldapbrowser.core.model.impl.BrowserConnection;
import org.junit.Test;


/**
 * Tests the {@link DirectoryApiConnectionWrapper}.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DirectoryApiConnectionWrapperTest extends ConnectionWrapperTestBase
{

    public DirectoryApiConnectionWrapperTest()
    {
        super( NetworkProvider.APACHE_DIRECTORY_LDAP_API );
    }


    // see tests in super class

    @Test
    public void testSearchContinuationFollowParent() throws NamingException
    {
        StudioProgressMonitor monitor = getProgressMonitor();
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope( SearchControls.SUBTREE_SCOPE );
        NamingEnumeration<SearchResult> result = getConnectionWrapper( monitor ).search( "ou=referrals,ou=system",
            "(objectClass=*)", searchControls, AliasDereferencingMethod.NEVER, ReferralHandlingMethod.FOLLOW, null,
            monitor, null );

        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        assertNotNull( result );

        List<String> dns = consume( result, sr -> sr.getNameInNamespace() );
        assertEquals( 4, dns.size() );
        assertThat( dns, hasItems( "ou=referrals,ou=system", "ou=referrals,ou=system", "ou=users,ou=system",
            "uid=user.1,ou=users,ou=system" ) );
    }


    /**
     * Test initializing of Root DSE.
     */
    @Test
    public void testInitializeAttributesRunnable() throws Exception
    {
        StudioProgressMonitor monitor = getProgressMonitor();
        ConnectionParameter connectionParameter = new ConnectionParameter( null, LOCALHOST, ldapServer.getPort(),
            EncryptionMethod.NONE, NetworkProvider.APACHE_DIRECTORY_LDAP_API, AuthenticationMethod.SIMPLE,
            "uid=admin,ou=system", "secret", null, true, null, 30L );
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
        final ConnectionParameter connectionParameter = new ConnectionParameter( null, LOCALHOST, ldapServer.getPort(),
            EncryptionMethod.NONE, NetworkProvider.APACHE_DIRECTORY_LDAP_API, AuthenticationMethod.SIMPLE,
            "uid=admin,ou=system", "secret", null, true, null, 30L );
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

}
