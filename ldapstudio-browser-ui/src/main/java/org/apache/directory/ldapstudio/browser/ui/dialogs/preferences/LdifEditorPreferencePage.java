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

package org.apache.directory.ldapstudio.browser.ui.dialogs.preferences;


import org.apache.directory.ldapstudio.browser.core.BrowserCorePlugin;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.widgets.BaseWidgetUtils;

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


public class LdifEditorPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

    // private Button autoWrapButton;

    private Button enableFoldingButton;

    private Label initiallyFoldLabel;

    private Button initiallyFoldCommentsButton;

    private Button initiallyFoldRecordsButton;

    private Button initiallyFoldWrappedLinesButton;

    private Button useLdifDoubleClickButton;


    public LdifEditorPreferencePage()
    {
        super( "LDIF Editor" );
        super.setPreferenceStore( BrowserUIPlugin.getDefault().getPreferenceStore() );
    }


    public void init( IWorkbench workbench )
    {
    }


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
                PreferencesUtil.createPreferenceDialogOn( getShell(), TextFormatsPreferencePage.class.getName(), null,
                    null ); //$NON-NLS-1$
            }
        } );

        // BaseWidgetUtils.createSpacer(composite, 1);
        // BaseWidgetUtils.createSpacer(composite, 1);
        // autoWrapButton = BaseWidgetUtils.createCheckbox(formatGroup, "Wrap
        // automatically", 1);
        // autoWrapButton.setSelection(getPreferenceStore().getBoolean(BrowserUIConstants.PREFERENCE_LDIFEDITOR_FORMATTER_AUTOWRAP));
        // autoWrapButton.setEnabled(false);

        BaseWidgetUtils.createSpacer( composite, 1 );
        BaseWidgetUtils.createSpacer( composite, 1 );

        Group foldGroup = BaseWidgetUtils.createGroup( BaseWidgetUtils.createColumnContainer( composite, 1, 1 ),
            "Folding", 1 );

        enableFoldingButton = BaseWidgetUtils.createCheckbox( foldGroup, "Enable Folding", 1 );
        enableFoldingButton.setSelection( getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_LDIFEDITOR_FOLDING_ENABLE ) );
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
            BrowserUIConstants.PREFERENCE_LDIFEDITOR_FOLDING_INITIALLYFOLDCOMMENTS ) );
        initiallyFoldRecordsButton = BaseWidgetUtils.createCheckbox( initiallyFoldComposiste, "Records", 1 );
        initiallyFoldRecordsButton.setSelection( getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_LDIFEDITOR_FOLDING_INITIALLYFOLDRECORDS ) );
        initiallyFoldWrappedLinesButton = BaseWidgetUtils.createCheckbox( initiallyFoldComposiste, "Wrapped lines", 1 );
        initiallyFoldWrappedLinesButton.setSelection( getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_LDIFEDITOR_FOLDING_INITIALLYFOLDWRAPPEDLINES ) );

        BaseWidgetUtils.createSpacer( composite, 1 );

        Group doubleClickGroup = BaseWidgetUtils.createGroup( BaseWidgetUtils.createColumnContainer( composite, 1, 1 ),
            "Double Click Behaviour", 1 );
        useLdifDoubleClickButton = BaseWidgetUtils.createCheckbox( doubleClickGroup,
            "Select whole attribute or value on double click", 1 );
        useLdifDoubleClickButton.setSelection( getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_LDIFEDITOR_DOUBLECLICK_USELDIFDOUBLECLICK ) );

        checkEnabled();

        return composite;
    }


    private void checkEnabled()
    {
        initiallyFoldLabel.setEnabled( enableFoldingButton.getSelection() );
        initiallyFoldCommentsButton.setEnabled( enableFoldingButton.getSelection() );
        initiallyFoldRecordsButton.setEnabled( enableFoldingButton.getSelection() );
        initiallyFoldWrappedLinesButton.setEnabled( enableFoldingButton.getSelection() );
    }


    public boolean performOk()
    {

        // getPreferenceStore().setValue(BrowserUIConstants.PREFERENCE_LDIFEDITOR_FORMATTER_AUTOWRAP,
        // this.autoWrapButton.getSelection());

        getPreferenceStore().setValue( BrowserUIConstants.PREFERENCE_LDIFEDITOR_FOLDING_ENABLE,
            this.enableFoldingButton.getSelection() );
        getPreferenceStore().setValue( BrowserUIConstants.PREFERENCE_LDIFEDITOR_FOLDING_INITIALLYFOLDCOMMENTS,
            this.initiallyFoldCommentsButton.getSelection() );
        getPreferenceStore().setValue( BrowserUIConstants.PREFERENCE_LDIFEDITOR_FOLDING_INITIALLYFOLDRECORDS,
            this.initiallyFoldRecordsButton.getSelection() );
        getPreferenceStore().setValue( BrowserUIConstants.PREFERENCE_LDIFEDITOR_FOLDING_INITIALLYFOLDWRAPPEDLINES,
            this.initiallyFoldWrappedLinesButton.getSelection() );

        getPreferenceStore().setValue( BrowserUIConstants.PREFERENCE_LDIFEDITOR_DOUBLECLICK_USELDIFDOUBLECLICK,
            this.useLdifDoubleClickButton.getSelection() );

        BrowserCorePlugin.getDefault().savePluginPreferences();

        return true;
    }


    protected void performDefaults()
    {

        // autoWrapButton.setSelection(getPreferenceStore().getDefaultBoolean(BrowserUIConstants.PREFERENCE_LDIFEDITOR_FORMATTER_AUTOWRAP));

        enableFoldingButton.setSelection( getPreferenceStore().getDefaultBoolean(
            BrowserUIConstants.PREFERENCE_LDIFEDITOR_FOLDING_ENABLE ) );
        initiallyFoldCommentsButton.setSelection( getPreferenceStore().getDefaultBoolean(
            BrowserUIConstants.PREFERENCE_LDIFEDITOR_FOLDING_INITIALLYFOLDCOMMENTS ) );
        initiallyFoldRecordsButton.setSelection( getPreferenceStore().getDefaultBoolean(
            BrowserUIConstants.PREFERENCE_LDIFEDITOR_FOLDING_INITIALLYFOLDRECORDS ) );
        initiallyFoldWrappedLinesButton.setSelection( getPreferenceStore().getDefaultBoolean(
            BrowserUIConstants.PREFERENCE_LDIFEDITOR_FOLDING_INITIALLYFOLDWRAPPEDLINES ) );

        useLdifDoubleClickButton.setSelection( getPreferenceStore().getDefaultBoolean(
            BrowserUIConstants.PREFERENCE_LDIFEDITOR_DOUBLECLICK_USELDIFDOUBLECLICK ) );

        super.performDefaults();

        checkEnabled();
    }

}
