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

package org.apache.directory.studio.ldapbrowser.ui.views.connection;


import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.jobs.OpenConnectionsRunnable;
import org.apache.directory.studio.connection.ui.actions.SelectionUtils;
import org.apache.directory.studio.connection.ui.widgets.ConnectionUniversalListener;
import org.apache.directory.studio.ldapbrowser.core.jobs.StudioBrowserJob;
import org.apache.directory.studio.ldapbrowser.ui.views.browser.BrowserView;
import org.apache.directory.studio.ldapbrowser.ui.views.modificationlogs.ModificationLogsView;
import org.apache.directory.studio.ldapbrowser.ui.views.searchlogs.SearchLogsView;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


/**
 * The ConnectionViewUniversalListener manages all events for the connection view.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ConnectionViewUniversalListener extends ConnectionUniversalListener
{

    /** The connection view */
    protected ConnectionView view;

    /** This listener is used to ensure that the browser view is opened
     when an object in the connection view is selected */
    private ISelectionChangedListener viewerSelectionListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            Connection[] connections = SelectionUtils.getConnections( event.getSelection() );
            if ( connections.length == 1 )
            {
                ensureViewVisible();
            }
        }
    };

    /** This listener opens/closes a connection when double clicking a connection */
    private IDoubleClickListener viewerDoubleClickListener = new IDoubleClickListener()
    {
        public void doubleClick( DoubleClickEvent event )
        {
            Connection[] connections = SelectionUtils.getConnections( event.getSelection() );
            if ( connections.length == 1 )
            {
                toggleConnection( connections[0] );
            }
        }
    };


    /**
     * Creates a new instance of ConnectionViewUniversalListener.
     *
     * @param view the connection view
     */
    public ConnectionViewUniversalListener( ConnectionView view )
    {
        super( view.getMainWidget().getViewer() );
        this.view = view;

        // listeners
        viewer.addSelectionChangedListener( viewerSelectionListener );
        viewer.addDoubleClickListener( viewerDoubleClickListener );
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        this.view = null;
        super.dispose();
    }


    /**
     * Ensures that the browser view and modification log views are opended 
     * and ready to show the given selection.
     *
     * @param selection the view's selection.
     */
    private void ensureViewVisible()
    {
        if ( view != null )
        {
            try
            {
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView( BrowserView.getId(),
                    null, IWorkbenchPage.VIEW_VISIBLE );
            }
            catch ( PartInitException e )
            {
            }
            catch ( NullPointerException e )
            {
            }

            try
            {
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
                    ModificationLogsView.getId(), null, IWorkbenchPage.VIEW_CREATE );
            }
            catch ( PartInitException e )
            {
            }
            catch ( NullPointerException e )
            {
            }

            try
            {
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView( SearchLogsView.getId(),
                    null, IWorkbenchPage.VIEW_CREATE );
            }
            catch ( PartInitException e )
            {
            }
            catch ( NullPointerException e )
            {
            }
        }
    }


    /**
     * Opens a closed connections or closes an opened connection. 
     *
     * @param connection the connection
     */
    private void toggleConnection( Connection connection )
    {
        if ( connection.getConnectionWrapper().isConnected() )
        {
            connection.getConnectionWrapper().disconnect();
        }
        else
        {
            new StudioBrowserJob( new OpenConnectionsRunnable( connection ) ).execute();
        }
    }

}
