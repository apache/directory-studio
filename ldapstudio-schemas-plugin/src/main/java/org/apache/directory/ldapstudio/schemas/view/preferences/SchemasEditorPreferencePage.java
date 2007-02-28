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
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;


/**
 * Schema Preference Page.
 * From there you can access schema related preferences.
 *
 */
public class SchemasEditorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{

    public static final String DEFAULT_DIRECTORY = "prefs_default_directory"; //$NON-NLS-1$
    public static final String SAVE_WORKSPACE = "prefs_save_workspace"; //$NON-NLS-1$
    public static final String SPECIFIC_CORE = "prefs_specific_core"; //$NON-NLS-1$
    public static final String SPECIFIC_CORE_DIRECTORY = "prefs_specific_core_directory"; //$NON-NLS-1$

    private ScopedPreferenceStore preferences;


    public SchemasEditorPreferencePage()
    {
        super( GRID );
        super.setDescription( "General settings for the Schemas Editor" );
        preferences = new ScopedPreferenceStore( new ConfigurationScope(), Activator.PLUGIN_ID );
        setPreferenceStore( preferences );
    }


    public void init( IWorkbench workbench )
    {
        // TODO Auto-generated method stub

    }


    @Override
    protected void createFieldEditors()
    {

        DirectoryFieldEditor directoryEditor = new DirectoryFieldEditor( DEFAULT_DIRECTORY, Messages
            .getString( "SchemaPreferencePage.Default_save-load_dialogs_directory" ), //$NON-NLS-1$
            getFieldEditorParent() );

        addField( directoryEditor );

        BooleanFieldEditor saveWorkspaceEditor = new BooleanFieldEditor( SAVE_WORKSPACE, Messages
            .getString( "SchemaPreferencePage.Save_schemas_configuration_when_exiting_LDAP_Studio" ), //$NON-NLS-1$
            getFieldEditorParent() );

        addField( saveWorkspaceEditor );

        BooleanFieldEditor specificCore = new BooleanFieldEditor( SPECIFIC_CORE, Messages
            .getString( "SchemaPreferencePage.Use_specific_core_files" ), //$NON-NLS-1$
            getFieldEditorParent() );

        addField( specificCore );

        DirectoryFieldEditor coreDirectoryEditor = new DirectoryFieldEditor( SPECIFIC_CORE_DIRECTORY, Messages
            .getString( "SchemaPreferencePage.Core_schemas_directory" ), //$NON-NLS-1$
            getFieldEditorParent() );

        addField( coreDirectoryEditor );
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
