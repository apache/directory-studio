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

package org.apache.directory.studio.common.ui;


import java.io.IOException;
import java.net.URL;
import java.util.PropertyResourceBundle;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * The activator class controls the plug-in life cycle
 */
public class CommonUIPlugin extends AbstractUIPlugin
{
    /** The shared plugin instance. */
    private static CommonUIPlugin plugin;

    /** The plugin properties */
    private PropertyResourceBundle properties;


    /**
     * The constructor
     */
    public CommonUIPlugin()
    {
        plugin = this;
    }


    /**
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start( BundleContext context ) throws Exception
    {
        super.start( context );
        
        // Create the colors we use
        CommonUIConstants.BLACK_COLOR = new Color( null, CommonUIConstants.BLACK );
        CommonUIConstants.WHITE_COLOR = new Color( null, CommonUIConstants.WHITE );
        
        // Greys
        CommonUIConstants.BD_GREY_COLOR = new Color( null, CommonUIConstants.M_GREY );
        CommonUIConstants.D_GREY_COLOR = new Color( null, CommonUIConstants.M_GREY );
        CommonUIConstants.MD_GREY_COLOR = new Color( null, CommonUIConstants.M_GREY );
        CommonUIConstants.M_GREY_COLOR = new Color( null, CommonUIConstants.M_GREY );
        CommonUIConstants.ML_GREY_COLOR = new Color( null, CommonUIConstants.M_GREY );
        CommonUIConstants.L_GREY_COLOR = new Color( null, CommonUIConstants.M_GREY );
        CommonUIConstants.WL_GREY_COLOR = new Color( null, CommonUIConstants.M_GREY );
        
        // Reds
        CommonUIConstants.M_RED_COLOR = new Color( null, CommonUIConstants.M_RED );
        CommonUIConstants.ML_RED_COLOR = new Color( null, CommonUIConstants.ML_RED );
        CommonUIConstants.RED_COLOR = new Color( null, CommonUIConstants.RED );
        
        // Greens
        CommonUIConstants.M_GREEN_COLOR = new Color( null, CommonUIConstants.M_GREEN );
        CommonUIConstants.ML_GREEN_COLOR = new Color( null, CommonUIConstants.ML_GREEN );
        CommonUIConstants.GREEN_COLOR = new Color( null, CommonUIConstants.GREEN );
        
        // Blues
        CommonUIConstants.M_BLUE_COLOR = new Color( null, CommonUIConstants.M_BLUE );
        CommonUIConstants.L_BLUE_COLOR = new Color( null, CommonUIConstants.L_BLUE );
        CommonUIConstants.BLUE_COLOR = new Color( null, CommonUIConstants.BLUE );
        
        // Purples
        CommonUIConstants.M_PURPLE_COLOR = new Color( null, CommonUIConstants.M_PURPLE );
        CommonUIConstants.PURPLE_COLOR = new Color( null, CommonUIConstants.PURPLE );
        
        // Other colors
        CommonUIConstants.R0_G127_B255_COLOR = new Color( null, CommonUIConstants.R0_G127_B255 );
        CommonUIConstants.R95_G63_B159_COLOR = new Color( null, CommonUIConstants.R95_G63_B159 );
        CommonUIConstants.R63_G127_B63_COLOR = new Color( null, CommonUIConstants.R63_G127_B63 );
    }


    /**
     * @see AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop( BundleContext context ) throws Exception
    {
        plugin = null;
        
        // Dispose the colors
        CommonUIConstants.BLACK_COLOR.dispose();
        CommonUIConstants.WHITE_COLOR.dispose();
        
        // greys
        CommonUIConstants.M_GREY_COLOR.dispose();
        CommonUIConstants.BD_GREY_COLOR.dispose();
        CommonUIConstants.D_GREY_COLOR.dispose();
        CommonUIConstants.MD_GREY_COLOR.dispose();
        CommonUIConstants.M_GREY_COLOR.dispose();
        CommonUIConstants.ML_GREY_COLOR.dispose();
        CommonUIConstants.L_GREY_COLOR.dispose();
        CommonUIConstants.WL_GREY_COLOR.dispose();

        //Reds
        CommonUIConstants.M_RED_COLOR.dispose();
        CommonUIConstants.ML_RED_COLOR.dispose();
        CommonUIConstants.RED_COLOR.dispose();
        
        // Greens
        CommonUIConstants.M_GREEN_COLOR.dispose();
        CommonUIConstants.ML_GREEN_COLOR.dispose();
        CommonUIConstants.GREEN_COLOR.dispose();

        // Blues
        CommonUIConstants.M_BLUE_COLOR.dispose();
        CommonUIConstants.L_BLUE_COLOR.dispose();
        CommonUIConstants.BLUE_COLOR.dispose();
        
        // Purple
        CommonUIConstants.M_PURPLE_COLOR.dispose();
        CommonUIConstants.PURPLE_COLOR.dispose();
        
        // Other colors
        CommonUIConstants.R0_G127_B255_COLOR.dispose();

        super.stop( context );
    }


    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static CommonUIPlugin getDefault()
    {
        return plugin;
    }


    /**
     * Use this method to get SWT images. Use the IMG_ constants from
     * BrowserWidgetsConstants for the key.
     *
     * @param key
     *                The key (relative path to the image in filesystem)
     * @return The image descriptor or null
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
     * the key-&gt;Image mapping.
     * <p>
     * Note: Don't dispose the returned SWT Image. It is disposed
     * automatically when the plugin is stopped.
     *
     * @param key
     *                The key (relative path to the image in filesystem)
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
                    "plugin.properties" ), false ) ); //$NON-NLS-1$
            }
            catch ( IOException e )
            {
                // We can't use the PLUGIN_ID constant since loading the plugin.properties file has failed,
                // So we're using a default plugin id.
                getLog().log( new Status( Status.ERROR, "org.apache.directory.studio.common.ui", Status.OK, //$NON-NLS-1$
                    Messages.getString( "CommonUIPlugin.UnableGetPluginProperties" ), e ) ); //$NON-NLS-1$
            }
        }

        return properties;
    }
}
