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


import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.IConnectionListener;
import org.apache.directory.studio.connection.core.Messages;
import org.apache.directory.studio.connection.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.impl.BrowserConnection;
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


    protected abstract Connection[] getConnections();


    protected abstract void executeAsyncJob( StudioProgressMonitor monitor ) throws Exception;


    protected String getErrorMessage()
    {
        return BrowserCoreMessages.jobs__error_occurred;
    }


    protected final IStatus run( IProgressMonitor ipm )
    {

        StudioProgressMonitor monitor = new StudioProgressMonitor( externalProgressMonitor == null ? ipm
            : externalProgressMonitor );

        // ensure that connections are opened
        Connection[] connections = getConnections();
        for ( Connection connection : connections )
        {
            if ( connection != null && !connection.getJNDIConnectionWrapper().isConnected() )
            {
                monitor.setTaskName( Messages.bind( Messages.jobs__open_connections_task, new String[]
                    { connection.getName() } ) );
                monitor.worked( 1 );

                connection.getJNDIConnectionWrapper().connect( monitor );
                connection.getJNDIConnectionWrapper().bind( monitor );
                for ( IConnectionListener listener : ConnectionCorePlugin.getDefault().getConnectionListeners() )
                {
                    listener.connectionOpened( connection, monitor );
                }
                ConnectionEventRegistry.fireConnectionOpened( connection, this );
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
        }
        // always set done, even if errors were reported
        monitor.done();
        ipm.done();

        // error handling
        if ( monitor.isCanceled() )
        {
            externalResult = Status.CANCEL_STATUS;
            return Status.CANCEL_STATUS;
        }
        else if ( monitor.errorsReported() )
        {
            externalResult = monitor.getErrorStatus( getErrorMessage() );
            if ( externalProgressMonitor == null )
            {
                return externalResult;
            }
            else
            {
                return Status.OK_STATUS;
            }
        }
        else
        {
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
                        if ( other.startsWith( my ) || my.startsWith( other ) )
                        {
                            return false;
                        }
                    }
                }
            }
        }
        return super.shouldSchedule();
    }


    protected static String[] getLockIdentifiers( Object[] objects )
    {
        String[] identifiers = new String[objects.length];
        for ( int i = 0; i < identifiers.length; i++ )
        {
            Object o = objects[i];
            if ( o instanceof IBrowserConnection )
            {
                identifiers[i] = getLockIdentifier( ( IBrowserConnection ) o );
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


    protected static String getLockIdentifier( IBrowserConnection browserConnection )
    {
        if ( browserConnection instanceof BrowserConnection && browserConnection.getConnection() != null )
        {
            return browserConnection.getConnection().getHost() + ":" + browserConnection.getConnection().getPort();
        }
        else
        {
            return "";
        }
    }


    protected static String getLockIdentifier( IEntry entry )
    {
        return getLockIdentifier( entry.getBrowserConnection() ) + "_"
            + new StringBuffer( entry.getDn().getNormName() ).reverse().toString();
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
        return getLockIdentifier( search.getBrowserConnection() ) + "_"
            + new StringBuffer( search.getSearchBase().getNormName() ).reverse().toString();
    }


    protected static String getLockIdentifier( Object object )
    {
        return object.toString();
    }

}
