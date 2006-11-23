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

package org.apache.directory.ldapstudio.browser.controller;


import org.apache.directory.ldapstudio.browser.controller.actions.AttributeDeleteAction;
import org.apache.directory.ldapstudio.browser.controller.actions.AttributeEditAction;
import org.apache.directory.ldapstudio.browser.controller.actions.AttributeNewAction;
import org.apache.directory.ldapstudio.browser.controller.actions.RenameAttributeAction;
import org.apache.directory.ldapstudio.browser.view.views.AttributesView;
import org.apache.directory.ldapstudio.browser.view.views.BrowserView;
import org.apache.directory.ldapstudio.browser.view.views.wrappers.ConnectionWrapper;
import org.apache.directory.ldapstudio.browser.view.views.wrappers.EntryWrapper;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;


/**
 * This class is the Controller for the Attributes View.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AttributesViewController implements IMenuListener
{
    private static final AttributesViewController instance;

    /** The controlled View */
    private AttributesView view;

    private static IAction attributeNewAction;
    private static IAction attributeEditAction;
    private static IAction attributeDeleteAction;

    // Static thread-safe singleton initializer
    static
    {
        try
        {
            instance = new AttributesViewController();
        }
        catch ( Throwable e )
        {
            throw new RuntimeException( e.getMessage() );
        }
    }


    /**
     * Use this method to get the singleton instance of the controller
     * @return
     */
    public static AttributesViewController getInstance()
    {
        return instance;
    }


    /**
     * Sets the controlled View
     * @param view the controlled View
     */
    public void setView( final AttributesView view )
    {
        this.view = view;

        // Handling selection of the Browser View to update this view
        view.getSite().getPage().addSelectionListener( BrowserView.ID, new ISelectionListener()
        {
            public void selectionChanged( IWorkbenchPart part, ISelection selection )
            {
                // Setting the new input
                view.setInput( ( ( TreeSelection ) selection ).getFirstElement() );

                // Resizing columns to fit
                view.resizeColumsToFit();
            }
        } );

        // Handling the double click modification
        view.getViewer().getTable().addSelectionListener( new SelectionAdapter()
        {
            public void widgetDefaultSelected( SelectionEvent e )
            {
                new RenameAttributeAction( view, view.getViewer().getTable(), "Rename" ).run();
            }
        } );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IMenuListener#menuAboutToShow(org.eclipse.jface.action.IMenuManager)
     */
    public void menuAboutToShow( IMenuManager manager )
    {
        manager.add( attributeNewAction );
        manager.add( attributeEditAction );
        manager.add( attributeDeleteAction );
    }


    /**
     * Creates all the actions
     */
    public void createActions()
    {
        // Creating Actions
        attributeNewAction = new AttributeNewAction( view, "New attribute" );
        attributeEditAction = new AttributeEditAction( view, "Edit attribute" );
        attributeDeleteAction = new AttributeDeleteAction( view, "Delete attribute" );

        // Disabling Actions by default
        attributeNewAction.setEnabled( false );
        attributeEditAction.setEnabled( false );
        attributeDeleteAction.setEnabled( false );

        registerUpdateActions();
    }


    /**
     * Registers a Listener on the Browser View and enable/disable the Actions
     * according to the selection
     */
    private void registerUpdateActions()
    {
        // Handling selection of the Browser View to enable/disable the Actions
        view.getSite().getPage().addSelectionListener( BrowserView.ID, new ISelectionListener()
        {
            public void selectionChanged( IWorkbenchPart part, ISelection selection )
            {
                Object selectedObject = ( ( TreeSelection ) selection ).getFirstElement();
                Table table = view.getViewer().getTable();

                if ( selectedObject == null )
                {
                    attributeNewAction.setEnabled( false );
                    table.setEnabled( false );
                }
                else
                {
                    if ( selectedObject instanceof ConnectionWrapper )
                    {
                        attributeNewAction.setEnabled( false );
                        table.setEnabled( false );
                    }
                    else if ( selectedObject instanceof EntryWrapper )
                    {
                        attributeNewAction.setEnabled( true );
                        table.setEnabled( true );
                    }
                }
            }
        } );

        // Handling selection of the Attributes View to enable/disable the Actions
        view.getSite().getPage().addSelectionListener( AttributesView.ID, new ISelectionListener()
        {
            public void selectionChanged( IWorkbenchPart part, ISelection selection )
            {
                if ( selection.isEmpty() )
                {
                    attributeEditAction.setEnabled( false );
                    attributeDeleteAction.setEnabled( false );
                }
                else
                {
                    if ( ( ( StructuredSelection ) selection ).size() == 1 )
                    {
                        attributeEditAction.setEnabled( true );
                        if ( attributeDeleteAction.getText().equals( "Delete attributes" ) )
                        {
                            attributeDeleteAction.setText( "Delete attribute" );
                            attributeDeleteAction.setToolTipText( "Delete attribute" );
                        }
                    }
                    else
                    {
                        attributeEditAction.setEnabled( false );
                        if ( attributeDeleteAction.getText().equals( "Delete attribute" ) )
                        {
                            attributeDeleteAction.setText( "Delete attributes" );
                            attributeDeleteAction.setToolTipText( "Delete attributes" );
                        }
                    }

                    attributeDeleteAction.setEnabled( true );
                }
            }
        } );
    }


    /**
     * Gets the AttibuteNewAction
     * @return the AttibuteNewAction
     */
    public IAction getAttributeNewAction()
    {
        return attributeNewAction;
    }


    /**
     * Gets the AttributeEditAction
     * @return the AttributeEditAction
     */
    public IAction getAttributeEditAction()
    {
        return attributeEditAction;
    }


    /**
     * Gets the AttributeDeleteAction
     * @return the AttributeDeleteAction
     */
    public IAction getAttributeDeleteAction()
    {
        return attributeDeleteAction;
    }

}
