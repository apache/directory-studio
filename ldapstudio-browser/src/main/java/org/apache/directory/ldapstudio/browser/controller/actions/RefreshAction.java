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
import org.apache.directory.ldapstudio.browser.view.ImageKeys;
import org.apache.directory.ldapstudio.browser.view.views.AttributesView;
import org.apache.directory.ldapstudio.browser.view.views.BrowserView;
import org.apache.directory.ldapstudio.browser.view.views.wrappers.ConnectionWrapper;
import org.apache.directory.ldapstudio.browser.view.views.wrappers.EntryWrapper;
import org.apache.directory.ldapstudio.dsmlv2.Dsmlv2ResponseParser;
import org.apache.directory.ldapstudio.dsmlv2.engine.Dsmlv2Engine;
import org.apache.directory.ldapstudio.dsmlv2.reponse.ErrorResponse;
import org.apache.directory.ldapstudio.dsmlv2.reponse.SearchResponse;
import org.apache.directory.shared.ldap.codec.LdapResponse;
import org.apache.directory.shared.ldap.codec.search.SearchResultEntry;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class implements the Refresh Action
 */
public class RefreshAction extends Action
{
    private BrowserView view;


    public RefreshAction( BrowserView view, String text )
    {
        super( text );
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, ImageKeys.REFRESH ) );
        setToolTipText( "Refresh" );
        this.view = view;
    }


    public void run()
    {
        TreeViewer viewer = view.getViewer();

        Object selection = ( ( TreeSelection ) viewer.getSelection() ).getFirstElement();

        // Clearing the children of the selected node
        if ( selection instanceof ConnectionWrapper )
        {
            ConnectionWrapper connectionWrapper = ( ConnectionWrapper ) selection;
            connectionWrapper.clearChildren();
        }
        else if ( selection instanceof EntryWrapper )
        {
            EntryWrapper entryWrapper = ( EntryWrapper ) selection;
            updateEntry( entryWrapper );
            entryWrapper.clearChildren();
        }

        // Refreshing the Browser View
        viewer.refresh( selection );
        viewer.setExpandedState( selection, true );

        // Refreshing the Attributes View
        AttributesView attributesView = ( AttributesView ) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
            .getActivePage().findView( AttributesView.ID );
        attributesView.refresh();
    }


    public void updateEntry( EntryWrapper entryWrapper )
    {
        try
        {
            // Initialization of the DSML Engine and the DSML Response Parser
            Dsmlv2Engine engine = entryWrapper.getDsmlv2Engine();
            Dsmlv2ResponseParser parser = new Dsmlv2ResponseParser();

            String request = "<batchRequest>" + "	<searchRequest dn=\""
                + entryWrapper.getEntry().getObjectName().getNormName() + "\""
                + "			scope=\"baseObject\" derefAliases=\"neverDerefAliases\">"
                + "		<filter><present name=\"objectclass\"></present></filter>" + "       <attributes>"
                + "			<attribute name=\"*\"/>" + "			<attribute name=\"namingContexts\"/>"
                + "			<attribute name=\"subSchemaSubEntry\"/>" + "			<attribute name=\"altServer\"/>"
                + "			<attribute name=\"supportedExtension\"/>" + "			<attribute name=\"supportedControl\"/>"
                + "			<attribute name=\"supportedSaslMechanism\"/>" + "			<attribute name=\"supportedLdapVersion\"/>"
                + "       </attributes>" + "	</searchRequest>" + "</batchRequest>";

            // Executing the request and sending the result to the Response Parser
            parser.setInput( engine.processDSML( request ) );
            parser.parse();

            LdapResponse ldapResponse = parser.getBatchResponse().getCurrentResponse();

            if ( ldapResponse instanceof ErrorResponse )
            {
                ErrorResponse errorResponse = ( ( ErrorResponse ) ldapResponse );

                // Displaying an error
                MessageDialog.openError( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error !",
                    "An error has ocurred.\n" + errorResponse.getMessage() );
                return;
            }
            else if ( ldapResponse instanceof SearchResponse )
            {

                // Getting the Search Result Entry List containing our objects for the response
                SearchResponse searchResponse = ( ( SearchResponse ) ldapResponse );

                SearchResultEntry sre = searchResponse.getSearchResultEntryList().get( 0 );

                entryWrapper.getEntry().setPartialAttributeList( sre.getPartialAttributeList() );
                return;
            }
        }
        catch ( Exception e )
        {
            // Displaying an error
            MessageDialog.openError( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error !",
                "An error has ocurred.\n" + e.getMessage() );
            return;
        }
    }
}
