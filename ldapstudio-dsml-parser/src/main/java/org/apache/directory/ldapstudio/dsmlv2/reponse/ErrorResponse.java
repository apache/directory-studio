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

package org.apache.directory.ldapstudio.dsmlv2.reponse;


import org.apache.directory.shared.ldap.codec.LdapResponse;
import org.dom4j.Element;


/**
 * Class representing Error Response.
 * 
 * An Error Response has a requestID, a message, and a type which can be :
 *     - NOT_ATTEMPTED,
 *     - COULD_NOT_CONNECT,
 *     - CONNECTION_CLOSED,
 *     - MALFORMED_REQUEST,
 *     - GATEWAY_INTERNAL_ERROR,
 *     - AUTHENTICATION_FAILED,
 *     - UNRESOLVABLE_URI,
 *     - OTHER
 */
public class ErrorResponse extends LdapResponse implements DsmlDecorator
{
    public enum ErrorResponseType
    {
        NOT_ATTEMPTED, COULD_NOT_CONNECT, CONNECTION_CLOSED, MALFORMED_REQUEST, GATEWAY_INTERNAL_ERROR, AUTHENTICATION_FAILED, UNRESOLVABLE_URI, OTHER
    };

    private ErrorResponseType type;

    private String message;

    private int requestID;


    public ErrorResponse()
    {
    }


    /**
     * Default constructor
     * @param requestID The requestID of the response
     * @param type The type of the response
     * @param message The associated message
     */
    public ErrorResponse( int requestID, ErrorResponseType type, String message )
    {
        this.requestID = requestID;
        this.type = type;
        this.message = message;
    }


    /**
     * Convert the request to its XML representation in the DSMLv2 format.
     * @param root the root dom4j Element
     * @return the dom4j Element corresponding to the entry.
     */
    public Element toDsml( Element root )
    {
        Element element = root.addElement( "errorResponse" );

        // RequestID
        if ( requestID != 0 )
        {
            element.addAttribute( "requestID", "" + requestID );
        }

        // Type
        element.addAttribute( "type", getTypeDescr( type ) );

        // TODO Add Detail

        if ( ( message != null ) && ( !"".equals( message ) ) )
        {
            Element messageElement = element.addElement( "message" );
            messageElement.addText( message );
        }

        return element;
    }


    /**
     * Returns the String associated to the error response type
     * @param type the error response type
     * @return the corresponding String
     */
    public String getTypeDescr( ErrorResponseType type )
    {
        if ( type.equals( ErrorResponseType.NOT_ATTEMPTED ) )
        {
            return "notAttempted";
        }
        else if ( type.equals( ErrorResponseType.COULD_NOT_CONNECT ) )
        {
            return "couldNotConnect";
        }
        else if ( type.equals( ErrorResponseType.CONNECTION_CLOSED ) )
        {
            return "connectionClosed";
        }
        else if ( type.equals( ErrorResponseType.MALFORMED_REQUEST ) )
        {
            return "malformedRequest";
        }
        else if ( type.equals( ErrorResponseType.GATEWAY_INTERNAL_ERROR ) )
        {
            return "gatewayInternalError";
        }
        else if ( type.equals( ErrorResponseType.AUTHENTICATION_FAILED ) )
        {
            return "authenticationFailed";
        }
        else if ( type.equals( ErrorResponseType.UNRESOLVABLE_URI ) )
        {
            return "unresolvableURI";
        }
        else if ( type.equals( ErrorResponseType.OTHER ) )
        {
            return "other";
        }
        else
        {
            return "unknown";
        }
    }


    public String getMessage()
    {
        return message;
    }


    public void setMessage( String message )
    {
        this.message = message;
    }


    public int getRequestID()
    {
        return requestID;
    }


    public void setRequestID( int requestID )
    {
        this.requestID = requestID;
    }


    public ErrorResponseType getType()
    {
        return type;
    }


    public void setType( ErrorResponseType type )
    {
        this.type = type;
    }
}
