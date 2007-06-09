package org.apache.directory.studio.ldifeditor;


import java.io.IOException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.GlobalTemplateVariables;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * The activator class controls the plug-in life cycle
 */
public class LdifEditorActivator extends AbstractUIPlugin
{

    /** The plugin ID */
    public static final String PLUGIN_ID = "org.apache.directory.studio.ldifeditor";

    /** The shared instance */
    private static LdifEditorActivator plugin;

    /** Resource bundle */
    private ResourceBundle resourceBundle;
    
    /** The color registry */
    private ColorRegistry colorRegistry;

    /** The template store */
    private ContributionTemplateStore ldifTemplateStore;

    /** The context type registry */
    private ContributionContextTypeRegistry ldifTemplateContextTypeRegistry;


    /**
     * The constructor
     */
    public LdifEditorActivator()
    {
        plugin = this;
        
        try
        {
            resourceBundle = ResourceBundle.getBundle( "org.apache.directory.studio.ldifeditor.messages" );
        }
        catch ( MissingResourceException x )
        {
            resourceBundle = null;
        }
    }


    /**
     * {@inheritDoc} 
     */
    public void start( BundleContext context ) throws Exception
    {
        super.start( context );

        if ( colorRegistry == null )
        {
            colorRegistry = new ColorRegistry( getWorkbench().getDisplay() );
        }

        if ( ldifTemplateContextTypeRegistry == null )
        {
            ldifTemplateContextTypeRegistry = new ContributionContextTypeRegistry();

            ldifTemplateContextTypeRegistry.addContextType( LdifEditorConstants.LDIF_FILE_TEMPLATE_ID );
            ldifTemplateContextTypeRegistry.getContextType( LdifEditorConstants.LDIF_FILE_TEMPLATE_ID ).addResolver(
                new GlobalTemplateVariables.Cursor() );

            ldifTemplateContextTypeRegistry.addContextType( LdifEditorConstants.LDIF_ATTR_VAL_RECORD_TEMPLATE_ID );
            ldifTemplateContextTypeRegistry.getContextType( LdifEditorConstants.LDIF_ATTR_VAL_RECORD_TEMPLATE_ID )
                .addResolver( new GlobalTemplateVariables.Cursor() );

            ldifTemplateContextTypeRegistry.addContextType( LdifEditorConstants.LDIF_MODIFICATION_RECORD_TEMPLATE_ID );
            ldifTemplateContextTypeRegistry.getContextType( LdifEditorConstants.LDIF_MODIFICATION_RECORD_TEMPLATE_ID )
                .addResolver( new GlobalTemplateVariables.Cursor() );

            ldifTemplateContextTypeRegistry.addContextType( LdifEditorConstants.LDIF_MODIFICATION_ITEM_TEMPLATE_ID );

            ldifTemplateContextTypeRegistry.addContextType( LdifEditorConstants.LDIF_MODDN_RECORD_TEMPLATE_ID );
        }

        if ( ldifTemplateStore == null )
        {
            ldifTemplateStore = new ContributionTemplateStore( getLdifTemplateContextTypeRegistry(),
                getPreferenceStore(), "templates" );
            try
            {
                ldifTemplateStore.load();
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

        if ( colorRegistry != null )
        {
            colorRegistry = null;
        }

        if ( ldifTemplateContextTypeRegistry != null )
        {
            ldifTemplateContextTypeRegistry = null;
        }

        if ( ldifTemplateStore != null )
        {
            try
            {
                ldifTemplateStore.save();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
            ldifTemplateStore = null;
        }
    }


    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static LdifEditorActivator getDefault()
    {
        return plugin;
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
        if ( !colorRegistry.hasValueFor( rgb.toString() ) )
        {
            colorRegistry.put( rgb.toString(), rgb );
        }

        return colorRegistry.get( rgb.toString() );
    }


    /**
     * Use this method to get SWT images. Use the IMG_ constants from
     * LdifEditorConstants for the key.
     * 
     * @param key
     *                The key (relative path to the image im filesystem)
     * @return The image discriptor or null
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
     * LdifEditorConstants for the key. A ImageRegistry is used to manage the
     * the key->Image mapping.
     * <p>
     * Note: Don't dispose the returned SWT Image. It is disposed
     * automatically when the plugin is stopped.
     * 
     * @param key
     *                The key (relative path to the image im filesystem)
     * @return The SWT Image or null
     * @see LdifEditorConstants
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
     * 
     * @return The LDIF template context type registry
     */
    public ContextTypeRegistry getLdifTemplateContextTypeRegistry()
    {
        return ldifTemplateContextTypeRegistry;
    }
    

    /**
     * 
     * @return The LDIF template store
     */
    public TemplateStore getLdifTemplateStore()
    {
        return ldifTemplateStore;
    }
    
    
    /**
     * Gets the resource bundle.
     * 
     * @return the resource bundle
     */
    public ResourceBundle getResourceBundle()
    {
        return resourceBundle;
    }

}
