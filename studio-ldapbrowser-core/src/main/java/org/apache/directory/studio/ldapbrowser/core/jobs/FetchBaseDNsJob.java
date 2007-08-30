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

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.StudioProgressMonitor;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IRootDSE;


public class FetchBaseDNsJob extends AbstractAsyncBulkJob
{

    private IBrowserConnection connection;

    private String[] baseDNs;


    public FetchBaseDNsJob( IBrowserConnection connection )
    {
        this.connection = connection;
        setName( BrowserCoreMessages.jobs__fetch_basedns_name );
    }


    protected Connection[] getConnections()
    {
        return new Connection[0];
    }


    protected Object[] getLockedObjects()
    {
        List l = new ArrayList();
        l.add( connection );
        return l.toArray();
    }


    protected void executeBulkJob( StudioProgressMonitor monitor )
    {
        monitor.beginTask( BrowserCoreMessages.jobs__fetch_basedns_task, 5 );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        IRootDSE rootDSE = connection.getRootDSE();
        InitializeAttributesJob.initializeAttributes( rootDSE, true, monitor );
//        IEntry[] baseDNEntries = connection.getRootDSE().getChildren();
//        baseDNs = new String[baseDNEntries.length];
//        for ( int i = 0; i < baseDNs.length; i++ )
//        {
//            baseDNs[i] = baseDNEntries[i].getDn().toString();
//        }
        
        IAttribute attribute = rootDSE.getAttribute( IRootDSE.ROOTDSE_ATTRIBUTE_NAMINGCONTEXTS );
        if ( attribute != null )
        {
            baseDNs = attribute.getStringValues();
        }
        
        monitor.worked( 1 );
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
