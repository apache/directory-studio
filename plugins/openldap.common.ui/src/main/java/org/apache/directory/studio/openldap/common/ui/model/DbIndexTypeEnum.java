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
 * The class defines the different kind of index that can be used :
 * <ul>
 * <li>approx</li>
 * <li>eq</li>
 * <li>nolang</li>
 * <li>nosubtypes</li>
 * <li>notags</li>
 * <li>pres</li>
 * <li>sub</li>
 * <li>subany</li>
 * <li>subfinal</li>
 * <li>subinitial</li>
 * <li>substr</li>
 * </ul> 
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum DbIndexTypeEnum
{
    APPROX( 0, "approx" ),
    EQ( 1, "eq" ),
    NOLANG( 2, "nolang" ),
    NOSUBTYPES( 3, "nosubtypes" ),
    NOTAGS( 4, "notags" ),
    PRES( 5, "pres" ),
    SUB( 6, "sub" ),
    SUBANY( 7, "subany" ),
    SUBFINAL( 8, "subfinal" ),
    SUBINITIAL( 9, "subinitial" ),
    SUBSTR( 10, "substr" ),  // Same as SUB
    NONE( 11, "none" );

    /** The internal name */
    private String name;
    
    /** The internal number */
    private int number;
    
    /**
     * A private constructor for this class
     */
    private DbIndexTypeEnum( int number, String name )
    {
        this.name = name;
        this.number = number;
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
    
        for ( DbIndexTypeEnum dbIndexType : values() )
        {
            names[pos] = dbIndexType.name;
            pos++;
        }
        
        return names;
    }

    
    /**
     * @return the number
     */
    public int getNumber()
    {
        return number;
    }

    
    /**
     * Return an instance of DbIndexTypeEnum from a String
     * 
     * @param name The indexType's name
     * @return The associated DbIndexTypeEnum
     */
    public static DbIndexTypeEnum getIndexType( String name )
    {
        for ( DbIndexTypeEnum indexType : values() )
        {
            if ( indexType.getName().equalsIgnoreCase( name ) )
            {
                if ( SUBSTR.getName().equalsIgnoreCase( name ) )
                {
                    // SUB and SUBSTR are the same. Return SUB
                    return SUB;
                }
                else
                {
                    return indexType;
                }
            }
        }
        
        return NONE;
    }
    
    
    /**
     * Get the DbIndexTypeEnum instance from its number
     * 
     * @param number The number we are looking for
     * @return The associated DbIndexTypeEnum instance
     */
    public static DbIndexTypeEnum getIndexType( int number )
    {
        switch ( number )
        {
            case 0 : return APPROX;
            case 1 : return EQ;
            case 2 : return NOLANG;
            case 3 : return NOSUBTYPES;
            case 4 : return NOTAGS;
            case 5 : return PRES;
            case 6 : return SUB;
            case 7 : return SUBANY;
            case 8 : return SUBFINAL;
            case 9 : return SUBINITIAL;
            case 10 : return SUBSTR;
            default : return NONE;
        }
    }
}
