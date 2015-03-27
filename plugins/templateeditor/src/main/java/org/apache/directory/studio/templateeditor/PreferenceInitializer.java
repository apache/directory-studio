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
package org.apache.directory.studio.templateeditor;


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
        IPreferenceStore store = EntryTemplatePlugin.getDefault().getPreferenceStore();

        // Preferences
        store.setDefault( EntryTemplatePluginConstants.PREF_TEMPLATES_PRESENTATION,
            EntryTemplatePluginConstants.PREF_TEMPLATES_PRESENTATION_OBJECT_CLASS );
        store.setDefault( EntryTemplatePluginConstants.PREF_DISABLED_TEMPLATES, "" ); //$NON-NLS-1$
        store.setDefault( EntryTemplatePluginConstants.PREF_USE_TEMPLATE_EDITOR_FOR,
            EntryTemplatePluginConstants.PREF_USE_TEMPLATE_EDITOR_FOR_ENTRIES_WITH_TEMPLATE );

        // Dialogs
        store.setDefault( EntryTemplatePluginConstants.DIALOG_IMPORT_TEMPLATES, System.getProperty( "user.home" ) ); //$NON-NLS-1$
    }
}
