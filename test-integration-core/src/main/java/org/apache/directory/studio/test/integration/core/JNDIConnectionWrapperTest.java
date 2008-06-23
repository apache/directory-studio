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


import java.net.ConnectException;
import java.net.UnknownHostException;

import javax.naming.AuthenticationException;
import javax.naming.CommunicationException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.directory.server.unit.AbstractServerTest;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.apache.directory.studio.connection.core.Credentials;
import org.apache.directory.studio.connection.core.IAuthHandler;
import org.apache.directory.studio.connection.core.ICredentials;
import org.apache.directory.studio.connection.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.connection.core.ConnectionParameter.AuthenticationMethod;
import org.apache.directory.studio.connection.core.ConnectionParameter.EncryptionMethod;
import org.apache.directory.studio.connection.core.io.jndi.JNDIConnectionWrapper;
import org.eclipse.core.runtime.NullProgressMonitor;


/**
 * Tests the {@link JNDIConnectionWrapper}.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class JNDIConnectionWrapperTest extends AbstractServerTest
{
    /**
     * Initialize the server.
     */
    public void setUp() throws Exception
    {
        super.setUp();
    }


    /**
     * Tests connecting to the server.
     */
    public void testConnect()
    {
        StudioProgressMonitor monitor = getProgressMonitor();
        ConnectionParameter connectionParameter = new ConnectionParameter( null, "localhost", ldapServer.getIpPort(),
            EncryptionMethod.NONE, AuthenticationMethod.NONE, null, null, null, true, null );
        Connection connection = new Connection( connectionParameter );
        JNDIConnectionWrapper connectionWrapper = connection.getJNDIConnectionWrapper();

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
    public void testConnectFailures()
    {
        StudioProgressMonitor monitor = null;
        ConnectionParameter connectionParameter = null;
        Connection connection = null;
        JNDIConnectionWrapper connectionWrapper = null;

        // invalid port
        monitor = getProgressMonitor();
        connectionParameter = new ConnectionParameter( null, "localhost", ldapServer.getIpPort() + 1,
            EncryptionMethod.NONE, AuthenticationMethod.NONE, null, null, null, true, null );
        connection = new Connection( connectionParameter );
        connectionWrapper = connection.getJNDIConnectionWrapper();
        connectionWrapper.connect( monitor );
        assertFalse( connectionWrapper.isConnected() );
        assertNotNull( monitor.getException() );
        assertTrue( monitor.getException() instanceof CommunicationException );
        assertNotNull( monitor.getException().getCause() );
        assertTrue( monitor.getException().getCause() instanceof ConnectException );

        // unknown host
        monitor = getProgressMonitor();
        connectionParameter = new ConnectionParameter( null, "555.555.555.555", ldapServer.getIpPort(),
            EncryptionMethod.NONE, AuthenticationMethod.NONE, null, null, null, true, null );
        connection = new Connection( connectionParameter );
        connectionWrapper = connection.getJNDIConnectionWrapper();
        connectionWrapper.connect( monitor );
        assertFalse( connectionWrapper.isConnected() );
        assertNotNull( monitor.getException() );
        assertTrue( monitor.getException() instanceof CommunicationException );
        assertNotNull( monitor.getException().getCause() );
        assertTrue( monitor.getException().getCause() instanceof UnknownHostException );

        // TODO: SSL, StartTLS
    }


    /**
     * Test binding to the server.
     */
    public void testBind()
    {
        StudioProgressMonitor monitor = getProgressMonitor();
        ConnectionParameter connectionParameter = new ConnectionParameter( null, "localhost", ldapServer.getIpPort(),
            EncryptionMethod.NONE, AuthenticationMethod.SIMPLE, "uid=admin,ou=system", "secret", null, true, null );
        Connection connection = new Connection( connectionParameter );
        JNDIConnectionWrapper connectionWrapper = connection.getJNDIConnectionWrapper();
        IAuthHandler authHandler = getAuthHandler();
        ConnectionCorePlugin.getDefault().setAuthHandler( authHandler );

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
    public void testBindFailures()
    {
        StudioProgressMonitor monitor = null;
        ConnectionParameter connectionParameter = null;
        Connection connection = null;
        JNDIConnectionWrapper connectionWrapper = null;

        // simple auth without principal and credential
        monitor = getProgressMonitor();
        connectionParameter = new ConnectionParameter( null, "localhost", ldapServer.getIpPort(),
            EncryptionMethod.NONE, AuthenticationMethod.SIMPLE, null, null, null, true, null );
        connection = new Connection( connectionParameter );
        connectionWrapper = connection.getJNDIConnectionWrapper();
        connectionWrapper.connect( monitor );
        connectionWrapper.bind( monitor );
        assertFalse( connectionWrapper.isConnected() );
        assertNotNull( monitor.getException() );
        assertTrue( monitor.getException() instanceof NamingException );

        // simple auth with invalid principal and credential
        monitor = getProgressMonitor();
        connectionParameter = new ConnectionParameter( null, "localhost", ldapServer.getIpPort(),
            EncryptionMethod.NONE, AuthenticationMethod.SIMPLE, "uid=admin,ou=system", "bar", null, true, null );
        connection = new Connection( connectionParameter );
        connectionWrapper = connection.getJNDIConnectionWrapper();
        connectionWrapper.connect( monitor );
        connectionWrapper.bind( monitor );
        assertFalse( connectionWrapper.isConnected() );
        assertNotNull( monitor.getException() );
        assertTrue( monitor.getException() instanceof AuthenticationException );
    }


    /**
     * Test searching.
     */
    public void testSearch()
    {
        StudioProgressMonitor monitor = null;
        ConnectionParameter connectionParameter = null;
        Connection connection = null;
        JNDIConnectionWrapper connectionWrapper = null;

        // simple auth without principal and credential
        monitor = getProgressMonitor();
        connectionParameter = new ConnectionParameter( null, "localhost", ldapServer.getIpPort(),
            EncryptionMethod.NONE, AuthenticationMethod.SIMPLE, "uid=admin,ou=system", "secret", null, true, null );
        connection = new Connection( connectionParameter );
        connectionWrapper = connection.getJNDIConnectionWrapper();
        connectionWrapper.connect( monitor );
        connectionWrapper.bind( monitor );
        assertTrue( connectionWrapper.isConnected() );
        assertNull( monitor.getException() );

        SearchControls searchControls = new SearchControls();
        NamingEnumeration<SearchResult> result = connectionWrapper.search( "ou=system", "objectClass=*",
            searchControls, AliasDereferencingMethod.NEVER, ReferralHandlingMethod.IGNORE, null, monitor, null );
        assertNotNull( result );
    }


    /**
     * Shutdown the server.
     */
    public void tearDown() throws Exception
    {
        super.tearDown();
    }


    private StudioProgressMonitor getProgressMonitor()
    {
        StudioProgressMonitor monitor = new StudioProgressMonitor( new NullProgressMonitor() );
        return monitor;
    }


    private IAuthHandler getAuthHandler()
    {
        IAuthHandler authHandler = new IAuthHandler()
        {
            public ICredentials getCredentials( ConnectionParameter connectionParameter )
            {
                return new Credentials( connectionParameter.getBindPrincipal(), connectionParameter.getBindPassword(),
                    connectionParameter );
            }
        };
        return authHandler;
    }

}
