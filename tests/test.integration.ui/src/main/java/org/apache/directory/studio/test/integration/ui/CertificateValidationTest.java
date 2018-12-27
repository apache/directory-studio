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
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.server.core.security.TlsKeyGenerator;
import org.apache.directory.server.ldap.handlers.extended.StartTlsHandler;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.test.integration.ui.bots.CertificateTrustDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.CertificateValidationPreferencePageBot;
import org.apache.directory.studio.test.integration.ui.bots.CheckAuthenticationDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.ConnectionsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.ErrorDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.NewConnectionWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.PreferencesBot;
import org.apache.directory.studio.test.integration.ui.bots.StudioBot;
import org.apache.directory.studio.test.integration.ui.bots.utils.Assertions;
import org.apache.directory.studio.test.integration.ui.bots.utils.FrameworkRunnerWithScreenshotCaptureListener;
import org.junit.After;
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
//@CreateDS(allowAnonAccess = true, name = "KeyStoreIT-class")
public class CertificateValidationTest extends AbstractLdapTestUnit
{
    static final long YEAR_MILLIS = 365L * 24L * 3600L * 1000L;

    @Rule
    public TestName name = new TestName();

    private static StudioBot studioBot;
    private static ConnectionsViewBot connectionsViewBot;
    private static NewConnectionWizardBot wizardBot;


    @BeforeClass
    public static void setUpClass() throws Exception
    {
        studioBot = new StudioBot();
        studioBot.resetLdapPerspective();
        connectionsViewBot = studioBot.getConnectionView();
        // ErrorDialog.AUTOMATED_MODE = false;
    }


    @Before
    public void setUp() throws Exception
    {
        studioBot.resetLdapPerspective();

        // let Java use the key store
        System.setProperty( "javax.net.ssl.trustStore", ROOT_CA_KEYSTORE_PATH );
        System.setProperty( "javax.net.ssl.trustStorePassword", KEYSTORE_PW );
        System.setProperty( "javax.net.ssl.keyStore", ROOT_CA_KEYSTORE_PATH );
        System.setProperty( "javax.net.ssl.keyStorePassword", KEYSTORE_PW );
    }


    @After
    public void tearDown() throws Exception
    {
        connectionsViewBot.deleteTestConnections();

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

    private static final String KEYSTORE_PW = "changeit";

    private static final String ROOT_CA_KEYSTORE_PATH = "target/classes/root-ca-keystore.ks";
    private static KeyStore ROOT_CA_KEYSTORE;

    private static final String VALID_KEYSTORE_PATH = "target/classes/valid-keystore.ks";

    private static final String EXPIRED_KEYSTORE_PATH = "target/classes/expired-keystore.ks";

    private static final String NOT_YET_VALID_KEYSTORE_PATH = "target/classes/not-yet-valid-keystore.ks";

    private static final String WRONG_HOSTNAME_KEYSTORE_PATH = "target/classes/invalid-hostname-keystore.ks";

    private static final String SMALL_KEYSIZE_KEYSTORE_PATH = "target/classes/small-keysize-keystore.ks";

    private static final String SELF_SIGNED_KEYSTORE_PATH = "target/classes/self-signed-keystore.ks";

    private static final String MULTIPLE_ISSUES_KEYSTORE_PATH = "target/classes/multiple-issues-keystore.ks";


    @BeforeClass
    public static void installKeyStoreWithCertificate() throws Exception
    {
        String hostName = InetAddress.getLocalHost().getHostName();
        String issuerDn = TlsKeyGenerator.CERTIFICATE_PRINCIPAL_DN;
        //String subjectDn = "CN=" + hostName;
        String subjectDn = "CN=" + LOCALHOST;
        Date startDate = new Date();
        Date expiryDate = new Date( System.currentTimeMillis() + TlsKeyGenerator.YEAR_MILLIS );
        String keyAlgo = "RSA";
        int keySize = 1024;

        // generate root CA, self-signed
        String rootCaSubjectDn = issuerDn;
        ROOT_CA_KEYSTORE = createKeyStore( rootCaSubjectDn, issuerDn, startDate, expiryDate, keyAlgo, keySize, null,
            ROOT_CA_KEYSTORE_PATH );
        PrivateKey rootCaPrivateKey = ( PrivateKey ) ROOT_CA_KEYSTORE.getKey( "apacheds", KEYSTORE_PW.toCharArray() );

        // generate a valid certificate, signed by root CA
        createKeyStore( subjectDn, issuerDn, startDate, expiryDate, keyAlgo, keySize, rootCaPrivateKey,
            VALID_KEYSTORE_PATH );

        // generate an expired certificate, signed by root CA
        Date expiredStartDate = new Date( System.currentTimeMillis() - TlsKeyGenerator.YEAR_MILLIS );
        Date expiredExpiryDate = new Date( System.currentTimeMillis() - TlsKeyGenerator.YEAR_MILLIS / 365 );
        createKeyStore( subjectDn, issuerDn, expiredStartDate, expiredExpiryDate, keyAlgo, keySize,
            rootCaPrivateKey, EXPIRED_KEYSTORE_PATH );

        // generate a not yet valid certificate, signed by root CA
        Date notYetValidStartDate = new Date( System.currentTimeMillis() + TlsKeyGenerator.YEAR_MILLIS / 365 );
        Date notYetValidExpiryDate = new Date( System.currentTimeMillis() + TlsKeyGenerator.YEAR_MILLIS );
        createKeyStore( subjectDn, issuerDn, notYetValidStartDate, notYetValidExpiryDate, keyAlgo, keySize,
            rootCaPrivateKey, NOT_YET_VALID_KEYSTORE_PATH );

        // generate a certificate with small key size, signed by root CA
        int smallKeySize = 512;
        createKeyStore( subjectDn, issuerDn, startDate, expiryDate, keyAlgo, smallKeySize,
            rootCaPrivateKey, SMALL_KEYSIZE_KEYSTORE_PATH );

        // generate a certificate with an invalid hostname, signed by root CA
        String wrongHostnameSubjectDn = "CN=foo.example.com";
        createKeyStore( wrongHostnameSubjectDn, issuerDn, startDate, expiryDate, keyAlgo, keySize, rootCaPrivateKey,
            WRONG_HOSTNAME_KEYSTORE_PATH );

        // generate a self-signed certificate
        createKeyStore( subjectDn, subjectDn, startDate, expiryDate, keyAlgo, keySize, null,
            SELF_SIGNED_KEYSTORE_PATH );

        // generate a certificate with multipe issues: expired, wrong hostname, self-signed
        createKeyStore( wrongHostnameSubjectDn, wrongHostnameSubjectDn, expiredStartDate, expiredExpiryDate, keyAlgo,
            keySize, null, MULTIPLE_ISSUES_KEYSTORE_PATH );
    }


    private static KeyStore createKeyStore( String subjectDn, String issuerDn, Date startDate, Date expiryDate,
        String keyAlgo, int keySize, PrivateKey optionalSigningKey, String keystorePath )
        throws Exception
    {
        File goodKeyStoreFile = new File( keystorePath );
        if ( goodKeyStoreFile.exists() )
        {
            goodKeyStoreFile.delete();
        }
        Entry entry = new DefaultEntry();
        TlsKeyGenerator.addKeyPair( entry, issuerDn, subjectDn, startDate, expiryDate, keyAlgo, keySize,
            optionalSigningKey );
        KeyPair keyPair = TlsKeyGenerator.getKeyPair( entry );
        X509Certificate cert = TlsKeyGenerator.getCertificate( entry );
        //System.out.println( cert );

        KeyStore keyStore = KeyStore.getInstance( KeyStore.getDefaultType() );
        keyStore.load( null, null );
        keyStore.setCertificateEntry( "apacheds", cert );
        keyStore.setKeyEntry( "apacheds", keyPair.getPrivate(), KEYSTORE_PW.toCharArray(), new Certificate[]
            { cert } );
        keyStore.store( new FileOutputStream( goodKeyStoreFile ), KEYSTORE_PW.toCharArray() );
        return keyStore;
    }


    private String getConnectionName()
    {
        return "NewConnectionWizardTest." + name.getMethodName();
    }


    /**
     * Tests ldaps:// with a valid certificate.
     */
    @CreateLdapServer(keyStore = VALID_KEYSTORE_PATH, certificatePassword = KEYSTORE_PW)
    @Test
    public void testLdapsCertificateValidationOK() throws Exception
    {
        wizardBotWithLdaps();

        // check the certificate, should be OK
        String result = wizardBot.clickCheckAuthenticationButton();
        assertNull( "Expected OK, valid and trusted certificate", result );

        wizardBot.clickCancelButton();
    }


    /**
     * Tests ldaps:// with an expired certificate.
     */
    @CreateLdapServer(keyStore = EXPIRED_KEYSTORE_PATH, certificatePassword = KEYSTORE_PW)
    @Test
    public void testLdapsCertificateValidationExpired() throws Exception
    {
        wizardBotWithLdaps();

        // check the certificate, expecting the trust dialog
        CertificateTrustDialogBot trustDialogBot = wizardBot
            .clickCheckAuthenticationButtonExpectingCertificateTrustDialog();
        assertTrue( trustDialogBot.isExpired() );
        assertFalse( trustDialogBot.isSelfSigned() );
        assertFalse( trustDialogBot.isNotYetValid() );
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
    @CreateLdapServer(keyStore = EXPIRED_KEYSTORE_PATH, certificatePassword = KEYSTORE_PW)
    @Test
    public void testLdapsCertificateDoNotTrust() throws Exception
    {
        wizardBotWithLdaps();

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
    @CreateLdapServer(keyStore = EXPIRED_KEYSTORE_PATH, certificatePassword = KEYSTORE_PW)
    @Test
    public void testLdapsCertificateTrustTemporary() throws Exception
    {
        wizardBotWithLdaps();

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
    @CreateLdapServer(keyStore = EXPIRED_KEYSTORE_PATH, certificatePassword = KEYSTORE_PW)
    @Test
    public void testLdapsCertificateTrustPermanent() throws Exception
    {
        wizardBotWithLdaps();

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
     * by putting the root certificate into a temporary key store.
     */
    @Test
    @CreateLdapServer(keyStore = VALID_KEYSTORE_PATH, certificatePassword = KEYSTORE_PW, extendedOpHandlers = StartTlsHandler.class)
    public void testStartTlsCertificateValidationOK() throws Exception
    {
        // enter connection parameter
        wizardBot = connectionsViewBot.openNewConnectionWizard();
        wizardBot.typeConnectionName( getConnectionName() );
        wizardBot.typeHost( LOCALHOST );
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
     * DIRSTUDIO-1205: SSL/TLS with small key size is not working.
     */
    @CreateLdapServer(keyStore = SMALL_KEYSIZE_KEYSTORE_PATH, certificatePassword = KEYSTORE_PW, extendedOpHandlers = StartTlsHandler.class)
    @Test
    public void testStartTlsCertificateValidationSmallKeysizeError() throws Exception
    {
        wizardBotWithStartTls();

        // check the certificate, expecting the trust dialog
        CertificateTrustDialogBot trustDialogBot = wizardBot
            .clickCheckAuthenticationButtonExpectingCertificateTrustDialog();
        assertFalse( trustDialogBot.isExpired() );
        assertFalse( trustDialogBot.isSelfSigned() );
        assertFalse( trustDialogBot.isNotYetValid() );
        assertFalse( trustDialogBot.isHostNameMismatch() );
        assertFalse( trustDialogBot.isIssuerUnkown() );
        assertTrue( trustDialogBot.hasErrorMessage( "Algorithm constraints check failed on keysize limits" ) );

        trustDialogBot.selectDontTrust();
        clickOkButtonExpectingCertficateErrorDialog( trustDialogBot, "Failed to verify certification path",
            "Algorithm constraints check failed on keysize limits", "RSA 512bit key used" );
        wizardBot.clickCancelButton();
    }


    /**
     * Tests StartTLS with an expired certificate.
     */
    @CreateLdapServer(keyStore = EXPIRED_KEYSTORE_PATH, certificatePassword = KEYSTORE_PW, extendedOpHandlers = StartTlsHandler.class)
    @Test
    public void testStartTlsCertificateValidationExpired() throws Exception
    {
        wizardBotWithStartTls();

        // check the certificate, expecting the trust dialog
        CertificateTrustDialogBot trustDialogBot = wizardBot
            .clickCheckAuthenticationButtonExpectingCertificateTrustDialog();
        assertTrue( trustDialogBot.isExpired() );
        assertFalse( trustDialogBot.isSelfSigned() );
        assertFalse( trustDialogBot.isNotYetValid() );
        assertFalse( trustDialogBot.isHostNameMismatch() );
        assertFalse( trustDialogBot.isIssuerUnkown() );

        trustDialogBot.selectDontTrust();
        clickOkButtonExpectingCertficateErrorDialog( trustDialogBot, "Certificate expired", "NotAfter" );
        wizardBot.clickCancelButton();
    }


    /**
     * Tests StartTLS with an not yet valid certificate.
     */
    @CreateLdapServer(keyStore = NOT_YET_VALID_KEYSTORE_PATH, certificatePassword = KEYSTORE_PW, extendedOpHandlers = StartTlsHandler.class)
    @Test
    public void testStartTlsCertificateValidationNotYetValid() throws Exception
    {
        wizardBotWithStartTls();

        // check the certificate, expecting the trust dialog
        CertificateTrustDialogBot trustDialogBot = wizardBot
            .clickCheckAuthenticationButtonExpectingCertificateTrustDialog();
        assertTrue( trustDialogBot.isNotYetValid() );
        assertFalse( trustDialogBot.isSelfSigned() );
        assertFalse( trustDialogBot.isExpired() );
        assertFalse( trustDialogBot.isHostNameMismatch() );
        assertFalse( trustDialogBot.isIssuerUnkown() );

        trustDialogBot.selectDontTrust();
        clickOkButtonExpectingCertficateErrorDialog( trustDialogBot, "Certificate not yet valid", "NotBefore" );
        wizardBot.clickCancelButton();
    }


    /**
     * Tests StartTLS with a certificate where the certificate's host name
     * doesn't match the server's host name (localhost)
     */
    @CreateLdapServer(keyStore = WRONG_HOSTNAME_KEYSTORE_PATH, certificatePassword = KEYSTORE_PW, extendedOpHandlers = StartTlsHandler.class)
    @Test
    public void testStartTlsCertificateValidationHostnameMismatch() throws Exception
    {
        wizardBotWithStartTls();

        // check the certificate, expecting the trust dialog
        CertificateTrustDialogBot trustDialogBot = wizardBot
            .clickCheckAuthenticationButtonExpectingCertificateTrustDialog();
        assertTrue( trustDialogBot.isHostNameMismatch() );
        assertFalse( trustDialogBot.isIssuerUnkown() );
        assertFalse( trustDialogBot.isSelfSigned() );
        assertFalse( trustDialogBot.isNotYetValid() );
        assertFalse( trustDialogBot.isExpired() );

        trustDialogBot.selectDontTrust();
        clickOkButtonExpectingCertficateErrorDialog( trustDialogBot, LOCALHOST, "foo.example.com" );
        wizardBot.clickCancelButton();
    }


    /**
     * Tests StartTLS with a certificate without valid certification path.
     */
    @CreateLdapServer(keyStore = VALID_KEYSTORE_PATH, certificatePassword = KEYSTORE_PW, extendedOpHandlers = StartTlsHandler.class)
    @Test
    public void testStartTlsCertificateValidationHNoValidCertificationPath() throws Exception
    {
        // delete custom Java key store settings
        System.clearProperty( "javax.net.ssl.trustStore" );
        System.clearProperty( "javax.net.ssl.trustStorePassword" );
        System.clearProperty( "javax.net.ssl.keyStore" );
        System.clearProperty( "javax.net.ssl.keyStorePassword" );

        wizardBotWithStartTls();

        // check the certificate, expecting the trust dialog
        CertificateTrustDialogBot trustDialogBot = wizardBot
            .clickCheckAuthenticationButtonExpectingCertificateTrustDialog();
        assertTrue( trustDialogBot.isIssuerUnkown() );
        assertFalse( trustDialogBot.isSelfSigned() );
        assertFalse( trustDialogBot.isHostNameMismatch() );
        assertFalse( trustDialogBot.isNotYetValid() );
        assertFalse( trustDialogBot.isExpired() );

        trustDialogBot.selectDontTrust();
        clickOkButtonExpectingCertficateErrorDialog( trustDialogBot,
            "unable to find valid certification path to requested target" );
        wizardBot.clickCancelButton();
    }


    /**
     * Tests StartTLS with a self-signed certificate.
     */
    @CreateLdapServer(extendedOpHandlers = StartTlsHandler.class, keyStore = SELF_SIGNED_KEYSTORE_PATH, certificatePassword = KEYSTORE_PW)
    @Test
    public void testStartTlsCertificateValidationSelfSigned() throws Exception
    {
        wizardBotWithStartTls();

        // check the certificate, expecting the trust dialog
        CertificateTrustDialogBot trustDialogBot = wizardBot
            .clickCheckAuthenticationButtonExpectingCertificateTrustDialog();
        assertTrue( trustDialogBot.isSelfSigned() );
        assertFalse( trustDialogBot.isHostNameMismatch() );
        assertFalse( trustDialogBot.isIssuerUnkown() );
        assertFalse( trustDialogBot.isNotYetValid() );
        assertFalse( trustDialogBot.isExpired() );

        trustDialogBot.selectDontTrust();
        clickOkButtonExpectingCertficateErrorDialog( trustDialogBot,
            "unable to find valid certification path to requested target" );
        wizardBot.clickCancelButton();
    }


    /**
     * Tests StartTLS with a certificate with multiple issues.
     */
    @CreateLdapServer(extendedOpHandlers = StartTlsHandler.class, keyStore = MULTIPLE_ISSUES_KEYSTORE_PATH, certificatePassword = KEYSTORE_PW)
    @Test
    public void testStartTlsCertificateValidationExpiredAndWrongHostnameAndSelfSigned() throws Exception
    {
        wizardBotWithStartTls();

        // check the certificate, expecting the trust dialog
        CertificateTrustDialogBot trustDialogBot = wizardBot
            .clickCheckAuthenticationButtonExpectingCertificateTrustDialog();
        assertTrue( trustDialogBot.isSelfSigned() );
        assertTrue( trustDialogBot.isHostNameMismatch() );
        assertTrue( trustDialogBot.isExpired() );
        assertFalse( trustDialogBot.isIssuerUnkown() );
        assertFalse( trustDialogBot.isNotYetValid() );

        trustDialogBot.selectDontTrust();
        clickOkButtonExpectingCertficateErrorDialog( trustDialogBot );
        wizardBot.clickCancelButton();
    }


    /**
     * Tests that when selecting "Don't trust" the certificate is not trusted
     * and not added to any key store.
     */
    @CreateLdapServer(transports = @CreateTransport(protocol = "LDAP"), extendedOpHandlers = StartTlsHandler.class, keyStore = EXPIRED_KEYSTORE_PATH, certificatePassword = KEYSTORE_PW)
    @Test
    public void testStartTlsCertificateDoNotTrust() throws Exception
    {
        wizardBotWithStartTls();

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
    @CreateLdapServer(transports = @CreateTransport(protocol = "LDAP"), extendedOpHandlers = StartTlsHandler.class, keyStore = EXPIRED_KEYSTORE_PATH, certificatePassword = KEYSTORE_PW)
    @Test
    public void testStartTlsCertificateTrustTemporary() throws Exception
    {
        wizardBotWithStartTls();

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
    @CreateLdapServer(transports = @CreateTransport(protocol = "LDAP"), extendedOpHandlers = StartTlsHandler.class, keyStore = EXPIRED_KEYSTORE_PATH, certificatePassword = KEYSTORE_PW)
    @Test
    public void testStartTlsCertificateTrustPermanent() throws Exception
    {
        wizardBotWithStartTls();

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


    private void wizardBotWithLdaps()
    {
        // enter connection parameter and authentication parameter
        wizardBot = connectionsViewBot.openNewConnectionWizard();
        wizardBot.typeConnectionName( getConnectionName() );
        wizardBot.typeHost( LOCALHOST );
        wizardBot.typePort( ldapServer.getPortSSL() );
        wizardBot.selectLdapsEncryption();
        wizardBot.clickNextButton();
        wizardBot.typeUser( "uid=admin,ou=system" );
        wizardBot.typePassword( "secret" );
    }


    private void wizardBotWithStartTls()
    {
        // enter connection parameter and authentication parameter
        wizardBot = connectionsViewBot.openNewConnectionWizard();
        wizardBot.typeConnectionName( getConnectionName() );
        wizardBot.typeHost( LOCALHOST );
        wizardBot.typePort( ldapServer.getPort() );
        wizardBot.selectStartTlsEncryption();
        wizardBot.clickNextButton();
        wizardBot.typeUser( "uid=admin,ou=system" );
        wizardBot.typePassword( "secret" );
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

}
