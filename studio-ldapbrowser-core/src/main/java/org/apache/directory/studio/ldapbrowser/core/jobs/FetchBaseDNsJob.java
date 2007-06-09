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
import java.util.List;

import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.model.IConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;


public class FetchBaseDNsJob extends AbstractAsyncBulkJob
{

    private IConnection connection;

    private String[] baseDNs;


    public FetchBaseDNsJob( IConnection connection )
    {
        this.connection = connection;
        setName( BrowserCoreMessages.jobs__fetch_basedns_name );
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

        monitor.beginTask( BrowserCoreMessages.jobs__fetch_basedns_task, 5 );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        connection.bind( monitor );
        connection.fetchRootDSE( monitor );

        IEntry[] baseDNEntries = connection.getRootDSE().getChildren();
        baseDNs = new String[baseDNEntries.length];
        for ( int i = 0; i < baseDNs.length; i++ )
        {
            baseDNs[i] = baseDNEntries[i].getDn().toString();
        }
        monitor.worked( 1 );

        connection.close();

    }


    protected String getErrorMessage()
    {
        return BrowserCoreMessages.jobs__fetch_basedns_error;
    }


    public String[] getBaseDNs()
    {
        if ( baseDNs == null )
        {
            baseDNs = new String[0];
        }
        return baseDNs;
    }


    protected void runNotification()
    {

    }

}
