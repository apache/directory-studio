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
import org.apache.directory.shared.ldap.util.LdapURL;
import org.apache.directory.shared.ldap.util.LdapURL.Extension;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.apache.directory.studio.connection.core.ConnectionParameter.AuthenticationMethod;
import org.apache.directory.studio.connection.core.jobs.CheckBindRunnable;
import org.apache.directory.studio.connection.ui.AbstractConnectionParameterPage;
import org.apache.directory.studio.connection.ui.ConnectionUIConstants;
import org.apache.directory.studio.connection.ui.RunnableContextRunner;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;


/**
 * The AuthenticationParameterPage is used the edit the authentication parameters of a
 * connection.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class AuthenticationParameterPage extends AbstractConnectionParameterPage
{

    private static final String X_AUTH_METHOD = "X-AUTH-METHOD";

    private static final String X_AUTH_METHOD_ANONYMOUS = "Anonymous";

    private static final String X_AUTH_METHOD_SIMPLE = "Simple";

    private static final String X_AUTH_METHOD_DIGEST_MD5 = "DIGEST-MD5";

    private static final String X_AUTH_METHOD_CRAM_MD5 = "CRAM-MD5";

    private static final String X_BIND_USER = "X-BIND-USER";

    private static final String X_BIND_PASSWORD = "X-BIND-PASSWORD";

    private static final String X_SASL_REALM = "X-SASL-REALM";

    /** The combo to select the authentication method */
    private Combo authenticationMethodCombo;

    /** The bind user combo with the history of recently used bind users */
    private Combo bindPrincipalCombo;

    /** The text widget to input bind password */
    private Text bindPasswordText;

    /** The text widget to input saslRealm */
    private Combo saslRealmText;

    /** The checkbox to choose if the bind password should be saved on disk */
    private Button saveBindPasswordButton;

    /** The button to check the authentication parameters */
    private Button checkPrincipalPasswordAuthButton;;


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
        Composite composite1 = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        Group group1 = BaseWidgetUtils.createGroup( composite1, "Authentication Method", 1 );
        Composite groupComposite = BaseWidgetUtils.createColumnContainer( group1, 1, 1 );

        String[] authMethods = new String[]
            { "Anonymous Authentication", "Simple Authentication", "DIGEST-MD5 (SASL)", "CRAM-MD5 (SASL)" };
        authenticationMethodCombo = BaseWidgetUtils.createReadonlyCombo( groupComposite, authMethods, 1, 2 );

        Composite composite2 = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        Group group2 = BaseWidgetUtils.createGroup( composite2, "Authentication Parameter", 1 );
        Composite composite = BaseWidgetUtils.createColumnContainer( group2, 3, 1 );

        BaseWidgetUtils.createLabel( composite, "Bind DN or user:", 1 );
        String[] dnHistory = HistoryUtils.load( ConnectionUIConstants.DIALOGSETTING_KEY_PRINCIPAL_HISTORY );
        bindPrincipalCombo = BaseWidgetUtils.createCombo( composite, dnHistory, -1, 2 );

        BaseWidgetUtils.createLabel( composite, "Bind password:", 1 );
        bindPasswordText = BaseWidgetUtils.createPasswordText( composite, "", 2 );

        BaseWidgetUtils.createLabel( composite, "SASL Realm:", 1 );
        String[] saslHistory = HistoryUtils.load( ConnectionUIConstants.DIALOGSETTING_KEY_REALM_HISTORY );
        saslRealmText = BaseWidgetUtils.createCombo( composite, saslHistory, -1, 2 );

        BaseWidgetUtils.createSpacer( composite, 1 );
        saveBindPasswordButton = BaseWidgetUtils.createCheckbox( composite, "Save password", 1 );
        saveBindPasswordButton.setSelection( true );

        checkPrincipalPasswordAuthButton = new Button( composite, SWT.PUSH );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalAlignment = SWT.RIGHT;
        checkPrincipalPasswordAuthButton.setLayoutData( gd );
        checkPrincipalPasswordAuthButton.setText( "Check Authentication" );
        checkPrincipalPasswordAuthButton.setEnabled( false );
    }


    /**
     * @see org.apache.directory.studio.connection.ui.AbstractConnectionParameterPage#validate()
     */
    protected void validate()
    {
        // set enabled/disabled state of fields and buttons
        bindPrincipalCombo.setEnabled( isPrincipalPasswordEnabled() );
        bindPasswordText.setEnabled( isPrincipalPasswordEnabled() && isSaveBindPassword() );
        saveBindPasswordButton.setEnabled( isPrincipalPasswordEnabled() );
        checkPrincipalPasswordAuthButton.setEnabled( isPrincipalPasswordEnabled() && isSaveBindPassword()
            && !bindPrincipalCombo.getText().equals( "" ) && !bindPasswordText.getText().equals( "" ) );
        saslRealmText.setEnabled( isSaslRealmTextEnabled() );

        // validate input fields
        message = null;
        infoMessage = null;
        errorMessage = null;
        if ( isPrincipalPasswordEnabled() )
        {
            if ( isSaveBindPassword() && "".equals( bindPasswordText.getText() ) )
            {
                message = "Please enter a bind password.";
            }
            if ( "".equals( bindPrincipalCombo.getText() ) )
            {
                message = "Please enter a bind DN or user.";
            }
        }

        if ( isSaslRealmTextEnabled() )
        {
            if ( "".equals( saslRealmText.getText() ) )
            {
                infoMessage = "Please enter an SASL Realm otherwise any available SASL realm is choosen";
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
            || ( getAuthenticationMethod() == AuthenticationMethod.SASL_CRAM_MD5 );
    }


    private boolean isSaslRealmTextEnabled()
    {
        return getAuthenticationMethod() == AuthenticationMethod.SASL_DIGEST_MD5;
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
        bindPasswordText.setText( parameter.getBindPassword() != null ? parameter.getBindPassword() : "" );
        saveBindPasswordButton.setSelection( parameter.getBindPassword() != null );
        saslRealmText.setText( parameter.getSaslRealm() != null ? parameter.getSaslRealm() : "" );
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

        saslRealmText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent even )
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
                    MessageDialog.openInformation( Display.getDefault().getActiveShell(), "Check Authentication",
                        "The authentication was successful." );
                }
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
            || !StringUtils.equals( connectionParameter.getSaslRealm(), getSaslRealm() );
    }


    /**
     * {@inheritDoc}
     */
    public void mergeParametersToLdapURL( ConnectionParameter parameter, LdapURL ldapUrl )
    {
        switch ( parameter.getAuthMethod() )
        {
            case SASL_CRAM_MD5:
                ldapUrl.getExtensions().add( new Extension( false, X_AUTH_METHOD, X_AUTH_METHOD_CRAM_MD5 ) );
                break;
            case SASL_DIGEST_MD5:
                ldapUrl.getExtensions().add( new Extension( false, X_AUTH_METHOD, X_AUTH_METHOD_DIGEST_MD5 ) );
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

        if ( StringUtils.isNotEmpty( parameter.getSaslRealm() ) )
        {
            ldapUrl.getExtensions().add( new Extension( false, X_SASL_REALM, parameter.getSaslRealm() ) );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void mergeLdapUrlToParameters( LdapURL ldapUrl, ConnectionParameter parameter )
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
    }
}
