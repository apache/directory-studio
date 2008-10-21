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
import java.util.Arrays;
import java.util.List;

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.StudioProgressMonitor;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.events.SearchUpdateEvent;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;


public class SearchJob extends AbstractAsyncBulkJob
{

    private ISearch[] searches;


    public SearchJob( ISearch[] searches )
    {
        this.searches = searches;
        setName( BrowserCoreMessages.jobs__search_name );
    }


    protected Connection[] getConnections()
    {
        Connection[] connections = new Connection[searches.length];
        for ( int i = 0; i < connections.length; i++ )
        {
            connections[i] = searches[i].getBrowserConnection().getConnection();
        }
        return connections;
    }


    protected Object[] getLockedObjects()
    {
        List l = new ArrayList();
        l.addAll( Arrays.asList( searches ) );
        return l.toArray();
    }


    protected void executeBulkJob( StudioProgressMonitor monitor )
    {

        monitor.beginTask( " ", searches.length + 1 ); //$NON-NLS-1$
        monitor.reportProgress( " " ); //$NON-NLS-1$

        for ( int pi = 0; pi < searches.length; pi++ )
        {
            ISearch search = searches[pi];

            monitor.setTaskName( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__search_task, new String[]
                { search.getName() } ) );
            monitor.worked( 1 );

            if ( search.getBrowserConnection() != null )
            {
                search.getBrowserConnection().search( search, monitor );
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
