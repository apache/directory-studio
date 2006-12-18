header {
package org.apache.directory.ldapstudio.browser.core.model.schema.parser;

import java.util.* ;

}

class SchemaValueLexer extends Lexer;

options    {
    k = 2 ;
    //importVocab=NonQuote ;
    exportVocab=SchemaValue ;
    charVocabulary = '\3'..'\377' ;
    caseSensitive = false ;
    //testLiterals = true ;
    defaultErrorHandler = false ;
}

WHSP : ( ' ' ) {$setType(Token.SKIP);} ;

LPAR : '(' ;
RPAR : ')' ;

QUOTE : '\'' ;
DOLLAR : '$' ;
LBRACKET : '{' ;
RBRACKET : '}' ;
LEN : LBRACKET (DIGIT)+ RBRACKET ;
DIGIT : ('0'..'9') ; 
NUMERICOID : ('0'..'9')+ ( '.' ('0'..'9')+ )+ ;
DESCR : ( 'a'..'z') ( 'a'..'z' | '0'..'9' | '-' | ';' | '.' )* ;



class SchemaValueParser extends Parser;
options    {
    k = 3 ;
    defaultErrorHandler = false ;
    //buildAST=true ;
}


oids returns [String[] oids]
    {
        oids = new String[0];
        List oidList = new ArrayList();
        String oid = null;
    }
    :
    (
        ( oid=oid { oidList.add(oid); } )
    |
        ( LPAR oid=oid { oidList.add(oid); } ( DOLLAR oid=oid { oidList.add(oid); } )* RPAR )
    )
    { 
        oids = (String[])oidList.toArray(new String[oidList.size()]); 
    }
    ;


oid returns [String oid=null]
    : 
    (
        n:NUMERICOID { oid = n.getText(); }
    | 
        d:DESCR { oid = d.getText(); }
    )
    ;


qdescrs returns [String[] qdescrs]
    {
    	qdescrs = new String[0];
        List qdescrList = new ArrayList();
        String qdescr = null;
    }
    :
    (
        ( qdescr=qdescr { qdescrList.add(qdescr); } )
    |
        ( LPAR qdescr=qdescr { qdescrList.add(qdescr); } ( qdescr=qdescr { qdescrList.add(qdescr); } )* RPAR )
    )
    { 
        qdescrs = (String[])qdescrList.toArray(new String[qdescrList.size()]); 
    }
    ;
    
qdescr returns [String qdescr=null]
    : 
    ( 
        QUOTE d:DESCR { qdescr = d.getText(); } QUOTE
    )
    ;    



