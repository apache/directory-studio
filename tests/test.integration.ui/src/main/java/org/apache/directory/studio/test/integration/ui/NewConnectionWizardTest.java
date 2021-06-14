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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.directory.api.ldap.model.constants.SaslQoP;
import org.apache.directory.api.ldap.model.constants.SaslSecurityStrength;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionManager;
import org.apache.directory.studio.connection.core.ConnectionParameter.AuthenticationMethod;
import org.apache.directory.studio.connection.core.ConnectionParameter.EncryptionMethod;
import org.apache.directory.studio.test.integration.junit5.LdapServerType;
import org.apache.directory.studio.test.integration.junit5.LdapServersSource;
import org.apache.directory.studio.test.integration.junit5.LdapServersSource.Mode;
import org.apache.directory.studio.test.integration.junit5.TestLdapServer;
import org.apache.directory.studio.test.integration.ui.bots.CertificateTrustDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.ErrorDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.NewConnectionWizardBot;
import org.apache.mina.util.AvailablePortFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;


/**
 * Tests the new connection wizard.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewConnectionWizardTest extends AbstractTestBase
{
    private NewConnectionWizardBot wizardBot;
    private TestInfo testInfo;

    @BeforeEach
    public void beforeEach( TestInfo testInfo )
    {
        this.wizardBot = connectionsViewBot.openNewConnectionWizard();
        this.testInfo = testInfo;
    }


    private String getConnectionName()
    {
        return testInfo.getTestMethod().map( Method::getName ).orElse( "null" ) + " "
            + testInfo.getDisplayName();
    }


    /**
     * Tests enabled and disabled widgets, depending on the provided input.
     */
    @Test
    public void testEnabledDisabledWidgets()
    {
        assertTrue( wizardBot.isVisible() );

        // check network parameter buttons
        assertFalse( wizardBot.isViewCertificateButtonEnabled() );
        assertFalse( wizardBot.isCheckNetworkParameterButtonEnabled() );
        // ensure "Next >" and "Finish" buttons are disabled
        assertFalse( wizardBot.isBackButtonEnabled() );
        assertFalse( wizardBot.isNextButtonEnabled() );
        assertFalse( wizardBot.isFinishButtonEnabled() );
        assertTrue( wizardBot.isCancelButtonEnabled() );

        // enter connection parameter
        wizardBot.typeConnectionName( getConnectionName() );
        wizardBot.typeHost( "test.example.com" );
        wizardBot.typePort( 389 );
        // check network parameter buttons
        assertFalse( wizardBot.isViewCertificateButtonEnabled() );
        assertTrue( wizardBot.isCheckNetworkParameterButtonEnabled() );
        // ensure "Next >" button is enabled
        assertFalse( wizardBot.isBackButtonEnabled() );
        assertTrue( wizardBot.isNextButtonEnabled() );
        assertFalse( wizardBot.isFinishButtonEnabled() );
        assertTrue( wizardBot.isCancelButtonEnabled() );

        // clear host
        wizardBot.typeHost( "" );
        // check network parameter buttons
        assertFalse( wizardBot.isViewCertificateButtonEnabled() );
        assertFalse( wizardBot.isCheckNetworkParameterButtonEnabled() );
        // ensure "Next >" is disabled
        assertFalse( wizardBot.isBackButtonEnabled() );
        assertFalse( wizardBot.isNextButtonEnabled() );
        assertFalse( wizardBot.isFinishButtonEnabled() );
        assertTrue( wizardBot.isCancelButtonEnabled() );

        // enter host again
        wizardBot.typeHost( "test.example.com" );
        // check network parameter buttons
        assertFalse( wizardBot.isViewCertificateButtonEnabled() );
        assertTrue( wizardBot.isCheckNetworkParameterButtonEnabled() );
        // ensure "Next >" button is enabled
        assertFalse( wizardBot.isBackButtonEnabled() );
        assertTrue( wizardBot.isNextButtonEnabled() );
        assertFalse( wizardBot.isFinishButtonEnabled() );
        assertTrue( wizardBot.isCancelButtonEnabled() );

        // set StartTLS encryption
        wizardBot.selectStartTlsEncryption();
        // check network parameter buttons
        assertTrue( wizardBot.isViewCertificateButtonEnabled() );
        assertTrue( wizardBot.isCheckNetworkParameterButtonEnabled() );
        // check wizard buttons
        assertFalse( wizardBot.isBackButtonEnabled() );
        assertTrue( wizardBot.isNextButtonEnabled() );
        assertFalse( wizardBot.isFinishButtonEnabled() );
        assertTrue( wizardBot.isCancelButtonEnabled() );

        // set SSL encryption
        wizardBot.selectLdapsEncryption();
        // check network parameter buttons
        assertTrue( wizardBot.isViewCertificateButtonEnabled() );
        assertTrue( wizardBot.isCheckNetworkParameterButtonEnabled() );
        // check wizard buttons
        assertFalse( wizardBot.isBackButtonEnabled() );
        assertTrue( wizardBot.isNextButtonEnabled() );
        assertFalse( wizardBot.isFinishButtonEnabled() );
        assertTrue( wizardBot.isCancelButtonEnabled() );

        // set no encryption
        wizardBot.selectNoEncryption();
        // check network parameter buttons
        assertFalse( wizardBot.isViewCertificateButtonEnabled() );
        assertTrue( wizardBot.isCheckNetworkParameterButtonEnabled() );
        // check wizard buttons
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
        // ensure "<Back" is enabled, "Next >" and "Finish" is enabled
        assertTrue( wizardBot.isBackButtonEnabled() );
        assertTrue( wizardBot.isNextButtonEnabled() );
        assertTrue( wizardBot.isFinishButtonEnabled() );
        assertTrue( wizardBot.isCancelButtonEnabled() );

        // select password save
        wizardBot.selectSavePassword();
        // ensure password field is enabled but empty
        assertTrue( wizardBot.isPasswordEnabled() );
        // ensure "<Back" is enabled, "Next >" and "Finish" is disabled
        assertTrue( wizardBot.isBackButtonEnabled() );
        assertFalse( wizardBot.isNextButtonEnabled() );
        assertFalse( wizardBot.isFinishButtonEnabled() );
        assertTrue( wizardBot.isCancelButtonEnabled() );

        // enter authentication parameters again
        wizardBot.selectSimpleAuthentication();
        wizardBot.typeUser( "uid=admin,ou=system" );
        wizardBot.typePassword( "secret" );
        // ensure "<Back" is enabled, "Next >" and "Finish" is enabled
        assertTrue( wizardBot.isBackButtonEnabled() );
        assertTrue( wizardBot.isNextButtonEnabled() );
        assertTrue( wizardBot.isFinishButtonEnabled() );
        assertTrue( wizardBot.isCancelButtonEnabled() );

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
        assertTrue( wizardBot.isSavePasswordEnabled() );
        assertTrue( wizardBot.isRealmEnabled() );

        // select CRAM-MD5
        wizardBot.selectCramMD5Authentication();
        // ensure authentication parameter input fields are enabled, excluding SASL Realm field
        assertTrue( wizardBot.isCramMD5AuthenticationSelected() );
        assertTrue( wizardBot.isUserEnabled() );
        assertTrue( wizardBot.isPasswordEnabled() );
        assertTrue( wizardBot.isSavePasswordEnabled() );
        assertFalse( wizardBot.isRealmEnabled() );

        // select GSSAPI (Kerberos)
        wizardBot.selectGssApiAuthentication();
        // ensure authentication parameter input fields are disabled by default
        assertTrue( wizardBot.isGssApiAuthenticationSelected() );
        assertFalse( wizardBot.isUserEnabled() );
        assertFalse( wizardBot.isPasswordEnabled() );
        assertFalse( wizardBot.isSavePasswordEnabled() );
        assertFalse( wizardBot.isRealmEnabled() );
        // by default "Use native TGT" is selected
        assertTrue( wizardBot.isUseNativeTgtSelected() );
        assertFalse( wizardBot.isObtainTgtFromKdcSelected() );
        assertTrue( wizardBot.isUseNativeSystemConfigurationSelected() );
        assertFalse( wizardBot.isUseConfigurationFileSelected() );
        assertFalse( wizardBot.isUseManualConfigurationSelected() );
        assertFalse( wizardBot.isKerberosRealmEnabled() );
        assertFalse( wizardBot.isKdcHostEnabled() );
        assertFalse( wizardBot.isKdcPortEnabled() );

        // select GSSAPI (Kerberos) and "Obtain TGT from KDC"
        wizardBot.selectObtainTgtFromKdc();
        // ensure authentication parameter input fields are enabled
        assertTrue( wizardBot.isGssApiAuthenticationSelected() );
        assertTrue( wizardBot.isUserEnabled() );
        assertTrue( wizardBot.isPasswordEnabled() );
        assertTrue( wizardBot.isSavePasswordEnabled() );
        assertFalse( wizardBot.isRealmEnabled() );
        assertFalse( wizardBot.isUseNativeTgtSelected() );

        // select GSSAPI (Kerberos) and "Use configuration file"
        wizardBot.selectUseConfigurationFile();
        assertFalse( wizardBot.isUseNativeSystemConfigurationSelected() );
        assertTrue( wizardBot.isUseConfigurationFileSelected() );
        assertFalse( wizardBot.isUseManualConfigurationSelected() );
        assertFalse( wizardBot.isKerberosRealmEnabled() );
        assertFalse( wizardBot.isKdcHostEnabled() );
        assertFalse( wizardBot.isKdcPortEnabled() );

        // select GSSAPI (Kerberos) and "Use manual configuration"
        wizardBot.selectUseManualConfiguration();
        assertFalse( wizardBot.isUseNativeSystemConfigurationSelected() );
        assertFalse( wizardBot.isUseConfigurationFileSelected() );
        assertTrue( wizardBot.isUseManualConfigurationSelected() );
        assertTrue( wizardBot.isKerberosRealmEnabled() );
        assertTrue( wizardBot.isKdcHostEnabled() );
        assertTrue( wizardBot.isKdcPortEnabled() );
        assertFalse( wizardBot.isNextButtonEnabled() );

        // select GSSAPI (Kerberos) and "Use native system configuration" again
        wizardBot.selectUseNativeSystemConfiguration();
        assertTrue( wizardBot.isUseNativeSystemConfigurationSelected() );
        assertFalse( wizardBot.isUseConfigurationFileSelected() );
        assertFalse( wizardBot.isUseManualConfigurationSelected() );
        assertFalse( wizardBot.isKerberosRealmEnabled() );
        assertFalse( wizardBot.isKdcHostEnabled() );
        assertFalse( wizardBot.isKdcPortEnabled() );

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


    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testCreateConnectionNoEncryptionNoAuthOK( TestLdapServer server )
    {
        setConnectionParameters( server, EncryptionMethod.NONE );

        wizardBot.selectNoAuthentication();

        finishAndAssertConnection( server, EncryptionMethod.NONE, AuthenticationMethod.NONE, "", "" );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testCreateConnectionNoEncryptionNoAuthInvalidHostname( TestLdapServer server )
    {
        setConnectionParameters( server, EncryptionMethod.NONE );

        wizardBot.clickBackButton();
        String hostname = getInvalidHostName();
        wizardBot.typeHost( hostname );
        wizardBot.clickNextButton();
        wizardBot.selectNoAuthentication();

        finishAndAssertConnectionError( hostname );
    }


    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testCreateConnectionNoEncryptionSimpleAuthOK( TestLdapServer server ) throws UnknownHostException
    {
        // enter connection parameter
        wizardBot.typeConnectionName( getConnectionName() );
        wizardBot.typeHost( server.getHost() );
        wizardBot.typePort( server.getPort() );

        // click "Check Network Parameter" button
        String result = wizardBot.clickCheckNetworkParameterButton();
        assertNull( result, "Expected OK" );

        // enter IPv4 address as host
        wizardBot.typeHost( InetAddress.getByName( server.getHost() ).getHostAddress() );

        // click "Check Network Parameter" button
        result = wizardBot.clickCheckNetworkParameterButton();
        assertNull( result, "Expected OK" );

        // enter hostname as host again
        wizardBot.typeHost( server.getHost() );

        // jump to auth page
        wizardBot.clickNextButton();

        // enter authentication parameters
        wizardBot.typeUser( server.getAdminDn() );
        wizardBot.typePassword( server.getAdminPassword() );

        // click "Check Network Parameter" button
        result = wizardBot.clickCheckAuthenticationButton();
        assertNull( result, "Expected OK" );

        finishAndAssertConnection( server, EncryptionMethod.NONE, AuthenticationMethod.SIMPLE,
            server.getAdminDn(), server.getAdminPassword() );
    }


    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testCreateConnectionNoEncryptionSimpleAuthConfidentialityRequired( TestLdapServer server )

    {
        setConnectionParameters( server, EncryptionMethod.NONE );

        wizardBot.selectSimpleAuthentication();
        wizardBot.typeUser( server.getAdminDn() );
        wizardBot.typePassword( server.getAdminPassword() );

        server.setConfidentialityRequired( true );

        String result = wizardBot.clickCheckAuthenticationButton();
        assertThat( result, containsString( "[LDAP result code 13 - confidentialityRequired]" ) );

        finishAndAssertConnectionError( "[LDAP result code 13 - confidentialityRequired]" );

    }


    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testCreateConnectionNoEncryptionSaslCramMd5OK( TestLdapServer server )
    {
        setConnectionParameters( server, EncryptionMethod.NONE );

        wizardBot.selectCramMD5Authentication();
        wizardBot.typeUser( "user.1" );
        wizardBot.typePassword( "password" );
        wizardBot.selectQualityOfProtection( SaslQoP.AUTH );
        wizardBot.selectProtectionStrength( SaslSecurityStrength.HIGH );

        String result = wizardBot.clickCheckAuthenticationButton();
        assertNull( result, "Expected OK" );

        finishAndAssertConnection( server, EncryptionMethod.NONE, AuthenticationMethod.SASL_CRAM_MD5,
            "user.1", "password" );
    }


    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testCreateConnectionNoEncryptionSaslDigestMd5OK( TestLdapServer server )
    {
        setConnectionParameters( server, EncryptionMethod.NONE );

        wizardBot.selectDigestMD5Authentication();
        wizardBot.typeUser( "user.1" );
        wizardBot.typePassword( "password" );
        wizardBot.selectQualityOfProtection( SaslQoP.AUTH );
        wizardBot.selectProtectionStrength( SaslSecurityStrength.HIGH );

        String result = wizardBot.clickCheckAuthenticationButton();
        assertNull( result, "Expected OK" );

        finishAndAssertConnection( server, EncryptionMethod.NONE, AuthenticationMethod.SASL_DIGEST_MD5,
            "user.1", "password" );
    }


    @ParameterizedTest
    @LdapServersSource(mode = Mode.All, except = LdapServerType.Fedora389ds, reason = "Only secure binds configured for 389ds")
    public void testCreateConnectionNoEncryptionSaslDigestMd5ConfidentialityRequired( TestLdapServer server )
    {
        setConnectionParameters( server, EncryptionMethod.NONE );

        wizardBot.selectDigestMD5Authentication();
        wizardBot.typeUser( "user.1" );
        wizardBot.typePassword( "password" );
        wizardBot.selectQualityOfProtection( SaslQoP.AUTH );
        wizardBot.selectProtectionStrength( SaslSecurityStrength.HIGH );

        server.setConfidentialityRequired( true );

        finishAndAssertConnectionError( "[LDAP result code 13 - confidentialityRequired]" );
    }


    @ParameterizedTest
    @LdapServersSource(mode = Mode.All, except = LdapServerType.ApacheDS, reason = "Missing OSGi import: org.apache.directory.server.kerberos.shared.store.PrincipalStoreEntryModifier cannot be found by org.apache.directory.server.protocol.shared_2.0.0.AM26")
    public void testCreateConnectionNoEncryptionSaslGssapiNativeTgtOK( TestLdapServer server ) throws Exception
    {
        // obtain native TGT
        String[] cmd =
            { "/bin/sh", "-c", "echo secret | /usr/bin/kinit hnelson" };
        Process process = Runtime.getRuntime().exec( cmd );
        int exitCode = process.waitFor();
        assertEquals( 0, exitCode );

        setConnectionParameters( server, EncryptionMethod.NONE );

        wizardBot.selectGssApiAuthentication();
        wizardBot.selectQualityOfProtection( SaslQoP.AUTH );
        wizardBot.selectProtectionStrength( SaslSecurityStrength.HIGH );
        wizardBot.selectUseNativeTgt();
        wizardBot.selectUseNativeSystemConfiguration();

        String result = wizardBot.clickCheckAuthenticationButton();
        assertNull( result, "Expected OK" );

        finishAndAssertConnection( server, EncryptionMethod.NONE, AuthenticationMethod.SASL_GSSAPI,
            "", "" );
    }


    @ParameterizedTest
    @LdapServersSource(mode = Mode.All, except = LdapServerType.ApacheDS, reason = "Missing OSGi import: org.apache.directory.server.kerberos.shared.store.PrincipalStoreEntryModifier cannot be found by org.apache.directory.server.protocol.shared_2.0.0.AM26")
    public void testCreateConnectionNoEncryptionSaslGssapiObtainOK( TestLdapServer server )
    {
        setConnectionParameters( server, EncryptionMethod.NONE );

        wizardBot.selectGssApiAuthentication();
        wizardBot.selectObtainTgtFromKdc();
        wizardBot.typeUser( "hnelson" );
        wizardBot.typePassword( "secret" );
        wizardBot.selectQualityOfProtection( SaslQoP.AUTH );
        wizardBot.selectProtectionStrength( SaslSecurityStrength.HIGH );
        wizardBot.selectUseManualConfiguration();
        wizardBot.typeKerberosRealm( "EXAMPLE.COM" );
        wizardBot.typeKdcHost( "kerby.example.com" );
        wizardBot.typeKdcPort( 60088 );

        String result = wizardBot.clickCheckAuthenticationButton();
        assertNull( result, "Expected OK" );

        finishAndAssertConnection( server, EncryptionMethod.NONE, AuthenticationMethod.SASL_GSSAPI,
            "hnelson", "secret" );
    }


    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testCreateConnectionLdapsEncryptionNoAuthOK( TestLdapServer server )
    {
        setConnectionParameters( server, EncryptionMethod.LDAPS );

        wizardBot.selectNoAuthentication();

        finishAndAssertConnection( server, EncryptionMethod.LDAPS, AuthenticationMethod.NONE, "", "" );
    }


    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testCreateConnectionLdapsEncryptionSimpleAuthOK( TestLdapServer server )
    {
        setConnectionParameters( server, EncryptionMethod.LDAPS );

        wizardBot.typeUser( server.getAdminDn() );
        wizardBot.typePassword( server.getAdminPassword() );

        finishAndAssertConnection( server, EncryptionMethod.LDAPS, AuthenticationMethod.SIMPLE,
            server.getAdminDn(), server.getAdminPassword() );
    }


    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testCreateConnectionLdapsEncryptionSimpleAuthInvalidCredentials( TestLdapServer server )
    {
        setConnectionParameters( server, EncryptionMethod.LDAPS );

        wizardBot.selectSimpleAuthentication();
        wizardBot.typeUser( "cn=invalid" );
        wizardBot.typePassword( "invalid" );

        String result = wizardBot.clickCheckAuthenticationButton();
        assertThat( result, containsString( "[LDAP result code 49 - invalidCredentials]" ) );

        finishAndAssertConnectionError( "[LDAP result code 49 - invalidCredentials]" );
    }


    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testCreateConnectionLdapsEncryptionSaslDigestMd5Ok( TestLdapServer server )
    {
        setConnectionParameters( server, EncryptionMethod.LDAPS );

        wizardBot.selectDigestMD5Authentication();
        wizardBot.typeUser( "user.1" );
        wizardBot.typePassword( "password" );
        wizardBot.selectQualityOfProtection( SaslQoP.AUTH );
        wizardBot.selectProtectionStrength( SaslSecurityStrength.HIGH );

        finishAndAssertConnection( server, EncryptionMethod.LDAPS, AuthenticationMethod.SASL_DIGEST_MD5,
            "user.1", "password" );
    }


    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testCreateConnectionLdapsEncryptionSaslDigestMd5InvalidCredentials( TestLdapServer server )
    {
        setConnectionParameters( server, EncryptionMethod.LDAPS );

        wizardBot.selectDigestMD5Authentication();
        wizardBot.typeUser( "user.1" );
        wizardBot.typePassword( "invalid" );
        wizardBot.selectQualityOfProtection( SaslQoP.AUTH );
        wizardBot.selectProtectionStrength( SaslSecurityStrength.HIGH );

        String result = wizardBot.clickCheckAuthenticationButton();
        assertThat( result, containsString( "[LDAP result code 49 - invalidCredentials]" ) );

        finishAndAssertConnectionError( "[LDAP result code 49 - invalidCredentials]" );
    }


    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testCreateConnectionLdapsEncryptionSaslDigestMd5InvalidRealm( TestLdapServer server )
    {
        setConnectionParameters( server, EncryptionMethod.LDAPS );

        wizardBot.selectDigestMD5Authentication();
        wizardBot.typeUser( "user.1" );
        wizardBot.typePassword( "password" );
        wizardBot.typeRealm( "APACHE.ORG" );
        wizardBot.selectQualityOfProtection( SaslQoP.AUTH );
        wizardBot.selectProtectionStrength( SaslSecurityStrength.HIGH );

        String result = wizardBot.clickCheckAuthenticationButton();
        assertThat( result, containsString( "[LDAP result code" ) );

        finishAndAssertConnectionError( "[LDAP result code" );
    }


    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testCreateConnectionStartTlsEncryptionNoAuthOK( TestLdapServer server )
    {
        setConnectionParameters( server, EncryptionMethod.START_TLS );

        wizardBot.selectNoAuthentication();

        finishAndAssertConnection( server, EncryptionMethod.START_TLS, AuthenticationMethod.NONE, "", "" );
    }


    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testCreateConnectionStartTlsEncryptionSimpleAuthOK( TestLdapServer server )
    {
        setConnectionParameters( server, EncryptionMethod.START_TLS );

        wizardBot.typeUser( server.getAdminDn() );
        wizardBot.typePassword( server.getAdminPassword() );

        finishAndAssertConnection( server, EncryptionMethod.START_TLS, AuthenticationMethod.SIMPLE,
            server.getAdminDn(), server.getAdminPassword() );
    }


    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testCreateConnectionStartTlsEncryptionSimpleAuthInvalidCredentials( TestLdapServer server )
    {
        setConnectionParameters( server, EncryptionMethod.START_TLS );

        wizardBot.selectSimpleAuthentication();
        wizardBot.typeUser( "cn=invalid" );
        wizardBot.typePassword( "invalid" );

        String result = wizardBot.clickCheckAuthenticationButton();
        assertThat( result, containsString( "[LDAP result code 49 - invalidCredentials]" ) );

        finishAndAssertConnectionError( "[LDAP result code 49 - invalidCredentials]" );
    }


    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testCreateConnectionStartTlsEncryptionSaslDigestMd5OK( TestLdapServer server )
    {
        setConnectionParameters( server, EncryptionMethod.START_TLS );

        wizardBot.selectDigestMD5Authentication();
        wizardBot.typeUser( "user.1" );
        wizardBot.typePassword( "password" );
        wizardBot.selectQualityOfProtection( SaslQoP.AUTH );
        wizardBot.selectProtectionStrength( SaslSecurityStrength.HIGH );

        finishAndAssertConnection( server, EncryptionMethod.START_TLS, AuthenticationMethod.SASL_DIGEST_MD5,
            "user.1", "password" );
    }


    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testCreateConnectionStartTlsEncryptionSaslDigestMd5InvalidCredentials( TestLdapServer server )
    {
        setConnectionParameters( server, EncryptionMethod.START_TLS );

        wizardBot.selectDigestMD5Authentication();
        wizardBot.typeUser( "user.1" );
        wizardBot.typePassword( "invalid" );
        wizardBot.selectQualityOfProtection( SaslQoP.AUTH );
        wizardBot.selectProtectionStrength( SaslSecurityStrength.HIGH );

        String result = wizardBot.clickCheckAuthenticationButton();
        assertThat( result, containsString( "[LDAP result code 49 - invalidCredentials]" ) );

        finishAndAssertConnectionError( "[LDAP result code 49 - invalidCredentials]" );
    }


    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testCheckNetworkParameterButtonNoEncryptionNotOk( TestLdapServer server )
    {
        setConnectionParameters( server, EncryptionMethod.NONE );
        wizardBot.clickBackButton();

        // Invalid port
        wizardBot.typePort( getInvalidPort() );
        String result = wizardBot.clickCheckNetworkParameterButton();
        assertThat( result, containsString( "The connection failed" ) );

        // Invalid host
        String hostname = getInvalidHostName();
        wizardBot.typeHost( hostname );
        result = wizardBot.clickCheckNetworkParameterButton();
        assertThat( result, containsString( "The connection failed" ) );
        assertThat( "Unknown host name must occur in error message", result, containsString( hostname ) );

        wizardBot.clickCancelButton();
    }


    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testCheckNetworkParameterButtonLdapsEncryptionNotOk( TestLdapServer server )
    {
        setConnectionParameters( server, EncryptionMethod.LDAPS );
        wizardBot.clickBackButton();

        // Invalid port
        wizardBot.typePort( getInvalidPort() );
        String result = wizardBot.clickCheckNetworkParameterButton();
        assertThat( result, containsString( "The connection failed" ) );

        // Non ldaps port
        wizardBot.typePort( server.getPort() );
        result = wizardBot.clickCheckNetworkParameterButton();
        assertThat( result, containsString( "The connection failed" ) );

        // Invalid host
        String hostname = getInvalidHostName();
        wizardBot.typeHost( hostname );
        result = wizardBot.clickCheckNetworkParameterButton();
        assertThat( result, containsString( "The connection failed" ) );
        assertThat( "Unknown host name must occur in error message", result, containsString( hostname ) );

        wizardBot.clickCancelButton();
    }


    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testCheckNetworkParameterButtonStartTlsEncryptionNotOk( TestLdapServer server )
    {
        setConnectionParameters( server, EncryptionMethod.START_TLS );
        wizardBot.clickBackButton();

        // Invalid port
        wizardBot.typePort( getInvalidPort() );
        String result = wizardBot.clickCheckNetworkParameterButton();
        assertThat( result, containsString( "The connection failed" ) );

        // Ldaps port
        wizardBot.typePort( server.getPortSSL() );
        result = wizardBot.clickCheckNetworkParameterButton();
        assertThat( result, containsString( "The connection failed" ) );

        // Invalid host
        String hostname = getInvalidHostName();
        wizardBot.typeHost( hostname );
        result = wizardBot.clickCheckNetworkParameterButton();
        assertThat( result, containsString( "The connection failed" ) );
        assertThat( "Unknown host name must occur in error message", result, containsString( hostname ) );

        wizardBot.clickCancelButton();
    }


    private void setConnectionParameters( TestLdapServer server, EncryptionMethod encryptionMethod )
    {
        wizardBot.typeConnectionName( getConnectionName() );
        wizardBot.typeHost( server.getHost() );
        wizardBot.typePort( encryptionMethod == EncryptionMethod.LDAPS ? server.getPortSSL() : server.getPort() );

        if ( encryptionMethod == EncryptionMethod.LDAPS )
        {
            wizardBot.selectLdapsEncryption();
        }
        if ( encryptionMethod == EncryptionMethod.START_TLS )
        {
            wizardBot.selectStartTlsEncryption();
        }

        if ( encryptionMethod != EncryptionMethod.NONE )
        {
            server.setConfidentialityRequired( true );
            CertificateTrustDialogBot trustDialog = wizardBot
                .clickCheckNetworkParameterButtonExpectingCertificateTrustDialog();
            trustDialog.selectTrustPermanent();
            trustDialog.clickOkButton();
            bot.button( "OK" ).click();
        }

        wizardBot.clickNextButton();
    }


    private void finishAndAssertConnection( TestLdapServer server, EncryptionMethod encryptionMethod,
        AuthenticationMethod authenticationMethod, String user, String password )
    {
        wizardBot.clickFinishButton( true );

        connectionsViewBot.waitForConnection( getConnectionName() );

        // ensure connection was created
        ConnectionManager connectionManager = ConnectionCorePlugin.getDefault().getConnectionManager();
        assertNotNull( connectionManager.getConnections() );
        assertEquals( 1, connectionManager.getConnections().length );
        Connection connection = connectionManager.getConnections()[0];
        assertEquals( getConnectionName(), connection.getName() );
        assertEquals( server.getHost(), connection.getHost() );
        assertEquals( encryptionMethod == EncryptionMethod.LDAPS ? server.getPortSSL() : server.getPort(),
            connection.getPort() );
        assertEquals( user, connection.getBindPrincipal() );
        assertEquals( password, connection.getBindPassword() );
        assertEquals( authenticationMethod, connection.getAuthMethod() );
        assertTrue( connection.getConnectionWrapper().isConnected() );
        if ( encryptionMethod == EncryptionMethod.NONE )
        {
            assertFalse( connection.getConnectionWrapper().isSecured() );
        }
        else
        {
            assertTrue( connection.getConnectionWrapper().isSecured() );
        }

        // ensure connection is visible in Connections view
        assertEquals( 1, connectionsViewBot.getCount() );

        // close connection
        connectionsViewBot.closeSelectedConnections();
    }


    private void finishAndAssertConnectionError( String errorText )
    {
        ErrorDialogBot errorBot = wizardBot.clickFinishButtonExpectingError();

        String errorMessage = errorBot.getErrorMessage();
        String errorDetails = errorBot.getErrorDetails();
        assertNotNull( errorMessage );
        assertNotNull( errorDetails );
        assertThat( errorDetails, containsString( errorText ) );
        errorBot.clickOkButton();

        ConnectionManager connectionManager = ConnectionCorePlugin.getDefault().getConnectionManager();
        assertNotNull( connectionManager.getConnections() );
        assertEquals( 1, connectionManager.getConnections().length );
        Connection connection = connectionManager.getConnections()[0];
        assertEquals( getConnectionName(), connection.getName() );
        assertFalse( connection.getConnectionWrapper().isConnected() );
        assertFalse( connection.getConnectionWrapper().isSecured() );

        // ensure connection is visible in Connections view
        assertEquals( 1, connectionsViewBot.getCount() );
    }


    private int getInvalidPort()
    {
        return AvailablePortFinder.getNextAvailable( 1025 );
    }


    private static String getInvalidHostName()
    {
        return "qwertzuiop.asdfghjkl.yxcvbnm";
    }

}
