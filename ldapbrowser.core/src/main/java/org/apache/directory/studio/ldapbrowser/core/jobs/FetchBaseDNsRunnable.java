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
import org.apache.directory.studio.connection.core.jobs.StudioBulkRunnableWithProgress;
import org.apache.directory.studio.connection.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.impl.DirectoryMetadataEntry;


/**
 * Runnable to fetch the base DNs from a directory server.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class FetchBaseDNsRunnable implements StudioBulkRunnableWithProgress
{

    private IBrowserConnection connection;

    private List<String> baseDNs;


    /**
     * Creates a new instance of FetchBaseDNsRunnable.
     * 
     * @param connection the connection
     */
    public FetchBaseDNsRunnable( IBrowserConnection connection )
    {
        this.connection = connection;
        this.baseDNs = new ArrayList<String>();
    }


    /**
     * {@inheritDoc}
     */
    public Connection[] getConnections()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return BrowserCoreMessages.jobs__fetch_basedns_name;
    }


    /**
     * {@inheritDoc}
     */
    public Object[] getLockedObjects()
    {
        return new Connection[]
            { connection.getConnection() };
    }


    /**
     * {@inheritDoc}
     */
    public void run( StudioProgressMonitor monitor )
    {
        monitor.beginTask( BrowserCoreMessages.jobs__fetch_basedns_task, 5 );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        InitializeRootDSERunnable.loadRootDSE( connection, monitor );

        IEntry[] baseDNEntries = connection.getRootDSE().getChildren();
        if ( baseDNEntries != null )
        {
            for ( IEntry baseDNEntry : baseDNEntries )
            {
                if ( !( baseDNEntry instanceof DirectoryMetadataEntry ) )
                {
                    baseDNs.add( baseDNEntry.getDn().getUpName() );
                }
            }
        }

        monitor.worked( 1 );
    }


    /**
     * {@inheritDoc}
     */
    public String getErrorMessage()
    {
        return BrowserCoreMessages.jobs__fetch_basedns_error;
    }


    /**
     * Gets the base DNs.
     * 
     * @return the base DNs
     */
    public List<String> getBaseDNs()
    {
        return baseDNs;
    }


    /**
     * {@inheritDoc}
     */
    public void runNotification( StudioProgressMonitor monitor )
    {
    }

}
