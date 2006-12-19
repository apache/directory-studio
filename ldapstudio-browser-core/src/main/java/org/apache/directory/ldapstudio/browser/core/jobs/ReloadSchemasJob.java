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
import org.apache.directory.ldapstudio.browser.core.events.ConnectionUpdateEvent;
import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;


public class ReloadSchemasJob extends AbstractAsyncBulkJob
{

    private IConnection[] connections;


    public ReloadSchemasJob( IConnection[] connections )
    {
        this.connections = connections;
        setName( connections.length == 1 ? BrowserCoreMessages.jobs__reload_schemas_name_1
            : BrowserCoreMessages.jobs__reload_schemas_name_n );
    }


    protected IConnection[] getConnections()
    {
        return connections;
    }


    protected Object[] getLockedObjects()
    {
        List l = new ArrayList();
        l.addAll( Arrays.asList( connections ) );
        return l.toArray();
    }


    protected void executeBulkJob( ExtendedProgressMonitor monitor )
    {

        monitor.beginTask( " ", connections.length + 1 ); //$NON-NLS-1$
        monitor.reportProgress( " " ); //$NON-NLS-1$

        for ( int i = 0; i < connections.length; i++ )
        {

            monitor.setTaskName( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__reload_schemas_task, new String[]
                { connections[i].getName() } ) );
            monitor.worked( 1 );

            if ( connections[i].isOpened() )
            {
                connections[i].reloadSchema( monitor );
            }
        }
    }


    protected void runNotification()
    {
        for ( int i = 0; i < connections.length; i++ )
        {
            EventRegistry.fireConnectionUpdated( new ConnectionUpdateEvent( connections[i],
                ConnectionUpdateEvent.CONNECTION_SCHEMA_LOADED ), this );
        }
    }


    protected String getErrorMessage()
    {
        return connections.length == 1 ? BrowserCoreMessages.jobs__reload_schemas_error_1
            : BrowserCoreMessages.jobs__reload_schemas_error_n;
    }
}
