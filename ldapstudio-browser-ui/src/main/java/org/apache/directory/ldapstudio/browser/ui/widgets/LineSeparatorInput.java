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


import java.util.Iterator;
import java.util.Map;

import org.apache.directory.ldapstudio.browser.core.BrowserCoreConstants;

import org.eclipse.core.runtime.Platform;


public class LineSeparatorInput extends OptionsInput
{

    public LineSeparatorInput( String initialRawValue, boolean asGroup )
    {
        super( "Line Separator", getDefaultDisplayValue(), getDefaultRawValue(), getOtherDisplayValues(),
            getOtherRawValues(), initialRawValue, asGroup, false );

    }


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


    private static String getDefaultRawValue()
    {
        return BrowserCoreConstants.LINE_SEPARATOR;
    }


    private static String[] getOtherDisplayValues()
    {
        Map lsMap = Platform.knownPlatformLineSeparators();
        String[] displayValues = ( String[] ) lsMap.keySet().toArray( new String[lsMap.size()] );
        for ( int i = 0; i < displayValues.length; i++ )
        {
            displayValues[i] = displayValues[i]
                + " ("
                + ( ( ( String ) lsMap.get( displayValues[i] ) ).replaceAll( "\n", "\\\\n" ).replaceAll( "\r", "\\\\r" ) )
                + ")";
        }
        return displayValues;
    }


    private static String[] getOtherRawValues()
    {
        Map lsMap = Platform.knownPlatformLineSeparators();
        String[] displayValues = ( String[] ) lsMap.keySet().toArray( new String[lsMap.size()] );
        String[] rawValues = new String[displayValues.length];
        for ( int i = 0; i < rawValues.length; i++ )
        {
            rawValues[i] = ( String ) lsMap.get( displayValues[i] );
        }
        return rawValues;
    }

}
