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
 * This enum implements all the possible values for the replication type value.
 */
public enum Type
{
    /** The 'refreshOnly' type value */
    REFRESH_ONLY("refreshOnly"),

    /** The 'refreshAndPersist' type value */
    REFRESH_AND_PERSIST("refreshAndPersist");

    /** The value */
    private String value;


    /**
     * Parses a type string.
     *
     * @param s the string
     * @return a type
     * @throws ParseException if an error occurs during parsing
     */
    public static Type parse( String s ) throws ParseException
    {
        // REFRESH_ONLY
        if ( REFRESH_ONLY.value.equalsIgnoreCase( s ) )
        {
            return REFRESH_ONLY;
        }
        // REFRESH_AND_PERSIST
        else if ( REFRESH_AND_PERSIST.value.equalsIgnoreCase( s ) )
        {
            return REFRESH_AND_PERSIST;
        }
        else
        {
            throw new ParseException( "Unable to parse string '" + s + "' as a valid type.", 0 );
        }
    }


    /**
     * Creates a new instance of Type.
     *
     * @param value the value
     */
    private Type( String value )
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
