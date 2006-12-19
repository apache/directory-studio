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
import org.apache.directory.ldapstudio.browser.ui.views.modificationlogs.ModificationLogsView;
import org.apache.directory.ldapstudio.browser.ui.widgets.connection.ConnectionUniversalListener;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;


public class ConnectionViewUniversalListener extends ConnectionUniversalListener implements ISelectionListener,
    IDoubleClickListener
{

    protected ConnectionView view;


    public ConnectionViewUniversalListener( ConnectionView view )
    {
        super( view.getMainWidget().getViewer() );
        this.view = view;

        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().addSelectionListener( this );
        viewer.addDoubleClickListener( this );
    }


    public void dispose()
    {
        if ( this.view != null )
        {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().removeSelectionListener( this );
            this.view = null;
        }
        if ( viewer != null )
        {
            viewer.removeDoubleClickListener( this );
        }
        super.dispose();
    }


    public void selectionChanged( IWorkbenchPart part, ISelection selection )
    {
        if ( this.view != null )
        {
            if ( part.getClass() == ConnectionView.class )
            {
                IConnection[] connections = SelectionUtils.getConnections( selection );
                if ( connections.length == 1 )
                {
                    BrowserView.setInput( connections[0] );
                    ModificationLogsView.setInput( connections[0] );
                }
                else
                {
                    BrowserView.setInput( null );
                    ModificationLogsView.setInput( null );
                }
            }
        }
    }


    public void doubleClick( DoubleClickEvent event )
    {
        if ( !event.getSelection().isEmpty() )
        {
            Object o = ( ( IStructuredSelection ) event.getSelection() ).getFirstElement();
            if ( o instanceof IConnection )
            {
                IConnection connection = ( IConnection ) o;
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
    }

}
