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


import org.apache.directory.studio.ldapbrowser.common.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldifeditor.LdifEditorActivator;
import org.apache.directory.studio.ldifeditor.LdifEditorConstants;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


public class LdifEditorContentAssistPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

    private Button insertSingleProposalAutoButton;

    private Button enableAutoActivationButton;

    private Label autoActivationDelayLabel;

    private Text autoActivationDelayText;

    private Label autoActivationDelayMs;

    private Button smartInsertAttributeInModspecButton;


    public LdifEditorContentAssistPreferencePage()
    {
        super( "Content Assist" );
        super.setPreferenceStore( LdifEditorActivator.getDefault().getPreferenceStore() );
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
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        composite.setLayoutData( gd );

        BaseWidgetUtils.createSpacer( composite, 1 );
        BaseWidgetUtils.createSpacer( composite, 1 );

        Group caGroup = BaseWidgetUtils.createGroup( BaseWidgetUtils.createColumnContainer( composite, 1, 1 ),
            "Content Assist", 1 );

        insertSingleProposalAutoButton = BaseWidgetUtils.createCheckbox( caGroup,
            "Insert single proposal automatically", 1 );
        insertSingleProposalAutoButton.setSelection( getPreferenceStore().getBoolean(
            LdifEditorConstants.PREFERENCE_LDIFEDITOR_CONTENTASSIST_INSERTSINGLEPROPOSALAUTO ) );

        enableAutoActivationButton = BaseWidgetUtils.createCheckbox( caGroup, "Enable auto activation", 1 );
        enableAutoActivationButton.setSelection( getPreferenceStore().getBoolean(
            LdifEditorConstants.PREFERENCE_LDIFEDITOR_CONTENTASSIST_ENABLEAUTOACTIVATION ) );
        enableAutoActivationButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                checkEnabled();
            }
        } );

        Composite autoActivationDelayComposite = BaseWidgetUtils.createColumnContainer( caGroup, 4, 1 );
        BaseWidgetUtils.createRadioIndent( autoActivationDelayComposite, 1 );
        autoActivationDelayLabel = BaseWidgetUtils.createLabel( autoActivationDelayComposite, "Auto activation delay:",
            1 );
        autoActivationDelayText = BaseWidgetUtils.createText( autoActivationDelayComposite, "", 4, 1 );
        autoActivationDelayText.setText( getPreferenceStore().getString(
            LdifEditorConstants.PREFERENCE_LDIFEDITOR_CONTENTASSIST_AUTOACTIVATIONDELAY ) );
        autoActivationDelayText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "[0-9]*" ) )
                {
                    e.doit = false;
                }
                if ( "".equals( autoActivationDelayText.getText() ) && e.text.matches( "[0]" ) )
                {
                    e.doit = false;
                }
            }
        } );
        autoActivationDelayMs = BaseWidgetUtils.createLabel( autoActivationDelayComposite, "ms", 1 );

        smartInsertAttributeInModspecButton = BaseWidgetUtils.createCheckbox( caGroup,
            "Smart insert attribute name in modification items", 1 );
        smartInsertAttributeInModspecButton.setSelection( getPreferenceStore().getBoolean(
            LdifEditorConstants.PREFERENCE_LDIFEDITOR_CONTENTASSIST_SMARTINSERTATTRIBUTEINMODSPEC ) );
        BaseWidgetUtils.createLabel( caGroup, "TODO: Smart insert must attributes", 1 );

        checkEnabled();

        return composite;
    }


    private void checkEnabled()
    {
        autoActivationDelayLabel.setEnabled( enableAutoActivationButton.getSelection() );
        autoActivationDelayText.setEnabled( enableAutoActivationButton.getSelection() );
        autoActivationDelayMs.setEnabled( enableAutoActivationButton.getSelection() );
    }


    public boolean performOk()
    {

        getPreferenceStore().setValue( LdifEditorConstants.PREFERENCE_LDIFEDITOR_CONTENTASSIST_INSERTSINGLEPROPOSALAUTO,
            this.insertSingleProposalAutoButton.getSelection() );
        getPreferenceStore().setValue( LdifEditorConstants.PREFERENCE_LDIFEDITOR_CONTENTASSIST_ENABLEAUTOACTIVATION,
            this.enableAutoActivationButton.getSelection() );
        getPreferenceStore().setValue( LdifEditorConstants.PREFERENCE_LDIFEDITOR_CONTENTASSIST_AUTOACTIVATIONDELAY,
            this.autoActivationDelayText.getText() );
        getPreferenceStore().setValue(
            LdifEditorConstants.PREFERENCE_LDIFEDITOR_CONTENTASSIST_SMARTINSERTATTRIBUTEINMODSPEC,
            this.smartInsertAttributeInModspecButton.getSelection() );

        BrowserCorePlugin.getDefault().savePluginPreferences();

        return true;
    }


    protected void performDefaults()
    {

        insertSingleProposalAutoButton.setSelection( getPreferenceStore().getDefaultBoolean(
            LdifEditorConstants.PREFERENCE_LDIFEDITOR_CONTENTASSIST_INSERTSINGLEPROPOSALAUTO ) );
        enableAutoActivationButton.setSelection( getPreferenceStore().getDefaultBoolean(
            LdifEditorConstants.PREFERENCE_LDIFEDITOR_CONTENTASSIST_ENABLEAUTOACTIVATION ) );
        autoActivationDelayText.setText( getPreferenceStore().getDefaultString(
            LdifEditorConstants.PREFERENCE_LDIFEDITOR_CONTENTASSIST_AUTOACTIVATIONDELAY ) );
        smartInsertAttributeInModspecButton.setSelection( getPreferenceStore().getDefaultBoolean(
            LdifEditorConstants.PREFERENCE_LDIFEDITOR_CONTENTASSIST_SMARTINSERTATTRIBUTEINMODSPEC ) );

        super.performDefaults();

        checkEnabled();
    }

}
