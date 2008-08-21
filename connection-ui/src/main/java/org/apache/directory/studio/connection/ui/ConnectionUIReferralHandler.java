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

package org.apache.directory.studio.connection.ui;


import java.util.HashMap;
import java.util.Map;

import org.apache.directory.shared.ldap.util.LdapURL;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.IReferralHandler;
import org.apache.directory.studio.connection.ui.dialogs.SelectReferralConnectionDialog;
import org.eclipse.ui.PlatformUI;


/**
 * Default implementation of {@link IReferralHandler}.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ConnectionUIReferralHandler implements IReferralHandler
{

    /** The referral URL to referral connection cache. */
    private Map<LdapURL, Connection> referralUrlToReferralConnectionCache = new HashMap<LdapURL, Connection>();


    /**
     * {@inheritDoc}
     */
    public Connection getReferralConnection( final LdapURL referralUrl )
    {
        // check cache
        if ( referralUrlToReferralConnectionCache.containsKey( referralUrl ) )
        {
            Connection referralConnection = referralUrlToReferralConnectionCache.get( referralUrl );
            if ( referralConnection != null )
            {
                Connection[] connections = ConnectionCorePlugin.getDefault().getConnectionManager().getConnections();
                for ( int i = 0; i < connections.length; i++ )
                {
                    Connection connection = connections[i];
                    if ( referralConnection == connection )
                    {
                        return referralConnection;
                    }
                }
            }
        }

        referralUrlToReferralConnectionCache.remove( referralUrl );

        // open dialog
        final Connection[] referralConnection = new Connection[1];
        PlatformUI.getWorkbench().getDisplay().syncExec( new Runnable()
        {
            public void run()
            {
                SelectReferralConnectionDialog dialog = new SelectReferralConnectionDialog( PlatformUI.getWorkbench()
                    .getDisplay().getActiveShell(), referralUrl );
                if ( dialog.open() == SelectReferralConnectionDialog.OK )
                {
                    Connection connection = dialog.getReferralConnection();
                    referralUrlToReferralConnectionCache.put( referralUrl, connection );
                    referralConnection[0] = connection;
                }
            }
        } );

        return referralConnection[0];
    }

}
