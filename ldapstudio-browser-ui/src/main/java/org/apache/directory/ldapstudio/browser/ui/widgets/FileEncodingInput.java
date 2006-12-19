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

package org.apache.directory.ldapstudio.browser.ui.widgets;


import java.nio.charset.Charset;

import org.apache.directory.ldapstudio.browser.core.BrowserCoreConstants;


public class FileEncodingInput extends OptionsInput
{

    public FileEncodingInput( String initialRawValue, boolean asGroup )
    {
        super( "File Encoding", getDefaultDisplayValue(), getDefaultRawValue(), getOtherDisplayValues(),
            getOtherRawValues(), initialRawValue, asGroup, false );

    }


    private static String getDefaultDisplayValue()
    {
        return getCharsetDisplayValue( getDefaultRawValue() );
    }


    private static String getDefaultRawValue()
    {
        return BrowserCoreConstants.DEFAULT_ENCODING;
    }


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


    private static String[] getOtherRawValues()
    {
        String[] otherEncodingsRawValues = ( String[] ) Charset.availableCharsets().keySet().toArray( new String[0] );
        return otherEncodingsRawValues;
    }


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
