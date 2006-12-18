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
import org.apache.directory.ldapstudio.browser.view.views.ConnectionWizard;
import org.apache.directory.ldapstudio.browser.view.views.ConnectionWizard.ConnectionWizardType;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * This class implements the Connection New Action
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ConnectionNewAction extends Action {
    private BrowserView view;

    public ConnectionNewAction(BrowserView view, String text) {
	super(text);
	setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
		Activator.PLUGIN_ID, ImageKeys.CONNECTION_NEW));
	setToolTipText("New connection");
	this.view = view;
    }

    public void run() {
	// Creating the new Connection
	Connection newConnection = new Connection();

	// Creating a new Connection Name with verification that a connection
        // with
	// the same name doesn't exist yet.
	String newConnectionString = "New Connection";
	String testString = newConnectionString;
	Connections connections = Connections.getInstance();

	int counter = 1;
	while (!connections.isConnectionNameAvailable(testString)) {
	    testString = newConnectionString + counter;
	    counter++;
	}
	newConnection.setName(testString);

	// Creating the Connection Wizard
	ConnectionWizard wizard = new ConnectionWizard();
	wizard.init(PlatformUI.getWorkbench(), StructuredSelection.EMPTY);
	wizard.setType(ConnectionWizardType.NEW);

	wizard.setConnection(newConnection);

	// Instantiates the wizard container with the wizard and opens it
	WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench()
		.getActiveWorkbenchWindow().getShell(), wizard);
	dialog.create();
	int result = dialog.open();

	// O is returned when "Finish" is clicked, 1 is returned when "Cancel"
        // is clicked
	if (result != 0) {
	    return;
	}

	// Adding the connection
	Connections.getInstance().addConnection(newConnection);
    }
}
