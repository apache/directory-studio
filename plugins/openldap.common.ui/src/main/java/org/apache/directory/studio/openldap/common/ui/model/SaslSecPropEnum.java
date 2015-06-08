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
 * An enum for the various possible value of the olcSaslSecProps parameter.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum SaslSecPropEnum
{
    NONE( "none", false ),
    NO_PLAIN( "noplain", false ),
    NO_ACTIVE( "noactive", false ),
    NO_DICT( "nodict", false ),
    NO_ANONYMOUS( "noanonymous", false ),
    FORWARD_SEC( "forwardsec", false ),
    PASS_CRED( "passcred", false ),
    MIN_SSF( "minssf", true ),
    MAX_SSF( "maxssf", true ),
    MAX_BUF_SIZE( "maxbufsize", true ),
    UNKNOWN( "---", false);

    /** The interned name */
    private String name;
    
    /** A flag set when the property has a value */
    private boolean hasValue;
    /**
     * A private constructor for this enum
     */
    private SaslSecPropEnum( String name, boolean hasValue )
    {
        this.name = name;
        this.hasValue = hasValue;
    }

    
    public static SaslSecPropEnum getFlag( String name )
    {
        if ( NONE.name.equalsIgnoreCase( name ) )
        {
            return NONE;
        }
        
        if ( NO_PLAIN.name.equalsIgnoreCase( name ) )
        {
            return NO_PLAIN;
        }
        
        if ( NO_ACTIVE.name.equalsIgnoreCase( name ) )
        {
            return NO_ACTIVE;
        }
        
        if ( NO_DICT.name.equalsIgnoreCase( name ) )
        {
            return NO_DICT;
        }
        
        if ( NO_ANONYMOUS.name.equalsIgnoreCase( name ) )
        {
            return NO_ANONYMOUS;
        }
        
        if ( FORWARD_SEC.name.equalsIgnoreCase( name ) )
        {
            return FORWARD_SEC;
        }
        
        if ( PASS_CRED.name.equalsIgnoreCase( name ) )
        {
            return PASS_CRED;
        }
        
        if ( MIN_SSF.name.equalsIgnoreCase( name ) )
        {
            return MIN_SSF;
        }
        
        if ( MAX_SSF.name.equalsIgnoreCase( name ) )
        {
            return MAX_SSF;
        }
        
        if ( MAX_BUF_SIZE.name.equalsIgnoreCase( name ) )
        {
            return MAX_BUF_SIZE;
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
    

    /**
     * @return the hasValue flag
     */
    public boolean hasValue()
    {
        return hasValue;
    }
}
