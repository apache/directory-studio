package org.apache.directory.studio.openldap.config;


/**
 * This interface contains all the Constants used in the Plugin.
 */
public interface OpenLdapConfigurationPluginConstants
{
    /** The plug-in ID */
    public static final String PLUGIN_ID = OpenLdapConfigurationPlugin.getDefault().getPluginProperties()
        .getString( "Plugin_id" ); //$NON-NLS-1$

    // ------
    // IMAGES
    // ------
    public static final String IMG_EDITOR = "resources/icons/editor.gif"; //$NON-NLS-1$
    public static final String IMG_DATABASE = "resources/icons/database.gif"; //$NON-NLS-1$
    public static final String IMG_OVERLAY = "resources/icons/overlay.gif"; //$NON-NLS-1$
}
