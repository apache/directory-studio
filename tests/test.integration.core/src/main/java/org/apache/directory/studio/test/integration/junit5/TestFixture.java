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
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.ldif.LdifEntry;
import org.apache.directory.api.ldap.model.ldif.LdifReader;
import org.apache.directory.api.ldap.model.message.AliasDerefMode;
import org.apache.directory.api.ldap.model.message.Control;
import org.apache.directory.api.ldap.model.message.DeleteRequest;
import org.apache.directory.api.ldap.model.message.DeleteRequestImpl;
import org.apache.directory.api.ldap.model.message.SearchRequest;
import org.apache.directory.api.ldap.model.message.SearchRequestImpl;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.name.Rdn;
import org.apache.directory.api.ldap.model.schema.comparators.DnComparator;
import org.apache.directory.ldap.client.api.EntryCursorImpl;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.studio.connection.core.Controls;


/**
 * A unified test fixture that defines the DIT structure for all tests and for all test LDAP servers.
 */
public class TestFixture
{

    public static Dn dn( String dn )
    {
        try
        {
            return new Dn( dn );
        }
        catch ( LdapInvalidDnException e )
        {
            throw new RuntimeException( e );
        }
    }


    public static Dn dn( Rdn rdn, Dn dn )
    {
        try
        {
            return dn.add( rdn );
        }
        catch ( LdapInvalidDnException e )
        {
            throw new RuntimeException( e );
        }
    }


    public static Dn dn( String rdn, Dn dn )
    {
        try
        {
            return dn.add( rdn );
        }
        catch ( LdapInvalidDnException e )
        {
            throw new RuntimeException( e );
        }
    }

    public static final String OBJECT_CLASS_ALL_FILTER = "(objectClass=*)";

    public static final String TEST_FIXTURE_LDIF = "TestFixture.ldif";

    public static final Dn CONTEXT_DN = dn( "dc=example,dc=org" );

    public static final Dn MISC_DN = dn( "ou=misc", CONTEXT_DN );
    public static final Dn MISC1_DN = dn( "ou=misc.1", MISC_DN );
    public static final Dn MISC11_DN = dn( "ou=misc.1.1", MISC1_DN );
    public static final Dn MISC111_DN = dn( "ou=misc.1.1.1", MISC11_DN );
    public static final Dn BJENSEN_DN = dn( "cn=Barbara Jensen", MISC_DN );
    public static final Dn HNELSON_DN = dn( "uid=hnelson", MISC_DN );
    public static final Dn GERMAN_UMLAUT_DN = dn( "cn=Wolfgang K\u00f6lbel", MISC_DN );
    public static final Dn MULTI_VALUED_RDN_DN = dn( "cn=Barbara Jensen+uid=bjensen", MISC_DN );
    public static final Dn DN_WITH_LEADING_SHARP_BACKSLASH_PREFIXED = dn( "cn=\\#123456", MISC_DN );
    public static final Dn DN_WITH_LEADING_SHARP_HEX_PAIR_ESCAPED = dn( "cn=\\23123456", MISC_DN );
    public static final Dn DN_WITH_ESCAPED_CHARACTERS_BACKSLASH_PREFIXED = dn( "cn=\\\"\\+\\,\\;\\<\\>\\\\", MISC_DN );
    public static final Dn DN_WITH_ESCAPED_CHARACTERS_HEX_PAIR_ESCAPED = dn( "cn=\\22\\2B\\2C\\3B\\3C\\3E\\5C",
        MISC_DN );
    public static final Dn DN_WITH_TRAILING_EQUALS_CHARACTER = dn( "cn=trailing=", MISC_DN );
    public static final Dn DN_WITH_TRAILING_EQUALS_CHARACTER_HEX_PAIR_ESCAPED = dn( "cn=trailing\\3D", MISC_DN );
    public static final Dn DN_WITH_IP_HOST_NUMBER = dn( "cn=loopback+ipHostNumber=127.0.0.1", MISC_DN );
    public static final Dn ALIAS_DN = dn( "cn=alias", MISC_DN );
    public static final Dn SUBENTRY_DN = dn( "cn=subentry", MISC_DN );

    public static final Dn USERS_DN = dn( "ou=users", CONTEXT_DN );
    public static final Dn USER1_DN = dn( "uid=user.1", USERS_DN );
    public static final Dn USER2_DN = dn( "uid=user.2", USERS_DN );
    public static final Dn USER3_DN = dn( "uid=user.3", USERS_DN );
    public static final Dn USER4_DN = dn( "uid=user.4", USERS_DN );
    public static final Dn USER5_DN = dn( "uid=user.5", USERS_DN );
    public static final Dn USER8_DN = dn( "uid=user.8", USERS_DN );

    public static final Dn GROUPS_DN = dn( "ou=groups", CONTEXT_DN );
    public static final Dn GROUP1_DN = dn( "cn=group.1", GROUPS_DN );

    public static final Dn TARGET_DN = dn( "ou=target", CONTEXT_DN );

    public static final Dn REFERRALS_DN = dn( "ou=referrals", CONTEXT_DN );
    public static final Dn REFERRAL_TO_USERS_DN = dn( "cn=referral-to-users", REFERRALS_DN );
    public static final Dn REFERRAL_TO_USER1_DN = dn( "cn=referral-to-user.1", REFERRALS_DN );
    public static final Dn REFERRAL_TO_REFERRAL_TO_USERS_DN = dn( "cn=referral-to-referral-to-users", REFERRALS_DN );
    public static final Dn REFERRAL_TO_REFERRALS_DN = dn( "cn=referral-to-referrals", REFERRALS_DN );
    public static final Dn REFERRAL_LOOP_1_DN = dn( "cn=referral-loop-1", REFERRALS_DN );
    public static final Dn REFERRAL_LOOP_2_DN = dn( "cn=referral-loop-2", REFERRALS_DN );
    public static final Dn REFERRAL_TO_MISC_DN = dn( "cn=referral-to-misc", REFERRALS_DN );

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
                ldifReader.setSchemaManager( connection.getSchemaManager() );
                for ( LdifEntry entry : ldifReader )
                {
                    replaceRefLdapUrl( ldapServer, entry );
                    connection.add( entry.getEntry() );
                }
            }
        } );
    }


    private static void replaceRefLdapUrl( TestLdapServer ldapServer, LdifEntry entry )
        throws LdapInvalidAttributeValueException
    {
        Attribute ref = entry.get( "ref" );
        if ( ref != null )
        {
            String oldRefLdapUrl = ref.getString();
            String newRefLdapUrl = oldRefLdapUrl.replace( "replace-with-host-port",
                ldapServer.getHost() + ":" + ldapServer.getPort() );
            ref.remove( oldRefLdapUrl );
            ref.add( newRefLdapUrl );
        }
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
            deleteTree( connection, REFERRALS_DN, Optional.empty() );
            // delete ou=groups
            deleteTree( connection, GROUPS_DN, Optional.empty() );
            // delete ou=users
            deleteTree( connection, USERS_DN, Optional.empty() );
            // delete ou=misc
            deleteTree( connection, MISC_DN, Optional.of( Controls.SUBENTRIES_CONTROL ) );
            deleteTree( connection, MISC_DN, Optional.empty() );
            // delete ou=target
            deleteTree( connection, TARGET_DN, Optional.of( Controls.SUBENTRIES_CONTROL ) );
            deleteTree( connection, TARGET_DN, Optional.empty() );
        } );
    }


    private static void deleteTree( LdapConnection connection, Dn baseDn, Optional<Control> control )
        throws Exception
    {
        SearchRequest searchRequest = new SearchRequestImpl();
        searchRequest.setBase( baseDn );
        searchRequest.setFilter( OBJECT_CLASS_ALL_FILTER );
        searchRequest.setScope( SearchScope.SUBTREE );
        searchRequest.setDerefAliases( AliasDerefMode.NEVER_DEREF_ALIASES );
        searchRequest.addControl( Controls.MANAGEDSAIT_CONTROL );
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
                deleteRequest.addControl( Controls.MANAGEDSAIT_CONTROL );
                control.ifPresent( c -> deleteRequest.addControl( c ) );
                connection.delete( deleteRequest );
            }
        }
    }

}
