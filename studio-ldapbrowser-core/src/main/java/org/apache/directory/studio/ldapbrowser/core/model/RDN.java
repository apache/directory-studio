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

package org.apache.directory.studio.ldapbrowser.core.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;


/**
 * A RDN represents a LDAP relative distinguished name.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class RDN implements Serializable
{

    /** The generated serialVersionUID. */
    private static final long serialVersionUID = -4165959915339033047L;

    /** The attribute type-value pairs */
    private RDNPart[] parts;


    /**
     * Creates an empty RDN.
     *
     */
    public RDN()
    {
        this.parts = new RDNPart[0];
    }


    /**
     * Creates a new instance of RDN. The given string is parsed.
     *
     * @param rdn the rdn
     * @throws NameException if parsing fails.
     */
    public RDN( String rdn ) throws NameException
    {
        if ( rdn == null )
        {
            throw new IllegalArgumentException( BrowserCoreMessages.model__empty_rdn );
        }

        // this.parseMultiRdn(rdn.trim());
        this.parseMultiRdn( rdn );
    }


    /**
     * Creates a clone of the given RDN.
     *
     * @param rdn the RDN
     */
    public RDN( RDN rdn )
    {
        if ( rdn == null )
        {
            throw new IllegalArgumentException( BrowserCoreMessages.model__empty_rdn );
        }

        this.parts = new RDNPart[rdn.getParts().length];
        for ( int i = 0; i < this.parts.length; i++ )
        {
            this.parts[i] = new RDNPart( rdn.getParts()[i] );
        }
    }


    /**
     * Create a single-valued RDN with the given type and value.
     * 
     * @param type the attribute type
     * @param value the value
     * @param isValueEncoded true if the value is already encoded according RFC4514, Section 2.4
     * @throws NameException if the type or value are invalid
     */
    public RDN( String type, String value, boolean isValueEncoded ) throws NameException
    {
        if ( type == null )
        {
            throw new IllegalArgumentException( BrowserCoreMessages.model__empty_attribute );
        }
        if ( value == null )
        {
            throw new IllegalArgumentException( BrowserCoreMessages.model__empty_value );
        }

        this.parts = new RDNPart[1];
        this.parts[0] = new RDNPart( type, value, isValueEncoded );
    }


    /**
     * Creates a multi-values RDN with the given types and values.
     *
     * @param types the attribute types
     * @param values the attribute values
     * @param areValuesEncoded true if the values is already encoded according RFC4514, Section 2.4
     * @throws NameException if the types or values are invalid
     */
    public RDN( String[] types, String[] values, boolean areValuesEncoded ) throws NameException
    {

        if ( types == null || types.length < 1 )
        {
            throw new IllegalArgumentException( BrowserCoreMessages.model__empty_attribute );
        }
        if ( values == null || values.length < 1 )
        {
            throw new IllegalArgumentException( BrowserCoreMessages.model__empty_value );
        }
        if ( types.length != values.length )
        {
            throw new IllegalArgumentException( "Size of types and values is not equal" ); //$NON-NLS-1$
        }

        this.parts = new RDNPart[types.length];
        for ( int i = 0; i < this.parts.length; i++ )
        {
            this.parts[i] = new RDNPart( types[i], values[i], areValuesEncoded );
        }
    }


    /**
     * Checks if the RDN is multi-valued.
     * 
     * @return true, if the RDN is multi-valued
     */
    public boolean isMultivalued()
    {
        return this.parts.length > 1;
    }


    /**
     * Gets the first attribute type.
     * 
     * @return the first attribute type
     */
    public String getType()
    {
        return this.parts.length > 0 ? this.parts[0].getType() : ""; //$NON-NLS-1$
    }


    /**
     * Gets the first attribute value.
     * 
     * @return the first attribute value
     */
    public String getValue()
    {
        return this.parts.length > 0 ? this.parts[0].getValue() : ""; //$NON-NLS-1$
    }


    /**
     * Gets the parts.
     * 
     * @return the parts
     */
    public RDNPart[] getParts()
    {
        return this.parts;
    }


    /**
     * Sets the parts.
     * 
     * @param parts the parts
     */
    public void setParts( RDNPart[] parts )
    {
        this.parts = parts;
    }


    /**
     * Gets the attribute types.
     * 
     * @return the attribute types
     */
    public String[] getTypes()
    {
        if ( !isMultivalued() )
        {
            return new String[]
                { getType() };
        }
        else
        {
            Set<String> typeSet = new LinkedHashSet<String>();
            for ( int i = 0; i < this.parts.length; i++ )
            {
                RDNPart entry = this.parts[i];
                typeSet.add( entry.getType() );
            }
            return typeSet.toArray( new String[typeSet.size()] );
        }
    }


    /**
     * Gets the values.
     * 
     * @return the values
     */
    public String[] getValues()
    {
        if ( !isMultivalued() )
        {
            return new String[]
                { getValue() };
        }
        else
        {
            Set<String> valueSet = new LinkedHashSet<String>();
            for ( int i = 0; i < this.parts.length; i++ )
            {
                RDNPart entry = this.parts[i];
                valueSet.add( entry.getValue() );
            }
            return valueSet.toArray( new String[valueSet.size()] );
        }
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
        if ( o instanceof RDN )
        {
            return this.toString().equals( ( ( RDN ) o ).toString() );
        }
        return false;
    }


    /**
     * Returns the string representation of this RDN, 
     * for example &lt;type&gt;=&lt;value&gt;
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        if ( isMultivalued() )
        {
            for ( int i = 0; i < this.parts.length; i++ )
            {
                RDNPart part = this.parts[i];
                sb.append( part.toString() );

                if ( i + 1 < this.parts.length )
                {
                    sb.append( "+" ); //$NON-NLS-1$
                }
            }
        }
        else if ( this.parts.length > 0 )
        {
            RDNPart part = this.parts[0];
            sb.append( part.toString() );
        }

        return sb.toString();
    }


    /**
     * Returns the string representation of this RDN, but 
     * lowercased and with the numerid OIDs instead of the types.
     *
     * @param schema the schema
     * @return the lowercased and OID-fizied string representation of this RDN
     */
    public String toOidString( Schema schema )
    {
        StringBuffer sb = new StringBuffer();

        if ( isMultivalued() )
        {
            for ( int i = 0; i < this.parts.length; i++ )
            {
                RDNPart part = this.parts[i];
                sb.append( part.toOidString( schema ) );

                if ( i + 1 < this.parts.length )
                {
                    sb.append( "+" ); //$NON-NLS-1$
                }
            }
        }
        else if ( this.parts.length > 0 )
        {
            RDNPart part = this.parts[0];
            sb.append( part.toOidString( schema ) );
        }

        return sb.toString();
    }


    /**
     * Parses the RDN.
     *
     * @param multirdn the rdn
     * @throws NameException if parsing fails
     */
    private void parseMultiRdn( String multirdn ) throws NameException
    {
        List<RDNPart> partList = new ArrayList<RDNPart>( 1 );

        boolean backslash = false;
        int start = 0;
        for ( int i = 0; i < multirdn.length(); i++ )
        {
            if ( multirdn.charAt( i ) == '\\' && !backslash )
            {
                backslash = true;
            }
            else
            {
                String rdn = null;
                if ( multirdn.charAt( i ) == '+' && !backslash )
                {
                    rdn = multirdn.substring( start, i );
                }
                else if ( i == multirdn.length() - 1 )
                {
                    rdn = multirdn.substring( start );
                }
                if ( rdn != null )
                {
                    int index = rdn.indexOf( '=' );
                    if ( index < 1 )
                    {
                        throw new NameException( BrowserCoreMessages.model__invalid_rdn );
                    }
                    String type = rdn.substring( 0, index );
                    String value = rdn.substring( index + 1, rdn.length() );
                    partList.add( new RDNPart( type, value, true ) );
                    start = i + 1;
                }
                backslash = false;
            }
        }

        if ( partList.isEmpty() )
        {

        }

        this.parts = partList.toArray( new RDNPart[partList.size()] );
    }

}
