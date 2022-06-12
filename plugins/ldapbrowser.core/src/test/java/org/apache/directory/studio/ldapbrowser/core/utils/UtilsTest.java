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
package org.apache.directory.studio.ldapbrowser.core.utils;


import static org.junit.jupiter.api.Assertions.assertEquals;
import org.apache.commons.text.translate.CharSequenceTranslator;
import org.junit.jupiter.api.Test;


public class UtilsTest
{
    @Test
    public void testPostalAddressTrivial()
    {
        assertEquals( "abc", Utils.createPostalAddressDecoder( "!" ).translate( "abc" ) );
        assertEquals( "abc", Utils.createPostalAddressEncoder( "!" ).translate( "abc" ) );
    }


    @Test
    public void testPostalAddressEscaped()
    {
        CharSequenceTranslator decoder = Utils.createPostalAddressDecoder( "!" );
        assertEquals( "!", decoder.translate( "$" ) );
        assertEquals( "$", decoder.translate( "\\24" ) );
        assertEquals( "\\", decoder.translate( "\\5C" ) );
        assertEquals( "\\", decoder.translate( "\\5c" ) );
        assertEquals( "\\5C", decoder.translate( "\\5c5C" ) );
        assertEquals( "\\5c", decoder.translate( "\\5C5c" ) );

        CharSequenceTranslator encoder = Utils.createPostalAddressEncoder( "!" );
        assertEquals( "$", encoder.translate( "!" ) );
        assertEquals( "\\24", encoder.translate( "$" ) );
        assertEquals( "\\5C", encoder.translate( "\\" ) );
    }


    @Test
    public void testPostalAddressRfcExamples()
    {
        CharSequenceTranslator decoder = Utils.createPostalAddressDecoder( "\n" );
        assertEquals( "1234 Main St.\nAnytown, CA 12345\nUSA",
            decoder.translate( "1234 Main St.$Anytown, CA 12345$USA" ) );
        assertEquals( "$1,000,000 Sweepstakes\nPO Box 1000000\nAnytown, CA 12345\nUSA",
            decoder.translate( "\\241,000,000 Sweepstakes$PO Box 1000000$Anytown, CA 12345$USA" ) );

        CharSequenceTranslator encoder = Utils.createPostalAddressEncoder( "\n" );
        assertEquals( "1234 Main St.$Anytown, CA 12345$USA",
            encoder.translate( "1234 Main St.\nAnytown, CA 12345\nUSA" ) );
        assertEquals( "\\241,000,000 Sweepstakes$PO Box 1000000$Anytown, CA 12345$USA",
            encoder.translate( "$1,000,000 Sweepstakes\nPO Box 1000000\nAnytown, CA 12345\nUSA" ) );
    }
}
