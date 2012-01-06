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

package org.apache.directory.studio.ldapbrowser.core.utils;


import org.apache.directory.studio.ldapbrowser.core.model.IValue;


/**
 * Utilies for filter handling.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdapFilterUtils
{

    /**
     * Creates a filter from the given value.
     *
     * @param value the value
     *
     * @return the filter
     */
    public static String getFilter( IValue value )
    {
        if ( value.isString() )
        {
            return "(" + value.getAttribute().getDescription() + "=" + getEncodedValue( value.getStringValue() ) + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        else
        {
            StringBuffer filter = new StringBuffer();
            filter.append( "(" ); //$NON-NLS-1$
            filter.append( value.getAttribute().getDescription() );
            filter.append( "=" ); //$NON-NLS-1$

            byte[] bytes = value.getBinaryValue();
            for ( int i = 0; i < bytes.length; i++ )
            {
                int b = ( int ) bytes[i];
                if ( b < 0 )
                {
                    b = 256 + b;
                }
                String s = Integer.toHexString( b );
                filter.append( "\\" ); //$NON-NLS-1$
                if ( s.length() == 1 )
                {
                    filter.append( "0" ); //$NON-NLS-1$
                }
                filter.append( s );
            }

            filter.append( ")" ); //$NON-NLS-1$
            return filter.toString();
        }
    }


    /**
     * Encodes the given value according RFC2254.
     *
     * <pre>
     * If a value should contain any of the following characters
     * Character       ASCII value
     * ---------------------------
     * *               0x2a
     * (               0x28
     * )               0x29
     * \               0x5c
     * NUL             0x00
     * the character must be encoded as the backslash '\' character (ASCII
     * 0x5c) followed by the two hexadecimal digits representing the ASCII
     * value of the encoded character. The case of the two hexadecimal
     * digits is not significant.
     * </pre>
     *
     * @param value the value
     *
     * @return the encoded value
     */
    public static String getEncodedValue( String value )
    {
        value = value.replaceAll( "\\\\", "\\\\5c" ); //$NON-NLS-1$ //$NON-NLS-2$
        value = value.replaceAll( "" + '\u0000', "\\\\00" ); //$NON-NLS-1$ //$NON-NLS-2$
        value = value.replaceAll( "\\*", "\\\\2a" ); //$NON-NLS-1$ //$NON-NLS-2$
        value = value.replaceAll( "\\(", "\\\\28" ); //$NON-NLS-1$ //$NON-NLS-2$
        value = value.replaceAll( "\\)", "\\\\29" ); //$NON-NLS-1$ //$NON-NLS-2$
        return value;
    }

}
