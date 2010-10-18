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

package org.apache.directory.studio.connection.core.jobs;


import org.apache.directory.studio.common.core.jobs.StudioJob;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.IConnectionListener;
import org.apache.directory.studio.connection.core.Messages;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;


/**
 * Job to run {@link StudioRunnableWithProgress} runnables.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StudioConnectionJob extends StudioJob<StudioConnectionRunnableWithProgress>
{
    /**
     * Creates a new instance of StudioConnectionJob.
     * 
     * @param runnables the runnables to run
     */
    public StudioConnectionJob( StudioConnectionRunnableWithProgress... runnables )
    {
        super( runnables );
    }


    /**
     * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    protected IStatus run( IProgressMonitor ipm )
    {
        StudioProgressMonitor monitor = new StudioProgressMonitor( ipm );

        // ensure that connections are opened
        for ( StudioConnectionRunnableWithProgress runnable : runnables )
        {
            Connection[] connections = runnable.getConnections();
            if ( connections != null )
            {
                for ( Connection connection : connections )
                {
                    if ( connection != null && !connection.getConnectionWrapper().isConnected() )
                    {
                        monitor.setTaskName( Messages.bind( Messages.jobs__open_connections_task, new String[]
                            { connection.getName() } ) );
                        monitor.worked( 1 );

                        connection.getConnectionWrapper().connect( monitor );
                        if ( connection.getConnectionWrapper().isConnected() )
                        {
                            connection.getConnectionWrapper().bind( monitor );
                        }

                        if ( connection.getConnectionWrapper().isConnected() )
                        {
                            for ( IConnectionListener listener : ConnectionCorePlugin.getDefault()
                                .getConnectionListeners() )
                            {
                                listener.connectionOpened( connection, monitor );
                            }
                            ConnectionEventRegistry.fireConnectionOpened( connection, this );
                        }
                    }
                }
            }
        }

        // execute job
        if ( !monitor.errorsReported() )
        {
            try
            {
                for ( StudioConnectionRunnableWithProgress runnable : runnables )
                {
                    if ( runnable instanceof StudioConnectionBulkRunnableWithProgress )
                    {
                        StudioConnectionBulkRunnableWithProgress bulkRunnable = ( StudioConnectionBulkRunnableWithProgress ) runnable;
                        suspendEventFiringInCurrentThread();
                        try
                        {
                            bulkRunnable.run( monitor );
                        }
                        finally
                        {
                            resumeEventFiringInCurrentThread();
                        }
                        bulkRunnable.runNotification( monitor );
                    }
                    else
                    {
                        runnable.run( monitor );
                    }
                }
            }
            catch ( Exception e )
            {
                monitor.reportError( e );
            }
        }
        // always set done, even if errors were reported
        monitor.done();
        ipm.done();

        // error handling
        if ( monitor.isCanceled() )
        {
            return Status.CANCEL_STATUS;
        }
        else if ( monitor.errorsReported() )
        {
            return monitor.getErrorStatus( runnables[0].getErrorMessage() );
        }
        else
        {
            return Status.OK_STATUS;
        }

    }


    /**
     * Suspends event firing in current thread.
     */
    protected void suspendEventFiringInCurrentThread()
    {
        ConnectionEventRegistry.suspendEventFiringInCurrentThread();
    }


    /**
     * Resumes event firing in current thread.
     */
    protected void resumeEventFiringInCurrentThread()
    {
        ConnectionEventRegistry.resumeEventFiringInCurrentThread();
    }


    /**
     * {@inheritDoc}
     */
    public boolean shouldSchedule()
    {
        // We don't schedule a job if the same type of runnable should run
        // that works on the same entry as the current runnable.

        for ( StudioConnectionRunnableWithProgress runnable : runnables )
        {
            Object[] myLockedObjects = runnable.getLockedObjects();
            String[] myLockedObjectsIdentifiers = getLockIdentifiers( myLockedObjects );

            Job[] jobs = getJobManager().find( null );
            for ( int i = 0; i < jobs.length; i++ )
            {
                Job job = jobs[i];
                if ( job instanceof StudioConnectionJob )
                {
                    StudioConnectionJob otherJob = ( StudioConnectionJob ) job;
                    for ( StudioConnectionRunnableWithProgress otherRunnable : otherJob.runnables )
                    {
                        if ( runnable.getClass() == otherRunnable.getClass() && runnable != otherRunnable )
                        {
                            Object[] otherLockedObjects = otherRunnable.getLockedObjects();
                            String[] otherLockedObjectIdentifiers = getLockIdentifiers( otherLockedObjects );

                            for ( int j = 0; j < otherLockedObjectIdentifiers.length; j++ )
                            {
                                String other = otherLockedObjectIdentifiers[j];
                                for ( int k = 0; k < myLockedObjectsIdentifiers.length; k++ )
                                {
                                    String my = myLockedObjectsIdentifiers[k];
                                    if ( other.startsWith( my ) || my.startsWith( other ) )
                                    {
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return super.shouldSchedule();
    }


    /**
     * {@inheritDoc}
     */
    protected String[] getLockIdentifiers( Object[] objects )
    {
        String[] identifiers = new String[objects.length];
        for ( int i = 0; i < identifiers.length; i++ )
        {
            Object o = objects[i];
            if ( o instanceof Connection )
            {
                identifiers[i] = getLockIdentifier( ( Connection ) o );
            }
            else
            {
                identifiers[i] = getLockIdentifier( objects[i] );
            }
        }
        return identifiers;
    }


    /**
     * Gets the string identifier for a {@link Connection} object.
     *
     * @param connection
     *      the connection
     * @return
     *      the lock identifier for the connection
     */
    private String getLockIdentifier( Connection connection )
    {
        return connection.getHost() + ':' + connection.getPort();
    }


    /**
     * Gets the generic lock identifier for an object.
     *
     * @param object
     *      the object
     * @return
     *      the lock identifier for the object
     */
    private String getLockIdentifier( Object object )
    {
        String s = object != null ? object.toString() : "null"; //$NON-NLS-1$
        s = '-' + s;
        return s;
    }
}
