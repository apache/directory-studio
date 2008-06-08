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


import org.apache.directory.server.unit.AbstractServerTest;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.apache.directory.studio.connection.core.Credentials;
import org.apache.directory.studio.connection.core.IAuthHandler;
import org.apache.directory.studio.connection.core.ICredentials;
import org.apache.directory.studio.connection.core.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.ConnectionParameter.AuthenticationMethod;
import org.apache.directory.studio.connection.core.ConnectionParameter.EncryptionMethod;
import org.apache.directory.studio.connection.core.io.jndi.JNDIConnectionWrapper;
import org.eclipse.core.runtime.NullProgressMonitor;


/**
 * 
 * TODO JNDIConnectionWrapperTest.
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
     * Connects to the server.
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
    }


    /**
     * Binds to the server.
     */
    public void testBind()
    {
        StudioProgressMonitor monitor = getProgressMonitor();
        ConnectionParameter connectionParameter = new ConnectionParameter( null, "localhost", ldapServer.getIpPort(),
            EncryptionMethod.NONE, AuthenticationMethod.SIMPLE, "uid=admin,ou=system", "secret", null, true, null );
        Connection connection = new Connection( connectionParameter );
        JNDIConnectionWrapper connectionWrapper = connection.getJNDIConnectionWrapper();
        IAuthHandler authHandler = new IAuthHandler()
        {
            public ICredentials getCredentials( ConnectionParameter connectionParameter )
            {
                return new Credentials( connectionParameter.getBindPrincipal(), connectionParameter.getBindPassword(),
                    connectionParameter );
            }
        };
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

}
