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
package org.apache.directory.studio.apacheds.configuration;


import java.net.URL;

import org.apache.directory.studio.apacheds.configuration.model.ServerXmlIO;
import org.apache.directory.studio.apacheds.configuration.model.ServerXmlV152IO;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import org.apache.directory.studio.apacheds.configuration.model.ServerXmlV150IO;
import org.apache.directory.studio.apacheds.configuration.model.ServerXmlV151IO;


/**
 * The activator class controls the plug-in life cycle.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ApacheDSConfigurationPlugin extends AbstractUIPlugin
{
    /** The plug-in ID */
    public static final String PLUGIN_ID = "org.apache.directory.studio.apacheds.configuration"; //$NON-NLS-1$

    /** The shared instance */
    private static ApacheDSConfigurationPlugin plugin;

    private ServerXmlIO[] serverXmlIOs = new ServerXmlIO[]
        { new ServerXmlV152IO(), new ServerXmlV151IO(), new ServerXmlV150IO(), };


    /**
     * Creates a new instance of Activator.
     */
    public ApacheDSConfigurationPlugin()
    {
        plugin = this;
    }


    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start( BundleContext context ) throws Exception
    {
        super.start( context );
    }


    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop( BundleContext context ) throws Exception
    {
        super.stop( context );
    }


    /**
     * Returns the shared instance.
     *
     * @return
     *      the shared instance
     */
    public static ApacheDSConfigurationPlugin getDefault()
    {
        return plugin;
    }


    /**
     * Gets the array of available {@link ServerXmlIO} classes that 
     * implements a parser and a writer for the 'server.xml' file. 
     *
     * @return
     *      the array of available {@link ServerXmlIO} classes
     */
    public ServerXmlIO[] getServerXmlIOs()
    {
        return serverXmlIOs;
    }


    /**
     * Returns the button with respect to the font metrics.
     *
     * @param control a control
     * @return the button width
     */
    public static int getButtonWidth( Control control )
    {
        GC gc = new GC( control );
        gc.setFont( JFaceResources.getDialogFont() );
        FontMetrics fontMetrics = gc.getFontMetrics();
        gc.dispose();

        int width = Dialog.convertHorizontalDLUsToPixels( fontMetrics, IDialogConstants.BUTTON_WIDTH );
        return width;
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
}
