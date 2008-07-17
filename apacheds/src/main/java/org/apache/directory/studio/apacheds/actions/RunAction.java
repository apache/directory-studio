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


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.apacheds.ApacheDsPlugin;
import org.apache.directory.studio.apacheds.ApacheDsPluginConstants;
import org.apache.directory.studio.apacheds.ApacheDsPluginUtils;
import org.apache.directory.studio.apacheds.configuration.model.ServerXmlIOException;
import org.apache.directory.studio.apacheds.configuration.model.v153.ServerConfigurationV153;
import org.apache.directory.studio.apacheds.configuration.model.v153.ServerXmlIOV153;
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
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;


/**
 * This class implements the run action for a server.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class RunAction extends Action implements IWorkbenchWindowActionDelegate
{
    private static final String ACTION_TEXT = "&Run";

    /** The associated view */
    private ServersView view;


    /**
     * Creates a new instance of RunAction.
     */
    public RunAction()
    {
        super( ACTION_TEXT );
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
        super( ACTION_TEXT );
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
        setToolTipText( "Run" );
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
                ServerXmlIOV153 serverXmlIOV153 = new ServerXmlIOV153();
                ServerConfigurationV153 serverConfiguration = null;
                try
                {
                    serverConfiguration = ( ServerConfigurationV153 ) serverXmlIOV153.parse( new FileInputStream(
                        new File( ApacheDsPluginUtils.getApacheDsServersFolder().append( server.getId() ).append(
                            "conf" ).append( "server.xml" ).toOSString() ) ) );
                }
                catch ( FileNotFoundException e )
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
                        title = "Port already in use";
                        message = "The port of the protocol " + alreadyInUseProtocolPortsList[0]
                            + " is already in use.";
                    }
                    else
                    {
                        title = "Ports already in use";
                        message = "The ports of the following protocols are already in use:";
                        for ( String alreadyInUseProtocolPort : alreadyInUseProtocolPortsList )
                        {
                            message += ApacheDsPluginUtils.LINE_SEPARATOR + "    - " + alreadyInUseProtocolPort;
                        }
                    }

                    message += ApacheDsPluginUtils.LINE_SEPARATOR + ApacheDsPluginUtils.LINE_SEPARATOR
                        + "Do you wish to continue?";

                    MessageDialog dialog = new MessageDialog( view.getSite().getShell(), title, null, message,
                        MessageDialog.WARNING, new String[]
                            { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, MessageDialog.OK );
                    if ( dialog.open() == MessageDialog.CANCEL )
                    {
                        return;
                    }
                }

                // Verifying the libraries in the plugin's folder
                ApacheDsPluginUtils.verifyLibrariesFolder();

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
            message = "Unable to read the server configuration.";
        }
        else
        {
            message = "Unable to read the server configuration." + ApacheDsPluginUtils.LINE_SEPARATOR
                + ApacheDsPluginUtils.LINE_SEPARATOR + "The following error occurred: " + errorMessage;
        }

        MessageDialog dialog = new MessageDialog( view.getSite().getShell(), "Unable to read the server configuration",
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
    private String[] getAlreadyInUseProtocolPorts( ServerConfigurationV153 serverConfiguration )
    {
        List<String> alreadyInUseProtocolPortsList = new ArrayList<String>();

        // LDAP
        if ( serverConfiguration.isEnableLdap() )
        {
            if ( !AvailablePortFinder.available( serverConfiguration.getLdapPort() ) )
            {
                alreadyInUseProtocolPortsList.add( "LDAP (port " + serverConfiguration.getLdapPort() + ")" );
            }
        }

        // LDAPS
        if ( serverConfiguration.isEnableLdaps() )
        {
            if ( !AvailablePortFinder.available( serverConfiguration.getLdapsPort() ) )
            {
                alreadyInUseProtocolPortsList.add( "LDAPS (port " + serverConfiguration.getLdapsPort() + ")" );
            }
        }
        // Kerberos
        if ( serverConfiguration.isEnableKerberos() )
        {
            if ( !AvailablePortFinder.available( serverConfiguration.getKerberosPort() ) )
            {
                alreadyInUseProtocolPortsList.add( "Kerberos (port " + serverConfiguration.getKerberosPort() + ")" );
            }
        }

        // DNS
        if ( serverConfiguration.isEnableDns() )
        {
            if ( !AvailablePortFinder.available( serverConfiguration.getDnsPort() ) )
            {
                alreadyInUseProtocolPortsList.add( "DNS (port " + serverConfiguration.getDnsPort() + ")" );
            }
        }

        // NTP
        if ( serverConfiguration.isEnableNtp() )
        {
            if ( !AvailablePortFinder.available( serverConfiguration.getNtpPort() ) )
            {
                alreadyInUseProtocolPortsList.add( "NTP (port " + serverConfiguration.getNtpPort() + ")" );
            }
        }

        // Change Password
        if ( serverConfiguration.isEnableChangePassword() )
        {
            if ( !AvailablePortFinder.available( serverConfiguration.getChangePasswordPort() ) )
            {
                alreadyInUseProtocolPortsList.add( "ChangePassword (port "
                    + serverConfiguration.getChangePasswordPort() + ")" );
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
