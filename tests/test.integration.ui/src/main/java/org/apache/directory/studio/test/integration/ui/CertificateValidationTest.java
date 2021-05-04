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


import static org.apache.directory.studio.test.integration.ui.utils.Constants.LOCALHOST;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

import org.apache.directory.api.ldap.model.constants.SchemaConstants;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.server.core.security.TlsKeyGenerator;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.test.integration.junit5.ApacheDirectoryServer;
import org.apache.directory.studio.test.integration.junit5.LdapServerType;
import org.apache.directory.studio.test.integration.junit5.LdapServersSource;
import org.apache.directory.studio.test.integration.junit5.TestLdapServer;
import org.apache.directory.studio.test.integration.ui.bots.CertificateTrustDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.CertificateValidationPreferencePageBot;
import org.apache.directory.studio.test.integration.ui.bots.CheckAuthenticationDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.ErrorDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.NewConnectionWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.PreferencesBot;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;


/**
 * Tests secure connection handling.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class CertificateValidationTest extends AbstractTestBase
{
    static final long YEAR_MILLIS = 365L * 24L * 3600L * 1000L;

    private TestInfo testInfo;

    private static NewConnectionWizardBot wizardBot;

    @BeforeEach
    public void setUp( TestInfo testInfo ) throws Exception
    {
        this.testInfo = testInfo;

        // let Java use the key store
        System.setProperty( "javax.net.ssl.trustStore", ROOT_CA_KEYSTORE_PATH );
        System.setProperty( "javax.net.ssl.trustStorePassword", KEYSTORE_PW );
        System.setProperty( "javax.net.ssl.keyStore", ROOT_CA_KEYSTORE_PATH );
        System.setProperty( "javax.net.ssl.keyStorePassword", KEYSTORE_PW );
    }


    @AfterEach
    public void tearDown() throws Exception
    {
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

    private static final String UNTRUSTED_ROOT_CA_KEYSTORE_PATH = "target/classes/untrusted-root-ca-keystore.ks";

    private static final String UNTRUSTED_KEYSTORE_PATH = "target/classes/untrusted-keystore.ks";

    private static final String MULTIPLE_ISSUES_KEYSTORE_PATH = "target/classes/multiple-issues-keystore.ks";

    @BeforeAll
    public static void installKeyStoreWithCertificate() throws Exception
    {
        String issuerDn = "CN=trusted-root-ca";
        String subjectDn = "CN=" + LOCALHOST;
        Date startDate = new Date();
        Date expiryDate = new Date( System.currentTimeMillis() + TlsKeyGenerator.YEAR_MILLIS );
        String keyAlgo = "RSA";
        int keySize = 1024;

        // generate root CA, self-signed
        ROOT_CA_KEYSTORE = createKeyStore( issuerDn, issuerDn, startDate, expiryDate, keyAlgo, keySize, null,
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

        // generate a certificate, signed by untrusted root CA
        String untrustedRootCaIssuerDn = "CN=untrusted-root-ca";
        createKeyStore( untrustedRootCaIssuerDn, untrustedRootCaIssuerDn, startDate, expiryDate, keyAlgo, keySize, null,
            UNTRUSTED_ROOT_CA_KEYSTORE_PATH );
        PrivateKey untrustedRootCaPrivateKey = ( PrivateKey ) ROOT_CA_KEYSTORE.getKey( "apacheds",
            KEYSTORE_PW.toCharArray() );
        createKeyStore( subjectDn, untrustedRootCaIssuerDn, startDate, expiryDate, keyAlgo, keySize,
            untrustedRootCaPrivateKey,
            UNTRUSTED_KEYSTORE_PATH );

        // generate a certificate with multiple issues: expired, wrong hostname, self-signed
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
        addKeyPair( entry, issuerDn, subjectDn, startDate, expiryDate, keyAlgo, keySize,
            optionalSigningKey );
        KeyPair keyPair = TlsKeyGenerator.getKeyPair( entry );
        X509Certificate cert = TlsKeyGenerator.getCertificate( entry );
        //System.out.println( cert );

        KeyStore keyStore = KeyStore.getInstance( KeyStore.getDefaultType() );
        keyStore.load( null, null );
        keyStore.setCertificateEntry( "apacheds", cert );
        keyStore.setKeyEntry( "apacheds", keyPair.getPrivate(), KEYSTORE_PW.toCharArray(), new Certificate[]
            { cert } );
        try ( FileOutputStream out = new FileOutputStream( goodKeyStoreFile ) )
        {
            keyStore.store( out, KEYSTORE_PW.toCharArray() );
        }
        return keyStore;
    }

    static
    {
        Security.addProvider( new BouncyCastleProvider() );
    }

    public static void addKeyPair( Entry entry, String issuerDN, String subjectDN, Date startDate, Date expiryDate,
        String keyAlgo, int keySize, PrivateKey optionalSigningKey ) throws LdapException
    {
        Attribute objectClass = entry.get( SchemaConstants.OBJECT_CLASS_AT );

        if ( objectClass == null )
        {
            entry.put( SchemaConstants.OBJECT_CLASS_AT, TlsKeyGenerator.TLS_KEY_INFO_OC,
                SchemaConstants.INET_ORG_PERSON_OC );
        }
        else
        {
            objectClass.add( TlsKeyGenerator.TLS_KEY_INFO_OC, SchemaConstants.INET_ORG_PERSON_OC );
        }

        KeyPairGenerator generator = null;
        try
        {
            generator = KeyPairGenerator.getInstance( keyAlgo );
        }
        catch ( NoSuchAlgorithmException e )
        {
            LdapException ne = new LdapException( "" );
            ne.initCause( e );
            throw ne;
        }

        generator.initialize( keySize );
        KeyPair keypair = generator.genKeyPair();
        entry.put( TlsKeyGenerator.KEY_ALGORITHM_AT, keyAlgo );

        // Generate the private key attributes
        PrivateKey privateKey = keypair.getPrivate();
        entry.put( TlsKeyGenerator.PRIVATE_KEY_AT, privateKey.getEncoded() );
        entry.put( TlsKeyGenerator.PRIVATE_KEY_FORMAT_AT, privateKey.getFormat() );

        PublicKey publicKey = keypair.getPublic();
        entry.put( TlsKeyGenerator.PUBLIC_KEY_AT, publicKey.getEncoded() );
        entry.put( TlsKeyGenerator.PUBLIC_KEY_FORMAT_AT, publicKey.getFormat() );

        // Generate the self-signed certificate
        BigInteger serialNumber = BigInteger.valueOf( System.currentTimeMillis() );

        X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
        X500Principal issuerName = new X500Principal( issuerDN );
        X500Principal subjectName = new X500Principal( subjectDN );

        certGen.setSerialNumber( serialNumber );
        certGen.setIssuerDN( issuerName );
        certGen.setNotBefore( startDate );
        certGen.setNotAfter( expiryDate );
        certGen.setSubjectDN( subjectName );
        certGen.setPublicKey( publicKey );
        certGen.setSignatureAlgorithm( "SHA256With" + keyAlgo );
        certGen.addExtension( Extension.basicConstraints, false, new BasicConstraints( true ) );
        certGen.addExtension( Extension.extendedKeyUsage, true, new ExtendedKeyUsage(
            new KeyPurposeId[]
            { KeyPurposeId.id_kp_clientAuth, KeyPurposeId.id_kp_serverAuth } ) );

        try
        {
            PrivateKey signingKey = optionalSigningKey != null ? optionalSigningKey : privateKey;
            X509Certificate cert = certGen.generate( signingKey, "BC" );
            entry.put( TlsKeyGenerator.USER_CERTIFICATE_AT, cert.getEncoded() );
        }
        catch ( Exception e )
        {
            LdapException ne = new LdapException( "" );
            ne.initCause( e );
            throw ne;
        }
    }


    private String getConnectionName()
    {
        return testInfo.getTestMethod().map( Method::getName ).orElse( "null" ) + " "
            + testInfo.getDisplayName();
    }


    /**
     * Tests ldaps:// with a valid certificate.
     */
    @ParameterizedTest
    @LdapServersSource(only = LdapServerType.ApacheDS, reason = "Update of keystore only implemented for ApacheDS")
    public void testLdapsCertificateValidationOK( ApacheDirectoryServer server ) throws Exception
    {
        server.setKeystore( VALID_KEYSTORE_PATH );
        wizardBotWithLdaps( server );

        // check the certificate, should be OK
        String result = wizardBot.clickCheckAuthenticationButton();
        assertNull( result, "Expected OK, valid and trusted certificate" );

        wizardBot.clickCancelButton();
    }


    /**
     * Tests ldaps:// with an expired certificate.
     */
    @ParameterizedTest
    @LdapServersSource(only = LdapServerType.ApacheDS, reason = "Update of keystore only implemented for ApacheDS")
    public void testLdapsCertificateValidationExpired( ApacheDirectoryServer server ) throws Exception
    {
        server.setKeystore( EXPIRED_KEYSTORE_PATH );
        wizardBotWithLdaps( server );

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
    @ParameterizedTest
    @LdapServersSource
    public void testLdapsCertificateDoNotTrust( TestLdapServer server ) throws Exception
    {
        wizardBotWithLdaps( server );

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
    @ParameterizedTest
    @LdapServersSource
    public void testLdapsCertificateTrustTemporary( TestLdapServer server ) throws Exception
    {
        wizardBotWithLdaps( server );

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
        assertNull( result, "Expected OK, valid and trusted certificate" );

        wizardBot.clickCancelButton();
    }


    /**
     * Tests that when selecting "Trust permanent" the certificate is trusted
     * and added to the permanent key store.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testLdapsCertificateTrustPermanent( TestLdapServer server ) throws Exception
    {
        wizardBotWithLdaps( server );

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
        assertNull( result, "Expected OK, valid and trusted certificate" );

        wizardBot.clickCancelButton();
    }


    /**
     * Tests StartTLS with an valid certificate. This is simulated
     * by putting the root certificate into a temporary key store.
     */
    @ParameterizedTest
    @LdapServersSource(only = LdapServerType.ApacheDS, reason = "Update of keystore only implemented for ApacheDS")
    public void testStartTlsCertificateValidationOK( ApacheDirectoryServer server ) throws Exception
    {
        server.setKeystore( VALID_KEYSTORE_PATH );

        // enter connection parameter
        wizardBot = connectionsViewBot.openNewConnectionWizard();
        wizardBot.typeConnectionName( getConnectionName() );
        wizardBot.typeHost( LOCALHOST );
        wizardBot.typePort( server.getPort() );
        wizardBot.selectStartTlsEncryption();

        // check the certificate, should be OK
        String result = wizardBot.clickCheckNetworkParameterButton();
        assertNull( result, "Expected OK, valid and trusted certificate" );

        // enter correct authentication parameter
        wizardBot.clickNextButton();
        wizardBot.typeUser( "uid=admin,ou=system" );
        wizardBot.typePassword( "secret" );

        // check the certificate again, should be OK
        String result2 = wizardBot.clickCheckAuthenticationButton();
        assertNull( result2, "Expected OK, valid and trusted certificate" );

        wizardBot.clickCancelButton();
    }


    /**
     * DIRSTUDIO-1205: SSL/TLS with small key size is not working.
     */
    @ParameterizedTest
    @LdapServersSource(only = LdapServerType.ApacheDS, reason = "Update of keystore only implemented for ApacheDS")
    public void testStartTlsCertificateValidationSmallKeysizeError( ApacheDirectoryServer server ) throws Exception
    {
        server.setKeystore( SMALL_KEYSIZE_KEYSTORE_PATH );
        wizardBotWithStartTls( server );

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
    @ParameterizedTest
    @LdapServersSource(only = LdapServerType.ApacheDS, reason = "Update of keystore only implemented for ApacheDS")
    public void testStartTlsCertificateValidationExpired( ApacheDirectoryServer server ) throws Exception
    {
        server.setKeystore( EXPIRED_KEYSTORE_PATH );
        wizardBotWithStartTls( server );

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
    @ParameterizedTest
    @LdapServersSource(only = LdapServerType.ApacheDS, reason = "Update of keystore only implemented for ApacheDS")
    public void testStartTlsCertificateValidationNotYetValid( ApacheDirectoryServer server ) throws Exception
    {
        server.setKeystore( NOT_YET_VALID_KEYSTORE_PATH );
        wizardBotWithStartTls( server );

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
    @ParameterizedTest
    @LdapServersSource(only = LdapServerType.ApacheDS, reason = "Update of keystore only implemented for ApacheDS")
    public void testStartTlsCertificateValidationHostnameMismatch( ApacheDirectoryServer server ) throws Exception
    {
        server.setKeystore( WRONG_HOSTNAME_KEYSTORE_PATH );
        wizardBotWithStartTls( server );

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
    @ParameterizedTest
    @LdapServersSource(only = LdapServerType.ApacheDS, reason = "Update of keystore only implemented for ApacheDS")
    public void testStartTlsCertificateValidationNoValidCertificationPath( ApacheDirectoryServer server )
        throws Exception
    {
        server.setKeystore( UNTRUSTED_KEYSTORE_PATH );
        wizardBotWithStartTls( server );

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
    @ParameterizedTest
    @LdapServersSource(only = LdapServerType.ApacheDS, reason = "Update of keystore only implemented for ApacheDS")
    public void testStartTlsCertificateValidationSelfSigned( ApacheDirectoryServer server ) throws Exception
    {
        server.setKeystore( SELF_SIGNED_KEYSTORE_PATH );
        wizardBotWithStartTls( server );

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
    @ParameterizedTest
    @LdapServersSource(only = LdapServerType.ApacheDS, reason = "Update of keystore only implemented for ApacheDS")
    public void testStartTlsCertificateValidationExpiredAndWrongHostnameAndSelfSigned( ApacheDirectoryServer server )
        throws Exception
    {
        server.setKeystore( MULTIPLE_ISSUES_KEYSTORE_PATH );
        wizardBotWithStartTls( server );

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
    @ParameterizedTest
    @LdapServersSource
    public void testStartTlsCertificateDoNotTrust( TestLdapServer server ) throws Exception
    {
        wizardBotWithStartTls( server );

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
    @ParameterizedTest
    @LdapServersSource
    public void testStartTlsCertificateTrustTemporary( TestLdapServer server ) throws Exception
    {
        wizardBotWithStartTls( server );

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
        assertNull( result, "Expected OK, valid and trusted certificate" );

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
    @ParameterizedTest
    @LdapServersSource
    public void testStartTlsCertificateTrustPermanent( TestLdapServer server ) throws Exception
    {
        wizardBotWithStartTls( server );

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
        assertNull( result, "Expected OK, valid and trusted certificate" );

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


    private void wizardBotWithLdaps( TestLdapServer server )
    {
        // enter connection parameter and authentication parameter
        wizardBot = connectionsViewBot.openNewConnectionWizard();
        wizardBot.typeConnectionName( getConnectionName() );
        wizardBot.typeHost( server.getHost() );
        wizardBot.typePort( server.getPortSSL() );
        wizardBot.selectLdapsEncryption();
        wizardBot.clickNextButton();
        wizardBot.typeUser( server.getAdminDn() );
        wizardBot.typePassword( server.getAdminPassword() );
    }


    private void wizardBotWithStartTls( TestLdapServer server )
    {
        // enter connection parameter and authentication parameter
        wizardBot = connectionsViewBot.openNewConnectionWizard();
        wizardBot.typeConnectionName( getConnectionName() );
        wizardBot.typeHost( server.getHost() );
        wizardBot.typePort( server.getPort() );
        wizardBot.selectStartTlsEncryption();
        wizardBot.clickNextButton();
        wizardBot.typeUser( server.getAdminDn() );
        wizardBot.typePassword( server.getAdminPassword() );
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
