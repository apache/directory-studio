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


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.cursor.SearchCursor;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.ldif.LdifEntry;
import org.apache.directory.api.ldap.model.ldif.LdifReader;
import org.apache.directory.api.ldap.model.message.Control;
import org.apache.directory.api.ldap.model.message.DeleteRequest;
import org.apache.directory.api.ldap.model.message.DeleteRequestImpl;
import org.apache.directory.api.ldap.model.message.SearchRequest;
import org.apache.directory.api.ldap.model.message.SearchRequestImpl;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.schema.comparators.DnComparator;
import org.apache.directory.ldap.client.api.EntryCursorImpl;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.studio.connection.core.Controls;


/**
 * A unified test fixture that defines the DIT structure for all tests and for all test LDAP servers.
 */
public class TestFixture
{

    public static final String OBJECT_CLASS_ALL_FILTER = "(objectClass=*)";

    public static final String TEST_FIXTURE_LDIF = "TestFixture.ldif";

    public static final String CONTEXT_DN = "dc=example,dc=org";

    public static final String MISC_OU_DN = "ou=misc," + CONTEXT_DN;

    public static final String USERS_OU_DN = "ou=users," + CONTEXT_DN;

    public static final String REFERRALS_OU_DN = "ou=referrals," + CONTEXT_DN;

    /**
     * Creates the context entry "dc=example,dc=org" if it doesn't exist yet.
     */
    public static void createContextEntry( TestLdapServer ldapServer )
    {
        ldapServer.withAdminConnection( connection -> {
            if ( !connection.exists( CONTEXT_DN ) )
            {
                connection.add(
                    new DefaultEntry( CONTEXT_DN, "objectClass", "top", "objectClass", "domain", "dc", "example" ) );
            }
        } );
    }


    public static void importData( TestLdapServer ldapServer )
    {
        ldapServer.withAdminConnection( connection -> {
            try ( LdifReader ldifReader = new LdifReader( TestFixture.class.getResourceAsStream( TEST_FIXTURE_LDIF ) ) )
            {
                for ( LdifEntry entry : ldifReader )
                {
                    connection.add( entry.getEntry() );
                }
            }
        } );
    }


    public static void importReferrals( TestLdapServer ldapServer )
    {
        ldapServer.withAdminConnection( connection -> {
            connection.bind( ldapServer.getAdminDn(), ldapServer.getAdminPassword() );

            // create referral entries
            Entry referralsOu = new DefaultEntry( connection.getSchemaManager() );
            referralsOu.setDn( new Dn( REFERRALS_OU_DN ) );
            referralsOu.add( "objectClass", "top", "organizationalUnit" );
            referralsOu.add( "ou", "referrals" );
            connection.add( referralsOu );

            // direct referral
            Entry r1 = new DefaultEntry( connection.getSchemaManager() );
            r1.setDn( new Dn( "cn=referral1", REFERRALS_OU_DN ) );
            r1.add( "objectClass", "top", "referral", "extensibleObject" );
            r1.add( "cn", "referral1" );
            r1.add( "ref", ldapServer.getLdapUrl() + "/" + TestFixture.USERS_OU_DN );
            connection.add( r1 );

            // referral via another immediate referral
            Entry r2 = new DefaultEntry( connection.getSchemaManager() );
            r2.setDn( new Dn( "cn=referral2", REFERRALS_OU_DN ) );
            r2.add( "objectClass", "top", "referral", "extensibleObject" );
            r2.add( "cn", "referral2" );
            r2.add( "ref", ldapServer.getLdapUrl() + "/" + r1.getDn().getName() );
            connection.add( r2 );

            // referral to parent which contains this referral
            Entry r3 = new DefaultEntry( connection.getSchemaManager() );
            r3.setDn( new Dn( "cn=referral3", REFERRALS_OU_DN ) );
            r3.add( "objectClass", "top", "referral", "extensibleObject" );
            r3.add( "cn", "referral3" );
            r3.add( "ref", ldapServer.getLdapUrl() + "/" + REFERRALS_OU_DN );
            connection.add( r3 );

            // referrals pointing to each other (loop)
            Entry r4a = new DefaultEntry( connection.getSchemaManager() );
            r4a.setDn( new Dn( "cn=referral4a", REFERRALS_OU_DN ) );
            r4a.add( "objectClass", "top", "referral", "extensibleObject" );
            r4a.add( "cn", "referral4a" );
            r4a.add( "ref", ldapServer.getLdapUrl() + "/cn=referral4b," + REFERRALS_OU_DN );
            connection.add( r4a );
            Entry r4b = new DefaultEntry( connection.getSchemaManager() );
            r4b.setDn( new Dn( "cn=referral4b", REFERRALS_OU_DN ) );
            r4b.add( "objectClass", "top", "referral", "extensibleObject" );
            r4b.add( "cn", "referral4b" );
            r4b.add( "ref", ldapServer.getLdapUrl() + "/cn=referral4a," + REFERRALS_OU_DN );
            connection.add( r4b );
        } );
    }


    /**
     * Cleans all test data.
     */
    public static void cleanup( TestLdapServer ldapServer )
    {
        ldapServer.withAdminConnection( connection -> {
            // skip cleanup if context entry doesn't exist yet
            if ( !connection.exists( CONTEXT_DN ) )
            {
                return;
            }

            // delete ou=referrals
            deleteTree( connection, REFERRALS_OU_DN, Optional.of( Controls.MANAGEDSAIT_CONTROL ) );
            // delete ou=users
            deleteTree( connection, USERS_OU_DN, Optional.empty() );
            // delete ou=misc
            deleteTree( connection, MISC_OU_DN, Optional.empty() );
        } );

    }


    private static void deleteTree( LdapConnection connection, String baseDn, Optional<Control> control )
        throws Exception
    {
        SearchRequest searchRequest = new SearchRequestImpl();
        searchRequest.setBase( new Dn( baseDn ) );
        searchRequest.setFilter( OBJECT_CLASS_ALL_FILTER );
        searchRequest.setScope( SearchScope.SUBTREE );
        control.ifPresent( c -> searchRequest.addControl( c ) );

        try ( SearchCursor searchCursor = connection.search( searchRequest );
            EntryCursor entryCursor = new EntryCursorImpl( searchCursor ) )
        {
            List<Dn> dns = new ArrayList<>();
            for ( Entry entry : entryCursor )
            {
                dns.add( entry.getDn() );
            }
            dns.sort( new DnComparator( "1.1" ) );
            for ( Dn dn : dns )
            {
                DeleteRequest deleteRequest = new DeleteRequestImpl();
                deleteRequest.setName( dn );
                control.ifPresent( c -> deleteRequest.addControl( c ) );
                connection.delete( deleteRequest );
            }
        }
    }

}
