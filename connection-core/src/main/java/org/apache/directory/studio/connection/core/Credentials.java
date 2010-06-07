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
 * Default implementation of ICredentials.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class Credentials implements ICredentials
{

    /** The bind principal. */
    private String bindPrincipal;

    /** The bind password. */
    private String bindPassword;

    /** The connection parameter. */
    private ConnectionParameter connectionParameter;


    /**
     * Creates a new instance of Credentials.
     *
     * @param bindPrincipal the bind principal, typically a DN
     * @param bindPassword the bind password
     * @param connectionParameter the connection parameter
     */
    public Credentials( String bindPrincipal, String bindPassword, ConnectionParameter connectionParameter )
    {
        this.bindPrincipal = bindPrincipal;
        this.bindPassword = bindPassword;
        this.connectionParameter = connectionParameter;
    }


    /**
     * {@inheritDoc}
     */
    public ConnectionParameter getConnectionParameter()
    {
        return connectionParameter;
    }


    /**
     * {@inheritDoc}
     */
    public String getBindPrincipal()
    {
        return bindPrincipal;
    }


    /**
     * {@inheritDoc}
     */
    public String getBindPassword()
    {
        return bindPassword;
    }

}
