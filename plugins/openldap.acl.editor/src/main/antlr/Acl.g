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
header
{

package org.apache.directory.studio.openldap.config.acl.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;

import java.util.List;
} 
    
//  ----------------------------------------------------------------------------
//  lexer class definition
//  ----------------------------------------------------------------------------

/**
 * The ANTLR generated OpenLDAP ACL parser.
 */
class AntlrAclLexer extends Lexer;


//  ----------------------------------------------------------------------------
//  lexer options
//  ----------------------------------------------------------------------------

options
{
    k = 10;
    charVocabulary = '\0'..'\377';
    caseSensitive = false ;
    defaultErrorHandler = false ;
}

//----------------------------------------------------------------------------
// tokens
//----------------------------------------------------------------------------

tokens
{
    ID_access = "access";
    ID_anonymous = "anonymous";
    ID_attr = "attr";
    ID_attrs = "attrs";
    ID_auth = "auth";
    ID_base = "base";
    ID_base_object = "baseobject";
    ID_break = "break";
    ID_by = "by";
    ID_c = "c";
    ID_children = "children";
    ID_compare = "compare";
    ID_continue = "continue";
    ID_disclose = "disclose";
    ID_dn = "dn";
    ID_dnattr = "dnattr";
    ID_entry = "entry";
    ID_exact = "exact";
    ID_expand = "expand";
    ID_filter = "filter";
    ID_group = "group";
    ID_level = "level";
    ID_m = "m";
    ID_manage = "manage";
    ID_matchingRule = "matchingRule";
    ID_none = "none";
    ID_one = "one";
    ID_one_level = "onelevel";
    ID_r = "r";
    ID_read = "read";
    ID_regex = "regex";
    ID_s = "s";
    ID_search = "search";
    ID_self = "self";
    ID_stop = "stop";
    ID_sub = "sub";
    ID_subtree = "subtree";
    ID_to = "to";
    ID_users = "users";
	ID_val = "val";
    ID_w = "w";
    ID_x = "x";
    ID_write = "write";
}

protected DIGIT : '0' | LDIGIT;

protected LDIGIT : '1'..'9';

INTEGER : DIGIT | ( LDIGIT ( DIGIT )+ );

DOUBLE_QUOTED_STRING : '"'! ( ~'"' )* '"'!;
        
//ATTR_IDENT : ('a'..'z' | '!' | '@' )  ('a'..'z' | DIGIT | '-' | ';' )*;

IDENT : ('a'..'z') ('a'..'z' | DIGIT | '-')*;

OPEN_CURLY : '{';

CLOSE_CURLY : '}';

SP : ( ' ' | '\t' | '\n' { newline(); } | '\r' )+;

SSF : "ssf" EQUAL strength:INTEGER { setText( strength.getText() ); };

TRANSPORT_SSF : "transport_ssf" EQUAL strength:INTEGER { setText( strength.getText() ); };

TLS_SSF : "tls_ssf" EQUAL strength:INTEGER { setText( strength.getText() ); };

SASL_SSF : "sasl_ssf" EQUAL strength:INTEGER { setText( strength.getText() ); };

DOT : '.';

SEP : ',';

EQUAL : '=';

STAR : '*';

PLUS : '+';

MINUS : '-';

SLASH : '/';

FILTER : '(' (SP)? ( ( '&' (SP)? (FILTER)+ ) | ( '|' (SP)? (FILTER)+ ) | ( '!' (SP)? FILTER ) | FILTER_VALUE ) (SP)? ')';

protected FILTER_VALUE : (options{greedy=true;}: ~( ')' | '(' | '&' | '|' | '!' ) ( ~(')') )* ) ;

// ----------------------------------------------------------------------------
// parser class definition
// ----------------------------------------------------------------------------

/**
 * The ANTLR generated OpenLDAP ACL parser.
 */
class AntlrAclParser extends Parser;


// ----------------------------------------------------------------------------
// parser options
// ----------------------------------------------------------------------------

options
{
    k = 2;
    defaultErrorHandler = false ;
}

// ----------------------------------------------------------------------------
// parser initialization
// ----------------------------------------------------------------------------
{
    private static final Logger log = LoggerFactory.getLogger( AntlrAclParser.class );
    
    private AclItem aclItem;
    
    public AclItem getAclItem()
    {
        return aclItem;
    }
}

// ----------------------------------------------------------------------------
// The entry rule :
// <parse> ::= SP? <aclItem> SP? #
// ----------------------------------------------------------------------------
parse
    {
        log.debug( "entered parse()" );
		System.out.println( "entered parse_init()" );
    }
    :
    ( SP )? aclItem ( SP )? EOF
    ;
    
// ----------------------------------------------------------------------------
// The initial ACI :
// <acl> ::= ('access' SP)? SP? 'to' SP (<what>)? ( SP 'by' SP <who> )+
// The <what> part hould be seen only once, it can be absent and then is
// equivalent to '*'
// the <who> part is seen at least once
// ----------------------------------------------------------------------------
aclItem
    {
        log.debug( "entered aclItem()" );
		System.out.println( "entered aclItem()" );

        aclItem = new AclItem();
    }
    :
    ( ID_access SP)? (SP)? ID_to SP (what SP)? ( ID_by SP who )+
	{
		if ( aclItem.getWhatClause() == null )
		{
			// The 'what' is equivalent to '*'
			aclItem.setWhatClause( new AclWhatClauseStar() );
		}
	}
    ;

// ----------------------------------------------------------------------------
// The <what> clause. 
// <what> ::= 'dn' <what-dn> | <what-filter> | <what-attrs> | <what-star> | e
// This is just a filtering clause
// ----------------------------------------------------------------------------
what
    {
        log.debug( "entered what()" );
		System.out.println( "entered what()" );
    }
    :
    ( ID_dn what_dn | what_filter | what_attrs | what_star )
    ;

// ----------------------------------------------------------------------------
// The catch all 'what' clause. We store an instance of a
// AclWhatClauseStar in the ACL
//
// <what-star> ::= '*'
// ----------------------------------------------------------------------------
what_star
	{
    	log.debug( "entered what_star()" );
		System.out.println( "entered what_star()" );
	}
    :
    STAR
    {
        aclItem.setWhatClause( new AclWhatClauseStar() );
    };

// ----------------------------------------------------------------------------
// The <what-dn> clause. We have three flavors :
// - the basic style which takes a regex parameter
// - the scope style that takes a DN parameter
// - a simple DN
//
// <what-dn> ::= <basic-dn-clause | <scope-dn-clause>| SP? '=' SP? DN
// ----------------------------------------------------------------------------
what_dn
	{
	    log.debug( "entered what_dn()" );
		System.out.println( "entered what_dn()" );
	}
    :
	basic_dn_style
	|
	scope_dn_clause
	|
	(SP)? EQUAL (SP)? 
	( 
		{
			System.out.println( "what-dn default" );
		}
		quoted_token:DOUBLE_QUOTED_STRING 
	    {
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
	    }
		| 
		string_token:STRING 
	    {
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
	    }
	)
    ;
	
// ----------------------------------------------------------------------------
// The <basic-dn-style> clause.
//
// <basic-dn-clause> ::= <exact-basic-dn-style> | <regex-basic-dn-style>
//
// ----------------------------------------------------------------------------
basic_dn_style
	{
	    log.debug( "entered basic_dn_style()" );
		System.out.println( "entered basic_dn_style()" );
	}
	:
	exact_basic_dn_style | regex_basic_dn_style
	;
	
// ----------------------------------------------------------------------------
// The <exact-basic-dn-style> clause.
//
// <exact-basic-dn-clause> ::= '.' 'exact' SP? '=' SP? DN
//
// ----------------------------------------------------------------------------
exact_basic_dn_style
	{
	    log.debug( "entered exact_basic_dn_style()" );
		System.out.println( "entered basic_dn_style()" );
		AclWhatClauseDn whatClauseDn = new AclWhatClauseDn();
		whatClauseDn.setType( AclWhatClauseDnTypeEnum.EXACT );
	}
	:
	DOT
	(
		ID_exact (SP)? EQUAL (SP)? 
		( 
			quoted_token:DOUBLE_QUOTED_STRING 
			{
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
			}
			| 
			string_token:STRING 
			{
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
			}
		)
	)
	{
		aclItem.setWhatClause( whatClauseDn );
	}
	;

// ----------------------------------------------------------------------------
// The <regex-basic-dn-style> clause.
//
// <regex-basic-dn-clause> ::= '.' 'regex' SP? '=' SP? REGEXP
//
// ----------------------------------------------------------------------------
regex_basic_dn_style
	{
	    log.debug( "entered regex_basic_dn_style()" );
		System.out.println( "entered regex_basic_dn_style()" );
		AclWhatClauseDn whatClauseDn = new AclWhatClauseDn();
		whatClauseDn.setType( AclWhatClauseDnTypeEnum.REGEX );
	}
	:
	DOT
	(
		ID_regex (SP)? EQUAL (SP)? 
		{
			System.out.println( "In '='" );
		}
		( 
			quoted_token:DOUBLE_QUOTED_STRING 
			{
				whatClauseDn.setPattern( quoted_token.getText() );
			}
			| 
			string_token:STRING 
			{
				whatClauseDn.setPattern( string_token.getText() );
			}
		)
	)
	{
		aclItem.setWhatClause( whatClauseDn );
	}
	;

// ----------------------------------------------------------------------------
// The <scope-dn-clause> clause.
//
// <scope-dn-clause> ::= <scope-dn-style> SP? '=' SP? DN
// ----------------------------------------------------------------------------
scope_dn_clause 
	{
	    log.debug( "entered scope_dn_clause()" );
		System.out.println( "entered scope_dn_clause()" );
		AclWhatClauseDn whatClauseDn = new AclWhatClauseDn();
	}
	:
	scope_dn_style[whatClauseDn]
	(SP)? EQUAL (SP)? 
	( 
		quoted_token:DOUBLE_QUOTED_STRING 
	    {
	        whatClauseDn.setPattern( quoted_token.getText() );
	    }
		| 
		string_token:STRING 
	    {
	        whatClauseDn.setPattern( string_token.getText() );
	    }
	)
	{
		aclItem.setWhatClause( whatClauseDn );
	}
	;

// ----------------------------------------------------------------------------
// <scope-dn-style>	::= '.' 'base' | '.' 'baseobject' | '.' 'one' | 
//						'.' 'onelevel' | '.' 'sub' | '.' 'subtree' | 
//						'.' 'children'
// ----------------------------------------------------------------------------
scope_dn_style [AclWhatClauseDn whatClauseDn]
	{
	    log.debug( "entered scope_dn_style()" );
		System.out.println( "entered scope_dn_style()" );
	}
    :
	DOT
	(
		ID_base
	    {
	        whatClauseDn.setType( AclWhatClauseDnTypeEnum.BASE );
	    }
	    |
	    ID_base_object
	    {
	        whatClauseDn.setType( AclWhatClauseDnTypeEnum.BASE_OBJECT );
	    }
	    |
	    ID_one
	    {
	        whatClauseDn.setType( AclWhatClauseDnTypeEnum.ONE );
	    }
	    |
	    ID_one_level
	    {
	        whatClauseDn.setType( AclWhatClauseDnTypeEnum.ONE_LEVEL );
	    }
	    |
	    ID_sub
	    {
	        whatClauseDn.setType( AclWhatClauseDnTypeEnum.SUB );
	    }
	    |
	    ID_subtree
	    {
	        whatClauseDn.setType( AclWhatClauseDnTypeEnum.SUBTREE );
	    }
	    |
	    ID_children
	    {
	        whatClauseDn.setType( AclWhatClauseDnTypeEnum.CHILDREN );
	    }
	)
    ;

// ----------------------------------------------------------------------------
// <what-filter> ::= 'filter' SP? '=' SP? <ldapFilter>
// ----------------------------------------------------------------------------
what_filter
	{
	    log.debug( "entered what_filter()" );
	    System.out.println( "entered what_filter()" );
    
	    AclWhatClauseFilter whatClauseFilter = new AclWhatClauseFilter();
	}
    :
    ID_filter ( SP )? EQUAL ( SP )? token:FILTER
    {
		// TODO : check tah the filter is valid
        whatClauseFilter.setFilter( token.getText() );

        aclItem.setWhatClause( whatClauseFilter );
    };
    

// ----------------------------------------------------------------------------
// <what-attrs> ::= ( 'attrs' | 'attr' ) SP? '=' SP? <what-attrs-list>
// ----------------------------------------------------------------------------
what_attrs
	{
	    log.debug( "entered what_attrs()" );
	    System.out.println( "entered what_attrs()" );

	    //AclWhatClauseAttributes whatClauseAttributes = new AclWhatClauseAttributess();
	}
    :
    ( ID_attrs | ID_attr ) ( SP )? EQUAL ( SP )? what_attrs_list
    ;

// ----------------------------------------------------------------------------
// <what-attrs-list>	::= IDENT <attr-val>? | <what_attr> <attr-list>
// ----------------------------------------------------------------------------
what_attrs_list
	{
	    log.debug( "entered what_attrs_list()" );
	    System.out.println( "entered what_attrs_list()" );
		
	}
	:
	(
		attribute:IDENT ( (attr_val)? | SEP attr_list )
		{
			// We are not allowed to have more than one attribute 
			// if we have a val
		}
		|
		ID_entry
		|
		ID_children
	)
	;

// ----------------------------------------------------------------------------
// <attr_val>	::= SP 'val' <matching-rule>? <attr-val-style> SP? '=' SP? REGEX
// ----------------------------------------------------------------------------
attr_val
	{
	    log.debug( "entered what_attrs_list()" );
	    System.out.println( "entered what_attrs_list()" );
	}
	:
	SP ID_val (matching_rule)? (attr_val_style)? (SP)? EQUAL (SP)? REGEX
	;

// ----------------------------------------------------------------------------
// <matching-rule>	::= '/' IDENT
// ----------------------------------------------------------------------------
matching_rule
	{
	    log.debug( "entered matching_rule()" );
	    System.out.println( "entered matching_rule()" );
	}
	:
	SLASH IDENT
	;

// ----------------------------------------------------------------------------
// <matching-rule>	::= '/' IDENT
// ----------------------------------------------------------------------------
attr_val_style
	{
	    log.debug( "entered attr_val_style()" );
	    System.out.println( "entered attr_val_style()" );
	}
	:
    DOT 
    (
        ID_exact
        {
            //whatClauseAttributes.setStyle( AclAttributeStyleEnum.EXACT );
        }
        |
        ID_base
        {
            //whatClauseAttributes.setStyle( AclAttributeStyleEnum.BASE );
        }
        |
        ID_base_object
        {
            //whatClauseAttributes.setStyle( AclAttributeStyleEnum.BASE_OBJECT );
        }
        |
        ID_regex
        {
            //whatClauseAttributes.setStyle( AclAttributeStyleEnum.REGEX );
        }
    )
	;
	
// ----------------------------------------------------------------------------
// <attr-list> 		::= ( IDENT | 'entry' | 'children' ) ( ',' <attr_list> )*
// ----------------------------------------------------------------------------
attr_list
	{
	    log.debug( "entered attr_list()" );
	    System.out.println( "entered attr_list()" );
	}
	:
	(IDENT | ID_entry | ID_children) ( SEP attr_list )*
	;
	
//what_attrs_val
//{
//    log.debug( "entered what_attrs_val()" );
//    
//    AclWhatClause whatClause = aclItem.getWhatClause();
//    AclWhatClauseAttributes whatClauseAttributes =  whatClause.getAttributesClause();
//    
//    if ( whatClauseAttributes == null )
//    {
//        // Throw an exception ?
//        return;
//    }
//}
//    :
//    ID_val ( what_attrs_matchingRule )? ( what_attrs_style )? ( SP )? EQUAL ( SP )? token:DOUBLE_QUOTED_STRING 
//    {
//        whatClauseAttributes.setVal( true );
//        whatClauseAttributes.setValue( token.getText() );
//    }
//    ;

//what_attrs_matchingRule
//{
//    log.debug( "entered what_attrs_matchingRule()" );
//    
//    AclWhatClause whatClause = aclItem.getWhatClause();
//    AclWhatClauseAttributes whatClauseAttributes =  whatClause.getAttributesClause();
//    
//    if ( whatClauseAttributes == null )
//    {
//        // Throw an exception ?
//        return;
//    }
//}
//    :
//    SLASH ID_matchingRule
//    {
//        whatClauseAttributes.setMatchingRule( true );
//    }
//    ;

//what_attrs_style
//{
//    log.debug( "entered what_attrs_style()" );
//    
//    AclWhatClause whatClause = aclItem.getWhatClause();
//    AclWhatClauseAttributes whatClauseAttributes =  whatClause.getAttributesClause();
//    
//    if ( whatClauseAttributes == null )
//    {
        // Throw an exception ?
//        return;
//    }
//}
//    :
//    DOT 
//    (
//        ID_exact
//        {
//            whatClauseAttributes.setStyle( AclAttributeStyleEnum.EXACT );
//        }
//        |
//        ID_base
//        {
//            whatClauseAttributes.setStyle( AclAttributeStyleEnum.BASE );
//        }
//        |
//        ID_base_object
//        {
//            whatClauseAttributes.setStyle( AclAttributeStyleEnum.BASE_OBJECT );
//        }
//        |
//        ID_regex
//        {
//            whatClauseAttributes.setStyle( AclAttributeStyleEnum.REGEX );
//        }
//        |
//        ID_one
//        {
//            whatClauseAttributes.setStyle( AclAttributeStyleEnum.ONE );
//        }
//        |
//        ID_one_level
//        {
//            whatClauseAttributes.setStyle( AclAttributeStyleEnum.ONE_LEVEL );
//        }
//        |
//        ID_sub
//        {
//            whatClauseAttributes.setStyle( AclAttributeStyleEnum.SUB );
//        }
//        |
//        ID_subtree
//        {
//            whatClauseAttributes.setStyle( AclAttributeStyleEnum.SUBTREE );
//        }
//        |
//        ID_children
//        {
//            whatClauseAttributes.setStyle( AclAttributeStyleEnum.CHILDREN );
//        }
//    )
//    ;
    
//what_attrs_attr_ident
//{
//    log.debug( "entered what_attrs_attr_ident()" );
//    
//    AclWhatClause whatClause = aclItem.getWhatClause();
//    AclWhatClauseAttributes whatClauseAttributes =  whatClause.getAttributesClause();
//    
//    if ( whatClauseAttributes == null )
//    {
//        // Throw an exception ?
//        return;
//    }
//}
//    :
//    token:IDENT
//    {
//        whatClauseAttributes.addAttribute( token.getText() );
//    }
//    ;

who
{
    log.debug( "entered who()" );
}
    :
    ( who_star | who_anonymous | who_users | who_self | who_dn | who_dnattr | who_group | who_ssf | who_transport_ssf | who_tls_ssf | who_sasl_ssf ) ( SP who_access_level )? ( SP who_control )?
    ;

who_anonymous
{
    log.debug( "entered who_anonymous()" );
}
    :
    ID_anonymous
    {
        aclItem.addWhoClause( new AclWhoClauseAnonymous() );
    };

who_users
{
    log.debug( "entered who_users()" );
}
    :
    ID_users
    {
        aclItem.addWhoClause( new AclWhoClauseUsers() );
    };

who_self
{
    log.debug( "entered who_self()" );
}
    :
    ID_self
    {
        aclItem.addWhoClause( new AclWhoClauseSelf() );
    };
    
who_star
{
    log.debug( "entered who_star()" );
}
    :
    STAR
    {
        aclItem.addWhoClause( new AclWhoClauseStar() );
    };
    
who_dnattr
{
    log.debug( "entered who_dnattr()" );
    
    AclWhoClauseDnAttr whoClauseDnAttr = new AclWhoClauseDnAttr();
    aclItem.addWhoClause( whoClauseDnAttr ); 
}
    :
    ID_dnattr ( SP )? EQUAL ( SP )? token:IDENT
    {
        whoClauseDnAttr.setAttribute( token.getText() );
    };
    
who_group
{
    log.debug( "entered who_group()" );
    
    AclWhoClauseGroup whoClauseGroup = new AclWhoClauseGroup();
    aclItem.addWhoClause( whoClauseGroup ); 
}
    :
    ID_group ( SLASH objectclass:IDENT ( SLASH attrname:IDENT )? )? ( DOT who_group_type )? EQUAL pattern:DOUBLE_QUOTED_STRING
    {
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
    };
    
who_group_type
{
    log.debug( "entered who_group_type()" );
    
    List<AclWhoClause> whoClauses = aclItem.getWhoClauses();
    AclWhoClause whoClause = aclItem.getWhoClauses().get( whoClauses.size() - 1 );
    
    if ( !( whoClause instanceof AclWhoClauseGroup ) )
    {
        // Throw an exception ?
        return;
    }
    AclWhoClauseGroup whoClauseGroup =  ( AclWhoClauseGroup ) whoClause;
}
    :
    ID_exact
    {
        whoClauseGroup.setType( AclWhoClauseGroupTypeEnum.EXACT );
    }
    |
    ID_expand
    {
        whoClauseGroup.setType( AclWhoClauseGroupTypeEnum.EXPAND );
    }
    ;

who_dn
{
    log.debug( "entered who_dn()" );
    
    AclWhoClauseDn whoClauseDn = new AclWhoClauseDn();
    aclItem.addWhoClause( whoClauseDn ); 
}
    :
    ID_dn ( DOT who_dn_type ( SEP who_dn_modifier )? )? ( SP )? EQUAL ( SP )? token:DOUBLE_QUOTED_STRING
    {
        whoClauseDn.setPattern( token.getText() );
    };

who_dn_type
{
    log.debug( "entered who_dn_type()" );
    
    List<AclWhoClause> whoClauses = aclItem.getWhoClauses();
    AclWhoClause whoClause = aclItem.getWhoClauses().get( whoClauses.size() - 1 );
    
    if ( !( whoClause instanceof AclWhoClauseDn ) )
    {
        // Throw an exception ?
        return;
    }
    
    AclWhoClauseDn whoClauseDn =  ( AclWhoClauseDn ) whoClause;
}
    :
    ID_regex
    {
        whoClauseDn.setType( AclWhoClauseDnTypeEnum.REGEX );
    }
    |
    ID_base
    {
        whoClauseDn.setType( AclWhoClauseDnTypeEnum.BASE );
    }
    |
    ID_exact
    {
        whoClauseDn.setType( AclWhoClauseDnTypeEnum.EXACT );
    }
    |
    ID_one
    {
        whoClauseDn.setType( AclWhoClauseDnTypeEnum.ONE );
    }
    |
    ID_subtree
    {
        whoClauseDn.setType( AclWhoClauseDnTypeEnum.SUBTREE );
    }
    |
    ID_children
    {
        whoClauseDn.setType( AclWhoClauseDnTypeEnum.CHILDREN );
    }
    |
    ID_level OPEN_CURLY token:INTEGER CLOSE_CURLY
    {
        AclWhoClauseDnTypeEnum levelType = AclWhoClauseDnTypeEnum.LEVEL;
        levelType.setLevel( Integer.valueOf( token.getText() ) );
        whoClauseDn.setType( levelType );
    }
    ;
    
who_dn_modifier
{
    log.debug( "entered who_dn_modifier()" );
    
    List<AclWhoClause> whoClauses = aclItem.getWhoClauses();
    AclWhoClause whoClause = aclItem.getWhoClauses().get( whoClauses.size() - 1 );

    if ( !( whoClause instanceof AclWhoClauseDn ) )
    {
        // Throw an exception ?
        return;
    }
    
    AclWhoClauseDn whoClauseDn =  ( AclWhoClauseDn ) whoClause;
}
    :
    ID_expand
    {
        whoClauseDn.setModifier( AclWhoClauseDnModifierEnum.EXPAND );
    }
    ;
    
who_access_level
{
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
}
    :
    ID_self SP ( who_access_level_level | who_access_level_priv )?
    {
        accessLevel.setSelf( true );
    }
    |
    ( who_access_level_level | who_access_level_priv )?
    {
        accessLevel.setSelf( false );
    }
    ;
    
who_access_level_level
{
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
}
    :
    ID_manage
    {
        accessLevel.setLevel( AclAccessLevelLevelEnum.MANAGE );
    }
    |
    ID_write
    {
        accessLevel.setLevel( AclAccessLevelLevelEnum.WRITE );
    }
    |
    ID_read
    {
        accessLevel.setLevel( AclAccessLevelLevelEnum.READ );
    }
    |
    ID_search
    {
        accessLevel.setLevel( AclAccessLevelLevelEnum.SEARCH );
    }
    |
    ID_compare
    {
        accessLevel.setLevel( AclAccessLevelLevelEnum.COMPARE );
    }
    |
    ID_auth
    {
        accessLevel.setLevel( AclAccessLevelLevelEnum.AUTH );
    }
    |
    ID_disclose
    {
        accessLevel.setLevel( AclAccessLevelLevelEnum.DISCLOSE );
    }
    |
    ID_none
    {
        accessLevel.setLevel( AclAccessLevelLevelEnum.NONE );
    }
    ;
    
who_access_level_priv
{
    log.debug( "entered who_access_level_priv()" );
}
    :
    who_access_level_priv_modifier  ( who_access_level_priv_priv )+
    ;
    
who_access_level_priv_modifier
{
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
}
    :
    EQUAL
    {
        accessLevel.setPrivilegeModifier( AclAccessLevelPrivModifierEnum.EQUAL );
    }
    |
    PLUS
    {
        accessLevel.setPrivilegeModifier( AclAccessLevelPrivModifierEnum.PLUS );
    }
    |
    MINUS
    {
        accessLevel.setPrivilegeModifier( AclAccessLevelPrivModifierEnum.MINUS );
    }
    ;
    
who_access_level_priv_priv
{
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
}
    :
    ID_m
    {
        accessLevel.addPrivilege( AclAccessLevelPrivilegeEnum.MANAGE );
    }
    |
    ID_w
    {
        accessLevel.addPrivilege( AclAccessLevelPrivilegeEnum.WRITE );
    }
    |
    ID_r
    {
        accessLevel.addPrivilege( AclAccessLevelPrivilegeEnum.READ );
    }
    |
    ID_s
    {
        accessLevel.addPrivilege( AclAccessLevelPrivilegeEnum.SEARCH );
    }
    |
    ID_c
    {
        accessLevel.addPrivilege( AclAccessLevelPrivilegeEnum.COMPARE );
    }
    |
    ID_x
    {
        accessLevel.addPrivilege( AclAccessLevelPrivilegeEnum.AUTHENTICATION );
    }
    ;
    
who_control
{
    log.debug( "entered who_control()" );
    
    List<AclWhoClause> whoClauses = aclItem.getWhoClauses();

    if ( whoClauses.size() == 0 )
    {
        // Throw an exception ?
        return;
    }
    
    AclWhoClause whoClause = aclItem.getWhoClauses().get( whoClauses.size() - 1 );
}
    :
    ID_stop
    {
        whoClause.setControl( AclControlEnum.STOP );
    }
    |
    ID_continue
    {
        whoClause.setControl( AclControlEnum.CONTINUE );
    }
    |
    ID_break
    {
        whoClause.setControl( AclControlEnum.BREAK );
    }
    ;
    
who_ssf
{
    log.debug( "entered who_ssf()" );
    
    AclWhoClauseSsf whoClauseSsf = new AclWhoClauseSsf();
    aclItem.addWhoClause( whoClauseSsf ); 
}
    :
    strength:SSF
    {
        whoClauseSsf.setStrength( Integer.valueOf( strength.getText() ) );
    }
    ;
    
who_transport_ssf
{
    log.debug( "entered who_transport_ssf()" );
    
    AclWhoClauseTransportSsf whoClauseTransportSsf = new AclWhoClauseTransportSsf();
    aclItem.addWhoClause( whoClauseTransportSsf ); 
}
    :
    strength:TRANSPORT_SSF
    {
        whoClauseTransportSsf.setStrength( Integer.valueOf( strength.getText() ) );
    }
    ;
    
who_tls_ssf
{
    log.debug( "entered who_tls_ssf()" );
    
    AclWhoClauseTlsSsf whoClauseTlsSsf = new AclWhoClauseTlsSsf();
    aclItem.addWhoClause( whoClauseTlsSsf ); 
}
    :
    strength:TLS_SSF
    {
        whoClauseTlsSsf.setStrength( Integer.valueOf( strength.getText() ) );
    }
    ;
    
who_sasl_ssf
{
    log.debug( "entered who_sasl_ssf()" );
    
    AclWhoClauseSaslSsf whoClauseSaslSsf = new AclWhoClauseSaslSsf();
    aclItem.addWhoClause( whoClauseSaslSsf ); 
}
    :
    strength:SASL_SSF
    {
        whoClauseSaslSsf.setStrength( Integer.valueOf( strength.getText() ) );
    }
    ;
