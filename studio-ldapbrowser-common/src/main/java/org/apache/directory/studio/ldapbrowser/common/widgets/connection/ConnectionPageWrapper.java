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

package org.apache.directory.studio.ldapbrowser.common.widgets.connection;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.common.jobs.RunnableContextJobAdapter;
import org.apache.directory.studio.ldapbrowser.common.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.HistoryUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.AliasesDereferencingWidget;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.LimitWidget;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.ReferralsHandlingWidget;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.internal.model.Connection;
import org.apache.directory.studio.ldapbrowser.core.jobs.CheckBindJob;
import org.apache.directory.studio.ldapbrowser.core.jobs.CheckNetworkParameterJob;
import org.apache.directory.studio.ldapbrowser.core.jobs.FetchBaseDNsJob;
import org.apache.directory.studio.ldapbrowser.core.model.DN;
import org.apache.directory.studio.ldapbrowser.core.model.IConnection;
import org.apache.directory.studio.ldapbrowser.core.model.NameException;
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


/**
 * The ConnectionPageWrapper is a wrapper for all UI widgets needed for the
 * connection configuration. It is used by the new connection wizard as well
 * as the connection property page. So all widgets and functionality is 
 * implemented only once in this wrapper.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ConnectionPageWrapper implements ModifyListener, SelectionListener
{

    /** The connection name text widget */
    private Text nameText;

    /** The host name combo with the history of recently used host names */
    private Combo hostCombo;

    /** The host name text widget */
    private Text hostText;

    /** The host combo with the history of recently used ports */
    private Combo portCombo;

    /** The port text widget */
    private Text portText;

    /** The combo to select the encryption method */
    private Combo encryptionMethodCombo;

    /** The button to check the connection parameters */
    private Button checkConnectionButton;

    /** The checkbox to fetch the base DN's from namingContexts whenever opening the connection */
    private Button autoFetchBaseDnsButton;

    /** The button to fetch the base DN's from namingContexts attribute */
    private Button fetchBaseDnsButton;

    /** The combo that displays the fetched base DN's */
    private Combo baseDNCombo;

    /** The widget with the count and time limits */
    private LimitWidget limitWidget;

    /** The widget to select the alias dereferencing method */
    private AliasesDereferencingWidget aliasesDereferencingWidget;

    /** The widget to select the referrals handling method */
    private ReferralsHandlingWidget referralsHandlingWidget;

    /** The checkbox to choose wether the connection should be opened when finishing the wizard */
    private Button openConnectionButton;
    
    /** The combo to select the authentication method */
    private Combo authenticationMethodCombo;

    /** The bind user combo with the history of recently used bind users */
    private Combo simpleAuthBindPrincipalCombo;

    /** The text widget with the bind user */
    private Text simpleAuthBindPrincipalText;

    /** The text widget to input bind password */
    private Text simpleAuthBindPasswordText;

    /** The checkbox to choose if the bind password should be saved on disk */
    private Button saveSimpleAuthBindPasswordButton;

    /** The button to check the authentication parameters */
    private Button checkPrincipalPasswordAuthButton;

    /** The list of listerns that are interested in modifications in this page */
    private List<ConnectionPageModifyListener> listenerList;

    /** 
     * This flag indicats if the connection is opened. It is used to determin wether to render
     * the combos or just simple text widgets.
     */
    private boolean isConnectionOpened;

    /** The runnable contxt that is used for long-running operations such as connection checks */
    private IRunnableContext runnableContext;


    /**
     * Creates a new instance of ConnectionPageWrapper.
     *
     * @param listener the initial modify listener, may be null
     * @param runnableContext the runnable context
     */
    public ConnectionPageWrapper( ConnectionPageModifyListener listener, IRunnableContext runnableContext )
    {
        this.listenerList = new ArrayList<ConnectionPageModifyListener>( 5 );
        if ( listener != null )
        {
            this.listenerList.add( listener );
            this.isConnectionOpened = listener.getRealConnection() != null && listener.getRealConnection().isOpened();
        }
        else
        {
            this.isConnectionOpened = false;
        }
        this.runnableContext = runnableContext;
    }


    /**
     * Add the give listnere to the list of modify listeners.
     *
     * @param listener the modify listener
     */
    public void addConnectionPageModifyListener( ConnectionPageModifyListener listener )
    {
        listenerList.add( listener );
    }


    /**
     * Gets the connection name.
     * 
     * @return the connectio name
     */
    public String getName()
    {
        return nameText.getText();
    }


    /**
     * Gets the host name.
     * 
     * @return the host name
     */
    public String getHostName()
    {
        return hostCombo != null ? hostCombo.getText() : hostText.getText();
    }


    /**
     * Gets the port.
     * 
     * @return the port
     */
    public int getPort()
    {
        return Integer.parseInt( portCombo != null ? portCombo.getText() : portText.getText() );
    }


    /**
     * Gets the encyrption method, one of IConnection.ENCYRPTION_NONE, 
     * IConnection.ENCYRPTION_LDAPS or IConnection.ENCYRPTION_STARTTLS.
     * 
     * @return the encyrption method
     */
    public int getEncyrptionMethod()
    {
        if ( encryptionMethodCombo != null )
        {
            switch ( encryptionMethodCombo.getSelectionIndex() )
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


    /**
     * Returns true if base DN's should be fetched 
     * whenever opening the connection.
     * 
     * @return true, if base DN's should be fetched
     */
    public boolean isAutoFetchBaseDns()
    {
        return autoFetchBaseDnsButton.getSelection();
    }


    /**
     * Gets the base DN.
     * 
     * @return the base DN
     */
    public String getBaseDN()
    {
        return baseDNCombo.getText();
    }


    /**
     * Gets the count limit.
     * 
     * @return the count limit
     */
    public int getCountLimit()
    {
        return limitWidget.getCountLimit();
    }


    /**
     * Gets the time limit.
     * 
     * @return the time limit
     */
    public int getTimeLimit()
    {
        return limitWidget.getTimeLimit();
    }


    /**
     * Gets the aliases dereferencing method.
     * 
     * @return the aliases dereferencing method
     */
    public int getAliasesDereferencingMethod()
    {
        return aliasesDereferencingWidget.getAliasesDereferencingMethod();
    }


    /**
     * Gets the referrals handling method.
     * 
     * @return the referrals handling method
     */
    public int getReferralsHandlingMethod()
    {
        return referralsHandlingWidget.getReferralsHandlingMethod();
    }


    /**
     * Sets the open connection on finish flag.
     * 
     * @param b the open connection on finish flag
     */
    public void setOpenConnectionOnFinish( boolean b )
    {
        if ( openConnectionButton != null )
        {
            openConnectionButton.setSelection( b );
        }
    }


    /**
     * Returns true if the connection should be opened
     * when finishing the wizard.
     * 
     * @return true, if the connection should be opened
     */
    public boolean isOpenConnectionOnFinish()
    {
        return openConnectionButton.getSelection();
    }


    /**
     * Gets the authentication method, one of IConnection.AUTH_ANONYMOUS
     * or IConnection.AUTH_SIMPLE.
     * 
     * @return the authentication method
     */
    public int getAuthenticationMethod()
    {
        if ( authenticationMethodCombo != null )
        {
            switch ( authenticationMethodCombo.getSelectionIndex() )
            {
                case 1:
                    return IConnection.AUTH_SIMPLE;
                case 2:
                    return IConnection.AUTH_SASL_DIGMD5;
                case 3:
                    return IConnection.AUTH_SASL_CRAMD5;
                default:
                    return IConnection.AUTH_ANONYMOUS;
            }
        }
        return IConnection.AUTH_ANONYMOUS;
    }
    


    /**
     * Gets the simple auth, digest md5 or cram md5 bind principal.
     * 
     * @return the simple auth bind principal
     */
    public String getAuthBindPrincipal()
    {
        return simpleAuthBindPrincipalCombo != null ? simpleAuthBindPrincipalCombo.getText()
            : simpleAuthBindPrincipalText.getText();
    }


    /**
     * Gets the simple, digest md5 or cram md5 auth bind password.
     * 
     * @return the auth bind password
     */
    public String getAuthBindPassword()
    {
        return simpleAuthBindPasswordText.getText();
    }


    /**
     * Returns true if the bind password should be saved on disk.
     * 
     * @return true, if the bind password should be saved on disk
     */
    public boolean isSaveSimpleAuthBindPassword()
    {
        return saveSimpleAuthBindPasswordButton.getSelection();
    }


    /**
     * Adds the main input widgets. In includes widgets for the connection name,
     * host, port and encrypition method 
     * 
     * @param name the initial name
     * @param host the initial host
     * @param port the initial port
     * @param encryptionMethod the initial encryption method
     * @param parent the parent
     */
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
            String[] hostHistory = HistoryUtils.load( BrowserCommonConstants.DIALOGSETTING_KEY_HOST_HISTORY );
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
            String[] portHistory = HistoryUtils.load( BrowserCommonConstants.DIALOGSETTING_KEY_PORT_HISTORY );
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
        encryptionMethodCombo = BaseWidgetUtils.createReadonlyCombo( groupComposite, encMethods, encryptionMethod, 2 );
        encryptionMethodCombo.addSelectionListener( this );
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

        setEnabled();
    }


    /**
     * Adds the base DN input.
     * 
     * @param autoFetchBaseDNs the initial auto fetch base DN's flag
     * @param baseDN the initial base DN
     * @param parent the parent
     */
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

        setEnabled();
    }


    /**
     * Adds the limit input.
     * 
     * @param countLimit the initial count limit
     * @param timeLimit the initial time limit
     * @param aliasesDereferencingMethod the initial aliases dereferencing method
     * @param referralsHandlingMethod the initial referrals handling method
     * @param parent the parent
     */
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

        setEnabled();
    }


    /**
     * Adds the open connection on finish input.
     * 
     * @param openConnectionOnFinish the initial value
     * @param parent the parent
     */
    public void addOpenConnectionInput( boolean openConnectionOnFinish, Composite parent )
    {
        openConnectionButton = BaseWidgetUtils.createCheckbox( parent, "Open connection on finish", 1 );
        openConnectionButton.setSelection( openConnectionOnFinish );
        openConnectionButton.addSelectionListener( this );
    }


    /**
     * Adds the authentication method input.
     * 
     * @param authMethod the initial auth method
     * @param parent the parent
     */
    public void addAuthenticationMethodInput( int authMethod, Composite parent )
    {

        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        Group group = BaseWidgetUtils.createGroup( composite, "Authentication Method", 1 );
        Composite groupComposite = BaseWidgetUtils.createColumnContainer( group, 1, 1 );

        String[] authMethods = new String[]
                                         { "Anonymous Authentication", "Simple Authentication", "DIGEST-MD5 (SASL)", "CRAM-MD5 (SASL)" };
        
        authenticationMethodCombo = BaseWidgetUtils.createReadonlyCombo( groupComposite, authMethods, authMethod, 2 );
        authenticationMethodCombo.addSelectionListener( this );
    }


    /**
     * Adds inputs for principal and pasword.
     * 
     * @param saveBindPassword the initial save bind password flag
     * @param bindPrincipal the initial bind principal
     * @param bindPassword the initial bind password
     * @param parent the parent
     */
    public void addPrincipalPasswordInput( boolean saveBindPassword, String bindPrincipal, String bindPassword,
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
            String[] dnHistory = HistoryUtils.load( BrowserCommonConstants.DIALOGSETTING_KEY_DN_HISTORY );
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

        checkPrincipalPasswordAuthButton = new Button( composite, SWT.PUSH );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalAlignment = SWT.RIGHT;
        checkPrincipalPasswordAuthButton.setLayoutData( gd );
        checkPrincipalPasswordAuthButton.setText( "Check Authentication" );
        checkPrincipalPasswordAuthButton.setEnabled( false );
        checkPrincipalPasswordAuthButton.addSelectionListener( new SelectionListener()
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
        setEnabled();
    }


    /**
     * Fires a connection page modified event when then page was modified.
     */
    private void fireConnectionPageModified()
    {
        for ( Iterator<ConnectionPageModifyListener> it = listenerList.iterator(); it.hasNext(); )
        {
            it.next().connectionPageModified();
        }
    }


    /**
     * Sets the enabled/disabled state of all widgets depending on the connection state.
     */
    private void setEnabled()
    {

        if ( isConnectionOpened )
        {
            if ( encryptionMethodCombo != null && checkConnectionButton != null )
            {
                encryptionMethodCombo.setEnabled( false );
                checkConnectionButton.setEnabled( false );
            }

            if ( baseDNCombo != null && autoFetchBaseDnsButton != null )
            {
                autoFetchBaseDnsButton.setEnabled( false );
                baseDNCombo.setEnabled( false );
                fetchBaseDnsButton.setEnabled( false );
            }

            if ( authenticationMethodCombo != null )
            {
                authenticationMethodCombo.setEnabled( false );
            }
            if ( saveSimpleAuthBindPasswordButton != null && saveSimpleAuthBindPasswordButton != null )
            {
                saveSimpleAuthBindPasswordButton.setEnabled( false );
                checkPrincipalPasswordAuthButton.setEnabled( false );
            }
        }
        else
        {
            if ( hostCombo != null && portCombo != null && checkConnectionButton != null )
            {
                if ( !hostCombo.getText().equals( "" ) && !hostCombo.getText().equals( "" ) )
                {
                    checkConnectionButton.setEnabled( true );
                }
                else
                {
                    checkConnectionButton.setEnabled( false );
                }
            }

            if ( baseDNCombo != null && autoFetchBaseDnsButton != null )
            {
                if ( autoFetchBaseDnsButton.getSelection() )
                {
                    baseDNCombo.setEnabled( false );
                }
                else
                {
                    baseDNCombo.setEnabled( true );
                }
            }
            if ( simpleAuthBindPrincipalCombo != null && simpleAuthBindPasswordText != null
                && saveSimpleAuthBindPasswordButton != null )
            {
                simpleAuthBindPrincipalCombo.setEnabled( getPrincipalPasswordEnabled () );
                simpleAuthBindPasswordText.setEnabled( saveSimpleAuthBindPasswordButton.getSelection()
                    && getPrincipalPasswordEnabled () );
                saveSimpleAuthBindPasswordButton.setEnabled( getPrincipalPasswordEnabled () );
                checkPrincipalPasswordAuthButton.setEnabled( saveSimpleAuthBindPasswordButton.getSelection()
                    && !simpleAuthBindPrincipalCombo.getText().equals( "" )
                    && !simpleAuthBindPasswordText.getText().equals( "" ) && getPrincipalPasswordEnabled () );
            }
        }
    }


    /**
     * Validates the connection parameters after each modification.
     */
    private void validate()
    {
        String message = null;
        String errorMessage = null;

        if ( baseDNCombo != null && baseDNCombo.isVisible() )
        {
            try
            {
                new DN( baseDNCombo.getText() );
            }
            catch ( NameException e )
            {
                message = "Please enter a valid base DN.";
            }
        }
        if ( simpleAuthBindPasswordText != null && getPrincipalPasswordEnabled () && simpleAuthBindPasswordText.isVisible() )
        {
            if ( saveSimpleAuthBindPasswordButton.getSelection() && "".equals( simpleAuthBindPasswordText.getText() ) )
            {
                message = "Please enter a bind password.";
            }
        }
        if ( simpleAuthBindPrincipalCombo != null && getPrincipalPasswordEnabled () && simpleAuthBindPrincipalCombo.isVisible() )
        {
            if ( "".equals( simpleAuthBindPrincipalCombo.getText() ) )
            {
                message = "Please enter a bind DN or user.";
            }
            else
            {
                // every bind principal is accepted
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
                && BrowserCorePlugin.getDefault().getConnectionManager().getConnection( nameText.getText() ) != listenerList
                    .get( 0 ).getRealConnection() )
            {
                errorMessage = "A connection named '" + nameText.getText() + "' already exists.";
            }
        }

        for ( Iterator<ConnectionPageModifyListener> it = listenerList.iterator(); it.hasNext(); )
        {
            ConnectionPageModifyListener listener = it.next();
            listener.setMessage( message );
            listener.setErrorMessage( errorMessage );
        }
    }

    
    private boolean getPrincipalPasswordEnabled ()
    {
        return ( getAuthenticationMethod() == IConnection.AUTH_SIMPLE )
        || ( getAuthenticationMethod() == IConnection.AUTH_SASL_DIGMD5 )
        || ( getAuthenticationMethod() == IConnection.AUTH_SASL_CRAMD5 );
    }

    /**
     * {@inheritDoc}
     */
    public void modifyText( ModifyEvent e )
    {
        setEnabled();
        validate();
        fireConnectionPageModified();
    }


    /**
     * {@inheritDoc}
     */
    public void widgetSelected( SelectionEvent e )
    {
        setEnabled();
        validate();
        fireConnectionPageModified();
    }


    /**
     * {@inheritDoc}
     */
    public void widgetDefaultSelected( SelectionEvent e )
    {
        setEnabled();
        validate();
        fireConnectionPageModified();
    }


    /**
     * Gets a temporary connection with all conection parameter 
     * entered in this page. 
     *
     * @return a test connection
     */
    public IConnection getTestConnection()
    {
        String principal;
        String password;
        Connection conn;
        
        if ( getAuthenticationMethod() == IConnection.AUTH_ANONYMOUS )
        {
            principal = null;
            password = null;
        }
        else if ( getAuthenticationMethod() == IConnection.AUTH_SIMPLE
            || getAuthenticationMethod() == IConnection.AUTH_SASL_DIGMD5
            || getAuthenticationMethod() == IConnection.AUTH_SASL_CRAMD5 )
        {
            principal = getAuthBindPrincipal();
            password = getAuthBindPassword();
        }
        else 
        {
            return null;
        }
        
        
        try
        {
            conn = new Connection( null, getHostName(), getPort(), getEncyrptionMethod(), isAutoFetchBaseDns(),
                new DN( getBaseDN() ), getCountLimit(), getTimeLimit(), getAliasesDereferencingMethod(),
                getReferralsHandlingMethod(), getAuthenticationMethod(), principal, password );
        }
        catch ( NameException e )
        {
            conn = null;
        }
        return conn;
    }


    /**
     * Saved the dialog settings. The curren values of host, port and bind principal are added
     * to the history.
     */
    public void saveDialogSettings()
    {
        if ( !isConnectionOpened )
        {
            if ( hostCombo != null )
            {
                HistoryUtils.save( BrowserCommonConstants.DIALOGSETTING_KEY_HOST_HISTORY, hostCombo.getText() );
            }
            if ( portCombo != null )
            {
                HistoryUtils.save( BrowserCommonConstants.DIALOGSETTING_KEY_PORT_HISTORY, portCombo.getText() );
            }
            if ( simpleAuthBindPrincipalCombo != null )
            {
                HistoryUtils.save( BrowserCommonConstants.DIALOGSETTING_KEY_DN_HISTORY, simpleAuthBindPrincipalCombo
                    .getText() );
            }
        }
    }

}
