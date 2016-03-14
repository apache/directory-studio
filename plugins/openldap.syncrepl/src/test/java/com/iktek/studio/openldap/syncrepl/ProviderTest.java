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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ProviderTest
{
    @Test
    public void testEmpty() throws Exception
    {
        try
        {
            Provider.parse( "" );

            fail();
        }
        catch ( Exception e )
        {
            // Should happen
        }
    }


    @Test
    public void testIncomplete1() throws Exception
    {
        try
        {
            Provider.parse( "ldap" );

            fail();
        }
        catch ( Exception e )
        {
            // Should happen
        }
    }


    @Test
    public void testIncomplete2() throws Exception
    {
        try
        {
            Provider.parse( "ldap:" );

            fail();
        }
        catch ( Exception e )
        {
            // Should happen
        }
    }


    @Test
    public void testIncomplete3() throws Exception
    {
        try
        {
            Provider.parse( "ldap://" );

            fail();
        }
        catch ( Exception e )
        {
            // Should happen
        }
    }


    @Test
    public void testOkLdapHost() throws Exception
    {
        Provider provider = Provider.parse( "ldap://localhost" );

        assertEquals( false, provider.isLdaps() );
        assertEquals( "localhost", provider.getHost() );
        assertEquals( Provider.NO_PORT, provider.getPort() );
        assertEquals( "ldap://localhost", provider.toString() );
    }


    @Test
    public void testOkLdapsHost() throws Exception
    {
        Provider provider = Provider.parse( "ldaps://localhost" );

        assertEquals( true, provider.isLdaps() );
        assertEquals( "localhost", provider.getHost() );
        assertEquals( Provider.NO_PORT, provider.getPort() );
        assertEquals( "ldaps://localhost", provider.toString() );
    }


    @Test
    public void testOkLdapHostPort() throws Exception
    {
        Provider provider = Provider.parse( "ldap://localhost:12345" );

        assertEquals( false, provider.isLdaps() );
        assertEquals( "localhost", provider.getHost() );
        assertEquals( 12345, provider.getPort() );
        assertEquals( "ldap://localhost:12345", provider.toString() );
    }


    @Test
    public void testOkLdapsHostPort() throws Exception
    {
        Provider provider = Provider.parse( "ldaps://localhost:12345" );

        assertEquals( true, provider.isLdaps() );
        assertEquals( "localhost", provider.getHost() );
        assertEquals( 12345, provider.getPort() );
        assertEquals( "ldaps://localhost:12345", provider.toString() );
    }


    @Test
    public void testOkUppercaseProtocolLdapHost() throws Exception
    {
        Provider provider = Provider.parse( "LDAP://localhost" );

        assertEquals( false, provider.isLdaps() );
        assertEquals( "localhost", provider.getHost() );
        assertEquals( Provider.NO_PORT, provider.getPort() );
        assertEquals( "ldap://localhost", provider.toString() );
    }


    @Test
    public void testOkUppercaseProtocolLdapsHost() throws Exception
    {
        Provider provider = Provider.parse( "LDAPS://localhost" );

        assertEquals( true, provider.isLdaps() );
        assertEquals( "localhost", provider.getHost() );
        assertEquals( Provider.NO_PORT, provider.getPort() );
        assertEquals( "ldaps://localhost", provider.toString() );
    }


    @Test
    public void testOkUppercaseProtocolLdapHostPort() throws Exception
    {
        Provider provider = Provider.parse( "LDAP://localhost:12345" );

        assertEquals( false, provider.isLdaps() );
        assertEquals( "localhost", provider.getHost() );
        assertEquals( 12345, provider.getPort() );
        assertEquals( "ldap://localhost:12345", provider.toString() );
    }


    @Test
    public void testOkUppercaseProtocolLdapsHostPort() throws Exception
    {
        Provider provider = Provider.parse( "LDAPS://localhost:12345" );

        assertEquals( true, provider.isLdaps() );
        assertEquals( "localhost", provider.getHost() );
        assertEquals( 12345, provider.getPort() );
        assertEquals( "ldaps://localhost:12345", provider.toString() );
    }
}
