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


import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionFolder;
import org.apache.directory.studio.connection.ui.ConnectionUIConstants;
import org.apache.directory.studio.connection.ui.ConnectionUIPlugin;
import org.apache.directory.studio.connection.ui.dialogs.ConnectionFolderDialog;
import org.apache.directory.studio.connection.ui.dialogs.CredentialsDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;


/**
 * This Action launches the New Connection Folder Dialog.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class NewConnectionFolderAction extends StudioAction
{
    /**
     * Creates a new instance of NewConnectionFolderAction.
     */
    public NewConnectionFolderAction()
    {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        ConnectionFolderDialog dialog = new ConnectionFolderDialog(
            PlatformUI.getWorkbench().getDisplay().getActiveShell(),
            Messages.getString( "NewConnectionFolderAction.NewConnectionFolder" ), Messages.getString( "NewConnectionFolderAction.NeterNameNewFolder" ), "", null ); //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
        if ( dialog.open() == CredentialsDialog.OK )
        {
            String name = dialog.getValue();
            ConnectionFolder folder = new ConnectionFolder( name );
            ConnectionCorePlugin.getDefault().getConnectionFolderManager().addConnectionFolder( folder );

            ConnectionFolder[] folders = getSelectedConnectionFolders();
            if ( folders != null && folders.length > 0 )
            {
                folders[0].addSubFolderId( folder.getId() );
            }
            else
            {
                ConnectionCorePlugin.getDefault().getConnectionFolderManager().getRootConnectionFolder()
                    .addSubFolderId( folder.getId() );
            }
        }

    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return Messages.getString( "NewConnectionFolderAction.NewConnectionFolderDots" ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return ConnectionUIPlugin.getDefault().getImageDescriptor( ConnectionUIConstants.IMG_CONNECTION_FOLDER_ADD );
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
        return true;
    }
}
