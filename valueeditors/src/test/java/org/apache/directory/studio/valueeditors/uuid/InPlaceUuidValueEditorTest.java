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

package org.apache.directory.studio.valueeditors.uuid;


import static junit.framework.Assert.assertEquals;

import org.junit.Test;


public class InPlaceUuidValueEditorTest
{

    @Test
    public void testConvertToString1()
    {
        InPlaceUuidValueEditor editor = new InPlaceUuidValueEditor();
        byte[] bytes = new byte[]
            {
            //
                ( byte ) 0x6b, ( byte ) 0xa7, ( byte ) 0xb8, ( byte ) 0x10, //
                ( byte ) 0x9d, ( byte ) 0xad, //
                ( byte ) 0x11, ( byte ) 0xd1, //
                ( byte ) 0x80, ( byte ) 0xb4, //
                ( byte ) 0x00, ( byte ) 0xc0, ( byte ) 0x4f, ( byte ) 0xd4, ( byte ) 0x30, ( byte ) 0xc8 };
        String displayValue = editor.convertToString( bytes );
        assertEquals( "6ba7b810-9dad-11d1-80b4-00c04fd430c8".toLowerCase(), displayValue );
    }


    @Test
    public void testConvertToString2()
    {
        InPlaceUuidValueEditor editor = new InPlaceUuidValueEditor();
        byte[] bytes = new byte[]
            {
            //
                ( byte ) 0x00, ( byte ) 0x11, ( byte ) 0x22, ( byte ) 0x33, //
                ( byte ) 0x44, ( byte ) 0x55, //
                ( byte ) 0x66, ( byte ) 0x77, //
                ( byte ) 0x88, ( byte ) 0x99, //
                ( byte ) 0xAA, ( byte ) 0xBB, ( byte ) 0xCC, ( byte ) 0xDD, ( byte ) 0xEE, ( byte ) 0xFF };
        String displayValue = editor.convertToString( bytes );
        assertEquals( "00112233-4455-6677-8899-AABBCCDDEEFF".toLowerCase(), displayValue );
    }


    @Test
    public void testConvertToStringInvalid()
    {
        InPlaceUuidValueEditor editor = new InPlaceUuidValueEditor();

        // test too short
        byte[] bytes = new byte[]
            { ( byte ) 0x00, ( byte ) 0x11, ( byte ) 0x22, ( byte ) 0x33 };
        String displayValue = editor.convertToString( bytes );
        assertEquals( "Invalid UUID", displayValue );

        // test too long
        byte[] bytes2 = new byte[]
            { ( byte ) 0x00, ( byte ) 0x00, ( byte ) 0x00, ( byte ) 0x00, ( byte ) 0x00, ( byte ) 0x00, ( byte ) 0x00,
                ( byte ) 0x00, ( byte ) 0x00, ( byte ) 0x00, ( byte ) 0x00, ( byte ) 0x00, ( byte ) 0x00,
                ( byte ) 0x00, ( byte ) 0x00, ( byte ) 0x00, ( byte ) 0x00, ( byte ) 0x00, ( byte ) 0x00,
                ( byte ) 0x00, };
        String displayValue2 = editor.convertToString( bytes2 );
        assertEquals( "Invalid UUID", displayValue2 );
    }


    @Test
    public void testConvertToStringNull()
    {
        InPlaceUuidValueEditor editor = new InPlaceUuidValueEditor();
        byte[] bytes = null;
        String displayValue = editor.convertToString( bytes );
        assertEquals( "Invalid UUID", displayValue );
    }

}
