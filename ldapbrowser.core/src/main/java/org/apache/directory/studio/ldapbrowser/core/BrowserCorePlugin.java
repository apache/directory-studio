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


import java.io.IOException;
import java.util.PropertyResourceBundle;

import org.apache.directory.studio.connection.core.event.CoreEventRunner;
import org.apache.directory.studio.connection.core.event.EventRunner;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;


/**
 * The main plugin class to be used in the desktop.
 */
public class BrowserCorePlugin extends Plugin
{
    /** The shared instance. */
    private static BrowserCorePlugin plugin;

    /** The connection manager */
    private BrowserConnectionManager connectionManager;

    /** The preferences */
    private BrowserCorePreferences preferences;

    /** The event runner. */
    private EventRunner eventRunner;

    /** The plugin properties */
    private PropertyResourceBundle properties;


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
            connectionManager = new BrowserConnectionManager();
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
            //            IConnection[] connections = connectionManager.getConnections();
            //            for ( int i = 0; i < connections.length; i++ )
            //            {
            //                connections[i].close();
            //            }
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
    public BrowserConnectionManager getConnectionManager()
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
     * Gets the event runner.
     *
     * @return the event runner
     */
    public EventRunner getEventRunner()
    {
        return eventRunner;
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
                    new Status( Status.ERROR, "org.apache.directory.studio.ldapbrowser.core", Status.OK,
                        "Unable to get the plugin properties.", e ) );
            }
        }

        return properties;
    }
}
