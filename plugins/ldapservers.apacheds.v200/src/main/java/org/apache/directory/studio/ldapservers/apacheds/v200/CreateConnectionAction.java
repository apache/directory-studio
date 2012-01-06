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

package org.apache.directory.studio.ldapservers.apacheds.v200;


import org.apache.directory.server.config.beans.ConfigBean;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.apache.directory.studio.connection.core.ConnectionParameter.AuthenticationMethod;
import org.apache.directory.studio.connection.core.ConnectionParameter.EncryptionMethod;
import org.apache.directory.studio.connection.core.ConnectionParameter.NetworkProvider;
import org.apache.directory.studio.ldapservers.actions.CreateConnectionActionHelper;
import org.apache.directory.studio.ldapservers.model.LdapServer;
import org.apache.directory.studio.ldapservers.views.ServersView;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;


/**
 * This class implements the create connection action for an ApacheDS 2.0.0 server.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CreateConnectionAction implements IObjectActionDelegate
{
    private static final String EXTENSION_ID = "org.apache.directory.server.2.0.0"; //$NON-NLS-1$

    /** The {@link ServersView} */
    private ServersView view;


    /**
     * {@inheritDoc}
     */
    public void run( IAction action )
    {
        if ( view != null )
        {
            // Getting the selection
            StructuredSelection selection = ( StructuredSelection ) view.getViewer().getSelection();
            if ( ( !selection.isEmpty() ) && ( selection.size() == 1 ) )
            {
                // Getting the server
                LdapServer server = ( LdapServer ) selection.getFirstElement();

                // Checking that the server is really an ApacheDS 2.0.0 server
                if ( !EXTENSION_ID.equalsIgnoreCase( server.getLdapServerAdapterExtension().getId() ) )
                {
                    String message = Messages.getString( "CreateConnectionAction.UnableReadServerConfiguration" ) //$NON-NLS-1$
                        + "\n\n" //$NON-NLS-1$
                        + Messages.getString( "CreateConnectionAction.NotA200Server" ); //$NON-NLS-1$

                    reportErrorReadingServerConfiguration( view, message );
                    return;
                }

                // Parsing the 'config.ldif' file
                ConfigBean configuration = null;
                try
                {
                    configuration = ApacheDS200LdapServerAdapter.getServerConfiguration( server );
                }
                catch ( Exception e )
                {
                    String message = Messages.getString( "CreateConnectionAction.UnableReadServerConfiguration" ) //$NON-NLS-1$
                        + "\n\n" //$NON-NLS-1$
                        + Messages.getString( "CreateConnectionAction.FollowingErrorOccurred" ) + e.getMessage(); //$NON-NLS-1$

                    reportErrorReadingServerConfiguration( view, message );
                    return;
                }

                // Checking if we could read the 'server.xml' file
                if ( configuration == null )
                {
                    reportErrorReadingServerConfiguration( view,
                        Messages.getString( "CreateConnectionAction.UnableReadServerConfiguration" ) ); //$NON-NLS-1$
                    return;
                }

                // Checking is LDAP and/or LDAPS is/are enabled
                if ( ( ApacheDS200LdapServerAdapter.isEnableLdap( configuration ) )
                    || ( ApacheDS200LdapServerAdapter.isEnableLdaps( configuration ) ) )
                {
                    // Creating the connection using the helper class
                    createConnection( server, configuration );
                }
                else
                {
                    // LDAP and LDAPS protocols are disabled, we report this error to the user
                    MessageDialog dialog = new MessageDialog( view.getSite().getShell(),
                        Messages.getString( "CreateConnectionAction.UnableCreateConnection" ), null, //$NON-NLS-1$
                        Messages.getString( "CreateConnectionAction.LDAPAndLDAPSDisabled" ), MessageDialog.ERROR, //$NON-NLS-1$
                        new String[]
                            { IDialogConstants.OK_LABEL }, MessageDialog.OK );
                    dialog.open();
                }
            }
        }
    }


    /**
     * Reports to the user an error message indicating the server 
     * configuration could not be read correctly.
     *
     * @param message
     *      the message
     */
    private void reportErrorReadingServerConfiguration( ServersView view, String message )
    {
        MessageDialog dialog = new MessageDialog( view.getSite().getShell(),
            Messages.getString( "CreateConnectionAction.UnableReadServerConfiguration" ), //$NON-NLS-1$
            null, message, MessageDialog.ERROR, new String[]
                { IDialogConstants.OK_LABEL }, MessageDialog.OK );
        dialog.open();
    }


    /**
     * Creates the connection
     */
    private void createConnection( LdapServer server, ConfigBean configuration )
    {
        // Creating the connection parameter object
        ConnectionParameter connectionParameter = new ConnectionParameter();

        // Authentication method
        connectionParameter.setAuthMethod( AuthenticationMethod.SIMPLE );

        // LDAP or LDAPS?
        if ( ApacheDS200LdapServerAdapter.isEnableLdap( configuration ) )
        {
            connectionParameter.setEncryptionMethod( EncryptionMethod.NONE );
            connectionParameter.setPort( ApacheDS200LdapServerAdapter.getLdapPort( configuration ) );
        }
        else if ( ApacheDS200LdapServerAdapter.isEnableLdaps( configuration ) )
        {
            connectionParameter.setEncryptionMethod( EncryptionMethod.LDAPS );
            connectionParameter.setPort( ApacheDS200LdapServerAdapter.getLdapsPort( configuration ) );
        }

        // Bind password
        connectionParameter.setBindPassword( "secret" ); //$NON-NLS-1$

        // Bind principal
        connectionParameter.setBindPrincipal( "uid=admin,ou=system" ); //$NON-NLS-1$

        // Host
        connectionParameter.setHost( "localhost" ); //$NON-NLS-1$

        // Name
        connectionParameter.setName( server.getName() );

        // Network Provider
        connectionParameter.setNetworkProvider( ConnectionCorePlugin.getDefault().getDefaultNetworkProvider() );

        // Creating the connection
        CreateConnectionActionHelper.createLdapBrowserConnection( server, new Connection( connectionParameter ) );
    }


    /**
     * {@inheritDoc}
     */
    public void selectionChanged( IAction action, ISelection selection )
    {
        // Nothing to do
    }


    /**
     * {@inheritDoc}
     */
    public void setActivePart( IAction action, IWorkbenchPart targetPart )
    {
        // Storing the Servers view
        if ( targetPart instanceof ServersView )
        {
            view = ( ServersView ) targetPart;
        }
    }
}
