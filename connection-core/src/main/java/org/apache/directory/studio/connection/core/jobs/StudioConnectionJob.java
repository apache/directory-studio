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
 * Job to run a {@link StudioRunnableWithProgress}.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class StudioConnectionJob extends Job
{

    /** The runnable. */
    private StudioRunnableWithProgress runnable;

    /** The external progress monitor. */
    private IProgressMonitor externalProgressMonitor;

    /** The external result. */
    private IStatus externalResult;


    /**
     * Creates a new instance of AbstractConnectionJob.
     */
    public StudioConnectionJob( StudioRunnableWithProgress runnable )
    {
        super( runnable.getName() ); //$NON-NLS-1$
        this.runnable = runnable;
    }


    /**
     * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    protected final IStatus run( IProgressMonitor ipm )
    {
        StudioProgressMonitor monitor = new StudioProgressMonitor( externalProgressMonitor == null ? ipm
            : externalProgressMonitor );

        // ensure that connections are opened
        Connection[] connections = runnable.getConnections();
        if ( connections != null )
        {
            for ( Connection connection : connections )
            {
                if ( connection != null && !connection.getJNDIConnectionWrapper().isConnected() )
                {
                    monitor.setTaskName( Messages.bind( Messages.jobs__open_connections_task, new String[]
                        { connection.getName() } ) );
                    monitor.worked( 1 );

                    connection.getJNDIConnectionWrapper().connect( monitor );
                    connection.getJNDIConnectionWrapper().bind( monitor );

                    if ( connection.getJNDIConnectionWrapper().isConnected() )
                    {
                        for ( IConnectionListener listener : ConnectionCorePlugin.getDefault().getConnectionListeners() )
                        {
                            listener.connectionOpened( connection, monitor );
                        }
                        ConnectionEventRegistry.fireConnectionOpened( connection, this );
                    }
                }
            }
        }

        // execute job
        if ( !monitor.errorsReported() )
        {
            try
            {
                if ( runnable instanceof StudioBulkRunnableWithProgress )
                {
                    StudioBulkRunnableWithProgress bulkRunnable = ( StudioBulkRunnableWithProgress ) runnable;
                    suspendEventFireingInCurrentThread();
                    try
                    {
                        bulkRunnable.run( monitor );
                    }
                    finally
                    {
                        resumeEventFireingInCurrentThread();
                    }
                    bulkRunnable.runNotification();
                }
                else
                {
                    System.out.println( "NoBulk: " + runnable );
                    runnable.run( monitor );
                }

            }
            catch ( Exception e )
            {
                monitor.reportError( e );
            }
            finally
            {
                monitor.done();
                ipm.done();
            }
        }

        // error handling
        if ( monitor.isCanceled() )
        {
            // System.out.println("Job: CANCEL+CANCEL");
            externalResult = Status.CANCEL_STATUS;
            return Status.CANCEL_STATUS;
        }
        else if ( monitor.errorsReported() )
        {
            externalResult = monitor.getErrorStatus( runnable.getErrorMessage() );
            if ( externalProgressMonitor == null )
            {
                // System.out.println("Job: ERROR+ERROR");
                return externalResult;
            }
            else
            {
                // System.out.println("Job: ERROR+OK");
                return Status.OK_STATUS;
            }
        }
        else
        {
            // System.out.println("Job: OK+OK");
            externalResult = Status.OK_STATUS;
            return Status.OK_STATUS;
        }
    }


    /**
     * Suspends event fireing in current thread.
     */
    protected void suspendEventFireingInCurrentThread()
    {
        ConnectionEventRegistry.suspendEventFireingInCurrentThread();
    }


    /**
     * Resumes event fireing in current thread.
     */
    protected void resumeEventFireingInCurrentThread()
    {
        ConnectionEventRegistry.resumeEventFireingInCurrentThread();
    }


//    /**
//     * Sets the external progress monitor.
//     * 
//     * @param externalProgressMonitor the external progress monitor
//     */
//    public void setExternalProgressMonitor( IProgressMonitor externalProgressMonitor )
//    {
//        this.externalProgressMonitor = externalProgressMonitor;
//    }
//
//
//    /**
//     * Gets the result of the executed job. Either Status.OK_STATUS, 
//     * Status.CANCEL_STATUS or an error status.
//     * 
//     * @return the result of the executed job
//     */
//    public IStatus getExternalResult()
//    {
//        return this.externalResult;
//    }


    /**
     * Executes the job.
     */
    public final void execute()
    {
        setUser( true );
        schedule();
    }


    /**
     * {@inheritDoc}
     */
    public boolean shouldSchedule()
    {
        // We don't schedule a job if the same type of runnable should run
        // that works on the same entry as the current runnable.

        Object[] myLockedObjects = runnable.getLockedObjects();
        String[] myLockedObjectsIdentifiers = getLockIdentifiers( myLockedObjects );

        Job[] jobs = getJobManager().find( null );
        for ( int i = 0; i < jobs.length; i++ )
        {
            Job job = jobs[i];
            if ( job instanceof StudioConnectionJob )
            {
                StudioConnectionJob otherJob = ( StudioConnectionJob ) job;
                if ( this.runnable.getClass() == otherJob.runnable.getClass() && this.runnable != otherJob.runnable )
                {
                    Object[] otherLockedObjects = otherJob.runnable.getLockedObjects();
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
        return super.shouldSchedule();
    }


    private static String[] getLockIdentifiers( Object[] objects )
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


    private static String getLockIdentifier( Connection connection )
    {
        return connection.getHost() + ":" + connection.getPort();
    }


    private static String getLockIdentifier( Object object )
    {
        return object != null ? object.toString() : "null";
    }

}
