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

package org.apache.directory.ldapstudio.dsmlv2.modifyRequest;

import java.util.ArrayList;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.ModificationItem;

import org.apache.directory.ldapstudio.dsmlv2.AbstractTest;
import org.apache.directory.ldapstudio.dsmlv2.Dsmlv2Parser;
import org.apache.directory.shared.ldap.codec.Control;
import org.apache.directory.shared.ldap.codec.LdapConstants;
import org.apache.directory.shared.ldap.codec.modify.ModifyRequest;
import org.apache.directory.shared.ldap.util.StringTools;

/**
 * Tests for the Modify Request parsing
 */
public class ModifyRequestTest extends AbstractTest
{
    /**
     * Test parsing of a request with the (optional) requestID attribute
     */
    public void testRequestWithRequestId()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();
            
            parser.setInputFile( ModifyRequestTest.class.getResource( "request_with_requestID_attribute.xml" ).getFile() );
        
            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }
        
        ModifyRequest modifyRequest = ( ModifyRequest ) parser.getBatchRequest().getCurrentRequest();
        
        assertEquals( 456, modifyRequest.getMessageId() );
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
            
            parser.setInputFile( ModifyRequestTest.class.getResource( "request_with_1_control.xml" ).getFile() );
        
            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }
        
        ModifyRequest modifyRequest = ( ModifyRequest ) parser.getBatchRequest().getCurrentRequest();
        
        assertEquals( 1, modifyRequest.getControls().size() );
        
        Control control = modifyRequest.getCurrentControl();
        
        assertTrue( control.getCriticality() );
        
        assertEquals( "1.2.840.113556.1.4.643", control.getControlType() );
        
        assertEquals( "Some text", StringTools.utf8ToString( ( byte[] ) control.getControlValue() ) );
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
            
            parser.setInputFile( ModifyRequestTest.class.getResource( "request_with_2_controls.xml" ).getFile() );
        
            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }
        
        ModifyRequest modifyRequest = ( ModifyRequest ) parser.getBatchRequest().getCurrentRequest();
        
        assertEquals( 2, modifyRequest.getControls().size() );
        
        Control control = modifyRequest.getCurrentControl();
        
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
            
            parser.setInputFile( ModifyRequestTest.class.getResource( "request_with_3_controls_without_value.xml" ).getFile() );
        
            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }
        
        ModifyRequest modifyRequest = ( ModifyRequest ) parser.getBatchRequest().getCurrentRequest();
        
        assertEquals( 3, modifyRequest.getControls().size() );
        
        Control control = modifyRequest.getCurrentControl();
        
        assertTrue( control.getCriticality() );
        
        assertEquals( "1.2.840.113556.1.4.456", control.getControlType() );
        
        assertEquals( StringTools.EMPTY_BYTES, control.getControlValue() );
    }
    
    /**
     * Test parsing of a request without dn attribute
     */
    public void testRequestWithoutDnAttribute()
    {
        testParsingFail( ModifyRequestTest.class, "request_without_dn_attribute.xml" );
    }
  
    /**
     * Test parsing of a request with a Modification element
     * @throws NamingException 
     */
    public void testRequestWith1Modification() throws NamingException
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();
            
            parser.setInputFile( ModifyRequestTest.class.getResource( "request_with_1_modification.xml" ).getFile() );
        
            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }
        
        ModifyRequest modifyRequest = ( ModifyRequest ) parser.getBatchRequest().getCurrentRequest();
        
        assertEquals( LdapConstants.OPERATION_ADD, modifyRequest.getCurrentOperation() );
        
        assertEquals( "directreport", modifyRequest.getCurrentAttributeType() );
        
        ArrayList modifications = modifyRequest.getModifications();
        
        assertEquals(1, modifications.size() );
        
        ModificationItem modification = ( ModificationItem ) modifications.get( 0 );
        
        Attribute attribute = modification.getAttribute();
        
        assertEquals( "CN=John Smith, DC=microsoft, DC=com", attribute.get( 0 ) );
    }
    
    /**
     * Test parsing of a request with 2 Modification elements
     * @throws NamingException 
     */
    public void testRequestWith2Modifications() throws NamingException
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();
            
            parser.setInputFile( ModifyRequestTest.class.getResource( "request_with_2_modifications.xml" ).getFile() );
        
            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }
        
        ModifyRequest modifyRequest = ( ModifyRequest ) parser.getBatchRequest().getCurrentRequest();
        
        assertEquals( LdapConstants.OPERATION_REPLACE, modifyRequest.getCurrentOperation() );
        
        assertEquals( "sn", modifyRequest.getCurrentAttributeType() );
        
        ArrayList modifications = modifyRequest.getModifications();
        
        assertEquals(2, modifications.size() );
        
        ModificationItem modification = ( ModificationItem ) modifications.get( 1 );
        
        Attribute attribute = modification.getAttribute();
        
        assertEquals( "CN=Steve Jobs, DC=apple, DC=com", attribute.get( 0 ) );
    }
    
    /**
     * Test parsing of a request without name attribute to the Modification element
     */
    public void testRequestWithoutNameAttribute()
    {
        testParsingFail( ModifyRequestTest.class, "request_without_name_attribute.xml" );
    }
    
    /**
     * Test parsing of a request without operation attribute to the Modification element
     */
    public void testRequestWithoutOperationAttribute()
    {
        testParsingFail( ModifyRequestTest.class, "request_without_operation_attribute.xml" );
    }
    
    /**
     * Test parsing of a request with operation attribute to Add value
     * @throws NamingException 
     */
    public void testRequestWithOperationAdd() throws NamingException
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();
            
            parser.setInputFile( ModifyRequestTest.class.getResource( "request_with_operation_add.xml" ).getFile() );
        
            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }
        
        ModifyRequest modifyRequest = ( ModifyRequest ) parser.getBatchRequest().getCurrentRequest();
        
        assertEquals( LdapConstants.OPERATION_ADD, modifyRequest.getCurrentOperation() );
    }
    
    /**
     * Test parsing of a request with operation attribute to Delete value
     * @throws NamingException 
     */
    public void testRequestWithOperationDelete() throws NamingException
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();
            
            parser.setInputFile( ModifyRequestTest.class.getResource( "request_with_operation_delete.xml" ).getFile() );
        
            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }
        
        ModifyRequest modifyRequest = ( ModifyRequest ) parser.getBatchRequest().getCurrentRequest();
        
        assertEquals( LdapConstants.OPERATION_DELETE, modifyRequest.getCurrentOperation() );
    }

    /**
     * Test parsing of a request with operation attribute to Replace value
     * @throws NamingException 
     */
    public void testRequestWithOperationReplace() throws NamingException
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();
            
            parser.setInputFile( ModifyRequestTest.class.getResource( "request_with_operation_replace.xml" ).getFile() );
        
            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }
        
        ModifyRequest modifyRequest = ( ModifyRequest ) parser.getBatchRequest().getCurrentRequest();
        
        assertEquals( LdapConstants.OPERATION_REPLACE, modifyRequest.getCurrentOperation() );
    }
    
    /**
     * Test parsing of a request without operation attribute to the Modification element
     */
    public void testRequestWithOperationError()
    {
        testParsingFail( ModifyRequestTest.class, "request_with_operation_error.xml" );
    }
    
    /**
     * Test parsing of a request with a Modification element without Value element
     * @throws NamingException 
     */
    public void testRequestWithModificationWithoutValue() throws NamingException
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();
            
            parser.setInputFile( ModifyRequestTest.class.getResource( "request_with_modification_without_value.xml" ).getFile() );
        
            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }
        
        ModifyRequest modifyRequest = ( ModifyRequest ) parser.getBatchRequest().getCurrentRequest();
        
        assertEquals( LdapConstants.OPERATION_ADD, modifyRequest.getCurrentOperation() );
        
        assertEquals( "directreport", modifyRequest.getCurrentAttributeType() );
        
        ArrayList modifications = modifyRequest.getModifications();

        ModificationItem modification = ( ModificationItem ) modifications.get( 0 );
        
        Attribute attribute = modification.getAttribute();
        
        assertEquals( 0, attribute.size() );
    }
    
    /**
     * Test parsing of a request with a Modification element
     * @throws NamingException 
     */
    public void testRequestWithModificationWith2Values() throws NamingException
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();
            
            parser.setInputFile( ModifyRequestTest.class.getResource( "request_with_modification_with_2_values.xml" ).getFile() );
        
            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }
        
        ModifyRequest modifyRequest = ( ModifyRequest ) parser.getBatchRequest().getCurrentRequest();
        
        assertEquals( LdapConstants.OPERATION_ADD, modifyRequest.getCurrentOperation() );
        
        assertEquals( "directreport", modifyRequest.getCurrentAttributeType() );
        
        ArrayList modifications = modifyRequest.getModifications();
        
        assertEquals( 1, modifications.size() );
        
        ModificationItem modification = ( ModificationItem ) modifications.get( 0 );
        
        Attribute attribute = modification.getAttribute();
        
        assertEquals( 2, attribute.size() );        
        assertEquals( "CN=John Smith, DC=microsoft, DC=com", attribute.get( 0 ) );
        assertEquals( "CN=Steve Jobs, DC=apple, DC=com", attribute.get( 1 ) );
    }
}
