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


import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.common.core.jobs.StudioRunnableWithProgress;
import org.apache.directory.studio.ldapservers.model.LdapServer;
import org.apache.directory.studio.ldapservers.model.LdapServerAdapter;
import org.apache.directory.studio.ldapservers.model.LdapServerAdapterExtension;
import org.apache.directory.studio.ldapservers.model.LdapServerStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;


/**
 * This class implements a {@link Job} that is used to start an LDAP Server.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StartLdapServerRunnable implements StudioRunnableWithProgress
{
    /** The server */
    private LdapServer server;


    /**
     * Creates a new instance of StartLdapServerRunnable.
     * 
     * @param server
     *            the LDAP Server
     */
    public StartLdapServerRunnable( LdapServer server )
    {
        super();
        this.server = server;
    }


    /**
     * {@inheritDoc}
     */
    public String getErrorMessage()
    {
        return NLS.bind( "Unable to start server ''{0}''", new String[]
            { server.getName() } );
    }


    /**
     * {@inheritDoc}
     */
    public Object[] getLockedObjects()
    {
        return new Object[]
            { server };
    }


    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return NLS
            .bind( Messages.getString( "StartLdapServerRunnable.StartServer" ), new String[] { server.getName() } ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public void run( StudioProgressMonitor monitor )
    {
        // Creating a Studio Progress Monitor
        //        StudioProgressMonitor studioConnectionProgressMonitor = new StudioProgressMonitor(
        //            LdapServersPluginConstants.PLUGIN_ID, monitor ); // TODO remove this?

        // Setting the status on the server to 'starting'
        server.setStatus( LdapServerStatus.STARTING );

        // Starting a new watchdog thread
        StartLdapServerWatchDogThread.runNewWatchDogThread( server );

        // Launching the 'start()' method of the LDAP Server Adapter
        LdapServerAdapterExtension ldapServerAdapterExtension = server.getLdapServerAdapterExtension();
        if ( ldapServerAdapterExtension != null )
        {
            LdapServerAdapter ldapServerAdapter = ldapServerAdapterExtension.getInstance();
            if ( ldapServerAdapter != null )
            {
                try
                {
                    ldapServerAdapter.start( server, monitor );
                }
                catch ( Exception e )
                {
                    // Setting the server as stopped
                    server.setStatus( LdapServerStatus.STOPPED );

                    // Reporting the error to the monitor
                    monitor.reportError( e );

                }
            }
        }
    }
}
