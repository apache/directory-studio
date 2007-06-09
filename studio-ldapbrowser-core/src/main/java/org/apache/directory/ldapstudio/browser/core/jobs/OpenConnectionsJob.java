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


public class OpenConnectionsJob extends AbstractAsyncBulkJob
{

    private IConnection[] connections;


    public OpenConnectionsJob( IConnection connection )
    {
        this( new IConnection[]
            { connection } );
    }


    public OpenConnectionsJob( IConnection[] connections )
    {
        this.connections = connections;
        setName( connections.length == 1 ? BrowserCoreMessages.jobs__open_connections_name_1
            : BrowserCoreMessages.jobs__open_connections_name_n );
    }


    protected IConnection[] getConnections()
    {
        return new IConnection[0];
    }


    protected Object[] getLockedObjects()
    {
        List l = new ArrayList();
        l.addAll( Arrays.asList( connections ) );
        return l.toArray();
    }


    protected String getErrorMessage()
    {
        return connections.length == 1 ? BrowserCoreMessages.jobs__open_connections_error_1
            : BrowserCoreMessages.jobs__open_connections_error_n;
    }


    protected void executeBulkJob( ExtendedProgressMonitor monitor )
    {

        monitor.beginTask( " ", connections.length * 6 + 1 ); //$NON-NLS-1$
        monitor.reportProgress( " " ); //$NON-NLS-1$

        for ( int i = 0; i < connections.length; i++ )
        {
            if ( connections[i].canOpen() )
            {

                monitor.setTaskName( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__open_connections_task,
                    new String[]
                        { this.connections[i].getName() } ) );
                monitor.worked( 1 );

                connections[i].open( monitor );
            }
        }
    }


    protected void runNotification()
    {
        for ( int i = 0; i < connections.length; i++ )
        {
            if ( connections[i].isOpened() )
            {
                EventRegistry.fireConnectionUpdated( new ConnectionUpdateEvent( connections[i],
                    ConnectionUpdateEvent.EventDetail.CONNECTION_OPENED ), this );
            }
            else
            {
                EventRegistry.fireConnectionUpdated( new ConnectionUpdateEvent( connections[i],
                    ConnectionUpdateEvent.EventDetail.CONNECTION_CLOSED ), this );
            }
        }
    }

}
