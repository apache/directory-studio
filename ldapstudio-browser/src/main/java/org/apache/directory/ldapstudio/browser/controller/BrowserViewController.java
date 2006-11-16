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


import org.apache.directory.ldapstudio.browser.controller.actions.ConnectionDeleteAction;
import org.apache.directory.ldapstudio.browser.controller.actions.ConnectionEditAction;
import org.apache.directory.ldapstudio.browser.controller.actions.ConnectionNewAction;
import org.apache.directory.ldapstudio.browser.controller.actions.EntryDeleteAction;
import org.apache.directory.ldapstudio.browser.controller.actions.EntryNewAction;
import org.apache.directory.ldapstudio.browser.controller.actions.RefreshAction;
import org.apache.directory.ldapstudio.browser.view.views.BrowserView;
import org.apache.directory.ldapstudio.browser.view.views.wrappers.ConnectionWrapper;
import org.apache.directory.ldapstudio.browser.view.views.wrappers.EntryWrapper;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;


/**
 * This class is the Controller for the Browser View.
 */
public class BrowserViewController implements IMenuListener
{
    private static final BrowserViewController instance;

    /** The controlled view */
    private BrowserView view;

    private static IAction connectionNewAction;
    private static IAction connectionEditAction;
    private static IAction connectionDeleteAction;
    private static IAction refreshAction;
    private static IAction entryNewAction;
    private static IAction entryDeleteAction;

    // Static thread-safe singleton initializer
    static
    {
        try
        {
            instance = new BrowserViewController();
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
    public static BrowserViewController getInstance()
    {
        return instance;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IMenuListener#menuAboutToShow(org.eclipse.jface.action.IMenuManager)
     */
    public void menuAboutToShow( IMenuManager manager )
    {
        manager.add( connectionNewAction );
        manager.add( connectionEditAction );
        manager.add( connectionDeleteAction );
        manager.add( new Separator() );
        manager.add( refreshAction );
        manager.add( new Separator() );
        manager.add( entryNewAction );
        manager.add( entryDeleteAction );
    }


    /**
     * Sets the controlled View
     * @param view the controlled View
     */
    public void setView( BrowserView view )
    {
        this.view = view;
    }


    /**
     * Creates all the actions
     */
    public void createActions()
    {
        connectionNewAction = new ConnectionNewAction( view, "New connection" );
        connectionDeleteAction = new ConnectionDeleteAction( view, "Delete connection" );
        connectionEditAction = new ConnectionEditAction( view, "Edit connection" );
        refreshAction = new RefreshAction( view, "Refresh" );
        entryNewAction = new EntryNewAction( view, "New entry" );
        entryDeleteAction = new EntryDeleteAction( view, "Delete entry" );

        registerUpdateActions();
    }


    /**
     * Registers a Listener on the Browser View and enable/disable the Actions
     * according to the selection
     */
    private void registerUpdateActions()
    {
        // Handling selection of the Browser View to enable/disable the Actions
        view.getSite().getPage().addPostSelectionListener( BrowserView.ID, new ISelectionListener()
        {
            public void selectionChanged( IWorkbenchPart part, ISelection selection )
            {
                Object selectedObject = ( ( TreeSelection ) selection ).getFirstElement();

                if ( selectedObject == null )
                {
                    connectionEditAction.setEnabled( false );
                    connectionDeleteAction.setEnabled( false );
                    entryNewAction.setEnabled( false );
                    entryDeleteAction.setEnabled( false );
                }
                else
                {
                    if ( selectedObject instanceof ConnectionWrapper )
                    {
                        connectionEditAction.setEnabled( true );
                        connectionDeleteAction.setEnabled( true );
                        entryNewAction.setEnabled( false );
                        entryDeleteAction.setEnabled( false );
                    }
                    else if ( selectedObject instanceof EntryWrapper )
                    {
                        connectionEditAction.setEnabled( false );
                        connectionDeleteAction.setEnabled( false );
                        entryNewAction.setEnabled( true );
                        entryDeleteAction.setEnabled( true );
                    }
                }
            }
        } );
    }


    /**
     * Gets the ConnectionDeleteAction
     * @return the ConnectionDeleteAction
     */
    public IAction getConnectionDeleteAction()
    {
        return connectionDeleteAction;
    }


    /**
     * Gets the ConnectionNewAction
     * @return the ConnectionNewAction
     */
    public IAction getConnectionNewAction()
    {
        return connectionNewAction;
    }


    /**
     * Gets the ConnectionEditAction
     * @return the ConnectionEditAction
     */
    public IAction getConnectionEditAction()
    {
        return connectionEditAction;
    }


    /**
     * Gets the RefreshAction
     * @return the RefreshAction
     */
    public IAction getRefreshAction()
    {
        return refreshAction;
    }


    /**
     * Gets the EntryDeleteAction
     * @return the EntryDeleteAction
     */
    public IAction getEntryDeleteAction()
    {
        return entryDeleteAction;
    }


    /**
     * Gets the EntryNewAction
     * @return the EntryNewAction
     */
    public IAction getEntryNewAction()
    {
        return entryNewAction;
    }

}
