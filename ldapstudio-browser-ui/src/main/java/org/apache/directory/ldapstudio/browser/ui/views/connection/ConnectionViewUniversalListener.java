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

package org.apache.directory.ldapstudio.browser.ui.views.connection;


import org.apache.directory.ldapstudio.browser.core.jobs.OpenConnectionsJob;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.ui.actions.SelectionUtils;
import org.apache.directory.ldapstudio.browser.ui.views.browser.BrowserView;
import org.apache.directory.ldapstudio.browser.ui.widgets.connection.ConnectionUniversalListener;
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
 * @version $Rev$, $Date$
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
            IConnection[] connections = SelectionUtils.getConnections( event.getSelection() );
            if ( connections.length == 1 )
            {
                ensureBrowserViewVisible( connections[0] );
            }
        }
    };

    /** This listener opens/closes a connection when double clicking a connection */
    private IDoubleClickListener viewerDoubleClickListener = new IDoubleClickListener()
    {
        public void doubleClick( DoubleClickEvent event )
        {
            IConnection[] connections = SelectionUtils.getConnections( event.getSelection() );
            if ( connections.length == 1 )
            {
                toggleConnection( connections[0] );
            }
        }
    };


    /**
     * Creates a new instance of ConnectionViewUniversalListener.
     *
     * @param view
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
     * Ensures that the browser view is opended and ready to show the given selection.
     *
     * @param selection the view's selection.
     */
    private void ensureBrowserViewVisible( IConnection selection )
    {
        if ( this.view != null )
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
        }
    }


    /**
     * Opens a closed connections or closes an opened connection. 
     *
     * @param connection the connection
     */
    private void toggleConnection( IConnection connection )
    {
        if ( connection.isOpened() )
        {
            if ( connection.canClose() )
            {
                connection.close();
            }
        }
        else
        {
            OpenConnectionsJob ocj = new OpenConnectionsJob( connection );
            ocj.execute();
        }
    }

}
