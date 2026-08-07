// $ANTLR 2.7.7 (20060906): "Acl.g" -> "AntlrAclLexer.java"$


package org.apache.directory.studio.openldap.config.acl.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;

import java.util.List;

public interface AntlrAclLexerTokenTypes {
	int EOF = 1;
	int NULL_TREE_LOOKAHEAD = 3;
	int ID_access = 4;
	int ID_anonymous = 5;
	int ID_attr = 6;
	int ID_attrs = 7;
	int ID_auth = 8;
	int ID_base = 9;
	int ID_base_object = 10;
	int ID_break = 11;
	int ID_by = 12;
	int ID_c = 13;
	int ID_children = 14;
	int ID_compare = 15;
	int ID_continue = 16;
	int ID_disclose = 17;
	int ID_dn = 18;
	int ID_dnattr = 19;
	int ID_entry = 20;
	int ID_exact = 21;
	int ID_expand = 22;
	int ID_filter = 23;
	int ID_group = 24;
	int ID_level = 25;
	int ID_m = 26;
	int ID_manage = 27;
	int ID_matchingRule = 28;
	int ID_none = 29;
	int ID_one = 30;
	int ID_one_level = 31;
	int ID_r = 32;
	int ID_read = 33;
	int ID_regex = 34;
	int ID_s = 35;
	int ID_search = 36;
	int ID_self = 37;
	int ID_stop = 38;
	int ID_sub = 39;
	int ID_subtree = 40;
	int ID_to = 41;
	int ID_users = 42;
	int ID_val = 43;
	int ID_w = 44;
	int ID_x = 45;
	int ID_write = 46;
	int DIGIT = 47;
	int LDIGIT = 48;
	int INTEGER = 49;
	int DOUBLE_QUOTED_STRING = 50;
	int IDENT = 51;
	int OPEN_CURLY = 52;
	int CLOSE_CURLY = 53;
	int SP = 54;
	int SSF = 55;
	int TRANSPORT_SSF = 56;
	int TLS_SSF = 57;
	int SASL_SSF = 58;
	int DOT = 59;
	int SEP = 60;
	int EQUAL = 61;
	int STAR = 62;
	int PLUS = 63;
	int MINUS = 64;
	int SLASH = 65;
	int FILTER = 66;
	int FILTER_VALUE = 67;
	int STRING = 68;
	int REGEX = 69;
}
