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

import org.apache.directory.shared.ldap.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.name.DN;
import org.apache.directory.shared.ldap.name.RDN;


/**
 * Utility class for DN specific stuff.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
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
    public static DN composeDn( RDN rdn, DN parent )
    {
        return parent.add( rdn );
    }


    /**
     * Gets the parent DN of the given DN or null if the given 
     * DN hasn't a parent.
     * 
     * @param dn the DN
     * 
     * @return the parent DN, null if the given DN hasn't a parent
     */
    public static DN getParent( DN dn )
    {
        if ( dn.size() < 1 )
        {
            return null;
        }
        else
        {
            DN parent = ( DN ) dn.getPrefix( dn.size() - 1 );
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
    public static DN composeDn( String rdn, String parent ) throws InvalidNameException
    {
        try
        {
            return composeDn( new RDN( rdn ), new DN( parent ) );
        }
        catch ( LdapInvalidDnException e )
        {
            throw new InvalidNameException( e.getMessage() );
        }
    }


    /**
     * Composes an DN based on the given prefix and suffix.
     * 
     * @param prefix the prefix
     * @param suffix the suffix
     * 
     * @return the composed DN
     */
    public static DN composeDn( DN prefix, DN suffix )
    {
        DN ldapDn = suffix;

        for ( int i = 0; i < prefix.size(); i++ )
        {
            ldapDn = ldapDn.add( ( RDN ) prefix.getRdn( i ).clone() );
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
    public static DN getPrefixName( DN dn, DN suffix )
    {
        if ( suffix.size() < 1 )
        {
            return null;
        }
        else
        {
            DN prefix = ( DN ) dn.getSuffix( suffix.size() );
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
    public static RDN composeRdn( String[] rdnTypes, String[] rdnValues ) throws InvalidNameException
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
            sb.append( RDN.escapeValue( rdnValues[i] ) );
        }

        String s = sb.toString();
        try
        {
            if ( DN.isValid( s ) )
            {
                RDN rdn = new RDN( sb.toString() );
                return rdn;
            }
        }
        catch ( Exception e )
        {
        }

        throw new InvalidNameException( Messages.error__invalid_rdn );
    }

}
