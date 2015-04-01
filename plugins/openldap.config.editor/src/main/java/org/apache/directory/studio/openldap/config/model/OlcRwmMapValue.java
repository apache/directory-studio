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

import org.apache.directory.api.util.Position;
import org.apache.directory.api.util.Strings;


/**
 * This class represents 'olcRwmMap' value.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OlcRwmMapValue
{
    /** The constant string for '*' */
    private static final String STAR_STRING = "*";

    /** The type */
    private OlcRwmMapValueTypeEnum type;

    /** The local name */
    private String localName;

    /** The foreign name */
    private String foreignName;


    /**
     * Gets the type.
     *
     * @return the type
     */
    public OlcRwmMapValueTypeEnum getType()
    {
        return type;
    }


    /**
     * Gets the local name.
     *
     * @return the local name
     */
    public String getLocalName()
    {
        return localName;
    }


    /**
     * Gets the foreign name.
     *
     * @return the foreign name
     */
    public String getForeignName()
    {
        return foreignName;
    }


    /**
     * Indicates if the local name is the '*' constant.
     *
     * @return <code>true</code> if the local name is the '*' constant,
     *         <code>false</code> if not.
     */
    public boolean isLocalNameStart()
    {
        return STAR_STRING.equals( localName );
    }


    /**
     * Indicates if the foreign name is the '*' constant.
     *
     * @return <code>true</code> if the foreign name is the '*' constant,
     *         <code>false</code> if not.
     */
    public boolean isLocalForeignStart()
    {
        return STAR_STRING.equals( foreignName );
    }


    /**
     * Sets the type.
     *
     * @param type the type
     */
    public void setType( OlcRwmMapValueTypeEnum type )
    {
        this.type = type;
    }


    /**
     * Sets the local name.
     *
     * @param localName the local name
     */
    public void setLocalName( String localName )
    {
        this.localName = localName;
    }


    /**
     * Sets the foreign name.
     *
     * @param foreignName the foreign name
     */
    public void setForeignName( String foreignName )
    {
        this.foreignName = foreignName;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        // Type
        sb.append( type );

        // Local Name
        if ( ( localName != null ) && ( localName.length() > 0 ) )
        {
            sb.append( ' ' );
            sb.append( localName );
        }

        // Foreign Name
        if ( ( foreignName != null ) && ( foreignName.length() > 0 ) )
        {
            sb.append( ' ' );
            sb.append( foreignName );
        }

        return sb.toString();
    }


    /**
     * Parses a OlcValSortValue value.
     * 
     * @param s
     *            the string to be parsed
     * @return the associated OlcValSortValue object
     * @throws ParseException
     *             if there are any recognition errors (bad syntax)
     */
    public static synchronized OlcRwmMapValue parse( String s ) throws ParseException
    {
        if ( s == null )
        {
            return null;
        }

        // Trimming the value
        s = Strings.trim( s );

        // Getting the chars of the string
        char[] chars = new char[s.length()];
        s.getChars( 0, s.length(), chars, 0 );

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
    private static OlcRwmMapValue parseInternal( char[] chars, Position pos ) throws ParseException
    {
        OlcRwmMapValue value = new OlcRwmMapValue();

        // Empty (trimmed) string?
        if ( chars.length == 0 )
        {
            return null;
        }

        do
        {
            // Type
            String typeString = getQuotedOrNotQuotedOptionValue( chars, pos );

            if ( ( typeString != null ) && ( !typeString.isEmpty() ) )
            {
                OlcRwmMapValueTypeEnum type = OlcRwmMapValueTypeEnum.fromString( typeString );

                if ( type != null )
                {
                    value.setType( type );
                }
                else
                {
                    throw new ParseException( "Could not identify keyword '" + typeString
                        + "' as a valid type.", pos.start );
                }
            }
            else
            {
                throw new ParseException( "Could not find the 'type' value", pos.start );
            }

            // First Name
            String firstName = getQuotedOrNotQuotedOptionValue( chars, pos );

            if ( ( firstName == null ) || ( firstName.isEmpty() ) )
            {
                throw new ParseException( "Could not find any 'localName' or 'foreignName' value", pos.start );
            }

            // Second Name
            String secondName = getQuotedOrNotQuotedOptionValue( chars, pos );

            if ( ( secondName == null ) || ( secondName.isEmpty() ) )
            {
                // Local Name is optional
                // If we got only one name, it's the foreign name
                value.setForeignName( firstName );
            }
            else
            {
                value.setLocalName( firstName );
                value.setForeignName( secondName );
            }
        }
        while ( ( pos.start != pos.length ) && ( ( Strings.charAt( chars, pos.start ) ) != '\0' ) );

        return value;
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