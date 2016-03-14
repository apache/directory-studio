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
package org.apache.directory.studio.openldap.config.editor.dialogs;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.text.ParseException;

import org.junit.Test;


/**
 * This class tests the {@link PurgeTimeSpan} class.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PurgeTimeSpanTest
{
    @Test
    public void testArguments() throws Exception
    {
        try
        {
            new PurgeTimeSpan( -1, 0, 0, 0 );
            fail();
        }
        catch ( IllegalArgumentException e )
        {
            // Should to pass here
        }

        try
        {
            new PurgeTimeSpan( 100000, 0, 0, 0 );
            fail();
        }
        catch ( IllegalArgumentException e )
        {
            // Should to pass here
        }

        try
        {
            new PurgeTimeSpan( 0, -1, 0, 0 );
            fail();
        }
        catch ( IllegalArgumentException e )
        {
            // Should to pass here
        }

        try
        {
            new PurgeTimeSpan( 0, 61, 0, 0 );
            fail();
        }
        catch ( IllegalArgumentException e )
        {
            // Should to pass here
        }

        try
        {
            new PurgeTimeSpan( 0, 0, -1, 0 );
            fail();
        }
        catch ( IllegalArgumentException e )
        {
            // Should to pass here
        }

        try
        {
            new PurgeTimeSpan( 0, 0, 61, 0 );
            fail();
        }
        catch ( IllegalArgumentException e )
        {
            // Should to pass here
        }

        try
        {
            new PurgeTimeSpan( 0, 0, 0, -1 );
            fail();
        }
        catch ( IllegalArgumentException e )
        {
            // Should to pass here
        }

        try
        {
            new PurgeTimeSpan( 0, 0, 0, 61 );
            fail();
        }
        catch ( IllegalArgumentException e )
        {
            // Should to pass here
        }
    }


    @Test
    public void testTooShort()
    {
        try
        {
            new PurgeTimeSpan( "0" );
            fail();
        }
        catch ( ParseException e )
        {
            // Should to pass here
        }

        try
        {
            new PurgeTimeSpan( "00" );
            fail();
        }
        catch ( ParseException e )
        {
            // Should to pass here
        }

        try
        {
            new PurgeTimeSpan( "00:" );
            fail();
        }
        catch ( ParseException e )
        {
            // Should to pass here
        }

        try
        {
            new PurgeTimeSpan( "00:0" );
            fail();
        }
        catch ( ParseException e )
        {
            // Should to pass here
        }
    }


    @Test
    public void testParsing() throws Exception
    {
        PurgeTimeSpan span1 = new PurgeTimeSpan( "12:34" );
        assertEquals( 0, span1.getDays() );
        assertEquals( 12, span1.getHours() );
        assertEquals( 34, span1.getMinutes() );
        assertEquals( 0, span1.getSeconds() );

        PurgeTimeSpan span2 = new PurgeTimeSpan( "12:34:56" );
        assertEquals( 0, span2.getDays() );
        assertEquals( 12, span2.getHours() );
        assertEquals( 34, span2.getMinutes() );
        assertEquals( 56, span2.getSeconds() );

        PurgeTimeSpan span3 = new PurgeTimeSpan( "1+23:45" );
        assertEquals( 1, span3.getDays() );
        assertEquals( 23, span3.getHours() );
        assertEquals( 45, span3.getMinutes() );
        assertEquals( 0, span3.getSeconds() );

        PurgeTimeSpan span4 = new PurgeTimeSpan( "12+14:56" );
        assertEquals( 12, span4.getDays() );
        assertEquals( 14, span4.getHours() );
        assertEquals( 56, span4.getMinutes() );
        assertEquals( 0, span4.getSeconds() );

        PurgeTimeSpan span5 = new PurgeTimeSpan( "123+15:37" );
        assertEquals( 123, span5.getDays() );
        assertEquals( 15, span5.getHours() );
        assertEquals( 37, span5.getMinutes() );
        assertEquals( 0, span5.getSeconds() );

        PurgeTimeSpan span6 = new PurgeTimeSpan( "1234+16:38" );
        assertEquals( 1234, span6.getDays() );
        assertEquals( 16, span6.getHours() );
        assertEquals( 38, span6.getMinutes() );
        assertEquals( 0, span6.getSeconds() );

        PurgeTimeSpan span7 = new PurgeTimeSpan( "12345+17:39" );
        assertEquals( 12345, span7.getDays() );
        assertEquals( 17, span7.getHours() );
        assertEquals( 39, span7.getMinutes() );
        assertEquals( 0, span7.getSeconds() );


        PurgeTimeSpan span8 = new PurgeTimeSpan( "1+23:45:41" );
        assertEquals( 1, span8.getDays() );
        assertEquals( 23, span8.getHours() );
        assertEquals( 45, span8.getMinutes() );
        assertEquals( 41, span8.getSeconds() );

        PurgeTimeSpan span9 = new PurgeTimeSpan( "12+14:56:42" );
        assertEquals( 12, span9.getDays() );
        assertEquals( 14, span9.getHours() );
        assertEquals( 56, span9.getMinutes() );
        assertEquals( 42, span9.getSeconds() );

        PurgeTimeSpan span10 = new PurgeTimeSpan( "123+15:37:43" );
        assertEquals( 123, span10.getDays() );
        assertEquals( 15, span10.getHours() );
        assertEquals( 37, span10.getMinutes() );
        assertEquals( 43, span10.getSeconds() );

        PurgeTimeSpan span11 = new PurgeTimeSpan( "1234+16:38:45" );
        assertEquals( 1234, span11.getDays() );
        assertEquals( 16, span11.getHours() );
        assertEquals( 38, span11.getMinutes() );
        assertEquals( 45, span11.getSeconds() );

        PurgeTimeSpan span12 = new PurgeTimeSpan( "12345+17:39:46" );
        assertEquals( 12345, span12.getDays() );
        assertEquals( 17, span12.getHours() );
        assertEquals( 39, span12.getMinutes() );
        assertEquals( 46, span12.getSeconds() );
    }


    @Test
    public void testToString() throws Exception
    {
        PurgeTimeSpan span1 = new PurgeTimeSpan( 0, 1, 2, 0 );
        assertEquals( "01:02", span1.toString() );

        PurgeTimeSpan span2 = new PurgeTimeSpan( 1, 2, 3, 0 );
        assertEquals( "1+02:03", span2.toString() );

        PurgeTimeSpan span3 = new PurgeTimeSpan( 12, 3, 4, 5 );
        assertEquals( "12+03:04:05", span3.toString() );

        PurgeTimeSpan span4 = new PurgeTimeSpan( 123, 4, 5, 6 );
        assertEquals( "123+04:05:06", span4.toString() );

        PurgeTimeSpan span5 = new PurgeTimeSpan( 1234, 5, 6, 7 );
        assertEquals( "1234+05:06:07", span5.toString() );

        PurgeTimeSpan span6 = new PurgeTimeSpan( 12345, 6, 7, 8 );
        assertEquals( "12345+06:07:08", span6.toString() );

        PurgeTimeSpan span7 = new PurgeTimeSpan( 0, 1, 2, 3 );
        assertEquals( "01:02:03", span7.toString() );
    }
}
