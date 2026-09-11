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
 * Final reference -> class shouldn't be extended
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class ConnectionUIConstants
{
    /** The plug-in ID */
    public static final String PLUGIN_ID = ConnectionUIConstants.class.getPackage().getName();

    /** The dialog setting key used for the history of host names. */
    public static final String DIALOGSETTING_KEY_HOST_HISTORY = "hostHistory"; //$NON-NLS-1$

    /** The dialog setting key used for the history of ports. */
    public static final String DIALOGSETTING_KEY_PORT_HISTORY = "portHistory"; //$NON-NLS-1$

    /** The dialog setting key used for the history of principals (bind DNs). */
    public static final String DIALOGSETTING_KEY_PRINCIPAL_HISTORY = "principalHistory"; //$NON-NLS-1$

    /** The dialog setting key used for the history of SASL realms. */
    public static final String DIALOGSETTING_KEY_REALM_HISTORY = "saslrealmHistory"; //$NON-NLS-1$

    /** The certificate image */
    public static final String IMG_CERTIFICATE = "resources/icons/certificate.png"; //$NON-NLS-1$

    /** The image to add a connection. */
    public static final String IMG_CONNECTION_ADD = "resources/icons/connection_add.png"; //$NON-NLS-1$

    /** The image used to display the connected state of connections. */
    public static final String IMG_CONNECTION_CONNECTED = "resources/icons/connection_connected.png"; //$NON-NLS-1$

    /** The image used to display the disconnected state of connections. */
    public static final String IMG_CONNECTION_DISCONNECTED = "resources/icons/connection_disconnected.png"; //$NON-NLS-1$

    /** The image used to display the connected state of SSL connections. */
    public static final String IMG_CONNECTION_SSL_CONNECTED = "resources/icons/connection_ssl_connected.png"; //$NON-NLS-1$

    /** The image used to display the disconnected state of SSL connections. */
    public static final String IMG_CONNECTION_SSL_DISCONNECTED = "resources/icons/connection_ssl_disconnected.png"; //$NON-NLS-1$

    /** The image to connect connections. */
    public static final String IMG_CONNECTION_CONNECT = "resources/icons/connection_connect.png"; //$NON-NLS-1$

    /** The image to disconnect connections. */
    public static final String IMG_CONNECTION_DISCONNECT = "resources/icons/connection_disconnect.png"; //$NON-NLS-1$

    /** The new connection wizard image */
    public static final String IMG_CONNECTION_WIZARD = "resources/icons/connection_wizard.png"; //$NON-NLS-1$

    /** The image used for connection folders. */
    public static final String IMG_CONNECTION_FOLDER = "resources/icons/connection_folder.png"; //$NON-NLS-1$

    /** The image to add a connection folder. */
    public static final String IMG_CONNECTION_FOLDER_ADD = "resources/icons/connection_folder_add.png"; //$NON-NLS-1$

    /** The expand all image */
    public static final String IMG_EXPANDALL = "resources/icons/expandall.png"; //$NON-NLS-1$

    /** The collapse all image */
    public static final String IMG_COLLAPSEALL = "resources/icons/collapseall.png"; //$NON-NLS-1$

    /** The export certificate wizard image */
    public static final String IMG_CERTIFICATE_EXPORT_WIZARD = "resources/icons/certificate_export_wizard.png"; //$NON-NLS-1$

    /** The connection transfer typename */
    public static final String TYPENAME = "org.apache.directory.studio.ldapbrowser.connection"; //$NON-NLS-1$

    /** The COPY command */
    public static final String CMD_COPY = "org.apache.directory.studio.ldapbrowser.action.copy"; //$NON-NLS-1$

    /** The PASTE command */
    public static final String CMD_PASTE = "org.apache.directory.studio.ldapbrowser.action.paste"; //$NON-NLS-1$

    /** The DELETE command */
    public static final String CMD_DELETE = "org.apache.directory.studio.ldapbrowser.action.delete"; //$NON-NLS-1$

    /** The PROPERTIES command */
    public static final String CMD_PROPERTIES = "org.apache.directory.studio.ldapbrowser.action.properties"; //$NON-NLS-1$

    /** The new wizard/new connection connection */
    public static final String NEW_WIZARD_NEW_CONNECTION = ConnectionUIPlugin.getDefault().getPluginProperties()
        .getString( "NewWizards_NewConnectionWizard_id" ); //$NON-NLS-1$


    /**
     *  Ensures no construction of this class, also ensures there is no need for final keyword above
     *  (Implicit super constructor is not visible for default constructor),
     *  but is still self documenting.
     */
    private ConnectionUIConstants()
    {
    }
}
