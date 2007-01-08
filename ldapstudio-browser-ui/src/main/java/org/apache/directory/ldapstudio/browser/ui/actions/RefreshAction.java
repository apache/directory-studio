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

package org.apache.directory.ldapstudio.browser.ui.actions;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.directory.ldapstudio.browser.core.events.ModelModifier;
import org.apache.directory.ldapstudio.browser.core.jobs.InitializeAttributesJob;
import org.apache.directory.ldapstudio.browser.core.jobs.InitializeChildrenJob;
import org.apache.directory.ldapstudio.browser.core.jobs.SearchJob;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.ISearch;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;

import org.eclipse.jface.resource.ImageDescriptor;


/**
 * This Action refreshes the selected item.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class RefreshAction extends BrowserAction implements ModelModifier
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
        IEntry[] entries = getEntries();
        ISearch[] searches = getSearches();
        IEntry entryInput = getEntryInput();
        ISearch searchInput = getSearchInput();

        if ( entries.length > 0 && searches.length == 0 && entryInput == null && searchInput == null )
        {
            return "Reload Attributes and Children";
        }
        else if ( searches.length > 0 && entries.length == 0 && entryInput == null && searchInput == null )
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
                return "Search Again";
            }
            else
            {
                return searches.length == 1 ? "Perform Search" : "Perform Searches";
            }
        }
        else if ( entryInput != null && searches.length == 0 && entries.length == 0 && searchInput == null )
        {
            return "Reload Attributes";
        }
        else if ( searchInput != null && searches.length == 0 && entryInput == null )
        {
            return searchInput.getSearchResults() == null ? "Perform Search" : "Search Again";
        }
        else
        {
            return "Refresh";
        }
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_REFRESH );
    }


    /**
     * {@inheritDoc}
     */
    public String getCommandId()
    {
        return "org.eclipse.ui.file.refresh";
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        IEntry[] entries = getEntries();
        ISearch[] searches = getSearches();
        IEntry entryInput = getEntryInput();
        ISearch searchInput = getSearchInput();
        boolean soa = BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_ENTRYEDITOR_SHOW_OPERATIONAL_ATTRIBUTES );

        if ( entries.length > 0 )
        {
            new InitializeAttributesJob( entries, soa ).execute();
            new InitializeChildrenJob( entries ).execute();
        }
        if ( searches.length > 0 )
        {
            new SearchJob( searches ).execute();
        }

        if ( entryInput != null )
        {
            new InitializeAttributesJob( new IEntry[]
                { entryInput }, soa ).execute();
        }
        if ( searchInput != null )
        {
            new SearchJob( new ISearch[]
                { searchInput } ).execute();
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        IEntry[] entries = getEntries();
        ISearch[] searches = getSearches();
        IEntry entryInput = getEntryInput();
        ISearch searchInput = getSearchInput();

        return entries.length > 0 || searches.length > 0 || entryInput != null || searchInput != null;
    }


    /**
     * Gets the Entries
     *
     * @return
     *      the entries
     */
    protected IEntry[] getEntries()
    {
        List<IEntry> entriesList = new ArrayList<IEntry>();
        entriesList.addAll( Arrays.asList( getSelectedEntries() ) );
        for ( int i = 0; i < getSelectedSearchResults().length; i++ )
        {
            entriesList.add( getSelectedSearchResults()[i].getEntry() );
        }
        for ( int i = 0; i < getSelectedBookmarks().length; i++ )
        {
            entriesList.add( getSelectedBookmarks()[i].getEntry() );
        }
        return ( IEntry[] ) entriesList.toArray( new IEntry[entriesList.size()] );
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
