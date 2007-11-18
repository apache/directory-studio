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

package org.apache.directory.studio.ldapbrowser.core.model.ldif.parser;


import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;


// RFC 2849
//
// ldif-file = ldif-content / ldif-changes
// ldif-content = version-spec 1*(1*SEP ldif-attrval-record)
// ldif-changes = version-spec 1*(1*SEP ldif-change-record)
// ldif-attrval-record = dn-spec SEP 1*attrval-spec
// ldif-change-record = dn-spec SEP *control changerecord
// version-spec = "version:" FILL version-number
// version-number = 1*DIGIT
// ; version-number MUST be "1" for the
// ; LDIF format described in this document.
// dn-spec = "dn:" (FILL distinguishedName /
// ":" FILL base64-distinguishedName)
// distinguishedName = SAFE-STRING
// ; a distinguished name, as defined in [3]
// base64-distinguishedName = BASE64-UTF8-STRING
// ; a distinguishedName which has been base64
// ; encoded (see note 10, below)
// rdn = SAFE-STRING
// ; a relative distinguished name, defined as
// ; <name-component> in [3]
// base64-rdn = BASE64-UTF8-STRING
// ; an rdn which has been base64 encoded (see
// ; note 10, below)
// control = "control:" FILL ldap-oid ; controlType
// 0*1(1*SPACE ("true" / "false")) ; criticality
// 0*1(value-spec) ; controlValue
// SEP
// ; (See note 9, below)
// ldap-oid = 1*DIGIT 0*1("." 1*DIGIT)
// ; An LDAPOID, as defined in [4]
// attrval-spec = AttributeDescription value-spec SEP
// value-spec = ":" ( FILL 0*1(SAFE-STRING) /
// ":" FILL (BASE64-STRING) /
// "<" FILL url)
// ; See notes 7 and 8, below
// url = <a Uniform Resource Locator,
// as defined in [6]>
// ; (See Note 6, below)
// AttributeDescription = AttributeType [";" options]
// ; Definition taken from [4]
// AttributeType = ldap-oid / (ALPHA *(attr-type-chars))
// options = option / (option ";" options)
// option = 1*opt-char
// attr-type-chars = ALPHA / DIGIT / "-"
// opt-char = attr-type-chars
// changerecord = "changetype:" FILL
// (change-add / change-delete /
// change-modify / change-moddn)
// change-add = "add" SEP 1*attrval-spec
// change-delete = "delete" SEP
// change-moddn = ("modrdn" / "moddn") SEP
// "newrdn:" ( FILL rdn /
// ":" FILL base64-rdn) SEP
// "deleteoldrdn:" FILL ("0" / "1") SEP
// 0*1("newsuperior:"
// ( FILL distinguishedName /
// ":" FILL base64-distinguishedName) SEP)
// change-modify = "modify" SEP *mod-spec
// mod-spec = ("add:" / "delete:" / "replace:")
// FILL AttributeDescription SEP
// *attrval-spec
// "-" SEP
// SPACE = %x20
// ; ASCII SP, space
// FILL = *SPACE
// SEP = (CR LF / LF)
// CR = %x0D
// ; ASCII CR, carriage return
// LF = %x0A
// ; ASCII LF, line feed
// ALPHA = %x41-5A / %x61-7A
// ; A-Z / a-z
// DIGIT = %x30-39
// ; 0-9
// UTF8-1 = %x80-BF
// UTF8-2 = %xC0-DF UTF8-1
// UTF8-3 = %xE0-EF 2UTF8-1
// UTF8-4 = %xF0-F7 3UTF8-1
// UTF8-5 = %xF8-FB 4UTF8-1
// UTF8-6 = %xFC-FD 5UTF8-1
// SAFE-CHAR = %x01-09 / %x0B-0C / %x0E-7F
// ; any value <= 127 decimal except NUL, LF,
// ; and CR
// SAFE-INIT-CHAR = %x01-09 / %x0B-0C / %x0E-1F /
// %x21-39 / %x3B / %x3D-7F
// ; any value <= 127 except NUL, LF, CR,
// ; SPACE, colon (":", ASCII 58 decimal)
// ; and less-than ("<" , ASCII 60 decimal)
// SAFE-STRING = [SAFE-INIT-CHAR *SAFE-CHAR]
// UTF8-CHAR = SAFE-CHAR / UTF8-2 / UTF8-3 /
// UTF8-4 / UTF8-5 / UTF8-6
// UTF8-STRING = *UTF8-CHAR
// BASE64-UTF8-STRING = BASE64-STRING
// ; MUST be the base64 encoding of a
// ; UTF8-STRING
// BASE64-CHAR = %x2B / %x2F / %x30-39 / %x3D / %x41-5A /
// %x61-7A
// ; +, /, 0-9, =, A-Z, and a-z
// ; as specified in [5]
// BASE64-STRING = [*(BASE64-CHAR)]

public class LdifScanner
{

    private Reader ldifReader;

    private char[] buffer = new char[256];

    private StringBuffer ldifBuffer;

    private int ldifBufferOffset;

    private int pos;


    public LdifScanner()
    {
        super();
    }


    public void setLdif( Reader ldifReader )
    {
        // this.ldif = ldif;
        this.ldifReader = ldifReader;
        this.pos = -1;

        this.ldifBuffer = new StringBuffer();
        this.ldifBufferOffset = 0;
    }


    char currentChar() throws EOFException
    {

        // check and fill buffer
        try
        {
            int num = 0;
            while ( ldifBufferOffset + ldifBuffer.length() <= pos && num > -1 )
            {
                num = this.ldifReader.read( buffer );
                if ( num > -1 )
                {
                    ldifBuffer.append( buffer, 0, num );
                }
            }
        }
        catch ( IOException e )
        {
        }

        if ( 0 <= pos && pos < ldifBufferOffset + ldifBuffer.length() )
        {
            try
            {
                return ldifBuffer.charAt( pos - ldifBufferOffset );
            }
            catch ( RuntimeException e )
            {
                e.printStackTrace();
                throw e;
            }
        }
        else
        {
            throw new EOFException();
        }

        // return 0<=pos&&pos<ldif.length() ? ldif.charAt(pos) : '\u0000';
    }


    void addFolding( StringBuffer sb )
    {

        int oldPos = pos;

        try
        {
            pos++;
            char c = currentChar();
            if ( c == '\n' || c == '\r' )
            {
                StringBuffer temp = new StringBuffer( 3 );
                temp.append( c );
                if ( c == '\r' )
                {
                    pos++;
                    c = currentChar();
                    if ( c == '\n' )
                    {
                        temp.append( c );
                    }
                    else
                    {
                        pos--;
                    }
                }
                else if ( c == '\n' )
                {
                    pos++;
                    c = currentChar();
                    if ( c == '\r' )
                    {
                        temp.append( c );
                    }
                    else
                    {
                        pos--;
                    }
                }

                pos++;
                c = currentChar();
                if ( c == ' ' )
                {
                    // space after newline, continue
                    temp.append( c );
                    sb.append( temp );
                }
                else
                {
                    for ( int i = 0; i < temp.length(); i++ )
                    {
                        pos--;
                    }
                    pos--;
                }
            }
            else
            {
                pos--;
            }
        }
        catch ( EOFException e )
        {
            // reset position
            pos = oldPos;
        }

    }


    /**
     * Reads the next character from input stram if available. If read was
     * possible the character is appended to the given StringBuffer and
     * returned. Otherwise throws a EOFException. Additionally this method
     * checks folding sequence SEP + SPACE. If any folding sequence was
     * found the sequence is appended to the given StringBuffer. So it is
     * possible the StringBuffer doesn't end with the read character after
     * calling this method but with a folding sequence
     * 
     * @param sb
     * @return the next character if available
     * @throws EOFException
     */
    public char read( StringBuffer sb ) throws EOFException
    {
        try
        {

            // check EOF
            // if(pos > -1) {
            // currentChar();
            // }

            // get next char
            pos++;
            char c = currentChar();
            sb.append( c );

            // folding
            addFolding( sb );

            return c;
        }
        catch ( EOFException e )
        {
            pos--;
            throw e;
        }
    }


    void removeFolding( StringBuffer sb )
    {

        int oldPos = pos;

        try
        {
            char c = currentChar();
            pos--;
            if ( c == ' ' )
            {
                StringBuffer temp = new StringBuffer();
                temp.insert( 0, c );
                c = currentChar();
                pos--;

                if ( c == '\n' || c == '\r' )
                {
                    if ( c == '\r' )
                    {
                        temp.insert( 0, c );
                        c = currentChar();
                        pos--;
                        if ( c == '\n' )
                        {
                            temp.insert( 0, c );
                        }
                        else
                        {
                            pos++;
                        }
                    }
                    else if ( c == '\n' )
                    {
                        temp.insert( 0, c );
                        c = currentChar();
                        pos--;
                        if ( c == '\r' )
                        {
                            temp.insert( 0, c );
                        }
                        else
                        {
                            pos++;
                        }
                    }

                    sb.delete( sb.length() - temp.length(), sb.length() );
                }
                else
                {
                    pos++;
                    pos++;
                }
            }
            else
            {
                pos++;
            }
        }
        catch ( EOFException e )
        {
            // reset position
            pos = oldPos;
        }
    }


    /**
     * Inverses the previous read().
     * 
     * @param sb
     * @return the previous character if available
     * @throws EOFException
     */
    public void unread( StringBuffer sb )
    {
        removeFolding( sb );

        if ( pos > -1 )
        {
            pos--;

            if ( sb.length() > 0 )
            {
                sb.deleteCharAt( sb.length() - 1 );
            }
        }
    }


    private String getFullLine( String start )
    {
        String s1 = this.getWord( start );
        if ( s1 != null )
        {
            String s2 = getContent();
            return s2 != null ? s1 + s2 : s1;
        }
        else
        {
            return null;
        }
    }


    private String getContent()
    {

        StringBuffer sb = new StringBuffer( 256 );

        try
        {
            char c = '\u0000';
            while ( c != '\n' && c != '\r' )
            {
                c = read( sb );
            }
            unread( sb );

        }
        catch ( EOFException e )
        {
        }

        return sb.length() > 0 ? sb.toString() : null;
    }


    // private String getStartAndFill(String start) {
    // String s = this.getWord(start);
    // if(s != null) {
    // StringBuffer sb = new StringBuffer(s);
    //			
    // try {
    // char c = '\u0000';
    // while (c==' ') {
    // c = read(sb);
    // }
    // unread(sb);
    // } catch (EOFException e) {
    // }
    //    		
    // return sb.toString();
    // }
    // else {
    // return null;
    // }
    // }

    private String getWord( String word )
    {
        StringBuffer sb = new StringBuffer();

        // read
        try
        {
            boolean matches = true;
            for ( int i = 0; i < word.length(); i++ )
            {

                char c = read( sb );
                if ( c != word.charAt( i ) )
                {
                    matches = false;
                    unread( sb );
                    break;
                }
            }

            if ( matches )
            {
                return sb.toString();
            }
        }
        catch ( EOFException e )
        {
        }

        // unread
        while ( sb.length() > 0 )
        {
            unread( sb );
        }
        // prevChar(sb);
        return null;
    }


    private String getWordTillColon( String word )
    {

        String wordWithColon = word + ":";
        String line = getWord( wordWithColon );
        if ( line != null )
        {
            StringBuffer sb = new StringBuffer( line );
            unread( sb );
            return sb.toString();
        }

        // allow eof and sep
        line = getWord( word );
        if ( line != null )
        {
            StringBuffer sb = new StringBuffer( line );
            try
            {
                char c = read( sb );
                unread( sb );
                if ( c == '\r' || c == '\n' )
                {
                    return sb.toString();
                }
                else
                {
                    while ( sb.length() > 0 )
                    {
                        unread( sb );
                    }
                    return null;
                }
            }
            catch ( EOFException e )
            {
                return sb.toString();
            }
        }

        return null;
    }


    private void flushBuffer()
    {

        // System.out.println("flushBuffer():
        // before("+this.pos+","+this.ldifBufferOffset+")");

        if ( this.ldifBufferOffset < this.pos && this.ldifBuffer.length() > 0 )
        {
            int delta = Math.min( pos - this.ldifBufferOffset, this.ldifBuffer.length() );
            delta--;
            this.ldifBuffer.delete( 0, delta );
            this.ldifBufferOffset += delta;
        }

        // System.out.println("flushBuffer():
        // after("+this.pos+","+this.ldifBufferOffset+")");
    }


    public LdifToken matchCleanupLine()
    {
        this.flushBuffer();

        String line = getContent();
        LdifToken sep = matchSep();

        if ( line != null || sep != null )
        {
            if ( line == null )
                line = "";

            if ( sep != null )
                line += sep.getValue();

            return new LdifToken( LdifToken.UNKNOWN, line, pos - line.length() + 1 );
        }

        return null;
    }


    public LdifToken matchOther()
    {
        this.flushBuffer();

        String line = getContent();
        if ( line != null )
        {
            LdifToken sep = matchSep();
            if ( sep != null )
                line += sep.getValue();
            return new LdifToken( LdifToken.UNKNOWN, line, pos - line.length() + 1 );
        }

        return null;
    }


    public LdifToken matchEOF()
    {
        this.flushBuffer();

        StringBuffer sb = new StringBuffer( 1 );
        try
        {
            read( sb );
            unread( sb );
            return null;
        }
        catch ( EOFException e )
        {
            return new LdifToken( LdifToken.EOF, "", pos + 1 );
        }

    }


    public LdifToken matchSep()
    {
        this.flushBuffer();

        try
        {
            StringBuffer sb = new StringBuffer();
            char c = read( sb );
            if ( c == '\n' || c == '\r' )
            {

                // check for two-char-linebreak
                try
                {
                    if ( c == '\r' )
                    {
                        c = read( sb );
                        if ( c != '\n' )
                        {
                            unread( sb );
                        }
                    }
                    else if ( c == '\n' )
                    {
                        c = read( sb );
                        if ( c != '\r' )
                        {
                            unread( sb );
                        }
                    }
                }
                catch ( EOFException e )
                {
                }

                return new LdifToken( LdifToken.SEP, sb.toString(), pos - sb.length() + 1 );
            }
            else
            {
                unread( sb );
            }
        }
        catch ( EOFException e )
        {
        }

        return null;
    }


    public LdifToken matchComment()
    {
        this.flushBuffer();

        String line = getFullLine( "#" );
        if ( line != null )
        {
            return new LdifToken( LdifToken.COMMENT, line, pos - line.length() + 1 );
        }

        return null;
    }


    public LdifToken matchVersionSpec()
    {
        this.flushBuffer();

        String line = getWordTillColon( "version" );
        if ( line != null )
        {
            return new LdifToken( LdifToken.VERSION_SPEC, line, pos - line.length() + 1 );
        }

        return null;
    }


    public LdifToken matchDnSpec()
    {
        this.flushBuffer();

        String line = getWordTillColon( "dn" );
        if ( line != null )
        {
            return new LdifToken( LdifToken.DN_SPEC, line, pos - line.length() + 1 );
        }

        return null;
    }


    public LdifToken matchControlSpec()
    {
        this.flushBuffer();

        String line = getWordTillColon( "control" );
        if ( line != null )
        {
            return new LdifToken( LdifToken.CONTROL_SPEC, line, pos - line.length() + 1 );
        }

        return null;
    }


    public LdifToken matchChangeTypeSpec()
    {
        this.flushBuffer();

        String line = getWordTillColon( "changetype" );
        if ( line != null )
        {
            return new LdifToken( LdifToken.CHANGETYPE_SPEC, line, pos - line.length() + 1 );
        }

        return null;
    }


    public LdifToken matchChangeType()
    {
        this.flushBuffer();

        String line = getWord( "add" );
        if ( line != null )
        {
            return new LdifToken( LdifToken.CHANGETYPE_ADD, line, pos - line.length() + 1 );
        }
        line = getWord( "modify" );
        if ( line != null )
        {
            return new LdifToken( LdifToken.CHANGETYPE_MODIFY, line, pos - line.length() + 1 );
        }
        line = getWord( "delete" );
        if ( line != null )
        {
            return new LdifToken( LdifToken.CHANGETYPE_DELETE, line, pos - line.length() + 1 );
        }
        line = getWord( "moddn" );
        if ( line != null )
        {
            return new LdifToken( LdifToken.CHANGETYPE_MODDN, line, pos - line.length() + 1 );
        }
        line = getWord( "modrdn" );
        if ( line != null )
        {
            return new LdifToken( LdifToken.CHANGETYPE_MODDN, line, pos - line.length() + 1 );
        }

        return null;
    }


    public LdifToken matchCriticality()
    {
        this.flushBuffer();

        StringBuffer sb = new StringBuffer();

        String s = getWord( " " );
        while ( s != null )
        {
            sb.append( s );
            s = getWord( " " );
        }

        String t = getWord( "true" );
        if ( t != null )
        {
            sb.append( t );
            return new LdifToken( LdifToken.CONTROL_CRITICALITY_TRUE, sb.toString(), pos - sb.length() + 1 );
        }
        String f = getWord( "false" );
        if ( f != null )
        {
            sb.append( f );
            return new LdifToken( LdifToken.CONTROL_CRITICALITY_FALSE, sb.toString(), pos - sb.length() + 1 );
        }

        while ( sb.length() > 0 )
        {
            unread( sb );
        }

        // for(int i=0; i<sb.length(); i++) {
        // unread(sb);
        // }

        return null;
    }


    public LdifToken matchNumber()
    {
        this.flushBuffer();

        try
        {
            StringBuffer sb = new StringBuffer();
            char c = read( sb );
            if ( '0' <= c && c <= '9' )
            {

                try
                {
                    while ( '0' <= c && c <= '9' )
                    {
                        c = read( sb );
                    }
                    unread( sb );
                }
                catch ( EOFException e )
                {
                }

                return new LdifToken( LdifToken.NUMBER, sb.toString(), pos - sb.length() + 1 );
            }
            else
            {
                unread( sb );
            }
        }
        catch ( EOFException e )
        {
        }

        return null;
    }


    public LdifToken matchOid()
    {
        this.flushBuffer();

        try
        {
            StringBuffer sb = new StringBuffer();
            char c = read( sb );
            if ( '0' <= c && c <= '9' )
            {

                try
                {
                    while ( '0' <= c && c <= '9' || c == '.' )
                    {
                        c = read( sb );
                    }
                    unread( sb );
                }
                catch ( EOFException e )
                {
                }

                return new LdifToken( LdifToken.OID, sb.toString(), pos - sb.length() + 1 );
            }
            else
            {
                unread( sb );
            }
        }
        catch ( EOFException e )
        {
        }

        return null;
    }


    public LdifToken matchAttributeDescription()
    {
        this.flushBuffer();

        try
        {
            StringBuffer sb = new StringBuffer();
            char c = read( sb );
            if ( 'a' <= c && c <= 'z' || 'A' <= c && c <= 'Z' || '0' <= c && c <= '9' )
            {

                try
                {
                    while ( 'a' <= c && c <= 'z' || 'A' <= c && c <= 'Z' || '0' <= c && c <= '9' || c == '.'
                        || c == ';' || c == '-' )
                    {
                        c = read( sb );
                    }
                    unread( sb );
                }
                catch ( EOFException e )
                {
                }

                return new LdifToken( LdifToken.ATTRIBUTE, sb.toString(), pos - sb.length() + 1 );
            }
            else
            {
                unread( sb );
            }
        }
        catch ( EOFException e )
        {
        }

        // // a-z,A-Z,0-9,.,-,;
        // StringBuffer sb = new StringBuffer();
        // char c = nextChar(sb);
        // if('a'<=c&&c<='z' || 'A'<=c&&c<='Z' || '0'<=c&&c<='9') {
        // while('a'<=c&&c<='z' || 'A'<=c&&c<='Z' || '0'<=c&&c<='9' || c=='.' ||
        // c==';' || c=='-') {
        // sb.append(c);
        // c = nextChar(sb);
        // }
        // unread(sb);
        //
        // return new LdifToken(LdifToken.ATTRIBUTE, sb.toString(),
        // pos-sb.length()+1);
        // }
        // else {
        // unread(sb);
        // }

        return null;
    }


    public LdifToken matchModTypeSpec()
    {
        this.flushBuffer();

        String line = getWord( "add" );
        if ( line != null )
        {
            return new LdifToken( LdifToken.MODTYPE_ADD_SPEC, line, pos - line.length() + 1 );
        }
        line = getWord( "replace" );
        if ( line != null )
        {
            return new LdifToken( LdifToken.MODTYPE_REPLACE_SPEC, line, pos - line.length() + 1 );
        }
        line = getWord( "delete" );
        if ( line != null )
        {
            return new LdifToken( LdifToken.MODTYPE_DELETE_SPEC, line, pos - line.length() + 1 );
        }

        return null;
    }


    public LdifToken matchModSep()
    {
        this.flushBuffer();

        String line = getWord( "-" );
        if ( line != null )
        {
            return new LdifToken( LdifToken.MODTYPE_SEP, line, pos - line.length() + 1 );
        }

        return null;
    }


    public LdifToken matchValueType()
    {
        this.flushBuffer();

        try
        {
            StringBuffer sb = new StringBuffer();
            char c = read( sb );
            if ( c == ':' )
            {

                int tokenType = LdifToken.VALUE_TYPE_SAFE;
                try
                {
                    c = read( sb );
                    if ( c == ':' )
                    {
                        tokenType = LdifToken.VALUE_TYPE_BASE64;
                    }
                    else if ( c == '<' )
                    {
                        tokenType = LdifToken.VALUE_TYPE_URL;
                    }
                    else
                    {
                        tokenType = LdifToken.VALUE_TYPE_SAFE;
                        unread( sb );
                    }

                    c = read( sb );
                    while ( c == ' ' )
                    {
                        c = read( sb );
                    }
                    unread( sb );

                }
                catch ( EOFException e )
                {
                }

                return new LdifToken( tokenType, sb.toString(), pos - sb.length() + 1 );
            }
            else
            {
                unread( sb );
            }
        }
        catch ( EOFException e )
        {
        }

        return null;
    }


    public LdifToken matchValue()
    {
        this.flushBuffer();

        String line = getContent();
        if ( line != null )
        {
            return new LdifToken( LdifToken.VALUE, line, pos - line.length() + 1 );
        }

        return null;
    }


    public LdifToken matchNewrdnSpec()
    {
        this.flushBuffer();

        String line = getWordTillColon( "newrdn" );
        if ( line != null )
        {
            return new LdifToken( LdifToken.MODDN_NEWRDN_SPEC, line, pos - line.length() + 1 );
        }

        return null;
    }


    public LdifToken matchDeleteoldrdnSpec()
    {
        this.flushBuffer();

        String line = getWordTillColon( "deleteoldrdn" );
        if ( line != null )
        {
            return new LdifToken( LdifToken.MODDN_DELOLDRDN_SPEC, line, pos - line.length() + 1 );
        }

        return null;
    }


    public LdifToken matchNewsuperiorSpec()
    {
        this.flushBuffer();

        String line = getWordTillColon( "newsuperior" );
        if ( line != null )
        {
            return new LdifToken( LdifToken.MODDN_NEWSUPERIOR_SPEC, line, pos - line.length() + 1 );
        }

        return null;
    }

}
