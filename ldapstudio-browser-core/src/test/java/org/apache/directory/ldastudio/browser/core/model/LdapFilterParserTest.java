/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */

package org.apache.directory.ldastudio.browser.core.model;

import junit.framework.TestCase;

import org.apache.directory.ldapstudio.browser.core.model.filter.parser.LdapFilterParser;

/**
 * Test the filter parser
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class LdapFilterParserTest extends TestCase
{
    private LdapFilterParser parser = new LdapFilterParser();
    
    /**
     * Tests an equals filter
     */
    public void testEqualsFilter()
    {
        parser.parse( "(cn=test)" );
        assertEquals( "(cn=test)", parser.getModel().toString());
        assertTrue( parser.getModel().isValid() );
    }
    
    /**
     * Tests an present filter
     */
    public void testPresentFilter()
    {
        parser.parse( "(cn=*)" );
        assertEquals( "(cn=*)", parser.getModel().toString());
        assertTrue( parser.getModel().isValid() );
    }
    
    /**
     * Tests an extensible filter
     * 
     * From RFC4515:
     * The first example shows use of the matching rule "caseExactMatch."
     */
    public void testExtensibleFilterRFC4515_1()
    {
        parser.parse( "(cn:caseExactMatch:=Fred Flintstone)" );
        assertEquals( "(cn:caseExactMatch:=Fred Flintstone)", parser.getModel().toString());
        assertTrue( parser.getModel().isValid() );
    }
    /**
     * Tests an extensible filter.
     * 
     * From RFC4515:
     * The second example demonstrates use of a MatchingRuleAssertion form
     * without a matchingRule.
     */
    public void testExtensibleFilterRFC4515_2()
    {
        parser.parse( "(cn:=Betty Rubble)" );
        assertEquals( "(cn:=Betty Rubble)", parser.getModel().toString());
        assertTrue( parser.getModel().isValid() );
    }
    /**
     * Tests an extensible filter.
     * 
     * From RFC4515:
     * The third example illustrates the use of the ":oid" notation to
     * indicate that the matching rule identified by the OID "2.4.6.8.10"
     * should be used when making comparisons, and that the attributes of an
     * entry's distinguished name should be considered part of the entry
     * when evaluating the match (indicated by the use of ":dn").
     */
    public void testExtensibleFilterRFC4515_3()
    {
        parser.parse( "(sn:dn:2.4.6.8.10:=Barney Rubble)" );
        assertEquals( "(sn:dn:2.4.6.8.10:=Barney Rubble)", parser.getModel().toString());
        assertTrue( parser.getModel().isValid() );
    }
    /**
     * Tests an extensible filter.
     * 
     * From RFC4515:
     * The fourth example denotes an equality match, except that DN
     * components should be considered part of the entry when doing the
     * match.
     */
    public void testExtensibleFilterRFC4515_4()
    {
        parser.parse( "(o:dn:=Ace Industry)" );
        assertEquals( "(o:dn:=Ace Industry)", parser.getModel().toString());
        assertTrue( parser.getModel().isValid() );
    }
    /**
     * Tests an extensible filter.
     * 
     * From RFC4515:
     * The fifth example is a filter that should be applied to any attribute
     * supporting the matching rule given (since the <attr> has been
     * omitted).
     */
    public void testExtensibleFilterRFC4515_5()
    {
        parser.parse( "(:1.2.3:=Wilma Flintstone)" );
        assertEquals( "(:1.2.3:=Wilma Flintstone)", parser.getModel().toString());
        assertTrue( parser.getModel().isValid() );
    }
    /**
     * Tests an extensible filter.
     * 
     * From RFC4515:
     * The sixth and final example is also a filter that should be applied
     * to any attribute supporting the matching rule given.  Attributes
     * supporting the matching rule contained in the DN should also be
     * considered.
     */
    public void testExtensibleFilterRFC4515_6()
    {
        parser.parse( "(:DN:2.4.6.8.10:=Dino)" );
        assertEquals( "(:DN:2.4.6.8.10:=Dino)", parser.getModel().toString());
        assertTrue( parser.getModel().isValid() );
    }

}
