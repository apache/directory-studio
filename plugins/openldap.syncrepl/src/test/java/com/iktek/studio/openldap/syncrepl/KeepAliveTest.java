package org.apache.directory.studio.openldap.syncrepl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;


public class KeepAliveTest
{
    @Test
    public void testEmpty() throws Exception
    {
        try
        {
            KeepAlive.parse( "" );

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
            KeepAlive.parse( "12:" );

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
            KeepAlive.parse( "12:" );

            fail();
        }
        catch ( Exception e )
        {
            // Should happen
        }
    }


    @Test
    public void testIncomplete4() throws Exception
    {
        try
        {
            KeepAlive.parse( "12:34" );

            fail();
        }
        catch ( Exception e )
        {
            // Should happen
        }
    }


    @Test
    public void testIncomplete5() throws Exception
    {
        try
        {
            KeepAlive.parse( "12:34:" );

            fail();
        }
        catch ( Exception e )
        {
            // Should happen
        }
    }


    @Test
    public void testOk1() throws Exception
    {
        KeepAlive keepAlive = KeepAlive.parse( "1:2:3" );

        assertEquals( 1, keepAlive.getIdle() );
        assertEquals( 2, keepAlive.getProbes() );
        assertEquals( 3, keepAlive.getInterval() );
        assertEquals( "1:2:3", keepAlive.toString() );
    }


    @Test
    public void testOk2() throws Exception
    {
        KeepAlive keepAlive = KeepAlive.parse( "12:34:56" );

        assertEquals( 12, keepAlive.getIdle() );
        assertEquals( 34, keepAlive.getProbes() );
        assertEquals( 56, keepAlive.getInterval() );
        assertEquals( "12:34:56", keepAlive.toString() );
    }


    @Test
    public void testOk3() throws Exception
    {
        KeepAlive keepAlive = KeepAlive.parse( "123:456:789" );

        assertEquals( 123, keepAlive.getIdle() );
        assertEquals( 456, keepAlive.getProbes() );
        assertEquals( 789, keepAlive.getInterval() );
        assertEquals( "123:456:789", keepAlive.toString() );
    }


    @Test
    public void testOk4() throws Exception
    {
        KeepAlive keepAlive = KeepAlive.parse( "1234:5678:9012" );

        assertEquals( 1234, keepAlive.getIdle() );
        assertEquals( 5678, keepAlive.getProbes() );
        assertEquals( 9012, keepAlive.getInterval() );
        assertEquals( "1234:5678:9012", keepAlive.toString() );
    }
}
