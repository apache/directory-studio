package org.apache.directory.studio.combinededitor;


/**
 * This interface contains all the Constants used in the Plugin.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface CombinedEditorPluginConstants
{
    /** The plug-in ID */
    public static final String PLUGIN_ID = CombinedEditorPlugin.getDefault().getPluginProperties().getString(
        "Plugin_id" ); //$NON-NLS-1$

    // Preferences

    public static final String PREF_DEFAULT_EDITOR = PLUGIN_ID + ".prefs.DefaultEditor"; //$NON-NLS-1$
    public static final int PREF_DEFAULT_EDITOR_TEMPLATE = 1;
    public static final int PREF_DEFAULT_EDITOR_TABLE = 2;
    public static final int PREF_DEFAULT_EDITOR_LDIF = 3;
    public static final String PREF_AUTO_SWITCH_TO_ANOTHER_EDITOR = PLUGIN_ID + ".prefs.AutoSwitchToAnotherEditor"; //$NON-NLS-1$
    public static final String PREF_AUTO_SWITCH_EDITOR = PLUGIN_ID + ".prefs.AutoSwitchEditor"; //$NON-NLS-1$
    public static final int PREF_AUTO_SWITCH_EDITOR_TABLE = 1;
    public static final int PREF_AUTO_SWITCH_EDITOR_LDIF = 2;
}
