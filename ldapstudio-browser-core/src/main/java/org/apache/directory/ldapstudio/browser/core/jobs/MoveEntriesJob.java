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

package org.apache.directory.ldapstudio.browser.core.jobs;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.directory.ldapstudio.browser.core.BrowserCoreMessages;
import org.apache.directory.ldapstudio.browser.core.events.EntryMovedEvent;
import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.events.SearchUpdateEvent;
import org.apache.directory.ldapstudio.browser.core.model.DN;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.ISearch;
import org.apache.directory.ldapstudio.browser.core.model.ISearchResult;


public class MoveEntriesJob extends AbstractAsyncBulkJob
{

    private IConnection connection;

    private IEntry[] oldEntries;

    private IEntry newParent;

    private IEntry[] newEntries;

    private Set searchesToUpdateSet = new HashSet();


    public MoveEntriesJob( IEntry[] entries, IEntry newParent )
    {
        this.connection = newParent.getConnection();
        this.oldEntries = entries;
        this.newParent = newParent;

        setName( entries.length == 1 ? BrowserCoreMessages.jobs__move_entry_name_1
            : BrowserCoreMessages.jobs__move_entry_name_n );
    }


    protected IConnection[] getConnections()
    {
        return new IConnection[]
            { connection };
    }


    protected Object[] getLockedObjects()
    {
        List l = new ArrayList();
        l.add( newParent );
        l.addAll( Arrays.asList( oldEntries ) );
        return l.toArray();
    }


    protected void executeBulkJob( ExtendedProgressMonitor monitor )
    {

        monitor.beginTask( BrowserCoreMessages.bind(
            oldEntries.length == 1 ? BrowserCoreMessages.jobs__move_entry_task_1
                : BrowserCoreMessages.jobs__move_entry_task_n, new String[]
                {} ), 3 );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        this.newEntries = new IEntry[oldEntries.length];

        for ( int i = 0; i < oldEntries.length; i++ )
        {

            IEntry oldEntry = oldEntries[i];
            IEntry oldParent = oldEntry.getParententry();
            DN newDn = new DN( oldEntry.getRdn(), newParent.getDn() );

            // move in directory
            // TODO: use manual/simulated move, if move of subtree is not
            // supported
            int errorStatusSize1 = monitor.getErrorStatus( "" ).getChildren().length; //$NON-NLS-1$
            connection.move( oldEntry, newParent.getDn(), monitor );
            int errorStatusSize2 = monitor.getErrorStatus( "" ).getChildren().length; //$NON-NLS-1$

            if ( errorStatusSize1 == errorStatusSize2 )
            {
                // move in parent
                oldParent.deleteChild( oldEntry );
                IEntry newEntry = connection.getEntry( newDn, monitor );
                this.newEntries[i] = newEntry;
                newParent.addChild( newEntry );
                newParent.setHasMoreChildren( false );

                newEntry.setHasChildrenHint( oldEntry.hasChildren() );
                if ( oldEntry.isChildrenInitialized() )
                {
                    InitializeChildrenJob.initializeChildren( newEntry, monitor );
                }

                // move in searches
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
    }


    protected void runNotification()
    {
        for ( int i = 0; i < newEntries.length; i++ )
        {
            if ( oldEntries[i] != null && newEntries[i] != null )
            {
                EventRegistry.fireEntryUpdated( new EntryMovedEvent( oldEntries[i], newEntries[i] ), this );
            }
        }
        for ( Iterator it = searchesToUpdateSet.iterator(); it.hasNext(); )
        {
            ISearch search = ( ISearch ) it.next();
            EventRegistry.fireSearchUpdated( new SearchUpdateEvent( search, SearchUpdateEvent.SEARCH_PERFORMED ), this );
        }
    }


    protected String getErrorMessage()
    {
        return oldEntries.length == 1 ? BrowserCoreMessages.jobs__move_entry_error_1
            : BrowserCoreMessages.jobs__move_entry_error_n;
    }

}
