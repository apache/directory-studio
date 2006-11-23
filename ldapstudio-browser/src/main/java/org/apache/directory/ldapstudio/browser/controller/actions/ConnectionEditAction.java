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
import org.apache.directory.ldapstudio.browser.view.ImageKeys;
import org.apache.directory.ldapstudio.browser.view.views.BrowserView;
import org.apache.directory.ldapstudio.browser.view.views.ConnectionWizard;
import org.apache.directory.ldapstudio.browser.view.views.ConnectionWizard.ConnectionWizardType;
import org.apache.directory.ldapstudio.browser.view.views.wrappers.ConnectionWrapper;
import org.apache.directory.ldapstudio.browser.view.views.wrappers.ConnectionWrapper.ConnectionWrapperState;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class implements the Connection Edit Action
 */
public class ConnectionEditAction extends Action
{
    private BrowserView view;


    public ConnectionEditAction( BrowserView view, String text )
    {
        super( text );
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, ImageKeys.CONNECTION_EDIT ) );
        setToolTipText( "Edit connection" );
        this.view = view;
    }


    public void run()
    {
        // Getting the selected connection
        ConnectionWrapper connectionWrapper = ( ConnectionWrapper ) ( ( TreeSelection ) view.getViewer().getSelection() )
            .getFirstElement();
        Connection selectedConnection = connectionWrapper.getConnection();

        // Creating the Connection Wizard
        ConnectionWizard wizard = new ConnectionWizard();
        wizard.init( PlatformUI.getWorkbench(), StructuredSelection.EMPTY );
        wizard.setType( ConnectionWizardType.EDIT );

        wizard.setConnection( selectedConnection );

        // Instantiates the wizard container with the wizard and opens it
        WizardDialog dialog = new WizardDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard );
        dialog.create();
        int result = dialog.open();

        // O is returned when "Finish" is clicked, 1 is returned when "Cancel" is clicked
        if ( result != 0 )
        {
            return;
        }

        // Updating the state of the Connection since it has changed (this causes the icon to change)
        connectionWrapper.setState( ConnectionWrapperState.NONE );
        connectionWrapper.connectionChanged();

        selectedConnection.notifyListeners();
    }
}
