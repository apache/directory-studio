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

package org.apache.directory.ldapstudio.browser.ui.widgets.browser;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.ui.actions.CollapseAllAction;
import org.apache.directory.ldapstudio.browser.ui.actions.FilterSubtreeAction;
import org.apache.directory.ldapstudio.browser.ui.actions.PropertiesAction;
import org.apache.directory.ldapstudio.browser.ui.actions.RefreshAction;
import org.apache.directory.ldapstudio.browser.ui.actions.UnfilterSubtreeAction;
import org.apache.directory.ldapstudio.browser.ui.actions.UpAction;
import org.apache.directory.ldapstudio.browser.ui.actions.proxy.BrowserViewActionProxy;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.commands.ICommandService;


public class BrowserActionGroup implements IMenuListener
{

    protected OpenSortDialogAction openSortDialogAction;

    protected CollapseAllAction collapseAllAction;

    protected static final String upAction = "upAction";

    protected static final String refreshAction = "refreshAction";

    protected static final String filterSubtreeAction = "filterSubtreeAction";

    protected static final String unfilterSubtreeAction = "unfilterSubtreeAction";

    protected static final String propertyDialogAction = "propertyDialogAction";

    protected Map browserActionMap;

    protected IActionBars actionBars;

    protected BrowserWidget mainWidget;


    public BrowserActionGroup( BrowserWidget mainWidget, BrowserConfiguration configuration )
    {
        this.mainWidget = mainWidget;
        this.browserActionMap = new HashMap();
        TreeViewer viewer = mainWidget.getViewer();

        this.openSortDialogAction = new OpenSortDialogAction( ( BrowserPreferences ) configuration.getPreferences() );
        this.collapseAllAction = new CollapseAllAction( viewer );

        this.browserActionMap.put( upAction, new BrowserViewActionProxy( viewer, new UpAction( viewer ) ) );
        this.browserActionMap.put( refreshAction, new BrowserViewActionProxy( viewer, new RefreshAction() ) );
        this.browserActionMap
            .put( filterSubtreeAction, new BrowserViewActionProxy( viewer, new FilterSubtreeAction() ) );
        this.browserActionMap.put( unfilterSubtreeAction, new BrowserViewActionProxy( viewer,
            new UnfilterSubtreeAction() ) );
        this.browserActionMap.put( propertyDialogAction, new BrowserViewActionProxy( viewer, new PropertiesAction() ) );
    }


    public void dispose()
    {
        if ( this.mainWidget != null )
        {

            this.openSortDialogAction.dispose();
            this.openSortDialogAction = null;
            this.collapseAllAction.dispose();
            this.collapseAllAction = null;

            for ( Iterator it = this.browserActionMap.keySet().iterator(); it.hasNext(); )
            {
                String key = ( String ) it.next();
                BrowserViewActionProxy action = ( BrowserViewActionProxy ) this.browserActionMap.get( key );
                action.dispose();
                action = null;
                it.remove();
            }
            this.browserActionMap.clear();
            this.browserActionMap = null;

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

        toolBarManager.add( ( IAction ) this.browserActionMap.get( upAction ) );
        toolBarManager.add( new Separator() );
        toolBarManager.add( this.collapseAllAction );
        toolBarManager.add( ( IAction ) this.browserActionMap.get( refreshAction ) );
        toolBarManager.update( true );

    }


    public void fillMenu( IMenuManager menuManager )
    {
        menuManager.add( this.openSortDialogAction );
        menuManager.add( new Separator() );
        menuManager.update( true );
    }


    public void fillContextMenu( IMenuManager menuManager )
    {
        menuManager.setRemoveAllWhenShown( true );
        menuManager.addMenuListener( this );
    }


    public void menuAboutToShow( IMenuManager menuManager )
    {

        // up
        menuManager.add( ( IAction ) this.browserActionMap.get( upAction ) );
        menuManager.add( new Separator() );

        // filter
        menuManager.add( ( IAction ) this.browserActionMap.get( filterSubtreeAction ) );
        if ( ( ( IAction ) this.browserActionMap.get( unfilterSubtreeAction ) ).isEnabled() )
        {
            menuManager.add( ( IAction ) this.browserActionMap.get( unfilterSubtreeAction ) );
        }
        menuManager.add( new Separator() );

        // refresh
        menuManager.add( ( IAction ) this.browserActionMap.get( refreshAction ) );
        menuManager.add( new Separator() );

        // properties
        menuManager.add( ( IAction ) this.browserActionMap.get( propertyDialogAction ) );

    }


    public void activateGlobalActionHandlers()
    {

        ICommandService commandService = ( ICommandService ) PlatformUI.getWorkbench().getAdapter(
            ICommandService.class );

        if ( this.actionBars != null )
        {
            actionBars.setGlobalActionHandler( ActionFactory.REFRESH.getId(), ( IAction ) this.browserActionMap
                .get( refreshAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.PROPERTIES.getId(), ( IAction ) this.browserActionMap
                .get( propertyDialogAction ) );
            actionBars.updateActionBars();
        }
        else
        {
            if ( commandService != null )
            {
                IAction pda = ( IAction ) this.browserActionMap.get( propertyDialogAction );
                pda.setActionDefinitionId( "org.apache.directory.ldapstudio.browser.action.properties" );
                commandService.getCommand( pda.getActionDefinitionId() ).setHandler( new ActionHandler( pda ) );

                IAction ra = ( IAction ) this.browserActionMap.get( refreshAction );
                commandService.getCommand( ra.getActionDefinitionId() ).setHandler( new ActionHandler( ra ) );
            }
        }

        if ( commandService != null )
        {
            IAction ua = ( IAction ) this.browserActionMap.get( upAction );
            commandService.getCommand( ua.getActionDefinitionId() ).setHandler( new ActionHandler( ua ) );
        }

    }


    public void deactivateGlobalActionHandlers()
    {

        ICommandService commandService = ( ICommandService ) PlatformUI.getWorkbench().getAdapter(
            ICommandService.class );

        if ( this.actionBars != null )
        {
            actionBars.setGlobalActionHandler( ActionFactory.REFRESH.getId(), null );
            actionBars.setGlobalActionHandler( ActionFactory.PROPERTIES.getId(), null );
            actionBars.updateActionBars();
        }
        else
        {
            if ( commandService != null )
            {
                IAction pda = ( IAction ) this.browserActionMap.get( propertyDialogAction );
                commandService.getCommand( pda.getActionDefinitionId() ).setHandler( null );

                IAction ra = ( IAction ) this.browserActionMap.get( refreshAction );
                commandService.getCommand( ra.getActionDefinitionId() ).setHandler( null );
            }
        }

        if ( commandService != null )
        {
            IAction ua = ( IAction ) this.browserActionMap.get( upAction );
            commandService.getCommand( ua.getActionDefinitionId() ).setHandler( null );
        }

    }


    public IAction getRefreshAction()
    {
        return ( IAction ) this.browserActionMap.get( refreshAction );
    }


    public void setInput( IConnection connection )
    {
        for ( Iterator it = this.browserActionMap.values().iterator(); it.hasNext(); )
        {
            BrowserViewActionProxy action = ( BrowserViewActionProxy ) it.next();
            action.inputChanged( connection );
        }
    }

}
