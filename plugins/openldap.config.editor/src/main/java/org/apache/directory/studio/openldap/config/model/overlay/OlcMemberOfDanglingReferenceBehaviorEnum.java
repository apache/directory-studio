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
 * This enum represents the various values for the  'olcMemberOfDangling' attribute.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum OlcMemberOfDanglingReferenceBehaviorEnum
{
    /** Enum value for 'ignore' */
    IGNORE,

    /** Enum value for 'drop' */
    DROP,

    /** Enum value for 'error' */
    ERROR;

    /** The constant string for 'ignore' */
    private static final String IGNORE_STRING = "ignore";

    /** The constant string for 'drop' */
    private static final String DROP_STRING = "drop";

    /** The constant string for 'error' */
    private static final String ERROR_STRING = "error";


    /**
     * Gets the associated enum element.
     *
     * @param s the string
     * @return the associated enum element
     */
    public static OlcMemberOfDanglingReferenceBehaviorEnum fromString( String s )
    {
        if ( IGNORE_STRING.equalsIgnoreCase( s ) )
        {
            return IGNORE;
        }
        else if ( DROP_STRING.equalsIgnoreCase( s ) )
        {
            return DROP;
        }
        else if ( ERROR_STRING.equalsIgnoreCase( s ) )
        {
            return ERROR;
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
            case IGNORE:
                return IGNORE_STRING;
            case DROP:
                return DROP_STRING;
            case ERROR:
                return ERROR_STRING;
        }

        return super.toString();
    }
}
