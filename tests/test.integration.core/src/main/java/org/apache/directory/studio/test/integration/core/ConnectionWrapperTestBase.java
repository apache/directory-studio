
package org.apache.directory.studio.test.integration.core;


import static org.apache.directory.studio.test.integration.core.Constants.LOCALHOST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.nio.channels.UnresolvedAddressException;

import javax.naming.AuthenticationException;
import javax.naming.CommunicationException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.directory.api.ldap.model.entry.AttributeUtils;
import org.apache.directory.api.ldap.model.entry.DefaultAttribute;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
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
import org.apache.directory.studio.connection.core.ConnectionParameter.NetworkProvider;
import org.apache.directory.studio.connection.core.IReferralHandler;
import org.apache.directory.studio.connection.core.io.ConnectionWrapper;
import org.apache.mina.util.AvailablePortFinder;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(FrameworkRunner.class)
@CreateLdapServer(transports =
    { @CreateTransport(protocol = "LDAP"), @CreateTransport(protocol = "LDAPS") })
@ApplyLdifFiles(clazz = ConnectionWrapperTestBase.class, value = "org/apache/directory/studio/test/integration/core/TestData.ldif")
public abstract class ConnectionWrapperTestBase extends AbstractLdapTestUnit
{

    protected NetworkProvider provider;


    public ConnectionWrapperTestBase( NetworkProvider provider )
    {
        this.provider = provider;
    }


    @Before
    public void setUp() throws Exception
    {
        // create referral entry
        Entry entry = new DefaultEntry( getService().getSchemaManager() );
        entry.setDn( new Dn( "cn=referral,ou=system" ) );
        entry.add( "objectClass", "top", "referral", "extensibleObject" );
        entry.add( "cn", "referral" );
        entry.add( "ref", "ldap://" + LOCALHOST + ":" + ldapServer.getPort() + "/ou=users,ou=system" );
        service.getAdminSession().add( entry );
    }


    /**
     * Tests connecting to the server.
     */
    @Test
    public void testConnect()
    {
        StudioProgressMonitor monitor = getProgressMonitor();
        ConnectionParameter connectionParameter = new ConnectionParameter( null, LOCALHOST, ldapServer.getPort(),
            EncryptionMethod.NONE, NetworkProvider.JNDI, AuthenticationMethod.NONE, null, null, null, true, null );
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
            EncryptionMethod.NONE, provider, AuthenticationMethod.NONE, null, null, null, true, null );
        connection = new Connection( connectionParameter );
        connectionWrapper = connection.getConnectionWrapper();
        connectionWrapper.connect( monitor );
        assertFalse( connectionWrapper.isConnected() );
        assertNotNull( monitor.getException() );
        if ( provider == NetworkProvider.JNDI )
        {
            assertTrue( monitor.getException() instanceof CommunicationException );
            assertNotNull( monitor.getException().getCause() );
            assertTrue( monitor.getException().getCause() instanceof ConnectException );
        }
        if ( provider == NetworkProvider.APACHE_DIRECTORY_LDAP_API )
        {
            assertTrue( monitor.getException() instanceof InvalidConnectionException );
            assertNotNull( monitor.getException().getCause() );
            assertTrue( monitor.getException().getCause() instanceof ConnectException );
        }

        // unknown host
        monitor = getProgressMonitor();
        connectionParameter = new ConnectionParameter( null, "555.555.555.555", ldapServer.getPort(),
            EncryptionMethod.NONE, provider, AuthenticationMethod.NONE, null, null, null, true, null );
        connection = new Connection( connectionParameter );
        connectionWrapper = connection.getConnectionWrapper();
        connectionWrapper.connect( monitor );
        assertFalse( connectionWrapper.isConnected() );
        assertNotNull( monitor.getException() );
        if ( provider == NetworkProvider.JNDI )
        {
            assertTrue( monitor.getException() instanceof CommunicationException );
            assertNotNull( monitor.getException().getCause() );
            assertTrue( monitor.getException().getCause() instanceof UnknownHostException );
        }
        if ( provider == NetworkProvider.APACHE_DIRECTORY_LDAP_API )
        {
            assertTrue( monitor.getException() instanceof InvalidConnectionException );
            assertNotNull( monitor.getException().getCause() );
            assertTrue( monitor.getException().getCause() instanceof UnresolvedAddressException );
        }

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
            EncryptionMethod.NONE, provider, AuthenticationMethod.SIMPLE, "uid=admin,ou=system", "secret", null, true,
            null );
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
            provider, AuthenticationMethod.SIMPLE, "uid=admin", "invalid", null, true, null );
        connection = new Connection( connectionParameter );
        connectionWrapper = connection.getConnectionWrapper();
        connectionWrapper.connect( monitor );
        connectionWrapper.bind( monitor );
        assertFalse( connectionWrapper.isConnected() );
        assertNotNull( monitor.getException() );
        if ( provider == NetworkProvider.JNDI )
        {
            assertTrue( monitor.getException() instanceof NamingException );
        }
        if ( provider == NetworkProvider.APACHE_DIRECTORY_LDAP_API )
        {
            assertTrue( monitor.getException() instanceof Exception );
            assertTrue( monitor.getException().getMessage().contains( "error code 49 - INVALID_CREDENTIALS" ) );
        }

        // simple auth with invalid principal and credential
        monitor = getProgressMonitor();
        connectionParameter = new ConnectionParameter( null, LOCALHOST, ldapServer.getPort(), EncryptionMethod.NONE,
            provider, AuthenticationMethod.SIMPLE, "uid=admin,ou=system", "bar", null, true, null );
        connection = new Connection( connectionParameter );
        connectionWrapper = connection.getConnectionWrapper();
        connectionWrapper.connect( monitor );
        connectionWrapper.bind( monitor );
        assertFalse( connectionWrapper.isConnected() );
        assertNotNull( monitor.getException() );
        if ( provider == NetworkProvider.JNDI )
        {
            assertTrue( monitor.getException() instanceof AuthenticationException );
        }
        if ( provider == NetworkProvider.APACHE_DIRECTORY_LDAP_API )
        {
            assertTrue( monitor.getException() instanceof Exception );
            assertTrue( monitor.getException().getMessage().contains( "error code 49 - INVALID_CREDENTIALS" ) );
        }
    }


    /**
     * Test searching.
     */
    @Test
    public void testSearch() throws NamingException
    {
        StudioProgressMonitor monitor = getProgressMonitor();
        SearchControls searchControls = new SearchControls();
        NamingEnumeration<SearchResult> result = getConnectionWrapper( monitor ).search( "ou=system", "(objectClass=*)",
            searchControls, AliasDereferencingMethod.NEVER, ReferralHandlingMethod.IGNORE, null, monitor, null );

        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        assertNotNull( result );
        assertTrue( result.hasMore() );
        SearchResult entry = result.next();
        assertNotNull( entry );
    }


    @Test
    public void testSearchContinuation() throws NamingException
    {
        StudioProgressMonitor monitor = getProgressMonitor();
        SearchControls searchControls = new SearchControls();
        NamingEnumeration<SearchResult> result = getConnectionWrapper( monitor ).search( "cn=referral,ou=system",
            "(objectClass=*)", searchControls, AliasDereferencingMethod.NEVER, ReferralHandlingMethod.FOLLOW, null,
            monitor, null );

        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        assertNotNull( result );
        assertTrue( result.hasMore() );
        SearchResult entry = result.next();
        assertNotNull( entry );
        assertEquals( "uid=user.1,ou=users,ou=system", entry.getNameInNamespace() );
    }


    @Test
    public void testAddFollowsReferral() throws Exception
    {
        String targetDn = "uid=user.X,ou=users,ou=system";
        String referralDn = "uid=user.X,cn=referral,ou=system";

        // create entry under referral
        StudioProgressMonitor monitor = getProgressMonitor();
        Attributes attributes = AttributeUtils.toAttributes(
            new DefaultEntry( referralDn, "objectClass: inetOrgPerson", "sn: X", "cn: X", "uid: user.X" ) );
        getConnectionWrapper( monitor ).createEntry( referralDn, attributes, null, monitor, null );

        // should have created target entry
        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        assertTrue( service.getAdminSession().exists( targetDn ) );
    }


    @Test
    public void testModifyFollowsReferral() throws Exception
    {
        String targetDn = "uid=user.X,ou=users,ou=system";
        String referralDn = "uid=user.X,cn=referral,ou=system";

        // create target entry
        service.getAdminSession().add( new DefaultEntry( service.getSchemaManager(), targetDn,
            "objectClass: inetOrgPerson", "sn: X", "cn: X", "uid: user.X" ) );

        // modify referral entry 
        StudioProgressMonitor monitor = getProgressMonitor();
        ModificationItem[] modificationItems =
            { new ModificationItem( DirContext.REPLACE_ATTRIBUTE,
                AttributeUtils.toJndiAttribute( new DefaultAttribute( "sn", "modified" ) ) ) };
        getConnectionWrapper( monitor ).modifyEntry( referralDn, modificationItems, null, monitor, null );

        // should have modified the target entry
        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        Entry entry = service.getAdminSession().lookup( new Dn( targetDn ) );
        assertEquals( "modified", entry.get( "sn" ).getString() );
    }


    @Test
    public void testDeleteFollowsReferral() throws Exception
    {
        String targetDn = "uid=user.X,ou=users,ou=system";
        String referralDn = "uid=user.X,cn=referral,ou=system";

        // create target entry
        service.getAdminSession().add( new DefaultEntry( service.getSchemaManager(), targetDn,
            "objectClass: inetOrgPerson", "sn: X", "cn: X", "uid: user.X" ) );

        // delete referral entry 
        StudioProgressMonitor monitor = getProgressMonitor();
        getConnectionWrapper( monitor ).deleteEntry( referralDn, null, monitor, null );

        // should have deleted the target entry
        assertFalse( monitor.isCanceled() );
        assertFalse( monitor.errorsReported() );
        assertFalse( service.getAdminSession().exists( targetDn ) );
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
            EncryptionMethod.NONE, provider, AuthenticationMethod.SIMPLE, "uid=admin,ou=system", "secret", null, false,
            null );

        Connection connection = new Connection( connectionParameter );

        ConnectionWrapper connectionWrapper = connection.getConnectionWrapper();
        connectionWrapper.connect( monitor );
        connectionWrapper.bind( monitor );

        IReferralHandler referralHandler = referralUrls -> {
            return connection;
        };
        ConnectionCorePlugin.getDefault().setReferralHandler( referralHandler );

        assertTrue( connectionWrapper.isConnected() );
        assertNull( monitor.getException() );

        return connectionWrapper;
    }
}
