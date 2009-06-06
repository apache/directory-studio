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


import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;


public class NewConnectionWizardBot extends WizardBot
{

    private static final String CERTIFICATE_TRUST = "Certificate Trust";
    private static final String CONNECTION_NAME = "Connection name:";
    private static final String HOSTNAME = "Hostname:";
    private static final String PORT = "Port:";
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
    private static final String NO_AUTHENTICATION = "No Authentication";
    private static final String SIMPLE_AUTHENTICATION = "Simple Authentication";
    private static final String AUTHENTICATION_METHOD = "Authentication Method";
    private static final String ENCRYPTION_METHOD = "Encryption method:";
    private static final String NO_ENCRYPTION = "No Encryption";
    private static final String START_TLS_ENCRYPTION = "Use StartTLS extension";
    private static final String LDAPS_ENCRYPTION = "Use SSL encryption (ldaps://)";


    public boolean isVisible()
    {
        return isVisible( "New LDAP Connection" );
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
        return clickCheckButton( CHECK_NETWORK_PARAMETER );
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
        return clickCheckButton( CHECK_AUTHENTICATION );
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


    private String clickCheckButton( final String label )
    {
        SWTBotShell shell = BotUtils.shell( new Runnable()
        {
            public void run()
            {
                bot.button( label ).click();
            }
        }, "Error", label );

        String shellText = shell.getText();
        String labelText = bot.label( 1 ).getText(); // label(0) is the image
        bot.button( "OK" ).click();

        if ( shellText.equals( label ) )
        {
            return null;
        }
        else
        {
            return labelText;
        }
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
