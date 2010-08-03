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

package org.apache.directory.studio.ldapservers;


import org.apache.directory.studio.ldapservers.model.LdapServer;
import org.apache.directory.studio.ldapservers.model.LdapServerStatus;
import org.apache.mina.util.AvailablePortFinder;


/**
 * The helper class defines various utility methods for the LDAP Servers plugin.
 */
public class LdapServersUtils
{
    /**
     * Runs the startup listener watchdog.
     *
     * @param server
     *      the server
     * @param port
     *      the port
     * @throws Exception
     */
    public static void runStartupListenerWatchdog( LdapServer server, int port ) throws Exception
    {
        // Getting the current time
        long startTime = System.currentTimeMillis();

        // Calculating the watch dog time
        final long watchDog = startTime + ( 1000 * 60 * 3 ); // 3 minutes

        // Looping until the end of the watchdog if the server is still 'starting'
        while ( ( System.currentTimeMillis() < watchDog ) && ( LdapServerStatus.STARTING == server.getStatus() ) )
        {
            // Getting the port to test
            try
            {
                // If no protocol is enabled, we pass this and 
                // declare the server as started
                if ( port != 0 )
                {
                    // Trying to see if the port is available
                    if ( AvailablePortFinder.available( port ) )
                    {
                        // The port is still available
                        throw new Exception();
                    }
                }

                // If we pass the creation of the context, it means
                // the server is correctly started

                // We set the state of the server to 'started'...
                server.setStatus( LdapServerStatus.STARTED );

                // ... and we exit the thread
                return;
            }
            catch ( Exception e )
            {
                // If we get an exception, it means the server is not 
                // yet started

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
        }

        // If, at the end of the watch dog, the state of the server is
        // still 'starting' then, we declare the server as 'stopped'
        if ( LdapServerStatus.STARTING == server.getStatus() )
        {
            server.setStatus( LdapServerStatus.STOPPED );
        }
    }
}
