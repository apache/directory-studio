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
 * An enumeration of all the possible olcLimits selector dnspec style
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum DnSpecStyleEnum
{
    EXACT( "exact" ),
    BASE( "base" ),
    ONE_LEVEL( "onelevel" ),
    SUBTREE( "subtree" ),
    CHILDREN( "children" ),
    REGEXP( "regexp" ),
    ANONYMOUS( "anonymous" ),
    NONE( "---" );
    
    /** The associated name */
    private String name;
    
    /**
     * Creates a dnspec style instance
     */
    private DnSpecStyleEnum( String name )
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
    public static DnSpecStyleEnum getSelector( String name )
    {
        for ( DnSpecStyleEnum dnSpecStyle : values() )
        {
            if ( dnSpecStyle.name.equalsIgnoreCase( name ) )
            {
                return dnSpecStyle;
            }
        }
        
        return NONE;
    }
}
