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
 * This enum implements all the possible values for the schema checking value.
 */
public enum SchemaChecking
{
    /** The 'on' schema checking value */
    ON("on"),

    /** The 'off' schema checking value */
    OFF("off");

    /** The value */
    private String value;


    /**
     * Parses a schema checking string.
     *
     * @param s the string
     * @return a schema checking
     * @throws ParseException if an error occurs during parsing
     */
    public static SchemaChecking parse( String s ) throws ParseException
    {
        // ON
        if ( ON.value.equalsIgnoreCase( s ) )
        {
            return ON;
        }
        // OFF
        else if ( OFF.value.equalsIgnoreCase( s ) )
        {
            return OFF;
        }
        else
        {
            throw new ParseException( "Unable to parse string '" + s + "' as a valid schema checking.", 0 );
        }
    }


    /**
     * Creates a new instance of SchemaChecking.
     *
     * @param value the value
     */
    private SchemaChecking( String value )
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
