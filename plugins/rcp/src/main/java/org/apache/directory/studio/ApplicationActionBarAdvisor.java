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

package org.apache.directory.studio;


import org.apache.directory.studio.actions.AddExtensionAction;
import org.apache.directory.studio.actions.ManageConfigurationAction;
import org.apache.directory.studio.actions.OpenFileAction;
import org.apache.directory.studio.actions.ReportABugAction;
import org.apache.directory.studio.actions.UpdateAction;
import org.apache.directory.studio.view.ImageKeys;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
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
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.actions.NewWizardDropDownAction;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * An action bar advisor is responsible for creating, adding, and disposing of the
 * actions added to a workbench window. Each window will be populated with
 * new actions.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor
{
    private static final String OS_MACOSX = "macosx"; //$NON-NLS-1$
    private OpenFileAction openFileAction;
    private IWorkbenchAction closeAction;
    private IWorkbenchAction closeAllAction;
    private IWorkbenchAction saveAction;
    private IWorkbenchAction saveAsAction;
    private IWorkbenchAction saveAllAction;
    private IWorkbenchAction printAction;
    private IWorkbenchAction refreshAction;
    private IWorkbenchAction renameAction;
    private IWorkbenchAction moveAction;
    private IWorkbenchAction exitAction;
    private IWorkbenchAction aboutAction;
    private IWorkbenchAction preferencesAction;
    private IWorkbenchAction helpAction;
    private IWorkbenchAction dynamicHelpAction;
    private UpdateAction updateAction;
    private ManageConfigurationAction manageConfigurationAction;
    private IWorkbenchAction newAction;
    private IWorkbenchAction newDropDownAction;
    private IWorkbenchAction importAction;
    private IWorkbenchAction exportAction;
    private IWorkbenchAction propertiesAction;
    private IWorkbenchAction closePerspectiveAction;
    private IWorkbenchAction closeAllPerspectivesAction;
    private IWorkbenchAction undoAction;
    private IWorkbenchAction redoAction;
    private IWorkbenchAction cutAction;
    private IWorkbenchAction copyAction;
    private IWorkbenchAction pasteAction;
    private IWorkbenchAction deleteAction;
    private IWorkbenchAction selectAllAction;
    private IWorkbenchAction findAction;
    private IContributionItem perspectivesList;
    private IContributionItem viewsList;
    private IContributionItem reopenEditorsList;
    private ReportABugAction reportABug;
    private IWorkbenchAction backwardHistoryAction;
    private IWorkbenchAction forwardHistoryAction;
    private IWorkbenchAction nextAction;
    private IWorkbenchAction previousAction;
    private IWorkbenchAction introAction;
    private AddExtensionAction addExtensionAction;


    /**
     * Creates a new instance of ApplicationActionBarAdvisor.
     *
     * @param configurer
     *          the action bar configurer
     */
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
        newAction.setText( Messages.getString( "ApplicationActionBarAdvisor.new" ) ); //$NON-NLS-1$

        newDropDownAction = new NewWizardDropDownAction( window );
        //        new NavigationHistoryAction( window, false );

        openFileAction = new OpenFileAction( window );
        register( openFileAction );

        closeAction = ActionFactory.CLOSE.create( window );
        register( closeAction );

        closeAllAction = ActionFactory.CLOSE_ALL.create( window );
        register( closeAllAction );

        saveAction = ActionFactory.SAVE.create( window );
        register( saveAction );

        saveAsAction = ActionFactory.SAVE_AS.create( window );
        register( saveAsAction );

        saveAllAction = ActionFactory.SAVE_ALL.create( window );
        register( saveAllAction );

        printAction = ActionFactory.PRINT.create( window );
        register( printAction );

        moveAction = ActionFactory.MOVE.create( window );
        register( moveAction );

        renameAction = ActionFactory.RENAME.create( window );
        register( renameAction );

        refreshAction = ActionFactory.REFRESH.create( window );
        register( refreshAction );

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

        findAction = ActionFactory.FIND.create( window );
        register( findAction );

        closePerspectiveAction = ActionFactory.CLOSE_PERSPECTIVE.create( window );
        register( closePerspectiveAction );

        closeAllPerspectivesAction = ActionFactory.CLOSE_ALL_PERSPECTIVES.create( window );
        register( closeAllPerspectivesAction );

        aboutAction = ActionFactory.ABOUT.create( window );
        aboutAction.setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Application.PLUGIN_ID,
            ImageKeys.ABOUT ) );
        register( aboutAction );

        preferencesAction = ActionFactory.PREFERENCES.create( window );
        preferencesAction.setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Application.PLUGIN_ID,
            ImageKeys.SHOW_PREFERENCES ) );
        register( preferencesAction );

        updateAction = new UpdateAction( window );
        updateAction.setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Application.PLUGIN_ID,
            ImageKeys.SEARCH_UPDATES ) );
        register( updateAction );

        addExtensionAction = new AddExtensionAction( window );
        register( addExtensionAction );

        manageConfigurationAction = new ManageConfigurationAction( window );
        manageConfigurationAction.setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin(
            Application.PLUGIN_ID, ImageKeys.MANAGE_CONFIGURATION ) );
        register( manageConfigurationAction );

        helpAction = ActionFactory.HELP_CONTENTS.create( window );
        register( helpAction );

        dynamicHelpAction = ActionFactory.DYNAMIC_HELP.create( window );
        register( dynamicHelpAction );

        viewsList = ContributionItemFactory.VIEWS_SHORTLIST.create( window );
        perspectivesList = ContributionItemFactory.PERSPECTIVES_SHORTLIST.create( window );
        reopenEditorsList = ContributionItemFactory.REOPEN_EDITORS.create( window );

        reportABug = new ReportABugAction( window );
        reportABug.setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Application.PLUGIN_ID,
            ImageKeys.REPORT_BUG ) );
        register( reportABug );

        forwardHistoryAction = ActionFactory.FORWARD_HISTORY.create( window );
        register( forwardHistoryAction );

        backwardHistoryAction = ActionFactory.BACKWARD_HISTORY.create( window );
        register( backwardHistoryAction );

        nextAction = ActionFactory.NEXT.create( window );
        register( nextAction );

        previousAction = ActionFactory.PREVIOUS.create( window );
        register( previousAction );

        introAction = ActionFactory.INTRO.create( window );
        introAction.setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Application.PLUGIN_ID,
            ImageKeys.INTRO ) );
        register( introAction );

    }


    /**
     * Populates the Menu Bar
     */
    protected void fillMenuBar( IMenuManager menuBar )
    {
        // Getting the OS
        String os = Platform.getOS();

        // Creating menus
        MenuManager fileMenu = new MenuManager(
            Messages.getString( "ApplicationActionBarAdvisor.file" ), IWorkbenchActionConstants.M_FILE ); //$NON-NLS-1$
        MenuManager editMenu = new MenuManager(
            Messages.getString( "ApplicationActionBarAdvisor.edit" ), IWorkbenchActionConstants.M_EDIT ); //$NON-NLS-1$
        MenuManager navigateMenu = new MenuManager(
            Messages.getString( "ApplicationActionBarAdvisor.navigate" ), IWorkbenchActionConstants.M_NAVIGATE ); //$NON-NLS-1$
        MenuManager windowMenu = new MenuManager(
            Messages.getString( "ApplicationActionBarAdvisor.windows" ), IWorkbenchActionConstants.M_WINDOW ); //$NON-NLS-1$
        MenuManager helpMenu = new MenuManager(
            Messages.getString( "ApplicationActionBarAdvisor.help" ), IWorkbenchActionConstants.M_HELP ); //$NON-NLS-1$
        MenuManager hiddenMenu = new MenuManager( "Hidden", "org.apache.directory.studio.rcp.hidden" ); //$NON-NLS-1$ //$NON-NLS-2$
        hiddenMenu.setVisible( false );

        // Adding menus
        menuBar.add( fileMenu );
        menuBar.add( editMenu );
        menuBar.add( navigateMenu );
        // Add a group marker indicating where action set menus will appear.
        menuBar.add( new GroupMarker( IWorkbenchActionConstants.MB_ADDITIONS ) );
        menuBar.add( windowMenu );
        menuBar.add( helpMenu );
        menuBar.add( hiddenMenu );

        // Populating File Menu
        fileMenu.add( newAction );
        fileMenu.add( new GroupMarker( IWorkbenchActionConstants.NEW_EXT ) );
        fileMenu.add( openFileAction );
        fileMenu.add( new GroupMarker( IWorkbenchActionConstants.OPEN_EXT ) );
        fileMenu.add( new Separator() );
        fileMenu.add( closeAction );
        fileMenu.add( closeAllAction );
        fileMenu.add( new GroupMarker( IWorkbenchActionConstants.CLOSE_EXT ) );
        fileMenu.add( new Separator() );
        fileMenu.add( saveAction );
        fileMenu.add( saveAsAction );
        fileMenu.add( saveAllAction );
        fileMenu.add( new GroupMarker( IWorkbenchActionConstants.SAVE_EXT ) );
        fileMenu.add( new Separator() );
        fileMenu.add( refreshAction );
        fileMenu.add( new Separator() );
        fileMenu.add( printAction );
        fileMenu.add( new GroupMarker( IWorkbenchActionConstants.PRINT_EXT ) );
        fileMenu.add( new Separator() );
        fileMenu.add( importAction );
        fileMenu.add( exportAction );
        fileMenu.add( new GroupMarker( IWorkbenchActionConstants.IMPORT_EXT ) );
        fileMenu.add( new Separator() );
        fileMenu.add( propertiesAction );
        fileMenu.add( reopenEditorsList );
        fileMenu.add( new GroupMarker( IWorkbenchActionConstants.MRU ) );
        if ( ApplicationActionBarAdvisor.OS_MACOSX.equalsIgnoreCase( os ) )
        {
            // We hide the exit (quit) action, it will be added by the "Carbon" plugin
            hiddenMenu.add( exitAction );
        }
        else
        {
            fileMenu.add( new Separator() );
            fileMenu.add( exitAction );
        }

        // Populating Edit Menu
        editMenu.add( undoAction );
        editMenu.add( redoAction );
        editMenu.add( new Separator() );
        editMenu.add( cutAction );
        editMenu.add( copyAction );
        editMenu.add( pasteAction );
        editMenu.add( new Separator() );
        editMenu.add( deleteAction );
        editMenu.add( selectAllAction );
        editMenu.add( new Separator() );
        editMenu.add( moveAction );
        editMenu.add( renameAction );
        editMenu.add( new Separator() );
        editMenu.add( findAction );

        // Populating Navigate Menu
        navigateMenu.add( nextAction );
        navigateMenu.add( previousAction );
        navigateMenu.add( new Separator( IWorkbenchActionConstants.MB_ADDITIONS ) );
        navigateMenu.add( new GroupMarker( IWorkbenchActionConstants.NAV_END ) );
        navigateMenu.add( new Separator() );
        navigateMenu.add( backwardHistoryAction );
        navigateMenu.add( forwardHistoryAction );

        // Window 
        MenuManager perspectiveMenu = new MenuManager( Messages
            .getString( "ApplicationActionBarAdvisor.openPerspective" ), "openPerspective" ); //$NON-NLS-1$ //$NON-NLS-2$
        perspectiveMenu.add( perspectivesList );
        windowMenu.add( perspectiveMenu );
        MenuManager viewMenu = new MenuManager( Messages.getString( "ApplicationActionBarAdvisor.showView" ) ); //$NON-NLS-1$
        viewMenu.add( viewsList );
        windowMenu.add( viewMenu );
        windowMenu.add( new Separator() );
        windowMenu.add( closePerspectiveAction );
        windowMenu.add( closeAllPerspectivesAction );
        if ( ApplicationActionBarAdvisor.OS_MACOSX.equalsIgnoreCase( os ) )
        {
            // We hide the preferences action, it will be added by the "Carbon" plugin
            hiddenMenu.add( preferencesAction );
        }
        else
        {
            windowMenu.add( new Separator() );
            windowMenu.add( preferencesAction );
        }

        // Help
        helpMenu.add( introAction );
        helpMenu.add( new Separator() );
        helpMenu.add( helpAction );
        helpMenu.add( dynamicHelpAction );
        helpMenu.add( new Separator() );
        helpMenu.add( reportABug );
        helpMenu.add( new Separator() );
        MenuManager softwareUpdates = new MenuManager( Messages
            .getString( "ApplicationActionBarAdvisor.Software_Updates" ), "softwareUpdates" ); //$NON-NLS-1$ //$NON-NLS-2$
        softwareUpdates.add( addExtensionAction );
        softwareUpdates.add( updateAction );
        softwareUpdates.add( manageConfigurationAction );
        helpMenu.add( softwareUpdates );
        if ( ApplicationActionBarAdvisor.OS_MACOSX.equalsIgnoreCase( os ) )
        {
            // We hide the about action, it will be added by the "Carbon" plugin
            hiddenMenu.add( aboutAction );
        }
        else
        {
            helpMenu.add( new Separator() );
            helpMenu.add( aboutAction );
        }
    }


    /**
     * Populates the Cool Bar
     */
    protected void fillCoolBar( ICoolBarManager coolBar )
    {
        // add main tool bar
        IToolBarManager toolbar = new ToolBarManager( SWT.FLAT | SWT.RIGHT );
        toolbar.add( newDropDownAction );
        toolbar.add( saveAction );
        toolbar.add( printAction );
        toolbar.add( preferencesAction );
        coolBar.add( new ToolBarContributionItem( toolbar, Application.PLUGIN_ID + ".toolbar" ) ); //$NON-NLS-1$

        // add marker for additions
        coolBar.add( new GroupMarker( IWorkbenchActionConstants.MB_ADDITIONS ) );

        // add navigation tool bar
        // some actions are added from org.eclipse.ui.editor to the HISTORY_GROUP
        IToolBarManager navToolBar = new ToolBarManager( SWT.FLAT | SWT.RIGHT );
        navToolBar.add( new Separator( IWorkbenchActionConstants.HISTORY_GROUP ) );
        navToolBar.add( backwardHistoryAction );
        navToolBar.add( forwardHistoryAction );
        coolBar.add( new ToolBarContributionItem( navToolBar, IWorkbenchActionConstants.TOOLBAR_NAVIGATE ) );
    }
}
