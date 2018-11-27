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

package org.apache.directory.studio.connection.ui.widgets;


import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionFolder;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;
import org.apache.directory.studio.connection.core.event.ConnectionUpdateListener;
import org.apache.directory.studio.connection.ui.ConnectionUIPlugin;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;


/**
 * The ConnectionUniversalListener manages all events for the connection widget.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ConnectionUniversalListener implements ConnectionUpdateListener
{
    /** The tree viewer */
    protected TreeViewer viewer;

    /** This listener expands/collapses a connection folder when double clicking */
    private IDoubleClickListener viewerDoubleClickListener = event ->
    {
        if ( event.getSelection() instanceof IStructuredSelection )
        {
            Object obj = ( ( IStructuredSelection ) event.getSelection() ).getFirstElement();

            if ( obj instanceof ConnectionFolder )
            {
                if ( viewer.getExpandedState( obj ) )
                {
                    viewer.collapseToLevel( obj, 1 );
                }
                else if ( ( ( ITreeContentProvider ) viewer.getContentProvider() ).hasChildren( obj ) )
                {
                    viewer.expandToLevel( obj, 1 );
                }
            }
        }
    };


    /**
     * Creates a new instance of ConnectionUniversalListener.
     *
     * @param viewer the tree viewer
     */
    public ConnectionUniversalListener( TreeViewer viewer )
    {
        this.viewer = viewer;

        this.viewer.addDoubleClickListener( viewerDoubleClickListener );
        ConnectionEventRegistry.addConnectionUpdateListener( this, ConnectionUIPlugin.getDefault().getEventRunner() );
    }


    /**
     * Disposes this universal listener.
     */
    public void dispose()
    {
        if ( viewer != null )
        {
            ConnectionEventRegistry.removeConnectionUpdateListener( this );
            viewer = null;
        }
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#
     *          connectionUpdated(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionUpdated( Connection connection )
    {
        if ( viewer != null )
        {
            viewer.refresh();
        }
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#
     *          connectionAdded(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionAdded( Connection connection )
    {
        connectionUpdated( connection );

        if ( viewer != null )
        {
            viewer.setSelection( new StructuredSelection( connection ), true );
        }
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#
     *          connectionRemoved(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionRemoved( Connection connection )
    {
        connectionUpdated( connection );
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#
     *          connectionOpened(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionOpened( Connection connection )
    {
        connectionUpdated( connection );
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#
     *          connectionClosed(org.apache.directory.studio.connection.core.Connection)
     */
    public void connectionClosed( Connection connection )
    {
        connectionUpdated( connection );
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#
     *          connectionFolderModified(org.apache.directory.studio.connection.core.ConnectionFolder)
     */
    public void connectionFolderModified( ConnectionFolder connectionFolder )
    {
        connectionUpdated( null );
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#
     *          connectionFolderAdded(org.apache.directory.studio.connection.core.ConnectionFolder)
     */
    public void connectionFolderAdded( ConnectionFolder connectionFolder )
    {
        connectionUpdated( null );
        if ( viewer != null )
        {
            viewer.setSelection( new StructuredSelection( connectionFolder ), true );
        }
    }


    /**
     * @see org.apache.directory.studio.connection.core.event.ConnectionUpdateListener#
     *          connectionFolderRemoved(org.apache.directory.studio.connection.core.ConnectionFolder)
     */
    public void connectionFolderRemoved( ConnectionFolder connectionFolder )
    {
        connectionUpdated( null );
    }
}
