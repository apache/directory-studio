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


import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;


/**
 * Job that watches the {@link StudioProgressMonitor}s.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StudioProgressMonitorWatcherJob extends Job
{

    /** The running flag */
    private final AtomicBoolean running;

    /** The list of active monitors being watched */
    private final ConcurrentLinkedQueue<StudioProgressMonitor> monitors;


    /**
     * The constructor
     */
    public StudioProgressMonitorWatcherJob()
    {
        super( Messages.getString( "StudioProgressMonitor.CheckCancellation" ) );
        running = new AtomicBoolean( true );
        monitors = new ConcurrentLinkedQueue<StudioProgressMonitor>();
    }


    /**
     * Set the running flag to false
     */
    public void stop()
    {
        running.set( false );
    }


    /**
     * Add a new monitor to the list of monitors being watched
     * 
     * @param monitor The monitor to add
     */
    public void addMonitor( StudioProgressMonitor monitor )
    {
        monitors.add( monitor );
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected IStatus run( IProgressMonitor monitor )
    {
        while ( running.get() )
        {
            for ( Iterator<StudioProgressMonitor> it = monitors.iterator(); it.hasNext(); )
            {
                StudioProgressMonitor next = it.next();
                StudioProgressMonitor spm = next;

                do
                {
                    // reset allow message reporting
                    if ( !spm.isCanceled() && !spm.isDone )
                    {
                        spm.allowMessageReporting.set( true );
                    }

                    // check if canceled
                    if ( spm.isCanceled() )
                    {
                        spm.fireCancelRequested();
                    }
                    
                    if ( spm.isCanceled() || spm.isDone )
                    {
                        it.remove();
                        break;
                    }

                    if ( spm.getWrappedProgressMonitor() instanceof StudioProgressMonitor )
                    {
                        spm = ( StudioProgressMonitor ) spm.getWrappedProgressMonitor();
                    }
                    else
                    {
                        spm = null;
                    }
                }
                while ( spm != null );
            }

            try
            {
                Thread.sleep( 1000 );
            }
            catch ( InterruptedException e )
            {
            }
        }
        
        return Status.OK_STATUS;
    }
}
