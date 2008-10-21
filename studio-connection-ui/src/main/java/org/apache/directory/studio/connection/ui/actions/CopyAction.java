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
import org.apache.directory.studio.connection.ui.dnd.ConnectionTransfer;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;


/**
 * This class implements the Copy Action
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class CopyAction extends StudioAction
{
    private StudioActionProxy pasteActionProxy;


    /**
     * Creates a new instance of CopyAction.
     *
     * @param pasteActionProxy
     *      the associated Paste Action
     */
    public CopyAction( StudioActionProxy pasteActionProxy )
    {
        super();
        this.pasteActionProxy = pasteActionProxy;
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {

        // connection
        Connection[] connections = getConnections();
        if ( connections != null )
        {
            return connections.length > 1 ? "Copy Connections" : "Copy Connection";
        }

        return "Copy";
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor( ISharedImages.IMG_TOOL_COPY );
    }


    /**
     * {@inheritDoc}
     */
    public String getCommandId()
    {
        return IWorkbenchActionDefinitionIds.COPY;
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        Connection[] connections = getConnections();

        // connection
        if ( connections != null )
        {
            copyToClipboard( new Object[]
                { connections }, new Transfer[]
                { ConnectionTransfer.getInstance() } );
        }

        // update paste action
        if ( pasteActionProxy != null )
        {
            pasteActionProxy.updateAction();
        }
    }


    /**
     * Copies data to Clipboard
     *
     * @param data
     *      the data to be set in the clipboard
     * @param dataTypes
     *      the transfer agents that will convert the data to its platform specific format; 
     *      each entry in the data array must have a corresponding dataType
     */
    public static void copyToClipboard( Object[] data, Transfer[] dataTypes )
    {
        Clipboard clipboard = null;
        try
        {
            clipboard = new Clipboard( Display.getCurrent() );
            clipboard.setContents( data, dataTypes );
        }
        finally
        {
            if ( clipboard != null )
                clipboard.dispose();
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {

        // connection
        if ( getConnections() != null )
        {
            return true;
        }

        else
        {
            return false;
        }
    }


    /**
     * Get the Connections
     *
     * @return
     *      the Connections
     */
    private Connection[] getConnections()
    {

        if ( getSelectedConnections().length > 0 )
        {
            return getSelectedConnections();
        }
        else
        {
            return null;
        }
    }

}
