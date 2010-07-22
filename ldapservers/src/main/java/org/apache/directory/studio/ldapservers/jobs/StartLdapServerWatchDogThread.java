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

package org.apache.directory.studio.ldapservers.jobs;


import org.apache.directory.studio.ldapservers.model.LdapServer;
import org.apache.directory.studio.ldapservers.model.LdapServerStatus;


/**
 * This class implements a {@link Thread} that is used as a watch for the start of an LDAP Server.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StartLdapServerWatchDogThread extends Thread
{
    /** The server */
    private LdapServer server;


    /**
     * Creates a new instance of StartLdapServerWatchDogThread.
     * 
     * @param server
     *            the LDAP Server
     */
    private StartLdapServerWatchDogThread( LdapServer server )
    {
        super();
        this.server = server;
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        // Getting the current time
        long startTime = System.currentTimeMillis();

        // Calculating the watchdog time
        final long watchDog = startTime + ( 1000 * 60 * 1 ); // 3 minutes

        // Looping until the end of the watchdog time or when the server status is no longer 'starting'
        while ( ( System.currentTimeMillis() < watchDog ) && ( LdapServerStatus.STARTING == server.getStatus() ) )
        {
            // We just wait one second before starting the test once
            // again
            try
            {
                Thread.sleep( 1000 );
            }
            catch ( InterruptedException e1 )
            {
                // Nothing to do...
            }
        }

        // We exited from the waiting loop

        // Checking if the watchdog time is expired
        if ( ( System.currentTimeMillis() >= watchDog ) && ( LdapServerStatus.STARTING == server.getStatus() ) )
        {
            // TODO Display an error message...

            // Setting the status of the server to 'Stopped'
            server.setStatus( LdapServerStatus.STOPPED );
        }
    }


    /**
     * Runs a new watchdog thread with the given LDAP Server.
     *
     * @param server
     *      the LDAP Server
     */
    public static void runNewWatchDogThread( LdapServer server )
    {
        new StartLdapServerWatchDogThread( server ).start();
    }
}
