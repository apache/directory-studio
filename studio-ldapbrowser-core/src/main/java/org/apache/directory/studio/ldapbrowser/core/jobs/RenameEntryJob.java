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

package org.apache.directory.studio.ldapbrowser.core.jobs;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.events.EntryRenamedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.events.SearchUpdateEvent;
import org.apache.directory.studio.ldapbrowser.core.model.DN;
import org.apache.directory.studio.ldapbrowser.core.model.IConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.RDN;


public class RenameEntryJob extends AbstractAsyncBulkJob
{

    private IConnection connection;

    private IEntry oldEntry;

    private RDN newRdn;

    private boolean deleteOldRdn;

    private IEntry newEntry;

    private Set searchesToUpdateSet = new HashSet();


    public RenameEntryJob( IEntry entry, RDN newRdn, boolean deleteOldRdn )
    {
        this.connection = entry.getConnection();
        this.oldEntry = entry;
        this.newEntry = entry;
        this.newRdn = newRdn;
        this.deleteOldRdn = deleteOldRdn;

        setName( BrowserCoreMessages.jobs__rename_entry_name );
    }


    protected IConnection[] getConnections()
    {
        return new IConnection[]
            { connection };
    }


    protected Object[] getLockedObjects()
    {
        List l = new ArrayList();
        l.add( oldEntry.getParententry() );
        return l.toArray();
    }


    protected void executeBulkJob( ExtendedProgressMonitor monitor )
    {

        monitor.beginTask( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__rename_entry_task, new String[]
            { this.oldEntry.getDn().toString() } ), 3 );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        IEntry parent = oldEntry.getParententry();
        DN newDn = new DN( newRdn, parent.getDn() );

        // rename in directory
        // TODO: use manual/simulated rename, if rename of subtree is not
        // supported
        connection.rename( oldEntry, newDn, deleteOldRdn, monitor );

        if ( !monitor.errorsReported() )
        {
            // rename in parent
            parent.deleteChild( oldEntry );
            this.newEntry = connection.getEntry( newDn, monitor );
            parent.addChild( newEntry );
            parent.setHasMoreChildren( false );

            newEntry.setHasChildrenHint( oldEntry.hasChildren() );
            if ( oldEntry.isChildrenInitialized() )
            {
                InitializeChildrenJob.initializeChildren( newEntry, monitor );
            }

            // rename in searches
            ISearch[] searches = connection.getSearchManager().getSearches();
            for ( int j = 0; j < searches.length; j++ )
            {
                ISearch search = searches[j];
                if ( search.getSearchResults() != null )
                {
                    ISearchResult[] searchResults = search.getSearchResults();
                    for ( int k = 0; k < searchResults.length; k++ )
                    {
                        ISearchResult result = searchResults[k];
                        if ( oldEntry.equals( result.getEntry() ) )
                        {
                            ISearchResult[] newsrs = new ISearchResult[searchResults.length - 1];
                            System.arraycopy( searchResults, 0, newsrs, 0, k );
                            System.arraycopy( searchResults, k + 1, newsrs, k, searchResults.length - k - 1 );
                            search.setSearchResults( newsrs );
                            searchResults = newsrs;
                            k--;
                            searchesToUpdateSet.add( search );
                        }
                    }
                }
            }
        }
    }


    protected void runNotification()
    {
        if ( oldEntry != null && newEntry != null )
        {
            EventRegistry.fireEntryUpdated( new EntryRenamedEvent( oldEntry, newEntry ), this );
        }
        for ( Iterator it = searchesToUpdateSet.iterator(); it.hasNext(); )
        {
            ISearch search = ( ISearch ) it.next();
            EventRegistry.fireSearchUpdated( new SearchUpdateEvent( search, SearchUpdateEvent.EventDetail.SEARCH_PERFORMED ), this );
        }
    }


    protected String getErrorMessage()
    {
        return BrowserCoreMessages.jobs__rename_entry_error;
    }

}
