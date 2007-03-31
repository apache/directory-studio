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
package org.apache.directory.ldapstudio.aciitemeditor.widgets;


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
 * @version $Rev$, $Date$
 */
public class ACICodeScanner extends RuleBasedScanner
{
    /** 'identificationTag' keyword */
    public static final String identificationTagPartition = "identificationTag";

    /** 'precedence' keyword */
    public static final String precedencePartition = "precedence";

    /** 'authenticationLevel' keyword */
    public static final String authenticationLevelPartition = "authenticationLevel";

    /** Keywords for the itemOrUserFirst Section */
    public static final String[] itemOrUserFirstSectionPartition = new String[]
        { "itemOrUserFirst", "itemFirst", "userFirst" };

    /** Keywords for 'userFirst' section */
    public static final String[] userSection = new String[]
        { "userClasses", "userPermissions" };

    /** Keywords for AciItems values */
    public static final String[] aciKeywords = new String[]
        { "protectedItems", "itemPermissions", "entry", "allUserAttributeTypes", "attributeType",
            "allAttributeValues", "allUserAttributeTypesAndValues", "attributeValue", "selfValue", "rangeOfValues",
            "maxValueCount", "maxImmSub", "restrictedBy", "classes", "grantsAndDenials", "allUsers", "thisEntry",
            "name", "userGroup", "subtree", "type", "valuesIn", "none", "simple", "strong" };

    /** Keywords for grant values */
    public static final String[] aciGrantValues = new String[]
        { "grantAdd", "grantDiscloseOnError", "grantRead", "grantRemove", "grantBrowse", "grantExport", "grantImport",
            "grantModify", "grantRename", "grantReturnDN", "grantCompare", "grantFilterMatch", "grantInvoke", };

    /** Keywords for deny values */
    public static final String[] aciDenyValues = new String[]
        { "denyAdd", "denyDiscloseOnError", "denyRead", "denyRemove", "denyBrowse", "denyExport", "denyImport",
            "denyModify", "denyRename", "denyReturnDN", "denyCompare", "denyFilterMatch", "denyInvoke" };


    /**
     * Creates a new instance of AciCodeScanner.
     *
     * @param provider
     *      the provider
     */
    public ACICodeScanner( ACITextAttributeProvider provider )
    {
        List<IRule> rules = new ArrayList<IRule>();

        IToken keyword = new Token( provider.getAttribute( ACITextAttributeProvider.KEYWORD_ATTRIBUTE ) );
        IToken undefined = new Token( provider.getAttribute( ACITextAttributeProvider.DEFAULT_ATTRIBUTE ) );
        IToken string = new Token( provider.getAttribute( ACITextAttributeProvider.STRING_ATTRIBUTE ) );
        IToken grantValue = new Token( provider.getAttribute( ACITextAttributeProvider.GRANT_VALUE ) );
        IToken denyValue = new Token( provider.getAttribute( ACITextAttributeProvider.DENY_VALUE ) );
        IToken identification = new Token( provider.getAttribute( ACITextAttributeProvider.IDENTIFICATION_ATTRIBUTE ) );
        IToken precedence = new Token( provider.getAttribute( ACITextAttributeProvider.PRECEDENCE_ATTRIBUTE ) );
        IToken authenticationLevel = new Token( provider
            .getAttribute( ACITextAttributeProvider.AUTHENTICATIONLEVEL_ATTRIBUTE ) );
        IToken itemOrUserFirst = new Token( provider.getAttribute( ACITextAttributeProvider.ITEMORUSERFIRST_ATTRIBUTE ) );
        IToken user = new Token( provider.getAttribute( ACITextAttributeProvider.USER_ATTRIBUTE ) );

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

        // If the word isn't in the List, returns undefined
        WordRule wr = new WordRule( new AciWordDetector(), undefined );

        // Adding Keywords
        for ( int i = 0; i < aciKeywords.length; ++i )
        {
            wr.addWord( aciKeywords[i], keyword );
        }

        // Adding GrantValues
        for ( int i = 0; i < aciGrantValues.length; ++i )
        {
            wr.addWord( aciGrantValues[i], grantValue );
        }

        // Adding DenyValues
        for ( int i = 0; i < aciDenyValues.length; ++i )
        {
            wr.addWord( aciDenyValues[i], denyValue );
        }

        // Adding itemOrUserFirstSectionPartition
        for ( int i = 0; i < itemOrUserFirstSectionPartition.length; ++i )
        {
            wr.addWord( itemOrUserFirstSectionPartition[i], itemOrUserFirst );
        }

        // Adding User
        for ( int i = 0; i < userSection.length; ++i )
        {
            wr.addWord( userSection[i], user );
        }

        wr.addWord( identificationTagPartition, identification );

        wr.addWord( precedencePartition, precedence );

        wr.addWord( authenticationLevelPartition, authenticationLevel );

        rules.add( wr );

        // Conversion de la List en tableau pour la passer à la méthode setRules
        IRule[] param = new IRule[rules.size()];
        rules.toArray( param );
        setRules( param );
    }

    /**
     * This class implements a word detector for ACI Items
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    static class AciWordDetector implements IWordDetector
    {
        /* (non-Javadoc)
         * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
         */
        public boolean isWordPart( char c )
        {
            return ( Character.isLetterOrDigit( c ) || c == '_' || c == '$' || c == '#' || c == '@' || c == '~'
                || c == '.' || c == '?' );
        }


        /* (non-Javadoc)
         * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
         */
        public boolean isWordStart( char c )
        {
            return ( Character.isLetter( c ) || c == '.' || c == '_' || c == '?' || c == '$' );
        }
    }
}
