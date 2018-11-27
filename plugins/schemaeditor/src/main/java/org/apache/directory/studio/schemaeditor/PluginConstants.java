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
package org.apache.directory.studio.schemaeditor;


import org.apache.directory.studio.schemaeditor.view.preferences.HierarchyViewPreferencePage;
import org.apache.directory.studio.schemaeditor.view.preferences.SchemaViewPreferencePage;
import org.apache.directory.studio.schemaeditor.view.preferences.SearchViewPreferencePage;


/**
 * This class contains all the Constants used in the Plugin.
 * Final reference -> class shouldn't be extended
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class PluginConstants
{
    /**
     *  Ensures no construction of this class, also ensures there is no need for final keyword above
     *  (Implicit super constructor is not visible for default constructor),
     *  but is still self documenting.
     */
    private PluginConstants()
    {
    }

    /** The plug-in ID */
    public static final String PLUGIN_ID = PluginConstants.class.getPackage().getName();

    /** The Schema Editor perspective ID */
    public static final String PERSPECTIVE_SCHEMA_EDITOR_ID = Activator.getDefault().getPluginProperties()
        .getString( "Perspective_SchemaEditor_id" ); //$NON-NLS-1$

    /** The LDAP Browser perspective ID */
    public static final String PERSPECTIVE_LDAP_BROWSER_ID = 
        "org.apache.directory.studio.ldapbrowser.ui.perspective.BrowserPerspective"; //$NON-NLS-1$

    /** The top left folder ID */
    public static final String PERSPECTIVE_TOP_LEFT_FOLDER_ID = 
        "org.apache.directory.studio.schemaeditor.topleftfolder"; //$NON-NLS-1$

    /** The bottom folder ID */
    public static final String PERSPECTIVE_BOTTOM_FOLDER_ID = 
        "org.apache.directory.studio.schemaeditor.bottomfolder"; //$NON-NLS-1$

    /** The Attribute Type Editor ID */
    public static final String EDITOR_ATTRIBUTE_TYPE_ID = Activator.getDefault().getPluginProperties()
        .getString( "Editor_AttributeTypeEditor_id" ); //$NON-NLS-1$

    /** The Object Class Editor ID */
    public static final String EDITOR_OBJECT_CLASS_ID = Activator.getDefault().getPluginProperties()
        .getString( "Editor_ObjectClassEditor_id" ); //$NON-NLS-1$

    /** The Schema Editor ID */
    public static final String EDITOR_SCHEMA_ID = Activator.getDefault().getPluginProperties()
        .getString( "Editor_SchemaEditor_id" ); //$NON-NLS-1$

    /** The Hierarchy View Preference Page ID */
    public static final String PREF_PAGE_HIERARCHY_VIEW_ID = Activator.getDefault().getPluginProperties()
        .getString( "PrefPage_HierarchyView_id" ); //$NON-NLS-1$

    /** The Schema View Preference Page ID */
    public static final String PREF_PAGE_SCHEMA_VIEW_ID = Activator.getDefault().getPluginProperties()
        .getString( "PrefPage_SchemaView_id" ); //$NON-NLS-1$

    /** The Search View Preference Page ID */
    public static final String PREF_PAGE_SEARCH_VIEW_ID = Activator.getDefault().getPluginProperties()
        .getString( "PrefPage_SearchView_id" ); //$NON-NLS-1$

    /** The Hierarchy View ID */
    public static final String VIEW_HIERARCHY_VIEW_ID = Activator.getDefault().getPluginProperties()
        .getString( "View_HierarchyView_id" ); //$NON-NLS-1$

    /** The Problems View ID */
    public static final String VIEW_PROBLEMS_VIEW_ID = Activator.getDefault().getPluginProperties()
        .getString( "View_ProblemsView_id" ); //$NON-NLS-1$

    /** The Projects View ID */
    public static final String VIEW_PROJECTS_VIEW_ID = Activator.getDefault().getPluginProperties()
        .getString( "View_ProjectsView_id" ); //$NON-NLS-1$

    /** The Schema View ID */
    public static final String VIEW_SCHEMA_VIEW_ID = Activator.getDefault().getPluginProperties()
        .getString( "View_SchemaView_id" ); //$NON-NLS-1$

    /** The Search View ID */
    public static final String VIEW_SEARCH_VIEW_ID = Activator.getDefault().getPluginProperties()
        .getString( "View_SearchView_id" ); //$NON-NLS-1$

    /** The New Attribute Type Wizard ID */
    public static final String NEW_WIZARD_NEW_ATTRIBUTE_TYPE_WIZARD = Activator.getDefault().getPluginProperties()
        .getString( "NewWizard_NewAttributeTypeWizard_id" ); //$NON-NLS-1$

    /** The New Object Class Wizard ID */
    public static final String NEW_WIZARD_NEW_OBJECT_CLASS_WIZARD = Activator.getDefault().getPluginProperties()
        .getString( "NewWizard_NewObjectClassWizard_id" ); //$NON-NLS-1$

    /** The New Project Wizard ID */
    public static final String NEW_WIZARD_NEW_PROJECT_WIZARD = Activator.getDefault().getPluginProperties()
        .getString( "NewWizard_NewProjectWizard_id" ); //$NON-NLS-1$

    /** The New Schema Wizard ID */
    public static final String NEW_WIZARD_NEW_SCHEMA_WIZARD = Activator.getDefault().getPluginProperties()
        .getString( "NewWizard_NewSchemaWizard_id" ); //$NON-NLS-1$

    // Images
    public static final String IMG_ATTRIBUTE_TYPE = "resources/icons/attribute_type.gif"; //$NON-NLS-1$
    public static final String IMG_ATTRIBUTE_TYPE_HIERARCHY_SELECTED = "resources/icons/attribute_type_hierarchy_selected.gif"; //$NON-NLS-1$
    public static final String IMG_ATTRIBUTE_TYPE_NEW = "resources/icons/attribute_type_new.gif"; //$NON-NLS-1$
    public static final String IMG_ATTRIBUTE_TYPE_NEW_WIZARD = "resources/icons/attribute_type_new_wizard.png"; //$NON-NLS-1$
    public static final String IMG_ATTRIBUTE_TYPE_OVERLAY_OPERATION = "resources/icons/attribute_type_overlay_operation.gif"; //$NON-NLS-1$
    public static final String IMG_ATTRIBUTE_TYPE_OVERLAY_USER_APPLICATION = "resources/icons/attribute_type_overlay_userApplication.gif"; //$NON-NLS-1$
    public static final String IMG_CONNECT = "resources/icons/connect.gif"; //$NON-NLS-1$
    public static final String IMG_COMMIT_CHANGES = "resources/icons/commit_changes.gif"; //$NON-NLS-1$
    public static final String IMG_COMMIT_CHANGES_WIZARD = "resources/icons/commit_changes_wizard.png"; //$NON-NLS-1$
    public static final String IMG_COLLAPSE_ALL = "resources/icons/collapse_all.gif"; //$NON-NLS-1$
    public static final String IMG_DELETE = "resources/icons/delete.gif"; //$NON-NLS-1$
    public static final String IMG_DISCONNECT = "resources/icons/disconnect.gif"; //$NON-NLS-1$
    public static final String IMG_DIFFERENCE_ATTRIBUTE_TYPE_ADD = "resources/icons/difference_attribute_type_add.gif"; //$NON-NLS-1$
    public static final String IMG_DIFFERENCE_ATTRIBUTE_TYPE_MODIFY = "resources/icons/difference_attribute_type_modify.gif"; //$NON-NLS-1$
    public static final String IMG_DIFFERENCE_ATTRIBUTE_TYPE_REMOVE = "resources/icons/difference_attribute_type_remove.gif"; //$NON-NLS-1$
    public static final String IMG_DIFFERENCE_OBJECT_CLASS_ADD = "resources/icons/difference_object_class_add.gif"; //$NON-NLS-1$
    public static final String IMG_DIFFERENCE_OBJECT_CLASS_MODIFY = "resources/icons/difference_object_class_modify.gif"; //$NON-NLS-1$
    public static final String IMG_DIFFERENCE_OBJECT_CLASS_REMOVE = "resources/icons/difference_object_class_remove.gif"; //$NON-NLS-1$
    public static final String IMG_DIFFERENCE_PROPERTY_ADD = "resources/icons/difference_property_add.gif"; //$NON-NLS-1$
    public static final String IMG_DIFFERENCE_PROPERTY_MODIFY = "resources/icons/difference_property_modify.gif"; //$NON-NLS-1$
    public static final String IMG_DIFFERENCE_PROPERTY_REMOVE = "resources/icons/difference_property_remove.gif"; //$NON-NLS-1$
    public static final String IMG_DIFFERENCE_SCHEMA_ADD = "resources/icons/difference_schema_add.gif"; //$NON-NLS-1$
    public static final String IMG_DIFFERENCE_SCHEMA_MODIFY = "resources/icons/difference_schema_modify.gif"; //$NON-NLS-1$
    public static final String IMG_DIFFERENCE_SCHEMA_REMOVE = "resources/icons/difference_schema_remove.gif"; //$NON-NLS-1$
    public static final String IMG_FOLDER = "resources/icons/folder.gif"; //$NON-NLS-1$
    public static final String IMG_FOLDER_AT = "resources/icons/folder_at.gif"; //$NON-NLS-1$
    public static final String IMG_FOLDER_OC = "resources/icons/folder_oc.gif"; //$NON-NLS-1$
    public static final String IMG_LINK_WITH_EDITOR = "resources/icons/link_with_editor.gif"; //$NON-NLS-1$
    public static final String IMG_OBJECT_CLASS = "resources/icons/object_class.gif"; //$NON-NLS-1$
    public static final String IMG_OBJECT_CLASS_HIERARCHY_SELECTED = "resources/icons/object_class_hierarchy_selected.gif"; //$NON-NLS-1$
    public static final String IMG_OBJECT_CLASS_NEW = "resources/icons/object_class_new.gif"; //$NON-NLS-1$
    public static final String IMG_OBJECT_CLASS_NEW_WIZARD = "resources/icons/object_class_new_wizard.png"; //$NON-NLS-1$
    public static final String IMG_OBJECT_CLASS_OVERLAY_ABSTRACT = "resources/icons/object_class_overlay_abstract.gif"; //$NON-NLS-1$
    public static final String IMG_OBJECT_CLASS_OVERLAY_AUXILIARY = "resources/icons/object_class_overlay_auxiliary.gif"; //$NON-NLS-1$
    public static final String IMG_OBJECT_CLASS_OVERLAY_STRUCTURAL = "resources/icons/object_class_overlay_structural.gif"; //$NON-NLS-1$
    public static final String IMG_OVERLAY_ERROR = "resources/icons/overlay_error.gif"; //$NON-NLS-1$
    public static final String IMG_OVERLAY_WARNING = "resources/icons/overlay_warning.gif"; //$NON-NLS-1$
    public static final String IMG_PROBLEMS_ERROR = "resources/icons/problems_error.gif"; //$NON-NLS-1$
    public static final String IMG_PROBLEMS_GROUP = "resources/icons/problems_group.gif"; //$NON-NLS-1$
    public static final String IMG_PROBLEMS_WARNING = "resources/icons/problems_warning.gif"; //$NON-NLS-1$
    public static final String IMG_PROJECT_EXPORT = "resources/icons/project_export.gif"; //$NON-NLS-1$
    public static final String IMG_PROJECT_EXPORT_WIZARD = "resources/icons/project_export_wizard.png"; //$NON-NLS-1$
    public static final String IMG_PROJECT_FILE = "resources/icons/project_file.gif"; //$NON-NLS-1$
    public static final String IMG_PROJECT_IMPORT = "resources/icons/project_import.gif"; //$NON-NLS-1$
    public static final String IMG_PROJECT_IMPORT_WIZARD = "resources/icons/project_import_wizard.png"; //$NON-NLS-1$
    public static final String IMG_PROJECT_NEW = "resources/icons/project_new.gif"; //$NON-NLS-1$
    public static final String IMG_PROJECT_NEW_WIZARD = "resources/icons/project_new_wizard.png"; //$NON-NLS-1$
    public static final String IMG_PROJECT_OFFLINE = "resources/icons/project_offline.gif"; //$NON-NLS-1$
    public static final String IMG_PROJECT_OFFLINE_CLOSED = "resources/icons/project_offline_closed.gif"; //$NON-NLS-1$
    public static final String IMG_PROJECT_ONLINE = "resources/icons/project_online.gif"; //$NON-NLS-1$
    public static final String IMG_PROJECT_ONLINE_CLOSED = "resources/icons/project_online_closed.gif"; //$NON-NLS-1$
    public static final String IMG_RENAME = "resources/icons/rename.gif"; //$NON-NLS-1$
    public static final String IMG_RUN_CURRENT_SEARCH_AGAIN = "resources/icons/run_current_search_again.gif"; //$NON-NLS-1$
    public static final String IMG_SCHEMA = "resources/icons/schema.gif"; //$NON-NLS-1$
    public static final String IMG_SCHEMA_CONNECTOR = "resources/icons/schema_connector.gif"; //$NON-NLS-1$
    public static final String IMG_SCHEMA_NEW = "resources/icons/schema_new.gif"; //$NON-NLS-1$
    public static final String IMG_SCHEMA_NEW_WIZARD = "resources/icons/schema_new_wizard.png"; //$NON-NLS-1$
    public static final String IMG_SCHEMAS_EXPORT = "resources/icons/schemas_export.gif"; //$NON-NLS-1$
    public static final String IMG_SCHEMAS_EXPORT_FOR_ADS = "resources/icons/schemas_export_for_ads.gif"; //$NON-NLS-1$
    public static final String IMG_SCHEMAS_EXPORT_FOR_ADS_WIZARD = "resources/icons/schemas_export_for_ads_wizard.png"; //$NON-NLS-1$
    public static final String IMG_SCHEMAS_EXPORT_WIZARD = "resources/icons/schemas_export_wizard.png"; //$NON-NLS-1$
    public static final String IMG_SCHEMAS_IMPORT = "resources/icons/schemas_import.gif"; //$NON-NLS-1$
    public static final String IMG_SCHEMAS_IMPORT_WIZARD = "resources/icons/schemas_import_wizard.png"; //$NON-NLS-1$
    public static final String IMG_SEARCH = "resources/icons/search.gif"; //$NON-NLS-1$
    public static final String IMG_SEARCH_HISTORY_ITEM = "resources/icons/search_history_item.gif"; //$NON-NLS-1$
    public static final String IMG_SHOW_SEARCH_FIELD = "resources/icons/show_search_field.gif"; //$NON-NLS-1$
    public static final String IMG_SHOW_SEARCH_HISTORY = "resources/icons/show_search_history.gif"; //$NON-NLS-1$
    public static final String IMG_SHOW_SUBTYPE_HIERARCHY = "resources/icons/hierarchy_subtype.gif"; //$NON-NLS-1$
    public static final String IMG_SHOW_SUPERTYPE_HIERARCHY = "resources/icons/hierarchy_supertype.gif"; //$NON-NLS-1$
    public static final String IMG_SHOW_TYPE_HIERARCHY = "resources/icons/hierarchy_type.gif"; //$NON-NLS-1$
    public static final String IMG_SORTING = "resources/icons/sorting.gif"; //$NON-NLS-1$
    public static final String IMG_TOOLBAR_MENU = "resources/icons/toolbar_menu.gif"; //$NON-NLS-1$
    public static final String IMG_TRANSPARENT_16X16 = "resources/icons/transparent_16x16.gif"; //$NON-NLS-1$
    public static final String IMG_WARNING_32X32 = "resources/icons/warning_32x32.png"; //$NON-NLS-1$

    // Commands
    public static final String CMD_DELETE_PROJECT = Activator.getDefault().getPluginProperties()
        .getString( "Cmd_DeleteProject_id" ); //$NON-NLS-1$
    public static final String CMD_DELETE_SCHEMA_ELEMENT = Activator.getDefault().getPluginProperties()
        .getString( "Cmd_DeleteSchemaElement_id" ); //$NON-NLS-1$
    public static final String CMD_OPEN_ELEMENT = Activator.getDefault().getPluginProperties()
        .getString( "Cmd_OpenElement_id" ); //$NON-NLS-1$
    public static final String CMD_OPEN_TYPE_HIERARCHY = Activator.getDefault().getPluginProperties()
        .getString( "Cmd_OpenTypeHierarchy_id" ); //$NON-NLS-1$
    public static final String CMD_NEW_ATTRIBUTE_TYPE = Activator.getDefault().getPluginProperties()
        .getString( "Cmd_NewAttributeType_id" ); //$NON-NLS-1$
    public static final String CMD_NEW_OBJECT_CLASS = Activator.getDefault().getPluginProperties()
        .getString( "Cmd_NewObjectClass_id" ); //$NON-NLS-1$
    public static final String CMD_NEW_PROJECT = Activator.getDefault().getPluginProperties()
        .getString( "Cmd_NewProject_id" ); //$NON-NLS-1$
    public static final String CMD_NEW_SCHEMA = Activator.getDefault().getPluginProperties()
        .getString( "Cmd_NewSchema_id" ); //$NON-NLS-1$
    public static final String CMD_RENAME_PROJECT = Activator.getDefault().getPluginProperties()
        .getString( "Cmd_RenameProject_id" ); //$NON-NLS-1$
    public static final String CMD_RENAME_SCHEMA_ELEMENT = Activator.getDefault().getPluginProperties()
        .getString( "Cmd_RenameSchemaElement_id" ); //$NON-NLS-1$

    // Preferences - DifferencesWidget
    /** The preferences ID for DifferencesWidget Grouping */
    public static final String PREFS_DIFFERENCES_WIDGET_GROUPING = PluginConstants.PLUGIN_ID
        + ".prefs.DifferencesWidget.grouping"; //$NON-NLS-1$
    /** The preference value for DifferencesWidget Grouping 'Property' */
    public static final int PREFS_DIFFERENCES_WIDGET_GROUPING_PROPERTY = 0;
    /** The preference value for DifferencesWidget Grouping 'Property' */
    public static final int PREFS_DIFFERENCES_WIDGET_GROUPING_TYPE = 1;

    // Preferences - SchemaView
    /** The preference ID for Schema View Schema Presentation */
    public static final String PREFS_SCHEMA_VIEW_SCHEMA_PRESENTATION = SchemaViewPreferencePage.ID
        + ".schemaPresentation"; //$NON-NLS-1$
    /** The preference value for Schema View Schema Presentation 'Flat' */
    public static final int PREFS_SCHEMA_VIEW_SCHEMA_PRESENTATION_FLAT = 0;
    /** The preference value for Schema View Schema Presentation 'Hierarchical' */
    public static final int PREFS_SCHEMA_VIEW_SCHEMA_PRESENTATION_HIERARCHICAL = 1;
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
    public static final String PREFS_SCHEMA_VIEW_SCHEMA_LABEL_DISPLAY = SchemaViewPreferencePage.ID
        + ".schemaLabel.display"; //$NON-NLS-1$
    /** The preference ID for Schema View Grouping */
    public static final String PREFS_SCHEMA_VIEW_GROUPING = PluginConstants.PLUGIN_ID
        + ".preferences.SchemaView.grouping"; //$NON-NLS-1$
    /** The preference value for Schema View Grouping 'group ATs and OCs in folders' */
    public static final int PREFS_SCHEMA_VIEW_GROUPING_FOLDERS = 0;
    /** The preference value for Schema View Grouping 'mixed' */
    public static final int PREFS_SCHEMA_VIEW_GROUPING_MIXED = 1;
    /** The preference ID for Schema View Sorting By */
    public static final String PREFS_SCHEMA_VIEW_SORTING_BY = PluginConstants.PLUGIN_ID
        + ".preferences.SchemaView.sortingBy"; //$NON-NLS-1$
    /** The preference value for Schema View Sorting 'First Name' */
    public static final int PREFS_SCHEMA_VIEW_SORTING_BY_FIRSTNAME = 0;
    /** The preference value for Schema View Sorting 'OID' */
    public static final int PREFS_SCHEMA_VIEW_SORTING_BY_OID = 1;
    /** The preference ID for Sorting Order */
    public static final String PREFS_SCHEMA_VIEW_SORTING_ORDER = PluginConstants.PLUGIN_ID
        + ".preferences.SchemaView.sortingOrder"; //$NON-NLS-1$
    /** The preference value for Schema View Sorting 'ascending' */
    public static final int PREFS_SCHEMA_VIEW_SORTING_ORDER_ASCENDING = 0;
    /** The preference value for Schema View Sorting 'descending' */
    public static final int PREFS_SCHEMA_VIEW_SORTING_ORDER_DESCENDING = 1;

    // Preferences - Hierarchy View
    /** The preference ID for Mode of the Hierarchy View */
    public static final String PREFS_HIERARCHY_VIEW_MODE = PluginConstants.PLUGIN_ID
        + ".preferences.HierarchyView.mode"; //$NON-NLS-1$
    /** The preference value for Hierarchy View Mode 'Supertype' */
    public static final int PREFS_HIERARCHY_VIEW_MODE_SUPERTYPE = 0;
    /** The preference value for Hierarchy View Mode 'Subtype' */
    public static final int PREFS_HIERARCHY_VIEW_MODE_SUBTYPE = 1;
    /** The preference value for Hierarchy View Mode 'Type' */
    public static final int PREFS_HIERARCHY_VIEW_MODE_TYPE = 2;
    /** The preference ID for Hierarchy View Label */
    public static final String PREFS_HIERARCHY_VIEW_LABEL = HierarchyViewPreferencePage.ID + ".label.labelValue"; //$NON-NLS-1$
    /** The preference value for Hierarchy View First Name label */
    public static final int PREFS_HIERARCHY_VIEW_LABEL_FIRST_NAME = 0;
    /** The preference value for Hierarchy View All Aliases label */
    public static final int PREFS_HIERARCHY_VIEW_LABEL_ALL_ALIASES = 1;
    /** The preference value for Hierarchy View OID label */
    public static final int PREFS_HIERARCHY_VIEW_LABEL_OID = 2;
    /** The preference ID for Hierarchy View Abbreviate */
    public static final String PREFS_HIERARCHY_VIEW_ABBREVIATE = HierarchyViewPreferencePage.ID + ".label.abbreviate"; //$NON-NLS-1$
    /** The preference ID for Hierarchy View Abbreviate Max Length*/
    public static final String PREFS_HIERARCHY_VIEW_ABBREVIATE_MAX_LENGTH = HierarchyViewPreferencePage.ID
        + ".label.abbreviate.maxLength"; //$NON-NLS-1$
    /** The preference ID for Hierarchy View Display Secondary Label */
    public static final String PREFS_HIERARCHY_VIEW_SECONDARY_LABEL_DISPLAY = HierarchyViewPreferencePage.ID
        + ".secondaryLabel.display"; //$NON-NLS-1$
    /** The preference ID for Hierarchy View Secondary Label */
    public static final String PREFS_HIERARCHY_VIEW_SECONDARY_LABEL = HierarchyViewPreferencePage.ID
        + ".secondaryLabel.labelValue"; //$NON-NLS-1$
    /** The preference ID for Hierarchy View Abbreviate Secondary Label */
    public static final String PREFS_HIERARCHY_VIEW_SECONDARY_LABEL_ABBREVIATE = HierarchyViewPreferencePage.ID
        + ".secondaryLabel.abbreviate"; //$NON-NLS-1$
    /** The preference ID for Hierarchy View Abbreviate Secondary Label Max Length*/
    public static final String PREFS_HIERARCHY_VIEW_SECONDARY_LABEL_ABBREVIATE_MAX_LENGTH = HierarchyViewPreferencePage.ID
        + ".secondaryLabel.abbreviate.maxLength"; //$NON-NLS-1$

    // Search - SearchPage
    /** The preference ID for Search History of the SearchPage */
    public static final String PREFS_SEARCH_PAGE_SEARCH_HISTORY = PluginConstants.PLUGIN_ID
        + ".preferences.SearchPage.searchHistory"; //$NON-NLS-1$
    /** The preference ID for Search In 'Aliases' of the SearchPage */
    public static final String PREFS_SEARCH_PAGE_SEARCH_IN_ALIASES = PluginConstants.PLUGIN_ID
        + ".preferences.SearchPage.scopeAliases"; //$NON-NLS-1$
    /** The preference ID for Search In 'OID' of the SearchPage */
    public static final String PREFS_SEARCH_PAGE_SEARCH_IN_OID = PluginConstants.PLUGIN_ID
        + ".preferences.SearchPage.scopeOid"; //$NON-NLS-1$
    /** The preference ID for Search In 'Description' of the SearchPage */
    public static final String PREFS_SEARCH_PAGE_SEARCH_IN_DESCRIPTION = PluginConstants.PLUGIN_ID
        + ".preferences.SearchPage.scopeDescription"; //$NON-NLS-1$
    /** The preference ID for Search In 'Superior' of the SearchPage */
    public static final String PREFS_SEARCH_PAGE_SEARCH_IN_SUPERIOR = PluginConstants.PLUGIN_ID
        + ".preferences.SearchPage.scopeSuperior"; //$NON-NLS-1$
    /** The preference ID for Search In 'Syntax' of the SearchPage */
    public static final String PREFS_SEARCH_PAGE_SEARCH_IN_SYNTAX = PluginConstants.PLUGIN_ID
        + ".preferences.SearchPage.scopeSyntax"; //$NON-NLS-1$
    /** The preference ID for Search In 'Matching Rules' of the SearchPage */
    public static final String PREFS_SEARCH_PAGE_SEARCH_IN_MATCHING_RULES = PluginConstants.PLUGIN_ID
        + ".preferences.SearchPage.scopeMatchingRules"; //$NON-NLS-1$
    /** The preference ID for Search In 'Superiors' of the SearchPage */
    public static final String PREFS_SEARCH_PAGE_SEARCH_IN_SUPERIORS = PluginConstants.PLUGIN_ID
        + ".preferences.SearchPage.scopeSuperiors"; //$NON-NLS-1$
    /** The preference ID for Search In 'Mandatory Attributes' of the SearchPage */
    public static final String PREFS_SEARCH_PAGE_SEARCH_IN_MANDATORY_ATTRIBUTES = PluginConstants.PLUGIN_ID
        + ".preferences.SearchPage.scopeMandatoryAttributes"; //$NON-NLS-1$
    /** The preference ID for Search In 'Optional Attributes' of the SearchPage */
    public static final String PREFS_SEARCH_PAGE_SEARCH_IN_OPTIONAL_ATTRIBUTES = PluginConstants.PLUGIN_ID
        + ".preferences.SearchPage.scopeOptionalAttributes"; //$NON-NLS-1$
    /** The preference ID for Scope of the SearchPage */
    public static final String PREFS_SEARCH_PAGE_SCOPE = PluginConstants.PLUGIN_ID + ".preferences.SearchPage.scope"; //$NON-NLS-1$
    /** The preference value for Scope Attribute Types And Object Classes of the SearchPage */
    public static final int PREFS_SEARCH_PAGE_SCOPE_AT_AND_OC = 0;
    /** The preference value for Scope Attribute Types only of the SearchPage */
    public static final int PREFS_SEARCH_PAGE_SCOPE_AT_ONLY = 1;
    /** The preference value for Scope Object Classes only of the SearchPage */
    public static final int PREFS_SEARCH_PAGE_SCOPE_OC_ONLY = 2;

    // Preferences - SearchView
    /** The preference ID for Search View Label */
    public static final String PREFS_SEARCH_VIEW_LABEL = SearchViewPreferencePage.ID + ".label.labelValue"; //$NON-NLS-1$
    /** The preference value for Search View First Name label */
    public static final int PREFS_SEARCH_VIEW_LABEL_FIRST_NAME = 0;
    /** The preference value for Search View All Aliases label */
    public static final int PREFS_SEARCH_VIEW_LABEL_ALL_ALIASES = 1;
    /** The preference value for Search View OID label */
    public static final int PREFS_SEARCH_VIEW_LABEL_OID = 2;
    /** The preference ID for Search View Abbreviate */
    public static final String PREFS_SEARCH_VIEW_ABBREVIATE = SearchViewPreferencePage.ID + ".label.abbreviate"; //$NON-NLS-1$
    /** The preference ID for Search View Abbreviate Max Length*/
    public static final String PREFS_SEARCH_VIEW_ABBREVIATE_MAX_LENGTH = SearchViewPreferencePage.ID
        + ".label.abbreviate.maxLength"; //$NON-NLS-1$
    /** The preference ID for Search View Display Secondary Label */
    public static final String PREFS_SEARCH_VIEW_SECONDARY_LABEL_DISPLAY = SearchViewPreferencePage.ID
        + ".secondaryLabel.display"; //$NON-NLS-1$
    /** The preference ID for Search View Secondary Label */
    public static final String PREFS_SEARCH_VIEW_SECONDARY_LABEL = SearchViewPreferencePage.ID
        + ".secondaryLabel.labelValue"; //$NON-NLS-1$
    /** The preference ID for Search View Abbreviate Secondary Label */
    public static final String PREFS_SEARCH_VIEW_SECONDARY_LABEL_ABBREVIATE = SearchViewPreferencePage.ID
        + ".secondaryLabel.abbreviate"; //$NON-NLS-1$
    /** The preference ID for Search View Abbreviate Secondary Label Max Length*/
    public static final String PREFS_SEARCH_VIEW_SECONDARY_LABEL_ABBREVIATE_MAX_LENGTH = SearchViewPreferencePage.ID
        + ".secondaryLabel.abbreviate.maxLength"; //$NON-NLS-1$
    /** The preference ID for Search View Grouping */
    public static final String PREFS_SEARCH_VIEW_GROUPING = PluginConstants.PLUGIN_ID
        + ".preferences.SearchView.grouping"; //$NON-NLS-1$
    /** The preference value for Search View Grouping 'Display ATs first' */
    public static final int PREFS_SEARCH_VIEW_GROUPING_ATTRIBUTE_TYPES_FIRST = 0;
    /** The preference value for Search View Grouping 'Display OCs first' */
    public static final int PREFS_SEARCH_VIEW_GROUPING_OBJECT_CLASSES_FIRST = 1;
    /** The preference value for Search View Grouping 'mixed' */
    public static final int PREFS_SEARCH_VIEW_GROUPING_MIXED = 2;
    /** The preference ID for Search View Sorting By */
    public static final String PREFS_SEARCH_VIEW_SORTING_BY = PluginConstants.PLUGIN_ID
        + ".preferences.SearchView.sortingBy"; //$NON-NLS-1$
    /** The preference value for Search View Sorting 'First Name' */
    public static final int PREFS_SEARCH_VIEW_SORTING_BY_FIRSTNAME = 0;
    /** The preference value for Search View Sorting 'OID' */
    public static final int PREFS_SEARCH_VIEW_SORTING_BY_OID = 1;
    /** The preference ID for Sorting Order */
    public static final String PREFS_SEARCH_VIEW_SORTING_ORDER = PluginConstants.PLUGIN_ID
        + ".preferences.SchemaView.sortingOrder"; //$NON-NLS-1$
    /** The preference value for Search View Sorting 'ascending' */
    public static final int PREFS_SEARCH_VIEW_SORTING_ORDER_ASCENDING = 0;
    /** The preference value for Search View Sorting 'descending' */
    public static final int PREFS_SEARCH_VIEW_SORTING_ORDER_DESCENDING = 1;
    /** The preference ID for Search View Display Secondary Label */
    public static final String PREFS_SEARCH_VIEW_SCHEMA_LABEL_DISPLAY = SearchViewPreferencePage.ID
        + ".schemaLabel.display"; //$NON-NLS-1$

    // Contexts
    /** The Context for the SchemaView */
    public static final String CONTEXT_SCHEMA_VIEW = PluginConstants.PLUGIN_ID + ".contexts.schemaView"; //$NON-NLS-1$
    /** The Context for the SchemaView */
    public static final String CONTEXT_PROJECTS_VIEW = PluginConstants.PLUGIN_ID + ".contexts.projectsView"; //$NON-NLS-1$

    // File Dialogs
    /** The File Dialog path for 'Export Schema Projects' */
    public static final String FILE_DIALOG_EXPORT_PROJECTS = PluginConstants.PLUGIN_ID + ".fileDialog.exportProjects"; //$NON-NLS-1$
    /** The File Dialog path for 'Export Schemas to OpenLDAP files' */
    public static final String FILE_DIALOG_EXPORT_SCHEMAS_OPENLDAP = PluginConstants.PLUGIN_ID
        + ".fileDialog.exportSchemasOpenLDAP"; //$NON-NLS-1$
    /** The File Dialog path for 'Export Schemas to XML files' */
    public static final String FILE_DIALOG_EXPORT_SCHEMAS_XML = PluginConstants.PLUGIN_ID
        + ".fileDialog.exportSchemasXML"; //$NON-NLS-1$
    /** The File Dialog path for 'Export Schemas for ApacheDS' */
    public static final String FILE_DIALOG_EXPORT_SCHEMAS_APACHE_DS = PluginConstants.PLUGIN_ID
        + ".fileDialog.exportSchemasApacheDS"; //$NON-NLS-1$
    /** The File Dialog path for 'Import Schema Projects' */
    public static final String FILE_DIALOG_IMPORT_PROJECTS = PluginConstants.PLUGIN_ID + ".fileDialog.importProjects"; //$NON-NLS-1$
    /** The File Dialog path for 'Import Schemas from OpenLDAP files' */
    public static final String FILE_DIALOG_IMPORT_SCHEMAS_OPENLDAP = PluginConstants.PLUGIN_ID
        + ".fileDialog.importSchemasOpenLDAP"; //$NON-NLS-1$
    /** The File Dialog path for 'Import Schemas from XML files' */
    public static final String FILE_DIALOG_IMPORT_SCHEMAS_XML = PluginConstants.PLUGIN_ID
        + ".fileDialog.importSchemasXML"; //$NON-NLS-1$

    // Dialog Settings
    public static final String DIALOG_SETTINGS_OID_HISTORY = PluginConstants.PLUGIN_ID + ".dialogSettings.oidHistory"; //$NON-NLS-1$
}
