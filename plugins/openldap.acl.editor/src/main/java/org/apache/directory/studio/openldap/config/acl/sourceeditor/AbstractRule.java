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
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;


/**
 * Rule to detect a "dn[.type[,modifier]]=" clause.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class AbstractRule implements IPredicateRule
{
    /** The token */
    protected IToken token;


    /**
     * Creates a new instance of AbstractRule.
     *
     * @param token the associated token
     */
    public AbstractRule( IToken token )
    {
        this.token = token;
    }


    public IToken getSuccessToken()
    {
        return token;
    }


    /**
     * Checks if the given char sequence matches the scanner input.
     *
     * @param scanner the scanner input
     * @param sequence the char sequence
     * @return <code>true</code> if the scanner input matches the char sequence,
     *         <code>false</code> if not.
     */
    boolean matchCharSequence( ICharacterScanner scanner, char[] sequence )
    {
        for ( int i = 0; i < sequence.length; i++ )
        {
            int c = scanner.read();

            if ( c != sequence[i] )
            {
                while ( i >= 0 )
                {
                    scanner.unread();
                    i--;
                }

                return false;
            }

        }

        return true;
    }


    /**
     * Checks if the given char sequence does not match the scanner input.
     *
     * @param scanner the scanner input
     * @param sequence the char sequence
     * @return <code>true</code> if the scanner input does not match the char sequence,
     *         <code>false</code> if it does.
     */
    boolean doesNotMatchCharSequence( ICharacterScanner scanner, char[] sequence )
    {
        return !matchCharSequence( scanner, sequence );
    }


    /**
     * Checks if the char matches the scanner input.
     *
     * @param scanner the scanner input
     * @return <code>true</code> if the scanner input matches the char,
     *         <code>false</code> if not.
     */
    boolean matchChar( ICharacterScanner scanner, char character )
    {
        int c = scanner.read();

        if ( c == character )
        {
            return true;
        }
        else
        {
            scanner.unread();
            return false;
        }
    }


    /**
     * Checks if EOF matches the scanner input.
     *
     * @return <code>true</code> if the scanner input matches EOF,
     *         <code>false</code> if not.
     */
    boolean matchEOF( ICharacterScanner scanner )
    {
        int c = scanner.read();

        if ( c == ICharacterScanner.EOF )
        {
            return true;
        }
        else
        {
            scanner.unread();
            return false;
        }
    }


    /**
     * Checks if the char does not match the scanner input.
     *
     * @param scanner the scanner input
     * @return <code>true</code> if the scanner input does not match the char,
     *         <code>false</code> if it does.
     */
    boolean doesNotMatchChar( ICharacterScanner scanner, char character )
    {
        return !matchChar( scanner, character );
    }


    /**
     * Checks if the '}' char matches the scanner input.
     *
     * @param scanner the scanner input
     * @return <code>true</code> if the scanner input matches the '}' char,
     *         <code>false</code> if not.
     */
    boolean matchDigit( ICharacterScanner scanner )
    {
        int c = scanner.read();

        if ( ( c >= '0' ) && ( c <= '9' ) )
        {
            return true;
        }
        else
        {
            scanner.unread();
            return false;
        }
    }
}
