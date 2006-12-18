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
import java.util.List;

import org.apache.directory.ldapstudio.browser.core.BrowserCoreMessages;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;


public class CheckBindJob extends AbstractAsyncBulkJob
{

    private IConnection connection;


    public CheckBindJob( IConnection connection )
    {
        this.connection = connection;
        setName( BrowserCoreMessages.jobs__check_bind_name );
    }


    protected IConnection[] getConnections()
    {
        return new IConnection[0];
    }


    protected Object[] getLockedObjects()
    {
        List l = new ArrayList();
        l.add( connection );
        return l.toArray();
    }


    protected void executeBulkJob( ExtendedProgressMonitor monitor )
    {

        monitor.beginTask( BrowserCoreMessages.jobs__check_bind_task, 4 );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        connection.bind( monitor );

        connection.close();
    }


    protected String getErrorMessage()
    {
        return BrowserCoreMessages.jobs__check_bind_error;
    }


    protected void runNotification()
    {

    }

}
