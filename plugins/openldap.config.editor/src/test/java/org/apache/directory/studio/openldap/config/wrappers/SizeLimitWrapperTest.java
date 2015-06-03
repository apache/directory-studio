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
import static org.junit.Assert.assertFalse;

import org.apache.directory.studio.openldap.config.editor.wrappers.SizeLimitWrapper;
import org.junit.Test;

/**
 * A test for the SizeLimitWrapper class
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SizeLimitWrapperTest
{
    @Test
    public void testToString()
    {
        SizeLimitWrapper slw = new SizeLimitWrapper( null, new Integer( 200 ), new Integer( 100 ), null, null, null, false );
        
        assertEquals( "size.hard=200 size.soft=100", slw.toString() );

        slw = new SizeLimitWrapper( null, new Integer( 100 ), new Integer( 200 ), null, null, null, false );
        
        assertEquals( "size=100", slw.toString() );

        slw = new SizeLimitWrapper( null, new Integer( 100 ), new Integer( 100 ), null, null, null, false );
        
        assertEquals( "size=100", slw.toString() );

        slw = new SizeLimitWrapper( null, new Integer( 100 ), null, null, null, null, false );
        
        assertEquals( "size.hard=100", slw.toString() );

        slw = new SizeLimitWrapper( null, null, new Integer( 100 ), null, null, null, false );
        
        assertEquals( "size.soft=100", slw.toString() );

        slw = new SizeLimitWrapper( null, null, null, null, null, null, false );
        
        assertEquals( "", slw.toString() );

        slw = new SizeLimitWrapper( new Integer( 100 ), null, null, null, null, null, false );
        
        assertEquals( "size=100", slw.toString() );

        slw = new SizeLimitWrapper( new Integer( 100 ), new Integer( 200 ), null, null, null, null, false );
        
        assertEquals( "size=100", slw.toString() );

        slw = new SizeLimitWrapper( new Integer( 100 ), null, new Integer( 200 ), null, null, null, false );
        
        assertEquals( "size=100", slw.toString() );

        slw = new SizeLimitWrapper( new Integer( 100 ), new Integer( 200 ),  new Integer( 300 ), null, null, null, false );
        
        assertEquals( "size=100", slw.toString() );

        slw = new SizeLimitWrapper( null, new Integer( -1 ),  new Integer( 300 ), null, null, null, false );
        
        assertEquals( "size.hard=unlimited size.soft=300", slw.toString() );

        slw = new SizeLimitWrapper( null, new Integer( 200 ),  new Integer( -1 ), null, null, null, false );
        
        assertEquals( "size=200", slw.toString() );

        slw = new SizeLimitWrapper( new Integer( -1 ), new Integer( 200 ),  new Integer( -1 ), null, null, null, false );
        
        assertEquals( "size=unlimited", slw.toString() );
    }
    
    
    @Test
    public void testIsValid()
    {
        assertTrue( SizeLimitWrapper.isValid( null ) );
        assertTrue( SizeLimitWrapper.isValid( "" ) );
        assertTrue( SizeLimitWrapper.isValid( "  " ) );
        
        assertTrue( SizeLimitWrapper.isValid( "size=100" ) );
        assertTrue( SizeLimitWrapper.isValid( "size=none" ) );
        assertTrue( SizeLimitWrapper.isValid( "size=unlimited" ) );
        
        assertTrue( SizeLimitWrapper.isValid( "size.hard=100" ) );
        assertTrue( SizeLimitWrapper.isValid( "size.hard=none" ) );
        assertTrue( SizeLimitWrapper.isValid( "size.hard=unlimited" ) );
        
        assertTrue( SizeLimitWrapper.isValid( "size.soft=100" ) );
        assertTrue( SizeLimitWrapper.isValid( "size.soft=none" ) );
        assertTrue( SizeLimitWrapper.isValid( "size.soft=unlimited" ) );

        assertTrue( SizeLimitWrapper.isValid( "size.soft=100 size.hard=200" ) );
        assertTrue( SizeLimitWrapper.isValid( "size.hard=100 size.soft=200" ) );

        assertTrue( SizeLimitWrapper.isValid( "size.soft=none size.hard=200" ) );
        assertTrue( SizeLimitWrapper.isValid( "size.hard=100 size.soft=unlimited" ) );

        assertTrue( SizeLimitWrapper.isValid( "size.hard=soft size.soft=unlimited size=100" ) );
    }
    
    
    @Test
    public void testCreateSizeLimit()
    {
        SizeLimitWrapper slw = new SizeLimitWrapper( null );
        assertEquals( null, slw.getGlobalLimit() );
        assertEquals( null, slw.getSoftLimit() );
        assertEquals( null, slw.getHardLimit() );

        slw = new SizeLimitWrapper( "" );
        assertEquals( null, slw.getGlobalLimit() );
        assertEquals( null, slw.getSoftLimit() );
        assertEquals( null, slw.getHardLimit() );
        
        slw = new SizeLimitWrapper( "  " );
        assertEquals( null, slw.getGlobalLimit() );
        assertEquals( null, slw.getSoftLimit() );
        assertEquals( null, slw.getHardLimit() );

        slw = new SizeLimitWrapper( "size=100" );
        assertEquals( 100, slw.getGlobalLimit().intValue() );
        assertEquals( null, slw.getSoftLimit() );
        assertEquals( null, slw.getHardLimit() );

        slw = new SizeLimitWrapper( "size=none" );
        assertEquals( -1, slw.getGlobalLimit().intValue() );
        assertEquals( null, slw.getSoftLimit() );
        assertEquals( null, slw.getHardLimit() );

        slw = new SizeLimitWrapper( "size=unlimited" );
        assertEquals( -1, slw.getGlobalLimit().intValue() );
        assertEquals( null, slw.getSoftLimit() );
        assertEquals( null, slw.getHardLimit() );


        slw = new SizeLimitWrapper( "size.hard=100" );
        assertEquals( null, slw.getGlobalLimit() );
        assertEquals( null, slw.getSoftLimit() );
        assertEquals( 100, slw.getHardLimit().intValue() );

        slw = new SizeLimitWrapper( "size.hard=none" );
        assertEquals( null, slw.getGlobalLimit() );
        assertEquals( null, slw.getSoftLimit() );
        assertEquals( -1, slw.getHardLimit().intValue() );

        slw = new SizeLimitWrapper( "size.hard=unlimited" );
        assertEquals( null, slw.getGlobalLimit() );
        assertEquals( null, slw.getSoftLimit() );
        assertEquals( -1, slw.getHardLimit().intValue() );
        
        slw = new SizeLimitWrapper( "size.soft=100" );
        assertEquals( null, slw.getGlobalLimit() );
        assertEquals( 100, slw.getSoftLimit().intValue() );
        assertEquals( null, slw.getHardLimit() );

        slw = new SizeLimitWrapper( "size.soft=none" );
        assertEquals( null, slw.getGlobalLimit() );
        assertEquals( -1, slw.getSoftLimit().intValue() );
        assertEquals( null, slw.getHardLimit() );

        slw = new SizeLimitWrapper( "size.soft=unlimited" );
        assertEquals( null, slw.getGlobalLimit() );
        assertEquals( -1, slw.getSoftLimit().intValue() );
        assertEquals( null, slw.getHardLimit() );

        slw = new SizeLimitWrapper( "size.soft=100 size.hard=200" );
        assertEquals( null, slw.getGlobalLimit() );
        assertEquals( 100, slw.getSoftLimit().intValue() );
        assertEquals( 200, slw.getHardLimit().intValue() );

        slw = new SizeLimitWrapper( "size.hard=100 size.soft=200" );
        assertEquals( null, slw.getGlobalLimit() );
        assertEquals( 200, slw.getSoftLimit().intValue() );
        assertEquals( 100, slw.getHardLimit().intValue() );

        slw = new SizeLimitWrapper( "size.soft=none size.hard=200" );
        assertEquals( null, slw.getGlobalLimit() );
        assertEquals( -1, slw.getSoftLimit().intValue() );
        assertEquals( 200, slw.getHardLimit().intValue() );
        
        slw = new SizeLimitWrapper( "size.hard=100 size.soft=unlimited" );
        assertEquals( null, slw.getGlobalLimit() );
        assertEquals( -1, slw.getSoftLimit().intValue() );
        assertEquals( 100, slw.getHardLimit().intValue() );

        slw = new SizeLimitWrapper( "size.hard=soft size.soft=unlimited size=100" );
        assertEquals( 100, slw.getGlobalLimit().intValue() );
        assertEquals( null, slw.getSoftLimit() );
        assertEquals( null, slw.getHardLimit() );
        assertFalse( slw.isNoEstimate() );

        slw = new SizeLimitWrapper( "size.hard=100 size.soft=50 size.unchecked=20 size.pr=10 size.prtotal=20 size.pr=noEstimate" );
        assertEquals( null, slw.getGlobalLimit() );
        assertEquals( 50, slw.getSoftLimit().intValue() );
        assertEquals( 100, slw.getHardLimit().intValue() );
        assertEquals( 20, slw.getUncheckedLimit().intValue() );
        assertEquals( 10, slw.getPrLimit().intValue() );
        assertEquals( 20, slw.getPrTotalLimit().intValue() );
        assertTrue( slw.isNoEstimate() );
    }
}
