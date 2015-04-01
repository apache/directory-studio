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
 * This enum represents the various values of part of a 'olcRwmMap' attribute.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum OlcRwmMapValueTypeEnum
{
    /** Enum value for 'attribute' */
    ATTRIBUTE,

    /** Enum value for 'objectclass' */
    OBJECTCLASS;

    /** The constant string for 'attribute' */
    private static final String ATTRIBUTE_STRING = "attribute";

    /** The constant string for 'objectclass' */
    private static final String OBJECTCLASS_STRING = "objectclass";


    /**
     * Gets the associated enum element.
     *
     * @param s the string
     * @return the associated enum element
     */
    public static OlcRwmMapValueTypeEnum fromString( String s )
    {
        if ( ATTRIBUTE_STRING.equalsIgnoreCase( s ) )
        {
            return ATTRIBUTE;
        }
        else if ( OBJECTCLASS_STRING.equalsIgnoreCase( s ) )
        {
            return OBJECTCLASS;
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
            case ATTRIBUTE:
                return ATTRIBUTE_STRING;
            case OBJECTCLASS:
                return OBJECTCLASS_STRING;
        }

        return super.toString();
    }
}
