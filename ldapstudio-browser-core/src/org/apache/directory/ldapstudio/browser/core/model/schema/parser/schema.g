header {
package org.apache.directory.ldapstudio.browser.core.model.schema.parser;

import java.io.* ;
import java.util.* ;
import org.apache.directory.ldapstudio.browser.core.model.schema.*;

}

// ----------------------------------------------------------------------------
// Main Lexer
// ----------------------------------------------------------------------------

class SchemaLexer extends Lexer;

options    {
    k = 5 ;
    //importVocab=NonQuote ;
    exportVocab=Schema ;
    charVocabulary = '\3'..'\377' ;
    caseSensitive = false ;
    //testLiterals = true ;
    defaultErrorHandler = false ;
}

WHSP : (options{greedy=true;}: ' ' )+ {$setType(Token.SKIP);} ;

LPAR : '(' ;
RPAR : ')' ;
QUOTE : '\'' ;
DOLLAR : '$' ;
LBRACKET : '{' ;
RBRACKET : '}' ;

LEN : LBRACKET ('0'..'9')+ RBRACKET ;

USAGE_USERAPPLICATIONS : "userapplications" ;
USAGE_DIRECTORYOPERATION : "directoryoperation" ;
USAGE_DISTRIBUTEDOPERATION : "distributedoperation" ;
USAGE_DSAOPERATION : "dsaoperation" ;

STARTNUMERICOID : ( LPAR ( numericoid:VALUE ) ) { setText(numericoid.getText().trim()); } ;
//NAME : ( "name" WHSP qdescrs:QDESCRS (WHSP)? ) { setText(qdescrs.getText()); } ; // use QDSTRINGS to allow apache-ds ldapsyntaxes
NAME : ( "name" WHSP qdstrings:VALUES ) { setText(qdstrings.getText().trim()); } ;
DESC : ( "desc" WHSP qdstring:VALUES ) { setText(qdstring.getText().trim()); } ;
SUP : ( "sup" WHSP sup:VALUES ) { setText(sup.getText().trim()); } ;
MUST : ( "must" WHSP must:VALUES ) { setText(must.getText().trim()); } ;
MAY : ( "may" WHSP may:VALUES ) { setText(may.getText()); } ;
EQUALITY : ( "equality" WHSP equality:VALUES ) { setText(equality.getText().trim()); } ;
ORDERING : ( "ordering" WHSP ordering:VALUES ) { setText(ordering.getText().trim()); } ;
SUBSTR : ( "substr" WHSP substr:VALUES ) { setText(substr.getText().trim()); } ;
SYNTAX : ( "syntax" WHSP syntax:VALUES (len:LEN)? ) { setText(syntax.getText().trim() + (len!=null?len.getText().trim():"")); } ;
//USAGE : ( "usage" WHSP ( USAGE_USERAPPLICATIONS | USAGE_DIRECTORYOPERATION | USAGE_DISTRIBUTEDOPERATION | USAGE_DSAOPERATION ) (WHSP)? ) ;
USAGE : ( "usage" WHSP op:VALUES ) { setText(op.getText().trim()); } ;
APPLIES : ( "applies" WHSP applies:VALUES ) { setText(applies.getText().trim()); } ;

X : ( "x-" ( 'a'..'z' | '0'..'9' | '-' | '_' )+ WHSP VALUES ) {$setType(Token.SKIP);} ; 

SINGLE_VALUE : ( "single-value" (WHSP)? ) ;
COLLECTIVE : ( "collective" (WHSP)? ) ;
NO_USER_MODIFICATION : ( "no-user-modification" (WHSP)? ) ;
OBSOLETE : ( "obsolete" (WHSP)? ) ;
ABSTRACT : ( "abstract" (WHSP)? ) ;
STRUCTURAL : ( "structural" (WHSP)? ) ;
AUXILIARY : ( "auxiliary" (WHSP)? ) ;

//APACHE_SYNTAX_NAME : ( "name" WHSP qdstring:QDSTRING (WHSP)? ) { setText(qdstring.getText()); } ;



protected VALUES : ( VALUE | LPAR  VALUE ( (DOLLAR)? VALUE )* RPAR ) ;
protected VALUE : (WHSP)? ( QUOTED_STRING | UNQUOTED_STRING ) (options {greedy=true;}: WHSP)? ;
//protected UNQUOTED_STRING : ( ~('\''|' '|'('|')'|'{'|'}'|'$') (options{greedy=true;}: ~' ')* ) ;
protected UNQUOTED_STRING : (options{greedy=true;}: 'a'..'z' | '0'..'9' | '-' | ';' | '.' )+ ;
protected QUOTED_STRING : ( QUOTE (~'\'')* QUOTE ) ;



//protected QDESCRS : ( QDESCR | ( LPAR (WHSP)? ( QDESCR (WHSP)? )+ RPAR ) ) ;
//protected QDESCR : ( QUOTE DESCR QUOTE ) ;
//protected DESCR : ( 'a'..'z') ( 'a'..'z' | '0'..'9' | '-' | ';' )* ;
//protected QDSTRINGS : ( QDESCR | ( LPAR (WHSP)? QDESCR (options{greedy=true;}: (WHSP)? QDESCR )* (WHSP)? RPAR ) ) ;
//protected QDSTRING : ( QUOTE DESCR QUOTE ) ;
//protected NONQUOTE : (~'\'')* ;
//protected OIDS : ( OID | ( LPAR (WHSP)? OID (options{greedy=true;}: (WHSP)? DOLLAR (WHSP)? OID )* (WHSP)? RPAR ) ) ;
//protected OID : ( NUMERICOID | DESCR ) ;
//protected NUMERICOID : ( ('0'..'9')+ ( '.' ('0'..'9')+ )+ ) ;    




class SchemaParser extends Parser;
options    {
    k = 3 ;
    defaultErrorHandler = false ;
    //buildAST=true ;
}

{
	public static final void main(String[] args) {
       try {
        	
        	//"( 11.222.333.4444 NAME ( 'test1' 'test2' ) DESC 'a b c' OBSOLETE SUP top ABSTRACT MUST ( cn ) may ( givenName $ sn) )"
        	//"( 2.5.4.11 NAME ( 'ou' 'organizationalUnitName' ) DESC 'RFC2256: organizational unit this object belongs to' SUP name EQUALITY caseIgnoreMatch SYNTAX 1.2.3.4.5{32} COLLECTIVE USAGE userApplications )"
        	//"( 2.5.4.11 DESC 'a b c' )"
        	//"( 1.3.6.1.4.1.4203.1.2.1 NAME 'caseExactIA5SubstringsMatch' SYNTAX 1.3.6.1.4.1.1466.115.121.1.26 )"
        	//"( 2.5.13.0 NAME 'objectIdentifierMatch' APPLIES ( supportedApplicationContext $ supportedFeatures $ supportedExtension $ supportedControl ) )"
        	//"( 1.2.840.113548.3.1.4.11110 NAME 'ciscoccnatPAUserPIN' DESC 'User Defined Attribute' SYNTAX 1.3.6.1.4.1.1466.115.121.1.15 SINGLE-VALUE X-ORIGIN ( 'Cisco AVVID' 'user defined' ) )"
            SchemaLexer mainLexer = new SchemaLexer(new StringReader(
"( 1.3.6.1.4.1.1466.115.121.1.48 NAME 'Supplier And Consumer'  )"
            		));

            SchemaParser parser = new SchemaParser(mainLexer);
            //ObjectClassDescription d = parser.objectClassDescription();
            //AttributeTypeDescription d = parser.attributeTypeDescription();
            LdapSyntaxDescription d = parser.syntaxDescription();
            //MatchingRuleDescription d = parser.matchingRuleDescription();
            //MatchingRuleUseDescription d = parser.matchingRuleUseDescription();
            System.out.println(d.toString());
        } catch(Exception e) {
            System.err.println("exception: "+e);
            e.printStackTrace();
        }		
	}
	
}


objectClassDescription returns [ObjectClassDescription ocd = new ObjectClassDescription()]
    :
    ( oid:STARTNUMERICOID { ocd.setNumericOID(oid.getText()); } )
    (
        ( name:NAME { ocd.setNames(qdescrs(name.getText())); } )
        |
        ( desc:DESC { ocd.setDesc(qdstring(desc.getText())); } )
        |
        ( OBSOLETE { ocd.setObsolete( true ); } )
        |
        ( sup:SUP { ocd.setSuperiorObjectClassDescriptionNames(oids(sup.getText())); } )
        |
        ( ABSTRACT { ocd.setAbstract( true ); }
          |
          STRUCTURAL { ocd.setStructural( true ); }
          |
          AUXILIARY { ocd.setAuxiliary( true ); } 
        )  
        |
        ( must:MUST { ocd.setMustAttributeTypeDescriptionNames(oids(must.getText())); } )
        |
        ( may:MAY { ocd.setMayAttributeTypeDescriptionNames(oids(may.getText())); } )
    )*
    RPAR
    ;

attributeTypeDescription returns [AttributeTypeDescription atd = new AttributeTypeDescription()]
    :
    ( oid:STARTNUMERICOID { atd.setNumericOID(oid.getText()); } )
    (
        ( name:NAME { atd.setNames(qdescrs(name.getText())); } )
        |
        ( desc:DESC { atd.setDesc(qdstring(desc.getText())); } )
        |
        ( OBSOLETE { atd.setObsolete( true ); } )
        |
        ( sup:SUP { atd.setSuperiorAttributeTypeDescriptionName(oid(sup.getText())); } )
        |
        ( equality:EQUALITY { atd.setEqualityMatchingRuleDescriptionOID(oid(equality.getText())); } )
        |
        ( ordering:ORDERING { atd.setOrderingMatchingRuleDescriptionOID(oid(ordering.getText())); } )
        |
        ( substr:SUBSTR { atd.setSubstringMatchingRuleDescriptionOID(oid(substr.getText())); } )
        |
        ( syntax:SYNTAX { atd.setSyntaxDescriptionNumericOIDPlusLength(qdstring(syntax.getText())); } )
        |
        ( SINGLE_VALUE { atd.setSingleValued( true ); } )
        |
        ( COLLECTIVE { atd.setCollective( true ); } )
        |
        ( NO_USER_MODIFICATION { atd.setNoUserModification( true ); } )
        |
        ( usage:USAGE { atd.setUsage(usage.getText()); } )
    )*
    RPAR
    ;

syntaxDescription returns [LdapSyntaxDescription lsd = new LdapSyntaxDescription()]
    :
    ( oid:STARTNUMERICOID { lsd.setNumericOID(oid.getText()); } )
    (
        ( desc:DESC { lsd.setDesc(qdstring(desc.getText())); } )
        |
        ( name:NAME { lsd.setDesc(qdstring(name.getText())); } )
    )*
    RPAR
    ;

matchingRuleDescription returns [MatchingRuleDescription mrd = new MatchingRuleDescription()]
    :
    ( oid:STARTNUMERICOID { mrd.setNumericOID(oid.getText()); } )
    (
        ( name:NAME { mrd.setNames(qdescrs(name.getText())); } )
        |
        ( desc:DESC { mrd.setDesc(qdstring(desc.getText())); } )
        |
        ( OBSOLETE { mrd.setObsolete( true ); } )    
        |
        ( syntax:SYNTAX { mrd.setSyntaxDescriptionNumericOID(syntax.getText()); } )
    )*
    RPAR
    ;
            
matchingRuleUseDescription returns [MatchingRuleUseDescription mrud = new MatchingRuleUseDescription()]
    :
    ( oid:STARTNUMERICOID { mrud.setNumericOID(oid.getText()); } )
    (
        ( name:NAME { mrud.setNames(qdescrs(name.getText())); } )
        |
        ( desc:DESC { mrud.setDesc(qdstring(desc.getText())); } )
        |
        ( OBSOLETE { mrud.setObsolete( true ); } )    
        |
        ( applies:APPLIES { mrud.setAppliesAttributeTypeDescriptionOIDs(oids(applies.getText())); } )
    )*
    RPAR
    ;
  
    

oid [String s] returns [String oid]
    {
    	SchemaValueLexer lexer = new SchemaValueLexer(new StringReader(s));
        SchemaValueParser parser = new SchemaValueParser(lexer);
        oid = parser.oid();
    }
    :
    ;

oids [String s] returns [String[] oids]
    {
    	SchemaValueLexer lexer = new SchemaValueLexer(new StringReader(s));
        SchemaValueParser parser = new SchemaValueParser(lexer);
        oids = parser.oids();
    }
    :
    ;

qdescrs [String s] returns [String[] qdescrs]
    {
    	SchemaValueLexer lexer = new SchemaValueLexer(new StringReader(s));
        SchemaValueParser parser = new SchemaValueParser(lexer);
        qdescrs = parser.qdescrs();
    }
    :
    ;

qdstring [String s] returns [String qdstring]
    {
    	if(s == null) {
	        qdstring = null;
    	}
    	else {
    		if(s.startsWith("'")) {
    			s = s.substring(1, s.length());
    		}
    		if(s.endsWith("'")) {
    			s = s.substring(0, s.length()-1);
    		}
    		qdstring = s;
    	}
    }
    :
    ;
    