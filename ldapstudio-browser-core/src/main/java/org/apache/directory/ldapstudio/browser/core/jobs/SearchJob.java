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
import java.util.List;

import org.apache.directory.ldapstudio.browser.core.BrowserCoreMessages;
import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.events.SearchUpdateEvent;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.ISearch;


public class SearchJob extends AbstractAsyncBulkJob
{

    private ISearch[] searches;


    public SearchJob( ISearch[] searches )
    {
        this.searches = searches;
        setName( BrowserCoreMessages.jobs__search_name );
    }


    protected IConnection[] getConnections()
    {
        IConnection[] connections = new IConnection[searches.length];
        for ( int i = 0; i < connections.length; i++ )
        {
            connections[i] = searches[i].getConnection();
        }
        return connections;
    }


    protected Object[] getLockedObjects()
    {
        List l = new ArrayList();
        l.addAll( Arrays.asList( searches ) );
        return l.toArray();
    }


    protected void executeBulkJob( ExtendedProgressMonitor monitor )
    {

        monitor.beginTask( " ", searches.length + 1 ); //$NON-NLS-1$
        monitor.reportProgress( " " ); //$NON-NLS-1$

        for ( int pi = 0; pi < searches.length; pi++ )
        {
            ISearch search = searches[pi];

            monitor.setTaskName( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__search_task, new String[]
                { search.getName() } ) );
            monitor.worked( 1 );

            if ( search.getConnection() != null && search.getConnection().isOpened() )
            {

                // // clear search result attributes
                // if(search.getSearchResults() != null) {
                // ISearchResult[] srs = search.getSearchResults();
                // for (int s = 0; s < srs.length; s++) {
                // IEntry entry = srs[s].getEntry();
                // entry.setAttributesInitialized(false, entry.getConnection());
                // }
                // }

                search.getConnection().search( search, monitor );
            }
        }
    }


    protected void runNotification()
    {
        for ( int pi = 0; pi < searches.length; pi++ )
        {
            EventRegistry.fireSearchUpdated( new SearchUpdateEvent( searches[pi], SearchUpdateEvent.EventDetail.SEARCH_PERFORMED ),
                this );
        }
    }


    protected String getErrorMessage()
    {
        return searches.length == 1 ? BrowserCoreMessages.jobs__search_error_1
            : BrowserCoreMessages.jobs__search_error_n;
    }

}
