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

package org.apache.directory.studio.ldifparser.model.lines;


import junit.framework.TestCase;

import org.apache.directory.studio.ldifparser.LdifFormatParameters;


public class LdifAttrValLineTest extends TestCase
{

    public void testToFormattedStringSimple()
    {
        LdifAttrValLine line = LdifAttrValLine.create( "cn", "abc" );
        LdifFormatParameters formatParameters = new LdifFormatParameters( true, 78, "\n" );
        String formattedString = line.toFormattedString( formatParameters );
        assertEquals( formattedString, "cn: abc\n" );
    }


    public void testToFormattedStringLineWrap()
    {
        LdifAttrValLine line = LdifAttrValLine.create( "cn",
            "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxy" );
        LdifFormatParameters formatParameters = new LdifFormatParameters( true, 78, "\n" );
        String formattedString = line.toFormattedString( formatParameters );
        assertEquals( formattedString,
            "cn: abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvw\n xyzabcdefghijklmnopqrstuvwxy\n" );
    }


    public void testToFormattedStringNoSpaceAfterColon()
    {
        LdifAttrValLine line = LdifAttrValLine.create( "cn", "abc" );
        LdifFormatParameters formatParameters = new LdifFormatParameters( false, 78, "\n" );
        String formattedString = line.toFormattedString( formatParameters );
        assertEquals( formattedString, "cn:abc\n" );
    }


    public void testToFormattedStringBase64()
    {
        LdifAttrValLine line = LdifAttrValLine.create( "cn", "\u00e4\u00f6\u00fc" );
        LdifFormatParameters formatParameters = new LdifFormatParameters( true, 78, "\n" );
        String formattedString = line.toFormattedString( formatParameters );
        assertEquals( formattedString, "cn:: w6TDtsO8\n" );
    }


    public void testToFormattedString_DIRSERVER_285()
    {
        LdifAttrValLine line = LdifAttrValLine.create( "cn", "abc::def:<ghi" );
        LdifFormatParameters formatParameters = new LdifFormatParameters( true, 78, "\n" );
        String formattedString = line.toFormattedString( formatParameters );
        assertEquals( formattedString, "cn: abc::def:<ghi\n" );
    }

}
