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


/**
 * A RDNPart represents a attribute type-value-pair, used in RDN. 
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class RDNPart implements Serializable
{
    /** The generated serialVersionUID */
    private static final long serialVersionUID = 3250931604639940667L;

    /** The attribute type */
    private String type;

    /** The value */
    private String value;


    /**
     * Creates a new instance of RDNPart with an empty type an value 
     *
     */
    public RDNPart()
    {
        this.type = ""; //$NON-NLS-1$
        this.value = ""; //$NON-NLS-1$
    }


    /**
     * Creates a new instance of RDNPart with the given type and value.
     *
     * @param type the attribute type
     * @param value the value
     * @param isValueEncoded true if the value is already encoded according RFC4514, Section 2.4
     * @throws NameException if the type or value are invalid
     */
    public RDNPart( String type, String value, boolean isValueEncoded ) throws NameException
    {
        if ( type == null || !type.matches( "([A-Za-z][A-Za-z0-9-]*)|([0-9]+(\\.[0-9]+)+)" ) ) { //$NON-NLS-1$
            throw new NameException( BrowserCoreMessages.model__empty_attribute );
        }
        if ( value == null || value.length() < 1 )
        {
            throw new NameException( BrowserCoreMessages.model__empty_value );
        }
        // this.type = type.trim();
        // this.value = value.trim();
        this.setType( type );
        if ( isValueEncoded )
        {
            this.setValue( value );
        }
        else
        {
            this.setUnencodedValue( value );
        }
    }


    /**
     * Creates a clone of the given RDNPart.
     *
     * @param rdnPart the RDNPart.
     */
    public RDNPart( RDNPart rdnPart )
    {
        this.type = rdnPart.type;
        this.value = rdnPart.value;
    }


    /**
     * Gets the type.
     * 
     * @return the type
     */
    public String getType()
    {
        return type;
    }


    /**
     * Sets the type.
     * 
     * @param type the type
     */
    public void setType( String type )
    {
        this.type = type;
    }


    /**
     * Gets the unencoded value. All escaped characters are unescaped
     * before returning the vaue.
     *
     * @return the unencoded value.
     */
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


    /**
     * Sets the unencoded value. The unencoded value will be encoded 
     * according RFC4514, Section 2.4.
     * 
     * @param unencodedValue the unencoded value
     */
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
        {
            unencodedValue = "\\" + unencodedValue; //$NON-NLS-1$
        }
        else if ( unencodedValue.startsWith( "#" ) ) //$NON-NLS-1$
        {
            unencodedValue = "\\" + unencodedValue; //$NON-NLS-1$
        }

        if ( unencodedValue.endsWith( " " ) ) //$NON-NLS-1$
        {
            unencodedValue = unencodedValue.substring( 0, unencodedValue.length() - 1 ) + "\\ "; //$NON-NLS-1$
        }

        this.value = unencodedValue;
    }


    /**
     * Gets the value. Note that the value is encoded
     * according RFC 4514, Section 2.4.
     * 
     * @return the value
     */
    public String getValue()
    {
        return value;
    }


    /**
     * Sets the value. Note that the value must be encoded
     * according RFC 4514, Section 2.4.
     * 
     * @param value the value
     */
    public void setValue( String value )
    {
        this.value = value;
    }


    /**
     * {@inheritDoc}
     */
    public int hashCode()
    {
        return this.toString().hashCode();
    }


    /**
     * {@inheritDoc}
     */
    public boolean equals( Object o )
    {
        if ( o instanceof RDNPart )
        {
            return this.toString().equals( ( ( RDNPart ) o ).toString() );
        }
        return false;
    }


    /**
     * Returns the string representation of this RDNPart, namely
     * &lt;type&gt;=&lt;value&gt;
     */
    public String toString()
    {
        return getType() + "=" + getValue(); //$NON-NLS-1$
    }


    /**
     * Returns the string representation of this RDNPart, but 
     * lowercased and with the numerid OID instead of the type.
     *
     * @param schema the schema
     * @return the lowercased and OID-fizied string representation of this RDNPart
     */
    public String toOidString( Schema schema )
    {
        String oid = schema != null ? schema.getAttributeTypeDescription( getType() ).getNumericOID() : getType();
        return oid.toLowerCase() + "=" + getValue().toLowerCase(); //$NON-NLS-1$
    }

}
