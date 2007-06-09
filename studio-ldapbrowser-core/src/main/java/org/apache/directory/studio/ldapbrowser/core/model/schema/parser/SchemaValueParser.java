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

// $ANTLR 2.7.5 (20050128): "schemavalue.g" -> "SchemaValueParser.java"$
package org.apache.directory.studio.ldapbrowser.core.model.schema.parser;


import java.util.*;

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


public class SchemaValueParser extends antlr.LLkParser implements SchemaValueTokenTypes
{

    protected SchemaValueParser( TokenBuffer tokenBuf, int k )
    {
        super( tokenBuf, k );
        tokenNames = _tokenNames;
    }


    public SchemaValueParser( TokenBuffer tokenBuf )
    {
        this( tokenBuf, 3 );
    }


    protected SchemaValueParser( TokenStream lexer, int k )
    {
        super( lexer, k );
        tokenNames = _tokenNames;
    }


    public SchemaValueParser( TokenStream lexer )
    {
        this( lexer, 3 );
    }


    public SchemaValueParser( ParserSharedInputState state )
    {
        super( state, 3 );
        tokenNames = _tokenNames;
    }


    public final String[] oids() throws RecognitionException, TokenStreamException
    {
        String[] oids;

        oids = new String[0];
        List oidList = new ArrayList();
        String oid = null;

        {
            switch ( LA( 1 ) )
            {
                case NUMERICOID:
                case DESCR:
                {
                    {
                        oid = oid();
                        oidList.add( oid );
                    }
                    break;
                }
                case LPAR:
                {
                    {
                        match( LPAR );
                        oid = oid();
                        oidList.add( oid );
                        {
                            _loop30: do
                            {
                                if ( ( LA( 1 ) == DOLLAR ) )
                                {
                                    match( DOLLAR );
                                    oid = oid();
                                    oidList.add( oid );
                                }
                                else
                                {
                                    break _loop30;
                                }

                            }
                            while ( true );
                        }
                        match( RPAR );
                    }
                    break;
                }
                default:
                {
                    throw new NoViableAltException( LT( 1 ), getFilename() );
                }
            }
        }

        oids = ( String[] ) oidList.toArray( new String[oidList.size()] );

        return oids;
    }


    public final String oid() throws RecognitionException, TokenStreamException
    {
        String oid = null;

        Token n = null;
        Token d = null;

        {
            switch ( LA( 1 ) )
            {
                case NUMERICOID:
                {
                    n = LT( 1 );
                    match( NUMERICOID );
                    oid = n.getText();
                    break;
                }
                case DESCR:
                {
                    d = LT( 1 );
                    match( DESCR );
                    oid = d.getText();
                    break;
                }
                default:
                {
                    throw new NoViableAltException( LT( 1 ), getFilename() );
                }
            }
        }
        return oid;
    }


    public final String[] qdescrs() throws RecognitionException, TokenStreamException
    {
        String[] qdescrs;

        qdescrs = new String[0];
        List qdescrList = new ArrayList();
        String qdescr = null;

        {
            switch ( LA( 1 ) )
            {
                case QUOTE:
                {
                    {
                        qdescr = qdescr();
                        qdescrList.add( qdescr );
                    }
                    break;
                }
                case LPAR:
                {
                    {
                        match( LPAR );
                        qdescr = qdescr();
                        qdescrList.add( qdescr );
                        {
                            _loop38: do
                            {
                                if ( ( LA( 1 ) == QUOTE ) )
                                {
                                    qdescr = qdescr();
                                    qdescrList.add( qdescr );
                                }
                                else
                                {
                                    break _loop38;
                                }

                            }
                            while ( true );
                        }
                        match( RPAR );
                    }
                    break;
                }
                default:
                {
                    throw new NoViableAltException( LT( 1 ), getFilename() );
                }
            }
        }

        qdescrs = ( String[] ) qdescrList.toArray( new String[qdescrList.size()] );

        return qdescrs;
    }


    public final String qdescr() throws RecognitionException, TokenStreamException
    {
        String qdescr = null;

        Token d = null;

        {
            match( QUOTE );
            d = LT( 1 );
            match( DESCR );
            qdescr = d.getText();
            match( QUOTE );
        }
        return qdescr;
    }

    public static final String[] _tokenNames =
        { "<0>", "EOF", "<2>", "NULL_TREE_LOOKAHEAD", "WHSP", "LPAR", "RPAR", "QUOTE", "DOLLAR", "LBRACKET",
            "RBRACKET", "LEN", "DIGIT", "NUMERICOID", "DESCR" };

}
