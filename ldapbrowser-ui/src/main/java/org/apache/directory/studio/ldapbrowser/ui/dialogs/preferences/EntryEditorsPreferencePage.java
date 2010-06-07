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
package org.apache.directory.studio.ldapbrowser.ui.dialogs.preferences;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.connection.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.entryeditors.EntryEditorExtension;
import org.apache.directory.studio.entryeditors.EntryEditorManager;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PreferencesUtil;


/**
 * The entry editors preference page contains settings 
 * for the Entry Editors.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EntryEditorsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
    /** A flag indicating whether or not to use the user's priority for entry editors */
    private boolean useUserPriority = false;

    /** The open mode setting value */
    private int openMode = 0;

    /** The ordered list of entry editors */
    private List<EntryEditorExtension> sortedEntryEditorsList;

    // UI fields
    private Button historicalBehaviorButton;
    private Button useApplicationWideOpenModeButton;
    private TableViewer entryEditorsTableViewer;
    private Button upEntryEditorButton;
    private Button downEntryEditorButton;
    private Button restoreDefaultsEntryEditorsButton;


    /**
     * Creates a new instance of EntryEditorsPreferencePage.
     */
    public EntryEditorsPreferencePage()
    {
        super( Messages.getString( "EntryEditorsPreferencePage.EntryEditorsPrefPageTitle" ) ); //$NON-NLS-1$
        super.setPreferenceStore( BrowserUIPlugin.getDefault().getPreferenceStore() );
        super.setDescription( Messages.getString( "EntryEditorsPreferencePage.EntryEditorsPrefPageDescription" ) ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbench workbench )
    {
        openMode = BrowserUIPlugin.getDefault().getPluginPreferences().getInt(
            BrowserUIConstants.PREFERENCE_ENTRYEDITORS_OPEN_MODE );

        useUserPriority = BrowserUIPlugin.getDefault().getPluginPreferences().getBoolean(
            BrowserUIConstants.PREFERENCE_ENTRYEDITORS_USE_USER_PRIORITIES );
    }


    /**
     * {@inheritDoc}
     */
    protected Control createContents( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        // Open Mode Group
        BaseWidgetUtils.createSpacer( composite, 1 );
        BaseWidgetUtils.createSpacer( composite, 1 );
        Group openModeGroup = BaseWidgetUtils.createGroup( BaseWidgetUtils.createColumnContainer( composite, 1, 1 ),
            Messages.getString( "EntryEditorsPreferencePage.OpenMode" ), 1 ); //$NON-NLS-1$

        // Historical Behavior Button
        historicalBehaviorButton = BaseWidgetUtils.createRadiobutton( openModeGroup, Messages
            .getString( "EntryEditorsPreferencePage.HistoricalBehavior" ), 1 ); //$NON-NLS-1$
        Composite historicalBehaviorComposite = BaseWidgetUtils.createColumnContainer( openModeGroup, 2, 1 );
        BaseWidgetUtils.createRadioIndent( historicalBehaviorComposite, 1 );
        Label historicalBehaviourLabel = BaseWidgetUtils.createWrappedLabel( historicalBehaviorComposite, Messages
            .getString( "EntryEditorsPreferencePage.HistoricalBehaviorTooltip" ), 1 ); //$NON-NLS-1$
        GridData historicalBehaviourLabelGridData = new GridData( GridData.FILL_HORIZONTAL );
        historicalBehaviourLabelGridData.widthHint = 300;
        historicalBehaviourLabel.setLayoutData( historicalBehaviourLabelGridData );

        // Use Application Wide Open Mode Button
        useApplicationWideOpenModeButton = BaseWidgetUtils.createRadiobutton( openModeGroup, Messages
            .getString( "EntryEditorsPreferencePage.ApplicationWideSetting" ), 1 ); //$NON-NLS-1$
        Composite useApplicationWideOpenModeComposite = BaseWidgetUtils.createColumnContainer( openModeGroup, 2, 1 );
        BaseWidgetUtils.createRadioIndent( useApplicationWideOpenModeComposite, 1 );
        Link link = BaseWidgetUtils.createLink( useApplicationWideOpenModeComposite, Messages
            .getString( "EntryEditorsPreferencePage.ApplicationWideSettingTooltip" ), 1 ); //$NON-NLS-1$
        GridData linkGridData = new GridData( GridData.FILL_HORIZONTAL );
        linkGridData.widthHint = 300;
        link.setLayoutData( linkGridData );
        link.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                PreferencesUtil.createPreferenceDialogOn( getShell(),
                    "org.eclipse.ui.preferencePages.Workbench", null, null ); //$NON-NLS-1$
            }
        } );

        // Initializing the UI from the preferences value
        if ( openMode == BrowserUIConstants.PREFERENCE_ENTRYEDITORS_OPEN_MODE_HISTORICAL_BEHAVIOR )
        {
            historicalBehaviorButton.setSelection( true );
            useApplicationWideOpenModeButton.setSelection( false );
        }
        else if ( openMode == BrowserUIConstants.PREFERENCE_ENTRYEDITORS_OPEN_MODE_APPLICATION_WIDE )
        {
            historicalBehaviorButton.setSelection( false );
            useApplicationWideOpenModeButton.setSelection( true );
        }

        // Entry Editors Group
        BaseWidgetUtils.createSpacer( composite, 1 );
        BaseWidgetUtils.createSpacer( composite, 1 );
        Group entryEditorsGroup = BaseWidgetUtils.createGroup(
            BaseWidgetUtils.createColumnContainer( composite, 1, 1 ), Messages
                .getString( "EntryEditorsPreferencePage.EntryEditors" ), 1 ); //$NON-NLS-1$

        // Entry Editors Label
        Label entryEditorsLabel = BaseWidgetUtils.createWrappedLabel( entryEditorsGroup, Messages
            .getString( "EntryEditorsPreferencePage.EntryEditorsLabel" ), 1 ); //$NON-NLS-1$
        GridData entryEditorsLabelGridData = new GridData( GridData.FILL_HORIZONTAL );
        entryEditorsLabelGridData.widthHint = 300;
        entryEditorsLabel.setLayoutData( entryEditorsLabelGridData );

        // Entry Editors Composite
        Composite entryEditorsComposite = new Composite( entryEditorsGroup, SWT.NONE );
        GridLayout gl = new GridLayout( 2, false );
        gl.marginHeight = gl.marginWidth = 0;
        entryEditorsComposite.setLayout( gl );
        entryEditorsComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // SchemaConnectors TableViewer
        entryEditorsTableViewer = new TableViewer( entryEditorsComposite, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION );
        GridData gridData = new GridData( SWT.FILL, SWT.NONE, true, false, 1, 3 );
        gridData.heightHint = 125;
        entryEditorsTableViewer.getTable().setLayoutData( gridData );
        entryEditorsTableViewer.setContentProvider( new ArrayContentProvider() );
        entryEditorsTableViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                return ( ( EntryEditorExtension ) element ).getName();
            }


            public Image getImage( Object element )
            {
                return ( ( EntryEditorExtension ) element ).getIcon().createImage();
            }
        } );
        entryEditorsTableViewer.setInput( BrowserUIPlugin.getDefault().getEntryEditorManager()
            .getEntryEditorExtensions() );

        // Up Button
        upEntryEditorButton = BaseWidgetUtils.createButton( entryEditorsComposite, Messages
            .getString( "EntryEditorsPreferencePage.Up" ), 1 ); //$NON-NLS-1$
        upEntryEditorButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        upEntryEditorButton.setEnabled( false );
        upEntryEditorButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                moveSelectedEntryEditor( MoveEntryEditorDirectionEnum.UP );
            }
        } );

        // Down Button
        downEntryEditorButton = BaseWidgetUtils.createButton( entryEditorsComposite, Messages
            .getString( "EntryEditorsPreferencePage.Down" ), 1 ); //$NON-NLS-1$
        downEntryEditorButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        downEntryEditorButton.setEnabled( false );
        downEntryEditorButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                moveSelectedEntryEditor( MoveEntryEditorDirectionEnum.DOWN );
            }
        } );

        // Restore Defaults Button
        restoreDefaultsEntryEditorsButton = BaseWidgetUtils.createButton( entryEditorsComposite, Messages
            .getString( "EntryEditorsPreferencePage.RestoreDefaults" ), 1 ); //$NON-NLS-1$
        restoreDefaultsEntryEditorsButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        restoreDefaultsEntryEditorsButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                performDefaultsEntryEditors();
            }
        } );

        // Description Label
        BaseWidgetUtils.createLabel( entryEditorsGroup, Messages
            .getString( "EntryEditorsPreferencePage.DescriptionColon" ), 1 ); //$NON-NLS-1$

        // Description Text
        final Text descriptionText = new Text( entryEditorsGroup, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY );
        descriptionText.setEditable( false );
        gridData = new GridData( SWT.FILL, SWT.NONE, true, false );
        gridData.heightHint = 27;
        gridData.widthHint = 300;
        descriptionText.setLayoutData( gridData );
        entryEditorsTableViewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                // Getting the selected entry editor
                EntryEditorExtension entryEditor = ( EntryEditorExtension ) ( ( StructuredSelection ) entryEditorsTableViewer
                    .getSelection() ).getFirstElement();
                if ( entryEditor != null )
                {
                    // Updating the description text field
                    descriptionText.setText( entryEditor.getDescription() );

                    // Updating the state of the buttons
                    updateButtonsState( entryEditor );
                }
            }
        } );

        if ( useUserPriority )
        {
            sortEntryEditorsByUserPriority();
        }
        else
        {
            sortEntryEditorsByDefaultPriority();
        }

        // Selecting the first entry editor
        if ( sortedEntryEditorsList.size() > 0 )
        {
            entryEditorsTableViewer.setSelection( new StructuredSelection( sortedEntryEditorsList.get( 0 ) ) );
        }

        return composite;
    }


    /**
     * Sorts the entry editors using the user's priority.
     */
    private void sortEntryEditorsByUserPriority()
    {
        // Getting the entry editors sorted by user's priority
        sortedEntryEditorsList = new ArrayList<EntryEditorExtension>( BrowserUIPlugin.getDefault()
            .getEntryEditorManager().getEntryEditorExtensionsSortedByUserPriority() );

        // Assigning the sorted editors to the viewer
        entryEditorsTableViewer.setInput( sortedEntryEditorsList );
    }


    /**
     * Sorts the entry editors using the default priority.
     */
    private void sortEntryEditorsByDefaultPriority()
    {
        // Getting the entry editors sorted by default priority
        sortedEntryEditorsList = new ArrayList<EntryEditorExtension>( BrowserUIPlugin.getDefault()
            .getEntryEditorManager().getEntryEditorExtensionsSortedByDefaultPriority() );

        // Assigning the sorted editors to the viewer
        entryEditorsTableViewer.setInput( sortedEntryEditorsList );
    }


    /**
     * Moves the currently selected entry editor.
     *
     * @param direction
     *      the direction (up or down)
     */
    private void moveSelectedEntryEditor( MoveEntryEditorDirectionEnum direction )
    {
        StructuredSelection selection = ( StructuredSelection ) entryEditorsTableViewer.getSelection();
        if ( selection.size() == 1 )
        {
            EntryEditorExtension entryEditor = ( EntryEditorExtension ) selection.getFirstElement();
            if ( sortedEntryEditorsList.contains( entryEditor ) )
            {
                int oldIndex = sortedEntryEditorsList.indexOf( entryEditor );
                int newIndex = 0;

                // Determining the new index number
                switch ( direction )
                {
                    case UP:
                        newIndex = oldIndex - 1;
                        break;
                    case DOWN:
                        newIndex = oldIndex + 1;
                        break;
                }

                // Checking bounds
                if ( ( newIndex >= 0 ) && ( newIndex < sortedEntryEditorsList.size() ) )
                {
                    // Switching the two entry editors
                    EntryEditorExtension newIndexEntryEditorBackup = sortedEntryEditorsList.set( newIndex, entryEditor );
                    sortedEntryEditorsList.set( oldIndex, newIndexEntryEditorBackup );

                    // Reloading the viewer
                    entryEditorsTableViewer.refresh();

                    // Updating the state of the buttons
                    updateButtonsState( entryEditor );

                    // Setting the "Use User Priority" to true
                    useUserPriority = true;
                }
            }
        }
    }


    /**
     * Updates the state of the buttons.
     *
     * @param entryEditor
     *      the selected entry editor
     */
    private void updateButtonsState( EntryEditorExtension entryEditor )
    {
        // Getting the index of the entry editor in the list
        int index = sortedEntryEditorsList.indexOf( entryEditor );

        // Updating up button state
        upEntryEditorButton.setEnabled( index > 0 );

        // Updating down button state
        downEntryEditorButton.setEnabled( index <= ( sortedEntryEditorsList.size() - 2 ) );
    }


    /**
     * Updates the state of the buttons.
     */
    private void updateButtonsState()
    {
        StructuredSelection selection = ( StructuredSelection ) entryEditorsTableViewer.getSelection();
        if ( selection.size() == 1 )
        {
            EntryEditorExtension entryEditor = ( EntryEditorExtension ) selection.getFirstElement();

            // Updating the state of the buttons
            updateButtonsState( entryEditor );
        }
    }

    /**
     * This enum is used to determine in which direction the entry editor
     * should be moved.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    private enum MoveEntryEditorDirectionEnum
    {
        UP, DOWN
    }


    /**
     * {@inheritDoc}
     */
    public boolean performOk()
    {
        if ( historicalBehaviorButton.getSelection() )
        {
            BrowserUIPlugin.getDefault().getPluginPreferences().setValue(
                BrowserUIConstants.PREFERENCE_ENTRYEDITORS_OPEN_MODE,
                BrowserUIConstants.PREFERENCE_ENTRYEDITORS_OPEN_MODE_HISTORICAL_BEHAVIOR );
        }
        else if ( useApplicationWideOpenModeButton.getSelection() )
        {
            BrowserUIPlugin.getDefault().getPluginPreferences().setValue(
                BrowserUIConstants.PREFERENCE_ENTRYEDITORS_OPEN_MODE,
                BrowserUIConstants.PREFERENCE_ENTRYEDITORS_OPEN_MODE_APPLICATION_WIDE );
        }

        BrowserUIPlugin.getDefault().getPluginPreferences().setValue(
            BrowserUIConstants.PREFERENCE_ENTRYEDITORS_USE_USER_PRIORITIES, useUserPriority );

        if ( useUserPriority )
        {
            StringBuilder sb = new StringBuilder();
            for ( EntryEditorExtension entryEditor : sortedEntryEditorsList )
            {
                sb.append( entryEditor.getId() + EntryEditorManager.PRIORITIES_SEPARATOR );
            }

            if ( sb.length() > 0 )
            {
                sb.deleteCharAt( sb.length() - 1 );
            }

            BrowserUIPlugin.getDefault().getPluginPreferences().setValue(
                BrowserUIConstants.PREFERENCE_ENTRYEDITORS_USER_PRIORITIES, sb.toString() );
        }

        return true;
    }


    /**
     * {@inheritDoc}
     */
    protected void performDefaults()
    {
        openMode = BrowserUIPlugin.getDefault().getPluginPreferences().getDefaultInt(
            BrowserUIConstants.PREFERENCE_ENTRYEDITORS_OPEN_MODE );

        if ( openMode == BrowserUIConstants.PREFERENCE_ENTRYEDITORS_OPEN_MODE_HISTORICAL_BEHAVIOR )
        {
            historicalBehaviorButton.setSelection( true );
            useApplicationWideOpenModeButton.setSelection( false );
        }
        else if ( openMode == BrowserUIConstants.PREFERENCE_ENTRYEDITORS_OPEN_MODE_APPLICATION_WIDE )
        {
            historicalBehaviorButton.setSelection( false );
            useApplicationWideOpenModeButton.setSelection( true );
        }

        performDefaultsEntryEditors();

        super.performDefaults();
    }


    /**
     * Restore defaults to the entry editors part of the UI.
     */
    private void performDefaultsEntryEditors()
    {
        useUserPriority = BrowserUIPlugin.getDefault().getPluginPreferences().getDefaultBoolean(
            BrowserUIConstants.PREFERENCE_ENTRYEDITORS_USE_USER_PRIORITIES );

        if ( useUserPriority )
        {
            sortEntryEditorsByUserPriority();
        }
        else
        {
            sortEntryEditorsByDefaultPriority();
        }

        updateButtonsState();
    }
}