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

package org.apache.directory.studio.connection.ui;


import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.IConnectionListener;
import org.apache.directory.studio.connection.core.Messages;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;
import org.apache.directory.studio.connection.core.jobs.StudioBulkRunnableWithProgress;
import org.apache.directory.studio.connection.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.jobs.StudioRunnableWithProgress;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;


/**
 * This class provides some convenience methods to execute a runnable within
 * an {@link IRunnableContext}.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class RunnableContextRunner
{

    /**
     * Executes the given job within the given runnable context.
     * 
     * @param runnable the runnable to execute
     * @param runnableContext the runnable context or null to create a progress monitor dialog
     * @param handleError true to handle errors
     */
    public static IStatus execute( final StudioRunnableWithProgress runnable, IRunnableContext runnableContext,
        boolean handleError )
    {
        if ( runnableContext == null )
        {
            runnableContext = new ProgressMonitorDialog( Display.getDefault().getActiveShell() );
        }

        final StudioProgressMonitor[] spm = new StudioProgressMonitor[1];
        IRunnableWithProgress runnableWithProgress = new IRunnableWithProgress()
        {
            public void run( IProgressMonitor monitor ) throws InterruptedException
            {
                spm[0] = new StudioProgressMonitor( monitor );

                // ensure that connections are opened
                Connection[] connections = runnable.getConnections();
                if ( connections != null )
                {
                    for ( Connection connection : connections )
                    {
                        if ( connection != null && !connection.getJNDIConnectionWrapper().isConnected() )
                        {
                            spm[0].setTaskName( Messages.bind( Messages.jobs__open_connections_task, new String[]
                                { connection.getName() } ) );
                            spm[0].worked( 1 );

                            connection.getJNDIConnectionWrapper().connect( spm[0] );
                            connection.getJNDIConnectionWrapper().bind( spm[0] );

                            if ( connection.getJNDIConnectionWrapper().isConnected() )
                            {
                                for ( IConnectionListener listener : ConnectionCorePlugin.getDefault()
                                    .getConnectionListeners() )
                                {
                                    listener.connectionOpened( connection, spm[0] );
                                }
                                ConnectionEventRegistry.fireConnectionOpened( connection, this );
                            }
                        }
                    }
                }

                //runnable.run( spm[0] );
                if ( runnable instanceof StudioBulkRunnableWithProgress )
                {
                    StudioBulkRunnableWithProgress bulkRunnable = ( StudioBulkRunnableWithProgress ) runnable;
                    ConnectionEventRegistry.suspendEventFireingInCurrentThread();
                    try
                    {
                        bulkRunnable.run( spm[0] );
                    }
                    finally
                    {
                        ConnectionEventRegistry.resumeEventFireingInCurrentThread();
                    }
                    bulkRunnable.runNotification();
                }
                else
                {
                    runnable.run( spm[0] );
                }

                spm[0].done();
            }
        };

        try
        {
            runnableContext.run( true, true, runnableWithProgress );
        }
        catch ( Exception ex )
        {
            ConnectionUIPlugin.getDefault().getExceptionHandler().handleException(
                new Status( IStatus.ERROR, ConnectionUIConstants.PLUGIN_ID, IStatus.ERROR, ex.getMessage() != null ? ex
                    .getMessage() : "", ex ) );
        }

        IStatus status = spm[0].getErrorStatus( runnable.getErrorMessage() );
        if ( handleError && !spm[0].isCanceled() && !status.isOK() )
        {
            ConnectionUIPlugin.getDefault().getExceptionHandler().handleException( status );
        }

        return status;
    }

}
