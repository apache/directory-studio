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

import org.junit.Test;


public class InPlaceMsAdObjectSidValueEditorTest
{

    @Test
    public void testConvertToString1()
    {
        InPlaceMsAdObjectSidValueEditor editor = new InPlaceMsAdObjectSidValueEditor();
        byte[] bytes = new byte[]
            {
                // 01 01 00 00 00 00 00 05  04 00 00 00 
                ( byte ) 0x01, //
                ( byte ) 0x01, //
                ( byte ) 0x00,
                ( byte ) 0x00,
                ( byte ) 0x00,
                ( byte ) 0x00,
                ( byte ) 0x00,
                ( byte ) 0x05, //
                ( byte ) 0x04,
                ( byte ) 0x00,
                ( byte ) 0x00,
                ( byte ) 0x00 //
        };
        String displayValue = editor.convertToString( bytes );
        assertEquals( "S-1-5-4", displayValue ); //$NON-NLS-1$
    }


    @Test
    public void testConvertToString2()
    {
        InPlaceMsAdObjectSidValueEditor editor = new InPlaceMsAdObjectSidValueEditor();
        byte[] bytes = new byte[]
            {
                // 01 02 00 00 00 00 00 05  20 00 00 00 25 02 00 00
                ( byte ) 0x01, //
                ( byte ) 0x02, //
                ( byte ) 0x00,
                ( byte ) 0x00,
                ( byte ) 0x00,
                ( byte ) 0x00,
                ( byte ) 0x00,
                ( byte ) 0x05, //
                ( byte ) 0x20,
                ( byte ) 0x00,
                ( byte ) 0x00,
                ( byte ) 0x00, //
                ( byte ) 0x25,
                ( byte ) 0x02,
                ( byte ) 0x00,
                ( byte ) 0x00 //
        };
        String displayValue = editor.convertToString( bytes );
        assertEquals( "S-1-5-32-549", displayValue ); //$NON-NLS-1$
    }


    @Test
    public void testConvertToString3()
    {
        InPlaceMsAdObjectSidValueEditor editor = new InPlaceMsAdObjectSidValueEditor();
        byte[] bytes = new byte[]
            {
                // 01 05 00 00 00 00 00 05  15 00 00 00 af 6e b6 27
                // 0c f5 77 a0 a7 10 df 6e  f4 01 00 00
                ( byte ) 0x01, //
                ( byte ) 0x05, //
                ( byte ) 0x00,
                ( byte ) 0x00,
                ( byte ) 0x00,
                ( byte ) 0x00,
                ( byte ) 0x00,
                ( byte ) 0x05, //
                ( byte ) 0x15,
                ( byte ) 0x00,
                ( byte ) 0x00,
                ( byte ) 0x00, //
                ( byte ) 0xaf,
                ( byte ) 0x6e,
                ( byte ) 0xb6,
                ( byte ) 0x27, //
                ( byte ) 0x0c,
                ( byte ) 0xf5,
                ( byte ) 0x77,
                ( byte ) 0xa0, //
                ( byte ) 0xa7,
                ( byte ) 0x10,
                ( byte ) 0xdf,
                ( byte ) 0x6e, //
                ( byte ) 0xf4,
                ( byte ) 0x01,
                ( byte ) 0x00,
                ( byte ) 0x00 //
        };
        String displayValue = editor.convertToString( bytes );
        assertEquals( "S-1-5-21-666267311-2692216076-1860112551-500", displayValue ); //$NON-NLS-1$
    }


    @Test
    public void testConvertToStringInvalid()
    {
        InPlaceMsAdObjectSidValueEditor editor = new InPlaceMsAdObjectSidValueEditor();

        // test too short
        byte[] bytes = new byte[]
            { ( byte ) 0x00 };
        String displayValue = editor.convertToString( bytes );
        assertEquals( "Invalid SID", displayValue );

        // test missing sub aurhority byte
        byte[] bytes2 = new byte[]
            {
                // 01 05 00 00 00 00 00 05  15 00 00 00 af 6e b6 27
                // 0c f5 77 a0 a7 10 df 6e  f4 01 00 00
                ( byte ) 0x01, //
                ( byte ) 0x05, //
                ( byte ) 0x00,
                ( byte ) 0x00,
                ( byte ) 0x00,
                ( byte ) 0x00,
                ( byte ) 0x00,
                ( byte ) 0x05, //
                ( byte ) 0x15,
                ( byte ) 0x00,
                ( byte ) 0x00,
                ( byte ) 0x00, //
                ( byte ) 0xaf,
                ( byte ) 0x6e,
                ( byte ) 0xb6,
                ( byte ) 0x27, //
                ( byte ) 0x0c,
                ( byte ) 0xf5,
                ( byte ) 0x77,
                ( byte ) 0xa0, //
                ( byte ) 0xa7,
                ( byte ) 0x10,
                ( byte ) 0xdf,
                ( byte ) 0x6e, //
                ( byte ) 0xf4,
                ( byte ) 0x01,
                ( byte ) 0x00, /*( byte ) 0x00*///
            };
        String displayValue2 = editor.convertToString( bytes2 );
        assertEquals( "Invalid SID", displayValue2 );

        // test additional sub authority byte
        byte[] bytes3 = new byte[]
            {
                // 01 05 00 00 00 00 00 05  15 00 00 00 af 6e b6 27
                // 0c f5 77 a0 a7 10 df 6e  f4 01 00 00
                ( byte ) 0x01, //
                ( byte ) 0x05, //
                ( byte ) 0x00,
                ( byte ) 0x00,
                ( byte ) 0x00,
                ( byte ) 0x00,
                ( byte ) 0x00,
                ( byte ) 0x05, //
                ( byte ) 0x15,
                ( byte ) 0x00,
                ( byte ) 0x00,
                ( byte ) 0x00, //
                ( byte ) 0xaf,
                ( byte ) 0x6e,
                ( byte ) 0xb6,
                ( byte ) 0x27, //
                ( byte ) 0x0c,
                ( byte ) 0xf5,
                ( byte ) 0x77,
                ( byte ) 0xa0, //
                ( byte ) 0xa7,
                ( byte ) 0x10,
                ( byte ) 0xdf,
                ( byte ) 0x6e, //
                ( byte ) 0xf4,
                ( byte ) 0x01,
                ( byte ) 0x00,
                ( byte ) 0x00,
                ( byte ) 0x00 //
        };
        String displayValue3 = editor.convertToString( bytes3 );
        assertEquals( "Invalid SID", displayValue3 );
    }


    @Test
    public void testConvertToStringNull()
    {
        InPlaceMsAdObjectSidValueEditor editor = new InPlaceMsAdObjectSidValueEditor();
        byte[] bytes = null;
        String displayValue = editor.convertToString( bytes );
        assertEquals( "Invalid SID", displayValue );
    }
}
