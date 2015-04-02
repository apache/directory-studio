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
public class KeywordEqualRule extends AbstractRule
{
    /**
     * The array of the types char sequences
     */
    private static char[][] KEYWORDS_SEQUENCES = new char[][]
        {
            new char[]
                { 'a', 't', 't', 'r', 's' },
            new char[]
                { 'a', 't', 't', 'r' },
            new char[]
                { 'd', 'n', 'a', 't', 't', 'r' },
            new char[]
                { 'f', 'i', 'l', 't', 'e', 'r' },
            new char[]
                { 's', 's', 'f' },
            new char[]
                { 's', 'a', 's', 'l', '_', 's', 's', 'f' },
            new char[]
                { 't', 'l', 's', '_', 's', 's', 'f' },
            new char[]
                { 't', 'r', 'a', 'n', 's', 'p', 'o', 'r', 't', '_', 's', 's', 'f' },
            new char[]
                { 's', 'a', 's', 's', '_', 's', 's', 'f' }
    };


    /**
     * Creates a new instance of DnRule.
     *
     * @param token the associated token
     */
    public KeywordEqualRule( IToken token )
    {
        super( token );
    }


    /**
     * {@inheritDoc}
     */
    public IToken evaluate( ICharacterScanner scanner, boolean resume )
    {
        // Looking for any keyword
        if ( matchKeyword( scanner ) )
        {
            // Looking for '='
            if ( matchEqual( scanner ) )
            {
                // Token evaluation complete
                return token;
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
     * Checks if one of the types char sequence matches the scanner input.
     *
     * @param scanner the scanner input
     * @return <code>true</code> if the scanner input matches one of the types char sequence,
     *         <code>false</code> if not.
     */
    private boolean matchKeyword( ICharacterScanner scanner )
    {
        for ( char[] typeSequence : KEYWORDS_SEQUENCES )
        {
            if ( matchCharSequence( scanner, typeSequence ) )
            {
                return true;
            }
        }

        return false;
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
}
