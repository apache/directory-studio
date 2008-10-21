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


import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCoreConstants;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;


/**
 * This Action implements the Delete Action. It deletes Connections, Entries, Searches, Bookmarks, Attributes or Values.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DeleteAction extends StudioAction
{
    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        try
        {
            Connection[] connections = getConnections();

            if ( connections.length > 0 )
            {
                return connections.length > 1 ? "Delete Connections" : "Delete Connection";
            }
        }
        catch ( Exception e )
        {
        }

        return "Delete";
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor( ISharedImages.IMG_TOOL_DELETE );
    }


    /**
     * {@inheritDoc}
     */
    public String getCommandId()
    {
        return IWorkbenchActionDefinitionIds.DELETE;
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        try
        {
            Connection[] connections = getConnections();

            StringBuffer message = new StringBuffer();

            if ( connections.length > 0 )
            {
                if ( connections.length <= 5 )
                {
                    message.append( connections.length == 1 ? "Are your sure to delete the following connection?"
                        : "Are your sure to delete the following connections?" );
                    for ( int i = 0; i < connections.length; i++ )
                    {
                        message.append( ConnectionCoreConstants.LINE_SEPARATOR );
                        message.append( "  - " );
                        message.append( connections[i].getName() );
                    }
                }
                else
                {
                    message.append( "Are your sure to delete the selected connections?" );
                }
                message.append( ConnectionCoreConstants.LINE_SEPARATOR );
                message.append( ConnectionCoreConstants.LINE_SEPARATOR );
            }

            if ( message.length() == 0 || MessageDialog.openConfirm( getShell(), getText(), message.toString() ) )
            {

                if ( connections.length > 0 )
                {
                    deleteConnections( connections );
                }
            }
        }
        catch ( Exception e )
        {
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        try
        {
            Connection[] connections = getConnections();
            return connections.length > 0;
        }
        catch ( Exception e )
        {
            // e.printStackTrace();
            return false;
        }
    }


    /**
     * Gets the Connections 
     *
     * @return
     *      the Connections
     * @throws Exception
     *      when a is opened
     */
    protected Connection[] getConnections() throws Exception
    {
        for ( int i = 0; i < getSelectedConnections().length; i++ )
        {
            if ( getSelectedConnections()[i].getJNDIConnectionWrapper().isConnected() )
            {
                throw new Exception();
            }
        }

        return getSelectedConnections();
    }


    /**
     * Deletes Connections
     *
     * @param connections
     *      the Connections to delete
     */
    protected void deleteConnections( Connection[] connections )
    {
        for ( int i = 0; i < connections.length; i++ )
        {
            ConnectionCorePlugin.getDefault().getConnectionManager().removeConnection( connections[i] );
        }
    }
}
