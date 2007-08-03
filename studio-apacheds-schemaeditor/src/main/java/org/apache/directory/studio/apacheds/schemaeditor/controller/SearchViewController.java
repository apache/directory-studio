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
package org.apache.directory.studio.apacheds.schemaeditor.controller;


import org.apache.directory.studio.apacheds.schemaeditor.controller.actions.RunCurrentSearchAgainAction;
import org.apache.directory.studio.apacheds.schemaeditor.controller.actions.ShowSearchFieldAction;
import org.apache.directory.studio.apacheds.schemaeditor.controller.actions.ShowSearchHistoryAction;
import org.apache.directory.studio.apacheds.schemaeditor.view.views.SearchView;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;


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

    // The Actions
    private ShowSearchFieldAction showSearchField;
    private RunCurrentSearchAgainAction runCurrentSearchAgain;
    private ShowSearchHistoryAction searchHistory;


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
    }


    /**
     * Initializes the Actions.
     */
    private void initActions()
    {
        showSearchField = new ShowSearchFieldAction( view );
        runCurrentSearchAgain = new RunCurrentSearchAgainAction( view );
        searchHistory = new ShowSearchHistoryAction( view );
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
}
