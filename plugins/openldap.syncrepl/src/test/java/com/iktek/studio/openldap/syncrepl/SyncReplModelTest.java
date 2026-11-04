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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;


/**
 * Tests the SyncRepl model: defaults, accessors, attribute handling and copying.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SyncReplModelTest
{
    @Test
    public void testDefaults()
    {
        SyncRepl syncRepl = new SyncRepl();

        assertNull( syncRepl.getRid() );
        assertNull( syncRepl.getProvider() );
        assertNull( syncRepl.getSearchBase() );
        assertNull( syncRepl.getType() );
        assertNull( syncRepl.getInterval() );
        assertNull( syncRepl.getRetry() );
        assertNull( syncRepl.getFilter() );
        assertNull( syncRepl.getScope() );
        assertArrayEquals( new String[0], syncRepl.getAttributes() );
        assertFalse( syncRepl.isAttrsOnly() );
        assertEquals( -1, syncRepl.getSizeLimit() );
        assertEquals( -1, syncRepl.getTimeLimit() );
        assertNull( syncRepl.getSchemaChecking() );
        assertEquals( -1, syncRepl.getNetworkTimeout() );
        assertEquals( -1, syncRepl.getTimeout() );
        assertNull( syncRepl.getBindMethod() );
        assertNull( syncRepl.getBindDn() );
        assertNull( syncRepl.getSaslMech() );
        assertNull( syncRepl.getAuthcid() );
        assertNull( syncRepl.getAuthzid() );
        assertNull( syncRepl.getCredentials() );
        assertNull( syncRepl.getRealm() );
        assertNull( syncRepl.getSecProps() );
        assertNull( syncRepl.getKeepAlive() );
        assertNull( syncRepl.getStartTls() );
        assertNull( syncRepl.getTlsCert() );
        assertNull( syncRepl.getTlsKey() );
        assertNull( syncRepl.getTlsCacert() );
        assertNull( syncRepl.getTlsCacertDir() );
        assertNull( syncRepl.getTlsReqcert() );
        assertNull( syncRepl.getTlsCipherSuite() );
        assertNull( syncRepl.getTlsCrlcheck() );
        assertNull( syncRepl.getLogBase() );
        assertNull( syncRepl.getLogFilter() );
        assertNull( syncRepl.getSyncData() );

        // nothing set, nothing written
        assertEquals( "", syncRepl.toString() );
        assertEquals( "", SyncRepl.createDefault().toString() );
    }


    @Test
    public void testSettersAndGetters() throws Exception
    {
        SyncRepl syncRepl = createFullSyncRepl();

        assertFullSyncRepl( syncRepl );
    }


    @Test
    public void testAddAndRemoveAttributes()
    {
        SyncRepl syncRepl = new SyncRepl();

        syncRepl.addAttribute( "cn", "sn" );
        syncRepl.addAttribute( "mail" );
        assertArrayEquals( new String[]
            { "cn", "sn", "mail" }, syncRepl.getAttributes() );

        syncRepl.removeAttribute( "sn" );
        assertArrayEquals( new String[]
            { "cn", "mail" }, syncRepl.getAttributes() );

        // null varargs are ignored
        syncRepl.addAttribute( ( String[] ) null );
        syncRepl.removeAttribute( ( String[] ) null );
        assertArrayEquals( new String[]
            { "cn", "mail" }, syncRepl.getAttributes() );

        syncRepl.setAttributes( new String[]
            { "uid" } );
        assertArrayEquals( new String[]
            { "uid" }, syncRepl.getAttributes() );
    }


    @Test
    public void testCopyOfNullIsNull()
    {
        assertNull( SyncRepl.copy( null ) );
    }


    @Test
    public void testCopyHasAllValues() throws Exception
    {
        SyncRepl original = createFullSyncRepl();

        SyncRepl copy = original.copy();

        assertNotSame( original, copy );
        assertFullSyncRepl( copy );
    }


    @Test
    public void testCopyIsIndependentOfTheOriginal() throws Exception
    {
        SyncRepl original = createFullSyncRepl();

        SyncRepl copy = original.copy();

        // nested value objects are copied, not shared
        assertNotSame( original.getProvider(), copy.getProvider() );
        assertNotSame( original.getInterval(), copy.getInterval() );
        assertNotSame( original.getRetry(), copy.getRetry() );
        assertNotSame( original.getKeepAlive(), copy.getKeepAlive() );

        copy.getProvider().setHost( "other.example.com" );
        copy.addAttribute( "description" );
        copy.setFilter( "(cn=other)" );

        assertEquals( "ldap.example.com", original.getProvider().getHost() );
        assertArrayEquals( new String[]
            { "cn", "sn" }, original.getAttributes() );
        assertEquals( "(objectClass=*)", original.getFilter() );
    }


    /**
     * Creates a SyncRepl with every option set to an ordinary value.
     */
    static SyncRepl createFullSyncRepl() throws Exception
    {
        SyncRepl syncRepl = new SyncRepl();

        syncRepl.setRid( "001" );
        syncRepl.setProvider( Provider.parse( "ldap://ldap.example.com:389" ) );
        syncRepl.setSearchBase( "dc=example,dc=com" );
        syncRepl.setType( Type.REFRESH_ONLY );
        syncRepl.setInterval( Interval.parse( "01:02:03:04" ) );
        syncRepl.setRetry( Retry.parse( "60 10 300 +" ) );
        syncRepl.setFilter( "(objectClass=*)" );
        syncRepl.setScope( Scope.ONE );
        syncRepl.addAttribute( "cn", "sn" );
        syncRepl.setAttrsOnly( true );
        syncRepl.setSizeLimit( 10 );
        syncRepl.setTimeLimit( 20 );
        syncRepl.setSchemaChecking( SchemaChecking.ON );
        syncRepl.setNetworkTimeout( 5 );
        syncRepl.setTimeout( 7 );
        syncRepl.setBindMethod( BindMethod.SASL );
        syncRepl.setBindDn( "cn=replicator,dc=example,dc=com" );
        syncRepl.setSaslMech( "GSSAPI" );
        syncRepl.setAuthcid( "replicator" );
        syncRepl.setAuthzid( "dn:cn=replicator,dc=example,dc=com" );
        syncRepl.setCredentials( "secret" );
        syncRepl.setRealm( "EXAMPLE.COM" );
        syncRepl.setSecProps( "noanonymous,minssf=56" );
        syncRepl.setKeepAlive( KeepAlive.parse( "240:10:30" ) );
        syncRepl.setStartTls( StartTls.CRITICAL );
        syncRepl.setTlsCert( "/etc/openldap/cert.pem" );
        syncRepl.setTlsKey( "/etc/openldap/key.pem" );
        syncRepl.setTlsCacert( "/etc/openldap/ca.pem" );
        syncRepl.setTlsCacertDir( "/etc/openldap/certs" );
        syncRepl.setTlsReqcert( TlsReqCert.DEMAND );
        syncRepl.setTlsCipherSuite( "HIGH" );
        syncRepl.setTlsCrlcheck( TlsCrlCheck.PEER );
        syncRepl.setLogBase( "cn=accesslog" );
        syncRepl.setLogFilter( "(&(objectClass=auditWriteObject)(reqResult=0))" );
        syncRepl.setSyncData( SyncData.ACCESSLOG );

        return syncRepl;
    }


    /**
     * Checks that the given SyncRepl has exactly the values set by {@link #createFullSyncRepl()}.
     */
    static void assertFullSyncRepl( SyncRepl syncRepl )
    {
        assertFullSyncRepl( syncRepl, true );
    }


    /**
     * Checks that the given SyncRepl has the values set by {@link #createFullSyncRepl()}, optionally
     * skipping tls_cacertdir.
     */
    static void assertFullSyncRepl( SyncRepl syncRepl, boolean checkTlsCacertDir )
    {
        assertEquals( "001", syncRepl.getRid() );
        assertFalse( syncRepl.getProvider().isLdaps() );
        assertEquals( "ldap.example.com", syncRepl.getProvider().getHost() );
        assertEquals( 389, syncRepl.getProvider().getPort() );
        assertEquals( "dc=example,dc=com", syncRepl.getSearchBase() );
        assertEquals( Type.REFRESH_ONLY, syncRepl.getType() );
        assertEquals( 1, syncRepl.getInterval().getDays() );
        assertEquals( 2, syncRepl.getInterval().getHours() );
        assertEquals( 3, syncRepl.getInterval().getMinutes() );
        assertEquals( 4, syncRepl.getInterval().getSeconds() );
        assertEquals( "60 10 300 +", syncRepl.getRetry().toString() );
        assertEquals( "(objectClass=*)", syncRepl.getFilter() );
        assertEquals( Scope.ONE, syncRepl.getScope() );
        assertArrayEquals( new String[]
            { "cn", "sn" }, syncRepl.getAttributes() );
        assertTrue( syncRepl.isAttrsOnly() );
        assertEquals( 10, syncRepl.getSizeLimit() );
        assertEquals( 20, syncRepl.getTimeLimit() );
        assertEquals( SchemaChecking.ON, syncRepl.getSchemaChecking() );
        assertEquals( 5, syncRepl.getNetworkTimeout() );
        assertEquals( 7, syncRepl.getTimeout() );
        assertEquals( BindMethod.SASL, syncRepl.getBindMethod() );
        assertEquals( "cn=replicator,dc=example,dc=com", syncRepl.getBindDn() );
        assertEquals( "GSSAPI", syncRepl.getSaslMech() );
        assertEquals( "replicator", syncRepl.getAuthcid() );
        assertEquals( "dn:cn=replicator,dc=example,dc=com", syncRepl.getAuthzid() );
        assertEquals( "secret", syncRepl.getCredentials() );
        assertEquals( "EXAMPLE.COM", syncRepl.getRealm() );
        assertEquals( "noanonymous,minssf=56", syncRepl.getSecProps() );
        assertEquals( 240, syncRepl.getKeepAlive().getIdle() );
        assertEquals( 10, syncRepl.getKeepAlive().getProbes() );
        assertEquals( 30, syncRepl.getKeepAlive().getInterval() );
        assertEquals( StartTls.CRITICAL, syncRepl.getStartTls() );
        assertEquals( "/etc/openldap/cert.pem", syncRepl.getTlsCert() );
        assertEquals( "/etc/openldap/key.pem", syncRepl.getTlsKey() );
        assertEquals( "/etc/openldap/ca.pem", syncRepl.getTlsCacert() );
        if ( checkTlsCacertDir )
        {
            assertEquals( "/etc/openldap/certs", syncRepl.getTlsCacertDir() );
        }
        assertEquals( TlsReqCert.DEMAND, syncRepl.getTlsReqcert() );
        assertEquals( "HIGH", syncRepl.getTlsCipherSuite() );
        assertEquals( TlsCrlCheck.PEER, syncRepl.getTlsCrlcheck() );
        assertEquals( "cn=accesslog", syncRepl.getLogBase() );
        assertEquals( "(&(objectClass=auditWriteObject)(reqResult=0))", syncRepl.getLogFilter() );
        assertEquals( SyncData.ACCESSLOG, syncRepl.getSyncData() );
    }
}
