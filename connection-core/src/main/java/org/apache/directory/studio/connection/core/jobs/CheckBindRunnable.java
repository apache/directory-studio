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
import org.apache.directory.studio.connection.core.Messages;


/**
 * Runnable to check binding (authentication) to a directory server
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class CheckBindRunnable implements StudioRunnableWithProgress
{

    private Connection connection;


    /**
     * Creates a new instance of CheckBindJob.
     * 
     * @param connection the connection
     */
    public CheckBindRunnable( Connection connection )
    {
        this.connection = connection;
    }


    /**
     * {@inheritDoc}
     */
    public Object[] getLockedObjects()
    {
        return new Object[]
            { connection };
    }


    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return Messages.jobs__check_bind_name;
    }


    /**
     * {@inheritDoc}
     */
    public void run( StudioProgressMonitor monitor )
    {
        monitor.beginTask( Messages.jobs__check_bind_task, 4 );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        connection.getJNDIConnectionWrapper().connect( monitor );
        connection.getJNDIConnectionWrapper().bind( monitor );
        connection.getJNDIConnectionWrapper().disconnect();
    }


    /**
     * {@inheritDoc}
     */
    public String getErrorMessage()
    {
        return Messages.jobs__check_bind_error;
    }


    /**
     * {@inheritDoc}
     */
    public Connection[] getConnections()
    {
        return null;
    }
}
