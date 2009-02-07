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

package org.apache.directory.studio.ldapbrowser.ui.views.searchlogs;


import java.util.HashMap;
import java.util.Map;

import org.apache.directory.studio.connection.core.ConnectionCoreConstants;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.ldapbrowser.common.actions.proxy.ActionHandlerManager;
import org.apache.directory.studio.ldapbrowser.ui.actions.proxy.SearchLogsViewActionProxy;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.ui.IActionBars;


/**
 * The SearchLogsViewActionGroup manages all the actions of the search logs view.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SearchLogsViewActionGroup implements ActionHandlerManager, IMenuListener
{

    /** The view. */
    private SearchLogsView view;

    /** The Constant olderAction. */
    private static final String olderAction = "olderAction"; //$NON-NLS-1$

    /** The Constant newerAction. */
    private static final String newerAction = "newerAction"; //$NON-NLS-1$

    /** The Constant refreshAction. */
    private static final String refreshAction = "refreshAction"; //$NON-NLS-1$

    /** The Constant refreshAction. */
    private static final String clearAction = "clearAction"; //$NON-NLS-1$

    /** The Constant exportAction. */
    private static final String exportAction = "exportAction"; //$NON-NLS-1$

    /** The enable search request logs action. */
    private EnableSearchRequestLogsAction enableSearchRequestLogsAction;

    /** The enable search result entry logs action. */
    private EnableSearchResultEntryLogsAction enableSearchResultEntryLogsAction;

    /** The open search logs preference page action. */
    private OpenSearchLogsPreferencePageAction openSearchLogsPreferencePageAction;

    /** The search logs view action map. */
    private Map<String, SearchLogsViewActionProxy> searchLogsViewActionMap;


    /**
     * Creates a new instance of SearchLogsViewActionGroup.
     *
     * @param view the search logs view
     */
    public SearchLogsViewActionGroup( SearchLogsView view )
    {
        this.view = view;
        SourceViewer viewer = this.view.getMainWidget().getSourceViewer();

        searchLogsViewActionMap = new HashMap<String, SearchLogsViewActionProxy>();
        searchLogsViewActionMap.put( olderAction, new SearchLogsViewActionProxy( viewer, new OlderAction( view ) ) );
        searchLogsViewActionMap.put( newerAction, new SearchLogsViewActionProxy( viewer, new NewerAction( view ) ) );
        searchLogsViewActionMap.put( refreshAction, new SearchLogsViewActionProxy( viewer, new RefreshAction( view ) ) );
        searchLogsViewActionMap.put( clearAction, new SearchLogsViewActionProxy( viewer, new ClearAction( view ) ) );
        searchLogsViewActionMap.put( exportAction, new SearchLogsViewActionProxy( viewer, new ExportAction() ) );
        enableSearchRequestLogsAction = new EnableSearchRequestLogsAction();
        enableSearchResultEntryLogsAction = new EnableSearchResultEntryLogsAction();
        openSearchLogsPreferencePageAction = new OpenSearchLogsPreferencePageAction();
    }


    /**
     * Disposes this action group.
     */
    public void dispose()
    {
        if ( view != null )
        {
            for ( SearchLogsViewActionProxy action : searchLogsViewActionMap.values() )
            {
                action.dispose();
                action = null;
            }
            searchLogsViewActionMap.clear();
            searchLogsViewActionMap = null;

            enableSearchRequestLogsAction = null;
            enableSearchResultEntryLogsAction = null;
            openSearchLogsPreferencePageAction = null;

            view = null;
        }
    }


    /**
     * Fill the action bars.
     * 
     * @param actionBars the action bars
     */
    public void fillActionBars( IActionBars actionBars )
    {
        // Tool Bar
        actionBars.getToolBarManager().add( searchLogsViewActionMap.get( clearAction ) );
        actionBars.getToolBarManager().add( searchLogsViewActionMap.get( refreshAction ) );
        actionBars.getToolBarManager().add( new Separator() );
        actionBars.getToolBarManager().add( searchLogsViewActionMap.get( olderAction ) );
        actionBars.getToolBarManager().add( searchLogsViewActionMap.get( newerAction ) );
        actionBars.getToolBarManager().add( new Separator() );
        actionBars.getToolBarManager().add( searchLogsViewActionMap.get( exportAction ) );

        // Menu Bar
        actionBars.getMenuManager().add( enableSearchRequestLogsAction );
        actionBars.getMenuManager().add( enableSearchResultEntryLogsAction );
        actionBars.getMenuManager().add( new Separator() );
        actionBars.getMenuManager().add( openSearchLogsPreferencePageAction );
        actionBars.getMenuManager().addMenuListener( new IMenuListener()
        {
            public void menuAboutToShow( IMenuManager manager )
            {
                enableSearchRequestLogsAction.setChecked( ConnectionCorePlugin.getDefault().getPluginPreferences()
                    .getBoolean( ConnectionCoreConstants.PREFERENCE_SEARCHREQUESTLOGS_ENABLE ) );
                enableSearchResultEntryLogsAction.setChecked( ConnectionCorePlugin.getDefault().getPluginPreferences()
                    .getBoolean( ConnectionCoreConstants.PREFERENCE_SEARCHRESULTENTRYLOGS_ENABLE ) );
            }
        } );
    }


    /**
     * {@inheritDoc}
     */
    public void menuAboutToShow( IMenuManager menuManager )
    {
    }


    /**
     * Propagates the input to all actions.
     * 
     * @param input the input
     */
    public void setInput( SearchLogsViewInput input )
    {
        for ( SearchLogsViewActionProxy action : searchLogsViewActionMap.values() )
        {
            action.inputChanged( input );
        }
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.common.actions.proxy.ActionHandlerManager#activateGlobalActionHandlers()
     */
    public void activateGlobalActionHandlers()
    {
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.common.actions.proxy.ActionHandlerManager#deactivateGlobalActionHandlers()
     */
    public void deactivateGlobalActionHandlers()
    {
    }

}
