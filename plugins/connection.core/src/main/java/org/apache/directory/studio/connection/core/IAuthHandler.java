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


/**
 * Callback interface to request credentials from a 
 * higher-level layer (from the UI plugin).
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface IAuthHandler
{
    /**
     * Gets credentials from this authentication handler. 
     * The credentials are used to bind to the given connection. 
     * The authentication handler may display a dialog to the user.
     * 
     * @param connectionParameter the connection to bind to
     * @return the credentials, null to cancel the authentication
     */
    ICredentials getCredentials( ConnectionParameter connectionParameter );
}
