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


import org.apache.directory.studio.connection.core.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;


/**
 * Base class for bulk jobs. It is used to execute large modifications without
 * firering modification events. The notification of the listeners is done after 
 * the job is executed.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public abstract class AbstractAsyncBulkJob extends AbstractConnectionJob
{

    /**
     * @see org.apache.directory.studio.connection.core.jobs.AbstractConnectionJob#executeAsyncJob(org.apache.directory.studio.connection.core.StudioProgressMonitor)
     */
    protected final void executeAsyncJob( StudioProgressMonitor pm )
    {
        ConnectionEventRegistry.suspendEventFireingInCurrentThread();

        try
        {
            executeBulkJob( pm );
        }
        finally
        {
            ConnectionEventRegistry.resumeEventFireingInCurrentThread();
        }

        this.runNotification();
    }


    /**
     * Executes the bulk job.
     * 
     * @param pm the pm
     */
    protected abstract void executeBulkJob( StudioProgressMonitor pm );


    /**
     * Notifies about changed objects.
     */
    protected abstract void runNotification();

}
