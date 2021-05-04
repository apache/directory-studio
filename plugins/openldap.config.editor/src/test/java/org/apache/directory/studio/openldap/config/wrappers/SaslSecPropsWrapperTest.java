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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.directory.studio.openldap.common.ui.model.SaslSecPropEnum;
import org.apache.directory.studio.openldap.config.editor.wrappers.SaslSecPropsWrapper;
import org.junit.jupiter.api.Test;

/**
 * A test for the SaslSecPropsWrapper class
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SaslSecPropsWrapperTest
{
    @Test
    public void testToString()
    {
        SaslSecPropsWrapper ssfWrapper = new SaslSecPropsWrapper();
        
        assertEquals( "", ssfWrapper.toString() );

        ssfWrapper = new SaslSecPropsWrapper( "none" );
        
        assertEquals( "none", ssfWrapper.toString() );

        ssfWrapper = new SaslSecPropsWrapper( "minssf = 100");
        
        assertEquals( "minssf=100", ssfWrapper.toString() );
    }
    
    
    @Test
    public void testIsValid()
    {
        assertTrue( SaslSecPropsWrapper.isValid( null ) );
        assertTrue( SaslSecPropsWrapper.isValid( "" ) );
        assertTrue( SaslSecPropsWrapper.isValid( "    " ) );
        
        // Standard flags
        assertTrue( SaslSecPropsWrapper.isValid( "none" ) );
        assertTrue( SaslSecPropsWrapper.isValid( "NoPlain" ) );
        assertTrue( SaslSecPropsWrapper.isValid( "noActive" ) );
        assertTrue( SaslSecPropsWrapper.isValid( "    noDict    " ) );
        assertTrue( SaslSecPropsWrapper.isValid( "noanonymous" ) );
        assertTrue( SaslSecPropsWrapper.isValid( "forwardSec" ) );
        assertTrue( SaslSecPropsWrapper.isValid( "passcred" ) );
        
        // properties with value
        assertTrue( SaslSecPropsWrapper.isValid( "minSSF=100" ) );
        assertTrue( SaslSecPropsWrapper.isValid( " minSsf =    1000  " ) );
        assertTrue( SaslSecPropsWrapper.isValid( " maxSsf =    1000  " ) );
        assertTrue( SaslSecPropsWrapper.isValid( " maxBufSize =    1000  " ) );
        
        // Multiple properties
        assertTrue( SaslSecPropsWrapper.isValid( " maxBufSize =    1000,none,  nodict, maxssF = 100  " ) );
        
        // Wrong properties
        assertFalse( SaslSecPropsWrapper.isValid( " abc  " ) );
        assertFalse( SaslSecPropsWrapper.isValid( " maxBufSize =    -1000" ) );
        assertFalse( SaslSecPropsWrapper.isValid( " maxBufSize =    ,none" ) );

        // Corner cases
        assertTrue( SaslSecPropsWrapper.isValid( " ,none" ) );
        assertTrue( SaslSecPropsWrapper.isValid( "none," ) );
        assertTrue( SaslSecPropsWrapper.isValid( " ,none," ) );
        assertTrue( SaslSecPropsWrapper.isValid( " maxBufSize =    1000,,none,  nodict, maxssF = 100  " ) );
    }
    
    
    @Test
    public void testCreateSspw()
    {
        SaslSecPropsWrapper sspw = new SaslSecPropsWrapper( null );
        assertEquals( 0, sspw.getFlags().size() );

        sspw = new SaslSecPropsWrapper( "" );
        assertEquals( 0, sspw.getFlags().size() );

        sspw = new SaslSecPropsWrapper( "none" );
        assertEquals( 1, sspw.getFlags().size() );
        assertTrue( sspw.getFlags().contains( SaslSecPropEnum.NONE ) );

        sspw = new SaslSecPropsWrapper( "noplain" );
        assertEquals( 1, sspw.getFlags().size() );
        assertTrue( sspw.getFlags().contains( SaslSecPropEnum.NO_PLAIN ) );

        sspw = new SaslSecPropsWrapper( "noactive" );
        assertEquals( 1, sspw.getFlags().size() );
        assertTrue( sspw.getFlags().contains( SaslSecPropEnum.NO_ACTIVE ) );

        sspw = new SaslSecPropsWrapper( "nodict" );
        assertEquals( 1, sspw.getFlags().size() );
        assertTrue( sspw.getFlags().contains( SaslSecPropEnum.NO_DICT ) );

        sspw = new SaslSecPropsWrapper( "noanonymous" );
        assertEquals( 1, sspw.getFlags().size() );
        assertTrue( sspw.getFlags().contains( SaslSecPropEnum.NO_ANONYMOUS ) );

        sspw = new SaslSecPropsWrapper( "forwardsec" );
        assertEquals( 1, sspw.getFlags().size() );
        assertTrue( sspw.getFlags().contains( SaslSecPropEnum.FORWARD_SEC ) );

        sspw = new SaslSecPropsWrapper( "passcred" );
        assertEquals( 1, sspw.getFlags().size() );
        assertTrue( sspw.getFlags().contains( SaslSecPropEnum.PASS_CRED ) );

        sspw = new SaslSecPropsWrapper( "minssf = 100" );
        assertEquals( 0, sspw.getFlags().size() );
        assertNotNull( sspw.getMinSsf() );
        assertEquals( 100, sspw.getMinSsf().intValue() );

        sspw = new SaslSecPropsWrapper( "minssf = 100, maxssf=200, maxBufSize= 2000, " );
        assertEquals( 0, sspw.getFlags().size() );
        assertNotNull( sspw.getMinSsf() );
        assertEquals( 100, sspw.getMinSsf().intValue() );
    }
}
