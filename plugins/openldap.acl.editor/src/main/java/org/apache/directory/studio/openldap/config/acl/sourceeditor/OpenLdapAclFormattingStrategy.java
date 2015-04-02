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
package org.apache.directory.studio.openldap.config.acl.sourceeditor;


import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.eclipse.jface.text.formatter.IFormattingStrategy;
import org.eclipse.jface.text.source.ISourceViewer;


/**
 * This class implements the formatting strategy for the OpenLDAP ACL Editor.
 * <ul>
 * <li>New line before the "by" keyword
 * </ul>
 */
public class OpenLdapAclFormattingStrategy implements IFormattingStrategy
{
    /** The Constant NEWLINE. */
    public static final String NEWLINE = BrowserCoreConstants.LINE_SEPARATOR;

    /** The source viewer. */
    private ISourceViewer sourceViewer;


    /**
     * Creates a new instance of OpenLdapAclFormattingStrategy.
     *
     * @param sourceViewer the source viewer
     */
    public OpenLdapAclFormattingStrategy( ISourceViewer sourceViewer )
    {
        this.sourceViewer = sourceViewer;
    }


    /**
     * {@inheritDoc}
     */
    public String format( String content, boolean isLineStart, String indentation, int[] positions )
    {
        String oldContent = sourceViewer.getDocument().get();
        String newContent = internFormat( oldContent );
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

        // Flag to track if we are within a quoted string
        boolean inQuotedString = false;

        // Flag to track if a new line was started
        boolean newLineStarted = true;

        // Char index
        int i = 0;

        int contentLength = content.length();
        while ( i < contentLength )
        {
            char currentChar = content.charAt( i );

            // Tracking quotes
            if ( currentChar == '"' )
            {
                inQuotedString = !inQuotedString;
            }
            else if ( newLineStarted && ( ( currentChar == '\n' ) || ( currentChar == '\r' ) ) )
            {
                // Compress multiple newlines
                i++;
                continue;
            }

            // Checking if we're not in a quoted text
            if ( !inQuotedString )
            {
                // Getting the next char (if available)
                char nextChar = 0;
                if ( ( i + 1 ) < contentLength )
                {
                    nextChar = content.charAt( i + 1 );
                }

                // Checking if we have the "by" keyword
                if ( ( !newLineStarted ) && ( currentChar == 'b' ) && ( nextChar == 'y' ) )
                {
                    sb.append( NEWLINE );
                    newLineStarted = true;
                }
                else
                {
                    // Tracking new line
                    newLineStarted = ( ( currentChar == '\n' ) || ( currentChar == '\r' ) );
                }
            }

            sb.append( currentChar );
            i++;
        }

        return sb.toString();
    }
}
