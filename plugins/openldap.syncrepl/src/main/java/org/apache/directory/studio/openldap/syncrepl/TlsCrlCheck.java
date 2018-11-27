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
 * This enum implements all the possible values for the TLS CRL Check value.
 */
public enum TlsCrlCheck
{
    /** The 'none' TLS CRL Check value */
    NONE("none"),

    /** The 'peer' TLS CRL Check value */
    PEER("peer"),

    /** The 'all' TLS CRL Check value */
    ALL("all");

    /** The value */
    private String value;


    /**
     * Parses a tls crl check string.
     *
     * @param s the string
     * @return a tls crl check
     * @throws ParseException if an error occurs during parsing
     */
    public static TlsCrlCheck parse( String s ) throws ParseException
    {
        // NONE
        if ( NONE.value.equalsIgnoreCase( s ) )
        {
            return NONE;
        }
        // PEER
        else if ( PEER.value.equalsIgnoreCase( s ) )
        {
            return PEER;
        }
        // ALL
        else if ( ALL.value.equalsIgnoreCase( s ) )
        {
            return ALL;
        }
        else
        {
            throw new ParseException( "Unable to parse string '" + s + "' as a valid tls crl check method.", 0 );
        }
    }


    /**
     * Creates a new instance of TlsCrlCheck.
     *
     * @param value the value
     */
    private TlsCrlCheck( String value )
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
