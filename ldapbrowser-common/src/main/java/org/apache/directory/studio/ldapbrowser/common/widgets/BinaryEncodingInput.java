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


import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;


/**
 * The BinaryEncodingInput is an OptionInput with fixed options. 
 * It is used to select the encoding of binary attributes. The default
 * value is always {@link BrowserCoreConstants#BINARYENCODING_IGNORE}.
 * The other options are always {@link BrowserCoreConstants#BINARYENCODING_IGNORE},
 * {@link BrowserCoreConstants#BINARYENCODING_BASE64} and
 * {@link BrowserCoreConstants#BINARYENCODING_HEX}. No custom input is allowed.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BinaryEncodingInput extends OptionsInput
{

    /**
     * Creates a new instance of BinaryEncodingInput.
     *
     * @param initialRawValue the initial raw value
     * @param asGroup the asGroup flag
     */
    public BinaryEncodingInput( String initialRawValue, boolean asGroup )
    {
        super( Messages.getString("BinaryEncodingInput.BinaryEncoding"), getDefaultDisplayValue(), getDefaultRawValue(), getOtherDisplayValues(), //$NON-NLS-1$
            getOtherRawValues(), initialRawValue, asGroup, false );

    }


    /**
     * Gets the default display value, always "Ignore".
     * 
     * @return the default display value
     */
    private static String getDefaultDisplayValue()
    {
        return Messages.getString("BinaryEncodingInput.Ignore"); //$NON-NLS-1$
    }


    /**
     * Gets the default raw value, always 
     * {@link BrowserCoreConstants.BINARYENCODING_IGNORE}.
     * 
     * @return the default raw value
     */
    private static String getDefaultRawValue()
    {
        return Integer.toString( BrowserCoreConstants.BINARYENCODING_IGNORE );
    }


    /**
     * Gets the other display values.
     * 
     * @return the other display values
     */
    private static String[] getOtherDisplayValues()
    {
        return new String[]
            { Messages.getString("BinaryEncodingInput.Ignore"), "BASE-64", "HEX" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }


    /**
     * Gets the other raw values.
     * 
     * @return the other raw values
     */
    private static String[] getOtherRawValues()
    {
        return new String[]
            { Integer.toString( BrowserCoreConstants.BINARYENCODING_IGNORE ),
                Integer.toString( BrowserCoreConstants.BINARYENCODING_BASE64 ),
                Integer.toString( BrowserCoreConstants.BINARYENCODING_HEX ) };
    }

}
