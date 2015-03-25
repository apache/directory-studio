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
 * This enum implements all the possible values for the SASL mechanism value.
 */
public enum SaslMechanism
{
    /** The 'digest-md5' SASL mechanism */
    DIGEST_MD5("DIGEST-MD5", "digest-md5"),

    /** The 'gssapi' SASL mechanism */
    GSSAPI("GSSAPI", "gssapi"), ;

    /** The title */
    private String title;

    /** The value */
    private String value;


    /**
     * Parses a sasl mechanism string.
     *
     * @param s the string
     * @return a sasl mechanism
     * @throws ParseException if an error occurs during parsing
     */
    public static SaslMechanism parse( String s ) throws ParseException
    {
        // DIGEST_MD5
        if ( DIGEST_MD5.value.equalsIgnoreCase( s ) )
        {
            return DIGEST_MD5;
        }
        // GSSAPI
        else if ( GSSAPI.value.equalsIgnoreCase( s ) )
        {
            return GSSAPI;
        }
        else
        {
            throw new ParseException( "Unable to parse string '" + s + "' as a valid sasl mechanism method.", 0 );
        }
    }


    /**
     * Creates a new instance of SaslMechanism.
     *
     * @param value the value
     */
    private SaslMechanism( String title, String value )
    {
        this.title = title;
        this.value = value;
    }


    /**
     * Gets the title.
     *
     * @return the title
     */
    public String getTitle()
    {
        return title;
    }


    /**
     * Gets the value.
     *
     * @return the value
     */
    public String getValue()
    {
        return value;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return title;
    }
}
