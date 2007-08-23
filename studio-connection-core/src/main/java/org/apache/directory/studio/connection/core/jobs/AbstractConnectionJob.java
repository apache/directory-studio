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
import org.apache.directory.studio.connection.core.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Messages;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;


/**
 * Base class for all connections related jobs.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public abstract class AbstractConnectionJob extends Job
{

    /** The external progress monitor. */
    private IProgressMonitor externalProgressMonitor;

    /** The external result. */
    private IStatus externalResult;


    /**
     * Creates a new instance of AbstractConnectionJob.
     */
    protected AbstractConnectionJob()
    {
        super( "" ); //$NON-NLS-1$
    }


    /**
     * Executes the job asynchronously.
     * 
     * @param monitor the progress monitor
     * 
     * @throws Exception the exception
     */
    protected abstract void executeAsyncJob( StudioProgressMonitor monitor ) throws Exception;


    /**
     * Gets the error message.
     * 
     * @return the error message.
     */
    protected String getErrorMessage()
    {
        return Messages.jobs__error_occurred;
    }


    /**
     * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    protected final IStatus run( IProgressMonitor ipm )
    {
        StudioProgressMonitor monitor = new StudioProgressMonitor( externalProgressMonitor == null ? ipm
            : externalProgressMonitor );

        // execute job
        if ( !monitor.errorsReported() )
        {
            try
            {
                executeAsyncJob( monitor );
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
            externalResult = monitor.getErrorStatus( getErrorMessage() );
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
     * Sets the external progress monitor.
     * 
     * @param externalProgressMonitor the external progress monitor
     */
    public void setExternalProgressMonitor( IProgressMonitor externalProgressMonitor )
    {
        this.externalProgressMonitor = externalProgressMonitor;
    }


    /**
     * Gets the result of the executed job. Either Status.OK_STATUS, 
     * Status.CANCEL_STATUS or an error status.
     * 
     * @return the result of the executed job
     */
    public IStatus getExternalResult()
    {
        return this.externalResult;
    }


    /**
     * Executes the job.
     */
    public final void execute()
    {
        setUser( true );
        schedule();
    }


    /**
     * Gets the locked objects.
     * 
     * @return the locked objects
     */
    protected abstract Object[] getLockedObjects();


    /**
     * @see org.eclipse.core.runtime.jobs.Job#shouldSchedule()
     */
    public boolean shouldSchedule()
    {
        Object[] myLockedObjects = getLockedObjects();
        String[] myLockedObjectsIdentifiers = getLockIdentifiers( myLockedObjects );

        // TODO: read, write

        Job[] jobs = Platform.getJobManager().find( null );
        for ( int i = 0; i < jobs.length; i++ )
        {
            Job job = jobs[i];

            // if(job instanceof AbstractEclipseJob) {
            if ( job.getClass() == this.getClass() && job != this )
            {
                AbstractConnectionJob otherJob = ( AbstractConnectionJob ) job;
                Object[] otherLockedObjects = otherJob.getLockedObjects();
                String[] otherLockedObjectIdentifiers = getLockIdentifiers( otherLockedObjects );

                for ( int j = 0; j < otherLockedObjectIdentifiers.length; j++ )
                {
                    String other = otherLockedObjectIdentifiers[j];
                    for ( int k = 0; k < myLockedObjectsIdentifiers.length; k++ )
                    {
                        String my = myLockedObjectsIdentifiers[k];

                        //System.out.print( "other:" + other + ", my: " + my );
                        if ( other.startsWith( my ) || my.startsWith( other ) )
                        {
                            //System.out.println( ", shouldSchedule() = " + false );
                            return false;
                        }
                        else
                        {
                            //System.out.println();
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
        return object.toString();
    }

}
