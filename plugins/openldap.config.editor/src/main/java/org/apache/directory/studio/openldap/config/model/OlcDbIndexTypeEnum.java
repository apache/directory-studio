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
package org.apache.directory.studio.openldap.config.model;


/**
 * This enum represents the various values for the  'OlcDbIndex' attribute type value.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum OlcDbIndexTypeEnum
{
    /** Enum value for 'pres' */
    PRES,

    /** Enum value for 'eq' */
    EQ,

    /** Enum value for 'approx' */
    APPROX,

    /** Enum value for 'sub' */
    SUB,

    /** Enum value for 'subinitial' */
    SUBINITIAL,

    /** Enum value for 'subany' */
    SUBANY,

    /** Enum value for 'subfinal' */
    SUBFINAL,

    /** Enum value for 'nolang' */
    NOLANG,

    /** Enum value for 'nosubtypes' */
    NOSUBTYPES;

    /** The constant string for 'pres' */
    private static final String PRES_STRING = "pres";

    /** The constant string for 'eq' */
    private static final String EQ_STRING = "eq";

    /** The constant string for 'approx' */
    private static final String APPROX_STRING = "approx";

    /** The constant string for 'sub' */
    private static final String SUB_STRING = "sub";

    /** The constant string for 'subinitial' */
    private static final String SUBINITIAL_STRING = "subinitial";

    /** The constant string for 'subany' */
    private static final String SUBANY_STRING = "subany";

    /** The constant string for 'subfinal' */
    private static final String SUBFINAL_STRING = "subfinal";

    /** The constant string for 'nolang' */
    private static final String NOLANG_STRING = "nolang";

    /** The constant string for 'nosubtypes' */
    private static final String NOSUBTYPES_STRING = "nosubtypes";


    /**
     * Gets the associated enum element.
     *
     * @param s the string
     * @return the associated enum element
     */
    public static OlcDbIndexTypeEnum fromString( String s )
    {
        if ( PRES_STRING.equalsIgnoreCase( s ) )
        {
            return PRES;
        }
        else if ( EQ_STRING.equalsIgnoreCase( s ) )
        {
            return EQ;
        }
        else if ( APPROX_STRING.equalsIgnoreCase( s ) )
        {
            return APPROX;
        }
        else if ( SUB_STRING.equalsIgnoreCase( s ) )
        {
            return SUB;
        }
        else if ( SUBINITIAL_STRING.equalsIgnoreCase( s ) )
        {
            return SUBINITIAL;
        }
        else if ( SUBANY_STRING.equalsIgnoreCase( s ) )
        {
            return SUBANY;
        }
        else if ( SUBFINAL_STRING.equalsIgnoreCase( s ) )
        {
            return SUBFINAL;
        }
        else if ( NOLANG_STRING.equalsIgnoreCase( s ) )
        {
            return NOLANG;
        }
        else if ( NOSUBTYPES_STRING.equalsIgnoreCase( s ) )
        {
            return NOSUBTYPES;
        }

        return null;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        switch ( this )
        {
            case PRES:
                return PRES_STRING;
            case EQ:
                return EQ_STRING;
            case APPROX:
                return APPROX_STRING;
            case SUB:
                return SUB_STRING;
            case SUBINITIAL:
                return SUBINITIAL_STRING;
            case SUBANY:
                return SUBANY_STRING;
            case SUBFINAL:
                return SUBFINAL_STRING;
            case NOLANG:
                return NOLANG_STRING;
            case NOSUBTYPES:
                return NOSUBTYPES_STRING;
        }

        return super.toString();
    }
}
