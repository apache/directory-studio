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


import java.io.IOException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.directory.ldapstudio.browser.core.BrowserCorePlugin;
import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.GlobalTemplateVariables;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * The main plugin class to be used in the desktop.
 */
public class BrowserUIPlugin extends AbstractUIPlugin
{

    // The plugin ID
    public static final String PLUGIN_ID = "org.apache.directory.ldapstudio.browser.ui";

    // The shared instance.
    private static BrowserUIPlugin plugin;

    // Resource bundle.
    private ResourceBundle resourceBundle;

    // The event dispatcher
    private ExceptionHandler exceptionHandler;

    // The font registry
    private FontRegistry fontRegistry;

    // The color registry
    private ColorRegistry colorRegistry;

    // The template store
    private ContributionTemplateStore ldifTemplateStore;

    private ContributionTemplateStore filterTemplateStore;

    // The context type registry
    private ContributionContextTypeRegistry ldifTemplateContextTypeRegistry;

    private ContributionContextTypeRegistry filterTemplateContextTypeRegistry;

    // The event dispatcher
    private EventDispatcherSync eventDispatcher;

    // The preferences
    private BrowserUIPreferences uiPreferences;


    /**
     * The constructor.
     */
    public BrowserUIPlugin()
    {
        plugin = this;
        this.uiPreferences = new BrowserUIPreferences();
        try
        {
            resourceBundle = ResourceBundle.getBundle( "org.apache.directory.ldapstudio.browser.ui.browseruimessages" );
        }
        catch ( MissingResourceException x )
        {
            resourceBundle = null;
        }
    }


    /**
     * This method is called upon plug-in activation
     */
    public void start( BundleContext context ) throws Exception
    {
        super.start( context );

        if ( this.eventDispatcher == null )
        {
            this.eventDispatcher = new EventDispatcherSync();
            this.eventDispatcher.startEventDispatcher();
        }
        EventRegistry.init( this.eventDispatcher );

        if ( this.exceptionHandler == null )
        {
            this.exceptionHandler = new ExceptionHandler();
        }

        if ( this.fontRegistry == null )
        {
            this.fontRegistry = new FontRegistry( this.getWorkbench().getDisplay() );
        }
        if ( this.colorRegistry == null )
        {
            this.colorRegistry = new ColorRegistry( this.getWorkbench().getDisplay() );
        }
        if ( this.filterTemplateContextTypeRegistry == null )
        {
            this.filterTemplateContextTypeRegistry = new ContributionContextTypeRegistry();
            this.filterTemplateContextTypeRegistry.addContextType( BrowserUIConstants.FILTER_TEMPLATE_ID );
            this.filterTemplateContextTypeRegistry.getContextType( BrowserUIConstants.FILTER_TEMPLATE_ID ).addResolver(
                new GlobalTemplateVariables.Cursor() );
        }
        if ( this.ldifTemplateContextTypeRegistry == null )
        {
            this.ldifTemplateContextTypeRegistry = new ContributionContextTypeRegistry();

            this.ldifTemplateContextTypeRegistry.addContextType( BrowserUIConstants.LDIF_FILE_TEMPLATE_ID );
            this.ldifTemplateContextTypeRegistry.getContextType( BrowserUIConstants.LDIF_FILE_TEMPLATE_ID )
                .addResolver( new GlobalTemplateVariables.Cursor() );

            this.ldifTemplateContextTypeRegistry.addContextType( BrowserUIConstants.LDIF_ATTR_VAL_RECORD_TEMPLATE_ID );
            this.ldifTemplateContextTypeRegistry.getContextType( BrowserUIConstants.LDIF_ATTR_VAL_RECORD_TEMPLATE_ID )
                .addResolver( new GlobalTemplateVariables.Cursor() );

            this.ldifTemplateContextTypeRegistry
                .addContextType( BrowserUIConstants.LDIF_MODIFICATION_RECORD_TEMPLATE_ID );
            this.ldifTemplateContextTypeRegistry.getContextType(
                BrowserUIConstants.LDIF_MODIFICATION_RECORD_TEMPLATE_ID ).addResolver(
                new GlobalTemplateVariables.Cursor() );

            this.ldifTemplateContextTypeRegistry.addContextType( BrowserUIConstants.LDIF_MODIFICATION_ITEM_TEMPLATE_ID );

            this.ldifTemplateContextTypeRegistry.addContextType( BrowserUIConstants.LDIF_MODDN_RECORD_TEMPLATE_ID );
        }
        if ( this.filterTemplateStore == null )
        {
            this.filterTemplateStore = new ContributionTemplateStore( getFilterTemplateContextTypeRegistry(),
                getPreferenceStore(), "templates" );
            try
            {
                this.filterTemplateStore.load();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }
        if ( this.ldifTemplateStore == null )
        {
            this.ldifTemplateStore = new ContributionTemplateStore( getLdifTemplateContextTypeRegistry(),
                getPreferenceStore(), "templates" );
            try
            {
                this.ldifTemplateStore.load();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }

        BrowserCorePlugin.getDefault().setAuthHandler( new BrowserUIAuthHandler() );
        BrowserCorePlugin.getDefault().setReferralHandler( new BrowserUIReferralHandler() );
    }


    /**
     * This method is called when the plug-in is stopped
     */
    public void stop( BundleContext context ) throws Exception
    {
        super.stop( context );

        if ( this.eventDispatcher != null )
        {
            this.eventDispatcher.stopEventDispatcher();
            this.eventDispatcher = null;
        }

        if ( this.exceptionHandler != null )
        {
            this.exceptionHandler = null;
        }

        if ( this.fontRegistry != null )
        {
            this.fontRegistry = null;
        }
        if ( this.colorRegistry != null )
        {
            this.colorRegistry = null;
        }
        if ( this.filterTemplateContextTypeRegistry != null )
        {
            this.filterTemplateContextTypeRegistry = null;
        }
        if ( this.ldifTemplateContextTypeRegistry != null )
        {
            this.ldifTemplateContextTypeRegistry = null;
        }
        if ( this.filterTemplateStore != null )
        {
            try
            {
                this.filterTemplateStore.save();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
            this.filterTemplateStore = null;
        }
        if ( this.ldifTemplateStore != null )
        {
            try
            {
                this.ldifTemplateStore.save();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
            this.ldifTemplateStore = null;
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


    public ResourceBundle getResourceBundle()
    {
        return resourceBundle;
    }


    public static String getResourceString( String key )
    {
        ResourceBundle bundle = getDefault().getResourceBundle();
        try
        {
            return ( bundle != null ) ? bundle.getString( key ) : key;
        }
        catch ( MissingResourceException e )
        {
            return key;
        }
    }


    /**
     * 
     * @return The exception handler
     */
    public ExceptionHandler getExceptionHandler()
    {
        return this.exceptionHandler;
    }


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
     * Use this method to get SWT fonts. A FontRegistry is used to manage
     * the FontData[]->Font mapping.
     * <p>
     * Note: Don't dispose the returned SWT Font. It is disposed
     * automatically when the plugin is stopped.
     * 
     * @param fontData
     *                the font data
     * @return The SWT Font
     */

    public Font getFont( FontData[] fontData )
    {
        if ( !this.fontRegistry.hasValueFor( fontData[0].toString() ) )
            this.fontRegistry.put( fontData[0].toString(), fontData );

        return this.fontRegistry.get( fontData[0].toString() );
    }


    /**
     * Use this method to get SWT colors. A ColorRegistry is used to manage
     * the RGB->Color mapping.
     * <p>
     * Note: Don't dispose the returned color. It is disposed automatically
     * when the plugin is stopped.
     * 
     * @param rgb
     *                the rgb color data
     * @return The SWT Color
     */
    public Color getColor( RGB rgb )
    {
        if ( !this.colorRegistry.hasValueFor( rgb.toString() ) )
            this.colorRegistry.put( rgb.toString(), rgb );

        return this.colorRegistry.get( rgb.toString() );
    }


    /**
     * 
     * @return The filter template store
     */
    public TemplateStore getFilterTemplateStore()
    {
        return this.filterTemplateStore;
    }


    /**
     * 
     * @return The LDIF template store
     */
    public TemplateStore getLdifTemplateStore()
    {
        return this.ldifTemplateStore;
    }


    /**
     * 
     * @return The filter template context type registry
     */
    public ContextTypeRegistry getFilterTemplateContextTypeRegistry()
    {
        return this.filterTemplateContextTypeRegistry;
    }


    /**
     * 
     * @return The LDIF template context type registry
     */
    public ContextTypeRegistry getLdifTemplateContextTypeRegistry()
    {
        return this.ldifTemplateContextTypeRegistry;
    }


    public BrowserUIPreferences getUIPreferences()
    {
        return this.uiPreferences;
    }

}
