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

package org.apache.directory.studio.ldapbrowser.ui.actions;


import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Utils;
import org.apache.directory.studio.ldapbrowser.common.dialogs.DnDialog;
import org.apache.directory.studio.ldapbrowser.common.dialogs.TextDialog;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.apache.directory.studio.ldapbrowser.ui.views.connection.ConnectionView;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;


/**
 * This action locates a Dn that the user entered into a dialog.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class GotoDnNavigateMenuAction extends LocateInDitAction
{
    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return Messages.getString( "GotoDnAction.GotoDN" ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_LOCATE_DN_IN_DIT );
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        return ( getSelectedConnection() != null );
    }


    /**
     * {@inheritDoc}
     */
    protected ConnectionAndDn getConnectionAndDn()
    {
        Connection selectedConnection = getSelectedConnection();

        if ( selectedConnection != null )
        {
            // Getting the browser connection associated with the connection
            IBrowserConnection connection = BrowserCorePlugin.getDefault().getConnectionManager()
                .getBrowserConnection( selectedConnection );

            // Getting the DN from the clipboard (if any)
            Dn dn = Utils.getLdapDn( getStringFromClipboard() );

            // Displaying the DN dialog
            DnDialog dialog = new DnDialog(
                getShell(),
                Messages.getString( "GotoDnAction.GotoDNAction" ), Messages.getString( "GotoDnAction.EnterDNAction" ), connection, dn ); //$NON-NLS-1$ //$NON-NLS-2$
            if ( dialog.open() == TextDialog.OK && dialog.getDn() != null )
            {
                dn = dialog.getDn();
                return new ConnectionAndDn( connection, dn );
            }
        }

        return null;
    }


    /**
     * Gets the currently selected connection.
     *
     * @return the currently selected connection
     */
    private Connection getSelectedConnection()
    {
        // Getting the connections view
        ConnectionView connectionView = ( ConnectionView ) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
            .getActivePage().findView( ConnectionView.getId() );

        if ( connectionView != null )
        {
            // Getting the selection of the connections view
            StructuredSelection selection = ( StructuredSelection ) connectionView.getMainWidget().getViewer()
                .getSelection();

            // Checking if only one object is selected
            if ( selection.size() == 1 )
            {
                Object selectedObject = selection.getFirstElement();

                // Checking if the selected object is a connection
                if ( selectedObject instanceof Connection )
                {
                    return ( Connection ) selectedObject;
                }
            }
        }

        return null;
    }


    /**
     * Gets the string from the clipboard.
     *
     * @return the string from the clipboard
     */
    private String getStringFromClipboard()
    {
        Clipboard clipboard = null;
        try
        {
            clipboard = new Clipboard( Display.getCurrent() );
            Object contents = clipboard.getContents( TextTransfer.getInstance() );
            if ( contents != null && contents instanceof String )
            {
                return ( String ) contents;
            }
        }
        finally
        {
            if ( clipboard != null )
            {
                clipboard.dispose();
            }
        }
        return null;
    }
}
