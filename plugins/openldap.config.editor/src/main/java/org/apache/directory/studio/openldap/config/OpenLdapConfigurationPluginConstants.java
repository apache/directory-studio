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
    String PLUGIN_ID = OpenLdapConfigurationPluginConstants.class.getPackage().getName();

    // ------
    // IMAGES
    // ------
    String IMG_ATTRIBUTE = "resources/icons/attribute.png"; //$NON-NLS-1$
    String IMG_DATABASE = "resources/icons/database.png"; //$NON-NLS-1$
    String IMG_DISABLED_DATABASE = "resources/icons/disabledDatabase.png"; //$NON-NLS-1$
    String IMG_EDITOR = "resources/icons/editor.png"; //$NON-NLS-1$
    String IMG_EXPORT = "resources/icons/export.png"; //$NON-NLS-1$
    String IMG_INDEX = "resources/icons/index.png"; //$NON-NLS-1$
    String IMG_INFORMATION = "resources/icons/information.png"; //$NON-NLS-1$
    String IMG_IMPORT = "resources/icons/import.png"; //$NON-NLS-1$
    String IMG_OVERLAY = "resources/icons/overlay.png"; //$NON-NLS-1$
    String IMG_LDAP_SERVER = "resources/icons/server.png"; //$NON-NLS-1$

    public static final String WIZARD_NEW_OPENLDAP_CONFIG = OpenLdapConfigurationPlugin.getDefault().getPluginProperties()
        .getString( "NewWizards_NewOpenLdapConfigurationFileWizard_id" ); //$NON-NLS-1$
}
