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

package org.apache.directory.ldapstudio.browser.view.views;


import javax.naming.InvalidNameException;

import org.apache.directory.ldapstudio.browser.Activator;
import org.apache.directory.ldapstudio.browser.model.Connection;
import org.apache.directory.ldapstudio.browser.model.Connections;
import org.apache.directory.ldapstudio.browser.view.ImageKeys;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class implements the Connection Information Page of the Connection Wizard
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ConnectionWizardInformationPage extends WizardPage
{
    private Connection connection;

    private Group connectionGroup;

    private Label nameLabel;
    private Text nameText;

    private Group hostGroup;

    private Label hostLabel;
    private Text hostText;

    private Label portLabel;
    private Text portText;

    private Label baseDNLabel;
    private Text baseDNText;

    private Button anonymousBind;

    private Group userGroup;

    private Label userDNLabel;
    private Text userDNText;

    private Label passwordLabel;
    private Text passwordText;

    private Button appendBaseDNtoUserDNWithBaseDNButton;


    protected ConnectionWizardInformationPage()
    {
        super( "ConnectionInformationPage" );
        setTitle( "Specify information for this connection" );
        setDescription( "Specify your settings and click \"Finish\" to save." );
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
            ImageKeys.WIZARD_CONNECTION ) );
    }


    public void createControl( Composite parent )
    {
        Composite container = new Composite( parent, SWT.NULL );
        GridLayout layout = new GridLayout();
        container.setLayout( layout );
        layout.numColumns = 1;

        // Connection Group
        connectionGroup = new Group( container, SWT.NONE );
        connectionGroup.setText( "Connection Information" );
        connectionGroup.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false ) );
        connectionGroup.setLayout( new GridLayout( 2, false ) );

        // Name
        nameLabel = new Label( connectionGroup, SWT.NONE );
        nameLabel.setText( "Name:    " );
        nameText = new Text( connectionGroup, SWT.BORDER );
        nameText.setText( "" );
        nameText.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false ) );

        // Host Group
        hostGroup = new Group( container, SWT.NONE );
        hostGroup.setText( "Host Information" );
        hostGroup.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false ) );
        hostGroup.setLayout( new GridLayout( 2, false ) );

        // Host
        hostLabel = new Label( hostGroup, SWT.NONE );
        hostLabel.setText( "Host:" );
        hostText = new Text( hostGroup, SWT.BORDER );
        hostText.setText( "" );
        hostText.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false ) );

        // Port
        portLabel = new Label( hostGroup, SWT.NONE );
        portLabel.setText( "Port:" );
        portText = new Text( hostGroup, SWT.BORDER );
        portText.setText( "" );
        portText.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false ) );

        // Base DN
        baseDNLabel = new Label( hostGroup, SWT.NONE );
        baseDNLabel.setText( "Base DN:" );
        baseDNText = new Text( hostGroup, SWT.BORDER );
        baseDNText.setText( "" );
        baseDNText.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false ) );

        // Anonymous Bind
        new Label( hostGroup, SWT.NONE );
        anonymousBind = new Button( hostGroup, SWT.CHECK );
        anonymousBind.setText( "Anonymous Bind" );
        anonymousBind.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false ) );

        // User Group
        userGroup = new Group( container, SWT.NONE );
        userGroup.setText( "User Information" );
        userGroup.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false ) );
        userGroup.setLayout( new GridLayout( 2, false ) );

        // User DN
        userDNLabel = new Label( userGroup, SWT.NONE );
        userDNLabel.setText( "User DN:" );
        userDNText = new Text( userGroup, SWT.BORDER );
        userDNText.setText( "" );
        userDNText.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false ) );

        // Prefix User DN with Base DN
        new Label( userGroup, SWT.NONE );
        appendBaseDNtoUserDNWithBaseDNButton = new Button( userGroup, SWT.CHECK );
        appendBaseDNtoUserDNWithBaseDNButton.setText( "Append Base DN to User DN" );
        appendBaseDNtoUserDNWithBaseDNButton.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false ) );

        // Password
        passwordLabel = new Label( userGroup, SWT.NONE );
        passwordLabel.setText( "Password:" );
        passwordText = new Text( userGroup, SWT.BORDER | SWT.PASSWORD );
        passwordText.setText( "" );
        passwordText.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false ) );

        setControl( nameText );

        // Initializing the fields
        initFieldsFromConnection();

        // Initializing Listeners
        initListeners();
    }


    /**
     * Initilizes the Listeners on the SWT UI Components (List, Button, etc.)
     */
    private void initListeners()
    {
        // Name
        nameText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                verifyConnection();
            }
        } );

        // Host
        hostText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                verifyConnection();
            }
        } );

        // Port
        portText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                verifyConnection();
            }
        } );

        // Base DN
        baseDNText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                verifyConnection();
            }
        } );

        // Anonymous Bind
        anonymousBind.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                verifyConnection();

                if ( anonymousBind.getSelection() )
                {
                    // If the checkbox is checked, we have to disable the User Information fields
                    disableUserInformation();
                }
                else
                {
                    // If the the checkbox isn't checked, we have to enable the User Information fields
                    enableUserInformation();
                }
            }
        } );

        // User DN
        userDNText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                verifyConnection();
            }
        } );

        // Password
        passwordText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                verifyConnection();
            }
        } );
    }


    /**
     * Saves the modifications done on the Connection
     */
    private void verifyConnection()
    {
        // We need to use asyncExec to be able to access the UI. See http://wiki.eclipse.org/index.php/FAQ_Why_do_I_get_an_invalid_thread_access_exception%3F
        Display.getDefault().asyncExec( new Runnable()
        {
            public void run()
            {
                // Reseting previous message
                setErrorMessage( null );
                setPageComplete( true );

                // Name
                if ( ( nameText.getText() == null ) || ( "".equals( nameText.getText() ) ) )
                {
                    setErrorMessage( "Name can't be empty." );
                    setPageComplete( false );
                    return;
                }
                else
                {
                    Connections connections = Connections.getInstance();

                    if ( !connections.isConnectionNameAvailable( nameText.getText(), connection.getName() ) )
                    {
                        setErrorMessage( "A connection with same Name already exists." );
                        setPageComplete( false );
                        return;
                    }
                }

                // Host
                if ( ( hostText.getText() == null ) || ( "".equals( hostText.getText() ) ) )
                {
                    setErrorMessage( "Host can't be empty." );
                    setPageComplete( false );
                    return;
                }

                // Port
                if ( ( portText.getText() == null ) || ( "".equals( portText.getText() ) ) )
                {
                    setErrorMessage( "Port can't be empty." );
                    setPageComplete( false );
                    return;
                }
                else
                {
                    try
                    {
                        int port = Integer.parseInt( portText.getText() );
                        if ( ( port <= 0 ) || ( port > 65535 ) )
                        {
                            setErrorMessage( "Port must between 1 and 65535" );
                            setPageComplete( false );
                            return;
                        }
                    }
                    catch ( NumberFormatException e )
                    {
                        setErrorMessage( "Port must be an integer" );
                        setPageComplete( false );
                        return;
                    }
                }

                // Base DN
                if ( ( baseDNText.getText() != null ) && ( !"".equals( baseDNText.getText() ) ) )
                {
                    try
                    {
                        new LdapDN( baseDNText.getText() );
                    }
                    catch ( InvalidNameException e )
                    {
                        setErrorMessage( "Base DN is not a correct DN." );
                        setPageComplete( false );
                        return;
                    }
                }

                // User DN
                if ( !anonymousBind.getSelection() )
                {
                    if ( ( userDNText.getText() == null ) || ( "".equals( userDNText.getText() ) ) )
                    {
                        setErrorMessage( "User DN can't be empty." );
                        setPageComplete( false );
                        return;
                    }
                }
                if ( ( userDNText.getText() != null ) && ( !"".equals( userDNText.getText() ) ) )
                {
                    try
                    {
                        new LdapDN( userDNText.getText() );
                    }
                    catch ( InvalidNameException e )
                    {
                        setErrorMessage( "User DN is not a correct DN." );
                        setPageComplete( false );
                        return;
                    }
                }

            }
        } );
    }


    /**
     * Initializes the UI fields from the Connection
     */
    private void initFieldsFromConnection()
    {
        // Name
        nameText.setText( ( connection.getName() == null ) ? "" : connection.getName() );

        // Host
        hostText.setText( ( connection.getHost() == null ) ? "" : connection.getHost() );

        // Port
        portText.setText( ( connection.getPort() == 0 ) ? "" : ( connection.getPort() + "" ) );

        // Base DN
        baseDNText.setText( ( connection.getBaseDN() == null ) ? "" : connection.getBaseDN().getNormName() );

        // Anonymous Bind
        if ( connection.isAnonymousBind() )
        {
            anonymousBind.setSelection( true );
            disableUserInformation();
        }
        else
        {
            anonymousBind.setSelection( false );
        }

        // User DN
        userDNText.setText( ( connection.getUserDN() == null ) ? "" : connection.getUserDN().getNormName() );

        // Append Base DN to User DN
        appendBaseDNtoUserDNWithBaseDNButton.setSelection( connection.isAppendBaseDNtoUserDNWithBaseDN() );

        // Password
        passwordText.setText( ( connection.getPassword() == null ) ? "" : connection.getPassword() );
    }


    /**
     * Enables the User Information fields
     */
    private void enableUserInformation()
    {
        userGroup.setEnabled( true );
        userDNLabel.setEnabled( true );
        userDNText.setEnabled( true );
        appendBaseDNtoUserDNWithBaseDNButton.setEnabled( true );
        passwordLabel.setEnabled( true );
        passwordText.setEnabled( true );
    }


    /**
     * Disables the User Information fields
     */
    private void disableUserInformation()
    {
        userGroup.setEnabled( false );
        userDNLabel.setEnabled( false );
        userDNText.setEnabled( false );
        appendBaseDNtoUserDNWithBaseDNButton.setEnabled( false );
        passwordLabel.setEnabled( false );
        passwordText.setEnabled( false );
    }


    /**
     * Saves the modifications made on the connection within the UI
     */
    public void saveConnection()
    {
        try
        {
            connection.setName( nameText.getText() );
            connection.setHost( hostText.getText() );
            int port = Integer.parseInt( portText.getText() );
            connection.setPort( port );
            connection.setBaseDN( ( "".equals( baseDNText.getText() ) ? LdapDN.EMPTY_LDAPDN : new LdapDN( baseDNText
                .getText() ) ) );
            connection.setAnonymousBind( anonymousBind.getSelection() );
            connection.setUserDN( ( "".equals( userDNText.getText() ) ? LdapDN.EMPTY_LDAPDN : new LdapDN( userDNText
                .getText() ) ) );
            connection.setAppendBaseDNtoUserDNWithBaseDN( appendBaseDNtoUserDNWithBaseDNButton.getSelection() );
            connection.setPassword( passwordText.getText() );
        }
        catch ( InvalidNameException e )
        {
            // Should never arrive since the "Finish Button" can't be pushed until the
            // Base DN and User DN are correct
        }
    }


    /**
     * Indicates if the Wizard is able to finish
     * @return true if the Wizard is able to finish
     */
    public boolean canFinish()
    {
        return isPageComplete();
    }


    /**
     * Gets the Connection
     * @return the  Connection
     */
    public Connection getConnection()
    {
        return connection;
    }


    /**
     * Gets the currently selected Connection
     * @return the currently selected Connection, null if no Connection is selected
     */
    public void setConnection( Connection connection )
    {
        this.connection = connection;
    }
}
