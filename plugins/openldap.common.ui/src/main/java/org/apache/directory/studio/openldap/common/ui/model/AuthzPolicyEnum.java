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
 * An enum for the various possible value of the olcAuthzPolicy parameter. One of
 * <ul>
 * <li>none</li>
 * <li>from</li>
 * <li>to</li>
 * <li>any</li>
 * <li>all</li>
 * <li>both</li>
 * </ul>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum AuthzPolicyEnum
{
    ALL( "all" ),
    ANY( "any" ),
    BOTH( "both" ),
    FROM( "from" ),
    NONE( "none" ),
    TO( "to" ),
    UNKNOWN( "---" );
    
    /** The interned name */
    private String name;
    
    /**
     * A private constructor for this enum
     */
    private AuthzPolicyEnum( String name )
    {
        this.name = name;
    }

    
    /**
     * Return an instance of AuthzPolicyEnum from a String
     * 
     * @param name The policy's name
     * @return The associated AuthzPolicyEnum
     */
    public static AuthzPolicyEnum getAuthzPolicy( String name )
    {
        for ( AuthzPolicyEnum authzPolicy : values() )
        {
            if ( authzPolicy.name.equalsIgnoreCase( name ) )
            {
                return authzPolicy;
            }
        }
        
        return UNKNOWN;
    }

    
    /**
     * @return An array with all the Enum value's name
     */
    public static String[] getNames()
    {
        String[] names = new String[values().length];
        int pos = 0;
    
        for ( AuthzPolicyEnum authzPolicy : values() )
        {
            names[pos] = authzPolicy.name;
            pos++;
        }
        
        return names;
    }

    
    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }
}
