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


import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.directory.studio.common.core.jobs.StudioJob;
import org.apache.directory.studio.connection.core.jobs.StudioConnectionRunnableWithProgress;
import org.apache.directory.studio.connection.ui.RunnableContextRunner;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
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
    private final IJobManager jobManager = StudioJob.getJobManager();
    private final JobChangeAdapter jobChangeListener;
    private final RunnableContextRunner.Listener runnnableContextRunnerListener;
    private final List<String> jobNames;


    private void removeListeners()
    {
        jobManager.removeJobChangeListener( jobChangeListener );
        RunnableContextRunner.removeListener( runnnableContextRunnerListener );
    }


    /**
     * Creates a new instance of JobWatcher.
     *
     * @param jobName the name of the watched job
     */
    public JobWatcher( final String... jobNames )
    {
        this.jobNames = Arrays.asList( jobNames );
        // System.out.println( "Init for jobs: " + this.jobNames );

        // register a job listener that checks if the job is finished
        jobChangeListener = new JobChangeAdapter()
        {

            public void done( IJobChangeEvent event )
            {
                // System.out.println( "Done called: event=" + event.getJob().getName() );
                // if the done job has the expected name we are done
                for ( String jobName : jobNames )
                {
                    if ( event.getJob().getName().startsWith( jobName ) )
                    {
                        // System.out.println( "Done done: " + jobName );
                        done.set( true );
                        removeListeners();
                    }
                }
            }

        };
        jobManager.addJobChangeListener( jobChangeListener );

        runnnableContextRunnerListener = new RunnableContextRunner.Listener()
        {
            @Override
            public void done( StudioConnectionRunnableWithProgress runnable, IStatus status )
            {
                // System.out.println( "Done called: runnable=" + runnable.getName() );
                for ( String jobName : jobNames )
                {
                    if ( runnable.getName().startsWith( jobName ) )
                    {
                        // System.out.println( "Done done: " + jobName );
                        done.set( true );
                        removeListeners();
                    }
                }
            }
        };
        RunnableContextRunner.addListener( runnnableContextRunnerListener );
    }


    /**
     * Waits until the watched job is done.
     */
    public void waitUntilDone()
    {
        // System.out.println( "Wait for jobs: " + jobNames );
        Instant start = Instant.now();
        SWTBot bot = new SWTBot();
        bot.waitUntil( new DefaultCondition()
        {

            public boolean test() throws Exception
            {
                if ( done.get() )
                {
                    // System.out.println( "Done is true: " + jobNames );
                    removeListeners();
                    return true;
                }

                return false;
            }


            public String getFailureMessage()
            {
                removeListeners();
                return "Waited for jobs " + jobNames + " to finish";
            }
        }, SWTBotPreferences.TIMEOUT * 4 );
    }
}
