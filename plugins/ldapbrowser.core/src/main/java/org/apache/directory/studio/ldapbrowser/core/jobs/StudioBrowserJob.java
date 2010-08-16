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


import org.apache.directory.studio.connection.core.jobs.StudioConnectionJob;
import org.apache.directory.studio.connection.core.jobs.StudioConnectionRunnableWithProgress;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;


/**
 * Job to run {@link StudioConnectionRunnableWithProgress} runnables.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StudioBrowserJob extends StudioConnectionJob
{
    /**
     * Creates a new instance of StudioBrowserJob.
     * 
     * @param runnables the runnables to run
     */
    public StudioBrowserJob( StudioConnectionRunnableWithProgress... runnables )
    {
        super( runnables );
    }


    /**
     * {@inheritDoc}
     */
    protected void suspendEventFiringInCurrentThread()
    {
        EventRegistry.suspendEventFiringInCurrentThread();
        super.suspendEventFiringInCurrentThread();
    }


    /**
     * {@inheritDoc}
     */
    protected void resumeEventFiringInCurrentThread()
    {
        EventRegistry.resumeEventFiringInCurrentThread();
        super.resumeEventFiringInCurrentThread();
    }
}
