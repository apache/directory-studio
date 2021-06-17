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


import static org.apache.directory.studio.test.integration.junit5.Constants.LOCALHOST;

import org.apache.directory.api.ldap.model.entry.DefaultModification;
import org.apache.directory.api.ldap.model.entry.Modification;
import org.apache.directory.api.ldap.model.entry.ModificationOperation;
import org.apache.directory.api.ldap.model.exception.LdapNoSuchAttributeException;
import org.apache.directory.api.ldap.model.ldif.LdifEntry;
import org.apache.directory.api.ldap.model.ldif.LdifReader;
import org.apache.directory.ldap.client.api.LdapConnection;


/**
 * An OpenLDAP implementation of a test LDAP server.
 * 
 * This implementation expects that an existing OpenLDAP server is running
 * and connection parameters are provided via environment variables.
 */
public class OpenLdapServer extends TestLdapServer
{
    private static final String OPENLDAP_HOST = getEnvOrDefault( "OPENLDAP_HOST", "openldap.example.com" );
    private static final int OPENLDAP_PORT = Integer.parseInt( getEnvOrDefault( "OPENLDAP_PORT", "20389" ) );
    private static final int OPENLDAP_PORT_SSL = Integer.parseInt( getEnvOrDefault( "OPENLDAP_PORT_SSL", "20636" ) );
    private static final String OPENLDAP_ADMIN_DN = getEnvOrDefault( "OPENLDAP_ADMIN_DN",
        "cn=admin,dc=example,dc=org" );
    private static final String OPENLDAP_ADMIN_PASSWORD = getEnvOrDefault( "OPENLDAP_ADMIN_PASSWORD", "admin" );
    private static final String OPENLDAP_CONFIG_DN = getEnvOrDefault( "OPENLDAP_CONFIG_DN", "cn=admin,cn=config" );
    private static final String OPENLDAP_CONFIG_PASSWORD = getEnvOrDefault( "OPENLDAP_CONFIG_PASSWORD", "config" );

    public static OpenLdapServer getInstance()
    {
        return new OpenLdapServer();
    }


    private OpenLdapServer()
    {
        super( LdapServerType.OpenLdap, OPENLDAP_HOST, OPENLDAP_PORT, OPENLDAP_PORT_SSL, OPENLDAP_ADMIN_DN,
            OPENLDAP_ADMIN_PASSWORD );
    }


    public void prepare()
    {
        super.prepare();

        try ( LdapConnection connection = openConnection();
            LdifReader ldifReader = new LdifReader( TestFixture.class.getResourceAsStream( "OpenLdapConfig.ldif" ) ) )
        {
            connection.bind( OPENLDAP_CONFIG_DN, OPENLDAP_CONFIG_PASSWORD );
            for ( LdifEntry entry : ldifReader )
            {
                for ( Modification modification : entry.getModifications() )
                {
                    connection.modify( entry.getDn(), modification );
                }
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Unexpected exception: " + e, e );
        }
    }


    @Override
    public void setConfidentialityRequired( boolean confidentialityRequired )
    {
        try ( LdapConnection connection = openConnection() )
        {
            connection.bind( OPENLDAP_CONFIG_DN, OPENLDAP_CONFIG_PASSWORD );
            Modification modification = new DefaultModification( ModificationOperation.REPLACE_ATTRIBUTE,
                "olcSecurity", confidentialityRequired ? "ssf=256 tls=256" : "ssf=0 tls=0" );
            connection.modify( "cn=config", modification );
        }
        catch ( LdapNoSuchAttributeException e )
        {
            // ignore
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Unexpected exception: " + e, e );
        }
    }

}
