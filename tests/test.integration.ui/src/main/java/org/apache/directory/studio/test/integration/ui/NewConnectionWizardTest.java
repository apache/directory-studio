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

package org.apache.directory.studio.test.integration.ui;


import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.CoreSession;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.server.core.integ.FrameworkRunner;
import org.apache.directory.shared.ldap.entry.Entry;
import org.apache.directory.shared.ldap.entry.Modification;
import org.apache.directory.shared.ldap.message.ModifyRequest;
import org.apache.directory.shared.ldap.message.ModifyRequestImpl;
import org.apache.directory.shared.ldap.name.DN;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionManager;
import org.apache.directory.studio.connection.core.ConnectionParameter.AuthenticationMethod;
import org.apache.directory.studio.test.integration.ui.bots.CertificateTrustDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.ConnectionsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.ErrorDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.NewConnectionWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.StudioBot;
import org.bouncycastle.x509.X509V1CertificateGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests the new connection wizard.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
@RunWith(FrameworkRunner.class)
@CreateLdapServer(transports =
    { @CreateTransport(protocol = "LDAP") })
public class NewConnectionWizardTest extends AbstractLdapTestUnit
{
    private File ksFile;

    private StudioBot studioBot;
    private ConnectionsViewBot connectionsViewBot;
    private NewConnectionWizardBot wizardBot;


    @Before
    public void setUpLdaps() throws Exception
    {
        // TODO: setup LDAPS
        //        if ( ldapsService == null )
        //        {
        //            ldapsService = new LdapServer();
        //            ldapsService.setDirectoryService( ldapService.getDirectoryService() );
        //            int port = AvailablePortFinder.getNextAvailable( ldapService.getPort() + 10 );
        //            ldapsService.setTcpTransport( new TcpTransport( port ) );
        //            ldapsService.setEnabled( true );
        //            ldapsService.setEnableLdaps( true );
        //            ldapsService.setConfidentialityRequired( true );
        //            ldapsService.start();
        //        }
    }


    @Before
    public void setUp() throws Exception
    {
        studioBot = new StudioBot();
        studioBot.resetLdapPerspective();
        connectionsViewBot = studioBot.getConnectionView();

        // open the new connection wizard
        wizardBot = connectionsViewBot.openNewConnectionWizard();

        //ErrorDialog.AUTOMATED_MODE = false;
    }


    @After
    public void tearDown() throws Exception
    {
        connectionsViewBot.deleteTestConnections();
        studioBot = null;
        connectionsViewBot = null;
        wizardBot = null;

        // delete old key store
        if ( ksFile != null && ksFile.exists() )
        {
            ksFile.delete();
        }

        // delete custom trust stores
        X509Certificate[] permanentCertificates = ConnectionCorePlugin.getDefault().getPermanentTrustStoreManager()
            .getCertificates();
        for ( X509Certificate certificate : permanentCertificates )
        {
            ConnectionCorePlugin.getDefault().getPermanentTrustStoreManager().removeCertificate( certificate );
        }
        X509Certificate[] temporaryCertificates = ConnectionCorePlugin.getDefault().getSessionTrustStoreManager()
            .getCertificates();
        for ( X509Certificate certificate : temporaryCertificates )
        {
            ConnectionCorePlugin.getDefault().getSessionTrustStoreManager().removeCertificate( certificate );
        }

        // delete custom JNDI key store settings
        System.getProperties().remove( "javax.net.ssl.trustStore" );
        System.getProperties().remove( "javax.net.ssl.keyStore" );
        System.getProperties().remove( "javax.net.ssl.keyStorePassword" );
    }


    /**
     * Tests enabled and disabled widgets, depending on the provided input.
     */
    @Test
    public void testEnabledDisabledWidgets()
    {
        assertTrue( wizardBot.isVisible() );

        // ensure "Next >" and "Finish" buttons are disabled
        assertFalse( wizardBot.isBackButtonEnabled() );
        assertFalse( wizardBot.isNextButtonEnabled() );
        assertFalse( wizardBot.isFinishButtonEnabled() );
        assertTrue( wizardBot.isCancelButtonEnabled() );

        // enter connection parameter
        wizardBot.typeConnectionName( "NewConnectionWizardTest" );
        wizardBot.typeHost( "localhost" );
        wizardBot.typePort( ldapServer.getPort() );
        // ensure "Next >" button is enabled
        assertFalse( wizardBot.isBackButtonEnabled() );
        assertTrue( wizardBot.isNextButtonEnabled() );
        assertFalse( wizardBot.isFinishButtonEnabled() );
        assertTrue( wizardBot.isCancelButtonEnabled() );

        // clear host
        wizardBot.typeHost( "" );
        // ensure "Next >" is disabled
        assertFalse( wizardBot.isBackButtonEnabled() );
        assertFalse( wizardBot.isNextButtonEnabled() );
        assertFalse( wizardBot.isFinishButtonEnabled() );
        assertTrue( wizardBot.isCancelButtonEnabled() );

        // enter host again
        wizardBot.typeHost( "localhost" );
        // ensure "Next >" button is enabled
        assertFalse( wizardBot.isBackButtonEnabled() );
        assertTrue( wizardBot.isNextButtonEnabled() );
        assertFalse( wizardBot.isFinishButtonEnabled() );
        assertTrue( wizardBot.isCancelButtonEnabled() );

        wizardBot.clickNextButton();

        // check default settings
        assertTrue( wizardBot.isSimpleAuthenticationSelected() );
        assertTrue( wizardBot.isUserEnabled() );
        assertTrue( wizardBot.isPasswordEnabled() );
        assertFalse( wizardBot.isRealmEnabled() );
        assertTrue( wizardBot.isSavePasswordSelected() );
        // ensure "<Back" is enabled, "Next >" and "Finish" is disabled
        assertTrue( wizardBot.isBackButtonEnabled() );
        assertFalse( wizardBot.isNextButtonEnabled() );
        assertFalse( wizardBot.isFinishButtonEnabled() );
        assertTrue( wizardBot.isCancelButtonEnabled() );

        // enter authentication parameters
        wizardBot.selectSimpleAuthentication();
        wizardBot.typeUser( "uid=admin,ou=system" );
        wizardBot.typePassword( "secret" );
        // ensure "<Back" is enabled, "Next >" and "Finish" is enabled
        assertTrue( wizardBot.isBackButtonEnabled() );
        assertTrue( wizardBot.isNextButtonEnabled() );
        assertTrue( wizardBot.isFinishButtonEnabled() );
        assertTrue( wizardBot.isCancelButtonEnabled() );

        // clear user
        wizardBot.typeUser( "" );
        // ensure "<Back" is enabled, "Next >" and "Finish" is disabled
        assertTrue( wizardBot.isBackButtonEnabled() );
        assertFalse( wizardBot.isNextButtonEnabled() );
        assertFalse( wizardBot.isFinishButtonEnabled() );
        assertTrue( wizardBot.isCancelButtonEnabled() );

        // enter user again
        wizardBot.typeUser( "uid=admin,ou=system" );
        // ensure "<Back" is enabled, "Next >" and "Finish" is enabled
        assertTrue( wizardBot.isBackButtonEnabled() );
        assertTrue( wizardBot.isNextButtonEnabled() );
        assertTrue( wizardBot.isFinishButtonEnabled() );
        assertTrue( wizardBot.isCancelButtonEnabled() );

        // deselect password save
        wizardBot.deselectSavePassword();
        // ensure password field is disabled
        assertFalse( wizardBot.isPasswordEnabled() );

        // select password save
        wizardBot.selectSavePassword();
        // ensure password field is enabled
        assertTrue( wizardBot.isPasswordEnabled() );

        // select no authentication
        wizardBot.selectNoAuthentication();
        // ensure authentication parameter input fields are disabled
        assertTrue( wizardBot.isNoAuthenticationSelected() );
        assertFalse( wizardBot.isUserEnabled() );
        assertFalse( wizardBot.isPasswordEnabled() );
        assertFalse( wizardBot.isRealmEnabled() );
        assertFalse( wizardBot.isSavePasswordEnabled() );

        // select DIGEST-MD5
        wizardBot.selectDigestMD5Authentication();
        // ensure authentication parameter input fields are enabled, including SASL Realm field
        assertTrue( wizardBot.isDigestMD5AuthenticationSelected() );
        assertTrue( wizardBot.isUserEnabled() );
        assertTrue( wizardBot.isPasswordEnabled() );
        assertTrue( wizardBot.isRealmEnabled() );
        assertTrue( wizardBot.isSavePasswordEnabled() );

        // select CRAM-MD5
        wizardBot.selectCramMD5Authentication();
        // ensure authentication parameter input fields are enabled, excluding SASL Realm field
        assertTrue( wizardBot.isCramMD5AuthenticationSelected() );
        assertTrue( wizardBot.isUserEnabled() );
        assertTrue( wizardBot.isPasswordEnabled() );
        assertFalse( wizardBot.isRealmEnabled() );
        assertTrue( wizardBot.isSavePasswordEnabled() );

        wizardBot.clickNextButton();

        // check default settings
        assertTrue( wizardBot.isGetBaseDnsFromRootDseSelected() );
        assertFalse( wizardBot.isBaseDnEnabled() );
        // ensure "<Back" and "Finish" is enabled, "Next >" enabled
        assertTrue( wizardBot.isBackButtonEnabled() );
        assertTrue( wizardBot.isNextButtonEnabled() );
        assertTrue( wizardBot.isFinishButtonEnabled() );
        assertTrue( wizardBot.isCancelButtonEnabled() );

        // deselect get base DNs from Root DSE
        wizardBot.deselectGetBaseDnsFromRootDse();
        assertFalse( wizardBot.isGetBaseDnsFromRootDseSelected() );
        assertTrue( wizardBot.isBaseDnEnabled() );

        // select get base DNs from Root DSE
        wizardBot.selectGetBaseDnsFromRootDse();
        assertTrue( wizardBot.isGetBaseDnsFromRootDseSelected() );
        assertFalse( wizardBot.isBaseDnEnabled() );

        wizardBot.clickCancelButton();
    }


    /**
     * Creates a new connection using the new connection wizard.
     */
    @Test
    public void testCreateConnection()
    {
        // enter connection parameter
        wizardBot.typeConnectionName( "NewConnectionWizardTest" );
        wizardBot.typeHost( "localhost" );
        wizardBot.typePort( ldapServer.getPort() );

        // jump to auth page
        wizardBot.clickNextButton();

        // enter authentication parameters
        wizardBot.typeUser( "uid=admin,ou=system" );
        wizardBot.typePassword( "secret" );

        // finish dialog
        wizardBot.clickFinishButton();
        connectionsViewBot.waitForConnection( "NewConnectionWizardTest" );

        // ensure connection was created
        ConnectionManager connectionManager = ConnectionCorePlugin.getDefault().getConnectionManager();
        assertNotNull( connectionManager.getConnections() );
        assertEquals( 1, connectionManager.getConnections().length );
        Connection connection = connectionManager.getConnections()[0];
        assertEquals( "NewConnectionWizardTest", connection.getName() );
        assertEquals( "localhost", connection.getHost() );
        assertEquals( ldapServer.getPort(), connection.getPort() );
        assertEquals( AuthenticationMethod.SIMPLE, connection.getAuthMethod() );
        assertEquals( "uid=admin,ou=system", connection.getBindPrincipal() );
        assertEquals( "secret", connection.getBindPassword() );

        // ensure connection is visible in Connections view
        assertEquals( 1, connectionsViewBot.getConnectionCount() );

        // close connection
        connectionsViewBot.closeSelectedConnections();
    }


    /**
     * Tests the "Check Network Parameter" button.
     */
    @Test
    public void testCheckNetworkParameterButtonOK()
    {
        // enter connection parameter with host name
        wizardBot.typeConnectionName( "NewConnectionWizardTest" );
        wizardBot.typeHost( "localhost" );
        wizardBot.typePort( ldapServer.getPort() );

        // click "Check Network Parameter" button
        String result1 = wizardBot.clickCheckNetworkParameterButton();
        assertNull( "Expected OK", result1 );

        // enter connection parameter with IPv4 address
        wizardBot.typeHost( "127.0.0.1" );
        wizardBot.typePort( ldapServer.getPort() );

        // click "Check Network Parameter" button
        String result2 = wizardBot.clickCheckNetworkParameterButton();
        assertNull( "Expected OK", result2 );

        //
        // Don't know why this doesn't work with SWTBot.
        // When testing manually it works.
        //
        // // enter connection parameter with IPv6 address
        // wizardBot.typeHost( "[::1]" );
        // wizardBot.typePort( ldapService.getPort() );
        //
        // // click "Check Network Parameter" button
        // String result3 = wizardBot.clickCheckNetworkParameterButton();
        // assertNull( "Expected OK", result3 );

        wizardBot.clickCancelButton();
    }


    /**
     * Tests the "Check Network Parameter" button.
     */
    @Test
    public void testCheckNetworkParameterButtonNotOK()
    {
        // enter connection parameter with invalid port
        wizardBot.typeConnectionName( "NewConnectionWizardTest" );
        int port = ldapServer.getPort() + 1;
        wizardBot.typeHost( "localhost" );
        wizardBot.typePort( port );

        // click "Check Network Parameter" button and get the result
        String result1 = wizardBot.clickCheckNetworkParameterButton();
        assertNotNull( "Expected Error", result1 );
        assertTrue( "'Connection refused' message must occur in error message", result1.contains( "Connection refused" ) );
        assertTrue( "Invalid port number must occur in error message", result1.contains( "" + port ) );

        // enter connection parameter with invalid host name
        String hostname = "qwertzuiop.asdfghjkl.yxcvbnm";
        wizardBot.typeHost( hostname );
        wizardBot.typePort( ldapServer.getPort() );

        // click "Check Network Parameter" button and get the result
        String result2 = wizardBot.clickCheckNetworkParameterButton();
        assertNotNull( "Expected Error", result2 );
        assertTrue( "'Unknown Host' message must occur in error message", result2.contains( "Unknown Host" ) );
        assertTrue( "Unknown host name must occur in error message", result2.contains( hostname ) );

        // disabled this test because it does not work properly
        // as it depends from the network connection settings.
        //        // enter connection parameter with non-routed IP address
        //        String ipAddress = "10.11.12.13";
        //        wizardBot.typeHost( ipAddress );
        //        wizardBot.typePort( ldapServer.getPort() );
        //
        //        // click "Check Network Parameter" button and get the result
        //        String result3 = wizardBot.clickCheckNetworkParameterButton();
        //        assertNotNull( "Expected Error", result3 );
        //        assertTrue( "'No route to host' or 'Network is unreachable' message must occur in error message", //
        //            result3.contains( "No route to host" ) || result3.contains( "Network is unreachable" ) );
        //        assertTrue( "IP address must occur in error message", result3.contains( ipAddress ) );

        wizardBot.clickCancelButton();
    }


    /**
     * Tests the "Check Network Parameter" button.
     */
    @Test
    public void testCheckAuthenticationButtonOK()
    {
        // enter connection parameter
        wizardBot.typeConnectionName( "NewConnectionWizardTest" );
        wizardBot.typeHost( "localhost" );
        wizardBot.typePort( ldapServer.getPort() );
        wizardBot.clickNextButton();

        // enter correct authentication parameter
        wizardBot.typeUser( "uid=admin,ou=system" );
        wizardBot.typePassword( "secret" );

        // click "Check Network Parameter" button
        String result = wizardBot.clickCheckAuthenticationButton();
        assertNull( "Expected OK", result );

        wizardBot.clickCancelButton();
    }


    /**
     * Tests the "Check Authentication" button.
     */
    @Test
    public void testCheckAuthenticationButtonNotOK()
    {
        // enter connection parameter
        wizardBot.typeConnectionName( "NewConnectionWizardTest" );
        wizardBot.typeHost( "localhost" );
        wizardBot.typePort( ldapServer.getPort() );
        wizardBot.clickNextButton();

        // enter incorrect authentication parameter
        wizardBot.typeUser( "uid=admin,ou=system" );
        wizardBot.typePassword( "secret45" );

        // click "Check Network Parameter" button
        String result = wizardBot.clickCheckAuthenticationButton();
        assertNotNull( "Expected Error", result );
        assertTrue( "'error code 49' message must occur in error message", result.contains( "error code 49" ) );

        wizardBot.clickCancelButton();
    }


    /**
     * Tests StartTLS with an valid certificate. This is simulated
     * by putting the self-signed certificate into a temporary key store
     * and using this key store for JNDI
     */
    @Test
    @Ignore
    // till DIRSERVER-1373 is fixed
    public void testStartTlsCertificateValidationOK() throws Exception
    {
        // prepare certificate
        Date startDate = new Date( System.currentTimeMillis() - YEAR_MILLIS );
        Date endDate = new Date( System.currentTimeMillis() + YEAR_MILLIS );
        updateCertificate( "cn=localhost", "cn=localhost", startDate, endDate );

        // prepare key store
        installKeyStoreWithCertificate();

        // let JNDI use the key store
        System.setProperty( "javax.net.ssl.trustStore", ksFile.getAbsolutePath() );
        System.setProperty( "javax.net.ssl.keyStore", ksFile.getAbsolutePath() );
        System.setProperty( "javax.net.ssl.keyStorePassword", "changeit" );

        // enter connection parameter
        wizardBot.typeConnectionName( "NewConnectionWizardTest" );
        wizardBot.typeHost( "localhost" );
        wizardBot.typePort( ldapServer.getPort() );
        wizardBot.selectStartTlsEncryption();

        // check the certificate, should be OK
        String result = wizardBot.clickCheckNetworkParameterButton();
        assertNull( "Expected OK, valid and trusted certificate", result );

        // enter correct authentication parameter
        wizardBot.clickNextButton();
        wizardBot.typeUser( "uid=admin,ou=system" );
        wizardBot.typePassword( "secret" );

        // check the certificate again, should be OK
        String result2 = wizardBot.clickCheckAuthenticationButton();
        assertNull( "Expected OK, valid and trusted certificate", result2 );

        wizardBot.clickCancelButton();
    }


    /**
     * Tests StartTLS with an expired certificate.
     */
    @Test
    @Ignore
    // till DIRSERVER-1373 is fixed
    public void testStartTlsCertificateValidationExpired() throws Exception
    {
        // prepare certificate
        Date startDate = new Date( System.currentTimeMillis() - YEAR_MILLIS - YEAR_MILLIS );
        Date endDate = new Date( System.currentTimeMillis() - YEAR_MILLIS );
        updateCertificate( "cn=localhost", "cn=localhost", startDate, endDate );

        // enter connection parameter
        wizardBot.typeConnectionName( "NewConnectionWizardTest" );
        wizardBot.typeHost( "localhost" );
        wizardBot.typePort( ldapServer.getPort() );
        wizardBot.selectStartTlsEncryption();

        // check the certificate, expecting the trust dialog
        CertificateTrustDialogBot trustDialogBot = wizardBot
            .clickCheckNetworkParameterButtonExpectingCertificateTrustDialog();
        assertTrue( trustDialogBot.isSelfSigned() );
        assertTrue( trustDialogBot.isExpired() );
        assertFalse( trustDialogBot.isNotYetValid() );
        assertFalse( trustDialogBot.isHostNameMismatch() );
        assertFalse( trustDialogBot.isIssuerUnkown() );
        trustDialogBot.selectDontTrust();
        ErrorDialogBot errorBot = trustDialogBot.clickOkButtonExpectingErrorDialog();
        assertTrue( errorBot.getErrorMessage().contains( "Untrusted certificate" ) );
        errorBot.clickOkButton();

        wizardBot.clickCancelButton();
    }


    /**
     * Tests SSL with an not yet valid certificate.
     */
    @Test
    @Ignore
    // till DIRSERVER-1373 is fixed
    public void testStartTlsCertificateValidationNotYetValid() throws Exception
    {
        // prepare certificate
        Date startDate = new Date( System.currentTimeMillis() + YEAR_MILLIS );
        Date endDate = new Date( System.currentTimeMillis() + YEAR_MILLIS + YEAR_MILLIS );
        updateCertificate( "cn=localhost", "cn=localhost", startDate, endDate );

        // enter connection parameter
        wizardBot.typeConnectionName( "NewConnectionWizardTest" );
        wizardBot.typeHost( "localhost" );
        wizardBot.typePort( ldapServer.getPort() );
        wizardBot.selectStartTlsEncryption();

        // check the certificate, expecting the trust dialog
        CertificateTrustDialogBot trustDialogBot = wizardBot
            .clickCheckNetworkParameterButtonExpectingCertificateTrustDialog();
        assertTrue( trustDialogBot.isSelfSigned() );
        assertTrue( trustDialogBot.isNotYetValid() );
        assertFalse( trustDialogBot.isExpired() );
        assertFalse( trustDialogBot.isHostNameMismatch() );
        assertFalse( trustDialogBot.isIssuerUnkown() );
        trustDialogBot.selectDontTrust();
        ErrorDialogBot errorBot = trustDialogBot.clickOkButtonExpectingErrorDialog();
        assertTrue( errorBot.getErrorMessage().contains( "Untrusted certificate" ) );
        errorBot.clickOkButton();

        wizardBot.clickCancelButton();
    }


    /**
     * Tests StartTLS with an invalid certificate (unknown issuer).
     */
    @Test
    @Ignore
    // till DIRSERVER-1373 is fixed
    public void testStartTlsCertificateValidationIssuerUnknown() throws Exception
    {
        // prepare certificate
        Date startDate = new Date( System.currentTimeMillis() - YEAR_MILLIS );
        Date endDate = new Date( System.currentTimeMillis() + YEAR_MILLIS );
        updateCertificate( "cn=TheUnknownStuntman", "cn=localhost", startDate, endDate );

        // enter connection parameter
        wizardBot.typeConnectionName( "NewConnectionWizardTest" );
        wizardBot.typeHost( "localhost" );
        wizardBot.typePort( ldapServer.getPort() );
        wizardBot.selectStartTlsEncryption();

        // check the certificate, expecting the trust dialog
        CertificateTrustDialogBot trustDialogBot = wizardBot
            .clickCheckNetworkParameterButtonExpectingCertificateTrustDialog();
        assertTrue( trustDialogBot.isIssuerUnkown() );
        assertFalse( trustDialogBot.isHostNameMismatch() );
        assertFalse( trustDialogBot.isSelfSigned() );
        assertFalse( trustDialogBot.isNotYetValid() );
        assertFalse( trustDialogBot.isExpired() );
        trustDialogBot.selectDontTrust();
        ErrorDialogBot errorBot = trustDialogBot.clickOkButtonExpectingErrorDialog();
        assertTrue( errorBot.getErrorMessage().contains( "Untrusted certificate" ) );
        errorBot.clickOkButton();

        wizardBot.clickCancelButton();
    }


    /**
     * Tests StartTLS with an certificate, where the certificate's host name
     * doesn't match the server's host name (localhost)
     */
    @Test
    @Ignore
    // till DIRSERVER-1373 is fixed
    public void testStartTlsCertificateValidationHostnameMismatch() throws Exception
    {
        // prepare certificate
        Date startDate = new Date( System.currentTimeMillis() - YEAR_MILLIS );
        Date endDate = new Date( System.currentTimeMillis() + YEAR_MILLIS );
        updateCertificate( "cn=TheUnknownStuntman", "cn=ldap.example.com", startDate, endDate );

        // enter connection parameter and authentication parameter
        wizardBot.typeConnectionName( "NewConnectionWizardTest" );
        wizardBot.typeHost( "localhost" );
        wizardBot.typePort( ldapServer.getPort() );
        wizardBot.selectStartTlsEncryption();
        wizardBot.clickNextButton();
        wizardBot.typeUser( "uid=admin,ou=system" );
        wizardBot.typePassword( "secret" );

        // check the certificate, expecting the trust dialog
        CertificateTrustDialogBot trustDialogBot = wizardBot
            .clickCheckAuthenticationButtonExpectingCertificateTrustDialog();
        assertTrue( trustDialogBot.isHostNameMismatch() );
        assertTrue( trustDialogBot.isIssuerUnkown() );
        assertFalse( trustDialogBot.isSelfSigned() );
        assertFalse( trustDialogBot.isNotYetValid() );
        assertFalse( trustDialogBot.isExpired() );
        trustDialogBot.selectDontTrust();
        ErrorDialogBot errorBot = trustDialogBot.clickOkButtonExpectingErrorDialog();
        assertTrue( errorBot.getErrorMessage().contains( "Untrusted certificate" ) );
        errorBot.clickOkButton();

        wizardBot.clickCancelButton();
    }


    /**
     * Tests that when selecting "Don't trust" the certificate is not trusted
     * and not added to any key store.
     *
     * @throws Exception
     */
    @Test
    @Ignore
    // till DIRSERVER-1373 is fixed
    public void testStartTlsCertificateDontTrust() throws Exception
    {
        // prepare certificate
        Date startDate = new Date( System.currentTimeMillis() - YEAR_MILLIS );
        Date endDate = new Date( System.currentTimeMillis() + YEAR_MILLIS );
        updateCertificate( "cn=TheUnknownStuntman", "cn=localhost", startDate, endDate );

        // enter connection parameter and authentication parameter
        wizardBot.typeConnectionName( "NewConnectionWizardTest" );
        wizardBot.typeHost( "localhost" );
        wizardBot.typePort( ldapServer.getPort() );
        wizardBot.selectStartTlsEncryption();

        // check trust, expect trust dialog, select don't trust
        CertificateTrustDialogBot trustDialogBot = wizardBot
            .clickCheckNetworkParameterButtonExpectingCertificateTrustDialog();
        assertTrue( trustDialogBot.isVisible() );
        trustDialogBot.selectDontTrust();
        ErrorDialogBot errorBot = trustDialogBot.clickOkButtonExpectingErrorDialog();
        errorBot.clickOkButton();

        // check trust again, expect trust dialog, select don't trust
        trustDialogBot = wizardBot.clickCheckNetworkParameterButtonExpectingCertificateTrustDialog();
        assertTrue( trustDialogBot.isVisible() );
        trustDialogBot.selectDontTrust();
        errorBot = trustDialogBot.clickOkButtonExpectingErrorDialog();
        errorBot.clickOkButton();

        // certificate must not be added to a trust store
        assertEquals( 0, ConnectionCorePlugin.getDefault().getPermanentTrustStoreManager().getCertificates().length );
        assertEquals( 0, ConnectionCorePlugin.getDefault().getSessionTrustStoreManager().getCertificates().length );

        // enter authentication parameter
        wizardBot.clickNextButton();
        wizardBot.typeUser( "uid=admin,ou=system" );
        wizardBot.typePassword( "secret" );

        // check trust again, expect trust dialog, select don't trust
        trustDialogBot = wizardBot.clickCheckAuthenticationButtonExpectingCertificateTrustDialog();
        assertTrue( trustDialogBot.isVisible() );
        trustDialogBot.selectDontTrust();
        errorBot = trustDialogBot.clickOkButtonExpectingErrorDialog();
        errorBot.clickOkButton();

        // click finish, that opens the connection
        wizardBot.clickFinishButton();

        // expecting trust dialog again.
        trustDialogBot = new CertificateTrustDialogBot();
        assertTrue( trustDialogBot.isVisible() );
        trustDialogBot.selectDontTrust();
        errorBot = trustDialogBot.clickOkButtonExpectingErrorDialog();
        errorBot.clickOkButton();

        assertEquals( 0, ConnectionCorePlugin.getDefault().getPermanentTrustStoreManager().getCertificates().length );
        assertEquals( 0, ConnectionCorePlugin.getDefault().getSessionTrustStoreManager().getCertificates().length );
    }


    /**
     * Tests that when selecting "Trust temporary" the certificate is trusted
     * and added to the session key store.
     *
     * @throws Exception
     */
    @Test
    @Ignore
    // till DIRSERVER-1373 is fixed
    public void testStartTlsCertificateTrustTemporary() throws Exception
    {
        // prepare certificate
        Date startDate = new Date( System.currentTimeMillis() - YEAR_MILLIS );
        Date endDate = new Date( System.currentTimeMillis() + YEAR_MILLIS );
        updateCertificate( "cn=TheUnknownStuntman", "cn=localhost2", startDate, endDate );

        // enter connection parameter and authentication parameter
        wizardBot.typeConnectionName( "NewConnectionWizardTest" );
        wizardBot.typeHost( "localhost" );
        wizardBot.typePort( ldapServer.getPort() );
        wizardBot.selectStartTlsEncryption();
        wizardBot.clickNextButton();
        wizardBot.typeUser( "uid=admin,ou=system" );
        wizardBot.typePassword( "secret" );

        // check trust, expect trust dialog, select trust temporary
        CertificateTrustDialogBot trustDialogBot = wizardBot
            .clickCheckAuthenticationButtonExpectingCertificateTrustDialog();
        assertTrue( trustDialogBot.isVisible() );
        trustDialogBot.selectTrustTemporary();
        trustDialogBot.clickOkButton();

        // TODO: expect ok dialog
        trustDialogBot.clickOkButton();

        // certificate must be added to the temporary trust store
        assertEquals( 0, ConnectionCorePlugin.getDefault().getPermanentTrustStoreManager().getCertificates().length );
        assertEquals( 1, ConnectionCorePlugin.getDefault().getSessionTrustStoreManager().getCertificates().length );

        // check trust again, now the certificate is already trusted
        String result = wizardBot.clickCheckAuthenticationButton();
        assertNull( "Expected OK, valid and trusted certificate", result );

        wizardBot.clickCancelButton();
    }


    /**
     * Tests that when selecting "Trust permanent" the certificate is trusted
     * and added to the permanent key store.
     *
     * @throws Exception
     */
    @Test
    @Ignore
    // till DIRSERVER-1373 is fixed
    public void testStartTlsCertificateTrustPermanent() throws Exception
    {
        // prepare certificate
        Date startDate = new Date( System.currentTimeMillis() - YEAR_MILLIS );
        Date endDate = new Date( System.currentTimeMillis() + YEAR_MILLIS );
        updateCertificate( "cn=TheUnknownStuntman", "cn=localhost3", startDate, endDate );

        // enter connection parameter and authentication parameter
        wizardBot.typeConnectionName( "NewConnectionWizardTest" );
        wizardBot.typeHost( "localhost" );
        wizardBot.typePort( ldapServer.getPort() );
        wizardBot.selectStartTlsEncryption();

        // check trust, expect trust dialog, select trust temporary
        CertificateTrustDialogBot trustDialogBot = wizardBot
            .clickCheckNetworkParameterButtonExpectingCertificateTrustDialog();
        assertTrue( trustDialogBot.isVisible() );
        trustDialogBot.selectTrustPermanent();
        trustDialogBot.clickOkButton();

        // TODO: expect ok dialog
        trustDialogBot.clickOkButton();

        // certificate must be added to the temporary trust store
        assertEquals( 1, ConnectionCorePlugin.getDefault().getPermanentTrustStoreManager().getCertificates().length );
        assertEquals( 0, ConnectionCorePlugin.getDefault().getSessionTrustStoreManager().getCertificates().length );

        // check trust again, now the certificate is already trusted
        String result = wizardBot.clickCheckNetworkParameterButton();
        assertNull( "Expected OK, valid and trusted certificate", result );

        wizardBot.clickCancelButton();
    }


    /**
     * Tests ldaps:// with an valid certificate. This is simulated
     * by putting the self-signed certificate into a temporary key store
     * and using this key store for JNDI
     */
    @Test
    @Ignore
    // till DIRSERVER-1373 is fixed
    public void testLdapsCertificateValidationOK() throws Exception
    {
        // prepare certificate
        Date startDate = new Date( System.currentTimeMillis() - YEAR_MILLIS );
        Date endDate = new Date( System.currentTimeMillis() + YEAR_MILLIS );
        updateCertificate( "cn=localhost", "cn=localhost", startDate, endDate );

        // prepare key store
        installKeyStoreWithCertificate();

        // let JNDI use the key store
        System.setProperty( "javax.net.ssl.trustStore", ksFile.getAbsolutePath() );
        System.setProperty( "javax.net.ssl.keyStore", ksFile.getAbsolutePath() );
        System.setProperty( "javax.net.ssl.keyStorePassword", "changeit" );

        // enter connection parameter and authentication parameter
        wizardBot.typeConnectionName( "NewConnectionWizardTest" );
        wizardBot.typeHost( "localhost" );
        wizardBot.typePort( ldapServer.getPortSSL() );
        wizardBot.selectLdapsEncryption();
        wizardBot.clickNextButton();
        wizardBot.typeUser( "uid=admin,ou=system" );
        wizardBot.typePassword( "secret" );

        // check the certificate, should be OK
        String result = wizardBot.clickCheckAuthenticationButton();
        assertNull( "Expected OK, valid and trusted certificate", result );

        wizardBot.clickCancelButton();
    }


    @Test
    @Ignore
    // till DIRSERVER-1373 is fixed
    public void testLdapsCertificateValidationNotOK() throws Exception
    {
        // prepare certificate
        Date startDate = new Date( System.currentTimeMillis() + YEAR_MILLIS );
        Date endDate = new Date( System.currentTimeMillis() + YEAR_MILLIS + YEAR_MILLIS );
        updateCertificate( "cn=localhost", "cn=localhost", startDate, endDate );

        // enter connection parameter and authentication parameter
        wizardBot.typeConnectionName( "NewConnectionWizardTest" );
        wizardBot.typeHost( "localhost" );
        wizardBot.typePort( ldapServer.getPortSSL() );
        wizardBot.selectLdapsEncryption();
        wizardBot.clickNextButton();
        wizardBot.typeUser( "uid=admin,ou=system" );
        wizardBot.typePassword( "secret" );

        // check the certificate, expecting the trust dialog
        CertificateTrustDialogBot trustDialogBot = wizardBot
            .clickCheckAuthenticationButtonExpectingCertificateTrustDialog();
        assertTrue( trustDialogBot.isSelfSigned() );
        assertTrue( trustDialogBot.isNotYetValid() );
        assertFalse( trustDialogBot.isExpired() );
        assertFalse( trustDialogBot.isHostNameMismatch() );
        assertFalse( trustDialogBot.isIssuerUnkown() );
        trustDialogBot.selectDontTrust();
        ErrorDialogBot errorBot = trustDialogBot.clickOkButtonExpectingErrorDialog();
        assertTrue( errorBot.getErrorMessage().contains( "failed" ) );
        errorBot.clickOkButton();

        wizardBot.clickCancelButton();
    }


    /**
     * Tests that when selecting "Don't trust" the certificate is not trusted
     * and not added to any key store.
     *
     * @throws Exception
     */
    @Test
    @Ignore
    // till DIRSERVER-1373 is fixed
    public void testLdapsCertificateDontTrust() throws Exception
    {
        // prepare certificate
        Date startDate = new Date( System.currentTimeMillis() - YEAR_MILLIS );
        Date endDate = new Date( System.currentTimeMillis() + YEAR_MILLIS );
        updateCertificate( "cn=TheUnknownStuntman", "cn=localhost4", startDate, endDate );

        // enter connection parameter and authentication parameter
        wizardBot.typeConnectionName( "NewConnectionWizardTest" );
        wizardBot.typeHost( "localhost" );
        wizardBot.typePort( ldapServer.getPortSSL() );
        wizardBot.selectLdapsEncryption();
        wizardBot.clickNextButton();
        wizardBot.typeUser( "uid=admin,ou=system" );
        wizardBot.typePassword( "secret" );

        // check trust, expect trust dialog, select don't trust
        CertificateTrustDialogBot trustDialogBot = wizardBot
            .clickCheckAuthenticationButtonExpectingCertificateTrustDialog();
        assertTrue( trustDialogBot.isVisible() );
        trustDialogBot.selectDontTrust();
        ErrorDialogBot errorBot = trustDialogBot.clickOkButtonExpectingErrorDialog();
        errorBot.clickOkButton();

        // check trust again, expect trust dialog, select don't trust
        trustDialogBot = wizardBot.clickCheckAuthenticationButtonExpectingCertificateTrustDialog();
        assertTrue( trustDialogBot.isVisible() );
        trustDialogBot.selectDontTrust();
        errorBot = trustDialogBot.clickOkButtonExpectingErrorDialog();
        errorBot.clickOkButton();

        // certificate must not be added to a trust store
        assertEquals( 0, ConnectionCorePlugin.getDefault().getPermanentTrustStoreManager().getCertificates().length );
        assertEquals( 0, ConnectionCorePlugin.getDefault().getSessionTrustStoreManager().getCertificates().length );

        // click finish, that opens the connection
        wizardBot.clickFinishButton();

        // expecting trust dialog again.
        trustDialogBot = new CertificateTrustDialogBot();
        assertTrue( trustDialogBot.isVisible() );
        trustDialogBot.selectDontTrust();
        errorBot = trustDialogBot.clickOkButtonExpectingErrorDialog();
        errorBot.clickOkButton();

        assertEquals( 0, ConnectionCorePlugin.getDefault().getPermanentTrustStoreManager().getCertificates().length );
        assertEquals( 0, ConnectionCorePlugin.getDefault().getSessionTrustStoreManager().getCertificates().length );
    }


    /**
     * Tests that when selecting "Trust temporary" the certificate is trusted
     * and added to the session key store.
     *
     * @throws Exception
     */
    @Test
    @Ignore
    // till DIRSERVER-1373 is fixed
    public void testLdapsCertificateTrustTemporary() throws Exception
    {
        // prepare certificate
        Date startDate = new Date( System.currentTimeMillis() - YEAR_MILLIS );
        Date endDate = new Date( System.currentTimeMillis() + YEAR_MILLIS );
        updateCertificate( "cn=TheUnknownStuntman", "cn=localhost5", startDate, endDate );

        // enter connection parameter and authentication parameter
        wizardBot.typeConnectionName( "NewConnectionWizardTest" );
        wizardBot.typeHost( "localhost" );
        wizardBot.typePort( ldapServer.getPortSSL() );
        wizardBot.selectLdapsEncryption();
        wizardBot.clickNextButton();
        wizardBot.typeUser( "uid=admin,ou=system" );
        wizardBot.typePassword( "secret" );

        // check trust, expect trust dialog, select trust temporary
        CertificateTrustDialogBot trustDialogBot = wizardBot
            .clickCheckAuthenticationButtonExpectingCertificateTrustDialog();
        assertTrue( trustDialogBot.isVisible() );
        trustDialogBot.selectTrustTemporary();
        trustDialogBot.clickOkButton();

        // TODO: expect ok dialog
        trustDialogBot.clickOkButton();

        // certificate must be added to the temporary trust store
        assertEquals( 0, ConnectionCorePlugin.getDefault().getPermanentTrustStoreManager().getCertificates().length );
        assertEquals( 1, ConnectionCorePlugin.getDefault().getSessionTrustStoreManager().getCertificates().length );

        // check trust again, now the certificate is already trusted
        String result = wizardBot.clickCheckAuthenticationButton();
        assertNull( "Expected OK, valid and trusted certificate", result );

        wizardBot.clickCancelButton();
    }


    /**
     * Tests that when selecting "Trust permanent" the certificate is trusted
     * and added to the permanent key store.
     *
     * @throws Exception
     */
    @Test
    @Ignore
    // till DIRSERVER-1373 is fixed
    public void testLdapsCertificateTrustPermanent() throws Exception
    {
        // prepare certificate
        Date startDate = new Date( System.currentTimeMillis() - YEAR_MILLIS );
        Date endDate = new Date( System.currentTimeMillis() + YEAR_MILLIS );
        updateCertificate( "cn=TheUnknownStuntman", "cn=localhost6", startDate, endDate );

        // enter connection parameter and authentication parameter
        wizardBot.typeConnectionName( "NewConnectionWizardTest" );
        wizardBot.typeHost( "localhost" );
        wizardBot.typePort( ldapServer.getPortSSL() );
        wizardBot.selectLdapsEncryption();
        wizardBot.clickNextButton();
        wizardBot.typeUser( "uid=admin,ou=system" );
        wizardBot.typePassword( "secret" );

        // check trust, expect trust dialog, select trust temporary
        CertificateTrustDialogBot trustDialogBot = wizardBot
            .clickCheckAuthenticationButtonExpectingCertificateTrustDialog();
        assertTrue( trustDialogBot.isVisible() );
        trustDialogBot.selectTrustPermanent();
        trustDialogBot.clickOkButton();

        // TODO: expect ok dialog
        trustDialogBot.clickOkButton();

        // certificate must be added to the temporary trust store
        assertEquals( 1, ConnectionCorePlugin.getDefault().getPermanentTrustStoreManager().getCertificates().length );
        assertEquals( 0, ConnectionCorePlugin.getDefault().getSessionTrustStoreManager().getCertificates().length );

        // check trust again, now the certificate is already trusted
        String result = wizardBot.clickCheckAuthenticationButton();
        assertNull( "Expected OK, valid and trusted certificate", result );

        wizardBot.clickCancelButton();
    }

    /*
     * Eventually we have to make several of these parameters configurable,
     * however note to pass export restrictions we must use a key size of
     * 512 or less here as the default.  Users can configure this setting
     * later based on their own legal situations.  This is required to
     * classify ApacheDS in the ECCN 5D002 category.  Please see the following
     * page for more information:
     *
     *    http://www.apache.org/dev/crypto.html
     *
     * Also ApacheDS must be classified on the following page:
     *
     *    http://www.apache.org/licenses/exports
     */
    private static final int KEY_SIZE = 512;
    private static final long YEAR_MILLIS = 365L * 24L * 3600L * 1000L;
    private static final String PRIVATE_KEY_AT = "privateKey";
    private static final String PUBLIC_KEY_AT = "publicKey";
    private static final String KEY_ALGORITHM_AT = "keyAlgorithm";
    private static final String PRIVATE_KEY_FORMAT_AT = "privateKeyFormat";
    private static final String PUBLIC_KEY_FORMAT_AT = "publicKeyFormat";
    private static final String USER_CERTIFICATE_AT = "userCertificate";
    private static final String PRINCIPAL = "uid=admin,ou=system";


    /**
     *
     */
    private void updateCertificate( String issuerDN, String subjectDN, Date startDate, Date expiryDate )
        throws Exception
    {
        DN dn = new DN( PRINCIPAL );
        List<Modification> modifications = new ArrayList<Modification>();

        // Get old key algorithm
        Entry entry = ldapServer.getDirectoryService().getAdminSession().lookup( dn );
        String keyAlgo = entry.get( KEY_ALGORITHM_AT ).getString();

        // Generate key pair
        KeyPairGenerator generator = KeyPairGenerator.getInstance( keyAlgo );
        generator.initialize( KEY_SIZE );
        KeyPair keypair = generator.genKeyPair();

        // Generate the private key attributes
        PrivateKey privateKey = keypair.getPrivate();

        // Generate public key
        PublicKey publicKey = keypair.getPublic();

        // Generate the self-signed certificate
        BigInteger serialNumber = BigInteger.valueOf( System.currentTimeMillis() );
        X509V1CertificateGenerator certGen = new X509V1CertificateGenerator();
        X500Principal issuerName = new X500Principal( issuerDN );
        X500Principal subjectName = new X500Principal( subjectDN );
        certGen.setSerialNumber( serialNumber );
        certGen.setIssuerDN( issuerName );
        certGen.setNotBefore( startDate );
        certGen.setNotAfter( expiryDate );
        certGen.setSubjectDN( subjectName );
        certGen.setPublicKey( publicKey );
        certGen.setSignatureAlgorithm( "SHA1With" + keyAlgo );
        X509Certificate cert = certGen.generate( privateKey, "BC" );

        // Write the modifications
        ModifyRequest request = new ModifyRequestImpl();
        request.setName( dn );
        request.replace( PRIVATE_KEY_AT, privateKey.getEncoded() );
        request.replace( PRIVATE_KEY_FORMAT_AT, privateKey.getFormat() );
        request.replace( PUBLIC_KEY_AT, publicKey.getEncoded() );
        request.replace( PUBLIC_KEY_FORMAT_AT, publicKey.getFormat() );
        request.replace( USER_CERTIFICATE_AT, cert.getEncoded() );
        ldapServer.getDirectoryService().getAdminSession().modify( dn, modifications );

        // TODO: activate when DIRSERVER-1373 is fixed
        //ldapService.reloadSslContext();
        //ldapsService.reloadSslContext();
    }


    private void installKeyStoreWithCertificate() throws Exception
    {
        if ( ksFile != null && ksFile.exists() )
        {
            ksFile.delete();
        }
        ksFile = File.createTempFile( "testStore", "ks" );

        CoreSession session = ldapServer.getDirectoryService().getAdminSession();
        Entry entry = session.lookup( new DN( "uid=admin,ou=system" ), new String[]
            { USER_CERTIFICATE_AT } );
        byte[] userCertificate = entry.get( USER_CERTIFICATE_AT ).getBytes();
        assertNotNull( userCertificate );

        ByteArrayInputStream in = new ByteArrayInputStream( userCertificate );
        CertificateFactory factory = CertificateFactory.getInstance( "X.509" );
        Certificate cert = factory.generateCertificate( in );
        KeyStore ks = KeyStore.getInstance( KeyStore.getDefaultType() );
        ks.load( null, null );
        ks.setCertificateEntry( "apacheds", cert );
        ks.store( new FileOutputStream( ksFile ), "changeit".toCharArray() );
    }
}
