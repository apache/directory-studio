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


import java.io.File;

import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.common.core.jobs.StudioRunnableWithProgress;
import org.apache.directory.studio.ldapservers.LdapServersManager;
import org.apache.directory.studio.ldapservers.model.LdapServer;
import org.apache.directory.studio.ldapservers.model.LdapServerStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;


/**
 * This class implements a {@link Job} that is used to delete an LDAP Server.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DeleteLdapServerRunnable implements StudioRunnableWithProgress
{
    /** The server */
    private LdapServer server;


    /**
     * Creates a new instance of StartLdapServerRunnable.
     * 
     * @param server
     *            the LDAP Server
     */
    public DeleteLdapServerRunnable( LdapServer server )
    {
        super();
        this.server = server;
    }


    /**
     * {@inheritDoc}
     */
    public String getErrorMessage()
    {
        return NLS.bind( "Unable to delete server ''{0}''", new String[]
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
        return NLS.bind(
            Messages.getString( "DeleteLdapServerRunnable.DeleteServer" ), new String[] { server.getName() } ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public void run( StudioProgressMonitor monitor )
    {
        // Storing the started status of the server
        boolean serverStarted = server.getStatus() == LdapServerStatus.STARTED;

        try
        {
            // Checking if the server is running
            // If yes, we need to shut it down before removing its data
            if ( serverStarted )
            {
                // Creating, scheduling and waiting on the job to stop the server
                StudioLdapServerJob job = new StudioLdapServerJob( new StopLdapServerRunnable( server ) );
                job.schedule();
                job.join();
            }

            // Removing the server
            LdapServersManager.getDefault().removeServer( server );

            // Deleting the associated directory on disk
            deleteDirectory( LdapServersManager.getServerFolder( server ).toFile() );

            // Letting the LDAP Server Adapter finish the deletion of the server
            server.getLdapServerAdapterExtension().getInstance().delete( server, monitor );
        }
        catch ( InterruptedException e )
        {
            // Nothing to do
        }
        catch ( Exception e )
        {
            if ( serverStarted )
            {
                // Setting the server as started
                server.setStatus( LdapServerStatus.STARTED );
            }
            else
            {
                // Setting the server as stopped
                server.setStatus( LdapServerStatus.STOPPED );
            }

            // Reporting the error to the monitor
            monitor.reportError( e );
        }
    }


    /**
     * Deletes the given directory
     *
     * @param path
     *      the directory
     * @return
     *      <code>true</code> if and only if the directory is 
     *      successfully deleted; <code>false</code> otherwise
     */
    private boolean deleteDirectory( File path )
    {
        if ( path.exists() )
        {
            File[] files = path.listFiles();
            for ( int i = 0; i < files.length; i++ )
            {
                if ( files[i].isDirectory() )
                {
                    deleteDirectory( files[i] );
                }
                else
                {
                    files[i].delete();
                }
            }
        }
        return ( path.delete() );
    }
}
