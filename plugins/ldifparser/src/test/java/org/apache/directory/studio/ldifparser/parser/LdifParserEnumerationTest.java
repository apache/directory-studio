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
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.ldifparser.model.LdifEnumeration;
import org.apache.directory.studio.ldifparser.model.container.LdifContainer;
import org.junit.jupiter.api.Test;


/**
 * Characterization tests for the streaming API {@link LdifParser#parse(java.io.Reader)},
 * which is used when importing LDIF files.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdifParserEnumerationTest
{
    private static final String LDIF = ""
        + "# header comment\n"
        + "version: 1\n"
        + "\n"
        + "dn: cn=a\n"
        + "cn: a\n"
        + "\n"
        + "# between records\n"
        + "dn: cn=b\n"
        + "changetype: modify\n"
        + "replace: cn\n"
        + "cn: b\n"
        + "-\n"
        + "\n"
        + "garbage\n"
        + "\n"
        + "dn: cn=c\n"
        + "changetype: delete\n"
        + "\n";


    private static List<String> describe( List<LdifContainer> containers )
    {
        List<String> result = new ArrayList<String>();

        for ( LdifContainer container : containers )
        {
            result.add( container.getClass().getSimpleName() + ":" + container.toRawString() );
        }

        return result;
    }


    @Test
    public void testEnumerationReturnsContainersInOrder() throws Exception
    {
        List<LdifContainer> streamed = new ArrayList<LdifContainer>();
        LdifEnumeration enumeration = new LdifParser().parse( new StringReader( LDIF ) );

        while ( enumeration.hasNext() )
        {
            streamed.add( enumeration.next() );
        }

        List<String> expected = new ArrayList<String>();
        expected.add( "LdifCommentContainer:# header comment\n" );
        expected.add( "LdifVersionContainer:version: 1\n" );
        expected.add( "LdifSepContainer:\n" );
        expected.add( "LdifContentRecord:dn: cn=a\ncn: a\n\n" );
        expected.add( "LdifCommentContainer:# between records\n" );
        expected.add( "LdifChangeModifyRecord:dn: cn=b\nchangetype: modify\nreplace: cn\ncn: b\n-\n\n" );
        expected.add( "LdifInvalidContainer:garbage\n" );
        expected.add( "LdifSepContainer:\n" );
        expected.add( "LdifChangeDeleteRecord:dn: cn=c\nchangetype: delete\n\n" );

        assertEquals( expected, describe( streamed ) );
    }


    @Test
    public void testEnumerationIsExhausted() throws Exception
    {
        LdifEnumeration enumeration = new LdifParser().parse( new StringReader( "dn: cn=a\ncn: a\n" ) );

        assertEquals( "dn: cn=a\ncn: a\n", enumeration.next().toRawString() );
        assertFalse( enumeration.hasNext() );
        assertNull( enumeration.next() );
    }


    @Test
    public void testEmptyReader() throws Exception
    {
        LdifEnumeration enumeration = new LdifParser().parse( new StringReader( "" ) );

        assertFalse( enumeration.hasNext() );
    }
}
