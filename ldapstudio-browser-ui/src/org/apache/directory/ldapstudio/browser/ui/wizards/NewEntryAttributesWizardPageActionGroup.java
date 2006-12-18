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

package org.apache.directory.ldapstudio.browser.ui.wizards;


import org.apache.directory.ldapstudio.browser.ui.actions.DeleteAllValuesAction;
import org.apache.directory.ldapstudio.browser.ui.actions.NewAttributeAction;
import org.apache.directory.ldapstudio.browser.ui.actions.proxy.EntryEditorActionProxy;
import org.apache.directory.ldapstudio.browser.ui.widgets.entryeditor.EditAttributeDescriptionAction;
import org.apache.directory.ldapstudio.browser.ui.widgets.entryeditor.EntryEditorWidget;
import org.apache.directory.ldapstudio.browser.ui.widgets.entryeditor.EntryEditorWidgetActionGroup;
import org.apache.directory.ldapstudio.browser.ui.widgets.entryeditor.EntryEditorWidgetConfiguration;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;


public class NewEntryAttributesWizardPageActionGroup extends EntryEditorWidgetActionGroup
{

    private EditAttributeDescriptionAction editAttributeDescriptionAction;

    private static final String newAttributeAction = "newAttributeAction";

    private static final String deleteAllValuesAction = "deleteAllValuesAction";


    public NewEntryAttributesWizardPageActionGroup( EntryEditorWidget mainWidget,
        EntryEditorWidgetConfiguration configuration )
    {
        super( mainWidget, configuration );
        TreeViewer viewer = mainWidget.getViewer();

        this.editAttributeDescriptionAction = new EditAttributeDescriptionAction( viewer );

        this.entryEditorActionMap.put( newAttributeAction,
            new EntryEditorActionProxy( viewer, new NewAttributeAction() ) );
        this.entryEditorActionMap.put( deleteAllValuesAction, new EntryEditorActionProxy( viewer,
            new DeleteAllValuesAction() ) );

    }


    public void dispose()
    {
        if ( this.editAttributeDescriptionAction != null )
        {

            this.editAttributeDescriptionAction.dispose();
            this.editAttributeDescriptionAction = null;
        }
        super.dispose();
    }


    public void fillToolBar( IToolBarManager toolBarManager )
    {
        toolBarManager.add( ( IAction ) this.entryEditorActionMap.get( newValueAction ) );
        toolBarManager.add( ( IAction ) this.entryEditorActionMap.get( newAttributeAction ) );
        toolBarManager.add( new Separator() );
        toolBarManager.add( ( IAction ) this.entryEditorActionMap.get( deleteAction ) );
        toolBarManager.add( ( IAction ) this.entryEditorActionMap.get( deleteAllValuesAction ) );
        toolBarManager.add( new Separator() );
        toolBarManager.add( this.showQuickFilterAction );
        toolBarManager.update( true );
    }


    public void menuAboutToShow( IMenuManager menuManager )
    {

        // new
        menuManager.add( ( IAction ) this.entryEditorActionMap.get( newAttributeAction ) );
        menuManager.add( ( IAction ) this.entryEditorActionMap.get( newValueAction ) );
        menuManager.add( new Separator() );

        // copy, paste, delete
        menuManager.add( ( IAction ) this.entryEditorActionMap.get( copyAction ) );
        menuManager.add( ( IAction ) this.entryEditorActionMap.get( pasteAction ) );
        menuManager.add( ( IAction ) this.entryEditorActionMap.get( deleteAction ) );
        menuManager.add( ( IAction ) this.entryEditorActionMap.get( selectAllAction ) );
        MenuManager copyMenuManager = new MenuManager( "Advanced" );
        copyMenuManager.add( ( IAction ) this.entryEditorActionMap.get( deleteAllValuesAction ) );
        menuManager.add( copyMenuManager );
        menuManager.add( new Separator() );

        // edit
        menuManager.add( this.editAttributeDescriptionAction );
        super.addEditMenu( menuManager );
        menuManager.add( new Separator() );

        // properties
        menuManager.add( ( IAction ) this.entryEditorActionMap.get( propertyDialogAction ) );
    }


    public void activateGlobalActionHandlers()
    {

        super.activateGlobalActionHandlers();

        ICommandService commandService = ( ICommandService ) PlatformUI.getWorkbench().getAdapter(
            ICommandService.class );
        if ( commandService != null )
        {
            IAction naa = ( IAction ) this.entryEditorActionMap.get( newAttributeAction );
            commandService.getCommand( naa.getActionDefinitionId() ).setHandler( new ActionHandler( naa ) );
            commandService.getCommand( editAttributeDescriptionAction.getActionDefinitionId() ).setHandler(
                new ActionHandler( editAttributeDescriptionAction ) );
        }
    }


    public void deactivateGlobalActionHandlers()
    {

        super.deactivateGlobalActionHandlers();

        ICommandService commandService = ( ICommandService ) PlatformUI.getWorkbench().getAdapter(
            ICommandService.class );
        if ( commandService != null )
        {
            IAction naa = ( IAction ) this.entryEditorActionMap.get( newAttributeAction );
            commandService.getCommand( naa.getActionDefinitionId() ).setHandler( null );
            commandService.getCommand( editAttributeDescriptionAction.getActionDefinitionId() ).setHandler( null );
        }
    }

}
