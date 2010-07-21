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

import org.apache.directory.studio.ldapbrowser.core.jobs.InitializeAttributesRunnable;
import org.apache.directory.studio.ldapbrowser.core.jobs.StudioBrowserJob;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;


/**
 * This Action toggles weather to fetch operational attributes for an entry or not.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class FetchOperationalAttributesAction extends BrowserAction
{
    /**
     * Creates a new instance of FetchOperationalAttributesAction.
     */
    public FetchOperationalAttributesAction()
    {
    }


    @Override
    public int getStyle()
    {
        return Action.AS_CHECK_BOX;
    }


    @Override
    public String getText()
    {
        return Messages.getString( "FetchOperationalAttributesAction.FetchOperationalAttributes" ); //$NON-NLS-1$
    }


    @Override
    public ImageDescriptor getImageDescriptor()
    {
        return null;
    }


    @Override
    public String getCommandId()
    {
        return null;
    }


    @Override
    public boolean isEnabled()
    {
        List<IEntry> entries = getEntries();
        return !entries.isEmpty() && !entries.iterator().next().getBrowserConnection().isFetchOperationalAttributes();
    }


    @Override
    public boolean isChecked()
    {
        boolean checked = true;
        List<IEntry> entries = getEntries();
        if ( entries.isEmpty() )
        {
            checked = false;
        }
        else
        {
            for ( IEntry entry : entries )
            {
                if ( !entry.isInitOperationalAttributes() )
                {
                    checked = false;
                }
            }
        }
        return checked;
    }


    @Override
    public void run()
    {
        IEntry[] entries = getEntries().toArray( new IEntry[0] );
        boolean init = !isChecked();
        for ( IEntry entry : entries )
        {
            entry.setInitOperationalAttributes( init );
        }
        new StudioBrowserJob( new InitializeAttributesRunnable( entries ) ).execute();
    }


    /**
     * Gets the Entries
     *
     * @return
     *      the entries
     */
    protected List<IEntry> getEntries()
    {
        List<IEntry> entriesList = new ArrayList<IEntry>();
        entriesList.addAll( Arrays.asList( getSelectedEntries() ) );
        for ( ISearchResult sr : getSelectedSearchResults() )
        {
            entriesList.add( sr.getEntry() );
        }
        for ( IBookmark bm : getSelectedBookmarks() )
        {
            entriesList.add( bm.getEntry() );
        }
        if ( getInput() != null && getInput() instanceof IEntry )
        {
            // the entry input is usually a cloned entry, lookup the real entry from connection
            IEntry input = ( IEntry ) getInput();
            IEntry entry = input.getBrowserConnection().getEntryFromCache( input.getDn() );
            if ( entry != null )
            {
                entriesList.add( entry );
            }
        }
        return entriesList;
    }

}
