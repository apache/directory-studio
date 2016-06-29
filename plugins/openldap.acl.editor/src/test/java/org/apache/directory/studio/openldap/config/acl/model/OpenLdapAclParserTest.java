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
package org.apache.directory.studio.openldap.config.acl.model;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import antlr.RecognitionException;

import org.apache.directory.studio.openldap.config.acl.model.AclAttribute;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OpenLdapAclParserTest
{
    @Test( expected = ParseException.class )
    public void testEmpty() throws Exception
    {
    	System.out.println( "\n--> testEmpty" );
        OpenLdapAclParser parser = new OpenLdapAclParser();
        parser.parse( "" );

        fail();
    }


    @Test
    public void testImpliedWhatStarWithAccess() throws Exception
    {
    	System.out.println( "\n--> testImpliedWhatStarWithAccess" );
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access   to by *" );
        assertNotNull( aclItem );

        // Testing the 'what' clause
        AclWhatClause whatClause = aclItem.getWhatClause();
        assertNotNull( whatClause );
        assertTrue( whatClause instanceof AclWhatClauseStar );
        
        System.out.println( "<-- ACL:" + aclItem );
    }


    @Test
    public void testWhatStarWithAccess() throws Exception
    {
    	System.out.println( "\n--> testWhatStarWithAccess" );
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access   to * by *" );
        assertNotNull( aclItem );

        // Testing the 'what' clause
        AclWhatClause whatClause = aclItem.getWhatClause();
        assertNotNull( whatClause );
        assertTrue( whatClause instanceof AclWhatClauseStar );
        
        System.out.println( "<-- ACL:" + aclItem );
    }

    
    @Test
    public void testWhatStarWithoutAccess() throws Exception
    {
    	System.out.println( "\n--> testWhatStarWithoutAccess" );
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( " to * by *" );
        assertNotNull( aclItem );

        // Testing the 'what' clause
        AclWhatClause whatClause = aclItem.getWhatClause();
        assertNotNull( whatClause );
        assertTrue( whatClause instanceof AclWhatClauseStar );
        
        System.out.println( "<-- ACL:" + aclItem );
    }


    @Test
    public void testWhatStarWithSpaces() throws Exception
    {
    	System.out.println( "\n--> testWhatStarWithSpaces" );
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "   access    to     *    by    *    " );
        assertNotNull( aclItem );

        // Testing the 'what' clause
        AclWhatClause whatClause = aclItem.getWhatClause();
        assertNotNull( whatClause );
        assertTrue( whatClause instanceof AclWhatClauseStar );
        
        System.out.println( "<-- ACL:" + aclItem );
    }


    @Test( expected = ParseException.class )
    public void testWhatTwoStars() throws Exception
    {
    	System.out.println( "\n--> testWhatTwoStars" );
        OpenLdapAclParser parser = new OpenLdapAclParser();
        parser.parse( "to * * by *" );

        fail();
    }

    
    //-----------------------------------------------------------------------------------------
    // The WHAT DN rule
    //-----------------------------------------------------------------------------------------
    @Test
    public void testWhatDnValidDnWithQuote() throws Exception
    {
    	System.out.println( "\n--> testWhatDnValidDnWithQuote" );
        String dnPattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to dn=\"" + dnPattern + "\" by *" );
        assertNotNull( aclItem );

        // Testing the 'what' clause
        AclWhatClause whatClause = aclItem.getWhatClause();
        assertNotNull( whatClause );
        assertTrue( whatClause instanceof AclWhatClauseDn );
        AclWhatClauseDn whatClauseDn = (AclWhatClauseDn)whatClause;
        assertEquals( dnPattern, whatClauseDn.getPattern() );
        
        System.out.println( "<-- ACL:" + aclItem );
    }

    
    @Test( expected = ParseException.class )
    public void testWhatDnStar() throws Exception
    {
    	System.out.println( "\n--> testWhatDnStar" );
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        parser.parse( "access to dn=* by *" );
        
        fail();
    }

    
    @Test( expected=ParseException.class)
    public void testWhatDnValidDnNoQuote() throws Exception
    {
    	System.out.println( "\n--> testWhatDnValidDnNoQuote" );
        String dnPattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        parser.parse( "access to dn=" + dnPattern + "\n by *" );
        
        fail();
    }

    
    @Test ( expected=ParseException.class)
    public void testWhatDnBadDn() throws Exception
    {
    	System.out.println( "\n--> testWhatDnBadDn" );
        String dnPattern = "Blah";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
    	parser.parse( "access to dn=\"" + dnPattern + "\"\n by *" );
        
        fail();
    }

    
    //-----------------------------------------------------------------------------------------
    // The basic-dn-style WHAT DN exact
    //-----------------------------------------------------------------------------------------
    @Test
    public void testWhatDnBasicDnExact() throws Exception
    {
    	System.out.println( "\n--> testWhatDnBasicDnExact" );
        String dnPattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to dn.exact=\"" + dnPattern + "\" by *" );
        assertNotNull( aclItem );

        // Testing the 'what' clause
        AclWhatClause whatClause = aclItem.getWhatClause();
        assertNotNull( whatClause );
        assertTrue( whatClause instanceof AclWhatClauseDn );
        AclWhatClauseDn whatClauseDn = (AclWhatClauseDn) whatClause;

        // test the content
        assertEquals( dnPattern, whatClauseDn.getPattern() );
        assertEquals( AclWhatClauseDnTypeEnum.EXACT, whatClauseDn.getType() );

        System.out.println( "<-- ACL:" + aclItem );
    }
    
    
    @Test ( expected=ParseException.class)
    public void testWhatDnBasicDnExactNoQuote() throws Exception
    {
    	System.out.println( "\n--> testWhatDnBasicDnExactNoQuote" );
        String dnPattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        parser.parse( "access to dn.exact=" + dnPattern + " by *" );
        
        fail();
    }
    
    
    @Test ( expected=ParseException.class)
    public void testWhatDnBasicDnExactBadDn() throws Exception
    {
    	System.out.println( "\n--> testWhatDnBasicDnExactBadDn" );
        String dnPattern = "example";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        parser.parse( "access to dn.exact=" + dnPattern + " by *" );
        
        fail();
    }
    
    
    //-----------------------------------------------------------------------------------------
    // The basic-dn-style WHAT DN regex
    //-----------------------------------------------------------------------------------------
    @Test
    public void testWhatDnBasicDnRegex() throws Exception
    {
    	System.out.println( "\n--> testWhatDnBasicDnRegex" );
        String dnPattern = "dc=*";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to dn.regex=\"" + dnPattern + "\" by *" );
        assertNotNull( aclItem );

        // Testing the 'what' clause
        AclWhatClause whatClause = aclItem.getWhatClause();
        assertNotNull( whatClause );
        assertTrue( whatClause instanceof AclWhatClauseDn );
        AclWhatClauseDn whatClauseDn = (AclWhatClauseDn) whatClause;

        // test the content
        assertEquals( dnPattern, whatClauseDn.getPattern() );
        assertEquals( AclWhatClauseDnTypeEnum.REGEX, whatClauseDn.getType() );

        System.out.println( "<-- ACL:" + aclItem );
    }
    
    
    @Test( expected=ParseException.class)
    public void testWhatDnBasicDnRegexNoQuote() throws Exception
    {
    	System.out.println( "\n--> testWhatDnBasicDnRegexNoQuote" );
        String dnPattern = "dc=*,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to dn.regex=" + dnPattern + " by *" );
        
        fail();
    }
    
    
    //-----------------------------------------------------------------------------------------
    // The scope-dn-clause WHAT DN regex
    //-----------------------------------------------------------------------------------------
    @Test
    public void testWhatDnScopeOneQuotedDn() throws Exception
    {
    	System.out.println( "\n--> testWhatDnScopeOneQuotedDn" );
        String dnPattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to dn.one=\"" + dnPattern + "\" by *" );
        assertNotNull( aclItem );

        // Testing the 'what' clause
        AclWhatClause whatClause = aclItem.getWhatClause();
        assertNotNull( whatClause );
        assertTrue( whatClause instanceof AclWhatClauseDn );
        AclWhatClauseDn whatClauseDn = (AclWhatClauseDn) whatClause;

        // test the content
        assertEquals( dnPattern, whatClauseDn.getPattern() );
        assertEquals( AclWhatClauseDnTypeEnum.ONE, whatClauseDn.getType() );

        System.out.println( "<-- ACL:" + aclItem );
    }
    
    
    @Test
    public void testWhatDnScopeOneLevelQuotedDn() throws Exception
    {
    	System.out.println( "\n--> testWhatDnScopeOneLevelQuotedDn" );
        String dnPattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to dn.onelevel=\"" + dnPattern + "\" by *" );
        assertNotNull( aclItem );

        // Testing the 'what' clause
        AclWhatClause whatClause = aclItem.getWhatClause();
        assertNotNull( whatClause );
        assertTrue( whatClause instanceof AclWhatClauseDn );
        AclWhatClauseDn whatClauseDn = (AclWhatClauseDn) whatClause;

        // test the content
        assertEquals( dnPattern, whatClauseDn.getPattern() );
        assertEquals( AclWhatClauseDnTypeEnum.ONE_LEVEL, whatClauseDn.getType() );

        System.out.println( "<-- ACL:" + aclItem );
    }
    
    
    @Test
    public void testWhatDnScopeBaseQuotedDn() throws Exception
    {
    	System.out.println( "\n--> testWhatDnScopeBaseQuotedDn" );
        String dnPattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to dn.base=\"" + dnPattern + "\" by *" );
        assertNotNull( aclItem );

        // Testing the 'what' clause
        AclWhatClause whatClause = aclItem.getWhatClause();
        assertNotNull( whatClause );
        assertTrue( whatClause instanceof AclWhatClauseDn );
        AclWhatClauseDn whatClauseDn = (AclWhatClauseDn) whatClause;

        // test the content
        assertEquals( dnPattern, whatClauseDn.getPattern() );
        assertEquals( AclWhatClauseDnTypeEnum.BASE, whatClauseDn.getType() );

        System.out.println( "<-- ACL:" + aclItem );
    }
    
    
    @Test
    public void testWhatDnScopeBaseobjectQuotedDn() throws Exception
    {
    	System.out.println( "\n--> testWhatDnScopeBaseobjectQuotedDn" );
        String dnPattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to dn.baseobject=\"" + dnPattern + "\" by *" );
        assertNotNull( aclItem );

        // Testing the 'what' clause
        AclWhatClause whatClause = aclItem.getWhatClause();
        assertNotNull( whatClause );
        assertTrue( whatClause instanceof AclWhatClauseDn );
        AclWhatClauseDn whatClauseDn = (AclWhatClauseDn) whatClause;

        // test the content
        assertEquals( dnPattern, whatClauseDn.getPattern() );
        assertEquals( AclWhatClauseDnTypeEnum.BASE_OBJECT, whatClauseDn.getType() );

        System.out.println( "<-- ACL:" + aclItem );
    }
    
    
    @Test
    public void testWhatDnScopeSubQuotedDn() throws Exception
    {
    	System.out.println( "\n--> testWhatDnScopeSubQuotedDn" );
        String dnPattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to dn.sub=\"" + dnPattern + "\" by *" );
        assertNotNull( aclItem );

        // Testing the 'what' clause
        AclWhatClause whatClause = aclItem.getWhatClause();
        assertNotNull( whatClause );
        assertTrue( whatClause instanceof AclWhatClauseDn );
        AclWhatClauseDn whatClauseDn = (AclWhatClauseDn) whatClause;

        // test the content
        assertEquals( dnPattern, whatClauseDn.getPattern() );
        assertEquals( AclWhatClauseDnTypeEnum.SUB, whatClauseDn.getType() );

        System.out.println( "<-- ACL:" + aclItem );
    }
    
    
    @Test
    public void testWhatDnScopeSubtreeQuotedDn() throws Exception
    {
    	System.out.println( "\n--> testWhatDnScopeSubtreeQuotedDn" );
        String dnPattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to dn.subtree=\"" + dnPattern + "\" by *" );
        assertNotNull( aclItem );

        // Testing the 'what' clause
        AclWhatClause whatClause = aclItem.getWhatClause();
        assertNotNull( whatClause );
        assertTrue( whatClause instanceof AclWhatClauseDn );
        AclWhatClauseDn whatClauseDn = (AclWhatClauseDn) whatClause;

        // test the content
        assertEquals( dnPattern, whatClauseDn.getPattern() );
        assertEquals( AclWhatClauseDnTypeEnum.SUBTREE, whatClauseDn.getType() );

        System.out.println( "<-- ACL:" + aclItem );
    }
    
    
    @Test
    public void testWhatDnScopeChildrenQuotedDn() throws Exception
    {
    	System.out.println( "\n--> testWhatDnScopeChildrenQuotedDn" );
        String dnPattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to dn.children=\"" + dnPattern + "\" by *" );
        assertNotNull( aclItem );

        // Testing the 'what' clause
        AclWhatClause whatClause = aclItem.getWhatClause();
        assertNotNull( whatClause );
        assertTrue( whatClause instanceof AclWhatClauseDn );
        AclWhatClauseDn whatClauseDn = (AclWhatClauseDn) whatClause;

        // test the content
        assertEquals( dnPattern, whatClauseDn.getPattern() );
        assertEquals( AclWhatClauseDnTypeEnum.CHILDREN, whatClauseDn.getType() );

        System.out.println( "<-- ACL:" + aclItem );
    }

    
    @Test
    public void testWhatDnScopeOneEmptyDn() throws Exception
    {
    	System.out.println( "\n--> testWhatDnScopeOneEmptyDn" );

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to dn.one=\"\" by *" );
        assertNotNull( aclItem );

        // Testing the 'what' clause
        AclWhatClause whatClause = aclItem.getWhatClause();
        assertNotNull( whatClause );
        assertTrue( whatClause instanceof AclWhatClauseDn );
        AclWhatClauseDn whatClauseDn = (AclWhatClauseDn) whatClause;

        // test the content
        assertEquals( "", whatClauseDn.getPattern() );
        assertEquals( AclWhatClauseDnTypeEnum.ONE, whatClauseDn.getType() );

        System.out.println( "<-- ACL:" + aclItem );
    }

    
    @Test( expected=ParseException.class)
    public void testWhatDnScopeOneNoQuotedDn() throws Exception
    {
    	System.out.println( "\n--> testWhatDnScopeOneNoQuotedDn" );
        String dnPattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        parser.parse( "access to dn.one=" + dnPattern + " by *" );
    }

    
    @Test( expected=ParseException.class)
    public void testWhatDnScopeOneNoDn() throws Exception
    {
    	System.out.println( "\n--> testWhatDnScopeOneNoDn" );

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        parser.parse( "access to dn.one= by *" );
    }

    
    @Test( expected=ParseException.class)
    public void testWhatDnScopeOneStar() throws Exception
    {
    	System.out.println( "\n--> testWhatDnScopeOneStar" );

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        parser.parse( "access to dn.one=* by *" );
    }
    
    
    //-----------------------------------------------------------------------------------------
    // The scope-dn-clause WHAT FILTER 
    //-----------------------------------------------------------------------------------------
    @Test
    public void testWhatFilterSimple() throws Exception
    {
    	System.out.println( "\n--> testWhatFilter" );
        String filter = "(objectclass=*)";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to filter=" + filter + " by *" );
        assertNotNull( aclItem );

        // Testing the 'what' clause
        AclWhatClause whatClause = aclItem.getWhatClause();
        assertNotNull( whatClause );
        assertTrue( whatClause instanceof AclWhatClauseFilter );
        
        AclWhatClauseFilter whatClauseFilter = (AclWhatClauseFilter) whatClause;

        // test the content
        assertEquals( filter, whatClauseFilter.getFilter() );

        System.out.println( "<-- ACL:" + aclItem );
    }


    @Test
    public void testWhatFilterComplex() throws Exception
    {
    	System.out.println( "\n--> testWhatFilterComplex" );
        String filter = "(&(objectclass=*)(cn=test)(!(sn=test)))";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to filter=" + filter + " by *" );
        assertNotNull( aclItem );

        // Testing the 'what' clause
        AclWhatClause whatClause = aclItem.getWhatClause();
        assertNotNull( whatClause );
        assertTrue( whatClause instanceof AclWhatClauseFilter );
        
        AclWhatClauseFilter whatClauseFilter = (AclWhatClauseFilter) whatClause;

        // test the content
        assertEquals( filter, whatClauseFilter.getFilter() );

        System.out.println( "<-- ACL:" + aclItem );
    }


    @Test( expected=ParseException.class)
    public void testWhatFilterWrongSimple() throws Exception
    {
    	System.out.println( "\n--> testWhatFilterWrongSimple" );
        String filter = "(objectclass=*";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        parser.parse( "access to filter=" + filter + " by *" );
        
        fail();
    }
 

    

    @Test
    public void testFail() throws Exception
    {
    	fail();
    }
    
    /*
    @Test
    public void testWhatDnWithoutAccess() throws Exception
    {
        String dnPattern = "dsqdsqdq";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to dn=\"" + dnPattern + "\" by *" );
        assertNotNull( aclItem );

        // Testing the 'what' clause
        AclWhatClause whatClause = aclItem.getWhatClause();
        assertNotNull( whatClause );
        AclWhatClauseDn whatClauseDn = whatClause.getDnClause();
        assertNotNull( whatClauseDn );
        assertEquals( dnPattern, whatClauseDn.getPattern() );
    }


    @Test
    public void testWhatTwoDns() throws Exception
    {
        try
        {
            OpenLdapAclParser parser = new OpenLdapAclParser();
            parser.parse( "to dn=\"dsdsfsd\" dn=\"dsdsfsd\" by *" );

            fail();
        }
        catch ( Exception e )
        {
            // Should happen
        }
    }


    @Test
    public void testWhatAttributes() throws Exception
    {
        String attribute = "userPassword";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to attrs=" + attribute + " by *" );
        assertNotNull( aclItem );

        // Testing the 'what' clause
        AclWhatClause whatClause = aclItem.getWhatClause();
        assertNotNull( whatClause );
        AclWhatClauseAttributes whatClauseAttributes = whatClause.getAttributesClause();
        assertNotNull( whatClauseAttributes );
        List<AclAttribute> attributesList = whatClauseAttributes.getAttributes();
        assertEquals( 1, attributesList.size() );
        assertEquals( attribute, attributesList.get( 0 ).getName() );
    }


    @Test
    public void testWhatAttributesWithoutAccess() throws Exception
    {
        String attribute = "userPassword";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to attrs=" + attribute + " by *" );
        assertNotNull( aclItem );

        // Testing the 'what' clause
        AclWhatClause whatClause = aclItem.getWhatClause();
        assertNotNull( whatClause );
        AclWhatClauseAttributes whatClauseAttributes = whatClause.getAttributesClause();
        assertNotNull( whatClauseAttributes );
        List<AclAttribute> attributesList = whatClauseAttributes.getAttributes();
        assertEquals( 1, attributesList.size() );
        assertEquals( attribute, attributesList.get( 0 ).getName() );
    }


    @Test
    public void testWhatAttributesWithSpaces() throws Exception
    {
        String attribute = "userPassword";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "   access    to     attrs=" + attribute + "    by    *    " );
        assertNotNull( aclItem );

        // Testing the 'what' clause
        AclWhatClause whatClause = aclItem.getWhatClause();
        assertNotNull( whatClause );
        AclWhatClauseAttributes whatClauseAttributes = whatClause.getAttributesClause();
        assertNotNull( whatClauseAttributes );
        List<AclAttribute> attributesList = whatClauseAttributes.getAttributes();
        assertEquals( 1, attributesList.size() );
        assertEquals( attribute, attributesList.get( 0 ).getName() );
    }


    @Test
    public void testWhatAttributesMultiple() throws Exception
    {
        String attribute1 = "userPassword";
        String attribute2 = "uid";
        String attribute3 = "cn";

        String attributes = attribute1 + "," + attribute2 + "," + attribute3;

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to attrs=" + attributes + " by *" );
        assertNotNull( aclItem );

        // Testing the 'what' clause
        AclWhatClause whatClause = aclItem.getWhatClause();
        assertNotNull( whatClause );
        AclWhatClauseAttributes whatClauseAttributes = whatClause.getAttributesClause();
        assertNotNull( whatClauseAttributes );
        List<AclAttribute> attributesList = whatClauseAttributes.getAttributes();
        assertEquals( 3, attributesList.size() );
        assertEquals( attribute1, attributesList.get( 0 ).getName() );
        assertEquals( attribute2, attributesList.get( 1 ).getName() );
        assertEquals( attribute3, attributesList.get( 2 ).getName() );
    }


    @Test
    public void testWhatAttributesMultipleWithoutAccess() throws Exception
    {
        String attribute1 = "userPassword";
        String attribute2 = "uid";
        String attribute3 = "cn";

        String attributes = attribute1 + "," + attribute2 + "," + attribute3;

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to attrs=" + attributes + " by *" );
        assertNotNull( aclItem );

        // Testing the 'what' clause
        AclWhatClause whatClause = aclItem.getWhatClause();
        assertNotNull( whatClause );
        AclWhatClauseAttributes whatClauseAttributes = whatClause.getAttributesClause();
        assertNotNull( whatClauseAttributes );
        List<AclAttribute> attributesList = whatClauseAttributes.getAttributes();
        assertEquals( 3, attributesList.size() );
        assertEquals( attribute1, attributesList.get( 0 ).getName() );
        assertEquals( attribute2, attributesList.get( 1 ).getName() );
        assertEquals( attribute3, attributesList.get( 2 ).getName() );
    }


    @Test
    public void testWhatTwoAttributes() throws Exception
    {
        try
        {
            OpenLdapAclParser parser = new OpenLdapAclParser();
            parser.parse( "access to attrs=userPassword attrs=userPassword by *" );

            fail();
        }
        catch ( Exception e )
        {
            // Should happen
        }
    }


    @Test
    public void testWhatFilterWithoutAccess() throws Exception
    {
        String filter = "(objectclass=*)";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to filter=" + filter + " by *" );
        assertNotNull( aclItem );

        // Testing the 'what' clause
        AclWhatClause whatClause = aclItem.getWhatClause();
        assertNotNull( whatClause );
        AclWhatClauseFilter whatClauseFilter = whatClause.getFilterClause();
        assertNotNull( whatClauseFilter );
        assertEquals( filter, whatClauseFilter.getFilter() );
    }


    @Test
    public void testWhatFilterWithSpaces() throws Exception
    {
        String filter = "(objectclass=*)";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "   access    to     filter=" + filter + "    by    *    " );
        assertNotNull( aclItem );

        // Testing the 'what' clause
        AclWhatClause whatClause = aclItem.getWhatClause();
        assertNotNull( whatClause );
        AclWhatClauseFilter whatClauseFilter = whatClause.getFilterClause();
        assertNotNull( whatClauseFilter );
        assertEquals( filter, whatClauseFilter.getFilter() );
    }


    @Test
    public void testWhatFilterComplex() throws Exception
    {
        String filter = "(&(&(!(cn=jbond))(|(ou=ResearchAndDevelopment)(ou=HumanResources)))(objectclass=Person))";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to filter=" + filter + " by *" );
        assertNotNull( aclItem );

        // Testing the 'what' clause
        AclWhatClause whatClause = aclItem.getWhatClause();
        assertNotNull( whatClause );
        AclWhatClauseFilter whatClauseFilter = whatClause.getFilterClause();
        assertNotNull( whatClauseFilter );
        assertEquals( filter, whatClauseFilter.getFilter() );
    }


    @Test
    public void testWhatFilterComplexWithoutAccess() throws Exception
    {
        String filter = "(&(&(!(cn=jbond))(|(ou=ResearchAndDevelopment)(ou=HumanResources)))(objectclass=Person))";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to filter=" + filter + " by *" );
        assertNotNull( aclItem );

        // Testing the 'what' clause
        AclWhatClause whatClause = aclItem.getWhatClause();
        assertNotNull( whatClause );
        AclWhatClauseFilter whatClauseFilter = whatClause.getFilterClause();
        assertNotNull( whatClauseFilter );
        assertEquals( filter, whatClauseFilter.getFilter() );
    }


    @Test
    public void testWhatTwoFilters() throws Exception
    {
        try
        {
            OpenLdapAclParser parser = new OpenLdapAclParser();
            parser.parse( "access to filter=(objectClass=*) filter=(objectClass=*) by *" );

            fail();
        }
        catch ( Exception e )
        {
            // Should happen
        }
    }


    @Test
    public void testWhatDnAndFilter() throws Exception
    {
        String dnPattern = "dsqdsqdq";
        String filter = "(objectclass=*)";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to  dn=\"" + dnPattern + "\" filter=" + filter + " by *" );
        assertNotNull( aclItem );

        // Testing the 'what' clause
        AclWhatClause whatClause = aclItem.getWhatClause();
        assertNotNull( whatClause );
        AclWhatClauseDn whatClauseDn = whatClause.getDnClause();
        assertNotNull( whatClauseDn );
        assertEquals( dnPattern, whatClauseDn.getPattern() );
        AclWhatClauseFilter whatClauseFilter = whatClause.getFilterClause();
        assertNotNull( whatClauseFilter );
        assertEquals( filter, whatClauseFilter.getFilter() );
    }


    @Test
    public void testWhatDnAndAttributes() throws Exception
    {
        String dnPattern = "dsqdsqdq";
        String attribute = "userPassword";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to  dn=\"" + dnPattern + "\" attrs=" + attribute + " by *" );
        assertNotNull( aclItem );

        // Testing the 'what' clause
        AclWhatClause whatClause = aclItem.getWhatClause();
        assertNotNull( whatClause );
        AclWhatClauseDn whatClauseDn = whatClause.getDnClause();
        assertNotNull( whatClauseDn );
        assertEquals( dnPattern, whatClauseDn.getPattern() );
        AclWhatClauseAttributes whatClauseAttributes = whatClause.getAttributesClause();
        assertNotNull( whatClauseAttributes );
        List<AclAttribute> attributesList = whatClauseAttributes.getAttributes();
        assertEquals( 1, attributesList.size() );
        assertEquals( attribute, attributesList.get( 0 ).getName() );
    }


    @Test
    public void testWhatFilterAndAttributes() throws Exception
    {
        String dnPattern = "dsqdsqdq";
        String filter = "(objectclass=*)";
        String attribute = "userPassword";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to dn=\"" + dnPattern + "\" filter=" + filter + " attrs=" + attribute
            + " by *" );
        assertNotNull( aclItem );

        // Testing the 'what' clause
        AclWhatClause whatClause = aclItem.getWhatClause();
        assertNotNull( whatClause );
        AclWhatClauseDn whatClauseDn = whatClause.getDnClause();
        assertNotNull( whatClauseDn );
        assertEquals( dnPattern, whatClauseDn.getPattern() );
        AclWhatClauseFilter whatClauseFilter = whatClause.getFilterClause();
        assertNotNull( whatClauseFilter );
        assertEquals( filter, whatClauseFilter.getFilter() );
        AclWhatClauseAttributes whatClauseAttributes = whatClause.getAttributesClause();
        assertNotNull( whatClauseAttributes );
        List<AclAttribute> attributesList = whatClauseAttributes.getAttributes();
        assertEquals( 1, attributesList.size() );
        assertEquals( attribute, attributesList.get( 0 ).getName() );
    }


    @Test
    public void testWhatDnAndFilterAndAttributes() throws Exception
    {
        String filter = "(objectclass=*)";
        String attribute = "userPassword";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to  filter=" + filter + " attrs=" + attribute + " by *" );
        assertNotNull( aclItem );

        // Testing the 'what' clause
        AclWhatClause whatClause = aclItem.getWhatClause();
        assertNotNull( whatClause );
        AclWhatClauseFilter whatClauseFilter = whatClause.getFilterClause();
        assertNotNull( whatClauseFilter );
        assertEquals( filter, whatClauseFilter.getFilter() );
        AclWhatClauseAttributes whatClauseAttributes = whatClause.getAttributesClause();
        assertNotNull( whatClauseAttributes );
        List<AclAttribute> attributesList = whatClauseAttributes.getAttributes();
        assertEquals( 1, attributesList.size() );
        assertEquals( attribute, attributesList.get( 0 ).getName() );
    }


    @Test
    public void testWhoStar() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by *" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );
    }


    @Test
    public void testWhoStarWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by *" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );
    }


    @Test
    public void testWhoStarWithSpaces() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "   access    to     *    by    *    " );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );
    }


    @Test
    public void testWhoAnonymous() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by anonymous" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseAnonymous );
    }


    @Test
    public void testWhoAnonymousWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by anonymous" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseAnonymous );
    }


    @Test
    public void testWhoAnonymousWithSpaces() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "   access    to     *    by    anonymous    " );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseAnonymous );
    }


    @Test
    public void testWhoUsers() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by users" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseUsers );
    }


    @Test
    public void testWhoUsersWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by users" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseUsers );
    }


    @Test
    public void testWhoUsersWithSpaces() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "   access    to     *    by    users    " );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseUsers );
    }


    @Test
    public void testWhoSelf() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by self" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseSelf );
    }


    @Test
    public void testWhoSelfWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by self" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseSelf );
    }


    @Test
    public void testWhoSelfWithSpaces() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "   access    to     *    by    self    " );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseSelf );
    }


    @Test
    public void testWhoDn() throws Exception
    {
        String dnPattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by dn=\"" + dnPattern + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseDn );
        assertEquals( dnPattern, ( ( AclWhoClauseDn ) whoClause ).getPattern() );
    }


    @Test
    public void testWhoDnWithoutAccess() throws Exception
    {
        String dnPattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by dn=\"" + dnPattern + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseDn );
        assertEquals( dnPattern, ( ( AclWhoClauseDn ) whoClause ).getPattern() );
    }


    @Test
    public void testWhoDnWithSpaces() throws Exception
    {
        String dnPattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "   access    to   * by   dn=\"" + dnPattern + "\"   " );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseDn );
        assertEquals( dnPattern, ( ( AclWhoClauseDn ) whoClause ).getPattern() );
    }


    @Test
    public void testWhoDnDefaultType() throws Exception
    {
        String dnPattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by dn=\"" + dnPattern + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseDn );
        AclWhoClauseDn whoClauseDn = ( AclWhoClauseDn ) whoClause;
        assertEquals( dnPattern, whoClauseDn.getPattern() );
        assertEquals( AclWhoClauseDnTypeEnum.BASE, whoClauseDn.getType() );
    }


    @Test
    public void testWhoDnDefaultTypeWithoutAccess() throws Exception
    {
        String dnPattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by dn=\"" + dnPattern + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseDn );
        AclWhoClauseDn whoClauseDn = ( AclWhoClauseDn ) whoClause;
        assertEquals( dnPattern, whoClauseDn.getPattern() );
        assertEquals( AclWhoClauseDnTypeEnum.BASE, whoClauseDn.getType() );
    }


    @Test
    public void testWhoDnRegex() throws Exception
    {
        String dnPattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by dn.regex=\"" + dnPattern + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseDn );
        AclWhoClauseDn whoClauseDn = ( AclWhoClauseDn ) whoClause;
        assertEquals( dnPattern, whoClauseDn.getPattern() );
        assertEquals( AclWhoClauseDnTypeEnum.REGEX, whoClauseDn.getType() );
    }


    @Test
    public void testWhoDnRegexWithoutAccess() throws Exception
    {
        String dnPattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by dn.regex=\"" + dnPattern + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseDn );
        AclWhoClauseDn whoClauseDn = ( AclWhoClauseDn ) whoClause;
        assertEquals( dnPattern, whoClauseDn.getPattern() );
        assertEquals( AclWhoClauseDnTypeEnum.REGEX, whoClauseDn.getType() );
    }


    @Test
    public void testWhoDnBase() throws Exception
    {
        String dnPattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by dn.base=\"" + dnPattern + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseDn );
        AclWhoClauseDn whoClauseDn = ( AclWhoClauseDn ) whoClause;
        assertEquals( dnPattern, whoClauseDn.getPattern() );
        assertEquals( AclWhoClauseDnTypeEnum.BASE, whoClauseDn.getType() );
    }


    @Test
    public void testWhoDnBaseWithoutAccess() throws Exception
    {
        String dnPattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by dn.base=\"" + dnPattern + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseDn );
        AclWhoClauseDn whoClauseDn = ( AclWhoClauseDn ) whoClause;
        assertEquals( dnPattern, whoClauseDn.getPattern() );
        assertEquals( AclWhoClauseDnTypeEnum.BASE, whoClauseDn.getType() );
    }


    @Test
    public void testWhoDnExact() throws Exception
    {
        String dnPattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by dn.exact=\"" + dnPattern + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseDn );
        AclWhoClauseDn whoClauseDn = ( AclWhoClauseDn ) whoClause;
        assertEquals( dnPattern, whoClauseDn.getPattern() );
        assertEquals( AclWhoClauseDnTypeEnum.EXACT, whoClauseDn.getType() );
    }


    @Test
    public void testWhoDnExactWithoutAccess() throws Exception
    {
        String dnPattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by dn.exact=\"" + dnPattern + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseDn );
        AclWhoClauseDn whoClauseDn = ( AclWhoClauseDn ) whoClause;
        assertEquals( dnPattern, whoClauseDn.getPattern() );
        assertEquals( AclWhoClauseDnTypeEnum.EXACT, whoClauseDn.getType() );
    }


    @Test
    public void testWhoDnOne() throws Exception
    {
        String dnPattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by dn.one=\"" + dnPattern + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseDn );
        AclWhoClauseDn whoClauseDn = ( AclWhoClauseDn ) whoClause;
        assertEquals( dnPattern, whoClauseDn.getPattern() );
        assertEquals( AclWhoClauseDnTypeEnum.ONE, whoClauseDn.getType() );
    }


    @Test
    public void testWhoDnOneWithoutAccess() throws Exception
    {
        String dnPattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by dn.one=\"" + dnPattern + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseDn );
        AclWhoClauseDn whoClauseDn = ( AclWhoClauseDn ) whoClause;
        assertEquals( dnPattern, whoClauseDn.getPattern() );
        assertEquals( AclWhoClauseDnTypeEnum.ONE, whoClauseDn.getType() );
    }


    @Test
    public void testWhoDnChildren() throws Exception
    {
        String dnPattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by dn.children=\"" + dnPattern + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseDn );
        AclWhoClauseDn whoClauseDn = ( AclWhoClauseDn ) whoClause;
        assertEquals( dnPattern, whoClauseDn.getPattern() );
        assertEquals( AclWhoClauseDnTypeEnum.CHILDREN, whoClauseDn.getType() );
    }


    @Test
    public void testWhoDnChildrenWithoutAccess() throws Exception
    {
        String dnPattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by dn.children=\"" + dnPattern + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseDn );
        AclWhoClauseDn whoClauseDn = ( AclWhoClauseDn ) whoClause;
        assertEquals( dnPattern, whoClauseDn.getPattern() );
        assertEquals( AclWhoClauseDnTypeEnum.CHILDREN, whoClauseDn.getType() );
    }


    @Test
    public void testWhoDnSubtree() throws Exception
    {
        String dnPattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by dn.subtree=\"" + dnPattern + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseDn );
        AclWhoClauseDn whoClauseDn = ( AclWhoClauseDn ) whoClause;
        assertEquals( dnPattern, whoClauseDn.getPattern() );
        assertEquals( AclWhoClauseDnTypeEnum.SUBTREE, whoClauseDn.getType() );
    }


    @Test
    public void testWhoDnSubtreeWithoutAccess() throws Exception
    {
        String dnPattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by dn.subtree=\"" + dnPattern + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseDn );
        AclWhoClauseDn whoClauseDn = ( AclWhoClauseDn ) whoClause;
        assertEquals( dnPattern, whoClauseDn.getPattern() );
        assertEquals( AclWhoClauseDnTypeEnum.SUBTREE, whoClauseDn.getType() );
    }


    @Test
    public void testWhoDnLevel() throws Exception
    {
        String dnPattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by dn.level{0}=\"" + dnPattern + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseDn );
        AclWhoClauseDn whoClauseDn = ( AclWhoClauseDn ) whoClause;
        assertEquals( dnPattern, whoClauseDn.getPattern() );
        AclWhoClauseDnTypeEnum whoClauseDnType = whoClauseDn.getType();
        assertEquals( AclWhoClauseDnTypeEnum.LEVEL, whoClauseDnType );
        assertEquals( 0, whoClauseDnType.getLevel() );
    }


    @Test
    public void testWhoDnLevelWithoutAccess() throws Exception
    {
        String dnPattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by dn.level{0}=\"" + dnPattern + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseDn );
        AclWhoClauseDn whoClauseDn = ( AclWhoClauseDn ) whoClause;
        assertEquals( dnPattern, whoClauseDn.getPattern() );
        AclWhoClauseDnTypeEnum whoClauseDnType = whoClauseDn.getType();
        assertEquals( AclWhoClauseDnTypeEnum.LEVEL, whoClauseDnType );
        assertEquals( 0, whoClauseDnType.getLevel() );
    }


    @Test
    public void testWhoDnLevelTwoDigits() throws Exception
    {
        String dnPattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by dn.level{12}=\"" + dnPattern + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseDn );
        AclWhoClauseDn whoClauseDn = ( AclWhoClauseDn ) whoClause;
        assertEquals( dnPattern, whoClauseDn.getPattern() );
        AclWhoClauseDnTypeEnum whoClauseDnType = whoClauseDn.getType();
        assertEquals( AclWhoClauseDnTypeEnum.LEVEL, whoClauseDnType );
        assertEquals( 12, whoClauseDnType.getLevel() );
    }


    @Test
    public void testWhoDnLevelTwoDigitsWithoutAccess() throws Exception
    {
        String dnPattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by dn.level{12}=\"" + dnPattern + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseDn );
        AclWhoClauseDn whoClauseDn = ( AclWhoClauseDn ) whoClause;
        assertEquals( dnPattern, whoClauseDn.getPattern() );
        AclWhoClauseDnTypeEnum whoClauseDnType = whoClauseDn.getType();
        assertEquals( AclWhoClauseDnTypeEnum.LEVEL, whoClauseDnType );
        assertEquals( 12, whoClauseDnType.getLevel() );
    }


    @Test
    public void testWhoDnDefaultModifier() throws Exception
    {
        String dnPattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by dn=\"" + dnPattern + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseDn );
        AclWhoClauseDn whoClauseDn = ( AclWhoClauseDn ) whoClause;
        assertEquals( dnPattern, whoClauseDn.getPattern() );
        assertNull( whoClauseDn.getModifier() );
    }


    @Test
    public void testWhoDnDefaultModifierWithoutAccess() throws Exception
    {
        String dnPattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by dn=\"" + dnPattern + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseDn );
        AclWhoClauseDn whoClauseDn = ( AclWhoClauseDn ) whoClause;
        assertEquals( dnPattern, whoClauseDn.getPattern() );
        assertNull( whoClauseDn.getModifier() );
    }


    @Test
    public void testWhoDnExpandModifier() throws Exception
    {
        String dnPattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by dn.exact,expand=\"" + dnPattern + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseDn );
        AclWhoClauseDn whoClauseDn = ( AclWhoClauseDn ) whoClause;
        assertEquals( dnPattern, whoClauseDn.getPattern() );
        assertEquals( AclWhoClauseDnModifierEnum.EXPAND, whoClauseDn.getModifier() );
    }


    @Test
    public void testWhoDnExpandModifierWithoutAccess() throws Exception
    {
        String dnPattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by dn.exact,expand=\"" + dnPattern + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseDn );
        AclWhoClauseDn whoClauseDn = ( AclWhoClauseDn ) whoClause;
        assertEquals( dnPattern, whoClauseDn.getPattern() );
        assertEquals( AclWhoClauseDnModifierEnum.EXPAND, whoClauseDn.getModifier() );
    }


    @Test
    public void testWhoDnExpandModifierWrong() throws Exception
    {
        try
        {
            // Create parser
            OpenLdapAclParser parser = new OpenLdapAclParser();
            parser.parse( "access to * by dn,expand=\"dc=example,dc=com\"" );

            fail();
        }
        catch ( Exception e )
        {
            // Should happen
        }
    }


    @Test
    public void testWhoDnAttr() throws Exception
    {
        String attribute = "dsqdsqdq";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by dnattr=" + attribute );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseDnAttr );
        AclWhoClauseDnAttr whoClauseDnAttr = ( AclWhoClauseDnAttr ) whoClause;
        assertEquals( attribute, whoClauseDnAttr.getAttribute() );
    }


    @Test
    public void testWhoDnAttrWithoutAccess() throws Exception
    {
        String attribute = "dsqdsqdq";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by dnattr=" + attribute );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseDnAttr );
        AclWhoClauseDnAttr whoClauseDnAttr = ( AclWhoClauseDnAttr ) whoClause;
        assertEquals( attribute, whoClauseDnAttr.getAttribute() );
    }


    @Test
    public void testWhoGroupPattern() throws Exception
    {
        String pattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by group=\"" + pattern + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseGroup );
        AclWhoClauseGroup whoClauseGroup = ( AclWhoClauseGroup ) whoClause;
        assertNull( whoClauseGroup.getObjectclass() );
        assertNull( whoClauseGroup.getAttribute() );
        assertEquals( pattern, whoClauseGroup.getPattern() );
    }


    @Test
    public void testWhoGroupPatternWithoutAccess() throws Exception
    {
        String pattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by group=\"" + pattern + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseGroup );
        AclWhoClauseGroup whoClauseGroup = ( AclWhoClauseGroup ) whoClause;
        assertNull( whoClauseGroup.getObjectclass() );
        assertNull( whoClauseGroup.getAttribute() );
        assertEquals( pattern, whoClauseGroup.getPattern() );
    }


    @Test
    public void testWhoGroupObjectclassPattern() throws Exception
    {
        String objectclass = "objectclass";
        String pattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by group/" + objectclass + "=\"" + pattern + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseGroup );
        AclWhoClauseGroup whoClauseGroup = ( AclWhoClauseGroup ) whoClause;
        assertEquals( objectclass, whoClauseGroup.getObjectclass() );
        assertNull( whoClauseGroup.getAttribute() );
        assertEquals( pattern, whoClauseGroup.getPattern() );
    }


    @Test
    public void testWhoGroupObjectclassPatternWithoutAccess() throws Exception
    {
        String objectclass = "objectclass";
        String pattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by group/" + objectclass + "=\"" + pattern + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseGroup );
        AclWhoClauseGroup whoClauseGroup = ( AclWhoClauseGroup ) whoClause;
        assertEquals( objectclass, whoClauseGroup.getObjectclass() );
        assertNull( whoClauseGroup.getAttribute() );
        assertEquals( pattern, whoClauseGroup.getPattern() );
    }


    @Test
    public void testWhoGroupObjectclassAttrnamePattern() throws Exception
    {
        String objectclass = "objectclass";
        String attrname = "attrname";
        String pattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by group/" + objectclass + "/" + attrname + "=\""
            + pattern
            + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseGroup );
        AclWhoClauseGroup whoClauseGroup = ( AclWhoClauseGroup ) whoClause;
        assertEquals( objectclass, whoClauseGroup.getObjectclass() );
        assertEquals( attrname, whoClauseGroup.getAttribute() );
        assertEquals( pattern, whoClauseGroup.getPattern() );
    }


    @Test
    public void testWhoGroupObjectclassAttrnamePatternWithoutAccess() throws Exception
    {
        String objectclass = "objectclass";
        String attrname = "attrname";
        String pattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by group/" + objectclass + "/" + attrname + "=\"" + pattern
            + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseGroup );
        AclWhoClauseGroup whoClauseGroup = ( AclWhoClauseGroup ) whoClause;
        assertEquals( objectclass, whoClauseGroup.getObjectclass() );
        assertEquals( attrname, whoClauseGroup.getAttribute() );
        assertEquals( pattern, whoClauseGroup.getPattern() );
    }


    @Test
    public void testWhoGroupExpandPattern() throws Exception
    {
        String type = "expand";
        String pattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by group" + "." + type + "=\"" + pattern + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseGroup );
        AclWhoClauseGroup whoClauseGroup = ( AclWhoClauseGroup ) whoClause;
        assertNull( whoClauseGroup.getObjectclass() );
        assertNull( whoClauseGroup.getAttribute() );
        assertEquals( pattern, whoClauseGroup.getPattern() );
        assertEquals( AclWhoClauseGroupTypeEnum.EXPAND, whoClauseGroup.getType() );
    }


    @Test
    public void testWhoGroupExpandPatternWithoutAccess() throws Exception
    {
        String type = "expand";
        String pattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by group" + "." + type + "=\"" + pattern + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseGroup );
        AclWhoClauseGroup whoClauseGroup = ( AclWhoClauseGroup ) whoClause;
        assertNull( whoClauseGroup.getObjectclass() );
        assertNull( whoClauseGroup.getAttribute() );
        assertEquals( pattern, whoClauseGroup.getPattern() );
        assertEquals( AclWhoClauseGroupTypeEnum.EXPAND, whoClauseGroup.getType() );
    }


    @Test
    public void testWhoGroupObjectclassExpandPattern() throws Exception
    {
        String objectclass = "objectclass";
        String type = "expand";
        String pattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by group/" + objectclass + "." + type + "=\"" + pattern
            + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseGroup );
        AclWhoClauseGroup whoClauseGroup = ( AclWhoClauseGroup ) whoClause;
        assertEquals( objectclass, whoClauseGroup.getObjectclass() );
        assertNull( whoClauseGroup.getAttribute() );
        assertEquals( pattern, whoClauseGroup.getPattern() );
        assertEquals( AclWhoClauseGroupTypeEnum.EXPAND, whoClauseGroup.getType() );
    }


    @Test
    public void testWhoGroupObjectclassExpandPatternWithoutAccess() throws Exception
    {
        String objectclass = "objectclass";
        String type = "expand";
        String pattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by group/" + objectclass + "." + type + "=\"" + pattern + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseGroup );
        AclWhoClauseGroup whoClauseGroup = ( AclWhoClauseGroup ) whoClause;
        assertEquals( objectclass, whoClauseGroup.getObjectclass() );
        assertNull( whoClauseGroup.getAttribute() );
        assertEquals( pattern, whoClauseGroup.getPattern() );
        assertEquals( AclWhoClauseGroupTypeEnum.EXPAND, whoClauseGroup.getType() );
    }


    @Test
    public void testWhoGroupObjectclassAttrnameExactPattern() throws Exception
    {
        String objectclass = "objectclass";
        String attrname = "attrname";
        String type = "exact";
        String pattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by group/" + objectclass + "/" + attrname + "." + type
            + "=\""
            + pattern + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseGroup );
        AclWhoClauseGroup whoClauseGroup = ( AclWhoClauseGroup ) whoClause;
        assertEquals( objectclass, whoClauseGroup.getObjectclass() );
        assertEquals( attrname, whoClauseGroup.getAttribute() );
        assertEquals( pattern, whoClauseGroup.getPattern() );
        assertEquals( AclWhoClauseGroupTypeEnum.EXACT, whoClauseGroup.getType() );
    }


    @Test
    public void testWhoGroupObjectclassAttrnameExactPatternWithoutAccess() throws Exception
    {
        String objectclass = "objectclass";
        String attrname = "attrname";
        String type = "exact";
        String pattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by group/" + objectclass + "/" + attrname + "." + type + "=\""
            + pattern + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseGroup );
        AclWhoClauseGroup whoClauseGroup = ( AclWhoClauseGroup ) whoClause;
        assertEquals( objectclass, whoClauseGroup.getObjectclass() );
        assertEquals( attrname, whoClauseGroup.getAttribute() );
        assertEquals( pattern, whoClauseGroup.getPattern() );
        assertEquals( AclWhoClauseGroupTypeEnum.EXACT, whoClauseGroup.getType() );
    }


    @Test
    public void testWhoGroupObjectclassAttrnameExpandPattern() throws Exception
    {
        String objectclass = "objectclass";
        String attrname = "attrname";
        String type = "expand";
        String pattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by group/" + objectclass + "/" + attrname + "." + type
            + "=\""
            + pattern + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseGroup );
        AclWhoClauseGroup whoClauseGroup = ( AclWhoClauseGroup ) whoClause;
        assertEquals( objectclass, whoClauseGroup.getObjectclass() );
        assertEquals( attrname, whoClauseGroup.getAttribute() );
        assertEquals( pattern, whoClauseGroup.getPattern() );
        assertEquals( AclWhoClauseGroupTypeEnum.EXPAND, whoClauseGroup.getType() );
    }


    @Test
    public void testWhoGroupObjectclassAttrnameExpandPatternWithoutAccess() throws Exception
    {
        String objectclass = "objectclass";
        String attrname = "attrname";
        String type = "expand";
        String pattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by group/" + objectclass + "/" + attrname + "." + type + "=\""
            + pattern + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseGroup );
        AclWhoClauseGroup whoClauseGroup = ( AclWhoClauseGroup ) whoClause;
        assertEquals( objectclass, whoClauseGroup.getObjectclass() );
        assertEquals( attrname, whoClauseGroup.getAttribute() );
        assertEquals( pattern, whoClauseGroup.getPattern() );
        assertEquals( AclWhoClauseGroupTypeEnum.EXPAND, whoClauseGroup.getType() );
    }


    @Test
    public void testWhoGroupObjectclassAttrnameNoTypePattern() throws Exception
    {
        String objectclass = "objectclass";
        String attrname = "attrname";
        String pattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by group/" + objectclass + "/" + attrname + "=\""
            + pattern
            + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseGroup );
        AclWhoClauseGroup whoClauseGroup = ( AclWhoClauseGroup ) whoClause;
        assertEquals( objectclass, whoClauseGroup.getObjectclass() );
        assertEquals( attrname, whoClauseGroup.getAttribute() );
        assertEquals( pattern, whoClauseGroup.getPattern() );
        assertNull( whoClauseGroup.getType() );
    }


    @Test
    public void testWhoGroupObjectclassAttrnameNoTypePatternWithoutAccess() throws Exception
    {
        String objectclass = "objectclass";
        String attrname = "attrname";
        String pattern = "dc=example,dc=com";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by group/" + objectclass + "/" + attrname + "=\"" + pattern
            + "\"" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseGroup );
        AclWhoClauseGroup whoClauseGroup = ( AclWhoClauseGroup ) whoClause;
        assertEquals( objectclass, whoClauseGroup.getObjectclass() );
        assertEquals( attrname, whoClauseGroup.getAttribute() );
        assertEquals( pattern, whoClauseGroup.getPattern() );
        assertNull( whoClauseGroup.getType() );
    }


    @Test
    public void testWhoAccessLevelManage() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by * manage" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertFalse( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.MANAGE, level );
    }


    @Test
    public void testWhoAccessLevelManageWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by * manage" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertFalse( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.MANAGE, level );
    }


    @Test
    public void testWhoAccessLevelWrite() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by * write" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertFalse( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.WRITE, level );
    }


    @Test
    public void testWhoAccessLevelWriteWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by * write" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertFalse( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.WRITE, level );
    }


    @Test
    public void testWhoAccessLevelRead() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by * read" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertFalse( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.READ, level );
    }


    @Test
    public void testWhoAccessLevelReadWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by * read" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertFalse( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.READ, level );
    }


    @Test
    public void testWhoAccessLevelSearch() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by * search" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertFalse( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.SEARCH, level );
    }


    @Test
    public void testWhoAccessLevelSearchWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by * search" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertFalse( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.SEARCH, level );
    }


    @Test
    public void testWhoAccessLevelCompare() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by * compare" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertFalse( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.COMPARE, level );
    }


    @Test
    public void testWhoAccessLevelCompareWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by * compare" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertFalse( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.COMPARE, level );
    }


    @Test
    public void testWhoAccessLevelAuth() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by * auth" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertFalse( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.AUTH, level );
    }


    @Test
    public void testWhoAccessLevelAuthWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by * auth" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertFalse( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.AUTH, level );
    }


    @Test
    public void testWhoAccessLevelDisclose() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by * disclose" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertFalse( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.DISCLOSE, level );
    }


    @Test
    public void testWhoAccessLevelDiscloseWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by * disclose" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertFalse( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.DISCLOSE, level );
    }


    @Test
    public void testWhoAccessLevelNone() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by * none" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertFalse( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.NONE, level );
    }


    @Test
    public void testWhoAccessLevelNoneWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by * none" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertFalse( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.NONE, level );
    }


    @Test
    public void testWhoAccessLevelSelfManage() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by * self manage" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.MANAGE, level );
    }


    @Test
    public void testWhoAccessLevelSelfManageWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by * self manage" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.MANAGE, level );
    }


    @Test
    public void testWhoAccessLevelSelfWrite() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by * self write" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.WRITE, level );
    }


    @Test
    public void testWhoAccessLevelSelfWriteWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by * self write" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.WRITE, level );
    }


    @Test
    public void testWhoAccessLevelSelfRead() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by * self read" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.READ, level );
    }


    @Test
    public void testWhoAccessLevelSelfReadWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by * self read" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.READ, level );
    }


    @Test
    public void testWhoAccessLevelSelfSearch() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by * self search" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.SEARCH, level );
    }


    @Test
    public void testWhoAccessLevelSelfSearchWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by * self search" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.SEARCH, level );
    }


    @Test
    public void testWhoAccessLevelSelfCompare() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by * self compare" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.COMPARE, level );
    }


    @Test
    public void testWhoAccessLevelSelfCompareWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by * self compare" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.COMPARE, level );
    }


    @Test
    public void testWhoAccessLevelSelfAuth() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by * self auth" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.AUTH, level );
    }


    @Test
    public void testWhoAccessLevelSelfAuthWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by * self auth" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.AUTH, level );
    }


    @Test
    public void testWhoAccessLevelSelfDisclose() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by * self disclose" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.DISCLOSE, level );
    }


    @Test
    public void testWhoAccessLevelSelfDiscloseWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by * self disclose" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.DISCLOSE, level );
    }


    @Test
    public void testWhoAccessLevelSelfNone() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by * self none" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.NONE, level );
    }


    @Test
    public void testWhoAccessLevelSelfNoneWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by * self none" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.NONE, level );
    }


    @Test
    public void testWhoAccessLevelPrivEqualM() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by * =m" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertFalse( accessLevel.isSelf() );
        assertNull( accessLevel.getLevel() );
        assertEquals( AclAccessLevelPrivModifierEnum.EQUAL, accessLevel.getPrivilegeModifier() );
        List<AclAccessLevelPrivilegeEnum> privileges = accessLevel.getPrivileges();
        assertEquals( 1, privileges.size() );
        assertEquals( AclAccessLevelPrivilegeEnum.MANAGE, privileges.get( 0 ) );
    }


    @Test
    public void testWhoAccessLevelPrivEqualMWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by * =m" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertFalse( accessLevel.isSelf() );
        assertNull( accessLevel.getLevel() );
        assertEquals( AclAccessLevelPrivModifierEnum.EQUAL, accessLevel.getPrivilegeModifier() );
        List<AclAccessLevelPrivilegeEnum> privileges = accessLevel.getPrivileges();
        assertEquals( 1, privileges.size() );
        assertEquals( AclAccessLevelPrivilegeEnum.MANAGE, privileges.get( 0 ) );
    }


    @Test
    public void testWhoAccessLevelPrivEqualW() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by * =w" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertFalse( accessLevel.isSelf() );
        assertNull( accessLevel.getLevel() );
        assertEquals( AclAccessLevelPrivModifierEnum.EQUAL, accessLevel.getPrivilegeModifier() );
        List<AclAccessLevelPrivilegeEnum> privileges = accessLevel.getPrivileges();
        assertEquals( 1, privileges.size() );
        assertEquals( AclAccessLevelPrivilegeEnum.WRITE, privileges.get( 0 ) );
    }


    @Test
    public void testWhoAccessLevelPrivEqualWWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by * =w" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertFalse( accessLevel.isSelf() );
        assertNull( accessLevel.getLevel() );
        assertEquals( AclAccessLevelPrivModifierEnum.EQUAL, accessLevel.getPrivilegeModifier() );
        List<AclAccessLevelPrivilegeEnum> privileges = accessLevel.getPrivileges();
        assertEquals( 1, privileges.size() );
        assertEquals( AclAccessLevelPrivilegeEnum.WRITE, privileges.get( 0 ) );
    }


    @Test
    public void testWhoAccessLevelPrivEqualR() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by * =r" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertFalse( accessLevel.isSelf() );
        assertNull( accessLevel.getLevel() );
        assertEquals( AclAccessLevelPrivModifierEnum.EQUAL, accessLevel.getPrivilegeModifier() );
        List<AclAccessLevelPrivilegeEnum> privileges = accessLevel.getPrivileges();
        assertEquals( 1, privileges.size() );
        assertEquals( AclAccessLevelPrivilegeEnum.READ, privileges.get( 0 ) );
    }


    @Test
    public void testWhoAccessLevelPrivEqualRWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by * =r" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertFalse( accessLevel.isSelf() );
        assertNull( accessLevel.getLevel() );
        assertEquals( AclAccessLevelPrivModifierEnum.EQUAL, accessLevel.getPrivilegeModifier() );
        List<AclAccessLevelPrivilegeEnum> privileges = accessLevel.getPrivileges();
        assertEquals( 1, privileges.size() );
        assertEquals( AclAccessLevelPrivilegeEnum.READ, privileges.get( 0 ) );
    }


    @Test
    public void testWhoAccessLevelPrivEqualS() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by * =s" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertFalse( accessLevel.isSelf() );
        assertNull( accessLevel.getLevel() );
        assertEquals( AclAccessLevelPrivModifierEnum.EQUAL, accessLevel.getPrivilegeModifier() );
        List<AclAccessLevelPrivilegeEnum> privileges = accessLevel.getPrivileges();
        assertEquals( 1, privileges.size() );
        assertEquals( AclAccessLevelPrivilegeEnum.SEARCH, privileges.get( 0 ) );
    }


    @Test
    public void testWhoAccessLevelPrivEqualSWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by * =s" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertFalse( accessLevel.isSelf() );
        assertNull( accessLevel.getLevel() );
        assertEquals( AclAccessLevelPrivModifierEnum.EQUAL, accessLevel.getPrivilegeModifier() );
        List<AclAccessLevelPrivilegeEnum> privileges = accessLevel.getPrivileges();
        assertEquals( 1, privileges.size() );
        assertEquals( AclAccessLevelPrivilegeEnum.SEARCH, privileges.get( 0 ) );
    }


    @Test
    public void testWhoAccessLevelPrivEqualC() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by * =c" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertFalse( accessLevel.isSelf() );
        assertNull( accessLevel.getLevel() );
        assertEquals( AclAccessLevelPrivModifierEnum.EQUAL, accessLevel.getPrivilegeModifier() );
        List<AclAccessLevelPrivilegeEnum> privileges = accessLevel.getPrivileges();
        assertEquals( 1, privileges.size() );
        assertEquals( AclAccessLevelPrivilegeEnum.COMPARE, privileges.get( 0 ) );
    }


    @Test
    public void testWhoAccessLevelPrivEqualCWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by * =c" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertFalse( accessLevel.isSelf() );
        assertNull( accessLevel.getLevel() );
        assertEquals( AclAccessLevelPrivModifierEnum.EQUAL, accessLevel.getPrivilegeModifier() );
        List<AclAccessLevelPrivilegeEnum> privileges = accessLevel.getPrivileges();
        assertEquals( 1, privileges.size() );
        assertEquals( AclAccessLevelPrivilegeEnum.COMPARE, privileges.get( 0 ) );
    }


    @Test
    public void testWhoAccessLevelPrivEqualX() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by * =x" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertFalse( accessLevel.isSelf() );
        assertNull( accessLevel.getLevel() );
        assertEquals( AclAccessLevelPrivModifierEnum.EQUAL, accessLevel.getPrivilegeModifier() );
        List<AclAccessLevelPrivilegeEnum> privileges = accessLevel.getPrivileges();
        assertEquals( 1, privileges.size() );
        assertEquals( AclAccessLevelPrivilegeEnum.AUTHENTICATION, privileges.get( 0 ) );
    }


    @Test
    public void testWhoAccessLevelPrivEqualXWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by * =x" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertFalse( accessLevel.isSelf() );
        assertNull( accessLevel.getLevel() );
        assertEquals( AclAccessLevelPrivModifierEnum.EQUAL, accessLevel.getPrivilegeModifier() );
        List<AclAccessLevelPrivilegeEnum> privileges = accessLevel.getPrivileges();
        assertEquals( 1, privileges.size() );
        assertEquals( AclAccessLevelPrivilegeEnum.AUTHENTICATION, privileges.get( 0 ) );
    }


    @Test
    public void testWhoAccessLevelSelfPrivEqualM() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by * self =m" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        assertNull( accessLevel.getLevel() );
        assertEquals( AclAccessLevelPrivModifierEnum.EQUAL, accessLevel.getPrivilegeModifier() );
        List<AclAccessLevelPrivilegeEnum> privileges = accessLevel.getPrivileges();
        assertEquals( 1, privileges.size() );
        assertEquals( AclAccessLevelPrivilegeEnum.MANAGE, privileges.get( 0 ) );
    }


    @Test
    public void testWhoAccessLevelSelfPrivEqualMWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by * self =m" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        assertNull( accessLevel.getLevel() );
        assertEquals( AclAccessLevelPrivModifierEnum.EQUAL, accessLevel.getPrivilegeModifier() );
        List<AclAccessLevelPrivilegeEnum> privileges = accessLevel.getPrivileges();
        assertEquals( 1, privileges.size() );
        assertEquals( AclAccessLevelPrivilegeEnum.MANAGE, privileges.get( 0 ) );
    }


    @Test
    public void testWhoAccessLevelSelfPrivEqualW() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by * self =w" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        assertNull( accessLevel.getLevel() );
        assertEquals( AclAccessLevelPrivModifierEnum.EQUAL, accessLevel.getPrivilegeModifier() );
        List<AclAccessLevelPrivilegeEnum> privileges = accessLevel.getPrivileges();
        assertEquals( 1, privileges.size() );
        assertEquals( AclAccessLevelPrivilegeEnum.WRITE, privileges.get( 0 ) );
    }


    @Test
    public void testWhoAccessLevelSelfPrivEqualWWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by * self =w" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        assertNull( accessLevel.getLevel() );
        assertEquals( AclAccessLevelPrivModifierEnum.EQUAL, accessLevel.getPrivilegeModifier() );
        List<AclAccessLevelPrivilegeEnum> privileges = accessLevel.getPrivileges();
        assertEquals( 1, privileges.size() );
        assertEquals( AclAccessLevelPrivilegeEnum.WRITE, privileges.get( 0 ) );
    }


    @Test
    public void testWhoAccessLevelSelfPrivEqualR() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by * self =r" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        assertNull( accessLevel.getLevel() );
        assertEquals( AclAccessLevelPrivModifierEnum.EQUAL, accessLevel.getPrivilegeModifier() );
        List<AclAccessLevelPrivilegeEnum> privileges = accessLevel.getPrivileges();
        assertEquals( 1, privileges.size() );
        assertEquals( AclAccessLevelPrivilegeEnum.READ, privileges.get( 0 ) );
    }


    @Test
    public void testWhoAccessLevelSelfPrivEqualRWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by * self =r" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        assertNull( accessLevel.getLevel() );
        assertEquals( AclAccessLevelPrivModifierEnum.EQUAL, accessLevel.getPrivilegeModifier() );
        List<AclAccessLevelPrivilegeEnum> privileges = accessLevel.getPrivileges();
        assertEquals( 1, privileges.size() );
        assertEquals( AclAccessLevelPrivilegeEnum.READ, privileges.get( 0 ) );
    }


    @Test
    public void testWhoAccessLevelSelfPrivEqualS() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by * self =s" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        assertNull( accessLevel.getLevel() );
        assertEquals( AclAccessLevelPrivModifierEnum.EQUAL, accessLevel.getPrivilegeModifier() );
        List<AclAccessLevelPrivilegeEnum> privileges = accessLevel.getPrivileges();
        assertEquals( 1, privileges.size() );
        assertEquals( AclAccessLevelPrivilegeEnum.SEARCH, privileges.get( 0 ) );
    }


    @Test
    public void testWhoAccessLevelSelfPrivEqualSWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by * self =s" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        assertNull( accessLevel.getLevel() );
        assertEquals( AclAccessLevelPrivModifierEnum.EQUAL, accessLevel.getPrivilegeModifier() );
        List<AclAccessLevelPrivilegeEnum> privileges = accessLevel.getPrivileges();
        assertEquals( 1, privileges.size() );
        assertEquals( AclAccessLevelPrivilegeEnum.SEARCH, privileges.get( 0 ) );
    }


    @Test
    public void testWhoAccessLevelSelfPrivEqualC() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by * self =c" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        assertNull( accessLevel.getLevel() );
        assertEquals( AclAccessLevelPrivModifierEnum.EQUAL, accessLevel.getPrivilegeModifier() );
        List<AclAccessLevelPrivilegeEnum> privileges = accessLevel.getPrivileges();
        assertEquals( 1, privileges.size() );
        assertEquals( AclAccessLevelPrivilegeEnum.COMPARE, privileges.get( 0 ) );
    }


    @Test
    public void testWhoAccessLevelSelfPrivEqualCWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by * self =c" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        assertNull( accessLevel.getLevel() );
        assertEquals( AclAccessLevelPrivModifierEnum.EQUAL, accessLevel.getPrivilegeModifier() );
        List<AclAccessLevelPrivilegeEnum> privileges = accessLevel.getPrivileges();
        assertEquals( 1, privileges.size() );
        assertEquals( AclAccessLevelPrivilegeEnum.COMPARE, privileges.get( 0 ) );
    }


    @Test
    public void testWhoAccessLevelSelfPrivEqualX() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by * self =x" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        assertNull( accessLevel.getLevel() );
        assertEquals( AclAccessLevelPrivModifierEnum.EQUAL, accessLevel.getPrivilegeModifier() );
        List<AclAccessLevelPrivilegeEnum> privileges = accessLevel.getPrivileges();
        assertEquals( 1, privileges.size() );
        assertEquals( AclAccessLevelPrivilegeEnum.AUTHENTICATION, privileges.get( 0 ) );
    }


    @Test
    public void testWhoAccessLevelSelfPrivEqualXWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by * self =x" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        assertNull( accessLevel.getLevel() );
        assertEquals( AclAccessLevelPrivModifierEnum.EQUAL, accessLevel.getPrivilegeModifier() );
        List<AclAccessLevelPrivilegeEnum> privileges = accessLevel.getPrivileges();
        assertEquals( 1, privileges.size() );
        assertEquals( AclAccessLevelPrivilegeEnum.AUTHENTICATION, privileges.get( 0 ) );
    }


    @Test
    public void testWhoAccessLevelSelfPrivPlusX() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by * self +x" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        assertNull( accessLevel.getLevel() );
        assertEquals( AclAccessLevelPrivModifierEnum.PLUS, accessLevel.getPrivilegeModifier() );
        List<AclAccessLevelPrivilegeEnum> privileges = accessLevel.getPrivileges();
        assertEquals( 1, privileges.size() );
        assertEquals( AclAccessLevelPrivilegeEnum.AUTHENTICATION, privileges.get( 0 ) );
    }


    @Test
    public void testWhoAccessLevelSelfPrivPlusXWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by * self +x" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        assertNull( accessLevel.getLevel() );
        assertEquals( AclAccessLevelPrivModifierEnum.PLUS, accessLevel.getPrivilegeModifier() );
        List<AclAccessLevelPrivilegeEnum> privileges = accessLevel.getPrivileges();
        assertEquals( 1, privileges.size() );
        assertEquals( AclAccessLevelPrivilegeEnum.AUTHENTICATION, privileges.get( 0 ) );
    }


    @Test
    public void testWhoAccessLevelSelfPrivMinusX() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by * self -x" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        assertNull( accessLevel.getLevel() );
        assertEquals( AclAccessLevelPrivModifierEnum.MINUS, accessLevel.getPrivilegeModifier() );
        List<AclAccessLevelPrivilegeEnum> privileges = accessLevel.getPrivileges();
        assertEquals( 1, privileges.size() );
        assertEquals( AclAccessLevelPrivilegeEnum.AUTHENTICATION, privileges.get( 0 ) );
    }


    @Test
    public void testWhoAccessLevelSelfPrivMinusXWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by * self -x" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        assertNull( accessLevel.getLevel() );
        assertEquals( AclAccessLevelPrivModifierEnum.MINUS, accessLevel.getPrivilegeModifier() );
        List<AclAccessLevelPrivilegeEnum> privileges = accessLevel.getPrivileges();
        assertEquals( 1, privileges.size() );
        assertEquals( AclAccessLevelPrivilegeEnum.AUTHENTICATION, privileges.get( 0 ) );
    }


    @Test
    @Ignore
    public void testWhoAccessLevelSelfPrivMinusMWRSCX() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by * self -mwrscx" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        assertNull( accessLevel.getLevel() );
        assertEquals( AclAccessLevelPrivModifierEnum.MINUS, accessLevel.getPrivilegeModifier() );
        List<AclAccessLevelPrivilegeEnum> privileges = accessLevel.getPrivileges();
        assertEquals( 6, privileges.size() );
        assertEquals( AclAccessLevelPrivilegeEnum.MANAGE, privileges.get( 0 ) );
        assertEquals( AclAccessLevelPrivilegeEnum.WRITE, privileges.get( 1 ) );
        assertEquals( AclAccessLevelPrivilegeEnum.READ, privileges.get( 2 ) );
        assertEquals( AclAccessLevelPrivilegeEnum.SEARCH, privileges.get( 3 ) );
        assertEquals( AclAccessLevelPrivilegeEnum.COMPARE, privileges.get( 4 ) );
        assertEquals( AclAccessLevelPrivilegeEnum.AUTHENTICATION, privileges.get( 5 ) );
    }


    @Test
    public void testWhoNoControl() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by *" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );
        assertNull( whoClause.getControl() );
    }


    @Test
    public void testWhoNoControlWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by *" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );
        assertNull( whoClause.getControl() );
    }


    @Test
    public void testWhoControlStop() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by * stop" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the control
        AclControlEnum control = whoClause.getControl();
        assertNotNull( control );
        assertEquals( AclControlEnum.STOP, control );
    }


    @Test
    public void testWhoControlStopWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by * stop" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the control
        AclControlEnum control = whoClause.getControl();
        assertNotNull( control );
        assertEquals( AclControlEnum.STOP, control );
    }


    @Test
    public void testWhoControlContinue() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by * continue" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the control
        AclControlEnum control = whoClause.getControl();
        assertNotNull( control );
        assertEquals( AclControlEnum.CONTINUE, control );
    }


    @Test
    public void testWhoControlContinueWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by * continue" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the control
        AclControlEnum control = whoClause.getControl();
        assertNotNull( control );
        assertEquals( AclControlEnum.CONTINUE, control );
    }


    @Test
    public void testWhoControlBreak() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by * break" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );
        AclControlEnum control = whoClause.getControl();

        // Testing the control
        assertNotNull( control );
        assertEquals( AclControlEnum.BREAK, control );
    }


    @Test
    public void testWhoControlBreakWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by * break" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );
        AclControlEnum control = whoClause.getControl();

        // Testing the control
        assertNotNull( control );
        assertEquals( AclControlEnum.BREAK, control );
    }


    @Test
    public void testWhoAccessLevelSelfNoneStop() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by * self none stop" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.NONE, level );

        // Testing the control
        AclControlEnum control = whoClause.getControl();
        assertNotNull( control );
        assertEquals( AclControlEnum.STOP, control );
    }


    @Test
    public void testWhoAccessLevelSelfNoneStopWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by * self none stop" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.NONE, level );

        // Testing the control
        AclControlEnum control = whoClause.getControl();
        assertNotNull( control );
        assertEquals( AclControlEnum.STOP, control );
    }


    @Test
    public void testWhoAccessLevelSelfNoneContinue() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by * self none continue" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.NONE, level );

        // Testing the control
        AclControlEnum control = whoClause.getControl();
        assertNotNull( control );
        assertEquals( AclControlEnum.CONTINUE, control );
    }


    @Test
    public void testWhoAccessLevelSelfNoneContinueWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by * self none continue" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.NONE, level );

        // Testing the control
        AclControlEnum control = whoClause.getControl();
        assertNotNull( control );
        assertEquals( AclControlEnum.CONTINUE, control );
    }


    @Test
    public void testWhoAccessLevelSelfNoneBreak() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by * self none break" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.NONE, level );

        // Testing the control
        AclControlEnum control = whoClause.getControl();
        assertNotNull( control );
        assertEquals( AclControlEnum.BREAK, control );
    }


    @Test
    public void testWhoAccessLevelSelfNoneBreakWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by * self none break" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseStar );

        // Testing the access level
        AclAccessLevel accessLevel = whoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertTrue( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.NONE, level );

        // Testing the control
        AclControlEnum control = whoClause.getControl();
        assertNotNull( control );
        assertEquals( AclControlEnum.BREAK, control );
    }


    @Test
    public void testWhoSsf() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by ssf=128" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseSsf );
        AclWhoClauseSsf whoClauseSsf = ( AclWhoClauseSsf ) whoClause;
        assertEquals( 128, whoClauseSsf.getStrength() );
    }


    @Test
    public void testWhoSsfWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by ssf=128" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseSsf );
        AclWhoClauseSsf whoClauseSsf = ( AclWhoClauseSsf ) whoClause;
        assertEquals( 128, whoClauseSsf.getStrength() );
    }


    @Test
    public void testWhoTransportSsf() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by transport_ssf=128" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseTransportSsf );
        AclWhoClauseTransportSsf whoClauseTransportSsf = ( AclWhoClauseTransportSsf ) whoClause;
        assertEquals( 128, whoClauseTransportSsf.getStrength() );
    }


    @Test
    public void testWhoTransportSsfWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by transport_ssf=128" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseTransportSsf );
        AclWhoClauseTransportSsf whoClauseTransportSsf = ( AclWhoClauseTransportSsf ) whoClause;
        assertEquals( 128, whoClauseTransportSsf.getStrength() );
    }


    @Test
    public void testWhoTlsSsf() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by tls_ssf=128" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseTlsSsf );
        AclWhoClauseTlsSsf whoClauseTlsSsf = ( AclWhoClauseTlsSsf ) whoClause;
        assertEquals( 128, whoClauseTlsSsf.getStrength() );
    }


    @Test
    public void testWhoTlsSsfWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by tls_ssf=128" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseTlsSsf );
        AclWhoClauseTlsSsf whoClauseTlsSsf = ( AclWhoClauseTlsSsf ) whoClause;
        assertEquals( 128, whoClauseTlsSsf.getStrength() );
    }


    @Test
    public void testWhoSaslSsf() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by sasl_ssf=128" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseSaslSsf );
        AclWhoClauseSaslSsf whoClauseSaslSsf = ( AclWhoClauseSaslSsf ) whoClause;
        assertEquals( 128, whoClauseSaslSsf.getStrength() );
    }


    @Test
    public void testWhoSaslSsfWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by sasl_ssf=128" );
        assertNotNull( aclItem );

        // Testing the 'who' clause
        AclWhoClause whoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( whoClause );
        assertTrue( whoClause instanceof AclWhoClauseSaslSsf );
        AclWhoClauseSaslSsf whoClauseSaslSsf = ( AclWhoClauseSaslSsf ) whoClause;
        assertEquals( 128, whoClauseSaslSsf.getStrength() );
    }


    @Test
    public void testWhoTwoWhoClauses() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "access to * by sasl_ssf=128 by * read" );
        assertNotNull( aclItem );
        assertEquals( 2, aclItem.getWhoClauses().size() );

        // Testing the first 'who' clause
        AclWhoClause firstWhoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( firstWhoClause );
        assertTrue( firstWhoClause instanceof AclWhoClauseSaslSsf );
        AclWhoClauseSaslSsf whoClauseSaslSsf = ( AclWhoClauseSaslSsf ) firstWhoClause;
        assertEquals( 128, whoClauseSaslSsf.getStrength() );

        // Testing the second 'who' clause
        AclWhoClause secondWhoClause = aclItem.getWhoClauses().get( 1 );
        assertNotNull( secondWhoClause );
        assertTrue( secondWhoClause instanceof AclWhoClauseStar );
        AclAccessLevel accessLevel = secondWhoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertFalse( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.READ, level );
    }


    @Test
    public void testWhoTwoWhoClausesWithoutAccess() throws Exception
    {
        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( "to * by sasl_ssf=128 by * read" );
        assertNotNull( aclItem );
        assertEquals( 2, aclItem.getWhoClauses().size() );

        // Testing the first 'who' clause
        AclWhoClause firstWhoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( firstWhoClause );
        assertTrue( firstWhoClause instanceof AclWhoClauseSaslSsf );
        AclWhoClauseSaslSsf whoClauseSaslSsf = ( AclWhoClauseSaslSsf ) firstWhoClause;
        assertEquals( 128, whoClauseSaslSsf.getStrength() );

        // Testing the second 'who' clause
        AclWhoClause secondWhoClause = aclItem.getWhoClauses().get( 1 );
        assertNotNull( secondWhoClause );
        assertTrue( secondWhoClause instanceof AclWhoClauseStar );
        AclAccessLevel accessLevel = secondWhoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertFalse( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.READ, level );
    }


    @Test
    public void testWhoThreeWhoClausesWithNewLines() throws Exception
    {
        String dn = "dc=example,dc=com";

        String string = "access to *" + "\n"
            + "by sasl_ssf=128" + "\n"
            + "by * read" + "\n"
            + "by dn=\"" + dn + "\"";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( string );
        assertNotNull( aclItem );
        assertEquals( 3, aclItem.getWhoClauses().size() );

        // Testing the first 'who' clause
        AclWhoClause firstWhoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( firstWhoClause );
        assertTrue( firstWhoClause instanceof AclWhoClauseSaslSsf );
        AclWhoClauseSaslSsf whoClauseSaslSsf = ( AclWhoClauseSaslSsf ) firstWhoClause;
        assertEquals( 128, whoClauseSaslSsf.getStrength() );

        // Testing the second 'who' clause
        AclWhoClause secondWhoClause = aclItem.getWhoClauses().get( 1 );
        assertNotNull( secondWhoClause );
        assertTrue( secondWhoClause instanceof AclWhoClauseStar );
        AclAccessLevel accessLevel = secondWhoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertFalse( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.READ, level );

        // Testing the thrid 'who' clause
        AclWhoClause thirdWhoClause = aclItem.getWhoClauses().get( 2 );
        assertNotNull( thirdWhoClause );
        assertTrue( thirdWhoClause instanceof AclWhoClauseDn );
        AclWhoClauseDn whoClauseDn = ( AclWhoClauseDn ) thirdWhoClause;
        assertEquals( dn, whoClauseDn.getPattern() );
    }


    @Test
    public void testWhoThreeWhoClausesWithNewLinesWithoutAccess() throws Exception
    {
        String dn = "dc=example,dc=com";

        String string = "to *" + "\n"
            + "by sasl_ssf=128" + "\n"
            + "by * read" + "\n"
            + "by dn=\"" + dn + "\"";

        // Create parser
        OpenLdapAclParser parser = new OpenLdapAclParser();

        // Testing the ACL item
        AclItem aclItem = parser.parse( string );
        assertNotNull( aclItem );
        assertEquals( 3, aclItem.getWhoClauses().size() );

        // Testing the first 'who' clause
        AclWhoClause firstWhoClause = aclItem.getWhoClauses().get( 0 );
        assertNotNull( firstWhoClause );
        assertTrue( firstWhoClause instanceof AclWhoClauseSaslSsf );
        AclWhoClauseSaslSsf whoClauseSaslSsf = ( AclWhoClauseSaslSsf ) firstWhoClause;
        assertEquals( 128, whoClauseSaslSsf.getStrength() );

        // Testing the second 'who' clause
        AclWhoClause secondWhoClause = aclItem.getWhoClauses().get( 1 );
        assertNotNull( secondWhoClause );
        assertTrue( secondWhoClause instanceof AclWhoClauseStar );
        AclAccessLevel accessLevel = secondWhoClause.getAccessLevel();
        assertNotNull( accessLevel );
        assertFalse( accessLevel.isSelf() );
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        assertNotNull( level );
        assertEquals( AclAccessLevelLevelEnum.READ, level );

        // Testing the thrid 'who' clause
        AclWhoClause thirdWhoClause = aclItem.getWhoClauses().get( 2 );
        assertNotNull( thirdWhoClause );
        assertTrue( thirdWhoClause instanceof AclWhoClauseDn );
        AclWhoClauseDn whoClauseDn = ( AclWhoClauseDn ) thirdWhoClause;
        assertEquals( dn, whoClauseDn.getPattern() );
    }


    @Test
    /**
     * Tests examples given in the following page:
     * http://www.openldap.org/doc/admin24/access-control.html
     *
    public void testVerifyOpenLdapDocumentExamples() throws Exception
    {
        // List of string holding all examples
        List<String> examples = new ArrayList<String>();

        examples.add( "access to * by * read" );

        examples.add( "access to *\n" +
            "by self write\n" +
            "by anonymous auth\n" +
            "by * read" );

        // TODO: this one needs to be investigated
        //examples.add( "access to *\n" +
        //    "by ssf=128 self write\n" +
        //    "by ssf=64 anonymous auth\n" +
        //    "by ssf=64 users read" );

        examples.add( "access to dn.children=\"dc=example,dc=com\"\n" +
            "by * search" );

        examples.add( "access to dn.children=\"dc=com\"\n" +
            "by * read" );

        // TODO: this one needs to be investigated
        //examples.add( "access to dn.subtree=\"dc=example,dc=com\" attrs=homePhone\n" +
        //"by self write\n" +
        //"by dn.children=\"dc=example,dc=com\" search\n" +
        //"by peername.regex=IP:10\\..+ read");

        examples.add( "access to dn.subtree=\"dc=example,dc=com\"\n" +
            "by self write\n" +
            "by dn.children=\"dc=example,dc=com\" search\n" +
            "by anonymous auth" );

        // TODO: this one needs to be investigated
        //examples.add( "access to attrs=member,entry\n" +
        //    "by dnattr=member self write" );

        for ( String example : examples )
        {
            OpenLdapAclParser parser = new OpenLdapAclParser();
            parser.parse( example );
        }
    }


    @Test
    /**
     * Tests examples given in the following page:
     * http://www.openldap.org/doc/admin24/access-control.html
     *
    public void testVerifyOpenLdapDocumentExamplesWithoutAccess() throws Exception
    {
        // List of string holding all examples
        List<String> examples = new ArrayList<String>();

        examples.add( "to * by * read" );

        examples.add( "to *\n" +
            "by self write\n" +
            "by anonymous auth\n" +
            "by * read" );

        // TODO: this one needs to be investigated
        //examples.add( "to *\n" +
        //    "by ssf=128 self write\n" +
        //    "by ssf=64 anonymous auth\n" +
        //    "by ssf=64 users read" );

        examples.add( "to dn.children=\"dc=example,dc=com\"\n" +
            "by * search" );

        examples.add( "to dn.children=\"dc=com\"\n" +
            "by * read" );

        // TODO: this one needs to be investigated
        //examples.add( "to dn.subtree=\"dc=example,dc=com\" attrs=homePhone\n" +
        //"by self write\n" +
        //"by dn.children=\"dc=example,dc=com\" search\n" +
        //"by peername.regex=IP:10\\..+ read");

        examples.add( "to dn.subtree=\"dc=example,dc=com\"\n" +
            "by self write\n" +
            "by dn.children=\"dc=example,dc=com\" search\n" +
            "by anonymous auth" );

        // TODO: this one needs to be investigated
        //examples.add( "to attrs=member,entry\n" +
        //    "by dnattr=member self write" );

        for ( String example : examples )
        {
            OpenLdapAclParser parser = new OpenLdapAclParser();
            parser.parse( example );
        }
    }
    */
}
