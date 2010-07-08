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
package org.apache.directory.studio.test.integration.ui.bots.utils;


import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;


/**
 * Helper class that watches particular job. The job is identified by its name.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class JobWatcher
{

    private final AtomicBoolean done = new AtomicBoolean();
    private final IJobManager jobManager = Job.getJobManager();
    private final JobChangeAdapter listener;
    private final String jobName;


    /**
     * Creates a new instance of JobWatcher.
     *
     * @param jobName the name of the watched job
     */
    public JobWatcher( final String jobName )
    {
        this.jobName = jobName;

        // register a job listener that checks if the job is finished
        listener = new JobChangeAdapter()
        {

            public void done( IJobChangeEvent event )
            {
                // if the done job has the expected name we are done
                if ( jobName.equals( event.getJob().getName() ) )
                {
                    done.set( true );
                    jobManager.removeJobChangeListener( listener );
                }
            }

        };
        jobManager.addJobChangeListener( listener );
    }


    /**
     * Waits until the watched job is done.
     */
    public void waitUntilDone()
    {
        SWTBot bot = new SWTBot();
        bot.waitUntil( new DefaultCondition()
        {

            public boolean test() throws Exception
            {
                if ( done.get() )
                {
                    return true;
                }

                // fallback test to check if the expected job
                // is scheduled at all, otherwise we assume it is done.
                boolean running = false;
                Job[] find = jobManager.find( null );
                for ( Job job : find )
                {
                    if ( jobName.equals( job.getName() ) )
                    {
                        running = true;
                        break;
                    }
                }
                if ( !running )
                {
                    jobManager.removeJobChangeListener( listener );
                    return true;
                }

                return false;
            }


            public String getFailureMessage()
            {
                return "Job run too long";
            }
        } );
    }
}
