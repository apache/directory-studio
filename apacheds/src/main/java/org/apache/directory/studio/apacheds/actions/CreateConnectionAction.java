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
import java.util.PropertyResourceBundle;

import org.apache.directory.studio.apacheds.ApacheDsPlugin;
import org.apache.directory.studio.apacheds.ApacheDsPluginConstants;
import org.apache.directory.studio.apacheds.ApacheDsPluginUtils;
import org.apache.directory.studio.apacheds.configuration.model.ServerConfiguration;
import org.apache.directory.studio.apacheds.configuration.model.ServerXmlIOException;
import org.apache.directory.studio.apacheds.configuration.model.v153.ServerConfigurationV153;
import org.apache.directory.studio.apacheds.configuration.model.v154.ServerConfigurationV154;
import org.apache.directory.studio.apacheds.configuration.model.v155.ServerConfigurationV155;
import org.apache.directory.studio.apacheds.configuration.model.v156.ServerConfigurationV156;
import org.apache.directory.studio.apacheds.model.Server;
import org.apache.directory.studio.apacheds.views.ServersView;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.osgi.framework.Bundle;


/**
 * This class implements the create connection action for a server.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CreateConnectionAction extends Action implements IWorkbenchWindowActionDelegate
{
    /** The associated view */
    private ServersView view;


    /**
     * Creates a new instance of CreateConnectionAction.
     */
    public CreateConnectionAction()
    {
        super( Messages.getString( "CreateConnectionAction.CreateAConnection" ) ); //$NON-NLS-1$
        init();
    }


    /**
     * Creates a new instance of CreateConnectionAction.
     * 
     * @param view
     *      the associated view
     */
    public CreateConnectionAction( ServersView view )
    {
        super( Messages.getString( "CreateConnectionAction.CreateAConnection" ) ); //$NON-NLS-1$
        this.view = view;
        init();
    }


    /**
     * Initializes the action.
     */
    private void init()
    {
        setId( ApacheDsPluginConstants.CMD_CREATE_CONNECTION );
        setActionDefinitionId( ApacheDsPluginConstants.CMD_CREATE_CONNECTION );
        setToolTipText( Messages.getString( "CreateConnectionAction.StopToolTip" ) ); //$NON-NLS-1$
        setImageDescriptor( ApacheDsPlugin.getDefault().getImageDescriptor(
            ApacheDsPluginConstants.IMG_CREATE_CONNECTION ) );
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
                Server server = ( Server ) selection.getFirstElement();

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

                if ( isEnableLdapOrLdaps( serverConfiguration ) )
                {
                    // Creating the connection using the helper class
                    CreateConnectionActionHelper.createLdapBrowserConnection( server.getName(), serverConfiguration );
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
     * @param errorMessage
     *      an error message which can be <code>null</code>
     */
    private void reportErrorReadingServerConfiguration( String errorMessage )
    {
        String message = null;

        if ( errorMessage == null )
        {
            message = Messages.getString( "CreateConnectionAction.UnableReadServerConfiguration" ); //$NON-NLS-1$
        }
        else
        {
            message = Messages.getString( "CreateConnectionAction.UnableReadServerConfiguration" ) + ApacheDsPluginUtils.LINE_SEPARATOR //$NON-NLS-1$
                + ApacheDsPluginUtils.LINE_SEPARATOR
                + Messages.getString( "CreateConnectionAction.FollowingErrorOccurred" ) + errorMessage; //$NON-NLS-1$
        }

        MessageDialog dialog = new MessageDialog( view.getSite().getShell(),
            Messages.getString( "CreateConnectionAction.UnableReadServerConfiguration" ), //$NON-NLS-1$
            null, message, MessageDialog.ERROR, new String[]
                { IDialogConstants.OK_LABEL }, MessageDialog.OK );
        dialog.open();
    }


    /**
     * Indicates if LDAP or LDAPS is enabled.
     *
     * @param serverConfiguration
     *      the server configuration
     * @return
     *      <code>true</code> if LDAP or LDAPS is enabled, <code>false</code> if not
     */
    private boolean isEnableLdapOrLdaps( ServerConfiguration serverConfiguration )
    {
        if ( serverConfiguration instanceof ServerConfigurationV156 )
        {
            ServerConfigurationV156 serverConfiguration156 = ( ServerConfigurationV156 ) serverConfiguration;
            return ( serverConfiguration156.isEnableLdap() ) || ( serverConfiguration156.isEnableLdaps() );
        }
        else if ( serverConfiguration instanceof ServerConfigurationV155 )
        {
            ServerConfigurationV155 serverConfiguration155 = ( ServerConfigurationV155 ) serverConfiguration;
            return ( serverConfiguration155.isEnableLdap() ) || ( serverConfiguration155.isEnableLdaps() );
        }
        else if ( serverConfiguration instanceof ServerConfigurationV154 )
        {
            ServerConfigurationV154 serverConfiguration154 = ( ServerConfigurationV154 ) serverConfiguration;
            return ( serverConfiguration154.isEnableLdap() ) || ( serverConfiguration154.isEnableLdaps() );
        }
        else if ( serverConfiguration instanceof ServerConfigurationV153 )
        {
            ServerConfigurationV153 serverConfiguration153 = ( ServerConfigurationV153 ) serverConfiguration;
            return ( serverConfiguration153.isEnableLdap() ) || ( serverConfiguration153.isEnableLdaps() );
        }

        return false;
    }


    /**
     * Sets the enabled state of this action.
     * <p>
     * When an action is in the enabled state, the control associated with 
     * it is active; triggering it will end up inkoking this action's 
     * <code>run</code> method.
     * </p>
     * <p>
     * Fires a property change event for the <code>ENABLED</code> property
     * if the enabled state actually changes as a consequence.
     * </p>
     * <p>
     * In the particular case of this action, when the enabled value equals
     * <code>true</code>, a check on the presence of the necessary LDAP
     * Browser plugins is executed. The action is enabled only if all the 
     * required plugins are available.
     * </p>
     *
     * @param enabled <code>true</code> to enable, and
     *   <code>false</code> to disable
     * @see #ENABLED
     */
    public void setEnabled( boolean enabled )
    {
        if ( enabled )
        {
            super.setEnabled( isLdapBrowserPluginsAvailable() );
        }
        else
        {
            super.setEnabled( enabled );
        }
    }


    /**
     * Indicates if the LDAP Browser plugins are available or not.
     *
     * @return
     *  <code>true</code> if the LDAP Browser plugins are available, 
     *  <code>false</code> if not.
     */
    private boolean isLdapBrowserPluginsAvailable()
    {
        PropertyResourceBundle properties = ApacheDsPlugin.getDefault().getPluginProperties();

        // Connection Core Plugin
        Bundle connectionCoreBundle = Platform.getBundle( properties.getString( "Plugin_ConnectionCore_id" ) ); //$NON-NLS-1$
        if ( connectionCoreBundle != null )
        {
            // Checking the state of the plugin
            if ( connectionCoreBundle.getState() == Bundle.UNINSTALLED )
            {
                return false;
            }

            // Connection UI Plugin
            Bundle connectionUiBundle = Platform.getBundle( properties.getString( "Plugin_ConnectionUi_id" ) ); //$NON-NLS-1$
            if ( connectionUiBundle != null )
            {
                // Checking the state of the plugin
                if ( connectionUiBundle.getState() == Bundle.UNINSTALLED )
                {
                    return false;
                }

                // LDAP Browser Common Plugin
                Bundle ldapBrowserCommonBundle = Platform.getBundle( properties
                    .getString( "Plugin_LdapBrowserCommon_id" ) ); //$NON-NLS-1$
                if ( ldapBrowserCommonBundle != null )
                {
                    // Checking the state of the plugin
                    if ( ldapBrowserCommonBundle.getState() == Bundle.UNINSTALLED )
                    {
                        return false;
                    }

                    // LDAP Browser Core Plugin
                    Bundle ldapBrowserCoreBundle = Platform.getBundle( properties
                        .getString( "Plugin_LdapBrowserCore_id" ) ); //$NON-NLS-1$
                    if ( ldapBrowserCoreBundle != null )
                    {
                        // Checking the state of the plugin
                        if ( ldapBrowserCoreBundle.getState() == Bundle.UNINSTALLED )
                        {
                            return false;
                        }

                        // LDAP Browser UI Plugin
                        Bundle ldapBrowserUiBundle = Platform.getBundle( properties
                            .getString( "Plugin_LdapBrowserUi_id" ) ); //$NON-NLS-1$
                        if ( ldapBrowserUiBundle != null )
                        {
                            // Checking the state of the plugin
                            if ( ldapBrowserUiBundle.getState() == Bundle.UNINSTALLED )
                            {
                                return false;
                            }

                            // LDIF Editor Plugin
                            Bundle ldifEditorBundle = Platform
                                .getBundle( properties.getString( "Plugin_LdifEditor_id" ) ); //$NON-NLS-1$
                            if ( ldifEditorBundle != null )
                            {
                                // Checking the state of the plugin
                                if ( ldifEditorBundle.getState() == Bundle.UNINSTALLED )
                                {
                                    return false;
                                }

                                // LDIF Parser Plugin
                                Bundle ldifParserBundle = Platform.getBundle( properties
                                    .getString( "Plugin_LdifParser_id" ) ); //$NON-NLS-1$
                                if ( ldifParserBundle != null )
                                {
                                    // Checking the state of the plugin
                                    if ( ldifParserBundle.getState() == Bundle.UNINSTALLED )
                                    {
                                        return false;
                                    }

                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
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
