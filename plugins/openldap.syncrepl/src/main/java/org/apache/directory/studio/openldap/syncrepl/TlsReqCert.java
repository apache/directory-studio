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
package org.apache.directory.studio.openldap.syncrepl;


import java.text.ParseException;


/**
 * This enum implements all the possible values for the TLS REQ Cert value.
 */
public enum TlsReqCert
{
    /** The 'never' TLS REQ Cert value */
    NEVER("never"),

    /** The 'allow' TLS REQ Cert value */
    ALLOW("allow"),

    /** The 'try' TLS REQ Cert value */
    TRY("try"),

    /** The 'demand' TLS REQ Cert value */
    DEMAND("demand");

    /** The value */
    private String value;


    /**
     * Parses a tls req cert string.
     *
     * @param s the string
     * @return a tls req cert
     * @throws ParseException if an error occurs during parsing
     */
    public static TlsReqCert parse( String s ) throws ParseException
    {
        // NEVER
        if ( NEVER.value.equalsIgnoreCase( s ) )
        {
            return NEVER;
        }
        // ALLOW
        else if ( ALLOW.value.equalsIgnoreCase( s ) )
        {
            return ALLOW;
        }
        // TRY
        else if ( TRY.value.equalsIgnoreCase( s ) )
        {
            return TRY;
        }
        // DEMAND
        else if ( DEMAND.value.equalsIgnoreCase( s ) )
        {
            return DEMAND;
        }
        else
        {
            throw new ParseException( "Unable to parse string '" + s + "' as a valid tls req cert.", 0 );
        }
    }


    /**
     * Creates a new instance of TlsReqCert.
     *
     * @param value the value
     */
    private TlsReqCert( String value )
    {
        this.value = value;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return value;
    }
}
