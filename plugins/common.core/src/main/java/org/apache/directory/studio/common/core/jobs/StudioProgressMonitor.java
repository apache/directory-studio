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


import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.ProgressMonitorWrapper;
import org.eclipse.core.runtime.Status;


/**
 * The StudioProgressMonitor extends the the Eclipse
 * Progress Monitor with active cancellation capabilities.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StudioProgressMonitor extends ProgressMonitorWrapper
{
    /** The plugin ID */
    protected String pluginId;

    /** The flag indicating if the work is done */
    protected boolean isDone;

    /** The list of error statuses */
    protected List<Status> errorStatusList;

    /** The list of cancel listeners */
    protected List<CancelListener> cancelListenerList;

    /** Flag to indicate if message reporting is allowed. Whenever reporting a mesasage
     * this flag is set to false. The {@link StudioProgressMonitorWatcherJob} is resetting
     * it to true once a second. This way too many updates are prevented. */
    protected AtomicBoolean allowMessageReporting;


    /**
     * Creates a new instance of ExtendedProgressMonitor.
     * 
     * @param monitor the progress monitor to forward to
     */
    public StudioProgressMonitor( IProgressMonitor monitor )
    {
        this( CommonCoreConstants.PLUGIN_ID, monitor );
    }


    /**
     * Creates a new instance of ExtendedProgressMonitor.
     * 
     * @param pluginId the identifier of the plugin, used to report errors
     * @param monitor the progress monitor to forward to
     */
    public StudioProgressMonitor( String pluginId, IProgressMonitor monitor )
    {
        super( monitor );
        this.pluginId = pluginId;
        isDone = false;
        CommonCorePlugin.getDefault().getStudioProgressMonitorWatcherJob().addMonitor(this);
        allowMessageReporting = new AtomicBoolean( true );
    }


    /**
     * @see org.eclipse.core.runtime.ProgressMonitorWrapper#setCanceled(boolean)
     */
    public void setCanceled( boolean canceled )
    {
        super.setCanceled( canceled );
        
        if ( canceled )
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
            isDone = true;
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
        if ( ( cancelListenerList != null ) && cancelListenerList.contains( listener ) )
        {
            cancelListenerList.remove( listener );
        }
    }


    /* Package protected */void fireCancelRequested()
    {
        CancelEvent event = new CancelEvent( this );
        
        if ( cancelListenerList != null )
        {
            for ( CancelListener cancelListener : cancelListenerList )
            {
                cancelListener.cancelRequested( event );
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
        boolean doReport = allowMessageReporting.getAndSet( false );
        
        if ( doReport )
        {
            subTask( message );
        }
    }


    /**
     * Report error.
     * 
     * @param message the message
     */
    public void reportError( String message )
    {
        reportError( message, null );
    }


    /**
     * Report error.
     * 
     * @param exception the exception
     */
    public void reportError( Exception exception )
    {
        reportError( null, exception );
    }


    /**
     * Report error.
     * 
     * @param message the message
     * @param exception the exception
     */
    public void reportError( String message, Exception exception )
    {
        if ( errorStatusList == null )
        {
            errorStatusList = new ArrayList<Status>( 3 );
        }

        if ( message == null )
        {
            message = ""; //$NON-NLS-1$
        }

        Status errorStatus = new Status( IStatus.ERROR, pluginId, IStatus.ERROR, message, exception );
        errorStatusList.add( errorStatus );
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
        if ( ( errorStatusList == null ) || errorStatusList.isEmpty() )
        {
            if ( isCanceled() )
            {
                return Status.CANCEL_STATUS;
            }
            else
            {
                return Status.OK_STATUS;
            }
        }
        else
        {
            StringBuilder buffer = new StringBuilder(); 
            buffer.append( message );

            // append status messages to message
            for ( Status status : errorStatusList )
            {
                String statusMessage = status.getMessage();
                Throwable exception = status.getException();
                String exceptionMessage = null;
                
                if ( exception != null)
                {
                    exceptionMessage = exception.getMessage();
                }

                // append explicit status message
                if ( !StringUtils.isEmpty( statusMessage ) )
                {
                    buffer.append( "\n - " ).append(  statusMessage );
                }
                
                // append exception message if different to status message
                if ( (exception != null ) && ( exceptionMessage != null ) && !exceptionMessage.equals( statusMessage ) )
                {
                    // strip control characters
                    int indexOfAny = StringUtils.indexOfAny( exceptionMessage, "\n\r\t" ); //$NON-NLS-1$
                    
                    if ( indexOfAny > -1 )
                    {
                        exceptionMessage = exceptionMessage.substring( 0, indexOfAny - 1 );
                    }
                    
                    buffer.append( "\n - " ).append( exceptionMessage ); //$NON-NLS-1$
                }
            }

            // create main status
            MultiStatus multiStatus = new MultiStatus( pluginId, IStatus.ERROR, buffer.toString(), null );

            // append child status
            for ( Status status : errorStatusList )
            {
                String statusMessage = status.getMessage();
                
                if ( status.getException() != null )
                {
                    StringWriter stringWriter = new StringWriter();
                    PrintWriter printWriter = new PrintWriter( stringWriter );
                    status.getException().printStackTrace( printWriter );
                    statusMessage = stringWriter.toString();
                }
                
                multiStatus.add( new Status( status.getSeverity(), status.getPlugin(), status.getCode(), statusMessage,
                    status.getException() ) );
            }

            return multiStatus;
        }
    }


    /**
     * Gets the exception.
     * 
     * @return the exception
     */
    public Exception getException()
    {
        if ( errorStatusList != null )
        {
            return ( Exception ) errorStatusList.get( 0 ).getException();
        }
        
        return null;
    }


    /**
     * Resets this status.
     */
    public void reset()
    {
        isDone = false;
        errorStatusList = null;
    }

    /**
     * CancelEvent.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    public static class CancelEvent
    {
        /** The Monitor used by the Cancel Event */
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
     */
    public interface CancelListener
    {
        /**
         * Cancel requested.
         * 
         * @param event the event
         */
        void cancelRequested( CancelEvent event );
    }
}
