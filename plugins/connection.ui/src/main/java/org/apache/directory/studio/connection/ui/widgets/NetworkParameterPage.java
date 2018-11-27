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


import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.directory.api.ldap.model.url.LdapUrl;
import org.apache.directory.api.ldap.model.url.LdapUrl.Extension;
import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.common.ui.CommonUIUtils;
import org.apache.directory.studio.common.ui.HistoryUtils;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCoreConstants;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.apache.directory.studio.connection.core.ConnectionParameter.EncryptionMethod;
import org.apache.directory.studio.connection.core.ConnectionParameter.NetworkProvider;
import org.apache.directory.studio.connection.core.jobs.CheckNetworkParameterRunnable;
import org.apache.directory.studio.connection.ui.AbstractConnectionParameterPage;
import org.apache.directory.studio.connection.ui.ConnectionUIConstants;
import org.apache.directory.studio.connection.ui.ConnectionUIPlugin;
import org.apache.directory.studio.connection.ui.RunnableContextRunner;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PreferencesUtil;


/**
 * The NetworkParameterPage is used the edit the network parameters of a
 * connection. This is a tab in the connection property widget :
 * 
 * <pre>
 * .---------------------------------------------------------------------------.
 * | Connection                                                                |
 * +---------------------------------------------------------------------------+
 * | .---[Network Parameter]|Authentication||Browser Options||Edit Options|--. |
 * | |                                                                       | | 
 * | | Connection name : [-------------------------------------]             | |
 * | |                                                                       | |
 * | | Network Parameter                                                     | |
 * | | .-------------------------------------------------------------------. | |
 * | | |                                                                   | | |
 * | | |  Hostname :          [----------------------------------------|v] | | |
 * | | |  Port :              [----------------------------------------|v] | | |
 * | | |  Timeout :                  [                                   ] | | |
 * | | |  Encryption method : [-No encryption--------------------------|v] | | |
 * | | |                      Server certificates for LDAP connections can | | |
 * | | |                      managed in the '<certificate validation>'    | | |
 * | | |                      preference page.                             | | |
 * | | |  Provider :          [ Apache Directory LDAP API              |v] | | |
 * | | |                                                                   | | |
 * | | |                                         (Check Network Parameter) | | |
 * | | +-------------------------------------------------------------------+ | |
 * | |                                                                       | |
 * | | [] Read-Only (prevents any add, delete, modify or rename operation)   | |
 * | |                                                                       | |  
 * | +-----------------------------------------------------------------------+ |
 * +---------------------------------------------------------------------------+
 * </pre>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class NetworkParameterPage extends AbstractConnectionParameterPage
{
    private static final String X_CONNECTION_NAME = "X-CONNECTION-NAME"; //$NON-NLS-1$

    private static final String X_ENCRYPTION = "X-ENCRYPTION"; //$NON-NLS-1$

    private static final String X_ENCRYPTION_LDAPS = "ldaps"; //$NON-NLS-1$

    private static final String X_ENCRYPTION_START_TLS = "StartTLS"; //$NON-NLS-1$

    private static final String X_NETWORK_PROVIDER = "X-NETWORK-PROVIDER"; //$NON-NLS-1$

    private static final String X_NETWORK_PROVIDER_JNDI = "JNDI"; //$NON-NLS-1$

    private static final String X_NETWORK_PROVIDER_APACHE_DIRECTORY_LDAP_API = "ApacheDirectoryLdapApi"; //$NON-NLS-1$

    /** The connection name text widget */
    private Text nameText;

    /** The host name combo with the history of recently used host names */
    private Combo hostCombo;

    /** The host combo with the history of recently used ports */
    private Combo portCombo;

    /** The combo to select the encryption method */
    private Combo encryptionMethodCombo;

    /** The combo to select the network provider */
    private Combo networkProviderCombo;

    /** The button to check the connection parameters */
    private Button checkConnectionButton;

    /** The checkbox to make the connection read-only */
    private Button readOnlyConnectionCheckbox;

    /** A timeout for the connection. Default to 30s */
    private Text timeoutText;

    /**
     * A listener for the Link data widget. It will open the CertificateValidationPreference dialog.
     */
    private SelectionAdapter linkDataWidgetListener = new SelectionAdapter()
    {
        @Override
        public void widgetSelected( SelectionEvent event )
        {
            String certificateValidationPreferencePageId = ConnectionUIPlugin.getDefault()
                .getPluginProperties().getString( "PrefPage_CertificateValidationPreferencePage_id" ); //$NON-NLS-1$

            PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn( Display.getDefault()
                .getActiveShell(), certificateValidationPreferencePageId, new String[]
                { certificateValidationPreferencePageId }, null );
            dialog.open();
        }
    };


    /**
     * Gets the connection name.
     * 
     * @return the connectio name
     */
    private String getName()
    {
        return nameText.getText();
    }


    /**
     * Gets the host name.
     * 
     * @return the host name
     */
    private String getHostName()
    {
        return hostCombo.getText();
    }


    /**
     * Gets the port.
     * 
     * @return the port
     */
    private int getPort()
    {
        return Integer.parseInt( portCombo.getText() );
    }


    /**
     * Gets the timeout
     * 
     * @return The tiemout
     */
    private int getTimeout()
    {
        String timeoutString = timeoutText.getText();

        if ( Strings.isEmpty( timeoutString ) )
        {
            return 30;
        }
        else
        {
            return Integer.parseInt( timeoutString );
        }
    }


    /**
     * Gets the encyrption method.
     * 
     * @return the encyrption method
     */
    private ConnectionParameter.EncryptionMethod getEncyrptionMethod()
    {
        switch ( encryptionMethodCombo.getSelectionIndex() )
        {
            case 1:
                return ConnectionParameter.EncryptionMethod.LDAPS;

            case 2:
                return ConnectionParameter.EncryptionMethod.START_TLS;

            default:
                return ConnectionParameter.EncryptionMethod.NONE;
        }
    }


    /**
     * Gets the network type (JNDI or Apache LDAP API) 
     * 
     * @return the network type
     */
    private ConnectionParameter.NetworkProvider getNetworkProvider()
    {
        if ( networkProviderCombo.getSelectionIndex() == 1 )
        {
            return ConnectionParameter.NetworkProvider.JNDI;
        }

        return ConnectionParameter.NetworkProvider.APACHE_DIRECTORY_LDAP_API;
    }


    /**
     * Gets a temporary connection with all connection parameter 
     * entered in this page. 
     *
     * @return a test connection
     */
    private Connection getTestConnection()
    {
        ConnectionParameter connectionParameter = new ConnectionParameter( null, getHostName(), getPort(),
            getEncyrptionMethod(),
            getNetworkProvider(), ConnectionParameter.AuthenticationMethod.NONE, null, null, null, true, null, 30 );

        return new Connection( connectionParameter );
    }


    /**
     * Gets read only flag.
     * 
     * @return the read only flag
     */
    private boolean isReadOnly()
    {
        return readOnlyConnectionCheckbox.getSelection();
    }


    /**
     * {@inheritDoc}
     */
    protected void createComposite( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        Composite nameComposite = BaseWidgetUtils.createColumnContainer( composite, 2, 1 );
        BaseWidgetUtils.createLabel( nameComposite, Messages.getString( "NetworkParameterPage.ConnectionName" ), 1 ); //$NON-NLS-1$
        nameText = BaseWidgetUtils.createText( nameComposite, StringUtils.EMPTY, 1 ); //$NON-NLS-1$

        BaseWidgetUtils.createSpacer( composite, 1 );

        Group group = BaseWidgetUtils.createGroup( composite, Messages
            .getString( "NetworkParameterPage.NetworkParameter" ), 1 ); //$NON-NLS-1$

        IDialogSettings dialogSettings = ConnectionUIPlugin.getDefault().getDialogSettings();

        // The network hostname
        Composite groupComposite = BaseWidgetUtils.createColumnContainer( group, 3, 1 );
        BaseWidgetUtils.createLabel( groupComposite, Messages.getString( "NetworkParameterPage.HostName" ), 1 ); //$NON-NLS-1$
        String[] hostHistory = HistoryUtils.load( dialogSettings,
            ConnectionUIConstants.DIALOGSETTING_KEY_HOST_HISTORY );
        hostCombo = BaseWidgetUtils.createCombo( groupComposite, hostHistory, -1, 2 );

        // The network port
        BaseWidgetUtils.createLabel( groupComposite, Messages.getString( "NetworkParameterPage.Port" ), 1 ); //$NON-NLS-1$
        String[] portHistory = HistoryUtils.load( dialogSettings,
            ConnectionUIConstants.DIALOGSETTING_KEY_PORT_HISTORY );
        portCombo = BaseWidgetUtils.createCombo( groupComposite, portHistory, -1, 2 );
        portCombo.setTextLimit( 5 );
        portCombo.setText( "389" ); //$NON-NLS-1$

        // The timeout
        BaseWidgetUtils.createLabel( groupComposite, Messages.getString( "NetworkParameterPage.Timeout" ), 2 ); //$NON-NLS-1$
        timeoutText = BaseWidgetUtils.createText( groupComposite, "30", 1 ); //$NON-NLS-1$
        timeoutText.setTextLimit( 7 );

        String[] encMethods = new String[]
            {
                Messages.getString( "NetworkParameterPage.NoEncryption" ), //$NON-NLS-1$
                Messages.getString( "NetworkParameterPage.UseSSLEncryption" ), //$NON-NLS-1$
                Messages.getString( "NetworkParameterPage.UseStartTLS" ) //$NON-NLS-1$
            };

        BaseWidgetUtils.createLabel( groupComposite, Messages.getString( "NetworkParameterPage.EncryptionMethod" ), 1 ); //$NON-NLS-1$
        encryptionMethodCombo = BaseWidgetUtils.createReadonlyCombo( groupComposite, encMethods, 0, 2 );

        boolean validateCertificates = ConnectionCorePlugin.getDefault().getPluginPreferences().getBoolean(
            ConnectionCoreConstants.PREFERENCE_VALIDATE_CERTIFICATES );

        if ( validateCertificates )
        {
            BaseWidgetUtils.createSpacer( groupComposite, 1 );

            Link link = BaseWidgetUtils.createLink( groupComposite,
                Messages.getString( "NetworkParameterPage.CertificateValidationLink" ), 2 ); //$NON-NLS-1$
            GridData linkGridData = new GridData( GridData.FILL_HORIZONTAL );
            linkGridData.horizontalSpan = 2;
            linkGridData.widthHint = 100;
            link.setLayoutData( linkGridData );
            link.addSelectionListener( linkDataWidgetListener );
        }
        else
        {
            BaseWidgetUtils.createSpacer( groupComposite, 1 );
            BaseWidgetUtils.createLabel( groupComposite, Messages
                .getString( "NetworkParameterPage.WarningCertificateValidation" ), 2 ); //$NON-NLS-1$
        }

        String[] networkProviders = new String[]
            {
                "Apache Directory LDAP Client API",
                "JNDI (Java Naming and Directory Interface)"
            }; //$NON-NLS-1$ //$NON-NLS-2$

        BaseWidgetUtils.createLabel( groupComposite, Messages.getString( "NetworkParameterPage.Provider" ), 1 ); //$NON-NLS-1$
        networkProviderCombo = BaseWidgetUtils.createReadonlyCombo( groupComposite, networkProviders, 0, 2 );
        networkProviderCombo
            .select( ConnectionCorePlugin.getDefault()
                .getDefaultNetworkProvider() == NetworkProvider.APACHE_DIRECTORY_LDAP_API ? 0
                    : 1 );

        BaseWidgetUtils.createSpacer( groupComposite, 2 );
        checkConnectionButton = new Button( groupComposite, SWT.PUSH );
        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.RIGHT;
        gridData.verticalAlignment = SWT.BOTTOM;
        checkConnectionButton.setLayoutData( gridData );
        checkConnectionButton.setText( Messages.getString( "NetworkParameterPage.CheckNetworkParameter" ) ); //$NON-NLS-1$

        readOnlyConnectionCheckbox = BaseWidgetUtils.createCheckbox( composite,
            Messages.getString( "NetworkParameterPage.ReadOnly" ), 1 ); //$NON-NLS-1$

        BaseWidgetUtils.createSpacer( composite, 1 );
        nameText.setFocus();
    }


    /**
     * {@inheritDoc}
     */
    protected void validate()
    {
        // set enabled/disabled state of check connection button
        checkConnectionButton.setEnabled( !hostCombo.getText().equals( StringUtils.EMPTY ) &&
            !portCombo.getText().equals( StringUtils.EMPTY ) ); //$NON-NLS-1$ //$NON-NLS-2$

        // validate input fields
        message = null;
        infoMessage = null;
        errorMessage = null;

        if ( Strings.isEmpty( portCombo.getText() ) ) //$NON-NLS-1$
        {
            message = Messages.getString( "NetworkParameterPage.PleaseEnterPort" ); //$NON-NLS-1$
        }

        if ( Strings.isEmpty( hostCombo.getText() ) ) //$NON-NLS-1$
        {
            message = Messages.getString( "NetworkParameterPage.PleaseEnterHostname" ); //$NON-NLS-1$
        }

        if ( Strings.isEmpty( nameText.getText() ) ) //$NON-NLS-1$
        {
            message = Messages.getString( "NetworkParameterPage.PleaseEnterConnectionName" ); //$NON-NLS-1$
        }

        if ( Strings.isEmpty( timeoutText.getText() ) ) //$NON-NLS-1$
        {
            timeoutText.setText( "30" );
        }

        if ( ConnectionCorePlugin.getDefault().getConnectionManager().getConnectionByName( nameText.getText() ) != null
            && ( ( connectionParameter == null ) || !nameText.getText().equals( connectionParameter.getName() ) ) )
        {
            errorMessage = NLS.bind(
                Messages.getString( "NetworkParameterPage.ConnectionExists" ), new String[] //$NON-NLS-1$
                { nameText.getText() } );
        }
    }


    /**
     * {@inheritDoc}
     */
    protected void loadParameters( ConnectionParameter parameter )
    {
        connectionParameter = parameter;

        nameText.setText( CommonUIUtils.getTextValue( parameter.getName() ) );
        hostCombo.setText( CommonUIUtils.getTextValue( parameter.getHost() ) );
        portCombo.setText( Integer.toString( parameter.getPort() ) );
        int encryptionMethodIndex = 0;

        if ( parameter.getEncryptionMethod() == EncryptionMethod.LDAPS )
        {
            encryptionMethodIndex = 1;
        }
        else if ( parameter.getEncryptionMethod() == EncryptionMethod.START_TLS )
        {
            encryptionMethodIndex = 2;
        }

        encryptionMethodCombo.select( encryptionMethodIndex );
        networkProviderCombo.select( parameter.getNetworkProvider() == NetworkProvider.APACHE_DIRECTORY_LDAP_API ? 0
            : 1 );
        readOnlyConnectionCheckbox.setSelection( parameter.isReadOnly() );
        timeoutText.setText( Long.toString( parameter.getTimeout() / 1000L ) );

    }


    /**
     * {@inheritDoc}
     */
    protected void initListeners()
    {
        nameText.addModifyListener( event -> connectionPageModified() );

        hostCombo.addModifyListener( event -> connectionPageModified() );

        portCombo.addVerifyListener( event -> {
            if ( !event.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
            {
                event.doit = false;
            }
        } );

        portCombo.addModifyListener( event -> connectionPageModified() );

        encryptionMethodCombo.addSelectionListener( new SelectionAdapter()
        {
            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected( SelectionEvent event )
            {
                connectionPageModified();
            }
        } );

        networkProviderCombo.addSelectionListener( new SelectionAdapter()
        {
            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected( SelectionEvent event )
            {
                connectionPageModified();
            }
        } );

        checkConnectionButton.addSelectionListener( new SelectionAdapter()
        {
            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected( SelectionEvent event )
            {
                Connection connection = getTestConnection();
                CheckNetworkParameterRunnable runnable = new CheckNetworkParameterRunnable( connection );
                IStatus status = RunnableContextRunner.execute( runnable, runnableContext, true );

                if ( status.isOK() )
                {
                    MessageDialog.openInformation( Display.getDefault().getActiveShell(), Messages
                        .getString( "NetworkParameterPage.CheckNetworkParameter" ), //$NON-NLS-1$
                        Messages
                            .getString( "NetworkParameterPage.ConnectionEstablished" ) ); //$NON-NLS-1$
                }
            }
        } );

        readOnlyConnectionCheckbox.addSelectionListener( new SelectionAdapter()
        {
            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected( SelectionEvent event )
            {
                connectionPageModified();
            }
        } );

        // The timeout events
        timeoutText.addModifyListener( event -> connectionPageModified() );

        timeoutText.addVerifyListener( event -> {
            if ( !event.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
            {
                event.doit = false;
            }
        } );
    }


    /**
     * {@inheritDoc}
     */
    public void saveParameters( ConnectionParameter parameter )
    {
        parameter.setName( getName() );
        parameter.setHost( getHostName() );
        parameter.setPort( getPort() );
        parameter.setEncryptionMethod( getEncyrptionMethod() );
        parameter.setNetworkProvider( getNetworkProvider() );
        parameter.setReadOnly( isReadOnly() );
        parameter.setTimeout( getTimeout() * 1000L );
    }


    /**
     * {@inheritDoc}
     */
    public void saveDialogSettings()
    {
        IDialogSettings dialogSettings = ConnectionUIPlugin.getDefault().getDialogSettings();
        HistoryUtils.save( dialogSettings, ConnectionUIConstants.DIALOGSETTING_KEY_HOST_HISTORY, hostCombo.getText() );
        HistoryUtils.save( dialogSettings, ConnectionUIConstants.DIALOGSETTING_KEY_PORT_HISTORY, portCombo.getText() );
    }


    /**
     * {@inheritDoc}
     */
    public void setFocus()
    {
        nameText.setFocus();
    }


    /**
     * {@inheritDoc}
     */
    public boolean areParametersModifed()
    {
        return isReconnectionRequired() || !StringUtils.equals( connectionParameter.getName(), getName() );
    }


    /**
     * {@inheritDoc}
     */
    public boolean isReconnectionRequired()
    {
        return ( connectionParameter == null )
            || ( !StringUtils.equals( connectionParameter.getHost(), getHostName() ) )
            || ( connectionParameter.getPort() != getPort() )
            || ( connectionParameter.getEncryptionMethod() != getEncyrptionMethod() )
            || ( connectionParameter.getNetworkProvider() != getNetworkProvider() )
            || ( connectionParameter.isReadOnly() != isReadOnly() )
            || ( connectionParameter.getTimeout() != getTimeout() );
    }


    /**
     * {@inheritDoc}
     */
    public void mergeParametersToLdapURL( ConnectionParameter parameter, LdapUrl ldapUrl )
    {
        ldapUrl.getExtensions().add( new Extension( false, X_CONNECTION_NAME, parameter.getName() ) );
        ldapUrl.setHost( parameter.getHost() );
        ldapUrl.setPort( parameter.getPort() );

        switch ( parameter.getEncryptionMethod() )
        {
            case NONE:
                // default
                break;

            case LDAPS:
                ldapUrl.getExtensions().add( new Extension( false, X_ENCRYPTION, X_ENCRYPTION_LDAPS ) );
                break;

            case START_TLS:
                ldapUrl.getExtensions().add( new Extension( false, X_ENCRYPTION, X_ENCRYPTION_START_TLS ) );
                break;
        }

        if ( parameter.getNetworkProvider() == NetworkProvider.JNDI )
        {
            ldapUrl.getExtensions().add( new Extension( false, X_NETWORK_PROVIDER, X_NETWORK_PROVIDER_JNDI ) );
        }
        else
        {
            ldapUrl.getExtensions().add(
                new Extension( false, X_NETWORK_PROVIDER, X_NETWORK_PROVIDER_APACHE_DIRECTORY_LDAP_API ) );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void mergeLdapUrlToParameters( LdapUrl ldapUrl, ConnectionParameter parameter )
    {
        // connection name, current date if absent
        String name = ldapUrl.getExtensionValue( X_CONNECTION_NAME );

        if ( StringUtils.isEmpty( name ) )
        {
            name = new SimpleDateFormat( "yyyy-MM-dd HH-mm-ss" ).format( new Date() ); //$NON-NLS-1$
        }

        parameter.setName( name );

        // host
        parameter.setHost( ldapUrl.getHost() );

        // port
        parameter.setPort( ldapUrl.getPort() );

        // encryption method, none if unknown or absent 
        String encryption = ldapUrl.getExtensionValue( X_ENCRYPTION );

        if ( StringUtils.isNotEmpty( encryption ) && X_ENCRYPTION_LDAPS.equalsIgnoreCase( encryption ) )
        {
            parameter.setEncryptionMethod( ConnectionParameter.EncryptionMethod.LDAPS );
        }
        else if ( StringUtils.isNotEmpty( encryption ) && X_ENCRYPTION_START_TLS.equalsIgnoreCase( encryption ) )
        {
            parameter.setEncryptionMethod( ConnectionParameter.EncryptionMethod.START_TLS );
        }
        else
        {
            parameter.setEncryptionMethod( ConnectionParameter.EncryptionMethod.NONE );
        }

        // encryption method, none if unknown or absent 
        String networkProvider = ldapUrl.getExtensionValue( X_NETWORK_PROVIDER );

        if ( StringUtils.isNotEmpty( networkProvider )
            && X_NETWORK_PROVIDER_APACHE_DIRECTORY_LDAP_API.equalsIgnoreCase( networkProvider ) )
        {
            parameter.setNetworkProvider( ConnectionParameter.NetworkProvider.APACHE_DIRECTORY_LDAP_API );
        }
        else if ( StringUtils.isNotEmpty( networkProvider )
            && X_NETWORK_PROVIDER_JNDI.equalsIgnoreCase( networkProvider ) )
        {
            parameter.setNetworkProvider( ConnectionParameter.NetworkProvider.JNDI );
        }
        else
        {
            parameter.setNetworkProvider( ConnectionCorePlugin.getDefault().getDefaultNetworkProvider() );
        }
    }
}
