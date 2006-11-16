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
import org.apache.directory.ldapstudio.browser.controller.BrowserViewController;
import org.apache.directory.ldapstudio.browser.model.Connections;
import org.apache.directory.ldapstudio.browser.model.ConnectionsEvent;
import org.apache.directory.ldapstudio.browser.model.ConnectionsListener;
import org.apache.directory.ldapstudio.browser.view.views.wrappers.TreeViewerRootNode;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;


/**
 * This class implements the Browser View of the LDAP Browser's Perspective
 */
public class BrowserView extends ViewPart implements ConnectionsListener
{
    /** The Browser View's ID */
    public static final String ID = Activator.PLUGIN_ID + ".BrowserView";
    private TreeViewer viewer;
    private BrowserViewController controller;


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl( Composite parent )
    {
        viewer = new TreeViewer( parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );

        // Initializing ContentProvider and LabelProvider
        viewer.setContentProvider( new BrowserViewContentProvider() );
        viewer.setLabelProvider( new BrowserViewLabelProvider() );

        // Adding DoubleClick behavior TODO This handling should be in the controller
        viewer.addDoubleClickListener( new IDoubleClickListener()
        {
            public void doubleClick( DoubleClickEvent event )
            {
                // What we get from the treeViewer is a StructuredSelection
                StructuredSelection selection = ( StructuredSelection ) event.getSelection();

                // Here's the real object
                Object objectSelection = selection.getFirstElement();

                viewer.setExpandedState( objectSelection, !viewer.getExpandedState( objectSelection ) );
            }
        } );

        // Creating the controller, registering the view to the controller and creating the Actions
        controller = BrowserViewController.getInstance();
        controller.setView( this );
        controller.createActions();

        // Registring the view as a Listener for Connections changes
        Connections.getInstance().addListener( this );

        // Registering the Viewer, so other views can be notified when the viewer selection changes
        getSite().setSelectionProvider( viewer );

        createContextMenu();

        createToolbarButtons();

        // Creating the First Node and displaying Connections
        viewer.setInput( TreeViewerRootNode.getInstance() );
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
        toolBarManager.add( controller.getConnectionNewAction() );
        toolBarManager.add( controller.getConnectionEditAction() );
        toolBarManager.add( controller.getConnectionDeleteAction() );
        toolBarManager.add( new Separator() );
        toolBarManager.add( controller.getRefreshAction() );
        toolBarManager.add( new Separator() );
        toolBarManager.add( controller.getEntryNewAction() );
        toolBarManager.add( controller.getEntryDeleteAction() );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus()
    {
        viewer.getControl().setFocus();
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.ViewPart#setPartName(java.lang.String)
     */
    public void setPartName( String partName )
    {
        super.setPartName( partName );
    }


    /**
     * Gets the TreeViewer of the Browser View
     * @return the TreeViewer of the Browser View
     */
    public TreeViewer getViewer()
    {
        return viewer;
    }


    /**
     * Gets the TreeViewer's ContentProvider
     * @return
     */
    public IContentProvider getContentProvider()
    {
        return viewer.getContentProvider();
    }


    public void connectionsChanged( Connections connections, ConnectionsEvent event )
    {
        Object[] expandedObjects = viewer.getExpandedElements();

        TreeViewerRootNode treeViewerRootNode = ( TreeViewerRootNode ) viewer.getInput();

        treeViewerRootNode.updateChildren( event );

        viewer.refresh( treeViewerRootNode );

        viewer.setExpandedElements( expandedObjects );
    }
}
