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

package org.apache.directory.studio.ldifparser.model;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.apache.directory.studio.ldifparser.LdifFormatParameters;
import org.apache.directory.studio.ldifparser.parser.LdifParser;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


/**
 * Characterization tests for formatting (spacing, line separators and folding) of LDIF files.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdifFileFormatTest
{
    private static String format( String ldif, boolean spaceAfterColon, int lineWidth, String lineSeparator )
    {
        LdifFile model = new LdifParser().parse( ldif );

        return model.toFormattedString( new LdifFormatParameters( spaceAfterColon, lineWidth, lineSeparator ) );
    }


    @Test
    public void testShortLinesAreNotFolded()
    {
        String ldif = "dn: cn=foo,dc=example,dc=com\nobjectClass: top\ncn: foo\n";

        assertEquals( ldif, format( ldif, true, 78, "\n" ) );
    }


    @Test
    public void testLineOfExactlyLineWidthIsNotFolded()
    {
        // "cn: 012345" is exactly 10 characters
        String ldif = "dn: cn=a\ncn: 012345\n";

        assertEquals( ldif, format( ldif, true, 10, "\n" ) );
    }


    @Test
    public void testFoldingAtWidth10()
    {
        String ldif = "dn: cn=foo\ndescription: 0123456789012345678901234567890123456789\n";

        String expected = ""
            + "dn: cn=foo\n"
            + "descriptio\n"
            + " n: 012345\n"
            + " 678901234\n"
            + " 567890123\n"
            + " 456789012\n"
            + " 3456789\n";

        assertEquals( expected, format( ldif, true, 10, "\n" ) );
    }


    @Test
    public void testFoldingAtWidth20()
    {
        String ldif = "dn: cn=foo\ndescription: 0123456789012345678901234567890123456789\n";

        String expected = ""
            + "dn: cn=foo\n"
            + "description: 0123456\n"
            + " 7890123456789012345\n"
            + " 67890123456789\n";

        assertEquals( expected, format( ldif, true, 20, "\n" ) );
    }


    @Test
    public void testFoldedLinesAreUnfoldedBeforeRefolding()
    {
        String ldif = "dn: cn=very long na\n me,dc=example\ndescription: abc\n def\n ghi\n";

        assertEquals( "dn: cn=very long name,dc=example\ndescription: abcdefghi\n", format( ldif, true, 78, "\n" ) );
        assertEquals( "dn: cn=very long nam\n e,dc=example\ndescription: abcdefg\n hi\n", format( ldif, true, 20, "\n" ) );
    }


    @Test
    public void testBase64ValuesAreFolded()
    {
        String ldif = "dn:: Y249ZsO2byxkYz1leGFtcGxlLGRjPWNvbQ==\ncn:: ZsO2bw==\n";

        String expected = ""
            + "dn:: Y249ZsO2byxkYz1\n"
            + " leGFtcGxlLGRjPWNvbQ\n"
            + " ==\n"
            + "cn:: ZsO2bw==\n";

        assertEquals( expected, format( ldif, true, 20, "\n" ) );
    }


    @Test
    public void testNoSpaceAfterColonAndWindowsLineSeparator()
    {
        String ldif = ""
            + "version: 1\n"
            + "dn:    cn=foo\n"
            + "cn:foo\n"
            + "jpegPhoto:: AAEC\n"
            + "labeledURI:< file:///tmp/x\n"
            + "\n";

        String expected = ""
            + "version:1\r\n"
            + "dn:cn=foo\r\n"
            + "cn:foo\r\n"
            + "jpegPhoto::AAEC\r\n"
            + "labeledURI:<file:///tmp/x\r\n"
            + "\r\n";

        assertEquals( expected, format( ldif, false, 78, "\r\n" ) );
    }


    @Test
    public void testSpaceAfterColonIsNormalized()
    {
        assertEquals( "dn: cn=foo\ncn: foo\n", format( "dn:    cn=foo\ncn:foo\n", true, 78, "\n" ) );
    }


    @Test
    public void testChangeRecordFormatting()
    {
        String ldif = ""
            + "dn: cn=foo,dc=example,dc=com\n"
            + "changetype: modify\n"
            + "replace: telephoneNumber\n"
            + "-\n"
            + "\n"
            + "dn: cn=foo,dc=example,dc=com\n"
            + "changetype: moddn\n"
            + "newrdn: cn=bar\n"
            + "deleteoldrdn: 0\n"
            + "newsuperior: ou=people,dc=example,dc=com\n";

        String expected = ""
            + "dn: cn=foo,dc=exampl\n"
            + " e,dc=com\n"
            + "changetype: modify\n"
            + "replace: telephoneNu\n"
            + " mber\n"
            + "-\n"
            + "\n"
            + "dn: cn=foo,dc=exampl\n"
            + " e,dc=com\n"
            + "changetype: moddn\n"
            + "newrdn: cn=bar\n"
            + "deleteoldrdn: 0\n"
            + "newsuperior: ou=peop\n"
            + " le,dc=example,dc=co\n"
            + " m\n";

        assertEquals( expected, format( ldif, true, 20, "\n" ) );
    }


    @Test
    public void testInvalidLinesAreKeptAsTheyAre()
    {
        String ldif = "dm: cn=foo,dc=example,dc=com\ncn: foo\n";

        assertEquals( ldif, format( ldif, false, 10, "\r\n" ) );
    }


    @Test
    @Disabled("Known bug: folding with a line width of 1 never terminates (LdifLineBase.fold sets offset to "
        + "lineWidth - 1 = 0 after the first chunk)")
    public void testFoldingWithLineWidthOneTerminates() throws Exception
    {
        // run in a daemon thread: the unfixed loop never checks for interruption, so a JUnit timeout
        // could not stop it, and a non-daemon thread would keep the test JVM alive
        Thread folding = new Thread( () -> format( "dn: cn=foo\ncn: foo\n", true, 1, "\n" ) );
        folding.setDaemon( true );
        folding.start();
        folding.join( 5000 );

        assertFalse( folding.isAlive(), "folding with line width 1 did not terminate" );
    }
}
