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

import java.util.ArrayList;
import java.util.List;

import org.apache.directory.shared.ldap.codec.LdapMessage;

public class BatchRequest
{
    private List<LdapMessage> requests;
    
    private int requestID;
    
    public enum Processing { SEQUENTIAL, PARALLEL };
    
    private Processing processing;
    
    public enum OnError { RESUME, EXIT };
    
    private OnError onError;
    
    public enum ResponseOrder { SEQUENTIAL, UNORDERED };
    
    private ResponseOrder responseOrder;
    
    public BatchRequest()
    {
        requests = new ArrayList<LdapMessage>();
    }
    
    public boolean addRequest(LdapMessage request)
    {
        return requests.add( request );
    }
    
    public LdapMessage getCurrentRequest()
    {
        return requests.get( requests.size() - 1 );
    }

    public int getRequestID()
    {
        return requestID;
    }

    public void setRequestID( int requestID )
    {
        this.requestID = requestID;
    }

    public Processing getProcessing()
    {
        return processing;
    }

    public void setProcessing( Processing processing )
    {
        this.processing = processing;
    }

    public OnError getOnError()
    {
        return onError;
    }

    public void setOnError( OnError onError )
    {
        this.onError = onError;
    }

    public ResponseOrder getResponseOrder()
    {
        return responseOrder;
    }

    public void setResponseOrder( ResponseOrder responseOrder )
    {
        this.responseOrder = responseOrder;
    }
    
	public List getRequests() {
		return requests;
	}

    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        
        sb.append( "[" );
        sb.append( "processing: " + processing );
        sb.append( " - " );
        sb.append( "onError: " + onError );
        sb.append( " - " );
        sb.append( "responseOrder: " + responseOrder );
        sb.append( "]" );

        return sb.toString();
    }

}
