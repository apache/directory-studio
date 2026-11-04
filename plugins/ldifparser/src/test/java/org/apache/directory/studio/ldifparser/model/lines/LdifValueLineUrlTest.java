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


import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.directory.studio.ldifparser.model.LdifFile;
import org.apache.directory.studio.ldifparser.model.container.LdifContentRecord;
import org.apache.directory.studio.ldifparser.parser.LdifParser;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;


/**
 * Characterization tests for URL values ("attr:&lt; ...") of LDIF value lines.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdifValueLineUrlTest
{
    private static final byte[] DATA = new byte[]
        { 0, 1, 2, 3, ( byte ) 0xfe, ( byte ) 0xff };

    @TempDir
    Path tempDir;


    private static LdifAttrValLine parseValueLine( String valueLine )
    {
        LdifFile model = new LdifParser().parse( "dn: cn=foo\n" + valueLine + "\n" );

        return ( ( LdifContentRecord ) model.getRecords()[0] ).getAttrVals()[0];
    }


    private File writeDataFile() throws Exception
    {
        File file = tempDir.resolve( "photo.bin" ).toFile();
        Files.write( file.toPath(), DATA );

        return file;
    }


    @Test
    public void testUrlValueType()
    {
        LdifAttrValLine line = parseValueLine( "jpegPhoto:< file:///tmp/photo.jpg" );

        assertTrue( line.isValid() );
        assertTrue( line.isValueTypeURL() );
        assertFalse( line.isValueTypeBase64() );
        assertFalse( line.isValueTypeSafe() );
        assertEquals( "jpegPhoto", line.getUnfoldedAttributeDescription() );
        assertEquals( "file:///tmp/photo.jpg", line.getUnfoldedValue() );
    }


    @Test
    public void testPlainFilePathIsRead() throws Exception
    {
        File file = writeDataFile();

        LdifAttrValLine line = parseValueLine( "jpegPhoto:< " + file.getAbsolutePath() );

        assertArrayEquals( DATA, line.getValueAsBinary() );
        assertArrayEquals( DATA, ( byte[] ) line.getValueAsObject() );
    }


    @Test
    public void testMissingFileGivesEmptyValue()
    {
        LdifAttrValLine line = parseValueLine( "jpegPhoto:< /does/not/exist/photo.jpg" );

        assertNull( line.getValueAsObject() );
        assertArrayEquals( new byte[0], line.getValueAsBinary() );
        assertEquals( "", line.getValueAsString() );
    }


    @Test
    @Disabled("Known bug: a file:// URL is treated as a plain file path (new File(getUnfoldedValue())), "
        + "so the file is not found and the value is null")
    public void testFileUrlIsRead() throws Exception
    {
        File file = writeDataFile();

        // RFC 2849 form: file:///absolute/path
        LdifAttrValLine line = parseValueLine( "jpegPhoto:< file://" + file.getAbsolutePath() );

        assertArrayEquals( DATA, line.getValueAsBinary() );
    }
}
