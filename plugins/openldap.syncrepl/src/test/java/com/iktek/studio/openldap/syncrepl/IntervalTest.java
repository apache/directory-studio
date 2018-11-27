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
