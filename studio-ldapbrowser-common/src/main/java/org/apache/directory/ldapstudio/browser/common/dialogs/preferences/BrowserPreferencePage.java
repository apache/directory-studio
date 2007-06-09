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

package org.apache.directory.ldapstudio.browser.common.dialogs.preferences;


import org.apache.directory.ldapstudio.browser.common.BrowserCommonActivator;
import org.apache.directory.ldapstudio.browser.common.BrowserCommonConstants;
import org.apache.directory.ldapstudio.browser.common.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


public class BrowserPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

    private static final String DN = "DN";

    private static final String RDN = "RDN";

    private static final String RDN_VALUE = "RDN value";

    private Combo entryLabelCombo;

    private Button entryAbbreviateButton;

    private Text entryAbbreviateMaxLengthText;

    private Combo searchResultLabelCombo;

    private Button searchResultAbbreviateButton;

    private Text searchResultAbbreviateMaxLengthText;

    private Button enableFoldingButton;

    private Label foldingSizeLabel;

    private Text foldingSizeText;

    private Button expandBaseEntriesButton;;

    private Button checkForChildrenButton;

    private Button showAliasAndReferralObjectsButton;

    private Button fetchSubentriesButton;


    public BrowserPreferencePage()
    {
        super();
        super.setPreferenceStore( BrowserCommonActivator.getDefault().getPreferenceStore() );
        super.setDescription( "General settings for the LDAP browser view:" );
    }


    public void init( IWorkbench workbench )
    {
    }


    protected Control createContents( Composite parent )
    {

        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        BaseWidgetUtils.createSpacer( composite, 1 );
        BaseWidgetUtils.createSpacer( composite, 1 );
        Group entryLabelGroup = BaseWidgetUtils.createGroup( BaseWidgetUtils.createColumnContainer( composite, 1, 1 ),
            "Entry label", 1 );

        Composite entryLabelComposite = BaseWidgetUtils.createColumnContainer( entryLabelGroup, 3, 1 );
        BaseWidgetUtils.createLabel( entryLabelComposite, "Use ", 1 );
        entryLabelCombo = BaseWidgetUtils.createCombo( entryLabelComposite, new String[]
            { DN, RDN, RDN_VALUE }, 0, 1 );
        entryLabelCombo.setLayoutData( new GridData() );
        entryLabelCombo
            .select( getPreferenceStore().getInt( BrowserCommonConstants.PREFERENCE_BROWSER_ENTRY_LABEL ) == BrowserCommonConstants.SHOW_RDN_VALUE ? 2
                : getPreferenceStore().getInt( BrowserCommonConstants.PREFERENCE_BROWSER_ENTRY_LABEL ) == BrowserCommonConstants.SHOW_RDN ? 1
                    : 0 );
        BaseWidgetUtils.createLabel( entryLabelComposite, " as entry label", 1 );

        Composite entryAbbreviateComposite = BaseWidgetUtils.createColumnContainer( entryLabelGroup, 3, 1 );
        entryAbbreviateButton = BaseWidgetUtils.createCheckbox( entryAbbreviateComposite, "Limit label length to ", 1 );
        entryAbbreviateButton.setSelection( getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_BROWSER_ENTRY_ABBREVIATE ) );
        entryAbbreviateButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                updateEnabled();
            }
        } );
        entryAbbreviateMaxLengthText = BaseWidgetUtils.createText( entryAbbreviateComposite, getPreferenceStore()
            .getString( BrowserCommonConstants.PREFERENCE_BROWSER_ENTRY_ABBREVIATE_MAX_LENGTH ), 3, 1 );
        entryAbbreviateMaxLengthText.setEnabled( entryAbbreviateButton.getSelection() );
        entryAbbreviateMaxLengthText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "[0-9]*" ) )
                {
                    e.doit = false;
                }
                if ( "".equals( entryAbbreviateMaxLengthText.getText() ) && e.text.matches( "[0]" ) )
                {
                    e.doit = false;
                }
            }
        } );
        BaseWidgetUtils.createLabel( entryAbbreviateComposite, " characters", 1 );

        BaseWidgetUtils.createSpacer( composite, 1 );
        BaseWidgetUtils.createSpacer( composite, 1 );
        Group searchResultLabelGroup = BaseWidgetUtils.createGroup( BaseWidgetUtils.createColumnContainer( composite,
            1, 1 ), "Search result label", 1 );

        Composite searchResultLabelComposite = BaseWidgetUtils.createColumnContainer( searchResultLabelGroup, 3, 1 );
        BaseWidgetUtils.createLabel( searchResultLabelComposite, "Use ", 1 );
        searchResultLabelCombo = BaseWidgetUtils.createCombo( searchResultLabelComposite, new String[]
            { DN, RDN, RDN_VALUE }, 0, 1 );
        searchResultLabelCombo.setLayoutData( new GridData() );
        searchResultLabelCombo
            .select( getPreferenceStore().getInt( BrowserCommonConstants.PREFERENCE_BROWSER_SEARCH_RESULT_LABEL ) == BrowserCommonConstants.SHOW_RDN_VALUE ? 2
                : getPreferenceStore().getInt( BrowserCommonConstants.PREFERENCE_BROWSER_SEARCH_RESULT_LABEL ) == BrowserCommonConstants.SHOW_RDN ? 1
                    : 0 );
        BaseWidgetUtils.createLabel( searchResultLabelComposite, " as search result label", 1 );

        Composite searchResultAbbreviateComposite = BaseWidgetUtils
            .createColumnContainer( searchResultLabelGroup, 3, 1 );
        searchResultAbbreviateButton = BaseWidgetUtils.createCheckbox( searchResultAbbreviateComposite,
            "Limit label length to ", 1 );
        searchResultAbbreviateButton.setSelection( getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_BROWSER_SEARCH_RESULT_ABBREVIATE ) );
        searchResultAbbreviateButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                updateEnabled();
            }
        } );
        searchResultAbbreviateMaxLengthText = BaseWidgetUtils
            .createText( searchResultAbbreviateComposite, getPreferenceStore().getString(
                BrowserCommonConstants.PREFERENCE_BROWSER_SEARCH_RESULT_ABBREVIATE_MAX_LENGTH ), 3, 1 );
        searchResultAbbreviateMaxLengthText.setEnabled( searchResultAbbreviateButton.getSelection() );
        searchResultAbbreviateMaxLengthText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "[0-9]*" ) )
                {
                    e.doit = false;
                }
                if ( "".equals( searchResultAbbreviateMaxLengthText.getText() ) && e.text.matches( "[0]" ) )
                {
                    e.doit = false;
                }
            }
        } );
        BaseWidgetUtils.createLabel( searchResultAbbreviateComposite, " characters", 1 );

        BaseWidgetUtils.createSpacer( composite, 1 );
        BaseWidgetUtils.createSpacer( composite, 1 );
        Group foldingGroup = BaseWidgetUtils.createGroup( BaseWidgetUtils.createColumnContainer( composite, 1, 1 ),
            "Folding", 1 );
        Composite pagingGroupComposite = BaseWidgetUtils.createColumnContainer( foldingGroup, 2, 1 );
        enableFoldingButton = BaseWidgetUtils.createCheckbox( pagingGroupComposite, "Enable folding", 2 );
        enableFoldingButton.setSelection( getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_BROWSER_ENABLE_FOLDING ) );
        enableFoldingButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                updateEnabled();
            }
        } );
        foldingSizeLabel = BaseWidgetUtils.createLabel( pagingGroupComposite, "Folding size: ", 1 );
        foldingSizeLabel.setEnabled( enableFoldingButton.getSelection() );
        foldingSizeText = BaseWidgetUtils.createText( pagingGroupComposite, getPreferenceStore().getString(
            BrowserCommonConstants.PREFERENCE_BROWSER_FOLDING_SIZE ), 4, 1 );
        foldingSizeText.setEnabled( enableFoldingButton.getSelection() );
        foldingSizeText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "[0-9]*" ) )
                {
                    e.doit = false;
                }
                if ( "".equals( foldingSizeText.getText() ) && e.text.matches( "[0]" ) )
                {
                    e.doit = false;
                }
            }
        } );

        BaseWidgetUtils.createSpacer( composite, 1 );
        BaseWidgetUtils.createSpacer( composite, 1 );
        expandBaseEntriesButton = BaseWidgetUtils.createCheckbox( composite,
            "Expand base entries when opening connection", 1 );
        expandBaseEntriesButton.setSelection( getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_BROWSER_EXPAND_BASE_ENTRIES ) );
        Preferences coreStore = BrowserCorePlugin.getDefault().getPluginPreferences();
        checkForChildrenButton = BaseWidgetUtils.createCheckbox( composite, "Check for children", 1 );
        checkForChildrenButton
            .setSelection( coreStore.getBoolean( BrowserCoreConstants.PREFERENCE_CHECK_FOR_CHILDREN ) );
        showAliasAndReferralObjectsButton = BaseWidgetUtils.createCheckbox( composite,
            "Show alias and referral objects", 1 );
        showAliasAndReferralObjectsButton.setSelection( coreStore
            .getBoolean( BrowserCoreConstants.PREFERENCE_SHOW_ALIAS_AND_REFERRAL_OBJECTS ) );
        fetchSubentriesButton = BaseWidgetUtils.createCheckbox( composite,
            "Fetch subentries (requires additional search request)", 1 );
        fetchSubentriesButton.setSelection( coreStore.getBoolean( BrowserCoreConstants.PREFERENCE_FETCH_SUBENTRIES ) );

        updateEnabled();

        applyDialogFont( composite );

        return composite;
    }


    private void updateEnabled()
    {
        entryAbbreviateMaxLengthText.setEnabled( entryAbbreviateButton.getSelection() );
        searchResultAbbreviateMaxLengthText.setEnabled( searchResultAbbreviateButton.getSelection() );
        foldingSizeText.setEnabled( enableFoldingButton.getSelection() );
        foldingSizeLabel.setEnabled( enableFoldingButton.getSelection() );
    }


    public boolean performOk()
    {

        Preferences coreStore = BrowserCorePlugin.getDefault().getPluginPreferences();
        coreStore.setValue( BrowserCoreConstants.PREFERENCE_CHECK_FOR_CHILDREN, this.checkForChildrenButton
            .getSelection() );
        coreStore.setValue( BrowserCoreConstants.PREFERENCE_SHOW_ALIAS_AND_REFERRAL_OBJECTS,
            this.showAliasAndReferralObjectsButton.getSelection() );
        coreStore
            .setValue( BrowserCoreConstants.PREFERENCE_FETCH_SUBENTRIES, this.fetchSubentriesButton.getSelection() );
        BrowserCorePlugin.getDefault().savePluginPreferences();

        getPreferenceStore().setValue( BrowserCommonConstants.PREFERENCE_BROWSER_EXPAND_BASE_ENTRIES,
            this.expandBaseEntriesButton.getSelection() );

        getPreferenceStore().setValue( BrowserCommonConstants.PREFERENCE_BROWSER_ENABLE_FOLDING,
            this.enableFoldingButton.getSelection() );
        getPreferenceStore().setValue( BrowserCommonConstants.PREFERENCE_BROWSER_FOLDING_SIZE,
            this.foldingSizeText.getText().trim() );

        getPreferenceStore().setValue(
            BrowserCommonConstants.PREFERENCE_BROWSER_ENTRY_LABEL,
            this.entryLabelCombo.getSelectionIndex() == 2 ? BrowserCommonConstants.SHOW_RDN_VALUE : this.entryLabelCombo
                .getSelectionIndex() == 1 ? BrowserCommonConstants.SHOW_RDN : BrowserCommonConstants.SHOW_DN );
        getPreferenceStore().setValue( BrowserCommonConstants.PREFERENCE_BROWSER_ENTRY_ABBREVIATE,
            this.entryAbbreviateButton.getSelection() );
        getPreferenceStore().setValue( BrowserCommonConstants.PREFERENCE_BROWSER_ENTRY_ABBREVIATE_MAX_LENGTH,
            this.entryAbbreviateMaxLengthText.getText().trim() );

        getPreferenceStore().setValue(
            BrowserCommonConstants.PREFERENCE_BROWSER_SEARCH_RESULT_LABEL,
            this.searchResultLabelCombo.getSelectionIndex() == 2 ? BrowserCommonConstants.SHOW_RDN_VALUE
                : this.searchResultLabelCombo.getSelectionIndex() == 1 ? BrowserCommonConstants.SHOW_RDN
                    : BrowserCommonConstants.SHOW_DN );
        getPreferenceStore().setValue( BrowserCommonConstants.PREFERENCE_BROWSER_SEARCH_RESULT_ABBREVIATE,
            this.searchResultAbbreviateButton.getSelection() );
        getPreferenceStore().setValue( BrowserCommonConstants.PREFERENCE_BROWSER_SEARCH_RESULT_ABBREVIATE_MAX_LENGTH,
            this.searchResultAbbreviateMaxLengthText.getText().trim() );

        return true;
    }


    protected void performDefaults()
    {

        entryLabelCombo
            .select( getPreferenceStore().getDefaultInt( BrowserCommonConstants.PREFERENCE_BROWSER_ENTRY_LABEL ) == BrowserCommonConstants.SHOW_RDN_VALUE ? 2
                : getPreferenceStore().getDefaultInt( BrowserCommonConstants.PREFERENCE_BROWSER_ENTRY_LABEL ) == BrowserCommonConstants.SHOW_RDN ? 1
                    : 0 );
        entryAbbreviateButton.setSelection( getPreferenceStore().getDefaultBoolean(
            BrowserCommonConstants.PREFERENCE_BROWSER_ENTRY_ABBREVIATE ) );
        entryAbbreviateMaxLengthText.setText( getPreferenceStore().getDefaultString(
            BrowserCommonConstants.PREFERENCE_BROWSER_ENTRY_ABBREVIATE_MAX_LENGTH ) );

        searchResultLabelCombo
            .select( getPreferenceStore().getDefaultInt( BrowserCommonConstants.PREFERENCE_BROWSER_SEARCH_RESULT_LABEL ) == BrowserCommonConstants.SHOW_RDN_VALUE ? 2
                : getPreferenceStore().getDefaultInt( BrowserCommonConstants.PREFERENCE_BROWSER_SEARCH_RESULT_LABEL ) == BrowserCommonConstants.SHOW_RDN ? 1
                    : 0 );
        searchResultAbbreviateButton.setSelection( getPreferenceStore().getDefaultBoolean(
            BrowserCommonConstants.PREFERENCE_BROWSER_SEARCH_RESULT_ABBREVIATE ) );
        searchResultAbbreviateMaxLengthText.setText( getPreferenceStore().getDefaultString(
            BrowserCommonConstants.PREFERENCE_BROWSER_SEARCH_RESULT_ABBREVIATE_MAX_LENGTH ) );

        enableFoldingButton.setSelection( getPreferenceStore().getDefaultBoolean(
            BrowserCommonConstants.PREFERENCE_BROWSER_ENABLE_FOLDING ) );
        foldingSizeText.setText( getPreferenceStore().getDefaultString(
            BrowserCommonConstants.PREFERENCE_BROWSER_FOLDING_SIZE ) );

        expandBaseEntriesButton.setSelection( getPreferenceStore().getDefaultBoolean(
            BrowserCommonConstants.PREFERENCE_BROWSER_EXPAND_BASE_ENTRIES ) );
        Preferences coreStore = BrowserCorePlugin.getDefault().getPluginPreferences();
        checkForChildrenButton.setSelection( coreStore
            .getDefaultBoolean( BrowserCoreConstants.PREFERENCE_CHECK_FOR_CHILDREN ) );
        showAliasAndReferralObjectsButton.setSelection( coreStore
            .getDefaultBoolean( BrowserCoreConstants.PREFERENCE_SHOW_ALIAS_AND_REFERRAL_OBJECTS ) );
        fetchSubentriesButton.setSelection( coreStore
            .getDefaultBoolean( BrowserCoreConstants.PREFERENCE_FETCH_SUBENTRIES ) );

        updateEnabled();

        super.performDefaults();
    }

}
