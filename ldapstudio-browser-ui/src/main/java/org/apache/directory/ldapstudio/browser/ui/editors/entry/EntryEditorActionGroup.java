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

package org.apache.directory.ldapstudio.browser.ui.editors.entry;


import java.util.Iterator;

import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.actions.CollapseAllAction;
import org.apache.directory.ldapstudio.browser.ui.actions.CopyAttributeDescriptionAction;
import org.apache.directory.ldapstudio.browser.ui.actions.CopyDnAction;
import org.apache.directory.ldapstudio.browser.ui.actions.CopySearchFilterAction;
import org.apache.directory.ldapstudio.browser.ui.actions.CopyUrlAction;
import org.apache.directory.ldapstudio.browser.ui.actions.CopyValueAction;
import org.apache.directory.ldapstudio.browser.ui.actions.DeleteAllValuesAction;
import org.apache.directory.ldapstudio.browser.ui.actions.ExpandAllAction;
import org.apache.directory.ldapstudio.browser.ui.actions.LocateDnInDitAction;
import org.apache.directory.ldapstudio.browser.ui.actions.NewAttributeAction;
import org.apache.directory.ldapstudio.browser.ui.actions.NewBatchOperationAction;
import org.apache.directory.ldapstudio.browser.ui.actions.NewSearchAction;
import org.apache.directory.ldapstudio.browser.ui.actions.OpenSchemaBrowserAction;
import org.apache.directory.ldapstudio.browser.ui.actions.RefreshAction;
import org.apache.directory.ldapstudio.browser.ui.actions.proxy.EntryEditorActionProxy;
import org.apache.directory.ldapstudio.browser.ui.widgets.entryeditor.EditAttributeDescriptionAction;
import org.apache.directory.ldapstudio.browser.ui.widgets.entryeditor.EntryEditorWidgetActionGroup;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.commands.ICommandService;


public class EntryEditorActionGroup extends EntryEditorWidgetActionGroup
{

    private ShowOperationalAttributesAction showOperationalAttributesAction;

    private OpenEntryEditorPreferencePageAction openEntryEditorPreferencePage;

    private CollapseAllAction collapseAllAction;

    private ExpandAllAction expandAllAction;

    private EditAttributeDescriptionAction editAttributeDescriptionAction;

    private static final String refreshAttributesAction = "refreshAttributesAction";

    private static final String newAttributeAction = "newAttributeAction";

    private static final String newSearchAction = "newSearchDialogAction";

    private static final String newBatchOperationAction = "newBatchOperationAction";

    private static final String copyDnAction = "copyDnAction";

    private static final String copyUrlAction = "copyUrlAction";

    private static final String copyAttriuteDescriptionAction = "copyAttriuteDescriptionAction";

    private static final String copyValueUtf8Action = "copyValueUtf8Action";

    private static final String copyValueBase64Action = "copyValueBase64Action";

    private static final String copyValueHexAction = "copyValueHexAction";

    private static final String copyValueAsLdifAction = "copyValueAsLdifAction";

    private static final String copySearchFilterAction = "copySearchFilterAction";

    private static final String copyNotSearchFilterAction = "copyNotSearchFilterAction";

    private static final String copyAndSearchFilterAction = "copyAndSearchFilterAction";

    private static final String copyOrSearchFilterAction = "copyOrSearchFilterAction";

    private static final String deleteAllValuesAction = "deleteAllValuesAction";

    private static final String locateDnInDitAction = "locateDnInDitAction";

    private static final String showOcdAction = "showOcdAction";

    private static final String showAtdAction = "showAtdAction";

    private static final String showEqualityMrdAction = "showEqualityMrdAction";

    private static final String showSubstringMrdAction = "showSubstringMrdAction";

    private static final String showOrderingMrdAction = "showOrderingMrdAction";

    private static final String showLsdAction = "showLsdAction";


    public EntryEditorActionGroup( EntryEditor entryEditor )
    {
        super( entryEditor.getMainWidget(), entryEditor.getConfiguration() );
        TreeViewer viewer = entryEditor.getMainWidget().getViewer();

        this.openDefaultEditorAction.enableRenameEntryAction();

        this.showOperationalAttributesAction = new ShowOperationalAttributesAction();
        this.openEntryEditorPreferencePage = new OpenEntryEditorPreferencePageAction();
        this.collapseAllAction = new CollapseAllAction( viewer );
        this.expandAllAction = new ExpandAllAction( viewer );
        this.editAttributeDescriptionAction = new EditAttributeDescriptionAction( viewer );

        this.entryEditorActionMap.put( refreshAttributesAction,
            new EntryEditorActionProxy( viewer, new RefreshAction() ) );

        this.entryEditorActionMap.put( newAttributeAction,
            new EntryEditorActionProxy( viewer, new NewAttributeAction() ) );
        this.entryEditorActionMap.put( newSearchAction, new EntryEditorActionProxy( viewer, new NewSearchAction() ) );
        this.entryEditorActionMap.put( newBatchOperationAction, new EntryEditorActionProxy( viewer,
            new NewBatchOperationAction() ) );

        this.entryEditorActionMap.put( locateDnInDitAction, new EntryEditorActionProxy( viewer,
            new LocateDnInDitAction() ) );
        this.entryEditorActionMap.put( showOcdAction, new EntryEditorActionProxy( viewer, new OpenSchemaBrowserAction(
            OpenSchemaBrowserAction.MODE_OBJECTCLASS ) ) );
        this.entryEditorActionMap.put( showAtdAction, new EntryEditorActionProxy( viewer, new OpenSchemaBrowserAction(
            OpenSchemaBrowserAction.MODE_ATTRIBUTETYPE ) ) );
        this.entryEditorActionMap.put( showEqualityMrdAction, new EntryEditorActionProxy( viewer,
            new OpenSchemaBrowserAction( OpenSchemaBrowserAction.MODE_EQUALITYMATCHINGRULE ) ) );
        this.entryEditorActionMap.put( showSubstringMrdAction, new EntryEditorActionProxy( viewer,
            new OpenSchemaBrowserAction( OpenSchemaBrowserAction.MODE_SUBSTRINGMATCHINGRULE ) ) );
        this.entryEditorActionMap.put( showOrderingMrdAction, new EntryEditorActionProxy( viewer,
            new OpenSchemaBrowserAction( OpenSchemaBrowserAction.MODE_ORDERINGMATCHINGRULE ) ) );
        this.entryEditorActionMap.put( showLsdAction, new EntryEditorActionProxy( viewer, new OpenSchemaBrowserAction(
            OpenSchemaBrowserAction.MODE_SYNTAX ) ) );

        this.entryEditorActionMap.put( copyDnAction, new EntryEditorActionProxy( viewer, new CopyDnAction() ) );
        this.entryEditorActionMap.put( copyUrlAction, new EntryEditorActionProxy( viewer, new CopyUrlAction() ) );
        this.entryEditorActionMap.put( copyAttriuteDescriptionAction, new EntryEditorActionProxy( viewer,
            new CopyAttributeDescriptionAction() ) );
        this.entryEditorActionMap.put( copyValueUtf8Action, new EntryEditorActionProxy( viewer, new CopyValueAction(
            CopyValueAction.MODE_UTF8 ) ) );
        this.entryEditorActionMap.put( copyValueBase64Action, new EntryEditorActionProxy( viewer, new CopyValueAction(
            CopyValueAction.MODE_BASE64 ) ) );
        this.entryEditorActionMap.put( copyValueHexAction, new EntryEditorActionProxy( viewer, new CopyValueAction(
            CopyValueAction.MODE_HEX ) ) );
        this.entryEditorActionMap.put( copyValueAsLdifAction, new EntryEditorActionProxy( viewer, new CopyValueAction(
            CopyValueAction.MODE_LDIF ) ) );

        this.entryEditorActionMap.put( copySearchFilterAction, new EntryEditorActionProxy( viewer,
            new CopySearchFilterAction( CopySearchFilterAction.MODE_EQUALS ) ) );
        this.entryEditorActionMap.put( copyNotSearchFilterAction, new EntryEditorActionProxy( viewer,
            new CopySearchFilterAction( CopySearchFilterAction.MODE_NOT ) ) );
        this.entryEditorActionMap.put( copyAndSearchFilterAction, new EntryEditorActionProxy( viewer,
            new CopySearchFilterAction( CopySearchFilterAction.MODE_AND ) ) );
        this.entryEditorActionMap.put( copyOrSearchFilterAction, new EntryEditorActionProxy( viewer,
            new CopySearchFilterAction( CopySearchFilterAction.MODE_OR ) ) );

        this.entryEditorActionMap.put( deleteAllValuesAction, new EntryEditorActionProxy( viewer,
            new DeleteAllValuesAction() ) );

    }


    public void dispose()
    {
        if ( this.showOperationalAttributesAction != null )
        {

            this.deactivateGlobalActionHandlers();

            this.openEntryEditorPreferencePage = null;
            this.showOperationalAttributesAction.dispose();
            this.showOperationalAttributesAction = null;
            this.expandAllAction.dispose();
            this.expandAllAction = null;
            this.collapseAllAction.dispose();
            this.collapseAllAction = null;
            this.editAttributeDescriptionAction.dispose();
            this.editAttributeDescriptionAction = null;
        }

        super.dispose();
    }


    public void fillToolBar( IToolBarManager toolBarManager )
    {
        toolBarManager.add( new Separator() );
        toolBarManager.add( ( IAction ) this.entryEditorActionMap.get( newValueAction ) );
        toolBarManager.add( ( IAction ) this.entryEditorActionMap.get( newAttributeAction ) );
        toolBarManager.add( new Separator() );
        toolBarManager.add( ( IAction ) this.entryEditorActionMap.get( deleteAction ) );
        toolBarManager.add( ( IAction ) this.entryEditorActionMap.get( deleteAllValuesAction ) );
        toolBarManager.add( new Separator() );
        toolBarManager.add( ( IAction ) this.entryEditorActionMap.get( refreshAttributesAction ) );
        toolBarManager.add( new Separator() );
        toolBarManager.add( this.expandAllAction );
        toolBarManager.add( this.collapseAllAction );
        toolBarManager.add( new Separator() );
        toolBarManager.add( this.showQuickFilterAction );
        toolBarManager.update( true );
    }


    public void fillMenu( IMenuManager menuManager )
    {
        menuManager.add( this.openSortDialogAction );
        menuManager.add( new Separator() );
        menuManager.add( this.showOperationalAttributesAction );
        menuManager.add( this.showRawValuesAction );
        menuManager.add( new Separator() );
        menuManager.add( this.openEntryEditorPreferencePage );
        menuManager.addMenuListener( new IMenuListener()
        {
            public void menuAboutToShow( IMenuManager manager )
            {
                showRawValuesAction.setChecked( BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
                    BrowserUIConstants.PREFERENCE_SHOW_RAW_VALUES ) );
                showOperationalAttributesAction.setChecked( BrowserUIPlugin.getDefault().getPreferenceStore()
                    .getBoolean( BrowserUIConstants.PREFERENCE_ENTRYEDITOR_SHOW_OPERATIONAL_ATTRIBUTES ) );
            }
        } );
        menuManager.update( true );
    }


    public void menuAboutToShow( IMenuManager menuManager )
    {

        // new
        menuManager.add( ( IAction ) this.entryEditorActionMap.get( newAttributeAction ) );
        menuManager.add( ( IAction ) this.entryEditorActionMap.get( newValueAction ) );
        menuManager.add( ( IAction ) this.entryEditorActionMap.get( newSearchAction ) );
        menuManager.add( ( IAction ) this.entryEditorActionMap.get( newBatchOperationAction ) );
        menuManager.add( new Separator() );

        // navigation
        menuManager.add( ( IAction ) this.entryEditorActionMap.get( locateDnInDitAction ) );
        MenuManager schemaMenuManager = new MenuManager( "Open Schema Browser" );
        schemaMenuManager.add( ( IAction ) this.entryEditorActionMap.get( showOcdAction ) );
        schemaMenuManager.add( ( IAction ) this.entryEditorActionMap.get( showAtdAction ) );
        schemaMenuManager.add( ( IAction ) this.entryEditorActionMap.get( showEqualityMrdAction ) );
        schemaMenuManager.add( ( IAction ) this.entryEditorActionMap.get( showSubstringMrdAction ) );
        schemaMenuManager.add( ( IAction ) this.entryEditorActionMap.get( showOrderingMrdAction ) );
        schemaMenuManager.add( ( IAction ) this.entryEditorActionMap.get( showLsdAction ) );
        menuManager.add( schemaMenuManager );
        MenuManager showInSubMenu = new MenuManager( "Show In" );
        showInSubMenu.add( ContributionItemFactory.VIEWS_SHOW_IN.create( PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow() ) );
        menuManager.add( showInSubMenu );

        menuManager.add( new Separator() );

        // copy, paste, delete
        menuManager.add( ( IAction ) this.entryEditorActionMap.get( copyAction ) );
        menuManager.add( ( IAction ) this.entryEditorActionMap.get( pasteAction ) );
        menuManager.add( ( IAction ) this.entryEditorActionMap.get( deleteAction ) );
        menuManager.add( ( IAction ) this.entryEditorActionMap.get( selectAllAction ) );
        MenuManager advancedMenuManager = new MenuManager( "Advanced" );
        advancedMenuManager.add( ( IAction ) this.entryEditorActionMap.get( copyDnAction ) );
        advancedMenuManager.add( ( IAction ) this.entryEditorActionMap.get( copyUrlAction ) );
        advancedMenuManager.add( new Separator() );
        advancedMenuManager.add( ( IAction ) this.entryEditorActionMap.get( copyAttriuteDescriptionAction ) );
        advancedMenuManager.add( new Separator() );
        advancedMenuManager.add( ( IAction ) this.entryEditorActionMap.get( copyValueUtf8Action ) );
        advancedMenuManager.add( ( IAction ) this.entryEditorActionMap.get( copyValueBase64Action ) );
        advancedMenuManager.add( ( IAction ) this.entryEditorActionMap.get( copyValueHexAction ) );
        advancedMenuManager.add( new Separator() );
        advancedMenuManager.add( ( IAction ) this.entryEditorActionMap.get( copyValueAsLdifAction ) );
        advancedMenuManager.add( new Separator() );
        advancedMenuManager.add( ( IAction ) this.entryEditorActionMap.get( copySearchFilterAction ) );
        advancedMenuManager.add( ( IAction ) this.entryEditorActionMap.get( copyNotSearchFilterAction ) );
        advancedMenuManager.add( ( IAction ) this.entryEditorActionMap.get( copyAndSearchFilterAction ) );
        advancedMenuManager.add( ( IAction ) this.entryEditorActionMap.get( copyOrSearchFilterAction ) );
        advancedMenuManager.add( new Separator() );
        advancedMenuManager.add( ( IAction ) this.entryEditorActionMap.get( deleteAllValuesAction ) );
        menuManager.add( advancedMenuManager );
        menuManager.add( new Separator() );

        // edit
        menuManager.add( this.editAttributeDescriptionAction );
        super.addEditMenu( menuManager );
        menuManager.add( new Separator() );

        // refresh
        menuManager.add( ( IAction ) this.entryEditorActionMap.get( refreshAttributesAction ) );
        menuManager.add( new Separator() );

        // additions
        menuManager.add( new Separator( IWorkbenchActionConstants.MB_ADDITIONS ) );

        // properties
        menuManager.add( ( IAction ) this.entryEditorActionMap.get( propertyDialogAction ) );
    }


    public void activateGlobalActionHandlers()
    {

        if ( this.actionBars != null )
        {
            actionBars.setGlobalActionHandler( ActionFactory.REFRESH.getId(), ( IAction ) this.entryEditorActionMap
                .get( refreshAttributesAction ) );
        }

        super.activateGlobalActionHandlers();

        ICommandService commandService = ( ICommandService ) PlatformUI.getWorkbench().getAdapter(
            ICommandService.class );
        if ( commandService != null )
        {
            IAction naa = ( IAction ) this.entryEditorActionMap.get( newAttributeAction );
            commandService.getCommand( naa.getActionDefinitionId() ).setHandler( new ActionHandler( naa ) );
            IAction lid = ( IAction ) this.entryEditorActionMap.get( locateDnInDitAction );
            commandService.getCommand( lid.getActionDefinitionId() ).setHandler( new ActionHandler( lid ) );
            commandService.getCommand( editAttributeDescriptionAction.getActionDefinitionId() ).setHandler(
                new ActionHandler( editAttributeDescriptionAction ) );
        }
    }


    public void deactivateGlobalActionHandlers()
    {

        if ( this.actionBars != null )
        {
            actionBars.setGlobalActionHandler( ActionFactory.REFRESH.getId(), null );
        }

        super.deactivateGlobalActionHandlers();

        ICommandService commandService = ( ICommandService ) PlatformUI.getWorkbench().getAdapter(
            ICommandService.class );
        if ( commandService != null )
        {
            IAction naa = ( IAction ) this.entryEditorActionMap.get( newAttributeAction );
            commandService.getCommand( naa.getActionDefinitionId() ).setHandler( null );
            IAction lid = ( IAction ) this.entryEditorActionMap.get( locateDnInDitAction );
            commandService.getCommand( lid.getActionDefinitionId() ).setHandler( null );
            commandService.getCommand( editAttributeDescriptionAction.getActionDefinitionId() ).setHandler( null );
        }
    }


    public void setInput( IEntry entry )
    {
        for ( Iterator it = this.entryEditorActionMap.values().iterator(); it.hasNext(); )
        {
            EntryEditorActionProxy action = ( EntryEditorActionProxy ) it.next();
            action.inputChanged( entry );
        }
    }

}
