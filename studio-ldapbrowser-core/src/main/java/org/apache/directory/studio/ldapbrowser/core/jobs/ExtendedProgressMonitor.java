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

package org.apache.directory.studio.ldapbrowser.core.jobs;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.ProgressMonitorWrapper;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;


public class ExtendedProgressMonitor extends ProgressMonitorWrapper
{

    private boolean done;

    private List errorStatusList;

    private List cancelListenerList;

    private Job checkCanceledJob;


    public ExtendedProgressMonitor( IProgressMonitor monitor )
    {
        super( monitor );
        this.done = false;

        this.checkCanceledJob = new Job( BrowserCoreMessages.jobs__progressmonitor_check_cancellation )
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


    public void setCanceled( boolean b )
    {
        super.setCanceled( b );
        if ( b )
        {
            this.fireCancelRequested();
        }
    }


    public void done()
    {
        synchronized ( this )
        {
            this.done = true;
            super.done();
        }
    }


    public void addCancelListener( CancelListener listener )
    {
        if ( cancelListenerList == null )
            cancelListenerList = new ArrayList();
        if ( !cancelListenerList.contains( listener ) )
            cancelListenerList.add( listener );
    }


    public void removeCancelListener( CancelListener listener )
    {
        if ( cancelListenerList != null && cancelListenerList.contains( listener ) )
            cancelListenerList.remove( listener );
    }


    private void fireCancelRequested()
    {
        CancelEvent event = new CancelEvent( this );
        if ( cancelListenerList != null )
        {
            for ( int i = 0; i < cancelListenerList.size(); i++ )
            {
                CancelListener listener = ( CancelListener ) cancelListenerList.get( i );
                listener.cancelRequested( event );
            }
        }
    }

    Job reportProgressJob = null;

    String reportProgressMessage = null;


    public void reportProgress( String message )
    {
        synchronized ( this )
        {
            if ( !done )
            {
                if ( reportProgressJob == null )
                {
                    reportProgressJob = new Job( BrowserCoreMessages.jobs__progressmonitor_report_progress )
                    {
                        protected IStatus run( IProgressMonitor monitor )
                        {
                            synchronized ( ExtendedProgressMonitor.this )
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


    public void reportError( String message )
    {
        this.reportError( message, null );
    }


    public void reportError( Throwable throwable )
    {
        this.reportError( throwable.getMessage() != null ? throwable.getMessage() : throwable.toString(), throwable );
    }


    public void reportError( String message, Throwable exception )
    {

        if ( this.errorStatusList == null )
            this.errorStatusList = new ArrayList( 3 );

        do
        {
            if ( message == null )
                message = ""; //$NON-NLS-1$

            Status errorStatus = new Status( IStatus.ERROR, BrowserCorePlugin.PLUGIN_ID, IStatus.ERROR, message,
                exception );
            this.errorStatusList.add( errorStatus );

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


    public boolean errorsReported()
    {
        return this.errorStatusList != null;
    }


    public IStatus getErrorStatus( String message )
    {

        if ( this.errorStatusList != null && !this.errorStatusList.isEmpty() )
        {

            Throwable exception = null;
            for ( Iterator it = this.errorStatusList.iterator(); it.hasNext(); )
            {
                Status status = ( Status ) it.next();
                if ( status.getException() != null )
                {
                    exception = status.getException();
                    break;
                }
            }

            MultiStatus multiStatus = new MultiStatus( BrowserCorePlugin.PLUGIN_ID, IStatus.ERROR, message, exception );

            for ( Iterator it = this.errorStatusList.iterator(); it.hasNext(); )
            {
                Status status = ( Status ) it.next();
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

    public static class CancelEvent
    {
        private IProgressMonitor monitor;


        public CancelEvent( IProgressMonitor monitor )
        {
            this.monitor = monitor;
        }


        public IProgressMonitor getMonitor()
        {
            return monitor;
        }
    }

    public interface CancelListener
    {
        public void cancelRequested( CancelEvent event );
    }

}
