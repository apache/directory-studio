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
package org.apache.directory.ldapstudio.aciitemeditor;


import java.io.IOException;

import org.apache.directory.ldapstudio.aciitemeditor.sourceeditor.ACICodeScanner;
import org.apache.directory.ldapstudio.aciitemeditor.sourceeditor.ACITextAttributeProvider;
import org.apache.directory.shared.ldap.aci.ACIItemParser;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.templates.GlobalTemplateVariables;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * The activator class controls the plug-in life cycle
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class Activator extends AbstractUIPlugin
{

    /** The plug-in ID */
    public static final String PLUGIN_ID = "org.apache.directory.ldapstudio.aciitemeditor"; //$NON-NLS-1$

    /** The shared instance */
    private static Activator plugin;

    /** The shared ACI Item parser */
    private ACIItemParser aciItemParser;

    /** The shared ACI Code Scanner */
    private ACICodeScanner aciCodeScanner;

    /** The shared ACI TextAttribute Provider */
    private ACITextAttributeProvider textAttributeProvider;

    /** The template store */
    private ContributionTemplateStore aciTemplateStore;

    /** The context type registry */
    private ContributionContextTypeRegistry aciTemplateContextTypeRegistry;


    /**
     * The constructor
     */
    public Activator()
    {
        plugin = this;
    }


    /**
     * {@inheritDoc}
     */
    public void start( BundleContext context ) throws Exception
    {
        super.start( context );

        // ACI Template ContextType Registry initialization
        if ( aciTemplateContextTypeRegistry == null )
        {
            aciTemplateContextTypeRegistry = new ContributionContextTypeRegistry();

            aciTemplateContextTypeRegistry.addContextType( ACIITemConstants.ACI_ITEM_TEMPLATE_ID );
            aciTemplateContextTypeRegistry.getContextType( ACIITemConstants.ACI_ITEM_TEMPLATE_ID ).addResolver(
                new GlobalTemplateVariables.Cursor() );
        }

        // ACI Template Store initialization
        if ( aciTemplateStore == null )
        {
            aciTemplateStore = new ContributionTemplateStore( getAciTemplateContextTypeRegistry(),
                getPreferenceStore(), "templates" ); //$NON-NLS-1$
            try
            {
                aciTemplateStore.load();
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
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static Activator getDefault()
    {
        return plugin;
    }


    /**
     * Returns an image descriptor for the image file at the given
     * plug-in relative path
     *
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor( String path )
    {
        return imageDescriptorFromPlugin( PLUGIN_ID, path );
    }


    /**
     * Use this method to get SWT images. A ImageRegistry is used 
     * to manage the the path->Image mapping.
     * <p>
     * Note: Don't dispose the returned SWT Image. It is disposed
     * automatically when the plugin is stopped.
     * 
     * @param path the path 
     * @return The SWT Image or null
     */
    public Image getImage( String path )
    {
        Image image = getImageRegistry().get( path );
        if ( image == null )
        {
            ImageDescriptor id = getImageDescriptor( path );
            if ( id != null )
            {
                image = id.createImage();
                getImageRegistry().put( path, image );
            }
        }
        return image;
    }


    /**
     * Returns the shared ACI item parser. Take care that 
     * the parser isn't used concurrently.
     *
     * @return the shared ACI item parser.
     */
    public ACIItemParser getACIItemParser()
    {
        if ( aciItemParser == null )
        {
            aciItemParser = new ACIItemParser( null );
        }
        return aciItemParser;
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
     * Returns the TextAttribute Provider
     * 
     * @return
     *      the TextAttribute Provider
     */
    public ACITextAttributeProvider getTextAttributeProvider()
    {
        if ( textAttributeProvider == null )
        {
            textAttributeProvider = new ACITextAttributeProvider();
        }
        return textAttributeProvider;
    }


    /**
     * Retuns the the Aci Code Scanner
     * 
     * @return 
     *      the the Aci Code Scanner
     */
    public ACICodeScanner getAciCodeScanner()
    {
        if ( aciCodeScanner == null )
        {
            aciCodeScanner = new ACICodeScanner( getTextAttributeProvider() );
        }
        return aciCodeScanner;
    }


    /**
     * Gets the ACI Template ContextType Registry
     *
     * @return
     *      the ACI Template ContextType Registry
     */
    public ContributionContextTypeRegistry getAciTemplateContextTypeRegistry()
    {
        return aciTemplateContextTypeRegistry;
    }


    /**
     * Gets the ACI Template Store
     *
     * @return
     *      the ACI Template Store
     */
    public ContributionTemplateStore getAciTemplateStore()
    {
        return aciTemplateStore;
    }

}
