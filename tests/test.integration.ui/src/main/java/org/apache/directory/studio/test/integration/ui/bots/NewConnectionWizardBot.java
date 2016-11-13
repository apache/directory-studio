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
package org.apache.directory.studio.test.integration.ui.bots;


import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.test.integration.ui.bots.utils.JobWatcher;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;


public class NewConnectionWizardBot extends WizardBot
{

    private static final String TITLE = "New LDAP Connection";
    private static final String CERTIFICATE_TRUST = "Certificate Trust";
    private static final String CONNECTION_NAME = "Connection name:";
    private static final String HOSTNAME = "Hostname:";
    private static final String PORT = "Port:";
    private static final String PROVIDER = "Provider:";
    private static final String JNDI = "JNDI (Java Naming and Directory Interface)";
    private static final String LDAP_API = "Apache Directory LDAP Client API";
    private static final String CHECK_AUTHENTICATION = "Check Authentication";
    private static final String CHECK_NETWORK_PARAMETER = "Check Network Parameter";
    private static final String BASE_DN = "Base DN:";
    private static final String GET_BASE_DNS_FROM_ROOT_DSE = "Get base DNs from Root DSE";
    private static final String SAVE_PASSWORD = "Save password";
    private static final String SASL_REALM = "SASL Realm:";
    private static final String BIND_PASSWORD = "Bind password:";
    private static final String BIND_DN_OR_USER = "Bind DN or user:";
    private static final String CRAM_MD5_SASL = "CRAM-MD5 (SASL)";
    private static final String DIGEST_MD5_SASL = "DIGEST-MD5 (SASL)";
    private static final String GSS_API_SASL = "GSSAPI (Kerberos)";
    private static final String NO_AUTHENTICATION = "No Authentication";
    private static final String SIMPLE_AUTHENTICATION = "Simple Authentication";
    private static final String AUTHENTICATION_METHOD = "Authentication Method";
    private static final String ENCRYPTION_METHOD = "Encryption method:";
    private static final String NO_ENCRYPTION = "No Encryption";
    private static final String START_TLS_ENCRYPTION = "Use StartTLS extension";
    private static final String LDAPS_ENCRYPTION = "Use SSL encryption (ldaps://)";
    private static final String USE_NATIVE_TGT = "Use native TGT";
    private static final String OBTAIN_TGT_FROM_KDC = "Obtain TGT from KDC (provide username and password)";
    private static final String USE_NATIVE_SYSTEM_CONFIG = "Use native system configuration";
    private static final String USE_CONFIG_FILE = "Use configuration file:";
    private static final String USE_MANUAL_CONFIG = "Use following configuration:";
    private static final String KERBEROS_REALM = "Kerberos Realm:";
    private static final String KDC_HOST = "KDC Host:";
    private static final String KDC_PORT = "KDC Port:";


    public NewConnectionWizardBot()
    {
        super( TITLE );
    }


    public void clickFinishButton( boolean waitTillConnectionOpened )
    {
        JobWatcher watcher = null;
        if ( waitTillConnectionOpened )
        {
            watcher = new JobWatcher( BrowserCoreMessages.jobs__open_connections_name_1 );
        }
        super.clickFinishButton();
        if ( waitTillConnectionOpened )
        {
            watcher.waitUntilDone();
        }
    }


    public void typeConnectionName( String connectionName )
    {
        SWTBotText connText = bot.textWithLabel( CONNECTION_NAME );
        connText.setText( connectionName );
    }


    public void typeHost( String host )
    {
        SWTBotCombo hostnameCombo = bot.comboBoxWithLabel( HOSTNAME );
        hostnameCombo.setText( host );
    }


    public void typePort( int port )
    {
        SWTBotCombo portCombo = bot.comboBoxWithLabel( PORT );
        portCombo.setText( Integer.toString( port ) );
    }


    public void selectJndiProvider()
    {
        SWTBotCombo providerCombo = bot.comboBoxWithLabel( PROVIDER );
        providerCombo.setSelection( JNDI );
    }


    public void selectLdapApiProvider()
    {
        SWTBotCombo providerCombo = bot.comboBoxWithLabel( PROVIDER );
        providerCombo.setSelection( LDAP_API );
    }


    public boolean isSimpleAuthenticationSelected()
    {
        SWTBotCombo authMethodCombo = bot.comboBoxInGroup( AUTHENTICATION_METHOD );
        return SIMPLE_AUTHENTICATION.equals( authMethodCombo.selection() );
    }


    public void selectSimpleAuthentication()
    {
        SWTBotCombo authMethodCombo = bot.comboBoxInGroup( AUTHENTICATION_METHOD );
        authMethodCombo.setSelection( SIMPLE_AUTHENTICATION );
    }


    public boolean isNoAuthenticationSelected()
    {
        SWTBotCombo authMethodCombo = bot.comboBoxInGroup( AUTHENTICATION_METHOD );
        return NO_AUTHENTICATION.equals( authMethodCombo.selection() );
    }


    public void selectNoAuthentication()
    {
        SWTBotCombo authMethodCombo = bot.comboBoxInGroup( AUTHENTICATION_METHOD );
        authMethodCombo.setSelection( NO_AUTHENTICATION );
    }


    public boolean isDigestMD5AuthenticationSelected()
    {
        SWTBotCombo authMethodCombo = bot.comboBoxInGroup( AUTHENTICATION_METHOD );
        return DIGEST_MD5_SASL.equals( authMethodCombo.selection() );
    }


    public void selectDigestMD5Authentication()
    {
        SWTBotCombo authMethodCombo = bot.comboBoxInGroup( AUTHENTICATION_METHOD );
        authMethodCombo.setSelection( DIGEST_MD5_SASL );
    }


    public boolean isCramMD5AuthenticationSelected()
    {
        SWTBotCombo authMethodCombo = bot.comboBoxInGroup( AUTHENTICATION_METHOD );
        return CRAM_MD5_SASL.equals( authMethodCombo.selection() );
    }


    public void selectCramMD5Authentication()
    {
        SWTBotCombo authMethodCombo = bot.comboBoxInGroup( AUTHENTICATION_METHOD );
        authMethodCombo.setSelection( CRAM_MD5_SASL );
    }


    public boolean isGssApiAuthenticationSelected()
    {
        SWTBotCombo authMethodCombo = bot.comboBoxInGroup( AUTHENTICATION_METHOD );
        return GSS_API_SASL.equals( authMethodCombo.selection() );
    }


    public void selectGssApiAuthentication()
    {
        SWTBotCombo authMethodCombo = bot.comboBoxInGroup( AUTHENTICATION_METHOD );
        authMethodCombo.setSelection( GSS_API_SASL );
    }


    public boolean isUserEnabled()
    {
        return bot.comboBoxWithLabel( BIND_DN_OR_USER ).isEnabled();
    }


    public void typeUser( String user )
    {
        SWTBotCombo dnCombo = bot.comboBoxWithLabel( BIND_DN_OR_USER );
        dnCombo.setText( user );
    }


    public boolean isPasswordEnabled()
    {
        return bot.textWithLabel( BIND_PASSWORD ).isEnabled();
    }


    public void typePassword( String password )
    {
        SWTBotText passwordText = bot.textWithLabel( BIND_PASSWORD );
        passwordText.setText( password );
    }


    public boolean isRealmEnabled()
    {
        return bot.comboBoxWithLabel( SASL_REALM ).isEnabled();
    }


    public void typeRealm( String realm )
    {
        SWTBotCombo dnCombo = bot.comboBoxWithLabel( SASL_REALM );
        dnCombo.setText( realm );
    }


    public boolean isSavePasswordEnabled()
    {
        return bot.checkBox( SAVE_PASSWORD ).isEnabled();
    }


    public boolean isSavePasswordSelected()
    {
        return bot.checkBox( SAVE_PASSWORD ).isChecked();
    }


    public void selectSavePassword()
    {
        bot.checkBox( SAVE_PASSWORD ).select();
    }


    public void deselectSavePassword()
    {
        bot.checkBox( SAVE_PASSWORD ).deselect();
    }


    public boolean isUseNativeTgtSelected()
    {
        return bot.radio( USE_NATIVE_TGT ).isSelected();
    }


    public void selectUserNativeTgt()
    {
        bot.radio( USE_NATIVE_TGT ).click();
    }


    public boolean isObtainTgtFromKdcSelected()
    {
        return bot.radio( OBTAIN_TGT_FROM_KDC ).isSelected();
    }


    public void selectObtainTgtFromKdc()
    {
        bot.radio( OBTAIN_TGT_FROM_KDC ).click();
    }


    public boolean isUseNativeSystemConfigurationSelected()
    {
        return bot.radio( USE_NATIVE_SYSTEM_CONFIG ).isSelected();
    }


    public void selectUseNativeSystemConfiguration()
    {
        bot.radio( USE_NATIVE_SYSTEM_CONFIG ).click();
    }


    public boolean isUseConfigurationFileSelected()
    {
        return bot.radio( USE_CONFIG_FILE ).isSelected();
    }


    public void selectUseConfigurationFile()
    {
        bot.radio( USE_CONFIG_FILE ).click();
    }


    public boolean isUseManualConfigurationSelected()
    {
        return bot.radio( USE_MANUAL_CONFIG ).isSelected();
    }


    public void selectUseManualConfiguration()
    {
        bot.radio( USE_MANUAL_CONFIG ).click();
    }


    public boolean isKerberosRealmEnabled()
    {
        return bot.textWithLabel( KERBEROS_REALM ).isEnabled();
    }


    public void typeKerberosRealm( String realm )
    {
        bot.textWithLabel( KERBEROS_REALM ).setText( realm );
    }


    public boolean isKdcHostEnabled()
    {
        return bot.textWithLabel( KDC_HOST ).isEnabled();
    }


    public void typeKdcHost( String host )
    {
        bot.textWithLabel( KDC_HOST ).setText( host );
    }


    public boolean isKdcPortEnabled()
    {
        return bot.textWithLabel( KDC_PORT ).isEnabled();
    }


    public void typeKdcPort( int port )
    {
        bot.textWithLabel( KDC_PORT ).setText( Integer.toString( port ) );
    }


    public boolean isGetBaseDnsFromRootDseEnabled()
    {
        return bot.checkBox( GET_BASE_DNS_FROM_ROOT_DSE ).isEnabled();
    }


    public boolean isGetBaseDnsFromRootDseSelected()
    {
        return bot.checkBox( GET_BASE_DNS_FROM_ROOT_DSE ).isChecked();
    }


    public void selectGetBaseDnsFromRootDse()
    {
        bot.checkBox( GET_BASE_DNS_FROM_ROOT_DSE ).select();
    }


    public void deselectGetBaseDnsFromRootDse()
    {
        bot.checkBox( GET_BASE_DNS_FROM_ROOT_DSE ).deselect();
    }


    public boolean isBaseDnEnabled()
    {
        return bot.comboBoxWithLabel( BASE_DN ).isEnabled();
    }


    public void typeBaseDn( String baseDn )
    {
        SWTBotCombo dnCombo = bot.comboBoxWithLabel( BASE_DN );
        dnCombo.setText( baseDn );
    }


    /**
     * Clicks the "check network parameter" button.
     * 
     * @return null if the OK dialog pops up, the error message if the error dialog pops up
     */
    public String clickCheckNetworkParameterButton()
    {
        return clickCheckButton( CHECK_NETWORK_PARAMETER, CHECK_NETWORK_PARAMETER );
    }


    /**
     * Clicks the "check network parameter" button.
     */
    public CertificateTrustDialogBot clickCheckNetworkParameterButtonExpectingCertificateTrustDialog()
    {
        bot.button( CHECK_NETWORK_PARAMETER ).click();
        bot.shell( CERTIFICATE_TRUST );
        return new CertificateTrustDialogBot();
    }


    /**
     * Clicks the "check authentication" button.
     * 
     * @return null if the OK dialog pops up, the error message if the error dialog pops up
     */
    public String clickCheckAuthenticationButton()
    {
        return clickCheckButton( CHECK_AUTHENTICATION, CHECK_AUTHENTICATION );
    }


    /**
     * Clicks the "check authentication" button.
     */
    public CertificateTrustDialogBot clickCheckAuthenticationButtonExpectingCertificateTrustDialog()
    {
        bot.button( CHECK_AUTHENTICATION ).click();
        bot.shell( CERTIFICATE_TRUST );
        return new CertificateTrustDialogBot();
    }


    public boolean isNoEncryptionSelected()
    {
        SWTBotCombo encMethodCombo = bot.comboBoxWithLabel( ENCRYPTION_METHOD );
        return NO_ENCRYPTION.equals( encMethodCombo.selection() );
    }


    public void selectNoEncryption()
    {
        SWTBotCombo encMethodCombo = bot.comboBoxWithLabel( ENCRYPTION_METHOD );
        encMethodCombo.setSelection( NO_ENCRYPTION );
    }


    public boolean isStartTlsEncryptionSelected()
    {
        SWTBotCombo encMethodCombo = bot.comboBoxWithLabel( ENCRYPTION_METHOD );
        return START_TLS_ENCRYPTION.equals( encMethodCombo.selection() );
    }


    public void selectStartTlsEncryption()
    {
        SWTBotCombo encMethodCombo = bot.comboBoxWithLabel( ENCRYPTION_METHOD );
        encMethodCombo.setSelection( START_TLS_ENCRYPTION );
    }


    public boolean isLdapsEncryptionSelected()
    {
        SWTBotCombo encMethodCombo = bot.comboBoxWithLabel( ENCRYPTION_METHOD );
        return LDAPS_ENCRYPTION.equals( encMethodCombo.selection() );
    }


    public void selectLdapsEncryption()
    {
        SWTBotCombo encMethodCombo = bot.comboBoxWithLabel( ENCRYPTION_METHOD );
        encMethodCombo.setSelection( LDAPS_ENCRYPTION );
    }

}
