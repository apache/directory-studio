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


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.apache.directory.api.ldap.model.constants.LdapSecurityConstants;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.utils.UnixCrypt;
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
 *   <li>CRYPT</li>
 * </ul>
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class Password
{
    /** The hash method */
    private LdapSecurityConstants hashMethod;

    /** The hashed password */
    private byte[] hashedPassword;

    /** The salt */
    private byte[] salt;

    private boolean isUnsupportedHashMethod = false;

    private boolean isInvalidHashValue = false;

    /** The trash, used for unknown hash methods. */
    private String trash;


    /**
     * Creates a new instance of Password.
     *
     * @param password the password, either hashed or plain text
     */
    public Password( byte[] password )
    {
        this( LdifUtils.utf8decode( password ) );
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
        else if ( ( password.indexOf( '{' ) == 0 ) && ( password.indexOf( '}' ) > 0 ) )
        {
            try
            {
                // Getting the hash method
                String hashMethodString = password.substring( password.indexOf( '{' ) + 1, password.indexOf( '}' ) );
                hashMethod = LdapSecurityConstants.getAlgorithm( hashMethodString );

                // Getting the rest of the hashed password
                String rest = password.substring( hashMethodString.length() + 2 );

                if ( ( LdapSecurityConstants.HASH_METHOD_SHA == hashMethod )
                    || ( LdapSecurityConstants.HASH_METHOD_SHA256 == hashMethod )
                    || ( LdapSecurityConstants.HASH_METHOD_SHA384 == hashMethod )
                    || ( LdapSecurityConstants.HASH_METHOD_SHA512 == hashMethod )
                    || ( LdapSecurityConstants.HASH_METHOD_MD5 == hashMethod ) )
                {
                    hashedPassword = LdifUtils.base64decodeToByteArray( rest );
                    salt = null;
                }
                else if ( ( LdapSecurityConstants.HASH_METHOD_SSHA == hashMethod )
                    || ( LdapSecurityConstants.HASH_METHOD_SSHA256 == hashMethod )
                    || ( LdapSecurityConstants.HASH_METHOD_SSHA384 == hashMethod )
                    || ( LdapSecurityConstants.HASH_METHOD_SSHA512 == hashMethod )
                    || ( LdapSecurityConstants.HASH_METHOD_SMD5 == hashMethod ) )
                {
                    switch ( hashMethod )
                    {
                        case HASH_METHOD_SSHA:
                            hashedPassword = new byte[20];
                            break;
                        case HASH_METHOD_SSHA256:
                            hashedPassword = new byte[32];
                            break;
                        case HASH_METHOD_SSHA384:
                            hashedPassword = new byte[48];
                            break;
                        case HASH_METHOD_SSHA512:
                            hashedPassword = new byte[64];
                            break;
                        case HASH_METHOD_SMD5:
                            hashedPassword = new byte[16];
                            break;
                        default:
                            break;
                    }

                    byte[] hashedPasswordWithSalt = LdifUtils.base64decodeToByteArray( rest );
                    salt = new byte[hashedPasswordWithSalt.length - hashedPassword.length];
                    split( hashedPasswordWithSalt, hashedPassword, salt );
                }
                else if ( LdapSecurityConstants.HASH_METHOD_CRYPT == hashMethod )
                {
                    byte[] saltWithPassword = LdifUtils.utf8encode( rest );
                    salt = new byte[2];
                    hashedPassword = new byte[saltWithPassword.length - salt.length];
                    split( saltWithPassword, salt, hashedPassword );
                }
                else
                {
                    isUnsupportedHashMethod = true;
                    trash = password;
                }
            }
            catch ( RuntimeException e )
            {
                // happens if 'rest' is not valid BASE64
                isInvalidHashValue = true;
                trash = password;
            }
        }
        else
        {
            // plain text
            hashMethod = null;
            hashedPassword = LdifUtils.utf8encode( password );
            salt = null;
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
        // Checking the password as plain text
        if ( passwordAsPlaintext == null )
        {
            throw new IllegalArgumentException( BrowserCoreMessages.model__empty_password );
        }

        // Setting the hash method
        this.hashMethod = hashMethod;

        // Setting the salt
        if ( ( LdapSecurityConstants.HASH_METHOD_SSHA == hashMethod )
            || ( LdapSecurityConstants.HASH_METHOD_SSHA256 == hashMethod )
            || ( LdapSecurityConstants.HASH_METHOD_SSHA384 == hashMethod )
            || ( LdapSecurityConstants.HASH_METHOD_SSHA512 == hashMethod )
            || ( LdapSecurityConstants.HASH_METHOD_SMD5 == hashMethod ) )
        {
            this.salt = new byte[8];
            new SecureRandom().nextBytes( this.salt );
        }
        else if ( LdapSecurityConstants.HASH_METHOD_CRYPT == hashMethod )
        {
            this.salt = new byte[2];
            SecureRandom sr = new SecureRandom();
            int i1 = sr.nextInt( 64 );
            int i2 = sr.nextInt( 64 );
            this.salt[0] = ( byte ) ( i1 < 12 ? ( i1 + '.' ) : i1 < 38 ? ( i1 + 'A' - 12 ) : ( i1 + 'a' - 38 ) );
            this.salt[1] = ( byte ) ( i2 < 12 ? ( i2 + '.' ) : i2 < 38 ? ( i2 + 'A' - 12 ) : ( i2 + 'a' - 38 ) );
        }
        else
        {
            this.salt = null;
        }

        // Setting the hashed password
        if ( hashMethod == null )
        {
            this.hashedPassword = LdifUtils.utf8encode( passwordAsPlaintext );
        }
        else if ( ( LdapSecurityConstants.HASH_METHOD_SHA == hashMethod )
            || ( LdapSecurityConstants.HASH_METHOD_SSHA == hashMethod )
            || ( LdapSecurityConstants.HASH_METHOD_SHA256 == hashMethod )
            || ( LdapSecurityConstants.HASH_METHOD_SSHA256 == hashMethod )
            || ( LdapSecurityConstants.HASH_METHOD_SHA384 == hashMethod )
            || ( LdapSecurityConstants.HASH_METHOD_SSHA384 == hashMethod )
            || ( LdapSecurityConstants.HASH_METHOD_SHA512 == hashMethod )
            || ( LdapSecurityConstants.HASH_METHOD_SSHA512 == hashMethod )
            || ( LdapSecurityConstants.HASH_METHOD_MD5 == hashMethod )
            || ( LdapSecurityConstants.HASH_METHOD_SMD5 == hashMethod ) )
        {
            this.hashedPassword = digest( hashMethod, passwordAsPlaintext, this.salt );
        }
        else if ( LdapSecurityConstants.HASH_METHOD_CRYPT == hashMethod )
        {
            this.hashedPassword = crypt( passwordAsPlaintext, this.salt );
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

        boolean verified = false;

        if ( hashMethod == null )
        {
            verified = testPasswordAsPlaintext.equals( LdifUtils.utf8decode( hashedPassword ) );
        }
        else if ( ( LdapSecurityConstants.HASH_METHOD_SHA == hashMethod )
            || ( LdapSecurityConstants.HASH_METHOD_SSHA == hashMethod )
            || ( LdapSecurityConstants.HASH_METHOD_SHA256 == hashMethod )
            || ( LdapSecurityConstants.HASH_METHOD_SSHA256 == hashMethod )
            || ( LdapSecurityConstants.HASH_METHOD_SHA384 == hashMethod )
            || ( LdapSecurityConstants.HASH_METHOD_SSHA384 == hashMethod )
            || ( LdapSecurityConstants.HASH_METHOD_SHA512 == hashMethod )
            || ( LdapSecurityConstants.HASH_METHOD_SSHA512 == hashMethod )
            || ( LdapSecurityConstants.HASH_METHOD_MD5 == hashMethod )
            || ( LdapSecurityConstants.HASH_METHOD_SMD5 == hashMethod ) )
        {
            byte[] hash = digest( hashMethod, testPasswordAsPlaintext, salt );
            verified = equals( hash, hashedPassword );
        }
        else if ( LdapSecurityConstants.HASH_METHOD_CRYPT == hashMethod )
        {
            byte[] crypted = crypt( testPasswordAsPlaintext, salt );
            verified = equals( crypted, hashedPassword );
        }

        return verified;
    }


    /**
     * Gets the hash method.
     * 
     * @return the hash method
     */
    public LdapSecurityConstants getHashMethod()
    {
        return hashMethod;
    }


    /**
     * Gets the hashed password.
     * 
     * @return the hashed password
     */
    public byte[] getHashedPassword()
    {
        return hashedPassword;
    }


    /**
     * Gets the hashed password as hex string.
     * 
     * @return the hashed password as hex string
     */
    public String getHashedPasswordAsHexString()
    {
        return LdifUtils.hexEncode( hashedPassword );
    }


    /**
     * Gets the salt.
     * 
     * @return the salt
     */
    public byte[] getSalt()
    {
        return salt;
    }


    /**
     * Gets the salt as hex string.
     * 
     * @return the salt as hex string
     */
    public String getSaltAsHexString()
    {
        return LdifUtils.hexEncode( salt );
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
        StringBuffer sb = new StringBuffer();

        if ( isUnsupportedHashMethod || isInvalidHashValue )
        {
            sb.append( trash );
        }
        else if ( LdapSecurityConstants.HASH_METHOD_CRYPT == hashMethod )
        {
            sb.append( '{' ).append( hashMethod.getPrefix() ).append( '}' );
            sb.append( LdifUtils.utf8decode( salt ) );
            sb.append( LdifUtils.utf8decode( hashedPassword ) );
        }
        else if ( hashMethod != null )
        {
            sb.append( '{' ).append( hashMethod.getPrefix() ).append( '}' );
            if ( salt != null )
            {
                byte[] hashedPasswordWithSaltBytes = new byte[hashedPassword.length + salt.length];
                merge( hashedPasswordWithSaltBytes, hashedPassword, salt );
                sb.append( LdifUtils.base64encode( hashedPasswordWithSaltBytes ) );
            }
            else
            {
                sb.append( LdifUtils.base64encode( hashedPassword ) );
            }
        }
        else
        {
            sb.append( LdifUtils.utf8decode( hashedPassword ) );
        }

        return sb.toString();
    }


    private static void split( byte[] all, byte[] left, byte[] right )
    {
        System.arraycopy( all, 0, left, 0, left.length );
        System.arraycopy( all, left.length, right, 0, right.length );
    }


    private static void merge( byte[] all, byte[] left, byte[] right )
    {
        System.arraycopy( left, 0, all, 0, left.length );
        System.arraycopy( right, 0, all, left.length, right.length );
    }


    /**
     * Checks equality between two byte arrays.
     *
     * @param data1 the first byte array
     * @param data2 the first byte array
     * @return <code>true</code> if the two byte arrays are equal
     */
    private static boolean equals( byte[] data1, byte[] data2 )
    {
        if ( data1 == data2 )
        {
            return true;
        }

        if ( data1 == null || data2 == null )
        {
            return false;
        }

        if ( data1.length != data2.length )
        {
            return false;
        }

        for ( int i = 0; i < data1.length; i++ )
        {
            if ( data1[i] != data2[i] )
            {
                return false;
            }
        }

        return true;
    }


    /**
     * Computes the hashed value of a password with the given hash method and optional salt.
     *
     * @param hashMethod the hash method
     * @param password the password
     * @param salt the optional salt (can be <code>null</code>)
     * @return the hashed value of the password
     */
    private static byte[] digest( LdapSecurityConstants hashMethod, String password, byte[] salt )
    {
        // Converting password to byte array
        byte[] passwordBytes = LdifUtils.utf8encode( password );

        // Getting the message digest associated with the hash method
        try
        {
            MessageDigest digest = MessageDigest.getInstance( hashMethod.getAlgorithm() );

            // Computing the hashed password (salted or not)
            if ( salt != null )
            {
                digest.update( passwordBytes );
                digest.update( salt );
                return digest.digest();
            }
            else
            {
                return digest.digest( passwordBytes );
            }
        }
        catch ( NoSuchAlgorithmException e1 )
        {
            return null;
        }
    }


    /**
     * Computes the crypt value of a password (with salt).
     *
     * @param password the password
     * @param salt the salt
     * @return the crypt value of the password
     */
    private static byte[] crypt( String password, byte[] salt )
    {
        String saltWithCrypted = UnixCrypt.crypt( password, LdifUtils.utf8decode( salt ) );
        String crypted = saltWithCrypted.substring( 2 );
        return LdifUtils.utf8encode( crypted );
    }
}
