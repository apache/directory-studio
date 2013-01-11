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

package org.apache.directory.studio.valueeditors.msad;


import static junit.framework.Assert.assertEquals;

import org.apache.directory.api.util.Strings;
import org.junit.Test;


public class InPlaceMsAdObjectGuidValueEditorTest
{

    @Test
    public void testConvertToString1()
    {
        InPlaceMsAdObjectGuidValueEditor editor = new InPlaceMsAdObjectGuidValueEditor();
        byte[] bytes = new byte[]
            {
                //
                ( byte ) 0x89,
                ( byte ) 0xBA,
                ( byte ) 0x78,
                ( byte ) 0xDB, //
                ( byte ) 0x5F,
                ( byte ) 0xB8, //
                ( byte ) 0x7F,
                ( byte ) 0x44, //
                ( byte ) 0xBD,
                ( byte ) 0x06, //
                ( byte ) 0xE3,
                ( byte ) 0xA4,
                ( byte ) 0x09,
                ( byte ) 0x96,
                ( byte ) 0xA9,
                ( byte ) 0xA8 };
        String displayValue = editor.convertToString( bytes );
        assertEquals( Strings.toLowerCase( "{db78ba89-b85f-447f-bd06-e3a40996a9a8}" ), displayValue ); //$NON-NLS-1$
    }


    @Test
    public void testConvertToString2()
    {
        InPlaceMsAdObjectGuidValueEditor editor = new InPlaceMsAdObjectGuidValueEditor();
        byte[] bytes = new byte[]
            {
                //
                ( byte ) 0x00,
                ( byte ) 0x11,
                ( byte ) 0x22,
                ( byte ) 0x33, //
                ( byte ) 0x44,
                ( byte ) 0x55, //
                ( byte ) 0x66,
                ( byte ) 0x77, //
                ( byte ) 0x88,
                ( byte ) 0x99, //
                ( byte ) 0xAA,
                ( byte ) 0xBB,
                ( byte ) 0xCC,
                ( byte ) 0xDD,
                ( byte ) 0xEE,
                ( byte ) 0xFF };
        String displayValue = editor.convertToString( bytes );
        assertEquals( Strings.toLowerCase( "{33221100-5544-7766-8899-AABBCCDDEEFF}" ), displayValue ); //$NON-NLS-1$
    }


    @Test
    public void testConvertToStringInvalid()
    {
        InPlaceMsAdObjectGuidValueEditor editor = new InPlaceMsAdObjectGuidValueEditor();

        // test too short
        byte[] bytes = new byte[]
            { ( byte ) 0x00, ( byte ) 0x11, ( byte ) 0x22, ( byte ) 0x33 };
        String displayValue = editor.convertToString( bytes );
        assertEquals(  Messages.getString( "InPlaceMsAdObjectGuidValueEditor.InvalidGuid" ), displayValue ); //$NON-NLS-1$

        // test too long
        byte[] bytes2 = new byte[]
            { ( byte ) 0x00, ( byte ) 0x00, ( byte ) 0x00, ( byte ) 0x00, ( byte ) 0x00, ( byte ) 0x00, ( byte ) 0x00,
                ( byte ) 0x00, ( byte ) 0x00, ( byte ) 0x00, ( byte ) 0x00, ( byte ) 0x00, ( byte ) 0x00,
                ( byte ) 0x00, ( byte ) 0x00, ( byte ) 0x00, ( byte ) 0x00, ( byte ) 0x00, ( byte ) 0x00,
                ( byte ) 0x00, };
        String displayValue2 = editor.convertToString( bytes2 );
        assertEquals(  Messages.getString( "InPlaceMsAdObjectGuidValueEditor.InvalidGuid" ), displayValue2 ); //$NON-NLS-1$
    }


    @Test
    public void testConvertToStringNull()
    {
        InPlaceMsAdObjectGuidValueEditor editor = new InPlaceMsAdObjectGuidValueEditor();
        byte[] bytes = null;
        String displayValue = editor.convertToString( bytes );
        assertEquals(  Messages.getString( "InPlaceMsAdObjectGuidValueEditor.InvalidGuid" ), displayValue ); //$NON-NLS-1$
    }
}
