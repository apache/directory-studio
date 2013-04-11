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

package org.apache.directory.studio.connection.ui.preferences;


import java.io.File;
import java.io.IOException;
import java.security.KeyStoreException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.directory.studio.common.ui.CommonUIUtils;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCoreConstants;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionManager;
import org.apache.directory.studio.connection.core.PasswordsKeyStoreManager;
import org.apache.directory.studio.connection.ui.ConnectionUIConstants;
import org.apache.directory.studio.connection.ui.ConnectionUIPlugin;
import org.apache.directory.studio.connection.ui.dialogs.PasswordDialog;
import org.apache.directory.studio.connection.ui.dialogs.ResetPasswordDialog;
import org.apache.directory.studio.connection.ui.dialogs.SetupPasswordDialog;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * The passwords keystore preference page contains the settings for keystore 
 * where we store the connections passwords.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PasswordsKeystorePreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
    /** The filename for the temporary keystore */
    private static final String TEMPORARY_KEYSTORE_FILENAME = "passwords-prefs-temp.jks";

    /** The passwords keystore manager */
    private PasswordsKeyStoreManager passwordsKeyStoreManager;

    /** The map used to backup connections passwords */
    private Map<String, String> connectionsPasswordsBackup = new HashMap<String, String>();

    /** The connection manager */
    private ConnectionManager connectionManager;

    // UI Widgets
    private Button enableKeystoreCheckbox;
    private Button changeMasterPasswordButton;

    // Listeners
    private SelectionListener enableKeystoreCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            Boolean selected = enableKeystoreCheckbox.getSelection();

            try
            {
                if ( selected )
                {
                    if ( !enablePasswordsKeystore() )
                    {
                        enableKeystoreCheckbox.setSelection( !selected );
                    }
                }
                else
                {
                    if ( !disablePasswordsKeystore() )
                    {
                        enableKeystoreCheckbox.setSelection( !selected );
                    }
                }
            }
            catch ( KeyStoreException kse )
            {
                CommonUIUtils.openErrorDialog( "An error occurred when enabled/disabling the keystore.\n\n"
                    + kse.getMessage() );

                enableKeystoreCheckbox.setSelection( !selected );
            }

            updateButtonsEnabledState();
        }
    };
    private SelectionListener changeMasterPasswordButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            changeMasterPassword();
        }
    };


    /**
     * Creates a new instance of PasswordsKeyStorePreferencePage.
     */
    public PasswordsKeystorePreferencePage()
    {
        super( "Passwords KeyStore" );
        super.setDescription( "General settings for Passwords Keystore:" );
        super.noDefaultAndApplyButton();

        passwordsKeyStoreManager = new PasswordsKeyStoreManager( TEMPORARY_KEYSTORE_FILENAME );
        connectionManager = ConnectionCorePlugin.getDefault().getConnectionManager();
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbench workbench )
    {
        // Getting the keystore file
        File keystoreFile = ConnectionCorePlugin.getDefault().getPasswordsKeyStoreManager().getKeyStoreFile();

        // If the keystore file exists, let's create a copy of it
        if ( keystoreFile.exists() )
        {
            try
            {
                // Copying the file
                FileUtils.copyFile( keystoreFile, getTemporaryKeystoreFile() );
            }
            catch ( IOException e )
            {
                ConnectionUIPlugin
                    .getDefault()
                    .getLog()
                    .log(
                        new Status( Status.ERROR, ConnectionUIConstants.PLUGIN_ID, Status.ERROR,
                            "Couldn't duplicate the global keystore file.", e ) ); //$NON-NLS-1$
            }
        }
    }


    /**
     * Gets the file for the temporary keystore.
     *
     * @return the  file for the temporary keystore
     */
    private File getTemporaryKeystoreFile()
    {
        return ConnectionCorePlugin.getDefault().getStateLocation().append( TEMPORARY_KEYSTORE_FILENAME ).toFile();
    }


    /**
     * {@inheritDoc}
     */
    protected void contributeButtons( Composite parent )
    {
        // Increasing the number of columns on the parent layout
        ( ( GridLayout ) parent.getLayout() ).numColumns++;

        // Change Master Password Button
        changeMasterPasswordButton = BaseWidgetUtils.createButton( parent, "Change Master Password...", 1 );
        changeMasterPasswordButton.setLayoutData( new GridData( SWT.BEGINNING, SWT.CENTER, false, false ) );
        changeMasterPasswordButton.addSelectionListener( changeMasterPasswordButtonListener );

        updateButtonsEnabledState();
    }


    /**
     * {@inheritDoc}
     */
    protected Control createContents( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 2, 1 );

        // Enable Keystore Checkbox
        enableKeystoreCheckbox = new Button( composite, SWT.CHECK | SWT.WRAP );
        enableKeystoreCheckbox
            .setText( "Store connections passwords in a password-protected keystore." );
        enableKeystoreCheckbox.setLayoutData( new GridData( SWT.BEGINNING, SWT.CENTER, false, false, 2, 1 ) );

        // Warning Label
        BaseWidgetUtils.createRadioIndent( composite, 1 );
        BaseWidgetUtils
            .createWrappedLabel(
                composite,
                "Warning: The passwords keystore requires the definition of a master password which will forbid any access to the connections passwords stored in the keystore if it gets forgotten.\n\nIf you need to change the master password, use the 'Change Master Password...' button at the bottom of this preference page.",
                1 );

        initUI();
        addListeners();

        return composite;
    }


    /**
     * Initializes the UI.
     */
    private void initUI()
    {
        int connectionsPasswordsKeystore = ConnectionCorePlugin.getDefault().getPluginPreferences()
            .getInt( ConnectionCoreConstants.PREFERENCE_CONNECTIONS_PASSWORDS_KEYSTORE );

        if ( connectionsPasswordsKeystore == ConnectionCoreConstants.PREFERENCE_CONNECTIONS_PASSWORDS_KEYSTORE_OFF )
        {
            enableKeystoreCheckbox.setSelection( false );
        }
        else if ( connectionsPasswordsKeystore == ConnectionCoreConstants.PREFERENCE_CONNECTIONS_PASSWORDS_KEYSTORE_ON )
        {
            enableKeystoreCheckbox.setSelection( true );
        }
    }


    /**
     * Adds the listeners.
     */
    private void addListeners()
    {
        enableKeystoreCheckbox.addSelectionListener( enableKeystoreCheckboxListener );
    }


    /**
     * Removes the listeners.
     */
    private void removeListeners()
    {
        enableKeystoreCheckbox.removeSelectionListener( enableKeystoreCheckboxListener );
    }


    /**
     * Updates the buttons enabled state.
     */
    private void updateButtonsEnabledState()
    {
        changeMasterPasswordButton.setEnabled( enableKeystoreCheckbox.getSelection() );
    }


    /**
     * {@inheritDoc}
     */
    protected void performDefaults()
    {
        removeListeners();

        int enablePasswordsKeystore = ConnectionCorePlugin.getDefault().getPluginPreferences()
            .getDefaultInt( ConnectionCoreConstants.PREFERENCE_CONNECTIONS_PASSWORDS_KEYSTORE );

        if ( enablePasswordsKeystore == ConnectionCoreConstants.PREFERENCE_CONNECTIONS_PASSWORDS_KEYSTORE_OFF )
        {
            enableKeystoreCheckbox.setSelection( false );
        }
        else if ( enablePasswordsKeystore == ConnectionCoreConstants.PREFERENCE_CONNECTIONS_PASSWORDS_KEYSTORE_ON )
        {
            enableKeystoreCheckbox.setSelection( true );
        }

        updateButtonsEnabledState();
        addListeners();

        ConnectionCorePlugin.getDefault().savePluginPreferences();
        super.performDefaults();
    }


    /**
     * {@inheritDoc}
     */
    public boolean performOk()
    {
        PasswordsKeyStoreManager globalPasswordsKeyStoreManager = ConnectionCorePlugin.getDefault()
            .getPasswordsKeyStoreManager();

        if ( enableKeystoreCheckbox.getSelection() )
        {
            if ( passwordsKeyStoreManager.isLoaded() )
            {
                // First, let's save the temporary keystore manager
                try
                {
                    passwordsKeyStoreManager.save();
                }
                catch ( KeyStoreException e )
                {
                    ConnectionUIPlugin
                        .getDefault()
                        .getLog()
                        .log(
                            new Status( Status.ERROR, ConnectionUIConstants.PLUGIN_ID, Status.ERROR,
                                "Couldn't save the temporary password keystore.", e ) ); //$NON-NLS-1$
                }

                // Now, let's copy the temporary keystore as the global keystore
                try
                {
                    FileUtils.copyFile( getTemporaryKeystoreFile(), ConnectionCorePlugin.getDefault()
                        .getPasswordsKeyStoreManager().getKeyStoreFile() );
                }
                catch ( IOException e )
                {
                    ConnectionUIPlugin
                        .getDefault()
                        .getLog()
                        .log(
                            new Status( Status.ERROR, ConnectionUIConstants.PLUGIN_ID, Status.ERROR,
                                "Couldn't copy the temporary keystore as the global keystore.", e ) ); //$NON-NLS-1$
                }

                // Finally lets reload the global keystore
                try
                {
                    globalPasswordsKeyStoreManager.reload( passwordsKeyStoreManager.getMasterPassword() );
                }
                catch ( KeyStoreException e )
                {
                    ConnectionUIPlugin
                        .getDefault()
                        .getLog()
                        .log(
                            new Status( Status.ERROR, ConnectionUIConstants.PLUGIN_ID, Status.ERROR,
                                "Couldn't reload the global keystore file.", e ) ); //$NON-NLS-1$
                }

                // Clearing each connection password
                for ( Connection connection : connectionManager.getConnections() )
                {
                    connection.getConnectionParameter().setBindPassword( null );
                }

                // Saving the connections
                ConnectionCorePlugin.getDefault().getConnectionManager().saveConnections();

                // Saving the value to the preferences
                ConnectionCorePlugin.getDefault().getPluginPreferences()
                    .setValue( ConnectionCoreConstants.PREFERENCE_CONNECTIONS_PASSWORDS_KEYSTORE,
                        ConnectionCoreConstants.PREFERENCE_CONNECTIONS_PASSWORDS_KEYSTORE_ON );
            }
        }
        else
        {
            // Reseting the global passwords keystore
            globalPasswordsKeyStoreManager.reset();

            // Looking for connections passwords in the list
            if ( connectionsPasswordsBackup.size() > 0 )
            {
                // Adding them to the keystore
                for ( String connectionId : connectionsPasswordsBackup.keySet() )
                {
                    Connection connection = connectionManager.getConnectionById( connectionId );

                    if ( connection != null )
                    {
                        connection.getConnectionParameter().setBindPassword(
                            connectionsPasswordsBackup.get( connectionId ) );
                    }
                }

                // Saving the connections
                ConnectionCorePlugin.getDefault().getConnectionManager().saveConnections();
            }

            // Saving the value to the preferences
            ConnectionCorePlugin.getDefault().getPluginPreferences()
                .setValue( ConnectionCoreConstants.PREFERENCE_CONNECTIONS_PASSWORDS_KEYSTORE,
                    ConnectionCoreConstants.PREFERENCE_CONNECTIONS_PASSWORDS_KEYSTORE_OFF );
        }

        ConnectionCorePlugin.getDefault().savePluginPreferences();

        deleteTemporaryKeystore();
        return true;
    }


    /**
     * {@inheritDoc}
     */
    public boolean performCancel()
    {
        deleteTemporaryKeystore();
        return true;
    }


    /**
     * Deletes the temporary keystore (if it exists)
     */
    private void deleteTemporaryKeystore()
    {
        // Getting the temporary keystore file
        File temporaryKeystoreFile = getTemporaryKeystoreFile();

        // If the temporary keystore file exists, we need to remove it
        if ( temporaryKeystoreFile.exists() )
        {
            // Deleting the file
            FileUtils.deleteQuietly( temporaryKeystoreFile );
        }
    }


    /**
     * Enables the passwords keystore.
     *
     * @return <code>true</code> if the passwords keystore was successfully enabled,
     *         <code>false</code> if not.
     * @throws KeyStoreException 
     */
    private boolean enablePasswordsKeystore() throws KeyStoreException
    {
        // Asking the user for a password
        SetupPasswordDialog setupPasswordDialog = new SetupPasswordDialog(
            enableKeystoreCheckbox.getShell(),
            "Setup Master Password",
            "Please enter a master password to secure the passwords keystore.\n\nIf you forget this master password you will not be able to access information stored in the passwords keystore. The master password cannot be retrieved.",
            null );

        if ( setupPasswordDialog.open() == SetupPasswordDialog.OK )
        {
            // Getting the master password
            String masterPassword = setupPasswordDialog.getPassword();

            // Loading the keystore
            passwordsKeyStoreManager.load( masterPassword );

            // Storing each connection password in the keystore
            for ( Connection connection : connectionManager.getConnections() )
            {
                String connectionPassword = connection.getBindPassword();

                if ( connectionPassword != null )
                {
                    passwordsKeyStoreManager.storeConnectionPassword( connection, connectionPassword, false );
                }
            }

            // Saving the keystore on disk
            passwordsKeyStoreManager.save();

            return true;
        }

        return false;
    }


    /**
     * Disables the passwords keystore.
     *
     * @return <code>true</code> if the passwords keystore was successfully disabled,
     *         <code>false</code> if not.
     */
    private boolean disablePasswordsKeystore()
    {
        // Asking the user if he wants to keep its connections passwords
        MessageDialog keepConnectionsPasswordsDialog = new MessageDialog(
            enableKeystoreCheckbox.getShell(),
            "Keep Connections Passwords?",
            null,
            "Do you want to keep your connections passwords?\n\nAll connections passwords contained in the passwords keystore will be copied and stored as plain text on disk.\nRequires the master password of the passwords keystore.",
            MessageDialog.QUESTION, new String[]
                { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL,
                    IDialogConstants.CANCEL_LABEL }, 0 );
        int keepConnectionsPasswordsValue = keepConnectionsPasswordsDialog.open();

        if ( keepConnectionsPasswordsValue == 1 )
        {
            // The user chose NOT to keep the connections passwords
            connectionsPasswordsBackup.clear();
            passwordsKeyStoreManager.deleteKeystoreFile();
            return true;
        }
        else if ( keepConnectionsPasswordsValue == 0 )
        {
            // The user chose to keep the connections passwords
            connectionsPasswordsBackup.clear();

            while ( true )
            {
                // We ask the user for the keystore password
                PasswordDialog passwordDialog = new PasswordDialog( enableKeystoreCheckbox.getShell(),
                    "Verify Master Password", "Please enter your master password:",
                    null );

                if ( passwordDialog.open() == PasswordDialog.CANCEL )
                {
                    // The user cancelled the action
                    return false;
                }

                // Getting the password
                String password = passwordDialog.getPassword();

                // Checking the password
                Exception checkPasswordException = null;
                try
                {
                    if ( passwordsKeyStoreManager.checkMasterPassword( password ) )
                    {
                        break;
                    }
                }
                catch ( KeyStoreException e )
                {
                    checkPasswordException = e;
                }

                // Creating the message
                String message = null;

                if ( checkPasswordException != null )
                {
                    message = "The master password verification failed.\n\nThe following exception was raised:\n"
                        + checkPasswordException.getMessage();
                }
                else
                {
                    message = "The master password verification failed.";
                }

                // We ask the user if he wants to retry to unlock the passwords keystore
                MessageDialog errorDialog = new MessageDialog(
                    enableKeystoreCheckbox.getShell(),
                    "Verify Master Password Failed", null, message, MessageDialog.ERROR, new String[]
                        { IDialogConstants.RETRY_LABEL,
                            IDialogConstants.CANCEL_LABEL }, 0 );

                if ( errorDialog.open() == MessageDialog.CANCEL )
                {
                    // The user cancelled the action
                    password = null;
                    return false;
                }
            }

            // Getting the connection IDs having their passwords saved in the keystore
            String[] connectionIds = passwordsKeyStoreManager.getConnectionIds();

            if ( connectionIds != null )
            {
                // Adding the passwords to the backup map
                for ( String connectionId : connectionIds )
                {
                    String password = passwordsKeyStoreManager.getConnectionPassword( connectionId );

                    if ( password != null )
                    {
                        connectionsPasswordsBackup.put( connectionId, password );
                    }
                }
            }

            passwordsKeyStoreManager.deleteKeystoreFile();

            return true;
        }
        else
        {
            // The user cancelled the action
            return false;
        }
    }


    /**
     * Changes the master password.
     *
     * @return <code>true</code> if the master password was successfully changed,
     *         <code>false</code> if not.
     */
    private void changeMasterPassword()
    {
        String newMasterPassword = null;

        while ( true )
        {
            // We ask the user to reset his master password
            ResetPasswordDialog resetPasswordDialog = new ResetPasswordDialog( changeMasterPasswordButton.getShell(),
                "", null, null );

            if ( resetPasswordDialog.open() != ResetPasswordDialog.OK )
            {
                // The user cancelled the action
                return;
            }

            // Checking the password
            Exception checkPasswordException = null;
            try
            {
                if ( passwordsKeyStoreManager.checkMasterPassword( resetPasswordDialog.getCurrentPassword() ) )
                {
                    newMasterPassword = resetPasswordDialog.getNewPassword();
                    break;
                }
            }
            catch ( KeyStoreException e )
            {
                checkPasswordException = e;
            }

            // Creating the message
            String message = null;

            if ( checkPasswordException != null )
            {
                message = "The master password verification failed.\n\nThe following exception was raised:\n"
                    + checkPasswordException.getMessage();
            }
            else
            {
                message = "The master password verification failed.";
            }

            // We ask the user if he wants to retry to unlock the passwords keystore
            MessageDialog errorDialog = new MessageDialog(
                enableKeystoreCheckbox.getShell(),
                "Verify Master Password Failed", null, message, MessageDialog.ERROR, new String[]
                    { IDialogConstants.RETRY_LABEL,
                        IDialogConstants.CANCEL_LABEL }, 0 );

            if ( errorDialog.open() == MessageDialog.CANCEL )
            {
                // The user cancelled the action
                return;
            }
        }

        if ( newMasterPassword != null )
        {
            try
            {
                passwordsKeyStoreManager.setMasterPassword( newMasterPassword );
                passwordsKeyStoreManager.save();
            }
            catch ( KeyStoreException e )
            {
                ConnectionUIPlugin
                    .getDefault()
                    .getLog()
                    .log(
                        new Status( Status.ERROR, ConnectionUIConstants.PLUGIN_ID, Status.ERROR,
                            "Couldn't save the keystore file.", e ) ); //$NON-NLS-1$
            }
        }
    }
}