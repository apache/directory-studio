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


/**
 * 
 * TODO LdapFilterScanner.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class LdapFilterScanner
{

    // From RFC 2254:
    // -------------
    // The string representation of an LDAP search filter is defined by the
    // following grammar, following the ABNF notation defined in [5]. The
    // filter format uses a prefix notation.
    //	
    // filter = "(" filtercomp ")"
    // filtercomp = and / or / not / item
    // and = "&" filterlist
    // or = "|" filterlist
    // not = "!" filter
    // filterlist = 1*filter
    // item = simple / present / substring / extensible
    // simple = attr filtertype value
    // filtertype = equal / approx / greater / less
    // equal = "="
    // approx = "~="
    // greater = ">="
    // less = "<="
    // extensible = attr [":dn"] [":" matchingrule] ":=" value
    // / [":dn"] ":" matchingrule ":=" value
    // present = attr "=*"
    // substring = attr "=" [initial] any [final]
    // initial = value
    // any = "*" *(value "*")
    // final = value
    // attr = AttributeDescription from Section 4.1.5 of [1]
    // matchingrule = MatchingRuleId from Section 4.1.9 of [1]
    // value = AttributeValue from Section 4.1.6 of [1]
    //	
    // The attr, matchingrule, and value constructs are as described in the
    // corresponding section of [1] given above.
    //	
    // If a value should contain any of the following characters
    //	
    // Character ASCII value
    // ---------------------------
    // * 0x2a
    // ( 0x28
    // ) 0x29
    // \ 0x5c
    // NUL 0x00
    //	
    // the character must be encoded as the backslash '\' character (ASCII
    // 0x5c) followed by the two hexadecimal digits representing the ASCII
    // value of the encoded character. The case of the two hexadecimal
    // digits is not significant.

    /** The filter to scan */
    private String filter;

    /** The current position */
    private int pos;

    /** The last token type. */
    private int lastTokenType;


    /**
     * Creates a new instance of LdapFilterScanner.
     */
    public LdapFilterScanner()
    {
        super();
        this.filter = "";
    }


    
    /**
     * Resets this scanner.
     * 
     * @param filter the new filter to scan
     */
    public void reset( String filter )
    {
        this.filter = filter;
        this.pos = -1;
        this.lastTokenType = LdapFilterToken.NEW;
    }


    /**
     * Gets the character at the current position.
     * 
     * @return the character at the current position
     */
    private char currentChar()
    {
        return 0 <= pos && pos < filter.length() ? filter.charAt( pos ) : '\u0000';
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


    private char nextNonLinebreakChar()
    {
        while ( nextChar() == '\n' );
        return currentChar();
    }


    private char prevNonLinebreakChar()
    {
        while ( prevChar() == '\n' );
        return currentChar();
    }


    // private char nextNonWhitespaceChar() {
    // while(Character.isWhitespace(nextChar()));
    // return currentChar();
    // }
    // private char prevNonWhitespaceChar() {
    // while(Character.isWhitespace(prevChar()));
    // return currentChar();
    // }

    public LdapFilterToken nextToken()
    {
        char c;

        // check for EOF
        c = nextChar();
        if ( c == '\u0000' )
        {
            return new LdapFilterToken( LdapFilterToken.EOF, "", pos );
        }
        else
        {
            prevChar();
        }

        // check for ignorable whitespaces
        c = nextChar();
        if ( Character.isWhitespace( c )
            && ( lastTokenType == LdapFilterToken.RPAR || lastTokenType == LdapFilterToken.AND
                || lastTokenType == LdapFilterToken.OR || lastTokenType == LdapFilterToken.NOT ) )
        {
            StringBuffer sb = new StringBuffer();
            while ( Character.isWhitespace( c ) )
            {
                sb.append( c );
                c = nextChar();
            }
            prevChar();
            return new LdapFilterToken( LdapFilterToken.WHITESPACE, sb.toString(), pos - sb.length() + 1 );
        }
        else
        {
            prevChar();
        }

        // check special characters
        c = nextChar();
        switch ( c )
        {
            case '(':
                this.lastTokenType = LdapFilterToken.LPAR;
                return new LdapFilterToken( this.lastTokenType, "(", pos );
            case ')':
                this.lastTokenType = LdapFilterToken.RPAR;
                return new LdapFilterToken( this.lastTokenType, ")", pos );
            case '&':
                if ( lastTokenType == LdapFilterToken.LPAR )
                {
                    // if(nextNonWhitespaceChar()=='(') {
                    // prevNonWhitespaceChar();
                    this.lastTokenType = LdapFilterToken.AND;
                    return new LdapFilterToken( this.lastTokenType, "&", pos );
                    // }
                    // else {
                    // prevNonWhitespaceChar();
                    // }
                }
                break;
            case '|':
                if ( lastTokenType == LdapFilterToken.LPAR )
                {
                    // if(nextNonWhitespaceChar()=='(') {
                    // prevNonWhitespaceChar();
                    this.lastTokenType = LdapFilterToken.OR;
                    return new LdapFilterToken( this.lastTokenType, "|", pos );
                    // }
                    // else {
                    // prevNonWhitespaceChar();
                    // }
                }
                break;
            case '!':
                if ( lastTokenType == LdapFilterToken.LPAR )
                {
                    // if(nextNonWhitespaceChar()=='(') {
                    // prevNonWhitespaceChar();
                    this.lastTokenType = LdapFilterToken.NOT;
                    return new LdapFilterToken( this.lastTokenType, "!", pos );
                    // }
                    // else {
                    // prevNonWhitespaceChar();
                    // }
                }
                break;
            case '=':
                if ( lastTokenType == LdapFilterToken.ATTRIBUTE )
                {
                    if ( nextChar() == '*' )
                    {
                        char t = nextChar();
                        if ( t == ')' || t == '\u0000' )
                        {
                            prevChar();
                            this.lastTokenType = LdapFilterToken.PRESENT;
                            return new LdapFilterToken( this.lastTokenType, "=*", pos - 1 );
                        }
                        else
                        {
                            prevChar();
                            prevChar();
                            this.lastTokenType = LdapFilterToken.EQUAL;
                            return new LdapFilterToken( this.lastTokenType, "=", pos );
                        }
                    }
                    else
                    {
                        prevChar();
                        this.lastTokenType = LdapFilterToken.EQUAL;
                        return new LdapFilterToken( this.lastTokenType, "=", pos );
                    }
                }
                break;
            case '>':
                if ( lastTokenType == LdapFilterToken.ATTRIBUTE )
                {
                    if ( nextChar() == '=' )
                    {
                        this.lastTokenType = LdapFilterToken.GREATER;
                        return new LdapFilterToken( this.lastTokenType, ">=", pos - 1 );
                    }
                    else
                    {
                        prevChar();
                    }
                }
                break;
            case '<':
                if ( lastTokenType == LdapFilterToken.ATTRIBUTE )
                {
                    if ( nextChar() == '=' )
                    {
                        this.lastTokenType = LdapFilterToken.LESS;
                        return new LdapFilterToken( this.lastTokenType, "<=", pos - 1 );
                    }
                    else
                    {
                        prevChar();
                    }
                }
                break;
            case '~':
                if ( lastTokenType == LdapFilterToken.ATTRIBUTE )
                {
                    if ( nextChar() == '=' )
                    {
                        this.lastTokenType = LdapFilterToken.APROX;
                        return new LdapFilterToken( this.lastTokenType, "~=", pos - 1 );
                    }
                    else
                    {
                        prevChar();
                    }
                }
                break;
        } // switch
        prevChar();

        // check attribute
        if ( this.lastTokenType == LdapFilterToken.LPAR )
        {
            StringBuffer sb = new StringBuffer();

            // first char must be non-whitespace
            c = nextChar();
            while ( c != '=' && c != '<' && c != '>' && c != '~' && c != '(' && c != ')' && c != '\u0000'
                && !Character.isWhitespace( c ) )
            {
                sb.append( c );
                c = nextChar();
            }
            prevChar();

            if ( sb.length() > 0 )
            {
                this.lastTokenType = LdapFilterToken.ATTRIBUTE;
                return new LdapFilterToken( this.lastTokenType, sb.toString(), pos - sb.length() + 1 );
            }
        }

        // check value
        if ( lastTokenType == LdapFilterToken.EQUAL || lastTokenType == LdapFilterToken.GREATER
            || lastTokenType == LdapFilterToken.LESS || lastTokenType == LdapFilterToken.APROX )
        {

            StringBuffer sb = new StringBuffer();
            c = nextNonLinebreakChar();
            while ( c != '(' && c != ')' && c != '\u0000' )
            {
                sb.append( c );
                c = nextNonLinebreakChar();
            }
            prevNonLinebreakChar();

            if ( sb.length() > 0 )
            {
                this.lastTokenType = LdapFilterToken.VALUE;
                return new LdapFilterToken( this.lastTokenType, sb.toString(), pos - sb.length() + 1 );
            }
        }

        // no match
        StringBuffer sb = new StringBuffer();
        c = nextChar();
        while ( c != '(' && c != ')' && c != '\u0000' )
        {
            sb.append( c );
            c = nextChar();
        }
        prevChar();
        // this.lastTokenType = LdapFilterToken.UNKNOWN;
        // return new LdapFilterToken(this.lastTokenType, sb.toString(),
        // pos-sb.length());
        return new LdapFilterToken( LdapFilterToken.UNKNOWN, sb.toString(), pos - sb.length() + 1 );
    }

}
