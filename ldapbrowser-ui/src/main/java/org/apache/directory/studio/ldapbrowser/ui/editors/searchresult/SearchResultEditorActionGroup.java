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

package org.apache.directory.studio.ldapbrowser.ui.editors.searchresult;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.common.actions.CopyAction;
import org.apache.directory.studio.ldapbrowser.common.actions.NewValueAction;
import org.apache.directory.studio.ldapbrowser.common.actions.PropertiesAction;
import org.apache.directory.studio.ldapbrowser.common.actions.RefreshAction;
import org.apache.directory.studio.ldapbrowser.common.actions.ShowDecoratedValuesAction;
import org.apache.directory.studio.ldapbrowser.common.actions.ValueEditorPreferencesAction;
import org.apache.directory.studio.ldapbrowser.common.actions.proxy.ActionHandlerManager;
import org.apache.directory.studio.ldapbrowser.common.actions.proxy.BrowserActionProxy;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.ui.actions.CopyAttributeDescriptionAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.CopyDnAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.CopyEntryAsCsvAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.CopySearchFilterAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.CopyUrlAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.CopyValueAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.LocateDnInDitAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.NewBatchOperationAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.NewSearchAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.OpenSchemaBrowserAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.OpenSearchResultAction;
import org.apache.directory.studio.ldapbrowser.ui.actions.proxy.SearchResultEditorActionProxy;
import org.apache.directory.studio.utils.ActionUtils;
import org.apache.directory.studio.valueeditors.IValueEditor;
import org.apache.directory.studio.valueeditors.ValueEditorManager;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;


/**
 * The SearchResultEditorActionGroup manages all actions of the search result editor.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SearchResultEditorActionGroup implements ActionHandlerManager, IMenuListener
{

    /** The show DN action. */
    private ShowDNAction showDNAction;

    /** The show links action. */
    private ShowLinksAction showLinksAction;

    /** The show decorated values action. */
    private ShowDecoratedValuesAction showDecoratedValuesAction;

    /** The open search result editor preference page. */
    private OpenSearchResultEditorPreferencePage openSearchResultEditorPreferencePage;

    /** The show quick filter action. */
    private ShowQuickFilterAction showQuickFilterAction;

    /** The open default editor action. */
    private SearchResultEditorActionProxy openDefaultValueEditorActionProxy;

    /** The open best editor action. */
    private SearchResultEditorActionProxy openBestValueEditorActionProxy;

    /** The open editor actions. */
    private SearchResultEditorActionProxy[] openValueEditorActionProxies;

    /** The open entry value editor action. */
    private SearchResultEditorActionProxy openEntryValueEditorActionProxy;

    /** The open value editor preferences action. */
    private ValueEditorPreferencesAction openValueEditorPreferencesAction;

    private static final String copyTableAction = "copyTableAction"; //$NON-NLS-1$

    private static final String refreshSearchAction = "refreshSearchAction"; //$NON-NLS-1$

    private final static String newValueAction = "newValueAction"; //$NON-NLS-1$

    private final static String newSearchAction = "newSearchAction"; //$NON-NLS-1$

    private static final String newBatchOperationAction = "newBatchOperationAction"; //$NON-NLS-1$

    private final static String copyAction = "copyAction"; //$NON-NLS-1$

    private final static String pasteAction = "pasteAction"; //$NON-NLS-1$

    private final static String deleteAction = "deleteAction"; //$NON-NLS-1$

    private static final String copyDnAction = "copyDnAction"; //$NON-NLS-1$

    private static final String copyUrlAction = "copyUrlAction"; //$NON-NLS-1$

    private static final String copyAttriuteDescriptionAction = "copyAttriuteDescriptionAction"; //$NON-NLS-1$

    private static final String copyDisplayValueAction = "copyDisplayValueAction"; //$NON-NLS-1$

    private static final String copyValueUtf8Action = "copyValueUtf8Action"; //$NON-NLS-1$

    private static final String copyValueBase64Action = "copyValueBase64Action"; //$NON-NLS-1$

    private static final String copyValueHexAction = "copyValueHexAction"; //$NON-NLS-1$

    private static final String copyValueAsLdifAction = "copyValueAsLdifAction"; //$NON-NLS-1$

    private static final String copySearchFilterAction = "copySearchFilterAction"; //$NON-NLS-1$

    private static final String copyNotSearchFilterAction = "copyNotSearchFilterAction"; //$NON-NLS-1$

    private static final String copyAndSearchFilterAction = "copyAndSearchFilterAction"; //$NON-NLS-1$

    private static final String copyOrSearchFilterAction = "copyOrSearchFilterAction"; //$NON-NLS-1$

    private static final String openSearchResultAction = "showEntryInSearchResultsAction"; //$NON-NLS-1$

    private static final String locateDnInDitAction = "locateDnInDitAction"; //$NON-NLS-1$

    private static final String showOcdAction = "showOcdAction"; //$NON-NLS-1$

    private static final String showAtdAction = "showAtdAction"; //$NON-NLS-1$

    private static final String showEqualityMrdAction = "showEqualityMrdAction"; //$NON-NLS-1$

    private static final String showSubstringMrdAction = "showSubstringMrdAction"; //$NON-NLS-1$

    private static final String showOrderingMrdAction = "showOrderingMrdAction"; //$NON-NLS-1$

    private static final String showLsdAction = "showLsdAction"; //$NON-NLS-1$

    private final static String propertyDialogAction = "propertyDialogAction"; //$NON-NLS-1$

    /** The search result editor action map. */
    private Map<String, SearchResultEditorActionProxy> searchResultEditorActionMap;

    /** The action bars. */
    private IActionBars actionBars;

    /** The search result editor. */
    private SearchResultEditor searchResultEditor;


    /**
     * Creates a new instance of SearchResultEditorActionGroup.
     * 
     * @param searchResultEditor the search result editor
     */
    public SearchResultEditorActionGroup( SearchResultEditor searchResultEditor )
    {
        this.searchResultEditor = searchResultEditor;
        searchResultEditorActionMap = new HashMap<String, SearchResultEditorActionProxy>();

        TableViewer viewer = searchResultEditor.getMainWidget().getViewer();
        SearchResultEditorCursor cursor = searchResultEditor.getConfiguration().getCursor( viewer );
        ValueEditorManager valueEditorManager = searchResultEditor.getConfiguration().getValueEditorManager( viewer );

        showDNAction = new ShowDNAction();
        showLinksAction = new ShowLinksAction();
        showDecoratedValuesAction = new ShowDecoratedValuesAction();
        openSearchResultEditorPreferencePage = new OpenSearchResultEditorPreferencePage();
        showQuickFilterAction = new ShowQuickFilterAction( searchResultEditor.getMainWidget().getQuickFilterWidget() );

        openBestValueEditorActionProxy = new SearchResultEditorActionProxy( cursor, new OpenBestEditorAction( viewer,
            cursor, valueEditorManager, this ) );
        openDefaultValueEditorActionProxy = new SearchResultEditorActionProxy( cursor, new OpenDefaultEditorAction(
            viewer, cursor, valueEditorManager, openBestValueEditorActionProxy, this ) );
        IValueEditor[] valueEditors = searchResultEditor.getConfiguration().getValueEditorManager( viewer )
            .getAllValueEditors();
        openValueEditorActionProxies = new SearchResultEditorActionProxy[valueEditors.length];
        for ( int i = 0; i < openValueEditorActionProxies.length; i++ )
        {
            openValueEditorActionProxies[i] = new SearchResultEditorActionProxy( cursor, new OpenEditorAction( viewer,
                cursor, valueEditorManager, valueEditors[i], this ) );
        }
        openEntryValueEditorActionProxy = new SearchResultEditorActionProxy( cursor, new OpenEntryEditorAction( viewer,
            cursor, valueEditorManager, valueEditorManager.getEntryValueEditor(), this ) );
        openValueEditorPreferencesAction = new ValueEditorPreferencesAction();

        searchResultEditorActionMap.put( copyTableAction, new SearchResultEditorActionProxy( cursor,
            new CopyEntryAsCsvAction( CopyEntryAsCsvAction.MODE_TABLE ) ) );
        searchResultEditorActionMap.put( refreshSearchAction, new SearchResultEditorActionProxy( cursor,
            new RefreshAction() ) );

        searchResultEditorActionMap.put( newValueAction, new SearchResultEditorActionProxy( cursor,
            new NewValueAction() ) );
        searchResultEditorActionMap.put( newSearchAction, new SearchResultEditorActionProxy( cursor,
            new NewSearchAction() ) );
        searchResultEditorActionMap.put( newBatchOperationAction, new SearchResultEditorActionProxy( cursor,
            new NewBatchOperationAction() ) );

        searchResultEditorActionMap.put( locateDnInDitAction, new SearchResultEditorActionProxy( cursor,
            new LocateDnInDitAction() ) );
        searchResultEditorActionMap.put( openSearchResultAction, new SearchResultEditorActionProxy( cursor,
            new OpenSearchResultAction() ) );

        searchResultEditorActionMap.put( showOcdAction, new SearchResultEditorActionProxy( cursor,
            new OpenSchemaBrowserAction( OpenSchemaBrowserAction.MODE_OBJECTCLASS ) ) );
        searchResultEditorActionMap.put( showAtdAction, new SearchResultEditorActionProxy( cursor,
            new OpenSchemaBrowserAction( OpenSchemaBrowserAction.MODE_ATTRIBUTETYPE ) ) );
        searchResultEditorActionMap.put( showEqualityMrdAction, new SearchResultEditorActionProxy( cursor,
            new OpenSchemaBrowserAction( OpenSchemaBrowserAction.MODE_EQUALITYMATCHINGRULE ) ) );
        searchResultEditorActionMap.put( showSubstringMrdAction, new SearchResultEditorActionProxy( cursor,
            new OpenSchemaBrowserAction( OpenSchemaBrowserAction.MODE_SUBSTRINGMATCHINGRULE ) ) );
        searchResultEditorActionMap.put( showOrderingMrdAction, new SearchResultEditorActionProxy( cursor,
            new OpenSchemaBrowserAction( OpenSchemaBrowserAction.MODE_ORDERINGMATCHINGRULE ) ) );
        searchResultEditorActionMap.put( showLsdAction, new SearchResultEditorActionProxy( cursor,
            new OpenSchemaBrowserAction( OpenSchemaBrowserAction.MODE_SYNTAX ) ) );

        searchResultEditorActionMap.put( pasteAction, new SearchResultEditorActionProxy( cursor,
            new SearchResultEditorPasteAction() ) );
        searchResultEditorActionMap.put( copyAction, new SearchResultEditorActionProxy( cursor, new CopyAction(
            ( BrowserActionProxy ) this.searchResultEditorActionMap.get( pasteAction ), valueEditorManager ) ) );
        searchResultEditorActionMap.put( deleteAction, new SearchResultEditorActionProxy( cursor,
            new SearchResultDeleteAction() ) );

        searchResultEditorActionMap.put( copyDnAction, new SearchResultEditorActionProxy( cursor, new CopyDnAction() ) );
        searchResultEditorActionMap
            .put( copyUrlAction, new SearchResultEditorActionProxy( cursor, new CopyUrlAction() ) );
        searchResultEditorActionMap.put( copyAttriuteDescriptionAction, new SearchResultEditorActionProxy( cursor,
            new CopyAttributeDescriptionAction() ) );
        searchResultEditorActionMap.put( copyDisplayValueAction, new SearchResultEditorActionProxy( cursor,
            new CopyValueAction( CopyValueAction.Mode.DISPLAY, valueEditorManager ) ) );
        searchResultEditorActionMap.put( copyValueUtf8Action, new SearchResultEditorActionProxy( cursor,
            new CopyValueAction( CopyValueAction.Mode.UTF8, valueEditorManager ) ) );
        searchResultEditorActionMap.put( copyValueBase64Action, new SearchResultEditorActionProxy( cursor,
            new CopyValueAction( CopyValueAction.Mode.BASE64, valueEditorManager ) ) );
        searchResultEditorActionMap.put( copyValueHexAction, new SearchResultEditorActionProxy( cursor,
            new CopyValueAction( CopyValueAction.Mode.HEX, valueEditorManager ) ) );
        searchResultEditorActionMap.put( copyValueAsLdifAction, new SearchResultEditorActionProxy( cursor,
            new CopyValueAction( CopyValueAction.Mode.LDIF, valueEditorManager ) ) );

        searchResultEditorActionMap.put( copySearchFilterAction, new SearchResultEditorActionProxy( cursor,
            new CopySearchFilterAction( CopySearchFilterAction.MODE_EQUALS ) ) );
        searchResultEditorActionMap.put( copyNotSearchFilterAction, new SearchResultEditorActionProxy( cursor,
            new CopySearchFilterAction( CopySearchFilterAction.MODE_NOT ) ) );
        searchResultEditorActionMap.put( copyAndSearchFilterAction, new SearchResultEditorActionProxy( cursor,
            new CopySearchFilterAction( CopySearchFilterAction.MODE_AND ) ) );
        searchResultEditorActionMap.put( copyOrSearchFilterAction, new SearchResultEditorActionProxy( cursor,
            new CopySearchFilterAction( CopySearchFilterAction.MODE_OR ) ) );

        searchResultEditorActionMap.put( propertyDialogAction, new SearchResultEditorActionProxy( cursor,
            new PropertiesAction() ) );
    }


    /**
     * Disposes this action group.
     */
    public void dispose()
    {
        if ( searchResultEditor != null )
        {
            showDecoratedValuesAction = null;
            showDNAction.dispose();
            showDNAction = null;
            showLinksAction.dispose();
            showLinksAction = null;
            openSearchResultEditorPreferencePage = null;
            showQuickFilterAction.dispose();
            showQuickFilterAction = null;

            openDefaultValueEditorActionProxy.dispose();
            openDefaultValueEditorActionProxy = null;
            openBestValueEditorActionProxy.dispose();
            openBestValueEditorActionProxy = null;
            for ( int i = 0; i < openValueEditorActionProxies.length; i++ )
            {
                openValueEditorActionProxies[i].dispose();
                openValueEditorActionProxies[i] = null;
            }
            openEntryValueEditorActionProxy.dispose();
            openEntryValueEditorActionProxy = null;
            openValueEditorPreferencesAction = null;

            for ( Iterator<String> it = this.searchResultEditorActionMap.keySet().iterator(); it.hasNext(); )
            {
                String key = it.next();
                SearchResultEditorActionProxy action = searchResultEditorActionMap.get( key );
                action.dispose();
                action = null;
                it.remove();
            }
            searchResultEditorActionMap.clear();
            searchResultEditorActionMap = null;

            actionBars = null;
            searchResultEditor = null;
        }
    }


    /**
     * Fills the tool bar.
     * 
     * @param toolBarManager the tool bar manager
     */
    public void fillToolBar( IToolBarManager toolBarManager )
    {
        toolBarManager.add( new Separator() );
        toolBarManager.add( searchResultEditorActionMap.get( newValueAction ) );
        toolBarManager.add( new Separator() );
        toolBarManager.add( searchResultEditorActionMap.get( deleteAction ) );
        toolBarManager.add( new Separator() );
        toolBarManager.add( searchResultEditorActionMap.get( refreshSearchAction ) );
        toolBarManager.add( new Separator() );
        toolBarManager.add( searchResultEditorActionMap.get( copyTableAction ) );
        toolBarManager.add( new Separator() );
        toolBarManager.add( showQuickFilterAction );
        toolBarManager.update( true );
    }


    /**
     * Fills the menu.
     * 
     * @param menuManager the menu manager
     */
    public void fillMenu( IMenuManager menuManager )
    {
        menuManager.add( showDNAction );
        menuManager.add( showLinksAction );
        menuManager.add( showDecoratedValuesAction );
        menuManager.add( new Separator() );
        menuManager.add( openSearchResultEditorPreferencePage );
        menuManager.addMenuListener( new IMenuListener()
        {
            public void menuAboutToShow( IMenuManager manager )
            {
                showDecoratedValuesAction.setChecked( !BrowserCommonActivator.getDefault().getPreferenceStore()
                    .getBoolean( BrowserCommonConstants.PREFERENCE_SHOW_RAW_VALUES ) );
            }
        } );
        menuManager.update( true );
    }


    /**
     * Enable global action handlers.
     * 
     * @param actionBars the action bars
     */
    public void enableGlobalActionHandlers( IActionBars actionBars )
    {
        this.actionBars = actionBars;
    }


    /**
     * Fills the context menu.
     * 
     * @param menuManager the menu manager
     */
    public void fillContextMenu( IMenuManager menuManager )
    {
        menuManager.setRemoveAllWhenShown( true );
        menuManager.addMenuListener( this );
    }


    /**
     * {@inheritDoc}
     */
    public void menuAboutToShow( IMenuManager menuManager )
    {
        // new
        menuManager.add( searchResultEditorActionMap.get( newValueAction ) );
        menuManager.add( searchResultEditorActionMap.get( newSearchAction ) );
        menuManager.add( searchResultEditorActionMap.get( newBatchOperationAction ) );
        menuManager.add( new Separator() );

        // navigation
        menuManager.add( searchResultEditorActionMap.get( locateDnInDitAction ) );
        menuManager.add( searchResultEditorActionMap.get( openSearchResultAction ) );
        MenuManager schemaMenuManager = new MenuManager( Messages
            .getString( "SearchResultEditorActionGroup.OpenSchemaBrowser" ) ); //$NON-NLS-1$
        schemaMenuManager.add( searchResultEditorActionMap.get( showOcdAction ) );
        schemaMenuManager.add( searchResultEditorActionMap.get( showAtdAction ) );
        schemaMenuManager.add( searchResultEditorActionMap.get( showEqualityMrdAction ) );
        schemaMenuManager.add( searchResultEditorActionMap.get( showSubstringMrdAction ) );
        schemaMenuManager.add( searchResultEditorActionMap.get( showOrderingMrdAction ) );
        schemaMenuManager.add( searchResultEditorActionMap.get( showLsdAction ) );
        menuManager.add( schemaMenuManager );
        MenuManager showInSubMenu = new MenuManager( Messages.getString( "SearchResultEditorActionGroup.ShowIn" ) ); //$NON-NLS-1$
        showInSubMenu.add( ContributionItemFactory.VIEWS_SHOW_IN.create( PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow() ) );
        menuManager.add( showInSubMenu );
        menuManager.add( new Separator() );

        // copy, paste, delete
        menuManager.add( searchResultEditorActionMap.get( copyAction ) );
        menuManager.add( searchResultEditorActionMap.get( pasteAction ) );
        menuManager.add( searchResultEditorActionMap.get( deleteAction ) );
        MenuManager advancedMenuManager = new MenuManager( Messages
            .getString( "SearchResultEditorActionGroup.Advanced" ) ); //$NON-NLS-1$
        advancedMenuManager.add( searchResultEditorActionMap.get( copyDnAction ) );
        advancedMenuManager.add( searchResultEditorActionMap.get( copyUrlAction ) );
        advancedMenuManager.add( new Separator() );
        advancedMenuManager.add( searchResultEditorActionMap.get( copyAttriuteDescriptionAction ) );
        advancedMenuManager.add( new Separator() );
        advancedMenuManager.add( searchResultEditorActionMap.get( copyDisplayValueAction ) );
        advancedMenuManager.add( searchResultEditorActionMap.get( copyValueUtf8Action ) );
        advancedMenuManager.add( searchResultEditorActionMap.get( copyValueBase64Action ) );
        advancedMenuManager.add( searchResultEditorActionMap.get( copyValueHexAction ) );
        advancedMenuManager.add( new Separator() );
        advancedMenuManager.add( searchResultEditorActionMap.get( copyValueAsLdifAction ) );
        advancedMenuManager.add( new Separator() );
        advancedMenuManager.add( searchResultEditorActionMap.get( copySearchFilterAction ) );
        advancedMenuManager.add( searchResultEditorActionMap.get( copyNotSearchFilterAction ) );
        advancedMenuManager.add( searchResultEditorActionMap.get( copyAndSearchFilterAction ) );
        advancedMenuManager.add( searchResultEditorActionMap.get( copyOrSearchFilterAction ) );
        menuManager.add( advancedMenuManager );
        menuManager.add( new Separator() );

        // edit
        menuManager.add( openDefaultValueEditorActionProxy );
        MenuManager editorMenuManager = new MenuManager( Messages.getString( "SearchResultEditorActionGroup.EditValue" ) ); //$NON-NLS-1$
        if ( openBestValueEditorActionProxy.isEnabled() )
        {
            editorMenuManager.add( openBestValueEditorActionProxy );
            editorMenuManager.add( new Separator() );
        }
        for ( int i = 0; i < openValueEditorActionProxies.length; i++ )
        {
            if ( openValueEditorActionProxies[i].isEnabled()
                && ( ( OpenEditorAction ) openValueEditorActionProxies[i].getAction() ).getValueEditor().getClass() != ( ( OpenBestEditorAction ) openBestValueEditorActionProxy
                    .getAction() ).getBestValueEditor().getClass() )
            {
                editorMenuManager.add( openValueEditorActionProxies[i] );
            }
        }
        editorMenuManager.add( new Separator() );
        editorMenuManager.add( openValueEditorPreferencesAction );
        menuManager.add( editorMenuManager );
        menuManager.add( openEntryValueEditorActionProxy );
        menuManager.add( new Separator() );

        // refresh
        menuManager.add( searchResultEditorActionMap.get( refreshSearchAction ) );
        menuManager.add( new Separator() );

        // additions
        menuManager.add( new Separator( IWorkbenchActionConstants.MB_ADDITIONS ) );

        // / properties
        menuManager.add( searchResultEditorActionMap.get( propertyDialogAction ) );
    }


    /**
     * {@inheritDoc}
     */
    public void activateGlobalActionHandlers()
    {
        if ( actionBars != null )
        {
            actionBars
                .setGlobalActionHandler( ActionFactory.COPY.getId(), searchResultEditorActionMap.get( copyAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.PASTE.getId(), searchResultEditorActionMap
                .get( pasteAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.DELETE.getId(), searchResultEditorActionMap
                .get( deleteAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.REFRESH.getId(), searchResultEditorActionMap
                .get( refreshSearchAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.PROPERTIES.getId(), searchResultEditorActionMap
                .get( propertyDialogAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.FIND.getId(), showQuickFilterAction );
            actionBars.updateActionBars();
        }

        IAction nva = searchResultEditorActionMap.get( newValueAction );
        ActionUtils.activateActionHandler( nva );
        IAction lid = searchResultEditorActionMap.get( locateDnInDitAction );
        ActionUtils.activateActionHandler( lid );
        IAction osr = searchResultEditorActionMap.get( openSearchResultAction );
        ActionUtils.activateActionHandler( osr );
        ActionUtils.activateActionHandler( openDefaultValueEditorActionProxy );
        ActionUtils.activateActionHandler( openEntryValueEditorActionProxy );
    }


    /**
     * {@inheritDoc}
     */
    public void deactivateGlobalActionHandlers()
    {
        if ( actionBars != null )
        {
            actionBars.setGlobalActionHandler( ActionFactory.COPY.getId(), null );
            actionBars.setGlobalActionHandler( ActionFactory.PASTE.getId(), null );
            actionBars.setGlobalActionHandler( ActionFactory.DELETE.getId(), null );
            actionBars.setGlobalActionHandler( ActionFactory.REFRESH.getId(), null );
            actionBars.setGlobalActionHandler( ActionFactory.FIND.getId(), null );
            actionBars.setGlobalActionHandler( ActionFactory.PROPERTIES.getId(), null );
            actionBars.updateActionBars();
        }

        IAction nva = searchResultEditorActionMap.get( newValueAction );
        ActionUtils.deactivateActionHandler( nva );
        IAction lid = searchResultEditorActionMap.get( locateDnInDitAction );
        ActionUtils.deactivateActionHandler( lid );
        IAction osr = searchResultEditorActionMap.get( openSearchResultAction );
        ActionUtils.deactivateActionHandler( osr );
        ActionUtils.deactivateActionHandler( openDefaultValueEditorActionProxy );
        ActionUtils.deactivateActionHandler( openEntryValueEditorActionProxy );
    }


    /**
     * Gets the open best editor action.
     * 
     * @return the open best editor action
     */
    public OpenBestEditorAction getOpenBestEditorAction()
    {
        return ( OpenBestEditorAction ) openBestValueEditorActionProxy.getAction();
    }


    /**
     * Sets the input.
     * 
     * @param search the new input
     */
    public void setInput( ISearch search )
    {
        for ( SearchResultEditorActionProxy action : searchResultEditorActionMap.values() )
        {
            action.inputChanged( search );
        }
    }


    /**
     * Checks if is editor active.
     * 
     * @return true, if is editor active
     */
    public boolean isEditorActive()
    {
        if ( ( ( AbstractOpenEditorAction ) openDefaultValueEditorActionProxy.getAction() ).isActive() )
        {
            return true;
        }
        if ( ( ( AbstractOpenEditorAction ) openBestValueEditorActionProxy.getAction() ).isActive() )
        {
            return true;
        }
        if ( ( ( AbstractOpenEditorAction ) openEntryValueEditorActionProxy.getAction() ).isActive() )
        {
            return true;
        }
        for ( int i = 0; i < openValueEditorActionProxies.length; i++ )
        {
            if ( ( ( AbstractOpenEditorAction ) openValueEditorActionProxies[i].getAction() ).isActive() )
            {
                return true;
            }
        }

        return false;
    }

}
