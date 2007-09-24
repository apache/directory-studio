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
import org.apache.directory.studio.connection.ui.dnd.ConnectionTransfer;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;


/**
 * This class implements the Paste Action.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class PasteAction extends StudioAction
{
    /**
     * Creates a new instance of PasteAction.
     */
    public PasteAction()
    {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        // connection
        Connection[] connections = getConnectionsToPaste();
        if ( connections != null )
        {
            return connections.length > 1 ? "Paste Connections" : "Paste Connection";
        }

        return "Paste";
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor( ISharedImages.IMG_TOOL_PASTE );
    }


    /**
     * {@inheritDoc}
     */
    public String getCommandId()
    {
        return IWorkbenchActionDefinitionIds.PASTE;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        // connection
        if ( getConnectionsToPaste() != null )
        {
            return true;
        }

        return false;
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        // connection
        Connection[] connections = getConnectionsToPaste();
        if ( connections != null )
        {
            for ( int i = 0; i < connections.length; i++ )
            {
                Connection newConnection = ( Connection ) connections[i].clone();
                ConnectionCorePlugin.getDefault().getConnectionManager().addConnection( newConnection );
                ConnectionFolder[] folders = getSelectedConnectionFolders();
                if(folders != null && folders.length > 0)
                {
                    folders[0].addConnectionId( newConnection.getId() );
                }
                else
                {
                    ConnectionCorePlugin.getDefault().getConnectionFolderManager().getRootConnectionFolder()
                        .addConnectionId( newConnection.getId() );
                }
            }
            return;
        }
    }


    /**
     * Condition: there are connections in clipboard
     * 
     * @return the connections to paste
     */
    private Connection[] getConnectionsToPaste()
    {
        Object content = this.getFromClipboard( ConnectionTransfer.getInstance() );
        if ( content != null && content instanceof Connection[] )
        {
            Connection[] connections = ( Connection[] ) content;
            return connections;
        }

        return null;
    }


    /**
     * Retrieve the data of the specified type currently available on the system clipboard.
     *
     * @param dataType
     *      the transfer agent for the type of data being requested
     * @return
     *      the data obtained from the clipboard or null if no data of this type is available
     */
    protected Object getFromClipboard( Transfer dataType )
    {
        Clipboard clipboard = null;
        try
        {
            clipboard = new Clipboard( Display.getCurrent() );
            return clipboard.getContents( dataType );
        }
        finally
        {
            if ( clipboard != null )
                clipboard.dispose();
        }
    }
}
