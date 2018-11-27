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
package org.apache.directory.studio.connection.core.io;


import java.util.ArrayList;

import org.apache.directory.api.ldap.model.message.Referral;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.IConnectionListener;
import org.apache.directory.studio.connection.core.IReferralHandler;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;


/**
 * Connection wrapper helper class. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ConnectionWrapperUtils
{
    /**
     * Gets the referral connection from the given URL.
     * 
     * @param url the URL
     * @param monitor the progress monitor
     * @param source the source
     * 
     * @return the referral connection
     */
    public static Connection getReferralConnection( Referral referral, StudioProgressMonitor monitor, Object source )
    {
        Connection referralConnection = null;
        IReferralHandler referralHandler = ConnectionCorePlugin.getDefault().getReferralHandler();
        if ( referralHandler != null )
        {
            referralConnection = referralHandler
                .getReferralConnection( new ArrayList<String>( referral.getLdapUrls() ) );

            // open connection if not yet open
            if ( referralConnection != null && !referralConnection.getConnectionWrapper().isConnected() )
            {
                referralConnection.getConnectionWrapper().connect( monitor );
                referralConnection.getConnectionWrapper().bind( monitor );
                for ( IConnectionListener listener : ConnectionCorePlugin.getDefault().getConnectionListeners() )
                {
                    listener.connectionOpened( referralConnection, monitor );
                }
                ConnectionEventRegistry.fireConnectionOpened( referralConnection, source );
            }
        }
        return referralConnection;
    }
}
