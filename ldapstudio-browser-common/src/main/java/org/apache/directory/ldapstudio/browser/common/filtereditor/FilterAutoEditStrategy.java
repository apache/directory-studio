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

package org.apache.directory.ldapstudio.browser.common.filtereditor;


import org.apache.directory.ldapstudio.browser.core.model.filter.LdapFilter;
import org.apache.directory.ldapstudio.browser.core.model.filter.parser.LdapFilterParser;
import org.apache.directory.ldapstudio.browser.core.model.filter.parser.LdapFilterToken;

import org.eclipse.jface.text.DefaultAutoIndentStrategy;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.source.SourceViewer;


// TODO: Refactor Filter Editor
public class FilterAutoEditStrategy extends DefaultAutoIndentStrategy implements IAutoEditStrategy
{

    public static final String INDENT_STRING = "    ";

    private LdapFilterParser parser;

    private SourceViewer sourceViewer;

    private int autoRightParenthesisFilterOffset;


    public FilterAutoEditStrategy( SourceViewer sourceViewer, LdapFilterParser parser )
    {
        super();
        this.sourceViewer = sourceViewer;
        this.parser = parser;
        this.autoRightParenthesisFilterOffset = -1;
    }


    public void customizeDocumentCommand( IDocument d, DocumentCommand c )
    {

        LdapFilter filter = this.parser.getModel().getFilter( c.offset );

        // this.dumpDocumentCommand(c);

        if ( c.length == 0 && c.text != null )
        {
            // new line
            if ( TextUtilities.endsWith( d.getLegalLineDelimiters(), c.text ) != -1 )
            {
                super.customizeDocumentCommand( d, c );
                if ( filter != null && filter.getFilterComponent() != null )
                {
                    LdapFilterToken startToken = filter.getFilterComponent().getStartToken();
                    if ( startToken != null
                        && ( startToken.getType() == LdapFilterToken.AND || startToken.getType() == LdapFilterToken.OR ) )
                    {

                        if ( startToken.getOffset() == c.offset - 1 )
                        {
                            c.text += INDENT_STRING;
                            if ( filter.getStopToken() != null && filter.getStopToken().getOffset() == c.offset )
                            {
                                c.caretOffset = c.offset + c.text.length();
                                c.shiftsCaret = false;
                                c.text += "\n";
                                super.customizeDocumentCommand( d, c );
                            }
                        }
                    }
                }
            }

            // filter start/stop
            if ( c.text.equals( "(" ) )
            {
                c.text = "()";
                c.caretOffset = c.offset + 1;
                c.shiftsCaret = false;
                this.autoRightParenthesisFilterOffset = c.offset;
            }
            else if ( c.text.equals( ")" ) )
            {
                LdapFilter filter2 = this.parser.getModel().getFilter( this.autoRightParenthesisFilterOffset );
                if ( filter2 != null && filter2.getStopToken() != null
                    && filter2.getStopToken().getOffset() == c.offset )
                {
                    c.text = "";
                    c.caretOffset = c.offset + 1;
                    c.shiftsCaret = false;
                }
                this.autoRightParenthesisFilterOffset = -1;
            }

            // tab to IDENT_STRING
            if ( c.text.equals( "\t" ) )
            {
                c.text = INDENT_STRING;
            }

            // smart formatting
            if ( filter != null && filter.getStartToken() != null && filter.getFilterComponent() == null )
            {
                if ( c.text.equals( "&" ) || c.text.equals( "|" ) )
                {
                    if ( filter.getStartToken().getOffset() == c.offset - 1 )
                    {
                        c.text += "\n";
                        super.customizeDocumentCommand( d, c );
                        c.text += INDENT_STRING + "()";
                        c.caretOffset = c.offset + c.text.length() - 1;
                        c.shiftsCaret = false;
                        if ( filter.getStopToken() != null && filter.getStopToken().getOffset() == c.offset )
                        {
                            c.text += "\n";
                            super.customizeDocumentCommand( d, c );
                        }

                    }
                }
                else if ( c.text.equals( "!" ) )
                {
                    if ( filter.getStartToken().getOffset() == c.offset - 1 )
                    {
                        c.text += "()";
                        c.caretOffset = c.offset + c.text.length() - 1;
                        c.shiftsCaret = false;
                    }
                }
            }

        }
    }


    private void autoIndentAfterNewLine()
    {

    }


    private void dumpDocumentCommand( DocumentCommand command )
    {
        System.out.println( "----------------------------------" );
        System.out.println( "  offset     : " + command.offset );
        System.out.println( "  length     : " + command.length );
        System.out.println( "  text       : " + command.text );
        System.out.println( "  caretoffset: " + command.caretOffset );
        System.out.println( "  shiftsCaret: " + command.shiftsCaret );
        System.out.println( "  doit       : " + command.doit );

    }
}
