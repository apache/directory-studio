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

package org.apache.directory.studio.ldapbrowser.common.widgets.browser;


import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.common.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
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


/**
 * This class represents the dialog used to change the browser's sort settings.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BrowserSorterDialog extends Dialog
{

    /** The dialog title. */
    public static final String DIALOG_TITLE = "Browser Sorting";

    /** The Constant SORT_BY_NONE. */
    public static final String SORT_BY_NONE = "No Sorting";

    /** The Constant SORT_BY_RDN. */
    public static final String SORT_BY_RDN = "RDN";

    /** The Constant SORT_BY_RDN_VALUE. */
    public static final String SORT_BY_RDN_VALUE = "RDN Value";

    /** The browser preferences. */
    private BrowserPreferences preferences;

    /** The sort by combo. */
    private Combo sortByCombo;

    /** The sort acending button. */
    private Button sortAcendingButton;

    /** The sort descending button. */
    private Button sortDescendingButton;

    /** The leaf entries first button. */
    private Button leafEntriesFirstButton;

    /** The container entries first button. */
    private Button containerEntriesFirstButton;

    /** The mixed button. */
    private Button mixedButton;

    /** The meta entries last button. */
    private Button metaEntriesLastButton;

    /** The sort limit text. */
    private Text sortLimitText;


    /**
     * Creates a new instance of BrowserSorterDialog.
     *
     * @param parentShell the parent shell
     * @param preferences the browser preferences
     */
    public BrowserSorterDialog( Shell parentShell, BrowserPreferences preferences )
    {
        super( parentShell );
        this.preferences = preferences;
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation calls its super implementation and sets the dialog title.
     */
    protected void configureShell( Shell newShell )
    {
        super.configureShell( newShell );
        newShell.setText( DIALOG_TITLE );
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation save the changed settings when OK is pressed.
     */
    protected void buttonPressed( int buttonId )
    {
        if ( buttonId == IDialogConstants.OK_ID )
        {
            int sortLimit = preferences.getSortLimit();
            try
            {
                sortLimit = Integer.parseInt( sortLimitText.getText().trim() );
            }
            catch ( NumberFormatException nfe )
            {
            }

            IPreferenceStore store = BrowserCommonActivator.getDefault().getPreferenceStore();
            store.setValue( BrowserCommonConstants.PREFERENCE_BROWSER_LEAF_ENTRIES_FIRST, leafEntriesFirstButton
                .getSelection() );
            store.setValue( BrowserCommonConstants.PREFERENCE_BROWSER_CONTAINER_ENTRIES_FIRST,
                containerEntriesFirstButton.getSelection() );
            store.setValue( BrowserCommonConstants.PREFERENCE_BROWSER_META_ENTRIES_LAST, metaEntriesLastButton
                .getSelection() );
            store.setValue( BrowserCommonConstants.PREFERENCE_BROWSER_SORT_LIMIT, sortLimit );
            store.setValue( BrowserCommonConstants.PREFERENCE_BROWSER_SORT_ORDER,
                sortDescendingButton.getSelection() ? BrowserCoreConstants.SORT_ORDER_DESCENDING
                    : BrowserCoreConstants.SORT_ORDER_ASCENDING );
            store.setValue( BrowserCommonConstants.PREFERENCE_BROWSER_SORT_BY,
                sortByCombo.getSelectionIndex() == 2 ? BrowserCoreConstants.SORT_BY_RDN_VALUE : sortByCombo
                    .getSelectionIndex() == 1 ? BrowserCoreConstants.SORT_BY_RDN : BrowserCoreConstants.SORT_BY_NONE );
        }
        else
        {
            // no changes
        }

        super.buttonPressed( buttonId );
    }


    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea( Composite parent )
    {

        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        composite.setLayoutData( gd );

        Group groupingGroup = BaseWidgetUtils.createGroup( composite, "Group entries", 1 );
        Composite columGroup = BaseWidgetUtils.createColumnContainer( groupingGroup, 3, 1 );

        leafEntriesFirstButton = BaseWidgetUtils.createRadiobutton( columGroup, "Leaf entries first", 1 );
        leafEntriesFirstButton
            .setToolTipText( "This option displays entries without children before entries with children." );
        leafEntriesFirstButton.setSelection( preferences.isLeafEntriesFirst() );

        containerEntriesFirstButton = BaseWidgetUtils.createRadiobutton( columGroup, "Container entries first", 1 );
        containerEntriesFirstButton
        .setToolTipText( "This option displays entries with children before entries without children." );
        containerEntriesFirstButton.setSelection( preferences.isContainerEntriesFirst() );
        
        mixedButton = BaseWidgetUtils.createRadiobutton( columGroup, "Mixed", 1 );
        mixedButton.setToolTipText( "This option displays entries with and without children mixed." );
        mixedButton.setSelection( !preferences.isLeafEntriesFirst() && !preferences.isContainerEntriesFirst() );

        metaEntriesLastButton = BaseWidgetUtils.createCheckbox( groupingGroup, "Meta entries last", 1 );
        metaEntriesLastButton
            .setToolTipText( "This option displays meta entries after normal entries. Meta entries are e.g. the root DSE or the schema entry." );
        metaEntriesLastButton.setSelection( preferences.isMetaEntriesLast() );

        Group sortingGroup = BaseWidgetUtils.createGroup( composite, "Sort entries", 1 );

        Composite sortByComposite = BaseWidgetUtils.createColumnContainer( sortingGroup, 4, 1 );
        BaseWidgetUtils.createLabel( sortByComposite, "Sort by", 1 );
        sortByCombo = BaseWidgetUtils.createReadonlyCombo( sortByComposite, new String[]
            { SORT_BY_NONE, SORT_BY_RDN, SORT_BY_RDN_VALUE }, 0, 1 );
        sortByCombo.select( preferences.getSortBy() == BrowserCoreConstants.SORT_BY_RDN_VALUE ? 2 : preferences
            .getSortBy() == BrowserCoreConstants.SORT_BY_RDN ? 1 : 0 );
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
        sortAcendingButton.setSelection( preferences.getSortOrder() == BrowserCoreConstants.SORT_ORDER_ASCENDING );
        sortAcendingButton.setEnabled( sortByCombo.getSelectionIndex() != 0 );

        sortDescendingButton = BaseWidgetUtils.createRadiobutton( sortByComposite, "Descending", 1 );
        sortDescendingButton.setSelection( preferences.getSortOrder() == BrowserCoreConstants.SORT_ORDER_DESCENDING );
        sortDescendingButton.setEnabled( sortByCombo.getSelectionIndex() != 0 );

        Composite sortLimitComposite = BaseWidgetUtils.createColumnContainer( sortingGroup, 2, 1 );
        String sortLimitTooltip = "If there are more than the specified number of children they won't be sorted. Hint: For performance reason the maximum value should be 10.000!";
        Label sortLimitLabel = BaseWidgetUtils.createLabel( sortLimitComposite, "Sort limit:", 1 );
        sortLimitLabel.setToolTipText( sortLimitTooltip );
        sortLimitText = BaseWidgetUtils.createText( sortLimitComposite, "" + preferences.getSortLimit(), 5, 1 );
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
