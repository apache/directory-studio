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

package org.apache.directory.ldapstudio.schemas.view.views;


import org.apache.directory.ldapstudio.schemas.Activator;
import org.apache.directory.ldapstudio.schemas.Messages;
import org.apache.directory.ldapstudio.schemas.PluginConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;


/**
 * This class implements the Schema Elements View Sorter Dialog.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SchemaElementsViewSortDialog extends Dialog
{
    /** The title of the dialog */
    private static final String DIALOG_TITLE = Messages.getString("SchemaElementsViewSortDialog.Schema_Elements_View_Sorting"); //$NON-NLS-1$

    /** The Sorting First Name category */
    private static final String SORTING_FISTNAME = Messages.getString("SchemaElementsViewSortDialog.First_Name"); //$NON-NLS-1$

    /** The Sorting OID category */
    private static final String SORTING_OID = Messages.getString("SchemaElementsViewSortDialog.OID"); //$NON-NLS-1$

    // UI Fields
    private Button atFirstButton;
    private Button ocFirstButton;
    private Button mixedButton;
    private Combo sortingCombo;
    private Button ascendingButton;
    private Button descendingButton;


    /**
     * Creates a new instance of SchemaElementsSortDialog.
     *
     * @param parentShell
     *      the parent shell
     */
    public SchemaElementsViewSortDialog( Shell parentShell )
    {
        super( parentShell );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell newShell )
    {
        super.configureShell( newShell );
        newShell.setText( DIALOG_TITLE );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL );
        //        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        composite.setLayoutData( gd );

        // Grouping Group
        Group groupingGroup = new Group( composite, SWT.NONE );
        groupingGroup.setLayoutData( new GridData( GridData.FILL_BOTH ) );
        groupingGroup.setText( Messages.getString("SchemaElementsViewSortDialog.Grouping") ); //$NON-NLS-1$
        groupingGroup.setLayout( new GridLayout() );

        // Attribute Types first Button
        atFirstButton = new Button( groupingGroup, SWT.RADIO );
        atFirstButton.setText( Messages.getString("SchemaElementsViewSortDialog.Attribute_Types_first") ); //$NON-NLS-1$
        atFirstButton.setEnabled( true );

        // Object Classes first Button
        ocFirstButton = new Button( groupingGroup, SWT.RADIO );
        ocFirstButton.setText( Messages.getString("SchemaElementsViewSortDialog.Object_Classes_first") ); //$NON-NLS-1$
        ocFirstButton.setEnabled( true );

        // Mixed Button
        mixedButton = new Button( groupingGroup, SWT.RADIO );
        mixedButton.setText( Messages.getString("SchemaElementsViewSortDialog.Mixed") ); //$NON-NLS-1$
        mixedButton.setEnabled( true );

        // Sorting Group
        Group sortingGroup = new Group( composite, SWT.NONE );
        sortingGroup.setLayoutData( new GridData( GridData.FILL_BOTH ) );
        sortingGroup.setText( Messages.getString("SchemaElementsViewSortDialog.Sorting") ); //$NON-NLS-1$
        sortingGroup.setLayout( new GridLayout() );
        Composite sortingGroupComposite = new Composite( sortingGroup, SWT.NONE );
        GridLayout gl = new GridLayout( 4, false );
        gl.marginHeight = gl.marginWidth = 0;
        sortingGroupComposite.setLayout( gl );
        sortingGroupComposite.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );

        // Sort by Label
        Label sortByLabel = new Label( sortingGroupComposite, SWT.NONE );
        sortByLabel.setText( Messages.getString("SchemaElementsViewSortDialog.Sort_by") ); //$NON-NLS-1$

        // Sorting Combo
        sortingCombo = new Combo( sortingGroupComposite, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER );
        sortingCombo.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        sortingCombo.setItems( new String[]
            { SORTING_FISTNAME, SORTING_OID } );
        sortingCombo.setEnabled( true );

        // Ascending Button
        ascendingButton = new Button( sortingGroupComposite, SWT.RADIO );
        ascendingButton.setText( Messages.getString("SchemaElementsViewSortDialog.Ascending") ); //$NON-NLS-1$
        ascendingButton.setEnabled( true );

        // Descending Button
        descendingButton = new Button( sortingGroupComposite, SWT.RADIO );
        descendingButton.setText( Messages.getString("SchemaElementsViewSortDialog.Descending") ); //$NON-NLS-1$
        descendingButton.setEnabled( true );

        initFieldsFromPreferences();

        applyDialogFont( composite );
        return composite;
    }


    /**
     * Initializes the fields for the stored preferences.
     */
    private void initFieldsFromPreferences()
    {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();

        int grouping = store.getInt( PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_GROUPING );
        if ( grouping == PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_GROUPING_ATFIRST )
        {
            atFirstButton.setSelection( true );
        }
        else if ( grouping == PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_GROUPING_OCFIRST )
        {
            ocFirstButton.setSelection( true );
        }
        else if ( grouping == PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_GROUPING_MIXED )
        {
            mixedButton.setSelection( true );
        }

        int sortingBy = store.getInt( PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_SORTING_BY );
        if ( sortingBy == PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_SORTING_BY_FIRSTNAME )
        {
            sortingCombo.select( 0 );
        }
        else if ( sortingBy == PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_SORTING_BY_OID )
        {
            sortingCombo.select( 1 );
        }

        int sortingOrder = store.getInt( PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_SORTING_ORDER );
        if ( sortingOrder == PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_SORTING_ORDER_ASCENDING )
        {
            ascendingButton.setSelection( true );
        }
        else if ( sortingOrder == PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_SORTING_ORDER_DESCENDING )
        {
            descendingButton.setSelection( true );
        }

    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
     */
    protected void buttonPressed( int buttonId )
    {
        if ( buttonId == IDialogConstants.OK_ID )
        {
            IPreferenceStore store = Activator.getDefault().getPreferenceStore();
            if ( ( atFirstButton.getSelection() ) & ( !ocFirstButton.getSelection() ) & ( !mixedButton.getSelection() ) )
            {
                store.setValue( PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_GROUPING, PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_GROUPING_ATFIRST );
            }
            else if ( ( !atFirstButton.getSelection() ) & ( ocFirstButton.getSelection() )
                & ( !mixedButton.getSelection() ) )
            {
                store.setValue( PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_GROUPING, PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_GROUPING_OCFIRST );
            }
            else if ( ( !atFirstButton.getSelection() ) & ( !ocFirstButton.getSelection() )
                & ( mixedButton.getSelection() ) )
            {
                store.setValue( PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_GROUPING, PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_GROUPING_MIXED );
            }

            if ( sortingCombo.getItem( sortingCombo.getSelectionIndex() ).equals( SORTING_FISTNAME ) )
            {
                store.setValue( PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_SORTING_BY, PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_SORTING_BY_FIRSTNAME );
            }
            else if ( sortingCombo.getItem( sortingCombo.getSelectionIndex() ).equals( SORTING_OID ) )
            {
                store.setValue( PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_SORTING_BY, PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_SORTING_BY_OID );
            }

            if ( ascendingButton.getSelection() && !descendingButton.getSelection() )
            {
                store.setValue( PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_SORTING_ORDER, PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_SORTING_ORDER_ASCENDING );
            }
            else if ( !ascendingButton.getSelection() && descendingButton.getSelection() )
            {
                store.setValue( PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_SORTING_ORDER, PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_SORTING_ORDER_DESCENDING );
            }
        }

        super.buttonPressed( buttonId );
    }
}
