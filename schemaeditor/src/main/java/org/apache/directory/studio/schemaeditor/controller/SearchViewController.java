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
package org.apache.directory.studio.schemaeditor.controller;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.controller.actions.OpenSearchViewPreferenceAction;
import org.apache.directory.studio.schemaeditor.controller.actions.OpenSearchViewSortingDialogAction;
import org.apache.directory.studio.schemaeditor.controller.actions.RunCurrentSearchAgainAction;
import org.apache.directory.studio.schemaeditor.controller.actions.ShowSearchFieldAction;
import org.apache.directory.studio.schemaeditor.controller.actions.ShowSearchHistoryAction;
import org.apache.directory.studio.schemaeditor.view.views.SearchView;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;


/**
 * This class implements the Controller for the SearchView.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SearchViewController
{
    /** The associated view */
    private SearchView view;

    /** The authorized Preferences keys*/
    private List<String> authorizedPrefs;

    // The Actions
    private ShowSearchFieldAction showSearchField;
    private RunCurrentSearchAgainAction runCurrentSearchAgain;
    private ShowSearchHistoryAction searchHistory;
    private OpenSearchViewSortingDialogAction openSearchViewSortingDialog;
    private OpenSearchViewPreferenceAction openSearchViewPreference;


    /**
     * Creates a new instance of SearchViewController.
     *
     * @param view
     *      the associated view
     */
    public SearchViewController( SearchView view )
    {
        this.view = view;

        initActions();
        initToolbar();
        initMenu();
        initAuthorizedPrefs();
        initPreferencesListener();
    }


    /**
     * Initializes the Actions.
     */
    private void initActions()
    {
        showSearchField = new ShowSearchFieldAction( view );
        runCurrentSearchAgain = new RunCurrentSearchAgainAction( view );
        searchHistory = new ShowSearchHistoryAction( view );
        openSearchViewSortingDialog = new OpenSearchViewSortingDialogAction();
        openSearchViewPreference = new OpenSearchViewPreferenceAction();
    }


    /**
     * Initializes the Toolbar.
     */
    private void initToolbar()
    {
        IToolBarManager toolbar = view.getViewSite().getActionBars().getToolBarManager();
        toolbar.add( showSearchField );
        toolbar.add( new Separator() );
        toolbar.add( runCurrentSearchAgain );
        toolbar.add( searchHistory );
    }


    /**
     * Initializes the Menu.
     */
    private void initMenu()
    {
        IMenuManager menu = view.getViewSite().getActionBars().getMenuManager();
        menu.add( openSearchViewSortingDialog );
        menu.add( new Separator() );
        menu.add( openSearchViewPreference );
    }


    /**
     * Initializes the Authorized Prefs IDs.
     */
    private void initAuthorizedPrefs()
    {
        authorizedPrefs = new ArrayList<String>();
        authorizedPrefs.add( PluginConstants.PREFS_SEARCH_VIEW_LABEL );
        authorizedPrefs.add( PluginConstants.PREFS_SEARCH_VIEW_ABBREVIATE );
        authorizedPrefs.add( PluginConstants.PREFS_SEARCH_VIEW_ABBREVIATE_MAX_LENGTH );
        authorizedPrefs.add( PluginConstants.PREFS_SEARCH_VIEW_SECONDARY_LABEL_DISPLAY );
        authorizedPrefs.add( PluginConstants.PREFS_SEARCH_VIEW_SECONDARY_LABEL );
        authorizedPrefs.add( PluginConstants.PREFS_SEARCH_VIEW_SECONDARY_LABEL_ABBREVIATE );
        authorizedPrefs.add( PluginConstants.PREFS_SEARCH_VIEW_SECONDARY_LABEL_ABBREVIATE_MAX_LENGTH );
        authorizedPrefs.add( PluginConstants.PREFS_SEARCH_VIEW_SCHEMA_LABEL_DISPLAY );
        authorizedPrefs.add( PluginConstants.PREFS_SEARCH_VIEW_GROUPING );
        authorizedPrefs.add( PluginConstants.PREFS_SEARCH_VIEW_SORTING_BY );
        authorizedPrefs.add( PluginConstants.PREFS_SEARCH_VIEW_SORTING_ORDER );
    }


    /**
     * Initializes the listener on the preferences store
     */
    private void initPreferencesListener()
    {
        Activator.getDefault().getPreferenceStore().addPropertyChangeListener( new IPropertyChangeListener()
        {
            /* (non-Javadoc)
             * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
             */
            public void propertyChange( PropertyChangeEvent event )
            {
                if ( authorizedPrefs.contains( event.getProperty() ) )
                {
                    view.refresh();
                }
            }
        } );
    }
}
