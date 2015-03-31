package org.apache.directory.studio.combinededitor;


import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;


/**
 * This class initializes the preferences of the plugin.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer
{
    /**
     * {@inheritDoc}
     */
    public void initializeDefaultPreferences()
    {
        IPreferenceStore store = CombinedEditorPlugin.getDefault().getPreferenceStore();

        // Preferences
        store.setDefault( CombinedEditorPluginConstants.PREF_DEFAULT_EDITOR,
            CombinedEditorPluginConstants.PREF_DEFAULT_EDITOR_TEMPLATE );
        store.setDefault( CombinedEditorPluginConstants.PREF_AUTO_SWITCH_TO_ANOTHER_EDITOR, true );
        store.setDefault( CombinedEditorPluginConstants.PREF_AUTO_SWITCH_EDITOR,
            CombinedEditorPluginConstants.PREF_AUTO_SWITCH_EDITOR_TABLE );
    }
}
