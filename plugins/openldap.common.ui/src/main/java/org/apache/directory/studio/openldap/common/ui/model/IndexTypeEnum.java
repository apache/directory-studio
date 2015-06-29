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


import java.text.ParseException;
import java.util.regex.Pattern;


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
public enum IndexTypeEnum
{
    APPROX( "approx" ),
    EQ( "eq" ),
    NOLANG( "nolang" ),
    NOSUBTYPES( "nosubtypes" ),
    NOTAGS( "notags" ),
    PRES( "pres" ),
    SUB( "sub" ),
    SUBANY( "subany" ),
    SUBFINAL( "subfinal" ),
    SUBINITIAL( "subinitial" ),
    SUBSTR( "substr" ),
    NONE( "none" );

    /** The internal name */
    private String name;
    
    /**
     * A private constructor for this class
     */
    private IndexTypeEnum( String name )
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
     * Return an instance of IndexTypeEnum from a String
     * 
     * @param name The indexType's name
     * @return The associated IndexTypeEnum
     */
    public static IndexTypeEnum getIndexType( String name )
    {
        for ( IndexTypeEnum indexType : values() )
        {
            if ( indexType.getName().equalsIgnoreCase( name ) )
            {
                return indexType;
            }
        }
        
        return NONE;
    }
}
