// $ANTLR 2.7.7 (20060906): "Acl.g" -> "AntlrAclLexer.java"$


package org.apache.directory.studio.openldap.config.acl.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;

import java.util.List;

import java.io.InputStream;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.TokenStreamRecognitionException;
import antlr.CharStreamException;
import antlr.CharStreamIOException;
import antlr.ANTLRException;
import java.io.Reader;
import java.util.Hashtable;
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

/**
 * The ANTLR generated OpenLDAP ACL parser.
 */
public class AntlrAclLexer extends antlr.CharScanner implements AntlrAclLexerTokenTypes, TokenStream
 {
public AntlrAclLexer(InputStream in) {
	this(new ByteBuffer(in));
}
public AntlrAclLexer(Reader in) {
	this(new CharBuffer(in));
}
public AntlrAclLexer(InputBuffer ib) {
	this(new LexerSharedInputState(ib));
}
public AntlrAclLexer(LexerSharedInputState state) {
	super(state);
	caseSensitiveLiterals = true;
	setCaseSensitive(false);
	literals = new Hashtable();
	literals.put(new ANTLRHashString("read", this), new Integer(33));
	literals.put(new ANTLRHashString("to", this), new Integer(41));
	literals.put(new ANTLRHashString("m", this), new Integer(26));
	literals.put(new ANTLRHashString("s", this), new Integer(35));
	literals.put(new ANTLRHashString("anonymous", this), new Integer(5));
	literals.put(new ANTLRHashString("expand", this), new Integer(22));
	literals.put(new ANTLRHashString("disclose", this), new Integer(17));
	literals.put(new ANTLRHashString("self", this), new Integer(37));
	literals.put(new ANTLRHashString("baseobject", this), new Integer(10));
	literals.put(new ANTLRHashString("base", this), new Integer(9));
	literals.put(new ANTLRHashString("matchingRule", this), new Integer(28));
	literals.put(new ANTLRHashString("r", this), new Integer(32));
	literals.put(new ANTLRHashString("x", this), new Integer(45));
	literals.put(new ANTLRHashString("subtree", this), new Integer(40));
	literals.put(new ANTLRHashString("group", this), new Integer(24));
	literals.put(new ANTLRHashString("onelevel", this), new Integer(31));
	literals.put(new ANTLRHashString("auth", this), new Integer(8));
	literals.put(new ANTLRHashString("w", this), new Integer(44));
	literals.put(new ANTLRHashString("one", this), new Integer(30));
	literals.put(new ANTLRHashString("access", this), new Integer(4));
	literals.put(new ANTLRHashString("none", this), new Integer(29));
	literals.put(new ANTLRHashString("exact", this), new Integer(21));
	literals.put(new ANTLRHashString("continue", this), new Integer(16));
	literals.put(new ANTLRHashString("write", this), new Integer(46));
	literals.put(new ANTLRHashString("regex", this), new Integer(34));
	literals.put(new ANTLRHashString("compare", this), new Integer(15));
	literals.put(new ANTLRHashString("entry", this), new Integer(20));
	literals.put(new ANTLRHashString("dnattr", this), new Integer(19));
	literals.put(new ANTLRHashString("manage", this), new Integer(27));
	literals.put(new ANTLRHashString("search", this), new Integer(36));
	literals.put(new ANTLRHashString("c", this), new Integer(13));
	literals.put(new ANTLRHashString("dn", this), new Integer(18));
	literals.put(new ANTLRHashString("users", this), new Integer(42));
	literals.put(new ANTLRHashString("attr", this), new Integer(6));
	literals.put(new ANTLRHashString("break", this), new Integer(11));
	literals.put(new ANTLRHashString("attrs", this), new Integer(7));
	literals.put(new ANTLRHashString("filter", this), new Integer(23));
	literals.put(new ANTLRHashString("sub", this), new Integer(39));
	literals.put(new ANTLRHashString("stop", this), new Integer(38));
	literals.put(new ANTLRHashString("val", this), new Integer(43));
	literals.put(new ANTLRHashString("level", this), new Integer(25));
	literals.put(new ANTLRHashString("by", this), new Integer(12));
	literals.put(new ANTLRHashString("children", this), new Integer(14));
}

public Token nextToken() throws TokenStreamException {
	Token theRetToken=null;
tryAgain:
	for (;;) {
		Token _token = null;
		int _ttype = Token.INVALID_TYPE;
		resetText();
		try {   // for char stream error handling
			try {   // for lexical error handling
				switch ( LA(1)) {
				case '0':  case '1':  case '2':  case '3':
				case '4':  case '5':  case '6':  case '7':
				case '8':  case '9':
				{
					mINTEGER(true);
					theRetToken=_returnToken;
					break;
				}
				case '"':
				{
					mDOUBLE_QUOTED_STRING(true);
					theRetToken=_returnToken;
					break;
				}
				case '{':
				{
					mOPEN_CURLY(true);
					theRetToken=_returnToken;
					break;
				}
				case '}':
				{
					mCLOSE_CURLY(true);
					theRetToken=_returnToken;
					break;
				}
				case '\t':  case '\n':  case '\r':  case ' ':
				{
					mSP(true);
					theRetToken=_returnToken;
					break;
				}
				case '=':
				{
					mEQUAL(true);
					theRetToken=_returnToken;
					break;
				}
				case '.':
				{
					mDOT(true);
					theRetToken=_returnToken;
					break;
				}
				case ',':
				{
					mSEP(true);
					theRetToken=_returnToken;
					break;
				}
				case '*':
				{
					mSTAR(true);
					theRetToken=_returnToken;
					break;
				}
				case '+':
				{
					mPLUS(true);
					theRetToken=_returnToken;
					break;
				}
				case '-':
				{
					mMINUS(true);
					theRetToken=_returnToken;
					break;
				}
				case '/':
				{
					mSLASH(true);
					theRetToken=_returnToken;
					break;
				}
				case '(':
				{
					mFILTER(true);
					theRetToken=_returnToken;
					break;
				}
				default:
					if ((LA(1)=='t') && (LA(2)=='r') && (LA(3)=='a') && (LA(4)=='n') && (LA(5)=='s') && (LA(6)=='p') && (LA(7)=='o') && (LA(8)=='r') && (LA(9)=='t') && (LA(10)=='_')) {
						mTRANSPORT_SSF(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='s') && (LA(2)=='a') && (LA(3)=='s') && (LA(4)=='l') && (LA(5)=='_')) {
						mSASL_SSF(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='s') && (LA(2)=='s') && (LA(3)=='f') && (LA(4)=='=')) {
						mSSF(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='t') && (LA(2)=='l') && (LA(3)=='s') && (LA(4)=='_')) {
						mTLS_SSF(true);
						theRetToken=_returnToken;
					}
					else if (((LA(1) >= 'a' && LA(1) <= 'z')) && (true) && (true) && (true) && (true) && (true) && (true) && (true) && (true) && (true)) {
						mIDENT(true);
						theRetToken=_returnToken;
					}
				else {
					if (LA(1)==EOF_CHAR) {uponEOF(); _returnToken = makeToken(Token.EOF_TYPE);}
				else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				}
				if ( _returnToken==null ) continue tryAgain; // found SKIP token
				_ttype = _returnToken.getType();
				_ttype = testLiteralsTable(_ttype);
				_returnToken.setType(_ttype);
				return _returnToken;
			}
			catch (RecognitionException e) {
				throw new TokenStreamRecognitionException(e);
			}
		}
		catch (CharStreamException cse) {
			if ( cse instanceof CharStreamIOException ) {
				throw new TokenStreamIOException(((CharStreamIOException)cse).io);
			}
			else {
				throw new TokenStreamException(cse.getMessage());
			}
		}
	}
}

	protected final void mDIGIT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DIGIT;
		int _saveIndex;
		
		switch ( LA(1)) {
		case '0':
		{
			match('0');
			break;
		}
		case '1':  case '2':  case '3':  case '4':
		case '5':  case '6':  case '7':  case '8':
		case '9':
		{
			mLDIGIT(false);
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mLDIGIT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LDIGIT;
		int _saveIndex;
		
		matchRange('1','9');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mINTEGER(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = INTEGER;
		int _saveIndex;
		
		if (((LA(1) >= '1' && LA(1) <= '9')) && ((LA(2) >= '0' && LA(2) <= '9'))) {
			{
			mLDIGIT(false);
			{
			int _cnt6=0;
			_loop6:
			do {
				if (((LA(1) >= '0' && LA(1) <= '9'))) {
					mDIGIT(false);
				}
				else {
					if ( _cnt6>=1 ) { break _loop6; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				
				_cnt6++;
			} while (true);
			}
			}
		}
		else if (((LA(1) >= '0' && LA(1) <= '9')) && (true)) {
			mDIGIT(false);
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mDOUBLE_QUOTED_STRING(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DOUBLE_QUOTED_STRING;
		int _saveIndex;
		
		_saveIndex=text.length();
		match('"');
		text.setLength(_saveIndex);
		{
		_loop9:
		do {
			if ((_tokenSet_0.member(LA(1)))) {
				matchNot('"');
			}
			else {
				break _loop9;
			}
			
		} while (true);
		}
		_saveIndex=text.length();
		match('"');
		text.setLength(_saveIndex);
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mIDENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = IDENT;
		int _saveIndex;
		
		{
		matchRange('a','z');
		}
		{
		_loop13:
		do {
			switch ( LA(1)) {
			case 'a':  case 'b':  case 'c':  case 'd':
			case 'e':  case 'f':  case 'g':  case 'h':
			case 'i':  case 'j':  case 'k':  case 'l':
			case 'm':  case 'n':  case 'o':  case 'p':
			case 'q':  case 'r':  case 's':  case 't':
			case 'u':  case 'v':  case 'w':  case 'x':
			case 'y':  case 'z':
			{
				matchRange('a','z');
				break;
			}
			case '0':  case '1':  case '2':  case '3':
			case '4':  case '5':  case '6':  case '7':
			case '8':  case '9':
			{
				mDIGIT(false);
				break;
			}
			case '-':
			{
				match('-');
				break;
			}
			default:
			{
				break _loop13;
			}
			}
		} while (true);
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mOPEN_CURLY(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = OPEN_CURLY;
		int _saveIndex;
		
		match('{');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCLOSE_CURLY(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CLOSE_CURLY;
		int _saveIndex;
		
		match('}');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSP(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SP;
		int _saveIndex;
		
		{
		int _cnt18=0;
		_loop18:
		do {
			if ((LA(1)==' ') && (true) && (true) && (true) && (true) && (true) && (true) && (true) && (true) && (true)) {
				match(' ');
			}
			else if ((LA(1)=='\t') && (true) && (true) && (true) && (true) && (true) && (true) && (true) && (true) && (true)) {
				match('\t');
			}
			else if ((LA(1)=='\n') && (true) && (true) && (true) && (true) && (true) && (true) && (true) && (true) && (true)) {
				match('\n');
				newline();
			}
			else if ((LA(1)=='\r') && (true) && (true) && (true) && (true) && (true) && (true) && (true) && (true) && (true)) {
				match('\r');
			}
			else {
				if ( _cnt18>=1 ) { break _loop18; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
			}
			
			_cnt18++;
		} while (true);
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSSF(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SSF;
		int _saveIndex;
		Token strength=null;
		
		match("ssf");
		mEQUAL(false);
		mINTEGER(true);
		strength=_returnToken;
		setText( strength.getText() );
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mEQUAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = EQUAL;
		int _saveIndex;
		
		match('=');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mTRANSPORT_SSF(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = TRANSPORT_SSF;
		int _saveIndex;
		Token strength=null;
		
		match("transport_ssf");
		mEQUAL(false);
		mINTEGER(true);
		strength=_returnToken;
		setText( strength.getText() );
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mTLS_SSF(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = TLS_SSF;
		int _saveIndex;
		Token strength=null;
		
		match("tls_ssf");
		mEQUAL(false);
		mINTEGER(true);
		strength=_returnToken;
		setText( strength.getText() );
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSASL_SSF(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SASL_SSF;
		int _saveIndex;
		Token strength=null;
		
		match("sasl_ssf");
		mEQUAL(false);
		mINTEGER(true);
		strength=_returnToken;
		setText( strength.getText() );
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mDOT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DOT;
		int _saveIndex;
		
		match('.');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSEP(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SEP;
		int _saveIndex;
		
		match(',');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSTAR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = STAR;
		int _saveIndex;
		
		match('*');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mPLUS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = PLUS;
		int _saveIndex;
		
		match('+');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mMINUS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = MINUS;
		int _saveIndex;
		
		match('-');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSLASH(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SLASH;
		int _saveIndex;
		
		match('/');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mFILTER(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = FILTER;
		int _saveIndex;
		
		match('(');
		{
		if ((_tokenSet_1.member(LA(1))) && (_tokenSet_2.member(LA(2))) && ((LA(3) >= '\u0000' && LA(3) <= '\u00ff')) && (true) && (true) && (true) && (true) && (true) && (true) && (true)) {
			mSP(false);
		}
		else if ((_tokenSet_2.member(LA(1))) && ((LA(2) >= '\u0000' && LA(2) <= '\u00ff')) && (true) && (true) && (true) && (true) && (true) && (true) && (true) && (true)) {
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		}
		{
		switch ( LA(1)) {
		case '&':
		{
			{
			match('&');
			{
			switch ( LA(1)) {
			case '\t':  case '\n':  case '\r':  case ' ':
			{
				mSP(false);
				break;
			}
			case '(':
			{
				break;
			}
			default:
			{
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			}
			}
			{
			int _cnt36=0;
			_loop36:
			do {
				if ((LA(1)=='(')) {
					mFILTER(false);
				}
				else {
					if ( _cnt36>=1 ) { break _loop36; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				
				_cnt36++;
			} while (true);
			}
			}
			break;
		}
		case '|':
		{
			{
			match('|');
			{
			switch ( LA(1)) {
			case '\t':  case '\n':  case '\r':  case ' ':
			{
				mSP(false);
				break;
			}
			case '(':
			{
				break;
			}
			default:
			{
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			}
			}
			{
			int _cnt40=0;
			_loop40:
			do {
				if ((LA(1)=='(')) {
					mFILTER(false);
				}
				else {
					if ( _cnt40>=1 ) { break _loop40; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				
				_cnt40++;
			} while (true);
			}
			}
			break;
		}
		case '!':
		{
			{
			match('!');
			{
			switch ( LA(1)) {
			case '\t':  case '\n':  case '\r':  case ' ':
			{
				mSP(false);
				break;
			}
			case '(':
			{
				break;
			}
			default:
			{
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			}
			}
			mFILTER(false);
			}
			break;
		}
		default:
			if ((_tokenSet_3.member(LA(1)))) {
				mFILTER_VALUE(false);
			}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		{
		switch ( LA(1)) {
		case '\t':  case '\n':  case '\r':  case ' ':
		{
			mSP(false);
			break;
		}
		case ')':
		{
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		match(')');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mFILTER_VALUE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = FILTER_VALUE;
		int _saveIndex;
		
		{
		{
		match(_tokenSet_3);
		}
		{
		_loop49:
		do {
			if ((_tokenSet_4.member(LA(1))) && ((LA(2) >= '\u0000' && LA(2) <= '\u00ff')) && (true) && (true) && (true) && (true) && (true) && (true) && (true) && (true)) {
				{
				match(_tokenSet_4);
				}
			}
			else {
				break _loop49;
			}
			
		} while (true);
		}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	
	private static final long[] mk_tokenSet_0() {
		long[] data = new long[8];
		data[0]=-17179869185L;
		for (int i = 1; i<=3; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 4294977024L, 0L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = new long[8];
		data[0]=-3298534883329L;
		for (int i = 1; i<=3; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = new long[8];
		data[0]=-3582002724865L;
		data[1]=-1152921504606846977L;
		for (int i = 2; i<=3; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = new long[8];
		data[0]=-2199023255553L;
		for (int i = 1; i<=3; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	
	}
