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


import org.apache.directory.api.ldap.extras.extended.pwdModify.PasswordModifyRequest;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.ldapbrowser.common.actions.BrowserAction;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.ui.dialogs.PasswordModifyExtendedOperationDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;


/**
 * This Action opens the password modify extended operation dialog.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PasswordModifyExtendedOperationAction extends BrowserAction
{
    public PasswordModifyExtendedOperationAction()
    {
        super();
    }


    public void run()
    {
        ConnectionAndEntry connectionAndDn = getConnectionAndEntry();
        PasswordModifyExtendedOperationDialog passwordDialog = new PasswordModifyExtendedOperationDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getShell(),
            connectionAndDn.connection, connectionAndDn.entry );
        passwordDialog.open();
    }


    private ConnectionAndEntry getConnectionAndEntry()
    {
        if ( getSelectedEntries().length > 0 )
        {
            return new ConnectionAndEntry( getSelectedEntries()[0].getBrowserConnection(),
                getSelectedEntries()[0] );
        }
        else if ( getSelectedSearchResults().length > 0 )
        {
            return new ConnectionAndEntry( getSelectedSearchResults()[0].getEntry().getBrowserConnection(),
                getSelectedSearchResults()[0].getEntry() );
        }
        else if ( getSelectedBookmarks().length > 0 )
        {
            return new ConnectionAndEntry( getSelectedBookmarks()[0].getEntry().getBrowserConnection(),
                getSelectedBookmarks()[0].getEntry() );
        }
        else if ( getSelectedConnections().length > 0 )
        {
            Connection connection = getSelectedConnections()[0];
            IBrowserConnection browserConnection = BrowserCorePlugin.getDefault().getConnectionManager()
                .getBrowserConnection( connection );
            return new ConnectionAndEntry( browserConnection, null );
        }
        else if ( getInput() instanceof IBrowserConnection )
        {
            return new ConnectionAndEntry( ( IBrowserConnection ) getInput(), null );
        }

        return null;
    }

    protected class ConnectionAndEntry
    {
        private IBrowserConnection connection;
        private IEntry entry;


        protected ConnectionAndEntry( IBrowserConnection connection, IEntry entry )
        {
            this.connection = connection;
            this.entry = entry;
        }
    }


    public String getText()
    {
        return Messages.getString( "PasswordModifyExtendedOperationAction.Text" ); //$NON-NLS-1$
    }


    public ImageDescriptor getImageDescriptor()
    {
        return null;
    }


    public String getCommandId()
    {
        return null;
    }


    public boolean isEnabled()
    {
        return getConnectionAndEntry() != null
            && getConnectionAndEntry().connection.getRootDSE()
                .isExtensionSupported( PasswordModifyRequest.EXTENSION_OID );
    }

}
