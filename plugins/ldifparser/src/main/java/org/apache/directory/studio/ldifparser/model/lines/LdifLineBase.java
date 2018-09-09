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

package org.apache.directory.studio.ldifparser.model.lines;


import org.apache.directory.studio.ldifparser.LdifFormatParameters;
import org.apache.directory.studio.ldifparser.LdifUtils;
import org.apache.directory.studio.ldifparser.model.LdifPart;


/**
 * Base class for all lines in a LDIF file.
 * 
 * 
 */
public abstract class LdifLineBase implements LdifPart
{
    /** The position of this LdifPart in the Ldif */
    protected int offset;

    protected String rawNewLine;


    protected LdifLineBase()
    {
        super();
    }


    protected LdifLineBase( int offset, String rawNewLine )
    {
        this.offset = offset;
        this.rawNewLine = rawNewLine;
    }


    public final int getOffset()
    {
        return offset;
    }

    
    public final String getRawNewLine()
    {
        return getNonNull( rawNewLine );
    }


    public String getUnfoldedNewLine()
    {
        return unfold( getRawNewLine() );
    }


    public final void adjustOffset( int adjust )
    {
        offset += adjust;
    }


    public final int getLength()
    {
        return toRawString().length();
    }


    public boolean isValid()
    {
        return rawNewLine != null;
    }


    public String getInvalidString()
    {
        if ( rawNewLine == null )
        {
            return "Missing new line";
        }
        else
        {
            return null;
        }
    }


    public String toRawString()
    {
        return getRawNewLine();
    }


    public String toFormattedString( LdifFormatParameters formatParameters )
    {
        String raw = toRawString();
        String unfolded = unfold( raw );

        if ( rawNewLine != null )
        {
            int index = unfolded.lastIndexOf( rawNewLine );
            if ( index > -1 )
            {
                unfolded = unfolded.substring( 0, unfolded.length() - rawNewLine.length() );
                unfolded = unfolded + formatParameters.getLineSeparator();
            }
        }

        return unfolded;
    }


    public final String toString()
    {
        String text = toRawString();
        text = LdifUtils.convertNlRcToString( text ); //$NON-NLS-1$ //$NON-NLS-2$

        return getClass().getName() + " (" + getOffset() + "," + getLength() + "): '" + text + "'"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    }


    protected static String getNonNull( String s )
    {
        return s != null ? s : ""; //$NON-NLS-1$
    }


    protected static String unfold( String s )
    {
        char[] newString = s.toCharArray();
        int pos = 0;
        int length = newString.length;
        
        for ( int i = 0; i < length; i++ )
        {
            char c = newString[ i ];
            
            if ( c == '\n' )
            {
                if ( i + 1 < length )
                {
                    switch ( newString[ i + 1 ] )
                    {
                        case ' ' :
                            i++;
                            break;
                            
                        case '\r' :
                            if ( ( i + 2 < length ) && ( newString[ i + 2 ] == ' ' ) )
                            {
                                i += 2;
                            }
                            else
                            {
                                newString[pos++] = c;
                                newString[pos++] = '\r';
                                i++;
                            }
                            
                            break;
                            
                        default :
                            newString[pos++] = c;
                            break;
                    }
                }
                else
                {
                    newString[pos++] = c;
                }
            }
            else if ( c == '\r' )
            {
                if ( i + 1 < length )
                {
                    switch ( newString[ i + 1 ] )
                    {
                        case ' ' :
                            i++;
                            break;
                            
                        case '\n' :
                            if ( ( i + 2 < length ) && ( newString[ i + 2 ] == ' ' ) )
                            {
                                i += 2;
                            }
                            else
                            {
                                newString[pos++] = c;
                                newString[pos++] = '\n';
                                i++;
                            }
                            
                            break;
                            
                        default :
                            newString[pos++] = c;
                            break;
                    }
                }
                else
                {
                    newString[pos++] = c;
                }
            }
            else
            {
                newString[pos++] = c;
            }
        }
        
        return new String( newString, 0, pos );
    }


    protected static String fold( String value, int indent, LdifFormatParameters formatParameters )
    {
        StringBuffer formattedLdif = new StringBuffer();
        int offset = formatParameters.getLineWidth() - indent;
        int endIndex = 0 + offset;
        while ( endIndex + formatParameters.getLineSeparator().length() < value.length() )
        {
            formattedLdif.append( value.substring( endIndex - offset, endIndex ) );
            formattedLdif.append( formatParameters.getLineSeparator() );
            formattedLdif.append( ' ' );
            offset = formatParameters.getLineWidth() - 1;
            endIndex += offset;
        }
        String rest = value.substring( endIndex - offset, value.length() );
        formattedLdif.append( rest );

        // return
        return formattedLdif.toString();
    }

}
