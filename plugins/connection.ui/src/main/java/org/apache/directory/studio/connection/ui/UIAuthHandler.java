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


import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.apache.directory.studio.connection.core.Credentials;
import org.apache.directory.studio.connection.core.IAuthHandler;
import org.apache.directory.studio.connection.core.ICredentials;
import org.apache.directory.studio.connection.core.PasswordsKeyStoreManager;
import org.apache.directory.studio.connection.ui.dialogs.PasswordDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;


/**
 * Default authentication handler that ask for the password using
 * a UI dialog.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class UIAuthHandler implements IAuthHandler
{

    /**
     * {@inheritDoc}
     */
    public ICredentials getCredentials( final ConnectionParameter connectionParameter )
    {
        // Checking if the bind principal is null or empty (no authentication)
        if ( connectionParameter.getBindPrincipal() == null || "".equals( connectionParameter.getBindPrincipal() ) ) //$NON-NLS-1$
        {
            return new Credentials( "", "", connectionParameter ); //$NON-NLS-1$ //$NON-NLS-2$
        }
        else
        {
            // Checking of the connection passwords keystore is enabled
            if ( PasswordsKeyStoreManagerUtils.isPasswordsKeystoreEnabled() )
            {
                // Getting the passwords keystore manager
                PasswordsKeyStoreManager passwordsKeyStoreManager = ConnectionCorePlugin.getDefault()
                    .getPasswordsKeyStoreManager();

                // Checking if the keystore is not loaded 
                if ( !passwordsKeyStoreManager.isLoaded() )
                {
                    // Asking the user to load the keystore
                    if ( !PasswordsKeyStoreManagerUtils.askUserToLoadKeystore() )
                    {
                        // The user failed to load the keystore and cancelled
                        return null;
                    }
                }

                // Getting the password
                String password = passwordsKeyStoreManager.getConnectionPassword( connectionParameter.getId() );

                // Checking if the bind password is available (the user chose to store the password)
                if ( password != null
                    && !"".equals( password ) ) //$NON-NLS-1$
                {
                    return new Credentials( connectionParameter.getBindPrincipal(),
                        password, connectionParameter );
                }
                // The user chose NOT to store the password, we need to ask him
                else
                {
                    return askConnectionPassword( connectionParameter );
                }
            }
            // Connection passwords keystore is NOT enabled
            else
            {
                // Checking if the bind password is available (the user chose to store the password)
                if ( connectionParameter.getBindPassword() != null
                    && !"".equals( connectionParameter.getBindPassword() ) ) //$NON-NLS-1$
                {
                    return new Credentials( connectionParameter.getBindPrincipal(),
                        connectionParameter.getBindPassword(), connectionParameter );
                }
                // The user chose NOT to store the password, we need to ask him
                else
                {
                    return askConnectionPassword( connectionParameter );
                }
            }
        }
    }


    /**
     * Asks the user for the connection password.
     *
     * @param connectionParameter the connection parameter
     * @return the corresponding credentials
     */
    private Credentials askConnectionPassword( final ConnectionParameter connectionParameter )
    {
        final String[] password = new String[1];

        PlatformUI.getWorkbench().getDisplay().syncExec( new Runnable()
        {
            public void run()
            {
                PasswordDialog dialog = new PasswordDialog(
                    PlatformUI.getWorkbench().getDisplay().getActiveShell(),
                    NLS.bind(
                        Messages.getString( "UIAuthHandler.EnterPasswordFor" ), new String[] { connectionParameter.getName() } ), //$NON-NLS-1$
                    NLS.bind(
                        Messages.getString( "UIAuthHandler.PleaseEnterPasswordOfUser" ), connectionParameter.getBindPrincipal() ), "" ); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$

                if ( dialog.open() == PasswordDialog.OK )
                {
                    password[0] = dialog.getPassword();
                }
            }
        } );

        if ( password[0] != null )
        {
            return new Credentials( connectionParameter.getBindPrincipal(), password[0],
                connectionParameter );
        }

        return null;
    }
}
