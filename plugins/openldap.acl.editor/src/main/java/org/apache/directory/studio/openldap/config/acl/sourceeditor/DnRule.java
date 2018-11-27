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

package org.apache.directory.studio.openldap.config.acl.sourceeditor;


import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;


/**
 * Rule to detect a "dn[.type[,modifier]]=" clause.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DnRule extends AbstractRule
{

    /**
     * The "dn" char sequence.
     */
    private static char[] DN_SEQUENCE = new char[]
        { 'd', 'n' };

    /**
     * The array of the types char sequences
     */
    private static char[][] TYPES_SEQUENCES = new char[][]
        {
            new char[]
                { 'r', 'e', 'g', 'e', 'x' },
            new char[]
                { 'b', 'a', 's', 'e' },
            new char[]
                { 'e', 'x', 'a', 'c', 't' },
            new char[]
                { 'o', 'n', 'e' },
            new char[]
                { 's', 'u', 'b', 't', 'r', 'e', 'e' },
            new char[]
                { 'c', 'h', 'i', 'l', 'd', 'r', 'e', 'n' }
    };

    /**
     * The "expand" char sequence.
     */
    private static char[] EXPAND_SEQUENCE = new char[]
        { 'e', 'x', 'p', 'a', 'n', 'd' };

    /**
     * The "level" char sequence.
     */
    private static char[] LEVEL_SEQUENCE = new char[]
        { 'l', 'e', 'v', 'e', 'l' };


    /**
     * Creates a new instance of DnRule.
     *
     * @param token the associated token
     */
    public DnRule( IToken token )
    {
        super( token );
    }


    /**
     * {@inheritDoc}
     */
    public IToken evaluate( ICharacterScanner scanner, boolean resume )
    {
        // Looking for "dn"
        if ( matchDn( scanner ) )
        {
            // Looking for '='
            if ( matchEqual( scanner ) )
            {
                // Token evaluation complete
                return token;
            }
            // Looking for '.'
            else if ( matchDot( scanner ) )
            {
                // Looking for one of the types
                if ( matchType( scanner ) )
                {
                    // Looking for '='
                    if ( matchEqual( scanner ) )
                    {
                        // Token evaluation complete
                        return token;
                    }
                    // Looking for ','
                    else if ( matchComma( scanner ) )
                    {
                        // Looking for "expand"
                        if ( matchExpand( scanner ) )
                        {
                            // Looking for '='
                            if ( matchEqual( scanner ) )
                            {
                                // Token evaluation complete
                                return token;
                            }
                        }
                    }
                }
                // Looking for "level"
                else if ( matchLevel( scanner ) )
                {
                    // Looking for '{'
                    if ( matchOpenCurlyBracket( scanner ) )
                    {
                        // Looking for digits
                        boolean atLeastFoundOneDigit = false;
                        while ( matchDigit( scanner ) )
                        {
                            atLeastFoundOneDigit = true;
                        }

                        // Checking if we found at least one digit
                        // and the next char is '}'
                        if ( atLeastFoundOneDigit && ( matchCloseCurlyBracket( scanner ) ) )
                        {
                            // Looking for '='
                            if ( matchEqual( scanner ) )
                            {
                                // Token evaluation complete
                                return token;
                            }
                        }
                    }
                }
            }
        }

        return Token.UNDEFINED;
    }


    /**
     * {@inheritDoc}
     */
    public IToken evaluate( ICharacterScanner scanner )
    {
        return this.evaluate( scanner, false );
    }


    /**
     * Checks if the "dn" char sequence matches the scanner input.
     *
     * @param scanner the scanner input
     * @return <code>true</code> if the scanner input matches the "dn" char sequence,
     *         <code>false</code> if not.
     */
    private boolean matchDn( ICharacterScanner scanner )
    {
        return matchCharSequence( scanner, DN_SEQUENCE );
    }


    /**
     * Checks if one of the types char sequence matches the scanner input.
     *
     * @param scanner the scanner input
     * @return <code>true</code> if the scanner input matches one of the types char sequence,
     *         <code>false</code> if not.
     */
    private boolean matchType( ICharacterScanner scanner )
    {
        for ( char[] typeSequence : TYPES_SEQUENCES )
        {
            if ( matchCharSequence( scanner, typeSequence ) )
            {
                return true;
            }
        }

        return false;
    }


    /**
     * Checks if the "expand" char sequence matches the scanner input.
     *
     * @param scanner the scanner input
     * @return <code>true</code> if the scanner input matches the "expand" char sequence,
     *         <code>false</code> if not.
     */
    private boolean matchExpand( ICharacterScanner scanner )
    {
        return matchCharSequence( scanner, EXPAND_SEQUENCE );
    }


    /**
     * Checks if the "level" char sequence matches the scanner input.
     *
     * @param scanner the scanner input
     * @return <code>true</code> if the scanner input matches the "level" char sequence,
     *         <code>false</code> if not.
     */
    private boolean matchLevel( ICharacterScanner scanner )
    {
        return matchCharSequence( scanner, LEVEL_SEQUENCE );
    }


    /**
     * Checks if the '.' char matches the scanner input.
     *
     * @param scanner the scanner input
     * @return <code>true</code> if the scanner input matches the '.' char,
     *         <code>false</code> if not.
     */
    private boolean matchDot( ICharacterScanner scanner )
    {
        return matchChar( scanner, '.' );
    }


    /**
     * Checks if the ',' char matches the scanner input.
     *
     * @param scanner the scanner input
     * @return <code>true</code> if the scanner input matches the ',' char,
     *         <code>false</code> if not.
     */
    private boolean matchComma( ICharacterScanner scanner )
    {
        return matchChar( scanner, ',' );
    }


    /**
     * Checks if the '=' char matches the scanner input.
     *
     * @param scanner the scanner input
     * @return <code>true</code> if the scanner input matches the '=' char,
     *         <code>false</code> if not.
     */
    private boolean matchEqual( ICharacterScanner scanner )
    {
        return matchChar( scanner, '=' );
    }


    /**
     * Checks if the '{' char matches the scanner input.
     *
     * @param scanner the scanner input
     * @return <code>true</code> if the scanner input matches the '{' char,
     *         <code>false</code> if not.
     */
    private boolean matchOpenCurlyBracket( ICharacterScanner scanner )
    {
        return matchChar( scanner, '{' );
    }


    /**
     * Checks if the '}' char matches the scanner input.
     *
     * @param scanner the scanner input
     * @return <code>true</code> if the scanner input matches the '}' char,
     *         <code>false</code> if not.
     */
    private boolean matchCloseCurlyBracket( ICharacterScanner scanner )
    {
        return matchChar( scanner, '}' );
    }
}
