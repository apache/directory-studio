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

package org.apache.directory.ldapstudio.browser.common.widgets.entryeditor;


import org.apache.directory.ldapstudio.browser.common.BrowserCommonActivator;
import org.apache.directory.ldapstudio.browser.common.BrowserCommonConstants;
import org.apache.directory.ldapstudio.browser.common.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;


/**
 * This class represents the dialog used to change the entry editors's  default sort preferences.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class EntryEditorWidgetSorterDialog extends Dialog
{

    /** The Constant DIALOG_TITLE. */
    public static final String DIALOG_TITLE = "Entry Editor Sorting";

    /** The Constant SORT_BY_NONE. */
    public static final String SORT_BY_NONE = "No Default Sorting";

    /** The Constant SORT_BY_ATTRIBUTE. */
    public static final String SORT_BY_ATTRIBUTE = "Attribute Description";

    /** The Constant SORT_BY_VALUE. */
    public static final String SORT_BY_VALUE = "Value";

    /** The preferences. */
    private EntryEditorWidgetPreferences preferences;

    /** The object class and must attributes first button. */
    private Button objectClassAndMustAttributesFirstButton;

    /** The operational attributes last button. */
    private Button operationalAttributesLastButton;

    /** The sort by combo. */
    private Combo sortByCombo;

    /** The sort acending button. */
    private Button sortAcendingButton;

    /** The sort descending button. */
    private Button sortDescendingButton;


    /**
     * Creates a new instance of EntryEditorWidgetSorterDialog.
     * 
     * @param parentShell the parent shell
     * @param preferences the preferences
     */
    public EntryEditorWidgetSorterDialog( Shell parentShell, EntryEditorWidgetPreferences preferences )
    {
        super( parentShell );
        this.preferences = preferences;
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation calls its super implementation and sets the dialog's title.
     */
    protected void configureShell( Shell newShell )
    {
        super.configureShell( newShell );
        newShell.setText( DIALOG_TITLE );
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation saves the changed settings when OK is pressed.
     */
    protected void buttonPressed( int buttonId )
    {
        if ( buttonId == IDialogConstants.OK_ID )
        {
            IPreferenceStore store = BrowserCommonActivator.getDefault().getPreferenceStore();
            store.setValue( BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_OBJECTCLASS_AND_MUST_ATTRIBUTES_FIRST,
                objectClassAndMustAttributesFirstButton.getSelection() );
            store.setValue( BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_OPERATIONAL_ATTRIBUTES_LAST,
                operationalAttributesLastButton.getSelection() );
            store.setValue( BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_DEFAULT_SORT_ORDER, sortDescendingButton
                .getSelection() ? BrowserCoreConstants.SORT_ORDER_DESCENDING
                : BrowserCoreConstants.SORT_ORDER_ASCENDING );
            store.setValue( BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_DEFAULT_SORT_BY,
                sortByCombo.getSelectionIndex() == 2 ? BrowserCoreConstants.SORT_BY_VALUE : sortByCombo
                    .getSelectionIndex() == 1 ? BrowserCoreConstants.SORT_BY_ATTRIBUTE_DESCRIPTION
                    : BrowserCoreConstants.SORT_BY_NONE );
        }

        super.buttonPressed( buttonId );
    }


    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );

        Group group = BaseWidgetUtils.createGroup( composite, "Group attributes", 1 );
        GridData gd = new GridData( GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        group.setLayoutData( gd );

        objectClassAndMustAttributesFirstButton = BaseWidgetUtils.createCheckbox( group,
            "ObjectClass and must attributes first", 1 );
        objectClassAndMustAttributesFirstButton.setSelection( preferences.isObjectClassAndMustAttributesFirst() );

        operationalAttributesLastButton = BaseWidgetUtils.createCheckbox( group, "Operational attributes last", 1 );
        operationalAttributesLastButton.setSelection( preferences.isOperationalAttributesLast() );

        Group sortingGroup = BaseWidgetUtils.createGroup( composite, "Sort attributes", 1 );

        Composite sortByComposite = BaseWidgetUtils.createColumnContainer( sortingGroup, 4, 1 );
        BaseWidgetUtils.createLabel( sortByComposite, "Sort by", 1 );
        sortByCombo = BaseWidgetUtils.createReadonlyCombo( sortByComposite, new String[]
            { SORT_BY_NONE, SORT_BY_ATTRIBUTE, SORT_BY_VALUE }, 0, 1 );
        sortByCombo.select( preferences.getDefaultSortBy() == BrowserCoreConstants.SORT_BY_VALUE ? 2 : preferences
            .getDefaultSortBy() == BrowserCoreConstants.SORT_BY_ATTRIBUTE_DESCRIPTION ? 1 : 0 );
        sortByCombo.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                sortAcendingButton.setEnabled( sortByCombo.getSelectionIndex() != 0 );
                sortDescendingButton.setEnabled( sortByCombo.getSelectionIndex() != 0 );
            }
        } );

        sortAcendingButton = BaseWidgetUtils.createRadiobutton( sortByComposite, "Ascending", 1 );
        sortAcendingButton
            .setSelection( preferences.getDefaultSortOrder() == BrowserCoreConstants.SORT_ORDER_ASCENDING );
        sortAcendingButton.setEnabled( sortByCombo.getSelectionIndex() != 0 );

        sortDescendingButton = BaseWidgetUtils.createRadiobutton( sortByComposite, "Descending", 1 );
        sortDescendingButton
            .setSelection( preferences.getDefaultSortOrder() == BrowserCoreConstants.SORT_ORDER_DESCENDING );
        sortDescendingButton.setEnabled( sortByCombo.getSelectionIndex() != 0 );

        BaseWidgetUtils.createSpacer( composite, 2 );

        BaseWidgetUtils.createLabel( composite,
            "Please click to table headers to sort by attribute description or value.", 1 );

        applyDialogFont( composite );
        return composite;
    }

}
