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

package org.apache.directory.studio.common.core.jobs;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;


/**
 * Job to run {@link StudioRunnableWithProgress} runnables.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StudioJob<T extends StudioRunnableWithProgress> extends Job
{
    /** The runnables. */
    protected T[] runnables;


    /**
     * Creates a new instance of StudioJob.
     * 
     * @param runnables the runnables to run
     */
    public StudioJob( T... runnables )
    {
        super( runnables[0].getName() );
        this.runnables = runnables;
    }


    /**
     * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    protected IStatus run( IProgressMonitor ipm )
    {
        StudioProgressMonitor monitor = new StudioProgressMonitor( ipm );

        // Execute job
        if ( !monitor.errorsReported() )
        {
            try
            {
                for ( StudioRunnableWithProgress runnable : runnables )
                {
                    if ( runnable instanceof StudioBulkRunnableWithProgress )
                    {
                        StudioBulkRunnableWithProgress bulkRunnable = ( StudioBulkRunnableWithProgress ) runnable;
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
        // Default implementation does nothing.
    }


    /**
     * Resumes event firing in current thread.
     */
    protected void resumeEventFiringInCurrentThread()
    {
        // Default implementation does nothing.
    }


    /**
     * Executes the job.
     */
    public void execute()
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

        for ( T runnable : runnables )
        {
            Object[] myLockedObjects = runnable.getLockedObjects();
            String[] myLockedObjectsIdentifiers = getLockIdentifiers( myLockedObjects );

            Job[] jobs = getJobManager().find( null );
            for ( int i = 0; i < jobs.length; i++ )
            {
                Job job = jobs[i];
                if ( job instanceof StudioJob )
                {
                    @SuppressWarnings("unchecked")
                    StudioJob<StudioRunnableWithProgress> otherJob = ( StudioJob<StudioRunnableWithProgress> ) job;
                    for ( StudioRunnableWithProgress otherRunnable : otherJob.runnables )
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
     * Gets the string identifiers for the given objects.
     *
     * @param objects
     *      the objects
     * @return
     *      the string identifiers associated with the objects
     */
    protected String[] getLockIdentifiers( Object[] objects )
    {
        String[] identifiers = new String[objects.length];
        for ( int i = 0; i < identifiers.length; i++ )
        {
            identifiers[i] = getLockIdentifier( objects[i] );
        }
        return identifiers;
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
        return ( object != null ? object.toString() : "null" ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public boolean belongsTo( Object family )
    {
        return getFamily().equals( family );
    }


    /**
     * Get the object corresponding to the family of the Job.
     *
     * @return
     *      the object corresponding to the family of the Job
     */
    public Object getFamily()
    {
        return CommonCoreConstants.JOB_FAMILY_ID;
    }
}
