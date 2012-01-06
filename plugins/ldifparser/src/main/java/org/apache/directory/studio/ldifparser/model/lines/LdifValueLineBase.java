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


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.directory.studio.ldifparser.LdifFormatParameters;
import org.apache.directory.studio.ldifparser.LdifUtils;


public class LdifValueLineBase extends LdifNonEmptyLineBase
{

    private static final long serialVersionUID = -7030930374861554147L;

    private String rawValueType;

    private String rawValue;


    protected LdifValueLineBase()
    {
    }


    public LdifValueLineBase( int offset, String rawLineStart, String rawValueType, String rawValue, String rawNewLine )
    {
        super( offset, rawLineStart, rawNewLine );

        this.rawValueType = rawValueType;
        this.rawValue = rawValue;
    }


    public String getRawValueType()
    {
        return getNonNull( this.rawValueType );
    }


    public String getUnfoldedValueType()
    {
        return unfold( this.getRawValueType() );
    }


    public String getRawValue()
    {
        return getNonNull( this.rawValue );
    }


    public String getUnfoldedValue()
    {
        return unfold( this.getRawValue() );
    }


    public String toRawString()
    {
        return this.getRawLineStart() + this.getRawValueType() + this.getRawValue() + this.getRawNewLine();
    }


    public String toFormattedString( LdifFormatParameters formatParameters )
    {
        String raw = toRawString();
        String unfolded = unfold( raw );

        // Fix for DIRSTUDIO-285: We must take care that we only check
        // the first colon in the line. If there is another :: or :< 
        // in the value we must not use that as separator.
        int firstColonIndex = unfolded.indexOf( ":" ); //$NON-NLS-1$
        int firstDoubleColonIndex = unfolded.indexOf( "::" ); //$NON-NLS-1$
        int firstColonLessIndex = unfolded.indexOf( ":<" ); //$NON-NLS-1$

        if ( firstDoubleColonIndex > -1 && firstDoubleColonIndex == firstColonIndex )
        {
            unfolded = unfolded.replaceFirst( "::[ ]*", formatParameters.isSpaceAfterColon() ? ":: " : "::" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        else if ( firstColonLessIndex > -1 && firstColonLessIndex == firstColonIndex )
        {
            unfolded = unfolded.replaceFirst( ":<[ ]*", formatParameters.isSpaceAfterColon() ? ":< " : ":<" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        else if ( firstColonIndex > -1 )
        {
            unfolded = unfolded.replaceFirst( ":[ ]*", formatParameters.isSpaceAfterColon() ? ": " : ":" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        if ( rawNewLine != null )
        {
            int index = unfolded.lastIndexOf( rawNewLine );
            if ( index > -1 )
            {
                unfolded = unfolded.substring( 0, unfolded.length() - rawNewLine.length() );
                unfolded = unfolded + formatParameters.getLineSeparator();
            }
        }

        return fold( unfolded, 0, formatParameters );
    }


    public boolean isValid()
    {
        return super.isValid() && this.rawValueType != null && this.rawValue != null;
    }


    public String getInvalidString()
    {
        if ( this.rawValueType == null )
        {
            return "Missing value type ':', '::' or ':<'";
        }
        else if ( this.rawValue == null )
        {
            return "Missing value";
        }
        else
        {
            return super.getInvalidString();
        }
    }


    /**
     * 
     * @return the string representation of the value, non-base64, unfolded
     */
    public final String getValueAsString()
    {
        Object o = getValueAsObject();
        if ( o instanceof String )
        {
            return ( String ) o;
        }
        else if ( o instanceof byte[] )
        {
            return LdifUtils.utf8decode( ( byte[] ) o );
        }
        else
        {
            return ""; //$NON-NLS-1$
        }
    }


    /**
     * 
     * @return the binary representation of the real value, non-base64,
     *         unfolded
     */
    public final byte[] getValueAsBinary()
    {
        Object o = getValueAsObject();
        if ( o instanceof String )
        {
            return LdifUtils.utf8encode( ( String ) o );
        }
        else if ( o instanceof byte[] )
        {
            return ( byte[] ) o;
        }
        else
        {
            return new byte[0];
        }
    }


    /**
     * Returns the real data:
     * <ul>
     * <li>The unfolded String if value is a safe value.
     * </li>
     * <li>A byte array if value is base64 encoded.
     * </li>
     * <li>A byte array if value references an URL.
     * </li>
     * </ul>
     * 
     * @return the real value or null
     */
    public final Object getValueAsObject()
    {
        if ( this.isValueTypeSafe() )
        {
            return this.getUnfoldedValue();
        }
        else if ( this.isValueTypeBase64() )
        {
            return LdifUtils.base64decodeToByteArray( this.getUnfoldedValue() );
        }
        else if ( this.isValueTypeURL() )
        {
            try
            {
                File file = new File( this.getUnfoldedValue() );
                byte[] data = new byte[( int ) file.length()];
                FileInputStream fis = new FileInputStream( file );
                fis.read( data );
                return data;
            }
            catch ( IOException ioe )
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }


    public boolean isValueTypeURL()
    {
        return this.getUnfoldedValueType().startsWith( ":<" ); //$NON-NLS-1$
    }


    public boolean isValueTypeBase64()
    {
        return this.getUnfoldedValueType().startsWith( "::" ); //$NON-NLS-1$
    }


    public boolean isValueTypeSafe()
    {
        return this.getUnfoldedValueType().startsWith( ":" ) && !this.isValueTypeBase64() && !this.isValueTypeURL(); //$NON-NLS-1$
    }

}
