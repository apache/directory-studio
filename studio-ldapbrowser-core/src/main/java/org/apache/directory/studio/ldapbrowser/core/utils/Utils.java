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


import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


public class Utils
{

    public static String[] stringToArray( String s )
    {
        if ( s == null )
        {
            return null;
        }
        else
        {
            List attributeList = new ArrayList();

            StringBuffer temp = new StringBuffer();
            for ( int i = 0; i < s.length(); i++ )
            {
                char c = s.charAt( i );

                if ( ( c >= 'a' && c <= 'z' ) || ( c >= 'A' && c <= 'Z' ) || ( c >= '0' && c <= '9' ) || c == '-'
                    || c == '.' || c == ';' )
                {
                    temp.append( c );
                }
                else
                {
                    if ( temp.length() > 0 )
                    {
                        attributeList.add( temp.toString() );
                        temp = new StringBuffer();
                    }
                }
            }
            if ( temp.length() > 0 )
            {
                attributeList.add( temp.toString() );
            }

            return ( String[] ) attributeList.toArray( new String[attributeList.size()] );
        }

        // else {
        // s = s.trim();
        // s = s.replaceAll(" ", "");
        // s = s.replaceAll(",,", ",");
        // String[] a;
        // if(s.length() > 0) {
        // a = s.split(",", 0);
        // }
        // else {
        // a = new String[0];
        // }
        // return a;
        // }
    }


    public static String arrayToString( String[] array )
    {
        if ( array == null || array.length == 0 )
        {
            return "";
        }
        else
        {
            StringBuffer sb = new StringBuffer( array[0] );
            for ( int i = 1; i < array.length; i++ )
            {
                sb.append( ", " );
                sb.append( array[i] );
            }
            return sb.toString();
        }
    }


    public static boolean equals( byte[] data1, byte[] data2 )
    {
        if ( data1 == data2 )
            return true;
        if ( data1 == null || data2 == null )
            return false;
        if ( data1.length != data2.length )
            return false;
        for ( int i = 0; i < data1.length; i++ )
        {
            if ( data1[i] != data2[i] )
                return false;
        }
        return true;
    }


    public static String getShortenedString( String value, int length )
    {

        if ( value == null )
            return "";

        if ( value.length() > length )
        {
            value = value.substring( 0, length ) + "...";
        }

        return value;
    }


    public static String serialize( Object o )
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLEncoder encoder = new XMLEncoder( baos );
        encoder.writeObject( o );
        encoder.close();
        String s = LdifUtils.utf8decode( baos.toByteArray() );
        return s;
    }


    public static Object deserialize( String s )
    {
        ByteArrayInputStream bais = new ByteArrayInputStream( LdifUtils.utf8encode( s ) );
        XMLDecoder decoder = new XMLDecoder( bais );
        Object o = decoder.readObject();
        decoder.close();
        return o;
    }


    public static String getNonNullString( Object o )
    {
        return o == null ? "-" : o.toString();
    }


    public static String formatBytes( long bytes )
    {
        String size = "";
        if ( bytes > 1024 * 1024 )
            size += ( bytes / 1024 / 1024 ) + " MB (" + bytes + " Bytes)";
        else if ( bytes > 1024 )
            size += ( bytes / 1024 ) + " KB (" + bytes + " Bytes)";
        else if ( bytes > 1 )
            size += bytes + " Bytes";
        else
            size += bytes + " Byte";
        return size;
    }


    public static boolean containsIgnoreCase( Collection c, String s )
    {
        if ( c == null || s == null )
        {
            return false;
        }

        Iterator it = c.iterator();
        while ( it.hasNext() )
        {
            Object o = it.next();
            if ( o instanceof String && ( ( String ) o ).equalsIgnoreCase( s ) )
            {
                return true;
            }
        }

        return false;
    }


    public static String shorten( String label, int maxLength )
    {
        if ( label == null )
        {
            return null;
        }
        if ( maxLength < 3 )
        {
            return "...";
        }
        if ( label.length() > maxLength )
        {
            label = label.substring( 0, maxLength / 2 ) + "..."
                + label.substring( label.length() - maxLength / 2, label.length() );

        }
        StringBuffer sb = new StringBuffer( maxLength + 3 );
        for ( int i = 0; i < label.length(); i++ )
        {
            char c = label.charAt( i );
            if ( c > 31 && c < 127 )
                sb.append( c );
            else
                sb.append( '.' );
        }
        return sb.toString();
    }

}
