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


import static org.apache.directory.studio.test.integration.ui.Constants.LOCALHOST;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.test.integration.ui.bots.ApacheDSConfigurationEditorBot;
import org.apache.directory.studio.test.integration.ui.bots.ApacheDSServersViewBot;
import org.apache.directory.studio.test.integration.ui.bots.CertificateTrustDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.CertificateValidationPreferencePageBot;
import org.apache.directory.studio.test.integration.ui.bots.CheckAuthenticationDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.ConnectionsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.DeleteDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.ErrorDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.NewApacheDSServerWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.NewConnectionWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.PreferencesBot;
import org.apache.directory.studio.test.integration.ui.bots.StudioBot;
import org.apache.directory.studio.test.integration.ui.bots.utils.Assertions;
import org.apache.directory.studio.test.integration.ui.bots.utils.FrameworkRunnerWithScreenshotCaptureListener;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;


/**
 * Tests secure connection handling.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
@RunWith(FrameworkRunnerWithScreenshotCaptureListener.class)
public class CertificateValidationTest
{
    private static final String serverName = "CertificateValidationTest";

    static final long YEAR_MILLIS = 365L * 24L * 3600L * 1000L;

    @Rule
    public TestName name = new TestName();

    private File ksFile;

    private static int ldapPort;
    private static int ldapsPort;

    private static StudioBot studioBot;
    private static ApacheDSServersViewBot serversViewBot;
    private static ConnectionsViewBot connectionsViewBot;
    private static NewConnectionWizardBot wizardBot;


    @BeforeClass
    public static void setUpClass() throws Exception
    {
        studioBot = new StudioBot();
        studioBot.resetLdapPerspective();
        serversViewBot = studioBot.getApacheDSServersViewBot();
        connectionsViewBot = studioBot.getConnectionView();

        // create the server
        createServer( serverName );
        setAvailablePorts( serverName );

        // ErrorDialog.AUTOMATED_MODE = false;
    }


    @AfterClass
    public static void tearDownClass() throws Exception
    {
        deleteServer( serverName );
    }


    @Before
    public void setUp() throws Exception
    {
        studioBot.resetLdapPerspective();
    }


    @After
    public void tearDown() throws Exception
    {
        connectionsViewBot.deleteTestConnections();

        // stop ApacheDS
        serversViewBot.stopServer( serverName );
        serversViewBot.waitForServerStop( serverName );

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

        // delete custom Java key store settings
        System.clearProperty( "javax.net.ssl.trustStore" );
        System.clearProperty( "javax.net.ssl.trustStorePassword" );
        System.clearProperty( "javax.net.ssl.keyStore" );
        System.clearProperty( "javax.net.ssl.keyStorePassword" );

        Assertions.genericTearDownAssertions();
    }


    private String getConnectionName()
    {
        return "NewConnectionWizardTest." + name.getMethodName();
    }


    /**
     * Tests ldaps:// with an valid certificate. This is simulated
     * by putting the self-signed certificate into a temporary key store.
     */
    @Test
    public void testLdapsCertificateValidationOK() throws Exception
    {
        // create certificate
        Date startDate = new Date( System.currentTimeMillis() - YEAR_MILLIS );
        Date endDate = new Date( System.currentTimeMillis() + YEAR_MILLIS );
        createCertificateAndUpdateInApacheDS( "cn=localhost", "cn=localhost", startDate, endDate );

        // start ApacheDS
        serversViewBot.runServer( serverName );
        serversViewBot.waitForServerStart( serverName );

        // let Java use the key store
        System.setProperty( "javax.net.ssl.trustStore", ksFile.getAbsolutePath() );
        System.setProperty( "javax.net.ssl.trustStorePassword", "changeit" );
        System.setProperty( "javax.net.ssl.keyStore", ksFile.getAbsolutePath() );
        System.setProperty( "javax.net.ssl.keyStorePassword", "changeit" );

        // enter connection parameter and authentication parameter
        wizardBot = connectionsViewBot.openNewConnectionWizard();
        wizardBot.typeConnectionName( getConnectionName() );
        wizardBot.typeHost( LOCALHOST );
        wizardBot.typePort( ldapsPort );
        wizardBot.selectLdapsEncryption();
        wizardBot.clickNextButton();
        wizardBot.typeUser( "uid=admin,ou=system" );
        wizardBot.typePassword( "secret" );

        // check the certificate, should be OK
        String result = wizardBot.clickCheckAuthenticationButton();
        assertNull( "Expected OK, valid and trusted certificate", result );

        wizardBot.clickCancelButton();
    }


    /**
     * Tests ldaps:// with an expired certificate.
     */
    @Test
    public void testLdapsCertificateValidationNotOK() throws Exception
    {
        // prepare certificate
        Date startDate = new Date( System.currentTimeMillis() + YEAR_MILLIS );
        Date endDate = new Date( System.currentTimeMillis() + YEAR_MILLIS + YEAR_MILLIS );
        createCertificateAndUpdateInApacheDS( "cn=localhost", "cn=localhost", startDate, endDate );

        // start ApacheDS
        serversViewBot.runServer( serverName );
        serversViewBot.waitForServerStart( serverName );

        // enter connection parameter and authentication parameter
        wizardBot = connectionsViewBot.openNewConnectionWizard();
        wizardBot.typeConnectionName( getConnectionName() );
        wizardBot.typeHost( LOCALHOST );
        wizardBot.typePort( ldapsPort );
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
     */
    @Test
    public void testLdapsCertificateDoNotTrust() throws Exception
    {
        // prepare certificate
        Date startDate = new Date( System.currentTimeMillis() - YEAR_MILLIS );
        Date endDate = new Date( System.currentTimeMillis() + YEAR_MILLIS );
        createCertificateAndUpdateInApacheDS( "cn=TheUnknownStuntman", "cn=localhost1", startDate, endDate );

        // start ApacheDS
        serversViewBot.runServer( serverName );
        serversViewBot.waitForServerStart( serverName );

        // enter connection parameter and authentication parameter
        wizardBot = connectionsViewBot.openNewConnectionWizard();
        wizardBot.typeConnectionName( getConnectionName() );
        wizardBot.typeHost( LOCALHOST );
        wizardBot.typePort( ldapsPort );
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
        wizardBot.activate();
        trustDialogBot = wizardBot.clickCheckAuthenticationButtonExpectingCertificateTrustDialog();
        assertTrue( trustDialogBot.isVisible() );
        trustDialogBot.selectDontTrust();
        errorBot = trustDialogBot.clickOkButtonExpectingErrorDialog();
        errorBot.clickOkButton();

        // certificate must not be added to a trust store
        assertEquals( 0, ConnectionCorePlugin.getDefault().getPermanentTrustStoreManager().getCertificates().length );
        assertEquals( 0, ConnectionCorePlugin.getDefault().getSessionTrustStoreManager().getCertificates().length );

        // click finish, that opens the connection
        wizardBot.clickFinishButton( false );

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
     */
    @Test
    public void testLdapsCertificateTrustTemporary() throws Exception
    {
        // prepare certificate
        Date startDate = new Date( System.currentTimeMillis() - YEAR_MILLIS );
        Date endDate = new Date( System.currentTimeMillis() + YEAR_MILLIS );
        createCertificateAndUpdateInApacheDS( "cn=TheUnknownStuntman", "cn=localhost2", startDate, endDate );

        // start ApacheDS
        serversViewBot.runServer( serverName );
        serversViewBot.waitForServerStart( serverName );

        // enter connection parameter and authentication parameter
        wizardBot = connectionsViewBot.openNewConnectionWizard();
        wizardBot.typeConnectionName( getConnectionName() );
        wizardBot.typeHost( LOCALHOST );
        wizardBot.typePort( ldapsPort );
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

        // expect ok dialog
        new CheckAuthenticationDialogBot().clickOkButton();

        // certificate must be added to the temporary trust store
        assertEquals( 0, ConnectionCorePlugin.getDefault().getPermanentTrustStoreManager().getCertificates().length );
        assertEquals( 1, ConnectionCorePlugin.getDefault().getSessionTrustStoreManager().getCertificates().length );

        // check trust again, now the certificate is already trusted
        wizardBot.activate();
        String result = wizardBot.clickCheckAuthenticationButton();
        assertNull( "Expected OK, valid and trusted certificate", result );

        wizardBot.clickCancelButton();
    }


    /**
     * Tests that when selecting "Trust permanent" the certificate is trusted
     * and added to the permanent key store.
     */
    @Test
    public void testLdapsCertificateTrustPermanent() throws Exception
    {
        // prepare certificate
        Date startDate = new Date( System.currentTimeMillis() - YEAR_MILLIS );
        Date endDate = new Date( System.currentTimeMillis() + YEAR_MILLIS );
        createCertificateAndUpdateInApacheDS( "cn=TheUnknownStuntman", "cn=localhost3", startDate, endDate );

        // start ApacheDS
        serversViewBot.runServer( serverName );
        serversViewBot.waitForServerStart( serverName );

        // enter connection parameter and authentication parameter
        wizardBot = connectionsViewBot.openNewConnectionWizard();
        wizardBot.typeConnectionName( getConnectionName() );
        wizardBot.typeHost( LOCALHOST );
        wizardBot.typePort( ldapsPort );
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

        // expect ok dialog
        new CheckAuthenticationDialogBot().clickOkButton();

        // certificate must be added to the temporary trust store
        assertEquals( 1, ConnectionCorePlugin.getDefault().getPermanentTrustStoreManager().getCertificates().length );
        assertEquals( 0, ConnectionCorePlugin.getDefault().getSessionTrustStoreManager().getCertificates().length );

        // check trust again, now the certificate is already trusted
        wizardBot.activate();
        String result = wizardBot.clickCheckAuthenticationButton();
        assertNull( "Expected OK, valid and trusted certificate", result );

        wizardBot.clickCancelButton();
    }


    /**
     * Tests StartTLS with an valid certificate. This is simulated
     * by putting the self-signed certificate into a temporary key store.
     */
    @Test
    public void testStartTlsCertificateValidationOK() throws Exception
    {
        // create certificate
        Date startDate = new Date( System.currentTimeMillis() - YEAR_MILLIS );
        Date endDate = new Date( System.currentTimeMillis() + YEAR_MILLIS );
        createCertificateAndUpdateInApacheDS( "cn=localhost", "cn=localhost", startDate, endDate );

        // start ApacheDS
        serversViewBot.runServer( serverName );
        serversViewBot.waitForServerStart( serverName );

        // let Java use the key store
        System.setProperty( "javax.net.ssl.trustStore", ksFile.getAbsolutePath() );
        System.setProperty( "javax.net.ssl.trustStorePassword", "changeit" );
        System.setProperty( "javax.net.ssl.keyStore", ksFile.getAbsolutePath() );
        System.setProperty( "javax.net.ssl.keyStorePassword", "changeit" );

        // enter connection parameter
        wizardBot = connectionsViewBot.openNewConnectionWizard();
        wizardBot.typeConnectionName( getConnectionName() );
        wizardBot.typeHost( LOCALHOST );
        wizardBot.typePort( ldapPort );
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
     * DIRSTUDIO-1205: SSL/TLS with small key size is not working.
     */
    @Test
    public void testStartTlsCertificateValidationSmallKeysizeError() throws Exception
    {
        // prepare certificate
        Date startDate = new Date( System.currentTimeMillis() - YEAR_MILLIS );
        Date endDate = new Date( System.currentTimeMillis() + YEAR_MILLIS );
        createCertificateAndUpdateInApacheDS( "cn=localhost", "cn=localhost", startDate, endDate, 512 );

        // start ApacheDS
        serversViewBot.runServer( serverName );
        serversViewBot.waitForServerStart( serverName );

        // enter connection parameter
        wizardBot = connectionsViewBot.openNewConnectionWizard();
        wizardBot.typeConnectionName( getConnectionName() );
        wizardBot.typeHost( LOCALHOST );
        wizardBot.typePort( ldapPort );
        wizardBot.selectStartTlsEncryption();
        wizardBot.clickNextButton();
        wizardBot.typeUser( "uid=admin,ou=system" );
        wizardBot.typePassword( "secret" );

        // check the certificate, expecting the trust dialog
        CertificateTrustDialogBot trustDialogBot = wizardBot
            .clickCheckAuthenticationButtonExpectingCertificateTrustDialog();
        trustDialogBot.selectTrustTemporary();

        clickOkButtonExpectingCertficateErrorDialog( trustDialogBot, "Failed to verify certification path",
            "Algorithm constraints check failed on keysize limits", "RSA 512bit key used" );

        wizardBot.clickCancelButton();
    }


    /**
     * Tests StartTLS with an expired certificate.
     */
    @Test
    public void testStartTlsCertificateValidationExpired() throws Exception
    {
        // prepare certificate
        Date startDate = new Date( System.currentTimeMillis() - YEAR_MILLIS - YEAR_MILLIS );
        Date endDate = new Date( System.currentTimeMillis() - YEAR_MILLIS );
        createCertificateAndUpdateInApacheDS( "cn=localhost", "cn=localhost", startDate, endDate );

        // start ApacheDS
        serversViewBot.runServer( serverName );
        serversViewBot.waitForServerStart( serverName );

        // enter connection parameter
        wizardBot = connectionsViewBot.openNewConnectionWizard();
        wizardBot.typeConnectionName( getConnectionName() );
        wizardBot.typeHost( LOCALHOST );
        wizardBot.typePort( ldapPort );
        wizardBot.selectStartTlsEncryption();
        wizardBot.clickNextButton();
        wizardBot.typeUser( "uid=admin,ou=system" );
        wizardBot.typePassword( "secret" );

        // check the certificate, expecting the trust dialog
        CertificateTrustDialogBot trustDialogBot = wizardBot
            .clickCheckAuthenticationButtonExpectingCertificateTrustDialog();
        assertTrue( trustDialogBot.isSelfSigned() );
        assertTrue( trustDialogBot.isExpired() );
        assertFalse( trustDialogBot.isNotYetValid() );
        assertFalse( trustDialogBot.isHostNameMismatch() );
        assertFalse( trustDialogBot.isIssuerUnkown() );
        trustDialogBot.selectDontTrust();

        clickOkButtonExpectingCertficateErrorDialog( trustDialogBot );

        wizardBot.clickCancelButton();
    }


    /**
     * Tests SSL with an not yet valid certificate.
     */
    @Test
    public void testStartTlsCertificateValidationNotYetValid() throws Exception
    {
        // prepare certificate
        Date startDate = new Date( System.currentTimeMillis() + YEAR_MILLIS );
        Date endDate = new Date( System.currentTimeMillis() + YEAR_MILLIS + YEAR_MILLIS );
        createCertificateAndUpdateInApacheDS( "cn=localhost", "cn=localhost", startDate, endDate );

        // start ApacheDS
        serversViewBot.runServer( serverName );
        serversViewBot.waitForServerStart( serverName );

        // enter connection parameter
        wizardBot = connectionsViewBot.openNewConnectionWizard();
        wizardBot.typeConnectionName( getConnectionName() );
        wizardBot.typeHost( LOCALHOST );
        wizardBot.typePort( ldapPort );
        wizardBot.selectStartTlsEncryption();
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

        clickOkButtonExpectingCertficateErrorDialog( trustDialogBot );

        wizardBot.clickCancelButton();
    }


    private String clickOkButtonExpectingCertficateErrorDialog( CertificateTrustDialogBot trustDialogBot,
        String... expectedMessages )
    {
        ErrorDialogBot errorBot = trustDialogBot.clickOkButtonExpectingErrorDialog();

        String errorMessage = errorBot.getErrorMessage();

        assertThat( errorMessage, containsString( "ERR_04120_TLS_HANDSHAKE_ERROR" ) );
        assertThat( errorMessage, containsString( "The TLS handshake failed" ) );
        for ( String expectedMessage : expectedMessages )
        {
            assertThat( errorMessage, containsString( expectedMessage ) );
        }
        errorBot.clickOkButton();
        return errorMessage;
    }


    /**
     * Tests StartTLS with an invalid certificate (unknown issuer) and
     *  with an certificate, where the certificate's host name
     * doesn't match the server's host name (localhost)
     */
    @Test
    public void testStartTlsCertificateValidationHostnameMismatch() throws Exception
    {
        // prepare certificate
        Date startDate = new Date( System.currentTimeMillis() - YEAR_MILLIS );
        Date endDate = new Date( System.currentTimeMillis() + YEAR_MILLIS );
        createCertificateAndUpdateInApacheDS( "cn=TheUnknownStuntman", "cn=ldap.example.com", startDate, endDate );

        // start ApacheDS
        serversViewBot.runServer( serverName );
        serversViewBot.waitForServerStart( serverName );

        // enter connection parameter
        wizardBot = connectionsViewBot.openNewConnectionWizard();
        wizardBot.typeConnectionName( getConnectionName() );
        wizardBot.typeHost( LOCALHOST );
        wizardBot.typePort( ldapPort );
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

        clickOkButtonExpectingCertficateErrorDialog( trustDialogBot );

        wizardBot.clickCancelButton();
    }


    /**
     * Tests that when selecting "Don't trust" the certificate is not trusted
     * and not added to any key store.
     */
    @Test
    public void testStartTlsCertificateDoNotTrust() throws Exception
    {
        // prepare certificate
        Date startDate = new Date( System.currentTimeMillis() - YEAR_MILLIS );
        Date endDate = new Date( System.currentTimeMillis() + YEAR_MILLIS );
        createCertificateAndUpdateInApacheDS( "cn=TheUnknownStuntman", "cn=localhost4", startDate, endDate );

        // start ApacheDS
        serversViewBot.runServer( serverName );
        serversViewBot.waitForServerStart( serverName );

        // enter connection parameter and authentication parameter
        wizardBot = connectionsViewBot.openNewConnectionWizard();
        wizardBot.typeConnectionName( getConnectionName() );
        wizardBot.typeHost( LOCALHOST );
        wizardBot.typePort( ldapPort );
        wizardBot.selectStartTlsEncryption();
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
        wizardBot.activate();
        trustDialogBot = wizardBot.clickCheckAuthenticationButtonExpectingCertificateTrustDialog();
        assertTrue( trustDialogBot.isVisible() );
        trustDialogBot.selectDontTrust();
        errorBot = trustDialogBot.clickOkButtonExpectingErrorDialog();
        errorBot.clickOkButton();

        // certificate must not be added to a trust store
        assertEquals( 0, ConnectionCorePlugin.getDefault().getPermanentTrustStoreManager().getCertificates().length );
        assertEquals( 0, ConnectionCorePlugin.getDefault().getSessionTrustStoreManager().getCertificates().length );

        // click finish, that opens the connection
        wizardBot.clickFinishButton( false );

        // expecting trust dialog again.
        trustDialogBot = new CertificateTrustDialogBot();
        assertTrue( trustDialogBot.isVisible() );
        trustDialogBot.selectDontTrust();
        errorBot = trustDialogBot.clickOkButtonExpectingErrorDialog();
        errorBot.clickOkButton();

        // no trusted certificates expected
        assertEquals( 0, ConnectionCorePlugin.getDefault().getPermanentTrustStoreManager().getCertificates().length );
        assertEquals( 0, ConnectionCorePlugin.getDefault().getSessionTrustStoreManager().getCertificates().length );

        // no trusted certificates expected
        PreferencesBot preferencesBot = studioBot.openPreferences();
        CertificateValidationPreferencePageBot pageBot = preferencesBot.openCertificatValidationPage();
        pageBot.activatePermanentTab();
        assertEquals( 0, pageBot.getCertificateCount() );
        pageBot.activateTemporaryTab();
        assertEquals( 0, pageBot.getCertificateCount() );
        preferencesBot.clickCancelButton();
    }


    /**
     * Tests that when selecting "Trust temporary" the certificate is trusted
     * and added to the session key store.
     */
    @Test
    public void testStartTlsCertificateTrustTemporary() throws Exception
    {
        // prepare certificate
        Date startDate = new Date( System.currentTimeMillis() - YEAR_MILLIS );
        Date endDate = new Date( System.currentTimeMillis() + YEAR_MILLIS );
        createCertificateAndUpdateInApacheDS( "cn=TheUnknownStuntman", "cn=localhost5", startDate, endDate );

        // start ApacheDS
        serversViewBot.runServer( serverName );
        serversViewBot.waitForServerStart( serverName );

        // enter connection parameter and authentication parameter
        wizardBot = connectionsViewBot.openNewConnectionWizard();
        wizardBot.typeConnectionName( getConnectionName() );
        wizardBot.typeHost( LOCALHOST );
        wizardBot.typePort( ldapPort );
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

        // expect ok dialog
        new CheckAuthenticationDialogBot().clickOkButton();

        // certificate must be added to the temporary trust store
        assertEquals( 0, ConnectionCorePlugin.getDefault().getPermanentTrustStoreManager().getCertificates().length );
        assertEquals( 1, ConnectionCorePlugin.getDefault().getSessionTrustStoreManager().getCertificates().length );

        // check trust again, now the certificate is already trusted
        wizardBot.activate();
        String result = wizardBot.clickCheckAuthenticationButton();
        assertNull( "Expected OK, valid and trusted certificate", result );

        wizardBot.clickCancelButton();

        // certificate must be added to the temporary trust store
        PreferencesBot preferencesBot = studioBot.openPreferences();
        CertificateValidationPreferencePageBot pageBot = preferencesBot.openCertificatValidationPage();
        pageBot.activatePermanentTab();
        assertEquals( 0, pageBot.getCertificateCount() );
        pageBot.activateTemporaryTab();
        assertEquals( 1, pageBot.getCertificateCount() );
        preferencesBot.clickCancelButton();
    }


    /**
     * Tests that when selecting "Trust permanent" the certificate is trusted
     * and added to the permanent key store.
     */
    @Test
    public void testStartTlsCertificateTrustPermanent() throws Exception
    {
        // prepare certificate
        Date startDate = new Date( System.currentTimeMillis() - YEAR_MILLIS );
        Date endDate = new Date( System.currentTimeMillis() + YEAR_MILLIS );
        createCertificateAndUpdateInApacheDS( "cn=TheUnknownStuntman", "cn=localhost6", startDate, endDate );

        // start ApacheDS
        serversViewBot.runServer( serverName );
        serversViewBot.waitForServerStart( serverName );

        // enter connection parameter and authentication parameter
        wizardBot = connectionsViewBot.openNewConnectionWizard();
        wizardBot.typeConnectionName( getConnectionName() );
        wizardBot.typeHost( LOCALHOST );
        wizardBot.typePort( ldapPort );
        wizardBot.selectStartTlsEncryption();
        wizardBot.clickNextButton();
        wizardBot.typeUser( "uid=admin,ou=system" );
        wizardBot.typePassword( "secret" );

        // check trust, expect trust dialog, select trust temporary
        CertificateTrustDialogBot trustDialogBot = wizardBot
            .clickCheckAuthenticationButtonExpectingCertificateTrustDialog();
        assertTrue( trustDialogBot.isVisible() );
        trustDialogBot.selectTrustPermanent();
        trustDialogBot.clickOkButton();

        // expect ok dialog
        new CheckAuthenticationDialogBot().clickOkButton();

        // certificate must be added to the permanent trust store
        assertEquals( 1, ConnectionCorePlugin.getDefault().getPermanentTrustStoreManager().getCertificates().length );
        assertEquals( 0, ConnectionCorePlugin.getDefault().getSessionTrustStoreManager().getCertificates().length );

        // check trust again, now the certificate is already trusted
        wizardBot.activate();
        String result = wizardBot.clickCheckAuthenticationButton();
        assertNull( "Expected OK, valid and trusted certificate", result );

        wizardBot.clickCancelButton();

        // certificate must be added to the permanent trust store
        PreferencesBot preferencesBot = studioBot.openPreferences();
        CertificateValidationPreferencePageBot pageBot = preferencesBot.openCertificatValidationPage();
        pageBot.activatePermanentTab();
        assertEquals( 1, pageBot.getCertificateCount() );
        pageBot.activateTemporaryTab();
        assertEquals( 0, pageBot.getCertificateCount() );
        preferencesBot.clickCancelButton();
    }


    private static void createServer( String serverName )
    {
        // Showing view
        serversViewBot.show();

        // Opening wizard
        NewApacheDSServerWizardBot wizardBot = serversViewBot.openNewServerWizard();

        // Filling fields of the wizard
        wizardBot.selectApacheDS200();
        wizardBot.typeServerName( serverName );

        // Closing wizard
        wizardBot.clickFinishButton();
        serversViewBot.waitForServer( serverName );
    }


    private static void setAvailablePorts( String serverName )
    {
        ApacheDSConfigurationEditorBot editorBot = serversViewBot.openConfigurationEditor( serverName );

        editorBot.setAvailablePorts();
        ldapPort = editorBot.getLdapPort();
        ldapsPort = editorBot.getLdapsPort();

        editorBot.save();
        editorBot.close();
    }


    private static void deleteServer( String serverName )
    {
        // Stopping the server
        serversViewBot.stopServer( serverName );
        serversViewBot.waitForServerStop( serverName );

        // Deleting the server
        DeleteDialogBot deleteDialogBot = serversViewBot.openDeleteServerDialog();
        deleteDialogBot.clickOkButton();
    }


    private void createCertificateAndUpdateInApacheDS( String issuerDN, String subjectDN, Date startDate,
        Date expiryDate ) throws Exception
    {
        createCertificateAndUpdateInApacheDS( issuerDN, subjectDN, startDate, expiryDate, 1024 );
    }


    private void createCertificateAndUpdateInApacheDS( String issuerDN, String subjectDN, Date startDate,
        Date expiryDate, int keysize ) throws Exception
    {
        // create certificate in key store file
        if ( ksFile != null && ksFile.exists() )
        {
            ksFile.delete();
        }
        ksFile = CertificateUtils.createCertificateInKeyStoreFile( issuerDN, subjectDN, startDate, expiryDate,
            keysize );

        // configure certificate in ApacheDS
        ApacheDSConfigurationEditorBot editorBot = serversViewBot.openConfigurationEditor( serverName );
        editorBot.setKeystore( ksFile.getAbsolutePath(), "changeit" );
        editorBot.save();
        editorBot.close();
    }

}
