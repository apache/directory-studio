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

package org.apache.directory.studio.connection.ui.actions;


import org.apache.directory.studio.connection.core.jobs.OpenConnectionsRunnable;
import org.apache.directory.studio.connection.core.jobs.StudioConnectionJob;
import org.apache.directory.studio.connection.ui.ConnectionUIConstants;
import org.apache.directory.studio.connection.ui.ConnectionUIPlugin;
import org.eclipse.jface.resource.ImageDescriptor;


/**
 * This Action opens a Connection.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OpenConnectionAction extends StudioAction
{
    /**
     * Creates a new instance of OpenConnectionAction.
     */
    public OpenConnectionAction()
    {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        new StudioConnectionJob( new OpenConnectionsRunnable( getSelectedConnections() ) ).execute();
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return getSelectedConnections().length > 1 ? Messages.getString( "OpenConnectionAction.OpenConnections" ) : Messages.getString( "OpenConnectionAction.OpenConnection" ); //$NON-NLS-1$ //$NON-NLS-2$
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return ConnectionUIPlugin.getDefault().getImageDescriptor( ConnectionUIConstants.IMG_CONNECTION_CONNECT );
    }


    /**
     * {@inheritDoc}
     */
    public String getCommandId()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        boolean canOpen = false;
        for ( int i = 0; i < getSelectedConnections().length; i++ )
        {
            if ( !getSelectedConnections()[i].getJNDIConnectionWrapper().isConnected() )
            {
                canOpen = true;
                break;
            }
        }
        return getSelectedConnections().length > 0 && canOpen;
    }
}
