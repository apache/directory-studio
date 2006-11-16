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

package org.apache.directory.ldapstudio.schemas.controller;


import org.apache.directory.ldapstudio.schemas.view.preferences.GeneralPreferencePage;
import org.apache.directory.ldapstudio.schemas.view.preferences.SchemaPreferencePage;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;


public class PreferenceInitializer extends AbstractPreferenceInitializer
{

    public PreferenceInitializer()
    {
        super();
    }


    @Override
    public void initializeDefaultPreferences()
    {
        IEclipsePreferences defaults = new DefaultScope().getNode( Application.PLUGIN_ID );
        defaults.put( GeneralPreferencePage.COMPANY_OID, "1.2.3.4.5.6" ); //$NON-NLS-1$
        defaults.putBoolean( GeneralPreferencePage.AUTO_OID, true );
        defaults.put( SchemaPreferencePage.DEFAULT_DIRECTORY, System.getProperty( "user.home" ) ); //$NON-NLS-1$
        defaults.putBoolean( SchemaPreferencePage.SAVE_WORKSPACE, true );
        defaults.putBoolean( SchemaPreferencePage.SPECIFIC_CORE, false );
        defaults.put( SchemaPreferencePage.SPECIFIC_CORE_DIRECTORY, System.getProperty( "user.home" ) ); //$NON-NLS-1$
    }

}
