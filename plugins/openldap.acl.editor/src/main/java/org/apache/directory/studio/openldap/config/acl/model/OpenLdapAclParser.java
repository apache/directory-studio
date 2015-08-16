/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.studio.openldap.config.acl.model;


import java.io.StringReader;
import java.text.ParseException;

import antlr.CharBuffer;
import antlr.LexerSharedInputState;
import antlr.RecognitionException;
import antlr.TokenStreamException;


/**
 * A reusable wrapper around the antlr generated parser for an OpenLDAP ACL. The grammar 
 * to parse is the following :
 * <pre>
 * parse                ::= SP* aclItem SP* EOF
 * aclItem              ::= ( ID_access SP+ )? ID_to ( SP+ (what_star | what_dn | what_attrs | what_filter | ( ID_by SP+ who ) ) )+ 
 * what_star            ::= STAR
 * what_dn              ::= ID_dn ( DOT what_dn_type )? SP+ EQUAL SP+ DOUBLE_QUOTED_STRING
 * what_dn_type         ::= ID_regex | ID_base | ID_exact | ID_one | ID_subtree | ID_children
 * what_attrs           ::= ( ID_attrs | ID_attr ) SP* EQUAL SP* ATTR_IDENT ( SEP ATTR_IDENT )* 
 *                              ( SP* VAL ( SLASH MATCHING_RULE )? ( DOT what_attrs_style)? ) SP* EQUAL SP* DOUBLE_QUOTED_STRING )?
 * what_attrs_attr_ident::= ATTR_IDENT
 * what_attrs_style     ::= EXACT | BASE | BASE_OBJECT | REGEX | ONE | ONE_LEVEL | SUB | SUB_TREE | CHILDREN
 * what_filter          ::= ID_filter SP* EQUAL SP* FILTER
 * who                  ::= ( who_star | who_anonymous | who_users | who_self | who_dn | who_dnattr | who_group | who_ssf | 
 *                              who_transport_ssf | who_tls_ssf | who_sasl_ssf ) ( SP+ who_access_level )? ( SP+  who_control )?
 * who_anonymous        ::= ID_anonymous
 * who_users            ::= ID_users
 * who_self             ::= ID_self
 * who_star             ::= STAR
 * who_dnattr           ::= ID_dnattr SP* EQUAL SP* ATTR_IDENT
 * who_group            ::= ID_group ( SLASH ATTR_IDENT ( SLASH ATTR_IDENT )? )? ( DOT who_group_type )? 
 *                              EQUAL DOUBLE_QUOTED_STRING
 * who_group_type       ::= ID_exact | ID_expand
 * who_dn               ::= ID_dn ( DOT who_dn_type ( SEP who_dn_modifier )? )? SP* EQUAL SP* DOUBLE_QUOTED_STRING
 * who_dn_type          ::= ID_regex | ID_base | ID_exact | ID_one | ID_subtree | ID_children | 
 *                              ID_level OPEN_CURLY token:INTEGER CLOSE_CURLY
 * who_dn_modifier      ::= ID_expand
 * who_access_level     ::= ID_self SP+ ( who_access_level_level | who_access_level_priv )? | 
 *                              ( who_access_level_level | who_access_level_priv )?
 * who_access_level_level           ::= ID_manage | ID_write | ID_read | ID_search | ID_compare | ID_auth | ID_disclose | ID_none
 * who_access_level_priv            ::= who_access_level_priv_modifier ( who_access_level_priv_priv )+
 * who_access_level_priv_modifier   ::= EQUAL | PLUS | MINUS
 * who_access_level_priv_priv       ::= ID_m | ID_w | ID_r | ID_s | ID_c | ID_x
 * who_control          ::= ID_stop | ID_continue | ID_break
 * who_ssf              ::= strength:SSF
 * who_transport_ssf    ::= TRANSPORT_SSF
 * who_tls_ssf          ::= strength:TLS_SSF
 * who_sasl_ssf         ::= strength:SASL_SSF
 * </pre>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OpenLdapAclParser
{
    /** the antlr generated parser being wrapped */
    private AntlrAclParser parser;

    /** the antlr generated lexer being wrapped */
    private AntlrAclLexer lexer;


    /**
     * Creates an OpenLDAP ACL parser.
     */
    public OpenLdapAclParser()
    {
        this.lexer = new AntlrAclLexer( new StringReader( "" ) );
        this.parser = new AntlrAclParser( lexer );
    }


    /**
     * Parses an OpenLDAP ACL.
     * 
     * @param s the string to be parsed
     * @return the specification bean
     * @throws ParseException if there are any recognition errors (bad syntax)
     */
    public synchronized AclItem parse( String s ) throws ParseException
    {
        try
        {
            LexerSharedInputState state = new LexerSharedInputState( new CharBuffer( new StringReader( s ) ) );
            this.lexer.setInputState( state );
            this.parser.getInputState().reset();

            parser.parse();
            
            return parser.getAclItem();
        }
        catch ( TokenStreamException e )
        {
            throw new ParseException( "Unable to read ACL: " + e.getMessage(), -1 );
        }
        catch ( RecognitionException e )
        {
            throw new ParseException( "Unable to read ACL: " + e.getMessage() + " - [Line:" + e.getLine()
                + " - Column:" + e.getColumn() + "]", e.getColumn() );
        }
    }
}
