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

import java.io.ByteArrayInputStream;
import java.net.ConnectException;
import java.nio.channels.UnresolvedAddressException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import javax.naming.directory.SearchControls;

import org.apache.directory.api.ldap.model.entry.DefaultAttribute;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.DefaultModification;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Modification;
import org.apache.directory.api.ldap.model.entry.ModificationOperation;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapLoopDetectedException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.exception.InvalidConnectionException;
import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.server.core.integ.FrameworkRunner;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.apache.directory.studio.connection.core.ConnectionParameter.AuthenticationMethod;
import org.apache.directory.studio.connection.core.ConnectionParameter.EncryptionMethod;
import org.apache.directory.studio.connection.core.IReferralHandler;
import org.apache.directory.studio.connection.core.io.ConnectionWrapper;
import org.apache.directory.studio.connection.core.io.StudioNamingEnumeration;
import org.apache.directory.studio.connection.core.io.api.StudioSearchResult;
import org.apache.mina.util.AvailablePortFinder;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Base class for {@link ConnectionWrapper} tests.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
@RunWith(FrameworkRunner.class)
@CreateLdapServer(transports =
    { @CreateTransport(protocol = "LDAP"), @CreateTransport(protocol = "LDAPS") })
@ApplyLdifFiles(clazz = ConnectionWrapperTestBase.class, value = "org/apache/directory/studio/test/integration/core/TestData.ldif")
public abstract class ConnectionWrapperTestBase extends AbstractLdapTestUnit
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
            EncryptionMethod.NONE, AuthenticationMethod.NONE, null, null, null, true, null, 30L );
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
            EncryptionMethod.NONE, AuthenticationMethod.NONE, null, null, null, true, null, 30L );
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
            EncryptionMethod.NONE, AuthenticationMethod.NONE, null, null, null, true, null, 30L );
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
        ConnectionParameter connectionParameter = new ConnectionParameter( null, LOCALHOST, ldapServer.getPort(),
            EncryptionMethod.NONE, AuthenticationMethod.SIMPLE, "uid=admin,ou=system", "secret", null, true,
            null, 30L );
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
            AuthenticationMethod.SIMPLE, "uid=admin", "invalid", null, true, null, 30L );
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
        connectionParameter = new ConnectionParameter( null, LOCALHOST, ldapServer.getPort(), EncryptionMethod.NONE,
            AuthenticationMethod.SIMPLE, "uid=admin,ou=system", "bar", null, true, null, 30L );
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
    public void testSearch() throws Exception
    {
        StudioProgressMonitor monitor = getProgressMonitor();
        SearchControls searchControls = new SearchControls();
        StudioNamingEnumeration result = getConnectionWrapper( monitor ).search( "ou=system", "(objectClass=*)",
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
        StudioNamingEnumeration result = getConnectionWrapper( monitor ).search( "uid=admin,ou=system",
            "(objectClass=*)",
            searchControls, AliasDereferencingMethod.NEVER, ReferralHandlingMethod.IGNORE, null, monitor, null );

        assertNotNull( result );
        assertTrue( result.hasMore() );
        StudioSearchResult entry = result.next();
        assertNotNull( entry );

        Object userCertificateValue = entry.getEntry().get( "userCertificate" ).getBytes();
        assertEquals( byte[].class, userCertificateValue.getClass() );

        CertificateFactory cf = CertificateFactory.getInstance( "X.509" ); //$NON-NLS-1$
        Certificate certificate = cf.generateCertificate( new ByteArrayInputStream( ( byte[] ) userCertificateValue ) );
        assertTrue( certificate instanceof X509Certificate );
        X509Certificate x509Certificate = ( X509Certificate ) certificate;
        assertTrue( x509Certificate.getIssuerDN().getName().contains( "ApacheDS" ) );
    }


    @Test
    public void testSearchContinuation_Follow_DirectReferral() throws Exception
    {
        StudioProgressMonitor monitor = getProgressMonitor();
        SearchControls searchControls = new SearchControls();
        StudioNamingEnumeration result = getConnectionWrapper( monitor ).search(
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
        StudioNamingEnumeration result = getConnectionWrapper( monitor ).search(
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
        StudioNamingEnumeration result = getConnectionWrapper( monitor ).search(
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
        StudioNamingEnumeration result = getConnectionWrapper( monitor ).search(
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
        StudioNamingEnumeration result = connectionWrapper.search( "ou=referrals,ou=system", "(objectClass=*)",
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
        StudioNamingEnumeration result = getConnectionWrapper( monitor ).search( "ou=referrals,ou=system",
            "(objectClass=*)", searchControls, AliasDereferencingMethod.NEVER, ReferralHandlingMethod.IGNORE, null,
            monitor, null );

        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        assertNotNull( result );

        List<String> dns = consume( result, sr -> sr.getDn().getName() );
        assertEquals( 1, dns.size() );
        assertThat( dns, hasItems( "ou=referrals,ou=system" ) );
    }


    protected <T> List<T> consume( StudioNamingEnumeration result, Function<StudioSearchResult, T> fn )
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
        assertTrue( monitor.getException() instanceof LdapLoopDetectedException );
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
        assertTrue( monitor.getException() instanceof LdapLoopDetectedException );
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
        assertTrue( monitor.getException() instanceof LdapLoopDetectedException );
        assertTrue( service.getAdminSession().exists( targetDn ) );
    }


    protected StudioProgressMonitor getProgressMonitor()
    {
        StudioProgressMonitor monitor = new StudioProgressMonitor( new NullProgressMonitor() );
        return monitor;
    }


    protected ConnectionWrapper getConnectionWrapper( StudioProgressMonitor monitor )
    {
        // simple auth without principal and credential
        ConnectionParameter connectionParameter = new ConnectionParameter( null, LOCALHOST, ldapServer.getPort(),
            EncryptionMethod.NONE, AuthenticationMethod.SIMPLE, "uid=admin,ou=system", "secret", null, false,
            null, 30000L );

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
