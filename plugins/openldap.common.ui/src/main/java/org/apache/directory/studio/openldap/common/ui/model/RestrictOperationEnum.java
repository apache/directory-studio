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
 * An enum for the various possible value of the olcRestrict parameter. Some of
 * <ul>
 * <li>add</li>
 * <li>all</li>
 * <li>bind</li>
 * <li>compare</li>
 * <li>delete</li>
 * <li>extended</li>
 * <li>extended=1.3.6.1.4.1.1466.20037</li>
 * <li>extended=1.3.6.1.4.1.4203.1.11.1</li>
 * <li>extended=1.3.6.1.4.1.4203.1.11.3</li>
 * <li>extended=1.3.6.1.1.8</li>
 * <li>modify</li>
 * <li>modrdn</li>
 * <li>read</li>
 * <li>rename</li>
 * <li>search</li>
 * <li>write</li>
 * </ul>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum RestrictOperationEnum
{
    UNKNOWN( "---", "" ),
    ADD( "add", "add" ),
    ALL( "all", "all" ),
    BIND( "bind", "bind" ),
    COMPARE( "compare", "compare" ),
    DELETE( "delete", "delete" ),
    EXTENDED( "extended", "extended" ),
    EXTENDED_START_TLS( "extended=1.3.6.1.4.1.1466.20037", "START_TLS" ),
    EXTENDED_MODIFY_PASSWD( "extended=1.3.6.1.4.1.4203.1.11.1", "MODIFY_PASSWORD" ),
    EXTENDED_WHOAMI( "extended=1.3.6.1.4.1.4203.1.11.3", "WHOAMI" ),
    EXTENDED_CANCEL( "extended=1.3.6.1.1.8", "CANCEL" ),
    MODIFY( "modify", "modify" ),
    MODRDN( "modrdn", "modrdn" ),
    READ( "read", "read" ),
    RENAME( "rename", "rename" ),
    SEARCH( "search", "search" ),
    WRITE( "write", "write" );
    
    /** The interned name */
    private String name;
    
    /** The externalized name */
    private String externalName;
    
    /**
     * A private constructor for this enum
     */
    private RestrictOperationEnum( String name, String externalName )
    {
        this.name = name;
        this.externalName = externalName;
    }
    
    
    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }
    
    
    /**
     * @return the external name
     */
    public String getExternalName()
    {
        return externalName;
    }
    
    
    /**
     * Get the RestrictOperationEnum instance from its number
     * 
     * @param number The number we are looking for
     * @return The associated RestrictOperationEnum instance
     */
    public static RestrictOperationEnum getOperation( int number )
    {
        RestrictOperationEnum[] values = RestrictOperationEnum.values();
        
        if ( ( number > 0 ) && ( number < values.length ) )
        {
            return values[number];
        }
        else
        {
            return UNKNOWN;
        }
    }

    
    /**
     * Return an instance of RestrictOperationEnum from a String
     * 
     * @param name The operation's name
     * @return The associated RestrictOperationEnum
     */
    public static RestrictOperationEnum getOperation( String name )
    {
        if ( ADD.name.equalsIgnoreCase( name ) )
        {
            return ADD;
        }
        
        if ( ALL.name.equalsIgnoreCase( name ) )
        {
            return ALL;
        }
        
        if ( BIND.name.equalsIgnoreCase( name ) )
        {
            return BIND;
        }
        
        if ( COMPARE.name.equalsIgnoreCase( name ) )
        {
            return COMPARE;
        }
        
        if ( DELETE.name.equalsIgnoreCase( name ) )
        {
            return DELETE;
        }
        
        if ( EXTENDED.name.equalsIgnoreCase( name ) )
        {
            return EXTENDED;
        }
        
        if ( EXTENDED_CANCEL.name.equalsIgnoreCase( name ) )
        {
            return EXTENDED_CANCEL;
        }
        
        if ( EXTENDED_MODIFY_PASSWD.name.equalsIgnoreCase( name ) )
        {
            return EXTENDED_MODIFY_PASSWD;
        }
        
        if ( EXTENDED_START_TLS.name.equalsIgnoreCase( name ) )
        {
            return EXTENDED_START_TLS;
        }
        
        if ( EXTENDED_WHOAMI.name.equalsIgnoreCase( name ) )
        {
            return EXTENDED_WHOAMI;
        }
        
        if ( MODIFY.name.equalsIgnoreCase( name ) )
        {
            return MODIFY;
        }
        
        if ( MODRDN.name.equalsIgnoreCase( name ) )
        {
            return MODRDN;
        }
        
        if ( READ.name.equalsIgnoreCase( name ) )
        {
            return READ;
        }
        
        if ( RENAME.name.equalsIgnoreCase( name ) )
        {
            return RENAME;
        }
        
        if ( SEARCH.name.equalsIgnoreCase( name ) )
        {
            return SEARCH;
        }
        
        if ( WRITE.name.equalsIgnoreCase( name ) )
        {
            return WRITE;
        }
        
        return UNKNOWN;
    }
    
    
    /**
     * @see Object#toString()
     */
    public String toString()
    {
        return externalName;
    }
}
