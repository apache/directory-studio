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
    public static final String IMG_CONNECT = "resources/icons/connect.gif";
    public static final String IMG_DELETE = "resources/icons/delete.gif";
    public static final String IMG_DISCONNECT = "resources/icons/disconnect.gif";
    public static final String IMG_DIFFERENCE_ADD = "resources/icons/difference_add.png";
    public static final String IMG_DIFFERENCE_MODIFY = "resources/icons/difference_modify.png";
    public static final String IMG_DIFFERENCE_REMOVE = "resources/icons/difference_remove.png";
    public static final String IMG_FOLDER = "resources/icons/folder.gif";
    public static final String IMG_FOLDER_AT = "resources/icons/folder_at.gif";
    public static final String IMG_FOLDER_OC = "resources/icons/folder_oc.gif";
    public static final String IMG_OBJECT_CLASS = "resources/icons/object_class.gif";
    public static final String IMG_OBJECT_CLASS_NEW = "resources/icons/object_class_new.gif";
    public static final String IMG_OBJECT_CLASS_NEW_WIZARD = "resources/icons/object_class_new_wizard.png";
    public static final String IMG_PROBLEMS_ERROR = "resources/icons/problems_error.gif";
    public static final String IMG_PROBLEMS_GROUP = "resources/icons/problems_group.gif";
    public static final String IMG_PROBLEMS_WARNING = "resources/icons/problems_warning.gif";
    public static final String IMG_PROJECT_ADS = "resources/icons/project_ads.gif";
    public static final String IMG_PROJECT_ADS_CLOSED = "resources/icons/project_ads_closed.gif";
    public static final String IMG_PROJECT_EXPORT = "resources/icons/project_export.gif";
    public static final String IMG_PROJECT_EXPORT_WIZARD = "resources/icons/project_export_wizard.png";
    public static final String IMG_PROJECT_IMPORT = "resources/icons/project_import.gif";
    public static final String IMG_PROJECT_IMPORT_WIZARD = "resources/icons/project_import_wizard.png";
    public static final String IMG_PROJECT_NEW = "resources/icons/project_new.gif";
    public static final String IMG_PROJECT_NEW_WIZARD = "resources/icons/project_new_wizard.png";
    public static final String IMG_PROJECT_OFFLINE = "resources/icons/project_offline.gif";
    public static final String IMG_PROJECT_OFFLINE_CLOSED = "resources/icons/project_offline_closed.gif";
    public static final String IMG_RENAME = "resources/icons/rename.gif";
    public static final String IMG_SCHEMA = "resources/icons/schema.gif";
    public static final String IMG_SCHEMAS_EXPORT = "resources/icons/schemas_export.gif";
    public static final String IMG_SCHEMAS_EXPORT_WIZARD = "resources/icons/schemas_export_wizard.png";
    public static final String IMG_SCHEMAS_IMPORT = "resources/icons/schemas_import.gif";
    public static final String IMG_SCHEMAS_IMPORT_WIZARD = "resources/icons/schemas_import_wizard.png";
    public static final String IMG_TOOLBAR_MENU = "resources/icons/toolbar_menu.gif";

    // Commands
    public static final String CMD_CONNECT = Activator.PLUGIN_ID + ".commands.connect";
    public static final String CMD_NEW_PROJECT = Activator.PLUGIN_ID + ".commands.newProject";
    public static final String CMD_DELETE_PROJECT = Activator.PLUGIN_ID + ".commands.deleteProject";
    public static final String CMD_EXPORT_PROJECTS = Activator.PLUGIN_ID + ".commands.exportProjects";
    public static final String CMD_IMPORT_PROJECTS = Activator.PLUGIN_ID + ".commands.importProjects";
    public static final String CMD_RENAME_PROJECT = Activator.PLUGIN_ID + ".commands.renameProject";

    // Preferences
    /** The preferences ID for DifferencesWidget Grouping */
    public static final String PREFS_DIFFERENCES_WIDGET_GROUPING = Activator.PLUGIN_ID
        + ".prefs.DifferencesWidget.grouping";
    /** The preference value for DifferencesWidget Grouping 'Property' */
    public static final int PREFS_DIFFERENCES_WIDGET_GROUPING_PROPERTY = 0;
    /** The preference value for DifferencesWidget Grouping 'Property' */
    public static final int PREFS_DIFFERENCES_WIDGET_GROUPING_TYPE = 1;

}
