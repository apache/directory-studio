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


public class AliasesStringScanner
{
    private static final char CHAR_COMMA = ',';
    private static final char CHAR_EOF = '\u0000';

    /** The aliases to scan */
    private String aliases;

    /** The current position */
    private int pos;

    /** The last token type. */
    private int lastTokenType;


    /**
     * Creates a new instance of LdapFilterScanner.
     */
    public AliasesStringScanner()
    {
        super();
        aliases = "";
    }


    /**
     * Resets this scanner.
     * 
     * @param aliases the new aliases to scan
     */
    public void reset( String aliases )
    {
        this.aliases = aliases;
        pos = -1;
        lastTokenType = AliasesStringToken.START;
    }


    /**
     * Gets the character at the current position.
     * 
     * @return the character at the current position
     */
    private char currentChar()
    {
        return 0 <= pos && pos < aliases.length() ? aliases.charAt( pos ) : CHAR_EOF;
    }


    /**
     * Increments the position counter and gets
     * the character at that positon.
     * 
     * @return the character at the next position
     */
    private char nextChar()
    {
        pos++;
        return currentChar();
    }


    /**
     * Decrements the position counter and gets
     * the character at that positon.
     * 
     * @return the character at the previous position
     */
    private char prevChar()
    {
        pos--;
        return currentChar();
    }


    /**
     * Gets the next token.
     * 
     * @return the next token
     */
    public AliasesStringToken nextToken()
    {
        char c;

        // check for EOF
        c = nextChar();
        if ( c == CHAR_EOF )
        {
            lastTokenType = AliasesStringToken.EOF;
            return new AliasesStringToken( lastTokenType, "", pos );
        }
        prevChar();

        // check the substring if there was an error
        c = nextChar();
        if ( lastTokenType == AliasesStringToken.ERROR_ALIAS_PART
            || lastTokenType == AliasesStringToken.ERROR_ALIAS_START )
        {
            StringBuffer sb = new StringBuffer();
            while ( c != CHAR_COMMA && c != CHAR_EOF )
            {
                sb.append( c );
                c = nextChar();
            }

            lastTokenType = AliasesStringToken.ERROR_ALIAS_SUBSTRING;
            return new AliasesStringToken( lastTokenType, sb.toString(), pos - sb.length() + 1 );
        }
        prevChar();

        // check for ignorable whitespaces
        c = nextChar();
        if ( Character.isWhitespace( c ) )
        {
            StringBuffer sb = new StringBuffer();
            while ( Character.isWhitespace( c ) )
            {
                sb.append( c );
                c = nextChar();
            }
            prevChar();

            lastTokenType = AliasesStringToken.WHITESPACE;
            return new AliasesStringToken( lastTokenType, sb.toString(), pos - sb.length() + 1 );
        }
        prevChar();

        // check special characters
        c = nextChar();
        if ( c == CHAR_COMMA )
        {
            lastTokenType = AliasesStringToken.COMMA;
            return new AliasesStringToken( lastTokenType, ",", pos );
        }
        prevChar();

        // check Alias
        c = nextChar();
        if ( isAliasSafeCharStart( c ) )
        {
            StringBuffer sb = new StringBuffer();
            boolean hasError = false;

            sb.append( c );

            c = nextChar();
            while ( c != CHAR_COMMA && c != CHAR_EOF )
            {
                sb.append( c );

                if ( !isAliasSafeCharPart( c ) )
                {
                    hasError = true;
                    break;
                }

                c = nextChar();
            }
            prevChar();

            if ( hasError )
            {
                lastTokenType = AliasesStringToken.ERROR_ALIAS_PART;
                return new AliasesStringToken( lastTokenType, sb.toString(), pos - sb.length() + 1 );
            }
            else
            {
                lastTokenType = AliasesStringToken.ALIAS;
                return new AliasesStringToken( lastTokenType, sb.toString(), pos - sb.length() + 1 );
            }
        }
        else
        {
            lastTokenType = AliasesStringToken.ERROR_ALIAS_START;
            return new AliasesStringToken( lastTokenType, c + "", pos );
        }
    }


    /**
     * Determines if the specified character is
     * permissible as the first character in an attribute type or object class
     * alias.
     * <p>
     * A character may start an attribute type or object class alias if and
     * only if one of the following conditions is true:
     * <ul>
     * <li> it is a letter between 'a' to 'z' and between 'A' to 'Z'
     * </ul>
     *
     * <p><b>Note:</b> This method cannot handle <a
     * href="#supplementary"> supplementary characters</a>. To support
     * all Unicode characters, including supplementary characters, use
     * the {@link #isJavaIdentifierStart(int)} method.
     *
     * @param   c the character to be tested.
     * @return  <code>true</code> if the character may start an attribute type
     * or object class alias.; <code>false</code> otherwise.
     */
    private boolean isAliasSafeCharStart( char c )
    {
        return ( c == 'a' || c == 'b' || c == 'c' || c == 'd' || c == 'e' || c == 'f' || c == 'g' || c == 'h'
            || c == 'i' || c == 'j' || c == 'k' || c == 'l' || c == 'm' || c == 'n' || c == 'o' || c == 'p' || c == 'q'
            || c == 'r' || c == 's' || c == 't' || c == 'u' || c == 'v' || c == 'w' || c == 'x' || c == 'y' || c == 'z'
            || c == 'A' || c == 'B' || c == 'C' || c == 'D' || c == 'E' || c == 'F' || c == 'G' || c == 'H' || c == 'I'
            || c == 'J' || c == 'K' || c == 'L' || c == 'M' || c == 'N' || c == 'O' || c == 'P' || c == 'Q' || c == 'R'
            || c == 'S' || c == 'T' || c == 'U' || c == 'V' || c == 'W' || c == 'X' || c == 'Y' || c == 'Z' );
    }


    /**
     * Determines if the specified character may be part of an attribute type or
     * object class alias as other than the first character.
     * <p>
     * A character may be part of an attribute type or object class alias if any 
     * of the following are true:
     * <ul>
     * <li>  it is a letter between 'a' to 'z' and between 'A' to 'Z'
     * <li>  it is a digit
     * <li>  it is a hyphen ('-')
     * <li>  it is a semi-colon (';')
     * </ul>
     *
     * @param   c      the character to be tested.
     * @return <code>true</code> if the character may be part of an attribute 
     * type or object class alias; <code>false</code> otherwise.
     */
    private boolean isAliasSafeCharPart( char c )
    {
        return ( c == 'a' || c == 'b' || c == 'c' || c == 'd' || c == 'e' || c == 'f' || c == 'g' || c == 'h'
            || c == 'i' || c == 'j' || c == 'k' || c == 'l' || c == 'm' || c == 'n' || c == 'o' || c == 'p' || c == 'q'
            || c == 'r' || c == 's' || c == 't' || c == 'u' || c == 'v' || c == 'w' || c == 'x' || c == 'y' || c == 'z'
            || c == 'A' || c == 'B' || c == 'C' || c == 'D' || c == 'E' || c == 'F' || c == 'G' || c == 'H' || c == 'I'
            || c == 'J' || c == 'K' || c == 'L' || c == 'M' || c == 'N' || c == 'O' || c == 'P' || c == 'Q' || c == 'R'
            || c == 'S' || c == 'T' || c == 'U' || c == 'V' || c == 'W' || c == 'X' || c == 'Y' || c == 'Z' || c == '0'
            || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9' || c == '-' );
    }
}
