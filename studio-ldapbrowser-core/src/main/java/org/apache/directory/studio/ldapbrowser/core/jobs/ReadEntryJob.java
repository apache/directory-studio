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
import org.apache.directory.studio.ldapbrowser.core.model.DN;
import org.apache.directory.studio.ldapbrowser.core.model.IConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ModelModificationException;


public class ReadEntryJob extends AbstractAsyncBulkJob
{

    private IConnection connection;

    private DN dn;

    private IEntry readEntry;


    public ReadEntryJob( IConnection connection, DN dn )
    {
        this.connection = connection;
        this.dn = dn;
        this.readEntry = null;

        setName( BrowserCoreMessages.jobs__read_entry_name );
    }


    protected IConnection[] getConnections()
    {
        return new IConnection[]
            { connection };
    }


    protected Object[] getLockedObjects()
    {
        List l = new ArrayList();
        l.add( connection );
        return l.toArray();
    }


    public IEntry getReadEntry()
    {
        return readEntry;
    }


    protected String getErrorMessage()
    {
        return BrowserCoreMessages.jobs__read_entry_error;
    }


    protected void executeBulkJob( ExtendedProgressMonitor pm ) throws ModelModificationException
    {
        readEntry = connection.getEntryFromCache( dn );
        if ( readEntry == null )
        {

            pm.beginTask( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__read_entry_task, new String[]
                { dn.toString() } ), 2 );
            pm.reportProgress( " " ); //$NON-NLS-1$
            pm.worked( 1 );

            readEntry = connection.getEntry( dn, pm );
        }
    }


    protected void runNotification()
    {
    }

}
