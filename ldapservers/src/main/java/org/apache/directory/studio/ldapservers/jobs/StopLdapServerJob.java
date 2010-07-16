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
import org.apache.directory.studio.ldapservers.model.LdapServerAdapter;
import org.apache.directory.studio.ldapservers.model.LdapServerAdapterExtension;
import org.apache.directory.studio.ldapservers.model.LdapServerStatus;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;


/**
 * This class implements a {@link Job} that is used to stop an LDAP Server.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StopLdapServerJob extends Job
{
    /** The server */
    private LdapServer server;


    /**
     * Creates a new instance of StartLdapServerJob.
     * 
     * @param server
     *            the LDAP Server
     */
    public StopLdapServerJob( LdapServer server )
    {
        super( "" ); //$NON-NLS-1$
        this.server = server;
    }


    /**
     * {@inheritDoc}
     */
    protected IStatus run( IProgressMonitor monitor )
    {
        // Setting the name of the Job
        setName( NLS.bind( Messages.getString( "StopLdapServerJob.Stopping" ), new String[] { server.getName() } ) ); //$NON-NLS-1$

        // Setting the status on the server to 'stopping'
        server.setStatus( LdapServerStatus.STOPPING );

        // Starting a new watchdog thread
        StopLdapServerWatchDogThread.runNewWatchDogThread( server );

        // Launching the 'stop()' of the LDAP Server Adapter
        LdapServerAdapterExtension ldapServerAdapterExtension = server.getLdapServerAdapterExtension();
        if ( ldapServerAdapterExtension != null )
        {
            LdapServerAdapter ldapServerAdapter = ldapServerAdapterExtension.getInstance();
            if ( ldapServerAdapter != null )
            {
                try
                {
                    ldapServerAdapter.stop( server, monitor );
                }
                catch ( Exception e )
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        return Status.OK_STATUS;
    }
}
