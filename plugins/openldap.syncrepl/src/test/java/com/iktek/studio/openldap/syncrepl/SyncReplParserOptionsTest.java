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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


/**
 * Tests how {@link SyncReplParser} reads each option of an olcSyncrepl value.
 * <p>
 * Where the parser differs from the way slapd reads cn=config values (strtok_quote_ldif() in
 * servers/slapd/config.c: backslashes are literal, a double quote only closes a quoted value when it is
 * followed by whitespace or the end, and single quotes are not quote characters), the test asserts slapd's
 * behaviour and is disabled.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SyncReplParserOptionsTest
{
    private static SyncRepl parse( String value ) throws Exception
    {
        SyncRepl syncRepl = new SyncReplParser().parse( value );
        assertNotNull( syncRepl, value );

        return syncRepl;
    }


    private static SyncReplParserException parseFails( String value )
    {
        return assertThrows( SyncReplParserException.class, () -> new SyncReplParser().parse( value ) );
    }


    @Test
    public void testStringOptions() throws Exception
    {
        SyncRepl syncRepl = parse( "rid=042 searchbase=\"dc=example,dc=com\" filter=\"(objectClass=*)\" "
            + "binddn=\"cn=replicator,dc=example,dc=com\" saslmech=GSSAPI authcid=\"replicator\" "
            + "authzid=\"dn:cn=replicator\" credentials=secret realm=EXAMPLE.COM secprops=noanonymous,minssf=56 "
            + "tls_cert=/etc/cert.pem tls_key=/etc/key.pem tls_cacert=/etc/ca.pem "
            + "logbase=\"cn=accesslog\" logfilter=\"(reqResult=0)\"" );

        assertEquals( "042", syncRepl.getRid() );
        assertEquals( "dc=example,dc=com", syncRepl.getSearchBase() );
        assertEquals( "(objectClass=*)", syncRepl.getFilter() );
        assertEquals( "cn=replicator,dc=example,dc=com", syncRepl.getBindDn() );
        assertEquals( "GSSAPI", syncRepl.getSaslMech() );
        assertEquals( "replicator", syncRepl.getAuthcid() );
        assertEquals( "dn:cn=replicator", syncRepl.getAuthzid() );
        assertEquals( "secret", syncRepl.getCredentials() );
        assertEquals( "EXAMPLE.COM", syncRepl.getRealm() );
        assertEquals( "noanonymous,minssf=56", syncRepl.getSecProps() );
        assertEquals( "/etc/cert.pem", syncRepl.getTlsCert() );
        assertEquals( "/etc/key.pem", syncRepl.getTlsKey() );
        assertEquals( "/etc/ca.pem", syncRepl.getTlsCacert() );
        assertEquals( "cn=accesslog", syncRepl.getLogBase() );
        assertEquals( "(reqResult=0)", syncRepl.getLogFilter() );
    }


    @Test
    public void testQuotedAndUnquotedValuesAreEquivalent() throws Exception
    {
        SyncRepl quoted = parse( "rid=\"1\" credentials=\"secret\" tls_cacert=\"/etc/ca.pem\"" );
        SyncRepl unquoted = parse( "rid=1 credentials=secret tls_cacert=/etc/ca.pem" );

        assertEquals( "1", quoted.getRid() );
        assertEquals( unquoted.getRid(), quoted.getRid() );
        assertEquals( "secret", quoted.getCredentials() );
        assertEquals( unquoted.getCredentials(), quoted.getCredentials() );
        assertEquals( "/etc/ca.pem", quoted.getTlsCacert() );
        assertEquals( unquoted.getTlsCacert(), quoted.getTlsCacert() );
    }


    @Test
    public void testQuotedValueKeepsSpaces() throws Exception
    {
        SyncRepl syncRepl = parse( "rid=1 searchbase=\"ou=My Users,dc=example,dc=com\" credentials=\"my secret\"" );

        assertEquals( "ou=My Users,dc=example,dc=com", syncRepl.getSearchBase() );
        assertEquals( "my secret", syncRepl.getCredentials() );
    }


    @Test
    public void testUnquotedValueEndsAtWhitespace() throws Exception
    {
        SyncRepl syncRepl = parse( "rid=1 credentials=my secret" );

        assertEquals( "my", syncRepl.getCredentials() );
    }


    @Test
    public void testProvider() throws Exception
    {
        Provider provider = parse( "rid=1 provider=ldaps://ldap.example.com:636" ).getProvider();

        assertTrue( provider.isLdaps() );
        assertEquals( "ldap.example.com", provider.getHost() );
        assertEquals( 636, provider.getPort() );
    }


    @Test
    public void testIntervalRetryAndKeepAlive() throws Exception
    {
        SyncRepl syncRepl = parse( "rid=1 interval=01:02:03:04 retry=\"60 10 300 +\" keepalive=240:10:30" );

        Interval interval = syncRepl.getInterval();
        assertEquals( 1, interval.getDays() );
        assertEquals( 2, interval.getHours() );
        assertEquals( 3, interval.getMinutes() );
        assertEquals( 4, interval.getSeconds() );
        assertEquals( "60 10 300 +", syncRepl.getRetry().toString() );
        assertEquals( 240, syncRepl.getKeepAlive().getIdle() );
        assertEquals( 10, syncRepl.getKeepAlive().getProbes() );
        assertEquals( 30, syncRepl.getKeepAlive().getInterval() );
    }


    @Test
    public void testNumericOptions() throws Exception
    {
        SyncRepl syncRepl = parse( "rid=1 sizelimit=11 timelimit=9 network-timeout=5 timeout=7" );

        assertEquals( 11, syncRepl.getSizeLimit() );
        assertEquals( 9, syncRepl.getTimeLimit() );
        assertEquals( 5, syncRepl.getNetworkTimeout() );
        assertEquals( 7, syncRepl.getTimeout() );
    }


    @Test
    public void testNetworkTimeoutAndTimeoutAreDistinct() throws Exception
    {
        assertEquals( -1, parse( "rid=1 network-timeout=5" ).getTimeout() );
        assertEquals( -1, parse( "rid=1 timeout=7" ).getNetworkTimeout() );
    }


    @Test
    public void testAttributes() throws Exception
    {
        assertArrayEquals( new String[]
            { "cn", "sn", "mail" }, parse( "rid=1 attrs=\"cn, sn,  mail\"" ).getAttributes() );
        assertArrayEquals( new String[]
            { "cn", "sn" }, parse( "rid=1 attrs=cn,sn" ).getAttributes() );
    }


    @Test
    public void testAttrsOnlyIsAFlag() throws Exception
    {
        SyncRepl syncRepl = parse( "rid=1 attrsonly attrs=cn" );

        assertTrue( syncRepl.isAttrsOnly() );
        assertArrayEquals( new String[]
            { "cn" }, syncRepl.getAttributes() );

        SyncRepl withoutFlag = parse( "rid=1 attrs=cn" );

        assertFalse( withoutFlag.isAttrsOnly() );
    }


    @Test
    public void testEnumOptions() throws Exception
    {
        SyncRepl syncRepl = parse( "rid=1 type=refreshAndPersist scope=subord schemachecking=off bindmethod=sasl "
            + "starttls=critical tls_reqcert=allow tls_crlcheck=all syncdata=changelog" );

        assertEquals( Type.REFRESH_AND_PERSIST, syncRepl.getType() );
        assertEquals( Scope.SUBORD, syncRepl.getScope() );
        assertEquals( SchemaChecking.OFF, syncRepl.getSchemaChecking() );
        assertEquals( BindMethod.SASL, syncRepl.getBindMethod() );
        assertEquals( StartTls.CRITICAL, syncRepl.getStartTls() );
        assertEquals( TlsReqCert.ALLOW, syncRepl.getTlsReqcert() );
        assertEquals( TlsCrlCheck.ALL, syncRepl.getTlsCrlcheck() );
        assertEquals( SyncData.CHANGELOG, syncRepl.getSyncData() );
    }


    @Test
    public void testKeywordsAndEnumValuesAreCaseInsensitive() throws Exception
    {
        SyncRepl syncRepl = parse( "RID=1 TYPE=REFRESHONLY Scope=SUB" );

        assertEquals( "1", syncRepl.getRid() );
        assertEquals( Type.REFRESH_ONLY, syncRepl.getType() );
        assertEquals( Scope.SUB, syncRepl.getScope() );
    }


    @Test
    public void testWhitespaceAroundEqualsAndBetweenOptions() throws Exception
    {
        SyncRepl syncRepl = parse( "  rid = 1 \n\t filter =  \"(cn=x)\"   scope=one  " );

        assertEquals( "1", syncRepl.getRid() );
        assertEquals( "(cn=x)", syncRepl.getFilter() );
        assertEquals( Scope.ONE, syncRepl.getScope() );
    }


    @Test
    public void testOptionOrderDoesNotMatter() throws Exception
    {
        SyncRepl a = parse( "rid=1 filter=\"(cn=x)\" scope=one type=refreshOnly" );
        SyncRepl b = parse( "type=refreshOnly scope=one filter=\"(cn=x)\" rid=1" );

        assertEquals( "1", b.getRid() );
        assertEquals( "(cn=x)", b.getFilter() );
        assertEquals( Scope.ONE, b.getScope() );
        assertEquals( Type.REFRESH_ONLY, b.getType() );
        assertEquals( a.toString(), b.toString() );
    }


    @Test
    public void testNoKnownOptionGivesNull() throws Exception
    {
        assertNull( new SyncReplParser().parse( "   " ) );
        assertNull( new SyncReplParser().parse( "foo=bar" ) );
    }


    @Test
    public void testInvalidEnumValuesFail()
    {
        assertEquals( 1, parseFails( "rid=1 type=bogus" ).size() );
        assertEquals( 1, parseFails( "rid=1 scope=bogus" ).size() );
        assertEquals( 1, parseFails( "rid=1 schemachecking=maybe" ).size() );
        assertEquals( 1, parseFails( "rid=1 bindmethod=bogus" ).size() );
        assertEquals( 1, parseFails( "rid=1 starttls=no" ).size() );
        assertEquals( 1, parseFails( "rid=1 tls_reqcert=bogus" ).size() );
        assertEquals( 1, parseFails( "rid=1 tls_crlcheck=bogus" ).size() );
        assertEquals( 1, parseFails( "rid=1 syncdata=bogus" ).size() );
    }


    @Test
    public void testInvalidStructuredValuesFail()
    {
        assertEquals( 1, parseFails( "rid=1 provider=http://example.com" ).size() );
        assertEquals( 1, parseFails( "rid=1 interval=1:2:3:4" ).size() );
        assertEquals( 1, parseFails( "rid=1 retry=\"60\"" ).size() );
        assertEquals( 1, parseFails( "rid=1 keepalive=240" ).size() );
    }


    @Test
    public void testInvalidNumbersFail()
    {
        assertEquals( 1, parseFails( "rid=1 sizelimit=abc" ).size() );
        assertEquals( 1, parseFails( "rid=1 timelimit=abc" ).size() );
        assertEquals( 1, parseFails( "rid=1 network-timeout=abc" ).size() );
        assertEquals( 1, parseFails( "rid=1 timeout=abc" ).size() );
    }


    @Test
    public void testMissingValueFails()
    {
        assertEquals( 1, parseFails( "rid=1 filter" ).size() );
    }


    @Test
    @Disabled("Suspected bug: an option with '=' and no value at the very end (e.g. \"rid=1 filter=\") throws "
        + "ArrayIndexOutOfBoundsException from getQuotedOrNotQuotedOptionValue instead of a parser error")
    public void testEmptyValueAtEndFails()
    {
        assertEquals( 1, parseFails( "rid=1 filter=" ).size() );
    }


    @Test
    public void testEveryErrorIsCollected()
    {
        assertEquals( 3, parseFails( "rid=1 type=bogus scope=bogus sizelimit=abc" ).size() );
    }


    @Test
    @Disabled("Known divergence from slapd: \\\" is read as an escaped quote; in cn=config slapd has no escape, "
        + "so \"a\\\"b\" x=1 is the value \"a\\b")
    public void testBackslashQuoteIsNotAnEscape() throws Exception
    {
        assertEquals( "\"a\\b", parse( "rid=1 credentials=\"a\\\"b\" x=1" ).getCredentials() );
    }


    @Test
    @Disabled("Known divergence from slapd: a double quote that is not followed by whitespace ends the quoted "
        + "value; slapd only closes a quoted value at a quote followed by whitespace or the end")
    public void testQuoteInsideQuotedValue() throws Exception
    {
        // strtok_quote_ldif() removes only the last non-closing quote and the closing quote
        assertEquals( "\"ab", parse( "rid=1 credentials=\"a\"b\" scope=one" ).getCredentials() );
    }


    @Test
    @Disabled("Known divergence from slapd: single quotes are read as quote characters; slapd does not treat "
        + "them specially, so credentials='a b' is the value 'a")
    public void testSingleQuotesAreNotQuotes() throws Exception
    {
        assertEquals( "'a", parse( "rid=1 credentials='a b'" ).getCredentials() );
    }


    @Test
    @Disabled("Suspected bug: keywords are matched anywhere, not only at the start of an option, so xrid=5 "
        + "is read as rid=5 (slapd rejects the unknown option)")
    public void testKeywordMustStartAnOption() throws Exception
    {
        assertNull( new SyncReplParser().parse( "xrid=5" ) );
    }


    @Test
    @Disabled("Suspected bug: keywords are matched anywhere, so slapd's exattrs=userPassword (attributes to "
        + "exclude) is read as attrs=userPassword (the only attribute to replicate)")
    public void testExattrsIsNotReadAsAttrs() throws Exception
    {
        SyncRepl syncRepl = new SyncReplParser().parse( "rid=1 exattrs=userPassword" );

        assertArrayEquals( new String[0], syncRepl.getAttributes() );
    }


    @Test
    @Disabled("Suspected bug: options the parser does not know (e.g. exattrs, suffixmassage, lazycommit, "
        + "tls_protocol_min) are skipped silently and are lost when the value is written back")
    public void testUnknownOptionsAreKept() throws Exception
    {
        SyncRepl syncRepl = parse( "rid=1 lazycommit tls_protocol_min=3.3" );

        assertTrue( syncRepl.toString().contains( "tls_protocol_min=3.3" ), syncRepl.toString() );
    }


    @Test
    @Disabled("Suspected bug: every missing-value error names option 'rid', whatever the option was")
    public void testMissingValueErrorNamesTheOption()
    {
        String message = parseFails( "rid=1 filter" ).toString();

        assertTrue( message.contains( "filter" ), message );
    }


    @Test
    @Disabled("Suspected bug: the parser only knows tls_ciphersuite=, but slapd reads and writes "
        + "tls_cipher_suite=, so the cipher suite of an existing consumer is silently dropped")
    public void testCipherSuiteSlapdKeyword() throws Exception
    {
        SyncRepl syncRepl = parse( "rid=1 tls_cipher_suite=HIGH" );

        assertEquals( "HIGH", syncRepl.getTlsCipherSuite() );
    }
}
