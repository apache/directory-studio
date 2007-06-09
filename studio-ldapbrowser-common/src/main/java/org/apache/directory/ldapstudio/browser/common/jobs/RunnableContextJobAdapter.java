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

package org.apache.directory.ldapstudio.browser.common.jobs;


import org.apache.directory.ldapstudio.browser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;


/**
 * This class provides some convinience methods to execute a job within
 * an {@link IRunnableContext}.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class RunnableContextJobAdapter
{

    /**
     * Executes the given job within a new {@link ProgressMonitorDialog}.
     *
     * @param job the job to execute
     */
    public static void execute( final AbstractEclipseJob job )
    {
        execute( job, null );
    }


    /**
     * Executes the given job within the given runnable context and enabled error handling
     * 
     * @param runnableContext the runnable context
     * @param job the job to execute
     */
    public static void execute( final AbstractEclipseJob job, IRunnableContext runnableContext )
    {
        execute( job, runnableContext, true );
    }


    /**
     * Executes the given job within the given runnable context.
     * 
     * @param runnableContext the runnable context
     * @param job the job to execute
     * @param handleError true to handle errors
     */
    public static void execute( final AbstractEclipseJob job, IRunnableContext runnableContext, boolean handleError )
    {

        if ( runnableContext == null )
        {
            runnableContext = new ProgressMonitorDialog( Display.getDefault().getActiveShell() );
        }

        IRunnableWithProgress runnable = new IRunnableWithProgress()
        {
            public void run( IProgressMonitor ipm ) throws InterruptedException
            {
                job.setExternalProgressMonitor( ipm );
                job.execute();
                job.join();
            }
        };

        try
        {
            runnableContext.run( true, true, runnable );
        }
        catch ( Exception ex )
        {
            BrowserCommonActivator.getDefault().getExceptionHandler().handleException(
                new Status( IStatus.ERROR, BrowserCommonActivator.PLUGIN_ID, IStatus.ERROR, ex.getMessage(), ex ) );
        }

        if ( handleError && !job.getExternalResult().isOK() )
        {
            IStatus status = job.getExternalResult();
            BrowserCommonActivator.getDefault().getExceptionHandler().handleException( status );
        }

    }

}
