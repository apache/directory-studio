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

package org.apache.directory.studio.ldapbrowser.common;


import org.apache.directory.studio.ldapbrowser.common.dialogs.CredentialsDialog;
import org.apache.directory.studio.ldapbrowser.core.internal.model.Credentials;
import org.apache.directory.studio.ldapbrowser.core.model.ConnectionParameter;
import org.apache.directory.studio.ldapbrowser.core.model.IAuthHandler;
import org.apache.directory.studio.ldapbrowser.core.model.ICredentials;

import org.eclipse.ui.PlatformUI;


public class BrowserCommonAuthHandler implements IAuthHandler
{

    public ICredentials getCredentials( final ConnectionParameter connectionParameter )
    {

        if ( connectionParameter.getBindPrincipal() == null || "".equals( connectionParameter.getBindPrincipal() ) )
        {
            return new Credentials( "", "", connectionParameter );
        }
        else if ( connectionParameter.getBindPassword() != null && !"".equals( connectionParameter.getBindPassword() ) )
        {
            return new Credentials( connectionParameter.getBindPrincipal(), connectionParameter.getBindPassword(),
                connectionParameter );
        }
        else
        {
            final String[] pw = new String[1];
            PlatformUI.getWorkbench().getDisplay().syncExec( new Runnable()
            {
                public void run()
                {
                    CredentialsDialog dialog = new CredentialsDialog( PlatformUI.getWorkbench().getDisplay()
                        .getActiveShell(), "Enter Password for '" + connectionParameter.getName() + "'",
                        "Please enter password of user " + connectionParameter.getBindPrincipal() + ":", "", null );
                    if ( dialog.open() == CredentialsDialog.OK )
                    {
                        pw[0] = dialog.getValue();
                    }
                    else
                    {
                        pw[0] = null;
                    }
                }
            } );

            if ( pw[0] == null )
            {
                return null;
            }
            else
            {
                return new Credentials( connectionParameter.getBindPrincipal(), pw[0], connectionParameter );
            }
        }

    }

}
