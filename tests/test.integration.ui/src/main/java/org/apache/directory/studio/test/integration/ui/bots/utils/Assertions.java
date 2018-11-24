package org.apache.directory.studio.test.integration.ui.bots.utils;


import static org.junit.Assert.fail;

import org.apache.directory.studio.connection.core.Messages;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;


public class Assertions
{
    public static void genericTearDownAssertions()
    {
        IJobManager jobManager = Job.getJobManager();
        Job[] jobs = jobManager.find( null );
        for ( Job job : jobs )
        {
            if ( job.getName().equals( Messages.jobs__open_connections_name_1 ) )
            {
                fail( "No 'Open Connection' job expected" );
            }
        }
    }
}
