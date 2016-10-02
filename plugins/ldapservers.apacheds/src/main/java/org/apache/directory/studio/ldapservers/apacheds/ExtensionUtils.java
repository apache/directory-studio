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

package org.apache.directory.studio.ldapservers.apacheds;


import org.apache.directory.studio.ldapservers.model.LdapServer;
import org.apache.directory.studio.ldapservers.views.ServersView;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;


/**
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ExtensionUtils
{
    private static final String EXTENSION_ID = "org.apache.directory.server.2.0.0"; //$NON-NLS-1$


    public static boolean verifyApacheDs200OrPrintError( LdapServer server, ServersView view )
    {
        // Checking that the server is really an ApacheDS 2.0.0 server
        if ( !EXTENSION_ID.equalsIgnoreCase( server.getLdapServerAdapterExtension().getId() ) )
        {
            String message = Messages.getString( "CreateConnectionAction.UnableReadServerConfiguration" ) //$NON-NLS-1$
                + "\n\n" //$NON-NLS-1$
                + Messages.getString( "CreateConnectionAction.NotA200Server" ); //$NON-NLS-1$

            reportErrorReadingServerConfiguration( view, message );
            return false;
        }
        else
        {
            return true;
        }
    }


    /**
     * Reports to the user an error message indicating the server 
     * configuration could not be read correctly.
     *
     * @param message
     *      the message
     */
    private static void reportErrorReadingServerConfiguration( ServersView view, String message )
    {
        MessageDialog dialog = new MessageDialog( view.getSite().getShell(),
            Messages.getString( "CreateConnectionAction.UnableReadServerConfiguration" ), //$NON-NLS-1$
            null, message, MessageDialog.ERROR, new String[]
            { IDialogConstants.OK_LABEL }, MessageDialog.OK );
        dialog.open();
    }
}
