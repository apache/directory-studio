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

package org.apache.directory.ldapstudio.browser.ui.jobs;


import org.apache.directory.ldapstudio.browser.core.jobs.AbstractEclipseJob;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;


public class RunnableContextJobAdapter
{

    public static void execute( final AbstractEclipseJob job )
    {
        execute( job, null );
    }


    public static void execute( final AbstractEclipseJob job, IRunnableContext runnableContext )
    {
        execute( job, runnableContext, true );
    }


    public static void execute( final AbstractEclipseJob job, IRunnableContext runnableContext, boolean handleError )
    {

        if ( runnableContext == null )
            runnableContext = new TimeTriggeredProgressMonitorDialog( Display.getDefault().getActiveShell(), 1000 );

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
            BrowserUIPlugin.getDefault().getExceptionHandler().handleException(
                new Status( IStatus.ERROR, BrowserUIPlugin.PLUGIN_ID, IStatus.ERROR, ex.getMessage(), ex ) );
        }

        if ( handleError && !job.getExternalResult().isOK() )
        {
            IStatus status = job.getExternalResult();
            BrowserUIPlugin.getDefault().getExceptionHandler().handleException( status );
        }

    }

}
