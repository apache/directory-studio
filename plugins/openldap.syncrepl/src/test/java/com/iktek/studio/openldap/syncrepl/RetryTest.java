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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class RetryTest
{
    @Test
    public void testEmpty() throws Exception
    {
        try
        {
            Retry.parse( "" );

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
            Retry.parse( "12" );

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
            Retry.parse( "12 " );

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
            Retry.parse( "12 34 " );

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
        Retry retry = Retry.parse( "1 2" );

        assertNotNull( retry );
        assertEquals( 1, retry.size() );

        RetryPair pair = retry.getPairs()[0];
        assertNotNull( pair );
        assertEquals( 1, pair.getInterval() );
        assertEquals( 2, pair.getRetries() );

        assertEquals( "1 2", retry.toString() );
    }


    @Test
    public void testOk2() throws Exception
    {
        Retry retry = Retry.parse( "12 34" );

        assertNotNull( retry );
        assertEquals( 1, retry.size() );

        RetryPair pair = retry.getPairs()[0];
        assertNotNull( pair );
        assertEquals( 12, pair.getInterval() );
        assertEquals( 34, pair.getRetries() );

        assertEquals( "12 34", retry.toString() );
    }


    @Test
    public void testOk3() throws Exception
    {
        Retry retry = Retry.parse( "123 456" );

        assertNotNull( retry );
        assertEquals( 1, retry.size() );

        RetryPair pair = retry.getPairs()[0];
        assertNotNull( pair );
        assertEquals( 123, pair.getInterval() );
        assertEquals( 456, pair.getRetries() );

        assertEquals( "123 456", retry.toString() );
    }


    @Test
    public void testOk4() throws Exception
    {
        Retry retry = Retry.parse( "1234 5678" );

        assertNotNull( retry );
        assertEquals( 1, retry.size() );

        RetryPair pair = retry.getPairs()[0];
        assertNotNull( pair );
        assertEquals( 1234, pair.getInterval() );
        assertEquals( 5678, pair.getRetries() );

        assertEquals( "1234 5678", retry.toString() );
    }


    @Test
    public void testOk5() throws Exception
    {
        Retry retry = Retry.parse( "1 +" );

        assertNotNull( retry );
        assertEquals( 1, retry.size() );

        RetryPair pair = retry.getPairs()[0];
        assertNotNull( pair );
        assertEquals( 1, pair.getInterval() );
        assertEquals( RetryPair.PLUS, pair.getRetries() );

        assertEquals( "1 +", retry.toString() );
    }


    @Test
    public void testOk6() throws Exception
    {
        Retry retry = Retry.parse( "1 2 3 4" );

        assertNotNull( retry );
        assertEquals( 2, retry.size() );

        RetryPair pair1 = retry.getPairs()[0];
        assertNotNull( pair1 );
        assertEquals( 1, pair1.getInterval() );
        assertEquals( 2, pair1.getRetries() );

        RetryPair pair2 = retry.getPairs()[1];
        assertNotNull( pair2 );
        assertEquals( 3, pair2.getInterval() );
        assertEquals( 4, pair2.getRetries() );

        assertEquals( "1 2 3 4", retry.toString() );
    }


    @Test
    public void testOk7() throws Exception
    {
        Retry retry = Retry.parse( "12 34 56 78" );

        assertNotNull( retry );
        assertEquals( 2, retry.size() );

        RetryPair pair1 = retry.getPairs()[0];
        assertNotNull( pair1 );
        assertEquals( 12, pair1.getInterval() );
        assertEquals( 34, pair1.getRetries() );

        RetryPair pair2 = retry.getPairs()[1];
        assertNotNull( pair2 );
        assertEquals( 56, pair2.getInterval() );
        assertEquals( 78, pair2.getRetries() );

        assertEquals( "12 34 56 78", retry.toString() );
    }


    @Test
    public void testOk8() throws Exception
    {
        Retry retry = Retry.parse( "123 456 789 123" );

        assertNotNull( retry );
        assertEquals( 2, retry.size() );

        RetryPair pair1 = retry.getPairs()[0];
        assertNotNull( pair1 );
        assertEquals( 123, pair1.getInterval() );
        assertEquals( 456, pair1.getRetries() );

        RetryPair pair2 = retry.getPairs()[1];
        assertNotNull( pair2 );
        assertEquals( 789, pair2.getInterval() );
        assertEquals( 123, pair2.getRetries() );

        assertEquals( "123 456 789 123", retry.toString() );
    }


    @Test
    public void testOk9() throws Exception
    {
        Retry retry = Retry.parse( "1234 5678 9123 4567" );

        assertNotNull( retry );
        assertEquals( 2, retry.size() );

        RetryPair pair1 = retry.getPairs()[0];
        assertNotNull( pair1 );
        assertEquals( 1234, pair1.getInterval() );
        assertEquals( 5678, pair1.getRetries() );

        RetryPair pair2 = retry.getPairs()[1];
        assertNotNull( pair2 );
        assertEquals( 9123, pair2.getInterval() );
        assertEquals( 4567, pair2.getRetries() );

        assertEquals( "1234 5678 9123 4567", retry.toString() );
    }


    @Test
    public void testOk10() throws Exception
    {
        Retry retry = Retry.parse( "1 + 2 +" );

        assertNotNull( retry );
        assertEquals( 2, retry.size() );

        RetryPair pair1 = retry.getPairs()[0];
        assertNotNull( pair1 );
        assertEquals( 1, pair1.getInterval() );
        assertEquals( RetryPair.PLUS, pair1.getRetries() );

        RetryPair pair2 = retry.getPairs()[1];
        assertNotNull( pair2 );
        assertEquals( 2, pair2.getInterval() );
        assertEquals( RetryPair.PLUS, pair2.getRetries() );

        assertEquals( "1 + 2 +", retry.toString() );
    }


    @Test
    public void testOk11() throws Exception
    {
        Retry retry = Retry.parse( "1 2 3 4 5 6" );

        assertNotNull( retry );
        assertEquals( 3, retry.size() );

        RetryPair pair1 = retry.getPairs()[0];
        assertNotNull( pair1 );
        assertEquals( 1, pair1.getInterval() );
        assertEquals( 2, pair1.getRetries() );

        RetryPair pair2 = retry.getPairs()[1];
        assertNotNull( pair2 );
        assertEquals( 3, pair2.getInterval() );
        assertEquals( 4, pair2.getRetries() );

        RetryPair pair3 = retry.getPairs()[2];
        assertNotNull( pair3 );
        assertEquals( 5, pair3.getInterval() );
        assertEquals( 6, pair3.getRetries() );

        assertEquals( "1 2 3 4 5 6", retry.toString() );
    }


    @Test
    public void testOk12() throws Exception
    {
        Retry retry = Retry.parse( "12 34 56 78 90 12" );

        assertNotNull( retry );
        assertEquals( 3, retry.size() );

        RetryPair pair1 = retry.getPairs()[0];
        assertNotNull( pair1 );
        assertEquals( 12, pair1.getInterval() );
        assertEquals( 34, pair1.getRetries() );

        RetryPair pair2 = retry.getPairs()[1];
        assertNotNull( pair2 );
        assertEquals( 56, pair2.getInterval() );
        assertEquals( 78, pair2.getRetries() );

        RetryPair pair3 = retry.getPairs()[2];
        assertNotNull( pair3 );
        assertEquals( 90, pair3.getInterval() );
        assertEquals( 12, pair3.getRetries() );

        assertEquals( "12 34 56 78 90 12", retry.toString() );
    }


    @Test
    public void testOk13() throws Exception
    {
        Retry retry = Retry.parse( "123 456 789 123 456 789" );

        assertNotNull( retry );
        assertEquals( 3, retry.size() );

        RetryPair pair1 = retry.getPairs()[0];
        assertNotNull( pair1 );
        assertEquals( 123, pair1.getInterval() );
        assertEquals( 456, pair1.getRetries() );

        RetryPair pair2 = retry.getPairs()[1];
        assertNotNull( pair2 );
        assertEquals( 789, pair2.getInterval() );
        assertEquals( 123, pair2.getRetries() );

        RetryPair pair3 = retry.getPairs()[2];
        assertNotNull( pair3 );
        assertEquals( 456, pair3.getInterval() );
        assertEquals( 789, pair3.getRetries() );

        assertEquals( "123 456 789 123 456 789", retry.toString() );
    }


    @Test
    public void testOk14() throws Exception
    {
        Retry retry = Retry.parse( "1234 5678 9123 4567 8901 2345" );

        assertNotNull( retry );
        assertEquals( 3, retry.size() );

        RetryPair pair1 = retry.getPairs()[0];
        assertNotNull( pair1 );
        assertEquals( 1234, pair1.getInterval() );
        assertEquals( 5678, pair1.getRetries() );

        RetryPair pair2 = retry.getPairs()[1];
        assertNotNull( pair2 );
        assertEquals( 9123, pair2.getInterval() );
        assertEquals( 4567, pair2.getRetries() );

        RetryPair pair3 = retry.getPairs()[2];
        assertNotNull( pair3 );
        assertEquals( 8901, pair3.getInterval() );
        assertEquals( 2345, pair3.getRetries() );

        assertEquals( "1234 5678 9123 4567 8901 2345", retry.toString() );
    }


    @Test
    public void testOk15() throws Exception
    {
        Retry retry = Retry.parse( "1 + 2 + 3 +" );

        assertNotNull( retry );
        assertEquals( 3, retry.size() );

        RetryPair pair1 = retry.getPairs()[0];
        assertNotNull( pair1 );
        assertEquals( 1, pair1.getInterval() );
        assertEquals( RetryPair.PLUS, pair1.getRetries() );

        RetryPair pair2 = retry.getPairs()[1];
        assertNotNull( pair2 );
        assertEquals( 2, pair2.getInterval() );
        assertEquals( RetryPair.PLUS, pair2.getRetries() );

        RetryPair pair3 = retry.getPairs()[2];
        assertNotNull( pair3 );
        assertEquals( 3, pair3.getInterval() );
        assertEquals( RetryPair.PLUS, pair3.getRetries() );

        assertEquals( "1 + 2 + 3 +", retry.toString() );
    }
}
