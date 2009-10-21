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

package org.apache.directory.studio.ldapbrowser.common.widgets;


import java.nio.charset.Charset;

import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;


/**
 * The FileEncodingInput is an OptionInput with fixed options. 
 * It is used to select the file encoding. The default
 * value is always the platform's default encoding.
 * The other options are the values return from 
 * {@link Charset#availableCharsets()}. No custom input is allowed.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class FileEncodingInput extends OptionsInput
{

    /**
     * Creates a new instance of FileEncodingInput.
     *
     * @param initialRawValue the initial raw value
     * @param asGroup the asGroup flag
     */
    public FileEncodingInput( String initialRawValue, boolean asGroup )
    {
        super(
            Messages.getString( "FileEncodingInput.FileEncoding" ), getDefaultDisplayValue(), getDefaultRawValue(), getOtherDisplayValues(), //$NON-NLS-1$
            getOtherRawValues(), initialRawValue, asGroup, false );

    }


    /**
     * Gets the default display value.
     * 
     * @return the default display value
     */
    private static String getDefaultDisplayValue()
    {
        return getCharsetDisplayValue( getDefaultRawValue() );
    }


    /**
     * Gets the default raw value, always the platform's
     * default encoding.
     * 
     * @return the default raw value
     */
    private static String getDefaultRawValue()
    {
        return BrowserCoreConstants.DEFAULT_ENCODING;
    }


    /**
     * Gets the other display values.
     * 
     * @return the other display values
     */
    private static String[] getOtherDisplayValues()
    {
        String[] otherEncodingsRawValues = getOtherRawValues();
        String[] otherEncodingsDisplayValues = new String[otherEncodingsRawValues.length];
        for ( int i = 0; i < otherEncodingsDisplayValues.length; i++ )
        {
            String rawValue = otherEncodingsRawValues[i];
            otherEncodingsDisplayValues[i] = getCharsetDisplayValue( rawValue );
        }
        return otherEncodingsDisplayValues;
    }


    /**
     * Gets the other raw values. That are all values
     * returned from {@link Charset#availableCharsets()}. 
     * 
     * @return the other raw values
     */
    private static String[] getOtherRawValues()
    {
        String[] otherEncodingsRawValues = ( String[] ) Charset.availableCharsets().keySet().toArray( new String[0] );
        return otherEncodingsRawValues;
    }


    /**
     * Gets the charset display value.
     * 
     * @param charsetRawValue the charset raw value
     * 
     * @return the charset display value
     */
    private static String getCharsetDisplayValue( String charsetRawValue )
    {
        try
        {
            Charset charset = Charset.forName( charsetRawValue );
            return charset.displayName();
        }
        catch ( Exception e )
        {
            return charsetRawValue;
        }
    }

}
