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

import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.name.Rdn;


/**
 * Utility class for Dn specific stuff.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DnUtils
{
    /**
     * Gets the prefix, cuts the suffix from the given Dn.
     * 
     * @param dn the Dn
     * @param suffix the suffix
     * 
     * @return the prefix
     */
    public static Dn getPrefixName( Dn dn, Dn suffix ) 
    {
        if ( suffix.size() < 1 )
        {
            return null;
        }
        else
        {
            try
            {
                Dn prefix = dn.getDescendantOf( suffix );
                
                return prefix;
            }
            catch ( LdapInvalidDnException lide )
            {
                return null;
            }
        }
    }


    /**
     * Composes an Rdn based on the given types and values.
     * 
     * @param rdnTypes the types
     * @param rdnValues the values
     * 
     * @return the Rdn
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
            sb.append( Rdn.escapeValue(rdnValues[i]) );
        }

        String s = sb.toString();
        try
        {
            if ( Dn.isValid(s) )
            {
                Rdn rdn = new Rdn( sb.toString() );
                return rdn;
            }
        }
        catch ( Exception e )
        {
        }

        throw new InvalidNameException( Messages.error__invalid_rdn );
    }

}
