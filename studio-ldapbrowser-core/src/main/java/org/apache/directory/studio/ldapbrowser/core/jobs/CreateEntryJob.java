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


import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.StudioProgressMonitor;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.events.EntryAddedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;


public class CreateEntryJob extends AbstractAsyncBulkJob
{

    private IEntry entryToCreate;
    
    private IBrowserConnection browserConnection;

    private IEntry createdEntry;


    public CreateEntryJob( IEntry entryToCreate, IBrowserConnection browserConnection )
    {
        this.entryToCreate = entryToCreate;
        this.browserConnection = browserConnection;
        this.createdEntry = null;
        
        setName( BrowserCoreMessages.jobs__create_entry_name_1 );
    }


    protected Connection[] getConnections()
    {
        return new Connection[]
            { browserConnection.getConnection() };
    }


    protected Object[] getLockedObjects()
    {
        return new Object[]
            { browserConnection };
    }


    protected void executeBulkJob( StudioProgressMonitor monitor )
    {

        monitor.beginTask( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__create_entry_task_1, new String[]
                { entryToCreate.getDn().toString() } ), 2 + 1 );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        browserConnection.create( entryToCreate, monitor );

        if ( !monitor.errorsReported() )
        {
            createdEntry = browserConnection.getEntry( entryToCreate.getDn(), monitor );
            // createdEntries[i].getParententry().addChild(entry, this);
            createdEntry.setHasChildrenHint( false );
        }

        monitor.worked( 1 );
    }


    protected void runNotification()
    {
        if ( createdEntry != null )
        {
            EventRegistry.fireEntryUpdated( new EntryAddedEvent( browserConnection,
                createdEntry ), this );
        }
    }


    protected String getErrorMessage()
    {
        return BrowserCoreMessages.jobs__create_entry_error_1;
    }

}
