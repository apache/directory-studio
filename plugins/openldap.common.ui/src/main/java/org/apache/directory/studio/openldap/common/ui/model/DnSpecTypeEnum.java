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
 * An enumeration of all the possible olcLimits selector dnspec type
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum DnSpecTypeEnum
{
    SELF( "self" ),
    THIS( "this" ),
    NONE( "---" );
    
    /** The associated name */
    private String name;
    
    /**
     * Creates a dnspec type instance
     */
    private DnSpecTypeEnum( String name )
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
     * Retrieve the instance associated to a String. Return NONE if not found.
     * 
     * @param name The name to retrieve
     * @return The DnSpecTypeEnum instance found, or NONE.
     */
    public static DnSpecTypeEnum getType( String name )
    {
        for ( DnSpecTypeEnum dnSpecType : values() )
        {
            if ( dnSpecType.name.equalsIgnoreCase( name ) )
            {
                return dnSpecType;
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
    
        for ( DnSpecTypeEnum dnSpecType : values() )
        {
            names[pos] = dnSpecType.name;
            pos++;
        }
        
        return names;
    }
}
