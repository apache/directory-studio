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
package org.apache.directory.studio.proxy.view.wizards;


import org.apache.directory.studio.ldapbrowser.common.dialogs.SelectConnectionDialog;
import org.apache.directory.studio.ldapbrowser.core.model.IConnection;
import org.apache.directory.studio.proxy.Activator;
import org.apache.directory.studio.proxy.ProxyConstants;
import org.apache.directory.studio.proxy.view.BaseWidgetUtils;
import org.apache.directory.studio.proxy.view.HistoryUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.util.BundleUtility;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;


/**
 * This class implements the Connect Wizard Settings Page.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ConnectWizardBrowserAvailablePage extends WizardPage implements ModifyListener, SelectionListener
{
    /** The choosen Connection */
    private IConnection selectedConnection;

    // UI Fields
    private Combo proxyPortCombo;
    private Button useConnectionButton;
    private Text connectionText;
    private Button connectionButton;
    private Button useThisSettingsButton;
    private Combo serverHostCombo;
    private Combo serverPortCombo;


    /**
     * Creates a new instance of ConnectWizardSettingsPage.
     */
    public ConnectWizardBrowserAvailablePage()
    {
        super( ConnectWizardBrowserAvailablePage.class.getName() );
        setTitle( "Connect Wizard" );
        setDescription( "Specify the settings for the LDAP Proxy." );
        setPageComplete( false );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout gl = new GridLayout( 1, false );
        composite.setLayout( gl );

        Group proxyGroup = BaseWidgetUtils.createGroup( composite, "LDAP Proxy", 1 );
        proxyGroup.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        Composite proxyGroupComposite = BaseWidgetUtils.createColumnContainer( proxyGroup, 2, 1 );

        BaseWidgetUtils.createLabel( proxyGroupComposite, "Proxy port:", 1 );
        proxyPortCombo = BaseWidgetUtils.createCombo( proxyGroupComposite, new String[0], -1, 1 );
        proxyPortCombo.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "[0-9]*" ) )
                {
                    e.doit = false;
                }
                if ( serverPortCombo.getText().length() > 4 && e.text.length() > 0 )
                {
                    e.doit = false;
                }
            }
        } );

        Group serverGroup = BaseWidgetUtils.createGroup( composite, "LDAP Server", 1 );
        serverGroup.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );

        useConnectionButton = BaseWidgetUtils.createRadiobutton( serverGroup,
            "Use settings from an existing connection:", 1 );

        Composite connectionComposite = BaseWidgetUtils.createColumnContainer( serverGroup, 4, 1 );

        BaseWidgetUtils.createRadioIndent( connectionComposite, 1 );
        BaseWidgetUtils.createLabel( connectionComposite, "Connection:", 1 );
        connectionText = BaseWidgetUtils.createReadonlyText( connectionComposite, "", 1 );
        connectionButton = BaseWidgetUtils.createButton( connectionComposite, "B&rowse...", 1 );

        Label connectionInformationLabel = BaseWidgetUtils.createLabel( connectionComposite,
            "(Warning: This feature requires the LDAP Browser Plugin.)", 1 );
        connectionInformationLabel.setLayoutData( new GridData( SWT.RIGHT, SWT.NONE, true, false, 4, 1 ) );

        useThisSettingsButton = BaseWidgetUtils.createRadiobutton( serverGroup, "Use this settings:", 1 );

        Composite settingsComposite = BaseWidgetUtils.createColumnContainer( serverGroup, 3, 1 );
        BaseWidgetUtils.createRadioIndent( settingsComposite, 1 );
        BaseWidgetUtils.createLabel( settingsComposite, "Hostname:", 1 );
        serverHostCombo = BaseWidgetUtils.createCombo( settingsComposite, new String[0], -1, 1 );

        BaseWidgetUtils.createRadioIndent( settingsComposite, 1 );
        BaseWidgetUtils.createLabel( settingsComposite, "Port:", 1 );
        serverPortCombo = BaseWidgetUtils.createCombo( settingsComposite, new String[0], -1, 1 );
        serverPortCombo.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "[0-9]*" ) )
                {
                    e.doit = false;
                }
                if ( serverPortCombo.getText().length() > 4 && e.text.length() > 0 )
                {
                    e.doit = false;
                }
            }
        } );

        setControl( parent );

        checkBrowserPluginAvailability();
        loadDialogHistory();
        initListeners();
        setUiEnableState();
    }


    private void checkBrowserPluginAvailability()
    {
        if ( !isPluginAvailable( "org.apache.directory.studio.ldapbrowser.core" )
            || !isPluginAvailable( "org.apache.directory.studio.ldapbrowser.common" ) )
        {
            useConnectionButton.setEnabled( false );
            connectionButton.setEnabled( false );
            connectionText.setEnabled( false );
        }
    }


    /**
     * Loads the last values entered by the user from the Dialog History.
     */
    private void loadDialogHistory()
    {
        proxyPortCombo.setItems( HistoryUtils.load( ProxyConstants.DIALOGSETTING_KEY_PROXY_PORT_HISTORY ) );
        serverHostCombo.setItems( HistoryUtils.load( ProxyConstants.DIALOGSETTING_KEY_SERVER_HOST_HISTORY ) );
        serverPortCombo.setItems( HistoryUtils.load( ProxyConstants.DIALOGSETTING_KEY_SERVER_PORT_HISTORY ) );

        boolean useConnection = Activator.getDefault().getDialogSettings().getBoolean(
            ProxyConstants.DIALOGSETTING_KEY_SERVER_USE_CONNECTION );
        useConnectionButton.setSelection( useConnection );
        useThisSettingsButton.setSelection( !useConnection );
    }


    /**
     * Saves the values entered by the user in the Dialog History.
     */
    public void saveDialogHistory()
    {
        HistoryUtils.save( ProxyConstants.DIALOGSETTING_KEY_PROXY_PORT_HISTORY, proxyPortCombo.getText() );
        HistoryUtils.save( ProxyConstants.DIALOGSETTING_KEY_SERVER_HOST_HISTORY, serverHostCombo.getText() );
        HistoryUtils.save( ProxyConstants.DIALOGSETTING_KEY_SERVER_PORT_HISTORY, serverPortCombo.getText() );

        Activator.getDefault().getDialogSettings().put( ProxyConstants.DIALOGSETTING_KEY_SERVER_USE_CONNECTION,
            useConnectionButton.getSelection() );
    }


    /**
     * Initializes the listeners.
     */
    private void initListeners()
    {
        proxyPortCombo.addModifyListener( this );
        useConnectionButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                validate();
                setUiEnableState();
            }
        } );
        connectionButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                SelectConnectionDialog dialog = new SelectConnectionDialog( PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getShell(), "Select Connection", null );
                dialog.open();
                IConnection connection = dialog.getSelectedConnection();
                setConnection( connection );
                validate();
            }
        } );
        useThisSettingsButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                validate();
                setUiEnableState();
            }
        } );
        serverHostCombo.addModifyListener( this );
        serverPortCombo.addModifyListener( this );
    }


    /**
     * Enables the UI fields.
     */
    private void setUiEnableState()
    {
        if ( ( useConnectionButton.isEnabled() ) && ( useConnectionButton.getSelection() ) )
        {
            connectionText.setEnabled( true );
            connectionButton.setEnabled( true );
            serverHostCombo.setEnabled( false );
            serverPortCombo.setEnabled( false );
        }
        else
        {
            connectionText.setEnabled( false );
            connectionButton.setEnabled( false );
            serverHostCombo.setEnabled( true );
            serverPortCombo.setEnabled( true );
        }
    }


    /**
     * Checks if the given plugin is available (installed and active).
     * The plugin is actived if it's not already active.
     *
     * @param bundleId
     *      the id of the plugin
     * @return
     *      true if the given plugin is available, false if not.
     */
    public boolean isPluginAvailable( String bundleId )
    {
        Bundle pluginBundle = Platform.getBundle( bundleId );

        if ( pluginBundle == null )
        {
            return false;
        }

        if ( BundleUtility.isActive( pluginBundle ) )
        {
            return true;
        }
        else
        {
            try
            {
                pluginBundle.start();
            }
            catch ( BundleException e )
            {
                return false;
            }

            return BundleUtility.isActive( pluginBundle );
        }
    }


    public void setConnection( IConnection connection )
    {
        this.selectedConnection = connection;
        connectionText.setText( this.selectedConnection != null ? this.selectedConnection.getName() : "" );
    }


    /**
     * Gets the local port defined by the user.
     * 
     * @return
     *      the local port defined by the user
     */
    public int getLocalPort()
    {
        int port = 0;

        try
        {
            port = Integer.parseInt( proxyPortCombo.getText() );
        }
        catch ( NumberFormatException e )
        {
        }

        return port;
    }


    /**
     * Gets the remote host defined by the user.
     *
     * @return
     *      the remote host defined by the user
     */
    public String getRemoteHost()
    {
        if ( useConnectionButton.getSelection() )
        {
            return selectedConnection.getHost();
        }
        else
        {
            return serverHostCombo.getText();
        }
    }


    /**
     * Gets the remote port defined by the user.
     *
     * @return
     *      the remote port defined by the user
     */
    public int getRemotePort()
    {
        int port = 0;

        if ( useConnectionButton.getSelection() )
        {
            port = selectedConnection.getPort();
        }
        else
        {
            try
            {
                port = Integer.parseInt( serverPortCombo.getText() );
            }
            catch ( NumberFormatException e )
            {
            }
        }

        return port;
    }


    /* (non-Javadoc)
     * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
     */
    public void modifyText( ModifyEvent e )
    {
        validate();
    }


    /* (non-Javadoc)
     * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
     */
    public void widgetDefaultSelected( SelectionEvent e )
    {
        validate();
    }


    /* (non-Javadoc)
     * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
     */
    public void widgetSelected( SelectionEvent e )
    {
        validate();
    }


    /**
     * Validates the page.
     */
    private void validate()
    {
        String errorMessage = null;

        if ( useConnectionButton.getSelection() )
        {
            if ( selectedConnection == null )
            {
                errorMessage = "Please select a connection.";
            }
        }
        else
        {
            if ( "".equals( serverPortCombo.getText() ) )
            {
                errorMessage = "Please enter a port for the LDAP Server. The default LDAP port is 389.";
            }
            if ( "".equals( serverHostCombo.getText() ) )
            {
                errorMessage = "Please enter a hostname for the LDAP Server.";
            }
        }

        if ( "".equals( proxyPortCombo.getText() ) )
        {
            errorMessage = "Please enter a port for the LDAP Proxy.";
        }

        setErrorMessage( errorMessage );
        setPageComplete( getErrorMessage() == null );
    }
}
