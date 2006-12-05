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

package org.apache.directory.ldapstudio;


import org.apache.directory.ldapstudio.actions.AddExtensionAction;
import org.apache.directory.ldapstudio.actions.ManageExtensionsAction;
import org.apache.directory.ldapstudio.actions.UpdateAction;
import org.apache.directory.ldapstudio.view.ImageKeys;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * An action bar advisor is responsible for creating, adding, and disposing of the
 * actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor
{
    private IWorkbenchAction closeAction;
    private IWorkbenchAction closeAllAction;
    private IWorkbenchAction saveAction;
    private IWorkbenchAction saveAllAction;
    private IWorkbenchAction exitAction;
    private IWorkbenchAction aboutAction;
    private IWorkbenchAction preferencesAction;
    private IWorkbenchAction helpAction;
    private UpdateAction updateAction;
    private AddExtensionAction addExtensionAction;
    private ManageExtensionsAction manageExtensionsAction;
    private IWorkbenchAction newAction;
    private IWorkbenchAction importAction;
    private IWorkbenchAction exportAction;
    private IWorkbenchAction propertiesAction;
    private IWorkbenchAction openPerspectiveAction;
    private IWorkbenchAction closePerspectiveAction;
    private IWorkbenchAction closeAllPerspectivesAction;
    private IWorkbenchAction showViewAction;
    private IWorkbenchAction undoAction;
    private IWorkbenchAction redoAction;
    private IWorkbenchAction cutAction;
    private IWorkbenchAction copyAction;
    private IWorkbenchAction pasteAction;
    private IWorkbenchAction deleteAction;
    private IWorkbenchAction selectAllAction;


    public ApplicationActionBarAdvisor( IActionBarConfigurer configurer )
    {
        super( configurer );
    }


    /**
     * Creates the actions and registers them.
     * Registering is needed to ensure that key bindings work.
     * The corresponding commands keybindings are defined in the plugin.xml file.
     * Registering also provides automatic disposal of the actions when
     * the window is closed.
     */
    protected void makeActions( final IWorkbenchWindow window )
    {
        // Creates the actions and registers them.
        // Registering is needed to ensure that key bindings work.
        // The corresponding commands keybindings are defined in the plugin.xml file.
        // Registering also provides automatic disposal of the actions when
        // the window is closed.

        newAction = ActionFactory.NEW.create( window );
        register( newAction );
        newAction.setText( "New..." );
        
        closeAction = ActionFactory.CLOSE.create( window );
        register( closeAction );

        closeAllAction = ActionFactory.CLOSE_ALL.create( window );
        register( closeAllAction );

        saveAction = ActionFactory.SAVE.create( window );
        register( saveAction );

        saveAllAction = ActionFactory.SAVE_ALL.create( window );
        register( saveAllAction );
        
        importAction = ActionFactory.IMPORT.create( window );
        register( importAction );
        
        exportAction = ActionFactory.EXPORT.create( window );
        register( exportAction );
        
        propertiesAction = ActionFactory.PROPERTIES.create( window );
        register( propertiesAction );

        exitAction = ActionFactory.QUIT.create( window );
        register( exitAction );
        
        undoAction = ActionFactory.UNDO.create( window );
        register( undoAction );
        
        redoAction = ActionFactory.REDO.create( window );
        register( redoAction );
        
        cutAction = ActionFactory.CUT.create( window );
        register( cutAction );
        
        copyAction = ActionFactory.COPY.create( window );
        register( copyAction );
        
        pasteAction = ActionFactory.PASTE.create( window );
        register( pasteAction );
        
        deleteAction = ActionFactory.DELETE.create( window );
        register( deleteAction );
        
        selectAllAction = ActionFactory.SELECT_ALL.create( window );
        register( selectAllAction );
        
        openPerspectiveAction = ActionFactory.OPEN_PERSPECTIVE_DIALOG.create( window );
        register( openPerspectiveAction );
        
        showViewAction = ActionFactory.SHOW_VIEW_MENU.create( window );
        register( showViewAction );
        
        closePerspectiveAction = ActionFactory.CLOSE_PERSPECTIVE.create( window );
        register( closePerspectiveAction );
        
        closeAllPerspectivesAction = ActionFactory.CLOSE_ALL_PERSPECTIVES.create( window );
        register( closeAllPerspectivesAction );
        
        aboutAction = ActionFactory.ABOUT.create( window );
        register( aboutAction );

        preferencesAction = ActionFactory.PREFERENCES.create( window );
        preferencesAction.setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Application.PLUGIN_ID,
            ImageKeys.SHOW_PREFERENCES ) );
        register( preferencesAction );

        updateAction = new UpdateAction( window );
        register( updateAction );

        addExtensionAction = new AddExtensionAction( window );
        register( addExtensionAction );

        manageExtensionsAction = new ManageExtensionsAction( window );
        register( manageExtensionsAction );

        helpAction = ActionFactory.HELP_CONTENTS.create( window );
        register( helpAction );
    }


    /**
     * Populates the Menu Bar
     */
    protected void fillMenuBar( IMenuManager menuBar )
    {
        MenuManager fileMenu = new MenuManager( "&File", IWorkbenchActionConstants.M_FILE ); //$NON-NLS-1$
        MenuManager editMenu = new MenuManager( "&Edit", IWorkbenchActionConstants.M_EDIT ); //$NON-NLS-1$
        MenuManager windowMenu = new MenuManager( "&Window", IWorkbenchActionConstants.M_WINDOW ); //$NON-NLS-1$
        MenuManager helpMenu = new MenuManager( "&Help", IWorkbenchActionConstants.M_HELP ); //$NON-NLS-1$

        // Adding menus
        menuBar.add( fileMenu );
        menuBar.add( editMenu );
        // Add a group marker indicating where action set menus will appear.
        menuBar.add( new GroupMarker( IWorkbenchActionConstants.MB_ADDITIONS ) );
        menuBar.add( windowMenu );
        menuBar.add( helpMenu );

        // Populating File Menu
        fileMenu.add( newAction );
        fileMenu.add( new Separator() );
        fileMenu.add( closeAction );
        fileMenu.add( closeAllAction );
        fileMenu.add( new Separator() );
        fileMenu.add( saveAction );
        fileMenu.add( saveAllAction );
        fileMenu.add( new Separator() );
        fileMenu.add( importAction );
        fileMenu.add( exportAction );
        fileMenu.add( new Separator() );
        fileMenu.add( propertiesAction );
        fileMenu.add( new Separator() );
        fileMenu.add( exitAction );
        
        // Population Edit Menu
        editMenu.add( undoAction );
        editMenu.add( redoAction );
        editMenu.add( new Separator() );
        editMenu.add( cutAction );
        editMenu.add( copyAction );
        editMenu.add( pasteAction );
        editMenu.add( new Separator() );
        editMenu.add( deleteAction );
        editMenu.add( selectAllAction );
        
        // Window 
        windowMenu.add( openPerspectiveAction );
        windowMenu.add( showViewAction );
        windowMenu.add( new Separator() );
        windowMenu.add( closePerspectiveAction );
        windowMenu.add( closeAllPerspectivesAction );
        windowMenu.add( new Separator() );
        windowMenu.add( preferencesAction );

        // Help
        helpMenu.add( helpAction );
        MenuManager softwareUpdates = new MenuManager( Messages
            .getString( "ApplicationActionBarAdvisor.Software_Updates" ), "softwareUpdates" ); //$NON-NLS-1$ //$NON-NLS-2$
        softwareUpdates.add( updateAction );
        softwareUpdates.add( addExtensionAction );
        softwareUpdates.add( manageExtensionsAction );
        helpMenu.add( softwareUpdates );
        helpMenu.add( aboutAction );
    }


    /**
     * Populates the Cool Bar
     */
    protected void fillCoolBar( ICoolBarManager coolBar )
    {
        IToolBarManager toolbar = new ToolBarManager( SWT.FLAT | SWT.RIGHT );
        coolBar.add( new ToolBarContributionItem( toolbar, Application.PLUGIN_ID + ".toolbar" ) ); //$NON-NLS-1$
        toolbar.add( preferencesAction );
    }
}
