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

package org.apache.directory.studio.ldapbrowser.ui;


import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.PropertyResourceBundle;

import org.apache.directory.studio.entryeditors.EntryEditorExtension;
import org.apache.directory.studio.entryeditors.EntryEditorManager;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * The main plugin class to be used in the desktop.
 */
public class BrowserUIPlugin extends AbstractUIPlugin
{
    /** The shared instance */
    private static BrowserUIPlugin plugin;

    /** The entry editor manager */
    private EntryEditorManager entryEditorManager;

    /** The plugin properties */
    private PropertyResourceBundle properties;


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

        if ( entryEditorManager == null )
        {
            entryEditorManager = new EntryEditorManager();
        }

        // TODO: remove
        Collection<EntryEditorExtension> entryEditorExtensions = entryEditorManager.getEntryEditorExtensions();
        System.out.println( "Registered Entry Editors:" + entryEditorExtensions.size() );
        for ( EntryEditorExtension entryEditorExtension : entryEditorExtensions )
        {
            System.out.println( entryEditorExtension.toString() );
        }
    }


    /**
     * This method is called when the plug-in is stopped
     */
    public void stop( BundleContext context ) throws Exception
    {
        super.stop( context );

        if ( entryEditorManager != null )
        {
            entryEditorManager = null;
        }

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


    /**
     * Gets the entry editor manager
     *
     * @return
     *      the entry editor manager
     */
    public EntryEditorManager getEntryEditorManager()
    {
        return entryEditorManager;
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
                getLog().log( new Status( Status.ERROR, "org.apache.directory.studio.ldapbrowser.ui", Status.OK, //$NON-NLS-1$
                    Messages.getString( "BrowserUIPlugin.UnableGetPluginProperties" ), e ) ); //$NON-NLS-1$
            }
        }

        return properties;
    }
}
