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
import org.junit.jupiter.api.Test;


public class UtilsTest
{
    @Test
    public void testPostalAddressTrivial()
    {
        assertEquals( "abc", Utils.decodePostalAddress( "abc", "!" ) );
        assertEquals( "abc", Utils.encodePostalAddress( "abc", "!" ) );
    }


    @Test
    public void testPostalAddressEscaped()
    {
        assertEquals( "!", Utils.decodePostalAddress( "$", "!" ) );
        assertEquals( "$", Utils.decodePostalAddress( "\\24", "!" ) );
        assertEquals( "\\", Utils.decodePostalAddress( "\\5C", "!" ) );
        assertEquals( "\\", Utils.decodePostalAddress( "\\5c", "!" ) );

        assertEquals( "$", Utils.encodePostalAddress( "!", "!" ) );
        assertEquals( "\\24", Utils.encodePostalAddress( "$", "!" ) );
        assertEquals( "\\5C", Utils.encodePostalAddress( "\\", "!" ) );
    }


    @Test
    public void testPostalAddressRfcExamples()
    {
        assertEquals( "1234 Main St.\nAnytown, CA 12345\nUSA",
            Utils.decodePostalAddress( "1234 Main St.$Anytown, CA 12345$USA", "\n" ) );
        assertEquals( "$1,000,000 Sweepstakes\nPO Box 1000000\nAnytown, CA 12345\nUSA",
            Utils.decodePostalAddress( "\\241,000,000 Sweepstakes$PO Box 1000000$Anytown, CA 12345$USA", "\n" ) );

        assertEquals( "1234 Main St.$Anytown, CA 12345$USA",
            Utils.encodePostalAddress( "1234 Main St.\nAnytown, CA 12345\nUSA", "\n" ) );
        assertEquals( "\\241,000,000 Sweepstakes$PO Box 1000000$Anytown, CA 12345$USA",
            Utils.encodePostalAddress( "$1,000,000 Sweepstakes\nPO Box 1000000\nAnytown, CA 12345\nUSA", "\n" ) );
    }
}
