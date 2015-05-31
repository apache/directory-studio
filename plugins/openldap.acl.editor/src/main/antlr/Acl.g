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
    ID_none = "none";
    ID_one = "one";
    ID_r = "r";
    ID_read = "read";
    ID_regex = "regex";
    ID_s = "s";
    ID_search = "search";
    ID_self = "self";
    ID_stop = "stop";
    ID_subtree = "subtree";
    ID_to = "to";
    ID_users = "users";
    ID_x = "x";
    ID_w = "w";
    ID_write = "write";
}

protected DIGIT : '0' | LDIGIT;

protected LDIGIT : '1'..'9';

INTEGER : DIGIT | ( LDIGIT ( DIGIT )+ );

DOUBLE_QUOTED_STRING : '"'! ( ~'"' )* '"'!;
        
ATTR_IDENT : ('a'..'z')  ('a'..'z' | DIGIT | '-' | ';' )*;

OPEN_CURLY : '{';

CLOSE_CURLY : '}';

SP : ' ' | '\t' | '\n' { newline(); } | '\r';

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

FILTER : '(' ( ( '&' (SP)* (FILTER)+ ) | ( '|' (SP)* (FILTER)+ ) | ( '!' (SP)* FILTER ) | FILTER_VALUE ) ')';

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

parse
	{
    	log.debug( "entered parse()" );
    	//System.out.println( "entered parse()" );
	}
    :
    ( SP )* aclItem ( SP )* EOF
    ;
    
aclItem
	{
	    log.debug( "entered aclItem()" );
	    //System.out.println( "entered aclItem()" );

    	aclItem = new AclItem();
	}
    :
    ( ID_access ( SP )+ )? ID_to ( ( SP )+ (what_star | what_dn | what_attrs | what_filter | ( ID_by ( SP )+ who ) ) )+ 
    ;
    
what_star
{
    log.debug( "entered what_star()" );
    //System.out.println( "entered what_star()" );
}
	:
    STAR
    {
    	if ( aclItem.getWhatClause().getStarClause() == null )
    	{
			aclItem.getWhatClause().setStarClause( new AclWhatClauseStar() );
		}
		else
		{
			throw new RecognitionException( "A star (*) what clause already exists in the ACL." );
		}
    };

what_dn
{
    log.debug( "entered what_dn()" );
    //System.out.println( "entered what_dn()" );
    
    AclWhatClauseDn whatClauseDn = new AclWhatClauseDn();
    
	if ( aclItem.getWhatClause().getDnClause() == null )
	{
		aclItem.getWhatClause().setDnClause( whatClauseDn );
	}
	else
	{
		throw new RecognitionException( "A DN what clause already exists in the ACL." );
	}
}
	:
	ID_dn ( DOT what_dn_type )? ( SP! )* EQUAL ( SP! )* token:DOUBLE_QUOTED_STRING
	{
		whatClauseDn.setPattern( token.getText() );
	}
    ;
    
what_dn_type
{
    log.debug( "entered what_dn_type()" );
    //System.out.println( "entered what_dn_type()" );
    
	AclWhatClause whatClause = aclItem.getWhatClause();
	AclWhatClauseDn whatClauseDn =  whatClause.getDnClause();
	if ( whatClauseDn == null )
	{
    	// Throw an exception ?
    	return;
	}
}
	:
	ID_regex
    {
    	//System.out.println( "entered ID_regex()" );
    	whatClauseDn.setType( AclWhatClauseDnTypeEnum.REGEX );
    }
    |
    ID_base
    {
    	//System.out.println( "entered ID_base()" );
    	whatClauseDn.setType( AclWhatClauseDnTypeEnum.BASE );
    }
    |
    ID_exact
    {
    	//System.out.println( "entered ID_exact()" );
    	whatClauseDn.setType( AclWhatClauseDnTypeEnum.EXACT );
    }
    |
    ID_one
    {
    	//System.out.println( "entered ID_one()" );
    	whatClauseDn.setType( AclWhatClauseDnTypeEnum.ONE );
    }
    |
    ID_subtree
    {
    	//System.out.println( "entered ID_subtree()" );
    	whatClauseDn.setType( AclWhatClauseDnTypeEnum.SUBTREE );
    }
    |
    ID_children
    {
    	//System.out.println( "entered ID_children()" );
    	whatClauseDn.setType( AclWhatClauseDnTypeEnum.CHILDREN );
    }
    ;

what_attrs
{
    log.debug( "entered what_attrs()" );
    //System.out.println( "entered what_attrs()" );
    
	if ( aclItem.getWhatClause().getAttributesClause() == null )
	{
		aclItem.getWhatClause().setAttributesClause( new AclWhatClauseAttributes() );
	}
	else
	{
		throw new RecognitionException( "A attributes what clause already exists in the ACL." );
	}
}
	:
	( ID_attrs | ID_attr ) ( SP! )* EQUAL ( SP! )* what_attrs_attr_ident ( SEP what_attrs_attr_ident )*
    ;
    
what_attrs_attr_ident
{
    log.debug( "entered what_attrs_attr_ident()" );
    //System.out.println( "entered what_attrs_attr_ident()" );
    
	AclWhatClause whatClause = aclItem.getWhatClause();
	AclWhatClauseAttributes whatClauseAttributes =  whatClause.getAttributesClause();
	if ( whatClauseAttributes == null )
	{
		// Throw an exception ?
		return;
	}
}
	:
    token:ATTR_IDENT
    {
    	//System.out.println( token.getText()  );
    	whatClauseAttributes.addAttribute( token.getText() );
    };

what_filter
{
    log.debug( "entered what_filter()" );
    //System.out.println( "entered what_filter()" );
    
	if ( aclItem.getWhatClause().getFilterClause() != null )
	{
		throw new RecognitionException( "A filter what clause already exists in the ACL." );
	}
}
	:
	ID_filter ( SP! )* EQUAL ( SP! )* token:FILTER
    {
    	AclWhatClauseFilter whatClauseFilter = new AclWhatClauseFilter();
    	whatClauseFilter.setFilter( token.getText() );

		aclItem.getWhatClause().setFilterClause( whatClauseFilter );
    };
    
who
{
    log.debug( "entered who()" );
    //System.out.println( "entered who()" );
}
    :
    ( who_star | who_anonymous | who_users | who_self | who_dn | who_dnattr | who_group | who_ssf | who_transport_ssf | who_tls_ssf | who_sasl_ssf ) ( ( SP )+ who_access_level )? ( ( SP )+  who_control )?
    ;

who_anonymous
{
    log.debug( "entered who_anonymous()" );
    //System.out.println( "entered who_anonymous()" );
}
	:
    ID_anonymous
    {
		aclItem.addWhoClause( new AclWhoClauseAnonymous() );
    };

who_users
{
    log.debug( "entered who_users()" );
    //System.out.println( "entered who_users()" );
}
	:
    ID_users
    {
		aclItem.addWhoClause( new AclWhoClauseUsers() );
    };

who_self
{
    log.debug( "entered who_self()" );
    //System.out.println( "entered who_self()" );
}
	:
    ID_self
    {
		aclItem.addWhoClause( new AclWhoClauseSelf() );
    };
    
who_star
{
    log.debug( "entered who_star()" );
    //System.out.println( "entered who_star()" );
}
	:
    STAR
    {
		aclItem.addWhoClause( new AclWhoClauseStar() );
    };
    
who_dnattr
{
    log.debug( "entered who_dnattr()" );
    //System.out.println( "entered who_dnattr()" );
    
	AclWhoClauseDnAttr whoClauseDnAttr = new AclWhoClauseDnAttr();
	aclItem.addWhoClause( whoClauseDnAttr ); 
}
	:
	ID_dnattr ( SP! )* EQUAL ( SP! )* token:ATTR_IDENT
    {
		whoClauseDnAttr.setAttribute( token.getText() );
    };
    
who_group
{
    log.debug( "entered who_group()" );
    //System.out.println( "entered who_group()" );
    
	AclWhoClauseGroup whoClauseGroup = new AclWhoClauseGroup();
	aclItem.addWhoClause( whoClauseGroup ); 
}
	:
	ID_group ( SLASH objectclass:ATTR_IDENT ( SLASH attrname:ATTR_IDENT )? )? ( DOT who_group_type )? EQUAL pattern:DOUBLE_QUOTED_STRING
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
    //System.out.println( "entered who_group_type()" );
    
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
    //System.out.println( "entered who_dn()" );
    
	AclWhoClauseDn whoClauseDn = new AclWhoClauseDn();
	aclItem.addWhoClause( whoClauseDn ); 
}
	:
	ID_dn ( DOT who_dn_type ( SEP who_dn_modifier )? )? ( SP! )* EQUAL ( SP! )* token:DOUBLE_QUOTED_STRING
    {
		whoClauseDn.setPattern( token.getText() );
    };

who_dn_type
{
    log.debug( "entered who_dn_type()" );
    //System.out.println( "entered who_dn_type()" );
    
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
    //System.out.println( "entered who_dn_modifier()" );
    
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
    //System.out.println( "entered who_access_level()" );
    
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
	ID_self ( SP )+ ( who_access_level_level | who_access_level_priv )?
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
    //System.out.println( "entered who_access_level_level()" );
    
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
    //System.out.println( "entered who_access_level_priv()" );
}
	:
	who_access_level_priv_modifier  ( who_access_level_priv_priv )+
	;
	
who_access_level_priv_modifier
{
    log.debug( "entered who_access_level_priv_modifier()" );
    //System.out.println( "entered who_access_level_priv_modifier()" );
    
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
    //System.out.println( "entered who_access_level_priv_priv()" );
    
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
    //System.out.println( "entered who_control()" );
    
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
    //System.out.println( "entered who_ssf()" );
    
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
    //System.out.println( "entered who_transport_ssf()" );
    
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
    //System.out.println( "entered who_tls_ssf()" );
    
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
    //System.out.println( "entered who_sasl_ssf()" );
    
	AclWhoClauseSaslSsf whoClauseSaslSsf = new AclWhoClauseSaslSsf();
	aclItem.addWhoClause( whoClauseSaslSsf ); 
}
	:
	strength:SASL_SSF
	{
        whoClauseSaslSsf.setStrength( Integer.valueOf( strength.getText() ) );
	}
	;
