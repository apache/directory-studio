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
 * An enum for the various possible value of the olcAllows parameter. Some of
 * <ul>
 * <li>bind_v2</li>
 * <li>bind_anon_cred</li>
 * <li>bind_anon_dn</li>
 * <li>update_anon</li>
 * <li>proxy_authz_anon</li>
 * </ul>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum AllowFeatureEnum
{
    UNKNOWN( "---" ),
    BIND_ANON_CRED( "bind_anon_cred" ),
    BIND_ANON_DN( "bind_anon_dn" ),
    BIND_V2( "bind_v2" ),
    PROXY_AUTHZ_ANON( "proxy_authz_anon" ),
    UPDATE_ANON( "update_anon" );
    
    /** The interned name */
    private String name;
    
    /**
     * A private constructor for this enum
     */
    private AllowFeatureEnum( String name )
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
     * Get the AllowFeatureEnumd instance from its number
     * 
     * @param number The number we are looking for
     * @return The associated AllowFeatureEnum instance
     */
    public static AllowFeatureEnum getFeature( int number )
    {
        AllowFeatureEnum[] values = AllowFeatureEnum.values();
        
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
     * Return an instance of AllowFeatureEnum from a String
     * 
     * @param name The feature's name
     * @return The associated AllowFeatureEnum
     */
    public static AllowFeatureEnum getFeature( String name )
    {
        if ( BIND_V2.name.equalsIgnoreCase( name ) )
        {
            return BIND_V2;
        }
        
        if ( BIND_ANON_CRED.name.equalsIgnoreCase( name ) )
        {
            return BIND_ANON_CRED;
        }
        
        if ( BIND_ANON_DN.name.equalsIgnoreCase( name ) )
        {
            return BIND_ANON_DN;
        }
        
        if ( UPDATE_ANON.name.equalsIgnoreCase( name ) )
        {
            return UPDATE_ANON;
        }
        
        if ( PROXY_AUTHZ_ANON.name.equalsIgnoreCase( name ) )
        {
            return PROXY_AUTHZ_ANON;
        }
        
        return UNKNOWN;
    }
    
    
    /**
     * @see Object#toString()
     */
    public String toString()
    {
        return name;
    }
}
