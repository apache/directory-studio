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

package org.apache.directory.studio.ldapbrowser.common.actions;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.core.jobs.InitializeAttributesRunnable;
import org.apache.directory.studio.ldapbrowser.core.jobs.InitializeChildrenRunnable;
import org.apache.directory.studio.ldapbrowser.core.jobs.SearchRunnable;
import org.apache.directory.studio.ldapbrowser.core.jobs.StudioBrowserJob;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.eclipse.jface.resource.ImageDescriptor;


/**
 * This Action refreshes the selected item.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class RefreshAction extends BrowserAction
{
    /**
     * Creates a new instance of RefreshAction.
     */
    public RefreshAction()
    {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        List<IEntry> entries = getEntries();
        ISearch[] searches = getSearches();
        IEntry entryInput = getEntryInput();
        ISearch searchInput = getSearchInput();

        if ( entries.size() > 0 && searches.length == 0 && entryInput == null && searchInput == null )
        {
            return entries.size() == 1 ? Messages.getString( "RefreshAction.ReloadEntry" ) : Messages.getString( "RefreshAction.ReloadEntries" ); //$NON-NLS-1$ //$NON-NLS-2$
        }
        else if ( searches.length > 0 && entries.size() == 0 && entryInput == null && searchInput == null )
        {
            boolean searchAgain = true;
            for ( int i = 0; i < searches.length; i++ )
            {
                if ( searches[i].getSearchResults() == null )
                {
                    searchAgain = false;
                    break;
                }
            }
            if ( searchAgain )
            {
                return Messages.getString( "RefreshAction.SearchAgain" ); //$NON-NLS-1$
            }
            else
            {
                return searches.length == 1 ? Messages.getString( "RefreshAction.PerformSearch" ) : Messages.getString( "RefreshAction.PerformSearches" ); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        else if ( entryInput != null && searches.length == 0 && entries.size() == 0 && searchInput == null )
        {
            return Messages.getString( "RefreshAction.RelaodAttributes" ); //$NON-NLS-1$
        }
        else if ( searchInput != null && searches.length == 0 && entryInput == null )
        {
            return searchInput.getSearchResults() == null ? Messages.getString( "RefreshAction.PerformSearch" ) : Messages.getString( "RefreshAction.SearchAgain" ); //$NON-NLS-1$ //$NON-NLS-2$
        }
        else
        {
            return Messages.getString( "RefreshAction.Refresh" ); //$NON-NLS-1$
        }
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return BrowserCommonActivator.getDefault().getImageDescriptor( BrowserCommonConstants.IMG_REFRESH );
    }


    /**
     * {@inheritDoc}
     */
    public String getCommandId()
    {
        return "org.eclipse.ui.file.refresh"; //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        List<IEntry> entries = getEntries();
        ISearch[] searches = getSearches();
        IEntry entryInput = getEntryInput();
        ISearch searchInput = getSearchInput();

        if ( entries.size() > 0 )
        {
            InitializeChildrenRunnable initializeChildrenRunnable = new InitializeChildrenRunnable( entries
                .toArray( new IEntry[0] ), true );
            new StudioBrowserJob( initializeChildrenRunnable ).execute();
        }
        if ( searches.length > 0 )
        {
            for ( ISearch search : searches )
            {
                search.setSearchResults( null );
            }
            new StudioBrowserJob( new SearchRunnable( searches ) ).execute();
        }

        if ( entryInput != null )
        {
            // the entry input is usually a cloned entry, lookup the real entry from connection
            IEntry entry = entryInput.getBrowserConnection().getEntryFromCache( entryInput.getDn() );
            boolean foa = entry.getBrowserConnection().isFetchOperationalAttributes()
                || entry.isOperationalAttributesInitialized();
            new StudioBrowserJob( new InitializeAttributesRunnable( new IEntry[]
                { entry }, foa ) ).execute();
        }
        if ( searchInput != null )
        {
            searchInput.setSearchResults( null );
            new StudioBrowserJob( new SearchRunnable( new ISearch[]
                { searchInput } ) ).execute();
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        List<IEntry> entries = getEntries();
        ISearch[] searches = getSearches();
        IEntry entryInput = getEntryInput();
        ISearch searchInput = getSearchInput();

        return entries.size() > 0 || searches.length > 0 || entryInput != null || searchInput != null;
    }


    /**
     * Gets the Entries
     *
     * @return
     *      the entries
     */
    protected List<IEntry> getEntries()
    {
        List<IEntry> entries = new ArrayList<IEntry>();
        entries.addAll( Arrays.asList( getSelectedEntries() ) );
        for ( ISearchResult searchResult : getSelectedSearchResults() )
        {
            entries.add( searchResult.getEntry() );
        }
        for ( IBookmark bookmark : getSelectedBookmarks() )
        {
            entries.add( bookmark.getEntry() );
        }
        return entries;
    }


    /**
     * Gets the Searches.
     *
     * @return
     *      the Searches
     */
    protected ISearch[] getSearches()
    {
        return getSelectedSearches();
    }


    /**
     * Gets the Entry Input.
     *
     * @return
     *      the Entry Input
     */
    private IEntry getEntryInput()
    {
        if ( getInput() != null && getInput() instanceof IEntry )
        {
            return ( IEntry ) getInput();
        }
        else
        {
            return null;
        }
    }


    /**
     * Gets the Search Input.
     *
     * @return
     *      the Search Input
     */
    private ISearch getSearchInput()
    {
        if ( getInput() != null && getInput() instanceof ISearch )
        {
            return ( ISearch ) getInput();
        }
        else
        {
            return null;
        }
    }
}
