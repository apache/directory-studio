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
 * This enum represents the various access log operation.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum LogOperationEnum
{
    WRITES( "writes" ),
    ADD( "add" ),
    DELETE( "delete" ),
    MODIFY( "modify" ),
    MODIFY_RDN( "modrdn" ),
    READS( "reads" ),
    COMPARE( "compare" ),
    SEARCH( "search" ),
    SESSION( "session" ),
    ABANDON( "abandon" ),
    BIND( "bind" ),
    UNBIND( "unbind" ),
    ALL( "all" );

    /** The name */
    private String name;


    /**
     * Creates a new instance of LogOperation.
     *
     * @param name the name
     */
    private LogOperationEnum( String name )
    {
        this.name = name;
    }

    
    /**
     * @return the text
     */
    public String getName()
    {
        return name;
    }

    
    /**
     * @return An array with all the Enum value's name
     */
    public static String[] getNames()
    {
        String[] names = new String[values().length];
        int pos = 0;
    
        for ( LogOperationEnum logOperation : values() )
        {
            names[pos] = logOperation.name;
            pos++;
        }
        
        return names;
    }


    /**
     * Gets the log operation corresponding to the given string.
     *
     * @param s the string
     * @return the corresponding log operation
     */
    public static  LogOperationEnum fromString( String s )
    {
        if ( s != null )
        {
            if ( s.equalsIgnoreCase( WRITES.name ) )
            {
                return WRITES;
            }
            else if ( s.equalsIgnoreCase( ADD.name ) )
            {
                return ADD;
            }
            else if ( s.equalsIgnoreCase( DELETE.name ) )
            {
                return DELETE;
            }
            else if ( s.equalsIgnoreCase( MODIFY.name ) )
            {
                return MODIFY;
            }
            else if ( s.equalsIgnoreCase( MODIFY_RDN.name ) )
            {
                return MODIFY_RDN;
            }
            else if ( s.equalsIgnoreCase( READS.name ) )
            {
                return READS;
            }
            else if ( s.equalsIgnoreCase( COMPARE.name ) )
            {
                return COMPARE;
            }
            else if ( s.equalsIgnoreCase( SEARCH.name ) )
            {
                return SEARCH;
            }
            else if ( s.equalsIgnoreCase( SESSION.name ) )
            {
                return SESSION;
            }
            else if ( s.equalsIgnoreCase( ABANDON.name ) )
            {
                return ABANDON;
            }
            else if ( s.equalsIgnoreCase( BIND.name ) )
            {
                return BIND;
            }
            else if ( s.equalsIgnoreCase( UNBIND.name ) )
            {
                return UNBIND;
            }
            else if ( s.equalsIgnoreCase( ALL.name ) )
            {
                return ALL;
            }
        }

        return null;
    }
}
