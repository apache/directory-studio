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


/**
 * This interface contains all the Constants used in the Plugin.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface EntryTemplatePluginConstants
{
    /** The plug-in ID */
    String PLUGIN_ID = EntryTemplatePluginConstants.class.getPackage().getName();

    // Images
    String IMG_EXPORT_TEMPLATES_WIZARD = "resources/icons/export_templates_wizard.png"; //$NON-NLS-1$
    String IMG_FILE = "resources/icons/file.png"; //$NON-NLS-1$
    String IMG_IMPORT_TEMPLATES_WIZARD = "resources/icons/import_templates_wizard.png"; //$NON-NLS-1$
    String IMG_NO_IMAGE = "resources/icons/no_image.png"; //$NON-NLS-1$
    String IMG_OBJECT_CLASS = "resources/icons/object_class.png"; //$NON-NLS-1$
    String IMG_SWITCH_TEMPLATE = "resources/icons/switch_template.png"; //$NON-NLS-1$
    String IMG_TEMPLATE = "resources/icons/template.png"; //$NON-NLS-1$
    String IMG_TEMPLATE_DISABLED = "resources/icons/template_disabled.png"; //$NON-NLS-1$
    String IMG_TOOLBAR_ADD_VALUE = "resources/icons/toolbar_add_value.png"; //$NON-NLS-1$
    String IMG_TOOLBAR_BROWSE_FILE = "resources/icons/toolbar_browse_file.png"; //$NON-NLS-1$
    String IMG_TOOLBAR_BROWSE_IMAGE = "resources/icons/toolbar_browse_image.png"; //$NON-NLS-1$
    String IMG_TOOLBAR_CLEAR = "resources/icons/toolbar_clear.png"; //$NON-NLS-1$
    String IMG_TOOLBAR_DELETE_VALUE = "resources/icons/toolbar_delete_value.png"; //$NON-NLS-1$
    String IMG_TOOLBAR_EDIT_PASSWORD = "resources/icons/toolbar_edit_password.png"; //$NON-NLS-1$
    String IMG_TOOLBAR_EDIT_DATE = "resources/icons/toolbar_edit_date.png"; //$NON-NLS-1$
    String IMG_TOOLBAR_EDIT_VALUE = "resources/icons/toolbar_edit_value.png"; //$NON-NLS-1$
    String IMG_TOOLBAR_SAVE_AS = "resources/icons/toolbar_save_as.png"; //$NON-NLS-1$

    // Preferences
    String PREF_TEMPLATE_ENTRY_EDITOR_PAGE_ID = "org.apache.directory.studio.templateeditor.view.preferences.TemplateEntryEditorPreferencePage"; //$NON-NLS-1$
    String PREF_TEMPLATES_PRESENTATION = PLUGIN_ID + ".prefs.TemplatesPresentation"; //$NON-NLS-1$
    int PREF_TEMPLATES_PRESENTATION_TEMPLATE = 1;
    int PREF_TEMPLATES_PRESENTATION_OBJECT_CLASS = 2;
    String PREF_DISABLED_TEMPLATES = PLUGIN_ID + ".prefs.DisabledTemplates"; //$NON-NLS-1$
    String PREF_USE_TEMPLATE_EDITOR_FOR = PLUGIN_ID + ".prefs.UseTemplateEditorFor"; //$NON-NLS-1$
    int PREF_USE_TEMPLATE_EDITOR_FOR_ANY_ENTRY = 1;
    int PREF_USE_TEMPLATE_EDITOR_FOR_ENTRIES_WITH_TEMPLATE = 2;
    String PREF_DEFAULT_TEMPLATES = PLUGIN_ID + ".prefs.DefaultTemplates"; //$NON-NLS-1$

    // Dialogs
    String DIALOG_IMPORT_TEMPLATES = PLUGIN_ID + ".dialog.ImportTemplates"; //$NON-NLS-1$
    String DIALOG_EXPORT_TEMPLATES = PLUGIN_ID + ".dialog.ExportTemplates"; //$NON-NLS-1$
}
