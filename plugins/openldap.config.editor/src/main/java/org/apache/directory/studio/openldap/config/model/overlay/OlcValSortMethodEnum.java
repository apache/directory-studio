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
package org.apache.directory.studio.openldap.config.model.overlay;


/**
 * This enum represents the various values for the  'OlcValSortOverlay' sort method.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum OlcValSortMethodEnum
{
    /** Enum value for 'alpha-ascend' */
    ALPHA_ASCEND,

    /** Enum value for 'alpha-descend' */
    ALPHA_DESCEND,

    /** Enum value for 'numeric-ascend' */
    NUMERIC_ASCEND,

    /** Enum value for 'numeric-descend' */
    NUMERIC_DESCEND;

    /** The constant string for 'alpha-ascend' */
    private static final String ALPHA_ASCEND_STRING = "alpha-ascend";

    /** The constant string for 'alpha-descend' */
    private static final String ALPHA_DESCEND_STRING = "alpha-descend";

    /** The constant string for 'numeric-ascend' */
    private static final String NUMERIC_ASCEND_STRING = "numeric-ascend";

    /** The constant string for 'numeric-descend' */
    private static final String NUMERIC_DESCEND_STRING = "numeric-descend";


    /**
     * Gets the associated enum element.
     *
     * @param s the string
     * @return the associated enum element
     */
    public static OlcValSortMethodEnum fromString( String s )
    {
        if ( ALPHA_ASCEND_STRING.equalsIgnoreCase( s ) )
        {
            return ALPHA_ASCEND;
        }
        else if ( ALPHA_DESCEND_STRING.equalsIgnoreCase( s ) )
        {
            return ALPHA_DESCEND;
        }
        else if ( NUMERIC_ASCEND_STRING.equalsIgnoreCase( s ) )
        {
            return NUMERIC_ASCEND;
        }
        else if ( NUMERIC_DESCEND_STRING.equalsIgnoreCase( s ) )
        {
            return NUMERIC_DESCEND;
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
            case ALPHA_ASCEND:
                return ALPHA_ASCEND_STRING;
            case ALPHA_DESCEND:
                return ALPHA_DESCEND_STRING;
            case NUMERIC_ASCEND:
                return NUMERIC_ASCEND_STRING;
            case NUMERIC_DESCEND:
                return NUMERIC_DESCEND_STRING;
        }

        return super.toString();
    }
}
