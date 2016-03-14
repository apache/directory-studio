/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.studio.openldap.config.acl.model;


/**
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum AclAttributeStyleEnum
{
    EXACT( "exact" ),
    BASE( "base" ),
    BASE_OBJECT( "baseobject" ),
    REGEX( "regex" ),
    ONE( "one" ),
    ONE_LEVEL( "onelevel" ),
    SUB( "sub" ),
    SUBTREE( "subtree" ),
    CHILDREN( "children" ),
    NONE( "---" );

    /** The interned name */
    private String name;
    
    /**
     * A private constructor
     */
    private AclAttributeStyleEnum( String name )
    {
        this.name = name;
    }

    
    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    
    /**
     * Return an instance of AclAttributeStyleEnum from a String
     * 
     * @param name The feature's name
     * @return The associated AclAttributeStyleEnum
     */
    public static AclAttributeStyleEnum getStyle( String name )
    {
        for ( AclAttributeStyleEnum style : values() )
        {
            if ( style.name.equalsIgnoreCase( name ) )
            {
                return style;
            }
        }
        
        return NONE;
    }

    
    /**
     * @return An array with all the Enum value's name
     */
    public static String[] getNames()
    {
        String[] names = new String[values().length];
        int pos = 0;
    
        for ( AclAttributeStyleEnum AclAttributeStyleEnum : values() )
        {
            names[pos] = AclAttributeStyleEnum.name;
            pos++;
        }
        
        return names;
    }
}
