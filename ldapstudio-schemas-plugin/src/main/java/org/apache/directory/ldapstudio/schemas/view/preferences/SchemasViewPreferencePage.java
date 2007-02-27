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


import org.apache.directory.ldapstudio.schemas.Activator;
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * This class implements the Preference page for the Schemas View
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SchemasViewPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
    /** The preference page ID */
    public static final String ID = Activator.PLUGIN_ID + ".preferences.schemasView";

    /** The First Name category */
    private static final String FIRST_NAME = "First Name";

    /** The All Aliases category */
    private static final String ALL_ALIASES = "All Aliases";

    /** The OID category */
    private static final String OID = "OID";

    /** The preference ID for Label */
    public static final String PREFS_SCHEMAS_VIEW_LABEL = SchemasViewPreferencePage.ID + ".label.labelValue";

    /** The preference value for First Name label */
    public static final int PREFS_SCHEMAS_VIEW_LABEL_FIRST_NAME = 0;

    /** The preference value for All Aliases label */
    public static final int PREFS_SCHEMAS_VIEW_LABEL_ALL_ALIASES = 1;

    /** The preference value for OID label */
    public static final int PREFS_SCHEMAS_VIEW_LABEL_OID = 2;

    /** The preference ID for Abbreviate */
    public static final String PREFS_SCHEMAS_VIEW_ABBREVIATE = SchemasViewPreferencePage.ID + ".label.abbreviate";

    /** The preference ID for Abbreviate Max Length*/
    public static final String PREFS_SCHEMAS_VIEW_ABBREVIATE_MAX_LENGTH = SchemasViewPreferencePage.ID
        + ".label.abbreviate.maxLength";

    // UI fields
    private Combo labelCombo;
    private Button limitButton;
    private Text lengthText;


    /**
     * Creates a new instance of HierarchyViewPreferencePage.
     */
    public SchemasViewPreferencePage()
    {
        super();
        super.setPreferenceStore( Activator.getDefault().getPreferenceStore() );
        super.setDescription( "General settings for the Schemas Editor Schemas View" );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */
    protected Control createContents( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout gl = new GridLayout( 1, false );
        gl.marginHeight = gl.marginWidth = 0;
        composite.setLayout( gl );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalSpan = 1;
        composite.setLayoutData( gd );

        // Label Group
        Group labelGroup = new Group( composite, SWT.NONE );
        labelGroup.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        labelGroup.setText( "Label" );
        labelGroup.setLayout( new GridLayout() );
        Composite labelGroupComposite = new Composite( labelGroup, SWT.NONE );
        gl = new GridLayout( 1, false );
        gl.marginHeight = gl.marginWidth = 0;
        labelGroupComposite.setLayout( gl );
        labelGroupComposite.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );

        // Label row composite
        Composite labelComposite = new Composite( labelGroupComposite, SWT.NONE );
        gl = new GridLayout( 3, false );
        gl.marginHeight = gl.marginWidth = 0;
        labelComposite.setLayout( gl );
        gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalSpan = 1;
        labelComposite.setLayoutData( gd );

        // Use Label
        Label useLabel = new Label( labelComposite, SWT.NONE );
        useLabel.setText( "Use" );

        // Label Combo
        labelCombo = new Combo( labelComposite, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER );
        labelCombo.setLayoutData( new GridData() );
        labelCombo.setItems( new String[]
            { FIRST_NAME, ALL_ALIASES, OID } );
        labelCombo.setEnabled( true );

        // As label Label
        Label asLabel = new Label( labelComposite, SWT.NONE );
        asLabel.setText( "as label." );

        // Abbreviate row composite
        Composite abbreviateComposite = new Composite( labelGroupComposite, SWT.NONE );
        gl = new GridLayout( 3, false );
        gl.marginHeight = gl.marginWidth = 0;
        abbreviateComposite.setLayout( gl );
        gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalSpan = 1;
        abbreviateComposite.setLayoutData( gd );

        // Limit label lenght to Label
        limitButton = new Button( abbreviateComposite, SWT.CHECK );
        limitButton.setText( "Limit label length to" );
        gd = new GridData();
        gd.horizontalSpan = 1;
        limitButton.setLayoutData( gd );

        // Lenght Text
        lengthText = new Text( abbreviateComposite, SWT.NONE | SWT.BORDER );
        GridData gridData = new GridData();
        gridData.horizontalSpan = 1;
        gridData.widthHint = 9 * 3;
        lengthText.setLayoutData( gridData );
        lengthText.setTextLimit( 3 );
        lengthText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "[0-9]*" ) )
                {
                    e.doit = false;
                }
                if ( "".equals( lengthText.getText() ) && e.text.matches( "[0]" ) )
                {
                    e.doit = false;
                }
            }
        } );

        // Characters Label
        Label charactersLabel = new Label( abbreviateComposite, SWT.NONE );
        charactersLabel.setText( "characters." );

        initFieldsFromPreferences();

        initListeners();

        applyDialogFont( parent );

        return parent;
    }


    /**
     * Initializes the fields from the preferences store.
     */
    private void initFieldsFromPreferences()
    {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();

        labelCombo.select( store.getInt( PREFS_SCHEMAS_VIEW_LABEL ) );
        limitButton.setSelection( store.getBoolean( PREFS_SCHEMAS_VIEW_ABBREVIATE ) );
        lengthText.setEnabled( limitButton.getSelection() );
        lengthText.setText( store.getString( PREFS_SCHEMAS_VIEW_ABBREVIATE_MAX_LENGTH ) );
    }


    /**
     * Initializes the listeners.
     */
    private void initListeners()
    {
        limitButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                lengthText.setEnabled( limitButton.getSelection() );
            }
        } );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    protected void performDefaults()
    {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();

        labelCombo.select( store.getDefaultInt( PREFS_SCHEMAS_VIEW_LABEL ) );
        limitButton.setSelection( store.getDefaultBoolean( PREFS_SCHEMAS_VIEW_ABBREVIATE ) );
        lengthText.setEnabled( limitButton.getSelection() );
        lengthText.setText( store.getDefaultString( PREFS_SCHEMAS_VIEW_ABBREVIATE_MAX_LENGTH ) );

        super.performDefaults();
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferencePage#performOk()
     */
    public boolean performOk()
    {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();

        if ( labelCombo.getItem( labelCombo.getSelectionIndex() ).equals( FIRST_NAME ) )
        {
            store.setValue( PREFS_SCHEMAS_VIEW_LABEL, PREFS_SCHEMAS_VIEW_LABEL_FIRST_NAME );
        }
        else if ( labelCombo.getItem( labelCombo.getSelectionIndex() ).equals( ALL_ALIASES ) )
        {
            store.setValue( PREFS_SCHEMAS_VIEW_LABEL, PREFS_SCHEMAS_VIEW_LABEL_ALL_ALIASES );
        }
        else if ( labelCombo.getItem( labelCombo.getSelectionIndex() ).equals( OID ) )
        {
            store.setValue( PREFS_SCHEMAS_VIEW_LABEL, PREFS_SCHEMAS_VIEW_LABEL_OID );
        }
        store.setValue( PREFS_SCHEMAS_VIEW_ABBREVIATE, limitButton.getSelection() );
        store.setValue( PREFS_SCHEMAS_VIEW_ABBREVIATE_MAX_LENGTH, lengthText.getText() );

        return true;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init( IWorkbench workbench )
    {
    }
}
