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
 * An enumeration of all the possible SSF strengths.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum SsfStrengthEnum
{
    NONE( -1, "None" ),
    NO_PROTECTION( 0, "No protection" ),
    INTEGRITY_CHECK(1,  "Integrity check" ),
    DES( 56, "DES" ),
    THREE_DES( 112, "3DES" ),
    AES_128( 128, "AES-128" ),
    AES_256( 256, "AES-256" );
    
    /** The associated Text */
    private String text;
    
    /** The SSF strength position */
    private int nbBits;
    /**
     * Creates an SsfEnum instance
     */
    private SsfStrengthEnum( int nbBits, String text )
    {
        this.nbBits = nbBits;
        this.text = text;
    }

    /**
     * @return the text
     */
    public String getText()
    {
        return text;
    }

    /**
     * @return the number of bits
     */
    public int getNbBits()
    {
        return nbBits;
    }
    
    
    /**
     * Retrieve the instance associated to a String. Return NONE if not found.
     * 
     * @param feature The feature to retrieve
     * @return The SsfEnum instance found, or NONE.
     */
    public static SsfStrengthEnum getSsfStrength( int nbBits )
    {
        switch ( nbBits )
        {
            case 0 : return NO_PROTECTION;
            case 1 : return INTEGRITY_CHECK;
            case 56 : return DES;
            case 112 : return THREE_DES;
            case 128 : return AES_128;
            case 256 : return AES_256;
            default : return NONE;
        }
    }
    
    /**
     * Retrieve the instance associated to a String. Return NONE if not found.
     * 
     * @param text The text we are looking for
     * @return The SsfEnum instance found, or NONE.
     */
    public static SsfStrengthEnum getSsfStrength( String text )
    {
        for ( SsfStrengthEnum ssfStrength : values() )
        {
            if ( ssfStrength.text.equalsIgnoreCase( text ) )
            {
                return ssfStrength;
            }
        }
        
        // Default...
        return NONE;
    }
}
