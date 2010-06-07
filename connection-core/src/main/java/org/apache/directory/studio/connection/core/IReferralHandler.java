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

package org.apache.directory.studio.connection.core;


import java.util.List;

import org.apache.directory.shared.ldap.util.LdapURL;


/**
 * Callback interface to request the target connection 
 * of a referral from a higher-level layer (from the UI plugin).
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface IReferralHandler
{

    /**
     * Gets the connection from this referral handler.
     * The connection is used to continue a LDAP request.
     * The referral handler may display a dialog to the user
     * to select a proper connection.
     * 
     * @param referralURLs the referral URLs
     * @return the target connection, null to cancel referral chasing
     */
    public Connection getReferralConnection( List<LdapURL> referralUrls );

}
