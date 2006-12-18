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

import java.util.Iterator;
import java.util.List;

import org.apache.directory.ldapstudio.browser.Activator;
import org.apache.directory.ldapstudio.browser.view.ImageKeys;
import org.apache.directory.ldapstudio.browser.view.views.BrowserView;
import org.apache.directory.ldapstudio.browser.view.views.wrappers.EntryWrapper;
import org.apache.directory.ldapstudio.dsmlv2.Dsmlv2ResponseParser;
import org.apache.directory.ldapstudio.dsmlv2.engine.Dsmlv2Engine;
import org.apache.directory.ldapstudio.dsmlv2.reponse.ErrorResponse;
import org.apache.directory.ldapstudio.dsmlv2.reponse.SearchResponse;
import org.apache.directory.shared.ldap.codec.LdapResponse;
import org.apache.directory.shared.ldap.codec.del.DelResponse;
import org.apache.directory.shared.ldap.codec.search.SearchResultEntry;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * This class implements the Entry Delete Action
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EntryDeleteAction extends Action {
    private BrowserView view;

    public EntryDeleteAction(BrowserView view, String text) {
	super(text);
	setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
		Activator.PLUGIN_ID, ImageKeys.ENTRY_DELETE));
	setToolTipText("Delete entry");
	this.view = view;
    }

    public void run() {
	boolean answer = MessageDialog
		.openConfirm(PlatformUI.getWorkbench()
			.getActiveWorkbenchWindow().getShell(), "Confirm",
			"Are you sure you want to delete the selected entry, including its children?");
	if (!answer) {
	    // If the user clicks on the "Cancel" button, we return...
	    return;
	}

	try {
	    // Getting the selected items
	    StructuredSelection selection = (StructuredSelection) view
		    .getViewer().getSelection();
	    Iterator items = selection.iterator();

	    while (items.hasNext()) {
		EntryWrapper entryWrapper = (EntryWrapper) items.next();

		// Initialization of the DSML Engine and the DSML Response
		// Parser
		Dsmlv2Engine engine = entryWrapper.getDsmlv2Engine();
		Dsmlv2ResponseParser parser = new Dsmlv2ResponseParser();

		String searchRequest = "<batchRequest>"
			+ "   <searchRequest dn=\""
			+ entryWrapper.getEntry().getObjectName().getNormName()
			+ "\""
			+ "          scope=\"wholeSubtree\" derefAliases=\"neverDerefAliases\">"
			+ "     <filter><present name=\"objectclass\"></present></filter>"
			+ "       <attributes>"
			+ "         <attribute name=\"1.1\"/>"
			+ "       </attributes>" + "    </searchRequest>"
			+ "</batchRequest>";

		// Executing the request and sending the result to the Response
		// Parser
		parser.setInput(engine.processDSML(searchRequest));
		parser.parse();

		LdapResponse ldapResponse = parser.getBatchResponse()
			.getCurrentResponse();

		if (ldapResponse instanceof ErrorResponse) {
		    ErrorResponse errorResponse = ((ErrorResponse) ldapResponse);

		    // Displaying an error
		    MessageDialog.openError(PlatformUI.getWorkbench()
			    .getActiveWorkbenchWindow().getShell(), "Error !",
			    "An error has ocurred.\n"
				    + errorResponse.getMessage());
		    return;
		} else if (ldapResponse instanceof SearchResponse) {

		    // Getting the Search Result Entry List containing our
		    // objects for the response
		    SearchResponse searchResponse = ((SearchResponse) ldapResponse);
		    List<SearchResultEntry> sreList = searchResponse
			    .getSearchResultEntryList();

		    String deleteRequest = "<batchRequest>";
		    for (int i = sreList.size() - 1; i >= 0; i--) {
			deleteRequest += "<delRequest dn=\""
				+ sreList.get(i).getObjectName() + "\"/>\n";
		    }
		    deleteRequest += "</batchRequest>";

		    // Executing the request and sending the result to the
		    // Response Parser
		    parser.setInput(engine.processDSML(deleteRequest));
		    parser.parse();

		    ldapResponse = parser.getBatchResponse()
			    .getCurrentResponse();

		    if (ldapResponse instanceof ErrorResponse) {
			ErrorResponse errorResponse = ((ErrorResponse) ldapResponse);

			// Displaying an error
			MessageDialog.openError(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(),
				"Error !", "An error has ocurred.\n"
					+ errorResponse.getMessage());
			return;
		    } else if (ldapResponse instanceof DelResponse) {
			DelResponse delResponse = (DelResponse) ldapResponse;

			if (delResponse.getLdapResult().getResultCode() == 0) {
			    view.getViewer().remove(entryWrapper);
			} else {
			    // Displaying an error
			    MessageDialog.openError(PlatformUI.getWorkbench()
				    .getActiveWorkbenchWindow().getShell(),
				    "Error !", "An error has ocurred.\n"
					    + delResponse.getLdapResult()
						    .getErrorMessage());
			}
		    }
		}
	    }
	} catch (Exception e) {
	    // Displaying an error
	    MessageDialog.openError(PlatformUI.getWorkbench()
		    .getActiveWorkbenchWindow().getShell(), "Error !",
		    "An error has ocurred.\n" + e.getMessage());
	    return;
	}
    }
}
