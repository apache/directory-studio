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

package org.apache.directory.studio.ldapbrowser.core.model;


import junit.framework.TestCase;

import org.apache.directory.studio.ldapbrowser.core.model.filter.parser.LdapFilterParser;


/**
 * Tests the filter parser.
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
        assertEquals( "(cn=test)", parser.getModel().toString() );
        assertEquals( "(cn=test)", parser.getModel().toUserProvidedString() );
        assertTrue( parser.getModel().isValid() );
    }


    /**
     * Tests an greater equals filter
     */
    public void testGreaterEqualsFilter()
    {
        parser.parse( "(cn>=test)" );
        assertEquals( "(cn>=test)", parser.getModel().toString() );
        assertEquals( "(cn>=test)", parser.getModel().toUserProvidedString() );
        assertTrue( parser.getModel().isValid() );
    }


    /**
     * Tests an less equals filter
     */
    public void testLessEqualsFilter()
    {
        parser.parse( "(cn<=test)" );
        assertEquals( "(cn<=test)", parser.getModel().toString() );
        assertEquals( "(cn<=test)", parser.getModel().toUserProvidedString() );
        assertTrue( parser.getModel().isValid() );
    }


    /**
     * Tests an aprox filter
     */
    public void testAproxFilter()
    {
        parser.parse( "(cn~=test)" );
        assertEquals( "(cn~=test)", parser.getModel().toString() );
        assertEquals( "(cn~=test)", parser.getModel().toUserProvidedString() );
        assertTrue( parser.getModel().isValid() );
    }


    /**
     * Tests an substring filter
     */
    public void testSubstringFilter()
    {
        parser.parse( "(cn=te*st)" );
        assertEquals( "(cn=te*st)", parser.getModel().toString() );
        assertEquals( "(cn=te*st)", parser.getModel().toUserProvidedString() );
        assertTrue( parser.getModel().isValid() );
    }


    /**
     * Tests an present filter
     */
    public void testPresentFilter()
    {
        parser.parse( "(cn=*)" );
        assertEquals( "(cn=*)", parser.getModel().toString() );
        assertEquals( "(cn=*)", parser.getModel().toUserProvidedString() );
        assertTrue( parser.getModel().isValid() );
    }


    /**
     * Tests an simple filter
     */
    public void testRFC4515_1()
    {
        parser.parse( "(cn=Babs Jensen)" );
        assertEquals( "(cn=Babs Jensen)", parser.getModel().toString() );
        assertEquals( "(cn=Babs Jensen)", parser.getModel().toUserProvidedString() );
        assertTrue( parser.getModel().isValid() );
    }


    /**
     * Tests an not filter
     */
    public void testRFC4515_2()
    {
        parser.parse( "(!(cn=Tim Howes))" );
        assertEquals( "(!(cn=Tim Howes))", parser.getModel().toString() );
        assertEquals( "(!(cn=Tim Howes))", parser.getModel().toUserProvidedString() );
        assertTrue( parser.getModel().isValid() );
    }


    /**
     * Tests an and/or filter
     */
    public void testRFC4515_3()
    {
        parser.parse( "(&(objectClass=Person)(|(sn=Jensen)(cn=Babs J*)))" );
        assertEquals( "(&(objectClass=Person)(|(sn=Jensen)(cn=Babs J*)))", parser.getModel().toString() );
        assertEquals( "(&(objectClass=Person)(|(sn=Jensen)(cn=Babs J*)))", parser.getModel().toUserProvidedString() );
        assertTrue( parser.getModel().isValid() );
    }


    /**
     * Tests an substring filter
     */
    public void testRFC4515_4()
    {
        parser.parse( "(o=univ*of*mich*)" );
        assertEquals( "(o=univ*of*mich*)", parser.getModel().toString() );
        assertEquals( "(o=univ*of*mich*)", parser.getModel().toUserProvidedString() );
        assertTrue( parser.getModel().isValid() );
    }


    /**
     * Tests an empty assertion value
     */
    public void testRFC4515_5()
    {
        parser.parse( "(seeAlso=)" );
        assertEquals( "(seeAlso=)", parser.getModel().toString() );
        assertEquals( "(seeAlso=)", parser.getModel().toUserProvidedString() );
        assertTrue( parser.getModel().isValid() );
    }


    /**
     * Tests an filter with escaped assertion value.
     * 
     * From RFC4515:
     * The first example shows the use of the escaping mechanism to
     * represent parenthesis characters.  
     */
    public void testEscapeRFC4515_1()
    {
        parser.parse( "(o=Parens R Us \\28for all your parenthetical needs\\29)" );
        assertEquals( "(o=Parens R Us \\28for all your parenthetical needs\\29)", parser.getModel().toString() );
        assertEquals( "(o=Parens R Us \\28for all your parenthetical needs\\29)", parser.getModel()
            .toUserProvidedString() );
        assertTrue( parser.getModel().isValid() );
    }


    /**
     * Tests an filter with escaped assertion value.
     * 
     * From RFC4515:
     * The second shows how to represent
     * a "*" in an assertion value, preventing it from being interpreted as
     * a substring indicator.
     */
    public void testEscapeRFC4515_2()
    {
        parser.parse( "(cn=*\\2A*)" );
        assertEquals( "(cn=*\\2A*)", parser.getModel().toString() );
        assertEquals( "(cn=*\\2A*)", parser.getModel().toUserProvidedString() );
        assertTrue( parser.getModel().isValid() );
    }


    /**
     * Tests an filter with escaped assertion value.
     * 
     * From RFC4515:
     * The third illustrates the escaping of the backslash character.
     */
    public void testEscapeRFC4515_3()
    {
        parser.parse( "(filename=C:\\5cMyFile)" );
        assertEquals( "(filename=C:\\5cMyFile)", parser.getModel().toString() );
        assertEquals( "(filename=C:\\5cMyFile)", parser.getModel().toUserProvidedString() );
        assertTrue( parser.getModel().isValid() );
    }


    /**
     * Tests an filter with escaped assertion value.
     * 
     * From RFC4515:
     * The fourth example shows a filter searching for the four-octet value
     * 00 00 00 04 (hex), illustrating the use of the escaping mechanism to
     * represent arbitrary data, including NUL characters.
     */
    public void testEscapeRFC4515_4()
    {
        parser.parse( "(bin=\\00\\00\\00\\04)" );
        assertEquals( "(bin=\\00\\00\\00\\04)", parser.getModel().toString() );
        assertEquals( "(bin=\\00\\00\\00\\04)", parser.getModel().toUserProvidedString() );
        assertTrue( parser.getModel().isValid() );
    }


    /**
     * Tests an filter with escaped assertion value.
     * 
     * From RFC4515:
     * The fifth example illustrates the use of the escaping mechanism to
     * represent various non-ASCII UTF-8 characters.  Specifically, there
     * are 5 characters in the &lt;assertionvalue> portion of this example:
     * LATIN CAPITAL LETTER L (U+004C), LATIN SMALL LETTER U (U+0075), LATIN
     * SMALL LETTER C WITH CARON (U+010D), LATIN SMALL LETTER I (U+0069),
     * and LATIN SMALL LETTER C WITH ACUTE (U+0107).
     */
    public void testEscapeRFC4515_5()
    {
        parser.parse( "(sn=Lu\\c4\\8di\\c4\\87)" );
        assertEquals( "(sn=Lu\\c4\\8di\\c4\\87)", parser.getModel().toString() );
        assertEquals( "(sn=Lu\\c4\\8di\\c4\\87)", parser.getModel().toUserProvidedString() );
        assertTrue( parser.getModel().isValid() );
    }


    /**
     * Tests an filter with escaped assertion value.
     * 
     * From RFC4515:
     * The sixth and final example demonstrates assertion of a BER-encoded�
     * value.
     */
    public void testEscapeRFC4515_6()
    {
        parser.parse( "(1.3.6.1.4.1.1466.0=\\04\\02\\48\\69)" );
        assertEquals( "(1.3.6.1.4.1.1466.0=\\04\\02\\48\\69)", parser.getModel().toString() );
        assertEquals( "(1.3.6.1.4.1.1466.0=\\04\\02\\48\\69)", parser.getModel().toUserProvidedString() );
        assertTrue( parser.getModel().isValid() );
    }


    /**
     * Tests an extensible filter.
     * 
     * From RFC4515:
     * The first example shows use of the matching rule "caseExactMatch."
     */
    public void testExtensibleFilterRFC4515_1()
    {
        parser.parse( "(cn:caseExactMatch:=Fred Flintstone)" );
        assertEquals( "(cn:caseExactMatch:=Fred Flintstone)", parser.getModel().toString() );
        assertEquals( "(cn:caseExactMatch:=Fred Flintstone)", parser.getModel().toUserProvidedString() );
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
        assertEquals( "(cn:=Betty Rubble)", parser.getModel().toString() );
        assertEquals( "(cn:=Betty Rubble)", parser.getModel().toUserProvidedString() );
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
        assertEquals( "(sn:dn:2.4.6.8.10:=Barney Rubble)", parser.getModel().toString() );
        assertEquals( "(sn:dn:2.4.6.8.10:=Barney Rubble)", parser.getModel().toUserProvidedString() );
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
        assertEquals( "(o:dn:=Ace Industry)", parser.getModel().toString() );
        assertEquals( "(o:dn:=Ace Industry)", parser.getModel().toUserProvidedString() );
        assertTrue( parser.getModel().isValid() );
    }


    /**
     * Tests an extensible filter.
     * 
     * From RFC4515:
     * The fifth example is a filter that should be applied to any attribute
     * supporting the matching rule given (since the &lt;attr> has been
     * omitted).
     */
    public void testExtensibleFilterRFC4515_5()
    {
        parser.parse( "(:1.2.3:=Wilma Flintstone)" );
        assertEquals( "(:1.2.3:=Wilma Flintstone)", parser.getModel().toString() );
        assertEquals( "(:1.2.3:=Wilma Flintstone)", parser.getModel().toUserProvidedString() );
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
        assertEquals( "(:DN:2.4.6.8.10:=Dino)", parser.getModel().toString() );
        assertEquals( "(:DN:2.4.6.8.10:=Dino)", parser.getModel().toUserProvidedString() );
        assertTrue( parser.getModel().isValid() );
    }


    /**
     * Test for DIRSTUIO-210.
     */
    public void testDIRSTUDIO210()
    {
        parser.parse( "(objectClass>=z*) " );
        assertEquals( "(objectClass>=)", parser.getModel().toString() );
        assertEquals( "(objectClass>=z*) ", parser.getModel().toUserProvidedString() );
        assertFalse( parser.getModel().isValid() );
    }


    /**
     * Test for DIRSTUIO-279.
     */
    public void testDIRSTUDIO279()
    {
        parser.parse( " (&\n    (objectClass=person)\n    (cn=a*)\n) " );
        assertEquals( "(&(objectClass=person)(cn=a*))", parser.getModel().toString() );
        assertEquals( " (&\n    (objectClass=person)\n    (cn=a*)\n) ", parser.getModel().toUserProvidedString() );
        assertFalse( parser.getModel().isValid() );
    }
}
