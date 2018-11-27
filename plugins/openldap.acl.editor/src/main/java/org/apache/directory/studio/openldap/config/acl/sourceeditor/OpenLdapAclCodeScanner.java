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


import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;


/**
 * Scanner used to analyse ACI code. Allows syntax coloring.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OpenLdapAclCodeScanner extends RuleBasedScanner
{
    /** Keywords */
    public static final String[] aclKeywords = new String[]
        { "anonymous", //$NON-NLS-1$
            "auth", //$NON-NLS-1$
            "break", //$NON-NLS-1$
            "by", //$NON-NLS-1$
            "c", //$NON-NLS-1$
            "compare", //$NON-NLS-1$
            "continue", //$NON-NLS-1$
            "disclose", //$NON-NLS-1$
            "dnattr=", //$NON-NLS-1$
            "entry", //$NON-NLS-1$
            "m", //$NON-NLS-1$
            "manage", //$NON-NLS-1$
            "none", //$NON-NLS-1$
            "one", //$NON-NLS-1$
            "r", //$NON-NLS-1$
            "read", //$NON-NLS-1$
            "s", //$NON-NLS-1$
            "search", //$NON-NLS-1$
            "self", //$NON-NLS-1$
            "ssf=", //$NON-NLS-1$
            "stop", //$NON-NLS-1$
            "to", //$NON-NLS-1$
            "users", //$NON-NLS-1$
            "x", //$NON-NLS-1$
            "w", //$NON-NLS-1$
            "write" }; //$NON-NLS-1$


    /**
     * Creates a new instance of AciCodeScanner.
     *
     * @param provider
     *      the provider
     */
    public OpenLdapAclCodeScanner( OpenLdapAclTextAttributeProvider provider )
    {
        List<IRule> rules = new ArrayList<IRule>();

        IToken keyword = new Token( provider.getAttribute( OpenLdapAclTextAttributeProvider.KEYWORD_ATTRIBUTE ) );
        IToken string = new Token( provider.getAttribute( OpenLdapAclTextAttributeProvider.STRING_ATTRIBUTE ) );
        IToken undefined = new Token( provider.getAttribute( OpenLdapAclTextAttributeProvider.DEFAULT_ATTRIBUTE ) );

        // Rules for Strings
        rules.add( new SingleLineRule( "\"", "\"", string, '\0', true ) ); //$NON-NLS-1$ //$NON-NLS-2$
        rules.add( new SingleLineRule( "'", "'", string, '\0', true ) ); //$NON-NLS-1$ //$NON-NLS-2$

        // Generic rule for whitespaces
        rules.add( new WhitespaceRule( new IWhitespaceDetector()
        {
            public boolean isWhitespace( char c )
            {
                return Character.isWhitespace( c );
            }
        } ) );

        // Rules for specific not simple keywords
        rules.add( new DnRule( keyword ) );
        rules.add( new GroupRule( keyword ) );
        rules.add( new KeywordEqualRule( keyword ) );
        rules.add( new StarRule( keyword ) );

        // If the word isn't in the List, returns undefined
        WordRule wr = new WordRule( new OpenLdapAclWordDetector(), undefined );
        rules.add( wr );

        // Adding keywords
        for ( String aclKeyword : aclKeywords )
        {
            wr.addWord( aclKeyword, keyword );
        }

        IRule[] param = new IRule[rules.size()];
        rules.toArray( param );
        setRules( param );
    }

    /**
     * This class implements a word detector for ACI Items
     *
     * @author <a href="mailto:$dev@directory.apache.org">Apache Directory Project</a>
     */
    static class OpenLdapAclWordDetector implements IWordDetector
    {
        /**
         * {@inheritDoc}
         */
        public boolean isWordPart( char c )
        {
            return ( Character.isLetterOrDigit( c ) || c == '_' || c == '$' || c == '#' || c == '@' || c == '~'
                || c == '.' || c == '?' || c == '*' || c == '!' );
        }


        /**
         * {@inheritDoc}
         */
        public boolean isWordStart( char c )
        {
            return ( Character.isLetter( c ) || c == '.' || c == '_' || c == '?' || c == '$' );
        }
    }
}
