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

// $ANTLR 2.7.5 (20050128): "schema.g" -> "SchemaParser.java"$
package org.apache.directory.ldapstudio.browser.core.model.schema.parser;


import java.io.*;
import java.util.*;

import org.apache.directory.ldapstudio.browser.core.model.schema.*;

import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;


public class SchemaParser extends antlr.LLkParser implements SchemaTokenTypes
{

    public static final void main( String[] args )
    {
        try
        {

            // "( 11.222.333.4444 NAME ( 'test1' 'test2' ) DESC 'a b c'
            // OBSOLETE SUP top ABSTRACT MUST ( cn ) may ( givenName $ sn)
            // )"
            // "( 2.5.4.11 NAME ( 'ou' 'organizationalUnitName' ) DESC
            // 'RFC2256: organizational unit this object belongs to' SUP
            // name EQUALITY caseIgnoreMatch SYNTAX 1.2.3.4.5{32} COLLECTIVE
            // USAGE userApplications )"
            // "( 2.5.4.11 DESC 'a b c' )"
            // "( 1.3.6.1.4.1.4203.1.2.1 NAME 'caseExactIA5SubstringsMatch'
            // SYNTAX 1.3.6.1.4.1.1466.115.121.1.26 )"
            // "( 2.5.13.0 NAME 'objectIdentifierMatch' APPLIES (
            // supportedApplicationContext $ supportedFeatures $
            // supportedExtension $ supportedControl ) )"
            // "( 1.2.840.113548.3.1.4.11110 NAME 'ciscoccnatPAUserPIN' DESC
            // 'User Defined Attribute' SYNTAX 1.3.6.1.4.1.1466.115.121.1.15
            // SINGLE-VALUE X-ORIGIN ( 'Cisco AVVID' 'user defined' ) )"
            SchemaLexer mainLexer = new SchemaLexer( new StringReader(
                "( 1.3.6.1.4.1.1466.115.121.1.48 NAME 'Supplier And Consumer'  )" ) );

            SchemaParser parser = new SchemaParser( mainLexer );
            // ObjectClassDescription d = parser.objectClassDescription();
            // AttributeTypeDescription d =
            // parser.attributeTypeDescription();
            LdapSyntaxDescription d = parser.syntaxDescription();
            // MatchingRuleDescription d = parser.matchingRuleDescription();
            // MatchingRuleUseDescription d =
            // parser.matchingRuleUseDescription();
            System.out.println( d.toString() );
        }
        catch ( Exception e )
        {
            System.err.println( "exception: " + e );
            e.printStackTrace();
        }
    }


    protected SchemaParser( TokenBuffer tokenBuf, int k )
    {
        super( tokenBuf, k );
        tokenNames = _tokenNames;
    }


    public SchemaParser( TokenBuffer tokenBuf )
    {
        this( tokenBuf, 3 );
    }


    protected SchemaParser( TokenStream lexer, int k )
    {
        super( lexer, k );
        tokenNames = _tokenNames;
    }


    public SchemaParser( TokenStream lexer )
    {
        this( lexer, 3 );
    }


    public SchemaParser( ParserSharedInputState state )
    {
        super( state, 3 );
        tokenNames = _tokenNames;
    }


    public final ObjectClassDescription objectClassDescription() throws RecognitionException, TokenStreamException
    {
        ObjectClassDescription ocd = new ObjectClassDescription();

        Token oid = null;
        Token name = null;
        Token desc = null;
        Token sup = null;
        Token must = null;
        Token may = null;

        {
            oid = LT( 1 );
            match( STARTNUMERICOID );
            ocd.setNumericOID( oid.getText() );
        }
        {
            _loop94: do
            {
                switch ( LA( 1 ) )
                {
                    case NAME:
                    {
                        {
                            name = LT( 1 );
                            match( NAME );
                            ocd.setNames( qdescrs( name.getText() ) );
                        }
                        break;
                    }
                    case DESC:
                    {
                        {
                            desc = LT( 1 );
                            match( DESC );
                            ocd.setDesc( qdstring( desc.getText() ) );
                        }
                        break;
                    }
                    case OBSOLETE:
                    {
                        {
                            match( OBSOLETE );
                            ocd.setObsolete( true );
                        }
                        break;
                    }
                    case SUP:
                    {
                        {
                            sup = LT( 1 );
                            match( SUP );
                            ocd.setSuperiorObjectClassDescriptionNames( oids( sup.getText() ) );
                        }
                        break;
                    }
                    case ABSTRACT:
                    case STRUCTURAL:
                    case AUXILIARY:
                    {
                        {
                            switch ( LA( 1 ) )
                            {
                                case ABSTRACT:
                                {
                                    match( ABSTRACT );
                                    ocd.setAbstract( true );
                                    break;
                                }
                                case STRUCTURAL:
                                {
                                    match( STRUCTURAL );
                                    ocd.setStructural( true );
                                    break;
                                }
                                case AUXILIARY:
                                {
                                    match( AUXILIARY );
                                    ocd.setAuxiliary( true );
                                    break;
                                }
                                default:
                                {
                                    throw new NoViableAltException( LT( 1 ), getFilename() );
                                }
                            }
                        }
                        break;
                    }
                    case MUST:
                    {
                        {
                            must = LT( 1 );
                            match( MUST );
                            ocd.setMustAttributeTypeDescriptionNames( oids( must.getText() ) );
                        }
                        break;
                    }
                    case MAY:
                    {
                        {
                            may = LT( 1 );
                            match( MAY );
                            ocd.setMayAttributeTypeDescriptionNames( oids( may.getText() ) );
                        }
                        break;
                    }
                    default:
                    {
                        break _loop94;
                    }
                }
            }
            while ( true );
        }
        match( RPAR );
        return ocd;
    }


    public final AttributeTypeDescription attributeTypeDescription() throws RecognitionException, TokenStreamException
    {
        AttributeTypeDescription atd = new AttributeTypeDescription();

        Token oid = null;
        Token name = null;
        Token desc = null;
        Token sup = null;
        Token equality = null;
        Token ordering = null;
        Token substr = null;
        Token syntax = null;
        Token usage = null;

        {
            oid = LT( 1 );
            match( STARTNUMERICOID );
            atd.setNumericOID( oid.getText() );
        }
        {
            _loop110: do
            {
                switch ( LA( 1 ) )
                {
                    case NAME:
                    {
                        {
                            name = LT( 1 );
                            match( NAME );
                            atd.setNames( qdescrs( name.getText() ) );
                        }
                        break;
                    }
                    case DESC:
                    {
                        {
                            desc = LT( 1 );
                            match( DESC );
                            atd.setDesc( qdstring( desc.getText() ) );
                        }
                        break;
                    }
                    case OBSOLETE:
                    {
                        {
                            match( OBSOLETE );
                            atd.setObsolete( true );
                        }
                        break;
                    }
                    case SUP:
                    {
                        {
                            sup = LT( 1 );
                            match( SUP );
                            atd.setSuperiorAttributeTypeDescriptionName( oid( sup.getText() ) );
                        }
                        break;
                    }
                    case EQUALITY:
                    {
                        {
                            equality = LT( 1 );
                            match( EQUALITY );
                            atd.setEqualityMatchingRuleDescriptionOID( oid( equality.getText() ) );
                        }
                        break;
                    }
                    case ORDERING:
                    {
                        {
                            ordering = LT( 1 );
                            match( ORDERING );
                            atd.setOrderingMatchingRuleDescriptionOID( oid( ordering.getText() ) );
                        }
                        break;
                    }
                    case SUBSTR:
                    {
                        {
                            substr = LT( 1 );
                            match( SUBSTR );
                            atd.setSubstringMatchingRuleDescriptionOID( oid( substr.getText() ) );
                        }
                        break;
                    }
                    case SYNTAX:
                    {
                        {
                            syntax = LT( 1 );
                            match( SYNTAX );
                            atd.setSyntaxDescriptionNumericOIDPlusLength( qdstring( syntax.getText() ) );
                        }
                        break;
                    }
                    case SINGLE_VALUE:
                    {
                        {
                            match( SINGLE_VALUE );
                            atd.setSingleValued( true );
                        }
                        break;
                    }
                    case COLLECTIVE:
                    {
                        {
                            match( COLLECTIVE );
                            atd.setCollective( true );
                        }
                        break;
                    }
                    case NO_USER_MODIFICATION:
                    {
                        {
                            match( NO_USER_MODIFICATION );
                            atd.setNoUserModification( true );
                        }
                        break;
                    }
                    case USAGE:
                    {
                        {
                            usage = LT( 1 );
                            match( USAGE );
                            atd.setUsage( usage.getText() );
                        }
                        break;
                    }
                    default:
                    {
                        break _loop110;
                    }
                }
            }
            while ( true );
        }
        match( RPAR );
        return atd;
    }


    public final LdapSyntaxDescription syntaxDescription() throws RecognitionException, TokenStreamException
    {
        LdapSyntaxDescription lsd = new LdapSyntaxDescription();

        Token oid = null;
        Token desc = null;
        Token name = null;

        {
            oid = LT( 1 );
            match( STARTNUMERICOID );
            lsd.setNumericOID( oid.getText() );
        }
        {
            _loop116: do
            {
                switch ( LA( 1 ) )
                {
                    case DESC:
                    {
                        {
                            desc = LT( 1 );
                            match( DESC );
                            lsd.setDesc( qdstring( desc.getText() ) );
                        }
                        break;
                    }
                    case NAME:
                    {
                        {
                            name = LT( 1 );
                            match( NAME );
                            lsd.setDesc( qdstring( name.getText() ) );
                        }
                        break;
                    }
                    default:
                    {
                        break _loop116;
                    }
                }
            }
            while ( true );
        }
        match( RPAR );
        return lsd;
    }


    public final MatchingRuleDescription matchingRuleDescription() throws RecognitionException, TokenStreamException
    {
        MatchingRuleDescription mrd = new MatchingRuleDescription();

        Token oid = null;
        Token name = null;
        Token desc = null;
        Token syntax = null;

        {
            oid = LT( 1 );
            match( STARTNUMERICOID );
            mrd.setNumericOID( oid.getText() );
        }
        {
            _loop124: do
            {
                switch ( LA( 1 ) )
                {
                    case NAME:
                    {
                        {
                            name = LT( 1 );
                            match( NAME );
                            mrd.setNames( qdescrs( name.getText() ) );
                        }
                        break;
                    }
                    case DESC:
                    {
                        {
                            desc = LT( 1 );
                            match( DESC );
                            mrd.setDesc( qdstring( desc.getText() ) );
                        }
                        break;
                    }
                    case OBSOLETE:
                    {
                        {
                            match( OBSOLETE );
                            mrd.setObsolete( true );
                        }
                        break;
                    }
                    case SYNTAX:
                    {
                        {
                            syntax = LT( 1 );
                            match( SYNTAX );
                            mrd.setSyntaxDescriptionNumericOID( syntax.getText() );
                        }
                        break;
                    }
                    default:
                    {
                        break _loop124;
                    }
                }
            }
            while ( true );
        }
        match( RPAR );
        return mrd;
    }


    public final MatchingRuleUseDescription matchingRuleUseDescription() throws RecognitionException,
        TokenStreamException
    {
        MatchingRuleUseDescription mrud = new MatchingRuleUseDescription();

        Token oid = null;
        Token name = null;
        Token desc = null;
        Token applies = null;

        {
            oid = LT( 1 );
            match( STARTNUMERICOID );
            mrud.setNumericOID( oid.getText() );
        }
        {
            _loop132: do
            {
                switch ( LA( 1 ) )
                {
                    case NAME:
                    {
                        {
                            name = LT( 1 );
                            match( NAME );
                            mrud.setNames( qdescrs( name.getText() ) );
                        }
                        break;
                    }
                    case DESC:
                    {
                        {
                            desc = LT( 1 );
                            match( DESC );
                            mrud.setDesc( qdstring( desc.getText() ) );
                        }
                        break;
                    }
                    case OBSOLETE:
                    {
                        {
                            match( OBSOLETE );
                            mrud.setObsolete( true );
                        }
                        break;
                    }
                    case APPLIES:
                    {
                        {
                            applies = LT( 1 );
                            match( APPLIES );
                            mrud.setAppliesAttributeTypeDescriptionOIDs( oids( applies.getText() ) );
                        }
                        break;
                    }
                    default:
                    {
                        break _loop132;
                    }
                }
            }
            while ( true );
        }
        match( RPAR );
        return mrud;
    }


    public final String oid( String s ) throws RecognitionException, TokenStreamException
    {
        String oid;

        SchemaValueLexer lexer = new SchemaValueLexer( new StringReader( s ) );
        SchemaValueParser parser = new SchemaValueParser( lexer );
        oid = parser.oid();

        return oid;
    }


    public final String[] oids( String s ) throws RecognitionException, TokenStreamException
    {
        String[] oids;

        SchemaValueLexer lexer = new SchemaValueLexer( new StringReader( s ) );
        SchemaValueParser parser = new SchemaValueParser( lexer );
        oids = parser.oids();

        return oids;
    }


    public final String[] qdescrs( String s ) throws RecognitionException, TokenStreamException
    {
        String[] qdescrs;

        SchemaValueLexer lexer = new SchemaValueLexer( new StringReader( s ) );
        SchemaValueParser parser = new SchemaValueParser( lexer );
        qdescrs = parser.qdescrs();

        return qdescrs;
    }


    public final String qdstring( String s ) throws RecognitionException, TokenStreamException
    {
        String qdstring;

        if ( s == null )
        {
            qdstring = null;
        }
        else
        {
            if ( s.startsWith( "'" ) )
            {
                s = s.substring( 1, s.length() );
            }
            if ( s.endsWith( "'" ) )
            {
                s = s.substring( 0, s.length() - 1 );
            }
            qdstring = s;
        }

        return qdstring;
    }

    public static final String[] _tokenNames =
        { "<0>", "EOF", "<2>", "NULL_TREE_LOOKAHEAD", "WHSP", "LPAR", "RPAR", "QUOTE", "DOLLAR", "LBRACKET",
            "RBRACKET", "LEN", "USAGE_USERAPPLICATIONS", "USAGE_DIRECTORYOPERATION", "USAGE_DISTRIBUTEDOPERATION",
            "USAGE_DSAOPERATION", "STARTNUMERICOID", "NAME", "DESC", "SUP", "MUST", "MAY", "EQUALITY", "ORDERING",
            "SUBSTR", "SYNTAX", "USAGE", "APPLIES", "X", "SINGLE_VALUE", "COLLECTIVE", "NO_USER_MODIFICATION",
            "OBSOLETE", "ABSTRACT", "STRUCTURAL", "AUXILIARY", "VALUES", "VALUE", "UNQUOTED_STRING", "QUOTED_STRING" };

}
