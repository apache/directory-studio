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
    public static final String IMG_SCHEMA = "resources/icons/schema.gif";
    public static final String IMG_TOOLBAR_MENU = "resources/icons/toolbar_menu.gif";

    // Commands
    public static final String CMD_CONNECT = Activator.PLUGIN_ID + ".connect";

    // Preferences
    /** The preferences ID for DifferencesWidget Grouping */
    public static final String PREFS_DIFFERENCES_WIDGET_GROUPING = Activator.PLUGIN_ID
        + ".prefs.DifferencesWidget.grouping";
    /** The preference value for DifferencesWidget Grouping 'Property' */
    public static final int PREFS_DIFFERENCES_WIDGET_GROUPING_PROPERTY = 0;
    /** The preference value for DifferencesWidget Grouping 'Property' */
    public static final int PREFS_DIFFERENCES_WIDGET_GROUPING_TYPE = 1;

}
