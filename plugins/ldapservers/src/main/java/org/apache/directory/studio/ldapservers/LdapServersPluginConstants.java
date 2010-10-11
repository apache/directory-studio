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

package org.apache.directory.studio.ldapservers;




/**
 * Constants used in the LDAP Servers plugin.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface LdapServersPluginConstants
{
    /** The plug-in ID */
    public static final String PLUGIN_ID = LdapServersPlugin.getDefault().getPluginProperties().getString( "Plugin_id" ); //$NON-NLS-1$

    /** The LDAP Adapters Extension Point ID */
    public static final String LDAP_SERVER_ADAPTERS_EXTENSION_POINT = PLUGIN_ID + ".ldapServerAdapters"; //$NON-NLS-1$

    // ------
    // IMAGES
    // ------
    public static final String IMG_FOLDER = "resources/icons/folder.gif"; //$NON-NLS-1$
    public static final String IMG_SERVER_NEW = "resources/icons/server_new.gif"; //$NON-NLS-1$
    public static final String IMG_SERVER_NEW_WIZARD = "resources/icons/server_new_wizard.png"; //$NON-NLS-1$
    public static final String IMG_SERVER = "resources/icons/server.gif"; //$NON-NLS-1$
    public static final String IMG_SERVER_STARTED = "resources/icons/server_started.gif"; //$NON-NLS-1$
    public static final String IMG_SERVER_STARTING1 = "resources/icons/server_starting1.gif"; //$NON-NLS-1$
    public static final String IMG_SERVER_STARTING2 = "resources/icons/server_starting2.gif"; //$NON-NLS-1$
    public static final String IMG_SERVER_STARTING3 = "resources/icons/server_starting3.gif"; //$NON-NLS-1$
    public static final String IMG_SERVER_STOPPED = "resources/icons/server_stopped.gif"; //$NON-NLS-1$
    public static final String IMG_SERVER_STOPPING1 = "resources/icons/server_stopping1.gif"; //$NON-NLS-1$
    public static final String IMG_SERVER_STOPPING2 = "resources/icons/server_stopping2.gif"; //$NON-NLS-1$
    public static final String IMG_SERVER_STOPPING3 = "resources/icons/server_stopping3.gif"; //$NON-NLS-1$
    public static final String IMG_START = "resources/icons/start.gif"; //$NON-NLS-1$
    public static final String IMG_STOP = "resources/icons/stop.gif"; //$NON-NLS-1$

    // --------
    // COMMANDS
    // --------
    public static final String CMD_DELETE = LdapServersPlugin.getDefault().getPluginProperties()
        .getString( "Cmd_Delete_id" ); //$NON-NLS-1$
    public static final String CMD_NEW_SERVER = LdapServersPlugin.getDefault().getPluginProperties()
        .getString( "Cmd_NewServer_id" ); //$NON-NLS-1$
    public static final String CMD_OPEN_CONFIGURATION = LdapServersPlugin.getDefault().getPluginProperties()
        .getString( "Cmd_OpenConfiguration_id" ); //$NON-NLS-1$
    public static final String CMD_PROPERTIES = LdapServersPlugin.getDefault().getPluginProperties()
        .getString( "Cmd_Properties_id" ); //$NON-NLS-1$
    public static final String CMD_RENAME = LdapServersPlugin.getDefault().getPluginProperties()
        .getString( "Cmd_Rename_id" ); //$NON-NLS-1$
    public static final String CMD_START = LdapServersPlugin.getDefault().getPluginProperties()
        .getString( "Cmd_Start_id" ); //$NON-NLS-1$
    public static final String CMD_STOP = LdapServersPlugin.getDefault().getPluginProperties()
        .getString( "Cmd_Stop_id" ); //$NON-NLS-1$

    // --------------
    // PROPERTY PAGES
    // --------------
    public static final String PROP_SERVER_PROPERTY_PAGE = "org.apache.directory.studio.ldapservers.properties.ServerPropertyPage"; //$NON-NLS-1$

    // -----
    // VIEWS
    // -----
    public static final String VIEW_SERVERS_VIEW = LdapServersPlugin.getDefault().getPluginProperties()
        .getString( "View_ServersView_id" ); //$NON-NLS-1$

    // --------
    // CONTEXTS
    // --------
    public static final String CONTEXTS_SERVERS_VIEW = LdapServersPlugin.getDefault().getPluginProperties()
        .getString( "Ctx_ServersView_id" ); //$NON-NLS-1$
}
