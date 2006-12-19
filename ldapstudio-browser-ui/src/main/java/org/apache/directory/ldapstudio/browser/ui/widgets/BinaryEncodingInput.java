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


import org.apache.directory.ldapstudio.browser.core.BrowserCoreConstants;


public class BinaryEncodingInput extends OptionsInput
{

    public BinaryEncodingInput( String initialRawValue, boolean asGroup )
    {
        super( "Binary Encoding", getDefaultDisplayValue(), getDefaultRawValue(), getOtherDisplayValues(),
            getOtherRawValues(), initialRawValue, asGroup, false );

    }


    private static String getDefaultDisplayValue()
    {
        return "Ignore";
    }


    private static String getDefaultRawValue()
    {
        return Integer.toString( BrowserCoreConstants.BINARYENCODING_IGNORE );
    }


    private static String[] getOtherDisplayValues()
    {
        return new String[]
            { "Ignore", "BASE-64", "HEX" };
    }


    private static String[] getOtherRawValues()
    {
        return new String[]
            { Integer.toString( BrowserCoreConstants.BINARYENCODING_IGNORE ),
                Integer.toString( BrowserCoreConstants.BINARYENCODING_BASE64 ),
                Integer.toString( BrowserCoreConstants.BINARYENCODING_HEX ) };
    }

}
