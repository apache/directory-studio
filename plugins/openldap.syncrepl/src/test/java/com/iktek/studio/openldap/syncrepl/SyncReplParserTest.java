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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SyncReplParserTest
{
    @Test
    public void testEmpty() throws Exception
    {
        SyncReplParser parser = new SyncReplParser();
        SyncRepl syncRepl = parser.parse( "" );

        assertNull( syncRepl );
    }


    @Test
    public void testUnknownOption() throws Exception
    {
        SyncReplParser parser = new SyncReplParser();
        SyncRepl syncRepl = parser.parse( "unknownOption" );

        assertNull( syncRepl );
    }


    @Test
    public void testUnknownOptionWithValue() throws Exception
    {
        SyncReplParser parser = new SyncReplParser();
        SyncRepl syncRepl = parser.parse( "unknownOption=someValue" );

        assertNull( syncRepl );
    }


    @Test
    public void testRidOkOneDigit() throws Exception
    {
        SyncReplParser parser = new SyncReplParser();
        SyncRepl syncRepl = parser.parse( "rid=1" );

        assertNotNull( syncRepl );
        assertEquals( "1", syncRepl.getRid() );
    }


    @Test
    public void testRidOkTwoDigit() throws Exception
    {
        SyncReplParser parser = new SyncReplParser();
        SyncRepl syncRepl = parser.parse( "rid=12" );

        assertNotNull( syncRepl );
        assertEquals( "12", syncRepl.getRid() );
    }


    @Test
    public void testRidOkThreeDigit() throws Exception
    {
        SyncReplParser parser = new SyncReplParser();
        SyncRepl syncRepl = parser.parse( "rid=123" );

        assertNotNull( syncRepl );
        assertEquals( "123", syncRepl.getRid() );
    }


    @Test
    public void testRidOkThreeDigitWithWhiteSpaces() throws Exception
    {
        SyncReplParser parser = new SyncReplParser();
        SyncRepl syncRepl = parser.parse( "  rid = 123  " );

        assertNotNull( syncRepl );
        assertEquals( "123", syncRepl.getRid() );
    }


    @Test
    public void testProvider() throws Exception
    {
        SyncReplParser parser = new SyncReplParser();
        parser.parse( "provider=ldap://localhost:10389" );
    }


    @Test
    public void testProviderWithWhiteSpaces() throws Exception
    {
        SyncReplParser parser = new SyncReplParser();
        parser.parse( "  provider  =  ldap://localhost:10389" );
    }


    @Test
    public void testSymasDoc1SingleLine() throws Exception
    {
        SyncReplParser parser = new SyncReplParser();
        SyncRepl syncRepl = parser
            .parse( "rid=0 " +
                "provider=ldap://ldapmaster.symas.com:389 " +
                "bindmethod=simple " +
                "binddn=\"cn=replicator,dc=symas,dc=com\" " +
                "credentials=secret " +
                "searchbase=\"dc=symas,dc=com\" " +
                "logbase=\"cn=accesslog\" " +
                "logfilter=\"(&(objectClass=auditWriteObject)(reqResult=0))\" " +
                "schemachecking=on " +
                "type=refreshAndPersist " +
                "retry=\"60 +\" " +
                "syncdata=accesslog" );

        assertNotNull( syncRepl );
        assertEquals( "0", syncRepl.getRid() );

        Provider provider = syncRepl.getProvider();

        assertNotNull( provider );
        assertFalse( provider.isLdaps() );
        assertEquals( "ldapmaster.symas.com", provider.getHost() );
        assertEquals( 389, provider.getPort() );

        assertEquals( BindMethod.SIMPLE, syncRepl.getBindMethod() );
        assertEquals( "cn=replicator,dc=symas,dc=com", syncRepl.getBindDn() );
        assertEquals( "secret", syncRepl.getCredentials() );
        assertEquals( "dc=symas,dc=com", syncRepl.getSearchBase() );
        assertEquals( "cn=accesslog", syncRepl.getLogBase() );
        assertEquals( "(&(objectClass=auditWriteObject)(reqResult=0))", syncRepl.getLogFilter() );
        assertEquals( SchemaChecking.ON, syncRepl.getSchemaChecking() );
        assertEquals( Type.REFRESH_AND_PERSIST, syncRepl.getType() );

        Retry retry = syncRepl.getRetry();

        assertNotNull( retry );
        assertEquals( 1, retry.size() );

        RetryPair retryPair = retry.getPairs()[0];

        assertNotNull( retryPair );
        assertEquals( 60, retryPair.getInterval() );
        assertEquals( RetryPair.PLUS, retryPair.getRetries() );

        assertEquals( SyncData.ACCESSLOG, syncRepl.getSyncData() );
    }


    @Test
    public void testSymasDoc1WithSpacesMultilines() throws Exception
    {
        SyncReplParser parser = new SyncReplParser();
        SyncRepl syncRepl = parser
            .parse( "rid=0 \n" +
                "provider=ldap://ldapmaster.symas.com:389 \n" +
                "bindmethod=simple \n" +
                "binddn=\"cn=replicator,dc=symas,dc=com\" \n" +
                "credentials=secret \n" +
                "searchbase=\"dc=symas,dc=com\" \n" +
                "logbase=\"cn=accesslog\" \n" +
                "logfilter=\"(&(objectClass=auditWriteObject)(reqResult=0))\" \n" +
                "schemachecking=on \n" +
                "type=refreshAndPersist \n" +
                "retry=\"60 +\" \n" +
                "syncdata=accesslog" );

        assertNotNull( syncRepl );
        assertEquals( "0", syncRepl.getRid() );

        Provider provider = syncRepl.getProvider();

        assertNotNull( provider );
        assertFalse( provider.isLdaps() );
        assertEquals( "ldapmaster.symas.com", provider.getHost() );
        assertEquals( 389, provider.getPort() );

        assertEquals( BindMethod.SIMPLE, syncRepl.getBindMethod() );
        assertEquals( "cn=replicator,dc=symas,dc=com", syncRepl.getBindDn() );
        assertEquals( "secret", syncRepl.getCredentials() );
        assertEquals( "dc=symas,dc=com", syncRepl.getSearchBase() );
        assertEquals( "cn=accesslog", syncRepl.getLogBase() );
        assertEquals( "(&(objectClass=auditWriteObject)(reqResult=0))", syncRepl.getLogFilter() );
        assertEquals( SchemaChecking.ON, syncRepl.getSchemaChecking() );
        assertEquals( Type.REFRESH_AND_PERSIST, syncRepl.getType() );

        Retry retry = syncRepl.getRetry();

        assertNotNull( retry );
        assertEquals( 1, retry.size() );

        RetryPair retryPair = retry.getPairs()[0];

        assertNotNull( retryPair );
        assertEquals( 60, retryPair.getInterval() );
        assertEquals( RetryPair.PLUS, retryPair.getRetries() );

        assertEquals( SyncData.ACCESSLOG, syncRepl.getSyncData() );
    }


    @Test
    public void testSymasDoc1WithoutSpacesMultilines() throws Exception
    {
        SyncReplParser parser = new SyncReplParser();
        SyncRepl syncRepl = parser
            .parse( "rid=0\n" +
                "provider=ldap://ldapmaster.symas.com:389\n" +
                "bindmethod=simple\n" +
                "binddn=\"cn=replicator,dc=symas,dc=com\"\n" +
                "credentials=secret\n" +
                "searchbase=\"dc=symas,dc=com\"\n" +
                "logbase=\"cn=accesslog\"\n" +
                "logfilter=\"(&(objectClass=auditWriteObject)(reqResult=0))\"\n" +
                "schemachecking=on\n" +
                "type=refreshAndPersist\n" +
                "retry=\"60 +\"\n" +
                "syncdata=accesslog" );

        assertNotNull( syncRepl );
        assertEquals( "0", syncRepl.getRid() );

        Provider provider = syncRepl.getProvider();

        assertNotNull( provider );
        assertFalse( provider.isLdaps() );
        assertEquals( "ldapmaster.symas.com", provider.getHost() );
        assertEquals( 389, provider.getPort() );

        assertEquals( BindMethod.SIMPLE, syncRepl.getBindMethod() );
        assertEquals( "cn=replicator,dc=symas,dc=com", syncRepl.getBindDn() );
        assertEquals( "secret", syncRepl.getCredentials() );
        assertEquals( "dc=symas,dc=com", syncRepl.getSearchBase() );
        assertEquals( "cn=accesslog", syncRepl.getLogBase() );
        assertEquals( "(&(objectClass=auditWriteObject)(reqResult=0))", syncRepl.getLogFilter() );
        assertEquals( SchemaChecking.ON, syncRepl.getSchemaChecking() );
        assertEquals( Type.REFRESH_AND_PERSIST, syncRepl.getType() );

        Retry retry = syncRepl.getRetry();

        assertNotNull( retry );
        assertEquals( 1, retry.size() );

        RetryPair retryPair = retry.getPairs()[0];

        assertNotNull( retryPair );
        assertEquals( 60, retryPair.getInterval() );
        assertEquals( RetryPair.PLUS, retryPair.getRetries() );

        assertEquals( SyncData.ACCESSLOG, syncRepl.getSyncData() );
    }


    @Test
    public void testSymasDoc2SingleLine() throws Exception
    {
        SyncReplParser parser = new SyncReplParser();
        SyncRepl syncRepl = parser
            .parse( "rid=001\n provider=ldaps://ldapmaster.symas.com:389 binddn=\"cn=config\" bindmethod=simple credentials=secret searchbase=\"cn=config\" type=refreshAndPersist retry=\"5 5 300 5\" timeout=1" );

        assertNotNull( syncRepl );
        assertEquals( "001", syncRepl.getRid() );

        Provider provider = syncRepl.getProvider();

        assertNotNull( provider );
        assertTrue( provider.isLdaps() );
        assertEquals( "ldapmaster.symas.com", provider.getHost() );
        assertEquals( 389, provider.getPort() );

        assertEquals( BindMethod.SIMPLE, syncRepl.getBindMethod() );
        assertEquals( "cn=config", syncRepl.getBindDn() );
        assertEquals( "secret", syncRepl.getCredentials() );
        assertEquals( "cn=config", syncRepl.getSearchBase() );

        Retry retry = syncRepl.getRetry();

        assertNotNull( retry );
        assertEquals( 2, retry.size() );

        RetryPair retryPair1 = retry.getPairs()[0];

        assertNotNull( retryPair1 );
        assertEquals( 5, retryPair1.getInterval() );
        assertEquals( 5, retryPair1.getRetries() );

        RetryPair retryPair2 = retry.getPairs()[1];

        assertNotNull( retryPair2 );
        assertEquals( 300, retryPair2.getInterval() );
        assertEquals( 5, retryPair2.getRetries() );

        assertEquals( Type.REFRESH_AND_PERSIST, syncRepl.getType() );
        assertEquals( 1, syncRepl.getTimeout() );
    }


    @Test
    public void testSymasDoc2WithSpacesMultilines() throws Exception
    {
        SyncReplParser parser = new SyncReplParser();
        SyncRepl syncRepl = parser
            .parse( "rid=001\n " +
                "provider=ldaps://ldapmaster.symas.com:389\n " +
                "binddn=\"cn=config\"\n " +
                "bindmethod=simple\n " +
                "credentials=secret\n " +
                "searchbase=\"cn=config\"\n " +
                "type=refreshAndPersist\n " +
                "retry=\"5 5 300 5\"\n " +
                "timeout=1" );

        assertNotNull( syncRepl );
        assertEquals( "001", syncRepl.getRid() );

        Provider provider = syncRepl.getProvider();

        assertNotNull( provider );
        assertTrue( provider.isLdaps() );
        assertEquals( "ldapmaster.symas.com", provider.getHost() );
        assertEquals( 389, provider.getPort() );

        assertEquals( BindMethod.SIMPLE, syncRepl.getBindMethod() );
        assertEquals( "cn=config", syncRepl.getBindDn() );
        assertEquals( "secret", syncRepl.getCredentials() );
        assertEquals( "cn=config", syncRepl.getSearchBase() );

        Retry retry = syncRepl.getRetry();

        assertNotNull( retry );
        assertEquals( 2, retry.size() );

        RetryPair retryPair1 = retry.getPairs()[0];

        assertNotNull( retryPair1 );
        assertEquals( 5, retryPair1.getInterval() );
        assertEquals( 5, retryPair1.getRetries() );

        RetryPair retryPair2 = retry.getPairs()[1];

        assertNotNull( retryPair2 );
        assertEquals( 300, retryPair2.getInterval() );
        assertEquals( 5, retryPair2.getRetries() );

        assertEquals( Type.REFRESH_AND_PERSIST, syncRepl.getType() );
        assertEquals( 1, syncRepl.getTimeout() );
    }


    @Test
    public void testSymasDoc2WithoutSpacesMultilines() throws Exception
    {
        SyncReplParser parser = new SyncReplParser();
        SyncRepl syncRepl = parser
            .parse( "rid=001\n" +
                "provider=ldaps://ldapmaster.symas.com:389\n" +
                "binddn=\"cn=config\"\n" +
                "bindmethod=simple\n" +
                "credentials=secret\n" +
                "searchbase=\"cn=config\"\n" +
                "type=refreshAndPersist\n" +
                "retry=\"5 5 300 5\"\n" +
                "timeout=1" );

        assertNotNull( syncRepl );
        assertEquals( "001", syncRepl.getRid() );

        Provider provider = syncRepl.getProvider();

        assertNotNull( provider );
        assertTrue( provider.isLdaps() );
        assertEquals( "ldapmaster.symas.com", provider.getHost() );
        assertEquals( 389, provider.getPort() );

        assertEquals( BindMethod.SIMPLE, syncRepl.getBindMethod() );
        assertEquals( "cn=config", syncRepl.getBindDn() );
        assertEquals( "secret", syncRepl.getCredentials() );
        assertEquals( "cn=config", syncRepl.getSearchBase() );

        Retry retry = syncRepl.getRetry();

        assertNotNull( retry );
        assertEquals( 2, retry.size() );

        RetryPair retryPair1 = retry.getPairs()[0];

        assertNotNull( retryPair1 );
        assertEquals( 5, retryPair1.getInterval() );
        assertEquals( 5, retryPair1.getRetries() );

        RetryPair retryPair2 = retry.getPairs()[1];

        assertNotNull( retryPair2 );
        assertEquals( 300, retryPair2.getInterval() );
        assertEquals( 5, retryPair2.getRetries() );

        assertEquals( Type.REFRESH_AND_PERSIST, syncRepl.getType() );
        assertEquals( 1, syncRepl.getTimeout() );
    }


    @Test
    public void testSymasDoc3SingleLine() throws Exception
    {
        SyncReplParser parser = new SyncReplParser();
        SyncRepl syncRepl = parser
            .parse( "rid=123 " +
                "provider=ldap://provider.example.com:389 " +
                "type=refreshOnly " +
                "interval=01:00:00:00 " +
                "searchbase=\"dc=example,dc=com\" " +
                "filter=\"(objectClass=organizationalPerson)\" " +
                "scope=sub " +
                "attrs=\"cn,sn,ou,telephoneNumber,title,l\" " +
                "schemachecking=off " +
                "bindmethod=simple " +
                "binddn=\"cn=syncuser,dc=example,dc=com\" " +
                "credentials=secret" );

        assertNotNull( syncRepl );
        assertEquals( "123", syncRepl.getRid() );

        Provider provider = syncRepl.getProvider();

        assertNotNull( provider );
        assertFalse( provider.isLdaps() );
        assertEquals( "provider.example.com", provider.getHost() );
        assertEquals( 389, provider.getPort() );

        assertEquals( Type.REFRESH_ONLY, syncRepl.getType() );

        Interval interval = syncRepl.getInterval();

        assertNotNull( interval );
        assertEquals( 1, interval.getDays() );
        assertEquals( 0, interval.getHours() );
        assertEquals( 0, interval.getMinutes() );
        assertEquals( 0, interval.getSeconds() );

        assertEquals( "dc=example,dc=com", syncRepl.getSearchBase() );
        assertEquals( "(objectClass=organizationalPerson)", syncRepl.getFilter() );
        assertEquals( Scope.SUB, syncRepl.getScope() );

        String[] attributes = syncRepl.getAttributes();

        assertNotNull( attributes );
        assertEquals( 6, attributes.length );
        assertEquals( "cn", attributes[0] );
        assertEquals( "sn", attributes[1] );
        assertEquals( "ou", attributes[2] );
        assertEquals( "telephoneNumber", attributes[3] );
        assertEquals( "title", attributes[4] );
        assertEquals( "l", attributes[5] );

        assertEquals( SchemaChecking.OFF, syncRepl.getSchemaChecking() );
        assertEquals( BindMethod.SIMPLE, syncRepl.getBindMethod() );
        assertEquals( "cn=syncuser,dc=example,dc=com", syncRepl.getBindDn() );
        assertEquals( "secret", syncRepl.getCredentials() );
    }


    @Test
    public void testSymasDoc3WithoutSpacesMultilines() throws Exception
    {
        SyncReplParser parser = new SyncReplParser();
        SyncRepl syncRepl = parser
            .parse( "rid=123\n" +
                "provider=ldap://provider.example.com:389\n" +
                "type=refreshOnly\n" +
                "interval=01:00:00:00\n" +
                "searchbase=\"dc=example,dc=com\"\n" +
                "filter=\"(objectClass=organizationalPerson)\"\n" +
                "scope=sub\n" +
                "attrs=\"cn,sn,ou,telephoneNumber,title,l\"\n" +
                "schemachecking=off\n" +
                "bindmethod=simple\n" +
                "binddn=\"cn=syncuser,dc=example,dc=com\"\n" +
                "credentials=secret" );

        assertNotNull( syncRepl );
        assertEquals( "123", syncRepl.getRid() );

        Provider provider = syncRepl.getProvider();

        assertNotNull( provider );
        assertFalse( provider.isLdaps() );
        assertEquals( "provider.example.com", provider.getHost() );
        assertEquals( 389, provider.getPort() );

        assertEquals( Type.REFRESH_ONLY, syncRepl.getType() );

        Interval interval = syncRepl.getInterval();

        assertNotNull( interval );
        assertEquals( 1, interval.getDays() );
        assertEquals( 0, interval.getHours() );
        assertEquals( 0, interval.getMinutes() );
        assertEquals( 0, interval.getSeconds() );

        assertEquals( "dc=example,dc=com", syncRepl.getSearchBase() );
        assertEquals( "(objectClass=organizationalPerson)", syncRepl.getFilter() );
        assertEquals( Scope.SUB, syncRepl.getScope() );

        String[] attributes = syncRepl.getAttributes();

        assertNotNull( attributes );
        assertEquals( 6, attributes.length );
        assertEquals( "cn", attributes[0] );
        assertEquals( "sn", attributes[1] );
        assertEquals( "ou", attributes[2] );
        assertEquals( "telephoneNumber", attributes[3] );
        assertEquals( "title", attributes[4] );
        assertEquals( "l", attributes[5] );

        assertEquals( SchemaChecking.OFF, syncRepl.getSchemaChecking() );
        assertEquals( BindMethod.SIMPLE, syncRepl.getBindMethod() );
        assertEquals( "cn=syncuser,dc=example,dc=com", syncRepl.getBindDn() );
        assertEquals( "secret", syncRepl.getCredentials() );
    }


    @Test
    public void testSymasDoc3WithSpacesMultilines() throws Exception
    {
        SyncReplParser parser = new SyncReplParser();
        SyncRepl syncRepl = parser
            .parse( "rid=123 \n" +
                "provider=ldap://provider.example.com:389 \n" +
                "type=refreshOnly \n" +
                "interval=01:00:00:00 \n" +
                "searchbase=\"dc=example,dc=com\" \n" +
                "filter=\"(objectClass=organizationalPerson)\" \n" +
                "scope=sub \n" +
                "attrs=\"cn,sn,ou,telephoneNumber,title,l\" \n" +
                "schemachecking=off \n" +
                "bindmethod=simple \n" +
                "binddn=\"cn=syncuser,dc=example,dc=com\" \n" +
                "credentials=secret" );

        assertNotNull( syncRepl );
        assertEquals( "123", syncRepl.getRid() );

        Provider provider = syncRepl.getProvider();

        assertNotNull( provider );
        assertFalse( provider.isLdaps() );
        assertEquals( "provider.example.com", provider.getHost() );
        assertEquals( 389, provider.getPort() );

        assertEquals( Type.REFRESH_ONLY, syncRepl.getType() );

        Interval interval = syncRepl.getInterval();

        assertNotNull( interval );
        assertEquals( 1, interval.getDays() );
        assertEquals( 0, interval.getHours() );
        assertEquals( 0, interval.getMinutes() );
        assertEquals( 0, interval.getSeconds() );

        assertEquals( "dc=example,dc=com", syncRepl.getSearchBase() );
        assertEquals( "(objectClass=organizationalPerson)", syncRepl.getFilter() );
        assertEquals( Scope.SUB, syncRepl.getScope() );

        String[] attributes = syncRepl.getAttributes();

        assertNotNull( attributes );
        assertEquals( 6, attributes.length );
        assertEquals( "cn", attributes[0] );
        assertEquals( "sn", attributes[1] );
        assertEquals( "ou", attributes[2] );
        assertEquals( "telephoneNumber", attributes[3] );
        assertEquals( "title", attributes[4] );
        assertEquals( "l", attributes[5] );

        assertEquals( SchemaChecking.OFF, syncRepl.getSchemaChecking() );
        assertEquals( BindMethod.SIMPLE, syncRepl.getBindMethod() );
        assertEquals( "cn=syncuser,dc=example,dc=com", syncRepl.getBindDn() );
        assertEquals( "secret", syncRepl.getCredentials() );
    }


    @Test
    public void testSymasDoc3WithSpacesMultilinesAllQuoted() throws Exception
    {
        SyncReplParser parser = new SyncReplParser();
        SyncRepl syncRepl = parser
            .parse( "rid=\"123\" \n" +
                "provider=\"ldap://provider.example.com:389\" \n" +
                "type=\"refreshOnly\" \n" +
                "interval=\"01:00:00:00\" \n" +
                "searchbase=\"dc=example,dc=com\" \n" +
                "filter=\"(objectClass=organizationalPerson)\" \n" +
                "scope=\"sub\" \n" +
                "attrs=\"cn,sn,ou,telephoneNumber,title,l\" \n" +
                "schemachecking=\"off\" \n" +
                "bindmethod=\"simple\" \n" +
                "binddn=\"cn=syncuser,dc=example,dc=com\" \n" +
                "credentials=\"secret\"" );

        assertNotNull( syncRepl );
        assertEquals( "123", syncRepl.getRid() );

        Provider provider = syncRepl.getProvider();

        assertNotNull( provider );
        assertFalse( provider.isLdaps() );
        assertEquals( "provider.example.com", provider.getHost() );
        assertEquals( 389, provider.getPort() );

        assertEquals( Type.REFRESH_ONLY, syncRepl.getType() );

        Interval interval = syncRepl.getInterval();

        assertNotNull( interval );
        assertEquals( 1, interval.getDays() );
        assertEquals( 0, interval.getHours() );
        assertEquals( 0, interval.getMinutes() );
        assertEquals( 0, interval.getSeconds() );

        assertEquals( "dc=example,dc=com", syncRepl.getSearchBase() );
        assertEquals( "(objectClass=organizationalPerson)", syncRepl.getFilter() );
        assertEquals( Scope.SUB, syncRepl.getScope() );

        String[] attributes = syncRepl.getAttributes();

        assertNotNull( attributes );
        assertEquals( 6, attributes.length );
        assertEquals( "cn", attributes[0] );
        assertEquals( "sn", attributes[1] );
        assertEquals( "ou", attributes[2] );
        assertEquals( "telephoneNumber", attributes[3] );
        assertEquals( "title", attributes[4] );
        assertEquals( "l", attributes[5] );

        assertEquals( SchemaChecking.OFF, syncRepl.getSchemaChecking() );
        assertEquals( BindMethod.SIMPLE, syncRepl.getBindMethod() );
        assertEquals( "cn=syncuser,dc=example,dc=com", syncRepl.getBindDn() );
        assertEquals( "secret", syncRepl.getCredentials() );
    }
    @Test
    public void testSymasDoc3WithSpacesMultilinesAllNotQuoted() throws Exception
    {
        SyncReplParser parser = new SyncReplParser();
        SyncRepl syncRepl = parser
            .parse( "rid=123 \n" +
                "provider=ldap://provider.example.com:389 \n" +
                "type=refreshOnly \n" +
                "interval=01:00:00:00 \n" +
                "searchbase=dc=example,dc=com \n" +
                "filter=(objectClass=organizationalPerson) \n" +
                "scope=sub \n" +
                "attrs=cn,sn,ou,telephoneNumber,title,l \n" +
                "schemachecking=off \n" +
                "bindmethod=simple \n" +
                "binddn=cn=syncuser,dc=example,dc=com \n" +
                "credentials=secret" );

        assertNotNull( syncRepl );
        assertEquals( "123", syncRepl.getRid() );

        Provider provider = syncRepl.getProvider();

        assertNotNull( provider );
        assertFalse( provider.isLdaps() );
        assertEquals( "provider.example.com", provider.getHost() );
        assertEquals( 389, provider.getPort() );

        assertEquals( Type.REFRESH_ONLY, syncRepl.getType() );

        Interval interval = syncRepl.getInterval();

        assertNotNull( interval );
        assertEquals( 1, interval.getDays() );
        assertEquals( 0, interval.getHours() );
        assertEquals( 0, interval.getMinutes() );
        assertEquals( 0, interval.getSeconds() );

        assertEquals( "dc=example,dc=com", syncRepl.getSearchBase() );
        assertEquals( "(objectClass=organizationalPerson)", syncRepl.getFilter() );
        assertEquals( Scope.SUB, syncRepl.getScope() );

        String[] attributes = syncRepl.getAttributes();

        assertNotNull( attributes );
        assertEquals( 6, attributes.length );
        assertEquals( "cn", attributes[0] );
        assertEquals( "sn", attributes[1] );
        assertEquals( "ou", attributes[2] );
        assertEquals( "telephoneNumber", attributes[3] );
        assertEquals( "title", attributes[4] );
        assertEquals( "l", attributes[5] );

        assertEquals( SchemaChecking.OFF, syncRepl.getSchemaChecking() );
        assertEquals( BindMethod.SIMPLE, syncRepl.getBindMethod() );
        assertEquals( "cn=syncuser,dc=example,dc=com", syncRepl.getBindDn() );
        assertEquals( "secret", syncRepl.getCredentials() );
    }
}
