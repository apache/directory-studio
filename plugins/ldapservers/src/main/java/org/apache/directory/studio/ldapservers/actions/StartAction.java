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
package org.apache.directory.studio.ldapservers.actions;


import org.apache.directory.studio.ldapservers.LdapServersPlugin;
import org.apache.directory.studio.ldapservers.LdapServersPluginConstants;
import org.apache.directory.studio.ldapservers.jobs.StartLdapServerRunnable;
import org.apache.directory.studio.ldapservers.jobs.StudioLdapServerJob;
import org.apache.directory.studio.ldapservers.model.LdapServer;
import org.apache.directory.studio.ldapservers.model.LdapServerAdapter;
import org.apache.directory.studio.ldapservers.model.LdapServerAdapterExtension;
import org.apache.directory.studio.ldapservers.views.ServersView;
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
 * This class implements the start action for a server.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StartAction extends Action implements IWorkbenchWindowActionDelegate
{
    /** The associated view */
    private ServersView view;


    /**
     * Creates a new instance of StartAction.
     */
    public StartAction()
    {
        super( Messages.getString( "StartAction.Start" ) ); //$NON-NLS-1$
        init();
    }


    /**
     * Creates a new instance of StartAction.
     *
     * @param view
     *      the associated view
     */
    public StartAction( ServersView view )
    {
        super( Messages.getString( "StartAction.Start" ) ); //$NON-NLS-1$
        this.view = view;
        init();
    }


    /**
     * Initializes the action.
     */
    private void init()
    {
        setId( LdapServersPluginConstants.CMD_START );
        setActionDefinitionId( LdapServersPluginConstants.CMD_START );
        setToolTipText( Messages.getString( "StartAction.StartToolTip" ) ); //$NON-NLS-1$
        setImageDescriptor( LdapServersPlugin.getDefault().getImageDescriptor( LdapServersPluginConstants.IMG_START ) );
    }


    /**
     * {@inheritDoc}
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
                LdapServer server = ( LdapServer ) selection.getFirstElement();

                LdapServerAdapterExtension ldapServerAdapterExtension = server.getLdapServerAdapterExtension();
                if ( ( ldapServerAdapterExtension != null ) && ( ldapServerAdapterExtension.getInstance() != null ) )
                {
                    LdapServerAdapter ldapServerAdapter = ldapServerAdapterExtension.getInstance();

                    try
                    {

                        // Getting the ports already in use
                        String[] portsAlreadyInUse = ldapServerAdapter.checkPortsBeforeServerStart( server );
                        if ( ( portsAlreadyInUse == null ) || ( portsAlreadyInUse.length > 0 ) )
                        {
                            String title = null;
                            String message = null;

                            if ( portsAlreadyInUse.length == 1 )
                            {
                                title = Messages.getString( "StartAction.PortInUse" ); //$NON-NLS-1$
                                message = NLS
                                    .bind(
                                        Messages.getString( "StartAction.PortOfProtocolInUse" ), new String[] { portsAlreadyInUse[0] } ); //$NON-NLS-1$
                            }
                            else
                            {
                                title = Messages.getString( "StartAction.PortsInUse" ); //$NON-NLS-1$
                                message = Messages.getString( "StartAction.PortsOfProtocolsInUse" ); //$NON-NLS-1$
                                for ( String portAlreadyInUse : portsAlreadyInUse )
                                {
                                    message += "\n    - " + portAlreadyInUse; //$NON-NLS-1$
                                }
                            }

                            message += "\n\n" + Messages.getString( "StartAction.Continue" ); //$NON-NLS-1$ //$NON-NLS-2$

                            MessageDialog dialog = new MessageDialog( view.getSite().getShell(), title, null, message,
                                MessageDialog.WARNING, new String[]
                                    { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, MessageDialog.OK );
                            if ( dialog.open() == MessageDialog.CANCEL )
                            {
                                return;
                            }
                        }

                        // Creating and scheduling the job to start the server
                        StudioLdapServerJob job = new StudioLdapServerJob( new StartLdapServerRunnable( server ) );
                        job.schedule();
                    }
                    catch ( Exception e )
                    {
                        // Showing an error in case no LDAP Server Adapter can be found
                        MessageDialog
                            .openError( view.getSite().getShell(),
                                Messages.getString( "StartAction.ErrorStartingServer" ), //$NON-NLS-1$
                                NLS.bind(
                                    Messages.getString( "StartAction.ServerCanNotBeStarted" ) + "\n" + Messages.getString( "StartAction.Cause" ), server.getName(), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                    e.getMessage() ) );
                    }
                }
                else
                {
                    // Showing an error in case no LDAP Server Adapter can be found
                    MessageDialog.openError( view.getSite().getShell(),
                        Messages.getString( "StartAction.NoLdapServerAdapter" ), //$NON-NLS-1$
                        NLS.bind( Messages.getString( "StartAction.ServerCanNotBeStarted" ) + "\n" //$NON-NLS-1$ //$NON-NLS-2$
                            + Messages.getString( "StartAction.NoLdapServerAdapterCouldBeFound" ), server.getName() ) ); //$NON-NLS-1$
                }
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void run( IAction action )
    {
        run();
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        // Nothing to do
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbenchWindow window )
    {
        // Nothing to do
    }


    /**
     * {@inheritDoc}
     */
    public void selectionChanged( IAction action, ISelection selection )
    {
        // Nothing to do
    }
}
