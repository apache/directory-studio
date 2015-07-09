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
 * An enumeration of all the possible olcLimits selectors
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum LimitSelectorEnum
{
    ANONYMOUS( "anonymous" ),
    USERS( "users" ),
    DNSPEC( "dnspec" ),
    GROUP( "group" ),
    GROUP_OC( "group/oc" ),
    GROUP_OC_AC( "group/oc/ac" ),
    NONE( "---" );
    
    /** The associated name */
    private String name;
    
    /**
     * Creates a selector instance
     */
    private LimitSelectorEnum( String name )
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
     * @return The LimitSelectorEnum instance found, or NONE.
     */
    public static LimitSelectorEnum getSelector( String name )
    {
        for ( LimitSelectorEnum selector : values() )
        {
            if ( selector.name.equalsIgnoreCase( name ) )
            {
                return selector;
            }
        }
        
        return NONE;
    }
}
