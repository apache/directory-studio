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
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.directory.ldapstudio.browser.core.BrowserCoreMessages;
import org.apache.directory.ldapstudio.browser.core.model.schema.Schema;


public class RDN implements Serializable
{

    private static final long serialVersionUID = -4165959915339033047L;

    private RDNPart[] parts;


    public RDN()
    {
        this.parts = new RDNPart[0];
    }


    public RDN( String rdn ) throws NameException
    {

        if ( rdn == null )
        {
            throw new IllegalArgumentException( BrowserCoreMessages.model__empty_rdn );
        }

        // this.parseMultiRdn(rdn.trim());
        this.parseMultiRdn( rdn );
    }


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
     * Create a single-valued RDN with the given name and value
     * 
     * @param name
     * @param value
     */
    public RDN( String name, String value, boolean isValueEncoded ) throws NameException
    {

        if ( name == null )
        {
            throw new IllegalArgumentException( BrowserCoreMessages.model__empty_attribute );
        }
        if ( value == null )
        {
            throw new IllegalArgumentException( BrowserCoreMessages.model__empty_value );
        }

        this.parts = new RDNPart[1];
        this.parts[0] = new RDNPart( name, value, isValueEncoded );
    }


    public RDN( String[] names, String[] values, boolean areValuesEncoded ) throws NameException
    {

        if ( names == null || names.length < 1 )
        {
            throw new IllegalArgumentException( BrowserCoreMessages.model__empty_attribute );
        }
        if ( values == null || values.length < 1 )
        {
            throw new IllegalArgumentException( BrowserCoreMessages.model__empty_value );
        }
        if ( names.length != values.length )
        {
            throw new IllegalArgumentException( "Size of names and values is not equal" ); //$NON-NLS-1$
        }

        this.parts = new RDNPart[names.length];
        for ( int i = 0; i < this.parts.length; i++ )
        {
            this.parts[i] = new RDNPart( names[i], values[i], areValuesEncoded );
        }
    }


    public boolean isMultivalued()
    {
        return this.parts.length > 1;
    }


    public String getName()
    {
        return this.parts.length > 0 ? this.parts[0].getName() : ""; //$NON-NLS-1$
    }


    public String getValue()
    {
        return this.parts.length > 0 ? this.parts[0].getValue() : ""; //$NON-NLS-1$
    }


    public RDNPart[] getParts()
    {
        return this.parts;
    }


    public void setParts( RDNPart[] parts )
    {
        this.parts = parts;
    }


    public String[] getNames()
    {
        if ( !isMultivalued() )
        {
            return new String[]
                { getName() };
        }
        else
        {
            Set nameSet = new LinkedHashSet();
            for ( int i = 0; i < this.parts.length; i++ )
            {
                RDNPart entry = this.parts[i];
                nameSet.add( entry.getName() );
            }
            return ( String[] ) nameSet.toArray( new String[nameSet.size()] );
        }
    }


    public String[] getValues()
    {
        if ( !isMultivalued() )
        {
            return new String[]
                { getValue() };
        }
        else
        {
            Set valueSet = new LinkedHashSet();
            for ( int i = 0; i < this.parts.length; i++ )
            {
                RDNPart entry = this.parts[i];
                valueSet.add( entry.getValue() );
            }
            return ( String[] ) valueSet.toArray( new String[valueSet.size()] );
        }
    }


    public int hashCode()
    {
        return this.toString().hashCode();
    }


    public boolean equals( Object o )
    {
        if ( o instanceof RDN )
        {
            return this.toString().equals( ( ( RDN ) o ).toString() );
        }
        return false;
    }


    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        if ( isMultivalued() )
        {
            for ( int i = 0; i < this.parts.length; i++ )
            {
                RDNPart part = this.parts[i];
                sb.append( part.toString() );
                // sb.append(part.getName());
                // sb.append("="); //$NON-NLS-1$
                // sb.append(part.getValue());

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
            // sb.append(part.getName());
            // sb.append("="); //$NON-NLS-1$
            // sb.append(part.getValue());
        }

        return sb.toString();
    }


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


    private void parseMultiRdn( String multirdn ) throws NameException
    {

        List partList = new ArrayList( 1 );

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
                    String name = rdn.substring( 0, index );
                    String value = rdn.substring( index + 1, rdn.length() );
                    // partList.add(new RDNPart(name.trim(), value.trim()));
                    partList.add( new RDNPart( name, value, true ) );
                    start = i + 1;
                }
                backslash = false;
            }
        }

        if ( partList.isEmpty() )
        {

        }

        this.parts = ( RDNPart[] ) partList.toArray( new RDNPart[partList.size()] );

    }

}
