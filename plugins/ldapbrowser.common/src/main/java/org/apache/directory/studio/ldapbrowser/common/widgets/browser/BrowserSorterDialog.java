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


import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
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
 */
public class BrowserSorterDialog extends Dialog
{
    /** The dialog title. */
    public static final String DIALOG_TITLE = Messages.getString( "BrowserSorterDialog.BrowserSorting" ); //$NON-NLS-1$

    /** The Constant SORT_BY_NONE. */
    public static final String SORT_BY_NONE = Messages.getString( "BrowserSorterDialog.NoSorting" ); //$NON-NLS-1$

    /** The Constant SORT_BY_RDN. */
    public static final String SORT_BY_RDN = Messages.getString( "BrowserSorterDialog.RDN" ); //$NON-NLS-1$

    /** The Constant SORT_BY_RDN_VALUE. */
    public static final String SORT_BY_RDN_VALUE = Messages.getString( "BrowserSorterDialog.RDNValue" ); //$NON-NLS-1$

    /** The browser preferences. */
    private BrowserPreferences preferences;

    /** The leaf entries first button. */
    private Button leafEntriesFirstButton;

    /** The container entries first button. */
    private Button containerEntriesFirstButton;

    /** The mixed button. */
    private Button mixedButton;

    /** The meta entries last button. */
    private Button metaEntriesLastButton;

    /** The sort entries by combo. */
    private Combo sortEntriesByCombo;

    /** The sort entries ascending button. */
    private Button sortEntriesAscendingButton;

    /** The sort entries descending button. */
    private Button sortEntriesDescendingButton;

    /** The sort searches ascending button. */
    private Button sortSearchesAscendingButton;

    /** The sort searches descending button. */
    private Button sortSearchesDescendingButton;

    /** The sort searches none button. */
    private Button sortSearchesNoSortingButton;

    /** The sort bookmarks ascending button. */
    private Button sortBookmarksAscendingButton;

    /** The sort bookmarks descending button. */
    private Button sortBookmarksDescendingButton;

    /** The sort bookmarks none button. */
    private Button sortBookmarksNoSortingButton;

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

            store.setValue( BrowserCommonConstants.PREFERENCE_BROWSER_SORT_ORDER,
                sortEntriesDescendingButton.getSelection() ? BrowserCoreConstants.SORT_ORDER_DESCENDING
                    : BrowserCoreConstants.SORT_ORDER_ASCENDING );
            store.setValue( BrowserCommonConstants.PREFERENCE_BROWSER_SORT_BY,
                sortEntriesByCombo.getSelectionIndex() == 2 ? BrowserCoreConstants.SORT_BY_RDN_VALUE
                    : sortEntriesByCombo
                        .getSelectionIndex() == 1 ? BrowserCoreConstants.SORT_BY_RDN
                        : BrowserCoreConstants.SORT_BY_NONE );
            store.setValue( BrowserCommonConstants.PREFERENCE_BROWSER_SORT_LIMIT, sortLimit );

            if ( sortSearchesAscendingButton.getSelection() )
            {
                store.setValue( BrowserCommonConstants.PREFERENCE_BROWSER_SORT_SEARCHES_ORDER,
                    BrowserCoreConstants.SORT_ORDER_ASCENDING );
            }
            else if ( sortSearchesDescendingButton.getSelection() )
            {
                store.setValue( BrowserCommonConstants.PREFERENCE_BROWSER_SORT_SEARCHES_ORDER,
                    BrowserCoreConstants.SORT_ORDER_DESCENDING );
            }
            else if ( sortSearchesNoSortingButton.getSelection() )
            {
                store.setValue( BrowserCommonConstants.PREFERENCE_BROWSER_SORT_SEARCHES_ORDER,
                    BrowserCoreConstants.SORT_ORDER_NONE );
            }

            if ( sortBookmarksAscendingButton.getSelection() )
            {
                store.setValue( BrowserCommonConstants.PREFERENCE_BROWSER_SORT_BOOKMARKS_ORDER,
                    BrowserCoreConstants.SORT_ORDER_ASCENDING );
            }
            else if ( sortBookmarksDescendingButton.getSelection() )
            {
                store.setValue( BrowserCommonConstants.PREFERENCE_BROWSER_SORT_BOOKMARKS_ORDER,
                    BrowserCoreConstants.SORT_ORDER_DESCENDING );
            }
            else if ( sortBookmarksNoSortingButton.getSelection() )
            {
                store.setValue( BrowserCommonConstants.PREFERENCE_BROWSER_SORT_BOOKMARKS_ORDER,
                    BrowserCoreConstants.SORT_ORDER_NONE );
            }
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

        createGroupEntriesGroup( composite );
        createSortEntriesGroup( composite );
        createSortSearchesGroup( composite );
        createSortBookmarksGroup( composite );
        createSortLimitGroup( composite );

        applyDialogFont( composite );
        return composite;
    }


    /**
     * Creates the group entries group.
     *
     * @param composite the parent composite
     */
    private void createGroupEntriesGroup( Composite composite )
    {
        // Group entries group and composite
        Group groupEntriesGroup = BaseWidgetUtils.createGroup( composite, Messages
            .getString( "BrowserSorterDialog.GroupEntries" ), 1 ); //$NON-NLS-1$
        Composite groupEntriesButtonsComposite = BaseWidgetUtils.createColumnContainer( groupEntriesGroup, 3, 1 );

        // Leaf entries first button
        leafEntriesFirstButton = BaseWidgetUtils.createRadiobutton( groupEntriesButtonsComposite, Messages
            .getString( "BrowserSorterDialog.LeafEntriesFirst" ), 1 ); //$NON-NLS-1$
        leafEntriesFirstButton.setToolTipText( Messages.getString( "BrowserSorterDialog.LeafEntriesFirstToolTip" ) ); //$NON-NLS-1$
        leafEntriesFirstButton.setSelection( preferences.isLeafEntriesFirst() );

        // Container entries first button
        containerEntriesFirstButton = BaseWidgetUtils.createRadiobutton( groupEntriesButtonsComposite, Messages
            .getString( "BrowserSorterDialog.ContainerEntriesFirst" ), 1 ); //$NON-NLS-1$
        containerEntriesFirstButton.setToolTipText( Messages
            .getString( "BrowserSorterDialog.ContainerEntriesFirstToolTip" ) ); //$NON-NLS-1$
        containerEntriesFirstButton.setSelection( preferences.isContainerEntriesFirst() );

        // Mixed button
        mixedButton = BaseWidgetUtils.createRadiobutton( groupEntriesButtonsComposite,
            Messages.getString( "BrowserSorterDialog.Mixed" ), 1 ); //$NON-NLS-1$
        mixedButton.setToolTipText( Messages.getString( "BrowserSorterDialog.MixedToolTip" ) ); //$NON-NLS-1$
        mixedButton.setSelection( !preferences.isLeafEntriesFirst() && !preferences.isContainerEntriesFirst() );

        // Meta entries last button
        metaEntriesLastButton = BaseWidgetUtils.createCheckbox( groupEntriesGroup, Messages
            .getString( "BrowserSorterDialog.MetaEntriesLast" ), 1 ); //$NON-NLS-1$
        metaEntriesLastButton.setToolTipText( Messages.getString( "BrowserSorterDialog.MetaEntriesLastToolTip" ) ); //$NON-NLS-1$
        metaEntriesLastButton.setSelection( preferences.isMetaEntriesLast() );
    }


    /**
     * Creates the sort entries group.
     *
     * @param composite the parent composite
     */
    private void createSortEntriesGroup( Composite composite )
    {
        // Sort entries group and composite
        Group sortEntriesGroup = BaseWidgetUtils.createGroup( composite, Messages
            .getString( "BrowserSorterDialog.SortEntries" ), 1 ); //$NON-NLS-1$
        Composite sortByComposite = BaseWidgetUtils.createColumnContainer( sortEntriesGroup, 4, 1 );

        // Sort entries by combo
        BaseWidgetUtils.createLabel( sortByComposite, Messages.getString( "BrowserSorterDialog.SortBy" ), 1 ); //$NON-NLS-1$
        sortEntriesByCombo = BaseWidgetUtils.createReadonlyCombo( sortByComposite, new String[]
            { SORT_BY_NONE, SORT_BY_RDN, SORT_BY_RDN_VALUE }, 0, 1 );
        sortEntriesByCombo.select( preferences.getSortEntriesBy() == BrowserCoreConstants.SORT_BY_RDN_VALUE ? 2
            : preferences
                .getSortEntriesBy() == BrowserCoreConstants.SORT_BY_RDN ? 1 : 0 );
        sortEntriesByCombo.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                sortEntriesAscendingButton.setEnabled( sortEntriesByCombo.getSelectionIndex() != 0 );
                sortEntriesDescendingButton.setEnabled( sortEntriesByCombo.getSelectionIndex() != 0 );
            }
        } );

        // Sort entries ascending button
        sortEntriesAscendingButton = BaseWidgetUtils.createRadiobutton( sortByComposite, Messages
            .getString( "BrowserSorterDialog.Ascending" ), 1 ); //$NON-NLS-1$
        sortEntriesAscendingButton
            .setSelection( preferences.getSortEntriesOrder() == BrowserCoreConstants.SORT_ORDER_ASCENDING );
        sortEntriesAscendingButton.setEnabled( sortEntriesByCombo.getSelectionIndex() != 0 );

        // Sort entries descending button
        sortEntriesDescendingButton = BaseWidgetUtils.createRadiobutton( sortByComposite, Messages
            .getString( "BrowserSorterDialog.Descending" ), 1 ); //$NON-NLS-1$
        sortEntriesDescendingButton
            .setSelection( preferences.getSortEntriesOrder() == BrowserCoreConstants.SORT_ORDER_DESCENDING );
        sortEntriesDescendingButton.setEnabled( sortEntriesByCombo.getSelectionIndex() != 0 );
    }


    /**
     * Creates the sort searches group.
     *
     * @param parent the parent composite
     */
    private void createSortSearchesGroup( Composite parent )
    {
        // Sort searches group and composite
        Group sortSearchesGroup = BaseWidgetUtils.createGroup( parent, Messages
            .getString( "BrowserSorterDialog.SortSearches" ), 1 ); //$NON-NLS-1$
        Composite sortSearchesComposite = BaseWidgetUtils.createColumnContainer( sortSearchesGroup, 3, 1 );

        // Sort searches ascending button
        sortSearchesAscendingButton = BaseWidgetUtils.createRadiobutton( sortSearchesComposite, Messages
            .getString( "BrowserSorterDialog.Ascending" ), 1 ); //$NON-NLS-1$
        sortSearchesAscendingButton
            .setSelection( preferences.getSortSearchesOrder() == BrowserCoreConstants.SORT_ORDER_ASCENDING );

        // Sort searches descending button
        sortSearchesDescendingButton = BaseWidgetUtils.createRadiobutton( sortSearchesComposite, Messages
            .getString( "BrowserSorterDialog.Descending" ), 1 ); //$NON-NLS-1$
        sortSearchesDescendingButton
            .setSelection( preferences.getSortSearchesOrder() == BrowserCoreConstants.SORT_ORDER_DESCENDING );

        // Sort searches none button
        sortSearchesNoSortingButton = BaseWidgetUtils.createRadiobutton( sortSearchesComposite, Messages
            .getString( "BrowserSorterDialog.NoSorting" ), 1 ); //$NON-NLS-1$
        sortSearchesNoSortingButton
            .setSelection( preferences.getSortSearchesOrder() == BrowserCoreConstants.SORT_ORDER_NONE );
    }


    /**
     * Creates the sort bookmarks group.
     *
     * @param parent the parent composite
     */
    private void createSortBookmarksGroup( Composite parent )
    {
        // Sort bookmarks group and composite
        Group sortBookmarksGroup = BaseWidgetUtils.createGroup( parent, Messages
            .getString( "BrowserSorterDialog.SortBookmarks" ), 1 ); //$NON-NLS-1$
        Composite sortBookmarksComposite = BaseWidgetUtils.createColumnContainer( sortBookmarksGroup, 3, 1 );

        // Sort bookmarks ascending button
        sortBookmarksAscendingButton = BaseWidgetUtils.createRadiobutton( sortBookmarksComposite, Messages
            .getString( "BrowserSorterDialog.Ascending" ), 1 ); //$NON-NLS-1$
        sortBookmarksAscendingButton
            .setSelection( preferences.getSortBookmarksOrder() == BrowserCoreConstants.SORT_ORDER_ASCENDING );

        // Sort bookmarks descending button
        sortBookmarksDescendingButton = BaseWidgetUtils.createRadiobutton( sortBookmarksComposite, Messages
            .getString( "BrowserSorterDialog.Descending" ), 1 ); //$NON-NLS-1$
        sortBookmarksDescendingButton
            .setSelection( preferences.getSortBookmarksOrder() == BrowserCoreConstants.SORT_ORDER_DESCENDING );

        // Sort bookmarks none button
        sortBookmarksNoSortingButton = BaseWidgetUtils.createRadiobutton( sortBookmarksComposite, Messages
            .getString( "BrowserSorterDialog.NoSorting" ), 1 ); //$NON-NLS-1$
        sortBookmarksNoSortingButton
            .setSelection( preferences.getSortBookmarksOrder() == BrowserCoreConstants.SORT_ORDER_NONE );
    }


    /**
     * Creates the sort limit group.
     *
     * @param composite the parent composite
     */
    private void createSortLimitGroup( Composite composite )
    {
        // Sort limit group and composite
        Group sortLimitGroup = BaseWidgetUtils.createGroup( composite, Messages
            .getString( "BrowserSorterDialog.SortLimit" ), 1 ); //$NON-NLS-1$
        Composite sortLimitComposite = BaseWidgetUtils.createColumnContainer( sortLimitGroup, 2, 1 );

        // Sort limit text
        String sortLimitTooltip = Messages.getString( "BrowserSorterDialog.SortLimitToolTip" ); //$NON-NLS-1$
        Label sortLimitLabel = BaseWidgetUtils.createLabel( sortLimitComposite, Messages
            .getString( "BrowserSorterDialog.SortLimitColon" ), 1 ); //$NON-NLS-1$
        sortLimitLabel.setToolTipText( sortLimitTooltip );
        sortLimitText = BaseWidgetUtils.createText( sortLimitComposite, "" + preferences.getSortLimit(), 5, 1 ); //$NON-NLS-1$
        sortLimitText.setToolTipText( sortLimitTooltip );
        sortLimitText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
                {
                    e.doit = false;
                }
            }
        } );
    }
}
