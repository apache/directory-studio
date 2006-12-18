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

package org.apache.directory.ldapstudio.browser.core.model.filter.parser;


public class LdapFilterToken implements Comparable
{

    public static final int NEW = Integer.MIN_VALUE;

    public static final int ERROR = -2;

    public static final int EOF = -1;

    public static final int UNKNOWN = 0;

    public static final int WHITESPACE = 1;

    public static final int LPAR = 11;

    public static final int RPAR = 12;

    public static final int AND = 21;

    public static final int OR = 22;

    public static final int NOT = 23;

    public static final int ATTRIBUTE = 31;

    public static final int EQUAL = 41;

    public static final int APROX = 42;

    public static final int GREATER = 43;

    public static final int LESS = 44;

    public static final int PRESENT = 45;

    public static final int VALUE = 51;

    public static final int ASTERISK = 52;

    private int offset;

    private int type;

    private String value;


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
        return this.offset;
    }


    /**
     * Returns the length of the token in the original filter
     * 
     * @return the length of the token
     */
    public int getLength()
    {
        return this.value.length();
    }


    public int getType()
    {
        return this.type;
    }


    public String getValue()
    {
        return this.value;
    }


    public String toString()
    {
        return "(" + this.offset + ") " + "(" + this.type + ") " + this.value;
    }


    public int compareTo( Object o )
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
