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

package org.apache.directory.studio.ldifparser.parser;


public class LdifToken implements Comparable
{

    public static final int NEW = Integer.MIN_VALUE;

    public static final int ERROR = -2;

    public static final int EOF = -1;

    public static final int UNKNOWN = 0;

    public static final int COMMENT = 1;

    public static final int SEP = 2;

    public static final int VERSION_SPEC = 4;

    public static final int NUMBER = 5;

    public static final int OID = 6;

    public static final int DN_SPEC = 11;

    public static final int DN = 12;

    public static final int ATTRIBUTE = 21;

    public static final int VALUE_TYPE_SAFE = 22;

    public static final int VALUE_TYPE_BASE64 = 23;

    public static final int VALUE_TYPE_URL = 24;

    public static final int VALUE = 27;

    public static final int CHANGETYPE_SPEC = 30;

    public static final int CHANGETYPE_ADD = 31;

    public static final int CHANGETYPE_DELETE = 32;

    public static final int CHANGETYPE_MODIFY = 33;

    public static final int CHANGETYPE_MODDN = 34;

    public static final int MODTYPE_ADD_SPEC = 41;

    public static final int MODTYPE_DELETE_SPEC = 42;

    public static final int MODTYPE_REPLACE_SPEC = 43;

    public static final int MODTYPE_SEP = 45; //

    public static final int CONTROL_SPEC = 51; // control:FILL

    public static final int CONTROL_LDAPOID = 52; // 

    public static final int CONTROL_CRITICALITY_TRUE = 53; // FILLtrue

    public static final int CONTROL_CRITICALITY_FALSE = 54; // FILLfalse

    public static final int MODDN_NEWRDN_SPEC = 61;

    public static final int MODDN_DELOLDRDN_SPEC = 63;

    public static final int MODDN_NEWSUPERIOR_SPEC = 65;

    private int offset;

    private int type;

    private String value;


    public LdifToken( int type, String value, int offset )
    {
        this.type = type;
        this.value = value;
        this.offset = offset;
    }


    /**
     * Returns the start position of the token in the original ldif
     * 
     * @return the start positon of the token
     */
    public int getOffset()
    {
        return this.offset;
    }


    /**
     * Returns the length of the token in the original ldif
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
        return "(type=" + this.type + ") " + "(offset=" + this.offset + ") " + "(length=" + this.getLength() + ") '"
            + this.value + "'";
    }


    public int compareTo( Object o )
    {
        if ( o instanceof LdifToken )
        {
            LdifToken token = ( LdifToken ) o;
            return this.offset - token.offset;
        }
        else
        {
            throw new ClassCastException( "Not instanceof LdifToken: " + o.getClass().getName() );
        }
    }

}
