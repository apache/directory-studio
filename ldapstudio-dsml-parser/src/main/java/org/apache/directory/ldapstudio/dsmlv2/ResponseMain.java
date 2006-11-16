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

package org.apache.directory.ldapstudio.dsmlv2;

import java.io.FileNotFoundException;

import org.apache.directory.shared.ldap.codec.LdapMessage;
import org.xmlpull.v1.XmlPullParserException;


public class ResponseMain
{
    /**
     * @param args
     * @throws FileNotFoundException 
     * @throws XmlPullParserException 
     */
    public static void main(String[] args) throws FileNotFoundException, XmlPullParserException
    {
        Dsmlv2ResponseParser parser = new Dsmlv2ResponseParser();
        
        parser.setInputFile( "BatchResponse.xml" );
        
        try
        {
            parser.parseBatchResponse();

            BatchResponse batchResponse = parser.getBatchResponse();
            
            System.out.println("toto");
            
            LdapMessage request1 = parser.getNextResponse();
            
            System.out.println("toto2");

            LdapMessage request2 = parser.getNextResponse();
            
            System.out.println("toto3");
            
            parser.parseAllResponses();
        }
        catch ( Exception e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("Done");
        
//        Tag tag1 = new Tag("toto", Tag.START);
//        Tag tag2 = new Tag("toto", Tag.END);
//        Tag tag3 = new Tag("tata", Tag.START);
//        Tag tag4 = new Tag("toto", Tag.START);
//        
//        System.out.println( tag1.equals( tag3 ) );
        
   //     Dsmlv2Grammar dsmlv2Grammar = Dsmlv2Grammar.getInstance();
//        System.out.println( dsmlv2Grammar.getStateFromTag( Dsmlv2StatesEnum.BATCHREQUEST_START_TAG, new Tag("addrequESt", Tag.START) ) );
        
    }
}
