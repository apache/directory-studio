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

package org.apache.directory.studio.ldapbrowser.common.widgets.search;


import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ReturningAttributesWidgetTest
{

    @Test
    public void testStringToArrayNull()
    {
        String[] attributes = ReturningAttributesWidget.stringToArray( null );
        assertNull( attributes );
    }


    @Test
    public void testStringToArrayEmpty()
    {
        String[] attributes = ReturningAttributesWidget.stringToArray( "" );
        assertNotNull( attributes );
        assertArrayEquals( new String[0], attributes );
    }


    @Test
    public void testStringToArrayNoAttrs()
    {
        String[] attributes = ReturningAttributesWidget.stringToArray( "1.1" );
        assertNotNull( attributes );
        assertArrayEquals( new String[]
            { "1.1" }, attributes );
    }


    @Test
    public void testStringToArraySingleAttribute()
    {
        String[] attributes = ReturningAttributesWidget.stringToArray( "cn" );
        assertNotNull( attributes );
        assertArrayEquals( new String[]
            { "cn" }, attributes );
    }


    @Test
    public void testStringToArrayStingleAttributeWithTrailingWhitespace()
    {
        String[] attributes = ReturningAttributesWidget.stringToArray( " cn\t " );
        assertNotNull( attributes );
        assertArrayEquals( new String[]
            { "cn" }, attributes );
    }


    @Test
    public void testStringToArrayStingleAttributeWithTrailingCommas()
    {
        String[] attributes = ReturningAttributesWidget.stringToArray( " , ,cn,," );
        assertNotNull( attributes );
        assertArrayEquals( new String[]
            { "cn" }, attributes );
    }


    @Test
    public void testStringToArrayMultipleAttributes()
    {
        String[] attributes = ReturningAttributesWidget.stringToArray( "cn, sn uid" );
        assertNotNull( attributes );
        assertArrayEquals( new String[]
            { "cn", "sn", "uid" }, attributes );
    }


    @Test
    public void testStringToArrayMultiplwWithAllUserAndOperationalAttributes()
    {
        String[] attributes = ReturningAttributesWidget.stringToArray( "cn, sn uid, * +" );
        assertNotNull( attributes );
        assertArrayEquals( new String[]
            { "cn", "sn", "uid", "*", "+" }, attributes );
    }


    @Test
    public void testStringToArrayMultipleAttributesWithOptions()
    {
        String[] attributes = ReturningAttributesWidget.stringToArray( "cn, sn;lang-de;lang-en uid" );
        assertNotNull( attributes );
        assertArrayEquals( new String[]
            { "cn", "sn;lang-de;lang-en", "uid" }, attributes );
    }


    @Test
    public void testStringToArrayMultipleAttributesAsOid()
    {
        String[] attributes = ReturningAttributesWidget.stringToArray( "2.5.4.3, 2.5.4.4 0.9.2342.19200300.100.1.1" );
        assertNotNull( attributes );
        assertArrayEquals( new String[]
            { "2.5.4.3", "2.5.4.4", "0.9.2342.19200300.100.1.1" }, attributes );
    }


    @Test
    public void testStringToArrayMultipleAttributesWithUnderscore()
    {
        String[] attributes = ReturningAttributesWidget.stringToArray( "cn, s_n u_i_d" );
        assertNotNull( attributes );
        assertArrayEquals( new String[]
            { "cn", "s_n", "u_i_d" }, attributes );
    }


    @Test
    public void testStringToArrayMultipleAttributesWithRangeOption()
    {
        String[] attributes = ReturningAttributesWidget.stringToArray( "cn, member;Range=0-* objectClass" );
        assertNotNull( attributes );
        assertArrayEquals( new String[]
            { "cn", "member;Range=0-*", "objectClass" }, attributes );
    }

}
