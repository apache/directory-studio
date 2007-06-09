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

package org.apache.directory.ldapstudio.browser.ui.editors.searchresult;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.directory.ldapstudio.browser.ui.actions.CopyAttributeDescriptionAction;
import org.apache.directory.ldapstudio.browser.ui.actions.CopyDnAction;
import org.apache.directory.ldapstudio.browser.ui.actions.CopyEntryAsCsvAction;
import org.apache.directory.ldapstudio.browser.ui.actions.CopySearchFilterAction;
import org.apache.directory.ldapstudio.browser.ui.actions.CopyUrlAction;
import org.apache.directory.ldapstudio.browser.ui.actions.CopyValueAction;
import org.apache.directory.ldapstudio.browser.ui.actions.LocateDnInDitAction;
import org.apache.directory.ldapstudio.browser.ui.actions.NewBatchOperationAction;
import org.apache.directory.ldapstudio.browser.ui.actions.NewSearchAction;
import org.apache.directory.ldapstudio.browser.ui.actions.OpenSchemaBrowserAction;
import org.apache.directory.ldapstudio.browser.ui.actions.OpenSearchResultAction;
import org.apache.directory.ldapstudio.browser.ui.actions.proxy.SearchResultEditorActionProxy;
import org.apache.directory.ldapstudio.valueeditors.IValueEditor;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.common.actions.CopyAction;
import org.apache.directory.studio.ldapbrowser.common.actions.NewValueAction;
import org.apache.directory.studio.ldapbrowser.common.actions.PropertiesAction;
import org.apache.directory.studio.ldapbrowser.common.actions.RefreshAction;
import org.apache.directory.studio.ldapbrowser.common.actions.ShowRawValuesAction;
import org.apache.directory.studio.ldapbrowser.common.actions.ValueEditorPreferencesAction;
import org.apache.directory.studio.ldapbrowser.common.actions.proxy.BrowserActionProxy;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.commands.ICommandService;


public class SearchResultEditorActionGroup implements IMenuListener
{

    private ShowDNAction showDNAction;

    private ShowLinksAction showLinksAction;

    private ShowRawValuesAction showRawValuesAction;

    private OpenSearchResultEditorPreferencePage openSearchResultEditorPreferencePage;

    private ShowQuickFilterAction showQuickFilterAction;

    private OpenDefaultEditorAction openDefaultEditorAction;

    private OpenBestEditorAction openBestEditorAction;

    private OpenEditorAction[] openEditorActions;

    private ValueEditorPreferencesAction openValueEditorPreferencesAction;

    private static final String copyTableAction = "copyTableAction";

    private static final String refreshSearchAction = "refreshSearchAction";

    private final static String newValueAction = "newValueAction";

    private final static String newSearchAction = "newSearchAction";

    private static final String newBatchOperationAction = "newBatchOperationAction";

    private final static String copyAction = "copyAction";

    private final static String pasteAction = "pasteAction";

    private final static String deleteAction = "deleteAction";

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

    private static final String openSearchResultAction = "showEntryInSearchResultsAction";

    private static final String locateDnInDitAction = "locateDnInDitAction";

    private static final String showOcdAction = "showOcdAction";

    private static final String showAtdAction = "showAtdAction";

    private static final String showEqualityMrdAction = "showEqualityMrdAction";

    private static final String showSubstringMrdAction = "showSubstringMrdAction";

    private static final String showOrderingMrdAction = "showOrderingMrdAction";

    private static final String showLsdAction = "showLsdAction";

    private final static String propertyDialogAction = "propertyDialogAction";

    private Map searchResultEditorActionMap;

    private IActionBars actionBars;

    private SearchResultEditor searchResultEditor;


    public SearchResultEditorActionGroup( SearchResultEditor searchResultEditor )
    {
        this.searchResultEditor = searchResultEditor;
        this.searchResultEditorActionMap = new HashMap();

        TableViewer viewer = searchResultEditor.getMainWidget().getViewer();
        SearchResultEditorCursor cursor = searchResultEditor.getConfiguration().getCursor( viewer );

        this.showDNAction = new ShowDNAction();
        this.showLinksAction = new ShowLinksAction();
        this.showRawValuesAction = new ShowRawValuesAction();
        this.openSearchResultEditorPreferencePage = new OpenSearchResultEditorPreferencePage();
        this.showQuickFilterAction = new ShowQuickFilterAction( searchResultEditor.getMainWidget()
            .getQuickFilterWidget() );

        this.openBestEditorAction = new OpenBestEditorAction( viewer, searchResultEditor.getConfiguration().getCursor(
            viewer ), this, searchResultEditor.getConfiguration().getValueEditorManager( viewer ) );
        this.openDefaultEditorAction = new OpenDefaultEditorAction( this.openBestEditorAction );
        IValueEditor[] valueEditors = searchResultEditor.getConfiguration().getValueEditorManager( viewer )
            .getAllValueEditors();
        this.openEditorActions = new OpenEditorAction[valueEditors.length];
        for ( int i = 0; i < this.openEditorActions.length; i++ )
        {
            this.openEditorActions[i] = new OpenEditorAction( viewer, searchResultEditor.getConfiguration().getCursor(
                viewer ), this, searchResultEditor.getConfiguration().getValueEditorManager( viewer ),
                valueEditors[i] );
        }
        this.openValueEditorPreferencesAction = new ValueEditorPreferencesAction();

        this.searchResultEditorActionMap.put( copyTableAction, new SearchResultEditorActionProxy( cursor,
            new CopyEntryAsCsvAction( CopyEntryAsCsvAction.MODE_TABLE ) ) );
        this.searchResultEditorActionMap.put( refreshSearchAction, new SearchResultEditorActionProxy( cursor,
            new RefreshAction() ) );

        this.searchResultEditorActionMap.put( newValueAction, new SearchResultEditorActionProxy( cursor,
            new NewValueAction() ) );
        this.searchResultEditorActionMap.put( newSearchAction, new SearchResultEditorActionProxy( cursor,
            new NewSearchAction() ) );
        this.searchResultEditorActionMap.put( newBatchOperationAction, new SearchResultEditorActionProxy( cursor,
            new NewBatchOperationAction() ) );

        this.searchResultEditorActionMap.put( locateDnInDitAction, new SearchResultEditorActionProxy( cursor,
            new LocateDnInDitAction() ) );
        this.searchResultEditorActionMap.put( openSearchResultAction, new SearchResultEditorActionProxy( cursor,
            new OpenSearchResultAction() ) );

        this.searchResultEditorActionMap.put( showOcdAction, new SearchResultEditorActionProxy( cursor,
            new OpenSchemaBrowserAction( OpenSchemaBrowserAction.MODE_OBJECTCLASS ) ) );
        this.searchResultEditorActionMap.put( showAtdAction, new SearchResultEditorActionProxy( cursor,
            new OpenSchemaBrowserAction( OpenSchemaBrowserAction.MODE_ATTRIBUTETYPE ) ) );
        this.searchResultEditorActionMap.put( showEqualityMrdAction, new SearchResultEditorActionProxy( cursor,
            new OpenSchemaBrowserAction( OpenSchemaBrowserAction.MODE_EQUALITYMATCHINGRULE ) ) );
        this.searchResultEditorActionMap.put( showSubstringMrdAction, new SearchResultEditorActionProxy( cursor,
            new OpenSchemaBrowserAction( OpenSchemaBrowserAction.MODE_SUBSTRINGMATCHINGRULE ) ) );
        this.searchResultEditorActionMap.put( showOrderingMrdAction, new SearchResultEditorActionProxy( cursor,
            new OpenSchemaBrowserAction( OpenSchemaBrowserAction.MODE_ORDERINGMATCHINGRULE ) ) );
        this.searchResultEditorActionMap.put( showLsdAction, new SearchResultEditorActionProxy( cursor,
            new OpenSchemaBrowserAction( OpenSchemaBrowserAction.MODE_SYNTAX ) ) );

        this.searchResultEditorActionMap.put( pasteAction, new SearchResultEditorActionProxy( cursor,
            new SearchResultEditorPasteAction() ) );
        this.searchResultEditorActionMap.put( copyAction, new SearchResultEditorActionProxy( cursor, new CopyAction(
            ( BrowserActionProxy ) this.searchResultEditorActionMap.get( pasteAction ) ) ) );
        this.searchResultEditorActionMap.put( deleteAction, new SearchResultEditorActionProxy( cursor,
            new SearchResultDeleteAction() ) );

        this.searchResultEditorActionMap.put( copyDnAction, new SearchResultEditorActionProxy( cursor,
            new CopyDnAction() ) );
        this.searchResultEditorActionMap.put( copyUrlAction, new SearchResultEditorActionProxy( cursor,
            new CopyUrlAction() ) );
        this.searchResultEditorActionMap.put( copyAttriuteDescriptionAction, new SearchResultEditorActionProxy( cursor,
            new CopyAttributeDescriptionAction() ) );
        this.searchResultEditorActionMap.put( copyValueUtf8Action, new SearchResultEditorActionProxy( cursor,
            new CopyValueAction( CopyValueAction.MODE_UTF8 ) ) );
        this.searchResultEditorActionMap.put( copyValueBase64Action, new SearchResultEditorActionProxy( cursor,
            new CopyValueAction( CopyValueAction.MODE_BASE64 ) ) );
        this.searchResultEditorActionMap.put( copyValueHexAction, new SearchResultEditorActionProxy( cursor,
            new CopyValueAction( CopyValueAction.MODE_HEX ) ) );
        this.searchResultEditorActionMap.put( copyValueAsLdifAction, new SearchResultEditorActionProxy( cursor,
            new CopyValueAction( CopyValueAction.MODE_LDIF ) ) );

        this.searchResultEditorActionMap.put( copySearchFilterAction, new SearchResultEditorActionProxy( cursor,
            new CopySearchFilterAction( CopySearchFilterAction.MODE_EQUALS ) ) );
        this.searchResultEditorActionMap.put( copyNotSearchFilterAction, new SearchResultEditorActionProxy( cursor,
            new CopySearchFilterAction( CopySearchFilterAction.MODE_NOT ) ) );
        this.searchResultEditorActionMap.put( copyAndSearchFilterAction, new SearchResultEditorActionProxy( cursor,
            new CopySearchFilterAction( CopySearchFilterAction.MODE_AND ) ) );
        this.searchResultEditorActionMap.put( copyOrSearchFilterAction, new SearchResultEditorActionProxy( cursor,
            new CopySearchFilterAction( CopySearchFilterAction.MODE_OR ) ) );

        this.searchResultEditorActionMap.put( propertyDialogAction, new SearchResultEditorActionProxy( cursor,
            new PropertiesAction() ) );

    }


    public void dispose()
    {

        if ( this.searchResultEditor != null )
        {
            this.showRawValuesAction = null;
            this.showDNAction.dispose();
            this.showDNAction = null;
            this.showLinksAction.dispose();
            this.showLinksAction = null;
            this.openSearchResultEditorPreferencePage = null;
            this.showQuickFilterAction.dispose();
            this.showQuickFilterAction = null;

            this.openDefaultEditorAction.dispose();
            this.openDefaultEditorAction = null;
            this.openBestEditorAction.dispose();
            this.openBestEditorAction = null;
            for ( int i = 0; i < this.openEditorActions.length; i++ )
            {
                this.openEditorActions[i].dispose();
                this.openEditorActions[i] = null;
            }
            this.openValueEditorPreferencesAction = null;

            for ( Iterator it = this.searchResultEditorActionMap.keySet().iterator(); it.hasNext(); )
            {
                String key = ( String ) it.next();
                SearchResultEditorActionProxy action = ( SearchResultEditorActionProxy ) this.searchResultEditorActionMap
                    .get( key );
                action.dispose();
                action = null;
                it.remove();
            }
            this.searchResultEditorActionMap.clear();
            this.searchResultEditorActionMap = null;

            this.actionBars = null;
            this.searchResultEditor = null;
        }
    }


    public void fillToolBar( IToolBarManager toolBarManager )
    {
        toolBarManager.add( new Separator() );
        toolBarManager.add( ( IAction ) this.searchResultEditorActionMap.get( newValueAction ) );
        toolBarManager.add( new Separator() );
        toolBarManager.add( ( IAction ) this.searchResultEditorActionMap.get( deleteAction ) );
        toolBarManager.add( new Separator() );
        toolBarManager.add( ( IAction ) this.searchResultEditorActionMap.get( refreshSearchAction ) );
        toolBarManager.add( new Separator() );
        toolBarManager.add( ( IAction ) this.searchResultEditorActionMap.get( copyTableAction ) );
        toolBarManager.add( new Separator() );
        toolBarManager.add( this.showQuickFilterAction );
        toolBarManager.update( true );
    }


    public void fillMenu( IMenuManager menuManager )
    {
        menuManager.add( this.showDNAction );
        menuManager.add( this.showLinksAction );
        menuManager.add( this.showRawValuesAction );
        menuManager.add( new Separator() );
        menuManager.add( this.openSearchResultEditorPreferencePage );
        menuManager.addMenuListener( new IMenuListener()
        {
            public void menuAboutToShow( IMenuManager manager )
            {
                showRawValuesAction.setChecked( BrowserCommonActivator.getDefault().getPreferenceStore().getBoolean(
                    BrowserCommonConstants.PREFERENCE_SHOW_RAW_VALUES ) );
            }
        } );
        menuManager.update( true );
    }


    public void enableGlobalActionHandlers( IActionBars actionBars )
    {
        this.actionBars = actionBars;
        this.activateGlobalActionHandlers();
    }


    public void fillContextMenu( IMenuManager menuManager )
    {
        menuManager.setRemoveAllWhenShown( true );
        menuManager.addMenuListener( this );
    }


    public void menuAboutToShow( IMenuManager menuManager )
    {

        // new
        menuManager.add( ( IAction ) this.searchResultEditorActionMap.get( newValueAction ) );
        menuManager.add( ( IAction ) this.searchResultEditorActionMap.get( newSearchAction ) );
        menuManager.add( ( IAction ) this.searchResultEditorActionMap.get( newBatchOperationAction ) );
        menuManager.add( new Separator() );

        // navigation
        menuManager.add( ( IAction ) this.searchResultEditorActionMap.get( locateDnInDitAction ) );
        menuManager.add( ( IAction ) this.searchResultEditorActionMap.get( openSearchResultAction ) );
        MenuManager schemaMenuManager = new MenuManager( "Open Schema Browser" );
        schemaMenuManager.add( ( IAction ) this.searchResultEditorActionMap.get( showOcdAction ) );
        schemaMenuManager.add( ( IAction ) this.searchResultEditorActionMap.get( showAtdAction ) );
        schemaMenuManager.add( ( IAction ) this.searchResultEditorActionMap.get( showEqualityMrdAction ) );
        schemaMenuManager.add( ( IAction ) this.searchResultEditorActionMap.get( showSubstringMrdAction ) );
        schemaMenuManager.add( ( IAction ) this.searchResultEditorActionMap.get( showOrderingMrdAction ) );
        schemaMenuManager.add( ( IAction ) this.searchResultEditorActionMap.get( showLsdAction ) );
        menuManager.add( schemaMenuManager );
        MenuManager showInSubMenu = new MenuManager( "Show In" );
        showInSubMenu.add( ContributionItemFactory.VIEWS_SHOW_IN.create( PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow() ) );
        menuManager.add( showInSubMenu );
        menuManager.add( new Separator() );

        // copy, paste, delete
        menuManager.add( ( IAction ) this.searchResultEditorActionMap.get( copyAction ) );
        menuManager.add( ( IAction ) this.searchResultEditorActionMap.get( pasteAction ) );
        menuManager.add( ( IAction ) this.searchResultEditorActionMap.get( deleteAction ) );
        MenuManager advancedMenuManager = new MenuManager( "Advanced" );
        advancedMenuManager.add( ( IAction ) this.searchResultEditorActionMap.get( copyDnAction ) );
        advancedMenuManager.add( ( IAction ) this.searchResultEditorActionMap.get( copyUrlAction ) );
        advancedMenuManager.add( new Separator() );
        advancedMenuManager.add( ( IAction ) this.searchResultEditorActionMap.get( copyAttriuteDescriptionAction ) );
        advancedMenuManager.add( new Separator() );
        advancedMenuManager.add( ( IAction ) this.searchResultEditorActionMap.get( copyValueUtf8Action ) );
        advancedMenuManager.add( ( IAction ) this.searchResultEditorActionMap.get( copyValueBase64Action ) );
        advancedMenuManager.add( ( IAction ) this.searchResultEditorActionMap.get( copyValueHexAction ) );
        advancedMenuManager.add( new Separator() );
        advancedMenuManager.add( ( IAction ) this.searchResultEditorActionMap.get( copyValueAsLdifAction ) );
        advancedMenuManager.add( new Separator() );
        advancedMenuManager.add( ( IAction ) this.searchResultEditorActionMap.get( copySearchFilterAction ) );
        advancedMenuManager.add( ( IAction ) this.searchResultEditorActionMap.get( copyNotSearchFilterAction ) );
        advancedMenuManager.add( ( IAction ) this.searchResultEditorActionMap.get( copyAndSearchFilterAction ) );
        advancedMenuManager.add( ( IAction ) this.searchResultEditorActionMap.get( copyOrSearchFilterAction ) );
        menuManager.add( advancedMenuManager );
        menuManager.add( new Separator() );

        // edit
        menuManager.add( this.openDefaultEditorAction );
        MenuManager editorMenuManager = new MenuManager( "Edit Value With" );
        if ( this.openBestEditorAction.isEnabled() )
        {
            editorMenuManager.add( this.openBestEditorAction );
            editorMenuManager.add( new Separator() );
        }
        for ( int i = 0; i < this.openEditorActions.length; i++ )
        {
            if ( this.openEditorActions[i].isEnabled()
                && this.openEditorActions[i].getValueEditor().getClass() != this.openBestEditorAction
                    .getBestValueEditor().getClass() )
            {
                editorMenuManager.add( this.openEditorActions[i] );
            }
        }
        editorMenuManager.add( new Separator() );
        editorMenuManager.add( this.openValueEditorPreferencesAction );
        menuManager.add( editorMenuManager );
        menuManager.add( new Separator() );

        // refresh
        menuManager.add( ( IAction ) this.searchResultEditorActionMap.get( refreshSearchAction ) );
        menuManager.add( new Separator() );

        // additions
        menuManager.add( new Separator( IWorkbenchActionConstants.MB_ADDITIONS ) );

        // / properties
        menuManager.add( ( IAction ) this.searchResultEditorActionMap.get( propertyDialogAction ) );

    }


    public void activateGlobalActionHandlers()
    {

        if ( this.actionBars != null )
        {
            actionBars.setGlobalActionHandler( ActionFactory.COPY.getId(), ( IAction ) this.searchResultEditorActionMap
                .get( copyAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.PASTE.getId(),
                ( IAction ) this.searchResultEditorActionMap.get( pasteAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.DELETE.getId(),
                ( IAction ) this.searchResultEditorActionMap.get( deleteAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.REFRESH.getId(),
                ( IAction ) this.searchResultEditorActionMap.get( refreshSearchAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.PROPERTIES.getId(),
                ( IAction ) this.searchResultEditorActionMap.get( propertyDialogAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.FIND.getId(), this.showQuickFilterAction );
            actionBars.updateActionBars();
        }

        ICommandService commandService = ( ICommandService ) PlatformUI.getWorkbench().getAdapter(
            ICommandService.class );
        if ( commandService != null )
        {
            IAction nva = ( IAction ) this.searchResultEditorActionMap.get( newValueAction );
            commandService.getCommand( nva.getActionDefinitionId() ).setHandler( new ActionHandler( nva ) );
            IAction lid = ( IAction ) this.searchResultEditorActionMap.get( locateDnInDitAction );
            commandService.getCommand( lid.getActionDefinitionId() ).setHandler( new ActionHandler( lid ) );
            IAction osr = ( IAction ) this.searchResultEditorActionMap.get( openSearchResultAction );
            commandService.getCommand( osr.getActionDefinitionId() ).setHandler( new ActionHandler( osr ) );
            commandService.getCommand( openDefaultEditorAction.getActionDefinitionId() ).setHandler(
                new ActionHandler( openDefaultEditorAction ) );
        }

    }


    public void deactivateGlobalActionHandlers()
    {

        if ( this.actionBars != null )
        {
            actionBars.setGlobalActionHandler( ActionFactory.COPY.getId(), null );
            actionBars.setGlobalActionHandler( ActionFactory.PASTE.getId(), null );
            actionBars.setGlobalActionHandler( ActionFactory.DELETE.getId(), null );
            actionBars.setGlobalActionHandler( ActionFactory.REFRESH.getId(), null );
            actionBars.setGlobalActionHandler( ActionFactory.FIND.getId(), null );
            actionBars.setGlobalActionHandler( ActionFactory.PROPERTIES.getId(), null );
            actionBars.updateActionBars();
        }

        ICommandService commandService = ( ICommandService ) PlatformUI.getWorkbench().getAdapter(
            ICommandService.class );
        if ( commandService != null )
        {
            IAction nva = ( IAction ) this.searchResultEditorActionMap.get( newValueAction );
            commandService.getCommand( nva.getActionDefinitionId() ).setHandler( null );
            IAction lid = ( IAction ) this.searchResultEditorActionMap.get( locateDnInDitAction );
            commandService.getCommand( lid.getActionDefinitionId() ).setHandler( null );
            IAction osr = ( IAction ) this.searchResultEditorActionMap.get( openSearchResultAction );
            commandService.getCommand( osr.getActionDefinitionId() ).setHandler( null );
            commandService.getCommand( openDefaultEditorAction.getActionDefinitionId() ).setHandler( null );
        }

    }


    public OpenBestEditorAction getOpenBestEditorAction()
    {
        return openBestEditorAction;
    }


    public void setInput( ISearch search )
    {
        for ( Iterator it = this.searchResultEditorActionMap.values().iterator(); it.hasNext(); )
        {
            SearchResultEditorActionProxy action = ( SearchResultEditorActionProxy ) it.next();
            action.inputChanged( search );
        }
    }


    public boolean isEditorActive()
    {
        if ( this.openDefaultEditorAction.isActive() )
        {
            return true;
        }
        if ( this.openBestEditorAction.isActive() )
        {
            return true;
        }
        for ( int i = 0; i < this.openEditorActions.length; i++ )
        {
            if ( this.openEditorActions[i].isActive() )
            {
                return true;
            }
        }

        return false;
    }

}
