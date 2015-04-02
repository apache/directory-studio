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
 * A reusable wrapper around the antlr generated parser for an OpenLDAP ACL.
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
     * @param s
     *            the string to be parsed
     * @return the specification bean
     * @throws ParseException
     *             if there are any recognition errors (bad syntax)
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
