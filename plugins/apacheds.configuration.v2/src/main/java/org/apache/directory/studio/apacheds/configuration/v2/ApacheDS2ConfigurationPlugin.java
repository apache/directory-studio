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
package org.apache.directory.studio.apacheds.configuration.v2;


import java.io.IOException;
import java.net.URL;
import java.util.PropertyResourceBundle;

import org.apache.directory.api.ldap.model.schema.SchemaManager;
import org.apache.directory.api.ldap.model.schema.registries.SchemaLoader;
import org.apache.directory.api.ldap.schemaloader.JarLdifSchemaLoader;
import org.apache.directory.api.ldap.schemamanager.impl.DefaultSchemaManager;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * The activator class controls the plug-in life cycle.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ApacheDS2ConfigurationPlugin extends AbstractUIPlugin
{
    /** The shared instance */
    private static ApacheDS2ConfigurationPlugin plugin;

    /** The plugin properties */
    private PropertyResourceBundle properties;

    /** The schema manager */
    private SchemaManager schemaManager;


    /**
     * Creates a new instance of ApacheDS2ConfigurationPlugin.
     */
    public ApacheDS2ConfigurationPlugin()
    {
        plugin = this;
    }


    /**
     * {@inheritDoc}
     */
    public void start( BundleContext context ) throws Exception
    {
        super.start( context );
    }


    /**
     * {@inheritDoc}
     */
    public void stop( BundleContext context ) throws Exception
    {
        super.stop( context );
    }


    /**
     * Gets the schema manager.
     *
     * @return
     *      the schema manager
     * @throws Exception
     *      if an error occurs when initializing the schema manager
     */
    public SchemaManager getSchemaManager() throws Exception
    {
        // Is the schema manager initialized?
        if ( schemaManager == null )
        {
            // Initializing the schema loader and schema manager
            SchemaLoader loader = new JarLdifSchemaLoader();
            schemaManager = new DefaultSchemaManager( loader );

            // Loading only the 'adsconfig' schema with its dependencies
            schemaManager.loadWithDeps( "adsconfig" ); //$NON-NLS-1$

            // Checking if no error occurred when loading the schemas
            if ( schemaManager.getErrors().size() != 0 )
            {
                throw new Exception( Messages.getString("ApacheDS2ConfigurationPlugin.CouldNotLoadSchemaCorrectly") ); //$NON-NLS-1$
            }
        }

        return schemaManager;
    }


    /**
     * Returns the shared instance.
     *
     * @return
     *      the shared instance
     */
    public static ApacheDS2ConfigurationPlugin getDefault()
    {
        return plugin;
    }


    /**
     * Use this method to get SWT images. Use the IMG_ constants from
     * PluginConstants for the key.
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
     * PluginConstants for the key. A ImageRegistry is used to manage the
     * the key->Image mapping.
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
                getLog().log(
                    new Status( Status.ERROR, "org.apache.directory.studio.apacheds.configuration.v2", Status.OK, //$NON-NLS-1$
                        Messages.getString( "ApacheDS2ConfigurationPlugin.UnableGetProperties" ), e ) ); //$NON-NLS-1$
            }
        }

        return properties;
    }
}
