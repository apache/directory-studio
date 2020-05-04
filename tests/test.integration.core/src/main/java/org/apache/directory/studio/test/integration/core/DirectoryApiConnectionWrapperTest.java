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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.net.ConnectException;
import java.nio.channels.UnresolvedAddressException;
import java.nio.charset.StandardCharsets;
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
import org.apache.directory.api.ldap.model.entry.DefaultAttribute;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.DefaultModification;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Modification;
import org.apache.directory.api.ldap.model.entry.ModificationOperation;
import org.apache.directory.api.ldap.model.exception.LdapAuthenticationException;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapLoopDetectedException;
import org.apache.directory.api.ldap.model.message.ExtendedResponse;
import org.apache.directory.api.ldap.model.message.ResultCodeEnum;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.util.Strings;
import org.apache.directory.ldap.client.api.exception.InvalidConnectionException;
import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.server.core.integ.FrameworkRunner;
//import org.apache.directory.server.ldap.handlers.extended.EndTransactionHandler;
import org.apache.directory.server.ldap.handlers.extended.PwdModifyHandler;
//import org.apache.directory.server.ldap.handlers.extended.StartTransactionHandler;
import org.apache.directory.server.ldap.handlers.extended.WhoAmIHandler;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.apache.directory.studio.connection.core.IReferralHandler;
import org.apache.directory.studio.connection.core.ConnectionParameter.AuthenticationMethod;
import org.apache.directory.studio.connection.core.ConnectionParameter.EncryptionMethod;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;
import org.apache.directory.studio.connection.core.io.ConnectionWrapper;
import org.apache.directory.studio.connection.core.io.StudioLdapException;
import org.apache.directory.studio.connection.core.io.api.DirectoryApiConnectionWrapper;
import org.apache.directory.studio.connection.core.io.api.StudioSearchResult;
import org.apache.directory.studio.connection.core.io.api.StudioSearchResultEnumeration;
import org.apache.directory.studio.ldapbrowser.core.jobs.InitializeRootDSERunnable;
import org.apache.directory.studio.ldapbrowser.core.model.impl.BrowserConnection;
import org.apache.mina.util.AvailablePortFinder;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
    { @CreateTransport(protocol = "LDAP"), @CreateTransport(protocol = "LDAPS") }, extendedOpHandlers =
    { PwdModifyHandler.class, WhoAmIHandler.class })
@ApplyLdifFiles(clazz = DirectoryApiConnectionWrapperTest.class, value = "org/apache/directory/studio/test/integration/core/TestData.ldif")
public class DirectoryApiConnectionWrapperTest extends AbstractLdapTestUnit
{

    protected ConnectionWrapper connectionWrapper;

    @Before
    public void setUp() throws Exception
    {
        // create referral entries
        Entry referralsOu = new DefaultEntry( getService().getSchemaManager() );
        referralsOu.setDn( new Dn( "ou=referrals,ou=system" ) );
        referralsOu.add( "objectClass", "top", "organizationalUnit" );
        referralsOu.add( "ou", "referrals" );
        service.getAdminSession().add( referralsOu );

        // direct referral
        Entry r1 = new DefaultEntry( getService().getSchemaManager() );
        r1.setDn( new Dn( "cn=referral1,ou=referrals,ou=system" ) );
        r1.add( "objectClass", "top", "referral", "extensibleObject" );
        r1.add( "cn", "referral1" );
        r1.add( "ref", "ldap://" + LOCALHOST + ":" + ldapServer.getPort() + "/ou=users,ou=system" );
        service.getAdminSession().add( r1 );

        // referral via another immediate referral
        Entry r2 = new DefaultEntry( getService().getSchemaManager() );
        r2.setDn( new Dn( "cn=referral2,ou=referrals,ou=system" ) );
        r2.add( "objectClass", "top", "referral", "extensibleObject" );
        r2.add( "cn", "referral2" );
        r2.add( "ref", "ldap://" + LOCALHOST + ":" + ldapServer.getPort() + "/cn=referral1,ou=referrals,ou=system" );
        service.getAdminSession().add( r2 );

        // referral to parent which contains this referral
        Entry r3 = new DefaultEntry( getService().getSchemaManager() );
        r3.setDn( new Dn( "cn=referral3,ou=referrals,ou=system" ) );
        r3.add( "objectClass", "top", "referral", "extensibleObject" );
        r3.add( "cn", "referral3" );
        r3.add( "ref", "ldap://" + LOCALHOST + ":" + ldapServer.getPort() + "/ou=referrals,ou=system" );
        service.getAdminSession().add( r3 );

        // referrals pointing to each other (loop)
        Entry r4a = new DefaultEntry( getService().getSchemaManager() );
        r4a.setDn( new Dn( "cn=referral4a,ou=referrals,ou=system" ) );
        r4a.add( "objectClass", "top", "referral", "extensibleObject" );
        r4a.add( "cn", "referral4a" );
        r4a.add( "ref", "ldap://" + LOCALHOST + ":" + ldapServer.getPort() + "/cn=referral4b,ou=referrals,ou=system" );
        service.getAdminSession().add( r4a );
        Entry r4b = new DefaultEntry( getService().getSchemaManager() );
        r4b.setDn( new Dn( "cn=referral4b,ou=referrals,ou=system" ) );
        r4b.add( "objectClass", "top", "referral", "extensibleObject" );
        r4b.add( "cn", "referral4b" );
        r4b.add( "ref", "ldap://" + LOCALHOST + ":" + ldapServer.getPort() + "/cn=referral4a,ou=referrals,ou=system" );
        service.getAdminSession().add( r4b );
    }


    @After
    public void tearDown() throws Exception
    {
        if ( connectionWrapper != null )
        {
            connectionWrapper.disconnect();
        }
    }


    /**
     * Tests connecting to the server.
     */
    @Test
    public void testConnect()
    {
        StudioProgressMonitor monitor = getProgressMonitor();
        ConnectionParameter connectionParameter = new ConnectionParameter( null, LOCALHOST, ldapServer.getPort(),
            EncryptionMethod.NONE, AuthenticationMethod.NONE, null, null, null, true, null, 30000L );
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
        connectionParameter = new ConnectionParameter( null, LOCALHOST, AvailablePortFinder.getNextAvailable(),
            EncryptionMethod.NONE, AuthenticationMethod.NONE, null, null, null, true, null, 30000L );
        connection = new Connection( connectionParameter );
        connectionWrapper = connection.getConnectionWrapper();
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
        connectionParameter = new ConnectionParameter( null, "555.555.555.555", ldapServer.getPort(),
            EncryptionMethod.NONE, AuthenticationMethod.NONE, null, null, null, true, null, 30000L );
        connection = new Connection( connectionParameter );
        connectionWrapper = connection.getConnectionWrapper();
        connectionWrapper.connect( monitor );
        assertFalse( connectionWrapper.isConnected() );
        assertNotNull( monitor.getException() );
        assertTrue( monitor.getException() instanceof StudioLdapException );
        assertNotNull( monitor.getException().getCause() );
        assertTrue( monitor.getException().getCause() instanceof InvalidConnectionException );
        assertNotNull( monitor.getException().getCause().getCause() );
        assertTrue( monitor.getException().getCause().getCause() instanceof UnresolvedAddressException );

        // TODO: SSL, StartTLS
    }


    /**
     * Test binding to the server.
     */
    @Test
    public void testBind()
    {
        StudioProgressMonitor monitor = getProgressMonitor();
        ConnectionParameter connectionParameter = new ConnectionParameter( null, LOCALHOST, ldapServer.getPort(),
            EncryptionMethod.NONE, AuthenticationMethod.SIMPLE, "uid=admin,ou=system", "secret", null, true,
            null, 30000L );
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
        connectionParameter = new ConnectionParameter( null, LOCALHOST, ldapServer.getPort(), EncryptionMethod.NONE,
            AuthenticationMethod.SIMPLE, "uid=admin", "invalid", null, true, null, 30000L );
        connection = new Connection( connectionParameter );
        connectionWrapper = connection.getConnectionWrapper();
        connectionWrapper.connect( monitor );
        connectionWrapper.bind( monitor );
        assertFalse( connectionWrapper.isConnected() );
        assertNotNull( monitor.getException() );
        assertTrue( monitor.getException() instanceof StudioLdapException );
        assertTrue( monitor.getException().getMessage().contains( "[LDAP result code 49 - invalidCredentials]" ) );
        assertTrue( monitor.getException().getMessage().contains( "INVALID_CREDENTIALS" ) );
        assertTrue( monitor.getException().getCause() instanceof LdapAuthenticationException );
        assertTrue( monitor.getException().getCause().getMessage().contains( "INVALID_CREDENTIALS" ) );

        // simple auth with invalid principal and credential
        monitor = getProgressMonitor();
        connectionParameter = new ConnectionParameter( null, LOCALHOST, ldapServer.getPort(), EncryptionMethod.NONE,
            AuthenticationMethod.SIMPLE, "uid=admin,ou=system", "bar", null, true, null, 30000L );
        connection = new Connection( connectionParameter );
        connectionWrapper = connection.getConnectionWrapper();
        connectionWrapper.connect( monitor );
        connectionWrapper.bind( monitor );
        assertFalse( connectionWrapper.isConnected() );
        assertNotNull( monitor.getException() );
        assertTrue( monitor.getException() instanceof StudioLdapException );
        assertTrue( monitor.getException().getMessage().contains( "[LDAP result code 49 - invalidCredentials]" ) );
        assertTrue( monitor.getException().getMessage().contains( "INVALID_CREDENTIALS" ) );
        assertTrue( monitor.getException().getCause() instanceof LdapAuthenticationException );
        assertTrue( monitor.getException().getCause().getMessage().contains( "INVALID_CREDENTIALS" ) );
    }


    /**
     * Test searching.
     */
    @Test
    public void testSearch() throws Exception
    {
        StudioProgressMonitor monitor = getProgressMonitor();
        SearchControls searchControls = new SearchControls();
        StudioSearchResultEnumeration result = getConnectionWrapper( monitor ).search( "ou=system", "(objectClass=*)",
            searchControls, AliasDereferencingMethod.NEVER, ReferralHandlingMethod.IGNORE, null, monitor, null );

        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        assertNotNull( result );
        assertTrue( result.hasMore() );
        assertNotNull( result.next() );
    }


    /**
     * Test binary attributes.
     */
    @Test
    public void testSearchBinaryAttributes() throws Exception
    {
        StudioProgressMonitor monitor = getProgressMonitor();
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope( SearchControls.OBJECT_SCOPE );
        StudioSearchResultEnumeration result = getConnectionWrapper( monitor ).search( "uid=admin,ou=system",
            "(objectClass=*)",
            searchControls, AliasDereferencingMethod.NEVER, ReferralHandlingMethod.IGNORE, null, monitor, null );

        assertNotNull( result );
        assertTrue( result.hasMore() );
        StudioSearchResult entry = result.next();
        assertNotNull( entry );

        Object userPasswordValue = entry.getEntry().get( "userPassword" ).getBytes();
        assertEquals( byte[].class, userPasswordValue.getClass() );
        assertEquals( "secret", new String( ( byte[] ) userPasswordValue, StandardCharsets.UTF_8 ) );
    }


    @Test
    public void testSearchContinuation_Follow_DirectReferral() throws Exception
    {
        StudioProgressMonitor monitor = getProgressMonitor();
        SearchControls searchControls = new SearchControls();
        StudioSearchResultEnumeration result = getConnectionWrapper( monitor ).search(
            "cn=referral1,ou=referrals,ou=system", "(objectClass=*)", searchControls, AliasDereferencingMethod.NEVER,
            ReferralHandlingMethod.FOLLOW, null, monitor, null );

        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        assertNotNull( result );

        List<String> dns = consume( result, sr -> sr.getDn().getName() );
        assertEquals( 1, dns.size() );
        assertEquals( "uid=user.1,ou=users,ou=system", dns.get( 0 ) );
    }


    @Test
    public void testSearchContinuation_Follow_IntermediateReferral() throws Exception
    {
        StudioProgressMonitor monitor = getProgressMonitor();
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope( SearchControls.SUBTREE_SCOPE );
        StudioSearchResultEnumeration result = getConnectionWrapper( monitor ).search(
            "cn=referral2,ou=referrals,ou=system", "(objectClass=*)", searchControls, AliasDereferencingMethod.NEVER,
            ReferralHandlingMethod.FOLLOW, null, monitor, null );

        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        assertNotNull( result );

        List<String> dns = consume( result, sr -> sr.getDn().getName() );
        assertEquals( 2, dns.size() );
        assertThat( dns, hasItems( "ou=users,ou=system", "uid=user.1,ou=users,ou=system" ) );
    }


    @Test
    public void testSearchContinuation_Follow_ReferralToParent() throws Exception
    {
        StudioProgressMonitor monitor = getProgressMonitor();
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope( SearchControls.SUBTREE_SCOPE );
        StudioSearchResultEnumeration result = getConnectionWrapper( monitor ).search(
            "cn=referral3,ou=referrals,ou=system", "(objectClass=*)", searchControls, AliasDereferencingMethod.NEVER,
            ReferralHandlingMethod.FOLLOW, null, monitor, null );

        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        assertNotNull( result );

        List<String> dns = consume( result, sr -> sr.getDn().getName() );
        assertEquals( 3, dns.size() );
        assertThat( dns, hasItems( "ou=referrals,ou=system", "ou=users,ou=system", "uid=user.1,ou=users,ou=system" ) );
    }


    @Test
    public void testSearchContinuation_Follow_ReferralLoop() throws Exception
    {
        StudioProgressMonitor monitor = getProgressMonitor();
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope( SearchControls.SUBTREE_SCOPE );
        StudioSearchResultEnumeration result = getConnectionWrapper( monitor ).search(
            "cn=referral4a,ou=referrals,ou=system", "(objectClass=*)", searchControls, AliasDereferencingMethod.NEVER,
            ReferralHandlingMethod.FOLLOW, null, monitor, null );

        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        assertNotNull( result );

        List<String> dns = consume( result, sr -> sr.getDn().getName() );
        assertEquals( 0, dns.size() );
    }


    @Test
    public void testSearchContinuationFollowManually() throws Exception
    {
        StudioProgressMonitor monitor = getProgressMonitor();
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope( SearchControls.SUBTREE_SCOPE );
        ConnectionWrapper connectionWrapper = getConnectionWrapper( monitor );
        ConnectionCorePlugin.getDefault().setReferralHandler( null );
        StudioSearchResultEnumeration result = connectionWrapper.search( "ou=referrals,ou=system", "(objectClass=*)",
            searchControls, AliasDereferencingMethod.NEVER, ReferralHandlingMethod.FOLLOW_MANUALLY, null, monitor,
            null );

        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        assertNotNull( result );

        List<String> dns = consume( result, sr -> sr.getDn().getName() );
        assertEquals( 6, dns.size() );
        assertThat( dns,
            hasItems( "ou=referrals,ou=system", "ou=users,ou=system", "cn=referral1,ou=referrals,ou=system",
                "cn=referral4a,ou=referrals,ou=system", "cn=referral4b,ou=referrals,ou=system" ) );
    }


    @Test
    public void testSearchContinuationIgnore() throws Exception
    {
        StudioProgressMonitor monitor = getProgressMonitor();
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope( SearchControls.SUBTREE_SCOPE );
        StudioSearchResultEnumeration result = getConnectionWrapper( monitor ).search( "ou=referrals,ou=system",
            "(objectClass=*)", searchControls, AliasDereferencingMethod.NEVER, ReferralHandlingMethod.IGNORE, null,
            monitor, null );

        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        assertNotNull( result );

        List<String> dns = consume( result, sr -> sr.getDn().getName() );
        assertEquals( 1, dns.size() );
        assertThat( dns, hasItems( "ou=referrals,ou=system" ) );
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


    @Test
    public void testAdd() throws Exception
    {
        String dn = "uid=user.X,ou=users,ou=system";

        StudioProgressMonitor monitor = getProgressMonitor();
        Entry entry = new DefaultEntry( dn, "objectClass: inetOrgPerson", "sn: X", "cn: X", "uid: user.X" );
        getConnectionWrapper( monitor ).createEntry( entry, null, monitor, null );

        // should have created entry
        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        assertTrue( service.getAdminSession().exists( dn ) );
    }


    @Test
    public void testAddFollowsReferral_DirectReferral() throws Exception
    {
        String targetDn = "uid=user.X,ou=users,ou=system";
        String referralDn = "uid=user.X,cn=referral1,ou=referrals,ou=system";

        // create entry under referral
        StudioProgressMonitor monitor = getProgressMonitor();
        Entry entry = new DefaultEntry( referralDn, "objectClass: inetOrgPerson", "sn: X", "cn: X", "uid: user.X" );
        getConnectionWrapper( monitor ).createEntry( entry, null, monitor, null );

        // should have created target entry
        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        assertTrue( service.getAdminSession().exists( targetDn ) );
    }


    @Test
    public void testAddFollowsReferral_IntermediateReferral() throws Exception
    {
        String targetDn = "uid=user.X,ou=users,ou=system";
        String referralDn = "uid=user.X,cn=referral2,ou=referrals,ou=system";

        // create entry under referral
        StudioProgressMonitor monitor = getProgressMonitor();
        Entry entry = new DefaultEntry( referralDn, "objectClass: inetOrgPerson", "sn: X", "cn: X", "uid: user.X" );
        getConnectionWrapper( monitor ).createEntry( entry, null, monitor, null );

        // should have created target entry
        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        assertTrue( service.getAdminSession().exists( targetDn ) );
    }


    @Test
    public void testAddFollowsReferral_ReferralLoop() throws Exception
    {
        String referralDn = "uid=user.X,cn=referral4a,ou=referrals,ou=system";

        // create entry under referral
        StudioProgressMonitor monitor = getProgressMonitor();
        Entry entry = new DefaultEntry( referralDn, "objectClass: inetOrgPerson", "sn: X", "cn: X", "uid: user.X" );
        getConnectionWrapper( monitor ).createEntry( entry, null, monitor, null );

        // should not have created target entry
        assertFalse( monitor.isCanceled() );
        assertTrue( monitor.errorsReported() );
        assertNotNull( monitor.getException() );
        assertTrue( monitor.getException() instanceof StudioLdapException );
        assertTrue( monitor.getException().getMessage().contains( "[LDAP result code 54 - loopDetect]" ) );
        assertTrue( monitor.getException().getMessage().contains( "already processed" ) );
        assertTrue( monitor.getException().getCause() instanceof LdapLoopDetectedException );
    }


    @Test
    public void testModify() throws Exception
    {
        String dn = "uid=user.X,ou=users,ou=system";

        // create entry
        service.getAdminSession().add( new DefaultEntry( service.getSchemaManager(), dn,
            "objectClass: inetOrgPerson", "sn: X", "cn: X", "uid: user.X" ) );

        // modify entry
        StudioProgressMonitor monitor = getProgressMonitor();
        List<Modification> modifications = Collections.singletonList(
            new DefaultModification( ModificationOperation.REPLACE_ATTRIBUTE,
                new DefaultAttribute( "sn", "modified" ) ) );
        getConnectionWrapper( monitor ).modifyEntry( new Dn( dn ), modifications, null, monitor, null );

        // should have modified the entry
        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        Entry entry = service.getAdminSession().lookup( new Dn( dn ) );
        assertEquals( "modified", entry.get( "sn" ).getString() );
    }


    @Test
    public void testModifyFollowsReferral_DirectReferral() throws Exception
    {
        String targetDn = "uid=user.X,ou=users,ou=system";
        String referralDn = "uid=user.X,cn=referral1,ou=referrals,ou=system";

        // create target entry
        service.getAdminSession().add( new DefaultEntry( service.getSchemaManager(), targetDn,
            "objectClass: inetOrgPerson", "sn: X", "cn: X", "uid: user.X" ) );

        // modify referral entry 
        StudioProgressMonitor monitor = getProgressMonitor();
        List<Modification> modifications = Collections.singletonList(
            new DefaultModification( ModificationOperation.REPLACE_ATTRIBUTE,
                new DefaultAttribute( "sn", "modified" ) ) );
        getConnectionWrapper( monitor ).modifyEntry( new Dn( referralDn ), modifications, null, monitor, null );

        // should have modified the target entry
        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        Entry entry = service.getAdminSession().lookup( new Dn( targetDn ) );
        assertEquals( "modified", entry.get( "sn" ).getString() );
    }


    @Test
    public void testModifyFollowsReferral_IntermediateReferral() throws Exception
    {
        String targetDn = "uid=user.X,ou=users,ou=system";
        String referralDn = "uid=user.X,cn=referral2,ou=referrals,ou=system";

        // create target entry
        service.getAdminSession().add( new DefaultEntry( service.getSchemaManager(), targetDn,
            "objectClass: inetOrgPerson", "sn: X", "cn: X", "uid: user.X" ) );

        // modify referral entry 
        StudioProgressMonitor monitor = getProgressMonitor();
        List<Modification> modifications = Collections.singletonList(
            new DefaultModification( ModificationOperation.REPLACE_ATTRIBUTE,
                new DefaultAttribute( "sn", "modified" ) ) );
        getConnectionWrapper( monitor ).modifyEntry( new Dn( referralDn ), modifications, null, monitor, null );

        // should have modified the target entry
        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        Entry entry = service.getAdminSession().lookup( new Dn( targetDn ) );
        assertEquals( "modified", entry.get( "sn" ).getString() );
    }


    @Test
    public void testModifyFollowsReferral_ReferralLoop() throws Exception
    {
        String targetDn = "uid=user.X,ou=users,ou=system";
        String referralDn = "uid=user.X,cn=referral4a,ou=referrals,ou=system";

        // create target entry
        service.getAdminSession().add( new DefaultEntry( service.getSchemaManager(), targetDn,
            "objectClass: inetOrgPerson", "sn: X", "cn: X", "uid: user.X" ) );

        // modify referral entry 
        StudioProgressMonitor monitor = getProgressMonitor();
        List<Modification> modifications = Collections.singletonList(
            new DefaultModification( ModificationOperation.REPLACE_ATTRIBUTE,
                new DefaultAttribute( "sn", "modified" ) ) );
        getConnectionWrapper( monitor ).modifyEntry( new Dn( referralDn ), modifications, null, monitor, null );

        // should not have modified the target entry
        assertFalse( monitor.isCanceled() );
        assertTrue( monitor.errorsReported() );
        assertNotNull( monitor.getException() );
        assertTrue( monitor.getException() instanceof StudioLdapException );
        assertTrue( monitor.getException().getMessage().contains( "[LDAP result code 54 - loopDetect]" ) );
        assertTrue( monitor.getException().getMessage().contains( "already processed" ) );
        assertTrue( monitor.getException().getCause() instanceof LdapLoopDetectedException );
    }


    @Test
    public void testDelete() throws Exception
    {
        String dn = "uid=user.X,ou=users,ou=system";

        // create entry
        service.getAdminSession().add( new DefaultEntry( service.getSchemaManager(), dn,
            "objectClass: inetOrgPerson", "sn: X", "cn: X", "uid: user.X" ) );

        // delete entry
        StudioProgressMonitor monitor = getProgressMonitor();
        getConnectionWrapper( monitor ).deleteEntry( new Dn( dn ), null, monitor, null );

        // should have deleted the entry
        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        assertFalse( service.getAdminSession().exists( dn ) );
    }


    @Test
    public void testDeleteFollowsReferral_DirectReferral() throws Exception
    {
        String targetDn = "uid=user.X,ou=users,ou=system";
        String referralDn = "uid=user.X,cn=referral1,ou=referrals,ou=system";

        // create target entry
        service.getAdminSession().add( new DefaultEntry( service.getSchemaManager(), targetDn,
            "objectClass: inetOrgPerson", "sn: X", "cn: X", "uid: user.X" ) );

        // delete referral entry 
        StudioProgressMonitor monitor = getProgressMonitor();
        getConnectionWrapper( monitor ).deleteEntry( new Dn( referralDn ), null, monitor, null );

        // should have deleted the target entry
        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        assertFalse( service.getAdminSession().exists( targetDn ) );
    }


    @Test
    public void testDeleteFollowsReferral_IntermediateReferral() throws Exception
    {
        String targetDn = "uid=user.X,ou=users,ou=system";
        String referralDn = "uid=user.X,cn=referral2,ou=referrals,ou=system";

        // create target entry
        service.getAdminSession().add( new DefaultEntry( service.getSchemaManager(), targetDn,
            "objectClass: inetOrgPerson", "sn: X", "cn: X", "uid: user.X" ) );

        // delete referral entry 
        StudioProgressMonitor monitor = getProgressMonitor();
        getConnectionWrapper( monitor ).deleteEntry( new Dn( referralDn ), null, monitor, null );

        // should have deleted the target entry
        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        assertFalse( service.getAdminSession().exists( targetDn ) );
    }


    @Test
    public void testDeleteFollowsReferral_ReferralLoop() throws Exception
    {
        String targetDn = "uid=user.X,ou=users,ou=system";
        String referralDn = "uid=user.X,cn=referral4a,ou=referrals,ou=system";

        // create target entry
        service.getAdminSession().add( new DefaultEntry( service.getSchemaManager(), targetDn,
            "objectClass: inetOrgPerson", "sn: X", "cn: X", "uid: user.X" ) );

        // delete referral entry 
        StudioProgressMonitor monitor = getProgressMonitor();
        getConnectionWrapper( monitor ).deleteEntry( new Dn( referralDn ), null, monitor, null );

        // should not have deleted the target entry
        assertFalse( monitor.isCanceled() );
        assertTrue( monitor.errorsReported() );
        assertNotNull( monitor.getException() );
        assertTrue( monitor.getException() instanceof StudioLdapException );
        assertTrue( monitor.getException().getMessage().contains( "[LDAP result code 54 - loopDetect]" ) );
        assertTrue( monitor.getException().getMessage().contains( "already processed" ) );
        assertTrue( monitor.getException().getCause() instanceof LdapLoopDetectedException );
        assertTrue( service.getAdminSession().exists( targetDn ) );
    }


    @Test
    public void testSearchContinuationFollowParent() throws Exception
    {
        StudioProgressMonitor monitor = getProgressMonitor();
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope( SearchControls.SUBTREE_SCOPE );
        StudioSearchResultEnumeration result = getConnectionWrapper( monitor ).search( "ou=referrals,ou=system",
            "(objectClass=*)", searchControls, AliasDereferencingMethod.NEVER, ReferralHandlingMethod.FOLLOW, null,
            monitor, null );

        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        assertNotNull( result );

        List<String> dns = consume( result, sr -> sr.getDn().getName() );
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
            EncryptionMethod.NONE, AuthenticationMethod.SIMPLE,
            "uid=admin,ou=system", "secret", null, true, null, 30000L );
        Connection connection = new Connection( connectionParameter );
        BrowserConnection browserConnection = new BrowserConnection( connection );

        assertFalse( browserConnection.getRootDSE().isAttributesInitialized() );

        InitializeRootDSERunnable.loadRootDSE( browserConnection, monitor );

        assertTrue( browserConnection.getRootDSE().isAttributesInitialized() );
    }


    /**
     * DIRSTUDIO-1039
     */
    @Ignore
    @Test
    public void testConcurrentUseAndCloseOfConnection() throws Exception
    {
        final StudioProgressMonitor monitor = getProgressMonitor();
        final ConnectionParameter connectionParameter = new ConnectionParameter( null, LOCALHOST, ldapServer.getPort(),
            EncryptionMethod.NONE, AuthenticationMethod.SIMPLE,
            "uid=admin,ou=system", "secret", null, true, null, 30000L );
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


    @Test
    public void testPasswordModifyRequestExtendedOperation_AdminChangesUserPassword() throws Exception
    {
        String dn = "uid=user.X,ou=users,ou=system";

        // create target entry
        service.getAdminSession().add( new DefaultEntry( service.getSchemaManager(), dn,
            "objectClass: inetOrgPerson", "sn: X", "cn: X", "uid: user.X", "userPassword:  secret" ) );

        // modify password
        LdapApiService ldapApiService = LdapApiServiceFactory.getSingleton();
        PasswordModifyRequest request = ( PasswordModifyRequest ) ldapApiService.getExtendedRequestFactories()
            .get( PasswordModifyRequest.EXTENSION_OID ).newRequest();
        request.setUserIdentity( Strings.getBytesUtf8( dn ) );
        request.setNewPassword( Strings.getBytesUtf8( "s3cre3t" ) );
        StudioProgressMonitor monitor = getProgressMonitor();
        ExtendedResponse response = getConnectionWrapper( monitor ).extended( request, monitor );

        // should have modified password of the target entry
        assertEquals( ResultCodeEnum.SUCCESS, response.getLdapResult().getResultCode() );
        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        Entry entry = service.getAdminSession().lookup( new Dn( dn ) );
        assertEquals( "s3cre3t", Strings.utf8ToString( entry.get( "userPassword" ).getBytes() ) );
    }


    @Test
    public void testPasswordModifyRequestExtendedOperation_UserChangesOwnPassword() throws Exception
    {
        LdapApiService ldapApiService = LdapApiServiceFactory.getSingleton();
        String dn = "uid=user.X,ou=users,ou=system";

        // create target entry
        service.getAdminSession().add( new DefaultEntry( service.getSchemaManager(), dn,
            "objectClass: inetOrgPerson", "sn: X", "cn: X", "uid: user.X", "userPassword:  secret" ) );

        // modify password with wrong old password
        PasswordModifyRequest request1 = ( PasswordModifyRequest ) ldapApiService.getExtendedRequestFactories()
            .get( PasswordModifyRequest.EXTENSION_OID ).newRequest();
        request1.setUserIdentity( Strings.getBytesUtf8( dn ) );
        request1.setOldPassword( Strings.getBytesUtf8( "wrong" ) );
        request1.setNewPassword( Strings.getBytesUtf8( "s3cre3t" ) );
        StudioProgressMonitor monitor1 = getProgressMonitor();
        ExtendedResponse response1 = getConnectionWrapper( monitor1, dn, "secret" ).extended( request1, monitor1 );

        // should not have modified password of the target entry
        assertEquals( ResultCodeEnum.INVALID_CREDENTIALS, response1.getLdapResult().getResultCode() );
        assertFalse( monitor1.isCanceled() );
        assertTrue( monitor1.errorsReported() );
        Entry entry1 = service.getAdminSession().lookup( new Dn( dn ) );
        assertEquals( "secret", Strings.utf8ToString( entry1.get( "userPassword" ).getBytes() ) );

        // modify password with correct old password
        PasswordModifyRequest request2 = ( PasswordModifyRequest ) ldapApiService.getExtendedRequestFactories()
            .get( PasswordModifyRequest.EXTENSION_OID ).newRequest();
        request2.setUserIdentity( Strings.getBytesUtf8( dn ) );
        request2.setOldPassword( Strings.getBytesUtf8( "secret" ) );
        request2.setNewPassword( Strings.getBytesUtf8( "s3cre3t" ) );
        StudioProgressMonitor monitor2 = getProgressMonitor();
        ExtendedResponse response2 = getConnectionWrapper( monitor2, dn, "secret" ).extended( request2, monitor2 );

        // should have modified password of the target entry
        assertEquals( ResultCodeEnum.SUCCESS, response2.getLdapResult().getResultCode() );
        assertFalse( monitor2.isCanceled() );
        assertFalse( monitor2.errorsReported() );
        Entry entry2 = service.getAdminSession().lookup( new Dn( dn ) );
        assertEquals( "s3cre3t", Strings.utf8ToString( entry2.get( "userPassword" ).getBytes() ) );
    }


    @Ignore
    @Test
    public void testWhoAmIExtendedOperation() throws Exception
    {
        LdapApiService ldapApiService = LdapApiServiceFactory.getSingleton();
        WhoAmIRequest request = ( WhoAmIRequest ) ldapApiService.getExtendedRequestFactories()
            .get( WhoAmIRequest.EXTENSION_OID ).newRequest();
        StudioProgressMonitor monitor = getProgressMonitor();
        WhoAmIResponse response = ( WhoAmIResponse ) getConnectionWrapper( monitor ).extended( request, monitor );

        assertEquals( ResultCodeEnum.SUCCESS, response.getLdapResult().getResultCode() );
        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        assertTrue( response.isDnAuthzId() );
        assertEquals( "uid=admin,ou=system", response.getDn().toString() );
    }

    /*
    @Ignore
    @Test
    public void testStartEndTransactionExtendedOperation() throws Exception
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


    protected ConnectionWrapper getConnectionWrapper( StudioProgressMonitor monitor )
    {
        return getConnectionWrapper( monitor, "uid=admin,ou=system", "secret" );
    }


    protected ConnectionWrapper getConnectionWrapper( StudioProgressMonitor monitor, String dn, String password )
    {
        // simple auth without principal and credential
        ConnectionParameter connectionParameter = new ConnectionParameter( null, LOCALHOST, ldapServer.getPort(),
            EncryptionMethod.NONE, AuthenticationMethod.SIMPLE, dn, password, null, false, null, 30000L );

        Connection connection = new Connection( connectionParameter );

        connectionWrapper = connection.getConnectionWrapper();
        connectionWrapper.connect( monitor );
        connectionWrapper.bind( monitor );

        assertTrue( connectionWrapper.isConnected() );

        IReferralHandler referralHandler = referralUrls -> {
            return connection;
        };
        ConnectionCorePlugin.getDefault().setReferralHandler( referralHandler );

        assertTrue( connectionWrapper.isConnected() );
        assertNull( monitor.getException() );

        return connectionWrapper;
    }

}
