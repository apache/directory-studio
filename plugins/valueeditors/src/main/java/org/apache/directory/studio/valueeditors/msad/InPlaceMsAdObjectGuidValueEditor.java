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

package org.apache.directory.studio.valueeditors.msad;


import org.apache.commons.codec.binary.Hex;
import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.valueeditors.HexValueEditor;


/**
 * Implementation of IValueEditor for Microsoft Active Directory attribute 'objectGUID'.
 * 
 * Currently only the getDisplayValue() method is implemented.
 * For modification the raw string must be edited.
 * 
 * There are two special characteristics compared to the default UUID editor:
 * <ul>
 * <li>The first 64 bit of the MS AD GUID are little-endian, so we must reorder the bytes.
 *     See <a href="http://msdn.microsoft.com/en-us/library/dd302644(PROT.10).aspx">
 *     http://msdn.microsoft.com/en-us/library/dd302644(PROT.10).aspx</a> or
 *     <a href="http://en.wikipedia.org/wiki/Globally_Unique_Identifier">
 *     http://en.wikipedia.org/wiki/Globally_Unique_Identifier</a>.
 * <li>The Curly Braced GUID String Syntax is used.
 *     See <a href="http://msdn.microsoft.com/en-us/library/cc230316(PROT.10).aspx">
 *     http://msdn.microsoft.com/en-us/library/cc230316(PROT.10).aspx</a>.
 * </ul>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class InPlaceMsAdObjectGuidValueEditor extends HexValueEditor
{
    public String getDisplayValue( IValue value )
    {
        if ( !showRawValues() )
        {
            Object rawValue = super.getRawValue( value );
            if ( rawValue instanceof byte[] )
            {
                byte[] bytes = ( byte[] ) rawValue;
                return convertToString( bytes );
            }
        }

        return super.getDisplayValue( value );
    }


    String convertToString( byte[] bytes )
    {
        if ( bytes == null || bytes.length != 16 )
        {
            return "Invalid GUID";
        }

        char[] hex = Hex.encodeHex( bytes );
        StringBuffer sb = new StringBuffer();
        sb.append( '{' );
        sb.append( hex, 6, 2 );
        sb.append( hex, 4, 2 );
        sb.append( hex, 2, 2 );
        sb.append( hex, 0, 2 );
        sb.append( '-' );
        sb.append( hex, 10, 2 );
        sb.append( hex, 8, 2 );
        sb.append( '-' );
        sb.append( hex, 14, 2 );
        sb.append( hex, 12, 2 );
        sb.append( '-' );
        sb.append( hex, 16, 4 );
        sb.append( '-' );
        sb.append( hex, 20, 12 );
        sb.append( '}' );
        return Strings.toLowerCase( sb.toString() );
    }

}
