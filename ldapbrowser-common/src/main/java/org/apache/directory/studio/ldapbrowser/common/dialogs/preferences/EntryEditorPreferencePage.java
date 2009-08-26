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


import org.apache.directory.studio.connection.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
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


/**
 * The EntryEditorPreferencePage contains general settings for the entry editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class EntryEditorPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

    private Button showObjectClassAttributeButton;

    private Button showMustAttributesButton;

    private Button showMayAttributesButton;

    private Button showOperationalAttributesButton;

    private Button enableFoldingButton;

    private Label foldingThresholdLabel;

    private Text foldingThresholdText;

    private Button autoExpandFoldedAttributesButton;


    /**
     * Creates a new instance of EntryEditorPreferencePage.
     */
    public EntryEditorPreferencePage()
    {
        super( Messages.getString("EntryEditorPreferencePage.EntryEditor") ); //$NON-NLS-1$
        super.setPreferenceStore( BrowserCommonActivator.getDefault().getPreferenceStore() );
        super.setDescription( Messages.getString("EntryEditorPreferencePage.GeneralSettings") ); //$NON-NLS-1$
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
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        BaseWidgetUtils.createSpacer( composite, 1 );
        BaseWidgetUtils.createSpacer( composite, 1 );
        Group visibleAttributesGroup = BaseWidgetUtils.createGroup( BaseWidgetUtils.createColumnContainer( composite,
            1, 1 ), Messages.getString("EntryEditorPreferencePage.VisibleAttributes"), 1 ); //$NON-NLS-1$
        Composite visibleAttributesComposite = BaseWidgetUtils.createColumnContainer( visibleAttributesGroup, 1, 1 );
        showObjectClassAttributeButton = BaseWidgetUtils.createCheckbox( visibleAttributesComposite,
            Messages.getString("EntryEditorPreferencePage.ShowObjectClassAttributes"), 1 ); //$NON-NLS-1$
        showObjectClassAttributeButton.setSelection( getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_SHOW_OBJECTCLASS_ATTRIBUTES ) );
        showMustAttributesButton = BaseWidgetUtils.createCheckbox( visibleAttributesComposite, Messages.getString("EntryEditorPreferencePage.ShowMustAttributes"), //$NON-NLS-1$
            1 );
        showMustAttributesButton.setSelection( getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_SHOW_MUST_ATTRIBUTES ) );
        showMayAttributesButton = BaseWidgetUtils.createCheckbox( visibleAttributesComposite, Messages.getString("EntryEditorPreferencePage.ShowMayAttributes"), 1 ); //$NON-NLS-1$
        showMayAttributesButton.setSelection( getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_SHOW_MAY_ATTRIBUTES ) );
        showOperationalAttributesButton = BaseWidgetUtils.createCheckbox( visibleAttributesComposite,
            Messages.getString("EntryEditorPreferencePage.ShowOperationalAttributes"), 1 ); //$NON-NLS-1$
        showOperationalAttributesButton.setSelection( getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_SHOW_OPERATIONAL_ATTRIBUTES ) );
        BaseWidgetUtils.createWrappedLabel( visibleAttributesComposite, Messages
            .getString( "EntryEditorPreferencePage.ShowOperationalAttributesToolTip" ), 1 );

        BaseWidgetUtils.createSpacer( composite, 1 );
        BaseWidgetUtils.createSpacer( composite, 1 );
        String foldingTooltip = Messages.getString("EntryEditorPreferencePage.FoldingToolTip"); //$NON-NLS-1$
        Group foldingGroup = BaseWidgetUtils.createGroup( BaseWidgetUtils.createColumnContainer( composite, 1, 1 ),
            Messages.getString("EntryEditorPreferencePage.Folding"), 1 ); //$NON-NLS-1$
        Composite pagingGroupComposite = BaseWidgetUtils.createColumnContainer( foldingGroup, 3, 1 );
        enableFoldingButton = BaseWidgetUtils.createCheckbox( pagingGroupComposite, Messages.getString("EntryEditorPreferencePage.EnableFolding"), 3 ); //$NON-NLS-1$
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
        BaseWidgetUtils.createRadioIndent( pagingGroupComposite, 1 );
        foldingThresholdLabel = BaseWidgetUtils.createLabel( pagingGroupComposite, Messages.getString("EntryEditorPreferencePage.FoldingThreshold"), 1 ); //$NON-NLS-1$
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
                if ( !e.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
                {
                    e.doit = false;
                }
                if ( "".equals( foldingThresholdText.getText() ) && e.text.matches( "[0]" ) ) //$NON-NLS-1$ //$NON-NLS-2$
                {
                    e.doit = false;
                }
            }
        } );
        BaseWidgetUtils.createRadioIndent( pagingGroupComposite, 1 );
        autoExpandFoldedAttributesButton = BaseWidgetUtils.createCheckbox( pagingGroupComposite,
            Messages.getString("EntryEditorPreferencePage.AutoExpandFoldedAttributes"), 2 ); //$NON-NLS-1$
        autoExpandFoldedAttributesButton.setEnabled( enableFoldingButton.getSelection() );
        autoExpandFoldedAttributesButton.setSelection( getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_AUTO_EXPAND_FOLDED_ATTRIBUTES ) );

        updateEnabled();

        applyDialogFont( composite );

        return composite;
    }


    private void updateEnabled()
    {
        foldingThresholdText.setEnabled( enableFoldingButton.getSelection() );
        foldingThresholdLabel.setEnabled( enableFoldingButton.getSelection() );
        autoExpandFoldedAttributesButton.setEnabled( enableFoldingButton.getSelection() );
    }


    /**
     * {@inheritDoc}
     */
    public boolean performOk()
    {
        getPreferenceStore().setValue( BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_SHOW_OBJECTCLASS_ATTRIBUTES,
            showObjectClassAttributeButton.getSelection() );
        getPreferenceStore().setValue( BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_SHOW_MUST_ATTRIBUTES,
            showMustAttributesButton.getSelection() );
        getPreferenceStore().setValue( BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_SHOW_MAY_ATTRIBUTES,
            showMayAttributesButton.getSelection() );
        getPreferenceStore().setValue( BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_SHOW_OPERATIONAL_ATTRIBUTES,
            showOperationalAttributesButton.getSelection() );

        getPreferenceStore().setValue( BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_ENABLE_FOLDING,
            enableFoldingButton.getSelection() );
        getPreferenceStore().setValue( BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_FOLDING_THRESHOLD,
            foldingThresholdText.getText() );
        getPreferenceStore().setValue( BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_AUTO_EXPAND_FOLDED_ATTRIBUTES,
            autoExpandFoldedAttributesButton.getSelection() );

        return true;
    }


    /**
     * {@inheritDoc}
     */
    protected void performDefaults()
    {
        showObjectClassAttributeButton.setSelection( getPreferenceStore().getDefaultBoolean(
            BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_SHOW_OBJECTCLASS_ATTRIBUTES ) );
        showMustAttributesButton.setSelection( getPreferenceStore().getDefaultBoolean(
            BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_SHOW_MUST_ATTRIBUTES ) );
        showMayAttributesButton.setSelection( getPreferenceStore().getDefaultBoolean(
            BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_SHOW_MAY_ATTRIBUTES ) );
        showOperationalAttributesButton.setSelection( getPreferenceStore().getDefaultBoolean(
            BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_SHOW_OPERATIONAL_ATTRIBUTES ) );

        enableFoldingButton.setSelection( getPreferenceStore().getDefaultBoolean(
            BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_ENABLE_FOLDING ) );
        foldingThresholdText.setText( getPreferenceStore().getDefaultString(
            BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_FOLDING_THRESHOLD ) );
        autoExpandFoldedAttributesButton.setSelection( getPreferenceStore().getDefaultBoolean(
            BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_AUTO_EXPAND_FOLDED_ATTRIBUTES ) );

        updateEnabled();

        super.performDefaults();
    }

}
