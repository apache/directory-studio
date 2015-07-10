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
package org.apache.directory.studio.openldap.config.wrappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.directory.studio.openldap.config.editor.wrappers.TimeLimitWrapper;
import org.junit.Test;

/**
 * A test for the TimeLimitWrapper class
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class TimeLimitWrapperTest
{
    @Test
    public void testToString()
    {
        TimeLimitWrapper tlw = new TimeLimitWrapper( null, new Integer( 200 ), new Integer( 100 ) );
        
        assertEquals( "time.hard=200 time.soft=100", tlw.toString() );

        tlw = new TimeLimitWrapper( null, new Integer( 100 ), new Integer( 200 ) );
        
        assertEquals( "time=100", tlw.toString() );

        tlw = new TimeLimitWrapper( null, new Integer( 100 ), new Integer( 100 ) );
        
        assertEquals( "time=100", tlw.toString() );

        tlw = new TimeLimitWrapper( null, new Integer( 100 ), null );
        
        assertEquals( "time.hard=100", tlw.toString() );

        tlw = new TimeLimitWrapper( null, null, new Integer( 100 ) );
        
        assertEquals( "time.soft=100", tlw.toString() );

        tlw = new TimeLimitWrapper( null, null, null );
        
        assertEquals( "", tlw.toString() );

        tlw = new TimeLimitWrapper( new Integer( 100 ), null, null );
        
        assertEquals( "time=100", tlw.toString() );

        tlw = new TimeLimitWrapper( new Integer( 100 ), new Integer( 200 ), null );
        
        assertEquals( "time=100", tlw.toString() );

        tlw = new TimeLimitWrapper( new Integer( 100 ), null, new Integer( 200 ) );
        
        assertEquals( "time=100", tlw.toString() );

        tlw = new TimeLimitWrapper( new Integer( 100 ), new Integer( 200 ),  new Integer( 300 ) );
        
        assertEquals( "time=100", tlw.toString() );

        tlw = new TimeLimitWrapper( null, new Integer( -1 ),  new Integer( 300 ) );
        
        assertEquals( "time.hard=unlimited time.soft=300", tlw.toString() );

        tlw = new TimeLimitWrapper( null, new Integer( 200 ),  new Integer( -1 ) );
        
        assertEquals( "time=200", tlw.toString() );

        tlw = new TimeLimitWrapper( new Integer( -1 ), new Integer( 200 ),  new Integer( -1 ) );
        
        assertEquals( "time=unlimited", tlw.toString() );
    }
    
    
    @Test
    public void testIsValid()
    {
        TimeLimitWrapper timeLimitWrapper = new TimeLimitWrapper( null );
        assertTrue( timeLimitWrapper.isValid() );
        timeLimitWrapper = new TimeLimitWrapper( "" );
        assertTrue( timeLimitWrapper.isValid() );
        timeLimitWrapper = new TimeLimitWrapper( "  " );
        assertTrue( timeLimitWrapper.isValid() );
        
        timeLimitWrapper = new TimeLimitWrapper( "time=100" );
        assertTrue( timeLimitWrapper.isValid() );
        timeLimitWrapper = new TimeLimitWrapper( "time=none" );
        assertTrue( timeLimitWrapper.isValid() );
        timeLimitWrapper = new TimeLimitWrapper( "time=unlimited" );
        assertTrue( timeLimitWrapper.isValid() );
        
        timeLimitWrapper = new TimeLimitWrapper( "time.hard=100" );
        assertTrue( timeLimitWrapper.isValid() );
        timeLimitWrapper = new TimeLimitWrapper( "time.hard=none" );
        assertTrue( timeLimitWrapper.isValid() );
        timeLimitWrapper = new TimeLimitWrapper( "time.hard=unlimited" );
        assertTrue( timeLimitWrapper.isValid() );
        
        timeLimitWrapper = new TimeLimitWrapper( "time.soft=100" );
        assertTrue( timeLimitWrapper.isValid() );
        timeLimitWrapper = new TimeLimitWrapper( "time.soft=none" );
        assertTrue( timeLimitWrapper.isValid() );
        timeLimitWrapper = new TimeLimitWrapper( "time.soft=unlimited" );
        assertTrue( timeLimitWrapper.isValid() );

        timeLimitWrapper = new TimeLimitWrapper( "time.soft=100 time.hard=200" );
        assertTrue( timeLimitWrapper.isValid() );
        timeLimitWrapper = new TimeLimitWrapper( "time.hard=100 time.soft=200" );
        assertTrue( timeLimitWrapper.isValid() );

        timeLimitWrapper = new TimeLimitWrapper( "time.soft=none time.hard=200" );
        assertTrue( timeLimitWrapper.isValid() );
        timeLimitWrapper = new TimeLimitWrapper( "time.hard=100 time.soft=unlimited" );
        assertTrue( timeLimitWrapper.isValid() );

        timeLimitWrapper = new TimeLimitWrapper( "time.hard=soft time.soft=unlimited time=100" );
        assertTrue( timeLimitWrapper.isValid() );
    }
    
    
    @Test
    public void testCreateTimeLimit()
    {
        TimeLimitWrapper tlw = new TimeLimitWrapper( null );
        assertEquals( null, tlw.getGlobalLimit() );
        assertEquals( null,tlw.getSoftLimit() );
        assertEquals( null, tlw.getHardLimit() );

        tlw = new TimeLimitWrapper( "" );
        assertEquals( null, tlw.getGlobalLimit() );
        assertEquals( null, tlw.getSoftLimit() );
        assertEquals( null, tlw.getHardLimit() );
        
        tlw = new TimeLimitWrapper( "  " );
        assertEquals( null, tlw.getGlobalLimit() );
        assertEquals( null, tlw.getSoftLimit() );
        assertEquals( null, tlw.getHardLimit() );

        tlw = new TimeLimitWrapper( "time=100" );
        assertEquals( 100, tlw.getGlobalLimit().intValue() );
        assertEquals( null, tlw.getSoftLimit() );
        assertEquals( null, tlw.getHardLimit() );

        tlw = new TimeLimitWrapper( "time=none" );
        assertEquals( -1, tlw.getGlobalLimit().intValue() );
        assertEquals( null, tlw.getSoftLimit() );
        assertEquals( null, tlw.getHardLimit() );

        tlw = new TimeLimitWrapper( "time=unlimited" );
        assertEquals( -1, tlw.getGlobalLimit().intValue() );
        assertEquals( null, tlw.getSoftLimit() );
        assertEquals( null, tlw.getHardLimit() );


        tlw = new TimeLimitWrapper( "time.hard=100" );
        assertEquals( null, tlw.getGlobalLimit() );
        assertEquals( null, tlw.getSoftLimit() );
        assertEquals( 100, tlw.getHardLimit().intValue() );

        tlw = new TimeLimitWrapper( "time.hard=none" );
        assertEquals( null, tlw.getGlobalLimit() );
        assertEquals( null, tlw.getSoftLimit() );
        assertEquals( -1, tlw.getHardLimit().intValue() );

        tlw = new TimeLimitWrapper( "time.hard=unlimited" );
        assertEquals( null, tlw.getGlobalLimit() );
        assertEquals( null, tlw.getSoftLimit() );
        assertEquals( -1, tlw.getHardLimit().intValue() );
        
        tlw = new TimeLimitWrapper( "time.soft=100" );
        assertEquals( null, tlw.getGlobalLimit() );
        assertEquals( 100, tlw.getSoftLimit().intValue() );
        assertEquals( null, tlw.getHardLimit() );

        tlw = new TimeLimitWrapper( "time.soft=none" );
        assertEquals( null, tlw.getGlobalLimit() );
        assertEquals( -1, tlw.getSoftLimit().intValue() );
        assertEquals( null, tlw.getHardLimit() );

        tlw = new TimeLimitWrapper( "time.soft=unlimited" );
        assertEquals( null, tlw.getGlobalLimit() );
        assertEquals( -1, tlw.getSoftLimit().intValue() );
        assertEquals( null, tlw.getHardLimit() );

        tlw = new TimeLimitWrapper( "time.soft=100 time.hard=200" );
        assertEquals( null, tlw.getGlobalLimit() );
        assertEquals( 100, tlw.getSoftLimit().intValue() );
        assertEquals( 200, tlw.getHardLimit().intValue() );

        tlw = new TimeLimitWrapper( "time.hard=100 time.soft=200" );
        assertEquals( null, tlw.getGlobalLimit() );
        assertEquals( 200, tlw.getSoftLimit().intValue() );
        assertEquals( 100, tlw.getHardLimit().intValue() );

        tlw = new TimeLimitWrapper( "time.soft=none time.hard=200" );
        assertEquals( null, tlw.getGlobalLimit() );
        assertEquals( -1, tlw.getSoftLimit().intValue() );
        assertEquals( 200, tlw.getHardLimit().intValue() );
        
        tlw = new TimeLimitWrapper( "time.hard=100 time.soft=unlimited" );
        assertEquals( null, tlw.getGlobalLimit() );
        assertEquals( -1, tlw.getSoftLimit().intValue() );
        assertEquals( 100, tlw.getHardLimit().intValue() );

        tlw = new TimeLimitWrapper( "time.hard=soft time.soft=unlimited time=100" );
        assertEquals( 100, tlw.getGlobalLimit().intValue() );
        assertEquals( null, tlw.getSoftLimit() );
        assertEquals( null, tlw.getHardLimit() );
    }
}
