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

package org.apache.directory.studio.ldapbrowser.common.widgets.entryeditor;


import java.util.HashMap;
import java.util.Map;

import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.common.actions.CopyAction;
import org.apache.directory.studio.ldapbrowser.common.actions.DeleteAction;
import org.apache.directory.studio.ldapbrowser.common.actions.NewValueAction;
import org.apache.directory.studio.ldapbrowser.common.actions.PasteAction;
import org.apache.directory.studio.ldapbrowser.common.actions.PropertiesAction;
import org.apache.directory.studio.ldapbrowser.common.actions.SelectAllAction;
import org.apache.directory.studio.ldapbrowser.common.actions.ShowRawValuesAction;
import org.apache.directory.studio.ldapbrowser.common.actions.ValueEditorPreferencesAction;
import org.apache.directory.studio.ldapbrowser.common.actions.proxy.ActionHandlerManager;
import org.apache.directory.studio.ldapbrowser.common.actions.proxy.BrowserActionProxy;
import org.apache.directory.studio.ldapbrowser.common.actions.proxy.EntryEditorActionProxy;
import org.apache.directory.studio.valueeditors.IValueEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.commands.ICommandService;


/**
 * The EntryEditorWidgetActionGroup manages all actions of the entry editor widget.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class EntryEditorWidgetActionGroup implements ActionHandlerManager
{

    /** The open sort dialog action. */
    protected OpenSortDialogAction openSortDialogAction;

    /** The show raw values action. */
    protected ShowRawValuesAction showRawValuesAction;

    /** The show quick filter action. */
    protected ShowQuickFilterAction showQuickFilterAction;

    /** The open default editor action. */
    protected EntryEditorActionProxy openDefaultValueEditorActionProxy;

    /** The open best editor action. */
    protected EntryEditorActionProxy openBestValueEditorActionProxy;

    /** The open editor actions. */
    protected EntryEditorActionProxy[] openValueEditorActionProxies;

    /** The open value editor preferences action. */
    protected ValueEditorPreferencesAction openValueEditorPreferencesAction;

    /** The Constant newValueAction. */
    protected final static String newValueAction = "newValueAction";

    /** The Constant copyAction. */
    protected final static String copyAction = "copyAction";

    /** The Constant pasteAction. */
    protected final static String pasteAction = "pasteAction";

    /** The Constant deleteAction. */
    protected final static String deleteAction = "deleteAction";

    /** The Constant selectAllAction. */
    protected final static String selectAllAction = "selectAllAction";

    /** The Constant propertyDialogAction. */
    protected final static String propertyDialogAction = "propertyDialogAction";

    /** The entry editor action map. */
    protected Map<String, EntryEditorActionProxy> entryEditorActionMap;

    /** The action bars. */
    protected IActionBars actionBars;

    /** The main widget. */
    private EntryEditorWidget mainWidget;


    /**
     * Creates a new instance of EntryEditorWidgetActionGroup.
     * 
     * @param mainWidget the main widget
     * @param configuration the configuration
     */
    public EntryEditorWidgetActionGroup( EntryEditorWidget mainWidget, EntryEditorWidgetConfiguration configuration )
    {
        this.mainWidget = mainWidget;

        entryEditorActionMap = new HashMap<String, EntryEditorActionProxy>();
        TreeViewer viewer = mainWidget.getViewer();

        openSortDialogAction = new OpenSortDialogAction( configuration.getPreferences() );
        showRawValuesAction = new ShowRawValuesAction();
        showQuickFilterAction = new ShowQuickFilterAction( mainWidget.getQuickFilterWidget() );

        openBestValueEditorActionProxy = new EntryEditorActionProxy( viewer, this, new OpenBestEditorAction( viewer,
            configuration.getValueEditorManager( viewer ) ) );
        openDefaultValueEditorActionProxy = new EntryEditorActionProxy( viewer, this, new OpenDefaultEditorAction(
            viewer, openBestValueEditorActionProxy, false ) );
        IValueEditor[] valueEditors = configuration.getValueEditorManager( viewer ).getAllValueEditors();
        openValueEditorActionProxies = new EntryEditorActionProxy[valueEditors.length];
        for ( int i = 0; i < openValueEditorActionProxies.length; i++ )
        {
            openValueEditorActionProxies[i] = new EntryEditorActionProxy( viewer, this, new OpenEditorAction( viewer,
                configuration.getValueEditorManager( viewer ), valueEditors[i] ) );
        }
        openValueEditorPreferencesAction = new ValueEditorPreferencesAction();

        entryEditorActionMap.put( newValueAction, new EntryEditorActionProxy( viewer, this, new NewValueAction() ) );

        entryEditorActionMap.put( pasteAction, new EntryEditorActionProxy( viewer, this, new PasteAction() ) );
        entryEditorActionMap.put( copyAction, new EntryEditorActionProxy( viewer, this, new CopyAction(
            ( BrowserActionProxy ) entryEditorActionMap.get( pasteAction ) ) ) );
        entryEditorActionMap.put( deleteAction, new EntryEditorActionProxy( viewer, this, new DeleteAction() ) );
        entryEditorActionMap.put( selectAllAction, new EntryEditorActionProxy( viewer, this, new SelectAllAction(
            viewer ) ) );

        entryEditorActionMap.put( propertyDialogAction, new EntryEditorActionProxy( viewer, this,
            new PropertiesAction() ) );
    }


    /**
     * Disposes this action group.
     */
    public void dispose()
    {
        if ( mainWidget != null )
        {
            openSortDialogAction = null;
            showQuickFilterAction.dispose();
            showQuickFilterAction = null;
            showRawValuesAction = null;

            openDefaultValueEditorActionProxy.dispose();
            openDefaultValueEditorActionProxy = null;
            openBestValueEditorActionProxy.dispose();
            openBestValueEditorActionProxy = null;
            for ( EntryEditorActionProxy action : openValueEditorActionProxies )
            {
                action.dispose();
            }
            openValueEditorPreferencesAction = null;

            for ( EntryEditorActionProxy action : entryEditorActionMap.values() )
            {
                action.dispose();
            }
            entryEditorActionMap.clear();
            entryEditorActionMap = null;

            actionBars = null;
            mainWidget = null;
        }
    }


    /**
     * Enables global action handlers.
     * 
     * @param actionBars the action bars
     */
    public void enableGlobalActionHandlers( IActionBars actionBars )
    {
        this.actionBars = actionBars;
        activateGlobalActionHandlers();
    }


    /**
     * Fill the tool bar.
     * 
     * @param toolBarManager the tool bar manager
     */
    public void fillToolBar( IToolBarManager toolBarManager )
    {
        toolBarManager.add( ( IAction ) entryEditorActionMap.get( newValueAction ) );
        toolBarManager.add( new Separator() );
        toolBarManager.add( ( IAction ) entryEditorActionMap.get( deleteAction ) );
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
        menuManager.add( openSortDialogAction );
        menuManager.add( showRawValuesAction );
        menuManager.addMenuListener( new IMenuListener()
        {
            public void menuAboutToShow( IMenuManager manager )
            {
                showRawValuesAction.setChecked( BrowserCommonActivator.getDefault().getPreferenceStore().getBoolean(
                    BrowserCommonConstants.PREFERENCE_SHOW_RAW_VALUES ) );
            }
        } );
    }


    /**
     * Fills the context menu. Adds a menu listener to the given menu manager
     * to fill the context menu whenever it pops up.
     * 
     * @param menuManager the menu manager
     */
    public void fillContextMenu( IMenuManager menuManager )
    {
        menuManager.setRemoveAllWhenShown( true );
        menuManager.addMenuListener( new IMenuListener()
        {
            public void menuAboutToShow( IMenuManager manager )
            {
                contextMenuAboutToShow( manager );
            }
        } );
    }


    /**
     * Fills the context menu.
     * 
     * @param menuManager the menu manager
     */
    protected void contextMenuAboutToShow( IMenuManager menuManager )
    {
        // new
        menuManager.add( ( IAction ) entryEditorActionMap.get( newValueAction ) );
        menuManager.add( new Separator() );

        // copy, paste, delete
        menuManager.add( ( IAction ) entryEditorActionMap.get( copyAction ) );
        menuManager.add( ( IAction ) entryEditorActionMap.get( pasteAction ) );
        menuManager.add( ( IAction ) entryEditorActionMap.get( deleteAction ) );
        menuManager.add( ( IAction ) entryEditorActionMap.get( selectAllAction ) );
        menuManager.add( new Separator() );

        // edit
        addEditMenu( menuManager );
        menuManager.add( new Separator() );

        // properties
        menuManager.add( ( IAction ) entryEditorActionMap.get( propertyDialogAction ) );
    }


    /**
     * Adds the value editors to the menu.
     * 
     * @param menuManager the menu manager
     */
    protected void addEditMenu( IMenuManager menuManager )
    {
        menuManager.add( openDefaultValueEditorActionProxy );
        MenuManager editorMenuManager = new MenuManager( "Edit Value With" );
        if ( openBestValueEditorActionProxy.isEnabled() )
        {
            editorMenuManager.add( openBestValueEditorActionProxy );
            editorMenuManager.add( new Separator() );
        }
        for ( EntryEditorActionProxy action : openValueEditorActionProxies )
        {
            if ( action.isEnabled()
                && ( ( OpenEditorAction ) action.getAction() ).getValueEditor().getClass() != ( ( OpenBestEditorAction ) openBestValueEditorActionProxy
                    .getAction() ).getBestValueEditor().getClass() )
            {
                editorMenuManager.add( action );
            }
        }
        editorMenuManager.add( new Separator() );
        editorMenuManager.add( openValueEditorPreferencesAction );
        menuManager.add( editorMenuManager );
    }


    /**
     * Activates global action handlers.
     */
    public void activateGlobalActionHandlers()
    {
        ICommandService commandService = ( ICommandService ) PlatformUI.getWorkbench().getAdapter(
            ICommandService.class );

        if ( actionBars != null )
        {
            actionBars.setGlobalActionHandler( ActionFactory.COPY.getId(), ( IAction ) entryEditorActionMap
                .get( copyAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.PASTE.getId(), ( IAction ) entryEditorActionMap
                .get( pasteAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.DELETE.getId(), ( IAction ) entryEditorActionMap
                .get( deleteAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.SELECT_ALL.getId(), ( IAction ) entryEditorActionMap
                .get( selectAllAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.PROPERTIES.getId(), ( IAction ) entryEditorActionMap
                .get( propertyDialogAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.FIND.getId(), showQuickFilterAction ); // IWorkbenchActionDefinitionIds.FIND_REPLACE

            actionBars.updateActionBars();
        }
        else
        {
            if ( commandService != null )
            {
                IAction da = ( IAction ) entryEditorActionMap.get( deleteAction );
                da.setActionDefinitionId( "org.apache.directory.studio.ldapbrowser.action.delete" );
                commandService.getCommand( da.getActionDefinitionId() ).setHandler( new ActionHandler( da ) );

                IAction ca = ( IAction ) entryEditorActionMap.get( copyAction );
                ca.setActionDefinitionId( "org.apache.directory.studio.ldapbrowser.action.copy" );
                commandService.getCommand( ca.getActionDefinitionId() ).setHandler( new ActionHandler( ca ) );

                IAction pa = ( IAction ) entryEditorActionMap.get( pasteAction );
                pa.setActionDefinitionId( "org.apache.directory.studio.ldapbrowser.action.paste" );
                commandService.getCommand( pa.getActionDefinitionId() ).setHandler( new ActionHandler( pa ) );

                showQuickFilterAction.setActionDefinitionId( "org.apache.directory.studio.ldapbrowser.action.find" );
                commandService.getCommand( showQuickFilterAction.getActionDefinitionId() ).setHandler(
                    new ActionHandler( showQuickFilterAction ) );

                IAction pda = ( IAction ) entryEditorActionMap.get( propertyDialogAction );
                pda.setActionDefinitionId( "org.apache.directory.studio.ldapbrowser.action.properties" );
                commandService.getCommand( pda.getActionDefinitionId() ).setHandler( new ActionHandler( pda ) );
            }
        }

        if ( commandService != null )
        {
            IAction nva = ( IAction ) entryEditorActionMap.get( newValueAction );
            commandService.getCommand( nva.getActionDefinitionId() ).setHandler( new ActionHandler( nva ) );
            commandService.getCommand( openDefaultValueEditorActionProxy.getActionDefinitionId() ).setHandler(
                new ActionHandler( openDefaultValueEditorActionProxy ) );
        }
    }


    /**
     * Deactivates global action handlers.
     */
    public void deactivateGlobalActionHandlers()
    {
        ICommandService commandService = ( ICommandService ) PlatformUI.getWorkbench().getAdapter(
            ICommandService.class );

        if ( actionBars != null )
        {
            actionBars.setGlobalActionHandler( ActionFactory.COPY.getId(), null );
            actionBars.setGlobalActionHandler( ActionFactory.PASTE.getId(), null );
            actionBars.setGlobalActionHandler( ActionFactory.DELETE.getId(), null );
            actionBars.setGlobalActionHandler( ActionFactory.SELECT_ALL.getId(), null );
            actionBars.setGlobalActionHandler( ActionFactory.FIND.getId(), null );
            actionBars.setGlobalActionHandler( ActionFactory.PROPERTIES.getId(), null );

            actionBars.updateActionBars();
        }
        else
        {
            if ( commandService != null )
            {
                IAction da = ( IAction ) entryEditorActionMap.get( deleteAction );
                commandService.getCommand( da.getActionDefinitionId() ).setHandler( null );

                IAction ca = ( IAction ) entryEditorActionMap.get( copyAction );
                commandService.getCommand( ca.getActionDefinitionId() ).setHandler( null );

                IAction pa = ( IAction ) entryEditorActionMap.get( pasteAction );
                commandService.getCommand( pa.getActionDefinitionId() ).setHandler( null );

                commandService.getCommand( showQuickFilterAction.getActionDefinitionId() ).setHandler( null );

                IAction pda = ( IAction ) entryEditorActionMap.get( propertyDialogAction );
                commandService.getCommand( pda.getActionDefinitionId() ).setHandler( null );
            }
        }

        if ( commandService != null )
        {
            IAction nva = ( IAction ) entryEditorActionMap.get( newValueAction );
            commandService.getCommand( nva.getActionDefinitionId() ).setHandler( null );
            commandService.getCommand( openDefaultValueEditorActionProxy.getActionDefinitionId() ).setHandler( null );
        }
    }


    /**
     * Gets the open default editor action.
     * 
     * @return the open default editor action
     */
    public OpenDefaultEditorAction getOpenDefaultEditorAction()
    {
        return ( OpenDefaultEditorAction ) openDefaultValueEditorActionProxy.getAction();
    }

}
