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
import java.util.List;
import java.util.Map;

import org.apache.directory.shared.ldap.model.exception.LdapURLEncodingException;
import org.apache.directory.shared.ldap.model.filter.LdapURL;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.IReferralHandler;
import org.apache.directory.studio.connection.core.Utils;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;
import org.apache.directory.studio.connection.core.event.ConnectionUpdateAdapter;
import org.apache.directory.studio.connection.ui.dialogs.SelectReferralConnectionDialog;
import org.eclipse.ui.PlatformUI;


/**
 * Default implementation of {@link IReferralHandler}.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ConnectionUIReferralHandler extends ConnectionUpdateAdapter implements IReferralHandler
{

    /** The referral URL to referral connection cache. */
    private Map<String, Connection> referralUrlToReferralConnectionCache = new HashMap<String, Connection>();


    public ConnectionUIReferralHandler()
    {
        ConnectionEventRegistry.addConnectionUpdateListener( this, ConnectionCorePlugin.getDefault().getEventRunner() );
    }


    @Override
    public void connectionClosed( Connection connection )
    {
        referralUrlToReferralConnectionCache.clear();
    }


    /**
     * {@inheritDoc}
     */
    public Connection getReferralConnection( final List<String> referralUrls )
    {
        final Connection[] referralConnections = new Connection[1];
        try
        {
            // check cache
            for ( String url : referralUrls )
            {
                String normalizedUrl = Utils.getSimpleNormalizedUrl( new LdapURL( url ) );

                if ( referralUrlToReferralConnectionCache.containsKey( normalizedUrl ) )
                {
                    // check if referral connection exists in connection manager
                    Connection referralConnection = referralUrlToReferralConnectionCache.get( normalizedUrl );
                    Connection[] connections = ConnectionCorePlugin.getDefault().getConnectionManager()
                        .getConnections();
                    for ( int i = 0; i < connections.length; i++ )
                    {
                        Connection connection = connections[i];
                        if ( referralConnection == connection )
                        {
                            return referralConnection;
                        }
                    }

                    // referral connection doesn't exist in connection manager, remove it from cache
                    referralUrlToReferralConnectionCache.remove( normalizedUrl );
                }
            }

            // open dialog
            PlatformUI.getWorkbench().getDisplay().syncExec( new Runnable()
            {
                public void run()
            {
                SelectReferralConnectionDialog dialog = new SelectReferralConnectionDialog( PlatformUI.getWorkbench()
                    .getDisplay().getActiveShell(), referralUrls );
                if ( dialog.open() == SelectReferralConnectionDialog.OK )
                {
                    Connection connection = dialog.getReferralConnection();
                    referralConnections[0] = connection;
                }
            }
            } );

            // put to cache
            if ( referralConnections[0] != null )
            {
                for ( String url : referralUrls )
                {
                    String normalizedUrl = Utils.getSimpleNormalizedUrl( new LdapURL( url ) );
                    referralUrlToReferralConnectionCache.put( normalizedUrl, referralConnections[0] );
                }
            }
        }
        catch ( LdapURLEncodingException e )
        {
            // Will never occur
        }

        return referralConnections[0];
    }

}
