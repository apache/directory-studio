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

package org.apache.directory.studio.ldapbrowser.common.filtereditor;


import org.apache.directory.studio.ldapbrowser.core.model.filter.LdapAndFilterComponent;
import org.apache.directory.studio.ldapbrowser.core.model.filter.LdapFilter;
import org.apache.directory.studio.ldapbrowser.core.model.filter.LdapFilterComponent;
import org.apache.directory.studio.ldapbrowser.core.model.filter.LdapNotFilterComponent;
import org.apache.directory.studio.ldapbrowser.core.model.filter.LdapOrFilterComponent;
import org.apache.directory.studio.ldapbrowser.core.model.filter.parser.LdapFilterParser;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;


/**
 * The FilterAutoEditStrategy implements the IAutoEditStrategy for the filter editor widget.
 * It provides smart parentesis handling when typing the filter.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class FilterAutoEditStrategy extends DefaultIndentLineAutoEditStrategy implements IAutoEditStrategy
{

    /** The Constant INDENT_STRING. */
    public static final String INDENT_STRING = "    ";

    /** The filter parser. */
    private LdapFilterParser parser;


    /**
     * Creates a new instance of FilterAutoEditStrategy.
     * 
     * @param parser the filter parser
     */
    public FilterAutoEditStrategy( LdapFilterParser parser )
    {
        this.parser = parser;
    }


    /**
     * @see org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy#customizeDocumentCommand(org.eclipse.jface.text.IDocument, org.eclipse.jface.text.DocumentCommand)
     */
    public void customizeDocumentCommand( IDocument d, DocumentCommand c )
    {
        super.customizeDocumentCommand( d, c );
        AutoEditParameters aep = new AutoEditParameters( c.text, c.offset, c.length, c.caretOffset, c.shiftsCaret );
        customizeAutoEditParameters( d.get(), aep );
        c.offset = aep.offset;
        c.length = aep.length;
        c.text = aep.text;
        c.caretOffset = aep.caretOffset;
        c.shiftsCaret = aep.shiftsCaret;
    }


    /**
     * Customizes auto edit parameters.
     * 
     * @param currentFilter the current filter
     * @param aep the auto edit parameters
     */
    public void customizeAutoEditParameters( String currentFilter, AutoEditParameters aep )
    {
        parser.parse( currentFilter );
        LdapFilter filter = parser.getModel().getFilter( aep.offset );
        if ( filter == null )
        {
            return;
        }

        // check balanced parenthesis
        int balanced = 0;
        for ( int i = 0; i < currentFilter.length(); i++ )
        {
            if ( currentFilter.charAt( i ) == '(' )
            {
                balanced++;
            }
            else if ( currentFilter.charAt( i ) == ')' )
            {
                balanced--;
            }
        }

        if ( aep.length > 0 && ( aep.text == null || "".equals( aep.text ) ) )
        {
            // delete surrounding parenthesis after deleting the last character
            if ( filter.toString().length() - aep.length == 2 
                && filter.getStartToken() != null
                && filter.getStopToken() != null
                && aep.offset >= filter.getStartToken().getOffset() + filter.getStartToken().getLength()
                && aep.offset + aep.length <= filter.getStopToken().getOffset() )
            {
                if ( filter.toString().length() - aep.length == 2 )
                {
                    aep.offset -= 1;
                    aep.length += 2;
                    aep.caretOffset = aep.offset;
                    aep.shiftsCaret = false;
                }
            }
            
            // delete closing parenthesis after deleting the opening parenthesis
            if ( filter.toString().length() - aep.length == 1
                && filter.getStartToken() != null
                && filter.getStopToken() != null
                && aep.offset == filter.getStartToken().getOffset() )
            {
                aep.length += 1;
                aep.caretOffset = aep.offset;
                aep.shiftsCaret = false;
            }
            
        }

        if ( (aep.length == 0 || aep.length==currentFilter.length()) && aep.text != null && !"".equals( aep.text ) )
        {
            boolean isNewFilter = aep.text.equals( "(" );
            boolean isNewNestedFilter = aep.text.equals( "&" ) || aep.text.equals( "|" ) || aep.text.equals( "!" );
            boolean isSurroundNew = false;
            boolean isSurroundNested = false;
            boolean isSurroundBeforeOtherFilter = false;
            boolean isSurroundAfterOtherFilter = false;
            if( !Character.isWhitespace( aep.text.charAt( 0 ) ) && !aep.text.startsWith( "(" ) && !aep.text.endsWith( ")" ) )
            {
                // isSurroundNew
                isSurroundNew = aep.offset == 0;

                // isSurroundNested
                if ( filter.getStartToken() != null
                    && filter.getFilterComponent() != null
                    && ( filter.getFilterComponent() instanceof LdapAndFilterComponent
                        || filter.getFilterComponent() instanceof LdapOrFilterComponent || filter.getFilterComponent() instanceof LdapNotFilterComponent ) )
                {
                    LdapFilterComponent fc = filter.getFilterComponent();
                    LdapFilter[] filters = fc.getFilters();

                    if ( filters.length == 0 && aep.offset > fc.getStartToken().getOffset() )
                    {
                        // no nested filter yet
                        isSurroundNested = true;
                    }

                    if ( filters.length > 0 && aep.offset > fc.getStartToken().getOffset()
                        && aep.offset < filters[0].getStartToken().getOffset() )
                    {
                        // before first nested filter
                        isSurroundNested = true;
                    }

                    if ( filters.length > 0 && aep.offset > filters[filters.length - 1].getStopToken().getOffset()
                        && aep.offset <= filter.getStopToken().getOffset() )
                    {
                        // after last nested filter
                        isSurroundNested = true;
                    }

                    for ( int i = 0; i < filters.length; i++ )
                    {
                        if ( filters.length > i + 1 )
                        {
                            if ( aep.offset > filters[i].getStopToken().getOffset()
                                && aep.offset <= filters[i + 1].getStopToken().getOffset() )
                            {
                                // between nested filter
                                isSurroundNested = true;
                            }
                        }
                    }
                }

                // isSurroundBeforeOtherFilter
                isSurroundBeforeOtherFilter = filter.getStartToken() != null
                    && aep.offset == filter.getStartToken().getOffset();

                // isSurroundAfterOtherFilter
                isSurroundAfterOtherFilter = filter.getStopToken() != null
                    && aep.offset == filter.getStopToken().getOffset()
                    && ( filter.getFilterComponent() instanceof LdapAndFilterComponent
                        || filter.getFilterComponent() instanceof LdapOrFilterComponent || filter.getFilterComponent() instanceof LdapNotFilterComponent );
            }
            
            //System.out.println("isSurroundNew="+isSurroundNew+", isSurroundNested="+isSurroundNested+", isSurroundAfterOtherFilter="+isSurroundAfterOtherFilter+", isSurroundBeforeOtherFilter="+isSurroundBeforeOtherFilter);

            // add opening parenthesis '('
            if ( isSurroundNew || isSurroundNested || isSurroundAfterOtherFilter || isSurroundBeforeOtherFilter )
            {
                aep.text = "(" + aep.text;
                aep.caretOffset = aep.offset + aep.text.length();
                aep.shiftsCaret = false;
            }

            // add parenthesis for nested filters
            if ( isNewNestedFilter )
            {
                aep.text = aep.text + "()";
                aep.caretOffset = aep.offset + aep.text.length() - 1;
                aep.shiftsCaret = false;
            }

            // add closing parenthesis ')'
            if ( isNewFilter || isSurroundNew || isSurroundNested || isSurroundAfterOtherFilter
                || isSurroundBeforeOtherFilter )
            {
                if ( balanced == 0 )
                {
                    aep.text = aep.text + ")";
                    if( aep.caretOffset == -1 )
                    {
                        aep.caretOffset = aep.offset + aep.text.length() - 1;
                        aep.shiftsCaret = false;
                    }
                }
            }
            
            // translate tab to IDENT_STRING
            if ( aep.text.equals( "\t" ) )
            {
                aep.text = INDENT_STRING;
            }
        }
        
        //System.out.println( "aep='"+aep.text+"',"+aep.offset+","+aep.length+","+aep.caretOffset+","+aep.shiftsCaret+"; balanced="+balanced+"; filter='"+filter.toString()+"'" );

    }

    /**
     * Helper class.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    public static class AutoEditParameters
    {

        /** The text. */
        public String text;

        /** The offset. */
        public int offset;

        /** The length. */
        public int length;

        /** The caret offset. */
        public int caretOffset;

        /** The shifts caret flag. */
        public boolean shiftsCaret;


        /**
         * Creates a new instance of AutoEditParameters.
         * 
         * @param text the text
         * @param offset the offset
         * @param length the length
         * @param caretOffset the caret offset
         * @param shiftsCaret the shifts caret flag
         */
        public AutoEditParameters( String text, int offset, int length, int caretOffset, boolean shiftsCaret )
        {
            this.text = text;
            this.offset = offset;
            this.length = length;
            this.caretOffset = caretOffset;
            this.shiftsCaret = shiftsCaret;
        }
    }

}
