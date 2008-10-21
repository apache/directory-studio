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


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;


/**
 * Utilities for LDAP related encoding and decoding.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class LdifUtils
{


    /**
     * Encodes the given string to UTF-8
     *
     * @param s the string to encode
     *
     * @return the byte[] the encoded value
     */
    public static byte[] utf8encode( String s )
    {
        try
        {
            return s.getBytes( "UTF-8" );
        }
        catch ( UnsupportedEncodingException e )
        {
            return s.getBytes();
        }
    }


    /**
     * Encodes the given string into URL format.
     *
     * @param s the string to encode
     *
     * @return the string the URL encoded string
     */
    public static String urlEncode( String s )
    {
        try
        {
            return URLEncoder.encode( s, "UTF-8" );
        }
        catch ( UnsupportedEncodingException e )
        {
            return s;
        }
    }


    /**
     * Encodes the given byte array using BASE-64 encoding.
     *
     * @param b the b the byte array to encode
     *
     * @return the BASE-64 encoded string
     */
    public static String base64encode( byte[] b )
    {
        return utf8decode( Base64.encodeBase64( b ) );
    }


    /**
     * Encodes the given byte array to a sequence of
     * its hex values.
     *
     * @param data the data to encode
     * @return the HEX encoded string
     */
    public static String hexEncode( byte[] data )
    {
        if ( data == null )
            return null;

        char[] c = Hex.encodeHex( data );
        String s = new String( c );
        return s;

        // StringBuffer sb = new StringBuffer(data.length*3);
        // for(int i=0; i<data.length; i++) {
        // int b = (int)data[i];
        // if(b<0) b=256+b;
        // String s = Integer.toHexString(b)/*.toUpperCase()*/;
        // if(s.length() ==1) s = "0" + s; //$NON-NLS-1$
        // sb.append(s);
        // if(i+1 < data.length)
        // sb.append(' ');
        // }
        // return(sb.toString());
    }


    /**
     * Decodes the given UTF-8 byte array to an string.
     *
     * @param b the b the byte array to decode
     *
     * @return the decoded string
     */
    public static String utf8decode( byte[] b )
    {
        try
        {
            return new String( b, "UTF-8" );
        }
        catch ( UnsupportedEncodingException e )
        {
            return new String( b );
        }
    }


    /**
     * Decodes the given BASE-64 encoded string to its
     * bytes presentation.
     *
     * @param s the s the BASE-64 encoded string
     *
     * @return the byte[] the decoded byte array
     */
    public static byte[] base64decodeToByteArray( String s )
    {
        return Base64.decodeBase64( utf8encode( s ) );
    }


    /**
     * Checks if the given distinguished name must be encoded.
     *
     * @param dn the dn to check
     *
     * @return true, if must encode DN
     */
    public static boolean mustEncodeDN( String dn )
    {
        return mustEncode( dn );
    }


    /**
     * Checks if the given string must be encoded to be
     * used in an LDIF.
     *
     * @param value the value to check
     *
     * @return true, if must encode
     */
    public static boolean mustEncode( String value )
    {
        if ( value == null || value.length() < 1 )
        {
            return false;
        }

        if ( value.startsWith( " " ) || value.startsWith( ":" ) || value.startsWith( "<" ) )
        {
            return true;
        }
        if ( value.endsWith( " " ) )
        {
            return true;
        }

        for ( int i = 0; i < value.length(); i++ )
        {
            if ( value.charAt( i ) == '\r' || value.charAt( i ) == '\n' || value.charAt( i ) == '\u0000'
                || value.charAt( i ) > '\u007F' )
            {
                return true;
            }
        }

        return false;
    }


    /**
     * Gets the string value from the given {@link IValue}. If the given
     * {@link IValue} is binary is is encoded according to the regquested
     * encoding type.
     *
     * @param value the value
     * @param binaryEncoding the binary encoding type
     *
     * @return the string value
     */
    public static String getStringValue( IValue value, int binaryEncoding )
    {
        String s = value.getStringValue();
        if ( value.isBinary() && LdifUtils.mustEncode( s ) )
        {
            byte[] binary = value.getBinaryValue();
            if ( binaryEncoding == BrowserCoreConstants.BINARYENCODING_BASE64 )
            {
                s = LdifUtils.base64encode( binary );
            }
            else if ( binaryEncoding == BrowserCoreConstants.BINARYENCODING_HEX )
            {
                s = LdifUtils.hexEncode( binary );
            }
            else
            {
                s = BrowserCoreConstants.BINARY;
            }
        }
        return s;
    }

}
