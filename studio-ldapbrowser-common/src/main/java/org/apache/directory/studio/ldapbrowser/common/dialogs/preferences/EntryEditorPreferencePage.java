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

package org.apache.directory.studio.ldapbrowser.common.dialogs.preferences;


import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.common.widgets.BaseWidgetUtils;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


public class EntryEditorPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

    private Button showObjectClassAttributeButton;

    private Button showMustAttributesButton;

    private Button showMayAttributesButton;

    private Button showOperationalAttributesButton;

    private Button enableFoldingButton;

    private Label foldingThresholdLabel;

    private Text foldingThresholdText;


    public EntryEditorPreferencePage()
    {
        super();
        super.setPreferenceStore( BrowserCommonActivator.getDefault().getPreferenceStore() );
        super.setDescription( "General settings for the LDAP entry editor:" );
    }


    public void init( IWorkbench workbench )
    {
    }


    protected Control createContents( Composite parent )
    {

        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        BaseWidgetUtils.createSpacer( composite, 1 );
        BaseWidgetUtils.createSpacer( composite, 1 );
        Group visibleAttributesGroup = BaseWidgetUtils.createGroup( BaseWidgetUtils.createColumnContainer( composite,
            1, 1 ), "Visible Attributes", 1 );
        Composite visibleAttributesComposite = BaseWidgetUtils.createColumnContainer( visibleAttributesGroup, 1, 1 );
        showObjectClassAttributeButton = BaseWidgetUtils.createCheckbox( visibleAttributesComposite,
            "Show objectClass attribute", 1 );
        showObjectClassAttributeButton.setSelection( getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_SHOW_OBJECTCLASS_ATTRIBUTES ) );
        showMustAttributesButton = BaseWidgetUtils.createCheckbox( visibleAttributesComposite, "Show must attributes",
            1 );
        showMustAttributesButton.setSelection( getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_SHOW_MUST_ATTRIBUTES ) );
        showMayAttributesButton = BaseWidgetUtils.createCheckbox( visibleAttributesComposite, "Show may attributes", 1 );
        showMayAttributesButton.setSelection( getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_SHOW_MAY_ATTRIBUTES ) );
        showOperationalAttributesButton = BaseWidgetUtils.createCheckbox( visibleAttributesComposite,
            "Show operational attributes", 1 );
        showOperationalAttributesButton.setSelection( getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_SHOW_OPERATIONAL_ATTRIBUTES ) );

        BaseWidgetUtils.createSpacer( composite, 1 );
        BaseWidgetUtils.createSpacer( composite, 1 );
        String foldingTooltip = "If an attribute has more than the specified number of values it will be folded to one line. You may expand and collapse the values.";
        Group foldingGroup = BaseWidgetUtils.createGroup( BaseWidgetUtils.createColumnContainer( composite, 1, 1 ),
            "Folding", 1 );
        Composite pagingGroupComposite = BaseWidgetUtils.createColumnContainer( foldingGroup, 2, 1 );
        enableFoldingButton = BaseWidgetUtils.createCheckbox( pagingGroupComposite, "Enable folding", 2 );
        enableFoldingButton.setToolTipText( foldingTooltip );
        enableFoldingButton.setSelection( getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_ENABLE_FOLDING ) );
        enableFoldingButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                updateEnabled();
            }
        } );
        foldingThresholdLabel = BaseWidgetUtils.createLabel( pagingGroupComposite, "Folding threshold: ", 1 );
        foldingThresholdLabel.setToolTipText( foldingTooltip );
        foldingThresholdLabel.setEnabled( enableFoldingButton.getSelection() );
        foldingThresholdText = BaseWidgetUtils.createText( pagingGroupComposite, getPreferenceStore().getString(
            BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_FOLDING_THRESHOLD ), 4, 1 );
        foldingThresholdText.setToolTipText( foldingTooltip );
        foldingThresholdText.setEnabled( enableFoldingButton.getSelection() );
        foldingThresholdText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "[0-9]*" ) )
                {
                    e.doit = false;
                }
                if ( "".equals( foldingThresholdText.getText() ) && e.text.matches( "[0]" ) )
                {
                    e.doit = false;
                }
            }
        } );

        updateEnabled();

        applyDialogFont( composite );

        return composite;
    }


    private void updateEnabled()
    {
        foldingThresholdText.setEnabled( enableFoldingButton.getSelection() );
        foldingThresholdLabel.setEnabled( enableFoldingButton.getSelection() );
    }


    public boolean performOk()
    {

        getPreferenceStore().setValue( BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_SHOW_OBJECTCLASS_ATTRIBUTES,
            this.showObjectClassAttributeButton.getSelection() );
        getPreferenceStore().setValue( BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_SHOW_MUST_ATTRIBUTES,
            this.showMustAttributesButton.getSelection() );
        getPreferenceStore().setValue( BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_SHOW_MAY_ATTRIBUTES,
            this.showMayAttributesButton.getSelection() );
        getPreferenceStore().setValue( BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_SHOW_OPERATIONAL_ATTRIBUTES,
            this.showOperationalAttributesButton.getSelection() );

        getPreferenceStore().setValue( BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_ENABLE_FOLDING,
            this.enableFoldingButton.getSelection() );
        getPreferenceStore().setValue( BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_FOLDING_THRESHOLD,
            this.foldingThresholdText.getText() );

        return true;
    }


    protected void performDefaults()
    {

        this.showObjectClassAttributeButton.setSelection( getPreferenceStore().getDefaultBoolean(
            BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_SHOW_OBJECTCLASS_ATTRIBUTES ) );
        this.showMustAttributesButton.setSelection( getPreferenceStore().getDefaultBoolean(
            BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_SHOW_MUST_ATTRIBUTES ) );
        this.showMayAttributesButton.setSelection( getPreferenceStore().getDefaultBoolean(
            BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_SHOW_MAY_ATTRIBUTES ) );
        this.showOperationalAttributesButton.setSelection( getPreferenceStore().getDefaultBoolean(
            BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_SHOW_OPERATIONAL_ATTRIBUTES ) );

        this.foldingThresholdText.setText( getPreferenceStore().getDefaultString(
            BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_FOLDING_THRESHOLD ) );

        updateEnabled();

        super.performDefaults();
    }

}
