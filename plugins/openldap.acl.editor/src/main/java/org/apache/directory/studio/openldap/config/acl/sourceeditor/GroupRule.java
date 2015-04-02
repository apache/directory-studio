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
 * Rule to detect a "group[/objectclass[/attrname]][.type]=" clause.
 */
public class GroupRule extends AbstractRule
{

    /**
     * The "dn" char sequence.
     */
    private static char[] GROUP_SEQUENCE = new char[]
        { 'g', 'r', 'o', 'u', 'p' };

    /**
     * The array of the types char sequences
     */
    private static char[][] TYPES_SEQUENCES = new char[][]
        {
            new char[]
                { 'e', 'x', 'p', 'a', 'n', 'd' },
            new char[]
                { 'e', 'x', 'a', 'c', 't' }
    };


    /**
     * Creates a new instance of GroupRule.
     *
     * @param token the associated token
     */
    public GroupRule( IToken token )
    {
        super( token );
    }


    /**
     * {@inheritDoc}
     */
    public IToken evaluate( ICharacterScanner scanner, boolean resume )
    {
        // Looking for "group"
        if ( matchGroup( scanner ) )
        {

            // Looking for '='
            if ( matchEqual( scanner ) )
            {
                // Token evaluation complete
                return token;
            }
            // Looking for '/'
            else if ( matchSlash( scanner ) )
            {
                // Going forward until we find a '=', '/' or '.' char
                boolean atLeastFoundOneChar = false;
                while ( doesNotMatchEqualSlashOrDot( scanner ) )
                {
                    atLeastFoundOneChar = true;
                }

                // Checking if we found at least one char
                if ( atLeastFoundOneChar )
                {
                    // Looking for '='
                    if ( matchEqual( scanner ) )
                    {
                        // Token evaluation complete
                        return token;
                    }
                    // Looking for '/'
                    else if ( matchSlash( scanner ) )
                    {
                        // Going forward until we find a '=' or '.' char
                        boolean atLeastFoundOneChar2 = false;
                        while ( doesNotMatchEqualOrDot( scanner ) )
                        {
                            atLeastFoundOneChar2 = true;
                        }

                        // Checking if we found at least one char
                        if ( atLeastFoundOneChar2 )
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
                                }
                            }
                        }
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
                        }
                    }
                }
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
     * Checks if the "group" char sequence matches the scanner input.
     *
     * @param scanner the scanner input
     * @return <code>true</code> if the scanner input matches the "group" char sequence,
     *         <code>false</code> if not.
     */
    private boolean matchGroup( ICharacterScanner scanner )
    {
        return matchCharSequence( scanner, GROUP_SEQUENCE );
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
     * Checks if the '{' char matches the scanner input.
     *
     * @param scanner the scanner input
     * @return <code>true</code> if the scanner input matches the '{' char,
     *         <code>false</code> if not.
     */
    private boolean matchSlash( ICharacterScanner scanner )
    {
        return matchChar( scanner, '/' );
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
     * Checks if the '=', '/' or '.' chars don't match the scanner input.
     *
     * @param scanner the scanner input
     * @return <code>true</code> if the scanner input don't match the '=', '/' or '.' char,
     *         <code>false</code> if it does.
     */
    private boolean doesNotMatchEqualSlashOrDot( ICharacterScanner scanner )
    {
        if ( matchEOF( scanner ) )
        {
            scanner.unread();

            return false;
        }
        else if ( matchChar( scanner, '=' ) )
        {
            scanner.unread();

            return false;
        }
        else if ( matchChar( scanner, '/' ) )
        {
            scanner.unread();

            return false;
        }
        else if ( matchChar( scanner, '.' ) )
        {
            scanner.unread();

            return false;
        }
        else
        {
            scanner.read();

            return true;
        }
    }


    /**
     * Checks if the '=' or '.' chars don't match the scanner input.
     *
     * @param scanner the scanner input
     * @return <code>true</code> if the scanner input don't match the '=' or '.' char,
     *         <code>false</code> if it does.
     */
    private boolean doesNotMatchEqualOrDot( ICharacterScanner scanner )
    {
        if ( matchEOF( scanner ) )
        {
            scanner.unread();

            return false;
        }
        else if ( matchChar( scanner, '=' ) )
        {
            scanner.unread();

            return false;
        }
        else if ( matchChar( scanner, '.' ) )
        {
            scanner.unread();

            return false;
        }
        else
        {
            scanner.read();

            return true;
        }
    }
}
