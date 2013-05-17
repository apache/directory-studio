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
package org.apache.directory.studio.connection.ui;


import java.security.KeyStoreException;

import org.apache.directory.studio.connection.core.ConnectionCoreConstants;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.ui.dialogs.PasswordDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;


/**
 * This class contains utility methods for the passwords keystore.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PasswordsKeyStoreManagerUtils
{
    /**
     * Checks if the passwords keystore is enabled.
     *
     * @return <code>true</code> if the passwords keystore is enabled,
     *         <code>false</code> if not.
     */
    public static boolean isPasswordsKeystoreEnabled()
    {
        return ConnectionCorePlugin.getDefault().getPluginPreferences()
            .getInt( ConnectionCoreConstants.PREFERENCE_CONNECTIONS_PASSWORDS_KEYSTORE ) == ConnectionCoreConstants.PREFERENCE_CONNECTIONS_PASSWORDS_KEYSTORE_ON;
    }


    /**
     * Asks the user to load the keystore.
     *
     * @return <code>true</code> if the keystore was loaded,
     *         <code>false</code> if not.
     */
    public static boolean askUserToLoadKeystore()
    {
        final boolean keystoreLoaded[] = new boolean[1];
        keystoreLoaded[0] = false;

        PlatformUI.getWorkbench().getDisplay().syncExec( new Runnable()
        {
            public void run()
            {
                while ( true )
                {
                    // Getting the shell
                    Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();

                    // We ask the user for the keystore password
                    PasswordDialog passwordDialog = new PasswordDialog( shell, Messages
                        .getString( "PasswordsKeyStoreManagerUtils.VerifyMasterPassword" ), //$NON-NLS-1$
                        Messages.getString( "PasswordsKeyStoreManagerUtils.PleaseEnterMasterPassword" ), null ); //$NON-NLS-1$

                    if ( passwordDialog.open() == PasswordDialog.CANCEL )
                    {
                        // The user cancelled the action
                        keystoreLoaded[0] = false;
                        return;
                    }

                    // Getting the password
                    String masterPassword = passwordDialog.getPassword();

                    // Checking the password
                    Exception checkPasswordException = null;
                    try
                    {
                        if ( ConnectionCorePlugin.getDefault().getPasswordsKeyStoreManager()
                            .checkMasterPassword( masterPassword ) )
                        {
                            keystoreLoaded[0] = true;
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
                        message = Messages
                            .getString( "PasswordsKeyStoreManagerUtils.MasterPasswordVerificationFailedWithException" ) //$NON-NLS-1$
                            + checkPasswordException.getMessage();
                    }
                    else
                    {
                        message = Messages.getString( "PasswordsKeyStoreManagerUtils.MasterPasswordVerificationFailed" ); //$NON-NLS-1$
                    }

                    // We ask the user if he wants to retry to unlock the passwords keystore
                    MessageDialog errorDialog = new MessageDialog(
                        shell,
                        Messages.getString( "PasswordsKeyStoreManagerUtils.VerifyMasterPasswordFailed" ), null, message, MessageDialog.ERROR, new String[] //$NON-NLS-1$
                            { IDialogConstants.RETRY_LABEL,
                                IDialogConstants.CANCEL_LABEL }, 0 );

                    if ( errorDialog.open() == MessageDialog.CANCEL )
                    {
                        // The user cancelled the action
                        keystoreLoaded[0] = false;
                        return;
                    }
                }
            }
        } );

        return keystoreLoaded[0];
    }
}
