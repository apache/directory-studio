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

// $ANTLR 2.7.5 (20050128): "schema.g" -> "SchemaLexer.java"$
package org.apache.directory.studio.ldapbrowser.core.model.schema.parser;


import java.io.*;
import java.util.*;

import java.io.InputStream;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.TokenStreamRecognitionException;
import antlr.CharStreamException;
import antlr.CharStreamIOException;
import antlr.ANTLRException;
import java.io.Reader;
import java.util.Hashtable;

import org.apache.directory.studio.ldapbrowser.core.model.schema.*;

import antlr.CharScanner;
import antlr.InputBuffer;
import antlr.ByteBuffer;
import antlr.CharBuffer;
import antlr.Token;
import antlr.CommonToken;
import antlr.RecognitionException;
import antlr.NoViableAltForCharException;
import antlr.MismatchedCharException;
import antlr.TokenStream;
import antlr.ANTLRHashString;
import antlr.LexerSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.SemanticException;


public class SchemaLexer extends antlr.CharScanner implements SchemaTokenTypes, TokenStream
{
    public SchemaLexer( InputStream in )
    {
        this( new ByteBuffer( in ) );
    }


    public SchemaLexer( Reader in )
    {
        this( new CharBuffer( in ) );
    }


    public SchemaLexer( InputBuffer ib )
    {
        this( new LexerSharedInputState( ib ) );
    }


    public SchemaLexer( LexerSharedInputState state )
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
                        case 'e':
                        {
                            mEQUALITY( true );
                            theRetToken = _returnToken;
                            break;
                        }
                        case 'x':
                        {
                            mX( true );
                            theRetToken = _returnToken;
                            break;
                        }
                        case 'c':
                        {
                            mCOLLECTIVE( true );
                            theRetToken = _returnToken;
                            break;
                        }
                        default:
                            if ( ( LA( 1 ) == 'u' ) && ( LA( 2 ) == 's' ) && ( LA( 3 ) == 'e' ) )
                            {
                                mUSAGE_USERAPPLICATIONS( true );
                                theRetToken = _returnToken;
                            }
                            else if ( ( LA( 1 ) == 'd' ) && ( LA( 2 ) == 'i' ) && ( LA( 3 ) == 'r' ) )
                            {
                                mUSAGE_DIRECTORYOPERATION( true );
                                theRetToken = _returnToken;
                            }
                            else if ( ( LA( 1 ) == 'd' ) && ( LA( 2 ) == 'i' ) && ( LA( 3 ) == 's' ) )
                            {
                                mUSAGE_DISTRIBUTEDOPERATION( true );
                                theRetToken = _returnToken;
                            }
                            else if ( ( LA( 1 ) == 's' ) && ( LA( 2 ) == 'u' ) && ( LA( 3 ) == 'p' ) )
                            {
                                mSUP( true );
                                theRetToken = _returnToken;
                            }
                            else if ( ( LA( 1 ) == 's' ) && ( LA( 2 ) == 'u' ) && ( LA( 3 ) == 'b' ) )
                            {
                                mSUBSTR( true );
                                theRetToken = _returnToken;
                            }
                            else if ( ( LA( 1 ) == 'u' ) && ( LA( 2 ) == 's' ) && ( LA( 3 ) == 'a' ) )
                            {
                                mUSAGE( true );
                                theRetToken = _returnToken;
                            }
                            else if ( ( LA( 1 ) == '{' ) && ( ( LA( 2 ) >= '0' && LA( 2 ) <= '9' ) ) )
                            {
                                mLEN( true );
                                theRetToken = _returnToken;
                            }
                            else if ( ( LA( 1 ) == 'd' ) && ( LA( 2 ) == 's' ) )
                            {
                                mUSAGE_DSAOPERATION( true );
                                theRetToken = _returnToken;
                            }
                            else if ( ( LA( 1 ) == '(' ) && ( _tokenSet_0.member( LA( 2 ) ) ) )
                            {
                                mSTARTNUMERICOID( true );
                                theRetToken = _returnToken;
                            }
                            else if ( ( LA( 1 ) == 'n' ) && ( LA( 2 ) == 'a' ) )
                            {
                                mNAME( true );
                                theRetToken = _returnToken;
                            }
                            else if ( ( LA( 1 ) == 'd' ) && ( LA( 2 ) == 'e' ) )
                            {
                                mDESC( true );
                                theRetToken = _returnToken;
                            }
                            else if ( ( LA( 1 ) == 'm' ) && ( LA( 2 ) == 'u' ) )
                            {
                                mMUST( true );
                                theRetToken = _returnToken;
                            }
                            else if ( ( LA( 1 ) == 'm' ) && ( LA( 2 ) == 'a' ) )
                            {
                                mMAY( true );
                                theRetToken = _returnToken;
                            }
                            else if ( ( LA( 1 ) == 'o' ) && ( LA( 2 ) == 'r' ) )
                            {
                                mORDERING( true );
                                theRetToken = _returnToken;
                            }
                            else if ( ( LA( 1 ) == 's' ) && ( LA( 2 ) == 'y' ) )
                            {
                                mSYNTAX( true );
                                theRetToken = _returnToken;
                            }
                            else if ( ( LA( 1 ) == 'a' ) && ( LA( 2 ) == 'p' ) )
                            {
                                mAPPLIES( true );
                                theRetToken = _returnToken;
                            }
                            else if ( ( LA( 1 ) == 's' ) && ( LA( 2 ) == 'i' ) )
                            {
                                mSINGLE_VALUE( true );
                                theRetToken = _returnToken;
                            }
                            else if ( ( LA( 1 ) == 'n' ) && ( LA( 2 ) == 'o' ) )
                            {
                                mNO_USER_MODIFICATION( true );
                                theRetToken = _returnToken;
                            }
                            else if ( ( LA( 1 ) == 'o' ) && ( LA( 2 ) == 'b' ) )
                            {
                                mOBSOLETE( true );
                                theRetToken = _returnToken;
                            }
                            else if ( ( LA( 1 ) == 'a' ) && ( LA( 2 ) == 'b' ) )
                            {
                                mABSTRACT( true );
                                theRetToken = _returnToken;
                            }
                            else if ( ( LA( 1 ) == 's' ) && ( LA( 2 ) == 't' ) )
                            {
                                mSTRUCTURAL( true );
                                theRetToken = _returnToken;
                            }
                            else if ( ( LA( 1 ) == 'a' ) && ( LA( 2 ) == 'u' ) )
                            {
                                mAUXILIARY( true );
                                theRetToken = _returnToken;
                            }
                            else if ( ( LA( 1 ) == '(' ) && ( true ) )
                            {
                                mLPAR( true );
                                theRetToken = _returnToken;
                            }
                            else if ( ( LA( 1 ) == '{' ) && ( true ) )
                            {
                                mLBRACKET( true );
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
            int _cnt3 = 0;
            _loop3: do
            {
                if ( ( LA( 1 ) == ' ' ) && ( true ) && ( true ) && ( true ) && ( true ) )
                {
                    match( ' ' );
                }
                else
                {
                    if ( _cnt3 >= 1 )
                    {
                        break _loop3;
                    }
                    else
                    {
                        throw new NoViableAltForCharException( ( char ) LA( 1 ), getFilename(), getLine(), getColumn() );
                    }
                }

                _cnt3++;
            }
            while ( true );
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
            int _cnt12 = 0;
            _loop12: do
            {
                if ( ( ( LA( 1 ) >= '0' && LA( 1 ) <= '9' ) ) )
                {
                    matchRange( '0', '9' );
                }
                else
                {
                    if ( _cnt12 >= 1 )
                    {
                        break _loop12;
                    }
                    else
                    {
                        throw new NoViableAltForCharException( ( char ) LA( 1 ), getFilename(), getLine(), getColumn() );
                    }
                }

                _cnt12++;
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


    public final void mUSAGE_USERAPPLICATIONS( boolean _createToken ) throws RecognitionException, CharStreamException,
        TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = USAGE_USERAPPLICATIONS;
        int _saveIndex;

        match( "userapplications" );
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    public final void mUSAGE_DIRECTORYOPERATION( boolean _createToken ) throws RecognitionException,
        CharStreamException, TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = USAGE_DIRECTORYOPERATION;
        int _saveIndex;

        match( "directoryoperation" );
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    public final void mUSAGE_DISTRIBUTEDOPERATION( boolean _createToken ) throws RecognitionException,
        CharStreamException, TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = USAGE_DISTRIBUTEDOPERATION;
        int _saveIndex;

        match( "distributedoperation" );
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    public final void mUSAGE_DSAOPERATION( boolean _createToken ) throws RecognitionException, CharStreamException,
        TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = USAGE_DSAOPERATION;
        int _saveIndex;

        match( "dsaoperation" );
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    public final void mSTARTNUMERICOID( boolean _createToken ) throws RecognitionException, CharStreamException,
        TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = STARTNUMERICOID;
        int _saveIndex;
        Token numericoid = null;

        {
            mLPAR( false );
            {
                mVALUE( true );
                numericoid = _returnToken;
            }
        }
        setText( numericoid.getText().trim() );
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    protected final void mVALUE( boolean _createToken ) throws RecognitionException, CharStreamException,
        TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = VALUE;
        int _saveIndex;

        {
            switch ( LA( 1 ) )
            {
                case ' ':
                {
                    mWHSP( false );
                    break;
                }
                case '\'':
                case '-':
                case '.':
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
                case ';':
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
                    break;
                }
                default:
                {
                    throw new NoViableAltForCharException( ( char ) LA( 1 ), getFilename(), getLine(), getColumn() );
                }
            }
        }
        {
            switch ( LA( 1 ) )
            {
                case '\'':
                {
                    mQUOTED_STRING( false );
                    break;
                }
                case '-':
                case '.':
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
                case ';':
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
                    mUNQUOTED_STRING( false );
                    break;
                }
                default:
                {
                    throw new NoViableAltForCharException( ( char ) LA( 1 ), getFilename(), getLine(), getColumn() );
                }
            }
        }
        {
            if ( ( LA( 1 ) == ' ' ) && ( true ) && ( true ) && ( true ) && ( true ) )
            {
                mWHSP( false );
            }
            else
            {
            }

        }
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    public final void mNAME( boolean _createToken ) throws RecognitionException, CharStreamException,
        TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = NAME;
        int _saveIndex;
        Token qdstrings = null;

        {
            match( "name" );
            mWHSP( false );
            mVALUES( true );
            qdstrings = _returnToken;
        }
        setText( qdstrings.getText().trim() );
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    protected final void mVALUES( boolean _createToken ) throws RecognitionException, CharStreamException,
        TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = VALUES;
        int _saveIndex;

        {
            switch ( LA( 1 ) )
            {
                case ' ':
                case '\'':
                case '-':
                case '.':
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
                case ';':
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
                    mVALUE( false );
                    break;
                }
                case '(':
                {
                    mLPAR( false );
                    mVALUE( false );
                    {
                        _loop72: do
                        {
                            if ( ( _tokenSet_1.member( LA( 1 ) ) ) )
                            {
                                {
                                    switch ( LA( 1 ) )
                                    {
                                        case '$':
                                        {
                                            mDOLLAR( false );
                                            break;
                                        }
                                        case ' ':
                                        case '\'':
                                        case '-':
                                        case '.':
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
                                        case ';':
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
                                            break;
                                        }
                                        default:
                                        {
                                            throw new NoViableAltForCharException( ( char ) LA( 1 ), getFilename(),
                                                getLine(), getColumn() );
                                        }
                                    }
                                }
                                mVALUE( false );
                            }
                            else
                            {
                                break _loop72;
                            }

                        }
                        while ( true );
                    }
                    mRPAR( false );
                    break;
                }
                default:
                {
                    throw new NoViableAltForCharException( ( char ) LA( 1 ), getFilename(), getLine(), getColumn() );
                }
            }
        }
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    public final void mDESC( boolean _createToken ) throws RecognitionException, CharStreamException,
        TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = DESC;
        int _saveIndex;
        Token qdstring = null;

        {
            match( "desc" );
            mWHSP( false );
            mVALUES( true );
            qdstring = _returnToken;
        }
        setText( qdstring.getText().trim() );
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    public final void mSUP( boolean _createToken ) throws RecognitionException, CharStreamException,
        TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = SUP;
        int _saveIndex;
        Token sup = null;

        {
            match( "sup" );
            mWHSP( false );
            mVALUES( true );
            sup = _returnToken;
        }
        setText( sup.getText().trim() );
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    public final void mMUST( boolean _createToken ) throws RecognitionException, CharStreamException,
        TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = MUST;
        int _saveIndex;
        Token must = null;

        {
            match( "must" );
            mWHSP( false );
            mVALUES( true );
            must = _returnToken;
        }
        setText( must.getText().trim() );
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    public final void mMAY( boolean _createToken ) throws RecognitionException, CharStreamException,
        TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = MAY;
        int _saveIndex;
        Token may = null;

        {
            match( "may" );
            mWHSP( false );
            mVALUES( true );
            may = _returnToken;
        }
        setText( may.getText() );
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    public final void mEQUALITY( boolean _createToken ) throws RecognitionException, CharStreamException,
        TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = EQUALITY;
        int _saveIndex;
        Token equality = null;

        {
            match( "equality" );
            mWHSP( false );
            mVALUES( true );
            equality = _returnToken;
        }
        setText( equality.getText().trim() );
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    public final void mORDERING( boolean _createToken ) throws RecognitionException, CharStreamException,
        TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = ORDERING;
        int _saveIndex;
        Token ordering = null;

        {
            match( "ordering" );
            mWHSP( false );
            mVALUES( true );
            ordering = _returnToken;
        }
        setText( ordering.getText().trim() );
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    public final void mSUBSTR( boolean _createToken ) throws RecognitionException, CharStreamException,
        TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = SUBSTR;
        int _saveIndex;
        Token substr = null;

        {
            match( "substr" );
            mWHSP( false );
            mVALUES( true );
            substr = _returnToken;
        }
        setText( substr.getText().trim() );
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    public final void mSYNTAX( boolean _createToken ) throws RecognitionException, CharStreamException,
        TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = SYNTAX;
        int _saveIndex;
        Token syntax = null;
        Token len = null;

        {
            match( "syntax" );
            mWHSP( false );
            mVALUES( true );
            syntax = _returnToken;
            {
                if ( ( LA( 1 ) == '{' ) )
                {
                    mLEN( true );
                    len = _returnToken;
                }
                else
                {
                }

            }
        }
        setText( syntax.getText().trim() + ( len != null ? len.getText().trim() : "" ) );
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    public final void mUSAGE( boolean _createToken ) throws RecognitionException, CharStreamException,
        TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = USAGE;
        int _saveIndex;
        Token op = null;

        {
            match( "usage" );
            mWHSP( false );
            mVALUES( true );
            op = _returnToken;
        }
        setText( op.getText().trim() );
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    public final void mAPPLIES( boolean _createToken ) throws RecognitionException, CharStreamException,
        TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = APPLIES;
        int _saveIndex;
        Token applies = null;

        {
            match( "applies" );
            mWHSP( false );
            mVALUES( true );
            applies = _returnToken;
        }
        setText( applies.getText().trim() );
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    public final void mX( boolean _createToken ) throws RecognitionException, CharStreamException, TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = X;
        int _saveIndex;

        {
            match( "x-" );
            {
                int _cnt46 = 0;
                _loop46: do
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
                        case '_':
                        {
                            match( '_' );
                            break;
                        }
                        default:
                        {
                            if ( _cnt46 >= 1 )
                            {
                                break _loop46;
                            }
                            else
                            {
                                throw new NoViableAltForCharException( ( char ) LA( 1 ), getFilename(), getLine(),
                                    getColumn() );
                            }
                        }
                    }
                    _cnt46++;
                }
                while ( true );
            }
            mWHSP( false );
            mVALUES( false );
        }
        _ttype = Token.SKIP;
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    public final void mSINGLE_VALUE( boolean _createToken ) throws RecognitionException, CharStreamException,
        TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = SINGLE_VALUE;
        int _saveIndex;

        {
            match( "single-value" );
            {
                if ( ( LA( 1 ) == ' ' ) )
                {
                    mWHSP( false );
                }
                else
                {
                }

            }
        }
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    public final void mCOLLECTIVE( boolean _createToken ) throws RecognitionException, CharStreamException,
        TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = COLLECTIVE;
        int _saveIndex;

        {
            match( "collective" );
            {
                if ( ( LA( 1 ) == ' ' ) )
                {
                    mWHSP( false );
                }
                else
                {
                }

            }
        }
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    public final void mNO_USER_MODIFICATION( boolean _createToken ) throws RecognitionException, CharStreamException,
        TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = NO_USER_MODIFICATION;
        int _saveIndex;

        {
            match( "no-user-modification" );
            {
                if ( ( LA( 1 ) == ' ' ) )
                {
                    mWHSP( false );
                }
                else
                {
                }

            }
        }
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    public final void mOBSOLETE( boolean _createToken ) throws RecognitionException, CharStreamException,
        TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = OBSOLETE;
        int _saveIndex;

        {
            match( "obsolete" );
            {
                if ( ( LA( 1 ) == ' ' ) )
                {
                    mWHSP( false );
                }
                else
                {
                }

            }
        }
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    public final void mABSTRACT( boolean _createToken ) throws RecognitionException, CharStreamException,
        TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = ABSTRACT;
        int _saveIndex;

        {
            match( "abstract" );
            {
                if ( ( LA( 1 ) == ' ' ) )
                {
                    mWHSP( false );
                }
                else
                {
                }

            }
        }
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    public final void mSTRUCTURAL( boolean _createToken ) throws RecognitionException, CharStreamException,
        TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = STRUCTURAL;
        int _saveIndex;

        {
            match( "structural" );
            {
                if ( ( LA( 1 ) == ' ' ) )
                {
                    mWHSP( false );
                }
                else
                {
                }

            }
        }
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    public final void mAUXILIARY( boolean _createToken ) throws RecognitionException, CharStreamException,
        TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = AUXILIARY;
        int _saveIndex;

        {
            match( "auxiliary" );
            {
                if ( ( LA( 1 ) == ' ' ) )
                {
                    mWHSP( false );
                }
                else
                {
                }

            }
        }
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    protected final void mQUOTED_STRING( boolean _createToken ) throws RecognitionException, CharStreamException,
        TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = QUOTED_STRING;
        int _saveIndex;

        {
            mQUOTE( false );
            {
                _loop83: do
                {
                    if ( ( _tokenSet_2.member( LA( 1 ) ) ) )
                    {
                        matchNot( '\'' );
                    }
                    else
                    {
                        break _loop83;
                    }

                }
                while ( true );
            }
            mQUOTE( false );
        }
        if ( _createToken && _token == null && _ttype != Token.SKIP )
        {
            _token = makeToken( _ttype );
            _token.setText( new String( text.getBuffer(), _begin, text.length() - _begin ) );
        }
        _returnToken = _token;
    }


    protected final void mUNQUOTED_STRING( boolean _createToken ) throws RecognitionException, CharStreamException,
        TokenStreamException
    {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = UNQUOTED_STRING;
        int _saveIndex;

        {
            int _cnt79 = 0;
            _loop79: do
            {
                if ( ( ( LA( 1 ) >= 'a' && LA( 1 ) <= 'z' ) ) && ( true ) && ( true ) && ( true ) && ( true ) )
                {
                    matchRange( 'a', 'z' );
                }
                else if ( ( ( LA( 1 ) >= '0' && LA( 1 ) <= '9' ) ) && ( true ) && ( true ) && ( true ) && ( true ) )
                {
                    matchRange( '0', '9' );
                }
                else if ( ( LA( 1 ) == '-' ) && ( true ) && ( true ) && ( true ) && ( true ) )
                {
                    match( '-' );
                }
                else if ( ( LA( 1 ) == ';' ) && ( true ) && ( true ) && ( true ) && ( true ) )
                {
                    match( ';' );
                }
                else if ( ( LA( 1 ) == '.' ) && ( true ) && ( true ) && ( true ) && ( true ) )
                {
                    match( '.' );
                }
                else
                {
                    if ( _cnt79 >= 1 )
                    {
                        break _loop79;
                    }
                    else
                    {
                        throw new NoViableAltForCharException( ( char ) LA( 1 ), getFilename(), getLine(), getColumn() );
                    }
                }

                _cnt79++;
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
            { 864515760645472256L, 576460743713488896L, 0L, 0L, 0L };
        return data;
    }

    public static final BitSet _tokenSet_0 = new BitSet( mk_tokenSet_0() );


    private static final long[] mk_tokenSet_1()
    {
        long[] data =
            { 864515829364948992L, 576460743713488896L, 0L, 0L, 0L };
        return data;
    }

    public static final BitSet _tokenSet_1 = new BitSet( mk_tokenSet_1() );


    private static final long[] mk_tokenSet_2()
    {
        long[] data = new long[8];
        data[0] = -549755813896L;
        for ( int i = 1; i <= 3; i++ )
        {
            data[i] = -1L;
        }
        return data;
    }

    public static final BitSet _tokenSet_2 = new BitSet( mk_tokenSet_2() );

}
