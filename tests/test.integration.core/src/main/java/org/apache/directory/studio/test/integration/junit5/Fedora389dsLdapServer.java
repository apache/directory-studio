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

package org.apache.directory.studio.test.integration.junit5;


import org.apache.directory.api.ldap.model.entry.DefaultModification;
import org.apache.directory.api.ldap.model.entry.Modification;
import org.apache.directory.api.ldap.model.entry.ModificationOperation;


/**
 * An 389ds implementation of a test LDAP server.
 * 
 * This implementation expects that an existing 389ds server is running
 * and connection parameters are provided via environment variables.
 */
public class Fedora389dsLdapServer extends TestLdapServer
{
    private static final String FEDORA_389DS_HOST = getEnvOrDefault( "FEDORA_389DS_HOST", "fedora389ds.example.com" );
    private static final int FEDORA_389DS_PORT = Integer.parseInt( getEnvOrDefault( "FEDORA_389DS_PORT", "21389" ) );
    private static final int FEDORA_389DS_PORT_SSL = Integer
        .parseInt( getEnvOrDefault( "FEDORA_389DS_PORT_SSL", "21636" ) );
    private static final String FEDORA_389DS_ADMIN_DN = getEnvOrDefault( "FEDORA_389DS_ADMIN_DN",
        "cn=Directory Manager" );
    private static final String FEDORA_389DS_ADMIN_PASSWORD = getEnvOrDefault( "FEDORA_389DS_ADMIN_PASSWORD", "admin" );

    public static Fedora389dsLdapServer getInstance()
    {
        return new Fedora389dsLdapServer();
    }


    private Fedora389dsLdapServer()
    {
        super( LdapServerType.Fedora389ds, FEDORA_389DS_HOST, FEDORA_389DS_PORT, FEDORA_389DS_PORT_SSL,
            FEDORA_389DS_ADMIN_DN, FEDORA_389DS_ADMIN_PASSWORD );
    }


    @Override
    public void setConfidentialityRequired( boolean confidentialityRequired )
    {
        withAdminConnection( connection -> {
            Modification modification = new DefaultModification( ModificationOperation.REPLACE_ATTRIBUTE,
                "nsslapd-require-secure-binds", confidentialityRequired ? "on" : "off" );
            connection.modify( "cn=config", modification );
        } );
    }
}
