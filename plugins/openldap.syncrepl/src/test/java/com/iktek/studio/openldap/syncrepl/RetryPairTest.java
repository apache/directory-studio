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
public class RetryPairTest
{
    @Test
    public void testEmpty() throws Exception
    {
        try
        {
            RetryPair.parse( "" );

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
            RetryPair.parse( "12" );

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
            RetryPair.parse( "12 " );

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
            RetryPair.parse( "12 34 " );

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
        RetryPair retryPair = RetryPair.parse( "1 2" );

        assertEquals( 1, retryPair.getInterval() );
        assertEquals( 2, retryPair.getRetries() );
        assertEquals( "1 2", retryPair.toString() );
    }


    @Test
    public void testOk2() throws Exception
    {
        RetryPair retryPair = RetryPair.parse( "12 34" );

        assertEquals( 12, retryPair.getInterval() );
        assertEquals( 34, retryPair.getRetries() );
        assertEquals( "12 34", retryPair.toString() );
    }


    @Test
    public void testOk3() throws Exception
    {
        RetryPair retryPair = RetryPair.parse( "123 456" );

        assertEquals( 123, retryPair.getInterval() );
        assertEquals( 456, retryPair.getRetries() );
        assertEquals( "123 456", retryPair.toString() );
    }


    @Test
    public void testOk4() throws Exception
    {
        RetryPair retryPair = RetryPair.parse( "1234 5678" );

        assertEquals( 1234, retryPair.getInterval() );
        assertEquals( 5678, retryPair.getRetries() );
        assertEquals( "1234 5678", retryPair.toString() );
    }


    @Test
    public void testOk5() throws Exception
    {
        RetryPair retryPair = RetryPair.parse( "1 +" );

        assertEquals( 1, retryPair.getInterval() );
        assertEquals( RetryPair.PLUS, retryPair.getRetries() );
        assertEquals( "1 +", retryPair.toString() );
    }
}
