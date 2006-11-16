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

package org.apache.directory.ldapstudio.browser.controller.actions;


import org.apache.directory.ldapstudio.browser.Activator;
import org.apache.directory.ldapstudio.browser.model.Connection;
import org.apache.directory.ldapstudio.browser.model.Connections;
import org.apache.directory.ldapstudio.browser.view.ImageKeys;
import org.apache.directory.ldapstudio.browser.view.views.BrowserView;
import org.apache.directory.ldapstudio.browser.view.views.wrappers.ConnectionWrapper;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class implements the Connection Delete Action.
 */
public class ConnectionDeleteAction extends Action
{
    private BrowserView view;


    public ConnectionDeleteAction( BrowserView view, String text )
    {
        super( text );
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
            ImageKeys.CONNECTION_DELETE ) );
        setToolTipText( "Delete connection" );
        this.view = view;
    }


    public void run()
    {
        // Getting the selected connection
        ConnectionWrapper connectionWrapper = ( ConnectionWrapper ) ( ( TreeSelection ) view.getViewer().getSelection() )
            .getFirstElement();
        Connection selectedConnection = connectionWrapper.getConnection();

        boolean answer = MessageDialog.openConfirm( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            "Confirm", "Are you sure you want to delete the connection \"" + selectedConnection.getName() + "\"?" );

        if ( !answer )
        {
            // If the user clicks on the "Cancel" button, we return...
            return;
        }

        // Removing the connection
        Connections.getInstance().removeConnection( selectedConnection );
    }
}
