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
package org.apache.directory.studio.apacheds.actions;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.apacheds.ApacheDsPlugin;
import org.apache.directory.studio.apacheds.ApacheDsPluginConstants;
import org.apache.directory.studio.apacheds.ApacheDsPluginUtils;
import org.apache.directory.studio.apacheds.configuration.model.ServerConfiguration;
import org.apache.directory.studio.apacheds.configuration.model.ServerXmlIOException;
import org.apache.directory.studio.apacheds.configuration.model.v153.ServerConfigurationV153;
import org.apache.directory.studio.apacheds.configuration.model.v154.ServerConfigurationV154;
import org.apache.directory.studio.apacheds.configuration.model.v155.ServerConfigurationV155;
import org.apache.directory.studio.apacheds.configuration.model.v156.ServerConfigurationV156;
import org.apache.directory.studio.apacheds.jobs.LaunchServerJob;
import org.apache.directory.studio.apacheds.model.Server;
import org.apache.directory.studio.apacheds.views.ServersView;
import org.apache.mina.util.AvailablePortFinder;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;


/**
 * This class implements the run action for a server.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class RunAction extends Action implements IWorkbenchWindowActionDelegate
{
    /** The associated view */
    private ServersView view;


    /**
     * Creates a new instance of RunAction.
     */
    public RunAction()
    {
        super( Messages.getString( "RunAction.Run" ) ); //$NON-NLS-1$
        init();
    }


    /**
     * Creates a new instance of RunAction.
     *
     * @param view
     *      the associated view
     */
    public RunAction( ServersView view )
    {
        super( Messages.getString( "RunAction.Run" ) ); //$NON-NLS-1$
        this.view = view;
        init();
    }


    /**
     * Initializes the action.
     */
    private void init()
    {
        setId( ApacheDsPluginConstants.CMD_RUN );
        setActionDefinitionId( ApacheDsPluginConstants.CMD_RUN );
        setToolTipText( Messages.getString( "RunAction.RunToolTip" ) ); //$NON-NLS-1$
        setImageDescriptor( ApacheDsPlugin.getDefault().getImageDescriptor( ApacheDsPluginConstants.IMG_RUN ) );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run()
    {
        if ( view != null )
        {
            // Getting the selection
            StructuredSelection selection = ( StructuredSelection ) view.getViewer().getSelection();
            if ( ( !selection.isEmpty() ) && ( selection.size() == 1 ) )
            {
                // Getting the server
                final Server server = ( Server ) selection.getFirstElement();

                // Parsing the 'server.xml' file
                ServerConfiguration serverConfiguration = null;
                try
                {
                    serverConfiguration = ApacheDsPluginUtils.getServerConfiguration( server );
                }
                catch ( IOException e )
                {
                    reportErrorReadingServerConfiguration( e.getMessage() );
                    return;
                }
                catch ( ServerXmlIOException e )
                {
                    reportErrorReadingServerConfiguration( e.getMessage() );
                    return;
                }

                // Checking if we could read the 'server.xml' file
                if ( serverConfiguration == null )
                {
                    reportErrorReadingServerConfiguration( null );
                    return;
                }

                // Verifying if the protocol ports are currently available
                String[] alreadyInUseProtocolPortsList = getAlreadyInUseProtocolPorts( serverConfiguration );
                if ( ( alreadyInUseProtocolPortsList != null ) && ( alreadyInUseProtocolPortsList.length > 0 ) )
                {
                    String title = null;
                    String message = null;

                    if ( alreadyInUseProtocolPortsList.length == 1 )
                    {
                        title = Messages.getString( "RunAction.PortInUse" ); //$NON-NLS-1$
                        message = NLS
                            .bind(
                                Messages.getString( "RunAction.PortOfProtocolInUse" ), new String[] { alreadyInUseProtocolPortsList[0] } ); //$NON-NLS-1$
                    }
                    else
                    {
                        title = Messages.getString( "RunAction.PortsInUse" ); //$NON-NLS-1$
                        message = Messages.getString( "RunAction.PortsOfProtocolsInUse" ); //$NON-NLS-1$
                        for ( String alreadyInUseProtocolPort : alreadyInUseProtocolPortsList )
                        {
                            message += ApacheDsPluginUtils.LINE_SEPARATOR + "    - " + alreadyInUseProtocolPort; //$NON-NLS-1$
                        }
                    }

                    message += ApacheDsPluginUtils.LINE_SEPARATOR + ApacheDsPluginUtils.LINE_SEPARATOR
                        + Messages.getString( "RunAction.Continue" ); //$NON-NLS-1$

                    MessageDialog dialog = new MessageDialog( view.getSite().getShell(), title, null, message,
                        MessageDialog.WARNING, new String[]
                            { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, MessageDialog.OK );
                    if ( dialog.open() == MessageDialog.CANCEL )
                    {
                        return;
                    }
                }

                // Verifying the libraries in the plugin's folder
                ApacheDsPluginUtils.verifyLibrariesFolder( server );

                // Creating, setting and launching the launch job
                LaunchServerJob job = new LaunchServerJob( server, serverConfiguration );
                job.setLogsLevel( ApacheDsPluginUtils.getServerLogsLevel() );
                job.setLogsPattern( ApacheDsPluginUtils.getServerLogsPattern() );
                server.setLaunchJob( job );
                job.schedule();
            }
        }
    }


    /**
     * Reports to the user an error message indicating the server 
     * configuration could not be read correctly.
     *
     * @param errorMessage
     *      an error message which can be <code>null</code>
     */
    private void reportErrorReadingServerConfiguration( String errorMessage )
    {
        String message = null;

        if ( errorMessage == null )
        {
            message = Messages.getString( "RunAction.UnableReadServerConfiguration" ); //$NON-NLS-1$
        }
        else
        {
            message = Messages.getString( "RunAction.UnableReadServerConfiguration" ) + ApacheDsPluginUtils.LINE_SEPARATOR //$NON-NLS-1$
                + ApacheDsPluginUtils.LINE_SEPARATOR
                + Messages.getString( "RunAction.FollowingErrorOccurred" ) + errorMessage; //$NON-NLS-1$
        }

        MessageDialog dialog = new MessageDialog( view.getSite().getShell(), Messages
            .getString( "RunAction.UnableReadServerConfiguration" ), //$NON-NLS-1$
            null, message, MessageDialog.ERROR, new String[]
                { IDialogConstants.OK_LABEL }, MessageDialog.OK );
        dialog.open();
    }


    /**
     * Gets an array of String containing the ports and their associated 
     * protocols which are already in use.
     *
     * @param serverConfiguration
     *      the server configuration
     * @return
     *      an array of String containing the ports and their associated 
     * protocols which are already in use.
     */
    private String[] getAlreadyInUseProtocolPorts( ServerConfiguration serverConfiguration )
    {
        // Version 1.5.6
        if ( serverConfiguration instanceof ServerConfigurationV156 )
        {
            return getAlreadyInUseProtocolPortsVersion156( ( ServerConfigurationV156 ) serverConfiguration );
        }
        // Version 1.5.5
        if ( serverConfiguration instanceof ServerConfigurationV155 )
        {
            return getAlreadyInUseProtocolPortsVersion155( ( ServerConfigurationV155 ) serverConfiguration );
        }
        // Version 1.5.4
        else if ( serverConfiguration instanceof ServerConfigurationV154 )
        {
            return getAlreadyInUseProtocolPortsVersion154( ( ServerConfigurationV154 ) serverConfiguration );
        }
        // Version 1.5.3
        else if ( serverConfiguration instanceof ServerConfigurationV153 )
        {
            return getAlreadyInUseProtocolPortsVersion153( ( ServerConfigurationV153 ) serverConfiguration );
        }
        else
        {
            return new String[0];
        }
    }


    /**
     * Gets an array of String containing the ports and their associated 
     * protocols which are already in use.
     *
     * @param serverConfiguration
     *      the 1.5.3 server configuration
     * @return
     *      an array of String containing the ports and their associated 
     * protocols which are already in use.
     */
    private String[] getAlreadyInUseProtocolPortsVersion153( ServerConfigurationV153 serverConfiguration )
    {
        List<String> alreadyInUseProtocolPortsList = new ArrayList<String>();

        // LDAP
        if ( serverConfiguration.isEnableLdap() )
        {
            if ( !AvailablePortFinder.available( serverConfiguration.getLdapPort() ) )
            {
                alreadyInUseProtocolPortsList.add( NLS.bind(
                    Messages.getString( "RunAction.LDAPPort" ), new Object[] { serverConfiguration.getLdapPort() } ) ); //$NON-NLS-1$
            }
        }

        // LDAPS
        if ( serverConfiguration.isEnableLdaps() )
        {
            if ( !AvailablePortFinder.available( serverConfiguration.getLdapsPort() ) )
            {
                alreadyInUseProtocolPortsList.add( NLS.bind(
                    Messages.getString( "RunAction.LDAPSPort" ), new Object[] { serverConfiguration.getLdapsPort() } ) ); //$NON-NLS-1$
            }
        }

        // Kerberos
        if ( serverConfiguration.isEnableKerberos() )
        {
            if ( !AvailablePortFinder.available( serverConfiguration.getKerberosPort() ) )
            {
                alreadyInUseProtocolPortsList
                    .add( NLS
                        .bind(
                            Messages.getString( "RunAction.KerberosPort" ), new Object[] { serverConfiguration.getKerberosPort() } ) ); //$NON-NLS-1$
            }
        }

        // DNS
        if ( serverConfiguration.isEnableDns() )
        {
            if ( !AvailablePortFinder.available( serverConfiguration.getDnsPort() ) )
            {
                alreadyInUseProtocolPortsList.add( NLS.bind(
                    Messages.getString( "RunAction.DNSPort" ), new Object[] { serverConfiguration.getDnsPort() } ) ); //$NON-NLS-1$
            }
        }

        // NTP
        if ( serverConfiguration.isEnableNtp() )
        {
            if ( !AvailablePortFinder.available( serverConfiguration.getNtpPort() ) )
            {
                alreadyInUseProtocolPortsList.add( NLS.bind(
                    Messages.getString( "RunAction.NTPPort" ), new Object[] { serverConfiguration.getNtpPort() } ) ); //$NON-NLS-1$
            }
        }

        // Change Password
        if ( serverConfiguration.isEnableChangePassword() )
        {
            if ( !AvailablePortFinder.available( serverConfiguration.getChangePasswordPort() ) )
            {
                alreadyInUseProtocolPortsList
                    .add( NLS
                        .bind(
                            Messages.getString( "RunAction.ChangePasswordPort" ), new Object[] { serverConfiguration.getChangePasswordPort() } ) ); //$NON-NLS-1$
            }
        }

        return alreadyInUseProtocolPortsList.toArray( new String[0] );
    }


    /**
     * Gets an array of String containing the ports and their associated 
     * protocols which are already in use.
     *
     * @param serverConfiguration
     *      the 1.5.4 server configuration
     * @return
     *      an array of String containing the ports and their associated 
     * protocols which are already in use.
     */
    private String[] getAlreadyInUseProtocolPortsVersion154( ServerConfigurationV154 serverConfiguration )
    {
        List<String> alreadyInUseProtocolPortsList = new ArrayList<String>();

        // LDAP
        if ( serverConfiguration.isEnableLdap() )
        {
            if ( !AvailablePortFinder.available( serverConfiguration.getLdapPort() ) )
            {
                alreadyInUseProtocolPortsList.add( NLS.bind(
                    Messages.getString( "RunAction.LDAPPort" ), new Object[] { serverConfiguration.getLdapPort() } ) ); //$NON-NLS-1$
            }
        }

        // LDAPS
        if ( serverConfiguration.isEnableLdaps() )
        {
            if ( !AvailablePortFinder.available( serverConfiguration.getLdapsPort() ) )
            {
                alreadyInUseProtocolPortsList.add( NLS.bind(
                    Messages.getString( "RunAction.LDAPSPort" ), new Object[] { serverConfiguration.getLdapsPort() } ) ); //$NON-NLS-1$
            }
        }

        // Kerberos
        if ( serverConfiguration.isEnableKerberos() )
        {
            if ( !AvailablePortFinder.available( serverConfiguration.getKerberosPort() ) )
            {
                alreadyInUseProtocolPortsList
                    .add( NLS
                        .bind(
                            Messages.getString( "RunAction.KerberosPort" ), new Object[] { serverConfiguration.getKerberosPort() } ) ); //$NON-NLS-1$
            }
        }

        // DNS
        if ( serverConfiguration.isEnableDns() )
        {
            if ( !AvailablePortFinder.available( serverConfiguration.getDnsPort() ) )
            {
                alreadyInUseProtocolPortsList.add( NLS.bind(
                    Messages.getString( "RunAction.DNSPort" ), new Object[] { serverConfiguration.getDnsPort() } ) ); //$NON-NLS-1$
            }
        }

        // NTP
        if ( serverConfiguration.isEnableNtp() )
        {
            if ( !AvailablePortFinder.available( serverConfiguration.getNtpPort() ) )
            {
                alreadyInUseProtocolPortsList.add( NLS.bind( Messages.getString( "RunAction.NTPPort" ), new Object[] //$NON-NLS-1$
                    { serverConfiguration.getNtpPort() } ) );
            }
        }

        // Change Password
        if ( serverConfiguration.isEnableChangePassword() )
        {
            if ( !AvailablePortFinder.available( serverConfiguration.getChangePasswordPort() ) )
            {
                alreadyInUseProtocolPortsList
                    .add( NLS
                        .bind(
                            Messages.getString( "RunAction.ChangePasswordPort" ), new Object[] { serverConfiguration.getChangePasswordPort() } ) ); //$NON-NLS-1$
            }
        }

        return alreadyInUseProtocolPortsList.toArray( new String[0] );
    }


    /**
     * Gets an array of String containing the ports and their associated 
     * protocols which are already in use.
     *
     * @param serverConfiguration
     *      the 1.5.5 server configuration
     * @return
     *      an array of String containing the ports and their associated 
     * protocols which are already in use.
     */
    private String[] getAlreadyInUseProtocolPortsVersion155( ServerConfigurationV155 serverConfiguration )
    {
        List<String> alreadyInUseProtocolPortsList = new ArrayList<String>();

        // LDAP
        if ( serverConfiguration.isEnableLdap() )
        {
            if ( !AvailablePortFinder.available( serverConfiguration.getLdapPort() ) )
            {
                alreadyInUseProtocolPortsList.add( NLS.bind(
                    Messages.getString( "RunAction.LDAPPort" ), new Object[] { serverConfiguration.getLdapPort() } ) ); //$NON-NLS-1$
            }
        }

        // LDAPS
        if ( serverConfiguration.isEnableLdaps() )
        {
            if ( !AvailablePortFinder.available( serverConfiguration.getLdapsPort() ) )
            {
                alreadyInUseProtocolPortsList.add( NLS.bind(
                    Messages.getString( "RunAction.LDAPSPort" ), new Object[] { serverConfiguration.getLdapsPort() } ) ); //$NON-NLS-1$
            }
        }

        // Kerberos
        if ( serverConfiguration.isEnableKerberos() )
        {
            if ( !AvailablePortFinder.available( serverConfiguration.getKerberosPort() ) )
            {
                alreadyInUseProtocolPortsList
                    .add( NLS
                        .bind(
                            Messages.getString( "RunAction.KerberosPort" ), new Object[] { serverConfiguration.getKerberosPort() } ) ); //$NON-NLS-1$
            }
        }

        // DNS
        if ( serverConfiguration.isEnableDns() )
        {
            if ( !AvailablePortFinder.available( serverConfiguration.getDnsPort() ) )
            {
                alreadyInUseProtocolPortsList.add( NLS.bind(
                    Messages.getString( "RunAction.DNSPort" ), new Object[] { serverConfiguration.getDnsPort() } ) ); //$NON-NLS-1$
            }
        }

        // NTP
        if ( serverConfiguration.isEnableNtp() )
        {
            if ( !AvailablePortFinder.available( serverConfiguration.getNtpPort() ) )
            {
                alreadyInUseProtocolPortsList.add( NLS.bind( Messages.getString( "RunAction.NTPPort" ), new Object[] //$NON-NLS-1$
                    { serverConfiguration.getNtpPort() } ) );
            }
        }

        // Change Password
        if ( serverConfiguration.isEnableChangePassword() )
        {
            if ( !AvailablePortFinder.available( serverConfiguration.getChangePasswordPort() ) )
            {
                alreadyInUseProtocolPortsList
                    .add( NLS
                        .bind(
                            Messages.getString( "RunAction.ChangePasswordPort" ), new Object[] { serverConfiguration.getChangePasswordPort() } ) ); //$NON-NLS-1$
            }
        }

        return alreadyInUseProtocolPortsList.toArray( new String[0] );
    }


    /**
     * Gets an array of String containing the ports and their associated 
     * protocols which are already in use.
     *
     * @param serverConfiguration
     *      the 1.5.6 server configuration
     * @return
     *      an array of String containing the ports and their associated 
     * protocols which are already in use.
     */
    private String[] getAlreadyInUseProtocolPortsVersion156( ServerConfigurationV156 serverConfiguration )
    {
        List<String> alreadyInUseProtocolPortsList = new ArrayList<String>();

        // LDAP
        if ( serverConfiguration.isEnableLdap() )
        {
            if ( !AvailablePortFinder.available( serverConfiguration.getLdapPort() ) )
            {
                alreadyInUseProtocolPortsList.add( NLS.bind(
                    Messages.getString( "RunAction.LDAPPort" ), new Object[] { serverConfiguration.getLdapPort() } ) ); //$NON-NLS-1$
            }
        }

        // LDAPS
        if ( serverConfiguration.isEnableLdaps() )
        {
            if ( !AvailablePortFinder.available( serverConfiguration.getLdapsPort() ) )
            {
                alreadyInUseProtocolPortsList.add( NLS.bind(
                    Messages.getString( "RunAction.LDAPSPort" ), new Object[] { serverConfiguration.getLdapsPort() } ) ); //$NON-NLS-1$
            }
        }

        // Kerberos
        if ( serverConfiguration.isEnableKerberos() )
        {
            if ( !AvailablePortFinder.available( serverConfiguration.getKerberosPort() ) )
            {
                alreadyInUseProtocolPortsList
                    .add( NLS
                        .bind(
                            Messages.getString( "RunAction.KerberosPort" ), new Object[] { serverConfiguration.getKerberosPort() } ) ); //$NON-NLS-1$
            }
        }

        // DNS
        if ( serverConfiguration.isEnableDns() )
        {
            if ( !AvailablePortFinder.available( serverConfiguration.getDnsPort() ) )
            {
                alreadyInUseProtocolPortsList.add( NLS.bind(
                    Messages.getString( "RunAction.DNSPort" ), new Object[] { serverConfiguration.getDnsPort() } ) ); //$NON-NLS-1$
            }
        }

        // NTP
        if ( serverConfiguration.isEnableNtp() )
        {
            if ( !AvailablePortFinder.available( serverConfiguration.getNtpPort() ) )
            {
                alreadyInUseProtocolPortsList.add( NLS.bind( Messages.getString( "RunAction.NTPPort" ), new Object[] //$NON-NLS-1$
                    { serverConfiguration.getNtpPort() } ) );
            }
        }

        // Change Password
        if ( serverConfiguration.isEnableChangePassword() )
        {
            if ( !AvailablePortFinder.available( serverConfiguration.getChangePasswordPort() ) )
            {
                alreadyInUseProtocolPortsList
                    .add( NLS
                        .bind(
                            Messages.getString( "RunAction.ChangePasswordPort" ), new Object[] { serverConfiguration.getChangePasswordPort() } ) ); //$NON-NLS-1$
            }
        }

        return alreadyInUseProtocolPortsList.toArray( new String[0] );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run( IAction action )
    {
        run();
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
     */
    public void dispose()
    {
        // Nothing to do
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init( IWorkbenchWindow window )
    {
        // Nothing to do
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged( IAction action, ISelection selection )
    {
        // Nothing to do
    }
}
