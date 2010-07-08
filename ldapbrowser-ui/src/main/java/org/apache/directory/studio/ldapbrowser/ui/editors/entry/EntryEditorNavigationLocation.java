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

package org.apache.directory.studio.ldapbrowser.ui.editors.entry;


import javax.naming.InvalidNameException;

import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.studio.entryeditors.EntryEditorExtension;
import org.apache.directory.studio.entryeditors.EntryEditorInput;
import org.apache.directory.studio.entryeditors.EntryEditorManager;
import org.apache.directory.studio.entryeditors.EntryEditorUtils;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.INavigationLocation;
import org.eclipse.ui.NavigationLocation;


/**
 * This class is used to mark the entry editor input to the navigation history.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EntryEditorNavigationLocation extends NavigationLocation
{

    /**
     * Creates a new instance of EntryEditorNavigationLocation.
     *
     * @param editor the entry editor
     */
    EntryEditorNavigationLocation( EntryEditor editor )
    {
        super( editor );
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        String text = EntryEditorUtils.getHistoryNavigationText( getEntryEditorInput() );
        return text != null ? text : super.getText();
    }


    /**
     * {@inheritDoc}
     */
    public void saveState( IMemento memento )
    {
        EntryEditorInput eei = getEntryEditorInput();
        if ( eei != null )
        {
            memento.putString( "EXTENSION", eei.getExtension().getId() ); //$NON-NLS-1$
            if ( eei.getEntryInput() != null )
            {
                IEntry entry = eei.getEntryInput();
                memento.putString( "TYPE", "IEntry" ); //$NON-NLS-1$ //$NON-NLS-2$
                memento.putString( "DN", entry.getDn().getUpName() ); //$NON-NLS-1$
                memento.putString( "CONNECTION", entry.getBrowserConnection().getConnection().getId() ); //$NON-NLS-1$
            }
            else if ( eei.getSearchResultInput() != null )
            {
                ISearchResult searchResult = eei.getSearchResultInput();
                memento.putString( "TYPE", "ISearchResult" ); //$NON-NLS-1$ //$NON-NLS-2$
                memento.putString( "DN", searchResult.getDn().getUpName() ); //$NON-NLS-1$
                memento.putString( "SEARCH", searchResult.getSearch().getName() ); //$NON-NLS-1$
                memento.putString(
                    "CONNECTION", searchResult.getSearch().getBrowserConnection().getConnection().getId() ); //$NON-NLS-1$
            }
            else if ( eei.getBookmarkInput() != null )
            {
                IBookmark bookmark = eei.getBookmarkInput();
                memento.putString( "TYPE", "IBookmark" ); //$NON-NLS-1$ //$NON-NLS-2$
                memento.putString( "BOOKMARK", bookmark.getName() ); //$NON-NLS-1$
                memento.putString( "CONNECTION", bookmark.getBrowserConnection().getConnection().getId() ); //$NON-NLS-1$
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void restoreState( IMemento memento )
    {
        try
        {
            String type = memento.getString( "TYPE" ); //$NON-NLS-1$
            String extensionId = memento.getString( "EXTENSION" ); //$NON-NLS-1$
            EntryEditorManager entryEditorManager = BrowserUIPlugin.getDefault().getEntryEditorManager();
            EntryEditorExtension entryEditorExtension = entryEditorManager.getEntryEditorExtension( extensionId );
            if ( "IEntry".equals( type ) ) //$NON-NLS-1$
            {
                IBrowserConnection connection = BrowserCorePlugin.getDefault().getConnectionManager()
                    .getBrowserConnectionById( memento.getString( "CONNECTION" ) ); //$NON-NLS-1$
                LdapDN dn = new LdapDN( memento.getString( "DN" ) ); //$NON-NLS-1$
                IEntry entry = connection.getEntryFromCache( dn );
                super.setInput( new EntryEditorInput( entry, entryEditorExtension ) );
            }
            else if ( "ISearchResult".equals( type ) ) //$NON-NLS-1$
            {
                IBrowserConnection connection = BrowserCorePlugin.getDefault().getConnectionManager()
                    .getBrowserConnectionById( memento.getString( "CONNECTION" ) ); //$NON-NLS-1$
                ISearch search = connection.getSearchManager().getSearch( memento.getString( "SEARCH" ) ); //$NON-NLS-1$
                ISearchResult[] searchResults = search.getSearchResults();
                LdapDN dn = new LdapDN( memento.getString( "DN" ) ); //$NON-NLS-1$
                for ( int i = 0; i < searchResults.length; i++ )
                {
                    if ( dn.equals( searchResults[i].getDn() ) )
                    {
                        super.setInput( new EntryEditorInput( searchResults[i], entryEditorExtension ) );
                        break;
                    }
                }
            }
            else if ( "IBookmark".equals( type ) ) //$NON-NLS-1$
            {
                IBrowserConnection connection = BrowserCorePlugin.getDefault().getConnectionManager()
                    .getBrowserConnectionById( memento.getString( "CONNECTION" ) ); //$NON-NLS-1$
                IBookmark bookmark = connection.getBookmarkManager().getBookmark( memento.getString( "BOOKMARK" ) ); //$NON-NLS-1$
                super.setInput( new EntryEditorInput( bookmark, entryEditorExtension ) );
            }
        }
        catch ( InvalidNameException e )
        {
            e.printStackTrace();
        }
    }


    /**
     * {@inheritDoc}
     */
    public void restoreLocation()
    {
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

        EntryEditorNavigationLocation location = ( EntryEditorNavigationLocation ) currentLocation;
        Object other = location.getEntryEditorInput().getInput();
        Object entry = getEntryEditorInput().getInput();

        if ( other == null && entry == null )
        {
            return true;
        }
        else if ( other == null || entry == null )
        {
            return false;
        }
        else
        {
            return entry.equals( other );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void update()
    {
    }


    /**
     * Gets the input.
     *
     * @return the input
     */
    private EntryEditorInput getEntryEditorInput()
    {
        Object editorInput = getInput();
        if ( editorInput != null && editorInput instanceof EntryEditorInput )
        {
            EntryEditorInput entryEditorInput = ( EntryEditorInput ) editorInput;
            return entryEditorInput;
        }

        return null;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return "" + getEntryEditorInput().getInput(); //$NON-NLS-1$
    }

}
