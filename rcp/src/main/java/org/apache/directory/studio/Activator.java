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

package org.apache.directory.studio;


import java.io.IOException;
import java.util.PropertyResourceBundle;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * The main plugin class to be used in the desktop.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class Activator extends AbstractUIPlugin
{

    //The shared instance.
    private static Activator plugin;

    /** The plugin properties */
    private PropertyResourceBundle properties;


    /**
     * The constructor.
     */
    public Activator()
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
        plugin = null;
        super.stop( context );
    }


    /**
     * Returns the shared instance.
     *
     * @return the shared instance.
     */
    public static Activator getDefault()
    {
        return plugin;
    }


    /**
     * Returns an image descriptor for the image file at the given
     * plug-in relative path.
     *
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor( String path )
    {
        return AbstractUIPlugin.imageDescriptorFromPlugin( Application.PLUGIN_ID, path );
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
                    new Status( Status.ERROR, "org.apache.directory.studio.rcp", Status.OK, //$NON-NLS-1$
                        "Unable to get the plugin properties.", e ) ); //$NON-NLS-1$
            }
        }

        return properties;
    }
}
