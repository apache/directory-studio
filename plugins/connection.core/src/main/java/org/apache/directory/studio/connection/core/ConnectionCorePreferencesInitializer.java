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


import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;


/**
 * This class is used to set default preference values.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ConnectionCorePreferencesInitializer extends AbstractPreferenceInitializer
{
    /**
     * {@inheritDoc}
     */
    public void initializeDefaultPreferences()
    {
        Preferences preferences = ConnectionCorePlugin.getDefault().getPluginPreferences();
        IEclipsePreferences defaultPreferences = ConnectionCorePlugin.getDefault().getDefaultScopePreferences();

        // LDAP connection settings
        preferences.setDefault( ConnectionCoreConstants.PREFERENCE_VALIDATE_CERTIFICATES, true );
        String defaultKrb5LoginModule = ConnectionCorePlugin.getDefault().getDefaultKrb5LoginModule();
        preferences.setDefault( ConnectionCoreConstants.PREFERENCE_KRB5_LOGIN_MODULE, defaultKrb5LoginModule );
        preferences.setDefault( ConnectionCoreConstants.PREFERENCE_USE_KRB5_SYSTEM_PROPERTIES, false );

        // Modification Logs
        defaultPreferences.putBoolean( ConnectionCoreConstants.PREFERENCE_MODIFICATIONLOGS_ENABLE, true );
        defaultPreferences.put( ConnectionCoreConstants.PREFERENCE_MODIFICATIONLOGS_MASKED_ATTRIBUTES, "" );
        defaultPreferences.putInt( ConnectionCoreConstants.PREFERENCE_MODIFICATIONLOGS_FILE_COUNT, 10 );
        defaultPreferences.putInt( ConnectionCoreConstants.PREFERENCE_MODIFICATIONLOGS_FILE_SIZE, 100 );

        // Search Logs
        defaultPreferences.putBoolean( ConnectionCoreConstants.PREFERENCE_SEARCHREQUESTLOGS_ENABLE, true );
        defaultPreferences.putBoolean( ConnectionCoreConstants.PREFERENCE_SEARCHRESULTENTRYLOGS_ENABLE, false );
        defaultPreferences.putInt( ConnectionCoreConstants.PREFERENCE_SEARCHLOGS_FILE_COUNT, 10 );
        defaultPreferences.putInt( ConnectionCoreConstants.PREFERENCE_SEARCHLOGS_FILE_SIZE, 100 );

        // Connections Passwords Keystore
        preferences.setDefault( ConnectionCoreConstants.PREFERENCE_CONNECTIONS_PASSWORDS_KEYSTORE,
            ConnectionCoreConstants.PREFERENCE_CONNECTIONS_PASSWORDS_KEYSTORE_OFF );

        ConnectionCorePlugin.getDefault().flushDefaultScopePreferences();
    }
}
