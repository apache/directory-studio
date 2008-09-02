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

package org.apache.directory.studio.ldifeditor.dialogs.preferences;


import org.apache.directory.studio.connection.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldifeditor.LdifEditorActivator;
import org.apache.directory.studio.ldifeditor.LdifEditorConstants;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PreferencesUtil;


/**
 * The main preference page of the LDIF editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class LdifEditorPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

    // private Button autoWrapButton;

    /** The enable folding button. */
    private Button enableFoldingButton;

    /** The initially fold label. */
    private Label initiallyFoldLabel;

    /** The initially fold comments button. */
    private Button initiallyFoldCommentsButton;

    /** The initially fold records button. */
    private Button initiallyFoldRecordsButton;

    /** The initially fold wrapped lines button. */
    private Button initiallyFoldWrappedLinesButton;

    /** The use ldif double click button. */
    private Button useLdifDoubleClickButton;

    /** The update if entry exists button. */
    private Button updateIfEntryExistsButton;

    /** The continue on error button. */
    private Button continueOnErrorButton;


    /**
     * Creates a new instance of LdifEditorPreferencePage.
     */
    public LdifEditorPreferencePage()
    {
        super( "LDIF Editor" );
        super.setPreferenceStore( LdifEditorActivator.getDefault().getPreferenceStore() );
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbench workbench )
    {
    }


    /**
     * {@inheritDoc}
     */
    protected Control createContents( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout layout = new GridLayout( 1, false );
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.marginLeft = 0;
        layout.marginRight = 0;
        layout.marginTop = 0;
        layout.marginBottom = 0;
        composite.setLayout( layout );
        composite.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );

        String text = "See <a>Text Editors</a> for the general text editor preferences.";
        Link link = BaseWidgetUtils.createLink( composite, text, 1 );
        link.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                PreferencesUtil.createPreferenceDialogOn( getShell(),
                    "org.eclipse.ui.preferencePages.GeneralTextEditor", null, null ); //$NON-NLS-1$
            }
        } );
        String text2 = "See <a>Text Formats</a> for LDIF format preferences.";
        Link link2 = BaseWidgetUtils.createLink( composite, text2, 1 );
        link2.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                PreferencesUtil.createPreferenceDialogOn( getShell(), LdifEditorConstants.PREFERENCEPAGEID_TEXTFORMATS, null,
                    null ); //$NON-NLS-1$
            }
        } );

        BaseWidgetUtils.createSpacer( composite, 1 );
        BaseWidgetUtils.createSpacer( composite, 1 );

        Group foldGroup = BaseWidgetUtils.createGroup( BaseWidgetUtils.createColumnContainer( composite, 1, 1 ),
            "Folding", 1 );

        enableFoldingButton = BaseWidgetUtils.createCheckbox( foldGroup, "Enable Folding", 1 );
        enableFoldingButton.setSelection( getPreferenceStore().getBoolean(
            LdifEditorConstants.PREFERENCE_LDIFEDITOR_FOLDING_ENABLE ) );
        enableFoldingButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                checkEnabled();
            }
        } );

        Composite initiallyFoldComposiste = BaseWidgetUtils.createColumnContainer( foldGroup, 4, 1 );
        initiallyFoldLabel = BaseWidgetUtils.createLabel( initiallyFoldComposiste, "Initially fold:", 1 );
        initiallyFoldCommentsButton = BaseWidgetUtils.createCheckbox( initiallyFoldComposiste, "Comments", 1 );
        initiallyFoldCommentsButton.setSelection( getPreferenceStore().getBoolean(
            LdifEditorConstants.PREFERENCE_LDIFEDITOR_FOLDING_INITIALLYFOLDCOMMENTS ) );
        initiallyFoldRecordsButton = BaseWidgetUtils.createCheckbox( initiallyFoldComposiste, "Records", 1 );
        initiallyFoldRecordsButton.setSelection( getPreferenceStore().getBoolean(
            LdifEditorConstants.PREFERENCE_LDIFEDITOR_FOLDING_INITIALLYFOLDRECORDS ) );
        initiallyFoldWrappedLinesButton = BaseWidgetUtils.createCheckbox( initiallyFoldComposiste, "Wrapped lines", 1 );
        initiallyFoldWrappedLinesButton.setSelection( getPreferenceStore().getBoolean(
            LdifEditorConstants.PREFERENCE_LDIFEDITOR_FOLDING_INITIALLYFOLDWRAPPEDLINES ) );

        BaseWidgetUtils.createSpacer( composite, 1 );

        Group doubleClickGroup = BaseWidgetUtils.createGroup( BaseWidgetUtils.createColumnContainer( composite, 1, 1 ),
            "Double Click Behaviour", 1 );
        useLdifDoubleClickButton = BaseWidgetUtils.createCheckbox( doubleClickGroup,
            "Select whole attribute or value on double click", 1 );
        useLdifDoubleClickButton.setSelection( getPreferenceStore().getBoolean(
            LdifEditorConstants.PREFERENCE_LDIFEDITOR_DOUBLECLICK_USELDIFDOUBLECLICK ) );

        BaseWidgetUtils.createSpacer( composite, 1 );

        // Options
        Group optionsGroup = BaseWidgetUtils.createGroup( BaseWidgetUtils.createColumnContainer( composite, 1, 1 ),
            "Execute Options", 1 );

        updateIfEntryExistsButton = BaseWidgetUtils.createCheckbox( optionsGroup, "Update existing entries", 1 );
        updateIfEntryExistsButton
            .setToolTipText( "This options applies for LDIF content records and LDIF add records. "
                + "If enabled and the entry to add already exists it will be updated with the attributes defined in the LDIF record." );
        updateIfEntryExistsButton.setSelection( getPreferenceStore().getBoolean(
            LdifEditorConstants.PREFERENCE_LDIFEDITOR_OPTIONS_UPDATEIFENTRYEXISTS ) );

        continueOnErrorButton = BaseWidgetUtils.createCheckbox( optionsGroup, "Continue on error", 1 );
        continueOnErrorButton.setSelection( getPreferenceStore().getBoolean(
            LdifEditorConstants.PREFERENCE_LDIFEDITOR_OPTIONS_CONTINUEONERROR ) );

        checkEnabled();

        return composite;
    }


    /**
     * Enables/disables widgets dependent if options are selected.
     */
    private void checkEnabled()
    {
        initiallyFoldLabel.setEnabled( enableFoldingButton.getSelection() );
        initiallyFoldCommentsButton.setEnabled( enableFoldingButton.getSelection() );
        initiallyFoldRecordsButton.setEnabled( enableFoldingButton.getSelection() );
        initiallyFoldWrappedLinesButton.setEnabled( enableFoldingButton.getSelection() );
    }


    /**
     * {@inheritDoc}
     */
    public boolean performOk()
    {
        getPreferenceStore().setValue( LdifEditorConstants.PREFERENCE_LDIFEDITOR_FOLDING_ENABLE,
            enableFoldingButton.getSelection() );
        getPreferenceStore().setValue( LdifEditorConstants.PREFERENCE_LDIFEDITOR_FOLDING_INITIALLYFOLDCOMMENTS,
            initiallyFoldCommentsButton.getSelection() );
        getPreferenceStore().setValue( LdifEditorConstants.PREFERENCE_LDIFEDITOR_FOLDING_INITIALLYFOLDRECORDS,
            initiallyFoldRecordsButton.getSelection() );
        getPreferenceStore().setValue( LdifEditorConstants.PREFERENCE_LDIFEDITOR_FOLDING_INITIALLYFOLDWRAPPEDLINES,
            initiallyFoldWrappedLinesButton.getSelection() );

        getPreferenceStore().setValue( LdifEditorConstants.PREFERENCE_LDIFEDITOR_DOUBLECLICK_USELDIFDOUBLECLICK,
            useLdifDoubleClickButton.getSelection() );

        getPreferenceStore().setValue( LdifEditorConstants.PREFERENCE_LDIFEDITOR_OPTIONS_UPDATEIFENTRYEXISTS,
            updateIfEntryExistsButton.getSelection() );
        getPreferenceStore().setValue( LdifEditorConstants.PREFERENCE_LDIFEDITOR_OPTIONS_CONTINUEONERROR,
            continueOnErrorButton.getSelection() );

        BrowserCorePlugin.getDefault().savePluginPreferences();

        return true;
    }


    /**
     * {@inheritDoc}
     */
    protected void performDefaults()
    {
        enableFoldingButton.setSelection( getPreferenceStore().getDefaultBoolean(
            LdifEditorConstants.PREFERENCE_LDIFEDITOR_FOLDING_ENABLE ) );
        initiallyFoldCommentsButton.setSelection( getPreferenceStore().getDefaultBoolean(
            LdifEditorConstants.PREFERENCE_LDIFEDITOR_FOLDING_INITIALLYFOLDCOMMENTS ) );
        initiallyFoldRecordsButton.setSelection( getPreferenceStore().getDefaultBoolean(
            LdifEditorConstants.PREFERENCE_LDIFEDITOR_FOLDING_INITIALLYFOLDRECORDS ) );
        initiallyFoldWrappedLinesButton.setSelection( getPreferenceStore().getDefaultBoolean(
            LdifEditorConstants.PREFERENCE_LDIFEDITOR_FOLDING_INITIALLYFOLDWRAPPEDLINES ) );

        useLdifDoubleClickButton.setSelection( getPreferenceStore().getDefaultBoolean(
            LdifEditorConstants.PREFERENCE_LDIFEDITOR_DOUBLECLICK_USELDIFDOUBLECLICK ) );

        updateIfEntryExistsButton.setSelection( getPreferenceStore().getDefaultBoolean(
            LdifEditorConstants.PREFERENCE_LDIFEDITOR_OPTIONS_UPDATEIFENTRYEXISTS ) );
        continueOnErrorButton.setSelection( getPreferenceStore().getDefaultBoolean(
            LdifEditorConstants.PREFERENCE_LDIFEDITOR_OPTIONS_CONTINUEONERROR ) );

        super.performDefaults();

        checkEnabled();
    }

}
