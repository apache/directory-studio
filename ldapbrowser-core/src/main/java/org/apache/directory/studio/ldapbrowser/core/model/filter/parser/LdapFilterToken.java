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

package org.apache.directory.studio.ldapbrowser.core.model.filter.parser;


/**
 * The LdapFilterToken is used to exchange tokens from the scanner to the parser.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class LdapFilterToken implements Comparable<LdapFilterToken>
{

    /** The token identifier for a new filter */
    public static final int NEW = Integer.MIN_VALUE;

    /** The token identifier for an error token */
    public static final int ERROR = -2;

    /** The token identifier for end of file */
    public static final int EOF = -1;

    /** The token identifier for an unknown token */
    public static final int UNKNOWN = 0;

    /** The token identifier for a whitespace */
    public static final int WHITESPACE = 1;

    /** The token identifier for the left parenthesis ( */
    public static final int LPAR = 11;

    /** The token identifier for the right parenthesis ) */
    public static final int RPAR = 12;

    /** The token identifier for the and operator & */
    public static final int AND = 21;

    /** The token identifier for the or operator | */
    public static final int OR = 22;

    /** The token identifier for the not operator ! */
    public static final int NOT = 23;

    /** The token identifier for the attribute = */
    public static final int ATTRIBUTE = 31;

    /** The token identifier for the equal filter type = */
    public static final int EQUAL = 41;

    /** The token identifier for the approx filter type ~= */
    public static final int APROX = 42;

    /** The token identifier for the greater or equal filter type >= */
    public static final int GREATER = 43;

    /** The token identifier for the less or equal filter type <= */
    public static final int LESS = 44;

    /** The token identifier for the present filter type =* */
    public static final int PRESENT = 45;
    
    /** The token identifier for the substring filter type =* */
    public static final int SUBSTRING = 46;

    /** The token identifier for a value. */
    public static final int VALUE = 51;

    /** The token identifier for the asterisk. */
    public static final int ASTERISK = 52;

    /** The token identifier for the attribute type in extensible filters. */
    public static final int EXTENSIBLE_ATTRIBUTE = 61;

    /** The token identifier for the colon before the DN flag in extensible filters. */
    public static final int EXTENSIBLE_DNATTR_COLON = 62;

    /** The token identifier for the DN flag in extensible filters. */
    public static final int EXTENSIBLE_DNATTR = 63;

    /** The token identifier for the colon before the matching rule OID in extensible filters. */
    public static final int EXTENSIBLE_MATCHINGRULEOID_COLON = 64;

    /** The token identifier for the matching rule OID in extensible filters. */
    public static final int EXTENSIBLE_MATCHINGRULEOID = 65;

    /** The token identifier for the colon before the equals in extensible filters. */
    public static final int EXTENSIBLE_EQUALS_COLON = 66;

    /** The offset. */
    private int offset;

    /** The type. */
    private int type;

    /** The value. */
    private String value;


    /**
     * Creates a new instance of LdapFilterToken.
     * 
     * @param type the type
     * @param value the value
     * @param offset the offset
     */
    public LdapFilterToken( int type, String value, int offset )
    {
        this.type = type;
        this.value = value;
        this.offset = offset;
    }


    /**
     * Returns the start position of the token in the original filter
     * 
     * @return the start positon of the token
     */
    public int getOffset()
    {
        return offset;
    }


    /**
     * Returns the length of the token in the original filter
     * 
     * @return the length of the token
     */
    public int getLength()
    {
        return value.length();
    }


    /**
     * Gets the token type.
     * 
     * @return the token type
     */
    public int getType()
    {
        return type;
    }


    /**
     * Gets the value of the token in the original filter.
     * 
     * @return the value of the token
     */
    public String getValue()
    {
        return value;
    }


    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "(" + offset + ") " + "(" + type + ") " + value;
    }


    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo( LdapFilterToken o )
    {
        if ( o instanceof LdapFilterToken )
        {
            LdapFilterToken token = ( LdapFilterToken ) o;
            return this.offset - token.offset;
        }
        else
        {
            throw new ClassCastException( "Not instanceof LapFilterToken: " + o.getClass().getName() );
        }
    }

}
