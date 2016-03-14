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


import org.apache.directory.api.ldap.model.constants.LdapSecurityConstants;
import org.apache.directory.api.ldap.model.password.PasswordDetails;
import org.apache.directory.api.ldap.model.password.PasswordUtil;
import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldifparser.LdifUtils;


/**
 * The Password class is used to represent a hashed or plain text password.
 * It provides methods to retrieve information about the used hash method. 
 * And it provides a verify method to check if the hashed password is equal to 
 * a given plain text password.  
 * 
 * The following hash methods are supported:
 * <ul>
 *   <li>SHA</li>
 *   <li>SSHA</li>
 *   <li>SHA-256</li>
 *   <li>SSHA-256</li>
 *   <li>SHA-384</li>
 *   <li>SSHA-384</li>
 *   <li>SHA-512</li>
 *   <li>SSHA-512</li>
 *   <li>MD5</li>
 *   <li>SMD5</li>
 *   <li>PKCS5S2</li>
 *   <li>CRYPT</li>
 * </ul>
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class Password
{
    /** The password, either plain text or in encrypted format */
    private final byte[] password;
    
    /** The password details */
    private final PasswordDetails passwordDetails;


    /**
     * Creates a new instance of Password.
     *
     * @param password the password, either hashed or plain text
     */
    public Password( byte[] password )
    {
        if ( password == null )
        {
            throw new IllegalArgumentException( BrowserCoreMessages.model__empty_password );
        }
        else
        {
            this.password = password;
            this.passwordDetails = PasswordUtil.splitCredentials( password );
        }
    }


    /**
     * Creates a new instance of Password.
     *
     * @param password the password, either hashed or plain text
     */
    public Password( String password )
    {
        if ( password == null )
        {
            throw new IllegalArgumentException( BrowserCoreMessages.model__empty_password );
        }
        else
        {
            this.password = Strings.getBytesUtf8( password );
            this.passwordDetails = PasswordUtil.splitCredentials( this.password );
        }
    }


    /**
     * Creates a new instance of Password and calculates the hashed password.
     *
     * @param hashMethod the hash method to use
     * @param passwordAsPlaintext the plain text password
     * 
     * @throws IllegalArgumentException if the given password is null
     */
    public Password( LdapSecurityConstants hashMethod, String passwordAsPlaintext )
    {
        if ( passwordAsPlaintext == null )
        {
            throw new IllegalArgumentException( BrowserCoreMessages.model__empty_password );
        }
        else
        {
            this.password = PasswordUtil.createStoragePassword( passwordAsPlaintext, hashMethod );
            this.passwordDetails = PasswordUtil.splitCredentials( this.password );
        }
    }


    /**
     * Verifies if this password is equal to the given test password.
     * 
     * @param testPasswordAsPlaintext the test password as plaintext
     * 
     * @return true, if equal
     */
    public boolean verify( String testPasswordAsPlaintext )
    {
        if ( testPasswordAsPlaintext == null )
        {
            return false;
        }

        return PasswordUtil.compareCredentials( Strings.getBytesUtf8( testPasswordAsPlaintext ), this.password );
    }


    /**
     * Gets the hash method.
     * 
     * @return the hash method
     */
    public LdapSecurityConstants getHashMethod()
    {
        return passwordDetails.getAlgorithm();
    }


    /**
     * Gets the hashed password.
     * 
     * @return the hashed password
     */
    public byte[] getHashedPassword()
    {
        return passwordDetails.getPassword();
    }


    /**
     * Gets the hashed password as hex string.
     * 
     * @return the hashed password as hex string
     */
    public String getHashedPasswordAsHexString()
    {
        return LdifUtils.hexEncode( passwordDetails.getPassword() );
    }


    /**
     * Gets the salt.
     * 
     * @return the salt
     */
    public byte[] getSalt()
    {
        return passwordDetails.getSalt();
    }


    /**
     * Gets the salt as hex string.
     * 
     * @return the salt as hex string
     */
    public String getSaltAsHexString()
    {
        return LdifUtils.hexEncode( passwordDetails.getSalt() );
    }


    /**
     * Gets the 
     * 
     * @return the byte[]
     */
    public byte[] toBytes()
    {
        return LdifUtils.utf8encode( toString() );
    }


    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return Strings.utf8ToString( password );
    }

}
