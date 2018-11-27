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

package org.apache.directory.studio.connection.core.jobs;


import java.util.List;

import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.IConnectionListener;
import org.apache.directory.studio.connection.core.Messages;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;


/**
 * Runnable to close a connection to a directory server.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CloseConnectionsRunnable implements StudioConnectionBulkRunnableWithProgress
{

    private Connection[] connections;


    /**
     * Creates a new instance of CloseConnectionsJob.
     * 
     * @param connection the connection
     */
    public CloseConnectionsRunnable( Connection connection )
    {
        this( new Connection[]
            { connection } );
    }


    /**
     * Creates a new instance of CloseConnectionsJob.
     * 
     * @param connections the connections
     */
    public CloseConnectionsRunnable( Connection[] connections )
    {
        this.connections = connections;
    }


    /**
     * Creates a new instance of CloseConnectionsJob.
     * 
     * @param connections the connections
     */
    public CloseConnectionsRunnable( List<Connection> connections )
    {
        this.connections = connections.toArray( new Connection[connections.size()] );
    }


    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return connections.length == 1 ? Messages.jobs__close_connections_name_1
            : Messages.jobs__close_connections_name_n;
    }


    /**
     * {@inheritDoc}
     */
    public Object[] getLockedObjects()
    {
        return connections;
    }


    /**
     * {@inheritDoc}
     */
    public String getErrorMessage()
    {
        return connections.length == 1 ? Messages.jobs__close_connections_error_1
            : Messages.jobs__close_connections_error_n;
    }


    /**
     * {@inheritDoc}
     */
    public void run( StudioProgressMonitor monitor )
    {
        monitor.beginTask( " ", connections.length * 6 + 1 ); //$NON-NLS-1$
        monitor.reportProgress( " " ); //$NON-NLS-1$

        for ( Connection connection : connections )
        {
            if ( connection.getConnectionWrapper().isConnected() )
            {
                monitor.setTaskName( Messages.bind( Messages.jobs__close_connections_task, new String[]
                    { connection.getName() } ) );
                monitor.worked( 1 );

                connection.getConnectionWrapper().unbind();
                connection.getConnectionWrapper().disconnect();
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void runNotification( StudioProgressMonitor monitor )
    {
        for ( Connection connection : connections )
        {
            if ( !connection.getConnectionWrapper().isConnected() )
            {
                for ( IConnectionListener listener : ConnectionCorePlugin.getDefault().getConnectionListeners() )
                {
                    listener.connectionClosed( connection, monitor );
                }
            }
        }

        for ( Connection connection : connections )
        {
            if ( !connection.getConnectionWrapper().isConnected() )
            {
                ConnectionEventRegistry.fireConnectionClosed( connection, this );
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public Connection[] getConnections()
    {
        return null;
    }
}
