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

package org.apache.directory.ldapstudio.browser.ui.widgets.connection;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.ldapstudio.browser.core.BrowserCorePlugin;
import org.apache.directory.ldapstudio.browser.core.internal.model.Connection;
import org.apache.directory.ldapstudio.browser.core.jobs.CheckBindJob;
import org.apache.directory.ldapstudio.browser.core.jobs.CheckNetworkParameterJob;
import org.apache.directory.ldapstudio.browser.core.jobs.FetchBaseDNsJob;
import org.apache.directory.ldapstudio.browser.core.model.DN;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.NameException;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.jobs.RunnableContextJobAdapter;
import org.apache.directory.ldapstudio.browser.ui.widgets.BaseWidgetUtils;
import org.apache.directory.ldapstudio.browser.ui.widgets.HistoryUtils;
import org.apache.directory.ldapstudio.browser.ui.widgets.search.AliasesDereferencingWidget;
import org.apache.directory.ldapstudio.browser.ui.widgets.search.LimitWidget;
import org.apache.directory.ldapstudio.browser.ui.widgets.search.ReferralsHandlingWidget;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;


public class ConnectionPageWrapper implements ModifyListener, SelectionListener
{

    private Text nameText;

    private Combo hostCombo;

    private Text hostText;

    private Combo portCombo;

    private Text portText;

    private Combo encryptionMethodCombo;

    private Button checkConnectionButton;

    private Button autoFetchBaseDnsButton;

    private Button fetchBaseDnsButton;

    private Combo baseDNCombo;

    private LimitWidget limitWidget;

    private AliasesDereferencingWidget aliasesDereferencingWidget;

    private ReferralsHandlingWidget referralsHandlingWidget;

    private Button openConnectionButton;

    private Button anonymousAuthButton;

    private Button simpleAuthButton;

    private Combo simpleAuthBindPrincipalCombo;

    private Text simpleAuthBindPrincipalText;

    private Text simpleAuthBindPasswordText;

    private Button saveSimpleAuthBindPasswordButton;

    private Button checkSimpleAuthButton;

    private List listenerList;

    private boolean isConnectionOpened;

    private IRunnableContext runnableContext;


    public ConnectionPageWrapper( ConnectionPageModifyListener listener, IRunnableContext runnableContext )
    {
        this.listenerList = new ArrayList( 5 );
        this.listenerList.add( listener );
        this.isConnectionOpened = listener.getRealConnection() != null && listener.getRealConnection().isOpened();
        this.runnableContext = runnableContext;
    }


    public void addConnectionPageModifyListener( ConnectionPageModifyListener listener )
    {
        this.listenerList.add( listener );
    }


    public String getName()
    {
        return nameText.getText();
    }


    public String getHostName()
    {
        return hostCombo != null ? hostCombo.getText() : hostText.getText();
    }


    public int getPort()
    {
        return Integer.parseInt( portCombo != null ? portCombo.getText() : portText.getText() );
    }


    public int getEncyrptionMethod()
    {
        if ( this.encryptionMethodCombo != null )
        {
            switch ( this.encryptionMethodCombo.getSelectionIndex() )
            {
                case 1:
                    return IConnection.ENCYRPTION_LDAPS;
                case 2:
                    return IConnection.ENCYRPTION_STARTTLS;
                default:
                    return IConnection.ENCYRPTION_NONE;
            }
        }
        return IConnection.ENCYRPTION_NONE;
    }


    public boolean isAutoFetchBaseDns()
    {
        return autoFetchBaseDnsButton.getSelection();
    }


    public String getBaseDN()
    {
        return baseDNCombo.getText();
    }


    public int getCountLimit()
    {
        return limitWidget.getCountLimit();
    }


    public int getTimeLimit()
    {
        return limitWidget.getTimeLimit();
    }


    public int getAliasesDereferencingMethod()
    {
        return aliasesDereferencingWidget.getAliasesDereferencingMethod();
    }


    public int getReferralsHandlingMethod()
    {
        return referralsHandlingWidget.getReferralsHandlingMethod();
    }


    public void setOpenConnectionOnFinish( boolean b )
    {
        if ( openConnectionButton != null )
        {
            openConnectionButton.setSelection( b );
        }
    }


    public boolean isOpenConnectionOnFinish()
    {
        return openConnectionButton.getSelection();
    }


    public int getAuthenticationMethod()
    {
        if ( this.anonymousAuthButton.getSelection() )
        {
            return IConnection.AUTH_ANONYMOUS;
        }
        else if ( this.simpleAuthButton.getSelection() )
        {
            return IConnection.AUTH_SIMPLE;
        }

        return IConnection.AUTH_ANONYMOUS;
    }


    public String getSimpleAuthBindDN()
    {
        return simpleAuthBindPrincipalCombo != null ? simpleAuthBindPrincipalCombo.getText()
            : simpleAuthBindPrincipalText.getText();
    }


    public String getSimpleAuthBindPassword()
    {
        return simpleAuthBindPasswordText.getText();
    }


    public boolean isSaveSimpleAuthBindPassword()
    {
        return saveSimpleAuthBindPasswordButton.getSelection();
    }


    public void addMainInput( String name, String host, int port, int encryptionMethod, Composite parent )
    {

        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        Composite nameComposite = BaseWidgetUtils.createColumnContainer( composite, 2, 1 );
        BaseWidgetUtils.createLabel( nameComposite, "Connection name:", 1 );
        nameText = BaseWidgetUtils.createText( nameComposite, name, 1 );
        nameText.addModifyListener( this );

        BaseWidgetUtils.createSpacer( composite, 1 );

        Group group = BaseWidgetUtils.createGroup( composite, "Network Parameter", 1 );

        Composite groupComposite = BaseWidgetUtils.createColumnContainer( group, 3, 1 );
        BaseWidgetUtils.createLabel( groupComposite, "Hostname:", 1 );
        if ( isConnectionOpened )
        {
            hostText = BaseWidgetUtils.createReadonlyText( groupComposite, host, 2 );
        }
        else
        {
            String[] hostHistory = HistoryUtils.load( BrowserUIConstants.DIALOGSETTING_KEY_HOST_HISTORY );
            hostCombo = BaseWidgetUtils.createCombo( groupComposite, hostHistory, -1, 2 );
            hostCombo.setText( host );
            hostCombo.addModifyListener( this );
        }

        BaseWidgetUtils.createLabel( groupComposite, "Port:", 1 );
        if ( isConnectionOpened )
        {
            portText = BaseWidgetUtils.createReadonlyText( groupComposite, Integer.toString( port ), 2 );
        }
        else
        {
            String[] portHistory = HistoryUtils.load( BrowserUIConstants.DIALOGSETTING_KEY_PORT_HISTORY );
            portCombo = BaseWidgetUtils.createCombo( groupComposite, portHistory, -1, 2 );
            portCombo.setText( Integer.toString( port ) );
            portCombo.addVerifyListener( new VerifyListener()
            {
                public void verifyText( VerifyEvent e )
                {
                    if ( !e.text.matches( "[0-9]*" ) )
                    {
                        e.doit = false;
                    }
                    if ( portCombo.getText().length() > 4 && e.text.length() > 0 )
                    {
                        e.doit = false;
                    }
                }
            } );
            portCombo.addModifyListener( this );
        }

        String[] encMethods = new String[]
            { "No encryption", "Use SSL encryption (ldaps://)", "Use StartTLS extension" };
        BaseWidgetUtils.createLabel( groupComposite, "Encryption method:", 1 );
        this.encryptionMethodCombo = BaseWidgetUtils.createReadonlyCombo( groupComposite, encMethods, encryptionMethod,
            2 );
        this.encryptionMethodCombo.addSelectionListener( this );
        BaseWidgetUtils.createSpacer( groupComposite, 1 );
        BaseWidgetUtils
            .createLabel(
                groupComposite,
                "Warning: The current version doesn't support certificate validation, \nbe aware of invalid certificates or man-in-the-middle attacks!",
                2 );

        BaseWidgetUtils.createSpacer( groupComposite, 2 );
        checkConnectionButton = new Button( groupComposite, SWT.PUSH );
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.RIGHT;
        gd.verticalAlignment = SWT.BOTTOM;
        checkConnectionButton.setLayoutData( gd );
        checkConnectionButton.setText( "Check Network Parameter" );
        checkConnectionButton.setEnabled( !isConnectionOpened );
        checkConnectionButton.addSelectionListener( new SelectionListener()
        {
            public void widgetSelected( SelectionEvent e )
            {
                IConnection connection = getTestConnection();
                CheckNetworkParameterJob job = new CheckNetworkParameterJob( connection );
                RunnableContextJobAdapter.execute( job, runnableContext );
                if ( job.getExternalResult().isOK() )
                {
                    MessageDialog.openInformation( Display.getDefault().getActiveShell(), "Check Network Parameter",
                        "The connection was established successfully." );
                }
            }


            public void widgetDefaultSelected( SelectionEvent e )
            {
            }
        } );

        this.setEnabled();
    }


    public void addBaseDNInput( boolean autoFetchBaseDNs, String baseDN, Composite parent )
    {

        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        Group group = BaseWidgetUtils.createGroup( composite, "Base DN", 1 );
        Composite groupComposite = BaseWidgetUtils.createColumnContainer( group, 3, 1 );
        GridData gd;

        autoFetchBaseDnsButton = BaseWidgetUtils.createCheckbox( groupComposite, "Get base DNs from Root DSE", 2 );
        autoFetchBaseDnsButton.setSelection( autoFetchBaseDNs );
        autoFetchBaseDnsButton.addSelectionListener( this );

        fetchBaseDnsButton = new Button( groupComposite, SWT.PUSH );
        fetchBaseDnsButton.setText( "Fetch Base DNs" );
        fetchBaseDnsButton.setEnabled( true );
        gd = new GridData();
        gd.horizontalAlignment = SWT.RIGHT;
        fetchBaseDnsButton.setLayoutData( gd );
        fetchBaseDnsButton.addSelectionListener( new SelectionListener()
        {

            public void widgetSelected( SelectionEvent e )
            {
                IConnection connection = getTestConnection();

                FetchBaseDNsJob job = new FetchBaseDNsJob( connection );
                RunnableContextJobAdapter.execute( job, runnableContext );
                if ( job.getExternalResult().isOK() )
                {
                    if ( job.getBaseDNs().length > 0 )
                    {
                        String[] baseDNs = job.getBaseDNs();
                        baseDNCombo.setItems( baseDNs );
                        baseDNCombo.select( 0 );

                        String msg = "The server returned the following base DNs:";
                        for ( int i = 0; i < baseDNs.length; i++ )
                        {
                            msg += "\n  - " + baseDNs[i];
                        }
                        MessageDialog.openInformation( Display.getDefault().getActiveShell(), "Fetch Base DNs", msg );
                    }
                    else
                    {
                        MessageDialog.openWarning( Display.getDefault().getActiveShell(), "Fetch Base DNs",
                            "No base DN returned from server. Please enter the base DN manually." );
                        autoFetchBaseDnsButton.setSelection( false );
                    }
                }
            }


            public void widgetDefaultSelected( SelectionEvent e )
            {
            }
        } );

        BaseWidgetUtils.createLabel( groupComposite, "Base DN:", 1 );
        baseDNCombo = BaseWidgetUtils.createCombo( groupComposite, new String[]
            { baseDN.toString() }, 0, 2 );
        baseDNCombo.setText( baseDN.toString() );
        baseDNCombo.addModifyListener( this );

        this.setEnabled();
    }


    public void addLimitInput( int countLimit, int timeLimit, int aliasesDereferencingMethod,
        int referralsHandlingMethod, Composite parent )
    {

        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 3, 1 );

        limitWidget = new LimitWidget( countLimit, timeLimit );
        limitWidget.createWidget( composite );

        aliasesDereferencingWidget = new AliasesDereferencingWidget( aliasesDereferencingMethod );
        aliasesDereferencingWidget.createWidget( composite );

        referralsHandlingWidget = new ReferralsHandlingWidget( referralsHandlingMethod );
        referralsHandlingWidget.createWidget( composite );

        this.setEnabled();
    }


    public void addOpenConnectionInput( boolean openConnectionOnFinish, Composite parent )
    {
        openConnectionButton = BaseWidgetUtils.createCheckbox( parent, "Open connection on finish", 1 );
        openConnectionButton.setSelection( openConnectionOnFinish );
        openConnectionButton.addSelectionListener( this );
    }


    public void addAuthenticationMethodInput( int authMethod, Composite parent )
    {

        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        Group group = BaseWidgetUtils.createGroup( composite, "Authentication Method", 1 );
        Composite groupComposite = BaseWidgetUtils.createColumnContainer( group, 1, 1 );

        anonymousAuthButton = BaseWidgetUtils.createRadiobutton( groupComposite, "Anonymous Authentication", 1 );
        anonymousAuthButton.setSelection( authMethod == IConnection.AUTH_ANONYMOUS );
        anonymousAuthButton.addSelectionListener( this );

        simpleAuthButton = BaseWidgetUtils.createRadiobutton( groupComposite, "Simple Authentication", 1 );
        simpleAuthButton.setSelection( authMethod == IConnection.AUTH_SIMPLE );
        simpleAuthButton.addSelectionListener( this );

        // saslAuthButton = new Button(authenticationMethodGroup, SWT.RADIO);
        // saslAuthButton.setText("SASL Authentication");
        // saslAuthButton.setSelection(authMethod ==
        // ConnectionParameter.AUTH_SASL);
        // saslAuthButton.addSelectionListener(this);
    }


    public void addSimpleAuthInput( boolean saveBindPassword, String bindPrincipal, String bindPassword,
        Composite parent )
    {

        Composite composite2 = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        Group group = BaseWidgetUtils.createGroup( composite2, "Authentication Parameter", 1 );
        Composite composite = BaseWidgetUtils.createColumnContainer( group, 3, 1 );

        BaseWidgetUtils.createLabel( composite, "Bind DN or user:", 1 );
        if ( isConnectionOpened )
        {
            simpleAuthBindPrincipalText = BaseWidgetUtils.createReadonlyText( composite, bindPrincipal, 2 );
        }
        else
        {
            String[] dnHistory = HistoryUtils.load( BrowserUIConstants.DIALOGSETTING_KEY_DN_HISTORY );
            simpleAuthBindPrincipalCombo = BaseWidgetUtils.createCombo( composite, dnHistory, -1, 2 );
            simpleAuthBindPrincipalCombo.setText( bindPrincipal );
            simpleAuthBindPrincipalCombo.addModifyListener( this );
        }

        BaseWidgetUtils.createLabel( composite, "Bind password:", 1 );
        if ( isConnectionOpened )
        {
            simpleAuthBindPasswordText = BaseWidgetUtils.createReadonlyPasswordText( composite, bindPassword, 2 );
        }
        else
        {
            simpleAuthBindPasswordText = BaseWidgetUtils.createPasswordText( composite, bindPassword, 2 );
        }
        simpleAuthBindPasswordText.addModifyListener( this );

        BaseWidgetUtils.createSpacer( composite, 1 );
        saveSimpleAuthBindPasswordButton = BaseWidgetUtils.createCheckbox( composite, "Save password", 1 );
        saveSimpleAuthBindPasswordButton.setSelection( saveBindPassword );
        saveSimpleAuthBindPasswordButton.addSelectionListener( this );

        checkSimpleAuthButton = new Button( composite, SWT.PUSH );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalAlignment = SWT.RIGHT;
        checkSimpleAuthButton.setLayoutData( gd );
        checkSimpleAuthButton.setText( "Check Authentication" );
        checkSimpleAuthButton.setEnabled( false );
        checkSimpleAuthButton.addSelectionListener( new SelectionListener()
        {
            public void widgetSelected( SelectionEvent e )
            {
                IConnection connection = getTestConnection();
                CheckBindJob job = new CheckBindJob( connection );
                RunnableContextJobAdapter.execute( job, runnableContext );
                if ( job.getExternalResult().isOK() )
                {
                    MessageDialog.openInformation( Display.getDefault().getActiveShell(), "Check Authentication",
                        "The authentication was successful." );
                }
            }


            public void widgetDefaultSelected( SelectionEvent e )
            {
            }
        } );
        this.setEnabled();
    }


    private void fireConnectionPageModified()
    {
        for ( Iterator it = listenerList.iterator(); it.hasNext(); )
        {
            ( ( ConnectionPageModifyListener ) it.next() ).connectionPageModified();
        }
    }


    private void setEnabled()
    {

        if ( isConnectionOpened )
        {
            if ( this.encryptionMethodCombo != null && this.checkConnectionButton != null )
            {
                this.encryptionMethodCombo.setEnabled( false );
                this.checkConnectionButton.setEnabled( false );
            }

            if ( this.baseDNCombo != null && this.autoFetchBaseDnsButton != null )
            {
                this.autoFetchBaseDnsButton.setEnabled( false );
                this.baseDNCombo.setEnabled( false );
                this.fetchBaseDnsButton.setEnabled( false );
            }

            if ( this.anonymousAuthButton != null && this.simpleAuthButton != null )
            {
                this.anonymousAuthButton.setEnabled( false );
                this.simpleAuthButton.setEnabled( false );
            }
            if ( this.saveSimpleAuthBindPasswordButton != null && this.saveSimpleAuthBindPasswordButton != null )
            {
                this.saveSimpleAuthBindPasswordButton.setEnabled( false );
                this.checkSimpleAuthButton.setEnabled( false );
            }
        }
        else
        {
            if ( this.hostCombo != null && this.portCombo != null && this.checkConnectionButton != null )
            {
                if ( !this.hostCombo.getText().equals( "" ) && !this.hostCombo.getText().equals( "" ) )
                {
                    this.checkConnectionButton.setEnabled( true );
                }
                else
                {
                    this.checkConnectionButton.setEnabled( false );
                }
            }

            if ( this.baseDNCombo != null && this.autoFetchBaseDnsButton != null )
            {
                if ( autoFetchBaseDnsButton.getSelection() )
                {
                    this.baseDNCombo.setEnabled( false );
                }
                else
                {
                    this.baseDNCombo.setEnabled( true );
                }
            }
            if ( this.simpleAuthBindPrincipalCombo != null && this.simpleAuthBindPasswordText != null
                && this.saveSimpleAuthBindPasswordButton != null )
            {
                boolean simpleAuthSelected = simpleAuthButton == null || simpleAuthButton.getSelection();
                simpleAuthBindPrincipalCombo.setEnabled( simpleAuthSelected );
                simpleAuthBindPasswordText.setEnabled( saveSimpleAuthBindPasswordButton.getSelection()
                    && simpleAuthSelected );
                saveSimpleAuthBindPasswordButton.setEnabled( simpleAuthSelected );
                // try {
                // new DN(simpleAuthBindPrincipalCombo.getText());
                checkSimpleAuthButton.setEnabled( saveSimpleAuthBindPasswordButton.getSelection()
                    && !simpleAuthBindPrincipalCombo.getText().equals( "" )
                    && !simpleAuthBindPasswordText.getText().equals( "" ) && simpleAuthSelected );
                // }
                // catch (NameException e) {
                // checkSimpleAuthButton.setEnabled(false);
                // }
            }
        }
    }


    private void validate()
    {
        String message = null;
        String errorMessage = null;

        boolean simpleAuthSelected = simpleAuthButton == null || simpleAuthButton.getSelection();

        if ( baseDNCombo != null && baseDNCombo.isVisible() )
        {
            if ( !autoFetchBaseDnsButton.getSelection() && "".equals( baseDNCombo.getText() ) )
            {
                message = "Please enter a base DN. You can use the 'Fetch base DN' button to fetch valid base DNs from directory.";
            }
            else
            {
                try
                {
                    /* DN baseDn = */new DN( baseDNCombo.getText() );
                }
                catch ( NameException e )
                {
                    message = "Please enter a valid base DN.";
                }
            }
        }
        if ( simpleAuthBindPasswordText != null && simpleAuthSelected && simpleAuthBindPasswordText.isVisible() )
        {
            if ( saveSimpleAuthBindPasswordButton.getSelection() && "".equals( simpleAuthBindPasswordText.getText() ) )
            {
                message = "Please enter a bind password.";
            }
        }
        if ( simpleAuthBindPrincipalCombo != null && simpleAuthSelected && simpleAuthBindPrincipalCombo.isVisible() )
        {
            if ( "".equals( simpleAuthBindPrincipalCombo.getText() ) )
            {
                message = "Please enter a bind DN or user.";
            }
            else
            {
                // try {
                // new DN(simpleAuthBindPrincipalCombo.getText());
                // }
                // catch (NameException e) {
                // message = "Please enter a valid bind DN.";
                // }
            }

        }
        if ( portCombo != null && portCombo.isVisible() )
        {
            if ( "".equals( portCombo.getText() ) )
            {
                message = "Please enter a port. The default LDAP port is 389.";
            }
        }
        if ( hostCombo != null && hostCombo.isVisible() )
        {
            if ( "".equals( hostCombo.getText() ) )
            {
                message = "Please enter a hostname.";
            }
        }
        if ( nameText != null && nameText.isVisible() )
        {
            if ( "".equals( nameText.getText() ) )
            {
                message = "Please enter a connection name.";
            }
            if ( BrowserCorePlugin.getDefault().getConnectionManager().getConnection( nameText.getText() ) != null
                && BrowserCorePlugin.getDefault().getConnectionManager().getConnection( nameText.getText() ) != ( ( ConnectionPageModifyListener ) listenerList
                    .get( 0 ) ).getRealConnection() )
            {
                errorMessage = "A connection named '" + nameText.getText() + "' already exists.";
            }
        }

        for ( Iterator it = listenerList.iterator(); it.hasNext(); )
        {
            ConnectionPageModifyListener listener = ( ConnectionPageModifyListener ) it.next();
            listener.setMessage( message );
            listener.setErrorMessage( errorMessage );
        }
    }


    public void modifyText( ModifyEvent e )
    {
        this.setEnabled();
        this.validate();
        this.fireConnectionPageModified();
    }


    public void widgetSelected( SelectionEvent e )
    {
        this.setEnabled();
        this.validate();
        this.fireConnectionPageModified();
    }


    public void widgetDefaultSelected( SelectionEvent e )
    {
        this.setEnabled();
        this.validate();
        this.fireConnectionPageModified();
    }


    public IConnection getTestConnection()
    {
        if ( getAuthenticationMethod() == IConnection.AUTH_ANONYMOUS )
        {
            Connection conn;
            try
            {
                conn = new Connection( null, getHostName(), getPort(), getEncyrptionMethod(), isAutoFetchBaseDns(),
                    new DN( getBaseDN() ), getCountLimit(), getTimeLimit(), getAliasesDereferencingMethod(),
                    getReferralsHandlingMethod(), IConnection.AUTH_ANONYMOUS, null, null );
            }
            catch ( NameException e )
            {
                conn = null;
            }
            return conn;
        }
        else if ( getAuthenticationMethod() == IConnection.AUTH_SIMPLE )
        {
            Connection conn;
            try
            {
                conn = new Connection( null, getHostName(), getPort(), getEncyrptionMethod(), isAutoFetchBaseDns(),
                    new DN( getBaseDN() ), getCountLimit(), getTimeLimit(), getAliasesDereferencingMethod(),
                    getReferralsHandlingMethod(), IConnection.AUTH_SIMPLE, getSimpleAuthBindDN(),
                    getSimpleAuthBindPassword() );
            }
            catch ( NameException e )
            {
                conn = null;
            }
            return conn;
        }
        else
        {
            return null;
        }
    }


    public void saveDialogSettings()
    {
        if ( !isConnectionOpened )
        {
            if ( this.hostCombo != null )
            {
                HistoryUtils.save( BrowserUIConstants.DIALOGSETTING_KEY_HOST_HISTORY, this.hostCombo.getText() );
            }
            if ( this.portCombo != null )
            {
                HistoryUtils.save( BrowserUIConstants.DIALOGSETTING_KEY_PORT_HISTORY, this.portCombo.getText() );
            }
            if ( this.simpleAuthBindPrincipalCombo != null )
            {
                HistoryUtils.save( BrowserUIConstants.DIALOGSETTING_KEY_DN_HISTORY, this.simpleAuthBindPrincipalCombo
                    .getText() );
            }
        }
    }

}
