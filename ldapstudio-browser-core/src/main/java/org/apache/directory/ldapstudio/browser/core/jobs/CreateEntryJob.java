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
import org.apache.directory.ldapstudio.browser.core.events.EntryAddedEvent;
import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;


public class CreateEntryJob extends AbstractAsyncBulkJob
{

    private IEntry[] entriesToCreate;

    private IEntry[] createdEntries;


    public CreateEntryJob( IEntry[] entriesToCreate )
    {
        this.entriesToCreate = entriesToCreate;
        createdEntries = new IEntry[entriesToCreate.length];
        setName( entriesToCreate.length == 1 ? BrowserCoreMessages.jobs__create_entry_name_1
            : BrowserCoreMessages.jobs__create_entry_name_n );
    }


    protected IConnection[] getConnections()
    {
        IConnection[] connections = new IConnection[entriesToCreate.length];
        for ( int i = 0; i < connections.length; i++ )
        {
            connections[i] = entriesToCreate[i].getConnection();
        }
        return connections;
    }


    protected Object[] getLockedObjects()
    {
        List l = new ArrayList();
        l.addAll( Arrays.asList( entriesToCreate ) );
        return l.toArray();
    }


    protected void executeBulkJob( ExtendedProgressMonitor monitor )
    {

        monitor.beginTask( entriesToCreate.length == 1 ? BrowserCoreMessages.bind(
            BrowserCoreMessages.jobs__create_entry_task_1, new String[]
                { entriesToCreate[0].getDn().toString() } ) : BrowserCoreMessages.bind(
            BrowserCoreMessages.jobs__create_entry_task_n, new String[]
                { Integer.toString( entriesToCreate.length ) } ), 2 + entriesToCreate.length );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        for ( int i = 0; !monitor.isCanceled() && i < entriesToCreate.length; i++ )
        {
            IEntry entryToCreate = entriesToCreate[i];

            entryToCreate.getConnection().create( entryToCreate, monitor );

            if ( !monitor.errorsReported() )
            {
                createdEntries[i] = entryToCreate.getConnection().getEntry( entryToCreate.getDn(), monitor );
                // createdEntries[i].getParententry().addChild(entry, this);
                createdEntries[i].setHasChildrenHint( false, this );
            }

            monitor.worked( 1 );
        }
    }


    protected void runNotification()
    {
        for ( int i = 0; i < createdEntries.length; i++ )
        {
            if ( createdEntries[i] != null )
            {
                EventRegistry.fireEntryUpdated( new EntryAddedEvent( createdEntries[i].getConnection(),
                    createdEntries[i], this ), this );
            }
        }
    }


    protected String getErrorMessage()
    {
        return entriesToCreate.length == 1 ? BrowserCoreMessages.jobs__create_entry_error_1
            : BrowserCoreMessages.jobs__create_entry_error_n;
    }

}
