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
import java.util.List;

import org.apache.directory.ldapstudio.browser.core.BrowserCoreMessages;
import org.apache.directory.ldapstudio.browser.core.model.schema.Schema;


/**
 * A DN represents a LDAP distinguished name.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DN implements Serializable
{

    /** The generated serialVersionUID. */
    private static final long serialVersionUID = 2343676941769163982L;

    /** The rdns */
    private RDN[] rdns;


    /**
     * Creates an empty DN.
     *
     */
    public DN()
    {
        this.rdns = new RDN[0];
    }


    /**
     * Creates a new instance of DN containing only on RDN.
     * The given RDN is cloned.
     *
     * @param rdn the rdn
     */
    public DN( RDN rdn )
    {
        if ( rdn == null )
        {
            throw new IllegalArgumentException( BrowserCoreMessages.model__empty_rdn );
        }

        this.rdns = new RDN[1];
        this.rdns[0] = new RDN( rdn );
    }


    /**
     * Creates a new instance of DN. The given string is parsed.
     *
     * @param dn the dn
     * @throws NameException if parsing fails.
     */
    public DN( String dn ) throws NameException
    {
        if ( dn == null )
        {
            throw new IllegalArgumentException( BrowserCoreMessages.model__empty_dn );
        }

        // this.parseDn(dn.trim());
        this.parseDn( dn );
    }


    /**
     * Creates a clone of the given DN.
     *
     * @param dn the DN
     */
    public DN( DN dn )
    {
        if ( dn == null )
        {
            throw new IllegalArgumentException( BrowserCoreMessages.model__empty_dn );
        }

        this.rdns = new RDN[dn.getRdns().length];
        for ( int i = 0; i < dn.getRdns().length; i++ )
        {
            this.rdns[i] = new RDN( dn.getRdns()[i] );
        }
    }


    /**
     * Creates a new instance of DN using the given RDN and parent.
     * The given RDN and parent are cloned.
     *
     * @param rdn the RDN
     * @param parent the parent DN
     */
    public DN( RDN rdn, DN parent )
    {
        if ( rdn == null )
        {
            throw new IllegalArgumentException( BrowserCoreMessages.model__empty_rdn );
        }
        if ( parent == null )
        {
            throw new IllegalArgumentException( BrowserCoreMessages.model__empty_dn );
        }

        this.rdns = new RDN[parent.getRdns().length + 1];
        this.rdns[0] = new RDN( rdn );
        for ( int i = 0; i < parent.getRdns().length; i++ )
        {
            this.rdns[i + 1] = new RDN( parent.getRdns()[i] );
        }
    }


    /**
     * Creates a new instance of DN. The given strings are parsed.
     *
     * @param rdn the rdn
     * @param parent the parent dn
     * @throws NameException if parsing fails
     */
    public DN( String rdn, String parent ) throws NameException
    {

        if ( rdn == null )
        {
            throw new IllegalArgumentException( BrowserCoreMessages.model__empty_rdn );
        }
        if ( parent == null )
        {
            throw new IllegalArgumentException( BrowserCoreMessages.model__empty_dn );
        }

        // this.parseDn(parent.trim());
        this.parseDn( parent );

        RDN[] rdns = this.rdns;
        this.rdns = new RDN[rdns.length + 1];
        this.rdns[0] = new RDN( rdn );
        System.arraycopy( rdns, 0, this.rdns, 1, rdns.length );
    }


    /**
     * Gets the RDN of this DN.
     *
     * @return the RDN
     */
    public RDN getRdn()
    {
        if ( this.rdns.length > 0 )
        {
            return this.rdns[0];
        }
        else
        {
            return new RDN();
        }
    }


    /**
     * Gets the parent DN.
     * 
     * @return the parent DN
     */
    public DN getParentDn()
    {
        if ( this.rdns.length < 1 )
        {
            return null;
        }
        else
        {
            RDN[] parentRdns = new RDN[this.rdns.length - 1];
            for ( int i = 1; i < this.rdns.length; i++ )
            {
                parentRdns[i - 1] = new RDN( this.rdns[i] );
            }
            DN parent = new DN();
            parent.rdns = parentRdns;
            return parent;
        }
    }


    /**
     * Returns the string representation of this DN.
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        for ( int i = 0; i < this.rdns.length; i++ )
        {
            sb.append( this.rdns[i].toString() );
            if ( i + 1 < rdns.length )
            {
                sb.append( "," ); //$NON-NLS-1$
            }
        }

        return sb.toString();
    }


    /**
     * Returns the string representation of this DN, but 
     * lowercased and with the numerid OIDs instead of the types.
     *
     * @param schema the schema
     * @return the lowercased and OID-fizied string representation of this DN
     */
    public String toOidString( Schema schema )
    {
        StringBuffer sb = new StringBuffer();

        for ( int i = 0; i < this.rdns.length; i++ )
        {
            sb.append( this.rdns[i].toOidString( schema ) );
            if ( i + 1 < rdns.length )
            {
                sb.append( "," ); //$NON-NLS-1$
            }
        }

        return sb.toString();
    }


    /**
     * Parses the dn.
     *
     * @param dn the dn
     * @throws NameException if parsing fails
     */
    private void parseDn( String dn ) throws NameException
    {
        List<RDN> rdnList = new ArrayList<RDN>( 3 );

        boolean backslash = false;
        int start = 0;
        for ( int i = 0; i < dn.length(); i++ )
        {
            if ( dn.charAt( i ) == '\\' && !backslash )
            {
                backslash = true;
            }
            else
            {
                String rdn = null;
                if ( dn.charAt( i ) == ',' && !backslash )
                {
                    rdn = dn.substring( start, i );
                }
                else if ( i == dn.length() - 1 )
                {
                    rdn = dn.substring( start );
                }
                if ( rdn != null )
                {
                    rdnList.add( new RDN( rdn ) );
                    start = i + 1;

                    // remove spaces between RDNs
                    for ( ; start < dn.length() && dn.charAt( start ) == ' '; i++ )
                    {
                        start++;
                    }
                }
                backslash = false;
            }
        }

        this.rdns = rdnList.toArray( new RDN[rdnList.size()] );
    }


    /**
     * Gets the RDNs.
     * 
     * @return the RDNs
     */
    public RDN[] getRdns()
    {
        return rdns;
    }


    /**
     * Sets the RDNs.
     * 
     * @param rdns the RDNs
     */
    public void setRdns( RDN[] rdns )
    {
        this.rdns = rdns;
    }


    /**
     * {@inheritDoc}
     */
    public boolean equals( Object o ) throws ClassCastException
    {
        if ( o instanceof DN )
        {
            return this.toString().equals( ( ( DN ) o ).toString() );
        }
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public int hashCode()
    {
        return this.toString().hashCode();
    }

}
