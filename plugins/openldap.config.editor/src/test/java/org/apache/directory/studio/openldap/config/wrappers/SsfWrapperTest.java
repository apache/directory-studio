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

import org.apache.directory.studio.openldap.common.ui.SsfEnum;
import org.apache.directory.studio.openldap.config.editor.wrappers.SsfWrapper;
import org.junit.Test;

/**
 * A test for the SsfWrapper class
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SsfWrapperTest
{
    @Test
    public void testToString()
    {
        SsfWrapper ssfWrapper = new SsfWrapper( "ssf", 128 );
        
        assertEquals( "ssf=128", ssfWrapper.toString() );

        ssfWrapper = new SsfWrapper( "SSF", 128 );
        
        assertEquals( "ssf=128", ssfWrapper.toString() );

        ssfWrapper = new SsfWrapper( "ABC", 128 );
        
        assertEquals( "", ssfWrapper.toString() );
    }
    
    
    @Test
    public void testIsValid()
    {
        assertFalse( SsfWrapper.isValid( null ) );
        assertFalse( SsfWrapper.isValid( "" ) );
        assertFalse( SsfWrapper.isValid( "    " ) );
        
        assertTrue( SsfWrapper.isValid( "ssf=0" ) );
        assertTrue( SsfWrapper.isValid( "ssf=256" ) );
        assertTrue( SsfWrapper.isValid( "update_transport=64" ) );
        assertTrue( SsfWrapper.isValid( " SSF   =   128" ) );
        assertFalse( SsfWrapper.isValid( " ssf =  5 = 7 " ) );
        assertFalse( SsfWrapper.isValid( "ssf=-2    " ) );
    }
    
    
    @Test
    public void testCreateSsf()
    {
        SsfWrapper ssfWrapper = new SsfWrapper( "ssf", 128 );
        assertEquals( SsfEnum.SSF, ssfWrapper.getFeature() );
        assertEquals( 128, ssfWrapper.getNbBits() );

        ssfWrapper = new SsfWrapper( "ssf", -128 );
        assertEquals( SsfEnum.SSF, ssfWrapper.getFeature() );
        assertEquals( 0, ssfWrapper.getNbBits() );

        ssfWrapper = new SsfWrapper( "SSF", 128 );
        assertEquals( SsfEnum.SSF, ssfWrapper.getFeature() );
        assertEquals( 128, ssfWrapper.getNbBits() );
    }
}
