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


import java.util.TimeZone;


/**
 * Constants for the connection core plugin.
 * Final reference -> class shouldn't be extended
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class ConnectionCoreConstants
{
    /**
     *  Ensures no construction of this class, also ensures there is no need for final keyword above
     *  (Implicit super constructor is not visible for default constructor),
     *  but is still self documenting.
     */
    private ConnectionCoreConstants()
    {
    }

    /** The plug-in ID */
    public static final String PLUGIN_ID = ConnectionCorePlugin.getDefault().getPluginProperties()
        .getString( "Plugin_id" ); //$NON-NLS-1$

    /** The line separator. */
    public static final String LINE_SEPARATOR = System.getProperty( "line.separator" ); //$NON-NLS-1$

    /** The date format of the modification logger */
    public static final String DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS"; //$NON-NLS-1$

    /** Defines an UTC/GMT time zone */
    public static final TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone( "UTC" );

    /** The constant used to identify if certificates for secure connections should be validated */
    public static final String PREFERENCE_VALIDATE_CERTIFICATES = "validateCertificates"; //$NON-NLS-1$

    /** The constant used to identify the preferred LDAP context factory */
    public static final String PREFERENCE_LDAP_CONTEXT_FACTORY = "ldapContextFactory"; //$NON-NLS-1$

    /** The constant used to identify the "enable modification logs" preference  */
    public static final String PREFERENCE_MODIFICATIONLOGS_ENABLE = "modificationLogsEnable"; //$NON-NLS-1$

    /** The constant used to identify the "modification log file count" preference  */
    public static final String PREFERENCE_MODIFICATIONLOGS_FILE_COUNT = "modificationLogsFileCount"; //$NON-NLS-1$

    /** The constant used to identify the "modification log file size" preference  */
    public static final String PREFERENCE_MODIFICATIONLOGS_FILE_SIZE = "modificationLogsFileSize"; //$NON-NLS-1$

    /** The constant used to identify the "enable search request logs" preference  */
    public static final String PREFERENCE_SEARCHREQUESTLOGS_ENABLE = "searchRequestLogsEnable"; //$NON-NLS-1$

    /** The constant used to identify the "enable search result entry logs" preference  */
    public static final String PREFERENCE_SEARCHRESULTENTRYLOGS_ENABLE = "searchResultEntryLogsEnable"; //$NON-NLS-1$

    /** The constant used to identify the "search log file count" preference  */
    public static final String PREFERENCE_SEARCHLOGS_FILE_COUNT = "searchLogsFileCount"; //$NON-NLS-1$

    /** The constant used to identify the "search log file size" preference  */
    public static final String PREFERENCE_SEARCHLOGS_FILE_SIZE = "searchLogsFileSize"; //$NON-NLS-1$

    /** The constant used to identify the "masked attributes" preference  */
    public static final String PREFERENCE_MODIFICATIONLOGS_MASKED_ATTRIBUTES = "modificationLogsMaskedAttributes"; //$NON-NLS-1$

    /** The constant used to identify the "use KRB5 system properties" preference  */
    public static final String PREFERENCE_USE_KRB5_SYSTEM_PROPERTIES = "useKrb5SystemProperties"; //$NON-NLS-1$

    /** The constant used to identify the KRB5 login module class name */
    public static final String PREFERENCE_KRB5_LOGIN_MODULE = "krb5LoginModule"; //$NON-NLS-1$

    /** The constant used to identify the default network provider setting */
    public static final String PREFERENCE_DEFAULT_NETWORK_PROVIDER = "defaultNetworkProvider"; //$NON-NLS-1$

    /** The constant used to identify the default network provider setting */
    public static final int PREFERENCE_NETWORK_PROVIDER_JNDI = 0;

    /** The constant used to identify the default network provider setting */
    public static final int PREFERENCE_NETWORK_PROVIDER_APACHE_DIRECTORY_LDAP_API = 1;

}
