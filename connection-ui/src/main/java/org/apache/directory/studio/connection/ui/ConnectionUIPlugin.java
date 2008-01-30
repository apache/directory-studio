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

package org.apache.directory.studio.connection.ui;


import java.net.URL;

import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.event.EventRunner;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * The activator class controls the plug-in life cycle
 */
public class ConnectionUIPlugin extends AbstractUIPlugin
{

    /** The Constant PLUGIN_ID. */
    public static final String PLUGIN_ID = "org.apache.directory.studio.connection.ui";

    /** The shared plugin instance. */
    private static ConnectionUIPlugin plugin;

    /** The event dispatcher */
    private ExceptionHandler exceptionHandler;

    /** The event runner. */
    private EventRunner eventRunner;


    /**
     * The constructor
     */
    public ConnectionUIPlugin()
    {
        plugin = this;
    }


    /**
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start( BundleContext context ) throws Exception
    {
        super.start( context );

        if ( exceptionHandler == null )
        {
            exceptionHandler = new ExceptionHandler();
        }

        if ( eventRunner == null )
        {
            eventRunner = new UiThreadEventRunner();
        }

        ConnectionCorePlugin.getDefault().setAuthHandler( new UIAuthHandler() );
        ConnectionCorePlugin.getDefault().setReferralHandler( new ConnectionUIReferralHandler() );
    }


    /**
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop( BundleContext context ) throws Exception
    {
        plugin = null;
        super.stop( context );

        if ( exceptionHandler != null )
        {
            exceptionHandler = null;
        }

        if ( eventRunner != null )
        {
            eventRunner = null;
        }
    }


    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static ConnectionUIPlugin getDefault()
    {
        return plugin;
    }


    /**
     * Gets the exception handler.
     * 
     * @return the exception handler
     */
    public ExceptionHandler getExceptionHandler()
    {
        return exceptionHandler;
    }


    /**
     * Use this method to get SWT images. Use the IMG_ constants from
     * BrowserWidgetsConstants for the key.
     *
     * @param key
     *                The key (relative path to the image im filesystem)
     * @return The image discriptor or null
     */
    public ImageDescriptor getImageDescriptor( String key )
    {
        if ( key != null )
        {
            URL url = FileLocator.find( getBundle(), new Path( key ), null );
            if ( url != null )
                return ImageDescriptor.createFromURL( url );
            else
                return null;
        }
        else
        {
            return null;
        }
    }


    /**
     * Use this method to get SWT images. Use the IMG_ constants from
     * BrowserWidgetsConstants for the key. A ImageRegistry is used to manage the
     * the key->Image mapping.
     * <p>
     * Note: Don't dispose the returned SWT Image. It is disposed
     * automatically when the plugin is stopped.
     *
     * @param key
     *                The key (relative path to the image im filesystem)
     * @return The SWT Image or null
     */
    public Image getImage( String key )
    {
        Image image = getImageRegistry().get( key );
        if ( image == null )
        {
            ImageDescriptor id = getImageDescriptor( key );
            if ( id != null )
            {
                image = id.createImage();
                getImageRegistry().put( key, image );
            }
        }
        return image;
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
