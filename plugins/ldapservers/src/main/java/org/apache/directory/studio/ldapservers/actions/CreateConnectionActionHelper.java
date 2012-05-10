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


import java.util.PropertyResourceBundle;

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.ldapservers.LdapServersPlugin;
import org.apache.directory.studio.ldapservers.model.LdapServer;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;


/**
 * This class implements a helper class of the create connection action for a server.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CreateConnectionActionHelper
{
    public static void createLdapBrowserConnection( LdapServer server, Connection connection )
    {
        // Adding the connection to the connection manager
        ConnectionCorePlugin.getDefault().getConnectionManager().addConnection( connection );

        // Adding the connection to the root connection folder
        ConnectionCorePlugin.getDefault().getConnectionFolderManager().getRootConnectionFolder()
            .addConnectionId( connection.getId() );

        // Getting the window, LDAP perspective and current perspective
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        IPerspectiveDescriptor ldapPerspective = getLdapPerspective();
        IPerspectiveDescriptor currentPerspective = window.getActivePage().getPerspective();

        // Checking if we are already in the LDAP perspective
        if ( ( ldapPerspective != null ) && ( ldapPerspective.equals( currentPerspective ) ) )
        {
            // As we're already in the LDAP perspective, we only indicate to the user 
            // the name of the connection that has been created
            MessageDialog dialog = new MessageDialog(
                window.getShell(),
                Messages.getString( "CreateConnectionActionHelper.ConnectionCreated" ), null, //$NON-NLS-1$
                NLS.bind(
                    Messages.getString( "CreateConnectionActionHelper.ConnectionCalledCreated" ), new String[] { connection.getName() } ), MessageDialog.INFORMATION, //$NON-NLS-1$
                new String[]
                    { IDialogConstants.OK_LABEL }, MessageDialog.OK );
            dialog.open();
        }
        else
        {
            // We're not already in the LDAP perspective, we indicate to the user
            // the name of the connection that has been created and we ask him
            // if we wants to switch to the LDAP perspective
            MessageDialog dialog = new MessageDialog(
                window.getShell(),
                Messages.getString( "CreateConnectionActionHelper.ConnectionCreated" ), null, //$NON-NLS-1$
                NLS.bind(
                    Messages.getString( "CreateConnectionActionHelper.ConnectionCalledCreatedSwitch" ), new String[] { connection.getName() } ), //$NON-NLS-1$
                MessageDialog.INFORMATION, new String[]
                    { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL }, MessageDialog.OK );
            if ( dialog.open() == MessageDialog.OK )
            {
                // Switching to the LDAP perspective
                window.getActivePage().setPerspective( ldapPerspective );
            }
        }
    }


    /**
     * Get the LDAP perspective.
     *
     * @return
     *      the LDAP perspective
     */
    private static IPerspectiveDescriptor getLdapPerspective()
    {
        for ( IPerspectiveDescriptor perspective : PlatformUI.getWorkbench().getPerspectiveRegistry().getPerspectives() )
        {
            if ( LdapServersPlugin.getDefault().getPluginProperties()
                .getString( "Perspective_LdapBrowserPerspective_id" ) //$NON-NLS-1$
                .equalsIgnoreCase( perspective.getId() ) )
            {
                return perspective;
            }
        }

        return null;
    }
    

    /**
     * Indicates if the LDAP Browser plugins are available or not.
     *
     * @return
     *  <code>true</code> if the LDAP Browser plugins are available, 
     *  <code>false</code> if not.
     */
    public static boolean isLdapBrowserPluginsAvailable()
    {
        PropertyResourceBundle properties = LdapServersPlugin.getDefault().getPluginProperties();

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
}
