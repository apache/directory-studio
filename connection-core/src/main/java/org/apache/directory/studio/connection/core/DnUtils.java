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
package org.apache.directory.studio.connection.core;


import javax.naming.InvalidNameException;

import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.name.Rdn;


/**
 * Utility class for LdapDN specific stuff.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DnUtils
{

    /**
     * Composes an DN based on the given RDN and DN.
     * 
     * @param rdn the RDN
     * @param parent the parent DN
     * 
     * @return the composed DN
     */
    public static LdapDN composeDn( Rdn rdn, LdapDN parent )
    {
        LdapDN ldapDn = ( LdapDN ) parent.clone();
        ldapDn.add( ( Rdn ) rdn.clone() );
        return ldapDn;
    }


    /**
     * Gets the parent DN of the given DN or null if the given 
     * DN hasn't a parent.
     * 
     * @param dn the DN
     * 
     * @return the parent DN, null if the given DN hasn't a parent
     */
    public static LdapDN getParent( LdapDN dn )
    {
        if ( dn.size() < 1 )
        {
            return null;
        }
        else
        {
            LdapDN parent = ( LdapDN ) dn.getPrefix( dn.size() - 1 );
            return parent;
        }
    }


    /**
     * Compose an DN based on the given RDN and DN.
     * 
     * @param rdn the RDN
     * @param parent the parent DN
     * 
     * @return the composed RDN
     * 
     * @throws InvalidNameException the invalid name exception
     */
    public static LdapDN composeDn( String rdn, String parent ) throws InvalidNameException
    {
        return composeDn( new Rdn( rdn ), new LdapDN( parent ) );
    }


    /**
     * Composes an DN based on the given prefix and suffix.
     * 
     * @param prefix the prefix
     * @param suffix the suffix
     * 
     * @return the composed DN
     */
    public static LdapDN composeDn( LdapDN prefix, LdapDN suffix )
    {
        LdapDN ldapDn = ( LdapDN ) suffix.clone();

        for ( int i = 0; i < prefix.size(); i++ )
        {
        	ldapDn.add( (Rdn) prefix.getRdn( i ).clone() );
        }

        return ldapDn;
    }


    /**
     * Gets the prefix, cuts the suffix from the given DN.
     * 
     * @param dn the DN
     * @param suffix the suffix
     * 
     * @return the prefix
     */
    public static LdapDN getPrefixName( LdapDN dn, LdapDN suffix )
    {
        if ( suffix.size() < 1 )
        {
            return null;
        }
        else
        {
            LdapDN prefix = ( LdapDN ) dn.getSuffix( suffix.size() );
            return prefix;
        }
    }


    /**
     * Composes an RDN based on the given types and values.
     * 
     * @param rdnTypes the types
     * @param rdnValues the values
     * 
     * @return the RDN
     * 
     * @throws InvalidNameException the invalid name exception
     */
    public static Rdn composeRdn( String[] rdnTypes, String[] rdnValues ) throws InvalidNameException
    {
        StringBuffer sb = new StringBuffer();
        for ( int i = 0; i < rdnTypes.length; i++ )
        {
            if ( i > 0 )
            {
                sb.append( '+' );
            }

            sb.append( rdnTypes[i] );
            sb.append( '=' );
            sb.append( Rdn.escapeValue( rdnValues[i] ) );
        }

        String s = sb.toString();
        try
        {
            if ( LdapDN.isValid( s ) )
            {
                Rdn rdn = new Rdn( sb.toString() );
                return rdn;
            }
        }
        catch ( Exception e )
        {
        }

        throw new InvalidNameException( "RDN is invalid" );
    }

}
