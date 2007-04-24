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

package org.apache.directory.ldapstudio.browser.ui;


import java.net.URL;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * The main plugin class to be used in the desktop.
 */
public class BrowserUIPlugin extends AbstractUIPlugin
{

    /** The plugin ID */
    public static final String PLUGIN_ID = "org.apache.directory.ldapstudio.browser.ui";

    /** The shared instance */
    private static BrowserUIPlugin plugin;

    
    /**
     * The constructor.
     */
    public BrowserUIPlugin()
    {
        plugin = this;
    }


    /**
     * This method is called upon plug-in activation
     */
    public void start( BundleContext context ) throws Exception
    {
        super.start( context );
    }


    /**
     * This method is called when the plug-in is stopped
     */
    public void stop( BundleContext context ) throws Exception
    {
        super.stop( context );
        plugin = null;
    }


    /**
     * Returns the shared instance.
     */
    public static BrowserUIPlugin getDefault()
    {
        return plugin;
    }


//    public static String getResourceString( String key )
//    {
//        ResourceBundle bundle = getDefault().getResourceBundle();
//        try
//        {
//            return ( bundle != null ) ? bundle.getString( key ) : key;
//        }
//        catch ( MissingResourceException e )
//        {
//            return key;
//        }
//    }


    /**
     * Use this method to get SWT images. Use the IMG_ constants from
     * BrowserUIConstants for the key.
     * 
     * @param key
     *                The key (relative path to the image im filesystem)
     * @return The image discriptor or null
     */
    public ImageDescriptor getImageDescriptor( String key )
    {
        if ( key != null )
        {
            URL url = this.find( new Path( key ) );
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
     * BrowserUIConstants for the key. A ImageRegistry is used to manage the
     * the key->Image mapping.
     * <p>
     * Note: Don't dispose the returned SWT Image. It is disposed
     * automatically when the plugin is stopped.
     * 
     * @param key
     *                The key (relative path to the image im filesystem)
     * @return The SWT Image or null
     * @see BrowserUIConstants
     */
    public Image getImage( String key )
    {
        Image image = getImageRegistry().get( key );
        if ( image == null )
        {
            ImageDescriptor id = this.getImageDescriptor( key );
            if ( id != null )
            {
                image = id.createImage();
                getImageRegistry().put( key, image );
            }
        }
        return image;
    }

}
