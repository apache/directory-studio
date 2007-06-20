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


import java.util.List;

import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.PluginConstants;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.Difference;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.TableViewer;
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
import org.eclipse.swt.widgets.Table;
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
    /** The TableViewer */
    private TableViewer viewer;

    /** The PreferenceStore*/
    private IPreferenceStore store;

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
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginBottom = 0;
        gridLayout.marginHeight = 0;
        gridLayout.marginLeft = 0;
        gridLayout.marginRight = 0;
        gridLayout.marginTop = 0;
        gridLayout.marginWidth = 0;
        composite.setLayout( gridLayout );

        // ToolBar
        final ToolBar toolBar = new ToolBar( composite, SWT.HORIZONTAL );
        toolBar.setLayoutData( new GridData( SWT.RIGHT, SWT.NONE, false, false ) );
        // Creating the 'Menu' ToolBar item
        final ToolItem menuToolItem = new ToolItem( toolBar, SWT.PUSH );
        menuToolItem.setImage( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
            PluginConstants.IMG_TOOLBAR_MENU ).createImage() );
        menuToolItem.setToolTipText( "Menu" );
        // Creating the associated Menu
        final Menu menu = new Menu( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.POP_UP );
        // Adding the action to display the Menu when the item is clicked
        menuToolItem.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                Rectangle rect = menuToolItem.getBounds();
                Point pt = new Point( rect.x, rect.y + rect.height );
                pt = toolBar.toDisplay( pt );
                menu.setLocation( pt.x, pt.y );
                menu.setVisible( true );
            }
        } );
        // Adding the 'Group By Property' MenuItem
        groupByProperty = new MenuItem( menu, SWT.CHECK );
        groupByProperty.setText( "Group By Property" );
        groupByProperty.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent arg0 )
            {
                changeGrouping( PluginConstants.PREFS_DIFFERENCES_WIDGET_GROUPING_PROPERTY );
            }
        } );
        // Adding the 'Group By Type' MenuItem
        groupByType = new MenuItem( menu, SWT.CHECK );
        groupByType.setText( "Group By Type" );
        groupByType.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent arg0 )
            {
                changeGrouping( PluginConstants.PREFS_DIFFERENCES_WIDGET_GROUPING_TYPE );
            }
        } );
        updateMenuItemsCheckStatus();

        // Table
        Table table = new Table( composite, SWT.BORDER );
        table.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // TableViewer
        viewer = new TableViewer( table );
        viewer.setContentProvider( new DifferencesWidgetContentProvider() );
        viewer.setLabelProvider( new DifferencesWidgetLabelProvider() );
    }


    /**
     * Sets the Input of the TableViewer.
     *
     * @param input
     *      the input
     */
    public void setInput( List<Difference> input )
    {
        viewer.setInput( input );
    }


    /**
     * Gets the TableViewer used in the Widget.
     *
     * @return
     *      the TableViewer used in the Widget
     */
    public TableViewer getViewer()
    {
        return viewer;
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
        viewer.refresh();
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
}
