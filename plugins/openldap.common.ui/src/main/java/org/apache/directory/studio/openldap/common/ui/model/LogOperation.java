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
package org.apache.directory.studio.openldap.common.ui.model;


/**
 * This enums represents the various access log operation.
 */
public enum LogOperation
{
    WRITES("writes"),
    ADD("add"),
    DELETE("delete"),
    MODIFY("modify"),
    MODIFY_RDN("modrdn"),
    READS("reads"),
    COMPARE("compare"),
    SEARCH("search"),
    SESSION("session"),
    ABANDON("abandon"),
    BIND("bind"),
    UNBIND("unbind"),
    ALL("all");

    /** The value */
    protected String value;


    /**
     * Creates a new instance of LogOperation.
     *
     * @param value the value
     */
    private LogOperation( String value )
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


    /**
     * Gets the log operation corresponding to the given string.
     *
     * @param s the string
     * @return the corresponding log operation
     */
    public static  LogOperation fromString( String s )
    {
        if ( s != null )
        {
            if ( s.equalsIgnoreCase( WRITES.value ) )
            {
                return WRITES;
            }
            else if ( s.equalsIgnoreCase( ADD.value ) )
            {
                return ADD;
            }
            else if ( s.equalsIgnoreCase( DELETE.value ) )
            {
                return DELETE;
            }
            else if ( s.equalsIgnoreCase( MODIFY.value ) )
            {
                return MODIFY;
            }
            else if ( s.equalsIgnoreCase( MODIFY_RDN.value ) )
            {
                return MODIFY_RDN;
            }
            else if ( s.equalsIgnoreCase( READS.value ) )
            {
                return READS;
            }
            else if ( s.equalsIgnoreCase( COMPARE.value ) )
            {
                return COMPARE;
            }
            else if ( s.equalsIgnoreCase( SEARCH.value ) )
            {
                return SEARCH;
            }
            else if ( s.equalsIgnoreCase( SESSION.value ) )
            {
                return SESSION;
            }
            else if ( s.equalsIgnoreCase( ABANDON.value ) )
            {
                return ABANDON;
            }
            else if ( s.equalsIgnoreCase( BIND.value ) )
            {
                return BIND;
            }
            else if ( s.equalsIgnoreCase( UNBIND.value ) )
            {
                return UNBIND;
            }
            else if ( s.equalsIgnoreCase( ALL.value ) )
            {
                return ALL;
            }
        }

        return null;
    }
}
