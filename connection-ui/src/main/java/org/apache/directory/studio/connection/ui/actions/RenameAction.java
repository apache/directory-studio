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
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionFolder;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;


/**
 * This Action renames Connections, Entries, Searches, or Bookmarks.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class RenameAction extends StudioAction
{
    /**
     * Creates a new instance of RenameAction.
     *
     */
    public RenameAction()
    {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        Connection[] connections = getSelectedConnections();
        ConnectionFolder[] connectionFolders = getSelectedConnectionFolders();
        if ( connections.length == 1 && connectionFolders.length == 0 )
        {
            return Messages.getString( "RenameAction.Connection" ); //$NON-NLS-1$
        }
        else if ( connectionFolders.length == 1 && connections.length == 0 )
        {
            return Messages.getString( "RenameAction.ConnectionFolder" ); //$NON-NLS-1$
        }
        else
        {
            return Messages.getString( "RenameAction.Rename" ); //$NON-NLS-1$
        }
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public String getCommandId()
    {
        return IWorkbenchActionDefinitionIds.RENAME;
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        Connection[] connections = getSelectedConnections();
        ConnectionFolder[] connectionFolders = getSelectedConnectionFolders();
        if ( connections.length == 1 )
        {
            renameConnection( connections[0] );
        }
        else if ( connectionFolders.length == 1 )
        {
            renameConnectionFolder( connectionFolders[0] );
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        return getSelectedConnections().length + getSelectedConnectionFolders().length == 1;
    }


    /**
     * Renames a Connection.
     *
     * @param connection
     *      the Connection to rename
     */
    private void renameConnection( final Connection connection )
    {
        IInputValidator validator = new IInputValidator()
        {
            public String isValid( String newName )
            {
                if ( connection.getName().equals( newName ) )
                {
                    return null;
                }
                else if ( ConnectionCorePlugin.getDefault().getConnectionManager().getConnectionByName( newName ) != null )
                {
                    return Messages.getString( "RenameAction.ConnectionAlreadyExists" ); //$NON-NLS-1$
                }
                else
                {
                    return null;
                }
            }
        };

        InputDialog dialog = new InputDialog(
            getShell(),
            Messages.getString( "RenameAction.RenameConnection" ), Messages.getString( "RenameAction.NewNameConnection" ), connection.getName(), //$NON-NLS-1$ //$NON-NLS-2$
            validator );

        dialog.open();
        String newName = dialog.getValue();
        if ( newName != null )
        {
            connection.setName( newName );
        }
    }


    /**
     * Renames a ConnectionFolder.
     *
     * @param connectionFolder
     *      the ConnectionFolder to rename
     */
    private void renameConnectionFolder( final ConnectionFolder connectionFolder )
    {
        IInputValidator validator = new IInputValidator()
        {
            public String isValid( String newName )
            {
                if ( connectionFolder.getName().equals( newName ) )
                {
                    return null;
                }
                else if ( ConnectionCorePlugin.getDefault().getConnectionFolderManager().getConnectionFolderByName(
                    newName ) != null )
                {
                    return Messages.getString( "RenameAction.ConnectionFolderAlreadyExists" ); //$NON-NLS-1$
                }
                else
                {
                    return null;
                }
            }
        };

        InputDialog dialog = new InputDialog(
            getShell(),
            Messages.getString( "RenameAction.RenameConnectionFolder" ), Messages.getString( "RenameAction.NewNameConnectionFolder" ), connectionFolder.getName(), //$NON-NLS-1$ //$NON-NLS-2$
            validator );

        dialog.open();
        String newName = dialog.getValue();
        if ( newName != null )
        {
            connectionFolder.setName( newName );
        }
    }

}
