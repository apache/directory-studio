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

package org.apache.directory.ldapstudio.browser.core.model;


import java.io.Serializable;

import org.apache.directory.ldapstudio.browser.core.BrowserCoreMessages;
import org.apache.directory.ldapstudio.browser.core.model.schema.Schema;


public class RDNPart implements Serializable
{

    private static final long serialVersionUID = 3250931604639940667L;

    private String name;

    private String value;


    public RDNPart()
    {
        this.name = ""; //$NON-NLS-1$
        this.value = ""; //$NON-NLS-1$
    }


    public RDNPart( String name, String value, boolean isValueEncoded ) throws NameException
    {
        if ( name == null || !name.matches( "([A-Za-z][A-Za-z0-9-]*)|([0-9]+(\\.[0-9]+)+)" ) ) { //$NON-NLS-1$
            throw new NameException( BrowserCoreMessages.model__empty_attribute );
        }
        if ( value == null || value.length() < 1 )
        {
            throw new NameException( BrowserCoreMessages.model__empty_value );
        }
        // this.name = name.trim();
        // this.value = value.trim();
        this.setName( name );
        if ( isValueEncoded )
        {
            this.setValue( value );
        }
        else
        {
            this.setUnencodedValue( value );
        }
    }


    public RDNPart( RDNPart rdnPart )
    {
        this.name = rdnPart.name;
        this.value = rdnPart.value;
    }


    public String getName()
    {
        return name;
    }


    public void setName( String name )
    {
        this.name = name;
    }


    // If the UTF-8 string does not have any of the following characters
    // which need escaping, then that string can be used as the string
    // representation of the value.
    //
    // o a space or "#" character occurring at the beginning of the
    // string
    //
    // o a space character occurring at the end of the string
    //
    // o one of the characters ",", "+", """, "\", "<", ">" or ";"
    //
    // Implementations MAY escape other characters.
    //
    // If a character to be escaped is one of the list shown above, then it
    // is prefixed by a backslash ('\' ASCII 92).
    //
    // Otherwise the character to be escaped is replaced by a backslash and
    // two hex digits, which form a single byte in the code of the
    // character.
    public String getUnencodedValue()
    {
        StringBuffer unencodedValue = new StringBuffer( this.value );

        for ( int i = 0; i < unencodedValue.length(); i++ )
        {
            if ( unencodedValue.charAt( i ) == '\\' )
            {

                if ( i == 0 && unencodedValue.length() > i + 1 && unencodedValue.charAt( i + 1 ) == ' ' )
                {
                    unencodedValue.deleteCharAt( i );
                }
                else if ( i == unencodedValue.length() - 2 && unencodedValue.length() > i + 1
                    && unencodedValue.charAt( i + 1 ) == ' ' )
                {
                    unencodedValue.deleteCharAt( i );
                }
                else if ( i == 0 && unencodedValue.length() > i + 1 && unencodedValue.charAt( i + 1 ) == '#' )
                {
                    unencodedValue.deleteCharAt( i );
                }
                else if ( unencodedValue.length() > i + 1
                    && ( unencodedValue.charAt( i + 1 ) == '+' || unencodedValue.charAt( i + 1 ) == ','
                        || unencodedValue.charAt( i + 1 ) == ';' || unencodedValue.charAt( i + 1 ) == '<'
                        || unencodedValue.charAt( i + 1 ) == '>' || unencodedValue.charAt( i + 1 ) == '"' || unencodedValue
                        .charAt( i + 1 ) == '\\' ) )
                {
                    unencodedValue.deleteCharAt( i );
                }

            }
        }

        return unencodedValue.toString();
    }


    public void setUnencodedValue( String unencodedValue )
    {

        unencodedValue = unencodedValue.replaceAll( "\\\\", "\\\\\\\\" ); //$NON-NLS-1$ //$NON-NLS-2$
        unencodedValue = unencodedValue.replaceAll( "\\+", "\\\\+" ); //$NON-NLS-1$ //$NON-NLS-2$
        unencodedValue = unencodedValue.replaceAll( ",", "\\\\," ); //$NON-NLS-1$ //$NON-NLS-2$
        unencodedValue = unencodedValue.replaceAll( "\"", "\\\\\"" ); //$NON-NLS-1$ //$NON-NLS-2$
        unencodedValue = unencodedValue.replaceAll( "<", "\\\\<" ); //$NON-NLS-1$ //$NON-NLS-2$
        unencodedValue = unencodedValue.replaceAll( ">", "\\\\>" ); //$NON-NLS-1$ //$NON-NLS-2$
        unencodedValue = unencodedValue.replaceAll( ";", "\\\\;" ); //$NON-NLS-1$ //$NON-NLS-2$

        if ( unencodedValue.startsWith( " " ) ) //$NON-NLS-1$
            unencodedValue = "\\" + unencodedValue; //$NON-NLS-1$
        if ( unencodedValue.startsWith( "#" ) ) //$NON-NLS-1$
            unencodedValue = "\\" + unencodedValue; //$NON-NLS-1$
        if ( unencodedValue.endsWith( " " ) ) //$NON-NLS-1$
            unencodedValue = unencodedValue.substring( 0, unencodedValue.length() - 1 ) + "\\ "; //$NON-NLS-1$

        this.value = unencodedValue;
    }


    public String getValue()
    {
        return value;
    }


    public void setValue( String value )
    {
        this.value = value;
    }


    public int hashCode()
    {
        return this.toString().hashCode();
    }


    public boolean equals( Object o )
    {
        if ( o instanceof RDNPart )
        {
            return this.toString().equals( ( ( RDNPart ) o ).toString() );
        }
        return false;
    }


    public String toString()
    {
        return getName() + "=" + getValue(); //$NON-NLS-1$
    }


    public String toOidString( Schema schema )
    {
        String oid = schema != null ? schema.getAttributeTypeDescription( getName() ).getNumericOID() : getName();
        return oid + "=" + getValue(); //$NON-NLS-1$
    }

}
