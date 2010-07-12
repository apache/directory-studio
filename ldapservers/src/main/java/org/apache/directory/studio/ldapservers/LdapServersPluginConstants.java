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
    public static final String IMG_SERVER = "resources/icons/server.gif";
    public static final String IMG_SERVER_STARTED = "resources/icons/server_started.gif";
    public static final String IMG_SERVER_STARTING1 = "resources/icons/server_starting1.gif";
    public static final String IMG_SERVER_STARTING2 = "resources/icons/server_starting2.gif";
    public static final String IMG_SERVER_STARTING3 = "resources/icons/server_starting3.gif";
    public static final String IMG_SERVER_STOPPED = "resources/icons/server_stopped.gif";
    public static final String IMG_SERVER_STOPPING1 = "resources/icons/server_stopping1.gif";
    public static final String IMG_SERVER_STOPPING2 = "resources/icons/server_stopping2.gif";
    public static final String IMG_SERVER_STOPPING3 = "resources/icons/server_stopping3.gif";

}
