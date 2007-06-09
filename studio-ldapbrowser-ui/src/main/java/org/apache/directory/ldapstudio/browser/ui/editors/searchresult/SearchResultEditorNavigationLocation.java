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


import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.model.IConnection;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.INavigationLocation;
import org.eclipse.ui.NavigationLocation;


/**
 * This class is used to mark the search result editor input to the navigation history.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SearchResultEditorNavigationLocation extends NavigationLocation
{

    /**
     * Creates a new instance of SearchResultEditorNavigationLocation.
     *
     * @param editor the search result editor
     */
    SearchResultEditorNavigationLocation( SearchResultEditor editor )
    {
        super( editor );
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        ISearch search = getSearch();
        if ( search != null )
        {
            return "Search " + search.getName();
        }
        else
        {
            return super.getText();
        }
    }


    /**
     * {@inheritDoc}
     */
    public void saveState( IMemento memento )
    {
        ISearch search = getSearch();
        memento.putString( "SEARCH", search.getName() );
        memento.putString( "CONNECTION", search.getConnection().getName() );
    }


    /**
     * {@inheritDoc}
     */
    public void restoreState( IMemento memento )
    {
        IConnection connection = BrowserCorePlugin.getDefault().getConnectionManager().getConnection(
            memento.getString( "CONNECTION" ) );
        ISearch search = connection.getSearchManager().getSearch( memento.getString( "SEARCH" ) );
        super.setInput( new SearchResultEditorInput( search ) );
    }


    /**
     * {@inheritDoc}
     */
    public void restoreLocation()
    {
        IEditorPart editorPart = getEditorPart();
        if ( editorPart != null && editorPart instanceof SearchResultEditor )
        {
            SearchResultEditor searchResultEditor = ( SearchResultEditor ) editorPart;
            searchResultEditor.setInput( ( SearchResultEditorInput ) getInput() );
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean mergeInto( INavigationLocation currentLocation )
    {
        if ( currentLocation == null )
        {
            return false;
        }

        if ( getClass() != currentLocation.getClass() )
        {
            return false;
        }

        SearchResultEditorNavigationLocation location = ( SearchResultEditorNavigationLocation ) currentLocation;
        ISearch other = location.getSearch();
        ISearch search = getSearch();

        if ( other == null && search == null )
        {
            return true;
        }
        else if ( other == null || search == null )
        {
            return false;
        }
        else
        {
            return search.equals( other );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void update()
    {
    }


    /**
     * Gets the search.
     *
     * @return the search
     */
    private ISearch getSearch()
    {

        Object editorInput = getInput();
        if ( editorInput != null && editorInput instanceof SearchResultEditorInput )
        {
            SearchResultEditorInput searchResultEditorInput = ( SearchResultEditorInput ) editorInput;
            ISearch search = searchResultEditorInput.getSearch();
            if ( search != null )
            {
                return search;
            }
        }

        return null;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return "" + getSearch();
    }

}
