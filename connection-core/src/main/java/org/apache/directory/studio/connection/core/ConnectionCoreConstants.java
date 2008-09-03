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
package org.apache.directory.studio.connection.core;


/**
 * Constants for the connection core plugin.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface ConnectionCoreConstants
{
    /** The plug-in ID */
    public static final String PLUGIN_ID = ConnectionCorePlugin.getDefault().getPluginProperties().getString(
        "Plugin_id" );

    /** The line separator. */
    public static final String LINE_SEPARATOR = System.getProperty( "line.separator" ); //$NON-NLS-1$

    /** The date format of the modification logger */
    public static final String DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS"; //$NON-NLS-1$

    /** The constant used to preferred LDAP context factory */
    public static final String PREFERENCE_LDAP_CONTEXT_FACTORY = "ldapContextFactory";

    /** The constant used to identify the "enable modification logs" preference  */
    public static final String PREFERENCE_MODIFICATIONLOGS_ENABLE = "modificationLogsEnable";

    /** The constant used to identify the "enable search request logs" preference  */
    public static final String PREFERENCE_SEARCHREQUESTLOGS_ENABLE = "searchRequestLogsEnable";

    /** The constant used to identify the "enable search result entry logs" preference  */
    public static final String PREFERENCE_SEARCHRESULTENTRYLOGS_ENABLE = "searchResultEntryLogsEnable";

}
