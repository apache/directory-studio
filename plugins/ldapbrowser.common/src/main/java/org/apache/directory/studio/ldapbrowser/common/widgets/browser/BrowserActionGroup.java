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

package org.apache.directory.studio.ldapbrowser.common.widgets.browser;


import java.util.HashMap;
import java.util.Map;

import org.apache.directory.studio.connection.ui.actions.CollapseAllAction;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.common.actions.FilterChildrenAction;
import org.apache.directory.studio.ldapbrowser.common.actions.OpenQuickSearchAction;
import org.apache.directory.studio.ldapbrowser.common.actions.PropertiesAction;
import org.apache.directory.studio.ldapbrowser.common.actions.RefreshAction;
import org.apache.directory.studio.ldapbrowser.common.actions.UnfilterChildrenAction;
import org.apache.directory.studio.ldapbrowser.common.actions.UpAction;
import org.apache.directory.studio.ldapbrowser.common.actions.proxy.ActionHandlerManager;
import org.apache.directory.studio.ldapbrowser.common.actions.proxy.BrowserViewActionProxy;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.utils.ActionUtils;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionFactory;


/**
 * This class manages all the actions of the browser widget.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class BrowserActionGroup implements ActionHandlerManager, IMenuListener
{

    /** The open sort dialog action. */
    protected OpenSortDialogAction openSortDialogAction;

    /** The show quick search action. */
    protected ShowQuickSearchAction showQuickSearchAction;

    /** The collapse all action. */
    protected CollapseAllAction collapseAllAction;

    /** The Constant upAction. */
    protected static final String upAction = "upAction"; //$NON-NLS-1$

    /** The Constant refreshAction. */
    protected static final String refreshAction = "refreshAction"; //$NON-NLS-1$

    /** The Constant filterChildrenAction. */
    protected static final String filterChildrenAction = "filterChildrenAction"; //$NON-NLS-1$

    /** The Constant openQuickSearchAction. */
    protected static final String openQuickSearchAction = "openQuickSearch"; //$NON-NLS-1$

    /** The Constant unfilterChildrenAction. */
    protected static final String unfilterChildrenAction = "unfilterChildrenAction"; //$NON-NLS-1$

    /** The Constant propertyDialogAction. */
    protected static final String propertyDialogAction = "propertyDialogAction"; //$NON-NLS-1$

    /** The browser action map. */
    protected Map<String, BrowserViewActionProxy> browserActionMap;

    /** The action bars. */
    protected IActionBars actionBars;

    /** The browser's main widget. */
    protected BrowserWidget mainWidget;


    /**
     * Creates a new instance of BrowserActionGroup.
     *
     * @param mainWidget the browser's main widget
     * @param configuration the  browser's configuration
     */
    public BrowserActionGroup( BrowserWidget mainWidget, BrowserConfiguration configuration )
    {
        this.mainWidget = mainWidget;
        this.browserActionMap = new HashMap<String, BrowserViewActionProxy>();

        TreeViewer viewer = mainWidget.getViewer();
        openSortDialogAction = new OpenSortDialogAction( configuration.getPreferences() );
        showQuickSearchAction = new ShowQuickSearchAction( mainWidget.getQuickSearchWidget() );
        collapseAllAction = new CollapseAllAction( viewer );

        browserActionMap.put( openQuickSearchAction, new BrowserViewActionProxy( viewer, new OpenQuickSearchAction(
            mainWidget ) ) );
        browserActionMap.put( upAction, new BrowserViewActionProxy( viewer, new UpAction( viewer ) ) );
        browserActionMap.put( refreshAction, new BrowserViewActionProxy( viewer, new RefreshAction() ) );
        browserActionMap.put( filterChildrenAction, new BrowserViewActionProxy( viewer, new FilterChildrenAction() ) );
        browserActionMap
            .put( unfilterChildrenAction, new BrowserViewActionProxy( viewer, new UnfilterChildrenAction() ) );
        browserActionMap.put( propertyDialogAction, new BrowserViewActionProxy( viewer, new PropertiesAction() ) );
    }


    /**
     * Disposes this action group.
     */
    public void dispose()
    {
        if ( mainWidget != null )
        {
            openSortDialogAction.dispose();
            openSortDialogAction = null;
            showQuickSearchAction.dispose();
            showQuickSearchAction = null;
            collapseAllAction.dispose();
            collapseAllAction = null;

            for ( BrowserViewActionProxy action : browserActionMap.values() )
            {
                action.dispose();
            }
            browserActionMap.clear();
            browserActionMap = null;

            actionBars = null;
            mainWidget = null;
        }
    }


    /**
     * Enables the action handlers.
     *
     * @param actionBars the action bars
     */
    public void enableGlobalActionHandlers( IActionBars actionBars )
    {
        this.actionBars = actionBars;
    }


    /**
     * Fills the tool bar.
     *
     * @param toolBarManager the tool bar manager
     */
    public void fillToolBar( IToolBarManager toolBarManager )
    {
        toolBarManager.add( browserActionMap.get( upAction ) );
        toolBarManager.add( new Separator() );
        toolBarManager.add( browserActionMap.get( refreshAction ) );
        toolBarManager.add( new Separator() );
        toolBarManager.add( collapseAllAction );
        toolBarManager.update( true );
    }


    /**
     * Fills the local menu.
     *
     * @param menuManager the local menu manager
     */
    public void fillMenu( IMenuManager menuManager )
    {
        menuManager.add( openSortDialogAction );
        menuManager.add( new Separator() );
        menuManager.add( showQuickSearchAction );
        menuManager.add( new Separator() );
        menuManager.update( true );
    }


    /**
     * Fills the context menu.
     *
     * @param menuManager the context menu manager
     */
    public void fillContextMenu( IMenuManager menuManager )
    {
        menuManager.setRemoveAllWhenShown( true );
        menuManager.addMenuListener( this );
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation fills the context menu.
     */
    public void menuAboutToShow( IMenuManager menuManager )
    {
        // up
        menuManager.add( browserActionMap.get( upAction ) );
        menuManager.add( new Separator() );

        // filter
        menuManager.add( browserActionMap.get( filterChildrenAction ) );
        if ( ( browserActionMap.get( unfilterChildrenAction ) ).isEnabled() )
        {
            menuManager.add( browserActionMap.get( unfilterChildrenAction ) );
        }
        menuManager.add( browserActionMap.get( openQuickSearchAction ) );
        menuManager.add( new Separator() );

        // refresh
        menuManager.add( browserActionMap.get( refreshAction ) );
        menuManager.add( new Separator() );

        // additions
        menuManager.add( new Separator( IWorkbenchActionConstants.MB_ADDITIONS ) );
        menuManager.add( new Separator() );

        // properties
        menuManager.add( browserActionMap.get( propertyDialogAction ) );
    }


    /**
     * Activates the action handlers.
     */
    public void activateGlobalActionHandlers()
    {
        if ( actionBars != null )
        {
            actionBars.setGlobalActionHandler( ActionFactory.REFRESH.getId(), ( IAction ) browserActionMap
                .get( refreshAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.PROPERTIES.getId(), ( IAction ) browserActionMap
                .get( propertyDialogAction ) );
            actionBars.setGlobalActionHandler( ActionFactory.FIND.getId(), showQuickSearchAction ); // IWorkbenchActionDefinitionIds.FIND_REPLACE
            actionBars.updateActionBars();
        }
        else
        {
            IAction pda = browserActionMap.get( propertyDialogAction );
            pda.setActionDefinitionId( BrowserCommonConstants.CMD_PROPERTIES );
            ActionUtils.activateActionHandler( pda );

            IAction ra = browserActionMap.get( refreshAction );
            ActionUtils.activateActionHandler( ra );

            showQuickSearchAction.setActionDefinitionId( BrowserCommonConstants.CMD_FIND );
            ActionUtils.activateActionHandler( showQuickSearchAction );
        }

        IAction ua = browserActionMap.get( upAction );
        ActionUtils.activateActionHandler( ua );
    }


    /**
     * Deactivates the action handlers.
     */
    public void deactivateGlobalActionHandlers()
    {
        if ( actionBars != null )
        {
            actionBars.setGlobalActionHandler( ActionFactory.REFRESH.getId(), null );
            actionBars.setGlobalActionHandler( ActionFactory.PROPERTIES.getId(), null );
            actionBars.updateActionBars();
        }
        else
        {
            IAction ra = browserActionMap.get( refreshAction );
            ActionUtils.deactivateActionHandler( ra );

            IAction pda = browserActionMap.get( propertyDialogAction );
            ActionUtils.deactivateActionHandler( pda );
        }

        IAction ua = browserActionMap.get( upAction );
        ActionUtils.deactivateActionHandler( ua );
    }


    /**
     * Sets the connection input to all actions
     *
     * @param connection the connection
     */
    public void setInput( IBrowserConnection connection )
    {
        for ( BrowserViewActionProxy action : browserActionMap.values() )
        {
            action.inputChanged( connection );
        }
    }

}
