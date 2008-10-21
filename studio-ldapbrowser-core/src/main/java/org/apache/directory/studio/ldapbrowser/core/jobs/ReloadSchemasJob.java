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
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.events.BrowserConnectionUpdateEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;


public class ReloadSchemasJob extends AbstractAsyncBulkJob
{

    private IBrowserConnection[] browserConnections;


    public ReloadSchemasJob( IBrowserConnection[] connections )
    {
        this.browserConnections = connections;
        setName( connections.length == 1 ? BrowserCoreMessages.jobs__reload_schemas_name_1
            : BrowserCoreMessages.jobs__reload_schemas_name_n );
    }


    protected Connection[] getConnections()
    {
        Connection[] connections = new Connection[browserConnections.length];
        for ( int i = 0; i < browserConnections.length; i++ )
        {
            connections[i] = browserConnections[i].getConnection();
        }
        return connections;
    }


    protected Object[] getLockedObjects()
    {
        List l = new ArrayList();
        l.addAll( Arrays.asList( browserConnections ) );
        return l.toArray();
    }


    protected void executeBulkJob( StudioProgressMonitor monitor )
    {
        monitor.beginTask( " ", browserConnections.length + 1 ); //$NON-NLS-1$
        monitor.reportProgress( " " ); //$NON-NLS-1$

        for ( int i = 0; i < browserConnections.length; i++ )
        {
            monitor.setTaskName( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__reload_schemas_task, new String[]
                { browserConnections[i].getName() } ) );
            monitor.worked( 1 );

            browserConnections[i].reloadSchema( monitor );
        }
    }


    protected void runNotification()
    {
        for ( IBrowserConnection browserConnection : browserConnections )
        {
            BrowserConnectionUpdateEvent browserConnectionUpdateEvent = new BrowserConnectionUpdateEvent(
                browserConnection, BrowserConnectionUpdateEvent.Detail.SCHEMA_UPDATED );
            EventRegistry.fireBrowserConnectionUpdated( browserConnectionUpdateEvent, this );
        }
    }


    protected String getErrorMessage()
    {
        return browserConnections.length == 1 ? BrowserCoreMessages.jobs__reload_schemas_error_1
            : BrowserCoreMessages.jobs__reload_schemas_error_n;
    }
}
