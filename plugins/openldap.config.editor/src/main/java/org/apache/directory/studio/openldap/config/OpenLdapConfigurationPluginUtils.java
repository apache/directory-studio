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
package org.apache.directory.studio.openldap.config;

import java.util.List;

import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.util.Strings;

/**
 * The class is a utility class for the OpenLDAP Configuration Plugin
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OpenLdapConfigurationPluginUtils 
{
    private OpenLdapConfigurationPluginUtils()
    {
        // Nothing to do
    }
    
    
    /**
     * Strips the ordering prefix if the given string contains one.
     *
     * @param s the string
     * @return the string without the ordering prefix
     */
    public static String stripOrderingPrefix( String s )
    {
        if ( hasOrderingPrefix( s ) )
        {
            int indexOfClosingCurlyBracket = s.indexOf( '}' );

            if ( indexOfClosingCurlyBracket != -1 )
            {
                return s.substring( indexOfClosingCurlyBracket + 1 );
            }
        }

        return s;
    }

    
    /**
     * Strips the ordering postfix if the given string contains one.
     *
     * @param s the string
     * @return the string without the ordering postfix
     */
    public static String stripOrderingPostfix( String s )
    {
        if ( hasOrderingPostfix( s ) )
        {
            int indexOfOpeningCurlyBracket = s.indexOf( '{' );

            if ( indexOfOpeningCurlyBracket != -1 )
            {
                return s.substring( 0, indexOfOpeningCurlyBracket );
            }
        }

        return s;
    }


    /**
     * Indicates if the given string contains an ordering prefix.
     *
     * @param s the string
     * @return <code>true</code> if the given string contains an ordering prefix,
     *         <code>false</code> if not.
     */
    public static boolean hasOrderingPrefix( String s )
    {
        return parseOrderingPrefix( s ) != null;
    }


    /**
     * Indicates if the given string contains an ordering postfix.
     *
     * @param s the string
     * @return <code>true</code> if the given string contains an ordering postfix,
     *         <code>false</code> if not.
     */
    public static boolean hasOrderingPostfix( String s )
    {
        return parseOrderingPostfix( s ) != null;
    }


    /**
     * Fetch the prefix from a String. The prefix must respect the following regexp :
     * <pre>
     * ^\{-?\d+\}.*$
     * </pre>
     */
    private static Integer parseOrderingPrefix( String prefixString )
    {
        if ( prefixString == null )
        {
            return null;
        }

        int pos = 0;

        if ( !Strings.isCharASCII( prefixString, pos++, '{') )
        {
            return null;
        }

        boolean positive = true;
        int prefix = 0;

        if ( Strings.isCharASCII( prefixString, pos, '-') )
        {
            positive = false;
            pos++;
        }

        char car;

        while ( ( car = Strings.charAt( prefixString, pos++ ) ) != '\0' )
        {
            switch ( car )
            {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    prefix = prefix * 10 + car - '0';
                    break;

                case '}' :
                    if ( positive )
                    {
                        return prefix;
                    }
                    else
                    {
                        return -prefix;
                    }

                default :
                    return null;
            }
        }

        return null;
    }


    /**
     * Fetch the postfix from a String. The postfix must respect the following regexp :
     * <pre>
     * ^.*\{-?\d+\}$
     * </pre>
     */
    private static Integer parseOrderingPostfix( String postfixString )
    {
        if ( postfixString == null )
        {
            return null;
        }

        int pos = -1;

        for ( int idx = 0; idx < postfixString.length(); idx++ )
        { 
            if ( Strings.isCharASCII( postfixString, idx, '{') )
            {
                pos = idx + 1;
                break;
            }
        }
        
        if ( pos == -1 )
        {
            return null;
        }

        boolean positive = true;
        int prefix = 0;

        if ( Strings.isCharASCII( postfixString, pos, '-') )
        {
            positive = false;
        }

        char car;

        while ( ( car = Strings.charAt( postfixString, pos++ ) ) != '\0' )
        {
            switch ( car )
            {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    prefix = prefix * 10 + car - '0';
                    break;

                case '}' :
                    if ( positive )
                    {
                        return prefix;
                    }
                    else
                    {
                        return -prefix;
                    }

                default :
                    return null;
            }
        }

        return null;
    }


    /**
     * Gets the ordering prefix value (or -1 if none is found). The String's prefix
     * is at the beginning :
     * <pre>
     * {n}blah
     * </pre>
     *
     * @param prefixString the string
     * @return the precedence value (or -1 if none is found).
     */
    public static int getOrderingPrefix( String prefixString )
    {
        Integer orderingPrefix = parseOrderingPrefix( prefixString );

        if ( orderingPrefix == null )
        {
            return -1;
        }
        else
        {
            return orderingPrefix;
        }
    }


    /**
     * Gets the ordering postfix value (or -1 if none is found).
     *
     * @param postfixString the string
     * @return the precedence value (or -1 if none is found).
     */
    public static int getOrderingPostfix( String postfixString )
    {
        Integer orderingPostfix = parseOrderingPostfix( postfixString );

        if ( orderingPostfix == null )
        {
            return -1;
        }
        else
        {
            return orderingPostfix;
        }
    }


    /**
     * Gets the first value of the given list of values.
     *
     * @param values the list of values
     * @return the first value if it exists.
     */
    public static String getFirstValueString( List<String> values )
    {
        if ( ( values != null ) && !values.isEmpty() )
        {
            return values.get( 0 );
        }

        return null;
    }


    /**
     * Gets the first value of the given list of values.
     *
     * @param values the list of values
     * @return the first value if it exists.
     */
    public static String getFirstValueDn( List<Dn> values )
    {
        if ( ( values != null ) && !values.isEmpty() )
        {
            return values.get( 0 ).toString();
        }

        return null;
    }


    /**
     * Concatenates a list of string values.
     * Values are separated by a comma and a space (", ").
     *
     * @param list
     * @return
     */
    public static String concatenate( List<String> list )
    {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;

        for ( String string : list )
        {
            if ( isFirst )
            {
                isFirst = false;
            }
            else
            {
                sb.append( ", " );
            }

            sb.append( string );
        }

        return sb.toString();
    }
}
