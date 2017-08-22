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
package org.apache.directory.studio.openldap.config.model.database;


/**
 * This enum represents the various values for the  'olcDbLockDetect' attribute.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum OlcBdbConfigLockDetectEnum
{
    /** Enum value for 'oldest' */
    OLDEST,

    /** Enum value for 'youngest' */
    YOUNGEST,

    /** Enum value for 'fewest' */
    FEWEST,

    /** Enum value for 'random' */
    RANDOM,

    /** Enum value for 'default' */
    DEFAULT;

    /** The constant string for 'oldest' */
    private static final String OLDEST_STRING = "oldest";

    /** The constant string for 'youngest' */
    private static final String YOUNGEST_STRING = "youngest";

    /** The constant string for 'fewest' */
    private static final String FEWEST_STRING = "fewest";

    /** The constant string for 'random' */
    private static final String RANDOM_STRING = "random";

    /** The constant string for 'default' */
    private static final String DEFAULT_STRING = "default";


    /**
     * Gets the associated enum element.
     *
     * @param s the string
     * @return the associated enum element
     */
    public static OlcBdbConfigLockDetectEnum fromString( String s )
    {
        if ( OLDEST_STRING.equalsIgnoreCase( s ) )
        {
            return OLDEST;
        }
        else if ( YOUNGEST_STRING.equalsIgnoreCase( s ) )
        {
            return YOUNGEST;
        }
        else if ( FEWEST_STRING.equalsIgnoreCase( s ) )
        {
            return FEWEST;
        }
        else if ( RANDOM_STRING.equalsIgnoreCase( s ) )
        {
            return RANDOM;
        }
        else if ( DEFAULT_STRING.equalsIgnoreCase( s ) )
        {
            return DEFAULT;
        }

        return null;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        switch ( this )
        {
            case OLDEST:
                return OLDEST_STRING;
            case YOUNGEST:
                return YOUNGEST_STRING;
            case FEWEST:
                return FEWEST_STRING;
            case RANDOM:
                return RANDOM_STRING;
            case DEFAULT:
                return DEFAULT_STRING;
        }

        return super.toString();
    }
}
