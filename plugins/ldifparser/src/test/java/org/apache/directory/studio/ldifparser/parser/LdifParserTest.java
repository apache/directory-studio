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

package org.apache.directory.studio.ldifparser.parser;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.directory.studio.ldifparser.LdifFormatParameters;
import org.apache.directory.studio.ldifparser.model.LdifFile;

import org.junit.jupiter.api.Test;


public class LdifParserTest
{

    @Test
    public void testLdifNull()
    {
        String ldif = null;

        LdifParser parser = new LdifParser();
        LdifFile model = parser.parse( ldif );

        assertEquals( 0, model.getRecords().length );
    }


    @Test
    public void testParseAndFormatWithLdifWindowsLineBreak()
    {
        String ldif = ""
            + "dn: cn=foo,ou=users,ou=system\r\n"
            + "cn: foo\r\n"
            + "description: 12345678901234567890123456789012345678901234567890123456789012345\r\n"
            + " 678901234567890\r\n"
            + "description:: MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDEyMzQ1Njc4\r\n"
            + " OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDEyMzQ1Njc4OTAK\r\n";

        LdifParser parser = new LdifParser();
        LdifFile model = parser.parse( ldif );

        assertEquals( 1, model.getRecords().length );

        LdifFormatParameters formatParameters = new LdifFormatParameters( true, 78, "\r\n" );
        String formatted = model.toFormattedString( formatParameters );
        assertEquals( ldif, formatted );
    }


    @Test
    public void testParseAndFormatWithLdifUnixLineBreak()
    {
        String ldif = ""
            + "dn: cn=foo,ou=users,ou=system\n"
            + "cn: foo\n"
            + "description: 12345678901234567890123456789012345678901234567890123456789012345\n"
            + " 678901234567890\n"
            + "description:: MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDEyMzQ1Njc4\n"
            + " OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDEyMzQ1Njc4OTAK\n";

        LdifParser parser = new LdifParser();
        LdifFile model = parser.parse( ldif );

        assertEquals( 1, model.getRecords().length );

        LdifFormatParameters formatParameters = new LdifFormatParameters( true, 78, "\n" );
        String formatted = model.toFormattedString( formatParameters );
        assertEquals( ldif, formatted );
    }

}
