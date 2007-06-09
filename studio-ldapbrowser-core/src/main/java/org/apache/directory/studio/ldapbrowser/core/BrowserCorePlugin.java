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

package org.apache.directory.studio.ldapbrowser.core;


import org.apache.directory.studio.ldapbrowser.core.events.CoreEventRunner;
import org.apache.directory.studio.ldapbrowser.core.events.EventRunner;
import org.apache.directory.studio.ldapbrowser.core.model.IAuthHandler;
import org.apache.directory.studio.ldapbrowser.core.model.IConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IReferralHandler;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;


/**
 * The main plugin class to be used in the desktop.
 */
public class BrowserCorePlugin extends Plugin
{
    /** The plugin ID */
    public static final String PLUGIN_ID = "org.apache.directory.studio.ldapbrowser.core"; //$NON-NLS-1$

    /** The shared instance. */
    private static BrowserCorePlugin plugin;

    /** The connection manager */
    private ConnectionManager connectionManager;

    /** The credential provider */
    private IAuthHandler authHandler;

    /** The connection provider */
    private IReferralHandler referralHandler;

    /** The preferences */
    private BrowserCorePreferences preferences;

    /** The event runner. */
    private EventRunner eventRunner;


    /**
     * Creates a new instance of BrowserCorePlugin.
     */
    public BrowserCorePlugin()
    {
        super();
        plugin = this;
        this.preferences = new BrowserCorePreferences();
    }


    /**
     * {@inheritDoc}
     */
    public void start( BundleContext context ) throws Exception
    {
        super.start( context );

        if ( eventRunner == null )
        {
            eventRunner = new CoreEventRunner();
        }

        if ( connectionManager == null )
        {
            connectionManager = new ConnectionManager();
        }
    }


    /**
     * {@inheritDoc}
     */
    public void stop( BundleContext context ) throws Exception
    {
        super.stop( context );

        if ( eventRunner != null )
        {
            eventRunner = null;
        }

        if ( connectionManager != null )
        {
            IConnection[] connections = connectionManager.getConnections();
            for ( int i = 0; i < connections.length; i++ )
            {
                connections[i].close();
            }
            connectionManager = null;
        }
    }


    /**
     * Returns the BrowserPlugin instance.
     *
     * @return The BrowserPlugin instance
     */
    public static BrowserCorePlugin getDefault()
    {
        return plugin;
    }


    /**
     * Gets the Connection Manager
     *
     * @return
     *      the connection manager
     */
    public ConnectionManager getConnectionManager()
    {
        return connectionManager;
    }


    /**
     *
     * @return The preferences
     */
    public BrowserCorePreferences getCorePreferences()
    {
        return preferences;
    }


    /**
     * Gets the AuthHandler
     *
     * @return
     *      the AuthHandler
     */
    public IAuthHandler getAuthHandler()
    {
        return authHandler;
    }


    /**
     * Sets the AuthHandler
     *
     * @param authHandler
     *      the authHandler to set
     */
    public void setAuthHandler( IAuthHandler authHandler )
    {
        this.authHandler = authHandler;
    }


    /**
     * Gets the ReferralHanlder
     *
     * @return
     *      the ReferralHandler
     */
    public IReferralHandler getReferralHandler()
    {
        return referralHandler;
    }


    /**
     * Sets the ReferralHandler
     *
     * @param referralHandler
     *      the ReferralHandler to set
     */
    public void setReferralHandler( IReferralHandler referralHandler )
    {
        this.referralHandler = referralHandler;
    }


    /**
     * Gets the event runner.
     *
     * @return the event runner
     */
    public EventRunner getEventRunner()
    {
        return eventRunner;
    }
}
