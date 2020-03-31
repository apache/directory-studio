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


import org.apache.directory.api.ldap.model.message.ExtendedRequest;
import org.apache.directory.api.ldap.model.message.ExtendedResponse;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Utils;
import org.apache.directory.studio.connection.core.jobs.StudioConnectionRunnableWithProgress;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;


/**
 * Runnable to execute extended operations.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ExtendedOperationRunnable implements StudioConnectionRunnableWithProgress
{
    private IBrowserConnection connection;

    private ExtendedRequest request;

    private ExtendedResponse response;


    public ExtendedOperationRunnable( final IBrowserConnection connection, ExtendedRequest request )
    {
        this.connection = connection;
        this.request = request;
    }


    public Connection[] getConnections()
    {
        return new Connection[]
            { connection.getConnection() };
    }


    public String getName()
    {
        return BrowserCoreMessages.jobs__extended_operation_name;
    }


    public Object[] getLockedObjects()
    {
        return new Object[]
            { connection };
    }


    public String getErrorMessage()
    {
        return BrowserCoreMessages.jobs__extended_operation_error;
    }


    public void run( StudioProgressMonitor monitor )
    {

        monitor.beginTask( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__extended_operation_task,
            new String[]
            { Utils.getOidDescription( request.getRequestName() ) } ), 2 );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        try
        {
            response = connection.getConnection().getConnectionWrapper().extended( request, monitor );
        }
        catch ( Exception e )
        {
            monitor.reportError( e );
        }
    }


    public ExtendedResponse getResponse()
    {
        return response;
    }
}
