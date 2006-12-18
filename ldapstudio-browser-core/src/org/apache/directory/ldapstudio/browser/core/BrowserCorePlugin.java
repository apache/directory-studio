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

package org.apache.directory.ldapstudio.browser.core;


import org.apache.directory.ldapstudio.browser.core.events.EventPerformanceMeter;
import org.apache.directory.ldapstudio.browser.core.model.IAuthHandler;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IReferralHandler;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;


/**
 * The main plugin class to be used in the desktop.
 */
public class BrowserCorePlugin extends Plugin
{

    // The plugin ID
    public static final String PLUGIN_ID = "org.apache.directory.ldapstudio.browser.core"; //$NON-NLS-1$

    // The shared instance.
    private static BrowserCorePlugin plugin;

    // The connection manager
    private ConnectionManager connectionManager;

    // The credential provider
    private IAuthHandler authHandler;

    // The connection provider
    private IReferralHandler referralHandler;

    // The preferences
    private BrowserCorePreferences preferences;

    private EventPerformanceMeter eventPerformanceMeter;


    public BrowserCorePlugin()
    {
        super();
        plugin = this;
        this.preferences = new BrowserCorePreferences();
        this.eventPerformanceMeter = new EventPerformanceMeter();
    }


    public void start( BundleContext context ) throws Exception
    {
        super.start( context );

        if ( this.connectionManager == null )
        {
            this.connectionManager = new ConnectionManager();
        }

        // this.eventPerformanceMeter.start();
    }


    public void stop( BundleContext context ) throws Exception
    {
        super.stop( context );

        if ( this.connectionManager != null )
        {
            IConnection[] connections = this.connectionManager.getConnections();
            for ( int i = 0; i < connections.length; i++ )
            {
                connections[i].close();
            }
            this.connectionManager = null;
        }

        // this.eventPerformanceMeter.stop();
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
     * @return The connection mamanger
     */
    public ConnectionManager getConnectionManager()
    {
        return this.connectionManager;
    }


    /**
     * 
     * @return The preferences
     */
    public BrowserCorePreferences getCorePreferences()
    {
        return this.preferences;
    }


    public IAuthHandler getAuthHandler()
    {
        return authHandler;
    }


    public void setAuthHandler( IAuthHandler authHandler )
    {
        this.authHandler = authHandler;
    }


    public IReferralHandler getReferralHandler()
    {
        return referralHandler;
    }


    public void setReferralHandler( IReferralHandler referralHandler )
    {
        this.referralHandler = referralHandler;
    }

}
