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
package org.apache.directory.ldapstudio.proxy.view.wizards;


import org.apache.directory.ldapstudio.proxy.ProxyConstants;
import org.apache.directory.ldapstudio.proxy.view.BaseWidgetUtils;
import org.apache.directory.ldapstudio.proxy.view.HistoryUtils;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;


/**
 * This class implements the Connect Wizard Settings Page.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ConnectWizardBrowserNotAvailablePage extends WizardPage implements ModifyListener, SelectionListener
{
    // UI fields
    private Combo proxyPortCombo;
    private Combo serverHostCombo;
    private Combo serverPortCombo;


    /**
     * Creates a new instance of ConnectWizardSettingsPage.
     */
    public ConnectWizardBrowserNotAvailablePage()
    {
        super( ConnectWizardBrowserNotAvailablePage.class.getName() );
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

        Composite serverComposite = BaseWidgetUtils.createColumnContainer( serverGroup, 2, 1 );
        BaseWidgetUtils.createLabel( serverComposite, "Hostname:", 1 );
        serverHostCombo = BaseWidgetUtils.createCombo( serverComposite, new String[0], -1, 1 );

        BaseWidgetUtils.createLabel( serverComposite, "Port:", 1 );
        serverPortCombo = BaseWidgetUtils.createCombo( serverComposite, new String[0], -1, 1 );
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

        loadDialogHistory();
        initListeners();
    }


    /**
     * Loads the last values entered by the user from the Dialog History.
     */
    private void loadDialogHistory()
    {
        proxyPortCombo.setItems( HistoryUtils.load( ProxyConstants.DIALOGSETTING_KEY_PROXY_PORT_HISTORY ) );
        serverHostCombo.setItems( HistoryUtils.load( ProxyConstants.DIALOGSETTING_KEY_SERVER_HOST_HISTORY ) );
        serverPortCombo.setItems( HistoryUtils.load( ProxyConstants.DIALOGSETTING_KEY_SERVER_PORT_HISTORY ) );
    }


    /**
     * Saves the values entered by the user in the Dialog History.
     */
    public void saveDialogHistory()
    {
        HistoryUtils.save( ProxyConstants.DIALOGSETTING_KEY_PROXY_PORT_HISTORY, proxyPortCombo.getText() );
        HistoryUtils.save( ProxyConstants.DIALOGSETTING_KEY_SERVER_HOST_HISTORY, serverHostCombo.getText() );
        HistoryUtils.save( ProxyConstants.DIALOGSETTING_KEY_SERVER_PORT_HISTORY, serverPortCombo.getText() );
    }


    /**
     * Initializes the listeners.
     */
    private void initListeners()
    {
        proxyPortCombo.addModifyListener( this );
        serverHostCombo.addModifyListener( this );
        serverPortCombo.addModifyListener( this );
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
        return serverHostCombo.getText();
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

        try
        {
            port = Integer.parseInt( serverPortCombo.getText() );
        }
        catch ( NumberFormatException e )
        {
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

        if ( "".equals( serverPortCombo.getText() ) )
        {
            errorMessage = "Please enter a port for the LDAP Server. The default LDAP port is 389.";
        }
        if ( "".equals( serverHostCombo.getText() ) )
        {
            errorMessage = "Please enter a hostname for the LDAP Server.";
        }
        if ( "".equals( proxyPortCombo.getText() ) )
        {
            errorMessage = "Please enter a port for the LDAP Proxy.";
        }

        setErrorMessage( errorMessage );
        setPageComplete( getErrorMessage() == null );
    }
}
