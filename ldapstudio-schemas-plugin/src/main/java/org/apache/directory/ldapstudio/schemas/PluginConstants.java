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

package org.apache.directory.ldapstudio.schemas;


import org.apache.directory.ldapstudio.schemas.view.preferences.HierarchyViewPreferencePage;
import org.apache.directory.ldapstudio.schemas.view.preferences.SchemasViewPreferencePage;


/**
 * This interface is used to store all constants used in the Schemas Editor Plugin.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface PluginConstants
{
    // Preferences - Hierarchy View
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

    /** The preference ID for Hierarchy View Grouping */
    public static final String PREFS_HIERARCHY_VIEW_GROUPING = Activator.PLUGIN_ID
        + ".preferences.HierarchyView.grouping";

    /** The preference value for Hierarchy View Grouping 'attribute types first' */
    public static final int PREFS_HIERARCHY_VIEW_GROUPING_ATFIRST = 0;

    /** The preference value for Hierarchy View Grouping 'object classes first' */
    public static final int PREFS_HIERARCHY_VIEW_GROUPING_OCFIRST = 1;

    /** The preference value for Hierarchy View Grouping 'mixed' */
    public static final int PREFS_HIERARCHY_VIEW_GROUPING_MIXED = 2;

    /** The preference ID for Hierarchy View Sorting By */
    public static final String PREFS_HIERARCHY_VIEW_SORTING_BY = Activator.PLUGIN_ID
        + ".preferences.HierarchyView.sortingBy";

    /** The preference value for Hierarchy View Sorting 'First Name' */
    public static final int PREFS_HIERARCHY_VIEW_SORTING_BY_FIRSTNAME = 0;

    /** The prefence value for Hierarchy View Sorting 'OID' */
    public static final int PREFS_HIERARCHY_VIEW_SORTING_BY_OID = 1;

    /** The preference ID for Hierarchy View Sorting Order */
    public static final String PREFS_HIERARCHY_VIEW_SORTING_ORDER = Activator.PLUGIN_ID
        + ".preferences.HierarchyView.sortingOrder";

    /** The preference value for Hierarchy View Sorting 'ascending' */
    public static final int PREFS_HIERARCHY_VIEW_SORTING_ORDER_ASCENDING = 0;

    /** The prefence value for Hierarchy View Sorting 'descending' */
    public static final int PREFS_HIERARCHY_VIEW_SORTING_ORDER_DESCENDING = 1;

    // Preferences - Schemas View
    /** The preference ID for Schemas View Label */
    public static final String PREFS_SCHEMAS_VIEW_LABEL = SchemasViewPreferencePage.ID + ".label.labelValue";

    /** The preference value for Schemas View First Name label */
    public static final int PREFS_SCHEMAS_VIEW_LABEL_FIRST_NAME = 0;

    /** The preference value for Schemas View All Aliases label */
    public static final int PREFS_SCHEMAS_VIEW_LABEL_ALL_ALIASES = 1;

    /** The preference value for Schemas View OID label */
    public static final int PREFS_SCHEMAS_VIEW_LABEL_OID = 2;

    /** The preference ID for Schemas View Abbreviate */
    public static final String PREFS_SCHEMAS_VIEW_ABBREVIATE = SchemasViewPreferencePage.ID + ".label.abbreviate"; //$NON-NLS-1$

    /** The preference ID for Schemas View Abbreviate Max Length*/
    public static final String PREFS_SCHEMAS_VIEW_ABBREVIATE_MAX_LENGTH = SchemasViewPreferencePage.ID
        + ".label.abbreviate.maxLength"; //$NON-NLS-1$

    /** The preference ID for Schemas View Grouping */
    public static final String PREFS_SCHEMAS_VIEW_GROUPING = Activator.PLUGIN_ID + ".preferences.SchemasView.grouping"; //$NON-NLS-1$

    /** The preference value for Schemas View Grouping 'group ATs and OCs in folders' */
    public static final int PREFS_SCHEMAS_VIEW_GROUPING_FOLDERS = 0;

    /** The preference value for Schemas View Grouping 'mixed' */
    public static final int PREFS_SCHEMAS_VIEW_GROUPING_MIXED = 1;

    /** The preference ID for Schemas View Sorting By */
    public static final String PREFS_SCHEMAS_VIEW_SORTING_BY = Activator.PLUGIN_ID
        + ".preferences.SchemasView.sortingBy"; //$NON-NLS-1$

    /** The preference value for Schemas View Sorting 'First Name' */
    public static final int PREFS_SCHEMAS_VIEW_SORTING_BY_FIRSTNAME = 0;

    /** The prefence value for Schemas View Sorting 'OID' */
    public static final int PREFS_SCHEMAS_VIEW_SORTING_BY_OID = 1;

    /** The preference ID for Sorting Order */
    public static final String PREFS_SCHEMAS_VIEW_SORTING_ORDER = Activator.PLUGIN_ID
        + ".preferences.SchemasView.sortingOrder"; //$NON-NLS-1$

    /** The preference value for Schemas View Sorting 'ascending' */
    public static final int PREFS_SCHEMAS_VIEW_SORTING_ORDER_ASCENDING = 0;

    /** The prefence value for Schemas View Sorting 'descending' */
    public static final int PREFS_SCHEMAS_VIEW_SORTING_ORDER_DESCENDING = 1;

    // Preferences - Search View

    /** The preference ID for Search History of the Search View */
    public static final String PREFS_SEARCH_VIEW_SEARCH_HISTORY = Activator.PLUGIN_ID
        + ".preferences.SearchView.searchHistory";;

    // Images - Actions
    public static final String IMG_ABOUT = "ressources/icons/flag_blue.png"; //$NON-NLS-1$
    public static final String IMG_CREATE_A_NEW_ATTRIBUTETYPE = "ressources/icons/attribute_type_new.gif"; //$NON-NLS-1$
    public static final String IMG_CREATE_A_NEW_OBJECTCLASS = "ressources/icons/object_class_new.gif"; //$NON-NLS-1$
    public static final String IMG_CREATE_A_NEW_SCHEMA = "ressources/icons/schema_new.png"; //$NON-NLS-1$
    public static final String IMG_DELETE = "ressources/icons/delete.gif"; //$NON-NLS-1$
    public static final String IMG_ERASE_SEARCH = "ressources/icons/erase_search.gif"; //$NON-NLS-1$
    public static final String IMG_EXIT = "ressources/icons/stop.png"; //$NON-NLS-1$
    public static final String IMG_HIDE_OBJECT_CLASSES = "ressources/icons/hide_object_classes.png"; //$NON-NLS-1$
    public static final String IMG_HIDE_ATTRIBUTE_TYPES = "ressources/icons/hide_attribute_types.png"; //$NON-NLS-1$
    public static final String IMG_LINK_WITH_EDITOR = "ressources/icons/link_with_editor.gif"; //$NON-NLS-1$
    public static final String IMG_COLLAPSE_ALL = "ressources/icons/collapse_all.gif"; //$NON-NLS-1$
    public static final String IMG_OPEN = "ressources/icons/open.png"; //$NON-NLS-1$
    public static final String IMG_REMOVE_SCHEMA = "ressources/icons/schema_remove.png"; //$NON-NLS-1$
    public static final String IMG_SAVE = "ressources/icons/save.gif"; //$NON-NLS-1$
    public static final String IMG_SAVE_AS = "ressources/icons/save_as.png"; //$NON-NLS-1$
    public static final String IMG_SAVE_ALL = "ressources/icons/save_all.png"; //$NON-NLS-1$
    public static final String IMG_SHOW_PREFERENCES = "ressources/icons/preferences.png"; //$NON-NLS-1$
    public static final String IMG_SORT = "ressources/icons/sort.gif"; //$NON-NLS-1$

    // Images - Views
    public static final String IMG_ATTRIBUTE_TYPE = "ressources/icons/attribute_type.gif"; //$NON-NLS-1$
    public static final String IMG_ATTRIBUTE_TYPE_OVERLAY_OPERATION = "ressources/icons/attribute_type_overlay_operation.gif"; //$NON-NLS-1$
    public static final String IMG_ATTRIBUTE_TYPE_OVERLAY_USER_APPLICATION = "ressources/icons/attribute_type_overlay_userApplication.gif"; //$NON-NLS-1$
    public static final String IMG_FOLDER_ATTRIBUTE_TYPE = "ressources/icons/folder_at.gif"; //$NON-NLS-1$
    public static final String IMG_FOLDER_OBJECT_CLASS = "ressources/icons/folder_oc.gif"; //$NON-NLS-1$
    public static final String IMG_OBJECT_CLASS = "ressources/icons/object_class.gif"; //$NON-NLS-1$
    public static final String IMG_OBJECT_CLASS_OVERLAY_ABSTRACT = "ressources/icons/object_class_overlay_abstract.gif"; //$NON-NLS-1$
    public static final String IMG_OBJECT_CLASS_OVERLAY_AUXILIARY = "ressources/icons/object_class_overlay_auxiliary.gif"; //$NON-NLS-1$
    public static final String IMG_OBJECT_CLASS_OVERLAY_STRUCTURAL = "ressources/icons/object_class_overlay_structural.gif"; //$NON-NLS-1$
    public static final String IMG_OBJECT_CLASS_WARNING = "ressources/icons/object_class_warning.gif"; //$NON-NLS-1$
    public static final String IMG_SCHEMA = "ressources/icons/schema.gif"; //$NON-NLS-1$
    public static final String IMG_SCHEMA_CORE = "ressources/icons/schema_core.gif"; //$NON-NLS-1$

    // Images - Wizards
    public static final String IMG_ATTRIBUTE_TYPE_NEW_WIZARD = "ressources/icons/attribute_type_new_wizard.png"; //$NON-NLS-1$
    public static final String IMG_OBJECT_CLASS_NEW_WIZARD = "ressources/icons/object_class_new_wizard.png"; //$NON-NLS-1$
    public static final String IMG_SCHEMA_NEW_WIZARD = "ressources/icons/schema_new_wizard.png"; //$NON-NLS-1$
    
    // Action IDs
    public static final String CMD_COLLAPSE_ALL = Activator.PLUGIN_ID + ".cmd.CollapseAll"; //$NON-NLS-1$
    public static final String CMD_CREATE_A_NEW_ATTRIBUTETYPE = Activator.PLUGIN_ID + ".cmd.CreateANewAttributeAype"; //$NON-NLS-1$
    public static final String CMD_CREATE_A_NEW_OBJECTCLASS = Activator.PLUGIN_ID + ".cmd.CreateANewObjectClass"; //$NON-NLS-1$
    public static final String CMD_CREATE_A_NEW_SCHEMA = Activator.PLUGIN_ID + ".cmd.CreateANewSchema"; //$NON-NLS-1$
    public static final String CMD_DELETE = Activator.PLUGIN_ID + ".cmd.Delete"; //$NON-NLS-1$
    public static final String CMD_ERASE_SEARCH = Activator.PLUGIN_ID + ".cmd.EraseSearch"; //$NON-NLS-1$
    public static final String CMD_HIDE_ATTRIBUTE_TYPES = Activator.PLUGIN_ID + ".cmd.HideAttributesTypes"; //$NON-NLS-1$
    public static final String CMD_HIDE_OBJECT_CLASSES = Activator.PLUGIN_ID + ".cmd.HideObjectClasses"; //$NON-NLS-1$
    public static final String CMD_LINK_WITH_EDITOR_HIERARCHY_VIEW = Activator.PLUGIN_ID + ".cmd.LinkWithEditorHierarchyView"; //$NON-NLS-1$
    public static final String CMD_LINK_WITH_EDITOR_SCHEMA_VIEW = Activator.PLUGIN_ID + ".cmd.LinkWithEditorSchemasView"; //$NON-NLS-1$
    public static final String CMD_OPEN_LOCAL = Activator.PLUGIN_ID + ".cmd.OpenLocal"; //$NON-NLS-1$
    public static final String CMD_HIERARCHY_VIEW_PREFERENCES = Activator.PLUGIN_ID + ".cmd.OpenHierarchyViewPreferences"; //$NON-NLS-1$
    public static final String CMD_HIERARCHY_VIEW_SORT_DIALOG = Activator.PLUGIN_ID + ".cmd.OpenHierarchyViewSortDialog"; //$NON-NLS-1$
    public static final String CMD_OPEN_SCHEMA_SOURCE_CODE = Activator.PLUGIN_ID + ".cmd.OpenSchemaSourceSode"; //$NON-NLS-1$
    public static final String CMD_SCHEMAS_VIEW_PREFERENCES = Activator.PLUGIN_ID + ".cmd.OpenSchemasViewPreferences"; //$NON-NLS-1$
    public static final String CMD_SCHEMAS_VIEW_SORT_DIALOG = Activator.PLUGIN_ID + ".cmd.OpenSchemasViewSortDialog"; //$NON-NLS-1$
    public static final String CMD_REMOVE_SCHEMA = Activator.PLUGIN_ID + ".cmd.RemoveSchema"; //$NON-NLS-1$
    public static final String CMD_SAVE = Activator.PLUGIN_ID + ".cmd.Save"; //$NON-NLS-1$
    public static final String CMD_SAVE_AS = Activator.PLUGIN_ID + ".cmd.SaveAs"; //$NON-NLS-1$
    public static final String CMD_SAVE_ALL = Activator.PLUGIN_ID + ".cmd.SaveAll"; //$NON-NLS-1$
}
