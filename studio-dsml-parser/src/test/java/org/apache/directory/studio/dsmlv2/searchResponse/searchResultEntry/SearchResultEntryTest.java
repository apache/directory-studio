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

package org.apache.directory.studio.dsmlv2.searchResponse.searchResultEntry;


import java.io.UnsupportedEncodingException;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.apache.directory.shared.ldap.codec.Control;
import org.apache.directory.shared.ldap.codec.search.SearchResultEntry;
import org.apache.directory.shared.ldap.util.StringTools;
import org.apache.directory.studio.dsmlv2.AbstractResponseTest;
import org.apache.directory.studio.dsmlv2.Dsmlv2ResponseParser;
import org.apache.directory.studio.dsmlv2.reponse.SearchResponse;


/**
 * Tests for the Search Result Entry Response parsing
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SearchResultEntryTest extends AbstractResponseTest
{
    /**
     * Test parsing of a response with a (optional) Control element
     */
    public void testResponseWith1Control()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser();

            parser.setInputFile( SearchResultEntryTest.class.getResource( "response_with_1_control.xml" ).getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        SearchResultEntry searchResultEntry = ( ( SearchResponse ) parser.getBatchResponse().getCurrentResponse() )
            .getCurrentSearchResultEntry();

        assertEquals( 1, searchResultEntry.getControls().size() );

        Control control = searchResultEntry.getCurrentControl();

        assertTrue( control.getCriticality() );

        assertEquals( "1.2.840.113556.1.4.643", control.getControlType() );

        assertEquals( "Some text", StringTools.utf8ToString( ( byte[] ) control.getControlValue() ) );
    }


    /**
     * Test parsing of a response with a (optional) Control element with empty value
     */
    public void testResponseWith1ControlEmptyValue()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser();

            parser.setInputFile( SearchResultEntryTest.class.getResource( "response_with_1_control_empty_value.xml" )
                .getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        SearchResultEntry searchResultEntry = ( ( SearchResponse ) parser.getBatchResponse().getCurrentResponse() )
            .getCurrentSearchResultEntry();
        Control control = searchResultEntry.getCurrentControl();

        assertEquals( 1, searchResultEntry.getControls().size() );
        assertTrue( control.getCriticality() );
        assertEquals( "1.2.840.113556.1.4.643", control.getControlType() );
        assertEquals( StringTools.EMPTY_BYTES, ( byte[] ) control.getControlValue() );
    }


    /**
     * Test parsing of a response with 2 (optional) Control elements
     */
    public void testResponseWith2Controls()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser();

            parser.setInputFile( SearchResultEntryTest.class.getResource( "response_with_2_controls.xml" ).getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        SearchResultEntry searchResultEntry = ( ( SearchResponse ) parser.getBatchResponse().getCurrentResponse() )
            .getCurrentSearchResultEntry();

        assertEquals( 2, searchResultEntry.getControls().size() );

        Control control = searchResultEntry.getCurrentControl();

        assertFalse( control.getCriticality() );

        assertEquals( "1.2.840.113556.1.4.789", control.getControlType() );

        assertEquals( "Some other text", StringTools.utf8ToString( ( byte[] ) control.getControlValue() ) );
    }


    /**
     * Test parsing of a response with 3 (optional) Control elements without value
     */
    public void testResponseWith3ControlsWithoutValue()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser();

            parser.setInputFile( SearchResultEntryTest.class.getResource( "response_with_3_controls_without_value.xml" )
                .getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        SearchResultEntry searchResultEntry = ( ( SearchResponse ) parser.getBatchResponse().getCurrentResponse() )
            .getCurrentSearchResultEntry();

        assertEquals( 3, searchResultEntry.getControls().size() );

        Control control = searchResultEntry.getCurrentControl();

        assertTrue( control.getCriticality() );

        assertEquals( "1.2.840.113556.1.4.456", control.getControlType() );

        assertEquals( StringTools.EMPTY_BYTES, control.getControlValue() );
    }


    /**
     * Test parsing of a response without dn Attribute
     */
    public void testResponseWithoutDnAttribute()
    {
        testParsingFail( SearchResultEntryTest.class, "response_without_dn_attribute.xml" );
    }


    /**
     * Test parsing of a response with wrong dn Attribute
     */
    public void testResponseWithWrongDnAttribute()
    {
        testParsingFail( SearchResultEntryTest.class, "response_with_wrong_dn_attribute.xml" );
    }


    /**
     * Test parsing of a response with dn Attribute
     */
    public void testResponseWithDnAttribute()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser();

            parser.setInputFile( SearchResultEntryTest.class.getResource( "response_with_dn_attribute.xml" ).getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        SearchResultEntry searchResultEntry = ( ( SearchResponse ) parser.getBatchResponse().getCurrentResponse() )
            .getCurrentSearchResultEntry();

        assertEquals( "dc=example,dc=com", searchResultEntry.getObjectName().toString() );
    }


    /**
     * Test parsing of a Response with the (optional) requestID attribute
     */
    public void testResponseWithRequestId()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser();

            parser.setInputFile( SearchResultEntryTest.class.getResource( "response_with_requestID_attribute.xml" )
                .getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        SearchResultEntry searchResultEntry = ( ( SearchResponse ) parser.getBatchResponse().getCurrentResponse() )
            .getCurrentSearchResultEntry();

        assertEquals( 456, searchResultEntry.getMessageId() );
    }


    /**
     * Test parsing of a Response with the (optional) requestID attribute equals 0
     */
    public void testResponseWithRequestIdEquals0()
    {
        testParsingFail( SearchResultEntryTest.class, "response_with_requestID_equals_0.xml" );
    }


    /**
     * Test parsing of a response with 0 Attr
     */
    public void testResponseWith0Attr()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser();

            parser.setInputFile( SearchResultEntryTest.class.getResource( "response_with_0_attr.xml" ).getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        assertTrue( true );
    }


    /**
     * Test parsing of a response with 1 Attr 0 Value
     */
    public void testResponseWith1Attr0Value()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser();

            parser.setInputFile( SearchResultEntryTest.class.getResource( "response_with_1_attr_0_value.xml" )
                .getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        SearchResultEntry searchResultEntry = ( ( SearchResponse ) parser.getBatchResponse().getCurrentResponse() )
            .getCurrentSearchResultEntry();

        Attributes attributes = searchResultEntry.getPartialAttributeList();

        assertEquals( 1, attributes.size() );

        NamingEnumeration ne = attributes.getAll();

        Attribute attribute = ( Attribute ) ne.nextElement();

        assertEquals( "dc", attribute.getID() );
    }


    /**
     * Test parsing of a response with 1 Attr 1 Value
     */
    public void testResponseWith1Attr1Value()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser();

            parser.setInputFile( SearchResultEntryTest.class.getResource( "response_with_1_attr_1_value.xml" )
                .getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        SearchResultEntry searchResultEntry = ( ( SearchResponse ) parser.getBatchResponse().getCurrentResponse() )
            .getCurrentSearchResultEntry();

        Attributes attributes = searchResultEntry.getPartialAttributeList();

        assertEquals( 1, attributes.size() );

        NamingEnumeration ne = attributes.getAll();

        Attribute attribute = ( Attribute ) ne.nextElement();

        assertEquals( "dc", attribute.getID() );

        assertEquals( 1, attribute.size() );

        NamingEnumeration ne2 = null;
        try
        {
            ne2 = attribute.getAll();
        }
        catch ( NamingException e )
        {
            fail();
        }

        String value = ( String ) ne2.nextElement();

        assertEquals( "example", value );
    }


    /**
     * Test parsing of a response with 1 Attr 1 Base64 Value
     * @throws UnsupportedEncodingException 
     */
    public void testResponseWith1Attr1Base64Value() throws UnsupportedEncodingException
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser();

            parser.setInputFile( SearchResultEntryTest.class.getResource( "response_with_1_attr_1_base64_value.xml" )
                .getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        SearchResultEntry searchResultEntry = ( ( SearchResponse ) parser.getBatchResponse().getCurrentResponse() )
            .getCurrentSearchResultEntry();

        Attributes attributes = searchResultEntry.getPartialAttributeList();

        assertEquals( 1, attributes.size() );

        NamingEnumeration ne = attributes.getAll();

        Attribute attribute = ( Attribute ) ne.nextElement();

        assertEquals( "cn", attribute.getID() );

        assertEquals( 1, attribute.size() );

        NamingEnumeration ne2 = null;
        try
        {
            ne2 = attribute.getAll();
        }
        catch ( NamingException e )
        {
            fail();
        }

        Object value = ne2.nextElement();

        String expected = new String( new byte[]
            { 'E', 'm', 'm', 'a', 'n', 'u', 'e', 'l', ' ', 'L', ( byte ) 0xc3, ( byte ) 0xa9, 'c', 'h', 'a', 'r', 'n',
                'y' }, "UTF-8" );

        assertEquals( expected, new String( ( byte[] ) value, "UTF-8" ) );
    }


    /**
     * Test parsing of a response with 1 Attr 1 empty Value
     */
    public void testResponseWith1Attr1EmptyValue()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser();

            parser.setInputFile( SearchResultEntryTest.class.getResource( "response_with_1_attr_1_empty_value.xml" )
                .getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        SearchResultEntry searchResultEntry = ( ( SearchResponse ) parser.getBatchResponse().getCurrentResponse() )
            .getCurrentSearchResultEntry();

        Attributes attributes = searchResultEntry.getPartialAttributeList();

        assertEquals( 1, attributes.size() );

        NamingEnumeration ne = attributes.getAll();

        Attribute attribute = ( Attribute ) ne.nextElement();

        assertEquals( "dc", attribute.getID() );

        assertEquals( 1, attribute.size() );

        NamingEnumeration ne2 = null;
        try
        {
            ne2 = attribute.getAll();
        }
        catch ( NamingException e )
        {
            fail();
        }

        String value = ( String ) ne2.nextElement();

        assertEquals( "", value );
    }


    /**
     * Test parsing of a response with 1 Attr 2 Value
     */
    public void testResponseWith1Attr2Value()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser();

            parser.setInputFile( SearchResultEntryTest.class.getResource( "response_with_1_attr_2_value.xml" )
                .getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        SearchResultEntry searchResultEntry = ( ( SearchResponse ) parser.getBatchResponse().getCurrentResponse() )
            .getCurrentSearchResultEntry();

        Attributes attributes = searchResultEntry.getPartialAttributeList();

        assertEquals( 1, attributes.size() );

        NamingEnumeration ne = attributes.getAll();

        Attribute attribute = ( Attribute ) ne.nextElement();

        assertEquals( "objectclass", attribute.getID() );

        assertEquals( 2, attribute.size() );

        NamingEnumeration ne2 = null;
        try
        {
            ne2 = attribute.getAll();
        }
        catch ( NamingException e )
        {
            fail();
        }

        String value = ( String ) ne2.nextElement();

        assertEquals( "top", value );

        value = ( String ) ne2.nextElement();

        assertEquals( "domain", value );
    }


    /**
     * Test parsing of a response with 2 Attr 1 Value
     */
    public void testResponseWith2Attr1Value()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser();

            parser.setInputFile( SearchResultEntryTest.class.getResource( "response_with_2_attr_1_value.xml" )
                .getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        SearchResultEntry searchResultEntry = ( ( SearchResponse ) parser.getBatchResponse().getCurrentResponse() )
            .getCurrentSearchResultEntry();

        Attributes attributes = searchResultEntry.getPartialAttributeList();

        assertEquals( 2, attributes.size() );

        NamingEnumeration ne = attributes.getAll();

        Attribute attribute = ( Attribute ) ne.nextElement();

        assertEquals( "objectclass", attribute.getID() );

        assertEquals( 1, attribute.size() );

        NamingEnumeration ne2 = null;
        try
        {
            ne2 = attribute.getAll();
        }
        catch ( NamingException e )
        {
            fail();
        }

        String value = ( String ) ne2.nextElement();

        assertEquals( "top", value );

        attribute = ( Attribute ) ne.nextElement();

        assertEquals( "dc", attribute.getID() );

        assertEquals( 1, attribute.size() );

        ne2 = null;
        try
        {
            ne2 = attribute.getAll();
        }
        catch ( NamingException e )
        {
            fail();
        }

        value = ( String ) ne2.nextElement();

        assertEquals( "example", value );
    }


    /**
     * Test parsing of a response with 1 Attr without name Attribute
     */
    public void testResponseWith1AttrWithoutNameAttribute()
    {
        testParsingFail( SearchResultEntryTest.class, "response_with_1_attr_without_name_attribute.xml" );
    }
}
