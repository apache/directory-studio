package org.apache.directory.studio.openldap.syncrepl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;


public class IntervalTest
{
    @Test
    public void testEmpty() throws Exception
    {
        try
        {
            Interval.parse( "" );

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
            Interval.parse( "12" );

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
            Interval.parse( "12:" );

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
            Interval.parse( "12:34" );

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
            Interval.parse( "12:34:" );

            fail();
        }
        catch ( Exception e )
        {
            // Should happen
        }
    }


    @Test
    public void testIncomplete6() throws Exception
    {
        try
        {
            Interval.parse( "12:34:56" );

            fail();
        }
        catch ( Exception e )
        {
            // Should happen
        }
    }


    @Test
    public void testIncomplete7() throws Exception
    {
        try
        {
            Interval.parse( "12:34:56:" );

            fail();
        }
        catch ( Exception e )
        {
            // Should happen
        }
    }


    @Test
    public void testOk() throws Exception
    {
        Interval interval = Interval.parse( "12:34:56:78" );

        assertEquals( 12, interval.getDays() );
        assertEquals( 34, interval.getHours() );
        assertEquals( 56, interval.getMinutes() );
        assertEquals( 78, interval.getSeconds() );
        assertEquals( "12:34:56:78", interval.toString() );
    }
}
