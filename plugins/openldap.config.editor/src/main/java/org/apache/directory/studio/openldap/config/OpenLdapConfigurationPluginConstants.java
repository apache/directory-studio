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
package org.apache.directory.studio.openldap.config;


/**
 * This interface contains all the Constants used in the Plugin.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface OpenLdapConfigurationPluginConstants
{
    /** The plug-in ID */
    public static final String PLUGIN_ID = OpenLdapConfigurationPlugin.getDefault().getPluginProperties()
        .getString( "Plugin_id" ); //$NON-NLS-1$

    // ------
    // IMAGES
    // ------
    public static final String IMG_ATTRIBUTE = "resources/icons/attribute.gif"; //$NON-NLS-1$
    public static final String IMG_DATABASE = "resources/icons/database.gif"; //$NON-NLS-1$
    public static final String IMG_DISABLED_DATABASE = "resources/icons/disabledDatabase.gif"; //$NON-NLS-1$
    public static final String IMG_EDITOR = "resources/icons/editor.gif"; //$NON-NLS-1$
    public static final String IMG_EXPORT = "resources/icons/export.gif"; //$NON-NLS-1$
    public static final String IMG_INDEX = "resources/icons/index.png"; //$NON-NLS-1$
    public static final String IMG_INFORMATION = "resources/icons/information.gif"; //$NON-NLS-1$
    public static final String IMG_IMPORT = "resources/icons/import.gif"; //$NON-NLS-1$
    public static final String IMG_OVERLAY = "resources/icons/overlay.gif"; //$NON-NLS-1$
    public static final String IMG_LDAP_SERVER = "resources/icons/server.gif"; //$NON-NLS-1$
}
