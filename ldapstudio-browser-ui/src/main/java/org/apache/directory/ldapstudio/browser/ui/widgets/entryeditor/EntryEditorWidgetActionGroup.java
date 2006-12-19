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

package org.apache.directory.ldapstudio.browser.ui.widgets.entryeditor;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.actions.CopyAction;
import org.apache.directory.ldapstudio.browser.ui.actions.DeleteAction;
import org.apache.directory.ldapstudio.browser.ui.actions.NewValueAction;
import org.apache.directory.ldapstudio.browser.ui.actions.PasteAction;
import org.apache.directory.ldapstudio.browser.ui.actions.PropertiesAction;
import org.apache.directory.ldapstudio.browser.ui.actions.SelectAllAction;
import org.apache.directory.ldapstudio.browser.ui.actions.ShowRawValuesAction;
import org.apache.directory.ldapstudio.browser.ui.actions.ValueEditorPreferencesAction;
import org.apache.directory.ldapstudio.browser.ui.actions.proxy.BrowserActionProxy;
import org.apache.directory.ldapstudio.browser.ui.actions.proxy.EntryEditorActionProxy;
import org.apache.directory.ldapstudio.browser.ui.editors.entry.ShowQuickFilterAction;
import org.apache.directory.ldapstudio.browser.ui.valueproviders.ValueProvider;
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


public class EntryEditorWidgetActionGroup implements IMenuListener
{

    protected OpenSortDialogAction openSortDialogAction;

    protected ShowRawValuesAction showRawValuesAction;

    protected ShowQuickFilterAction showQuickFilterAction;

    protected OpenDefaultEditorAction openDefaultEditorAction;

    protected OpenBestEditorAction openBestEditorAction;

    protected OpenEditorAction[] openEditorActions;

    protected ValueEditorPreferencesAction openValueEditorPreferencesAction;

    protected final static String newValueAction = "newValueAction";

    protected final static String copyAction = "copyAction";

    protected final static String pasteAction = "pasteAction";

    protected final static String deleteAction = "deleteAction";

    protected final static String selectAllAction = "selectAllAction";

    protected final static String propertyDialogAction = "propertyDialogAction";

    protected Map entryEditorActionMap;

    protected IActionBars actionBars;

    private EntryEditorWidget mainWidget;


    public EntryEditorWidgetActionGroup( EntryEditorWidget mainWidget, EntryEditorWidgetConfiguration configuration )
    {

        this.mainWidget = mainWidget;
        this.entryEditorActionMap = new HashMap();
        TreeViewer viewer = mainWidget.getViewer();

        this.openSortDialogAction = new OpenSortDialogAction( configuration.getPreferences() );
        this.showRawValuesAction = new ShowRawValuesAction();
        this.showQuickFilterAction = new ShowQuickFilterAction( mainWidget.getQuickFilterWidget() );

        this.openBestEditorAction = new OpenBestEditorAction( viewer, this, configuration
            .getValueProviderManager( viewer ) );
        this.openDefaultEditorAction = new OpenDefaultEditorAction( viewer, this.openBestEditorAction );
        ValueProvider[] valueProviders = configuration.getValueProviderManager( viewer ).getAllValueProviders();
        this.openEditorActions = new OpenEditorAction[valueProviders.length];
        for ( int i = 0; i < this.openEditorActions.length; i++ )
        {
            this.openEditorActions[i] = new OpenEditorAction( viewer, this, configuration
                .getValueProviderManager( viewer ), valueProviders[i] );
        }
        this.openValueEditorPreferencesAction = new ValueEditorPreferencesAction();

        this.entryEditorActionMap.put( newValueAction, new EntryEditorActionProxy( viewer, new NewValueAction() ) );

        this.entryEditorActionMap.put( pasteAction, new EntryEditorActionProxy( viewer, new PasteAction() ) );
        this.entryEditorActionMap.put( copyAction, new EntryEditorActionProxy( viewer, new CopyAction(
            ( BrowserActionProxy ) this.entryEditorActionMap.get( pasteAction ) ) ) );
        this.entryEditorActionMap.put( deleteAction, new EntryEditorActionProxy( viewer, new DeleteAction() ) );
        this.entryEditorActionMap.put( selectAllAction, new EntryEditorActionProxy( viewer,
            new SelectAllAction( viewer ) ) );

        this.entryEditorActionMap.put( propertyDialogAction,
            new EntryEditorActionProxy( viewer, new PropertiesAction() ) );

    }


    public void dispose()
    {
        if ( this.mainWidget != null )
        {

            this.openSortDialogAction = null;
            this.showQuickFilterAction.dispose();
            this.showQuickFilterAction = null;
            this.showRawValuesAction.dispose();
            this.showRawValuesAction = null;

            this.openDefaultEditorAction.dispose();
            this.openDefaultEditorAction = null;
            this.openBestEditorAction.dispose();
            this.openBestEditorAction = null;
            for ( int i = 0; i < this.openEditorActions.length; i++ )
            {
                this.openEditorActions[i].dispose();
                this.openEditorActions[i] = null;
            }
            this.openValueEditorPreferencesAction.dispose();
            this.openValueEditorPreferencesAction = null;

            for ( Iterator it = this.entryEditorActionMap.keySet().iterator(); it.hasNext(); )
            {
                String key = ( String ) it.next();
                EntryEditorActionProxy action = ( EntryEditorActionProxy ) this.entryEditorActionMap.get( key );
                action.dispose();
                action = null;
                it.remove();
            }
            this.entryEditorActionMap.clear();
            this.entryEditorActionMap = null;

            this.actionBars = null;
            this.mainWidget = null;
        }

    }


    public void enableGlobalActionHandlers( IActionBars actionBars )
    {
        this.actionBars = actionBars;
        this.activateGlobalActionHandlers();
    }


    public void fillToolBar( IToolBarManager toolBarManager )
    {

        toolBarManager.add( ( IAction ) this.entryEditorActionMap.get( newValueAction ) );
        toolBarManager.add( new Separator() );
        toolBarManager.add( ( IAction ) this.entryEditorActionMap.get( deleteAction ) );
        toolBarManager.add( new Separator() );
        toolBarManager.add( this.showQuickFilterAction );
        toolBarManager.update( true );

    }


    public void fillMenu( IMenuManager menuManager )
    {
        menuManager.add( this.openSortDialogAction );
        menuManager.add( this.showRawValuesAction );
        menuManager.addMenuListener( new IMenuListener()
        {
            public void menuAboutToShow( IMenuManager manager )
            {
                showRawValuesAction.setChecked( BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
                    BrowserUIConstants.PREFERENCE_SHOW_RAW_VALUES ) );
            }
        } );
    }


    public void fillContextMenu( IMenuManager menuManager )
    {
        menuManager.setRemoveAllWhenShown( true );
        menuManager.addMenuListener( this );
    }


    public void menuAboutToShow( IMenuManager menuManager )
    {

        // new
        menuManager.add( ( IAction ) this.entryEditorActionMap.get( newValueAction ) );
        menuManager.add( new Separator() );

        // copy, paste, delete
        menuManager.add( ( IAction ) this.entryEditorActionMap.get( copyAction ) );
        menuManager.add( ( IAction ) this.entryEditorActionMap.get( pasteAction ) );
        menuManager.add( ( IAction ) this.entryEditorActionMap.get( deleteAction ) );
        menuManager.add( ( IAction ) this.entryEditorActionMap.get( selectAllAction ) );
        menuManager.add( new Separator() );

        // edit
        addEditMenu( menuManager );
        menuManager.add( new Separator() );

        // properties
        menuManager.add( ( IAction ) this.entryEditorActionMap.get( propertyDialogAction ) );
    }


    protected void addEditMenu( IMenuManager menuManager )
    {
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
                && this.openEditorActions[i].getValueProvider().getClass() != this.openBestEditorAction
                    .getBestValueProvider().getClass() )
            {
                editorMenuManager.add( this.openEditorActions[i] );
            }
        }
        editorMenuManager.add( new Separator() );
        editorMenuManager.add( this.openValueEditorPreferencesAction );
        menuManager.add( editorMenuManager );
    }


    public void activateGlobalActionHandlers()
    {

        ICommandService commandService = ( ICommandService ) PlatformUI.getWorkbench().getAdapter(
            ICommandService.class );

        if ( this.actionBars != null )
        {
            actionBars.setGlobalActionHandler( ActionFactory.COPY.getId(), ( IAction ) this.entryEditorActionMap
                .get( copyAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.PASTE.getId(), ( IAction ) this.entryEditorActionMap
                .get( pasteAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.DELETE.getId(), ( IAction ) this.entryEditorActionMap
                .get( deleteAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.SELECT_ALL.getId(), ( IAction ) this.entryEditorActionMap
                .get( selectAllAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.PROPERTIES.getId(), ( IAction ) this.entryEditorActionMap
                .get( propertyDialogAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.FIND.getId(), this.showQuickFilterAction ); // IWorkbenchActionDefinitionIds.FIND_REPLACE

            actionBars.updateActionBars();
        }
        else
        {
            if ( commandService != null )
            {
                IAction da = ( IAction ) this.entryEditorActionMap.get( deleteAction );
                da.setActionDefinitionId( "org.apache.directory.ldapstudio.browser.action.delete" );
                commandService.getCommand( da.getActionDefinitionId() ).setHandler( new ActionHandler( da ) );

                IAction ca = ( IAction ) this.entryEditorActionMap.get( copyAction );
                ca.setActionDefinitionId( "org.apache.directory.ldapstudio.browser.action.copy" );
                commandService.getCommand( ca.getActionDefinitionId() ).setHandler( new ActionHandler( ca ) );

                IAction pa = ( IAction ) this.entryEditorActionMap.get( pasteAction );
                pa.setActionDefinitionId( "org.apache.directory.ldapstudio.browser.action.paste" );
                commandService.getCommand( pa.getActionDefinitionId() ).setHandler( new ActionHandler( pa ) );

                showQuickFilterAction.setActionDefinitionId( "org.apache.directory.ldapstudio.browser.action.find" );
                commandService.getCommand( showQuickFilterAction.getActionDefinitionId() ).setHandler(
                    new ActionHandler( showQuickFilterAction ) );

                IAction pda = ( IAction ) this.entryEditorActionMap.get( propertyDialogAction );
                pda.setActionDefinitionId( "org.apache.directory.ldapstudio.browser.action.properties" );
                commandService.getCommand( pda.getActionDefinitionId() ).setHandler( new ActionHandler( pda ) );
            }
        }

        if ( commandService != null )
        {
            IAction nva = ( IAction ) this.entryEditorActionMap.get( newValueAction );
            commandService.getCommand( nva.getActionDefinitionId() ).setHandler( new ActionHandler( nva ) );
            commandService.getCommand( openDefaultEditorAction.getActionDefinitionId() ).setHandler(
                new ActionHandler( openDefaultEditorAction ) );
        }

    }


    public void deactivateGlobalActionHandlers()
    {

        ICommandService commandService = ( ICommandService ) PlatformUI.getWorkbench().getAdapter(
            ICommandService.class );

        if ( this.actionBars != null )
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
                IAction da = ( IAction ) this.entryEditorActionMap.get( deleteAction );
                commandService.getCommand( da.getActionDefinitionId() ).setHandler( null );

                IAction ca = ( IAction ) this.entryEditorActionMap.get( copyAction );
                commandService.getCommand( ca.getActionDefinitionId() ).setHandler( null );

                IAction pa = ( IAction ) this.entryEditorActionMap.get( pasteAction );
                commandService.getCommand( pa.getActionDefinitionId() ).setHandler( null );

                commandService.getCommand( showQuickFilterAction.getActionDefinitionId() ).setHandler( null );

                IAction pda = ( IAction ) this.entryEditorActionMap.get( propertyDialogAction );
                commandService.getCommand( pda.getActionDefinitionId() ).setHandler( null );
            }
        }

        if ( commandService != null )
        {
            IAction nva = ( IAction ) this.entryEditorActionMap.get( newValueAction );
            commandService.getCommand( nva.getActionDefinitionId() ).setHandler( null );
            commandService.getCommand( openDefaultEditorAction.getActionDefinitionId() ).setHandler( null );
        }

    }


    public OpenBestEditorAction getOpenBestEditorAction()
    {
        return openBestEditorAction;
    }


    public OpenDefaultEditorAction getOpenDefaultEditorAction()
    {
        return openDefaultEditorAction;
    }

}
