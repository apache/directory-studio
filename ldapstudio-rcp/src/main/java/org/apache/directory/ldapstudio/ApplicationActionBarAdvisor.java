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

    // Actions - important to allocate these only in makeActions, and then use them
    // in the fill methods.  This ensures that the actions aren't recreated
    // when fillActionBars is called with FILL_PROXY.
    private IWorkbenchAction closeEditorAction;
    private IWorkbenchAction closeAllEditorsAction;

    private IWorkbenchAction saveEditorAction;
    private IWorkbenchAction saveAllEditorsAction;

    private IWorkbenchAction exitAction;
    private IWorkbenchAction aboutAction;
    private IWorkbenchAction preferencesAction;
    private IWorkbenchAction helpAction;
    private UpdateAction updateAction;
    private AddExtensionAction addExtensionAction;
    private ManageExtensionsAction manageExtensionsAction;


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

        closeEditorAction = ActionFactory.CLOSE.create( window );
        register( closeEditorAction );

        closeAllEditorsAction = ActionFactory.CLOSE_ALL.create( window );
        register( closeAllEditorsAction );

        saveEditorAction = ActionFactory.SAVE.create( window );
        saveEditorAction.setText( Messages.getString( "ApplicationActionBarAdvisor.Save_editor" ) ); //$NON-NLS-1$
        register( saveEditorAction );

        saveAllEditorsAction = ActionFactory.SAVE_ALL.create( window );
        saveAllEditorsAction.setText( Messages.getString( "ApplicationActionBarAdvisor.Save_all_editors" ) ); //$NON-NLS-1$
        register( saveAllEditorsAction );

        exitAction = ActionFactory.QUIT.create( window );
        exitAction.setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Application.PLUGIN_ID,
            ImageKeys.EXIT ) );
        exitAction.setText( Messages.getString( "ApplicationActionBarAdvisor.Quit_LDAP_Studio" ) ); //$NON-NLS-1$
        exitAction.setToolTipText( Messages.getString( "ApplicationActionBarAdvisor.Quit_LDAP_Studio" ) ); //$NON-NLS-1$
        register( exitAction );

        aboutAction = ActionFactory.ABOUT.create( window );
        aboutAction.setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Application.PLUGIN_ID,
            ImageKeys.ABOUT ) );
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
        MenuManager helpMenu = new MenuManager( "&Help", IWorkbenchActionConstants.M_HELP ); //$NON-NLS-1$

        menuBar.add( fileMenu );
        // Add a group marker indicating where action set menus will appear.
        menuBar.add( new GroupMarker( IWorkbenchActionConstants.MB_ADDITIONS ) );
        menuBar.add( helpMenu );

        // File
        fileMenu.add( preferencesAction );
        fileMenu.add( new Separator() );
        fileMenu.add( closeEditorAction );
        fileMenu.add( closeAllEditorsAction );
        fileMenu.add( new Separator() );
        fileMenu.add( saveEditorAction );
        fileMenu.add( saveAllEditorsAction );
        fileMenu.add( new Separator() );
        fileMenu.add( exitAction );

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

        coolBar.add( new ToolBarContributionItem( toolbar, "main" ) ); //$NON-NLS-1$

        toolbar.add( exitAction );
        toolbar.add( preferencesAction );
        toolbar.add( aboutAction );
        toolbar.add( new Separator() );
    }
}
