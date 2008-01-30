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
package org.apache.directory.studio.schemaeditor.model.alias;


public class AliasesStringToken implements Comparable<AliasesStringToken>
{
    /** The token identifier for the start */
    public static final int START = Integer.MIN_VALUE;

    /** The token identifier for end of file */
    public static final int EOF = -1;

    /** The token identifier for a whitespace */
    public static final int WHITESPACE = 0;

    /** The token identifier for a comma ',' */
    public static final int COMMA = 1;

    /** The token identifier for an alias */
    public static final int ALIAS = 2;

    /** The token identifier for an error at the start of an alias */
    public static final int ERROR_ALIAS_START = 3;

    /** The token identifier for an error in a part (not the first character) of an alias */
    public static final int ERROR_ALIAS_PART = 4;

    /** The token identifier for the substring following an error in an alias */
    public static final int ERROR_ALIAS_SUBSTRING = 5;

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
    public AliasesStringToken( int type, String value, int offset )
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
    public int compareTo( AliasesStringToken o )
    {
        if ( o instanceof AliasesStringToken )
        {
            AliasesStringToken token = ( AliasesStringToken ) o;
            return this.offset - token.offset;
        }
        else
        {
            throw new ClassCastException( "Not instanceof AliasesToken: " + o.getClass().getName() );
        }
    }

}
