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


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.Consumer;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


/**
 * Tests the olcSyncrepl value written by {@link SyncRepl#toString()}.
 * <p>
 * The expected strings follow the format slapd itself writes for cn=config (syncrepl_unparse() and
 * bindconf_unparse() in servers/slapd): searchbase, filter, retry, attrs, logbase, logfilter, binddn,
 * credentials, authcid, authzid and the tls_cert/tls_key/tls_cacert/tls_cacertdir paths are written between
 * double quotes, nothing is escaped, and all other options are written unquoted. Where Studio's output
 * currently differs from that format, the test asserts slapd's format and is disabled.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SyncReplToStringTest
{
    /**
     * Returns the value written for a SyncRepl on which only the given option is set.
     */
    private static String write( Consumer<SyncRepl> option )
    {
        SyncRepl syncRepl = new SyncRepl();
        option.accept( syncRepl );

        return syncRepl.toString();
    }


    @Test
    public void testRid()
    {
        assertEquals( "rid=001", write( s -> s.setRid( "001" ) ) );
    }


    @Test
    public void testProvider() throws Exception
    {
        Provider ldap = Provider.parse( "ldap://ldap.example.com:389" );
        Provider ldaps = Provider.parse( "ldaps://ldap.example.com:636" );
        Provider noPort = Provider.parse( "ldap://ldap.example.com" );

        assertEquals( "provider=ldap://ldap.example.com:389", write( s -> s.setProvider( ldap ) ) );
        assertEquals( "provider=ldaps://ldap.example.com:636", write( s -> s.setProvider( ldaps ) ) );
        assertEquals( "provider=ldap://ldap.example.com", write( s -> s.setProvider( noPort ) ) );
    }


    @Test
    public void testQuotedDnAndFilterOptions()
    {
        assertEquals( "searchbase=\"dc=example,dc=com\"", write( s -> s.setSearchBase( "dc=example,dc=com" ) ) );
        assertEquals( "filter=\"(objectClass=*)\"", write( s -> s.setFilter( "(objectClass=*)" ) ) );
        assertEquals( "logbase=\"cn=accesslog\"", write( s -> s.setLogBase( "cn=accesslog" ) ) );
        assertEquals( "logfilter=\"(reqResult=0)\"", write( s -> s.setLogFilter( "(reqResult=0)" ) ) );
        assertEquals( "binddn=\"cn=replicator,dc=example,dc=com\"",
            write( s -> s.setBindDn( "cn=replicator,dc=example,dc=com" ) ) );
        assertEquals( "authcid=\"replicator\"", write( s -> s.setAuthcid( "replicator" ) ) );
        assertEquals( "authzid=\"dn:cn=replicator\"", write( s -> s.setAuthzid( "dn:cn=replicator" ) ) );
    }


    @Test
    public void testQuotedValuesWithSpaces() throws Exception
    {
        assertEquals( "searchbase=\"ou=My Users,dc=example,dc=com\"",
            write( s -> s.setSearchBase( "ou=My Users,dc=example,dc=com" ) ) );
        assertEquals( "filter=\"(cn=John Smith)\"", write( s -> s.setFilter( "(cn=John Smith)" ) ) );

        Retry retry = Retry.parse( "60 10 300 +" );
        assertEquals( "retry=\"60 10 300 +\"", write( s -> s.setRetry( retry ) ) );
    }


    @Test
    public void testAttributes()
    {
        assertEquals( "attrs=\"cn\"", write( s -> s.addAttribute( "cn" ) ) );
        assertEquals( "attrs=\"cn,sn,mail\"", write( s -> s.addAttribute( "cn", "sn", "mail" ) ) );
        assertEquals( "attrsonly", write( s -> s.setAttrsOnly( true ) ) );
        assertEquals( "", write( s -> s.setAttrsOnly( false ) ) );
    }


    @Test
    public void testEnumOptions()
    {
        assertEquals( "type=refreshOnly", write( s -> s.setType( Type.REFRESH_ONLY ) ) );
        assertEquals( "type=refreshAndPersist", write( s -> s.setType( Type.REFRESH_AND_PERSIST ) ) );
        assertEquals( "scope=sub", write( s -> s.setScope( Scope.SUB ) ) );
        assertEquals( "scope=one", write( s -> s.setScope( Scope.ONE ) ) );
        assertEquals( "scope=base", write( s -> s.setScope( Scope.BASE ) ) );
        assertEquals( "scope=subord", write( s -> s.setScope( Scope.SUBORD ) ) );
        assertEquals( "schemachecking=on", write( s -> s.setSchemaChecking( SchemaChecking.ON ) ) );
        assertEquals( "schemachecking=off", write( s -> s.setSchemaChecking( SchemaChecking.OFF ) ) );
        assertEquals( "bindmethod=simple", write( s -> s.setBindMethod( BindMethod.SIMPLE ) ) );
        assertEquals( "bindmethod=sasl", write( s -> s.setBindMethod( BindMethod.SASL ) ) );
        assertEquals( "starttls=yes", write( s -> s.setStartTls( StartTls.YES ) ) );
        assertEquals( "starttls=critical", write( s -> s.setStartTls( StartTls.CRITICAL ) ) );
        assertEquals( "tls_reqcert=never", write( s -> s.setTlsReqcert( TlsReqCert.NEVER ) ) );
        assertEquals( "tls_reqcert=allow", write( s -> s.setTlsReqcert( TlsReqCert.ALLOW ) ) );
        assertEquals( "tls_reqcert=try", write( s -> s.setTlsReqcert( TlsReqCert.TRY ) ) );
        assertEquals( "tls_reqcert=demand", write( s -> s.setTlsReqcert( TlsReqCert.DEMAND ) ) );
        assertEquals( "tls_crlcheck=none", write( s -> s.setTlsCrlcheck( TlsCrlCheck.NONE ) ) );
        assertEquals( "tls_crlcheck=peer", write( s -> s.setTlsCrlcheck( TlsCrlCheck.PEER ) ) );
        assertEquals( "tls_crlcheck=all", write( s -> s.setTlsCrlcheck( TlsCrlCheck.ALL ) ) );
        assertEquals( "syncdata=default", write( s -> s.setSyncData( SyncData.DEFAULT ) ) );
        assertEquals( "syncdata=accesslog", write( s -> s.setSyncData( SyncData.ACCESSLOG ) ) );
        assertEquals( "syncdata=changelog", write( s -> s.setSyncData( SyncData.CHANGELOG ) ) );
    }


    @Test
    public void testNumericOptions() throws Exception
    {
        assertEquals( "sizelimit=10", write( s -> s.setSizeLimit( 10 ) ) );
        assertEquals( "timelimit=20", write( s -> s.setTimeLimit( 20 ) ) );
        assertEquals( "network-timeout=5", write( s -> s.setNetworkTimeout( 5 ) ) );
        assertEquals( "timeout=7", write( s -> s.setTimeout( 7 ) ) );

        // -1 means "not set" and is not written
        assertEquals( "", write( s -> s.setSizeLimit( -1 ) ) );
        assertEquals( "", write( s -> s.setTimeLimit( -1 ) ) );
        assertEquals( "", write( s -> s.setNetworkTimeout( -1 ) ) );
        assertEquals( "", write( s -> s.setTimeout( -1 ) ) );

        // 0 is a value and is written
        assertEquals( "sizelimit=0", write( s -> s.setSizeLimit( 0 ) ) );

        Interval interval = Interval.parse( "01:02:03:04" );
        assertEquals( "interval=01:02:03:04", write( s -> s.setInterval( interval ) ) );

        KeepAlive keepAlive = KeepAlive.parse( "240:10:30" );
        assertEquals( "keepalive=240:10:30", write( s -> s.setKeepAlive( keepAlive ) ) );
    }


    @Test
    public void testUnquotedStringOptions()
    {
        assertEquals( "saslmech=GSSAPI", write( s -> s.setSaslMech( "GSSAPI" ) ) );
        assertEquals( "realm=EXAMPLE.COM", write( s -> s.setRealm( "EXAMPLE.COM" ) ) );
    }


    @Test
    @Disabled("Suspected bug: the cipher suite is written as tls_ciphersuite=, but slapd only knows "
        + "tls_cipher_suite= (config.c bindkey table) and rejects the whole syncrepl value")
    public void testCipherSuiteUsesSlapdKeyword()
    {
        assertEquals( "tls_cipher_suite=HIGH", write( s -> s.setTlsCipherSuite( "HIGH" ) ) );
    }


    @Test
    public void testOptionsAreSeparatedBySingleSpaces()
    {
        SyncRepl syncRepl = new SyncRepl();
        syncRepl.setRid( "001" );
        syncRepl.setFilter( "(cn=foo)" );
        syncRepl.setScope( Scope.SUB );

        assertEquals( "rid=001 filter=\"(cn=foo)\" scope=sub", syncRepl.toString() );
    }


    @Test
    public void testNoLeadingSpaceWithoutRid()
    {
        assertEquals( "scope=sub", write( s -> s.setScope( Scope.SUB ) ) );
    }


    @Test
    @Disabled("Known divergence from slapd: credentials are written unquoted, slapd writes credentials=\"...\"")
    public void testCredentialsAreQuoted()
    {
        assertEquals( "credentials=\"secret\"", write( s -> s.setCredentials( "secret" ) ) );
    }


    @Test
    @Disabled("Known divergence from slapd: the tls_cert, tls_key, tls_cacert and tls_cacertdir paths are "
        + "written unquoted, slapd writes them between double quotes")
    public void testTlsPathsAreQuoted()
    {
        assertEquals( "tls_cert=\"/etc/openldap/cert.pem\"", write( s -> s.setTlsCert( "/etc/openldap/cert.pem" ) ) );
        assertEquals( "tls_key=\"/etc/openldap/key.pem\"", write( s -> s.setTlsKey( "/etc/openldap/key.pem" ) ) );
        assertEquals( "tls_cacert=\"/etc/openldap/ca.pem\"", write( s -> s.setTlsCacert( "/etc/openldap/ca.pem" ) ) );
        assertEquals( "tls_cacertdir=\"/etc/openldap/certs\"",
            write( s -> s.setTlsCacertDir( "/etc/openldap/certs" ) ) );
    }


    // Not tested on purpose:
    // - a double quote inside a quoted value: cn=config has no escape mechanism, so no output string makes
    //   slapd read back e.g. (cn=a"b) unchanged; both the current \" and a verbatim " are lossy
    // - keyword case (secProps= vs secprops=, authcid= vs authcID=): slapd matches keywords case-insensitively
}
