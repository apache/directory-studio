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
 * This enum represents the list of Overlays
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum OverlayTypeEnum
{
    /** Access Log */
    ACCESS_LOG( "Access Log" ),

    /** Audit Log */
    AUDIT_LOG( "Audit Log" ),

    /** Member Of */
    MEMBER_OF( "Member Of" ),

    /** Password Policy */
    PASSWORD_POLICY( "Password Policy" ),

    /** Referential Integrity */
    REFERENTIAL_INTEGRITY( "Referential Integrity" ),

    /** Rewrite/Remap */
    REWRITE_REMAP( "Rewrite/Remap" ),

    /** Sync Prov (Replication) */
    SYNC_PROV( "Sync Prov (Replication)" ),
    
    /** Value Sorting */
    VALUE_SORTING( "Value Sorting" ),
    
    /** Unknown */
    UNKNOWN( "" );
    
    /** The Overlay name */
    private String name;
    
    /**
     * Create an instance of an OverlayTypeEnum
     */
    private OverlayTypeEnum( String name )
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
     * Return an instance of OverlayTypeEnum from a String
     * 
     * @param name The overlay's name
     * @return The associated OverlayTypeEnum
     */
    public static OverlayTypeEnum getOverlay( String name )
    {
        for ( OverlayTypeEnum overlay : values() )
        {
            if ( overlay.name.equalsIgnoreCase( name ) )
            {
                return overlay;
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
    
        for ( OverlayTypeEnum overlayType : values() )
        {
            names[pos] = overlayType.name;
            pos++;
        }
        
        return names;
    }
}
