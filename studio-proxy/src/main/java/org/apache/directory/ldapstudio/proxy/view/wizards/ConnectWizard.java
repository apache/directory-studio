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
package org.apache.directory.ldapstudio.proxy.view.wizards;


import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.internal.util.BundleUtility;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;


/**
 * This class implements the Connect Wizard.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ConnectWizard extends Wizard
{
    /** The Settings page*/
    private ConnectWizardBrowserAvailablePage browserAvailablePage;
    private ConnectWizardBrowserNotAvailablePage browserNotAvailablePage;

    /** The availability of the BrowserPlugin */
    private boolean isBrowserPluginAvailable;

    /** The proxy port */
    private int localPort = 0;

    /** The LDAP Server hostname */
    private String remoteHost = "";

    /** The LDAP Server port */
    private int remotePort = 0;


    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    public void addPages()
    {
        isBrowserPluginAvailable = isBrowserPluginAvailable();

        if ( isBrowserPluginAvailable )
        {
            browserAvailablePage = new ConnectWizardBrowserAvailablePage();
            addPage( browserAvailablePage );
        }
        else
        {
            browserNotAvailablePage = new ConnectWizardBrowserNotAvailablePage();
            addPage( browserNotAvailablePage );
        }
    }


    /**
     * Checks if the Browser Plugin is available.
     *
     * @return
     *      true if the Browser Plugin is available, false if not
     */
    private boolean isBrowserPluginAvailable()
    {
        return ( isPluginAvailable( "org.apache.directory.ldapstudio.browser.core" ) && isPluginAvailable( "org.apache.directory.ldapstudio.browser.core" ) );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    public boolean performFinish()
    {
        if ( isBrowserPluginAvailable )
        {
            browserAvailablePage.saveDialogHistory();
            localPort = browserAvailablePage.getLocalPort();
            remoteHost = browserAvailablePage.getRemoteHost();
            remotePort = browserAvailablePage.getRemotePort();
        }
        else
        {
            browserNotAvailablePage.saveDialogHistory();
            localPort = browserNotAvailablePage.getLocalPort();
            remoteHost = browserNotAvailablePage.getRemoteHost();
            remotePort = browserNotAvailablePage.getRemotePort();
        }

        return true;
    }


    /**
     * Checks if the given plugin is available (installed and active).
     * The plugin is actived if it's not already active.
     *
     * @param bundleId
     *      the id of the plugin
     * @return
     *      true if the given plugin is available, false if not.
     */
    public boolean isPluginAvailable( String bundleId )
    {
        Bundle pluginBundle = Platform.getBundle( bundleId );

        if ( pluginBundle == null )
        {
            return false;
        }

        if ( BundleUtility.isActive( pluginBundle ) )
        {
            return true;
        }
        else
        {
            try
            {
                pluginBundle.start();
            }
            catch ( BundleException e )
            {
                return false;
            }

            return BundleUtility.isActive( pluginBundle );
        }
    }


    /**
     * Gets the local port defined by the user.
     * 
     * @return
     *      the local port defined by the user
     */
    public int getLocalPort()
    {
        return localPort;
    }


    /**
     * Gets the remote host defined by the user.
     *
     * @return
     *      the remote host defined by the user
     */
    public String getRemoteHost()
    {
        return remoteHost;
    }


    /**
     * Gets the remote port defined by the user.
     *
     * @return
     *      the remote port defined by the user
     */
    public int getRemotePort()
    {
        return remotePort;
    }
}
