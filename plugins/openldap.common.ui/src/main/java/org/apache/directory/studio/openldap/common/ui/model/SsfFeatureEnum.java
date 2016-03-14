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
 * An enumeration of all the possible SSF features.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum SsfFeatureEnum
{
    SSF( "ssf" ),
    TRANSPORT( "transport" ),
    TLS( "tls" ),
    SASL( "sasl" ),
    UPDATE_SSF( "update_ssf" ),
    UPDATE_TRANSPORT( "update_transport" ),
    UPDATE_TLS( "update_tls" ),
    UPDATE_SASL( "update_sasl" ),
    SIMPLE_BIND( "simple_bind" ),
    NONE( "---" );
    
    /** The associated name */
    private String name;
    
    /**
     * Creates an SsfEnum instance
     */
    private SsfFeatureEnum( String name )
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
    
        for ( SsfFeatureEnum ssfFeature : values() )
        {
            names[pos] = ssfFeature.name;
            pos++;
        }
        
        return names;
    }

    
    /**
     * Retrieve the instance associated to a String. Return NONE if not found.
     * 
     * @param name The namr to retrieve
     * @return The SsfEnum instance found, or NONE.
     */
    public static SsfFeatureEnum getSsfFeature( String name )
    {
        for ( SsfFeatureEnum ssfFeature : values() )
        {
            if ( ssfFeature.name.equalsIgnoreCase( name ) )
            {
                return ssfFeature;
            }
        }
        
        return NONE;
    }
}
