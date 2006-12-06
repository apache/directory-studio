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

package org.apache.directory.ldapstudio.dsmlv2.abandonRequest;


import org.apache.directory.ldapstudio.dsmlv2.AbstractTest;
import org.apache.directory.ldapstudio.dsmlv2.Dsmlv2Parser;
import org.apache.directory.shared.ldap.codec.Control;
import org.apache.directory.shared.ldap.codec.abandon.AbandonRequest;
import org.apache.directory.shared.ldap.util.StringTools;


/**
 * Tests for the Abandon Request parsing
 */
public class AbandonRequestTest extends AbstractTest
{
    /**
     * Test parsing of a request without the abandonID attribute
     */
    public void testRequestWithoutAbandonId()
    {
        testParsingFail( AbandonRequestTest.class, "request_without_abandonID_attribute.xml" );
    }


    /**
     * Test parsing of a request with the abandonID attribute
     */
    public void testRequestWithAbandonId()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInputFile( AbandonRequestTest.class.getResource( "request_with_abandonID_attribute.xml" )
                .getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        AbandonRequest abandonRequest = ( AbandonRequest ) parser.getBatchRequest().getCurrentRequest();

        assertEquals( 123, abandonRequest.getAbandonedMessageId() );
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

            parser.setInputFile( AbandonRequestTest.class.getResource( "request_with_requestID_attribute.xml" )
                .getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        AbandonRequest abandonRequest = ( AbandonRequest ) parser.getBatchRequest().getCurrentRequest();

        assertEquals( 456, abandonRequest.getMessageId() );
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

            parser.setInputFile( AbandonRequestTest.class.getResource( "request_with_1_control.xml" ).getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        AbandonRequest abandonRequest = ( AbandonRequest ) parser.getBatchRequest().getCurrentRequest();

        assertEquals( 1, abandonRequest.getControls().size() );

        Control control = abandonRequest.getCurrentControl();

        assertTrue( control.getCriticality() );

        assertEquals( "1.2.840.113556.1.4.643", control.getControlType() );

        assertEquals( "Some text", StringTools.utf8ToString( ( byte[] ) control.getControlValue() ) );
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

            parser.setInputFile( AbandonRequestTest.class.getResource( "request_with_1_control_empty_value.xml" )
                .getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        AbandonRequest abandonRequest = ( AbandonRequest ) parser.getBatchRequest().getCurrentRequest();

        assertEquals( 1, abandonRequest.getControls().size() );

        Control control = abandonRequest.getCurrentControl();

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

            parser.setInputFile( AbandonRequestTest.class.getResource( "request_with_2_controls.xml" ).getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        AbandonRequest abandonRequest = ( AbandonRequest ) parser.getBatchRequest().getCurrentRequest();

        assertEquals( 2, abandonRequest.getControls().size() );

        Control control = abandonRequest.getCurrentControl();

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

            parser.setInputFile( AbandonRequestTest.class.getResource( "request_with_3_controls_without_value.xml" )
                .getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        AbandonRequest abandonRequest = ( AbandonRequest ) parser.getBatchRequest().getCurrentRequest();

        assertEquals( 3, abandonRequest.getControls().size() );

        Control control = abandonRequest.getCurrentControl();

        assertTrue( control.getCriticality() );

        assertEquals( "1.2.840.113556.1.4.456", control.getControlType() );

        assertEquals( StringTools.EMPTY_BYTES, control.getControlValue() );
    }
}
