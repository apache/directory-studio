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

package org.apache.directory.ldapstudio.browser.view.views.wrappers;

import java.util.ArrayList;
import java.util.List;

import org.apache.directory.ldapstudio.browser.Activator;
import org.apache.directory.ldapstudio.browser.model.Connection;
import org.apache.directory.ldapstudio.browser.view.ImageKeys;
import org.apache.directory.ldapstudio.browser.view.views.BrowserView;
import org.apache.directory.shared.ldap.codec.LdapResponse;
import org.apache.directory.shared.ldap.codec.LdapResult;
import org.apache.directory.shared.ldap.codec.search.SearchResultEntry;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.apache.directory.ldapstudio.dsmlv2.Dsmlv2ResponseParser;
import org.apache.directory.ldapstudio.dsmlv2.engine.Dsmlv2Engine;
import org.apache.directory.ldapstudio.dsmlv2.reponse.ErrorResponse;
import org.apache.directory.ldapstudio.dsmlv2.reponse.SearchResponse;

/**
 * ConnectionWrapper used to display a Connection in the TreeViewer of the
 * Browser View
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ConnectionWrapper implements Comparable<ConnectionWrapper>,
	DisplayableTreeViewerElement {
    /**
         * This enum represents the different states of a ConnectionWrapper
         * 
         * @author <a href="mailto:dev@directory.apache.org">Apache Directory
         *         Project</a>
         */
    public enum ConnectionWrapperState {
	NONE, RUNNING, ERROR
    }

    /** The parent element */
    private Object parent;

    /** The children list */
    private List<EntryWrapper> children;

    /** The wrapped conneection */
    private Connection connection;

    /** The state of the ConnectionWrapper */
    private ConnectionWrapperState state;

    /** The DSML Engine used to connect to the server */
    private Dsmlv2Engine engine;

    /**
         * Creates a new instance of ConnectionWrapper.
         * 
         * @param connection
         *                the connection to wrap
         */
    public ConnectionWrapper(Connection connection) {
	this.connection = connection;
	this.state = ConnectionWrapperState.NONE;
    }

    /*
         * (non-Javadoc)
         * 
         * @see org.apache.directory.ldapstudio.browser.view.views.wrappers.DisplayableTreeViewerElement#getDisplayName()
         */
    public String getDisplayName() {
	return connection.getName();
    }

    /*
         * (non-Javadoc)
         * 
         * @see org.apache.directory.ldapstudio.browser.view.views.wrappers.DisplayableTreeViewerElement#getDisplayImage()
         */
    public Image getDisplayImage() {
	if (state == ConnectionWrapperState.NONE) {
	    return AbstractUIPlugin.imageDescriptorFromPlugin(
		    Activator.PLUGIN_ID, ImageKeys.CONNECTION).createImage();
	} else if (state == ConnectionWrapperState.RUNNING) {
	    return AbstractUIPlugin.imageDescriptorFromPlugin(
		    Activator.PLUGIN_ID, ImageKeys.CONNECTION_RUNNING)
		    .createImage();
	} else if (state == ConnectionWrapperState.ERROR) {
	    return AbstractUIPlugin.imageDescriptorFromPlugin(
		    Activator.PLUGIN_ID, ImageKeys.CONNECTION_ERROR)
		    .createImage();
	}

	return AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
		ImageKeys.CONNECTION).createImage();
    }

    /**
         * Gets the wrapped connection
         * 
         * @return the wrapped connection
         */
    public Connection getConnection() {
	return connection;
    }

    /*
         * (non-Javadoc)
         * 
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
    public int compareTo(ConnectionWrapper o) {
	ConnectionWrapper otherWrapper = (ConnectionWrapper) o;
	return getDisplayName().compareToIgnoreCase(
		otherWrapper.getDisplayName());
    }

    /**
         * Get parent object in the TreeViewer Hierarchy
         * 
         * @return the parent
         */
    public Object getParent() {
	return parent;
    }

    /**
         * Set the parent object in the TreeViewer Hierarchy
         * 
         * @param parent
         *                the parent element
         */
    public void setParent(Object parent) {
	this.parent = parent;
    }

    public Object[] getChildren() {
	if (children == null) {
	    children = new ArrayList<EntryWrapper>();

	    // Getting the Browser View
	    BrowserView browserView = (BrowserView) PlatformUI.getWorkbench()
		    .getActiveWorkbenchWindow().getActivePage().findView(
			    BrowserView.ID);

	    try {
		if (engine == null) {
		    // Initialization of the DSML Engine and the DSML
                        // Response Parser
		    if (connection.isAppendBaseDNtoUserDNWithBaseDN()) {
			engine = new Dsmlv2Engine(connection.getHost(),
				connection.getPort(), new LdapDN(connection
					.getUserDN().getNormName()
					+ ","
					+ connection.getBaseDN().getNormName())
					.getNormName(), connection
					.getPassword());
		    } else {
			engine = new Dsmlv2Engine(connection.getHost(),
				connection.getPort(), connection.getUserDN()
					.getNormName(), connection
					.getPassword());
		    }
		}

		Dsmlv2ResponseParser parser = new Dsmlv2ResponseParser();

		String request = "<batchRequest>"
			+ "	<searchRequest dn=\""
			+ connection.getBaseDN().getNormName()
			+ "\""
			+ "			scope=\"baseObject\" derefAliases=\"neverDerefAliases\">"
			+ "		<filter><present name=\"objectclass\"></present></filter>"
			+ "       <attributes>" + "			<attribute name=\"*\"/>"
			+ "			<attribute name=\"namingContexts\"/>"
			+ "			<attribute name=\"subSchemaSubEntry\"/>"
			+ "			<attribute name=\"altServer\"/>"
			+ "			<attribute name=\"supportedExtension\"/>"
			+ "			<attribute name=\"supportedControl\"/>"
			+ "			<attribute name=\"supportedSaslMechanism\"/>"
			+ "			<attribute name=\"supportedLdapVersion\"/>"
			+ "       </attributes>" + "	</searchRequest>"
			+ "</batchRequest>";

		// Executing the request and sending the result to the Response
                // Parser
		parser.setInput(engine.processDSML(request));
		parser.parse();

		LdapResponse ldapResponse = parser.getBatchResponse()
			.getCurrentResponse();

		if (ldapResponse instanceof SearchResponse) {
		    SearchResponse searchResponse = (SearchResponse) ldapResponse;

		    LdapResult ldapResult = searchResponse
			    .getSearchResultDone().getLdapResult();

		    if (ldapResult.getResultCode() == 0) {
			// Getting the Base DN
			SearchResultEntry baseDN = ((SearchResponse) ldapResponse)
				.getCurrentSearchResultEntry();

			EntryWrapper baseDNWrapper = new EntryWrapper(baseDN);
			baseDNWrapper.setParent(this);
			baseDNWrapper.setIsBaseDN(true);

			children.add(baseDNWrapper);

			setState(ConnectionWrapperState.RUNNING);
			browserView.getViewer().update(this, null);
		    } else {
			setState(ConnectionWrapperState.ERROR);
			clearChildren();
			browserView.getViewer().update(this, null);

			// Displaying an error
			MessageDialog.openError(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(),
				"Error !", "An error has ocurred.\n"
					+ ldapResult.getErrorMessage());
			return null;
		    }
		} else if (ldapResponse instanceof ErrorResponse) {
		    setState(ConnectionWrapperState.ERROR);
		    children = new ArrayList<EntryWrapper>(0);
		    browserView.getViewer().update(this, null);

		    ErrorResponse errorResponse = (ErrorResponse) ldapResponse;
		    // Displaying an error
		    MessageDialog.openError(PlatformUI.getWorkbench()
			    .getActiveWorkbenchWindow().getShell(), "Error !",
			    "An error has ocurred.\n"
				    + errorResponse.getMessage());

		    return null;
		}
	    } catch (Exception e) {
		// Displaying an error
		MessageDialog.openError(PlatformUI.getWorkbench()
			.getActiveWorkbenchWindow().getShell(), "Error !",
			"An error has ocurred.\n" + e.getMessage());
		return null;
	    }
	}

	return children.toArray(new Object[0]);
    }

    /**
         * Erases the Children List
         */
    public void clearChildren() {
	children = null;
    }

    /**
         * Erases the Children List and the Dsmlv2 Engine
         */
    public void connectionChanged() {
	clearChildren();
	engine = null;
    }

    /**
         * Sets the current state of the ConnectionWrapper
         * 
         * @param state
         *                the state to set
         */
    public void setState(ConnectionWrapperState state) {
	this.state = state;
    }

    /**
         * Gets the current state of the ConnectionWrapper
         * 
         * @return the state of the ConnectionWrapper
         */
    public ConnectionWrapperState getState() {
	return this.state;
    }

    /**
         * Gets the Dsmlv2Engine
         * 
         * @return the Dsmlv2Engine
         */
    public Dsmlv2Engine getDsmlv2Engine() {
	return this.engine;
    }
}
