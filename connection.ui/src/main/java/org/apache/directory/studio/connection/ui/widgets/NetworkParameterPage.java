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
import org.apache.directory.shared.ldap.util.LdapURL;
import org.apache.directory.shared.ldap.util.LdapURL.Extension;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCoreConstants;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.apache.directory.studio.connection.core.ConnectionParameter.EncryptionMethod;
import org.apache.directory.studio.connection.core.jobs.CheckNetworkParameterRunnable;
import org.apache.directory.studio.connection.ui.AbstractConnectionParameterPage;
import org.apache.directory.studio.connection.ui.ConnectionUIConstants;
import org.apache.directory.studio.connection.ui.RunnableContextRunner;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;


/**
 * The NetworkParameterPage is used the edit the network parameters of a
 * connection.
 *  
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class NetworkParameterPage extends AbstractConnectionParameterPage
{

    private static final String X_CONNECTION_NAME = "X-CONNECTION-NAME"; //$NON-NLS-1$

    private static final String X_ENCRYPTION = "X-ENCRYPTION"; //$NON-NLS-1$

    private static final String X_ENCRYPTION_LDAPS = "ldaps"; //$NON-NLS-1$

    private static final String X_ENCRYPTION_START_TLS = "StartTLS"; //$NON-NLS-1$

    /** The connection name text widget */
    private Text nameText;

    /** The host name combo with the history of recently used host names */
    private Combo hostCombo;

    /** The host combo with the history of recently used ports */
    private Combo portCombo;

    /** The combo to select the encryption method */
    private Combo encryptionMethodCombo;

    /** The button to check the connection parameters */
    private Button checkConnectionButton;


    /**
     * Creates a new instance of NetworkParameterPage.
     */
    public NetworkParameterPage()
    {
    }


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
     * Gets a temporary connection with all conection parameter 
     * entered in this page. 
     *
     * @return a test connection
     */
    private Connection getTestConnection()
    {
        ConnectionParameter cp = new ConnectionParameter( null, getHostName(), getPort(), getEncyrptionMethod(),
            ConnectionParameter.AuthenticationMethod.NONE, null, null, null, true, null );
        Connection conn = new Connection( cp );
        return conn;
    }


    /**
     * {@inheritDoc}
     */
    protected void createComposite( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        Composite nameComposite = BaseWidgetUtils.createColumnContainer( composite, 2, 1 );
        BaseWidgetUtils.createLabel( nameComposite, Messages.getString( "NetworkParameterPage.ConnectionName" ), 1 ); //$NON-NLS-1$
        nameText = BaseWidgetUtils.createText( nameComposite, "", 1 ); //$NON-NLS-1$

        BaseWidgetUtils.createSpacer( composite, 1 );

        Group group = BaseWidgetUtils.createGroup( composite, Messages
            .getString( "NetworkParameterPage.NetworkParameter" ), 1 ); //$NON-NLS-1$

        Composite groupComposite = BaseWidgetUtils.createColumnContainer( group, 3, 1 );
        BaseWidgetUtils.createLabel( groupComposite, Messages.getString( "NetworkParameterPage.HostName" ), 1 ); //$NON-NLS-1$
        String[] hostHistory = HistoryUtils.load( ConnectionUIConstants.DIALOGSETTING_KEY_HOST_HISTORY );
        hostCombo = BaseWidgetUtils.createCombo( groupComposite, hostHistory, -1, 2 );

        BaseWidgetUtils.createLabel( groupComposite, Messages.getString( "NetworkParameterPage.Port" ), 1 ); //$NON-NLS-1$
        String[] portHistory = HistoryUtils.load( ConnectionUIConstants.DIALOGSETTING_KEY_PORT_HISTORY );
        portCombo = BaseWidgetUtils.createCombo( groupComposite, portHistory, -1, 2 );
        portCombo.setTextLimit( 5 );
        portCombo.setText( "389" ); //$NON-NLS-1$

        String[] encMethods = new String[]
            { Messages.getString( "NetworkParameterPage.NoEncryption" ), //$NON-NLS-1$
                Messages.getString( "NetworkParameterPage.UseSSLEncryption" ), //$NON-NLS-1$
                Messages.getString( "NetworkParameterPage.UseStartTLS" ) }; //$NON-NLS-1$
        int index = 0;
        BaseWidgetUtils.createLabel( groupComposite, Messages.getString( "NetworkParameterPage.EncryptionMethod" ), 1 ); //$NON-NLS-1$
        encryptionMethodCombo = BaseWidgetUtils.createReadonlyCombo( groupComposite, encMethods, index, 2 );

        boolean validateCertificates = ConnectionCorePlugin.getDefault().getPluginPreferences().getBoolean(
            ConnectionCoreConstants.PREFERENCE_VALIDATE_CERTIFICATES );
        if ( !validateCertificates )
        {
            BaseWidgetUtils.createSpacer( groupComposite, 1 );
            BaseWidgetUtils.createLabel( groupComposite, Messages
                .getString( "NetworkParameterPage.WarningCertificateValidation" ), 2 ); //$NON-NLS-1$
        }

        BaseWidgetUtils.createSpacer( groupComposite, 2 );
        checkConnectionButton = new Button( groupComposite, SWT.PUSH );
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.RIGHT;
        gd.verticalAlignment = SWT.BOTTOM;
        checkConnectionButton.setLayoutData( gd );
        checkConnectionButton.setText( Messages.getString( "NetworkParameterPage.CheckNetworkParameter" ) ); //$NON-NLS-1$

        nameText.setFocus();
    }


    /**
     * {@inheritDoc}
     */
    protected void validate()
    {
        // set enabled/disabled state of check connection button
        checkConnectionButton.setEnabled( !hostCombo.getText().equals( "" ) && !portCombo.getText().equals( "" ) ); //$NON-NLS-1$ //$NON-NLS-2$

        // validate input fields
        message = null;
        infoMessage = null;
        errorMessage = null;
        if ( "".equals( portCombo.getText() ) ) //$NON-NLS-1$
        {
            message = Messages.getString( "NetworkParameterPage.PleaseEnterPort" ); //$NON-NLS-1$
        }
        if ( "".equals( hostCombo.getText() ) ) //$NON-NLS-1$
        {
            message = Messages.getString( "NetworkParameterPage.PleaseEnterHostname" ); //$NON-NLS-1$
        }
        if ( "".equals( nameText.getText() ) ) //$NON-NLS-1$
        {
            message = Messages.getString( "NetworkParameterPage.PleaseEnterConnectionName" ); //$NON-NLS-1$
        }
        if ( ConnectionCorePlugin.getDefault().getConnectionManager().getConnectionByName( nameText.getText() ) != null
            && ( connectionParameter == null || !nameText.getText().equals( connectionParameter.getName() ) ) )
        {
            errorMessage = NLS.bind(
                Messages.getString( "NetworkParameterPage.ConnectionExists" ), new String[] { nameText.getText() } ); //$NON-NLS-1$
        }
    }


    /**
     * {@inheritDoc}
     */
    protected void loadParameters( ConnectionParameter parameter )
    {
        connectionParameter = parameter;

        nameText.setText( parameter.getName() );
        hostCombo.setText( parameter.getHost() );
        portCombo.setText( Integer.toString( parameter.getPort() ) );
        int index = parameter.getEncryptionMethod() == EncryptionMethod.LDAPS ? 1
            : parameter.getEncryptionMethod() == EncryptionMethod.START_TLS ? 2 : 0;
        encryptionMethodCombo.select( index );
    }


    /**
     * {@inheritDoc}
     */
    protected void initListeners()
    {
        nameText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent event )
            {
                connectionPageModified();
            }
        } );

        hostCombo.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent event )
            {
                connectionPageModified();
            }
        } );

        portCombo.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent event )
            {
                if ( !event.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
                {
                    event.doit = false;
                }
            }
        } );
        portCombo.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent event )
            {
                connectionPageModified();
            }
        } );

        encryptionMethodCombo.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                connectionPageModified();
            }
        } );

        checkConnectionButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                Connection connection = getTestConnection();
                CheckNetworkParameterRunnable runnable = new CheckNetworkParameterRunnable( connection );
                IStatus status = RunnableContextRunner.execute( runnable, runnableContext, true );
                if ( status.isOK() )
                {
                    MessageDialog.openInformation( Display.getDefault().getActiveShell(), Messages
                        .getString( "NetworkParameterPage.CheckNetworkParameter" ), Messages //$NON-NLS-1$
                        .getString( "NetworkParameterPage.ConnectionEstablished" ) ); //$NON-NLS-1$
                }
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
    }


    /**
     * {@inheritDoc}
     */
    public void saveDialogSettings()
    {
        HistoryUtils.save( ConnectionUIConstants.DIALOGSETTING_KEY_HOST_HISTORY, hostCombo.getText() );
        HistoryUtils.save( ConnectionUIConstants.DIALOGSETTING_KEY_PORT_HISTORY, portCombo.getText() );
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
        return connectionParameter == null || !StringUtils.equals( connectionParameter.getHost(), getHostName() )
            || connectionParameter.getPort() != getPort()
            || connectionParameter.getEncryptionMethod() != getEncyrptionMethod();
    }


    /**
     * {@inheritDoc}
     */
    public void mergeParametersToLdapURL( ConnectionParameter parameter, LdapURL ldapUrl )
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
    }


    /**
     * {@inheritDoc}
     */
    public void mergeLdapUrlToParameters( LdapURL ldapUrl, ConnectionParameter parameter )
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
    }
}
