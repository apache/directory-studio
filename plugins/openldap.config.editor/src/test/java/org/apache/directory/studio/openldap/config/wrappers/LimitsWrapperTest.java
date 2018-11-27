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
import static org.junit.Assert.assertNull;

import org.apache.directory.studio.openldap.common.ui.model.DnSpecStyleEnum;
import org.apache.directory.studio.openldap.common.ui.model.DnSpecTypeEnum;
import org.apache.directory.studio.openldap.common.ui.model.LimitSelectorEnum;
import org.apache.directory.studio.openldap.config.editor.wrappers.LimitWrapper;
import org.apache.directory.studio.openldap.config.editor.wrappers.LimitsWrapper;
import org.apache.directory.studio.openldap.config.editor.wrappers.TimeLimitWrapper;
import org.apache.directory.studio.openldap.config.editor.wrappers.SizeLimitWrapper;
import org.junit.Test;
import java.util.List;

/**
 * A test for the LimitsWrapper class
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LimitsWrapperTest
{
    @Test
    public void testLimitsWrapper()
    {
        LimitsWrapper lw = new LimitsWrapper( "dn.this.exact=\".*,dc=example,dc=com\"" );
        
        assertEquals( LimitSelectorEnum.DNSPEC, lw.getSelector() );
        assertEquals( DnSpecTypeEnum.THIS, lw.getDnSpecType() );
        assertEquals( DnSpecStyleEnum.EXACT, lw.getDnSpecStyle() );
        assertEquals( ".*,dc=example,dc=com", lw.getSelectorPattern() );
        assertFalse( lw.isValid() );

        assertEquals( "{0}dn.this.exact=\".*,dc=example,dc=com\"", lw.toString() );

        lw = new LimitsWrapper( "dn=\"*\"" );
        
        assertEquals( LimitSelectorEnum.DNSPEC, lw.getSelector() );
        assertNull( lw.getDnSpecType() );
        assertNull( lw.getDnSpecStyle() );
        assertEquals( "*", lw.getSelectorPattern() );
        assertFalse( lw.isValid() );

        assertEquals( "{0}dn=\"*\"", lw.toString() );
        
        lw = new LimitsWrapper( "dn.self.regex=\".*,dc=example,dc=com\" time.soft=100 time.hard=1000 size.soft=100 size.hard=soft" );
        assertEquals( LimitSelectorEnum.DNSPEC, lw.getSelector() );
        assertEquals( DnSpecTypeEnum.SELF, lw.getDnSpecType() );
        assertEquals( DnSpecStyleEnum.REGEXP, lw.getDnSpecStyle() );
        assertEquals( ".*,dc=example,dc=com", lw.getSelectorPattern() );
        assertTrue( lw.isValid() );
        List<LimitWrapper> limits = lw.getLimits();
        
        assertEquals( 4, limits.size() );
        assertTrue( limits.get( 0 ) instanceof TimeLimitWrapper );
        
        TimeLimitWrapper tlw1 = (TimeLimitWrapper)limits.get( 0 );
        assertTrue( tlw1.isValid() );
        assertEquals( 100, tlw1.getSoftLimit().intValue() );

        assertTrue( limits.get( 1 ) instanceof TimeLimitWrapper );
        TimeLimitWrapper tlw2 = (TimeLimitWrapper)limits.get( 1 );
        assertTrue( tlw2.isValid() );
        assertEquals( 1000, tlw2.getHardLimit().intValue() );

        assertTrue( limits.get( 2 ) instanceof SizeLimitWrapper );
        SizeLimitWrapper slw1 = (SizeLimitWrapper)limits.get( 2 );
        assertTrue( slw1.isValid() );
        assertEquals( 100, slw1.getSoftLimit().intValue() );

        assertTrue( limits.get( 3 ) instanceof SizeLimitWrapper );
        SizeLimitWrapper slw2 = (SizeLimitWrapper)limits.get( 3 );
        assertTrue( slw2.isValid() );
        assertEquals( SizeLimitWrapper.HARD_SOFT.intValue(), slw2.getGlobalLimit().intValue() );
    }
}
