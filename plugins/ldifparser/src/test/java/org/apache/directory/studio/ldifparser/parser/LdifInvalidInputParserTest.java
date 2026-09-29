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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.apache.directory.studio.ldifparser.model.LdifFile;
import org.apache.directory.studio.ldifparser.model.LdifInvalidPart;
import org.apache.directory.studio.ldifparser.model.LdifPart;
import org.apache.directory.studio.ldifparser.model.container.LdifContainer;
import org.apache.directory.studio.ldifparser.model.container.LdifContentRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifInvalidContainer;
import org.apache.directory.studio.ldifparser.model.container.LdifSepContainer;
import org.apache.directory.studio.ldifparser.model.lines.LdifAttrValLine;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


/**
 * Characterization tests for how the LDIF parser deals with invalid input.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdifInvalidInputParserTest
{
    private static LdifFile parse( String ldif )
    {
        LdifFile model = new LdifParser().parse( ldif );

        // whatever the input, the model always reproduces it exactly
        assertEquals( ldif, model.toRawString() );

        return model;
    }


    private static void assertInvalidContainer( LdifContainer container, String raw )
    {
        assertTrue( container instanceof LdifInvalidContainer );
        assertFalse( container.isValid() );
        assertEquals( "Unexpected Token", container.getInvalidString() );
        assertEquals( raw, container.toRawString() );
    }


    @Test
    public void testTypoInDnKeyword()
    {
        LdifFile model = parse( "dm: cn=foo,dc=example,dc=com\ncn: foo\n" );

        assertEquals( 0, model.getRecords().length );
        List<LdifContainer> containers = model.getContainers();
        assertEquals( 2, containers.size() );
        assertInvalidContainer( containers.get( 0 ), "dm: cn=foo,dc=example,dc=com\n" );
        assertInvalidContainer( containers.get( 1 ), "cn: foo\n" );
    }


    @Test
    public void testMissingDn()
    {
        LdifFile model = parse( "cn: foo\nsn: bar\n" );

        assertEquals( 0, model.getRecords().length );
        List<LdifContainer> containers = model.getContainers();
        assertEquals( 2, containers.size() );
        assertInvalidContainer( containers.get( 0 ), "cn: foo\n" );
        assertInvalidContainer( containers.get( 1 ), "sn: bar\n" );
    }


    @Test
    public void testGarbageLineInsideRecord()
    {
        LdifFile model = parse( "dn: cn=foo\ncn: foo\nthis is garbage\nsn: bar\n" );

        assertEquals( 1, model.getRecords().length );
        LdifContentRecord record = ( LdifContentRecord ) model.getRecords()[0];

        // the garbage becomes an invalid attribute line
        LdifAttrValLine[] attrVals = record.getAttrVals();
        assertEquals( 3, attrVals.length );
        assertEquals( "foo", attrVals[0].getValueAsString() );
        assertEquals( "this", attrVals[1].getUnfoldedAttributeDescription() );
        assertFalse( attrVals[1].isValid() );
        assertEquals( "bar", attrVals[2].getValueAsString() );

        int invalidParts = 0;
        for ( LdifPart part : record.getParts() )
        {
            if ( part instanceof LdifInvalidPart )
            {
                invalidParts++;
                assertEquals( " is garbage\n", part.toRawString() );
            }
        }
        assertEquals( 1, invalidParts );
    }


    @Test
    public void testGarbageBeforeFirstRecord()
    {
        LdifFile model = parse( "hello world\n\ndn: cn=foo\ncn: foo\n" );

        List<LdifContainer> containers = model.getContainers();
        assertEquals( 3, containers.size() );
        assertInvalidContainer( containers.get( 0 ), "hello world\n" );
        assertTrue( containers.get( 1 ) instanceof LdifSepContainer );
        assertTrue( containers.get( 2 ) instanceof LdifContentRecord );
        assertEquals( 1, model.getRecords().length );
        assertEquals( "cn=foo", model.getRecords()[0].getDnLine().getValueAsString() );
    }


    @Test
    public void testAttributeWithoutValueSeparator()
    {
        LdifFile model = parse( "dn: cn=foo\ncn\n" );

        LdifContentRecord record = ( LdifContentRecord ) model.getRecords()[0];
        LdifAttrValLine cn = record.getAttrVals()[0];

        assertFalse( cn.isValid() );
        assertEquals( "cn", cn.getUnfoldedAttributeDescription() );
        assertEquals( "Missing value type ':', '::' or ':<'", cn.getInvalidString() );
    }


    @Test
    public void testNullInput()
    {
        LdifFile model = new LdifParser().parse( ( String ) null );

        assertEquals( 0, model.getContainers().size() );
    }


    @Test
    @Disabled("Suspected bug: LdifContainer.isAbstractValid() returns true as soon as the first line (the dn) "
        + "is valid and never checks the remaining parts, so a record with a garbage line is valid "
        + "and ImportLdifRunnable imports it")
    public void testRecordWithGarbageLineIsInvalid()
    {
        LdifFile model = parse( "dn: cn=foo\ncn: foo\nthis is garbage\nsn: bar\n" );

        assertFalse( model.getRecords()[0].isValid() );
    }
}
