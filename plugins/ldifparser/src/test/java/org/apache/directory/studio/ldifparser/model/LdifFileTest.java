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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.directory.studio.ldifparser.model.container.LdifChangeModifyRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifContainer;
import org.apache.directory.studio.ldifparser.model.container.LdifContentRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifModSpec;
import org.apache.directory.studio.ldifparser.parser.LdifParser;
import org.junit.jupiter.api.Test;


/**
 * Characterization tests for the helper methods of {@link LdifFile}.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdifFileTest
{
    // container 0: offset 0, length 16; container 1: offset 16, length 48
    private static final String LDIF = ""
        + "dn: cn=a\n"
        + "cn: a\n"
        + "\n"
        + "dn: cn=b\n"
        + "changetype: modify\n"
        + "replace: cn\n"
        + "cn: b\n"
        + "-\n";


    @Test
    public void testContentAndChangeType()
    {
        LdifFile contentOnly = new LdifParser().parse( "dn: cn=a\ncn: a\n" );
        assertTrue( contentOnly.isContentType() );
        assertFalse( contentOnly.isChangeType() );

        LdifFile withChange = new LdifParser().parse( LDIF );
        assertFalse( withChange.isContentType() );
        assertTrue( withChange.isChangeType() );
    }


    @Test
    public void testAddContainerOfChangeRecordMakesFileChangeType()
    {
        LdifFile model = new LdifFile();
        assertTrue( model.isContentType() );

        model.addContainer( LdifContentRecord.create( "cn=a" ) );
        assertTrue( model.isContentType() );

        model.addContainer( LdifChangeModifyRecord.create( "cn=b" ) );
        assertTrue( model.isChangeType() );
        assertEquals( 2, model.getRecords().length );
    }


    @Test
    public void testGetContainerByOffset()
    {
        LdifFile model = new LdifParser().parse( LDIF );
        LdifContainer first = model.getContainers().get( 0 );
        LdifContainer second = model.getContainers().get( 1 );

        assertEquals( 16, first.getLength() );
        assertEquals( 16, second.getOffset() );

        assertSame( first, LdifFile.getContainer( model, 0 ) );
        assertSame( first, LdifFile.getContainer( model, 15 ) );
        assertSame( second, LdifFile.getContainer( model, 16 ) );
        assertSame( second, LdifFile.getContainer( model, LDIF.length() - 1 ) );
        assertNull( LdifFile.getContainer( model, LDIF.length() ) );
        assertNull( LdifFile.getContainer( model, -1 ) );
        assertNull( LdifFile.getContainer( null, 0 ) );
    }


    @Test
    public void testGetInnerContainerByOffset()
    {
        LdifFile model = new LdifParser().parse( LDIF );
        LdifContainer modify = model.getContainers().get( 1 );

        int modSpecOffset = LDIF.indexOf( "replace: cn" );

        LdifModSpec modSpec = LdifFile.getInnerContainer( modify, modSpecOffset );
        assertTrue( modSpec != null );
        assertTrue( modSpec.isReplace() );

        // the dn line of the modify record is not inside a mod spec
        assertNull( LdifFile.getInnerContainer( modify, modify.getOffset() ) );
        // offsets outside the container
        assertNull( LdifFile.getInnerContainer( modify, 0 ) );
        assertNull( LdifFile.getInnerContainer( null, modSpecOffset ) );
    }


    @Test
    public void testGetLastContainer()
    {
        LdifFile model = new LdifParser().parse( LDIF );

        assertSame( model.getContainers().get( 1 ), model.getLastContainer() );
        assertNull( new LdifFile().getLastContainer() );
    }
}
