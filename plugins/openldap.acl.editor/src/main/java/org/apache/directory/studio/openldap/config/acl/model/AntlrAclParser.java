// $ANTLR 2.7.7 (20060906): "Acl.g" -> "AntlrAclParser.java"$


package org.apache.directory.studio.openldap.config.acl.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;

import java.util.List;

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

/**
 * The ANTLR generated OpenLDAP ACL parser.
 */
public class AntlrAclParser extends antlr.LLkParser       implements AntlrAclLexerTokenTypes
 {

    private static final Logger log = LoggerFactory.getLogger( AntlrAclParser.class );
    
    private AclItem aclItem;
    
    public AclItem getAclItem()
    {
        return aclItem;
    }

protected AntlrAclParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public AntlrAclParser(TokenBuffer tokenBuf) {
  this(tokenBuf,2);
}

protected AntlrAclParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public AntlrAclParser(TokenStream lexer) {
  this(lexer,2);
}

public AntlrAclParser(ParserSharedInputState state) {
  super(state,2);
  tokenNames = _tokenNames;
}

	public final void parse() throws RecognitionException, TokenStreamException {
		
		
		log.debug( "entered parse()" );
				System.out.println( "entered parse_init()" );
		
		
		{
		if ((LA(1)==SP) && (LA(2)==ID_access||LA(2)==ID_to||LA(2)==SP)) {
			match(SP);
		}
		else if ((LA(1)==ID_access||LA(1)==ID_to||LA(1)==SP) && (LA(2)==ID_to||LA(2)==SP)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		aclItem();
		{
		switch ( LA(1)) {
		case SP:
		{
			match(SP);
			break;
		}
		case EOF:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(Token.EOF_TYPE);
	}
	
	public final void aclItem() throws RecognitionException, TokenStreamException {
		
		
		log.debug( "entered aclItem()" );
				System.out.println( "entered aclItem()" );
		
		aclItem = new AclItem();
		
		
		{
		switch ( LA(1)) {
		case ID_access:
		{
			match(ID_access);
			match(SP);
			break;
		}
		case ID_to:
		case SP:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		switch ( LA(1)) {
		case SP:
		{
			match(SP);
			break;
		}
		case ID_to:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(ID_to);
		match(SP);
		{
		switch ( LA(1)) {
		case ID_attr:
		case ID_attrs:
		case ID_dn:
		case ID_filter:
		case STAR:
		{
			what();
			match(SP);
			break;
		}
		case ID_by:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		int _cnt58=0;
		_loop58:
		do {
			if ((LA(1)==ID_by)) {
				match(ID_by);
				match(SP);
				who();
			}
			else {
				if ( _cnt58>=1 ) { break _loop58; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			
			_cnt58++;
		} while (true);
		}
		
				if ( aclItem.getWhatClause() == null )
				{
					// The 'what' is equivalent to '*'
					aclItem.setWhatClause( new AclWhatClauseStar() );
				}
			
	}
	
	public final void what() throws RecognitionException, TokenStreamException {
		
		
		log.debug( "entered what()" );
				System.out.println( "entered what()" );
		
		
		{
		switch ( LA(1)) {
		case ID_dn:
		{
			match(ID_dn);
			what_dn();
			break;
		}
		case ID_filter:
		{
			what_filter();
			break;
		}
		case ID_attr:
		case ID_attrs:
		{
			what_attrs();
			break;
		}
		case STAR:
		{
			what_star();
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
	}
	
	public final void who() throws RecognitionException, TokenStreamException {
		
		
		log.debug( "entered who()" );
		
		
		{
		switch ( LA(1)) {
		case STAR:
		{
			who_star();
			break;
		}
		case ID_anonymous:
		{
			who_anonymous();
			break;
		}
		case ID_users:
		{
			who_users();
			break;
		}
		case ID_self:
		{
			who_self();
			break;
		}
		case ID_dn:
		{
			who_dn();
			break;
		}
		case ID_dnattr:
		{
			who_dnattr();
			break;
		}
		case ID_group:
		{
			who_group();
			break;
		}
		case SSF:
		{
			who_ssf();
			break;
		}
		case TRANSPORT_SSF:
		{
			who_transport_ssf();
			break;
		}
		case TLS_SSF:
		{
			who_tls_ssf();
			break;
		}
		case SASL_SSF:
		{
			who_sasl_ssf();
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		if ((LA(1)==SP) && (_tokenSet_0.member(LA(2)))) {
			match(SP);
			who_access_level();
		}
		else if ((LA(1)==EOF||LA(1)==ID_by||LA(1)==SP) && (_tokenSet_1.member(LA(2)))) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		{
		if ((LA(1)==SP) && (LA(2)==ID_break||LA(2)==ID_continue||LA(2)==ID_stop)) {
			match(SP);
			who_control();
		}
		else if ((LA(1)==EOF||LA(1)==ID_by||LA(1)==SP) && (LA(2)==EOF||LA(2)==SP)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
	}
	
	public final void what_dn() throws RecognitionException, TokenStreamException {
		
		Token  quoted_token = null;
		Token  string_token = null;
		
			    log.debug( "entered what_dn()" );
				System.out.println( "entered what_dn()" );
			
		
		if ((LA(1)==DOT) && (LA(2)==ID_exact||LA(2)==ID_regex)) {
			basic_dn_style();
		}
		else if ((LA(1)==DOT) && (_tokenSet_2.member(LA(2)))) {
			scope_dn_clause();
		}
		else if ((LA(1)==SP||LA(1)==EQUAL)) {
			{
			switch ( LA(1)) {
			case SP:
			{
				match(SP);
				break;
			}
			case EQUAL:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(EQUAL);
			{
			switch ( LA(1)) {
			case SP:
			{
				match(SP);
				break;
			}
			case DOUBLE_QUOTED_STRING:
			case STRING:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case DOUBLE_QUOTED_STRING:
			{
				
							System.out.println( "what-dn default" );
						
				quoted_token = LT(1);
				match(DOUBLE_QUOTED_STRING);
				
							AclWhatClauseDn whatClauseDn = new AclWhatClauseDn();
							String dnString = quoted_token.getText();
				
							try
							{
								new Dn( dnString );
							}
							catch ( LdapInvalidDnException lide )
							{
								throw new  RecognitionException( "The DN is invalid : " + dnString );
							}
							
							whatClauseDn.setPattern( dnString );
							aclItem.setWhatClause( whatClauseDn );
					
				break;
			}
			case STRING:
			{
				string_token = LT(1);
				match(STRING);
				
							AclWhatClauseDn whatClauseDn = new AclWhatClauseDn();
							String dnString = string_token.getText();
							
							try
							{
								new Dn( dnString );
							}
							catch ( LdapInvalidDnException lide )
							{
								throw new  RecognitionException( "The DN is invalid" + dnString );
							}
							
							whatClauseDn.setPattern( dnString );
							aclItem.setWhatClause( whatClauseDn );
					
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
	}
	
	public final void what_filter() throws RecognitionException, TokenStreamException {
		
		Token  token = null;
		
			    log.debug( "entered what_filter()" );
			    System.out.println( "entered what_filter()" );
		
			    AclWhatClauseFilter whatClauseFilter = new AclWhatClauseFilter();
			
		
		match(ID_filter);
		{
		switch ( LA(1)) {
		case SP:
		{
			match(SP);
			break;
		}
		case EQUAL:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(EQUAL);
		{
		switch ( LA(1)) {
		case SP:
		{
			match(SP);
			break;
		}
		case FILTER:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		token = LT(1);
		match(FILTER);
		
				// TODO : check tah the filter is valid
		whatClauseFilter.setFilter( token.getText() );
		
		aclItem.setWhatClause( whatClauseFilter );
		
	}
	
	public final void what_attrs() throws RecognitionException, TokenStreamException {
		
		
			    log.debug( "entered what_attrs()" );
			    System.out.println( "entered what_attrs()" );
		
			    //AclWhatClauseAttributes whatClauseAttributes = new AclWhatClauseAttributess();
			
		
		{
		switch ( LA(1)) {
		case ID_attrs:
		{
			match(ID_attrs);
			break;
		}
		case ID_attr:
		{
			match(ID_attr);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		switch ( LA(1)) {
		case SP:
		{
			match(SP);
			break;
		}
		case EQUAL:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(EQUAL);
		{
		switch ( LA(1)) {
		case SP:
		{
			match(SP);
			break;
		}
		case ID_children:
		case ID_entry:
		case IDENT:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		what_attrs_list();
	}
	
	public final void what_star() throws RecognitionException, TokenStreamException {
		
		
			log.debug( "entered what_star()" );
				System.out.println( "entered what_star()" );
			
		
		match(STAR);
		
		aclItem.setWhatClause( new AclWhatClauseStar() );
		
	}
	
	public final void basic_dn_style() throws RecognitionException, TokenStreamException {
		
		
			    log.debug( "entered basic_dn_style()" );
				System.out.println( "entered basic_dn_style()" );
			
		
		if ((LA(1)==DOT) && (LA(2)==ID_exact)) {
			exact_basic_dn_style();
		}
		else if ((LA(1)==DOT) && (LA(2)==ID_regex)) {
			regex_basic_dn_style();
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
	}
	
	public final void scope_dn_clause() throws RecognitionException, TokenStreamException {
		
		Token  quoted_token = null;
		Token  string_token = null;
		
			    log.debug( "entered scope_dn_clause()" );
				System.out.println( "entered scope_dn_clause()" );
				AclWhatClauseDn whatClauseDn = new AclWhatClauseDn();
			
		
		scope_dn_style(whatClauseDn);
		{
		switch ( LA(1)) {
		case SP:
		{
			match(SP);
			break;
		}
		case EQUAL:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(EQUAL);
		{
		switch ( LA(1)) {
		case SP:
		{
			match(SP);
			break;
		}
		case DOUBLE_QUOTED_STRING:
		case STRING:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		switch ( LA(1)) {
		case DOUBLE_QUOTED_STRING:
		{
			quoted_token = LT(1);
			match(DOUBLE_QUOTED_STRING);
			
				        whatClauseDn.setPattern( quoted_token.getText() );
				
			break;
		}
		case STRING:
		{
			string_token = LT(1);
			match(STRING);
			
				        whatClauseDn.setPattern( string_token.getText() );
				
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		
				aclItem.setWhatClause( whatClauseDn );
			
	}
	
	public final void exact_basic_dn_style() throws RecognitionException, TokenStreamException {
		
		Token  quoted_token = null;
		Token  string_token = null;
		
			    log.debug( "entered exact_basic_dn_style()" );
				System.out.println( "entered basic_dn_style()" );
				AclWhatClauseDn whatClauseDn = new AclWhatClauseDn();
				whatClauseDn.setType( AclWhatClauseDnTypeEnum.EXACT );
			
		
		match(DOT);
		{
		match(ID_exact);
		{
		switch ( LA(1)) {
		case SP:
		{
			match(SP);
			break;
		}
		case EQUAL:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(EQUAL);
		{
		switch ( LA(1)) {
		case SP:
		{
			match(SP);
			break;
		}
		case DOUBLE_QUOTED_STRING:
		case STRING:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		switch ( LA(1)) {
		case DOUBLE_QUOTED_STRING:
		{
			quoted_token = LT(1);
			match(DOUBLE_QUOTED_STRING);
			
							String dnString = quoted_token.getText();
			
							try
							{
								new Dn( dnString );
							}
							catch ( LdapInvalidDnException lide )
							{
								throw new  RecognitionException( "The DN is invalid" + dnString );
							}
							
							whatClauseDn.setPattern( dnString );
						
			break;
		}
		case STRING:
		{
			string_token = LT(1);
			match(STRING);
			
							String dnString = string_token.getText();
							
							try
							{
								new Dn( dnString );
							}
							catch ( LdapInvalidDnException lide )
							{
								throw new  RecognitionException( "The DN is invalid" + dnString );
							}
							
							whatClauseDn.setPattern( dnString );
						
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		}
		
				aclItem.setWhatClause( whatClauseDn );
			
	}
	
	public final void regex_basic_dn_style() throws RecognitionException, TokenStreamException {
		
		Token  quoted_token = null;
		Token  string_token = null;
		
			    log.debug( "entered regex_basic_dn_style()" );
				System.out.println( "entered regex_basic_dn_style()" );
				AclWhatClauseDn whatClauseDn = new AclWhatClauseDn();
				whatClauseDn.setType( AclWhatClauseDnTypeEnum.REGEX );
			
		
		match(DOT);
		{
		match(ID_regex);
		{
		switch ( LA(1)) {
		case SP:
		{
			match(SP);
			break;
		}
		case EQUAL:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(EQUAL);
		{
		switch ( LA(1)) {
		case SP:
		{
			match(SP);
			break;
		}
		case DOUBLE_QUOTED_STRING:
		case STRING:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		
					System.out.println( "In '='" );
				
		{
		switch ( LA(1)) {
		case DOUBLE_QUOTED_STRING:
		{
			quoted_token = LT(1);
			match(DOUBLE_QUOTED_STRING);
			
							whatClauseDn.setPattern( quoted_token.getText() );
						
			break;
		}
		case STRING:
		{
			string_token = LT(1);
			match(STRING);
			
							whatClauseDn.setPattern( string_token.getText() );
						
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		}
		
				aclItem.setWhatClause( whatClauseDn );
			
	}
	
	public final void scope_dn_style(
		AclWhatClauseDn whatClauseDn
	) throws RecognitionException, TokenStreamException {
		
		
			    log.debug( "entered scope_dn_style()" );
				System.out.println( "entered scope_dn_style()" );
			
		
		match(DOT);
		{
		switch ( LA(1)) {
		case ID_base:
		{
			match(ID_base);
			
				        whatClauseDn.setType( AclWhatClauseDnTypeEnum.BASE );
				
			break;
		}
		case ID_base_object:
		{
			match(ID_base_object);
			
				        whatClauseDn.setType( AclWhatClauseDnTypeEnum.BASE_OBJECT );
				
			break;
		}
		case ID_one:
		{
			match(ID_one);
			
				        whatClauseDn.setType( AclWhatClauseDnTypeEnum.ONE );
				
			break;
		}
		case ID_one_level:
		{
			match(ID_one_level);
			
				        whatClauseDn.setType( AclWhatClauseDnTypeEnum.ONE_LEVEL );
				
			break;
		}
		case ID_sub:
		{
			match(ID_sub);
			
				        whatClauseDn.setType( AclWhatClauseDnTypeEnum.SUB );
				
			break;
		}
		case ID_subtree:
		{
			match(ID_subtree);
			
				        whatClauseDn.setType( AclWhatClauseDnTypeEnum.SUBTREE );
				
			break;
		}
		case ID_children:
		{
			match(ID_children);
			
				        whatClauseDn.setType( AclWhatClauseDnTypeEnum.CHILDREN );
				
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
	}
	
	public final void what_attrs_list() throws RecognitionException, TokenStreamException {
		
		Token  attribute = null;
		
			    log.debug( "entered what_attrs_list()" );
			    System.out.println( "entered what_attrs_list()" );
				
			
		
		{
		switch ( LA(1)) {
		case IDENT:
		{
			attribute = LT(1);
			match(IDENT);
			{
			switch ( LA(1)) {
			case SP:
			{
				{
				if ((LA(1)==SP) && (LA(2)==ID_val)) {
					attr_val();
				}
				else if ((LA(1)==SP) && (LA(2)==ID_by)) {
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				
				}
				break;
			}
			case SEP:
			{
				match(SEP);
				attr_list();
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			
						// We are not allowed to have more than one attribute 
						// if we have a val
					
			break;
		}
		case ID_entry:
		{
			match(ID_entry);
			break;
		}
		case ID_children:
		{
			match(ID_children);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
	}
	
	public final void attr_val() throws RecognitionException, TokenStreamException {
		
		
			    log.debug( "entered what_attrs_list()" );
			    System.out.println( "entered what_attrs_list()" );
			
		
		match(SP);
		match(ID_val);
		{
		switch ( LA(1)) {
		case SLASH:
		{
			matching_rule();
			break;
		}
		case SP:
		case DOT:
		case EQUAL:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		switch ( LA(1)) {
		case DOT:
		{
			attr_val_style();
			break;
		}
		case SP:
		case EQUAL:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		switch ( LA(1)) {
		case SP:
		{
			match(SP);
			break;
		}
		case EQUAL:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(EQUAL);
		{
		switch ( LA(1)) {
		case SP:
		{
			match(SP);
			break;
		}
		case REGEX:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(REGEX);
	}
	
	public final void attr_list() throws RecognitionException, TokenStreamException {
		
		
			    log.debug( "entered attr_list()" );
			    System.out.println( "entered attr_list()" );
			
		
		{
		switch ( LA(1)) {
		case IDENT:
		{
			match(IDENT);
			break;
		}
		case ID_entry:
		{
			match(ID_entry);
			break;
		}
		case ID_children:
		{
			match(ID_children);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		_loop105:
		do {
			if ((LA(1)==SEP) && (LA(2)==ID_children||LA(2)==ID_entry||LA(2)==IDENT)) {
				match(SEP);
				attr_list();
			}
			else {
				break _loop105;
			}
			
		} while (true);
		}
	}
	
	public final void matching_rule() throws RecognitionException, TokenStreamException {
		
		
			    log.debug( "entered matching_rule()" );
			    System.out.println( "entered matching_rule()" );
			
		
		match(SLASH);
		match(IDENT);
	}
	
	public final void attr_val_style() throws RecognitionException, TokenStreamException {
		
		
			    log.debug( "entered attr_val_style()" );
			    System.out.println( "entered attr_val_style()" );
			
		
		match(DOT);
		{
		switch ( LA(1)) {
		case ID_exact:
		{
			match(ID_exact);
			
			//whatClauseAttributes.setStyle( AclAttributeStyleEnum.EXACT );
			
			break;
		}
		case ID_base:
		{
			match(ID_base);
			
			//whatClauseAttributes.setStyle( AclAttributeStyleEnum.BASE );
			
			break;
		}
		case ID_base_object:
		{
			match(ID_base_object);
			
			//whatClauseAttributes.setStyle( AclAttributeStyleEnum.BASE_OBJECT );
			
			break;
		}
		case ID_regex:
		{
			match(ID_regex);
			
			//whatClauseAttributes.setStyle( AclAttributeStyleEnum.REGEX );
			
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
	}
	
	public final void who_star() throws RecognitionException, TokenStreamException {
		
		
		log.debug( "entered who_star()" );
		
		
		match(STAR);
		
		aclItem.addWhoClause( new AclWhoClauseStar() );
		
	}
	
	public final void who_anonymous() throws RecognitionException, TokenStreamException {
		
		
		log.debug( "entered who_anonymous()" );
		
		
		match(ID_anonymous);
		
		aclItem.addWhoClause( new AclWhoClauseAnonymous() );
		
	}
	
	public final void who_users() throws RecognitionException, TokenStreamException {
		
		
		log.debug( "entered who_users()" );
		
		
		match(ID_users);
		
		aclItem.addWhoClause( new AclWhoClauseUsers() );
		
	}
	
	public final void who_self() throws RecognitionException, TokenStreamException {
		
		
		log.debug( "entered who_self()" );
		
		
		match(ID_self);
		
		aclItem.addWhoClause( new AclWhoClauseSelf() );
		
	}
	
	public final void who_dn() throws RecognitionException, TokenStreamException {
		
		Token  token = null;
		
		log.debug( "entered who_dn()" );
		
		AclWhoClauseDn whoClauseDn = new AclWhoClauseDn();
		aclItem.addWhoClause( whoClauseDn ); 
		
		
		match(ID_dn);
		{
		switch ( LA(1)) {
		case DOT:
		{
			match(DOT);
			who_dn_type();
			{
			switch ( LA(1)) {
			case SEP:
			{
				match(SEP);
				who_dn_modifier();
				break;
			}
			case SP:
			case EQUAL:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			break;
		}
		case SP:
		case EQUAL:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		switch ( LA(1)) {
		case SP:
		{
			match(SP);
			break;
		}
		case EQUAL:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(EQUAL);
		{
		switch ( LA(1)) {
		case SP:
		{
			match(SP);
			break;
		}
		case DOUBLE_QUOTED_STRING:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		token = LT(1);
		match(DOUBLE_QUOTED_STRING);
		
		whoClauseDn.setPattern( token.getText() );
		
	}
	
	public final void who_dnattr() throws RecognitionException, TokenStreamException {
		
		Token  token = null;
		
		log.debug( "entered who_dnattr()" );
		
		AclWhoClauseDnAttr whoClauseDnAttr = new AclWhoClauseDnAttr();
		aclItem.addWhoClause( whoClauseDnAttr ); 
		
		
		match(ID_dnattr);
		{
		switch ( LA(1)) {
		case SP:
		{
			match(SP);
			break;
		}
		case EQUAL:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(EQUAL);
		{
		switch ( LA(1)) {
		case SP:
		{
			match(SP);
			break;
		}
		case IDENT:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		token = LT(1);
		match(IDENT);
		
		whoClauseDnAttr.setAttribute( token.getText() );
		
	}
	
	public final void who_group() throws RecognitionException, TokenStreamException {
		
		Token  objectclass = null;
		Token  attrname = null;
		Token  pattern = null;
		
		log.debug( "entered who_group()" );
		
		AclWhoClauseGroup whoClauseGroup = new AclWhoClauseGroup();
		aclItem.addWhoClause( whoClauseGroup ); 
		
		
		match(ID_group);
		{
		switch ( LA(1)) {
		case SLASH:
		{
			match(SLASH);
			objectclass = LT(1);
			match(IDENT);
			{
			switch ( LA(1)) {
			case SLASH:
			{
				match(SLASH);
				attrname = LT(1);
				match(IDENT);
				break;
			}
			case DOT:
			case EQUAL:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			break;
		}
		case DOT:
		case EQUAL:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		switch ( LA(1)) {
		case DOT:
		{
			match(DOT);
			who_group_type();
			break;
		}
		case EQUAL:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(EQUAL);
		pattern = LT(1);
		match(DOUBLE_QUOTED_STRING);
		
		if ( objectclass != null )
		{
		whoClauseGroup.setObjectclass( objectclass.getText() );
		}
		
		if ( attrname != null )
		{
		whoClauseGroup.setAttribute( attrname.getText() );
		}
		
		if ( pattern != null )
		{
		whoClauseGroup.setPattern( pattern.getText() );
		}
		
	}
	
	public final void who_ssf() throws RecognitionException, TokenStreamException {
		
		Token  strength = null;
		
		log.debug( "entered who_ssf()" );
		
		AclWhoClauseSsf whoClauseSsf = new AclWhoClauseSsf();
		aclItem.addWhoClause( whoClauseSsf ); 
		
		
		strength = LT(1);
		match(SSF);
		
		whoClauseSsf.setStrength( Integer.valueOf( strength.getText() ) );
		
	}
	
	public final void who_transport_ssf() throws RecognitionException, TokenStreamException {
		
		Token  strength = null;
		
		log.debug( "entered who_transport_ssf()" );
		
		AclWhoClauseTransportSsf whoClauseTransportSsf = new AclWhoClauseTransportSsf();
		aclItem.addWhoClause( whoClauseTransportSsf ); 
		
		
		strength = LT(1);
		match(TRANSPORT_SSF);
		
		whoClauseTransportSsf.setStrength( Integer.valueOf( strength.getText() ) );
		
	}
	
	public final void who_tls_ssf() throws RecognitionException, TokenStreamException {
		
		Token  strength = null;
		
		log.debug( "entered who_tls_ssf()" );
		
		AclWhoClauseTlsSsf whoClauseTlsSsf = new AclWhoClauseTlsSsf();
		aclItem.addWhoClause( whoClauseTlsSsf ); 
		
		
		strength = LT(1);
		match(TLS_SSF);
		
		whoClauseTlsSsf.setStrength( Integer.valueOf( strength.getText() ) );
		
	}
	
	public final void who_sasl_ssf() throws RecognitionException, TokenStreamException {
		
		Token  strength = null;
		
		log.debug( "entered who_sasl_ssf()" );
		
		AclWhoClauseSaslSsf whoClauseSaslSsf = new AclWhoClauseSaslSsf();
		aclItem.addWhoClause( whoClauseSaslSsf ); 
		
		
		strength = LT(1);
		match(SASL_SSF);
		
		whoClauseSaslSsf.setStrength( Integer.valueOf( strength.getText() ) );
		
	}
	
	public final void who_access_level() throws RecognitionException, TokenStreamException {
		
		
		log.debug( "entered who_access_level()" );
		
		List<AclWhoClause> whoClauses = aclItem.getWhoClauses();
		
		if ( whoClauses.size() == 0 )
		{
		// Throw an exception ?
		return;
		}
		
		AclWhoClause whoClause = aclItem.getWhoClauses().get( whoClauses.size() - 1 );
		AclAccessLevel accessLevel = new AclAccessLevel();
		whoClause.setAccessLevel( accessLevel );
		
		
		switch ( LA(1)) {
		case ID_self:
		{
			match(ID_self);
			match(SP);
			{
			switch ( LA(1)) {
			case ID_auth:
			case ID_compare:
			case ID_disclose:
			case ID_manage:
			case ID_none:
			case ID_read:
			case ID_search:
			case ID_write:
			{
				who_access_level_level();
				break;
			}
			case EQUAL:
			case PLUS:
			case MINUS:
			{
				who_access_level_priv();
				break;
			}
			case EOF:
			case ID_by:
			case SP:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			
			accessLevel.setSelf( true );
			
			break;
		}
		case EOF:
		case ID_auth:
		case ID_by:
		case ID_compare:
		case ID_disclose:
		case ID_manage:
		case ID_none:
		case ID_read:
		case ID_search:
		case ID_write:
		case SP:
		case EQUAL:
		case PLUS:
		case MINUS:
		{
			{
			switch ( LA(1)) {
			case ID_auth:
			case ID_compare:
			case ID_disclose:
			case ID_manage:
			case ID_none:
			case ID_read:
			case ID_search:
			case ID_write:
			{
				who_access_level_level();
				break;
			}
			case EQUAL:
			case PLUS:
			case MINUS:
			{
				who_access_level_priv();
				break;
			}
			case EOF:
			case ID_by:
			case SP:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			
			accessLevel.setSelf( false );
			
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void who_control() throws RecognitionException, TokenStreamException {
		
		
		log.debug( "entered who_control()" );
		
		List<AclWhoClause> whoClauses = aclItem.getWhoClauses();
		
		if ( whoClauses.size() == 0 )
		{
		// Throw an exception ?
		return;
		}
		
		AclWhoClause whoClause = aclItem.getWhoClauses().get( whoClauses.size() - 1 );
		
		
		switch ( LA(1)) {
		case ID_stop:
		{
			match(ID_stop);
			
			whoClause.setControl( AclControlEnum.STOP );
			
			break;
		}
		case ID_continue:
		{
			match(ID_continue);
			
			whoClause.setControl( AclControlEnum.CONTINUE );
			
			break;
		}
		case ID_break:
		{
			match(ID_break);
			
			whoClause.setControl( AclControlEnum.BREAK );
			
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void who_group_type() throws RecognitionException, TokenStreamException {
		
		
		log.debug( "entered who_group_type()" );
		
		List<AclWhoClause> whoClauses = aclItem.getWhoClauses();
		AclWhoClause whoClause = aclItem.getWhoClauses().get( whoClauses.size() - 1 );
		
		if ( !( whoClause instanceof AclWhoClauseGroup ) )
		{
		// Throw an exception ?
		return;
		}
		AclWhoClauseGroup whoClauseGroup =  ( AclWhoClauseGroup ) whoClause;
		
		
		switch ( LA(1)) {
		case ID_exact:
		{
			match(ID_exact);
			
			whoClauseGroup.setType( AclWhoClauseGroupTypeEnum.EXACT );
			
			break;
		}
		case ID_expand:
		{
			match(ID_expand);
			
			whoClauseGroup.setType( AclWhoClauseGroupTypeEnum.EXPAND );
			
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void who_dn_type() throws RecognitionException, TokenStreamException {
		
		Token  token = null;
		
		log.debug( "entered who_dn_type()" );
		
		List<AclWhoClause> whoClauses = aclItem.getWhoClauses();
		AclWhoClause whoClause = aclItem.getWhoClauses().get( whoClauses.size() - 1 );
		
		if ( !( whoClause instanceof AclWhoClauseDn ) )
		{
		// Throw an exception ?
		return;
		}
		
		AclWhoClauseDn whoClauseDn =  ( AclWhoClauseDn ) whoClause;
		
		
		switch ( LA(1)) {
		case ID_regex:
		{
			match(ID_regex);
			
			whoClauseDn.setType( AclWhoClauseDnTypeEnum.REGEX );
			
			break;
		}
		case ID_base:
		{
			match(ID_base);
			
			whoClauseDn.setType( AclWhoClauseDnTypeEnum.BASE );
			
			break;
		}
		case ID_exact:
		{
			match(ID_exact);
			
			whoClauseDn.setType( AclWhoClauseDnTypeEnum.EXACT );
			
			break;
		}
		case ID_one:
		{
			match(ID_one);
			
			whoClauseDn.setType( AclWhoClauseDnTypeEnum.ONE );
			
			break;
		}
		case ID_subtree:
		{
			match(ID_subtree);
			
			whoClauseDn.setType( AclWhoClauseDnTypeEnum.SUBTREE );
			
			break;
		}
		case ID_children:
		{
			match(ID_children);
			
			whoClauseDn.setType( AclWhoClauseDnTypeEnum.CHILDREN );
			
			break;
		}
		case ID_level:
		{
			match(ID_level);
			match(OPEN_CURLY);
			token = LT(1);
			match(INTEGER);
			match(CLOSE_CURLY);
			
			AclWhoClauseDnTypeEnum levelType = AclWhoClauseDnTypeEnum.LEVEL;
			levelType.setLevel( Integer.valueOf( token.getText() ) );
			whoClauseDn.setType( levelType );
			
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void who_dn_modifier() throws RecognitionException, TokenStreamException {
		
		
		log.debug( "entered who_dn_modifier()" );
		
		List<AclWhoClause> whoClauses = aclItem.getWhoClauses();
		AclWhoClause whoClause = aclItem.getWhoClauses().get( whoClauses.size() - 1 );
		
		if ( !( whoClause instanceof AclWhoClauseDn ) )
		{
		// Throw an exception ?
		return;
		}
		
		AclWhoClauseDn whoClauseDn =  ( AclWhoClauseDn ) whoClause;
		
		
		match(ID_expand);
		
		whoClauseDn.setModifier( AclWhoClauseDnModifierEnum.EXPAND );
		
	}
	
	public final void who_access_level_level() throws RecognitionException, TokenStreamException {
		
		
		log.debug( "entered who_access_level_level()" );
		
		List<AclWhoClause> whoClauses = aclItem.getWhoClauses();
		
		if ( whoClauses.size() == 0 )
		{
		// Throw an exception ?
		return;
		}
		
		AclWhoClause whoClause = aclItem.getWhoClauses().get( whoClauses.size() - 1 );
		
		AclAccessLevel accessLevel = whoClause.getAccessLevel();
		
		if ( accessLevel == null )
		{
		// Throw an exception ?
		return;
		}
		
		
		switch ( LA(1)) {
		case ID_manage:
		{
			match(ID_manage);
			
			accessLevel.setLevel( AclAccessLevelLevelEnum.MANAGE );
			
			break;
		}
		case ID_write:
		{
			match(ID_write);
			
			accessLevel.setLevel( AclAccessLevelLevelEnum.WRITE );
			
			break;
		}
		case ID_read:
		{
			match(ID_read);
			
			accessLevel.setLevel( AclAccessLevelLevelEnum.READ );
			
			break;
		}
		case ID_search:
		{
			match(ID_search);
			
			accessLevel.setLevel( AclAccessLevelLevelEnum.SEARCH );
			
			break;
		}
		case ID_compare:
		{
			match(ID_compare);
			
			accessLevel.setLevel( AclAccessLevelLevelEnum.COMPARE );
			
			break;
		}
		case ID_auth:
		{
			match(ID_auth);
			
			accessLevel.setLevel( AclAccessLevelLevelEnum.AUTH );
			
			break;
		}
		case ID_disclose:
		{
			match(ID_disclose);
			
			accessLevel.setLevel( AclAccessLevelLevelEnum.DISCLOSE );
			
			break;
		}
		case ID_none:
		{
			match(ID_none);
			
			accessLevel.setLevel( AclAccessLevelLevelEnum.NONE );
			
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void who_access_level_priv() throws RecognitionException, TokenStreamException {
		
		
		log.debug( "entered who_access_level_priv()" );
		
		
		who_access_level_priv_modifier();
		{
		int _cnt135=0;
		_loop135:
		do {
			if ((_tokenSet_3.member(LA(1)))) {
				who_access_level_priv_priv();
			}
			else {
				if ( _cnt135>=1 ) { break _loop135; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			
			_cnt135++;
		} while (true);
		}
	}
	
	public final void who_access_level_priv_modifier() throws RecognitionException, TokenStreamException {
		
		
		log.debug( "entered who_access_level_priv_modifier()" );
		
		List<AclWhoClause> whoClauses = aclItem.getWhoClauses();
		
		if ( whoClauses.size() == 0 )
		{
		// Throw an exception ?
		return;
		}
		
		AclWhoClause whoClause = aclItem.getWhoClauses().get( whoClauses.size() - 1 );
		
		AclAccessLevel accessLevel = whoClause.getAccessLevel();
		
		if ( accessLevel == null )
		{
		// Throw an exception ?
		return;
		}
		
		
		switch ( LA(1)) {
		case EQUAL:
		{
			match(EQUAL);
			
			accessLevel.setPrivilegeModifier( AclAccessLevelPrivModifierEnum.EQUAL );
			
			break;
		}
		case PLUS:
		{
			match(PLUS);
			
			accessLevel.setPrivilegeModifier( AclAccessLevelPrivModifierEnum.PLUS );
			
			break;
		}
		case MINUS:
		{
			match(MINUS);
			
			accessLevel.setPrivilegeModifier( AclAccessLevelPrivModifierEnum.MINUS );
			
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void who_access_level_priv_priv() throws RecognitionException, TokenStreamException {
		
		
		log.debug( "entered who_access_level_priv_priv()" );
		
		List<AclWhoClause> whoClauses = aclItem.getWhoClauses();
		
		if ( whoClauses.size() == 0 )
		{
		// Throw an exception ?
		return;
		}
		
		AclWhoClause whoClause = aclItem.getWhoClauses().get( whoClauses.size() - 1 );
		
		AclAccessLevel accessLevel = whoClause.getAccessLevel();
		
		if ( accessLevel == null )
		{
		// Throw an exception ?
		return;
		}
		
		
		switch ( LA(1)) {
		case ID_m:
		{
			match(ID_m);
			
			accessLevel.addPrivilege( AclAccessLevelPrivilegeEnum.MANAGE );
			
			break;
		}
		case ID_w:
		{
			match(ID_w);
			
			accessLevel.addPrivilege( AclAccessLevelPrivilegeEnum.WRITE );
			
			break;
		}
		case ID_r:
		{
			match(ID_r);
			
			accessLevel.addPrivilege( AclAccessLevelPrivilegeEnum.READ );
			
			break;
		}
		case ID_s:
		{
			match(ID_s);
			
			accessLevel.addPrivilege( AclAccessLevelPrivilegeEnum.SEARCH );
			
			break;
		}
		case ID_c:
		{
			match(ID_c);
			
			accessLevel.addPrivilege( AclAccessLevelPrivilegeEnum.COMPARE );
			
			break;
		}
		case ID_x:
		{
			match(ID_x);
			
			accessLevel.addPrivilege( AclAccessLevelPrivilegeEnum.AUTHENTICATION );
			
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"\"access\"",
		"\"anonymous\"",
		"\"attr\"",
		"\"attrs\"",
		"\"auth\"",
		"\"base\"",
		"\"baseobject\"",
		"\"break\"",
		"\"by\"",
		"\"c\"",
		"\"children\"",
		"\"compare\"",
		"\"continue\"",
		"\"disclose\"",
		"\"dn\"",
		"\"dnattr\"",
		"\"entry\"",
		"\"exact\"",
		"\"expand\"",
		"\"filter\"",
		"\"group\"",
		"\"level\"",
		"\"m\"",
		"\"manage\"",
		"\"matchingRule\"",
		"\"none\"",
		"\"one\"",
		"\"onelevel\"",
		"\"r\"",
		"\"read\"",
		"\"regex\"",
		"\"s\"",
		"\"search\"",
		"\"self\"",
		"\"stop\"",
		"\"sub\"",
		"\"subtree\"",
		"\"to\"",
		"\"users\"",
		"\"val\"",
		"\"w\"",
		"\"x\"",
		"\"write\"",
		"DIGIT",
		"LDIGIT",
		"INTEGER",
		"DOUBLE_QUOTED_STRING",
		"IDENT",
		"OPEN_CURLY",
		"CLOSE_CURLY",
		"SP",
		"SSF",
		"TRANSPORT_SSF",
		"TLS_SSF",
		"SASL_SSF",
		"DOT",
		"SEP",
		"EQUAL",
		"STAR",
		"PLUS",
		"MINUS",
		"SLASH",
		"FILTER",
		"FILTER_VALUE",
		"STRING",
		"REGEX"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { -6899444044967800574L, 1L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 18014673387456514L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 1652488685056L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 52815279955968L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	
	}
