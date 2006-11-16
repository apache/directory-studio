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


import javax.naming.directory.Attributes;

import org.apache.directory.ldapstudio.browser.Activator;
import org.apache.directory.ldapstudio.browser.model.Connection;
import org.apache.directory.ldapstudio.browser.view.ImageKeys;
import org.apache.directory.ldapstudio.browser.view.views.AttributesView;
import org.apache.directory.ldapstudio.browser.view.views.BrowserView;
import org.apache.directory.ldapstudio.browser.view.views.wrappers.EntryWrapper;
import org.apache.directory.shared.ldap.codec.LdapResponse;
import org.apache.directory.shared.ldap.codec.modify.ModifyResponse;
import org.apache.directory.shared.ldap.codec.search.SearchResultEntry;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.apache.directory.ldapstudio.dsmlv2.Dsmlv2ResponseParser;
import org.apache.directory.ldapstudio.dsmlv2.engine.Dsmlv2Engine;
import org.apache.directory.ldapstudio.dsmlv2.reponse.ErrorResponse;


/**
 * This class implements the Attribute Delete Action.
 */
public class AttributeDeleteAction extends Action
{
    private AttributesView view;


    public AttributeDeleteAction( AttributesView view, String text )
    {
        super( text );
        setImageDescriptor( AbstractUIPlugin
            .imageDescriptorFromPlugin( Activator.PLUGIN_ID, ImageKeys.ATTRIBUTE_DELETE ) );
        setToolTipText( "Delete attribute" );
        this.view = view;
    }


    public void run()
    {
        try
        {
            // Getting the selection for the Attributes View
            TableItem item = view.getSelectedAttributeTableItem();

            String selectedAttribute = item.getText( 0 );
            String selectedValue = item.getText( 1 );

            // Getting the selected Entry in the Browser View
            BrowserView browserView = ( BrowserView ) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getActivePage().findView( BrowserView.ID );
            EntryWrapper entryWrapper = ( EntryWrapper ) ( ( TreeSelection ) browserView.getViewer().getSelection() )
                .getFirstElement();
            SearchResultEntry entry = entryWrapper.getEntry();
            Connection connection = entryWrapper.getConnection();

            // Initialization of the DSML Engine and the DSML Response Parser
            Dsmlv2Engine engine = new Dsmlv2Engine( connection.getHost(), connection.getPort(), connection.getUserDN()
                .getNormName(), connection.getPassword() );
            Dsmlv2ResponseParser parser = new Dsmlv2ResponseParser();

            String request = "<batchRequest>" + "	<modifyRequest dn=\""
                + entry.getObjectName().getNormName().toString() + "\">" + "		<modification name=\""
                + selectedAttribute + "\" operation=\"delete\">" + "       	<value>" + selectedValue + "</value>"
                + "       </modification>" + "	</modifyRequest>" + "</batchRequest>";

            parser.setInput( engine.processDSML( request ) );
            parser.parse();

            LdapResponse ldapResponse = parser.getBatchResponse().getCurrentResponse();

            if ( ldapResponse instanceof ModifyResponse )
            {
                ModifyResponse modifyResponse = ( ModifyResponse ) ldapResponse;

                if ( modifyResponse.getLdapResult().getResultCode() == 0 )
                {
                    // Removing the selected attribute value
                    Attributes attributes = entry.getPartialAttributeList();
                    attributes.get( selectedAttribute ).remove( selectedValue );

                    // refreshing the UI
                    view.setInput( entryWrapper );
                    view.resizeColumsToFit();
                }
                else
                {
                    // Displaying an error
                    MessageDialog.openError( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        "Error !", "An error has ocurred.\n" + modifyResponse.getLdapResult().getErrorMessage() );
                }
            }
            else if ( ldapResponse instanceof ErrorResponse )
            {
                ErrorResponse errorResponse = ( ErrorResponse ) ldapResponse;

                // Displaying an error
                MessageDialog.openError( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error !",
                    "An error has ocurred.\n" + errorResponse.getMessage() );
            }
        }
        catch ( Exception e )
        {
            // Displaying an error
            MessageDialog.openError( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error !",
                "An error has ocurred.\n" + e.getMessage() );
        }
    }
}
