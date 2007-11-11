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
package org.apache.directory.studio.connection.core;


import org.apache.directory.studio.connection.core.event.CoreEventRunner;
import org.apache.directory.studio.connection.core.event.EventRunner;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;


/**
 * The activator class controls the plug-in life cycle
 */
public class ConnectionCorePlugin extends Plugin
{

    /** The plug-in ID */
    public static final String PLUGIN_ID = "org.apache.directory.studio.connection.core";

    /** The shared instance */
    private static ConnectionCorePlugin plugin;

    /** The connection manager */
    private ConnectionManager connectionManager;

    /** The connection folder manager */
    private ConnectionFolderManager connectionFolderManager;

    /** The event runner. */
    private EventRunner eventRunner;

    /** The authentication handler */
    private IAuthHandler authHandler;

    /** The referral handler */
    private IReferralHandler referralHandler;

    /**
     * The constructor
     */
    public ConnectionCorePlugin()
    {
        plugin = this;
    }


    /**
     * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
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

        if ( connectionFolderManager == null )
        {
            connectionFolderManager = new ConnectionFolderManager();
        }
    }


    /**
     * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop( BundleContext context ) throws Exception
    {
        plugin = null;
        super.stop( context );

        if ( eventRunner != null )
        {
            eventRunner = null;
        }

        if ( connectionManager != null )
        {
            Connection[] connections = connectionManager.getConnections();
            for ( int i = 0; i < connections.length; i++ )
            {
                connections[i].getJNDIConnectionWrapper().disconnect();
            }
            connectionManager = null;
        }

        if ( connectionFolderManager != null )
        {
            connectionFolderManager = null;
        }
    }


    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static ConnectionCorePlugin getDefault()
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
     * Gets the connection folder manager.
     *
     * @return the connection folder manager
     */
    public ConnectionFolderManager getConnectionFolderManager()
    {
        return connectionFolderManager;
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


    /**
     * Gets the authentication handler
     *
     * @return
     *      the authentication handler
     */
    public IAuthHandler getAuthHandler()
    {
        return authHandler;
    }


    /**
     * Sets the authentication handler
     *
     * @param authHandler
     *      the authentication handler to set
     */
    public void setAuthHandler( IAuthHandler authHandler )
    {
        this.authHandler = authHandler;
    }


    /**
     * Gets the referral handler
     *
     * @return
     *      the referral handler
     */
    public IReferralHandler getReferralHandler()
    {
        return referralHandler;
    }


    /**
     * Sets the referral handler
     *
     * @param referralHandler
     *      the referral handler to set
     */
    public void setReferralHandler( IReferralHandler referralHandler )
    {
        this.referralHandler = referralHandler;
    }

}
