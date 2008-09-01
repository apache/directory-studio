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

package org.apache.directory.studio.connection.ui;


/**
 * Constants used in the connection UI plugin.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface ConnectionUIConstants
{
    /** The plug-in ID */
    public static final String PLUGIN_ID = ConnectionUIPlugin.getDefault().getPluginProperties()
        .getString( "Plugin_id" );

    /** The dialog setting key used for the history of host names. */
    public static final String DIALOGSETTING_KEY_HOST_HISTORY = "hostHistory";

    /** The dialog setting key used for the history of ports. */
    public static final String DIALOGSETTING_KEY_PORT_HISTORY = "portHistory";

    /** The dialog setting key used for the history of principals (bind DNs). */
    public static final String DIALOGSETTING_KEY_PRINCIPAL_HISTORY = "principalHistory";

    /** The dialog setting key used for the history of SASL realms. */
    public static final String DIALOGSETTING_KEY_REALM_HISTORY = "saslrealmHistory";

    /** The image to add a connection. */
    public static final String IMG_CONNECTION_ADD = "resources/icons/connection_add.gif";

    /** The image used to display the connected state of connections. */
    public static final String IMG_CONNECTION_CONNECTED = "resources/icons/connection_connected.gif";

    /** The image used to display the disconnected state of connections. */
    public static final String IMG_CONNECTION_DISCONNECTED = "resources/icons/connection_disconnected.gif";

    /** The image used to display the connected state of SSL connections. */
    public static final String IMG_CONNECTION_SSL_CONNECTED = "resources/icons/connection_ssl_connected.gif";

    /** The image used to display the disconnected state of SSL connections. */
    public static final String IMG_CONNECTION_SSL_DISCONNECTED = "resources/icons/connection_ssl_disconnected.gif";

    /** The image to connect connections. */
    public static final String IMG_CONNECTION_CONNECT = "resources/icons/connection_connect.gif";

    /** The image to disconnect connections. */
    public static final String IMG_CONNECTION_DISCONNECT = "resources/icons/connection_disconnect.gif";

    /** The new connection wizard image */
    public static final String IMG_CONNECTION_WIZARD = "resources/icons/connection_wizard.gif";

    /** The pull-down image */
    public static final String IMG_PULLDOWN = "resources/icons/pulldown.gif";

    /** The image used for connection folders. */
    public static final String IMG_CONNECTION_FOLDER = "resources/icons/connection_folder.gif";

    /** The image to add a connection folder. */
    public static final String IMG_CONNECTION_FOLDER_ADD = "resources/icons/connection_folder_add.gif";

    /** The plug-in ID */
    public static final String TYPENAME = ConnectionUIPlugin.getDefault().getPluginProperties().getString(
        "ConnectionTransfert_Typename" );
    
    /** The plug-in ID */
    public static final String CMD_COPY = ConnectionUIPlugin.getDefault().getPluginProperties().getString(
        "Cmd_Copy_id" );
    
    /** The plug-in ID */
    public static final String CMD_PASTE = ConnectionUIPlugin.getDefault().getPluginProperties().getString(
        "Cmd_Paste_id" );
    
    /** The plug-in ID */
    public static final String CMD_DELETE = ConnectionUIPlugin.getDefault().getPluginProperties().getString(
        "Cmd_Delete_id" );
    
    /** The plug-in ID */
    public static final String CMD_PROPERTIES = ConnectionUIPlugin.getDefault().getPluginProperties().getString(
        "Cmd_Properties_id" );
}
