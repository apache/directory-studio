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


import org.apache.commons.lang.StringUtils;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.IConnectionListener;
import org.apache.directory.studio.connection.core.Messages;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;
import org.apache.directory.studio.connection.core.jobs.StudioConnectionBulkRunnableWithProgress;
import org.apache.directory.studio.connection.core.jobs.StudioConnectionRunnableWithProgress;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PlatformUI;


/**
 * This class provides some convenience methods to execute a runnable within
 * an {@link IRunnableContext}.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class RunnableContextRunner
{
    private RunnableContextRunner()
    {
        // Nothing to do
    }
    
    /**
     * Executes the given job within the given runnable context.
     * 
     * @param runnable the runnable to execute
     * @param runnableContext the runnable context or null to create a progress monitor dialog
     * @param handleError true to handle errors
     */
    public static IStatus execute( final StudioConnectionRunnableWithProgress runnable,
        IRunnableContext runnableContext, boolean handleError )
    {
        if ( runnableContext == null )
        {
            runnableContext = PlatformUI.getWorkbench().getProgressService();
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
                        if ( ( connection != null ) && !connection.getConnectionWrapper().isConnected() )
                        {
                            spm[0].setTaskName( Messages.bind( Messages.jobs__open_connections_task, new String[]
                                { connection.getName() } ) );
                            spm[0].worked( 1 );

                            connection.getConnectionWrapper().connect( spm[0] );

                            if ( connection.getConnectionWrapper().isConnected() )
                            {
                                connection.getConnectionWrapper().bind( spm[0] );
                            }

                            if ( connection.getConnectionWrapper().isConnected() )
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

                if ( !spm[0].errorsReported() )
                {
                    try
                    {
                        if ( runnable instanceof StudioConnectionBulkRunnableWithProgress )
                        {
                            StudioConnectionBulkRunnableWithProgress bulkRunnable = ( StudioConnectionBulkRunnableWithProgress ) runnable;
                            ConnectionEventRegistry.suspendEventFiringInCurrentThread();

                            try
                            {
                                bulkRunnable.run( spm[0] );
                            }
                            finally
                            {
                                ConnectionEventRegistry.resumeEventFiringInCurrentThread();
                            }

                            bulkRunnable.runNotification( spm[0] );
                        }
                        else
                        {
                            runnable.run( spm[0] );
                        }
                    }
                    catch ( Exception e )
                    {
                        spm[0].reportError( e );
                    }
                    finally
                    {
                        spm[0].done();
                        monitor.done();
                    }
                }
            }
        };

        try
        {
            runnableContext.run( true, true, runnableWithProgress );
        }
        catch ( Exception ex )
        {
            ConnectionUIPlugin
                .getDefault()
                .getExceptionHandler()
                .handleException(
                    new Status( IStatus.ERROR, ConnectionUIConstants.PLUGIN_ID, IStatus.ERROR,
                        ex.getMessage() != null ? ex.getMessage() : StringUtils.EMPTY, ex ) ); //$NON-NLS-1$
        }

        IStatus status = spm[0].getErrorStatus( runnable.getErrorMessage() );

        if ( ( handleError && !spm[0].isCanceled() ) && !status.isOK() )
        {
            ConnectionUIPlugin.getDefault().getExceptionHandler().handleException( status );
        }

        return status;
    }
}
