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

package org.apache.directory.ldapstudio.browser.ui.widgets.browser;


import org.apache.directory.ldapstudio.browser.core.BrowserCoreConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.widgets.BaseWidgetUtils;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class BrowserSorterDialog extends Dialog
{

    public static final String DIALOG_TITLE = "Browser Sorting";

    public static final String SORT_BY_NONE = "No Sorting";

    public static final String SORT_BY_RDN = "RDN";

    public static final String SORT_BY_RDN_VALUE = "RDN Value";

    private BrowserPreferences preferences;

    private Combo sortByCombo;

    private Button sortAcendingButton;

    private Button sortDescendingButton;

    private Button leafEntriesFirstButton;

    private Button metaEntriesLastButton;

    private Text sortLimitText;


    public BrowserSorterDialog( Shell parentShell, BrowserPreferences preferences )
    {
        super( parentShell );
        this.preferences = preferences;
    }


    protected void configureShell( Shell newShell )
    {
        super.configureShell( newShell );
        newShell.setText( DIALOG_TITLE );
    }


    protected void buttonPressed( int buttonId )
    {
        if ( buttonId == IDialogConstants.OK_ID )
        {
            int sortLimit = this.preferences.getSortLimit();
            try
            {
                sortLimit = Integer.parseInt( this.sortLimitText.getText().trim() );
            }
            catch ( NumberFormatException nfe )
            {
            }

            IPreferenceStore store = BrowserUIPlugin.getDefault().getPreferenceStore();
            store.setValue( BrowserUIConstants.PREFERENCE_BROWSER_LEAF_ENTRIES_FIRST, this.leafEntriesFirstButton
                .getSelection() );
            store.setValue( BrowserUIConstants.PREFERENCE_BROWSER_META_ENTRIES_LAST, this.metaEntriesLastButton
                .getSelection() );
            store.setValue( BrowserUIConstants.PREFERENCE_BROWSER_SORT_LIMIT, sortLimit );
            store.setValue( BrowserUIConstants.PREFERENCE_BROWSER_SORT_ORDER,
                this.sortDescendingButton.getSelection() ? BrowserCoreConstants.SORT_ORDER_DESCENDING
                    : BrowserCoreConstants.SORT_ORDER_ASCENDING );
            store.setValue( BrowserUIConstants.PREFERENCE_BROWSER_SORT_BY,
                this.sortByCombo.getSelectionIndex() == 2 ? BrowserCoreConstants.SORT_BY_RDN_VALUE : this.sortByCombo
                    .getSelectionIndex() == 1 ? BrowserCoreConstants.SORT_BY_RDN : BrowserCoreConstants.SORT_BY_NONE );
        }
        else
        {
            // no changes
        }

        super.buttonPressed( buttonId );
    }


    protected Control createDialogArea( Composite parent )
    {

        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        composite.setLayoutData( gd );

        Group groupingGroup = BaseWidgetUtils.createGroup( composite, "Group entries", 1 );

        leafEntriesFirstButton = BaseWidgetUtils.createCheckbox( groupingGroup, "Leaf enties first", 1 );
        leafEntriesFirstButton
            .setToolTipText( "This option displays entries without children before entries with children." );
        leafEntriesFirstButton.setSelection( this.preferences.isLeafEntriesFirst() );

        metaEntriesLastButton = BaseWidgetUtils.createCheckbox( groupingGroup, "Meta entries last", 1 );
        metaEntriesLastButton
            .setToolTipText( "This option displays meta entries after normal entries. Meta entries are e.g. the root DSE or the schema entry." );
        metaEntriesLastButton.setSelection( this.preferences.isMetaEntriesLast() );

        Group sortingGroup = BaseWidgetUtils.createGroup( composite, "Sort entries", 1 );

        Composite sortByComposite = BaseWidgetUtils.createColumnContainer( sortingGroup, 4, 1 );
        BaseWidgetUtils.createLabel( sortByComposite, "Sort by", 1 );
        sortByCombo = BaseWidgetUtils.createReadonlyCombo( sortByComposite, new String[]
            { SORT_BY_NONE, SORT_BY_RDN, SORT_BY_RDN_VALUE }, 0, 1 );
        sortByCombo.select( this.preferences.getSortBy() == BrowserCoreConstants.SORT_BY_RDN_VALUE ? 2
            : this.preferences.getSortBy() == BrowserCoreConstants.SORT_BY_RDN ? 1 : 0 );
        sortByCombo.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                sortAcendingButton.setEnabled( sortByCombo.getSelectionIndex() != 0 );
                sortDescendingButton.setEnabled( sortByCombo.getSelectionIndex() != 0 );
                sortLimitText.setEnabled( sortByCombo.getSelectionIndex() != 0 );
            }
        } );

        sortAcendingButton = BaseWidgetUtils.createRadiobutton( sortByComposite, "Ascending", 1 );
        sortAcendingButton.setSelection( this.preferences.getSortOrder() == BrowserCoreConstants.SORT_ORDER_ASCENDING );
        sortAcendingButton.setEnabled( sortByCombo.getSelectionIndex() != 0 );

        sortDescendingButton = BaseWidgetUtils.createRadiobutton( sortByComposite, "Descending", 1 );
        sortDescendingButton
            .setSelection( this.preferences.getSortOrder() == BrowserCoreConstants.SORT_ORDER_DESCENDING );
        sortDescendingButton.setEnabled( sortByCombo.getSelectionIndex() != 0 );

        Composite sortLimitComposite = BaseWidgetUtils.createColumnContainer( sortingGroup, 2, 1 );
        String sortLimitTooltip = "If there are more than the specified number of children they won't be sorted. Hint: For performance reason the maximum value should be 10.000!";
        Label sortLimitLabel = BaseWidgetUtils.createLabel( sortLimitComposite, "Sort limit:", 1 );
        sortLimitLabel.setToolTipText( sortLimitTooltip );
        sortLimitText = BaseWidgetUtils.createText( sortLimitComposite, "" + this.preferences.getSortLimit(), 5, 1 );
        sortLimitText.setToolTipText( sortLimitTooltip );
        sortLimitText.setEnabled( sortByCombo.getSelectionIndex() != 0 );
        sortLimitText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "[0-9]*" ) )
                {
                    e.doit = false;
                }
            }
        } );

        applyDialogFont( composite );
        return composite;
    }

}
