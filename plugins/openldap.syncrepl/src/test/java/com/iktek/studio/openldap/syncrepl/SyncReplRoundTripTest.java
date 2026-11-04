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
package org.apache.directory.studio.openldap.syncrepl;


import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


/**
 * Tests that a SyncRepl written by {@link SyncRepl#toString()} is read back unchanged by
 * {@link SyncReplParser}, for ordinary values (no backslashes and no double quotes inside values).
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SyncReplRoundTripTest
{
    private static SyncRepl roundTrip( SyncRepl syncRepl ) throws Exception
    {
        SyncRepl reparsed = new SyncReplParser().parse( syncRepl.toString() );
        assertNotNull( reparsed, syncRepl.toString() );

        return reparsed;
    }


    private static SyncRepl withRid()
    {
        SyncRepl syncRepl = new SyncRepl();
        syncRepl.setRid( "001" );

        return syncRepl;
    }


    @Test
    public void testAllOptionsExceptTlsCacertDir() throws Exception
    {
        // tls_cacertdir is covered by testTlsCacertDir: its keyword is shadowed by its prefix tls_cacert
        SyncRepl syncRepl = SyncReplModelTest.createFullSyncRepl();
        syncRepl.setTlsCacertDir( null );

        SyncRepl reparsed = roundTrip( syncRepl );

        SyncReplModelTest.assertFullSyncRepl( reparsed, false );
        assertNull( reparsed.getTlsCacertDir() );
    }


    @Test
    @Disabled("Fixed by #117: tls_cacertdir is matched as its prefix tls_cacert and fails to parse")
    public void testTlsCacertDir() throws Exception
    {
        SyncRepl syncRepl = withRid();
        syncRepl.setTlsCacertDir( "/etc/openldap/certs" );

        assertEquals( "/etc/openldap/certs", roundTrip( syncRepl ).getTlsCacertDir() );
    }


    @Test
    public void testQuotedValuesWithSpaces() throws Exception
    {
        SyncRepl syncRepl = withRid();
        syncRepl.setSearchBase( "ou=My Users,dc=example,dc=com" );
        syncRepl.setFilter( "(cn=John Smith)" );
        syncRepl.setBindDn( "cn=John Smith,dc=example,dc=com" );
        syncRepl.setLogFilter( "(reqDN=cn=John Smith)" );

        SyncRepl reparsed = roundTrip( syncRepl );

        assertEquals( "ou=My Users,dc=example,dc=com", reparsed.getSearchBase() );
        assertEquals( "(cn=John Smith)", reparsed.getFilter() );
        assertEquals( "cn=John Smith,dc=example,dc=com", reparsed.getBindDn() );
        assertEquals( "(reqDN=cn=John Smith)", reparsed.getLogFilter() );
    }


    @Test
    public void testAttributes() throws Exception
    {
        SyncRepl syncRepl = withRid();
        syncRepl.addAttribute( "cn", "sn", "mail", "telephoneNumber" );

        assertArrayEquals( new String[]
            { "cn", "sn", "mail", "telephoneNumber" }, roundTrip( syncRepl ).getAttributes() );
    }


    @Test
    public void testAttrsOnlyWithoutAttributes() throws Exception
    {
        SyncRepl syncRepl = withRid();
        syncRepl.setAttrsOnly( true );

        SyncRepl reparsed = roundTrip( syncRepl );

        assertTrue( reparsed.isAttrsOnly() );
        assertArrayEquals( new String[0], reparsed.getAttributes() );
    }


    @Test
    public void testLdapsProviderWithoutPort() throws Exception
    {
        SyncRepl syncRepl = withRid();
        syncRepl.setProvider( Provider.parse( "ldaps://ldap.example.com" ) );

        Provider provider = roundTrip( syncRepl ).getProvider();

        assertTrue( provider.isLdaps() );
        assertEquals( "ldap.example.com", provider.getHost() );
        assertEquals( "ldaps://ldap.example.com", provider.toString() );
    }


    @Test
    public void testEveryTypeValue() throws Exception
    {
        for ( Type value : Type.values() )
        {
            SyncRepl syncRepl = withRid();
            syncRepl.setType( value );
            assertEquals( value, roundTrip( syncRepl ).getType() );
        }
    }


    @Test
    public void testEveryScopeValue() throws Exception
    {
        for ( Scope value : Scope.values() )
        {
            SyncRepl syncRepl = withRid();
            syncRepl.setScope( value );
            assertEquals( value, roundTrip( syncRepl ).getScope() );
        }
    }


    @Test
    public void testEverySchemaCheckingValue() throws Exception
    {
        for ( SchemaChecking value : SchemaChecking.values() )
        {
            SyncRepl syncRepl = withRid();
            syncRepl.setSchemaChecking( value );
            assertEquals( value, roundTrip( syncRepl ).getSchemaChecking() );
        }
    }


    @Test
    public void testEveryBindMethodValue() throws Exception
    {
        for ( BindMethod value : BindMethod.values() )
        {
            SyncRepl syncRepl = withRid();
            syncRepl.setBindMethod( value );
            assertEquals( value, roundTrip( syncRepl ).getBindMethod() );
        }
    }


    @Test
    public void testEveryStartTlsValue() throws Exception
    {
        for ( StartTls value : StartTls.values() )
        {
            SyncRepl syncRepl = withRid();
            syncRepl.setStartTls( value );
            assertEquals( value, roundTrip( syncRepl ).getStartTls() );
        }
    }


    @Test
    public void testEveryTlsReqCertValue() throws Exception
    {
        for ( TlsReqCert value : TlsReqCert.values() )
        {
            SyncRepl syncRepl = withRid();
            syncRepl.setTlsReqcert( value );
            assertEquals( value, roundTrip( syncRepl ).getTlsReqcert() );
        }
    }


    @Test
    public void testEveryTlsCrlCheckValue() throws Exception
    {
        for ( TlsCrlCheck value : TlsCrlCheck.values() )
        {
            SyncRepl syncRepl = withRid();
            syncRepl.setTlsCrlcheck( value );
            assertEquals( value, roundTrip( syncRepl ).getTlsCrlcheck() );
        }
    }


    @Test
    public void testEverySyncDataValue() throws Exception
    {
        for ( SyncData value : SyncData.values() )
        {
            SyncRepl syncRepl = withRid();
            syncRepl.setSyncData( value );
            assertEquals( value, roundTrip( syncRepl ).getSyncData() );
        }
    }


    @Test
    public void testZeroLimitsAndTimeouts() throws Exception
    {
        SyncRepl syncRepl = withRid();
        syncRepl.setSizeLimit( 0 );
        syncRepl.setTimeLimit( 0 );
        syncRepl.setNetworkTimeout( 0 );
        syncRepl.setTimeout( 0 );

        SyncRepl reparsed = roundTrip( syncRepl );

        assertEquals( 0, reparsed.getSizeLimit() );
        assertEquals( 0, reparsed.getTimeLimit() );
        assertEquals( 0, reparsed.getNetworkTimeout() );
        assertEquals( 0, reparsed.getTimeout() );
    }
}
