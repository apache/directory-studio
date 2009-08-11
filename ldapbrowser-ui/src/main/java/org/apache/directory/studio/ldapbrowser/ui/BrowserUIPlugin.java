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
import java.util.ArrayList;
import java.util.Collection;
import java.util.PropertyResourceBundle;

import org.apache.directory.studio.entryeditors.EntryEditorExtension;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
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

        // TODO: remove
        Collection<EntryEditorExtension> entryEditorExtensions = getEntryEditorExtensions();
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


    /**
     * Gets the entry editor extensions.
     * 
     * @return the entry editor extensions
     */
    public Collection<EntryEditorExtension> getEntryEditorExtensions()
    {
        Collection<EntryEditorExtension> entryEditorExtensions = new ArrayList<EntryEditorExtension>();

        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint extensionPoint = registry.getExtensionPoint( BrowserUIConstants.ENTRY_EDITOR_EXTENSION_POINT );
        IConfigurationElement[] members = extensionPoint.getConfigurationElements();

        // For each extension:
        for ( int m = 0; m < members.length; m++ )
        {
            EntryEditorExtension bean = new EntryEditorExtension();
            entryEditorExtensions.add( bean );

            IConfigurationElement member = members[m];
            IExtension extension = member.getDeclaringExtension();
            String extendingPluginId = extension.getNamespaceIdentifier();

            //proxy.member = member;
            bean.setId( member.getAttribute( "id" ) );
            bean.setName( member.getAttribute( "name" ) );
            bean.setDescription( member.getAttribute( "description" ) );
            String iconPath = member.getAttribute( "icon" );
            ImageDescriptor icon = AbstractUIPlugin.imageDescriptorFromPlugin( extendingPluginId, iconPath );
            if ( icon == null )
            {
                icon = ImageDescriptor.getMissingImageDescriptor();
            }
            bean.setIcon( icon );
            bean.setClassName( member.getAttribute( "class" ) );
            bean.setEditorId( member.getAttribute( "editorId" ) );
            bean.setMultiWindow( "true".equalsIgnoreCase( member.getAttribute( "multiWindow" ) ) );
            bean.setPriority( Integer.parseInt( member.getAttribute( "priority" ) ) );
        }

        return entryEditorExtensions;
    }
}
