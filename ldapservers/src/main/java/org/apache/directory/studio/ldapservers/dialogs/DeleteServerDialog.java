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

package org.apache.directory.studio.ldapservers.dialogs;


import org.apache.directory.studio.ldapservers.model.LdapServer;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;


/**
 * Dialog that prompts a user to delete server(s) and/or server configuration(s).
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DeleteServerDialog extends MessageDialog
{
    protected LdapServer server;


    /**
     * Creates a new DeleteServerDialog.
     * 
     * @param parentShell a shell
     * @param server
     *      the server
     */
    public DeleteServerDialog( Shell parentShell, LdapServer server )
    {
        super( parentShell, Messages.getString( "DeleteServerDialog.DeleteServer" ), null, null, QUESTION, new String[] //$NON-NLS-1$
            { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, OK );

        if ( server == null )
        {
            throw new IllegalArgumentException();
        }

        this.server = server;
        message = NLS.bind( Messages.getString( "DeleteServerDialog.SureToDelete" ), server.getName() ); //$NON-NLS-1$
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.MessageDialog#buttonPressed(int)
     */
    protected void buttonPressed( int buttonId )
    {
        super.buttonPressed( buttonId );
    }
}