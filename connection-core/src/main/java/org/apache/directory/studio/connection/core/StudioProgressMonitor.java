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

package org.apache.directory.studio.connection.core;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.ProgressMonitorWrapper;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;


/**
 * The StudioProgressMonitor extends the the Eclipse
 * Progress Monitor with active cancellation capabilities.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class StudioProgressMonitor extends ProgressMonitorWrapper
{

    private boolean done;

    private List<Status> errorStatusList;

    private List<CancelListener> cancelListenerList;

    private Job checkCanceledJob;

    private Job reportProgressJob = null;

    private String reportProgressMessage = null;


    /**
     * Creates a new instance of ExtendedProgressMonitor.
     * 
     * @param monitor the progress monitor to forward to
     */
    public StudioProgressMonitor( IProgressMonitor monitor )
    {
        super( monitor );
        this.done = false;

        this.checkCanceledJob = new Job( Messages.jobs__progressmonitor_check_cancellation )
        {
            protected IStatus run( IProgressMonitor monitor )
            {
                while ( !done )
                {
                    if ( isCanceled() )
                    {
                        fireCancelRequested();
                        break;
                    }
                    else
                    {
                        try
                        {
                            Thread.sleep( 1000 );
                        }
                        catch ( InterruptedException e )
                        {
                        }
                    }
                }
                return Status.OK_STATUS;
            }
        };
        this.checkCanceledJob.setSystem( true );
        this.checkCanceledJob.schedule();
    }


    /**
     * @see org.eclipse.core.runtime.ProgressMonitorWrapper#setCanceled(boolean)
     */
    public void setCanceled( boolean b )
    {
        super.setCanceled( b );
        if ( b )
        {
            fireCancelRequested();
        }
    }


    /**
     * @see org.eclipse.core.runtime.ProgressMonitorWrapper#done()
     */
    public void done()
    {
        synchronized ( this )
        {
            done = true;
            super.done();
        }
    }


    /**
     * Adds the cancel listener.
     * 
     * @param listener the listener
     */
    public void addCancelListener( CancelListener listener )
    {
        if ( cancelListenerList == null )
        {
            cancelListenerList = new ArrayList<CancelListener>();
        }
        if ( !cancelListenerList.contains( listener ) )
        {
            cancelListenerList.add( listener );
        }
    }


    /**
     * Removes the cancel listener.
     * 
     * @param listener the listener
     */
    public void removeCancelListener( CancelListener listener )
    {
        if ( cancelListenerList != null && cancelListenerList.contains( listener ) )
        {
            cancelListenerList.remove( listener );
        }
    }


    private void fireCancelRequested()
    {
        CancelEvent event = new CancelEvent( this );
        if ( cancelListenerList != null )
        {
            for ( int i = 0; i < cancelListenerList.size(); i++ )
            {
                CancelListener listener = cancelListenerList.get( i );
                listener.cancelRequested( event );
            }
        }
    }


    /**
     * Report progress.
     * 
     * @param message the message
     */
    public void reportProgress( String message )
    {
        synchronized ( this )
        {
            if ( !done )
            {
                if ( reportProgressJob == null )
                {
                    reportProgressJob = new Job( Messages.jobs__progressmonitor_report_progress )
                    {
                        protected IStatus run( IProgressMonitor monitor )
                        {
                            synchronized ( StudioProgressMonitor.this )
                            {
                                if ( !done )
                                {
                                    subTask( reportProgressMessage );
                                }
                                return Status.OK_STATUS;
                            }
                        }
                    };
                    reportProgressJob.setSystem( true );
                }

                reportProgressMessage = message;
                reportProgressJob.schedule( 1000 );
            }
        }
    }


    /**
     * Report error.
     * 
     * @param message the message
     */
    public void reportError( String message )
    {
        this.reportError( message, null );
    }


    /**
     * Report error.
     * 
     * @param throwable the throwable
     */
    public void reportError( Throwable throwable )
    {
        reportError( throwable.getMessage() != null ? throwable.getMessage() : throwable.toString(), throwable );
    }


    /**
     * Report error.
     * 
     * @param exception the exception
     * @param message the message
     */
    public void reportError( String message, Throwable exception )
    {
        if ( errorStatusList == null )
        {
            errorStatusList = new ArrayList<Status>( 3 );
        }

        do
        {
            if ( message == null )
            {
                message = ""; //$NON-NLS-1$
            }

            Status errorStatus = new Status( IStatus.ERROR, ConnectionCorePlugin.PLUGIN_ID, IStatus.ERROR, message,
                exception );
            errorStatusList.add( errorStatus );

            if ( exception != null )
            {
                exception = exception.getCause();
            }
            if ( exception != null )
            {
                message = exception.getMessage();
            }
        }
        while ( exception != null );
    }


    /**
     * Errors reported.
     * 
     * @return true, if errors reported
     */
    public boolean errorsReported()
    {
        return errorStatusList != null;
    }


    /**
     * Gets the error status.
     * 
     * @param message the message
     * 
     * @return the error status
     */
    public IStatus getErrorStatus( String message )
    {
        if ( errorStatusList != null && !errorStatusList.isEmpty() )
        {
            Throwable exception = null;
            for ( Iterator<Status> it = errorStatusList.iterator(); it.hasNext(); )
            {
                Status status = it.next();
                if ( status.getException() != null )
                {
                    exception = status.getException();
                    break;
                }
            }

            MultiStatus multiStatus = new MultiStatus( ConnectionCorePlugin.PLUGIN_ID, IStatus.ERROR, message,
                exception );

            for ( Iterator<Status> it = errorStatusList.iterator(); it.hasNext(); )
            {
                Status status = it.next();
                multiStatus.add( new Status( status.getSeverity(), status.getPlugin(), status.getCode(), status
                    .getMessage(), null ) );
            }

            return multiStatus;

        }
        else
        {
            return Status.OK_STATUS;
        }
    }


    /**
     * Gets the exception.
     * 
     * @return the exception
     */
    public Throwable getException()
    {
        if ( errorStatusList != null )
        {
            return errorStatusList.get( 0 ).getException();
        }
        return null;
    }
    
    
    /**
     * Resets this status.
     */
    public void reset()
    {
        this.done = false;
        this.errorStatusList = null;
    }

    /**
     * CancelEvent.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    public static class CancelEvent
    {
        private IProgressMonitor monitor;


        /**
         * Creates a new instance of CancelEvent.
         * 
         * @param monitor the progress monitor
         */
        public CancelEvent( IProgressMonitor monitor )
        {
            this.monitor = monitor;
        }


        /**
         * Gets the monitor.
         * 
         * @return the progress monitor
         */
        public IProgressMonitor getMonitor()
        {
            return monitor;
        }
    }

    /**
     * CancelListener.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    public interface CancelListener
    {

        /**
         * Cancel requested.
         * 
         * @param event the event
         */
        public void cancelRequested( CancelEvent event );
    }

}
