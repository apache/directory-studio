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


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;

import org.apache.directory.studio.connection.core.event.CoreEventRunner;
import org.apache.directory.studio.connection.core.event.EventRunner;
import org.apache.directory.studio.connection.core.io.jndi.LdifModificationLogger;
import org.apache.directory.studio.connection.core.io.jndi.LdifSearchLogger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;


/**
 * The activator class controls the plug-in life cycle
 */
public class ConnectionCorePlugin extends Plugin
{
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

    /** The JNDI loggers. */
    private List<IJndiLogger> jndiLoggers;

    /** The connection listeners. */
    private List<IConnectionListener> connectionListeners;

    /** The plugin properties */
    private PropertyResourceBundle properties;


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


    /**
     * Gets the LDIF modification logger.
     * 
     * @return the LDIF modification logger, null if none found.
     */
    public LdifModificationLogger getLdifModificationLogger()
    {
        List<IJndiLogger> jndiLoggers = getJndiLoggers();
        for ( IJndiLogger jndiLogger : jndiLoggers )
        {
            if ( jndiLogger instanceof LdifModificationLogger )
            {
                return ( LdifModificationLogger ) jndiLogger;
            }
        }
        return null;
    }


    /**
     * Gets the LDIF search logger.
     * 
     * @return the LDIF search logger, null if none found.
     */
    public LdifSearchLogger getLdifSearchLogger()
    {
        List<IJndiLogger> jndiLoggers = getJndiLoggers();
        for ( IJndiLogger jndiLogger : jndiLoggers )
        {
            if ( jndiLogger instanceof LdifSearchLogger )
            {
                return ( LdifSearchLogger ) jndiLogger;
            }
        }
        return null;
    }


    /**
     * Gets the jndi loggers.
     * 
     * @return the JNDI loggers
     */
    public List<IJndiLogger> getJndiLoggers()
    {
        if ( jndiLoggers == null )
        {
            jndiLoggers = new ArrayList<IJndiLogger>();

            IExtensionRegistry registry = Platform.getExtensionRegistry();
            IExtensionPoint extensionPoint = registry.getExtensionPoint( getPluginProperties().getString(
                "ExtensionPoint_JndiLogger_id" ) );
            IConfigurationElement[] members = extensionPoint.getConfigurationElements();
            for ( IConfigurationElement member : members )
            {
                try
                {
                    IJndiLogger logger = ( IJndiLogger ) member.createExecutableExtension( "class" );
                    logger.setId( member.getAttribute( "id" ) );
                    logger.setName( member.getAttribute( "name" ) );
                    logger.setDescription( member.getAttribute( "description" ) );
                    jndiLoggers.add( logger );
                }
                catch ( Exception e )
                {
                    getLog().log(
                        new Status( IStatus.ERROR, ConnectionCoreConstants.PLUGIN_ID, 1, "Unable to create JNDI logger "
                            + member.getAttribute( "class" ), e ) );
                }
            }
        }

        return jndiLoggers;
    }


    /**
     * Gets the connection listeners.
     * 
     * @return the connection listeners
     */
    public List<IConnectionListener> getConnectionListeners()
    {
        if ( connectionListeners == null )
        {
            connectionListeners = new ArrayList<IConnectionListener>();

            IExtensionRegistry registry = Platform.getExtensionRegistry();
            IExtensionPoint extensionPoint = registry.getExtensionPoint( getPluginProperties().getString(
                "ExtensionPoint_ConnectionListener_id" ) );
            IConfigurationElement[] members = extensionPoint.getConfigurationElements();
            for ( IConfigurationElement member : members )
            {
                try
                {
                    IConnectionListener listener = ( IConnectionListener ) member.createExecutableExtension( "class" );
                    //                    listener.setId( member.getAttribute( "id" ) );
                    //                    listener.setName( member.getAttribute( "name" ) );
                    //                    listener.setDescription( member.getAttribute( "description" ) );
                    connectionListeners.add( listener );
                }
                catch ( Exception e )
                {
                    getLog().log(
                        new Status( IStatus.ERROR, ConnectionCoreConstants.PLUGIN_ID, 1,
                            "Unable to create connection listener " + member.getAttribute( "class" ), e ) );
                }
            }
        }

        return connectionListeners;
    }


    /**
     * Gets the plugin properties.
     *
     * @return
     *      the plugin properties
     */
    public PropertyResourceBundle getPluginProperties()
    {
        if ( properties == null )
        {
            try
            {
                properties = new PropertyResourceBundle( FileLocator.openStream( this.getBundle(), new Path(
                    "plugin.properties" ), false ) );
            }
            catch ( IOException e )
            {
                // We can't use the PLUGIN_ID constant since loading the plugin.properties file has failed,
                // So we're using a default plugin id.
                getLog().log(
                    new Status( Status.ERROR, "org.apache.directory.studio.connection.core", Status.OK,
                        "Unable to get the plugin properties.", e ) );
            }
        }

        return properties;
    }
}
