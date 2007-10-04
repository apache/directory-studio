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

package org.apache.directory.studio.ldapbrowser.core.internal.model;


import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.StudioProgressMonitor;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.jobs.AbstractAsyncBulkJob;


public class OpenBrowserConnectionsJob extends AbstractAsyncBulkJob
{

    private BrowserConnection connection;


    public OpenBrowserConnectionsJob( BrowserConnection connection )
    {
        this.connection = connection;
        setName( BrowserCoreMessages.jobs__open_connections_name_1 );
    }


    protected Connection[] getConnections()
    {
        return new Connection[0];
    }


    protected Object[] getLockedObjects()
    {
        return new Object[]
            { connection };
    }


    protected String getErrorMessage()
    {
        return BrowserCoreMessages.jobs__open_connections_error_1;
    }


    protected void executeBulkJob( StudioProgressMonitor monitor )
    {

        monitor.beginTask( " ", 1 * 6 + 1 ); //$NON-NLS-1$
        monitor.reportProgress( " " ); //$NON-NLS-1$

        monitor.setTaskName( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__open_connections_task, new String[]
            { this.connection.getConnection().getName() } ) );
        monitor.worked( 1 );

        connection.open( monitor );
    }


    protected void runNotification()
    {
    }

}
