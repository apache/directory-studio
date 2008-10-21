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

package org.apache.directory.studio.connection.core.jobs;


import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Messages;


/**
 * Job to check if a connection to a directory server could be established
 * using the given connection parmeter.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class CheckNetworkParameterJob extends AbstractConnectionJob
{

    private Connection connection;


    /**
     * Creates a new instance of CheckNetworkParameterJob.
     * 
     * @param connection the connection
     */
    public CheckNetworkParameterJob( Connection connection )
    {
        this.connection = connection;
        setName( Messages.jobs__check_network_name );
    }


    /**
     * @see org.apache.directory.studio.connection.core.jobs.AbstractConnectionJob#getLockedObjects()
     */
    protected Object[] getLockedObjects()
    {
        return new Object[]
            { connection };
    }


    /**
     * @see org.apache.directory.studio.connection.core.jobs.AbstractConnectionJob#executeAsyncJob(org.apache.directory.studio.connection.core.StudioProgressMonitor)
     */
    protected void executeAsyncJob( StudioProgressMonitor monitor )
    {
        monitor.beginTask( Messages.jobs__check_network_task, 3 );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        connection.getJNDIConnectionWrapper().connect( monitor );
        connection.getJNDIConnectionWrapper().disconnect();
    }


    /**
     * @see org.apache.directory.studio.connection.core.jobs.AbstractConnectionJob#getErrorMessage()
     */
    protected String getErrorMessage()
    {
        return Messages.jobs__check_network_error;
    }

}
