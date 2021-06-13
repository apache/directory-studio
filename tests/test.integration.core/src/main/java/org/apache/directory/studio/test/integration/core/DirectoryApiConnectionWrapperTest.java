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


import static org.apache.directory.studio.test.integration.junit5.TestFixture.CONTEXT_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.MISC_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.REFERRALS_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.REFERRAL_LOOP_1_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.REFERRAL_LOOP_2_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.REFERRAL_TO_REFERRALS_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.REFERRAL_TO_REFERRAL_TO_USERS_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.REFERRAL_TO_USERS_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.USER1_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.USER8_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.USERS_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.dn;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.ConnectException;
import java.nio.channels.UnresolvedAddressException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import javax.naming.directory.SearchControls;

import org.apache.directory.api.ldap.codec.api.LdapApiService;
import org.apache.directory.api.ldap.codec.api.LdapApiServiceFactory;
//import org.apache.directory.api.ldap.extras.extended.endTransaction.EndTransactionRequest;
//import org.apache.directory.api.ldap.extras.extended.endTransaction.EndTransactionResponse;
import org.apache.directory.api.ldap.extras.extended.pwdModify.PasswordModifyRequest;
//import org.apache.directory.api.ldap.extras.extended.startTransaction.StartTransactionRequest;
//import org.apache.directory.api.ldap.extras.extended.startTransaction.StartTransactionResponse;
import org.apache.directory.api.ldap.extras.extended.whoAmI.WhoAmIRequest;
import org.apache.directory.api.ldap.extras.extended.whoAmI.WhoAmIResponse;
import org.apache.directory.api.ldap.model.constants.SaslQoP;
import org.apache.directory.api.ldap.model.constants.SaslSecurityStrength;
import org.apache.directory.api.ldap.model.entry.DefaultAttribute;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.DefaultModification;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Modification;
import org.apache.directory.api.ldap.model.entry.ModificationOperation;
import org.apache.directory.api.ldap.model.exception.LdapAuthenticationException;
import org.apache.directory.api.ldap.model.exception.LdapAuthenticationNotSupportedException;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapLoopDetectedException;
import org.apache.directory.api.ldap.model.message.ExtendedResponse;
import org.apache.directory.api.ldap.model.message.ResultCodeEnum;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.util.Strings;
import org.apache.directory.ldap.client.api.exception.InvalidConnectionException;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.apache.directory.studio.connection.core.ConnectionParameter.AuthenticationMethod;
import org.apache.directory.studio.connection.core.ConnectionParameter.EncryptionMethod;
import org.apache.directory.studio.connection.core.ConnectionParameter.Krb5CredentialConfiguration;
import org.apache.directory.studio.connection.core.ICertificateHandler.TrustLevel;
import org.apache.directory.studio.connection.core.IReferralHandler;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;
import org.apache.directory.studio.connection.core.io.ConnectionWrapper;
import org.apache.directory.studio.connection.core.io.StudioLdapException;
import org.apache.directory.studio.connection.core.io.api.DirectoryApiConnectionWrapper;
import org.apache.directory.studio.connection.core.io.api.StudioSearchResult;
import org.apache.directory.studio.connection.core.io.api.StudioSearchResultEnumeration;
import org.apache.directory.studio.ldapbrowser.core.jobs.InitializeRootDSERunnable;
import org.apache.directory.studio.ldapbrowser.core.model.impl.BrowserConnection;
import org.apache.directory.studio.test.integration.junit5.LdapServerType;
import org.apache.directory.studio.test.integration.junit5.LdapServersSource;
import org.apache.directory.studio.test.integration.junit5.LdapServersSource.Mode;
import org.apache.directory.studio.test.integration.junit5.SkipTestIfLdapServerIsNotAvailableInterceptor;
import org.apache.directory.studio.test.integration.junit5.TestFixture;
import org.apache.directory.studio.test.integration.junit5.TestLdapServer;
import org.apache.mina.util.AvailablePortFinder;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;


/**
 * Tests the {@link DirectoryApiConnectionWrapper}.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
@ExtendWith(SkipTestIfLdapServerIsNotAvailableInterceptor.class)
public class DirectoryApiConnectionWrapperTest
{

    protected ConnectionWrapper connectionWrapper;

    @BeforeAll
    public static void suspendEventFiringInCurrentThread()
    {
        ConnectionEventRegistry.suspendEventFiringInCurrentThread();
        ConnectionCorePlugin.getDefault().setCertificateHandler( null );
    }


    @AfterAll
    public static void resumeEventFiringInCurrentThread()
    {
        ConnectionEventRegistry.resumeEventFiringInCurrentThread();
    }


    @AfterEach
    public void tearDown() throws Exception
    {
        if ( connectionWrapper != null )
        {
            connectionWrapper.disconnect();
        }
    }


    /**
     * Tests connecting to the server without encryption.
     */
    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testConnectPlain( TestLdapServer ldapServer )
    {
        StudioProgressMonitor monitor = getProgressMonitor();
        getConnection( monitor, ldapServer, null, null );

        assertFalse( connectionWrapper.isConnected() );

        X509Certificate[] certificates = connectionWrapper.connect( monitor );
        assertTrue( connectionWrapper.isConnected() );
        assertFalse( connectionWrapper.isSecured() );
        assertNull( monitor.getException() );
        assertNull( certificates );

        connectionWrapper.disconnect();
        assertFalse( connectionWrapper.isConnected() );
    }


    /**
     * Tests connecting to the server using ldaps:// encryption.
     */
    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testConnectLdaps( TestLdapServer ldapServer )
    {
        StudioProgressMonitor monitor = getProgressMonitor();
        Connection connection = getConnection( monitor, ldapServer, null, null );
        connection.setPort( ldapServer.getPortSSL() );
        connection.setEncryptionMethod( EncryptionMethod.LDAPS );
        acceptAllCertificates();

        assertFalse( connectionWrapper.isConnected() );

        X509Certificate[] certificates = connectionWrapper.connect( monitor );
        assertTrue( connectionWrapper.isConnected() );
        assertTrue( connectionWrapper.isSecured() );
        assertNull( monitor.getException() );
        assertNotNull( certificates );

        connectionWrapper.disconnect();
        assertFalse( connectionWrapper.isConnected() );
    }


    /**
     * Tests connecting to the server using StartTLS encryption.
     */
    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testConnectStartTls( TestLdapServer ldapServer )
    {
        StudioProgressMonitor monitor = getProgressMonitor();
        Connection connection = getConnection( monitor, ldapServer, null, null );
        connection.setEncryptionMethod( EncryptionMethod.START_TLS );
        acceptAllCertificates();

        assertFalse( connectionWrapper.isConnected() );

        X509Certificate[] certificates = connectionWrapper.connect( monitor );
        assertTrue( connectionWrapper.isConnected() );
        assertTrue( connectionWrapper.isSecured() );
        assertNull( monitor.getException() );
        assertNotNull( certificates );

        connectionWrapper.disconnect();
        assertFalse( connectionWrapper.isConnected() );
    }


    /**
     * Test failed connections to the server.
     */
    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testConnectFailures( TestLdapServer ldapServer )
    {
        StudioProgressMonitor monitor = null;
        Connection connection = null;

        // invalid port
        monitor = getProgressMonitor();
        connection = getConnection( monitor, ldapServer, null, null );
        connection.setPort( AvailablePortFinder.getNextAvailable() );
        connectionWrapper.connect( monitor );
        assertFalse( connectionWrapper.isConnected() );
        assertNotNull( monitor.getException() );
        assertTrue( monitor.getException() instanceof StudioLdapException );
        assertNotNull( monitor.getException().getCause() );
        assertTrue( monitor.getException().getCause() instanceof InvalidConnectionException );
        assertNotNull( monitor.getException().getCause().getCause() );
        assertTrue( monitor.getException().getCause().getCause() instanceof ConnectException );

        // unknown host
        monitor = getProgressMonitor();
        connection = getConnection( monitor, ldapServer, null, null );
        connection.setHost( "555.555.555.555" );
        connectionWrapper.connect( monitor );
        assertFalse( connectionWrapper.isConnected() );
        assertNotNull( monitor.getException() );
        assertTrue( monitor.getException() instanceof StudioLdapException );
        assertNotNull( monitor.getException().getCause() );
        assertTrue( monitor.getException().getCause() instanceof InvalidConnectionException );
        assertNotNull( monitor.getException().getCause().getCause() );
        assertTrue( monitor.getException().getCause().getCause() instanceof UnresolvedAddressException );
    }


    /**
     * Test binding to the server using simple auth and no encryption.
     */
    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testSimpleBindPlain( TestLdapServer ldapServer )
    {
        ldapServer.setConfidentialityRequired( false );
        StudioProgressMonitor monitor = getProgressMonitor();
        getConnection( monitor, ldapServer, ldapServer.getAdminDn(), ldapServer.getAdminPassword() );

        assertFalse( connectionWrapper.isConnected() );

        connectionWrapper.connect( monitor );
        connectionWrapper.bind( monitor );
        assertTrue( connectionWrapper.isConnected() );
        assertFalse( connectionWrapper.isSecured() );
        assertNull( monitor.getException() );

        connectionWrapper.unbind();
        connectionWrapper.disconnect();
        assertFalse( connectionWrapper.isConnected() );
    }


    /**
     * Test binding to the server using simple auth and no encryption should fail if the server requires confidentially.
     */
    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testSimpleBindPlainConfidentiallyRequired( TestLdapServer ldapServer )
    {
        ldapServer.setConfidentialityRequired( true );
        StudioProgressMonitor monitor = getProgressMonitor();
        getConnection( monitor, ldapServer, ldapServer.getAdminDn(), ldapServer.getAdminPassword() );

        assertFalse( connectionWrapper.isConnected() );

        connectionWrapper.connect( monitor );
        connectionWrapper.bind( monitor );

        assertFalse( connectionWrapper.isConnected() );
        assertFalse( connectionWrapper.isSecured() );
        assertNotNull( monitor.getException() );
        assertTrue( monitor.getException() instanceof StudioLdapException );
        assertTrue( monitor.getException().getMessage().contains( "[LDAP result code 13 - confidentialityRequired]" ) );
        assertNotNull( monitor.getException().getCause() );
        assertTrue( monitor.getException().getCause() instanceof LdapAuthenticationNotSupportedException );
    }


    /**
     * Test binding to the server using simple auth and ldaps:// encryption.
     */
    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testSimpleBindLdaps( TestLdapServer ldapServer )
    {
        ldapServer.setConfidentialityRequired( true );
        StudioProgressMonitor monitor = getProgressMonitor();
        Connection connection = getConnection( monitor, ldapServer, ldapServer.getAdminDn(),
            ldapServer.getAdminPassword() );
        connection.setPort( ldapServer.getPortSSL() );
        connection.setEncryptionMethod( EncryptionMethod.LDAPS );
        acceptAllCertificates();

        assertFalse( connectionWrapper.isConnected() );

        connectionWrapper.connect( monitor );
        connectionWrapper.bind( monitor );
        assertTrue( connectionWrapper.isConnected() );
        assertTrue( connectionWrapper.isSecured() );
        assertNull( monitor.getException() );

        connectionWrapper.unbind();
        connectionWrapper.disconnect();
        assertFalse( connectionWrapper.isConnected() );
    }


    /**
     * Test binding to the server using simple auth and StartTLS encryption.
     */
    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testSimpleBindStartTls( TestLdapServer ldapServer )
    {
        ldapServer.setConfidentialityRequired( true );
        StudioProgressMonitor monitor = getProgressMonitor();
        Connection connection = getConnection( monitor, ldapServer, ldapServer.getAdminDn(),
            ldapServer.getAdminPassword() );
        connection.setEncryptionMethod( EncryptionMethod.START_TLS );
        acceptAllCertificates();

        assertFalse( connectionWrapper.isConnected() );

        connectionWrapper.connect( monitor );
        connectionWrapper.bind( monitor );
        assertTrue( connectionWrapper.isConnected() );
        assertTrue( connectionWrapper.isSecured() );
        assertNull( monitor.getException() );

        connectionWrapper.unbind();
        connectionWrapper.disconnect();
        assertFalse( connectionWrapper.isConnected() );
    }


    /**
     * Test binding to the server using SASL auth and no encryption.
     */
    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testSaslDigestMd5BindAuthPlain( TestLdapServer ldapServer )
    {
        testSaslDigestMd5BindPlain( ldapServer, SaslQoP.AUTH );
    }


    /**
     * Test binding to the server using SASL auth-int and no encryption.
     */
    @ParameterizedTest
    @LdapServersSource(mode = Mode.All, except = LdapServerType.Fedora389ds)
    public void testSaslDigestMd5BindAuthIntPlain( TestLdapServer ldapServer )
    {
        testSaslDigestMd5BindPlain( ldapServer, SaslQoP.AUTH_INT );
    }


    /**
     * Test binding to the server using SASL auth-conf and no encryption.
     */
    @ParameterizedTest
    @LdapServersSource(mode = Mode.All, except = LdapServerType.Fedora389ds)
    public void testSaslDigestMd5BindAuthConfPlain( TestLdapServer ldapServer )
    {
        testSaslDigestMd5BindPlain( ldapServer, SaslQoP.AUTH_CONF );
    }


    private void testSaslDigestMd5BindPlain( TestLdapServer ldapServer, SaslQoP saslQoP )
    {
        ldapServer.setConfidentialityRequired( false );
        StudioProgressMonitor monitor = getProgressMonitor();
        Connection connection = getConnection( monitor, ldapServer, "user.1", "password" );
        connection.setAuthMethod( AuthenticationMethod.SASL_DIGEST_MD5 );
        connection.getConnectionParameter().setSaslQop( SaslQoP.AUTH_CONF );

        assertFalse( connectionWrapper.isConnected() );

        connectionWrapper.connect( monitor );
        connectionWrapper.bind( monitor );

        assertTrue( connectionWrapper.isConnected() );
        assertFalse( connectionWrapper.isSecured() );
        assertNull( monitor.getException() );

        connectionWrapper.unbind();
        connectionWrapper.disconnect();
        assertFalse( connectionWrapper.isConnected() );
    }


    /**
     * Test binding to the server using SASL and no encryption should fail if the server requires confidentially.
     */
    @ParameterizedTest
    @LdapServersSource(mode = Mode.All, except = LdapServerType.Fedora389ds)
    public void testSaslBindPlainConfidentiallyRequired( TestLdapServer ldapServer )
    {
        ldapServer.setConfidentialityRequired( true );
        StudioProgressMonitor monitor = getProgressMonitor();
        Connection connection = getConnection( monitor, ldapServer, "user.1", "password" );
        connection.setAuthMethod( AuthenticationMethod.SASL_DIGEST_MD5 );

        assertFalse( connectionWrapper.isConnected() );

        connectionWrapper.connect( monitor );
        connectionWrapper.bind( monitor );

        assertFalse( connectionWrapper.isConnected() );
        assertFalse( connectionWrapper.isSecured() );
        assertNotNull( monitor.getException() );
        assertTrue( monitor.getException() instanceof StudioLdapException );
        assertTrue( monitor.getException().getMessage().contains( "[LDAP result code 13 - confidentialityRequired]" ) );
        assertNotNull( monitor.getException().getCause() );
        assertTrue( monitor.getException().getCause() instanceof LdapAuthenticationNotSupportedException );
    }


    /**
     * Test binding to the server using SASL auth and ldaps:// encryption.
     */
    @ParameterizedTest
    @LdapServersSource(mode = Mode.All, except = LdapServerType.Fedora389ds)
    public void testSaslDigestMd5BindAuthLdaps( TestLdapServer ldapServer )
    {
        testSaslDigestMd5BindLdaps( ldapServer, SaslQoP.AUTH );
    }


    /**
     * Test binding to the server using SASL auth-int and ldaps:// encryption.
     */
    @ParameterizedTest
    @LdapServersSource(mode = Mode.All, except = LdapServerType.Fedora389ds)
    public void testSaslDigestMd5BindAuthIntLdaps( TestLdapServer ldapServer )
    {
        testSaslDigestMd5BindLdaps( ldapServer, SaslQoP.AUTH_INT );
    }


    /**
     * Test binding to the server using SASL auth-conf and ldaps:// encryption.
     */
    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testSaslDigestMd5BindAuthConfLdaps( TestLdapServer ldapServer )
    {
        testSaslDigestMd5BindLdaps( ldapServer, SaslQoP.AUTH_CONF );
    }


    private void testSaslDigestMd5BindLdaps( TestLdapServer ldapServer, SaslQoP saslQoP )
    {
        ldapServer.setConfidentialityRequired( true );
        StudioProgressMonitor monitor = getProgressMonitor();
        Connection connection = getConnection( monitor, ldapServer, "user.1", "password" );
        connection.setPort( ldapServer.getPortSSL() );
        connection.setEncryptionMethod( EncryptionMethod.LDAPS );
        connection.setAuthMethod( AuthenticationMethod.SASL_DIGEST_MD5 );
        connection.getConnectionParameter().setSaslQop( saslQoP );
        acceptAllCertificates();

        assertFalse( connectionWrapper.isConnected() );

        connectionWrapper.connect( monitor );
        connectionWrapper.bind( monitor );

        assertTrue( connectionWrapper.isConnected() );
        assertTrue( connectionWrapper.isSecured() );
        assertNull( monitor.getException() );

        connectionWrapper.unbind();
        connectionWrapper.disconnect();
        assertFalse( connectionWrapper.isConnected() );
    }


    /**
     * Test binding to the server using SASL auth and StartTLS encryption.
     */
    @ParameterizedTest
    @LdapServersSource(mode = Mode.All, except = LdapServerType.Fedora389ds)
    public void testSaslDigestMd5BindAuthStartTls( TestLdapServer ldapServer )
    {
        testSaslDigestMd5BindStartTls( ldapServer, SaslQoP.AUTH );
    }


    /**
     * Test binding to the server using SASL auth-int and StartTLS encryption.
     */
    @ParameterizedTest
    @LdapServersSource(mode = Mode.All, except = LdapServerType.Fedora389ds)
    public void testSaslDigestMd5BindAuthIntStartTls( TestLdapServer ldapServer )
    {
        testSaslDigestMd5BindStartTls( ldapServer, SaslQoP.AUTH_INT );
    }


    /**
     * Test binding to the server using SASL auth-conf and StartTLS encryption.
     */
    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testSaslDigestMd5BindAuthConfStartTls( TestLdapServer ldapServer )
    {
        testSaslDigestMd5BindStartTls( ldapServer, SaslQoP.AUTH_CONF );
    }


    private void testSaslDigestMd5BindStartTls( TestLdapServer ldapServer, SaslQoP saslQoP )
    {
        ldapServer.setConfidentialityRequired( true );
        StudioProgressMonitor monitor = getProgressMonitor();
        Connection connection = getConnection( monitor, ldapServer, "user.1", "password" );
        connection.setEncryptionMethod( EncryptionMethod.START_TLS );
        connection.setAuthMethod( AuthenticationMethod.SASL_DIGEST_MD5 );
        connection.getConnectionParameter().setSaslQop( saslQoP );
        acceptAllCertificates();

        assertFalse( connectionWrapper.isConnected() );

        connectionWrapper.connect( monitor );
        connectionWrapper.bind( monitor );

        assertTrue( connectionWrapper.isConnected() );
        assertTrue( connectionWrapper.isSecured() );
        assertNull( monitor.getException() );

        connectionWrapper.unbind();
        connectionWrapper.disconnect();
        assertFalse( connectionWrapper.isConnected() );
    }


    /**
     * Test binding to the server using GSSAPI auth and no encryption.
     */
    @ParameterizedTest
    @LdapServersSource(mode = Mode.All, except = LdapServerType.ApacheDS, reason = "Missing OSGi import: org.apache.directory.server.kerberos.shared.store.PrincipalStoreEntryModifier cannot be found by org.apache.directory.server.protocol.shared_2.0.0.AM26")
    public void testSaslGssapiBindAuthPlain( TestLdapServer ldapServer )
    {
        testSaslGssapiBindPlain( ldapServer, SaslQoP.AUTH );
    }


    /**
     * Test binding to the server using GSSAPI auth-int and no encryption.
     */
    @ParameterizedTest
    @LdapServersSource(mode = Mode.All, except = LdapServerType.ApacheDS, reason = "Missing OSGi import: org.apache.directory.server.kerberos.shared.store.PrincipalStoreEntryModifier cannot be found by org.apache.directory.server.protocol.shared_2.0.0.AM26")
    public void testSaslGssapiBindAuthIntPlain( TestLdapServer ldapServer )
    {
        testSaslGssapiBindPlain( ldapServer, SaslQoP.AUTH_INT );
    }


    /**
     * Test binding to the server using GSSAPI auth-conf and no encryption.
     */
    @ParameterizedTest
    @LdapServersSource(mode = Mode.All, except = LdapServerType.ApacheDS, reason = "Missing OSGi import: org.apache.directory.server.kerberos.shared.store.PrincipalStoreEntryModifier cannot be found by org.apache.directory.server.protocol.shared_2.0.0.AM26")
    public void testSaslGssapiBindAuthConfPlain( TestLdapServer ldapServer )
    {
        testSaslGssapiBindPlain( ldapServer, SaslQoP.AUTH_CONF );
    }


    private void testSaslGssapiBindPlain( TestLdapServer ldapServer, SaslQoP saslQoP )
    {
        TestFixture.skipIfKdcServerIsNotAvailable();

        ldapServer.setConfidentialityRequired( false );
        StudioProgressMonitor monitor = getProgressMonitor();
        Connection connection = getConnection( monitor, ldapServer, "hnelson", "secret" );
        connection.setAuthMethod( AuthenticationMethod.SASL_GSSAPI );
        connection.getConnectionParameter().setKrb5CredentialConfiguration( Krb5CredentialConfiguration.OBTAIN_TGT );
        connection.getConnectionParameter().setSaslQop( saslQoP );

        assertFalse( connectionWrapper.isConnected() );

        connectionWrapper.connect( monitor );
        connectionWrapper.bind( monitor );

        assertTrue( connectionWrapper.isConnected() );
        assertFalse( connectionWrapper.isSecured() );
        assertNull( monitor.getException() );

        connectionWrapper.unbind();
        connectionWrapper.disconnect();
        assertFalse( connectionWrapper.isConnected() );
    }


    /**
     * Test binding to the server using GSSAPI auth and ldaps:// encryption.
     */
    @ParameterizedTest
    @LdapServersSource(mode = Mode.All, except = LdapServerType.ApacheDS, reason = "Missing OSGi import: org.apache.directory.server.kerberos.shared.store.PrincipalStoreEntryModifier cannot be found by org.apache.directory.server.protocol.shared_2.0.0.AM26")
    public void testSaslGssapiBindAuthLdaps( TestLdapServer ldapServer ) throws Exception
    {
        testSaslGssapiBindLdaps( ldapServer, SaslQoP.AUTH );
    }


    /**
     * Test binding to the server using GSSAPI auth-int and ldaps:// encryption.
     */
    @ParameterizedTest
    @LdapServersSource(mode = Mode.All, except = LdapServerType.ApacheDS, reason = "Missing OSGi import: org.apache.directory.server.kerberos.shared.store.PrincipalStoreEntryModifier cannot be found by org.apache.directory.server.protocol.shared_2.0.0.AM26")
    public void testSaslGssapiBindAuthIntLdaps( TestLdapServer ldapServer ) throws Exception
    {
        testSaslGssapiBindLdaps( ldapServer, SaslQoP.AUTH_INT );
    }


    /**
     * Test binding to the server using GSSAPI auth-conf and ldaps:// encryption.
     */
    @ParameterizedTest
    @LdapServersSource(mode = Mode.All, except = LdapServerType.ApacheDS, reason = "Missing OSGi import: org.apache.directory.server.kerberos.shared.store.PrincipalStoreEntryModifier cannot be found by org.apache.directory.server.protocol.shared_2.0.0.AM26")
    public void testSaslGssapiBindAuthConfLdaps( TestLdapServer ldapServer ) throws Exception
    {
        testSaslGssapiBindLdaps( ldapServer, SaslQoP.AUTH_CONF );
    }


    private void testSaslGssapiBindLdaps( TestLdapServer ldapServer, SaslQoP saslQoP ) throws Exception
    {
        TestFixture.skipIfKdcServerIsNotAvailable();

        // obtain native TGT
        String[] cmd =
            { "/bin/sh", "-c", "echo secret | /usr/bin/kinit hnelson" };
        Process process = Runtime.getRuntime().exec( cmd );
        int exitCode = process.waitFor();
        assertEquals( 0, exitCode );

        ldapServer.setConfidentialityRequired( true );
        StudioProgressMonitor monitor = getProgressMonitor();
        Connection connection = getConnection( monitor, ldapServer, "hnelson", "secret" );
        connection.setPort( ldapServer.getPortSSL() );
        connection.setEncryptionMethod( EncryptionMethod.LDAPS );
        connection.setAuthMethod( AuthenticationMethod.SASL_GSSAPI );
        connection.getConnectionParameter().setKrb5CredentialConfiguration( Krb5CredentialConfiguration.USE_NATIVE );
        connection.getConnectionParameter().setSaslQop( saslQoP );
        acceptAllCertificates();

        assertFalse( connectionWrapper.isConnected() );

        connectionWrapper.connect( monitor );
        connectionWrapper.bind( monitor );

        assertTrue( connectionWrapper.isConnected() );
        assertTrue( connectionWrapper.isSecured() );
        assertNull( monitor.getException() );

        connectionWrapper.unbind();
        connectionWrapper.disconnect();
        assertFalse( connectionWrapper.isConnected() );
    }


    /**
     * Test binding to the server using GSSAPI auth and StartTLS encryption.
     */
    @ParameterizedTest
    @LdapServersSource(mode = Mode.All, except = LdapServerType.ApacheDS, reason = "Missing OSGi import: org.apache.directory.server.kerberos.shared.store.PrincipalStoreEntryModifier cannot be found by org.apache.directory.server.protocol.shared_2.0.0.AM26")
    public void testSaslGssapiBindAuthStartTls( TestLdapServer ldapServer )
    {
        testSaslGssapiBindStartTls( ldapServer, SaslQoP.AUTH );
    }


    /**
     * Test binding to the server using GSSAPI auth-int and StartTLS encryption.
     */
    @ParameterizedTest
    @LdapServersSource(mode = Mode.All, except = LdapServerType.ApacheDS, reason = "Missing OSGi import: org.apache.directory.server.kerberos.shared.store.PrincipalStoreEntryModifier cannot be found by org.apache.directory.server.protocol.shared_2.0.0.AM26")
    public void testSaslGssapiBindAuthIntStartTls( TestLdapServer ldapServer )
    {
        testSaslGssapiBindStartTls( ldapServer, SaslQoP.AUTH_INT );
    }


    /**
     * Test binding to the server using GSSAPI auth-conf and StartTLS encryption.
     */
    @ParameterizedTest
    @LdapServersSource(mode = Mode.All, except = LdapServerType.ApacheDS, reason = "Missing OSGi import: org.apache.directory.server.kerberos.shared.store.PrincipalStoreEntryModifier cannot be found by org.apache.directory.server.protocol.shared_2.0.0.AM26")
    public void testSaslGssapiBindAuthConfStartTls( TestLdapServer ldapServer )
    {
        testSaslGssapiBindStartTls( ldapServer, SaslQoP.AUTH_CONF );
    }


    private void testSaslGssapiBindStartTls( TestLdapServer ldapServer, SaslQoP saslQoP )
    {
        TestFixture.skipIfKdcServerIsNotAvailable();

        ldapServer.setConfidentialityRequired( true );
        StudioProgressMonitor monitor = getProgressMonitor();
        Connection connection = getConnection( monitor, ldapServer, "hnelson", "secret" );
        connection.setEncryptionMethod( EncryptionMethod.START_TLS );
        connection.setAuthMethod( AuthenticationMethod.SASL_GSSAPI );
        connection.getConnectionParameter().setKrb5CredentialConfiguration( Krb5CredentialConfiguration.OBTAIN_TGT );
        connection.getConnectionParameter().setSaslQop( saslQoP );
        acceptAllCertificates();

        assertFalse( connectionWrapper.isConnected() );

        connectionWrapper.connect( monitor );
        connectionWrapper.bind( monitor );

        assertTrue( connectionWrapper.isConnected() );
        assertTrue( connectionWrapper.isSecured() );
        assertNull( monitor.getException() );

        connectionWrapper.unbind();
        connectionWrapper.disconnect();
        assertFalse( connectionWrapper.isConnected() );
    }


    /**
     * Test failed binds to the server.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testBindFailures( TestLdapServer ldapServer )
    {
        StudioProgressMonitor monitor = null;

        // simple auth with invalid user
        monitor = getProgressMonitor();
        getConnection( monitor, ldapServer, "cn=invalid," + USERS_DN, "invalid" );
        connectionWrapper.connect( monitor );
        connectionWrapper.bind( monitor );
        assertFalse( connectionWrapper.isConnected() );
        assertNotNull( monitor.getException() );
        assertTrue( monitor.getException() instanceof StudioLdapException );
        assertTrue( monitor.getException().getMessage().contains( "[LDAP result code 49 - invalidCredentials]" ) );
        assertTrue( monitor.getException().getCause() instanceof LdapAuthenticationException );
        if ( ldapServer.getType() == LdapServerType.ApacheDS )
        {
            assertTrue( monitor.getException().getMessage().contains( "INVALID_CREDENTIALS" ) );
            assertTrue( monitor.getException().getCause().getMessage().contains( "INVALID_CREDENTIALS" ) );
        }

        // simple auth with invalid password
        monitor = getProgressMonitor();
        getConnection( monitor, ldapServer, ldapServer.getAdminDn(), "invalid" );
        connectionWrapper.connect( monitor );
        connectionWrapper.bind( monitor );
        assertFalse( connectionWrapper.isConnected() );
        assertNotNull( monitor.getException() );
        assertTrue( monitor.getException() instanceof StudioLdapException );
        assertTrue( monitor.getException().getMessage().contains( "[LDAP result code 49 - invalidCredentials]" ) );
        assertTrue( monitor.getException().getCause() instanceof LdapAuthenticationException );
        if ( ldapServer.getType() == LdapServerType.ApacheDS )
        {
            assertTrue( monitor.getException().getMessage().contains( "INVALID_CREDENTIALS" ) );
            assertTrue( monitor.getException().getCause().getMessage().contains( "INVALID_CREDENTIALS" ) );
        }
    }


    /**
     * Test searching.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testSearch( TestLdapServer ldapServer ) throws Exception
    {
        StudioProgressMonitor monitor = getProgressMonitor();
        SearchControls searchControls = new SearchControls();
        StudioSearchResultEnumeration result = getConnectionWrapper( monitor, ldapServer ).search( CONTEXT_DN.getName(),
            "(objectClass=*)", searchControls, AliasDereferencingMethod.NEVER, ReferralHandlingMethod.IGNORE, null,
            monitor, null );

        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        assertNotNull( result );
        assertTrue( result.hasMore() );
        assertNotNull( result.next() );
    }


    /**
     * Test binary attributes.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testSearchBinaryAttributes( TestLdapServer ldapServer ) throws Exception
    {
        StudioProgressMonitor monitor = getProgressMonitor();
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope( SearchControls.OBJECT_SCOPE );
        StudioSearchResultEnumeration result = getConnectionWrapper( monitor, ldapServer ).search(
            "uid=user.1,ou=users,dc=example,dc=org", "(objectClass=*)", searchControls, AliasDereferencingMethod.NEVER,
            ReferralHandlingMethod.IGNORE, null, monitor, null );

        assertNotNull( result );
        assertTrue( result.hasMore() );
        StudioSearchResult entry = result.next();
        assertNotNull( entry );

        Object userPasswordValue = entry.getEntry().get( "userPassword" ).getBytes();
        assertEquals( byte[].class, userPasswordValue.getClass() );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testSearchContinuation_Follow_DirectReferral( TestLdapServer ldapServer ) throws Exception
    {
        StudioProgressMonitor monitor = getProgressMonitor();
        SearchControls searchControls = new SearchControls();
        StudioSearchResultEnumeration result = getConnectionWrapper( monitor, ldapServer ).search(
            REFERRAL_TO_USERS_DN.getName(), "(objectClass=*)", searchControls,
            AliasDereferencingMethod.NEVER,
            ReferralHandlingMethod.FOLLOW, null, monitor, null );

        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        assertNotNull( result );

        List<Dn> dns = consume( result, sr -> sr.getDn() );
        if ( ldapServer.getType() != LdapServerType.Fedora389ds )
        {
            // TODO: check why 389ds returns ou=users
            assertEquals( 8, dns.size() );
            assertThat( dns, hasItems( USER1_DN, USER8_DN ) );
        }
    }


    @ParameterizedTest
    @LdapServersSource
    public void testSearchContinuation_Follow_IntermediateReferral( TestLdapServer ldapServer ) throws Exception
    {
        StudioProgressMonitor monitor = getProgressMonitor();
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope( SearchControls.SUBTREE_SCOPE );
        StudioSearchResultEnumeration result = getConnectionWrapper( monitor, ldapServer ).search(
            REFERRAL_TO_REFERRAL_TO_USERS_DN.getName(), "(objectClass=*)", searchControls,
            AliasDereferencingMethod.NEVER,
            ReferralHandlingMethod.FOLLOW, null, monitor, null );

        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        assertNotNull( result );

        List<Dn> dns = consume( result, sr -> sr.getDn() );
        if ( ldapServer.getType() != LdapServerType.Fedora389ds )
        {
            // TODO: check why 389ds returns ou=users only
            assertEquals( 9, dns.size() );
            assertThat( dns, hasItems( USERS_DN, USER1_DN, USER8_DN ) );
        }
    }


    @ParameterizedTest
    @LdapServersSource
    public void testSearchContinuation_Follow_ReferralToParent( TestLdapServer ldapServer ) throws Exception
    {
        StudioProgressMonitor monitor = getProgressMonitor();
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope( SearchControls.ONELEVEL_SCOPE );
        StudioSearchResultEnumeration result = getConnectionWrapper( monitor, ldapServer ).search(
            REFERRAL_TO_REFERRALS_DN.getName(), "(objectClass=*)", searchControls, AliasDereferencingMethod.NEVER,
            ReferralHandlingMethod.FOLLOW, null, monitor, null );

        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        assertNotNull( result );

        List<Dn> dns = consume( result, sr -> sr.getDn() );
        if ( ldapServer.getType() != LdapServerType.Fedora389ds )
        {
            // TODO: check why 389ds returns nothing
            assertEquals( 5, dns.size() );
            assertThat( dns, hasItems( REFERRALS_DN, USERS_DN, USER1_DN, MISC_DN ) );
        }
    }


    @ParameterizedTest
    @LdapServersSource
    public void testSearchContinuation_Follow_ReferralLoop( TestLdapServer ldapServer ) throws Exception
    {
        StudioProgressMonitor monitor = getProgressMonitor();
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope( SearchControls.SUBTREE_SCOPE );
        StudioSearchResultEnumeration result = getConnectionWrapper( monitor, ldapServer ).search(
            REFERRAL_LOOP_1_DN.getName(), "(objectClass=*)", searchControls, AliasDereferencingMethod.NEVER,
            ReferralHandlingMethod.FOLLOW, null, monitor, null );

        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        assertNotNull( result );

        List<String> dns = consume( result, sr -> sr.getDn().getName() );
        assertEquals( 0, dns.size() );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testSearchContinuationFollowManually( TestLdapServer ldapServer ) throws Exception
    {
        StudioProgressMonitor monitor = getProgressMonitor();
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope( SearchControls.SUBTREE_SCOPE );
        ConnectionWrapper connectionWrapper = getConnectionWrapper( monitor, ldapServer );
        ConnectionCorePlugin.getDefault().setReferralHandler( null );
        StudioSearchResultEnumeration result = connectionWrapper.search( REFERRALS_DN.getName(), "(objectClass=*)",
            searchControls, AliasDereferencingMethod.NEVER, ReferralHandlingMethod.FOLLOW_MANUALLY, null, monitor,
            null );

        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        assertNotNull( result );

        List<Dn> dns = consume( result, sr -> sr.getDn() );
        assertEquals( 8, dns.size() );
        assertThat( dns, hasItems( REFERRALS_DN, USER1_DN, USERS_DN, REFERRAL_TO_USERS_DN, REFERRAL_LOOP_1_DN,
            REFERRAL_LOOP_2_DN, MISC_DN ) );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testSearchContinuationIgnore( TestLdapServer ldapServer ) throws Exception
    {
        StudioProgressMonitor monitor = getProgressMonitor();
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope( SearchControls.SUBTREE_SCOPE );
        StudioSearchResultEnumeration result = getConnectionWrapper( monitor, ldapServer ).search(
            REFERRALS_DN.getName(), "(objectClass=*)", searchControls, AliasDereferencingMethod.NEVER,
            ReferralHandlingMethod.IGNORE, null, monitor, null );

        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        assertNotNull( result );

        List<Dn> dns = consume( result, sr -> sr.getDn() );
        assertEquals( 1, dns.size() );
        assertThat( dns, hasItems( REFERRALS_DN ) );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testSearchContinuationFollowParent( TestLdapServer ldapServer ) throws Exception
    {
        StudioProgressMonitor monitor = getProgressMonitor();
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope( SearchControls.ONELEVEL_SCOPE );
        StudioSearchResultEnumeration result = getConnectionWrapper( monitor, ldapServer ).search(
            REFERRALS_DN.getName(), "(objectClass=*)", searchControls, AliasDereferencingMethod.NEVER,
            ReferralHandlingMethod.FOLLOW, null, monitor, null );

        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        assertNotNull( result );

        List<Dn> dns = consume( result, sr -> sr.getDn() );
        assertEquals( 5, dns.size() );
        assertThat( dns, hasItems( REFERRALS_DN, USERS_DN, USER1_DN, MISC_DN ) );
    }


    protected <T> List<T> consume( StudioSearchResultEnumeration result, Function<StudioSearchResult, T> fn )
        throws LdapException
    {
        List<T> list = new ArrayList<>();
        while ( result.hasMore() )
        {
            StudioSearchResult sr = result.next();
            list.add( fn.apply( sr ) );
        }
        return list;
    }


    @ParameterizedTest
    @LdapServersSource
    public void testAdd( TestLdapServer ldapServer ) throws Exception
    {
        String dn = "uid=user.X," + USERS_DN;

        StudioProgressMonitor monitor = getProgressMonitor();
        Entry entry = new DefaultEntry( dn, "objectClass: inetOrgPerson", "sn: X", "cn: X", "uid: user.X" );
        getConnectionWrapper( monitor, ldapServer ).createEntry( entry, null, monitor, null );

        // should have created entry
        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        assertTrue( ldapServer.withAdminConnectionAndGet( connection -> connection.exists( dn ) ) );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testAddFollowsReferral_DirectReferral( TestLdapServer ldapServer ) throws Exception
    {
        Dn targetDn = dn( "uid=user.X", USERS_DN );
        Dn referralDn = dn( "uid=user.X", REFERRAL_TO_USERS_DN );

        // create entry under referral
        StudioProgressMonitor monitor = getProgressMonitor();
        Entry entry = new DefaultEntry( referralDn, "objectClass: inetOrgPerson", "sn: X", "cn: X", "uid: user.X" );
        getConnectionWrapper( monitor, ldapServer ).createEntry( entry, null, monitor, null );

        // should have created target entry
        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        assertTrue( ldapServer.withAdminConnectionAndGet( connection -> connection.exists( targetDn ) ) );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testAddFollowsReferral_IntermediateReferral( TestLdapServer ldapServer ) throws Exception
    {
        Dn targetDn = dn( "uid=user.X", USERS_DN );
        Dn referralDn = dn( "uid=user.X", REFERRAL_TO_REFERRAL_TO_USERS_DN );

        // create entry under referral
        StudioProgressMonitor monitor = getProgressMonitor();
        Entry entry = new DefaultEntry( referralDn, "objectClass: inetOrgPerson", "sn: X", "cn: X", "uid: user.X" );
        getConnectionWrapper( monitor, ldapServer ).createEntry( entry, null, monitor, null );

        // should have created target entry
        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        assertTrue( ldapServer.withAdminConnectionAndGet( connection -> connection.exists( targetDn ) ) );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testAddFollowsReferral_ReferralLoop( TestLdapServer ldapServer ) throws Exception
    {
        Dn referralDn = dn( "uid=user.X", REFERRAL_LOOP_1_DN );

        // create entry under referral
        StudioProgressMonitor monitor = getProgressMonitor();
        Entry entry = new DefaultEntry( referralDn, "objectClass: inetOrgPerson", "sn: X", "cn: X", "uid: user.X" );
        getConnectionWrapper( monitor, ldapServer ).createEntry( entry, null, monitor, null );

        // should not have created target entry
        assertFalse( monitor.isCanceled() );
        assertTrue( monitor.errorsReported() );
        assertNotNull( monitor.getException() );
        assertTrue( monitor.getException() instanceof StudioLdapException );
        assertTrue( monitor.getException().getMessage().contains( "[LDAP result code 54 - loopDetect]" ) );
        assertTrue( monitor.getException().getMessage().contains( "already processed" ) );
        assertTrue( monitor.getException().getCause() instanceof LdapLoopDetectedException );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testModify( TestLdapServer ldapServer ) throws Exception
    {
        String dn = "uid=user.X," + USERS_DN;

        // create entry
        ldapServer.withAdminConnection( connection -> connection.add( new DefaultEntry( dn,
            "objectClass: inetOrgPerson", "sn: X", "cn: X", "uid: user.X" ) ) );

        // modify entry
        StudioProgressMonitor monitor = getProgressMonitor();
        List<Modification> modifications = Collections.singletonList(
            new DefaultModification( ModificationOperation.REPLACE_ATTRIBUTE,
                new DefaultAttribute( "sn", "modified" ) ) );
        getConnectionWrapper( monitor, ldapServer ).modifyEntry( new Dn( dn ), modifications, null, monitor, null );

        // should have modified the entry
        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        Entry entry = ldapServer.withAdminConnectionAndGet( connection -> connection.lookup( new Dn( dn ) ) );
        assertEquals( "modified", entry.get( "sn" ).getString() );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testModifyFollowsReferral_DirectReferral( TestLdapServer ldapServer ) throws Exception
    {
        Dn targetDn = dn( "uid=user.X", USERS_DN );
        Dn referralDn = dn( "uid=user.X", REFERRAL_TO_USERS_DN );

        // create target entry
        ldapServer.withAdminConnection( connection -> connection.add( new DefaultEntry( targetDn,
            "objectClass: inetOrgPerson", "sn: X", "cn: X", "uid: user.X" ) ) );

        // modify referral entry 
        StudioProgressMonitor monitor = getProgressMonitor();
        List<Modification> modifications = Collections.singletonList(
            new DefaultModification( ModificationOperation.REPLACE_ATTRIBUTE,
                new DefaultAttribute( "sn", "modified" ) ) );
        getConnectionWrapper( monitor, ldapServer ).modifyEntry( referralDn, modifications, null, monitor,
            null );

        // should have modified the target entry
        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        Entry entry = ldapServer.withAdminConnectionAndGet( connection -> connection.lookup( targetDn ) );
        assertEquals( "modified", entry.get( "sn" ).getString() );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testModifyFollowsReferral_IntermediateReferral( TestLdapServer ldapServer ) throws Exception
    {
        Dn targetDn = dn( "uid=user.X", USERS_DN );
        Dn referralDn = dn( "uid=user.X", REFERRAL_TO_REFERRAL_TO_USERS_DN );

        // create target entry
        ldapServer.withAdminConnection( connection -> connection.add( new DefaultEntry( targetDn,
            "objectClass: inetOrgPerson", "sn: X", "cn: X", "uid: user.X" ) ) );

        // modify referral entry 
        StudioProgressMonitor monitor = getProgressMonitor();
        List<Modification> modifications = Collections.singletonList(
            new DefaultModification( ModificationOperation.REPLACE_ATTRIBUTE,
                new DefaultAttribute( "sn", "modified" ) ) );
        getConnectionWrapper( monitor, ldapServer ).modifyEntry( referralDn, modifications, null, monitor,
            null );

        // should have modified the target entry
        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        Entry entry = ldapServer.withAdminConnectionAndGet( connection -> connection.lookup( targetDn ) );
        assertEquals( "modified", entry.get( "sn" ).getString() );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testModifyFollowsReferral_ReferralLoop( TestLdapServer ldapServer ) throws Exception
    {
        Dn targetDn = dn( "uid=user.X", USERS_DN );
        Dn referralDn = dn( "uid=user.X", REFERRAL_LOOP_1_DN );

        // create target entry
        ldapServer.withAdminConnection( connection -> connection.add( new DefaultEntry( targetDn,
            "objectClass: inetOrgPerson", "sn: X", "cn: X", "uid: user.X" ) ) );

        // modify referral entry 
        StudioProgressMonitor monitor = getProgressMonitor();
        List<Modification> modifications = Collections.singletonList(
            new DefaultModification( ModificationOperation.REPLACE_ATTRIBUTE,
                new DefaultAttribute( "sn", "modified" ) ) );
        getConnectionWrapper( monitor, ldapServer ).modifyEntry( referralDn, modifications, null, monitor,
            null );

        // should not have modified the target entry
        assertFalse( monitor.isCanceled() );
        assertTrue( monitor.errorsReported() );
        assertNotNull( monitor.getException() );
        assertTrue( monitor.getException() instanceof StudioLdapException );
        assertTrue( monitor.getException().getMessage().contains( "[LDAP result code 54 - loopDetect]" ) );
        assertTrue( monitor.getException().getMessage().contains( "already processed" ) );
        assertTrue( monitor.getException().getCause() instanceof LdapLoopDetectedException );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testDelete( TestLdapServer ldapServer ) throws Exception
    {
        String dn = "uid=user.X," + USERS_DN;

        // create entry
        ldapServer.withAdminConnection( connection -> connection.add( new DefaultEntry( dn,
            "objectClass: inetOrgPerson", "sn: X", "cn: X", "uid: user.X" ) ) );

        // delete entry
        StudioProgressMonitor monitor = getProgressMonitor();
        getConnectionWrapper( monitor, ldapServer ).deleteEntry( new Dn( dn ), null, monitor, null );

        // should have deleted the entry
        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        assertFalse( ldapServer.withAdminConnectionAndGet( connection -> connection.exists( dn ) ) );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testDeleteFollowsReferral_DirectReferral( TestLdapServer ldapServer ) throws Exception
    {
        Dn targetDn = dn( "uid=user.X", USERS_DN );
        Dn referralDn = dn( "uid=user.X", REFERRAL_TO_USERS_DN );

        // create target entry
        ldapServer.withAdminConnection( connection -> connection.add( new DefaultEntry( targetDn,
            "objectClass: inetOrgPerson", "sn: X", "cn: X", "uid: user.X" ) ) );

        // delete referral entry 
        StudioProgressMonitor monitor = getProgressMonitor();
        getConnectionWrapper( monitor, ldapServer ).deleteEntry( referralDn, null, monitor, null );

        // should have deleted the target entry
        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        assertFalse( ldapServer.withAdminConnectionAndGet( connection -> connection.exists( targetDn ) ) );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testDeleteFollowsReferral_IntermediateReferral( TestLdapServer ldapServer ) throws Exception
    {
        Dn targetDn = dn( "uid=user.X", USERS_DN );
        Dn referralDn = dn( "uid=user.X", REFERRAL_TO_REFERRAL_TO_USERS_DN );

        // create target entry
        ldapServer.withAdminConnection( connection -> connection.add( new DefaultEntry( targetDn,
            "objectClass: inetOrgPerson", "sn: X", "cn: X", "uid: user.X" ) ) );

        // delete referral entry 
        StudioProgressMonitor monitor = getProgressMonitor();
        getConnectionWrapper( monitor, ldapServer ).deleteEntry( referralDn, null, monitor, null );

        // should have deleted the target entry
        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        assertFalse( ldapServer.withAdminConnectionAndGet( connection -> connection.exists( targetDn ) ) );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testDeleteFollowsReferral_ReferralLoop( TestLdapServer ldapServer ) throws Exception
    {
        Dn targetDn = dn( "uid=user.X", USERS_DN );
        Dn referralDn = dn( "uid=user.X", REFERRAL_LOOP_1_DN );

        // create target entry
        ldapServer.withAdminConnection( connection -> connection.add( new DefaultEntry( targetDn,
            "objectClass: inetOrgPerson", "sn: X", "cn: X", "uid: user.X" ) ) );

        // delete referral entry 
        StudioProgressMonitor monitor = getProgressMonitor();
        getConnectionWrapper( monitor, ldapServer ).deleteEntry( referralDn, null, monitor, null );

        // should not have deleted the target entry
        assertFalse( monitor.isCanceled() );
        assertTrue( monitor.errorsReported() );
        assertNotNull( monitor.getException() );
        assertTrue( monitor.getException() instanceof StudioLdapException );
        assertTrue( monitor.getException().getMessage().contains( "[LDAP result code 54 - loopDetect]" ) );
        assertTrue( monitor.getException().getMessage().contains( "already processed" ) );
        assertTrue( monitor.getException().getCause() instanceof LdapLoopDetectedException );
        assertTrue( ldapServer.withAdminConnectionAndGet( connection -> connection.exists( targetDn ) ) );
    }


    /**
     * Test initializing of Root DSE.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testInitializeAttributesRunnable( TestLdapServer ldapServer ) throws Exception
    {
        StudioProgressMonitor monitor = getProgressMonitor();
        ConnectionParameter connectionParameter = new ConnectionParameter( null, ldapServer.getHost(),
            ldapServer.getPort(), EncryptionMethod.NONE, AuthenticationMethod.SIMPLE,
            ldapServer.getAdminDn(), ldapServer.getAdminPassword(), null, true, null, 30000L );
        Connection connection = new Connection( connectionParameter );
        BrowserConnection browserConnection = new BrowserConnection( connection );

        assertFalse( browserConnection.getRootDSE().isAttributesInitialized() );

        InitializeRootDSERunnable.loadRootDSE( browserConnection, monitor );

        assertTrue( browserConnection.getRootDSE().isAttributesInitialized() );
    }


    /**
     * DIRSTUDIO-1039
     */
    @Disabled("Flaky test")
    @ParameterizedTest
    @LdapServersSource
    public void testConcurrentUseAndCloseOfConnection( TestLdapServer ldapServer ) throws Exception
    {
        final StudioProgressMonitor monitor = getProgressMonitor();
        final ConnectionParameter connectionParameter = new ConnectionParameter( null, ldapServer.getHost(),
            ldapServer.getPort(),
            EncryptionMethod.NONE, AuthenticationMethod.SIMPLE,
            ldapServer.getAdminDn(), ldapServer.getAdminPassword(), null, true, null, 30000L );
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


    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testPasswordModifyRequestExtendedOperation_AdminChangesUserPassword( TestLdapServer ldapServer )
        throws Exception
    {
        String dn = "uid=user.X," + USERS_DN;

        // create target entry
        String password0 = "{SSHA}VHg6ewDaPUmVWw3efXL5NF6bVuRHGWhrCRH1xA==";
        ldapServer.withAdminConnection( connection -> connection.add( new DefaultEntry( dn,
            "objectClass: inetOrgPerson", "sn: X", "cn: X", "uid: user.X",
            "userPassword: " + password0 ) ) );
        Entry entry0 = ldapServer.withAdminConnectionAndGet( connection -> connection.lookup( new Dn( dn ) ) );
        assertEquals( password0, Strings.utf8ToString( entry0.get( "userPassword" ).getBytes() ) );

        // modify password
        LdapApiService ldapApiService = LdapApiServiceFactory.getSingleton();
        PasswordModifyRequest request = ( PasswordModifyRequest ) ldapApiService.getExtendedRequestFactories()
            .get( PasswordModifyRequest.EXTENSION_OID ).newRequest();
        request.setUserIdentity( Strings.getBytesUtf8( dn ) );
        request.setNewPassword( Strings.getBytesUtf8( "s3cre3t" ) );
        StudioProgressMonitor monitor = getProgressMonitor();
        ExtendedResponse response = getConnectionWrapper( monitor, ldapServer ).extended( request, monitor );

        if ( ldapServer.getType() == LdapServerType.Fedora389ds )
        {
            assertEquals( ResultCodeEnum.CONFIDENTIALITY_REQUIRED, response.getLdapResult().getResultCode() );
            assertFalse( monitor.isCanceled() );
            assertTrue( monitor.errorsReported() );
        }
        else
        {
            // should have modified password of the target entry
            assertEquals( ResultCodeEnum.SUCCESS, response.getLdapResult().getResultCode() );
            assertFalse( monitor.isCanceled() );
            assertFalse( monitor.errorsReported() );
            Entry entry = ldapServer.withAdminConnectionAndGet( connection -> connection.lookup( new Dn( dn ) ) );
            assertNotEquals( password0, Strings.utf8ToString( entry.get( "userPassword" ).getBytes() ) );
        }
    }


    @ParameterizedTest
    @LdapServersSource(mode = Mode.All, except = LdapServerType.Fedora389ds, reason = "389ds requires secure connection")
    public void testPasswordModifyRequestExtendedOperation_UserChangesOwnPassword( TestLdapServer ldapServer )
        throws Exception
    {
        String dn = "uid=user.X," + USERS_DN;

        // create target entry
        String password0 = "{SSHA}VHg6ewDaPUmVWw3efXL5NF6bVuRHGWhrCRH1xA==";
        ldapServer.withAdminConnection( connection -> connection.add( new DefaultEntry( dn,
            "objectClass: inetOrgPerson", "sn: X", "cn: X", "uid: user.X",
            "userPassword: " + password0 ) ) );
        Entry entry0 = ldapServer.withAdminConnectionAndGet( connection -> connection.lookup( new Dn( dn ) ) );
        assertEquals( password0, Strings.utf8ToString( entry0.get( "userPassword" ).getBytes() ) );

        // modify password with wrong old password
        LdapApiService ldapApiService = LdapApiServiceFactory.getSingleton();
        PasswordModifyRequest request1 = ( PasswordModifyRequest ) ldapApiService.getExtendedRequestFactories()
            .get( PasswordModifyRequest.EXTENSION_OID ).newRequest();
        request1.setUserIdentity( Strings.getBytesUtf8( dn ) );
        request1.setOldPassword( Strings.getBytesUtf8( "wrong" ) );
        request1.setNewPassword( Strings.getBytesUtf8( "s3cre3t" ) );
        StudioProgressMonitor monitor1 = getProgressMonitor();
        ExtendedResponse response1 = getConnectionWrapper( monitor1, ldapServer, dn, "secret" ).extended( request1,
            monitor1 );

        // should not have modified password of the target entry
        if ( ldapServer.getType() == LdapServerType.OpenLdap )
        {
            assertEquals( ResultCodeEnum.UNWILLING_TO_PERFORM, response1.getLdapResult().getResultCode() );
        }
        else
        {
            assertEquals( ResultCodeEnum.INVALID_CREDENTIALS, response1.getLdapResult().getResultCode() );
        }
        assertFalse( monitor1.isCanceled() );
        assertTrue( monitor1.errorsReported() );
        Entry entry1 = ldapServer.withAdminConnectionAndGet( connection -> connection.lookup( new Dn( dn ) ) );
        assertEquals( password0, Strings.utf8ToString( entry1.get( "userPassword" ).getBytes() ) );

        // modify password with correct old password
        PasswordModifyRequest request2 = ( PasswordModifyRequest ) ldapApiService.getExtendedRequestFactories()
            .get( PasswordModifyRequest.EXTENSION_OID ).newRequest();
        request2.setUserIdentity( Strings.getBytesUtf8( dn ) );
        request2.setOldPassword( Strings.getBytesUtf8( "secret" ) );
        request2.setNewPassword( Strings.getBytesUtf8( "s3cre3t" ) );
        StudioProgressMonitor monitor2 = getProgressMonitor();
        ExtendedResponse response2 = getConnectionWrapper( monitor2, ldapServer, dn, "secret" ).extended( request2,
            monitor2 );

        // should have modified password of the target entry
        assertEquals( ResultCodeEnum.SUCCESS, response2.getLdapResult().getResultCode() );
        assertFalse( monitor2.isCanceled() );
        assertFalse( monitor2.errorsReported() );
        Entry entry2 = ldapServer.withAdminConnectionAndGet( connection -> connection.lookup( new Dn( dn ) ) );
        assertNotEquals( password0, Strings.utf8ToString( entry2.get( "userPassword" ).getBytes() ) );
    }


    @ParameterizedTest
    @LdapServersSource(except = LdapServerType.ApacheDS, reason = "OSGi issue when using WhoAmI extended operation in ApacheDS")
    public void testWhoAmIExtendedOperation( TestLdapServer ldapServer ) throws Exception
    {
        LdapApiService ldapApiService = LdapApiServiceFactory.getSingleton();
        WhoAmIRequest request = ( WhoAmIRequest ) ldapApiService.getExtendedRequestFactories()
            .get( WhoAmIRequest.EXTENSION_OID ).newRequest();
        StudioProgressMonitor monitor = getProgressMonitor();
        WhoAmIResponse response = ( WhoAmIResponse ) getConnectionWrapper( monitor, ldapServer ).extended( request,
            monitor );

        assertEquals( ResultCodeEnum.SUCCESS, response.getLdapResult().getResultCode() );
        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        assertTrue( response.isDnAuthzId() );
        assertEquals( ldapServer.getAdminDn().toLowerCase(), response.getDn().toString().toLowerCase().trim() );
    }

    /*
    @Ignore
    @Test
    public void testStartEndTransactionExtendedOperation( TestLdapServer ldapServer ) throws Exception
    {
        LdapApiService ldapApiService = LdapApiServiceFactory.getSingleton();
    
        StartTransactionRequest request1 = ( StartTransactionRequest ) ldapApiService.getExtendedRequestFactories()
            .get( StartTransactionRequest.EXTENSION_OID ).newRequest();
        StudioProgressMonitor monitor1 = getProgressMonitor();
        StartTransactionResponse response1 = ( StartTransactionResponse ) getConnectionWrapper( monitor1 )
            .extended( request1, monitor1 );
    
        assertEquals( ResultCodeEnum.SUCCESS, response1.getLdapResult().getResultCode() );
        assertFalse( monitor1.isCanceled() );
        assertFalse( monitor1.errorsReported() );
    
        EndTransactionRequest request2 = ( EndTransactionRequest ) ldapApiService.getExtendedRequestFactories()
            .get( EndTransactionRequest.EXTENSION_OID ).newRequest();
        request2.setTransactionId( response1.getTransactionId() );
        request2.setCommit( true );
        StudioProgressMonitor monitor2 = getProgressMonitor();
        EndTransactionResponse response2 = ( EndTransactionResponse ) getConnectionWrapper( monitor2 )
            .extended( request2, monitor2 );
    
        assertEquals( ResultCodeEnum.SUCCESS, response2.getLdapResult().getResultCode() );
        assertFalse( monitor2.isCanceled() );
        assertFalse( monitor2.errorsReported() );
    }
    */


    protected StudioProgressMonitor getProgressMonitor()
    {
        StudioProgressMonitor monitor = new StudioProgressMonitor( new NullProgressMonitor() );
        return monitor;
    }


    protected void acceptAllCertificates()
    {
        ConnectionCorePlugin.getDefault().setCertificateHandler( ( host, certChain, failCauses ) -> {
            return TrustLevel.Permanent;
        } );
    }


    protected ConnectionWrapper getConnectionWrapper( StudioProgressMonitor monitor, TestLdapServer ldapServer )
    {
        return getConnectionWrapper( monitor, ldapServer, ldapServer.getAdminDn(),
            ldapServer.getAdminPassword() );
    }


    protected ConnectionWrapper getConnectionWrapper( StudioProgressMonitor monitor, TestLdapServer ldapServer,
        String dn, String password )
    {
        getConnection( monitor, ldapServer, dn, password );

        connectionWrapper.connect( monitor );
        connectionWrapper.bind( monitor );
        assertTrue( connectionWrapper.isConnected() );
        assertNull( monitor.getException() );

        return connectionWrapper;

    }


    protected Connection getConnection( StudioProgressMonitor monitor, TestLdapServer ldapServer,
        String dn, String password )
    {
        // simple auth without principal and credential
        ConnectionParameter connectionParameter = new ConnectionParameter( null, ldapServer.getHost(),
            ldapServer.getPort(), EncryptionMethod.NONE, AuthenticationMethod.SIMPLE, dn, password, null, false, null,
            30000L );
        connectionParameter.setSaslQop( SaslQoP.AUTH_CONF );
        connectionParameter.setSaslSecurityStrength( SaslSecurityStrength.HIGH );
        connectionParameter.setSaslMutualAuthentication( true );

        Connection connection = new Connection( connectionParameter );

        IReferralHandler referralHandler = referralUrls -> {
            return connection;
        };
        ConnectionCorePlugin.getDefault().setReferralHandler( referralHandler );

        connectionWrapper = connection.getConnectionWrapper();

        return connection;
    }

}
