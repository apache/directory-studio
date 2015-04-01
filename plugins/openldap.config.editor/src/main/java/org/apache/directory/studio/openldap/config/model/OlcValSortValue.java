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


import java.text.ParseException;

import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.util.Position;
import org.apache.directory.api.util.Strings;


/**
 * This class represents 'OlcValSortOverlay' value.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OlcValSortValue
{
    /** The 'weighted' constant string */
    private static final String WEIGHTED_STRING = "weighted";

    /** The attribute */
    private String attribute;

    /** The base DN */
    private Dn baseDn;

    /** The sort method */
    private OlcValSortMethodEnum sortMethod;

    private boolean isWeighted = false;


    /**
     * Gets the attribute.
     *
     * @return the attribute
     */
    public String getAttribute()
    {
        return attribute;
    }


    /**
     * Gets the base DN.
     *
     * @return the base DN
     */
    public Dn getBaseDn()
    {
        return baseDn;
    }


    /**
     * Gets the sort method.
     *
     * @return the sort method
     */
    public OlcValSortMethodEnum getSortMethod()
    {
        return sortMethod;
    }


    /**
     * Return whether or the selected sort method is weighted.
     *
     * @return <code>true</code> if the sort method is weighted,
     *         <code>false</code> if not
     */
    public boolean isWeighted()
    {
        return isWeighted;
    }


    /**
     * Sets the attribute.
     *
     * @param attribute the attribute
     */
    public void setAttribute( String attribute )
    {
        this.attribute = attribute;
    }


    /**
     * Sets the base DN.
     *
     * @param baseDn the base DN
     */
    public void setBaseDn( Dn baseDn )
    {
        this.baseDn = baseDn;
    }


    /**
     * Sets the sort method.
     *
     * @param sortMethod the sort method
     */
    public void setSortMethod( OlcValSortMethodEnum sortMethod )
    {
        this.sortMethod = sortMethod;
    }


    /**
     * Sets whether or the selected sort method is weighted..
     *
     * @param isWeighted the value
     */
    public void setWeighted( boolean isWeighted )
    {
        this.isWeighted = isWeighted;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        // Attribute
        sb.append( attribute );
        sb.append( ' ' );

        // Base DN
        boolean baseDnNeedsEscaping = false;

        if ( baseDn != null )
        {
            baseDnNeedsEscaping = needsEscaping( baseDn.toString() );
        }

        if ( baseDnNeedsEscaping )
        {
            sb.append( '"' );
        }

        sb.append( baseDn );

        if ( baseDnNeedsEscaping )
        {
            sb.append( '"' );
        }

        sb.append( ' ' );

        // Weighted
        if ( isWeighted )
        {
            sb.append( WEIGHTED_STRING );

            // Sort method
            if ( sortMethod != null )
            {
                // Sort method
                sb.append( ' ' );
                sb.append( sortMethod );
            }
        }
        else
        {
            // Sort method
            sb.append( sortMethod );
        }

        return sb.toString();
    }


    /**
     * Indicates if the given string needs escaping.
     *
     * @param s the string
     * @return <code>true</code> if the given string needs escaping
     *         <code>false</code> if not.
     */
    private boolean needsEscaping( String s )
    {
        if ( s != null )
        {
            return s.contains( " " );
        }

        return false;
    }


    /**
     * Parses a OlcValSortValue value.
     *
     * @param s the string to be parsed
     * @return the associated OlcValSortValue object
     * @throws ParseException if there are any recognition errors (bad syntax)
     */
    public static synchronized OlcValSortValue parse( String s ) throws ParseException
    {
        if ( s == null )
        {
            return null;
        }

        // Trimming the value
        s = Strings.trim( s );

        // Getting the chars of the string
        char[] chars = s.toCharArray();

        // Creating the position
        Position pos = new Position();
        pos.start = 0;
        pos.end = 0;
        pos.length = chars.length;

        return parseInternal( chars, pos );
    }


    /**
     * Parses the given string.
     *
     * @param chars the characters
     * @param pos the position
     * @return the associated OlcValSortValue object
     * @throws ParseException
     */
    private static OlcValSortValue parseInternal( char[] chars, Position pos ) throws ParseException
    {
        OlcValSortValue olcValSortValue = new OlcValSortValue();

        // Empty (trimmed) string?
        if ( chars.length == 0 )
        {
            return null;
        }

        do
        {
            // Attribute
            String attribute = getQuotedOrNotQuotedOptionValue( chars, pos );

            if ( ( attribute != null ) && ( !attribute.isEmpty() ) )
            {
                olcValSortValue.setAttribute( attribute );
            }
            else
            {
                throw new ParseException( "Could not find the 'Attribute' value", pos.start );
            }

            // Base DN
            String baseDn = getQuotedOrNotQuotedOptionValue( chars, pos );

            if ( ( baseDn != null ) && ( !baseDn.isEmpty() ) )
            {
                try
                {
                    olcValSortValue.setBaseDn( new Dn( baseDn ) );
                }
                catch ( LdapInvalidDnException e )
                {
                    throw new ParseException( "Could not convert '" + baseDn + "' to a valid DN.", pos.start );
                }
            }
            else
            {
                throw new ParseException( "Could not find the 'Base DN value", pos.start );
            }

            // Getting the next item
            // It can either "weighted" or a sort method
            String weightedOrSortMethod = getQuotedOrNotQuotedOptionValue( chars, pos );

            if ( ( weightedOrSortMethod != null ) && ( !weightedOrSortMethod.isEmpty() ) )
            {
                // Weighted
                if ( isWeighted( weightedOrSortMethod ) )
                {
                    olcValSortValue.setWeighted( true );
                }
                // Sort Method
                else if ( isSortMethod( weightedOrSortMethod ) )
                {
                    olcValSortValue.setSortMethod( OlcValSortMethodEnum.fromString( weightedOrSortMethod ) );
                }
                else
                {
                    throw new ParseException( "Could not identify keyword '" + weightedOrSortMethod
                        + "' as a valid sort method.", pos.start );
                }
            }

            // Getting the next item
            // It should not exist if the previous item was "weighted" and
            // must a sort method it the previous item was "weighted"
            String sortMethod = getQuotedOrNotQuotedOptionValue( chars, pos );

            if ( ( sortMethod != null ) && ( !sortMethod.isEmpty() ) )
            {
                if ( olcValSortValue.isWeighted() )
                {
                    if ( isSortMethod( sortMethod ) )
                    {
                        olcValSortValue.setSortMethod( OlcValSortMethodEnum.fromString( sortMethod ) );
                    }
                    else
                    {
                        throw new ParseException( "Could not identify keyword '" + sortMethod
                            + "' as a valid sort method.", pos.start );
                    }
                }
                else
                {
                    throw new ParseException( "Keyword '" + sortMethod + "' is not allowed after sort method.",
                        pos.start );
                }
            }
        }
        while ( ( pos.start != pos.length ) && ( ( Strings.charAt( chars, pos.start ) ) != '\0' ) );

        return olcValSortValue;
    }


    /**
     * Indicates if the given string is "weighted".
     *
     * @param s the string to test
     * @return <code>true</code> if the given string is "weighted",
     *         <code>false</code> if not.
     */
    private static boolean isWeighted( String s )
    {
        return WEIGHTED_STRING.equalsIgnoreCase( s );
    }


    /**
     * Indicates if the given string is one of the sort methods.
     *
     * @param s the string to test
     * @return <code>true</code> if the given string is one of the sort methods,
     *         <code>false</code> if not.
     */
    private static boolean isSortMethod( String s )
    {
        return ( OlcValSortMethodEnum.fromString( s ) != null );
    }


    private static String getQuotedOrNotQuotedOptionValue( char[] chars, Position pos ) throws ParseException
    {
        if ( pos.start != pos.length )
        {
            char quoteChar = '\0';
            boolean isInQuotes = false;
            char c = Strings.charAt( chars, pos.start );
            char[] v = new char[chars.length - pos.start];
            int current = 0;

            do
            {
                if ( ( current == 0 ) && !isInQuotes )
                {
                    // Whitespace
                    if ( Character.isWhitespace( c ) )
                    {
                        // We ignore all whitespaces until we find the start of the value
                        pos.start++;
                        continue;
                    }
                    // Double quotes (") or single quotes (')
                    else if ( ( c == '"' ) || ( c == '\'' ) )
                    {
                        isInQuotes = true;
                        quoteChar = c;
                        pos.start++;
                        continue;
                    }
                    // Any other char is part of a value
                    else
                    {
                        v[current++] = c;
                        pos.start++;
                    }
                }
                else
                {
                    if ( isInQuotes )
                    {
                        // Double quotes (") or single quotes (')
                        if ( quoteChar == c )
                        {
                            isInQuotes = false;
                            pos.start++;
                            continue;
                        }
                        // Checking for escaped quotes
                        else if ( c == '\\' )
                        {
                            // Double quotes (")
                            if ( ( quoteChar == '"' ) && ( Strings.areEquals( chars, pos.start, "\\\"" ) >= 0 ) )
                            {
                                v[current++] = '"';
                                pos.start += 2;
                                continue;
                            }
                            // Single quotes (')
                            else if ( ( quoteChar == '\'' ) && ( Strings.areEquals( chars, pos.start, "\\'" ) >= 0 ) )
                            {
                                v[current++] = '\'';
                                pos.start += 2;
                                continue;
                            }
                        }
                        // Any other char is part of a value
                        else
                        {
                            v[current++] = c;
                            pos.start++;
                        }
                    }
                    else
                    {
                        // Whitespace
                        if ( Character.isWhitespace( c ) )
                        {
                            // Once we have found the start of the value, the first whitespace is the exit
                            break;
                        }
                        // Any other char is part of a value
                        else
                        {
                            v[current++] = c;
                            pos.start++;
                        }
                    }
                }
            }
            while ( ( c = Strings.charAt( chars, pos.start ) ) != '\0' );

            // Checking the resulting value
            if ( current == 0 )
            {
                throw new ParseException( "Couldn't find a value.", pos.start );
            }

            char[] value = new char[current];
            System.arraycopy( v, 0, value, 0, current );

            // Getting the value as a String
            return new String( value );
        }

        return null;
    }
}
