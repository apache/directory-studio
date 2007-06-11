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

package org.apache.directory.studio.schemas.view.preferences;


import org.apache.directory.studio.schemas.Activator;
import org.apache.directory.studio.schemas.Messages;
import org.apache.directory.studio.schemas.PluginConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;


/**
 * Schema Preference Page.
 * From there you can access schema related preferences.
 *
 */
public class SchemasEditorPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
    // UI fields
    private Button specificCoreSchemasCheckbox;
    private Label specificCoreSchemasLabel;
    private Text specificCoreSchemasText;
    private Button specificCoreSchemasButton;
    private Button defaultOidCheckbox;
    private Label defaultOidLabel;
    private Text defaultOidText;


    /**
     * Creates a new instance of SchemasEditorPreferencePage.
     */
    public SchemasEditorPreferencePage()
    {
        super();
        setPreferenceStore( Activator.getDefault().getPreferenceStore() );
        setDescription( Messages
            .getString( "SchemasEditorPreferencePage.General_settings_for_the_Schemas_Editor_Plugin" ) ); //$NON-NLS-1$
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */
    protected Control createContents( Composite parent )
    {

        // SPECIFIC CORE SCHEMAS Group
        Group specificCoreSchemasGroup = new Group( parent, SWT.NONE );
        specificCoreSchemasGroup.setText( Messages.getString( "SchemasEditorPreferencePage.Core_Schemas" ) ); //$NON-NLS-1$
        specificCoreSchemasGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        specificCoreSchemasGroup.setLayout( new GridLayout( 3, false ) );

        // SPECIFIC CORE SCHEMAS Checkbox
        specificCoreSchemasCheckbox = new Button( specificCoreSchemasGroup, SWT.CHECK );
        specificCoreSchemasCheckbox.setText( Messages
            .getString( "SchemasEditorPreferencePage.Use_specific_core_schemas" ) ); //$NON-NLS-1$
        specificCoreSchemasCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, false, false, 3, 1 ) );

        // SPECIFIC CORE SCHEMAS Label 
        specificCoreSchemasLabel = new Label( specificCoreSchemasGroup, SWT.NONE );
        specificCoreSchemasLabel.setText( Messages.getString( "SchemasEditorPreferencePage.Core_schemas_directory" ) ); //$NON-NLS-1$

        // SPECIFIC CORE SCHEMAS Text
        specificCoreSchemasText = new Text( specificCoreSchemasGroup, SWT.BORDER );
        specificCoreSchemasText.setEditable( false );
        specificCoreSchemasText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // SPECIFIC CORE SCHEMAS Button
        specificCoreSchemasButton = new Button( specificCoreSchemasGroup, SWT.PUSH );
        specificCoreSchemasButton.setText( Messages.getString( "SchemasEditorPreferencePage.Browse..." ) ); //$NON-NLS-1$

        // DEFAULT OID Group
        Group defaultOidGroup = new Group( parent, SWT.NONE );
        defaultOidGroup.setText( Messages.getString( "SchemasEditorPreferencePage.Default_OID" ) ); //$NON-NLS-1$
        defaultOidGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        defaultOidGroup.setLayout( new GridLayout( 2, false ) );

        // DEFAULT OID Checkbox
        defaultOidCheckbox = new Button( defaultOidGroup, SWT.CHECK );
        defaultOidCheckbox.setText( Messages
            .getString( "SchemasEditorPreferencePage.Automatically_prefix_new_elements_with_this_OID" ) ); //$NON-NLS-1$
        defaultOidCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, false, false, 2, 1 ) );

        // DEFAULT OID  Label
        defaultOidLabel = new Label( defaultOidGroup, SWT.NONE );
        defaultOidLabel.setText( Messages.getString( "SchemasEditorPreferencePage.Your_organizations_default_OID" ) ); //$NON-NLS-1$

        // DEFAULT OID Text
        defaultOidText = new Text( defaultOidGroup, SWT.BORDER );
        defaultOidText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        initFieldsFromPreferences();

        initListeners();

        applyDialogFont( parent );

        return parent;
    }


    /**
     * Initializes the UI Fields from the preferences.
     */
    private void initFieldsFromPreferences()
    {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();

        specificCoreSchemasCheckbox
            .setSelection( store.getBoolean( PluginConstants.PREFS_SCHEMAS_EDITOR_SPECIFIC_CORE ) );
        specificCoreSchemasLabel.setEnabled( specificCoreSchemasCheckbox.getSelection() );
        specificCoreSchemasText.setEnabled( specificCoreSchemasCheckbox.getSelection() );
        specificCoreSchemasText
            .setText( store.getString( PluginConstants.PREFS_SCHEMAS_EDITOR_SPECIFIC_CORE_DIRECTORY ) );
        specificCoreSchemasButton.setEnabled( specificCoreSchemasCheckbox.getSelection() );

        defaultOidCheckbox.setSelection( store.getBoolean( PluginConstants.PREFS_SCHEMAS_EDITOR_AUTO_OID ) );
        defaultOidLabel.setEnabled( defaultOidCheckbox.getSelection() );
        defaultOidText.setEnabled( defaultOidCheckbox.getSelection() );
        defaultOidText.setText( store.getString( PluginConstants.PREFS_SCHEMAS_EDITOR_COMPANY_OID ) );
    }


    /**
     * Initializes the listeners.
     */
    private void initListeners()
    {
        specificCoreSchemasCheckbox.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                specificCoreSchemasLabel.setEnabled( specificCoreSchemasCheckbox.getSelection() );
                specificCoreSchemasText.setEnabled( specificCoreSchemasCheckbox.getSelection() );
                specificCoreSchemasButton.setEnabled( specificCoreSchemasCheckbox.getSelection() );
            }
        } );

        specificCoreSchemasButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                DirectoryDialog dd = new DirectoryDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                    .getShell() );
                dd.setFilterPath( System.getProperty( "user.home" ) ); //$NON-NLS-1$
                String selectedFolder = dd.open();
                if ( selectedFolder != null )
                {
                    specificCoreSchemasText.setText( selectedFolder );
                }
            }
        } );

        defaultOidCheckbox.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                defaultOidLabel.setEnabled( defaultOidCheckbox.getSelection() );
                defaultOidText.setEnabled( defaultOidCheckbox.getSelection() );
            }
        } );

        defaultOidText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "([0-9]*\\.?)*" ) ) //$NON-NLS-1$
                {
                    e.doit = false;
                }
            }
        } );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferencePage#performOk()
     */
    public boolean performOk()
    {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();

        store.setValue( PluginConstants.PREFS_SCHEMAS_EDITOR_SPECIFIC_CORE, specificCoreSchemasCheckbox.getSelection() );
        store
            .setValue( PluginConstants.PREFS_SCHEMAS_EDITOR_SPECIFIC_CORE_DIRECTORY, specificCoreSchemasText.getText() );

        store.setValue( PluginConstants.PREFS_SCHEMAS_EDITOR_AUTO_OID, defaultOidCheckbox.getSelection() );
        store.setValue( PluginConstants.PREFS_SCHEMAS_EDITOR_COMPANY_OID, defaultOidText.getText() );

        return super.performOk();
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    protected void performDefaults()
    {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();

        specificCoreSchemasCheckbox.setSelection( store
            .getDefaultBoolean( PluginConstants.PREFS_SCHEMAS_EDITOR_SPECIFIC_CORE ) );
        specificCoreSchemasLabel.setEnabled( specificCoreSchemasCheckbox.getSelection() );
        specificCoreSchemasText.setEnabled( specificCoreSchemasCheckbox.getSelection() );
        specificCoreSchemasText.setText( store
            .getDefaultString( PluginConstants.PREFS_SCHEMAS_EDITOR_SPECIFIC_CORE_DIRECTORY ) );
        specificCoreSchemasButton.setEnabled( specificCoreSchemasCheckbox.getSelection() );

        defaultOidCheckbox.setSelection( store.getDefaultBoolean( PluginConstants.PREFS_SCHEMAS_EDITOR_AUTO_OID ) );
        defaultOidLabel.setEnabled( defaultOidCheckbox.getSelection() );
        defaultOidText.setEnabled( defaultOidCheckbox.getSelection() );
        defaultOidText.setText( store.getDefaultString( PluginConstants.PREFS_SCHEMAS_EDITOR_COMPANY_OID ) );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init( IWorkbench workbench )
    {
    }
}
