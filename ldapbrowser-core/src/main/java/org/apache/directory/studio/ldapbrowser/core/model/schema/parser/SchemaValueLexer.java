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

// $ANTLR 2.7.5 (20050128): "schemavalue.g" -> "SchemaValueLexer.java"$
package org.apache.directory.studio.ldapbrowser.core.model.schema.parser;


import java.io.InputStream;
import java.io.Reader;
import java.util.Hashtable;

import antlr.ByteBuffer;
import antlr.CharBuffer;
import antlr.CharStreamException;
import antlr.CharStreamIOException;
import antlr.InputBuffer;
import antlr.LexerSharedInputState;
import antlr.NoViableAltForCharException;
import antlr.RecognitionException;
import antlr.Token;
import antlr.TokenStream;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.TokenStreamRecognitionException;
import antlr.collections.impl.BitSet;


public class SchemaValueLexer extends antlr.CharScanner implements SchemaValueTokenTypes, TokenStream
{
    public SchemaValueLexer( InputStream in )
    {
        this( new ByteBuffer( in ) );
    }


    public SchemaValueLexer( Reader in )
    {
        this( new CharBuffer( in ) );
    }


    public SchemaValueLexer( InputBuffer ib )
    {
        this( new LexerSharedInputState( ib ) );
    }


    public SchemaValueLexer( LexerSharedInputState state )
    {
        super( state );
        caseSensitiveLiterals = true;
        setCaseSensitive( false );
        literals = new Hashtable();
    }


    public Token nextToken() throws TokenStreamException
    {
        Token theRetToken = null;
        tryAgain: for ( ;; )
        {
            Token _token = null;
            int _ttype = Token.INVALID_TYPE;
            resetText();
            try
            { // for char stream error handling
                try
                { // for lexical error handling
                    switch ( LA( 1 ) )
                    {
                        case ' ':
                        {
                            mWHSP( true );
                            theRetToken = _returnToken;
                            break;
                        }
                        case '(':
                        {
                            mLPAR( true );
                            theRetToken = _returnToken;
                            break;
                        }
                        case ')':
                        {
                            mRPAR( true );
                            theRetToken = _returnToken;
                            break;
                        }
                        case '\'':
                        {
                            mQUOTE( true );
                            theRetToken = _returnToken;
                            break;
                        }
                        case '$':
                        {
                            mDOLLAR( true );
                            theRetToken = _returnToken;
                            break;
                        }
                        case '}':
                        {
                            mRBRACKET( true );
                            theRetToken = _returnToken;
                            break;
                        }
                        case 'a':
                        case 'b':
                        case 'c':
                        case 'd':
                        case 'e':
                        case 'f':
                        case 'g':
                        case 'h':
                        case 'i':
                        case 'j':
                        case 'k':
                        case 'l':
                        case 'm':
                        case 'n':
                        case 'o':
                        case 'p':
                        case 'q':
                        case 'r':
                        case 's':
                        case 't':
                        case 'u':
                        case 'v':
                        case 'w':
                        case 'x':
                        case 'y':
                        case 'z':
                        {
                            mDESCR( true );
                            theRetToken = _returnToken;
                            break;
                        }
                        default:
                            if ( ( LA( 1 ) == '{' ) && ( ( LA( 2 ) >= '0' && LA( 2 ) <= '9' ) ) )
                            {
                                mLEN( true );
                                theRetToken = _returnToken;
                            }
                            else if ( ( ( LA( 1 ) >= '0' && LA( 1 ) <= '9' ) ) && ( _tokenSet_0.member( LA( 2 ) ) ) )
                            {
                                mNUMERICOID( true );
                                theRetToken = _returnToken;
                            }
                            else if ( ( LA( 1 ) == '{' ) && ( true ) )
                            {
                                mLBRACKET( true );
                                theRetToken = _returnToken;
                            }
                            else if ( ( ( LA( 1 ) >= '0' && LA( 1 ) <= '9' ) ) && ( true ) )
                            {
                                mDIGIT( true );
                                theRetToken = _returnToken;
                            }
                            else
                            {
                                if ( LA( 1 ) == EOF_CHAR )
                                {
                                    uponEOF();
                                    _returnToken = makeToken( Token.EOF_TYPE );
                                }
                                else
                                {
                                    throw new NoViableAltForCharException( ( char ) LA( 1 ), getFilename(), getLine(),
                                        getColumn() );
                                }
                            }
                    }
                    if ( _returnToken == null )
                        continue tryAgain; // found SKIP token
                    _ttype = _returnToken.getType();
                    _ttype = testLiteralsTable( _ttype );
                    _returnToken.setType( _ttype );
                    return _returnToken;
                }
                catch ( RecognitionException e )
                {
                    throw new TokenStreamRecognitionException( e );
                }
            }
            catch ( CharStreamException cse )
            {
                if ( cse instanceof CharStreamIOException )
                {
                    throw new TokenStreamIOException( ( ( CharStreamIOException ) cse ).io );
                }
                else
                {
                    throw new TokenStreamException( cse.getMessage() );
                }
            }
        }
    }


    public final void mWHSP( boolean _createToken ) throws RecognitionException, CharStreamException,
        TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = WHSP;
        int _saveIndex;

        {
            match( ' ' );
        }
        _ttype = Token.SKIP;
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    public final void mLPAR( boolean _createToken ) throws RecognitionException, CharStreamException,
        TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = LPAR;
        int _saveIndex;

        match( '(' );
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    public final void mRPAR( boolean _createToken ) throws RecognitionException, CharStreamException,
        TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = RPAR;
        int _saveIndex;

        match( ')' );
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    public final void mQUOTE( boolean _createToken ) throws RecognitionException, CharStreamException,
        TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = QUOTE;
        int _saveIndex;

        match( '\'' );
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    public final void mDOLLAR( boolean _createToken ) throws RecognitionException, CharStreamException,
        TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = DOLLAR;
        int _saveIndex;

        match( '$' );
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    public final void mLBRACKET( boolean _createToken ) throws RecognitionException, CharStreamException,
        TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = LBRACKET;
        int _saveIndex;

        match( '{' );
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    public final void mRBRACKET( boolean _createToken ) throws RecognitionException, CharStreamException,
        TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = RBRACKET;
        int _saveIndex;

        match( '}' );
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    public final void mLEN( boolean _createToken ) throws RecognitionException, CharStreamException,
        TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = LEN;
        int _saveIndex;

        mLBRACKET( false );
        {
            int _cnt11 = 0;
            _loop11: do
            {
                if ( ( ( LA( 1 ) >= '0' && LA( 1 ) <= '9' ) ) )
                {
                    mDIGIT( false );
                }
                else
                {
                    if ( _cnt11 >= 1 )
                    {
                        break _loop11;
                    }
                    else
                    {
                        throw new NoViableAltForCharException( ( char ) LA( 1 ), getFilename(), getLine(), getColumn() );
                    }
                }

                _cnt11++;
            }
            while ( true );
        }
        mRBRACKET( false );
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    public final void mDIGIT( boolean _createToken ) throws RecognitionException, CharStreamException,
        TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = DIGIT;
        int _saveIndex;

        {
            matchRange( '0', '9' );
        }
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    public final void mNUMERICOID( boolean _createToken ) throws RecognitionException, CharStreamException,
        TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = NUMERICOID;
        int _saveIndex;

        {
            int _cnt16 = 0;
            _loop16: do
            {
                if ( ( ( LA( 1 ) >= '0' && LA( 1 ) <= '9' ) ) )
                {
                    matchRange( '0', '9' );
                }
                else
                {
                    if ( _cnt16 >= 1 )
                    {
                        break _loop16;
                    }
                    else
                    {
                        throw new NoViableAltForCharException( ( char ) LA( 1 ), getFilename(), getLine(), getColumn() );
                    }
                }

                _cnt16++;
            }
            while ( true );
        }
        {
            int _cnt20 = 0;
            _loop20: do
            {
                if ( ( LA( 1 ) == '.' ) )
                {
                    match( '.' );
                    {
                        int _cnt19 = 0;
                        _loop19: do
                        {
                            if ( ( ( LA( 1 ) >= '0' && LA( 1 ) <= '9' ) ) )
                            {
                                matchRange( '0', '9' );
                            }
                            else
                            {
                                if ( _cnt19 >= 1 )
                                {
                                    break _loop19;
                                }
                                else
                                {
                                    throw new NoViableAltForCharException( ( char ) LA( 1 ), getFilename(), getLine(),
                                        getColumn() );
                                }
                            }

                            _cnt19++;
                        }
                        while ( true );
                    }
                }
                else
                {
                    if ( _cnt20 >= 1 )
                    {
                        break _loop20;
                    }
                    else
                    {
                        throw new NoViableAltForCharException( ( char ) LA( 1 ), getFilename(), getLine(), getColumn() );
                    }
                }

                _cnt20++;
            }
            while ( true );
        }
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    public final void mDESCR( boolean _createToken ) throws RecognitionException, CharStreamException,
        TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = DESCR;
        int _saveIndex;

        {
            matchRange( 'a', 'z' );
        }
        {
            _loop24: do
            {
                switch ( LA( 1 ) )
                {
                    case 'a':
                    case 'b':
                    case 'c':
                    case 'd':
                    case 'e':
                    case 'f':
                    case 'g':
                    case 'h':
                    case 'i':
                    case 'j':
                    case 'k':
                    case 'l':
                    case 'm':
                    case 'n':
                    case 'o':
                    case 'p':
                    case 'q':
                    case 'r':
                    case 's':
                    case 't':
                    case 'u':
                    case 'v':
                    case 'w':
                    case 'x':
                    case 'y':
                    case 'z':
                    {
                        matchRange( 'a', 'z' );
                        break;
                    }
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                    {
                        matchRange( '0', '9' );
                        break;
                    }
                    case '-':
                    {
                        match( '-' );
                        break;
                    }
                    case ';':
                    {
                        match( ';' );
                        break;
                    }
                    case '.':
                    {
                        match( '.' );
                        break;
                    }
                    default:
                    {
                        break _loop24;
                    }
                }
            }
            while ( true );
        }
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    private static final long[] mk_tokenSet_0()
    {
        long[] data =
            { 288019269919178752L, 0L, 0L, 0L, 0L };
        return data;
    }

    public static final BitSet _tokenSet_0 = new BitSet( mk_tokenSet_0() );

}
