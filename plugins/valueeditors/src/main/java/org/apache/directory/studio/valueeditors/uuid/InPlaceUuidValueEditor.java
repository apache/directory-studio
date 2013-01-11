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

package org.apache.directory.studio.valueeditors.uuid;


import org.apache.commons.codec.binary.Hex;
import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.valueeditors.HexValueEditor;


/**
 * Implementation of IValueEditor for syntax 1.3.6.1.1.16.1 (UUID), e.g. used by entryUUID.
 * 
 * Currently only the getDisplayValue() method is implemented.
 * For modification the raw string must be edited.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class InPlaceUuidValueEditor extends HexValueEditor
{
    private static final String UUID_REGEX = "^[A-Fa-f0-9]{8}-[A-Fa-f0-9]{4}-[A-Fa-f0-9]{4}-[A-Fa-f0-9]{4}-[A-Fa-f0-9]{12}$"; //$NON-NLS-1$


    /**
     * {@inheritDoc}
     */
    public String getDisplayValue( IValue value )
    {
        if ( !showRawValues() )
        {
            Object rawValue = super.getRawValue( value );
            if ( rawValue instanceof byte[] )
            {
                byte[] bytes = ( byte[] ) rawValue;
                String string = Strings.utf8ToString( bytes );
                if ( string.matches( UUID_REGEX ) || Strings.isEmpty( string ) )
                {
                    return string;
                }
                else
                {
                    return convertToString( bytes );
                }
            }
        }

        return super.getDisplayValue( value );
    }


    String convertToString( byte[] bytes )
    {
        if ( bytes == null || bytes.length != 16 )
        {
            return Messages.getString( "InPlaceUuidValueEditor.InvalidUuid" );
        }

        char[] hex = Hex.encodeHex( bytes );
        StringBuffer sb = new StringBuffer();
        sb.append( hex, 0, 8 );
        sb.append( '-' );
        sb.append( hex, 8, 4 );
        sb.append( '-' );
        sb.append( hex, 12, 4 );
        sb.append( '-' );
        sb.append( hex, 16, 4 );
        sb.append( '-' );
        sb.append( hex, 20, 12 );
        return Strings.toLowerCase( sb.toString() );
    }
}
