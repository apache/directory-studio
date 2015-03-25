package org.apache.directory.studio.openldap.syncrepl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;


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
