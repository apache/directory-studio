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
package org.apache.directory.ldapstudio.schemas.view.viewers;


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
 * Scanner used to analyse Schema code. Allows syntax coloring.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SchemaCodeScanner extends RuleBasedScanner
{
    String attributype = "attributetype";

    String objectclass = "objectclass";

    String[] keywords = new String[]
        { "NAME", "DESC", "OBSOLETE", "SUP", "EQUALITY", "ORDERING", "MUST", "MAY", "STRUCTURAL", "SUBSTR", "SYNTAX",
            "SINGLE-VALUE", "COLLECTIVE", "NO-USER-MODIFICATION", "USAGE", "userApplications", "directoryOperation",
            "distributedOperation", "dSAOperation", "ABSTRACT", "STRUCTURAL", "AUXILIARY", "MUST", "MAY" };


    public SchemaCodeScanner( SchemaTextAttributeProvider provider )
    {
        List<IRule> rules = new ArrayList<IRule>();

        IToken keyword = new Token( provider.getAttribute( SchemaTextAttributeProvider.KEYWORD_ATTRIBUTE ) );
        IToken string = new Token( provider.getAttribute( SchemaTextAttributeProvider.STRING_ATTRIBUTE ) );
        IToken undefined = new Token( provider.getAttribute( SchemaTextAttributeProvider.DEFAULT_ATTRIBUTE ) );
        IToken ATToken = new Token( provider.getAttribute( SchemaTextAttributeProvider.ATTRIBUTETYPE_ATTRIBUTE ) );
        IToken OCToken = new Token( provider.getAttribute( SchemaTextAttributeProvider.OBJECTCLASS_ATTRIBUTE ) );
        IToken oid = new Token( provider.getAttribute( SchemaTextAttributeProvider.OID_ATTRIBUTE ) );

        // Rules for Strings
        rules.add( new SingleLineRule( "\"", "\"", string, '\0', true ) );
        rules.add( new SingleLineRule( "'", "'", string, '\0', true ) );
        // Generic rule for whitespaces
        rules.add( new WhitespaceRule( new IWhitespaceDetector()
        {
            /**
             * Indicates if the given character is a whitespace
             * @param c the character to analyse
             * @return <code>true</code> if the character is to be considered as a whitespace,  <code>false</code> if not.
             * @see org.eclipse.jface.text.rules.IWhitespaceDetector#isWhitespace(char)
             */
            public boolean isWhitespace( char c )
            {
                return Character.isWhitespace( c );
            }
        } ) );

        WordRule wrOID = new WordRule( new SchemaOIDDetector(), oid );

        rules.add( wrOID );

        // If the word isn't in the List, returns undefined
        WordRule wr = new WordRule( new SchemaWordDetector(), undefined );

        // 'attributetype' rule
        wr.addWord( attributype, ATToken );

        // 'objectclass' rule
        wr.addWord( objectclass, OCToken );

        // Adding Keywords
        for (String kw : keywords )
        {
            wr.addWord( kw, keyword );
        }

        rules.add( wr );

        IRule[] param = new IRule[rules.size()];
        rules.toArray( param );
        setRules( param );
    }

    /**
     * This class implements a word detector for Schema
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    static class SchemaWordDetector implements IWordDetector
    {
        /* (non-Javadoc)
         * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
         */
        public boolean isWordPart( char c )
        {
            return ( Character.isLetterOrDigit( c ) || c == '_' || c == '-' || c == '$' || c == '#' || c == '@'
                || c == '~' || c == '.' || c == '?' );
        }


        /* (non-Javadoc)
         * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
         */
        public boolean isWordStart( char c )
        {
            return ( Character.isLetter( c ) || c == '.' || c == '_' || c == '?' || c == '$' );
        }
    }

    /**
     * This class implements an OID detector for Schema
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    static class SchemaOIDDetector implements IWordDetector
    {
        /* (non-Javadoc)
         * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
         */
        public boolean isWordPart( char c )
        {
            return ( Character.isDigit( c ) || c == '.' );
        }


        /* (non-Javadoc)
         * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
         */
        public boolean isWordStart( char c )
        {
            return ( Character.isDigit( c ) );
        }
    }
}
