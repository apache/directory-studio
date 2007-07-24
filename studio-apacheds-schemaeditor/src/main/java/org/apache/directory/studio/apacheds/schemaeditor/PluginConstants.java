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
package org.apache.directory.studio.apacheds.schemaeditor;


import org.apache.directory.studio.apacheds.schemaeditor.view.preferences.SchemaViewPreferencePage;


/**
 * This interface contains all the Constants used in the Plugin.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface PluginConstants
{
    // Images
    public static final String IMG_ATTRIBUTE_TYPE = "resources/icons/attribute_type.gif";
    public static final String IMG_ATTRIBUTE_TYPE_NEW = "resources/icons/attribute_type_new.gif";
    public static final String IMG_ATTRIBUTE_TYPE_NEW_WIZARD = "resources/icons/attribute_type_new_wizard.png";
    public static final String IMG_ATTRIBUTE_TYPE_OVERLAY_OPERATION = "resources/icons/attribute_type_overlay_operation.gif";
    public static final String IMG_ATTRIBUTE_TYPE_OVERLAY_USER_APPLICATION = "resources/icons/attribute_type_overlay_userApplication.gif";
    public static final String IMG_CONNECT = "resources/icons/connect.gif";
    public static final String IMG_COMMIT_CHANGES_WIZARD = "resources/icons/commit_changes_wizard.png";
    public static final String IMG_COLLAPSE_ALL = "resources/icons/collapse_all.gif";
    public static final String IMG_DELETE = "resources/icons/delete.gif";
    public static final String IMG_DISCONNECT = "resources/icons/disconnect.gif";
    public static final String IMG_DIFFERENCE_ATTRIBUTE_TYPE_ADD = "resources/icons/difference_attribute_type_add.gif";
    public static final String IMG_DIFFERENCE_ATTRIBUTE_TYPE_MODIFY = "resources/icons/difference_attribute_type_modify.gif";
    public static final String IMG_DIFFERENCE_ATTRIBUTE_TYPE_REMOVE = "resources/icons/difference_attribute_type_remove.gif";
    public static final String IMG_DIFFERENCE_OBJECT_CLASS_ADD = "resources/icons/difference_object_class_add.gif";
    public static final String IMG_DIFFERENCE_OBJECT_CLASS_MODIFY = "resources/icons/difference_object_class_modify.gif";
    public static final String IMG_DIFFERENCE_OBJECT_CLASS_REMOVE = "resources/icons/difference_object_class_remove.gif";
    public static final String IMG_DIFFERENCE_PROPERTY_ADD = "resources/icons/difference_property_add.gif";
    public static final String IMG_DIFFERENCE_PROPERTY_MODIFY = "resources/icons/difference_property_modify.gif";
    public static final String IMG_DIFFERENCE_PROPERTY_REMOVE = "resources/icons/difference_property_remove.gif";
    public static final String IMG_DIFFERENCE_SCHEMA_ADD = "resources/icons/difference_schema_add.gif";
    public static final String IMG_DIFFERENCE_SCHEMA_MODIFY = "resources/icons/difference_schema_modify.gif";
    public static final String IMG_DIFFERENCE_SCHEMA_REMOVE = "resources/icons/difference_schema_remove.gif";
    public static final String IMG_FOLDER = "resources/icons/folder.gif";
    public static final String IMG_FOLDER_AT = "resources/icons/folder_at.gif";
    public static final String IMG_FOLDER_OC = "resources/icons/folder_oc.gif";
    public static final String IMG_LINK_WITH_EDITOR = "resources/icons/link_with_editor.gif";
    public static final String IMG_OBJECT_CLASS = "resources/icons/object_class.gif";
    public static final String IMG_OBJECT_CLASS_NEW = "resources/icons/object_class_new.gif";
    public static final String IMG_OBJECT_CLASS_NEW_WIZARD = "resources/icons/object_class_new_wizard.png";
    public static final String IMG_OBJECT_CLASS_OVERLAY_ABSTRACT = "resources/icons/object_class_overlay_abstract.gif";
    public static final String IMG_OBJECT_CLASS_OVERLAY_AUXILIARY = "resources/icons/object_class_overlay_auxiliary.gif";
    public static final String IMG_OBJECT_CLASS_OVERLAY_STRUCTURAL = "resources/icons/object_class_overlay_structural.gif";
    public static final String IMG_OVERLAY_ERROR = "resources/icons/overlay_error.gif";
    public static final String IMG_OVERLAY_WARNING = "resources/icons/overlay_warning.gif";
    public static final String IMG_PROBLEMS_ERROR = "resources/icons/problems_error.gif";
    public static final String IMG_PROBLEMS_GROUP = "resources/icons/problems_group.gif";
    public static final String IMG_PROBLEMS_WARNING = "resources/icons/problems_warning.gif";
    public static final String IMG_PROJECT_ADS = "resources/icons/project_ads.gif";
    public static final String IMG_PROJECT_ADS_CLOSED = "resources/icons/project_ads_closed.gif";
    public static final String IMG_PROJECT_EXPORT = "resources/icons/project_export.gif";
    public static final String IMG_PROJECT_EXPORT_WIZARD = "resources/icons/project_export_wizard.png";
    public static final String IMG_PROJECT_FILE = "resources/icons/project_file.gif";
    public static final String IMG_PROJECT_IMPORT = "resources/icons/project_import.gif";
    public static final String IMG_PROJECT_IMPORT_WIZARD = "resources/icons/project_import_wizard.png";
    public static final String IMG_PROJECT_NEW = "resources/icons/project_new.gif";
    public static final String IMG_PROJECT_NEW_WIZARD = "resources/icons/project_new_wizard.png";
    public static final String IMG_PROJECT_OFFLINE = "resources/icons/project_offline.gif";
    public static final String IMG_PROJECT_OFFLINE_CLOSED = "resources/icons/project_offline_closed.gif";
    public static final String IMG_RENAME = "resources/icons/rename.gif";
    public static final String IMG_SCHEMA = "resources/icons/schema.gif";
    public static final String IMG_SCHEMA_NEW = "resources/icons/schema_new.gif";
    public static final String IMG_SCHEMA_NEW_WIZARD = "resources/icons/schema_new_wizard.png";
    public static final String IMG_SCHEMAS_EXPORT = "resources/icons/schemas_export.gif";
    public static final String IMG_SCHEMAS_EXPORT_WIZARD = "resources/icons/schemas_export_wizard.png";
    public static final String IMG_SCHEMAS_IMPORT = "resources/icons/schemas_import.gif";
    public static final String IMG_SCHEMAS_IMPORT_WIZARD = "resources/icons/schemas_import_wizard.png";
    public static final String IMG_SORTING = "resources/icons/sorting.gif";
    public static final String IMG_TOOLBAR_MENU = "resources/icons/toolbar_menu.gif";

    // Commands
    public static final String CMD_CONNECT = Activator.PLUGIN_ID + ".commands.connect";
    public static final String CMD_CLOSE_PROJECT = Activator.PLUGIN_ID + ".commands.closeProject";
    public static final String CMD_COLLAPSE_ALL = Activator.PLUGIN_ID + ".commands.collapseAll";
    public static final String CMD_DELETE_PROJECT = Activator.PLUGIN_ID + ".commands.deleteProject";
    public static final String CMD_DELETE_SCHEMA_ELEMENT = Activator.PLUGIN_ID + ".commands.deleteSchemaElement";
    public static final String CMD_EXPORT_PROJECTS = Activator.PLUGIN_ID + ".commands.exportProjects";
    public static final String CMD_EXPORT_SCHEMAS_AS_OPENLDAP = Activator.PLUGIN_ID
        + ".commands.exportSchemasAsOpenLDAP";
    public static final String CMD_EXPORT_SCHEMAS_AS_XML = Activator.PLUGIN_ID + ".commands.exportSchemasAsXML";
    public static final String CMD_IMPORT_PROJECTS = Activator.PLUGIN_ID + ".commands.importProjects";
    public static final String CMD_IMPORT_SCHEMAS_FROM_OPENLDAP = Activator.PLUGIN_ID
        + ".commands.importSchemasFromOpenLDAP";
    public static final String CMD_IMPORT_SCHEMAS_FROM_XML = Activator.PLUGIN_ID + ".commands.importSchemasFromXML";
    public static final String CMD_LINK_WITH_EDITOR = Activator.PLUGIN_ID + ".commands.linkWithEditor";
    public static final String CMD_OPEN_ELEMENT = Activator.PLUGIN_ID + ".commands.openElement";
    public static final String CMD_OPEN_PROJECT = Activator.PLUGIN_ID + ".commands.openProject";
    public static final String CMD_OPEN_SCHEMA_VIEW_PREFERENCE = Activator.PLUGIN_ID
        + ".commands.openSchemaViewPreference";
    public static final String CMD_OPEN_SCHEMA_VIEW_SORTING_DIALOG = Activator.PLUGIN_ID
        + ".commands.openSchemaViewSortingDialog";
    public static final String CMD_NEW_ATTRIBUTE_TYPE = Activator.PLUGIN_ID + ".commands.newAttributeType";
    public static final String CMD_NEW_OBJECT_CLASS = Activator.PLUGIN_ID + ".commands.newObjectClass";
    public static final String CMD_NEW_PROJECT = Activator.PLUGIN_ID + ".commands.newProject";
    public static final String CMD_NEW_SCHEMA = Activator.PLUGIN_ID + ".commands.newSchema";
    public static final String CMD_RENAME_PROJECT = Activator.PLUGIN_ID + ".commands.renameProject";

    // Preferences - DifferencesWidget
    /** The preferences ID for DifferencesWidget Grouping */
    public static final String PREFS_DIFFERENCES_WIDGET_GROUPING = Activator.PLUGIN_ID
        + ".prefs.DifferencesWidget.grouping";
    /** The preference value for DifferencesWidget Grouping 'Property' */
    public static final int PREFS_DIFFERENCES_WIDGET_GROUPING_PROPERTY = 0;
    /** The preference value for DifferencesWidget Grouping 'Property' */
    public static final int PREFS_DIFFERENCES_WIDGET_GROUPING_TYPE = 1;

    // Preferences - SchemaView
    /** The preference ID for Schema View Label */
    public static final String PREFS_SCHEMA_VIEW_LABEL = SchemaViewPreferencePage.ID + ".label.labelValue"; //$NON-NLS-1$
    /** The preference value for Schema View First Name label */
    public static final int PREFS_SCHEMA_VIEW_LABEL_FIRST_NAME = 0;
    /** The preference value for Schema View All Aliases label */
    public static final int PREFS_SCHEMA_VIEW_LABEL_ALL_ALIASES = 1;
    /** The preference value for Schema View OID label */
    public static final int PREFS_SCHEMA_VIEW_LABEL_OID = 2;
    /** The preference ID for Schema View Abbreviate */
    public static final String PREFS_SCHEMA_VIEW_ABBREVIATE = SchemaViewPreferencePage.ID + ".label.abbreviate"; //$NON-NLS-1$
    /** The preference ID for Schema View Abbreviate Max Length*/
    public static final String PREFS_SCHEMA_VIEW_ABBREVIATE_MAX_LENGTH = SchemaViewPreferencePage.ID
        + ".label.abbreviate.maxLength"; //$NON-NLS-1$
    /** The preference ID for Schema View Display Secondary Label */
    public static final String PREFS_SCHEMA_VIEW_SECONDARY_LABEL_DISPLAY = SchemaViewPreferencePage.ID
        + ".secondaryLabel.display"; //$NON-NLS-1$
    /** The preference ID for Schema View Secondary Label */
    public static final String PREFS_SCHEMA_VIEW_SECONDARY_LABEL = SchemaViewPreferencePage.ID
        + ".secondaryLabel.labelValue"; //$NON-NLS-1$
    /** The preference ID for Schema View Abbreviate Secondary Label */
    public static final String PREFS_SCHEMA_VIEW_SECONDARY_LABEL_ABBREVIATE = SchemaViewPreferencePage.ID
        + ".secondaryLabel.abbreviate"; //$NON-NLS-1$
    /** The preference ID for Schema View Abbreviate Secondary Label Max Length*/
    public static final String PREFS_SCHEMA_VIEW_SECONDARY_LABEL_ABBREVIATE_MAX_LENGTH = SchemaViewPreferencePage.ID
        + ".secondaryLabel.abbreviate.maxLength"; //$NON-NLS-1$
    /** The preference ID for Schema View Grouping */
    public static final String PREFS_SCHEMA_VIEW_GROUPING = Activator.PLUGIN_ID + ".preferences.SchemaView.grouping"; //$NON-NLS-1$
    /** The preference value for Schema View Grouping 'group ATs and OCs in folders' */
    public static final int PREFS_SCHEMA_VIEW_GROUPING_FOLDERS = 0;
    /** The preference value for Schema View Grouping 'mixed' */
    public static final int PREFS_SCHEMA_VIEW_GROUPING_MIXED = 1;
    /** The preference ID for Schema View Sorting By */
    public static final String PREFS_SCHEMA_VIEW_SORTING_BY = Activator.PLUGIN_ID + ".preferences.SchemaView.sortingBy"; //$NON-NLS-1$
    /** The preference value for Schema View Sorting 'First Name' */
    public static final int PREFS_SCHEMA_VIEW_SORTING_BY_FIRSTNAME = 0;
    /** The preference value for Schema View Sorting 'OID' */
    public static final int PREFS_SCHEMA_VIEW_SORTING_BY_OID = 1;
    /** The preference ID for Sorting Order */
    public static final String PREFS_SCHEMA_VIEW_SORTING_ORDER = Activator.PLUGIN_ID
        + ".preferences.SchemaView.sortingOrder"; //$NON-NLS-1$
    /** The preference value for Schema View Sorting 'ascending' */
    public static final int PREFS_SCHEMA_VIEW_SORTING_ORDER_ASCENDING = 0;
    /** The preference value for Schema View Sorting 'descending' */
    public static final int PREFS_SCHEMA_VIEW_SORTING_ORDER_DESCENDING = 1;
}
