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
package org.apache.directory.studio.openldap.config.acl;


import java.io.IOException;
import java.net.URL;
import java.util.PropertyResourceBundle;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.templates.GlobalTemplateVariables;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import org.apache.directory.studio.openldap.config.acl.sourceeditor.OpenLdapAclCodeScanner;
import org.apache.directory.studio.openldap.config.acl.sourceeditor.OpenLdapAclTextAttributeProvider;


/**
 * The activator class controls the plug-in life cycle
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OpenLdapAclEditorPlugin extends AbstractUIPlugin
{
    /** The shared instance */
    private static OpenLdapAclEditorPlugin plugin;

    /** The shared OpenLDAP ACL Code Scanner */
    private OpenLdapAclCodeScanner codeScanner;

    /** The shared OpenLDAP ACL TextAttribute Provider */
    private OpenLdapAclTextAttributeProvider textAttributeProvider;

    /** The context type registry */
    private ContributionContextTypeRegistry templateContextTypeRegistry;

    /** The template store */
    private ContributionTemplateStore templateStore;

    /** The plugin properties */
    private PropertyResourceBundle properties;


    /**
     * The constructor
     */
    public OpenLdapAclEditorPlugin()
    {
        plugin = this;
    }


    /**
     * {@inheritDoc} 
     */
    public void start( BundleContext context ) throws Exception
    {
        super.start( context );

        // OpenLDAP ACL Template ContextType Registry initialization
        if ( templateContextTypeRegistry == null )
        {
            templateContextTypeRegistry = new ContributionContextTypeRegistry();

            templateContextTypeRegistry.addContextType( OpenLdapAclEditorPluginConstants.TEMPLATE_ID );
            templateContextTypeRegistry.getContextType( OpenLdapAclEditorPluginConstants.TEMPLATE_ID ).addResolver(
                new GlobalTemplateVariables.Cursor() );
        }

        // OpenLDAP ACL Template Store initialization
        if ( templateStore == null )
        {
            templateStore = new ContributionTemplateStore( getTemplateContextTypeRegistry(),
                getPreferenceStore(), "templates" ); //$NON-NLS-1$
            
            try
            {
                templateStore.load();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }
    }


    /**
     * {@inheritDoc} 
     */
    public void stop( BundleContext context ) throws Exception
    {
        plugin = null;
        super.stop( context );
    }


    /**
     * Retuns the the OpenLDAP ACL Code Scanner
     * 
     * @return the the OpenLDAP ACL Code Scanner
     */
    public OpenLdapAclCodeScanner getCodeScanner()
    {
        if ( codeScanner == null )
        {
            codeScanner = new OpenLdapAclCodeScanner( getTextAttributeProvider() );
        }
        
        return codeScanner;
    }


    /**
     * Returns the TextAttribute Provider
     * 
     * @return the TextAttribute Provider
     */
    public OpenLdapAclTextAttributeProvider getTextAttributeProvider()
    {
        if ( textAttributeProvider == null )
        {
            textAttributeProvider = new OpenLdapAclTextAttributeProvider();
        }
        
        return textAttributeProvider;
    }


    /**
     * Gets the OpenLDAP ACL Template ContextType Registry
     *
     * @return the OpenLDAP ACL Template ContextType Registry
     */
    public ContributionContextTypeRegistry getTemplateContextTypeRegistry()
    {
        return templateContextTypeRegistry;
    }


    /**
     * Gets the OpenLDAP ACL Template Store
     *
     * @return the OpenLDAP ACL Template Store
     */
    public ContributionTemplateStore getTemplateStore()
    {
        return templateStore;
    }


    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static OpenLdapAclEditorPlugin getDefault()
    {
        return plugin;
    }


    /**
     * Use this method to get SWT images. Use the IMG_ constants from
     * ValueEditorConstants for the key.
     * 
     * @param key The key (relative path to the image im filesystem)
     * @return The image discriptor or null
     */
    public ImageDescriptor getImageDescriptor( String key )
    {
        if ( key != null )
        {
            URL url = FileLocator.find( getBundle(), new Path( key ), null );
            
            if ( url != null )
            {
                return ImageDescriptor.createFromURL( url );
            }
        }

        return null;
    }


    /**
     * Use this method to get SWT images. Use the IMG_ constants from
     * ValueEditorConstants for the key. A ImageRegistry is used to manage the
     * the key->Image mapping.
     * <p>
     * Note: Don't dispose the returned SWT Image. It is disposed
     * automatically when the plugin is stopped.
     * 
     * @param key The key (relative path to the image im filesystem)
     * @return The SWT Image or null
     * @see OpenLdapAclEditorPluginConstants
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
     * @return the plugin properties
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
                getLog().log( new Status( Status.ERROR, "org.apache.directory.studio.openldap.acl.editor", Status.OK, //$NON-NLS-1$
                    Messages.getString( "OpenLdapAclEditorPlugin.UnableGetPluginProperties" ), e ) ); //$NON-NLS-1$
            }
        }

        return properties;
    }
}
