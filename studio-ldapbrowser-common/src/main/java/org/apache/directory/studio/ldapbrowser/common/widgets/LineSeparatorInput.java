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


import java.util.Iterator;
import java.util.Map;

import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.eclipse.core.runtime.Platform;


/**
 * The LineSeparatorInput is an OptionInput with fixed options. 
 * It is used to select the line separator. The default
 * value is always the platform's default line separator.
 * The other options are the values return from 
 * {@link Platform#knownPlatformLineSeparators()}. 
 * No custom input is allowed.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class LineSeparatorInput extends OptionsInput
{

    /**
     * Creates a new instance of LineSeparatorInput.
     *
     * @param initialRawValue the initial raw value
     * @param asGroup the asGroup flag
     */
    public LineSeparatorInput( String initialRawValue, boolean asGroup )
    {
        super( "Line Separator", getDefaultDisplayValue(), getDefaultRawValue(), getOtherDisplayValues(),
            getOtherRawValues(), initialRawValue, asGroup, false );

    }


    /**
     * Gets the default display value.
     * 
     * @return the default display value
     */
    private static String getDefaultDisplayValue()
    {
        Map lsMap = Platform.knownPlatformLineSeparators();
        for ( Iterator iter = lsMap.keySet().iterator(); iter.hasNext(); )
        {
            String k = ( String ) iter.next();
            String v = ( String ) lsMap.get( k );
            if ( v.equals( getDefaultRawValue() ) )
            {
                k = k + " (" + ( v.replaceAll( "\n", "\\\\n" ).replaceAll( "\r", "\\\\r" ) ) + ")";
                return k;
            }
        }
        return getDefaultRawValue();
    }


    /**
     * Gets the default raw value, always the platform's default
     * line separator.
     * 
     * @return the default raw value
     */
    private static String getDefaultRawValue()
    {
        return BrowserCoreConstants.LINE_SEPARATOR;
    }


    /**
     * Gets the other display values That are all values
     * returned from {@link Platform#knownPlatformLineSeparators()}. 
     * 
     * @return the other display values
     */
    @SuppressWarnings("unchecked")
    private static String[] getOtherDisplayValues()
    {
        Map<String, String> lsMap = Platform.knownPlatformLineSeparators();
        String[] displayValues = lsMap.keySet().toArray( new String[lsMap.size()] );
        for ( int i = 0; i < displayValues.length; i++ )
        {
            displayValues[i] = displayValues[i]
                + " ("
                + ( ( ( String ) lsMap.get( displayValues[i] ) ).replaceAll( "\n", "\\\\n" ).replaceAll( "\r", "\\\\r" ) )
                + ")";
        }
        return displayValues;
    }


    /**
     * Gets the other raw values.
     * 
     * @return the other raw values
     */
    @SuppressWarnings("unchecked")
    private static String[] getOtherRawValues()
    {
        Map<String, String> lsMap = Platform.knownPlatformLineSeparators();
        String[] displayValues = lsMap.keySet().toArray( new String[lsMap.size()] );
        String[] rawValues = new String[displayValues.length];
        for ( int i = 0; i < rawValues.length; i++ )
        {
            rawValues[i] = ( String ) lsMap.get( displayValues[i] );
        }
        return rawValues;
    }

}
