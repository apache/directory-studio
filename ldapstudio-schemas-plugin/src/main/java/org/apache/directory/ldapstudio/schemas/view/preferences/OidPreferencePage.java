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

package org.apache.directory.ldapstudio.schemas.view.preferences;


import java.io.IOException;

import org.apache.directory.ldapstudio.schemas.Activator;
import org.apache.directory.ldapstudio.schemas.Messages;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;


/**
 * General Preference Page (actually contains everything related to object classes
 * and attribute types).
 * From there you can configure the company OID and auto-prefix settings.
 *
 */
public class OidPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{

    public static final String COMPANY_OID = "prefs_company_oid"; //$NON-NLS-1$
    public static final String AUTO_OID = "prefs_auto_oid"; //$NON-NLS-1$

    private ScopedPreferenceStore preferences;


    public OidPreferencePage()
    {
        super( GRID );
        super.setDescription( "General settings for OID" );
        preferences = new ScopedPreferenceStore( new ConfigurationScope(), Activator.PLUGIN_ID );
        setPreferenceStore( preferences );
    }


    public void init( IWorkbench workbench )
    {
    }


    @Override
    protected void createFieldEditors()
    {

        StringFieldEditor oidEditor = new StringFieldEditor( COMPANY_OID, Messages
            .getString( "GeneralPreferencePage.Your_organizations_default_OID" ), //$NON-NLS-1$
            getFieldEditorParent() );

        addField( oidEditor );

        BooleanFieldEditor autoOidEditor = new BooleanFieldEditor( AUTO_OID, Messages
            .getString( "GeneralPreferencePage.Automatically_prefix_new_elements_with_this_OID" ), //$NON-NLS-1$
            getFieldEditorParent() );

        addField( autoOidEditor );
    }


    public boolean performOk()
    {
        try
        {
            preferences.save();
        }
        catch ( IOException e )
        {
        }
        return super.performOk();
    }
}
