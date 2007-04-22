package org.apache.directory.ldapstudio.browser.common;


import java.io.IOException;
import java.net.URL;

import org.apache.directory.ldapstudio.browser.core.BrowserCorePlugin;
import org.apache.directory.ldapstudio.browser.core.events.EventRunner;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
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
 * The activator class controls the plug-in life cycle
 */
public class BrowserCommonActivator extends AbstractUIPlugin
{

    /** The plug-in ID */
    public static final String PLUGIN_ID = "org.apache.directory.ldapstudio.browser.common";

    /** The shared instance */
    private static BrowserCommonActivator plugin;

    /** The font registry */
    private FontRegistry fontRegistry;

    /** The color registry */
    private ColorRegistry colorRegistry;

    /** The event dispatcher */
    private ExceptionHandler exceptionHandler;

    /** The value editor preferences */
    private ValueEditorsPreferences valueEditorPreferences;

    /** The filter template store. */
    private ContributionTemplateStore filterTemplateStore;

    /** The filter template context type registry. */
    private ContributionContextTypeRegistry filterTemplateContextTypeRegistry;

    /** The event runner. */
    private EventRunner eventRunner;


    /**
     * The constructor
     */
    public BrowserCommonActivator()
    {
        plugin = this;
    }


    /**
     * {@inheritDoc}
     */
    public void start( BundleContext context ) throws Exception
    {
        super.start( context );

        if ( eventRunner == null )
        {
            eventRunner = new UiThreadEventRunner();
        }

        if ( fontRegistry == null )
        {
            fontRegistry = new FontRegistry( getWorkbench().getDisplay() );
        }

        if ( colorRegistry == null )
        {
            colorRegistry = new ColorRegistry( getWorkbench().getDisplay() );
        }

        if ( exceptionHandler == null )
        {
            exceptionHandler = new ExceptionHandler();
        }

        valueEditorPreferences = new ValueEditorsPreferences();

        if ( filterTemplateContextTypeRegistry == null )
        {
            filterTemplateContextTypeRegistry = new ContributionContextTypeRegistry();
            filterTemplateContextTypeRegistry.addContextType( BrowserCommonConstants.FILTER_TEMPLATE_ID );
            filterTemplateContextTypeRegistry.getContextType( BrowserCommonConstants.FILTER_TEMPLATE_ID ).addResolver(
                new GlobalTemplateVariables.Cursor() );
        }

        if ( filterTemplateStore == null )
        {
            filterTemplateStore = new ContributionTemplateStore( getFilterTemplateContextTypeRegistry(),
                getPreferenceStore(), "templates" );
            try
            {
                filterTemplateStore.load();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }

        BrowserCorePlugin.getDefault().setAuthHandler( new BrowserCommonAuthHandler() );
        BrowserCorePlugin.getDefault().setReferralHandler( new BrowserCommonReferralHandler() );
    }


    /**
     * {@inheritDoc}
     */
    public void stop( BundleContext context ) throws Exception
    {
        plugin = null;
        super.stop( context );

        if ( eventRunner != null )
        {
            eventRunner = null;
        }

        if ( fontRegistry != null )
        {
            fontRegistry = null;
        }

        if ( colorRegistry != null )
        {
            colorRegistry = null;
        }

        if ( exceptionHandler != null )
        {
            exceptionHandler = null;
        }

        if ( filterTemplateContextTypeRegistry != null )
        {
            filterTemplateContextTypeRegistry = null;
        }

        if ( filterTemplateStore != null )
        {
            try
            {
                filterTemplateStore.save();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
            filterTemplateStore = null;
        }
    }


    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static BrowserCommonActivator getDefault()
    {
        return plugin;
    }


    /**
     * Use this method to get SWT images. Use the IMG_ constants from
     * BrowserWidgetsConstants for the key.
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
     * BrowserWidgetsConstants for the key. A ImageRegistry is used to manage the
     * the key->Image mapping.
     * <p>
     * Note: Don't dispose the returned SWT Image. It is disposed
     * automatically when the plugin is stopped.
     *
     * @param key
     *                The key (relative path to the image im filesystem)
     * @return The SWT Image or null
     * @see BrowserCommonConstants
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
        if ( !fontRegistry.hasValueFor( fontData[0].toString() ) )
        {
            fontRegistry.put( fontData[0].toString(), fontData );
        }

        return fontRegistry.get( fontData[0].toString() );
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
     *
     * @return The exception handler
     */
    public ExceptionHandler getExceptionHandler()
    {
        return exceptionHandler;
    }


    /**
     * Gets the value editors preferences.
     *
     * @return the value editors preferences
     */
    public ValueEditorsPreferences getValueEditorsPreferences()
    {
        return valueEditorPreferences;
    }


    /**
     *
     * @return The filter template store
     */
    public TemplateStore getFilterTemplateStore()
    {
        return filterTemplateStore;
    }


    /**
     *
     * @return The filter template context type registry
     */
    public ContextTypeRegistry getFilterTemplateContextTypeRegistry()
    {
        return filterTemplateContextTypeRegistry;
    }


    /**
     * Checks, if this plugins runs in the Eclipse IDE or in RCP environment.
     * This is done by looking for the Resource perspective extensions.
     *
     * @return true if this plugin runs in IDE environment
     */
    public static boolean isIDEEnvironment()
    {
        IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(
            "org.eclipse.ui.perspectives" );
        if ( extensionPoint != null )
        {
            IExtension[] extensions = extensionPoint.getExtensions();
            if ( extensions != null )
            {
                for ( int i = 0; i < extensions.length; i++ )
                {
                    IExtension extension = extensions[i];
                    IConfigurationElement[] elements = extension.getConfigurationElements();
                    for ( int j = 0; j < elements.length; j++ )
                    {
                        IConfigurationElement element = elements[j];
                        if ( element.getName().equals( "perspective" ) )
                        {
                            if ( "org.eclipse.ui.resourcePerspective".equals( element.getAttribute( "id" ) ) )
                            {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }


    /**
     * Gets the event runner.
     *
     * @return the event runner
     */
    public EventRunner getEventRunner()
    {
        return eventRunner;
    }

}
