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

package org.apache.directory.studio.connection.ui.widgets;


import org.apache.commons.lang.StringUtils;
import org.apache.directory.shared.ldap.model.constants.SaslQoP;
import org.apache.directory.shared.ldap.model.constants.SaslSecurityStrength;
import org.apache.directory.shared.ldap.model.url.LdapUrl;
import org.apache.directory.shared.ldap.model.url.LdapUrl.Extension;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCoreConstants;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.apache.directory.studio.connection.core.ConnectionParameter.AuthenticationMethod;
import org.apache.directory.studio.connection.core.ConnectionParameter.Krb5Configuration;
import org.apache.directory.studio.connection.core.ConnectionParameter.Krb5CredentialConfiguration;
import org.apache.directory.studio.connection.core.jobs.CheckBindRunnable;
import org.apache.directory.studio.connection.ui.AbstractConnectionParameterPage;
import org.apache.directory.studio.connection.ui.ConnectionUIConstants;
import org.apache.directory.studio.connection.ui.RunnableContextRunner;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;


/**
 * The AuthenticationParameterPage is used the edit the authentication parameters of a
 * connection.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AuthenticationParameterPage extends AbstractConnectionParameterPage
{

    private static final String X_AUTH_METHOD = "X-AUTH-METHOD"; //$NON-NLS-1$

    private static final String X_AUTH_METHOD_ANONYMOUS = "Anonymous"; //$NON-NLS-1$

    private static final String X_AUTH_METHOD_SIMPLE = "Simple"; //$NON-NLS-1$

    private static final String X_AUTH_METHOD_DIGEST_MD5 = "DIGEST-MD5"; //$NON-NLS-1$

    private static final String X_AUTH_METHOD_CRAM_MD5 = "CRAM-MD5"; //$NON-NLS-1$

    private static final String X_AUTH_METHOD_GSSAPI = "GSSAPI"; //$NON-NLS-1$

    private static final String X_BIND_USER = "X-BIND-USER"; //$NON-NLS-1$

    private static final String X_BIND_PASSWORD = "X-BIND-PASSWORD"; //$NON-NLS-1$

    private static final String X_SASL_REALM = "X-SASL-REALM"; //$NON-NLS-1$
    private static final String X_SASL_QOP = "X-SASL-QOP"; //$NON-NLS-1$
    private static final String X_SASL_QOP_AUTH_INT = "AUTH-INT"; //$NON-NLS-1$
    private static final String X_SASL_QOP_AUTH_INT_PRIV = "AUTH-INT-PRIV"; //$NON-NLS-1$
    private static final String X_SASL_SEC_STRENGTH = "X-SASL-SEC-STRENGTH"; //$NON-NLS-1$
    private static final String X_SASL_SEC_STRENGTH_MEDIUM = "MEDIUM"; //$NON-NLS-1$
    private static final String X_SASL_SEC_STRENGTH_LOW = "LOW"; //$NON-NLS-1$
    private static final String X_SASL_NO_MUTUAL_AUTH = "X-SASL-NO-MUTUAL-AUTH"; //$NON-NLS-1$

    private static final String X_KRB5_CREDENTIALS_CONF = "X-KRB5-CREDENTIALS-CONF"; //$NON-NLS-1$
    private static final String X_KRB5_CREDENTIALS_CONF_OBTAIN_TGT = "OBTAIN-TGT"; //$NON-NLS-1$
    private static final String X_KRB5_CONFIG = "X-KRB5-CONFIG"; //$NON-NLS-1$
    private static final String X_KRB5_CONFIG_FILE = "FILE"; //$NON-NLS-1$
    private static final String X_KRB5_CONFIG_FILE_FILE = "X-KRB5-CONFIG-FILE"; //$NON-NLS-1$
    private static final String X_KRB5_CONFIG_MANUAL = "MANUAL"; //$NON-NLS-1$
    private static final String X_KRB5_CONFIG_MANUAL_REALM = "X-KRB5-REALM"; //$NON-NLS-1$
    private static final String X_KRB5_CONFIG_MANUAL_KDC_HOST = "X-KRB5-KDC-HOST"; //$NON-NLS-1$
    private static final String X_KRB5_CONFIG_MANUAL_KDC_PORT = "X-KRB5-KDC-PORT"; //$NON-NLS-1$

    /** The combo to select the authentication method */
    private Combo authenticationMethodCombo;

    /** The bind user combo with the history of recently used bind users */
    private Combo bindPrincipalCombo;

    /** The text widget to input bind password */
    private Text bindPasswordText;

    /** The checkbox to choose if the bind password should be saved on disk */
    private Button saveBindPasswordButton;

    /** The button to check the authentication parameters */
    private Button checkPrincipalPasswordAuthButton;

    // SASL stuff
    private ExpandableComposite saslExpandableComposite;
    private Composite saslComposite;
    private Combo saslRealmText;
    private Combo saslQopCombo;
    private Combo saslSecurityStrengthCombo;
    private Button saslMutualAuthenticationButton;

    // Kerberos stuff
    private ExpandableComposite krb5ExpandableComposite;
    private Composite krb5Composite;
    private Button krb5CredentialConfigurationUseNativeButton;
    private Button krb5CredentialConfigurationObtainTgtButton;
    private Button krb5ConfigDefaultButton;
    private Button krb5ConfigFileButton;
    private Text krb5ConfigFileText;
    private Button krb5ConfigManualButton;
    private Text krb5ConfigManualRealmText;
    private Text krb5ConfigManualHostText;
    private Text krb5ConfigManualPortText;


    /**
     * Creates a new instance of AuthenticationParameterPage.
     */
    public AuthenticationParameterPage()
    {
    }


    /**
     * Gets the authentication method.
     * 
     * @return the authentication method
     */
    private ConnectionParameter.AuthenticationMethod getAuthenticationMethod()
    {
        switch ( authenticationMethodCombo.getSelectionIndex() )
        {
            case 1:
                return ConnectionParameter.AuthenticationMethod.SIMPLE;
            case 2:
                return ConnectionParameter.AuthenticationMethod.SASL_DIGEST_MD5;
            case 3:
                return ConnectionParameter.AuthenticationMethod.SASL_CRAM_MD5;
            case 4:
                return ConnectionParameter.AuthenticationMethod.SASL_GSSAPI;
            default:
                return ConnectionParameter.AuthenticationMethod.NONE;
        }
    }


    /**
     * Gets the bind principal.
     * 
     * @return the bind principal
     */
    private String getBindPrincipal()
    {
        return bindPrincipalCombo.getText();
    }


    /**
     * Gets the bind password.
     * 
     * @return the bind password, null if saving of bind password is disabled
     */
    private String getBindPassword()
    {
        return isSaveBindPassword() ? bindPasswordText.getText() : null;
    }


    private String getSaslRealm()
    {
        return saslRealmText.getText();
    }


    private SaslQoP getSaslQop()
    {
        switch ( saslQopCombo.getSelectionIndex() )
        {
            case 1:
                return SaslQoP.AUTH_INT;
            case 2:
                return SaslQoP.AUTH_CONF;
            default:
                return SaslQoP.AUTH;
        }
    }


    private SaslSecurityStrength getSaslSecurityStrength()
    {
        switch ( saslSecurityStrengthCombo.getSelectionIndex() )
        {
            case 1:
                return SaslSecurityStrength.MEDIUM;
            case 2:
                return SaslSecurityStrength.LOW;
            default:
                return SaslSecurityStrength.HIGH;
        }
    }


    private Krb5CredentialConfiguration getKrb5CredentialProvider()
    {
        if ( krb5CredentialConfigurationUseNativeButton.getSelection() )
        {
            return Krb5CredentialConfiguration.USE_NATIVE;
        }
        else
        {
            return Krb5CredentialConfiguration.OBTAIN_TGT;
        }
    }


    private Krb5Configuration getKrb5Configuration()
    {
        if ( krb5ConfigDefaultButton.getSelection() )
        {
            return Krb5Configuration.DEFAULT;
        }
        else if ( krb5ConfigFileButton.getSelection() )
        {
            return Krb5Configuration.FILE;
        }
        else
        {
            return Krb5Configuration.MANUAL;
        }
    }


    private int getKdcPort()
    {
        return !"".equals( krb5ConfigManualPortText.getText() ) ? Integer.parseInt( krb5ConfigManualPortText.getText() )
            : 0;
    }


    //    {
    //        krb5
    //    }

    /**
     * Returns true if the bind password should be saved on disk.
     * 
     * @return true, if the bind password should be saved on disk
     */
    public boolean isSaveBindPassword()
    {
        return saveBindPasswordButton.getSelection();
    }


    /**
     * Gets a temporary connection with all conection parameter 
     * entered in this page. 
     *
     * @return a test connection
     */
    private Connection getTestConnection()
    {
        ConnectionParameter cp = connectionParameterPageModifyListener.getTestConnectionParameters();
        Connection conn = new Connection( cp );
        return conn;
    }


    /**
     * @see org.apache.directory.studio.connection.ui.AbstractConnectionParameterPage#createComposite(org.eclipse.swt.widgets.Composite)
     */
    protected void createComposite( Composite parent )
    {
        // Authentication Method
        Composite composite1 = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        Group group1 = BaseWidgetUtils.createGroup( composite1, Messages
            .getString( "AuthenticationParameterPage.AuthenticationMethod" ), 1 ); //$NON-NLS-1$
        Composite groupComposite = BaseWidgetUtils.createColumnContainer( group1, 1, 1 );

        String[] authMethods = new String[]
            { Messages.getString( "AuthenticationParameterPage.AnonymousAuthentication" ), //$NON-NLS-1$
                Messages.getString( "AuthenticationParameterPage.SimpleAuthentication" ), //$NON-NLS-1$
                Messages.getString( "AuthenticationParameterPage.DigestMD5" ), //$NON-NLS-1$
                Messages.getString( "AuthenticationParameterPage.CramMD5" ), //$NON-NLS-1$
                Messages.getString( "AuthenticationParameterPage.GSSAPI" ) }; //$NON-NLS-1$
        authenticationMethodCombo = BaseWidgetUtils.createReadonlyCombo( groupComposite, authMethods, 1, 2 );

        // Authentication Parameter
        Composite composite2 = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        Group group2 = BaseWidgetUtils.createGroup( composite2, Messages
            .getString( "AuthenticationParameterPage.AuthenticationParameter" ), 1 ); //$NON-NLS-1$
        Composite composite = BaseWidgetUtils.createColumnContainer( group2, 3, 1 );

        BaseWidgetUtils.createLabel( composite, Messages.getString( "AuthenticationParameterPage.BindDNOrUser" ), 1 ); //$NON-NLS-1$
        String[] dnHistory = HistoryUtils.load( ConnectionUIConstants.DIALOGSETTING_KEY_PRINCIPAL_HISTORY );
        bindPrincipalCombo = BaseWidgetUtils.createCombo( composite, dnHistory, -1, 2 );

        BaseWidgetUtils.createLabel( composite, Messages.getString( "AuthenticationParameterPage.BindPassword" ), 1 ); //$NON-NLS-1$
        bindPasswordText = BaseWidgetUtils.createPasswordText( composite, "", 2 ); //$NON-NLS-1$

        BaseWidgetUtils.createSpacer( composite, 1 );
        saveBindPasswordButton = BaseWidgetUtils.createCheckbox( composite, Messages
            .getString( "AuthenticationParameterPage.SavePassword" ), 1 ); //$NON-NLS-1$
        saveBindPasswordButton.setSelection( true );

        checkPrincipalPasswordAuthButton = new Button( composite, SWT.PUSH );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalAlignment = SWT.RIGHT;
        checkPrincipalPasswordAuthButton.setLayoutData( gd );
        checkPrincipalPasswordAuthButton.setText( Messages
            .getString( "AuthenticationParameterPage.CheckAuthentication" ) ); //$NON-NLS-1$
        checkPrincipalPasswordAuthButton.setEnabled( false );

        ScrolledComposite sc = new ScrolledComposite( parent, SWT.H_SCROLL | SWT.V_SCROLL );
        sc.setLayout( new GridLayout() );
        sc.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        Composite c = BaseWidgetUtils.createColumnContainer( sc, 1, 1 );
        sc.setContent( c );

        saslExpandableComposite = createExpandableSection( c, Messages
            .getString( "AuthenticationParameterPage.SaslOptions" ), 1 ); //$NON-NLS-1$
        saslComposite = BaseWidgetUtils.createColumnContainer( saslExpandableComposite, 2, 1 );
        saslExpandableComposite.setClient( saslComposite );
        createSaslControls();

        krb5ExpandableComposite = createExpandableSection( c, Messages
            .getString( "AuthenticationParameterPage.Krb5Options" ), 1 ); //$NON-NLS-1$
        krb5Composite = BaseWidgetUtils.createColumnContainer( krb5ExpandableComposite, 1, 1 );
        krb5ExpandableComposite.setClient( krb5Composite );
        createKrb5Controls();

        c.setSize( c.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    }


    protected ExpandableComposite createExpandableSection( Composite parent, String label, int nColumns )
    {
        ExpandableComposite excomposite = new ExpandableComposite( parent, SWT.NONE, ExpandableComposite.TWISTIE
            | ExpandableComposite.CLIENT_INDENT );
        excomposite.setText( label );
        excomposite.setExpanded( false );
        excomposite.setFont( JFaceResources.getFontRegistry().getBold( JFaceResources.DIALOG_FONT ) );
        excomposite.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, false, nColumns, 1 ) );
        excomposite.addExpansionListener( new ExpansionAdapter()
        {
            public void expansionStateChanged( ExpansionEvent e )
            {
                ExpandableComposite excomposite = ( ExpandableComposite ) e.getSource();
                excomposite.getParent().setSize( excomposite.getParent().computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
            }
        } );
        return excomposite;
    }


    private void createSaslControls()
    {
        BaseWidgetUtils.createLabel( saslComposite, Messages.getString( "AuthenticationParameterPage.SaslRealm" ), 1 ); //$NON-NLS-1$
        String[] saslHistory = HistoryUtils.load( ConnectionUIConstants.DIALOGSETTING_KEY_REALM_HISTORY );
        saslRealmText = BaseWidgetUtils.createCombo( saslComposite, saslHistory, -1, 1 );

        BaseWidgetUtils.createLabel( saslComposite, Messages.getString( "AuthenticationParameterPage.SaslQop" ), 1 ); //$NON-NLS-1$
        String[] qops = new String[]
            { Messages.getString( "AuthenticationParameterPage.SaslQopAuth" ), //$NON-NLS-1$
                Messages.getString( "AuthenticationParameterPage.SaslQopAuthInt" ), //$NON-NLS-1$
                Messages.getString( "AuthenticationParameterPage.SaslQopAuthIntPriv" ) }; //$NON-NLS-1$
        saslQopCombo = BaseWidgetUtils.createReadonlyCombo( saslComposite, qops, 0, 1 );

        BaseWidgetUtils.createLabel( saslComposite, Messages
            .getString( "AuthenticationParameterPage.SaslSecurityStrength" ), 1 ); //$NON-NLS-1$
        String[] securityStrengths = new String[]
            { Messages.getString( "AuthenticationParameterPage.SaslSecurityStrengthHigh" ), //$NON-NLS-1$
                Messages.getString( "AuthenticationParameterPage.SaslSecurityStrengthMedium" ), //$NON-NLS-1$
                Messages.getString( "AuthenticationParameterPage.SaslSecurityStrengthLow" ) }; //$NON-NLS-1$
        saslSecurityStrengthCombo = BaseWidgetUtils.createReadonlyCombo( saslComposite, securityStrengths, 0, 1 );

        saslMutualAuthenticationButton = BaseWidgetUtils.createCheckbox( saslComposite, Messages
            .getString( "AuthenticationParameterPage.SaslMutualAuthentication" ), 2 ); //$NON-NLS-1$
    }


    private void createKrb5Controls()
    {
        Group credentialProviderGroup = BaseWidgetUtils.createGroup( krb5Composite, Messages
            .getString( "AuthenticationParameterPage.Krb5CredentialConf" ), 1 ); //$NON-NLS-1$
        Composite credentialProviderComposite = BaseWidgetUtils.createColumnContainer( credentialProviderGroup, 1, 1 );
        krb5CredentialConfigurationUseNativeButton = BaseWidgetUtils.createRadiobutton( credentialProviderComposite,
            Messages.getString( "AuthenticationParameterPage.Krb5CredentialConfUseNative" ), 1 ); //$NON-NLS-1$
        krb5CredentialConfigurationUseNativeButton.setToolTipText( Messages
            .getString( "AuthenticationParameterPage.Krb5CredentialConfUseNativeTooltip" ) ); //$NON-NLS-1$
        krb5CredentialConfigurationUseNativeButton.setSelection( true );
        krb5CredentialConfigurationObtainTgtButton = BaseWidgetUtils.createRadiobutton( credentialProviderComposite,
            Messages.getString( "AuthenticationParameterPage.Krb5CredentialConfObtainTgt" ), 1 ); //$NON-NLS-1$
        krb5CredentialConfigurationObtainTgtButton.setToolTipText( Messages
            .getString( "AuthenticationParameterPage.Krb5CredentialConfObtainTgtTooltip" ) ); //$NON-NLS-1$

        Group configGroup = BaseWidgetUtils.createGroup( krb5Composite, Messages
            .getString( "AuthenticationParameterPage.Krb5Config" ), 1 ); //$NON-NLS-1$
        Composite configComposite = BaseWidgetUtils.createColumnContainer( configGroup, 3, 1 );
        krb5ConfigDefaultButton = BaseWidgetUtils.createRadiobutton( configComposite, Messages
            .getString( "AuthenticationParameterPage.Krb5ConfigDefault" ), 3 ); //$NON-NLS-1$
        krb5ConfigDefaultButton.setSelection( true );
        krb5ConfigFileButton = BaseWidgetUtils.createRadiobutton( configComposite, Messages
            .getString( "AuthenticationParameterPage.Krb5ConfigFile" ), 1 ); //$NON-NLS-1$
        krb5ConfigFileText = BaseWidgetUtils.createText( configComposite, "", 2 ); //$NON-NLS-1$
        krb5ConfigManualButton = BaseWidgetUtils.createRadiobutton( configComposite, Messages
            .getString( "AuthenticationParameterPage.Krb5ConfigManual" ), 1 ); //$NON-NLS-1$
        BaseWidgetUtils.createLabel( configComposite, Messages.getString( "AuthenticationParameterPage.Krb5Realm" ), 1 ); //$NON-NLS-1$
        krb5ConfigManualRealmText = BaseWidgetUtils.createText( configComposite, "", 1 ); //$NON-NLS-1$
        BaseWidgetUtils.createSpacer( configComposite, 1 );
        BaseWidgetUtils.createLabel( configComposite,
            Messages.getString( "AuthenticationParameterPage.Krb5KdcHost" ), 1 ); //$NON-NLS-1$
        krb5ConfigManualHostText = BaseWidgetUtils.createText( configComposite, "", 1 ); //$NON-NLS-1$
        BaseWidgetUtils.createSpacer( configComposite, 1 );
        BaseWidgetUtils.createLabel( configComposite,
            Messages.getString( "AuthenticationParameterPage.Krb5KdcPort" ), 1 ); //$NON-NLS-1$
        krb5ConfigManualPortText = BaseWidgetUtils.createText( configComposite, "88", 1 ); //$NON-NLS-1$
        krb5ConfigManualPortText.setTextLimit( 5 );
    }


    /**
     * @see org.apache.directory.studio.connection.ui.AbstractConnectionParameterPage#validate()
     */
    protected void validate()
    {
        // set enabled/disabled state of fields and buttons
        if ( saslComposite != null )
        {
            for ( Control c : saslComposite.getChildren() )
            {
                c.setEnabled( isSaslEnabled() );
            }
            saslRealmText.setEnabled( isSaslRealmTextEnabled() );
        }

        // TODO: get setting from global preferences.
        Preferences preferences = ConnectionCorePlugin.getDefault().getPluginPreferences();
        boolean useKrb5SystemProperties = preferences
            .getBoolean( ConnectionCoreConstants.PREFERENCE_USE_KRB5_SYSTEM_PROPERTIES );
        if ( krb5Composite != null )
        {
            krb5CredentialConfigurationUseNativeButton.setEnabled( isGssapiEnabled() && !useKrb5SystemProperties );
            krb5CredentialConfigurationObtainTgtButton.setEnabled( isGssapiEnabled() && !useKrb5SystemProperties );

            krb5ConfigDefaultButton.setEnabled( isGssapiEnabled() && !useKrb5SystemProperties );
            krb5ConfigFileButton.setEnabled( isGssapiEnabled() && !useKrb5SystemProperties );
            krb5ConfigManualButton.setEnabled( isGssapiEnabled() && !useKrb5SystemProperties );

            krb5ConfigFileText.setEnabled( isGssapiEnabled() && krb5ConfigFileButton.getSelection()
                && !useKrb5SystemProperties );
            krb5ConfigManualRealmText.setEnabled( isGssapiEnabled() && krb5ConfigManualButton.getSelection()
                && !useKrb5SystemProperties );
            krb5ConfigManualHostText.setEnabled( isGssapiEnabled() && krb5ConfigManualButton.getSelection()
                && !useKrb5SystemProperties );
            krb5ConfigManualPortText.setEnabled( isGssapiEnabled() && krb5ConfigManualButton.getSelection()
                && !useKrb5SystemProperties );
        }
        bindPrincipalCombo.setEnabled( isPrincipalPasswordEnabled() );
        bindPasswordText.setEnabled( isPrincipalPasswordEnabled() && isSaveBindPassword() );
        saveBindPasswordButton.setEnabled( isPrincipalPasswordEnabled() );
        checkPrincipalPasswordAuthButton
            .setEnabled( ( isPrincipalPasswordEnabled() && isSaveBindPassword()
                && !bindPrincipalCombo.getText().equals( "" ) && !bindPasswordText.getText().equals( "" ) ) || isGssapiEnabled() ); //$NON-NLS-1$ //$NON-NLS-2$

        // validate input fields
        message = null;
        infoMessage = null;
        errorMessage = null;
        if ( isPrincipalPasswordEnabled() )
        {
            if ( isSaveBindPassword() && "".equals( bindPasswordText.getText() ) ) //$NON-NLS-1$
            {
                message = Messages.getString( "AuthenticationParameterPage.PleaseEnterBindPassword" ); //$NON-NLS-1$
            }
            if ( "".equals( bindPrincipalCombo.getText() ) && !isGssapiEnabled() ) //$NON-NLS-1$
            {
                message = Messages.getString( "AuthenticationParameterPage.PleaseEnterBindDNOrUser" ); //$NON-NLS-1$
            }
        }

        if ( isSaslRealmTextEnabled() )
        {
            if ( "".equals( saslRealmText.getText() ) ) //$NON-NLS-1$
            {
                infoMessage = Messages.getString( "AuthenticationParameterPage.PleaseEnterSaslRealm" ); //$NON-NLS-1$
            }
        }

        if ( isGssapiEnabled() && krb5ConfigFileButton.getSelection() )
        {
            if ( "".equals( krb5ConfigFileText.getText() ) ) //$NON-NLS-1$
            {
                message = Messages.getString( "AuthenticationParameterPage.PleaseEnterKrb5ConfigFile" ); //$NON-NLS-1$
            }
        }

        if ( isGssapiEnabled() && krb5ConfigManualButton.getSelection() )
        {
            if ( "".equals( krb5ConfigManualPortText.getText() ) ) //$NON-NLS-1$
            {
                message = Messages.getString( "AuthenticationParameterPage.PleaseEnterKrb5Port" ); //$NON-NLS-1$
            }
            if ( "".equals( krb5ConfigManualHostText.getText() ) ) //$NON-NLS-1$
            {
                message = Messages.getString( "AuthenticationParameterPage.PleaseEnterKrb5Host" ); //$NON-NLS-1$
            }
            if ( "".equals( krb5ConfigManualRealmText.getText() ) ) //$NON-NLS-1$
            {
                message = Messages.getString( "AuthenticationParameterPage.PleaseEnterKrb5Realm" ); //$NON-NLS-1$
            }
        }
    }


    /**
     * Checks if is principal password enabled.
     * 
     * @return true, if is principal password enabled
     */
    private boolean isPrincipalPasswordEnabled()
    {
        return ( getAuthenticationMethod() == AuthenticationMethod.SIMPLE )
            || ( getAuthenticationMethod() == AuthenticationMethod.SASL_DIGEST_MD5 )
            || ( getAuthenticationMethod() == AuthenticationMethod.SASL_CRAM_MD5 )
            || ( getAuthenticationMethod() == AuthenticationMethod.SASL_GSSAPI && krb5CredentialConfigurationObtainTgtButton
                .getSelection() );
    }


    private boolean isSaslRealmTextEnabled()
    {
        return getAuthenticationMethod() == AuthenticationMethod.SASL_DIGEST_MD5;
    }


    private boolean isSaslEnabled()
    {
        return ( getAuthenticationMethod() == AuthenticationMethod.SASL_DIGEST_MD5 )
            || ( getAuthenticationMethod() == AuthenticationMethod.SASL_CRAM_MD5 )
            || ( getAuthenticationMethod() == AuthenticationMethod.SASL_GSSAPI );
    }


    private boolean isGssapiEnabled()
    {
        return getAuthenticationMethod() == AuthenticationMethod.SASL_GSSAPI;
    }


    /**
     * @see org.apache.directory.studio.connection.ui.AbstractConnectionParameterPage#loadParameters(org.apache.directory.studio.connection.core.ConnectionParameter)
     */
    protected void loadParameters( ConnectionParameter parameter )
    {
        this.connectionParameter = parameter;

        int index = parameter.getAuthMethod() == AuthenticationMethod.SIMPLE ? 1
            : parameter.getAuthMethod() == AuthenticationMethod.SASL_DIGEST_MD5 ? 2
                : parameter.getAuthMethod() == AuthenticationMethod.SASL_CRAM_MD5 ? 3
                    : parameter.getAuthMethod() == AuthenticationMethod.SASL_GSSAPI ? 4 : 0;
        authenticationMethodCombo.select( index );
        bindPrincipalCombo.setText( parameter.getBindPrincipal() );
        bindPasswordText.setText( parameter.getBindPassword() != null ? parameter.getBindPassword() : "" ); //$NON-NLS-1$
        saveBindPasswordButton.setSelection( parameter.getBindPassword() != null );

        saslRealmText.setText( parameter.getSaslRealm() != null ? parameter.getSaslRealm() : "" ); //$NON-NLS-1$
        int qopIndex = parameter.getSaslQop() == SaslQoP.AUTH_INT ? 1
            : parameter.getSaslQop() == SaslQoP.AUTH_CONF ? 2 : 0;
        saslQopCombo.select( qopIndex );
        int securityStrengthIndex = parameter.getSaslSecurityStrength() == SaslSecurityStrength.MEDIUM ? 1 : parameter
            .getSaslSecurityStrength() == SaslSecurityStrength.LOW ? 2 : 0;
        saslSecurityStrengthCombo.select( securityStrengthIndex );
        saslMutualAuthenticationButton.setSelection( parameter.isSaslMutualAuthentication() );

        krb5CredentialConfigurationUseNativeButton
            .setSelection( parameter.getKrb5CredentialConfiguration() == Krb5CredentialConfiguration.USE_NATIVE );
        krb5CredentialConfigurationObtainTgtButton
            .setSelection( parameter.getKrb5CredentialConfiguration() == Krb5CredentialConfiguration.OBTAIN_TGT );
        krb5ConfigDefaultButton.setSelection( parameter.getKrb5Configuration() == Krb5Configuration.DEFAULT );
        krb5ConfigFileButton.setSelection( parameter.getKrb5Configuration() == Krb5Configuration.FILE );
        krb5ConfigManualButton.setSelection( parameter.getKrb5Configuration() == Krb5Configuration.MANUAL );
        krb5ConfigFileText.setText( parameter.getKrb5ConfigurationFile() != null ? parameter.getKrb5ConfigurationFile()
            : "" ); //$NON-NLS-1$
        krb5ConfigManualRealmText.setText( parameter.getKrb5Realm() != null ? parameter.getKrb5Realm() : "" ); //$NON-NLS-1$
        krb5ConfigManualHostText.setText( parameter.getKrb5KdcHost() != null ? parameter.getKrb5KdcHost() : "" ); //$NON-NLS-1$
        krb5ConfigManualPortText.setText( parameter.getKrb5KdcPort() != 0 ? "" + parameter.getKrb5KdcPort() : "" ); //$NON-NLS-1$
    }


    /**
     * @see org.apache.directory.studio.connection.ui.AbstractConnectionParameterPage#initListeners()
     */
    protected void initListeners()
    {
        authenticationMethodCombo.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                connectionPageModified();
            }
        } );

        bindPrincipalCombo.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent event )
            {
                connectionPageModified();
            }
        } );

        bindPasswordText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent event )
            {
                connectionPageModified();
            }
        } );

        saveBindPasswordButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                connectionPageModified();
            }
        } );

        checkPrincipalPasswordAuthButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                Connection connection = getTestConnection();
                CheckBindRunnable runnable = new CheckBindRunnable( connection );
                IStatus status = RunnableContextRunner.execute( runnable, runnableContext, true );
                if ( status.isOK() )
                {
                    MessageDialog.openInformation( Display.getDefault().getActiveShell(), Messages
                        .getString( "AuthenticationParameterPage.CheckAuthentication" ), //$NON-NLS-1$
                        Messages.getString( "AuthenticationParameterPage.AuthenticationSuccessfull" ) ); //$NON-NLS-1$
                }
            }
        } );

        saslRealmText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent even )
            {
                connectionPageModified();
            }
        } );

        saslQopCombo.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                connectionPageModified();
            }
        } );

        saslSecurityStrengthCombo.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                connectionPageModified();
            }
        } );

        saslMutualAuthenticationButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                connectionPageModified();
            }
        } );

        krb5CredentialConfigurationUseNativeButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                connectionPageModified();
            }
        } );

        krb5CredentialConfigurationObtainTgtButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                connectionPageModified();
            }
        } );

        krb5ConfigDefaultButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                connectionPageModified();
            }
        } );

        krb5ConfigFileButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                connectionPageModified();
            }
        } );

        krb5ConfigFileText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent event )
            {
                connectionPageModified();
            }
        } );

        krb5ConfigManualButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                connectionPageModified();
            }
        } );

        krb5ConfigManualRealmText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent event )
            {
                connectionPageModified();
            }
        } );

        krb5ConfigManualHostText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent event )
            {
                connectionPageModified();
            }
        } );

        krb5ConfigManualPortText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent event )
            {
                if ( !event.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
                {
                    event.doit = false;
                }
            }
        } );

        krb5ConfigManualPortText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent event )
            {
                connectionPageModified();
            }
        } );
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPage#saveParameters(org.apache.directory.studio.connection.core.ConnectionParameter)
     */
    public void saveParameters( ConnectionParameter parameter )
    {
        parameter.setAuthMethod( getAuthenticationMethod() );
        parameter.setBindPrincipal( getBindPrincipal() );
        parameter.setBindPassword( getBindPassword() );

        parameter.setSaslRealm( getSaslRealm() );
        parameter.setSaslQop( getSaslQop() );
        parameter.setSaslSecurityStrength( getSaslSecurityStrength() );
        parameter.setSaslMutualAuthentication( saslMutualAuthenticationButton.getSelection() );

        parameter.setKrb5CredentialConfiguration( getKrb5CredentialProvider() );
        parameter.setKrb5Configuration( getKrb5Configuration() );
        parameter.setKrb5ConfigurationFile( krb5ConfigFileText.getText() );
        parameter.setKrb5Realm( krb5ConfigManualRealmText.getText() );
        parameter.setKrb5KdcHost( krb5ConfigManualHostText.getText() );
        parameter.setKrb5KdcPort( getKdcPort() );
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPage#saveDialogSettings()
     */
    public void saveDialogSettings()
    {
        HistoryUtils.save( ConnectionUIConstants.DIALOGSETTING_KEY_PRINCIPAL_HISTORY, bindPrincipalCombo.getText() );
        if ( getAuthenticationMethod().equals( AuthenticationMethod.SASL_DIGEST_MD5 ) )
        {
            HistoryUtils.save( ConnectionUIConstants.DIALOGSETTING_KEY_REALM_HISTORY, saslRealmText.getText() );
        }
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPage#setFocus()
     */
    public void setFocus()
    {
        bindPrincipalCombo.setFocus();
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPage#areParametersModifed()
     */
    public boolean areParametersModifed()
    {
        return isReconnectionRequired();
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPage#isReconnectionRequired()
     */
    public boolean isReconnectionRequired()
    {
        return connectionParameter == null || connectionParameter.getAuthMethod() != getAuthenticationMethod()
            || !StringUtils.equals( connectionParameter.getBindPrincipal(), getBindPrincipal() )
            || !StringUtils.equals( connectionParameter.getBindPassword(), getBindPassword() )
            || !StringUtils.equals( connectionParameter.getSaslRealm(), getSaslRealm() )
            || connectionParameter.getSaslQop() != getSaslQop()
            || connectionParameter.getSaslSecurityStrength() != getSaslSecurityStrength()
            || connectionParameter.isSaslMutualAuthentication() != saslMutualAuthenticationButton.getSelection()
            || connectionParameter.getKrb5CredentialConfiguration() != getKrb5CredentialProvider()
            || connectionParameter.getKrb5Configuration() != getKrb5Configuration()
            || !StringUtils.equals( connectionParameter.getKrb5ConfigurationFile(), krb5ConfigFileText.getText() )
            || !StringUtils.equals( connectionParameter.getKrb5Realm(), krb5ConfigManualRealmText.getText() )
            || !StringUtils.equals( connectionParameter.getKrb5KdcHost(), krb5ConfigManualHostText.getText() )
            || connectionParameter.getKrb5KdcPort() != getKdcPort();
    }


    /**
     * {@inheritDoc}
     */
    public void mergeParametersToLdapURL( ConnectionParameter parameter, LdapUrl ldapUrl )
    {
        switch ( parameter.getAuthMethod() )
        {
            case SASL_CRAM_MD5:
                ldapUrl.getExtensions().add( new Extension( false, X_AUTH_METHOD, X_AUTH_METHOD_CRAM_MD5 ) );
                break;
            case SASL_DIGEST_MD5:
                ldapUrl.getExtensions().add( new Extension( false, X_AUTH_METHOD, X_AUTH_METHOD_DIGEST_MD5 ) );
                break;
            case SASL_GSSAPI:
                ldapUrl.getExtensions().add( new Extension( false, X_AUTH_METHOD, X_AUTH_METHOD_GSSAPI ) );
                break;
            case SIMPLE:
                if ( StringUtils.isEmpty( parameter.getBindPrincipal() ) )
                {
                    // default if bind user is present
                    ldapUrl.getExtensions().add( new Extension( false, X_AUTH_METHOD, X_AUTH_METHOD_SIMPLE ) );
                }
                break;
            case NONE:
                if ( StringUtils.isNotEmpty( parameter.getBindPrincipal() ) )
                {
                    // default if bind user is absent
                    ldapUrl.getExtensions().add( new Extension( false, X_AUTH_METHOD, X_AUTH_METHOD_ANONYMOUS ) );
                }
                break;
        }

        if ( StringUtils.isNotEmpty( parameter.getBindPrincipal() ) )
        {
            ldapUrl.getExtensions().add( new Extension( false, X_BIND_USER, parameter.getBindPrincipal() ) );
        }

        if ( StringUtils.isNotEmpty( parameter.getBindPassword() ) )
        {
            ldapUrl.getExtensions().add( new Extension( false, X_BIND_PASSWORD, parameter.getBindPassword() ) );
        }

        switch ( parameter.getAuthMethod() )
        {
            case SASL_CRAM_MD5:
            case SASL_DIGEST_MD5:
            case SASL_GSSAPI:
                if ( StringUtils.isNotEmpty( parameter.getSaslRealm() ) )
                {
                    ldapUrl.getExtensions().add( new Extension( false, X_SASL_REALM, parameter.getSaslRealm() ) );
                }

                switch ( parameter.getSaslQop() )
                {
                    case AUTH:
                        // default
                        break;
                    case AUTH_INT:
                        ldapUrl.getExtensions().add( new Extension( false, X_SASL_QOP, X_SASL_QOP_AUTH_INT ) );
                        break;
                    case AUTH_CONF:
                        ldapUrl.getExtensions().add( new Extension( false, X_SASL_QOP, X_SASL_QOP_AUTH_INT_PRIV ) );
                        break;
                }

                switch ( parameter.getSaslSecurityStrength() )
                {
                    case HIGH:
                        // default
                        break;
                    case MEDIUM:
                        ldapUrl.getExtensions().add(
                            new Extension( false, X_SASL_SEC_STRENGTH, X_SASL_SEC_STRENGTH_MEDIUM ) );
                        break;
                    case LOW:
                        ldapUrl.getExtensions().add(
                            new Extension( false, X_SASL_SEC_STRENGTH, X_SASL_SEC_STRENGTH_LOW ) );
                        break;
                }

                if ( !parameter.isSaslMutualAuthentication() )
                {
                    ldapUrl.getExtensions().add( new Extension( false, X_SASL_NO_MUTUAL_AUTH, null ) );
                }
        }

        switch ( parameter.getAuthMethod() )
        {
            case SASL_GSSAPI:
                switch ( parameter.getKrb5CredentialConfiguration() )
                {
                    case USE_NATIVE:
                        // default
                        break;
                    case OBTAIN_TGT:
                        ldapUrl.getExtensions().add(
                            new Extension( false, X_KRB5_CREDENTIALS_CONF, X_KRB5_CREDENTIALS_CONF_OBTAIN_TGT ) );
                        break;
                }

                switch ( parameter.getKrb5Configuration() )
                {
                    case DEFAULT:
                        // default
                        break;
                    case FILE:
                        ldapUrl.getExtensions().add( new Extension( false, X_KRB5_CONFIG, X_KRB5_CONFIG_FILE ) );
                        ldapUrl.getExtensions().add(
                            new Extension( false, X_KRB5_CONFIG_FILE_FILE, parameter.getKrb5ConfigurationFile() ) );
                        break;
                    case MANUAL:
                        ldapUrl.getExtensions().add( new Extension( false, X_KRB5_CONFIG, X_KRB5_CONFIG_MANUAL ) );
                        ldapUrl.getExtensions().add(
                            new Extension( false, X_KRB5_CONFIG_MANUAL_REALM, parameter.getKrb5Realm() ) );
                        ldapUrl.getExtensions().add(
                            new Extension( false, X_KRB5_CONFIG_MANUAL_KDC_HOST, parameter.getKrb5KdcHost() ) );
                        ldapUrl.getExtensions().add(
                            new Extension( false, X_KRB5_CONFIG_MANUAL_KDC_PORT, "" + parameter.getKrb5KdcPort() ) );
                        break;
                }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void mergeLdapUrlToParameters( LdapUrl ldapUrl, ConnectionParameter parameter )
    {
        // bind user and password, none if empty or absent
        String principal = ldapUrl.getExtensionValue( X_BIND_USER );
        if ( principal == null )
        {
            principal = StringUtils.EMPTY;
        }
        parameter.setBindPrincipal( principal );

        String password = ldapUrl.getExtensionValue( X_BIND_PASSWORD );
        parameter.setBindPassword( password );

        // auth method, simple if unknown or absent and X-BIND-USER is present, else anonymous 
        String authMethod = ldapUrl.getExtensionValue( X_AUTH_METHOD );
        if ( StringUtils.isNotEmpty( authMethod ) && X_AUTH_METHOD_ANONYMOUS.equalsIgnoreCase( authMethod ) )
        {
            parameter.setAuthMethod( ConnectionParameter.AuthenticationMethod.NONE );
        }
        else if ( StringUtils.isNotEmpty( authMethod ) && X_AUTH_METHOD_SIMPLE.equalsIgnoreCase( authMethod ) )
        {
            parameter.setAuthMethod( ConnectionParameter.AuthenticationMethod.SIMPLE );
        }
        else if ( StringUtils.isNotEmpty( authMethod ) && X_AUTH_METHOD_DIGEST_MD5.equalsIgnoreCase( authMethod ) )
        {
            parameter.setAuthMethod( ConnectionParameter.AuthenticationMethod.SASL_DIGEST_MD5 );
        }
        else if ( StringUtils.isNotEmpty( authMethod ) && X_AUTH_METHOD_CRAM_MD5.equalsIgnoreCase( authMethod ) )
        {
            parameter.setAuthMethod( ConnectionParameter.AuthenticationMethod.SASL_CRAM_MD5 );
        }
        else if ( StringUtils.isNotEmpty( parameter.getBindPrincipal() ) )
        {
            parameter.setAuthMethod( ConnectionParameter.AuthenticationMethod.SIMPLE );
        }
        else
        {
            parameter.setAuthMethod( ConnectionParameter.AuthenticationMethod.NONE );
        }

        // SASL realm, none if empty or absent 
        String saslRealm = ldapUrl.getExtensionValue( X_SASL_REALM );
        if ( StringUtils.isNotEmpty( saslRealm ) )
        {
            parameter.setSaslRealm( saslRealm );
        }

        // SASL QOP, default to AUTH
        String saslQop = ldapUrl.getExtensionValue( X_SASL_QOP );
        if ( StringUtils.isNotEmpty( saslQop ) && X_SASL_QOP_AUTH_INT.equalsIgnoreCase( saslQop ) )
        {
            parameter.setSaslQop( SaslQoP.AUTH_INT );
        }
        else if ( StringUtils.isNotEmpty( saslQop ) && X_SASL_QOP_AUTH_INT_PRIV.equalsIgnoreCase( saslQop ) )
        {
            parameter.setSaslQop( SaslQoP.AUTH_CONF );
        }
        else
        {
            parameter.setSaslQop( SaslQoP.AUTH );
        }

        // SASL security strength, default to HIGH
        String saslSecStrength = ldapUrl.getExtensionValue( X_SASL_SEC_STRENGTH );
        if ( StringUtils.isNotEmpty( saslSecStrength ) && X_SASL_SEC_STRENGTH_MEDIUM.equalsIgnoreCase( saslSecStrength ) )
        {
            parameter.setSaslSecurityStrength( SaslSecurityStrength.MEDIUM );
        }
        else if ( StringUtils.isNotEmpty( saslSecStrength )
            && X_SASL_SEC_STRENGTH_LOW.equalsIgnoreCase( saslSecStrength ) )
        {
            parameter.setSaslSecurityStrength( SaslSecurityStrength.LOW );
        }
        else
        {
            parameter.setSaslSecurityStrength( SaslSecurityStrength.HIGH );
        }

        // SASL mutual authentication, default to true
        Extension saslNoMutualAuth = ldapUrl.getExtension( X_SASL_NO_MUTUAL_AUTH );
        parameter.setSaslMutualAuthentication( saslNoMutualAuth == null );

        // KRB5 credentials
        String krb5CredentialsConf = ldapUrl.getExtensionValue( X_KRB5_CREDENTIALS_CONF );
        if ( StringUtils.isNotEmpty( krb5CredentialsConf )
            && X_KRB5_CREDENTIALS_CONF_OBTAIN_TGT.equalsIgnoreCase( krb5CredentialsConf ) )
        {
            parameter.setKrb5CredentialConfiguration( Krb5CredentialConfiguration.OBTAIN_TGT );
        }
        else
        {
            parameter.setKrb5CredentialConfiguration( Krb5CredentialConfiguration.USE_NATIVE );
        }

        // KRB5 configuration
        String krb5Config = ldapUrl.getExtensionValue( X_KRB5_CONFIG );
        if ( StringUtils.isNotEmpty( krb5Config ) && X_KRB5_CONFIG_FILE.equalsIgnoreCase( krb5Config ) )
        {
            parameter.setKrb5Configuration( Krb5Configuration.FILE );
        }
        else if ( StringUtils.isNotEmpty( krb5Config ) && X_KRB5_CONFIG_MANUAL.equalsIgnoreCase( krb5Config ) )
        {
            parameter.setKrb5Configuration( Krb5Configuration.MANUAL );
        }
        else
        {
            parameter.setKrb5Configuration( Krb5Configuration.DEFAULT );
        }

        parameter.setKrb5ConfigurationFile( ldapUrl.getExtensionValue( X_KRB5_CONFIG_FILE_FILE ) );
        parameter.setKrb5Realm( ldapUrl.getExtensionValue( X_KRB5_CONFIG_MANUAL_REALM ) );
        parameter.setKrb5KdcHost( ldapUrl.getExtensionValue( X_KRB5_CONFIG_MANUAL_KDC_HOST ) );

        String kdcPort = ldapUrl.getExtensionValue( X_KRB5_CONFIG_MANUAL_KDC_PORT );
        try
        {
            parameter.setKrb5KdcPort( new Integer( kdcPort ).intValue() );
        }
        catch ( NumberFormatException e )
        {
            parameter.setKrb5KdcPort( 88 );
        }
    }
}
