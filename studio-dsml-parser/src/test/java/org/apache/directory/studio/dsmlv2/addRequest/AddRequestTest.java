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

package org.apache.directory.studio.dsmlv2.addRequest;


import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.apache.directory.shared.ldap.codec.Control;
import org.apache.directory.shared.ldap.codec.add.AddRequest;
import org.apache.directory.shared.ldap.util.StringTools;
import org.apache.directory.studio.dsmlv2.AbstractTest;
import org.apache.directory.studio.dsmlv2.Dsmlv2Parser;


/**
 * Tests for the Add Request parsing
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class AddRequestTest extends AbstractTest
{
    /**
     * Test parsing of a request without the dn attribute
     */
    public void testRequestWithoutDn()
    {
        testParsingFail( AddRequestTest.class, "request_without_dn_attribute.xml" );
    }


    /**
     * Test parsing of a request with the dn attribute
     */
    public void testRequestWithDn()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInputFile( AddRequestTest.class.getResource( "request_with_dn_attribute.xml" ).getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        AddRequest addRequest = ( AddRequest ) parser.getBatchRequest().getCurrentRequest();

        assertEquals( "cn=Bob Rush,ou=Dev,dc=Example,dc=COM", addRequest.getEntry().toString() );
    }


    /**
     * Test parsing of a request with the (optional) requestID attribute
     */
    public void testRequestWithRequestId()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInputFile( AddRequestTest.class.getResource( "request_with_requestID_attribute.xml" ).getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        AddRequest addRequest = ( AddRequest ) parser.getBatchRequest().getCurrentRequest();

        assertEquals( 456, addRequest.getMessageId() );
    }


    /**
     * Test parsing of a request with the (optional) requestID attribute equals to 0
     */
    public void testRequestWithRequestIdEquals0()
    {
        testParsingFail( AddRequestTest.class, "request_with_requestID_equals_0.xml" );
    }


    /**
     * Test parsing of a request with a (optional) Control element
     */
    public void testRequestWith1Control()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInputFile( AddRequestTest.class.getResource( "request_with_1_control.xml" ).getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        AddRequest addRequest = ( AddRequest ) parser.getBatchRequest().getCurrentRequest();

        assertEquals( 1, addRequest.getControls().size() );

        Control control = addRequest.getCurrentControl();

        assertTrue( control.getCriticality() );

        assertEquals( "1.2.840.113556.1.4.643", control.getControlType() );

        assertEquals( "Some text", StringTools.utf8ToString( ( byte[] ) control.getControlValue() ) );
    }


    /**
     * Test parsing of a request with a (optional) Control element with Base64 value
     */
    public void testRequestWith1ControlBase64Value()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInputFile( AddRequestTest.class.getResource( "request_with_1_control_base64_value.xml" )
                .getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        AddRequest addRequest = ( AddRequest ) parser.getBatchRequest().getCurrentRequest();
        Control control = addRequest.getCurrentControl();

        assertEquals( 1, addRequest.getControls().size() );
        assertTrue( control.getCriticality() );
        assertEquals( "1.2.840.113556.1.4.643", control.getControlType() );
        assertEquals( "DSMLv2.0 rocks!!", StringTools.utf8ToString( ( byte[] ) control.getControlValue() ) );
    }


    /**
     * Test parsing of a request with a (optional) Control element with empty value
     */
    public void testRequestWith1ControlEmptyValue()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser
                .setInputFile( AddRequestTest.class.getResource( "request_with_1_control_empty_value.xml" ).getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        AddRequest addRequest = ( AddRequest ) parser.getBatchRequest().getCurrentRequest();
        Control control = addRequest.getCurrentControl();

        assertEquals( 1, addRequest.getControls().size() );
        assertTrue( control.getCriticality() );
        assertEquals( "1.2.840.113556.1.4.643", control.getControlType() );
        assertEquals( StringTools.EMPTY_BYTES, control.getControlValue() );
    }


    /**
     * Test parsing of a request with 2 (optional) Control elements
     */
    public void testRequestWith2Controls()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInputFile( AddRequestTest.class.getResource( "request_with_2_controls.xml" ).getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        AddRequest addRequest = ( AddRequest ) parser.getBatchRequest().getCurrentRequest();
        Control control = addRequest.getCurrentControl();

        assertEquals( 2, addRequest.getControls().size() );
        assertFalse( control.getCriticality() );
        assertEquals( "1.2.840.113556.1.4.789", control.getControlType() );
        assertEquals( "Some other text", StringTools.utf8ToString( ( byte[] ) control.getControlValue() ) );
    }


    /**
     * Test parsing of a request with 3 (optional) Control elements without value
     */
    public void testRequestWith3ControlsWithoutValue()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInputFile( AddRequestTest.class.getResource( "request_with_3_controls_without_value.xml" )
                .getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        AddRequest addRequest = ( AddRequest ) parser.getBatchRequest().getCurrentRequest();
        Control control = addRequest.getCurrentControl();

        assertEquals( 3, addRequest.getControls().size() );
        assertTrue( control.getCriticality() );
        assertEquals( "1.2.840.113556.1.4.456", control.getControlType() );
        assertEquals( StringTools.EMPTY_BYTES, control.getControlValue() );
    }


    /**
     * Test parsing of a request with an Attr elements with value
     */
    public void testRequestWith1AttrWithoutValue()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInputFile( AddRequestTest.class.getResource( "request_with_1_attr_without_value.xml" ).getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        AddRequest addRequest = ( AddRequest ) parser.getBatchRequest().getCurrentRequest();

        Attributes attributes = addRequest.getAttributes();

        assertEquals( 1, attributes.size() );

        // Getting the Attribute       
        NamingEnumeration ne = attributes.getAll();

        Attribute attribute = null;
        try
        {
            attribute = ( Attribute ) ne.next();
        }
        catch ( NamingException e )
        {
            fail( e.getMessage() );
        }

        assertEquals( "objectclass", attribute.getID() );

        // Getting the Value
        NamingEnumeration ne2 = null;
        try
        {
            ne2 = attribute.getAll();
        }
        catch ( NamingException e )
        {
            fail( e.getMessage() );
        }

        assertFalse( ne2.hasMoreElements() );
    }


    /**
     * Test parsing of a request with an Attr elements with empty value
     */
    public void testRequestWith1AttrEmptyValue()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInputFile( AddRequestTest.class.getResource( "request_with_1_attr_empty_value.xml" ).getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        AddRequest addRequest = ( AddRequest ) parser.getBatchRequest().getCurrentRequest();

        Attributes attributes = addRequest.getAttributes();

        assertEquals( 1, attributes.size() );

        // Getting the Attribute       
        NamingEnumeration ne = attributes.getAll();

        Attribute attribute = null;
        try
        {
            attribute = ( Attribute ) ne.next();
        }
        catch ( NamingException e )
        {
            fail( e.getMessage() );
        }

        assertEquals( "objectclass", attribute.getID() );

        // Getting the Value
        NamingEnumeration ne2 = null;
        try
        {
            ne2 = attribute.getAll();
        }
        catch ( NamingException e )
        {
            fail( e.getMessage() );
        }

        assertFalse( ne2.hasMoreElements() );
    }


    /**
     * Test parsing of a request with an Attr elements with value
     */
    public void testRequestWith1AttrWithValue()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInputFile( AddRequestTest.class.getResource( "request_with_1_attr_with_value.xml" ).getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        AddRequest addRequest = ( AddRequest ) parser.getBatchRequest().getCurrentRequest();

        Attributes attributes = addRequest.getAttributes();

        assertEquals( 1, attributes.size() );

        // Getting the Attribute       
        NamingEnumeration ne = attributes.getAll();

        Attribute attribute = null;
        try
        {
            attribute = ( Attribute ) ne.next();
        }
        catch ( NamingException e )
        {
            fail( e.getMessage() );
        }

        assertEquals( "objectclass", attribute.getID() );

        // Getting the Value
        NamingEnumeration ne2 = null;
        try
        {
            ne2 = attribute.getAll();
        }
        catch ( NamingException e )
        {
            fail( e.getMessage() );
        }

        String value = null;
        try
        {
            value = ( String ) ne2.next();
        }
        catch ( NamingException e )
        {
            fail( e.getMessage() );
        }

        assertEquals( "top", value );
    }


    /**
     * Test parsing of a request with an Attr elements with value
     */
    public void testRequestWith1AttrWithBase64Value()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInputFile( AddRequestTest.class.getResource( "request_with_1_attr_with_base64_value.xml" )
                .getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        AddRequest addRequest = ( AddRequest ) parser.getBatchRequest().getCurrentRequest();

        Attributes attributes = addRequest.getAttributes();

        assertEquals( 1, attributes.size() );

        // Getting the Attribute       
        NamingEnumeration ne = attributes.getAll();

        Attribute attribute = null;
        try
        {
            attribute = ( Attribute ) ne.next();
        }
        catch ( NamingException e )
        {
            fail( e.getMessage() );
        }

        assertEquals( "objectclass", attribute.getID() );

        // Getting the Value
        NamingEnumeration ne2 = null;
        try
        {
            ne2 = attribute.getAll();
        }
        catch ( NamingException e )
        {
            fail( e.getMessage() );
        }

        Object value = null;
        try
        {
            value = ne2.next();
        }
        catch ( NamingException e )
        {
            fail( e.getMessage() );
        }

        assertEquals( "DSMLv2.0 rocks!!", new String( ( byte[] ) value ) );
    }


    /**
     * Test parsing of a request with 2 Attr elements with value
     */
    public void testRequestWith2AttrWithValue()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInputFile( AddRequestTest.class.getResource( "request_with_2_attr_with_value.xml" ).getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        AddRequest addRequest = ( AddRequest ) parser.getBatchRequest().getCurrentRequest();

        Attributes attributes = addRequest.getAttributes();

        assertEquals( 1, attributes.size() );

        // Getting the Attribute       
        NamingEnumeration ne = attributes.getAll();

        Attribute attribute = null;
        try
        {
            attribute = ( Attribute ) ne.next();
        }
        catch ( NamingException e )
        {
            fail( e.getMessage() );
        }

        assertEquals( "objectclass", attribute.getID() );

        // Getting the Value
        NamingEnumeration ne2 = null;
        try
        {
            ne2 = attribute.getAll();
        }
        catch ( NamingException e )
        {
            fail( e.getMessage() );
        }

        String value = null;
        try
        {
            value = ( String ) ne2.next();
        }
        catch ( NamingException e )
        {
            fail( e.getMessage() );
        }

        assertEquals( "top", value );

        try
        {
            value = ( String ) ne2.next();
        }
        catch ( NamingException e )
        {
            fail( e.getMessage() );
        }

        assertEquals( "person", value );
    }


    /**
     * Test parsing of a request with 1 Attr element without attribute value
     */
    public void testRequestWith1AttrWithoutNameAttribute()
    {
        testParsingFail( AddRequestTest.class, "request_with_1_attr_without_name_attribute.xml" );
    }


    /**
     * Test parsing of a request with 1 Attr element with 2 Values
     */
    public void testRequestWith1AttrWith2Values()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInputFile( AddRequestTest.class.getResource( "request_with_1_attr_with_2_values.xml" ).getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        AddRequest addRequest = ( AddRequest ) parser.getBatchRequest().getCurrentRequest();

        Attributes attributes = addRequest.getAttributes();

        assertEquals( 1, attributes.size() );

        // Getting the Attribute       
        NamingEnumeration ne = attributes.getAll();

        Attribute attribute = null;
        try
        {
            attribute = ( Attribute ) ne.next();
        }
        catch ( NamingException e )
        {
            fail( e.getMessage() );
        }

        assertEquals( "objectclass", attribute.getID() );

        // Getting the Value
        NamingEnumeration ne2 = null;
        try
        {
            ne2 = attribute.getAll();
        }
        catch ( NamingException e )
        {
            fail( e.getMessage() );
        }

        String value = null;
        try
        {
            value = ( String ) ne2.next();
        }
        catch ( NamingException e )
        {
            fail( e.getMessage() );
        }

        assertEquals( "top", value );

        try
        {
            value = ( String ) ne2.next();
        }
        catch ( NamingException e )
        {
            fail( e.getMessage() );
        }

        assertEquals( "person", value );
    }


    /**
     * Test parsing of a request with a needed requestID attribute
     * 
     * DIRSTUDIO-1
     */
    public void testRequestWithNeededRequestId()
    {
        testParsingFail( AddRequestTest.class, "request_with_needed_requestID.xml" );
    }
}
