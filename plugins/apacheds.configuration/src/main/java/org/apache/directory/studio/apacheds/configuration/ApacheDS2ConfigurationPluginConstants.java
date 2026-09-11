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
package org.apache.directory.studio.apacheds.configuration;


/**
 * This interface contains all the Constants used in the Plugin.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface ApacheDS2ConfigurationPluginConstants
{
    /** The plug-in ID */
    String PLUGIN_ID = ApacheDS2ConfigurationPluginConstants.class.getPackage().getName();

    // ------
    // IMAGES
    // ------
    String IMG_EDITOR = "resources/icons/editor.png"; //$NON-NLS-1$
    String IMG_EXPORT = "resources/icons/export.png"; //$NON-NLS-1$
    String IMG_INDEX = "resources/icons/index.png"; //$NON-NLS-1$
    String IMG_IMPORT = "resources/icons/import.png"; //$NON-NLS-1$
    String IMG_EXTENDED_OPERATION = "resources/icons/extended_operation.png"; //$NON-NLS-1$
    String IMG_HORIZONTAL_ORIENTATION = "resources/icons/horizontal_orientation.png"; //$NON-NLS-1$
    String IMG_INTERCEPTOR = "resources/icons/interceptor.png"; //$NON-NLS-1$
    String IMG_NEW_SERVER_CONFIGURATION_FILE_WIZARD = "resources/icons/new_server_configuration_file_wizard.png"; //$NON-NLS-1$
    String IMG_PARTITION = "resources/icons/partition.png"; //$NON-NLS-1$
    String IMG_PARTITION_SYSTEM = "resources/icons/partition_system.png"; //$NON-NLS-1$
    String IMG_PASSWORD_POLICY = "resources/icons/password_policy.png"; //$NON-NLS-1$
    String IMG_PASSWORD_POLICY_DEFAULT = "resources/icons/password_policy_default.png"; //$NON-NLS-1$
    String IMG_REPLICATION_CONSUMER = "resources/icons/replication_consumer.png"; //$NON-NLS-1$
    String IMG_VERTICAL_ORIENTATION = "resources/icons/vertical_orientation.png"; //$NON-NLS-1$

    String CONFIG_LDIF = "config.ldif"; //$NON-NLS-1$
    String OU_CONFIG = "ou=config"; //$NON-NLS-1$
    String OU_CONFIG_LDIF = "ou=config.ldif"; //$NON-NLS-1$
}
