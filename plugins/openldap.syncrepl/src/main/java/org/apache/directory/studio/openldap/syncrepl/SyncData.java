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
 * This enum implements all the possible values for the Sync Data value.
 */
public enum SyncData
{
    /** The 'default' Sync Data value */
    DEFAULT("default"),

    /** The 'accesslog' Sync Data value */
    ACCESSLOG("accesslog"),

    /** The 'changelog' Sync Data value */
    CHANGELOG("changelog");

    /** The value */
    private String value;


    /**
     * Parses a sync data string.
     *
     * @param s the string
     * @return a sync data
     * @throws ParseException if an error occurs during parsing
     */
    public static SyncData parse( String s ) throws ParseException
    {
        // DEFAULT
        if ( DEFAULT.value.equalsIgnoreCase( s ) )
        {
            return DEFAULT;
        }
        // ACCESSLOG
        else if ( ACCESSLOG.value.equalsIgnoreCase( s ) )
        {
            return ACCESSLOG;
        }
        // CHANGELOG
        else if ( CHANGELOG.value.equalsIgnoreCase( s ) )
        {
            return CHANGELOG;
        }
        else
        {
            throw new ParseException( "Unable to parse string '" + s + "' as a valid sync data.", 0 );
        }
    }


    /**
     * Creates a new instance of SyncData.
     *
     * @param value the value
     */
    private SyncData( String value )
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
