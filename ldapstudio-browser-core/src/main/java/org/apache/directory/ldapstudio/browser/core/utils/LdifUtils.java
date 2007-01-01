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

package org.apache.directory.ldapstudio.browser.core.utils;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.directory.ldapstudio.browser.core.BrowserCoreConstants;
import org.apache.directory.ldapstudio.browser.core.model.IValue;


public class LdifUtils
{

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


    public static String base64encode( byte[] b )
    {
        return utf8decode( Base64.encodeBase64( b ) );
    }


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


    public static byte[] base64decodeToByteArray( String s )
    {
        return Base64.decodeBase64( utf8encode( s ) );
    }


    public static boolean mustEncodeDN( String dn )
    {
        return mustEncode( dn );
    }


    public static boolean mustEncode( byte[] b )
    {

        if ( b == null || b.length < 1 )
        {
            return false;
        }

        if ( b[0] == ' ' || b[0] == ':' || b[0] == '<' )
        {
            return true;
        }
        if ( b[b.length - 1] == ' ' )
        {
            return true;
        }

        for ( int i = 0; i < b.length; i++ )
        {
            if ( b[i] == '\n' || b[i] == '\r' || b[i] == '\u0000' || ( ( b[i] & 0x7F ) != 0x7F ) )
            {
                return true;
            }
        }

        return false;
    }


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


    public static String getStringValue( IValue value, int binaryEncoding )
    {
        String s;
        byte[] binary = value.getBinaryValue();
        if ( value.isBinary() && LdifUtils.mustEncode( binary ) )
        {
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
        else
        {
            s = value.getStringValue();

        }
        return s;
    }

}
