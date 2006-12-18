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

package org.apache.directory.ldapstudio.browser.core.jobs;


import org.apache.directory.ldapstudio.browser.core.BrowserCoreMessages;
import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.ISearch;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;


public abstract class AbstractEclipseJob extends Job
{

    private IProgressMonitor externalProgressMonitor;

    private IStatus externalResult;


    protected AbstractEclipseJob()
    {
        super( "" ); //$NON-NLS-1$
    }


    protected abstract IConnection[] getConnections();


    protected abstract void executeAsyncJob( ExtendedProgressMonitor monitor ) throws Exception;


    protected String getErrorMessage()
    {
        return BrowserCoreMessages.jobs__error_occurred;
    }


    protected final IStatus run( IProgressMonitor ipm )
    {

        ExtendedProgressMonitor monitor = new ExtendedProgressMonitor( externalProgressMonitor == null ? ipm
            : externalProgressMonitor );

        // check if connection is opened
        IConnection[] connections = getConnections();
        for ( int i = 0; i < connections.length; i++ )
        {
            IConnection connection = connections[i];
            if ( !connection.isOpened() )
            {
                // monitor.reportError("Connection is closed");

                EventRegistry.suspendEventFireingInCurrentThread();
                try
                {
                    connection.open( monitor );
                }
                finally
                {
                    EventRegistry.resumeEventFireingInCurrentThread();
                }
            }
        }

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


    public void setExternalProgressMonitor( IProgressMonitor externalProgressMonitor )
    {
        this.externalProgressMonitor = externalProgressMonitor;
    }


    public IStatus getExternalResult()
    {
        return this.externalResult;
    }


    public final void execute()
    {
        setUser( true );
        schedule();
    }


    protected abstract Object[] getLockedObjects();


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

                AbstractEclipseJob otherJob = ( AbstractEclipseJob ) job;
                Object[] otherLockedObjects = otherJob.getLockedObjects();
                String[] otherLockedObjectIdentifiers = getLockIdentifiers( otherLockedObjects );

                for ( int j = 0; j < otherLockedObjectIdentifiers.length; j++ )
                {
                    String other = otherLockedObjectIdentifiers[j];
                    for ( int k = 0; k < myLockedObjectsIdentifiers.length; k++ )
                    {
                        String my = myLockedObjectsIdentifiers[k];

                        System.out.print( "other:" + other + ", my: " + my );

                        if ( other.startsWith( my ) || my.startsWith( other ) )
                        {
                            System.out.println( ", shouldSchedule() = " + false );
                            return false;
                        }
                        else
                        {
                            System.out.println();
                        }

                    }
                }

            }
        }
        return super.shouldSchedule();

        // // Doesn't work
        // Job[] jobs = getJobManager().find(null);
        // for (int i = 0; i < jobs.length; i++) {
        // Job job = jobs[i];
        // if(job instanceof AbstractEclipseJob) {
        // System.out.println("shouldSchedule() = " + false);
        // return false;
        // }
        // }
        // System.out.println("shouldSchedule() = " + true);
        // return true;

        // return super.shouldSchedule();
    }


    protected static String[] getLockIdentifiers( Object[] objects )
    {
        String[] identifiers = new String[objects.length];
        for ( int i = 0; i < identifiers.length; i++ )
        {
            Object o = objects[i];
            if ( o instanceof IConnection )
            {
                identifiers[i] = getLockIdentifier( ( IConnection ) o );
            }
            else if ( o instanceof IEntry )
            {
                identifiers[i] = getLockIdentifier( ( IEntry ) o );
            }
            else if ( o instanceof IAttribute )
            {
                identifiers[i] = getLockIdentifier( ( IAttribute ) o );
            }
            else if ( o instanceof IValue )
            {
                identifiers[i] = getLockIdentifier( ( IValue ) o );
            }
            else if ( o instanceof ISearch )
            {
                identifiers[i] = getLockIdentifier( ( ISearch ) o );
            }
            else
            {
                identifiers[i] = getLockIdentifier( objects[i] );
            }
        }
        return identifiers;
    }


    protected static String getLockIdentifier( IConnection connection )
    {
        return connection.getHost() + ":" + connection.getPort();
    }


    protected static String getLockIdentifier( IEntry entry )
    {
        return getLockIdentifier( entry.getConnection() ) + "_"
            + new StringBuffer( entry.getDn().toString() ).reverse().toString();
    }


    protected static String getLockIdentifier( IAttribute attribute )
    {
        return getLockIdentifier( attribute.getEntry() ) + "_" + attribute.getDescription();
    }


    protected static String getLockIdentifier( IValue value )
    {
        return getLockIdentifier( value.getAttribute() ) + "_" + value.getStringValue();
    }


    protected static String getLockIdentifier( ISearch search )
    {
        return getLockIdentifier( search.getConnection() ) + "_"
            + new StringBuffer( search.getSearchBase().toString() ).reverse().toString();
    }


    protected static String getLockIdentifier( Object object )
    {
        return object.toString();
    }

}
