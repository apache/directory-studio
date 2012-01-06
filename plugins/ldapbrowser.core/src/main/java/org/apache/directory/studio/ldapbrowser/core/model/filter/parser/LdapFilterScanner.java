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
 * The LdapFilterScanner is a scanner for LDAP filters. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
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
        this.filter = ""; //$NON-NLS-1$
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


    /**
     * Increments the position counter as long as there are
     * line breaks and gets the character at that positon.
     * 
     * @return the character at the next position
     */
    private char nextNonLinebreakChar()
    {
        while ( nextChar() == '\n' );
        return currentChar();
    }


    /**
     * Decrements the position counter as long as there are
     * line breaks and gets the character at that positon.
     * 
     * @return the character at the previous position
     */
    private char prevNonLinebreakChar()
    {
        while ( prevChar() == '\n' );
        return currentChar();
    }


    /**
     * Gets the next token.
     * 
     * @return the next token
     */
    public LdapFilterToken nextToken()
    {
        char c;

        // check for EOF
        c = nextChar();
        if ( c == '\u0000' )
        {
            return new LdapFilterToken( LdapFilterToken.EOF, "", pos ); //$NON-NLS-1$
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
                return new LdapFilterToken( this.lastTokenType, "(", pos ); //$NON-NLS-1$
            case ')':
                if ( lastTokenType != LdapFilterToken.EQUAL && lastTokenType != LdapFilterToken.GREATER
                    && lastTokenType != LdapFilterToken.LESS && lastTokenType != LdapFilterToken.APROX
                    && lastTokenType != LdapFilterToken.SUBSTRING )
                {
                    this.lastTokenType = LdapFilterToken.RPAR;
                    return new LdapFilterToken( this.lastTokenType, ")", pos ); //$NON-NLS-1$
                }
            case '&':
                if ( lastTokenType == LdapFilterToken.LPAR )
                {
                    // if(nextNonWhitespaceChar()=='(') {
                    // prevNonWhitespaceChar();
                    this.lastTokenType = LdapFilterToken.AND;
                    return new LdapFilterToken( this.lastTokenType, "&", pos ); //$NON-NLS-1$
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
                    return new LdapFilterToken( this.lastTokenType, "|", pos ); //$NON-NLS-1$
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
                    return new LdapFilterToken( this.lastTokenType, "!", pos ); //$NON-NLS-1$
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
                            return new LdapFilterToken( this.lastTokenType, "=*", pos - 1 ); //$NON-NLS-1$
                        }
                        else
                        {
                            prevChar();
                            prevChar();
                        }
                    }
                    else
                    {
                        prevChar();
                    }

                    // substring or equal
                    // read till ) or eof, if we found an * we have an substring
                    boolean asteriskFound = false;
                    c = nextNonLinebreakChar();
                    int count = 1;
                    while ( c != ')' && c != '\u0000' )
                    {
                        if ( c == '*' )
                        {
                            asteriskFound = true;
                            break;
                        }

                        c = nextNonLinebreakChar();
                        count++;
                    }
                    while ( count > 0 )
                    {
                        prevNonLinebreakChar();
                        count--;
                    }
                    if ( asteriskFound )
                    {
                        this.lastTokenType = LdapFilterToken.SUBSTRING;
                        return new LdapFilterToken( this.lastTokenType, "=", pos ); //$NON-NLS-1$
                    }
                    else
                    {
                        this.lastTokenType = LdapFilterToken.EQUAL;
                        return new LdapFilterToken( this.lastTokenType, "=", pos ); //$NON-NLS-1$
                    }
                }
                else if ( lastTokenType == LdapFilterToken.EXTENSIBLE_EQUALS_COLON )
                {
                    this.lastTokenType = LdapFilterToken.EQUAL;
                    return new LdapFilterToken( this.lastTokenType, "=", pos ); //$NON-NLS-1$
                }
                break;
            case '>':
                if ( lastTokenType == LdapFilterToken.ATTRIBUTE )
                {
                    if ( nextChar() == '=' )
                    {
                        this.lastTokenType = LdapFilterToken.GREATER;
                        return new LdapFilterToken( this.lastTokenType, ">=", pos - 1 ); //$NON-NLS-1$
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
                        return new LdapFilterToken( this.lastTokenType, "<=", pos - 1 ); //$NON-NLS-1$
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
                        return new LdapFilterToken( this.lastTokenType, "~=", pos - 1 ); //$NON-NLS-1$
                    }
                    else
                    {
                        prevChar();
                    }
                }
                break;
            case ':':
                char t1 = nextChar();
                char t2 = nextChar();
                char t3 = nextChar();
                prevChar();
                prevChar();
                prevChar();
                if ( ( lastTokenType == LdapFilterToken.LPAR || lastTokenType == LdapFilterToken.EXTENSIBLE_ATTRIBUTE )
                    && (
                    //                        ( t1 == ':' && t2 != '=' )
                    //                        ||
                    //                        ( ( t1 == 'd' || t1 == 'D' ) && t2 == ':' && t3 == ':' )
                    //                        ||
                    ( ( t1 == 'd' || t1 == 'D' ) && ( t2 == 'n' || t2 == 'N' ) && ( t3 == ':' ) ) ) )
                {
                    this.lastTokenType = LdapFilterToken.EXTENSIBLE_DNATTR_COLON;
                    return new LdapFilterToken( this.lastTokenType, ":", pos ); //$NON-NLS-1$
                }
                else if ( ( lastTokenType == LdapFilterToken.EXTENSIBLE_ATTRIBUTE
                    || lastTokenType == LdapFilterToken.EXTENSIBLE_DNATTR
                    || lastTokenType == LdapFilterToken.EXTENSIBLE_MATCHINGRULEOID || lastTokenType == LdapFilterToken.EXTENSIBLE_MATCHINGRULEOID_COLON )
                    && t1 == '=' )
                {
                    this.lastTokenType = LdapFilterToken.EXTENSIBLE_EQUALS_COLON;
                    return new LdapFilterToken( this.lastTokenType, ":", pos ); //$NON-NLS-1$
                }
                else if ( ( lastTokenType == LdapFilterToken.LPAR
                    || lastTokenType == LdapFilterToken.EXTENSIBLE_ATTRIBUTE
                    || lastTokenType == LdapFilterToken.EXTENSIBLE_DNATTR || lastTokenType == LdapFilterToken.EXTENSIBLE_DNATTR_COLON ) )
                {
                    this.lastTokenType = LdapFilterToken.EXTENSIBLE_MATCHINGRULEOID_COLON;
                    return new LdapFilterToken( this.lastTokenType, ":", pos ); //$NON-NLS-1$
                }
                break;

        } // switch
        prevChar();

        // check attribute or extensible attribute
        if ( this.lastTokenType == LdapFilterToken.LPAR )
        {
            StringBuffer sb = new StringBuffer();

            // first char must be non-whitespace
            c = nextChar();
            while ( c != ':' && c != '=' && c != '<' && c != '>' && c != '~' && c != '(' && c != ')' && c != '\u0000'
                && !Character.isWhitespace( c ) )
            {
                sb.append( c );
                c = nextChar();
            }
            prevChar();

            if ( sb.length() > 0 )
            {
                boolean isExtensible = ( c == ':' );
                if ( isExtensible )
                {
                    this.lastTokenType = LdapFilterToken.EXTENSIBLE_ATTRIBUTE;
                    return new LdapFilterToken( this.lastTokenType, sb.toString(), pos - sb.length() + 1 );
                }
                else
                {
                    this.lastTokenType = LdapFilterToken.ATTRIBUTE;
                    return new LdapFilterToken( this.lastTokenType, sb.toString(), pos - sb.length() + 1 );
                }
            }
        }

        // check value
        if ( lastTokenType == LdapFilterToken.EQUAL || lastTokenType == LdapFilterToken.GREATER
            || lastTokenType == LdapFilterToken.LESS || lastTokenType == LdapFilterToken.APROX )
        {
            boolean forbiddenCharFound = false;
            StringBuffer sb = new StringBuffer();
            c = nextNonLinebreakChar();
            int count = 0;
            while ( c != ')' && c != '\u0000' )
            {
                if ( c == '*' || c == '(' )
                {
                    forbiddenCharFound = true;
                    break;
                }

                sb.append( c );
                c = nextNonLinebreakChar();
                count++;
            }
            prevNonLinebreakChar();

            if ( forbiddenCharFound )
            {
                while ( count > 0 )
                {
                    prevNonLinebreakChar();
                    count--;
                }
            }
            else
            //if ( sb.length() > 0 )
            {
                this.lastTokenType = LdapFilterToken.VALUE;
                return new LdapFilterToken( this.lastTokenType, sb.toString(), pos - sb.length() + 1 );
            }
        }
        if ( lastTokenType == LdapFilterToken.SUBSTRING )
        {
            boolean forbiddenCharFound = false;
            StringBuffer sb = new StringBuffer();
            c = nextNonLinebreakChar();
            int count = 0;
            while ( c != ')' && c != '\u0000' )
            {
                if ( c == '(' )
                {
                    forbiddenCharFound = true;
                    break;
                }

                sb.append( c );
                c = nextNonLinebreakChar();
                count++;
            }
            prevNonLinebreakChar();

            if ( forbiddenCharFound )
            {
                while ( count > 0 )
                {
                    prevNonLinebreakChar();
                    count--;
                }
            }
            else if ( sb.length() > 0 )
            {
                this.lastTokenType = LdapFilterToken.VALUE;
                return new LdapFilterToken( this.lastTokenType, sb.toString(), pos - sb.length() + 1 );
            }
        }

        // check extensible dn
        if ( lastTokenType == LdapFilterToken.EXTENSIBLE_DNATTR_COLON )
        {
            char t1 = nextChar();
            char t2 = nextChar();
            char t3 = nextChar();
            prevChar();
            if ( ( t1 == 'd' || t1 == 'D' ) && ( t2 == 'n' || t2 == 'N' ) && ( t3 == ':' || t3 == '\u0000' ) )
            {
                this.lastTokenType = LdapFilterToken.EXTENSIBLE_DNATTR;
                return new LdapFilterToken( this.lastTokenType, "" + t1 + t2, pos - 1 ); //$NON-NLS-1$
            }
            prevChar();
            prevChar();
        }

        // check extensible matchingrule
        if ( lastTokenType == LdapFilterToken.EXTENSIBLE_MATCHINGRULEOID_COLON )
        {
            StringBuffer sb = new StringBuffer();

            // first char must be non-whitespace
            c = nextChar();
            while ( c != ':' && c != '=' && c != '<' && c != '>' && c != '~' && c != '(' && c != ')' && c != '\u0000'
                && !Character.isWhitespace( c ) )
            {
                sb.append( c );
                c = nextChar();
            }
            prevChar();

            if ( sb.length() > 0 )
            {
                this.lastTokenType = LdapFilterToken.EXTENSIBLE_MATCHINGRULEOID;
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
