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

package org.apache.directory.ldapstudio.dsmlv2.batchRequest;


import java.util.List;

import org.apache.directory.ldapstudio.dsmlv2.AbstractTest;
import org.apache.directory.ldapstudio.dsmlv2.BatchRequest;
import org.apache.directory.ldapstudio.dsmlv2.Dsmlv2Parser;
import org.apache.directory.shared.ldap.codec.LdapMessage;
import org.apache.directory.shared.ldap.codec.abandon.AbandonRequest;
import org.apache.directory.shared.ldap.codec.add.AddRequest;
import org.apache.directory.shared.ldap.codec.bind.BindRequest;
import org.apache.directory.shared.ldap.codec.compare.CompareRequest;
import org.apache.directory.shared.ldap.codec.del.DelRequest;
import org.apache.directory.shared.ldap.codec.extended.ExtendedRequest;
import org.apache.directory.shared.ldap.codec.modify.ModifyRequest;
import org.apache.directory.shared.ldap.codec.modifyDn.ModifyDNRequest;
import org.apache.directory.shared.ldap.codec.search.SearchRequest;


/**
 * Tests for the Compare Response parsing
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BatchRequestTest extends AbstractTest
{
    /**
     * Test parsing of a Request with the (optional) requestID attribute
     */
    public void testResponseWithRequestId()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser
                .setInputFile( BatchRequestTest.class.getResource( "request_with_requestID_attribute.xml" ).getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequest batchRequest = parser.getBatchRequest();

        assertEquals( 1234567890, batchRequest.getRequestID() );
    }
    
    /**
     * Test parsing of a request with the (optional) requestID attribute equals to 0
     */
    public void testRequestWithRequestIdEquals0()
    {
        testParsingFail( BatchRequestTest.class, "request_with_requestID_equals_0.xml" );
    }


    /**
     * Test parsing of a Request with the (optional) requestID attribute
     */
    public void testResponseWith0Request()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser
                .setInputFile( BatchRequestTest.class.getResource( "request_with_requestID_attribute.xml" ).getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequest batchRequest = parser.getBatchRequest();

        assertEquals( 0, batchRequest.getRequests().size() );
    }


    /**
     * Test parsing of a Request with 1 AuthRequest
     */
    public void testResponseWith1AuthRequest()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInputFile( BatchRequestTest.class.getResource( "request_with_1_AuthRequest.xml" ).getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequest batchRequest = parser.getBatchRequest();

        assertEquals( 1, batchRequest.getRequests().size() );

        LdapMessage request = batchRequest.getCurrentRequest();

        if ( request instanceof BindRequest )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Request with 1 AddRequest
     */
    public void testResponseWith1AddRequest()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInputFile( BatchRequestTest.class.getResource( "request_with_1_AddRequest.xml" ).getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequest batchRequest = parser.getBatchRequest();

        assertEquals( 1, batchRequest.getRequests().size() );

        LdapMessage request = batchRequest.getCurrentRequest();

        if ( request instanceof AddRequest )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Request with 1 CompareRequest
     */
    public void testResponseWith1CompareRequest()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInputFile( BatchRequestTest.class.getResource( "request_with_1_CompareRequest.xml" ).getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequest batchRequest = parser.getBatchRequest();

        assertEquals( 1, batchRequest.getRequests().size() );

        LdapMessage request = batchRequest.getCurrentRequest();

        if ( request instanceof CompareRequest )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Request with 1 AbandonRequest
     */
    public void testResponseWith1AbandonRequest()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInputFile( BatchRequestTest.class.getResource( "request_with_1_AbandonRequest.xml" ).getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequest batchRequest = parser.getBatchRequest();

        assertEquals( 1, batchRequest.getRequests().size() );

        LdapMessage request = batchRequest.getCurrentRequest();

        if ( request instanceof AbandonRequest )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Request with 1 DelRequest
     */
    public void testResponseWith1DelRequest()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInputFile( BatchRequestTest.class.getResource( "request_with_1_DelRequest.xml" ).getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequest batchRequest = parser.getBatchRequest();

        assertEquals( 1, batchRequest.getRequests().size() );

        LdapMessage request = batchRequest.getCurrentRequest();

        if ( request instanceof DelRequest )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Request with 1 ExtendedRequest
     */
    public void testResponseWith1ExtendedRequest()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInputFile( BatchRequestTest.class.getResource( "request_with_1_ExtendedRequest.xml" ).getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequest batchRequest = parser.getBatchRequest();

        assertEquals( 1, batchRequest.getRequests().size() );

        LdapMessage request = batchRequest.getCurrentRequest();

        if ( request instanceof ExtendedRequest )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Request with 1 ModDNRequest
     */
    public void testResponseWith1ModDNRequest()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInputFile( BatchRequestTest.class.getResource( "request_with_1_ModDNRequest.xml" ).getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequest batchRequest = parser.getBatchRequest();

        assertEquals( 1, batchRequest.getRequests().size() );

        LdapMessage request = batchRequest.getCurrentRequest();

        if ( request instanceof ModifyDNRequest )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Request with 1 ModifyRequest
     */
    public void testResponseWith1ModifyRequest()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInputFile( BatchRequestTest.class.getResource( "request_with_1_ModifyRequest.xml" ).getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequest batchRequest = parser.getBatchRequest();

        assertEquals( 1, batchRequest.getRequests().size() );

        LdapMessage request = batchRequest.getCurrentRequest();

        if ( request instanceof ModifyRequest )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Request with 1 SearchRequest
     */
    public void testResponseWith1SearchRequest()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInputFile( BatchRequestTest.class.getResource( "request_with_1_SearchRequest.xml" ).getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequest batchRequest = parser.getBatchRequest();

        assertEquals( 1, batchRequest.getRequests().size() );

        LdapMessage request = batchRequest.getCurrentRequest();

        if ( request instanceof SearchRequest )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Request with 2 AddRequest
     */
    public void testResponseWith2AddRequest()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInputFile( BatchRequestTest.class.getResource( "request_with_2_AddRequest.xml" ).getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequest batchRequest = parser.getBatchRequest();

        assertEquals( 2, batchRequest.getRequests().size() );

        LdapMessage request = batchRequest.getCurrentRequest();

        if ( request instanceof AddRequest )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Request with 2 CompareRequest
     */
    public void testResponseWith2CompareRequest()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInputFile( BatchRequestTest.class.getResource( "request_with_2_CompareRequest.xml" ).getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequest batchRequest = parser.getBatchRequest();

        assertEquals( 2, batchRequest.getRequests().size() );

        LdapMessage request = batchRequest.getCurrentRequest();

        if ( request instanceof CompareRequest )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Request with 2 AbandonRequest
     */
    public void testResponseWith2AbandonRequest()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInputFile( BatchRequestTest.class.getResource( "request_with_2_AbandonRequest.xml" ).getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequest batchRequest = parser.getBatchRequest();

        assertEquals( 2, batchRequest.getRequests().size() );

        LdapMessage request = batchRequest.getCurrentRequest();

        if ( request instanceof AbandonRequest )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Request with 2 DelRequest
     */
    public void testResponseWith2DelRequest()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInputFile( BatchRequestTest.class.getResource( "request_with_2_DelRequest.xml" ).getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequest batchRequest = parser.getBatchRequest();

        assertEquals( 2, batchRequest.getRequests().size() );

        LdapMessage request = batchRequest.getCurrentRequest();

        if ( request instanceof DelRequest )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Request with 2 ExtendedRequest
     */
    public void testResponseWith2ExtendedRequest()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInputFile( BatchRequestTest.class.getResource( "request_with_2_ExtendedRequest.xml" ).getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequest batchRequest = parser.getBatchRequest();

        assertEquals( 2, batchRequest.getRequests().size() );

        LdapMessage request = batchRequest.getCurrentRequest();

        if ( request instanceof ExtendedRequest )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Request with 2 ModDNRequest
     */
    public void testResponseWith2ModDNRequest()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInputFile( BatchRequestTest.class.getResource( "request_with_2_ModDNRequest.xml" ).getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequest batchRequest = parser.getBatchRequest();

        assertEquals( 2, batchRequest.getRequests().size() );

        LdapMessage request = batchRequest.getCurrentRequest();

        if ( request instanceof ModifyDNRequest )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Request with 2 ModifyRequest
     */
    public void testResponseWith2ModifyRequest()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInputFile( BatchRequestTest.class.getResource( "request_with_2_ModifyRequest.xml" ).getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequest batchRequest = parser.getBatchRequest();

        assertEquals( 2, batchRequest.getRequests().size() );

        LdapMessage request = batchRequest.getCurrentRequest();

        if ( request instanceof ModifyRequest )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Request with 2 SearchRequest
     */
    public void testResponseWith2SearchRequest()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInputFile( BatchRequestTest.class.getResource( "request_with_2_SearchRequest.xml" ).getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequest batchRequest = parser.getBatchRequest();

        assertEquals( 2, batchRequest.getRequests().size() );

        LdapMessage request = batchRequest.getCurrentRequest();

        if ( request instanceof SearchRequest )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Request with 1 AuthRequest and 1 AddRequest
     */
    public void testResponseWith1AuthRequestAnd1AddRequest()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInputFile( BatchRequestTest.class.getResource( "request_with_1_AuthRequest_1_AddRequest.xml" )
                .getFile() );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequest batchRequest = parser.getBatchRequest();

        List requests = batchRequest.getRequests();

        assertEquals( 2, requests.size() );

        LdapMessage request = ( LdapMessage ) requests.get( 0 );

        if ( request instanceof BindRequest )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }

        request = ( LdapMessage ) requests.get( 1 );

        if ( request instanceof AddRequest )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a request with 1 wrong placed AuthRequest
     */
    public void testRequestWithWrongPlacedAuthRequest()
    {
        testParsingFail( BatchRequestTest.class, "request_with_wrong_placed_AuthRequest.xml" );
    }
}
