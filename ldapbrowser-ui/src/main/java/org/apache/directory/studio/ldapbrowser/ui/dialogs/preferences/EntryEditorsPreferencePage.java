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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.entryeditors.EntryEditorExtension;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * The entry editors preference page contains settings 
 * for the Entry Editors.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class EntryEditorsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
    /** The priorities separator */
    private static final String PRIORITIES_SEPARATOR = ",";

    /** A flag indicating whether or not to use the user's priority for entry editors */
    private boolean useUserPriority = false;

    /** The ordered list of entry editors */
    private List<EntryEditorExtension> sortedEntryEditorsList;

    /** */
    private Comparator<EntryEditorExtension> entryEditorComparator = new Comparator<EntryEditorExtension>()
    {
        public int compare( EntryEditorExtension o1, EntryEditorExtension o2 )
        {
            if ( o1 == null )
            {
                return ( o2 == null ) ? 0 : -1;
            }

            if ( o2 == null )
            {
                return 1;
            }

            // Getting priorities
            int o1Priority = o1.getPriority();
            int o2Priority = o2.getPriority();

            if ( o1Priority != o2Priority )
            {
                return ( o1Priority > o2Priority ) ? -1 : 1;
            }

            // Getting names
            String o1Name = o1.getName();
            String o2Name = o2.getName();

            if ( o1Name == null )
            {
                return ( o2Name == null ) ? 0 : -1;
            }

            if ( o2 == null )
            {
                return 1;
            }

            return o1Name.compareTo( o2Name );
        }
    };

    // UI fields
    private TableViewer entryEditorsTableViewer;
    private Button upButton;
    private Button downButton;


    /**
     * Creates a new instance of EntryEditorsPreferencePage.
     */
    public EntryEditorsPreferencePage()
    {
        super( "Entry Editors" );
        super.setPreferenceStore( BrowserUIPlugin.getDefault().getPreferenceStore() );
        super.setDescription( "Description" );
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbench workbench )
    {
        useUserPriority = ConnectionCorePlugin.getDefault().getPluginPreferences().getBoolean(
            BrowserUIConstants.PREFERENCE_ENTRYEDITORS_USE_USER_PRIORITIES );
    }


    /**
     * {@inheritDoc}
     */
    protected Control createContents( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayout( new GridLayout() );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // Entry Editors Label
        BaseWidgetUtils.createLabel( composite, "Entry Editors:", 1 );

        // Entry Editors Composite
        Composite entryEditorsComposite = new Composite( composite, SWT.NONE );
        GridLayout gl = new GridLayout( 2, false );
        gl.marginHeight = gl.marginWidth = 0;
        entryEditorsComposite.setLayout( gl );
        entryEditorsComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // SchemaConnectors TableViewer
        entryEditorsTableViewer = new TableViewer( entryEditorsComposite, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION );
        GridData gridData = new GridData( SWT.FILL, SWT.NONE, true, false, 1, 2 );
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
        upButton = BaseWidgetUtils.createButton( entryEditorsComposite, "Up", 1 );
        upButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        upButton.setEnabled( false );
        upButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                moveSelectedEntryEditor( MoveEntryEditorDirectionEnum.UP );
            }
        } );

        // Down Button
        downButton = BaseWidgetUtils.createButton( entryEditorsComposite, "Down", 1 );
        downButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        downButton.setEnabled( false );
        downButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                moveSelectedEntryEditor( MoveEntryEditorDirectionEnum.DOWN );
            }
        } );

        // Description Label
        BaseWidgetUtils.createLabel( composite, "Description:", 1 );

        // Description Text
        final Text descriptionText = new Text( composite, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY );
        descriptionText.setEditable( false );
        gridData = new GridData( SWT.FILL, SWT.NONE, true, false );
        gridData.heightHint = 27;
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

        return parent;
    }


    /**
     * Sorts the entry editors using the user's priority.
     */
    private void sortEntryEditorsByUserPriority()
    {
        // Getting the user's priorities
        String userPriorities = ConnectionCorePlugin.getDefault().getPluginPreferences().getString(
            BrowserUIConstants.PREFERENCE_ENTRYEDITORS_USER_PRIORITIES );
        if ( ( userPriorities != null ) && ( !"".equals( userPriorities ) ) )
        {
            // Getting all entry editors
            Collection<EntryEditorExtension> entryEditorExtensions = BrowserUIPlugin.getDefault()
                .getEntryEditorManager().getEntryEditorExtensions();

            String[] splittedUserPriorities = userPriorities.split( PRIORITIES_SEPARATOR );
            if ( ( splittedUserPriorities != null ) && ( splittedUserPriorities.length > 0 ) )
            {

                // Creating a map where entry editors are accessible via their ID
                Map<String, EntryEditorExtension> entryEditorsMap = new HashMap<String, EntryEditorExtension>();
                for ( EntryEditorExtension entryEditorExtension : entryEditorExtensions )
                {
                    entryEditorsMap.put( entryEditorExtension.getId(), entryEditorExtension );
                }

                // Creating the sorted entry editors list
                sortedEntryEditorsList = new ArrayList<EntryEditorExtension>( entryEditorExtensions.size() );

                // Adding the entry editors according to the user's priority
                for ( String entryEditorId : splittedUserPriorities )
                {
                    // Verifying the entry editor is present in the map
                    if ( entryEditorsMap.containsKey( entryEditorId ) )
                    {
                        // Adding it to the sorted list
                        sortedEntryEditorsList.add( entryEditorsMap.get( entryEditorId ) );
                    }
                }
            }

            // If some new plugins have been added recently, their new 
            // entry editors may not be present in the string stored in 
            // the preferences.
            // We are then adding them at the end of the sorted list.

            // Creating a list of remaining entry editors
            List<EntryEditorExtension> remainingEntryEditors = new ArrayList<EntryEditorExtension>();
            for ( EntryEditorExtension entryEditorExtension : entryEditorExtensions )
            {
                // Verifying the entry editor is present in the sorted list
                if ( !sortedEntryEditorsList.contains( entryEditorExtension ) )
                {
                    // Adding it to the remaining list
                    remainingEntryEditors.add( entryEditorExtension );
                }
            }

            // Sorting the remaining entry editors based on their priority
            Collections.sort( remainingEntryEditors, entryEditorComparator );

            // Adding the remaining entry editors
            for ( EntryEditorExtension entryEditorExtension : remainingEntryEditors )
            {
                sortedEntryEditorsList.add( entryEditorExtension );
            }

            // Assigning the sorted editors to the viewer
            entryEditorsTableViewer.setInput( sortedEntryEditorsList );
        }
    }


    /**
     * Sorts the entry editors using the default priority.
     */
    private void sortEntryEditorsByDefaultPriority()
    {
        // Getting all entry editors
        Collection<EntryEditorExtension> entryEditorExtensions = BrowserUIPlugin.getDefault().getEntryEditorManager()
            .getEntryEditorExtensions();

        // Creating the sorted entry editors list
        sortedEntryEditorsList = new ArrayList<EntryEditorExtension>( entryEditorExtensions.size() );

        // Adding the remaining entry editors
        for ( EntryEditorExtension entryEditorExtension : entryEditorExtensions )
        {
            sortedEntryEditorsList.add( entryEditorExtension );
        }

        // Sorting the remaining entry editors based on their priority
        Collections.sort( sortedEntryEditorsList, entryEditorComparator );

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
        if ( index > 0 )
        {
            upButton.setEnabled( true );
        }
        else
        {
            upButton.setEnabled( false );
        }

        // Updating down button state
        if ( index <= sortedEntryEditorsList.size() - 2 )
        {
            downButton.setEnabled( true );
        }
        else
        {
            downButton.setEnabled( false );
        }
    }

    /**
     * This enum is used to determine in which direction the entry editor
     * should be moved.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
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
        ConnectionCorePlugin.getDefault().getPluginPreferences().setValue(
            BrowserUIConstants.PREFERENCE_ENTRYEDITORS_USE_USER_PRIORITIES, useUserPriority );

        if ( useUserPriority )
        {
            StringBuilder sb = new StringBuilder();
            for ( EntryEditorExtension entryEditor : sortedEntryEditorsList )
            {
                sb.append( entryEditor.getId() + PRIORITIES_SEPARATOR );
            }

            if ( sb.length() > 0 )
            {
                sb.deleteCharAt( sb.length() - 1 );
            }

            ConnectionCorePlugin.getDefault().getPluginPreferences().setValue(
                BrowserUIConstants.PREFERENCE_ENTRYEDITORS_USER_PRIORITIES, sb.toString() );
        }

        return true;
    }


    /**
     * {@inheritDoc}
     */
    protected void performDefaults()
    {
        useUserPriority = ConnectionCorePlugin.getDefault().getPluginPreferences().getDefaultBoolean(
            BrowserUIConstants.PREFERENCE_ENTRYEDITORS_USE_USER_PRIORITIES );

        if ( useUserPriority )
        {
            sortEntryEditorsByUserPriority();
        }
        else
        {
            sortEntryEditorsByDefaultPriority();
        }

        super.performDefaults();
    }
}