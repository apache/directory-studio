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
package org.apache.directory.ldapstudio.aciitemeditor.sourceeditor;

import org.apache.directory.ldapstudio.browser.core.BrowserCoreConstants;
import org.eclipse.jface.text.formatter.IFormattingStrategy;
import org.eclipse.jface.text.source.ISourceViewer;

/**
 * This class implements the formatting strategy for the ACI Item Editor.
 * <ul>
 * <li>New line after a comma
 * <li>New line after a opened left curly
 * </ul>
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ACIFormattingStrategy implements IFormattingStrategy
{

    /** The Constant INDENT_STRING. */
    public static final String INDENT_STRING = "    "; //$NON-NLS-1$
    
    /** The Constant NEWLINE. */
    public static final String NEWLINE = BrowserCoreConstants.LINE_SEPARATOR;
    
    /** The source viewer. */
    private ISourceViewer sourceViewer;
    
    
    /**
     * Creates a new instance of ACIFormattingStrategy.
     *
     * @param sourceViewer the source viewer
     */
    public ACIFormattingStrategy( ISourceViewer sourceViewer )
    {
        this.sourceViewer = sourceViewer;
    }


    /**
     * {@inheritDoc}
     */
    public String format( String content, boolean isLineStart, String indentation, int[] positions )
    {
        String oldContent = sourceViewer.getDocument().get();
        String newContent = internFormat ( oldContent );
        sourceViewer.getDocument().set( newContent );
        
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public void formatterStarts( String initialIndentation )
    {
    }


    /**
     * {@inheritDoc}
     */
    public void formatterStops()
    {
    }

    private String internFormat( String content )
    {
        StringBuffer sb = new StringBuffer();

        // flag to track if a new line was started
        boolean newLineStarted = true;
        
        // flag to track if we are within a quoted string
        boolean inQuotedString = false;
        
        // flag to track if the current expression is appended in one-line mode
        boolean oneLineMode = false;
        
        // the current indent
        int indent = 0;

        int contentLength = content.length();
        for (int i=0; i<contentLength; i++)
        {
            char c = content.charAt( i );
            
            // track quotes
            if( c == '"')
            {
                inQuotedString = !inQuotedString;
            }
            
            if( c == '{' && !inQuotedString )
            {
                // check one-line mode
                oneLineMode = checkInOneLine(i, content);
                
                if(oneLineMode)
                {
                    // no new line in one-line mode
                    sb.append( c );
                    newLineStarted = false;
                }
                else
                {
                    // start a new line, but avoid blank lines if there are multiple opened curlies
                    if( !newLineStarted )
                    {
                        sb.append( NEWLINE );
                        for ( int x = 0; x < indent; x++ )
                        {
                            sb.append( INDENT_STRING );
                        }
                    }
                    
                    // append the curly
                    sb.append( c );
                    
                    // start a new line and increment indent
                    sb.append( NEWLINE );
                    newLineStarted = true;
                    indent++;
                    for ( int x = 0; x < indent; x++ )
                    {
                        sb.append( INDENT_STRING );
                    }
                }
            }
            else if (c == '}' && !inQuotedString )
            {
                if(oneLineMode)
                {
                    // no new line in one-line mode
                    sb.append( c );
                    newLineStarted = false;
                    
                    // closed curly indicates end of one-line mode
                    oneLineMode = false;
                }
                else
                {
                    // decrement indent
                    indent--;
    
                    // start a new line, but avoid blank lines if there are multiple closed curlies
                    if( newLineStarted )
                    {
                        // delete one indent 
                        sb.delete( sb.length()-INDENT_STRING.length(), sb.length() );
                    }
                    else
                    {
                        sb.append( NEWLINE );
                        for ( int x = 0; x < indent; x++ )
                        {
                            sb.append( INDENT_STRING );
                        }
                    }
                    
                    // append the curly
                    sb.append( c );
                    
                    // start a new line 
                    sb.append( NEWLINE );
                    newLineStarted = true;
                    for ( int x = 0; x < indent; x++ )
                    {
                        sb.append( INDENT_STRING );
                    }
                }
            }
            else if (c == ',' && !inQuotedString )
            {
                // start new line on comma
                if(oneLineMode)
                {
                    sb.append( c );
                    newLineStarted = false;
                }
                else
                {
                    sb.append( c );
                    
                    sb.append( NEWLINE );
                    newLineStarted = true;
                    
                    for ( int x = 0; x < indent; x++ )
                    {
                        sb.append( INDENT_STRING );
                    }
                }
            }
            else if ( Character.isWhitespace( c ) )
            {
                char c1 = 'A';
                if(i+1 < contentLength )
                {
                    c1 = content.charAt( i+1 );
                }
                
                if( newLineStarted )
                {
                    // ignore space after starting a new line
                }
                else if (c == '\n' || c == '\r' )
                {
                    // ignore new lines
                }
                else if( Character.isWhitespace( c1 ) || c1 == '\n' || c1 == '\r' )
                {
                    // compress whitespaces
                }
                else
                {
                    sb.append( c );
                }
            }
            
            else
            {
                // default case: append the char
                sb.append( c );
                newLineStarted = false;
            }
        }

        return sb.toString();
    }


    /**
     * Checks if an expression could be appended in one line. That is 
     * if the expression starting at index i doesn't contain nested
     * expressions and only one comma.
     * 
     * @param i the starting index of the expression
     * @param content the content
     * 
     * @return true, if the expression could be appended in one line
     */
    private boolean checkInOneLine( int i, String content )
    {
        // flag to track if we are within a quoted string
        boolean inQuote = false;
        
        // counter for commas
        int commaCounter = 0;

        int contentLength = content.length();
        for ( int k=i+1; k<contentLength; k++)
        {
            char c = content.charAt( k );

            // track quotes
            if( c == '"')
            {
                inQuote = !inQuote;
            }
            
            // open curly indicates nested expression
            if( ( c == '{'  )  && !inQuote )
            {
                return false;
            }
            
            // closing curly indicates end of expression
            if ( c == '}' && !inQuote ) 
            {
                return true;
            }
            
            // allow only single comma in an expression in one line
            if (c == ',' && !inQuote )
            {
                commaCounter++;
                if(commaCounter > 1)
                {
                    return false;
                }
            }
        }
        
        return false;
    }
    
}
