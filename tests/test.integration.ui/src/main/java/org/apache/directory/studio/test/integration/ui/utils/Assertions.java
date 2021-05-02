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
package org.apache.directory.studio.test.integration.ui.utils;


import static org.junit.jupiter.api.Assertions.fail;

import org.apache.directory.studio.connection.core.Messages;
import org.apache.directory.studio.test.integration.ui.bots.BotUtils;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;


public class Assertions
{
    public static void genericTearDownAssertions()
    {
        if ( isOpenConnectionJobRunning() )
        {
            BotUtils.sleep( 5000L );
            if ( isOpenConnectionJobRunning() )
            {
                fail( "No 'Open Connection' job expected" );
            }
        }
    }


    private static boolean isOpenConnectionJobRunning()
    {
        IJobManager jobManager = Job.getJobManager();
        Job[] jobs = jobManager.find( null );
        for ( Job job : jobs )
        {
            if ( job.getName().equals( Messages.jobs__open_connections_name_1 ) )
            {
                return true;
            }
        }
        return false;
    }
}
