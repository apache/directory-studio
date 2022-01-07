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
package org.apache.directory.studio.test.integration.ui.utils;


import java.nio.charset.StandardCharsets;
import java.util.Base64;


public class Characters
{
    public static final String LATIN_A = "A";
    public static final String LATIN_E_ACUTE = "\u00E9";
    public static final String EURO = "\u20AC";
    public static final String CYRILLIC_YA = "\u042F";
    public static final String GREEK_LAMBDA = "\u03BB";
    public static final String HEBREW_SHIN = "\u05E9";
    public static final String ARABIC_AIN = "\u0639";
    public static final String CJK_RADICAL_HEAD = "\u2EE1";
    public static final String SMILEY = "\uD83D\uDE08";

    public static final String ALL = "" +
        LATIN_A + " " +
        LATIN_E_ACUTE + " " +
        EURO + " " +
        CYRILLIC_YA + " " +
        GREEK_LAMBDA + " " +
        HEBREW_SHIN + " " +
        ARABIC_AIN + " " +
        CJK_RADICAL_HEAD + " " 
//        SMILEY + " "
        ;

    public static final String ALL_UTF8_BASE64 = toUtf8Base64( ALL );


    public static final String toUtf8Base64( String s )
    {
        return Base64.getEncoder()
            .encodeToString( s.getBytes( StandardCharsets.UTF_8 ) );
    }


    public void print()
    {
        System.out.println( LATIN_A );
        System.out.println( LATIN_E_ACUTE );
        System.out.println( EURO );
        System.out.println( CYRILLIC_YA );
        System.out.println( GREEK_LAMBDA );
        System.out.println( HEBREW_SHIN );
        System.out.println( ARABIC_AIN );
        System.out.println( CJK_RADICAL_HEAD );
        System.out.println( SMILEY );
        System.out.println( ALL );
        System.out.println( ALL_UTF8_BASE64 );
    }

}
