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
 * An enum for the various possible value of the olcDisallows parameter. One of
 * <ul>
 * <li>bind_anon</li>
 * <li>bind_simple</li>
 * <li>tls_2_anon</li>
 * <li>tls_authc</li>
 * <li>proxy_authz_non_critical</li>
 * <li>dontusecopy_non_critical</li>
 * </ul>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum DisallowFeatureEnum
{
    UNKNOWN( "---" ),
    BIND_ANON( "bind_anon" ),
    BIND_SIMPLE( "bind_simple" ),
    TLS_2_ANON( "tls_2_anon" ),
    TLS_AUTHC( "tls_authc" ),
    PROXY_AUTHZ_NON_CRITICAL( "proxy_authz_non_critical" ),
    DONTUSECOPY_NON_CRITICAL( "dontusecopy_non_critical" );
    
    /** The interned name */
    private String name;
    
    /**
     * A private constructor for this enum
     */
    private DisallowFeatureEnum( String name )
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
     * @return An array with all the Enum value's name
     */
    public static String[] getNames()
    {
        String[] names = new String[values().length];
        int pos = 0;
    
        for ( DisallowFeatureEnum disallowFeature : values() )
        {
            names[pos] = disallowFeature.name;
            pos++;
        }
        
        return names;
    }

    
    /**
     * Get the DisallowFeatureEnumd instance from its number
     * 
     * @param number The number we are looking for
     * @return The associated DisallowFeatureEnum instance
     */
    public static DisallowFeatureEnum getFeature( int number )
    {
        DisallowFeatureEnum[] values = DisallowFeatureEnum.values();
        
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
     * Return an instance of DisallowFeatureEnum from a String
     * 
     * @param name The feature's name
     * @return The associated DisallowFeatureEnum
     */
    public static DisallowFeatureEnum getFeature( String name )
    {
        for ( DisallowFeatureEnum disallowFeature : values() )
        {
            if ( disallowFeature.name.equalsIgnoreCase( name ) )
            {
                return disallowFeature;
            }
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
