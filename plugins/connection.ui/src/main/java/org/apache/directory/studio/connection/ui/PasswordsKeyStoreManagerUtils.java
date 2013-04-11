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
                    PasswordDialog passwordDialog = new PasswordDialog( shell, "Verify Master Password",
                        "Please enter your master password:", null );

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
                        message = "The master password verification failed.\n\nThe following exception was raised:\n"
                            + checkPasswordException.getMessage();
                    }
                    else
                    {
                        message = "The master password verification failed.";
                    }

                    // We ask the user if he wants to retry to unlock the passwords keystore
                    MessageDialog errorDialog = new MessageDialog( shell,
                        "Verify Master Password Failed", null, message, MessageDialog.ERROR, new String[]
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
