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
package org.apache.directory.ldapstudio.proxy;


/**
 * This class contains all the constants used by the Proxy Plugin.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface ProxyConstants
{
    /** The plugin ID */
    public static final String PLUGIN_ID = "org.apache.directory.ldapstudio.proxy";
    
    /** The size of the Dialog History */
    public static final int DIALOG_HISTORY_SIZE = 20;

    /** The dialog setting key for Proxy Port History */
    public static final String DIALOGSETTING_KEY_PROXY_PORT_HISTORY = "proxyPortHistory";

    /** The dialog setting key for Server Host History */
    public static final String DIALOGSETTING_KEY_SERVER_HOST_HISTORY = "serverHostHistory";

    /** The dialog setting key for Server Port History */
    public static final String DIALOGSETTING_KEY_SERVER_PORT_HISTORY = "serverPortHistory";

    /** The dialog setting value for Use Connection */
    public static final String DIALOGSETTING_KEY_SERVER_USE_CONNECTION = "useConnection";
}
