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
 * An enum for the various possible value of the olcRequires parameter. One of
 * <ul>
 * <li>bind</li>
 * <li>authc</li>
 * <li>LDAPv3</li>
 * <li>sasl</li>
 * <li>strong</li>
 * <li>none</li>
 * </ul>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum RequireConditionEnum
{
    BIND( "bind" ),
    AUTHC( "authc" ),
    LDAP_V3( "LDAPv3" ),
    SASL( "sasl" ),
    STRONG( "strong" ),
    NONE( "none" ),
    UNKNOWN( "---" );
    
    /** The interned name */
    private String name;
    
    /**
     * A private constructor for this enum
     */
    private RequireConditionEnum( String name )
    {
        this.name = name;
    }

    
    /**
     * Return an instance of RequireConditionEnum from a String
     * 
     * @param name The feature's name
     * @return The associated RequireConditionEnum
     */
    public static RequireConditionEnum getFeature( String name )
    {
        if ( BIND.name.equalsIgnoreCase( name ) )
        {
            return BIND;
        }
        
        if ( AUTHC.name.equalsIgnoreCase( name ) )
        {
            return AUTHC;
        }
        
        if ( LDAP_V3.name.equalsIgnoreCase( name ) )
        {
            return LDAP_V3;
        }
        
        if ( SASL.name.equalsIgnoreCase( name ) )
        {
            return SASL;
        }
        
        if ( STRONG.name.equalsIgnoreCase( name ) )
        {
            return STRONG;
        }
        
        if ( NONE.name.equalsIgnoreCase( name ) )
        {
            return NONE;
        }
        
        return UNKNOWN;
    }
    
    
    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }
}
