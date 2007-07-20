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
package org.apache.directory.studio.apacheds.schemaeditor.view.widget;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.PluginConstants;
import org.apache.directory.studio.apacheds.schemaeditor.controller.actions.OpenSchemaViewPreferenceAction;
import org.apache.directory.studio.apacheds.schemaeditor.controller.actions.OpenSchemaViewSortingDialogAction;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AttributeTypeDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.DifferenceType;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ObjectClassDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.SchemaDifference;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class represents the DifferencesWidget.
 * <p>
 * It is used to display a List of Difference given in input.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DifferencesWidget
{
    /** The TreeViewer */
    private TreeViewer treeViewer;

    /** The TableViewer */
    private TableViewer tableViewer;

    /** The PreferenceStore*/
    private IPreferenceStore store;

    /** The authorized Preferences keys*/
    private List<String> authorizedPrefs;

    /** The preference listener */
    private IPropertyChangeListener preferenceListener = new IPropertyChangeListener()
    {
        /* (non-Javadoc)
         * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
         */
        public void propertyChange( PropertyChangeEvent event )
        {
            if ( authorizedPrefs.contains( event.getProperty() ) )
            {
                //                    if ( PluginConstants.PREFS_SCHEMA_VIEW_GROUPING == event.getProperty() )
                //                    {
                //                        view.reloadViewer();
                //                    }
                //                    else
                //                    {
                treeViewer.refresh();
                //                    }
            }
        }
    };

    // The MenuItems
    private MenuItem groupByType;
    private MenuItem groupByProperty;


    /**
     * Creates a new instance of DifferencesWidget.
     *
     */
    public DifferencesWidget()
    {
        store = Activator.getDefault().getPreferenceStore();
    }


    /**
     * Creates the widget.
     *
     * @param parent
     *      the parent Composite
     */
    public void createWidget( Composite parent )
    {
        // Composite
        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout gridLayout = new GridLayout( 2, true );
        gridLayout.marginBottom = 0;
        gridLayout.marginHeight = 0;
        gridLayout.marginLeft = 0;
        gridLayout.marginRight = 0;
        gridLayout.marginTop = 0;
        gridLayout.marginWidth = 0;
        composite.setLayout( gridLayout );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // Left Composite
        Composite leftComposite = new Composite( composite, SWT.NONE );
        gridLayout = new GridLayout();
        gridLayout.marginBottom = 0;
        gridLayout.marginHeight = 0;
        gridLayout.marginLeft = 0;
        gridLayout.marginRight = 0;
        gridLayout.marginTop = 0;
        gridLayout.marginWidth = 0;
        leftComposite.setLayout( gridLayout );
        leftComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // ToolBar
        final ToolBar leftToolBar = new ToolBar( leftComposite, SWT.HORIZONTAL );
        leftToolBar.setLayoutData( new GridData( SWT.RIGHT, SWT.NONE, false, false ) );
        // Creating the 'Menu' ToolBar item
        final ToolItem leftMenuToolItem = new ToolItem( leftToolBar, SWT.PUSH );
        leftMenuToolItem.setImage( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
            PluginConstants.IMG_TOOLBAR_MENU ).createImage() );
        leftMenuToolItem.setToolTipText( "Menu" );
        // Creating the associated Menu
        final Menu leftMenu = new Menu( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.POP_UP );
        // Adding the action to display the Menu when the item is clicked
        leftMenuToolItem.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                Rectangle rect = leftMenuToolItem.getBounds();
                Point pt = new Point( rect.x, rect.y + rect.height );
                pt = leftToolBar.toDisplay( pt );
                leftMenu.setLocation( pt.x, pt.y );
                leftMenu.setVisible( true );
            }
        } );
        // Adding the 'Sorting...' MenuItem
        MenuItem sortingMenuItem = new MenuItem( leftMenu, SWT.PUSH );
        sortingMenuItem.setText( "Sorting..." );
        sortingMenuItem.setImage( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
            PluginConstants.IMG_SORTING ).createImage() );
        sortingMenuItem.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent arg0 )
            {
                new OpenSchemaViewSortingDialogAction().run();
            }
        } );
        // Adding the 'Separator' MenuItem
        new MenuItem( leftMenu, SWT.SEPARATOR );
        // Adding the 'Preferences...' MenuItem
        MenuItem preferencesMenuItem = new MenuItem( leftMenu, SWT.PUSH );
        preferencesMenuItem.setText( "Preferences..." );
        preferencesMenuItem.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent arg0 )
            {
                new OpenSchemaViewPreferenceAction().run();
            }
        } );

        // TreeViewer
        treeViewer = new TreeViewer( leftComposite, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
        treeViewer.getTree().setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        treeViewer.setContentProvider( new DifferencesWidgetSchemaContentProvider() );
        treeViewer.setLabelProvider( new DifferencesWidgetSchemaLabelProvider() );
        treeViewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                StructuredSelection selection = ( StructuredSelection ) event.getSelection();
                Object element = selection.getFirstElement();
                if ( element instanceof AttributeTypeDifference )
                {
                    AttributeTypeDifference atd = ( AttributeTypeDifference ) element;
                    if ( atd.getType().equals( DifferenceType.MODIFIED ) )
                    {
                        tableViewer.setInput( atd.getDifferences() );
                        return;
                    }
                }
                else if ( element instanceof ObjectClassDifference )
                {
                    ObjectClassDifference ocd = ( ObjectClassDifference ) element;
                    if ( ocd.getType().equals( DifferenceType.MODIFIED ) )
                    {
                        tableViewer.setInput( ocd.getDifferences() );
                        return;
                    }
                }

                // Default
                tableViewer.setInput( null );
            }
        } );
        treeViewer.addDoubleClickListener( new IDoubleClickListener()
        {
            public void doubleClick( DoubleClickEvent event )
            {
                StructuredSelection selection = ( StructuredSelection ) event.getSelection();
                Object element = selection.getFirstElement();
                if ( ( element instanceof Folder ) || ( element instanceof SchemaDifference ) )
                {
                    treeViewer.setExpandedState( element, !treeViewer.getExpandedState( element ) );
                }
            }
        } );

        // Right Composite
        Composite rightComposite = new Composite( composite, SWT.NONE );
        gridLayout = new GridLayout();
        gridLayout.marginBottom = 0;
        gridLayout.marginHeight = 0;
        gridLayout.marginLeft = 0;
        gridLayout.marginRight = 0;
        gridLayout.marginTop = 0;
        gridLayout.marginWidth = 0;
        rightComposite.setLayout( gridLayout );
        rightComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // ToolBar
        final ToolBar rightToolBar = new ToolBar( rightComposite, SWT.HORIZONTAL );
        rightToolBar.setLayoutData( new GridData( SWT.RIGHT, SWT.NONE, false, false ) );
        // Creating the 'Menu' ToolBar item
        final ToolItem rightMenuToolItem = new ToolItem( rightToolBar, SWT.PUSH );
        rightMenuToolItem.setImage( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
            PluginConstants.IMG_TOOLBAR_MENU ).createImage() );
        rightMenuToolItem.setToolTipText( "Menu" );
        // Creating the associated Menu
        final Menu rightMenu = new Menu( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.POP_UP );
        // Adding the action to display the Menu when the item is clicked
        rightMenuToolItem.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                Rectangle rect = rightMenuToolItem.getBounds();
                Point pt = new Point( rect.x, rect.y + rect.height );
                pt = rightToolBar.toDisplay( pt );
                rightMenu.setLocation( pt.x, pt.y );
                rightMenu.setVisible( true );
            }
        } );
        // Adding the 'Group By Property' MenuItem
        groupByProperty = new MenuItem( rightMenu, SWT.CHECK );
        groupByProperty.setText( "Group By Property" );
        groupByProperty.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent arg0 )
            {
                changeGrouping( PluginConstants.PREFS_DIFFERENCES_WIDGET_GROUPING_PROPERTY );
            }
        } );
        // Adding the 'Group By Type' MenuItem
        groupByType = new MenuItem( rightMenu, SWT.CHECK );
        groupByType.setText( "Group By Type" );
        groupByType.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent arg0 )
            {
                changeGrouping( PluginConstants.PREFS_DIFFERENCES_WIDGET_GROUPING_TYPE );
            }
        } );
        updateMenuItemsCheckStatus();

        // TableViewer
        tableViewer = new TableViewer( rightComposite, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
        tableViewer.getTable().setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        tableViewer.setContentProvider( new DifferencesWidgetPropertiesContentProvider() );
        tableViewer.setLabelProvider( new DifferencesWidgetPropertiesLabelProvider() );

        initAuthorizedPrefs();
        initPreferencesListener();
    }


    /**
     * Sets the Input of the DifferencesWidget.
     *
     * @param input
     *      the input
     */
    public void setInput( List<SchemaDifference> input )
    {
        treeViewer.setInput( input );
    }


    /**
     * Changes the Grouping option.
     *
     * @param value
     *      the value to store in the PreferenceStore
     */
    private void changeGrouping( int value )
    {
        store.setValue( PluginConstants.PREFS_DIFFERENCES_WIDGET_GROUPING, value );
        updateMenuItemsCheckStatus();
        tableViewer.refresh();
    }


    /**
     * Updates the MenuItmes 'check' state according to the value from the PreferenceStore.
     */
    private void updateMenuItemsCheckStatus()
    {
        int prefValue = store.getInt( PluginConstants.PREFS_DIFFERENCES_WIDGET_GROUPING );
        if ( prefValue == PluginConstants.PREFS_DIFFERENCES_WIDGET_GROUPING_PROPERTY )
        {
            groupByProperty.setSelection( true );
            groupByType.setSelection( false );
        }
        else if ( prefValue == PluginConstants.PREFS_DIFFERENCES_WIDGET_GROUPING_TYPE )
        {
            groupByProperty.setSelection( false );
            groupByType.setSelection( true );
        }
        else
        {
            groupByProperty.setSelection( false );
            groupByType.setSelection( false );
        }
    }


    /**
     * Initializes the Authorized Prefs IDs.
     */
    private void initAuthorizedPrefs()
    {
        authorizedPrefs = new ArrayList<String>();
        authorizedPrefs.add( PluginConstants.PREFS_SCHEMA_VIEW_LABEL );
        authorizedPrefs.add( PluginConstants.PREFS_SCHEMA_VIEW_ABBREVIATE );
        authorizedPrefs.add( PluginConstants.PREFS_SCHEMA_VIEW_ABBREVIATE_MAX_LENGTH );
        authorizedPrefs.add( PluginConstants.PREFS_SCHEMA_VIEW_SECONDARY_LABEL_DISPLAY );
        authorizedPrefs.add( PluginConstants.PREFS_SCHEMA_VIEW_SECONDARY_LABEL );
        authorizedPrefs.add( PluginConstants.PREFS_SCHEMA_VIEW_SECONDARY_LABEL_ABBREVIATE );
        authorizedPrefs.add( PluginConstants.PREFS_SCHEMA_VIEW_SECONDARY_LABEL_ABBREVIATE_MAX_LENGTH );
        authorizedPrefs.add( PluginConstants.PREFS_SCHEMA_VIEW_GROUPING );
        authorizedPrefs.add( PluginConstants.PREFS_SCHEMA_VIEW_SORTING_BY );
        authorizedPrefs.add( PluginConstants.PREFS_SCHEMA_VIEW_SORTING_ORDER );
    }


    /**
     * Initializes the listener on the preferences store
     */
    private void initPreferencesListener()
    {
        store.addPropertyChangeListener( preferenceListener );
    }


    /**
     * Disposes the SWT resources allocated by this dialog page.
     */
    public void dispose()
    {
        store.removePropertyChangeListener( preferenceListener );
    }
}
