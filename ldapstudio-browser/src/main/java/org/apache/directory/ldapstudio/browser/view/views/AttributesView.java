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

package org.apache.directory.ldapstudio.browser.view.views;


import org.apache.directory.ldapstudio.browser.Activator;
import org.apache.directory.ldapstudio.browser.controller.AttributesViewController;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;


/**
 * This class implements the Attributes View of the LDAP Browser's Perspective
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AttributesView extends ViewPart
{
    /** The Attributes View's ID */
    public static final String ID = Activator.PLUGIN_ID + ".AttributesView";
    private Table table;
    private TableViewer viewer;
    private AttributesViewController controller;


    public AttributesView()
    {
        controller = AttributesViewController.getInstance();
    }


    @Override
    public void createPartControl( Composite parent )
    {
        table = new Table( parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL );
        table.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, true ) );
        table.setLinesVisible( true );
        table.setHeaderVisible( true );
        table.setEnabled( false ); // The table is disabled by default since nothing is selected in the Browser View

        viewer = new TableViewer( table );
        viewer.setUseHashlookup( true );

        // Adding columns headers
        TableColumn attributeColumn = new TableColumn( table, SWT.NONE );
        attributeColumn.setText( "Attribute" );
        TableColumn valueColumn = new TableColumn( table, SWT.NONE );
        valueColumn.setText( "Value" );

        // Initializing ContentProvider and LabelProvider
        viewer.setContentProvider( new AttributesViewContentProvider() );
        viewer.setLabelProvider( new AttributesViewLabelProvider() );

        // Displaying and resizing the columns
        resizeColumsToFit();

        // Registering the view to the controller and creating the Actions
        controller.setView( this );
        controller.createActions();

        // Registering the Viewer, so other views can be notified when the viewer selection changes
        getSite().setSelectionProvider( viewer );

        createContextMenu();

        createToolbarButtons();
    }


    private void createContextMenu()
    {
        // Initialization of the Menu Manager used to display context menu
        MenuManager manager = new MenuManager();
        manager.setRemoveAllWhenShown( true );
        manager.addMenuListener( controller );
        // Set the context menu to the table viewer
        viewer.getControl().setMenu( manager.createContextMenu( viewer.getControl() ) );
        // Register the context menu to enable extension actions
        getSite().registerContextMenu( manager, viewer );
    }


    private void createToolbarButtons()
    {
        IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();

        // Adding the Actions
        toolBarManager.add( controller.getAttributeNewAction() );
        toolBarManager.add( controller.getAttributeEditAction() );
        toolBarManager.add( controller.getAttributeDeleteAction() );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus()
    {
        table.setFocus();
    }


    /**
     * Sets the input of the TableViewer
     * @param input the input to send to the TableViewer
     */
    public void setInput( Object input )
    {
        viewer.setInput( input );
    }


    /**
     * Resizes the columns to fit the size of the cells
     */
    public void resizeColumsToFit()
    {
        // Resizing the first column
        table.getColumn( 0 ).pack();
        // Adding a little space to the first column
        table.getColumn( 0 ).setWidth( table.getColumn( 0 ).getWidth() + 5 );
        // Resizing the second column
        table.getColumn( 1 ).pack();
    }


    public TableItem getSelectedAttributeTableItem()
    {
        return table.getSelection()[0];
    }


    public TableViewer getViewer()
    {
        return viewer;
    }


    /**
     * Refreshes the UI
     */
    public void refresh()
    {
        // Getting the selected Entry in the Browser View
        BrowserView browserView = ( BrowserView ) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .findView( BrowserView.ID );
        setInput( ( ( TreeSelection ) browserView.getViewer().getSelection() ).getFirstElement() );

        // Resizing columns to fit
        resizeColumsToFit();
    }
}
