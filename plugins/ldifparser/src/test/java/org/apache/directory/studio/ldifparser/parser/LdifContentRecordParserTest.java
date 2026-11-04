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


import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.apache.directory.studio.ldifparser.model.LdifFile;
import org.apache.directory.studio.ldifparser.model.container.LdifCommentContainer;
import org.apache.directory.studio.ldifparser.model.container.LdifContainer;
import org.apache.directory.studio.ldifparser.model.container.LdifContentRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifVersionContainer;
import org.apache.directory.studio.ldifparser.model.lines.LdifAttrValLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifDnLine;
import org.junit.jupiter.api.Test;


/**
 * Characterization tests for parsing LDIF content records.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdifContentRecordParserTest
{
    private static LdifFile parse( String ldif )
    {
        return new LdifParser().parse( ldif );
    }


    @Test
    public void testVersionCommentAndTwoRecords()
    {
        String ldif = ""
            + "version: 1\n"
            + "# a comment\n"
            + "dn: cn=foo,dc=example,dc=com\n"
            + "objectClass: top\n"
            + "objectClass: person\n"
            + "cn: foo\n"
            + "sn: bar\n"
            + "\n"
            + "dn: cn=bar,dc=example,dc=com\n"
            + "cn: bar\n";

        LdifFile model = parse( ldif );

        assertTrue( model.isContentType() );
        assertFalse( model.isChangeType() );

        List<LdifContainer> containers = model.getContainers();
        assertEquals( 4, containers.size() );
        assertTrue( containers.get( 0 ) instanceof LdifVersionContainer );
        assertTrue( containers.get( 1 ) instanceof LdifCommentContainer );
        assertTrue( containers.get( 2 ) instanceof LdifContentRecord );
        assertTrue( containers.get( 3 ) instanceof LdifContentRecord );

        assertEquals( 2, model.getRecords().length );

        LdifContentRecord first = ( LdifContentRecord ) model.getRecords()[0];
        assertEquals( "cn=foo,dc=example,dc=com", first.getDnLine().getValueAsString() );
        assertTrue( first.isValid() );
        assertTrue( first.getSepLine() != null );

        LdifAttrValLine[] attrVals = first.getAttrVals();
        assertEquals( 4, attrVals.length );
        assertEquals( "objectClass", attrVals[0].getUnfoldedAttributeDescription() );
        assertEquals( "top", attrVals[0].getValueAsString() );
        assertEquals( "objectClass", attrVals[1].getUnfoldedAttributeDescription() );
        assertEquals( "person", attrVals[1].getValueAsString() );
        assertEquals( "cn", attrVals[2].getUnfoldedAttributeDescription() );
        assertEquals( "foo", attrVals[2].getValueAsString() );
        assertEquals( "sn", attrVals[3].getUnfoldedAttributeDescription() );
        assertEquals( "bar", attrVals[3].getValueAsString() );

        LdifContentRecord second = ( LdifContentRecord ) model.getRecords()[1];
        assertEquals( "cn=bar,dc=example,dc=com", second.getDnLine().getValueAsString() );
        assertEquals( 1, second.getAttrVals().length );

        assertEquals( ldif, model.toRawString() );
    }


    @Test
    public void testOffsetsAndLengthsOfContainers()
    {
        String ldif = "dn: cn=a\ncn: a\n\ndn: cn=b\ncn: b\n";

        LdifFile model = parse( ldif );

        List<LdifContainer> containers = model.getContainers();
        assertEquals( 2, containers.size() );
        assertEquals( 0, containers.get( 0 ).getOffset() );
        assertEquals( 16, containers.get( 0 ).getLength() );
        assertEquals( 16, containers.get( 1 ).getOffset() );
        assertEquals( 15, containers.get( 1 ).getLength() );
    }


    @Test
    public void testLastRecordWithoutEmptyLineIsValid()
    {
        LdifFile model = parse( "dn: cn=bar,dc=example,dc=com\ncn: bar\n" );

        LdifContentRecord record = ( LdifContentRecord ) model.getRecords()[0];

        assertTrue( record.isValid() );
        assertEquals( null, record.getSepLine() );
    }


    @Test
    public void testBase64DnAndValues()
    {
        String ldif = ""
            + "dn:: Y249ZsO2byxkYz1leGFtcGxlLGRjPWNvbQ==\n"
            + "cn:: ZsO2bw==\n"
            + "description:: AAEC/w==\n";

        LdifFile model = parse( ldif );

        LdifContentRecord record = ( LdifContentRecord ) model.getRecords()[0];

        LdifDnLine dnLine = record.getDnLine();
        assertTrue( dnLine.isValueTypeBase64() );
        assertFalse( dnLine.isValueTypeSafe() );
        assertEquals( "Y249ZsO2byxkYz1leGFtcGxlLGRjPWNvbQ==", dnLine.getRawValue() );
        assertEquals( "cn=föo,dc=example,dc=com", dnLine.getValueAsString() );

        LdifAttrValLine cn = record.getAttrVals()[0];
        assertTrue( cn.isValueTypeBase64() );
        assertEquals( "föo", cn.getValueAsString() );
        assertArrayEquals( new byte[]
            { 'f', ( byte ) 0xc3, ( byte ) 0xb6, 'o' }, cn.getValueAsBinary() );

        LdifAttrValLine description = record.getAttrVals()[1];
        assertArrayEquals( new byte[]
            { 0, 1, 2, ( byte ) 0xff }, description.getValueAsBinary() );
        assertTrue( description.getValueAsObject() instanceof byte[] );

        assertEquals( ldif, model.toRawString() );
    }


    @Test
    public void testSafeValueIsReturnedAsString()
    {
        LdifFile model = parse( "dn: cn=foo\ncn: foo\n" );

        LdifAttrValLine cn = ( ( LdifContentRecord ) model.getRecords()[0] ).getAttrVals()[0];

        assertTrue( cn.isValueTypeSafe() );
        assertFalse( cn.isValueTypeBase64() );
        assertFalse( cn.isValueTypeURL() );
        assertEquals( "foo", cn.getValueAsObject() );
        assertArrayEquals( new byte[]
            { 'f', 'o', 'o' }, cn.getValueAsBinary() );
    }


    @Test
    public void testWindowsLineBreaks()
    {
        String ldif = "dn: cn=foo\r\ncn: foo\r\n\r\ndn: cn=bar\r\ncn: bar\r\n";

        LdifFile model = parse( ldif );

        assertEquals( 2, model.getRecords().length );
        LdifContentRecord first = ( LdifContentRecord ) model.getRecords()[0];
        assertEquals( "cn=foo", first.getDnLine().getValueAsString() );
        assertEquals( "foo", first.getAttrVals()[0].getValueAsString() );
        assertEquals( "\r\n", first.getSepLine().toRawString() );
        assertEquals( "cn=bar", model.getRecords()[1].getDnLine().getValueAsString() );

        assertEquals( ldif, model.toRawString() );
    }


    @Test
    public void testFoldedDnAndValue()
    {
        String ldif = ""
            + "dn: cn=very long na\n"
            + " me,dc=example\n"
            + "description: abc\n"
            + " def\n"
            + " ghi\n";

        LdifFile model = parse( ldif );

        LdifContentRecord record = ( LdifContentRecord ) model.getRecords()[0];

        assertEquals( "cn=very long na\n me,dc=example", record.getDnLine().getRawValue() );
        assertEquals( "cn=very long name,dc=example", record.getDnLine().getUnfoldedValue() );
        assertEquals( "cn=very long name,dc=example", record.getDnLine().getValueAsString() );

        LdifAttrValLine description = record.getAttrVals()[0];
        assertEquals( "abc\n def\n ghi", description.getRawValue() );
        assertEquals( "abcdefghi", description.getUnfoldedValue() );

        assertEquals( ldif, model.toRawString() );
    }


    @Test
    public void testFoldedValueWithWindowsLineBreaks()
    {
        LdifFile model = parse( "dn: cn=foo\r\ndescription: abc\r\n def\r\n" );

        LdifAttrValLine description = ( ( LdifContentRecord ) model.getRecords()[0] ).getAttrVals()[0];

        assertEquals( "abcdef", description.getUnfoldedValue() );
    }


    @Test
    public void testNoSpaceOrManySpacesAfterColon()
    {
        LdifFile model = parse( "dn:    cn=foo\ncn:foo\n" );

        LdifContentRecord record = ( LdifContentRecord ) model.getRecords()[0];

        assertEquals( ":    ", record.getDnLine().getRawValueType() );
        assertEquals( "cn=foo", record.getDnLine().getValueAsString() );
        assertEquals( ":", record.getAttrVals()[0].getRawValueType() );
        assertEquals( "foo", record.getAttrVals()[0].getValueAsString() );
    }


    @Test
    public void testLastLineWithoutLineBreak()
    {
        LdifFile model = parse( "dn: cn=foo\ncn: foo" );

        LdifContentRecord record = ( LdifContentRecord ) model.getRecords()[0];

        LdifAttrValLine cn = record.getAttrVals()[0];
        assertEquals( "foo", cn.getValueAsString() );
        // the line itself is flagged invalid because it lacks the line separator
        assertFalse( cn.isValid() );
        assertEquals( "dn: cn=foo\ncn: foo", model.toRawString() );
    }


    @Test
    public void testEmptyInput()
    {
        LdifFile model = parse( "" );

        assertTrue( model.isContentType() );
        assertEquals( 0, model.getContainers().size() );
        assertEquals( 0, model.getRecords().length );
        assertEquals( null, model.getLastContainer() );
        assertEquals( "", model.toRawString() );
    }


    @Test
    public void testAttributeDescriptionWithOptions()
    {
        LdifFile model = parse( "dn: cn=foo\nuserCertificate;binary:: AAE=\ncn;lang-de: foo\n" );

        LdifAttrValLine[] attrVals = ( ( LdifContentRecord ) model.getRecords()[0] ).getAttrVals();

        assertEquals( "userCertificate;binary", attrVals[0].getUnfoldedAttributeDescription() );
        assertArrayEquals( new byte[]
            { 0, 1 }, attrVals[0].getValueAsBinary() );
        assertEquals( "cn;lang-de", attrVals[1].getUnfoldedAttributeDescription() );
    }
}
