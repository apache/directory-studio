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

package org.apache.directory.studio.ldifparser;


import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;


/**
 * Characterization tests for {@link LdifUtils}.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdifUtilsTest
{
    @Test
    public void testMustEncodeSafeValues()
    {
        assertFalse( LdifUtils.mustEncode( null ) );
        assertFalse( LdifUtils.mustEncode( "" ) );
        assertFalse( LdifUtils.mustEncode( "abc" ) );
        assertFalse( LdifUtils.mustEncode( "a b" ) );
        assertFalse( LdifUtils.mustEncode( "a:b<c" ) );
        assertFalse( LdifUtils.mustEncode( "~!@#$%^&*()" ) );
    }


    @Test
    public void testMustEncodeUnsafeStart()
    {
        assertTrue( LdifUtils.mustEncode( " abc" ) );
        assertTrue( LdifUtils.mustEncode( ":abc" ) );
        assertTrue( LdifUtils.mustEncode( "<abc" ) );
    }


    @Test
    public void testMustEncodeTrailingSpace()
    {
        assertTrue( LdifUtils.mustEncode( "abc " ) );
    }


    @Test
    public void testMustEncodeUnsafeCharacters()
    {
        assertTrue( LdifUtils.mustEncode( "a\nb" ) );
        assertTrue( LdifUtils.mustEncode( "a\rb" ) );
        assertTrue( LdifUtils.mustEncode( "a\u0000b" ) );
        assertTrue( LdifUtils.mustEncode( "föo" ) );
        assertTrue( LdifUtils.mustEncode( "€" ) );
        assertFalse( LdifUtils.mustEncode( "\u007f" ) );
    }


    @Test
    public void testMustEncodeDnFollowsValueRules()
    {
        assertFalse( LdifUtils.mustEncodeDN( "cn=foo,dc=example,dc=com" ) );
        assertTrue( LdifUtils.mustEncodeDN( " cn=foo" ) );
        assertTrue( LdifUtils.mustEncodeDN( "cn=foo " ) );
        assertTrue( LdifUtils.mustEncodeDN( "cn=föo" ) );
        assertFalse( LdifUtils.mustEncodeDN( null ) );
    }


    @Test
    public void testBase64RoundTrip()
    {
        byte[] data = new byte[]
            { 0, 1, 2, ( byte ) 0xff };

        assertEquals( "AAEC/w==", LdifUtils.base64encode( data ) );
        assertArrayEquals( data, LdifUtils.base64decodeToByteArray( "AAEC/w==" ) );
        assertEquals( "", LdifUtils.base64encode( new byte[0] ) );
    }


    @Test
    public void testUtf8RoundTrip()
    {
        byte[] bytes = new byte[]
            { 'f', ( byte ) 0xc3, ( byte ) 0xb6, 'o' };

        assertArrayEquals( bytes, LdifUtils.utf8encode( "föo" ) );
        assertEquals( "föo", LdifUtils.utf8decode( bytes ) );
    }


    @Test
    public void testHexEncode()
    {
        assertEquals( "000fff", LdifUtils.hexEncode( new byte[]
            { 0, 15, ( byte ) 0xff } ) );
        assertEquals( "", LdifUtils.hexEncode( new byte[0] ) );
        assertNull( LdifUtils.hexEncode( null ) );
    }


    @Test
    public void testUrlEncode()
    {
        assertEquals( "a+b%2F%C3%B6", LdifUtils.urlEncode( "a b/ö" ) );
        assertEquals( "abc", LdifUtils.urlEncode( "abc" ) );
    }


    @Test
    public void testConvertNlRcToString()
    {
        assertEquals( "a\\nb\\rc", LdifUtils.convertNlRcToString( "a\nb\rc" ) );
        assertEquals( "\\r\\n", LdifUtils.convertNlRcToString( "\r\n" ) );
        assertEquals( "abc", LdifUtils.convertNlRcToString( "abc" ) );
        assertEquals( "", LdifUtils.convertNlRcToString( null ) );
    }
}
